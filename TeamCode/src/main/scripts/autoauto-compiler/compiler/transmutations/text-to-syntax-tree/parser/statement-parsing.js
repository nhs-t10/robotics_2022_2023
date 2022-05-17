const { expect, unexpectedError, improperContextError } = require("./common-utils");


const statements = {
    AFTER: parseAfterStatement,
    FUNCTION: parseFunctionDefinition,
    GOTO: parseGotoStatement,
    IF: parseIfStatement,
    RETURN: parseReturnStatement,
    PROVIDE: parseProvideStatement,
    PASS: parsePassStatement,
    NEXT: parseNextStatement,
    SKIP: parseSkipStatement,
    LET: parseLetStatement,


    OPEN_CURLY_BRACKET: parseBlock
}

const prefixParsers = {
    TRUE: parseTrueLiteral,
    FALSE: parseFalseLiteral,
    STRING_LITERAL: parseStringLiteral,
    IDENTIFIER: parseIdentifier,
    NUMERIC_VALUE: parseNumeric,
    NUMERIC_VALUE_WITH_UNIT: parseNumericWithUnit,
    NOT: parseNotOperator,
    OPEN_SQUARE_BRACKET: parseTableLiteral,
    FUNC: parseFunctionLiteral,
    OPEN_PAREN: parseParenGroup,
    DELEGATE: parseDelegatorExpression
}

const precedenceTable = {

    RELATION: 10,
    COMPARISON: 20,
    ADDITION: 30,
    MULTIPLICATION: 30,
    MODULO: 40,
    EXPONENT: 40,

    PROPERTY_GET: 50,
    FUNCTION_CALL: 50
}

const tokenPrecedence = {
    OPEN_PAREN: precedenceTable.FUNCTION_CALL,

    PLUS: precedenceTable.ADDITION,
    MINUS: precedenceTable.ADDITION,

    DIVIDE: precedenceTable.MULTIPLICATION,
    MULTIPLY: precedenceTable.MULTIPLICATION,

    MODULUS: precedenceTable.MODULO,

    EXPONENTIATE: precedenceTable.EXPONENT,
    EXPONENTIATE: precedenceTable.EXPONENT,

    COMPARE_LT: precedenceTable.COMPARISON,
    COMPARE_LTE: precedenceTable.COMPARISON,
    COMPARE_EQ: precedenceTable.COMPARISON,
    COMPARE_NEQ: precedenceTable.COMPARISON,
    COMPARE_GTE: precedenceTable.COMPARISON,
    COMPARE_GT: precedenceTable.COMPARISON,

    DOT: precedenceTable.PROPERTY_GET,
    OPEN_SQUARE_BRACKET: precedenceTable.PROPERTY_GET,

    EQUALS_OR_COLON: precedenceTable.RELATION
}

const infixParsers = {
    OPEN_PAREN: parseFunctionCall,
    EQUALS_OR_COLON: parseRelation,

    COMPARE_LT: binaryLeftAssociativeOperator(precedenceTable.COMPARISON),
    COMPARE_LTE: binaryLeftAssociativeOperator(precedenceTable.COMPARISON),
    COMPARE_EQ: binaryLeftAssociativeOperator(precedenceTable.COMPARISON),
    COMPARE_NEQ: binaryLeftAssociativeOperator(precedenceTable.COMPARISON),
    COMPARE_GTE: binaryLeftAssociativeOperator(precedenceTable.COMPARISON),
    COMPARE_GT: binaryLeftAssociativeOperator(precedenceTable.COMPARISON),

    PLUS: binaryLeftAssociativeOperator(precedenceTable.ADDITION),
    MINUS: binaryLeftAssociativeOperator(precedenceTable.ADDITION),
    DIVIDE: binaryLeftAssociativeOperator(precedenceTable.MULTIPLICATION),
    MULTIPLY: binaryLeftAssociativeOperator(precedenceTable.MULTIPLICATION),
    MODULUS: binaryLeftAssociativeOperator(precedenceTable.MODULO),
    EXPONENTIATE: binaryLeftAssociativeOperator(precedenceTable.EXPONENT),

    DOT: parseDotGetter,
    OPEN_SQUARE_BRACKET: parseArrayGetter,
}

module.exports = parseStatement

function parseStatement(tokenStream) {

    let statementSigil = tokenStream.peek().name;

    if (statementSigil in statements) {
        return statements[statementSigil](tokenStream);
    } else {
        return wrapValueStatement(parseExpression(tokenStream));
    }
}

