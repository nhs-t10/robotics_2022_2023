autoautoFile = 
  c:commentedWhitespace f:frontMatter? _ u:unlabeledStatepath? s:labeledStatepath* commentedWhitespace {
  	return {
      comments: c,
      type: "Program", location: location(),
      frontMatter: f,
      statepaths: u ? [u].concat(s) : s
    }
  }

frontMatter =
 DOLLAR_SIGN _ head:frontMatterKeyValue tail:(COMMA _ frontMatterKeyValue)* _ DOLLAR_SIGN {
   return {
     type: "FrontMatter", location: location(),
     values: [head].concat(tail.map(x=>x[2]))
   }
 }

frontMatterKeyValue =
 c:commentedWhitespace k:IDENTIFIER COLON _ v:value _ {
   return {
     comments: c,
     type: "FrontMatterKeyValue", location: location(),
     key: k,
     value: v
   }
 }

unlabeledStatepath = c:commentedWhitespace s:statepath _ {
    return { comments: c, type: "LabeledStatepath", location: location(), statepath: s, label: "<init>" }
}

labeledStatepath =
 c:commentedWhitespace l:statepathLabelIdentifier COLON _ s:statepath _  {
   return { comments: c, type: "LabeledStatepath", location: location(), statepath: s, label: l.value }
 }


statepath = head:state tail:(SEMICOLON state)* _ (SEMICOLON _)? {
  	return {
      type: "Statepath", location: location(),
      states: [head].concat(tail.map(x=>x[1]))
    }
  }

state =
  c:commentedWhitespace head:statement tail:(COMMA statement)* _ {
    return {
      comments: c,
      type: "State", location: location(),
      statement: [head].concat(tail.map(x=>x[1]))
    }
  }

statement = singleStatement / multiStatement

multiStatement "block" = c:commentedWhitespace OPEN_CURLY_BRACKET s:state? CLOSE_CURLY_BRACKET _ {
	return { type: "Block", 
          state: s || {comments:[], type: "State", location: location(), statement: []},
          location: location(), 
          comments: c 
        };
}

singleStatement "statement" =
  c:commentedWhitespace  s:(returnStatement/provideStatement/passStatement/valueStatement/funcDefStatement/afterStatement/gotoStatement/ifStatement/letStatement/nextStatement/skipStatement)

  {
    if(s.comments && s.comments.length) s.comments = c.concat(s.comments);
    else s.comments = c;
    
    return s;
  }
 
provideStatement = PROVIDE _ v:value {return { type: "ProvideStatement", value:v, location: location() }; }

delegatorExpression = DELEGATE _ f:(
	OPEN_PAREN d:stringLiteral _ a:(COMMA valueList)? CLOSE_PAREN { return [d, (a||[])[1] ] } / 
    d:stringLiteral a:(_ WITH _ valueList)? { return [d, (a||[])[3] ]; }
) {
   return {
       type: "DelegatorExpression",
       delegateTo: f[0],
       location: location(),
       args: f[1] || {type:"ArgumentList",args:[], location: location()}
   }
}
  
returnStatement = RETURN u:(_ value)? { 
	return { type: "ReturnStatement", location:location(), value:u?u[1]:undefined }; 
}

afterStatement =
 AFTER _ u:value _ s:statement { return { type: "AfterStatement", location: location(), unitValue: u, statement: s } }

valueStatement =
 f:value { return { type: "ValueStatement", location: location(), call: f } }

funcDefStatement = FUNCTION _
	name:IDENTIFIER _ OPEN_PAREN args:argumentList? _ CLOSE_PAREN _ b:statement
    { return { type: "FunctionDefStatement", name: name, args: args || {type:"ArgumentList",args:[], location: location()}, body: b, location: location() }; }

gotoStatement =
 GOTO _ p:IDENTIFIER { return { type: "GotoStatement", location: location(), path: p } }

ifStatement =
 (IF/WHEN) _ OPEN_PAREN t:value CLOSE_PAREN s:statement e:elseClause? {
 return { type: "IfStatement", location: location(), conditional: t, statement: s, elseClause: e || {type: "PassStatement", location: location()} } }

elseClause = c:commentedWhitespace (ELSE / OTHERWISE) _ s:statement {
s.comments = c.concat(s.comments);
return s;
}

passStatement = PASS { return { type: "PassStatement", location: location() } }

letStatement =
 LET _ v:IDENTIFIER _ EQUALS _ val:value { return { type: "LetStatement", location: location(), variable: v, value: val } }
 /
 LET _ v:baseExpression _ EQUALS _ val:value { return { type: "LetPropertyStatement", location: location(), variable: v, value: val } }

nextStatement =
 NEXT { return { type: "NextStatement", location: location() }   }

