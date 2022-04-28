var cp = require("child_process");
var fs = require("fs");
var path = require("path");
var cache = require("../../cache");

module.exports = function() {
    var commits = getGenealogyCommits();

    var cachedBuilds = cache.get(commits, false);
    if(cachedBuilds) return cachedBuilds;


    var objects = [];
    var objectToCommitMap = {}

    for(var i = 0; i < commits.length; i++) {
        var commitObjects = getReleventObjectsInCommit(commits[i])
        objects = objects.concat(commitObjects);
        commitObjects.forEach(x=>objectToCommitMap[x] = commits[i]);
    }

    objects = Array.from(new Set(objects));

    var builds = {};

    for(var i = 0; i < objects.length; i++) {
        Object.assign(builds, getBuildsFromFileHash(objects[i], objectToCommitMap[objects[i]]));
    }
    
    var buildArr = Object.values(builds);
    cache.save(commits, buildArr);
    return buildArr;
}

function getGenealogyCommits() {
    console.info(getGitRootDirectory());
    return cp.execSync("git rev-list HEAD -- **/genealogy/*", {
        cwd: getGitRootDirectory()
    })
    .toString()
    .split("\n")
    .map(x=>x.trim())
}

function getGitRootDirectory() {
    var dir = __dirname.split(path.sep);
    while(true) {
        var dirPath = dir.join(path.sep);
        if(fs.existsSync(path.join(dirPath, ".git"))) break;
        else dir.pop();
    }
    return dir.join(path.sep);
}

function getBuildsFromFileHash(hash, commit) {
    var file = cp.execSync(`git cat-file blob ${hash}`).toString();
    try {
        var parse = JSON.parse(file);
        var builds = {};
        for(var i = 0; i < parse.builds.length; i++) {
            var id = parse.builds[i].name;

            builds[id] = parse.builds[i];

            builds[id].buildNumber = parse.buildCount - ( parse.builds.length - (i + 1));
            builds[id].cognomen = parse.cognomen;
            builds[id].browserHash = parse.browser;
            builds[id].commit = commit
        }
        return builds;
    } catch(e) {}
}

function getReleventObjectsInCommit(commitID) {
    try {
        var genealogy = "TeamCode/src/main/java/org/firstinspires/ftc/teamcode/auxilary/buildhistory/genealogy";
        var treeID = cp.execSync(`git rev-parse ${commitID} -- ${genealogy}`).toString().split("\n")[0].trim();

        var objects = cp.execSync(`git ls-tree -rt --full-tree ${treeID} ${genealogy}`)
            .toString()
            .trim()
            .split("\n")
            .filter(x=>
                x
                .split(" ")[1] == "blob")
            .map(x=>
                x
                .split(" ")[2]
                .split("\t")[0]
            );
        return objects;
    } catch(e) {
        return [];
    }
}