function wrapValueStatement(expr) {
    return {
        type: "ValueStatement",
        location: Object.assign({}, expr.location),
        call: expr
    }
}

function parseAfterStatement(tokenStream) {
    //pop and discard the 'after'. The only thing we need to save is the 
    //start location.
    const locStart = tokenStream.pop().location.start;

    const condition = parseExpression(tokenStream);

    return {
        type: "AfterStatement",
        location: { start: locStart, end: condition.location.end },
        unitValue: condition,
        statement: parseStatement(tokenStream)
    };
}

function parseFunctionDefinition(tokenStream) {
    const locStart = tokenStream.pop().location.start;

    const name = expect(tokenStream, "IDENTIFIER", "Expected an identifier to name the function; instead, got", [
        "If you want to create anonymous functions, try using the 'func' keyword"
    ]);

    expect(tokenStream, "OPEN_PAREN", "Expected a open-paren ('(') to start the arguments of this function; got a");

    const parameterList = parseParameterList(tokenStream);

    expect(tokenStream, "CLOSE_PAREN", "Expected a close-paren (')') to finish the arguments of this function; got a");

    const body = parseStatement(tokenStream);

    return {
        type: "FunctionDefStatement",
        name: wrapIdentifierToken(name),
        args: parameterList,
        body: body,
        location: { start: locStart, end: body.location.end }
    }
}

function parseGotoStatement(tokenStream) {
    const locStart = tokenStream.pop().location.start;

    let name = expect(tokenStream, "IDENTIFIER", "Expected an identifier for this goto statement; got a", ["Make sure that it doesn't start with a numerical digit"]);

    return {
        type: "GotoStatement",
        location: { start: locStart, end: name.location.end },
        path: wrapIdentifierToken(name)
    }
}

function parseIfStatement(tokenStream) {
    const locStart = tokenStream.pop().location.start;

    expect(tokenStream, "OPEN_PAREN", "If you're used to Python or Lua, you may have forgot to put (parentheses) around this `if` statement's condition. The parser expected an open-paren ('(') to start the condition, but saw a");

    const conditional = parseExpression(tokenStream);

    expect(tokenStream, "CLOSE_PAREN", "Expected a closing paren (')') to end this `if` condition. Got a");

    const statement = parseStatement(tokenStream);

    const elseClause = tokenStream.peek().name == "ELSE" ?
        parseElseClause(tokenStream)
        : generatePassStatement(statement.location, true, []);


    return {
        type: "IfStatement",
        location: { start: locStart, end: elseClause.location.end },
        conditional: conditional,
        statement: statement,
        elseClause, elseClause
    }
}

function parseElseClause(tokenStream) {
    //we don't need the 'else'
    tokenStream.pop();

    return parseStatement(tokenStream);
}

function parsePassStatement(tokenStream) {
    return generatePassStatement(tokenStream.pop().location, false);
}

function generatePassStatement(location, makeSynthetic) {
    if (makeSynthetic) {
        location = Object.assign({}, location);
        location.synthetic = true;
    }
    return {
        type: "PassStatement",
        location: location
    }
}

function parseReturnStatement(tokenStream) {
    const location = tokenStream.pop().location;

    let expression = undefined;
    const nextName = tokenStream.peek().name;
    if (nextName != "EOF" && nextName != "SEMICOLON" && nextName != "HASHTAG") {
        expression = parseExpression(tokenStream);
    }

    return {
        type: "ReturnStatement",
        value: expression,
        location: location
    };
}

function parseBlock(tokenStream) {
    const locStart = tokenStream.pop().location.start;

    const innerStatements = [];
    while (tokenStream.peek().name != "CLOSE_CURLY_BRACKET") {
        if (tokenStream.peek().name == "EOF") {
            throw unexpectedError("End-of-file", tokenStream.peek().location, "EOF", [
                `There may be a missing ending curly-bracket ('}'). If so, its corresponding open-curly is on line ${locStart.line}, column ${locStart.column}`
            ]);
        }


        innerStatements.push(parseStatement(tokenStream));

        if (tokenStream.peek().name == "COMMA" || tokenStream.peek().name == "SEMICOLON") {
            tokenStream.pop();
        }
    }
    //must be a closing curly. pop it!
    const locEnd = tokenStream.pop().location.end;

    return {
        type: "Block",
        location: { start: locStart, end: locEnd },
        state: { //i don't know why i set the schema up this way :/
            type: "State",
            location: { start: locStart, end: locEnd },
            statement: innerStatements
        }
    };

}