skipStatement =
 SKIP s:value  { return { type: "SkipStatement", location: location(), skip: s }   }

value =
 c:commentedWhitespace  b:boolean _ { b.comments = c; return b }



valueInParens =
  c:commentedWhitespace OPEN_PAREN v:value CLOSE_PAREN _ { v.comments = c.concat(v.comments); return v; }

modulo =
 l:baseExpression _ r:(MODULUS _ baseExpression)? {
   if(!r) return l;

    return { type: "OperatorExpression", location: location(), operator: r[0], left: l, right: r[2] }
}

exponent =
 l:modulo _ r:(EXPONENTIATE _ modulo)? {
   if(!r) return l;

    return { type: "OperatorExpression", location: location(), operator: r[0], left: l, right: r[2] }
}

product =
 l:exponent _ tail:(o:(MULTIPLY / DIVIDE) _ r:product { return [o, r]; })* {
   if(!tail.length) return l;

  var r = { type: "OperatorExpression", location: location(), left: l };
  var tar = r;
  for(var i = 0; i < tail.length - 1; i++) {
    tar.operator = tail[i][0];
    var newTar = { type: "OperatorExpression", location: location(), left: tail[i][1] };
    tar.right = newTar;
    tar = newTar;
  }
  tar.operator = tail[tail.length - 1][0];
  tar.right = tail[tail.length - 1][1];
  return r;
}

sum = l:product _ tail:(o:(PLUS / MINUS) _ r:product { return [o, r]; })* {
  if(!tail.length) return l;

  var r = { type: "OperatorExpression", location: location(), left: l };
  var tar = r;
  for(var i = 0; i < tail.length - 1; i++) {
    tar.operator = tail[i][0];
    var newTar = { type: "OperatorExpression", location: location(), left: tail[i][1] };
    tar.right = newTar;
    tar = newTar;
  }
  tar.operator = tail[tail.length - 1][0];
  tar.right = tail[tail.length - 1][1];
  return r;
}

arithmeticValue =
 s:sum {
   return s;
 }

atom =
 _  x:(arrayLiteral / stringLiteral / relationLiteral / unitValue / booleanLiteral / NUMERIC_VALUE / functionLiteral / variableReference / valueInParens / delegatorExpression) _ {
   return x;
 }

baseExpression = a:atom _ t:tail* _ {
     if(!t.length) return a;

     var value = a;
     for(var i = 0; i < t.length; i++) {
       if(t[i].type == "FunctionCall") value = {type: "FunctionCall",func: value, args: t[i].args, location: t[i].location};
       else if(t[i].type == "TailedValue") value = { type: "TailedValue", head: value, tail: t[i].tail, location: t[i].location };
     }

     return value;
}

settableTail = arrayStyleGetter / dotStyleGetter
tail = settableTail / callFunction

callFunction "function call" = OPEN_PAREN _ a:valueList? CLOSE_PAREN { return { type: "FunctionCall", func: null, args: a || {type:"ArgumentList",args:[], location: location()}, location: location() } }

arrayStyleGetter "array-style property getter (obj[i])" = OPEN_SQUARE_BRACKET a:value CLOSE_SQUARE_BRACKET { return {type: "TailedValue", head: null, tail: a, location: location() } }

dotStyleGetter "dot-style property getter (obj.prop)" = DOT a:IDENTIFIER { return {type: "TailedValue", head: null, tail: a, location: location() }; }

variableReference =
 i:IDENTIFIER { return { type: "VariableReference", location: location(), variable: i }; }

arrayLiteral =
 OPEN_SQUARE_BRACKET _ a:valueList? CLOSE_SQUARE_BRACKET {
 return {
   type: "ArrayLiteral", location: location(),
   elems: a || {type:"ArgumentList",args:[], location: location()}
 }
}

functionLiteral = FUNC _ name:IDENTIFIER? _ OPEN_PAREN args:argumentList? _ CLOSE_PAREN _ b:statement
    { return { type: "FunctionLiteral", name: name, args: args || {type:"ArgumentList",args:[], location: location()}, body: b, location: location() }; }

booleanLiteral =
TRUE { return { type: "BooleanLiteral", location: location(), value: true }; }
 / FALSE { return { type: "BooleanLiteral", location: location(), value: false }; }

boolean =
 l:arithmeticValue _ o:comparisonOperator _ r:arithmeticValue { return { type: "ComparisonOperator", location: location(), left: l, operator: o, right: r }; }
 / r:arithmeticValue { return r; }

comparisonOperator "comparison operator" =
 o:(COMPARE_LTE / COMPARE_LT / COMPARE_EQ / COMPARE_NEQ / COMPARE_GTE / COMPARE_GT) { return o; }


