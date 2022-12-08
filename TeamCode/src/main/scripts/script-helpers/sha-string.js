const crypto = require("crypto");

module.exports = {
    sha: sha,
    shaJSON: shaJSON    
}

function sha(k) {
    return crypto.createHash("sha256").update(k + "").digest("hex");
}

function shaJSON(k) {
    return crypto.createHash("sha256").update(JSON.stringify(k)).digest("hex");
}