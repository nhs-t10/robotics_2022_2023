var cIndex = 0;

var colours = ["#9d6b1b", "#004fa3", "#163b64", "#611a2d", "#740606", "#351431", "#100b00", "#2a513b", "#006638", "#EBB2B2", "#8383FC"];

module.exports = {
    getForNum: function (cIndex) {
        return colours[cIndex % colours.length];
    },
    getDashes: function(index) {
        var dIndex = index % 16;

        var on = dIndex & 0b11;
        var off = (dIndex >>> 2) & 0b11;

        return on*3 + " " + off;
    }
}