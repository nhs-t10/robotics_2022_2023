const path = require("path");
const fs = require("fs");

module.exports = folderScanner

/**
 * @callback includeFileCallback
 * @param {string} filename The file's name. This does not include the folder or path.
 * @returns {boolean} Whether or not to include the file.
 */

/**
 * 
 * @param {string} folder folder to search. Should exist.
 * @param {string|includeFileCallback} extension Either a string file extension, or a function used to filter files.
 * @param {boolean?} requiresSort if the output requires a defined order. Default: false.
 * @returns 
 */
async function* folderScanner(folder, extension, requiresSort) {
    let folderContents = await getMatchingFolderContents(folder, extension, requiresSort);


    for(var i = 0; i < folderContents.length; i++) {
        const subfile = folderContents[i];

        if(subfile.directory) {
            var subfolder = await getMatchingFolderContents(subfile.name, extension, requiresSort);
            folderContents = folderContents.concat(...subfolder);
        } else {
            yield path.normalize(subfile.name);
        }
    }
    return undefined;
}

async function getMatchingFolderContents(folder, extension, requiresSort) {
    
    const test = typeof extension === "function" ? extension : 
        typeof extension === "undefined" ? ()=>true :
            x => x.endsWith(extension);
    
    return new Promise(function(resolve, reject) {
        fs.readdir(folder, {
            withFileTypes: true
        }, function(err, names) {
            if(err) reject(err);
            
            const filepaths = [];

            for (const x of names) {
                if (x.isDirectory()) {
                    filepaths.push({
                        name: path.join(folder, x.name),
                        directory: true
                    });
                } else if (x.isFile() && test(x.name)) {
                    filepaths.push({
                        name: path.join(folder, x.name)
                    });
                }
            }
            
            if (requiresSort) {
                resolve(filepaths.sort((a,b)=>  (a.name > b.name ? 1 : -1)  ));
            } else {
                resolve(filepaths);
            }
        });
    });
}