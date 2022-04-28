module.exports = {
    id: "bc-basic-dead-code-elimination",
    requires: ["build-cgraph", "inverted-cgraph", "bc-condense-constants"],
    type: "transformation"
}