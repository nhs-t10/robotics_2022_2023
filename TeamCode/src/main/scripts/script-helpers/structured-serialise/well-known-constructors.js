var deprecatedGlobalKeys = ["GLOBAL", "root"];

var wkc = Object.getOwnPropertyNames(globalThis)
    .filter(x=>!deprecatedGlobalKeys.includes(x))
    .map(x=>[x, globalThis[x]])
    .filter(x=>typeof x[1] === "function" && x[1].prototype != Function );

var constructors = Object.fromEntries(wkc);

module.exports = {
    getName: function(obj) {
        if(obj === undefined) return undefined;
        if(typeof obj.constructor !== "function") return undefined;
        if(obj.constructor === Object) return undefined;
        if(!constructors[obj.constructor.name]) return undefined;
        return obj.constructor.name;
    },
    byName: function(name) {
        return constructors[name];
    },
    isWellKnownObject: function(obj) {
        return this.getName(obj) !== undefined;
    }
}