stringLiteral =
 DOUBLE_QUOTE q:NON_QUOTE_CHARACTER* DOUBLE_QUOTE { return { type: "StringLiteral", location: location(), str: q.join("") } }

unitValue =
 v:(NUMERIC_VALUE/dynamicValue) u:(IDENTIFIER/dynamicValue) { return {type: "UnitValue", location: location(), value: v, unit: u } }

argumentList =
 head:argument tail:(COMMA argument)* {
 return {
   type: "ArgumentList", location: location(),
   len: tail.length + 1,
   args: [head].concat(tail.map(x=> x[1]))
 }
}

relationLiteral = titledArgument

valueList = head:value tail:(COMMA value)* {
  return {
   type: "ArgumentList", location: location(),
   len: tail.length + 1,
   args: [head].concat(tail.map(x=> x[1]))
 }
}

argument = titledArgument / _ f:IDENTIFIER _ { return f; }

titledArgument = _ n:IDENTIFIER _ TITLE_ARG_SEP _ v:value {
  return { type: "TitledArgument", value: v, name: n, location: location()  };
}

dynamicValue = OPEN_SQUARE_BRACKET v:value CLOSE_SQUARE_BRACKET { return { type: "DynamicValue", value: v, location: location() } }

_ "whitespace" = [ \t\n\r]*

commentedWhitespace "comment" =
    _ c:( comment _)* { return c.map(x=>x[1]); }

comment = "//" t:[^\n]* (EOL/EOF) { return t.join("") }
  / "/*" t:commentText* END_OF_COMMENT { return t.join("") }

statepathLabelIdentifier = HASHTAG i:IDENTIFIER { return i; }

  END_OF_COMMENT "end of comment" = "*/"

commentText = !"*/" f:. { return f; }

NON_QUOTE_CHARACTER = [^"]
DOUBLE_QUOTE = '"'

HASHTAG "hashtag" = "#"

  EOL "end of line" = '\r'? '\n'
  COLON "colon" =
    ":"
SEMICOLON "semicolon" =
    ";"
COMMA "comma" =
    ","
AFTER =
    "after"
FUNC = "func"
FUNCTION = "function"
GOTO =
    "goto"
IF =
    "if"
WHEN = "when"
OPEN_PAREN "opening paren" =
    "("
CLOSE_PAREN "closing paren" =
    ")"
LET =
    "let"
TITLE_ARG_SEP "key/value separator (= or :)" =
    EQUALS / COLON
EQUALS "equals sign" =
    "="
NEXT =
    "next"
SKIP =
    "skip"
COMPARE_LTE =
    "<="
COMPARE_EQ =
    "=="
COMPARE_NEQ =
    "!="
COMPARE_GTE =
    ">="
COMPARE_GT =
    ">"
COMPARE_LT =
    "<"
TRUE =
    "true"
FALSE =
    "false"
PLUS "arithmetic operator" =
    "+"
MINUS "arithmetic operator" =
    "-"
DIVIDE "arithmetic operator" =
    "/"
MULTIPLY "arithmetic operator" =
    "*"
MODULUS "arithmetic operator" =
    "%"
EXPONENTIATE "arithmetic operator" =
    "^" / "**"
DOLLAR_SIGN "dollar sign" =
    "$"
OPEN_SQUARE_BRACKET "opening square bracket" =
    "["
CLOSE_SQUARE_BRACKET "closing square bracket" =
    "]"
OPEN_CURLY_BRACKET "opening curly bracket" =
    "{"
CLOSE_CURLY_BRACKET "closing curly bracket" =
    "}"
DOT "dot (.)" = "."
RETURN = "return"
PROVIDE = "provide"
DELEGATE = "delegate"
PASS = "pass"
ELSE = "else"
OTHERWISE = "otherwise"
WITH = "with"
IDENTIFIER =
    l:(LETTER / DIGIT)+
    &{
    var name = l.join("");
    var reserved = ["if", "goto", "skip", "let", "next", "function", "func", "when", "after","return","provide","delegate"];
    if(reserved.indexOf(name) == -1) return true;
    else return false;
    }
    { return { type: "Identifier", location: location(), value: l.join("") } }
DIGIT "digit" = [0-9]
LETTER "letter" = [A-Za-z_]
NUMERIC_VALUE = m:MINUS? v:(
    h:DIGIT* '.' t:DIGIT+ { return h.join("") + "." + t.join("") }
    /
    d:DIGIT+ { return d.join(""); })
{
    return { type: "NumericValue", location: location(), v: parseFloat((m||"") + v ) }
}
NUMERIC_VALUE_WITH_UNIT = n:NUMERIC_VALUE u:IDENTIFIER {
    return { type: "UnitValue", location: location(), value: n, unit: u }
}
EOF = !.