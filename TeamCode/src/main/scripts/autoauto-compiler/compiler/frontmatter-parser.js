module.exports = function parseFrontmatter(fileContent) {    
    var MODE = "base";
    var frontmatterObject = {}, ctx = { key: "", value: "", group: 0, afterCommentReturnInFrontmatter: false };
    const fContLength = fileContent.length;
    for (var i = 0; i < fContLength; i++) {
        MODE = evaluateParseAtCharacter(MODE, fileContent[i], frontmatterObject, ctx);
        if(MODE == "end") break;
    }
    return frontmatterObject;
}

function evaluateParseAtCharacter(MODE, char, frontmatterObject, ctx) {
    
    switch (MODE) {
        case "base":
            if (char == "$") return "in_frontmatter";
            else if(char == "\"" || char == "'") return "end";
            else if (char == "/") { ctx.afterCommentReturnInFrontmatter = false; return "beginning_comment";  }
            else return "base";
        case "in_frontmatter":
            if(char == "/") {
                ctx.afterCommentReturnInFrontmatter = true;
                return "beginning_comment";
            } else if (isAlphanumeric(char)) {
                ctx.key = char;
                return "in_key";
            } else if(char == "$") {
                return "end";
            } else {
                return "in_frontmatter";
            }
        case "in_key":
            if(isAlphanumeric(char)) {
                ctx.key += char;
                return "in_key";
            } else {
                return "before_value";
            }
        case "before_value":
            if (isWhitespace(char) || char == "=" || char == ":") {
                return "before_value";
            } else {
                ctx.value = char;
                if (isOpenGroupingChar(char)) ctx.group++;
                else if (isCloseGroupingChar(char)) ctx.group--;
                else if (char == "\"") return "in_quotes";
        
                return "in_value";
            }
        case "in_value":
            if(isAlphanumeric(char) || char == ".") {
                ctx.value += char;
            } else if (isOpenGroupingChar(char)) {
                ctx.group++;
                ctx.value += char;
            } else if(isCloseGroupingChar(char)) {
                ctx.group--;
                ctx.value += char;
            } else if(char == "\"") {
                ctx.value += char;
                return "in_quotes";
            } else if(ctx.group == 0) {
                applyKv(frontmatterObject, ctx);
                
                if(char == "$") return "end";
                else return "in_frontmatter";
            }
            return "in_value";
        case "in_quotes":
            ctx.value += char;
            if(char == "\\") {
                return "in_escape";
            } else if (char == "\"") {
                return "in_value";
            } else {
                return "in_quotes";
            }
        case "in_escape":
            ctx.value += char;
            return "in_quotes";
        case "beginning_comment":
            if(char == "/") {
                return "in_line_comment";
            } else if(char == "*") {
                return "in_multiline_comment"
            } else {
                return ctx.afterCommentReturnInFrontmatter ? "in_frontmatter" : "base";
            }
        case "in_line_comment":
            if (char == "\n") return ctx.afterCommentReturnInFrontmatter ? "in_frontmatter" : "base";
            else return "in_line_comment";
        case "in_multiline_comment":
            if(char == "*") return "maybe_multiline_comment_end";
            else return "in_multiline_comment";
        case "maybe_multiline_comment_end":
            if (char == "/") return ctx.afterCommentReturnInFrontmatter ? "in_frontmatter" : "base";
            else return "in_multiline_comment";
    }
}

function applyKv(frontmatterObject, ctx) {
    try {
        frontmatterObject[ctx.key] = JSON.parse(ctx.value);
    } catch(e) {
        frontmatterObject[ctx.key] = ctx.value;
    }
    ctx.key = ctx.value = "";
}

function isWhitespace(char) {
    return char == " " || char == "\t" || char == "\r" || char == "\n";
}
function isOpenGroupingChar(char) {
    return char == "[" || char == "(" || char == "{";
}
function isCloseGroupingChar(char) {
    return char == "]" || char == ")" || char == "}";
}

function isAlphanumeric(str) {
    const code = str.charCodeAt(0);
    return ((code > 47 && code < 58) || // numeric
        (code > 64 && code < 91) || // uppercase alphabet
        (code > 96 && code < 123)) || //lowercase alphabet
        code == 95 || // "_"
        code == 45; // "-""
}