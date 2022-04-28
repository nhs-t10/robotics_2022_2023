module.exports = function () {
    var pool = new Map();
    var invPool = {};
    return {
        getCodeFor: function (cons) {

            //if it's an integer between 0 and 0xFFFFFF, use `loadint`.
            if (typeof cons === "number" && cons >= 0
                && (cons | 0) == cons && cons <= 0xFFFFFF) {
                return 0x0E000000 | cons;
            }

            if(pool.has(cons)) {
                return pool.get(cons);
            } else {
                var pid = pool.size;
                var code = 0x0F000000 | pid;
                
                pool.set(cons, code);
                invPool[code] = cons;
                
                return code;
            }
        },
        denseCodeMap: function() {
            var f = {};
            var entries = Array.from(pool.entries());
            entries.forEach((x,i)=> {
                f[x[1]] = i;
            });
            
            var v = entries.map(x => x[0]);
            
            return {
                map: f,
                valueArray: v
            };
        }
    }
}

