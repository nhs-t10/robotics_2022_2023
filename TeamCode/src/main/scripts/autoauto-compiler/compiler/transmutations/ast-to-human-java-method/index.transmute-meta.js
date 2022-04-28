module.exports = {
    requires: ["text-to-syntax-tree"],
    id: "ast-to-human-java-method",
    type: "transformation",
    readsFiles: [require("./template-file-name")]
}