const path = require("path");
const fs = require("fs");

module.exports = folderScanner


async function* folderScanner(folder, ending) {
    let folderContents = await getMatchingFolderContents(folder, ending);


    for(var i = 0; i < folderContents.length; i++) {
        const subfile = folderContents[i];

        if(subfile.directory) {
            var subfolder = await getMatchingFolderContents(subfile.name, ending);
            folderContents = folderContents.concat(...subfolder);
        } else {
            yield path.normalize(subfile.name);
        }
    }
    return undefined;
}

async function getMatchingFolderContents(folder, ending) {
    return new Promise(function(resolve, reject) {
        fs.readdir(folder, {
            withFileTypes: true
        }, function(err, unsorted) {
            if(err) reject(err);
            
            const foldersFirst = [];

            for (const x of unsorted) {
                if (x.isDirectory()) foldersFirst.push({
                    name: path.join(folder, x.name),
                    directory: true
                });
            }
            for (const x of unsorted) {
                if (x.isFile() && x.name.endsWith(ending)) foldersFirst.push({
                    name: path.join(folder, x.name)
                });
            }

            resolve(foldersFirst);
        });
    });
}