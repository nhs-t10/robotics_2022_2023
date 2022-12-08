"use strict";

var PRIMITIVES_PACKAGE = `dev.autoauto.runtime.values`;

module.exports = function(packge, className, javaParts) {
    return template(packge, className, javaParts);
}

function template(packge, className, javaParts) {
    return `package ${packge};
    import ${PRIMITIVES_PACKAGE}.*;
    public class ${className} extends ${javaParts.fullExtendsName} {
        private final static ${javaParts.constants}

        private final static ${javaParts.bytecodes}

        public ${className}() {
            super(${javaParts.instructions});
        }
        
        public static void main(String[] args) {
            ${className} t = new ${className}();
            
            while(true) t.loop();
        }
    }`
}