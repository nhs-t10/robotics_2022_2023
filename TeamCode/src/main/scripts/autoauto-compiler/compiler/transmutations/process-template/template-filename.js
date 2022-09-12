"use strict";

const { existsSync } = require("fs");
var path = require("path");
const commandLineInterface = require("../../../../command-line-interface");

module.exports = path.resolve(".", commandLineInterface["output-java-template"]);
