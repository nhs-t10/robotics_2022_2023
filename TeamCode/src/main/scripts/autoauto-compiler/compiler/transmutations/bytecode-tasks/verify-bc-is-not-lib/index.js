const androidStudioLogging = require("../../../../../script-helpers/android-studio-logging");
const formatFrontmatterBlock = require("../../../../../script-helpers/format-helpers/format-frontmatter");
const systemVariableNames = require("../../../data/system-variable-names");
const bytecodeSpec = require("../bytecode-spec");

module.exports = async function(context) {

    
    const libraryProofLocation = verifyIsNotLib(context.inputs["text-to-syntax-tree"]);

    if(libraryProofLocation === undefined) {
        context.status = "pass";
    }
    else {
        androidStudioLogging.sendTreeLocationMessage({
            kind: "ERROR",
            text: "Attempt to compile helper file as opmode",
            original: 
`This file uses module_args or provide(). That indicates that it's a helper file (a.k.a. a 'library', or 'lib')-- however,
it's being compiled as a full OpMode. This does not work; a helper file must be compiled as a library.
This file will fail to compile, and all files which depend on it will fail also.
To correct this, add the \`compilerMode: "lib"\` property to your frontmatter:
${formatFrontmatterBlock(Object.assign({}, context.fileFrontmatter, {compilerMode: "lib"}), { compilerMode: "+" })}
`,
            location: libraryProofLocation
        }, context.sourceFullFileName, "ERROR");
    }
}

function verifyIsNotLib(ast) {

    if(ast.type == "ProvideStatement") return ast.location;
    if(ast.type == "VariableReference" && ast.variable.value == systemVariableNames.MODULE_ARGS) return ast.location;

    for(var key in ast) {
        const o = ast[key];
        if(typeof o === "object" && o != null && typeof o.type === "string") {
            const v = verifyIsNotLib(o);
            if(v !== undefined) return v;
        } else if(Array.isArray(o)) {
            for(const _o of o) {
                const v = verifyIsNotLib(_o);
                if(v !== undefined) return v;
            }
        }
    }
}