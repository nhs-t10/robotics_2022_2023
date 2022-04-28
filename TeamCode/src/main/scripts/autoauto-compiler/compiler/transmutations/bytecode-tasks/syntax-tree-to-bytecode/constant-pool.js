module.exports = function() {
    var pool = [];
    var subId = 0;
    var tempvars = 0;
    return {
        pool: pool,
        getCodeFor: function(cons) {
            
            //if it's an integer between 0 and 0xFFFFFF, use `loadint`.
            if(typeof cons === "number" && cons >= 0
            && (cons | 0) == cons && cons <= 0xFFFFFF) {
                return 0x0E000000 | cons;
            }
            
            var pid = pool.length;
            pool.push(cons);
            
            return 0x0F000000 | pid;
        },
        subblockLabel: function(label, subcategory) {
            if(arguments.length == 1) {
                subcategory = label;
                label = "subcat";
            }
            subId++
            return `${label}/${subcategory}/${subId.toString(16)}`;
        },
        tempVar: function() {
            return "@temp" + (tempvars++);
        }
    }
}