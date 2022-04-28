module.exports = function run(context) {
    context.output = context.inputs["type-checking"];
    context.status = "pass";
}