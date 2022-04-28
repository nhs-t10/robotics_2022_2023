var nameMap = {
    touchpadfinger1: "Touchpad",
    touchpadfinger2: "Touchpad",
    touchpadfinger1x: "Touchpad (x)",
    touchpadfinger1y: "Touchpad (y)",
    touchpadfinger2x: "Touchpad (x)",
    touchpadfinger2y: "Touchpad (y)",
    dpadup: "D-pad Up",
    dpaddown: "D-pad Down",
    dpadleft: "D-pad Left",
    dpadright: "D-pad Right",
    guide: "Guide",
    select: "Select",
    start: "Start",
    back: "Back",
    leftbumper: "Left Bumper",
    rightbumper: "Right Bumper",
    leftstickbutton: "Left Stick Press",
    rightstickbutton: "Right Stick Press",
    circle: "Circle (Red)",
    "b2-circle": "Circle",
    cross: "X (Blue)",
    "b2-cross": "X (Blue)",
    triangle: "Triangle",
    square: "Square",
    share: "Share",
    options: "Options",
    touchpad: "Touchpad",
    ps: "System Button",
    leftstickx: "Left Stick (X)",
    leftsticky: "Left Stick (Y)",
    rightstickx: "Right Stick (X)",
    rightsticky: "Right Stick (Y)",
    lefttrigger: "Left Trigger",
    righttrigger: "Right Trigger"
};

module.exports = function prettyButton(b) {
    
    if(b == "b2-cross") return "X";
    if(b == "b2-circle") return "Circle";
    
    var tag = b.split("-")[0];
    var bNoTag = b.replace(/b\d?-/, "");
    return {
        b: "GP1",
        b2: "GP2"
    }[tag] + " " + (nameMap[b] || nameMap[bNoTag]);
}