var functionLoader = require("./loader");

module.exports = async function run(context, contexts) {
    context.output = await functionLoader();
    context.status = "pass";
}