function parseProvideStatement(tokenStream) {
    const location = tokenStream.pop().location;

    const nextName = tokenStream.peek().name;
    if (nextName == "EOF" || nextName == "SEMICOLON" || nextName == "HASHTAG") {
        throw improperContextError("Provide statement missing an expression", location, [
            "Unlike 'return', you need to provide some value. Try 'provide undefined' or 'provide false'."
        ]);
    }

    const expression = parseExpression(tokenStream);

    return {
        type: "ProvideStatement",
        value: expression,
        location: location
    };
}

function parseNextStatement(tokenStream) {
    const location = tokenStream.pop().location;
    return {
        type: "NextStatement",
        location: location
    }
}

function parseSkipStatement(tokenStream) {
    const locStart = tokenStream.pop().location.start;

    const distance = tokenStream.peek();
    if (distance.name != "NUMERIC_VALUE") {
        throw improperContextError("Expected a number of states to skip; got a " + distance.name, distance.location, [
            "Make sure that you have given a number of states to skip."
        ]);
    }

    return {
        type: "SkipStatement",
        location: { start: locStart, end: distance.location.end },
        skip: parseExpression(tokenStream)
    }
}

function parseLetStatement(tokenStream) {
    const loc = tokenStream.pop().location;

    const name = expect(tokenStream, "IDENTIFIER", "Expected an identifier for let variable; got a");

    const nextTokenType = tokenStream.peek().name;
    if (nextTokenType == "EQUALS_OR_COLON") {
        //discard the '='
        tokenStream.pop();
        return {
            type: "LetStatement",
            location: loc,
            variable: wrapIdentifierToken(name),
            value: parseExpression(tokenStream),
        }
    }

    const mainVariable = wrapVariableReferenceOnIdentifierToken(name);
    let setPath;
    if (nextTokenType == "DOT") {
        setPath = parseDotGetter(tokenStream, mainVariable);
    } else if (nextTokenType == "OPEN_SQUARE_BRACKET") {
        setPath = parseArrayGetter(tokenStream, mainVariable);
    } else {
        throw improperContextError("Expected a property-getting tail (dot-style, e.g. 'a.b', or array style, e.g. 'a[\"b\"]'); got a " + nextTokenType, loc);
    }

    expect(tokenStream, "EQUALS_OR_COLON", "Expected an equals sign to separate the thing being set and the value; got a");

    return {
        type: "LetPropertyStatement",
        location: loc,
        variable: setPath,
        value: parseExpression(tokenStream),
    }

}


function parseExpression(tokenStream, precedence) {
    //make sure precedence is a number
    precedence |= 0;

    const next = tokenStream.peek();
    let left;
    if (next.name in prefixParsers) {
        left = prefixParsers[next.name](tokenStream);
    } else {
        throw improperContextError("Couldn't parse an expression from the token '" + next.name + "'", next.location);
    }

    while (precedence < getNextPrecedence(tokenStream)) {
        const maybeInfix = tokenStream.peek();
        if (maybeInfix.name in infixParsers) {
            left = infixParsers[maybeInfix.name](tokenStream, left);
        } else {
            throw improperContextError("Couldn't parse an expression from the infix token '" + maybeInfix.name + "'", maybeInfix.location);
        }
    }

    return left;
}

function getNextPrecedence(tokenStream) {
    const nextName = tokenStream.peek().name;
    if (nextName in tokenPrecedence) {
        return tokenPrecedence[nextName];
    } else {
        return 0;
    }
}

function parseTrueLiteral(tokenStream) {
    return {
        type: "BooleanLiteral",
        location: tokenStream.pop().location,
        value: true
    };
}
function parseFalseLiteral(tokenStream) {
    return {
        type: "BooleanLiteral",
        location: tokenStream.pop().location,
        value: false
    };
}
function parseStringLiteral(tokenStream) {
    return wrapStringLiteralToken(tokenStream.pop());
}

function wrapStringLiteralToken(tkn) {
    let stringContent = "";
    try {
        stringContent = JSON.parse(tkn.content);
    } catch (e) {
        throw improperContextError("Unable to process string content", tkn.location, [
            "Make sure that you don't have any invalid escape sequences (e.g. '\\un')"
        ]);
    }

    return {
        type: "StringLiteral",
        location: tkn.location,
        str: stringContent
    }
}

