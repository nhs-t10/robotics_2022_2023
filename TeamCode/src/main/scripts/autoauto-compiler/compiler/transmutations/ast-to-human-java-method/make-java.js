var nicelyIndent = require("./good-indenting");

module.exports = function(ast, functionLookup) {
    var states = ast.statepaths.map(x=> 
        x.statepath.states.map((y,i)=> {
            y.index = i;
            y.statepathName = x.label;
            return y;
        }))
        .flat();

    states.forEach((x,i)=>x.globalIndexId = i + 1);

    var variablePool = makeVariablePool();

    states.forEach(x => x.case = stateToJavaCase(x, variablePool, states, functionLookup));

    var switchStatement = "switch(step) {\n" + states.map(x=>`case ${x.globalIndexId}:\n${x.case}\nbreak;`).join("\n") + "}";

    var src = `return new AutoautoProgram() {
        int step = 1;
        ${variablePool.toString()}
        public void init() {}
        public void loop() {
            ${switchStatement}
        }
        public void stepInit() {}
        public org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope getScope() { return null; }
        public void setScope(org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope s) {};
        public Location getLocation() { return null; }
        public void setLocation(Location s) {};
        public AutoautoProgram clone() {return this;}
    };`

    return nicelyIndent(src, 2);
}

function stateToJavaCase(state, variables, states, functionLookup) {
    return state.statement.map(x => statementToJava(x, variables, states, functionLookup)).join("\n");
}

function statementToJava(stmt, variables, states, functionLookup) {
    switch(stmt.type) {
        case "Block":
            return "{" + stateToJavaCase(stmt.state, variables, states, functionLookup) + "\n}";
        case "PassStatement":
            return "//pass";
        case "AfterStatement":
            var initedVar = variables.add("timerStarted_" + variables.nonce(), "boolean");
            var timeVar = variables.add("timer_" + variables.nonce(), unitValueType(stmt.unitValue));

            if(stmt.unitValue.type != "UnitValue") throw {
                text: "Dynamic after statements are not allowed in the Human-Readable Java CompilerMode.",
                location: stmt.location
            }

            var getter = unitValueGetter(stmt.unitValue, functionLookup);

            return `if(${initedVar} == false) {
                ${timeVar} = ${getter};
                ${initedVar} = true;
            }
            if(Math.abs(${getter} - ${timeVar}) > ${stmt.unitValue.value.v}) {
                ${initedVar} = false;
                ${statementToJava(stmt.statement, variables, states, functionLookup)}
            }`;
        case "IfStatement":
            return `if (${valueToJava(stmt.conditional, variables, functionLookup)}) ${statementToJava(stmt.statement, variables, states, functionLookup)}`;
        case "NextStatement":
            return `step++;`
        case "SkipStatement":
            return `step += ${stmt.skip};`;
        case "LetStatement":
            var v = valueToJava(stmt.variable, variables, functionLookup);
            return `${v} = ${valueToJava(stmt.value, variables, functionLookup)};`;
        case "GotoStatement":
            var destName = stmt.path.value;
            var destStartState = states.find(x=>x.statepathName == destName);
            if(!destStartState) return `step = -1;`;

            return `step = ${destStartState.globalIndexId};\n`;
        case "ValueStatement":
            return valueToJava(stmt.call, variables, functionLookup) + ";";
        default:
            throw {
                text: `A ${stmt.type} is not allowed in the Human-Readable Java CompilerMode.`,
                __stmt: stmt,
                location: stmt.location
            };
    }
}

function valueToJava(value, variables, functionLookup) {
    switch(value.type) {
        case "OperatorExpression":
        case "ComparisonOperator":
            return `${valueToJava(value.left, variables, functionLookup)} ${value.operator} ${valueToJava(value.right, variables, functionLookup)}`;
        case "BooleanLiteral":
            return value.value + "";
        case "StringLiteral":
            return JSON.stringify(value.str);
        case "UnitValue":
            return valueToJava(value.value, variables, functionLookup);
        case "BooleanLiteral":
            return value.value;
        case "NumericValue":
            return value.v + "f";
        case "FunctionLiteral":
            throw {
                text: "Function literals are not allowed in the Human-Readable Java CompilerMode.",
                location: value.location
            };
        case "VariableReference":
            return variables.add(value.variable.value, "float");
        case "Identifier":
            return variables.add(value.value, "float");
        case "FunctionCall":
            var functionName = value.func;
            if(functionName.type != "VariableReference") throw {
                text: "Calling a non-variable is not allowed in the Human-Readable Java CompilerMode.",
                location: value.location
            } 
            functionName = functionName.variable.value;

            return `${functionLookup(functionName)}.${functionName}(${
                value.args.args.map(x => valueToJava(x, variables, functionLookup)).join(",")
            })`;
        default:
            throw {
                text: `A ${value.type} is not allowed in the Human-Readable Java CompilerMode.`,
                __stmt: value,
                location: value.location
            };
    }
}

function makeVariablePool() {
    var num = 0;

    var vars = {};
    var prefixUsedCounts = {};

    return {
        add: function(prefix, type) {
            type = type || "float";
        
            if(vars[prefix]) return vars[prefix].name;

            var fullName = `variable_${prefix}`;
            vars[prefix] = {
                name: fullName,
                type: type
            };

            return fullName;
        },
        nonce: function() {
            return num++;
        },
        toString: function() {
            return Object.values(vars).map(x=>`${x.type} ${x.name};`).join("\n")
        }
    }
}

function unitValueType(unitValue) {

    return ({
        "cm":  "float",
        "degs": "float",
        "ms": "long"
    })[unitValue.unit.value];
}

function unitValueGetter(unitValue, functionLookup) {
    var unit = unitValue.unit.value;
    var value = unitValue.value.v;

    var methodName = ({
        "cm":  functionLookup("getCentimeters") + ".getCentimeters()",
        "degs": functionLookup("getThirdAngleOrientation") + ".getThirdAngleOrientation()",
        "ms": "System.currentTimeMillis()"
    })[unit];

    if(methodName) {
        return methodName;
    } else {
        throw {
            text: "Unit conversion is not supported in the Human-Readable Java CompilerMode. Please use `ms`, `cm`, or `degs`.",
            location: unitValue.location
        }
    }
}