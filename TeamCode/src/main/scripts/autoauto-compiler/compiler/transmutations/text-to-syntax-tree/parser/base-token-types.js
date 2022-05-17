module.exports = [
    { name: "HASHTAG", regex: /#/ },
    { name: "SEMICOLON", regex: /;/ },
    { name: "COMMA", regex: /,/ },
    { name: "DOLLAR_SIGN", regex: /\$/ },
    
    { name: "NUMERIC_VALUE_WITH_UNIT", regex: /(-?\d+(?:\.\d+)?(?:e[+-]?\d+)?)([a-zA-Z]+)/ },
    { name: "NUMERIC_VALUE", regex: /(-?\d+(?:\.\d+)?(?:e[+-]?\d+)?)/ },
    
    { name: "STRING_LITERAL", regex: /"(\\.|[^"])*"/ },
    { name: "COMMENT", regex: /\/\/.*|\/\*([^*]*\*)+\// },
    
    { name: "TRUE", regex: /true\b/ },
    { name: "FALSE", regex: /false\b/ },
    
    { name: "OPEN_PAREN", regex: /\(/ },
    { name: "CLOSE_PAREN", regex: /\)/ },
    { name: "COMPARE_LTE", regex: /<=/ },
    { name: "COMPARE_EQ", regex: /==/ },
    { name: "COMPARE_NEQ", regex: /!=/ },
    { name: "COMPARE_GTE", regex: />=/ },
    { name: "COMPARE_GT", regex: />/ },
    { name: "COMPARE_LT", regex: /</ },
    { name: "EXPONENTIATE", regex: /\^|\*\*/ },
    { name: "AND", regex: /&&/ },
    { name: "OR", regex: /\|\|/ },
    
    { name: "PLUS", regex: /\+/ },
    { name: "MINUS", regex: /-/ },
    { name: "DIVIDE", regex: /\// },
    { name: "MULTIPLY", regex: /\*/ },
    { name: "MODULUS", regex: /%/ },
    { name: "OPEN_SQUARE_BRACKET", regex: /\[/ },
    { name: "CLOSE_SQUARE_BRACKET", regex: /\]/ },
    { name: "DOT", regex: /\./ },
    { name: "NOT", regex: /!/ },
    { name: "EQUALS_OR_COLON", regex: /=|:/ },
    
    { name: "FUNC", regex: /func\b/ },
    { name: "DELEGATE", regex: /delegate\b/ },
        { name: "WITH", regex: /with\b/ },
    
    { name: "OPEN_CURLY_BRACKET", regex: /\{/ },
    { name: "CLOSE_CURLY_BRACKET", regex: /\}/ },
    
    { name: "AFTER", regex: /after\b/ },
    { name: "FUNCTION", regex: /function\b/ },
    { name: "GOTO", regex: /goto\b/ },
    { name: "IF", regex: /if\b|when\b/ },
        { name: "ELSE", regex: /else\b|otherwise\b/ },
    { name: "RETURN", regex: /return\b/ },
    { name: "PROVIDE", regex: /provide\b/ },
    { name: "PASS", regex: /pass\b/ },
    { name: "NEXT", regex: /next\b/ },
    { name: "SKIP", regex: /skip\b/ },
    { name: "LET", regex: /let\b/ },
    
    { name: "IDENTIFIER", regex: /[a-zA-Z]\w*/ }
]