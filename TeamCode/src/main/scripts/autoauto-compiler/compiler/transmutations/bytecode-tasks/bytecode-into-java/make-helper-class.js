var PRIMITIVES_PACKAGE = `org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives`;

module.exports = function(package, className, javaParts) {
    return template(package, className, javaParts);
}

function template(package, className, javaParts) {
    return `package ${package};
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