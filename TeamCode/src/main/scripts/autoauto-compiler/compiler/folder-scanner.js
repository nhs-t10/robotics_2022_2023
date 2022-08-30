"use strict";

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
 * @param {string | string[]} folder folder(s) to search. May exist or not.
 * @param {string|includeFileCallback} extension Either a string file extension, or a function used to filter files.
 * @param {boolean} [requiresSort] if the output requires a defined order. Default: false.
 * @param {boolean} includeRoot If `true`, yields an array for each item. The array holds the root directory and the file address, in that order. Default: false.
 * @returns {Generator<Promise<string | string[]>, undefined, undefined>}
 */
async function* folderScanner(folders, extension, requiresSort, includeRoot) {
    if (typeof folders === "string") {
        for await (const file of individualFolderScanner(folders, extension, requiresSort)) {
            yield file;
        }
    } else {
        for (const folder of folders) {
            for await (const file of individualFolderScanner(folder, extension, requiresSort)) {
                if(includeRoot) yield [folder, file];
                else yield file;
            }
        }
    }
    return undefined;
}

/**
 * 
 * @param {string} folder folder to search. May exist or not.
 * @param {string|includeFileCallback} extension Either a string file extension, or a function used to filter files.
 * @param {boolean?} requiresSort if the output requires a defined order. Default: false.
 * @returns 
 */
async function* individualFolderScanner(folder, extension, requiresSort) {
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
            if(err) {
                if (err.errno === -4058) return resolve([]);
                else if (err.errno === -4052) return resolve([{ name: folder }]);
                else return reject(err);
            }
            
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