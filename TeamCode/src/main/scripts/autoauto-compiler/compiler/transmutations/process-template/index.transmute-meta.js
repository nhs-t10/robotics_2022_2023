module.exports = {
    requires: ["make-runtime-flag-setters", "text-to-syntax-tree", "get-json-outline-java", "get-result-package"],
    id: "process-template",
    type: "information",
    readsFiles: [require("./template-filename")]
}