function parseIdentifier(tokenStream) {
    const tkn = tokenStream.pop();
    return wrapVariableReferenceOnIdentifierToken(tkn);
}

function parseNumeric(tokenStream) {
    const tkn = tokenStream.pop();

    return {
        type: "NumericValue",
        location: tkn.location,
        v: +tkn.content
    };
}

function parseNumericWithUnit(tokenStream) {
    const tkn = tokenStream.pop();

    return {
        type: "UnitValue",
        location: tkn.location,
        value: {
            type: "NumericValue",
            location: tkn.location,
            v: +tkn.regex[1]
        },
        unit: wrapIdentifierString(tkn.regex[2], tkn.location)
    }
}

function parseNotOperator(tokenStream) {
    throw improperContextError("Sorry, the unary NOT operator ('!') is not supported right now.", tokenStream.pop().location);
}

function parseTableLiteral(tokenStream) {
    const locStart = tokenStream.pop().location.start;

    const elements = parseArgumentList(tokenStream);

    const closingSquare = tokenStream.pop();
    if (closingSquare.name != "CLOSE_SQUARE_BRACKET") {
        throw unexpectedError(closingSquare.name, closingSquare.location, closingSquare.name, [
            `It appears that there might be a missing ending square-bracket (']'). If so, its corresponding open-square is on line ${locStart.line}, column ${locStart.column}`
        ]);
    }

    return {
        type: "ArrayLiteral",
        elems: elements,
        location: { start: locStart, end: closingSquare.location.end }
    }
}

function parseFunctionLiteral(tokenStream) {
    const loc = tokenStream.pop().location;

    expect(tokenStream, "OPEN_PAREN", "Expected a open-paren ('(') to start the arguments of this function; got a", [
        "You can't give a name to anonymous function literals. If you want to name your function, use the 'function' statement."
    ]);

    const parameterList = parseParameterList(tokenStream);

    expect(tokenStream, "CLOSE_PAREN", "Expected a close-paren (')') to finish the arguments of this function; got a");

    const body = parseStatement(tokenStream);

    return {
        type: "FunctionLiteral",
        name: wrapIdentifierString("anonymous", loc),
        args: parameterList,
        body: body,
        location: { start: loc.start, end: body.location.end }
    };
}

//ARGUMENT LIST is different from PARAMETER LIST.
//Argument list: f(3, 5, 3)
//Parameter list: function x(arg1, arg2) {...}
function parseArgumentList(tokenStream) {
    const args = [];

    let location;
    const peek = tokenStream.peek();
    if (peek.name != "CLOSE_SQUARE_BRACKET" && peek.name != "CLOSE_PAREN") {
        while (true) {
            args.push(parseExpression(tokenStream));

            if (tokenStream.peek().name == "COMMA") tokenStream.pop();
            else break;
        }
        location = { start: args[0].location.start, end: args[args.length - 1].location.end };
    } else {
        location = peek.location;
    }


    return {
        type: "ArgumentList",
        location: location,
        len: args.length,
        args: args
    }
}
function parseParameterList(tokenStream) {
    const args = [];

    let location;
    const peek = tokenStream.peek();
    if (peek.name != "CLOSE_SQUARE_BRACKET" && peek.name != "CLOSE_PAREN") {
        while (true) {
            args.push(parseParameter(tokenStream));

            if (tokenStream.peek().name == "COMMA") tokenStream.pop();
            else break;
        }
        location = { start: args[0].location.start, end: args[args.length - 1].location.end };
    } else {
        location = peek.location;
    }

    return {
        type: "ArgumentList",
        location: location,
        len: args.length,
        args: args
    }
}

function parseParameter(tokenStream) {
    const tkn = expect(tokenStream, "IDENTIFIER", "Expected an identifier for parameter name, got a", [
        "If you want to make a default value, use 'name = value' instead of 'value'."
    ]);

    const identifier = wrapIdentifierToken(tkn);

    if (tokenStream.peek().name == "EQUALS_OR_COLON") {
        return parseRelation(tokenStream, identifier);
    } else {
        return identifier;
    }
}

function parseParenGroup(tokenStream) {
    const locStart = tokenStream.pop().location.start;

    const expr = parseExpression(tokenStream);

    const closingParen = tokenStream.pop();
    if (closingParen.name != "CLOSE_PAREN") {
        throw unexpectedError(closingParen.name, closingParen.location, closingParen.name, [
            `It appears that there might be a missing ending paren (')'). If so, its corresponding open-paren is on line ${locStart.line}, column ${locStart.column}`
        ]);
    }

    return expr;
}

function parseFunctionCall(tokenStream, left) {
    const locStart = tokenStream.pop().location.start;

    const args = parseArgumentList(tokenStream);

    expect(tokenStream, "CLOSE_PAREN", "Missing end-parentheses; instead, got", [
        `It appears that there might be a missing ending paren (')') after this method call. Its corresponding open-paren is on line ${locStart.line}, column ${locStart.column}`
    ]);

    return {
        type: "FunctionCall",
        location: { start: locStart, end: args.location.end },
        args: args,
        func: left
    }
}

function parseRelation(tokenStream, left) {
    const loc = tokenStream.pop().location;

    if (left.type == "VariableReference") left = left.variable;
    if (left.type == "NumericValue") left = wrapIdentifierString(left.v + "", left.location);

    if (left.type != "Identifier") {
        throw improperContextError("Could not make a relation with a '" + left.type + "' for the title", left.location);
    }

    return {
        type: "TitledArgument",
        value: parseExpression(tokenStream, 0),
        name: left,
        location: loc
    }
}

function binaryLeftAssociativeOperator(precedence) {
    return function parse(tokenStream, leftSide) {
        const tkn = tokenStream.pop();
        const loc = tkn.location;
        const op = tkn.content;

        const rightSide = parseExpression(tokenStream, precedence);

        return {
            type: "OperatorExpression",
            location: loc,
            left: leftSide,
            right: rightSide,
            operator: op
        }
    }
}

function parseDotGetter(tokenStream, left) {
    //we don't need the dot
    tokenStream.pop();

    const identifier = tokenStream.pop();
    if (identifier.name != "IDENTIFIER") {
        throw improperContextError("Expected a property name to get. Got a " + identifier.name, identifier.location, [
            "To get an integer or variable key, use an array style-getter: e.g. 'table[3]' to get the '3' key."
        ]);
    }

    return {
        type: "TailedValue",
        head: left,
        tail: wrapIdentifierToken(identifier),
        location: { start: left.location.start, end: identifier.location.end }
    };
}

function wrapIdentifierToken(identifier) {
    return {
        type: "Identifier",
        location: identifier.location,
        value: identifier.content
    };
}

function wrapIdentifierString(name, location) {
    return {
        type: "Identifier",
        location: location,
        value: name
    }
}

function wrapVariableReferenceOnIdentifierToken(identifier) {
    return {
        type: "VariableReference",
        location: identifier.location,
        variable: wrapIdentifierToken(identifier)
    };
}

function parseArrayGetter(tokenStream, left) {
    const locStart = tokenStream.pop().location.start;

    const tail = parseExpression(tokenStream, 0);

    expect(tokenStream, "CLOSE_SQUARE_BRACKET", "Missing end-square-bracket; instead, got", [
        `It appears that there might be a missing ending squre-bracket (']') after this array-style property get. Its corresponding open-square is on line ${locStart.line}, column ${locStart.column}`
    ]);

    return {
        type: "TailedValue",
        head: left,
        tail: tail,
        location: { start: locStart, end: tail.location.end }
    };
}

function parseDelegatorExpression(tokenStream) {
    const locStart = tokenStream.pop().location.start;

    let hasParens = false;
    if (tokenStream.peek().name == "OPEN_PAREN") {
        hasParens = true;
        tokenStream.pop();
    }

    const strLit = expect(tokenStream, "STRING_LITERAL", "Expected a string literal as the first argument to delegate; got a");
    const delegateTo = wrapStringLiteralToken(strLit);

    if (tokenStream.peek().name == "COMMA") tokenStream.pop();

    const args = parseArgumentList(tokenStream);

    let locEnd;
    if (hasParens) {
        locEnd = expect(tokenStream, "CLOSE_PAREN", "Expected a close-paren to finish delegate; got a").location.end;
    } else {
        locEnd = args.location.end;
    }

    return {
        type: "DelegatorExpression",
        delegateTo: delegateTo,
        args: args,
        location: { start: locStart, end: locEnd }
    };
}