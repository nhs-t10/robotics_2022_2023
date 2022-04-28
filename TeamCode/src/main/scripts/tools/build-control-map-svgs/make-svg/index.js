var fs = require("fs");
var path = require("path");

var getMapLayoutEngine = require("./map-layout");
var prettyButton = require("./pretty-button-names");

var outFolder = path.join(__dirname, "../out");
if(!fs.existsSync(outFolder)) fs.mkdirSync(outFolder);

module.exports = function(controlMapEntry) {
    var mapName = controlMapEntry[0];
    var map = controlMapEntry[1];

    var layoutEngine = getMapLayoutEngine();

    layoutEngine.title = mapName;

    addButtonLabels(map, layoutEngine);
    
    var targetFileName = path.join(outFolder, mapName + ".svg");

    fs.writeFileSync(targetFileName, layoutEngine.render());
}

function addButtonLabels(map, layoutEngine) {
    var mapEntries = Object.entries(map);

    var btnDescriptors = mapEntries.map(x=>{
        var f = getButtonDescriptors(x[1]).flat(Infinity);
        f.forEach(y=>y.to = x[0]);
        return f;
    }).flat();

    btnDescriptors.forEach(x=>layoutEngine.addLabel(x.button, makeLabelFromDescriptor(x)));
}

function makeLabelFromDescriptor(desc) {
    var title = desc.to;

    var attributes = desc.attributes;
    var subtitle = prettyButton(desc.button);
    if(attributes.Scaled) {
        var scalePc = attributes.Scaled.reduce((a,b)=>a*b, 1) * 100;
        if(scalePc != 100) subtitle += " - scaled by " + scalePc + "%";
    }
    if(attributes.Multiplied) {
            var MultiPc = attributes.Multiplied
            if(MultiPc != 100) subtitle += " - multiplied by " + MultiPc;
        }
    if(attributes.Toggle) subtitle += " - Toggle";
    if(attributes.All) subtitle += " and";
    if(attributes.Any) subtitle += " or";
    if(attributes.StartGroup) subtitle = "(" + subtitle;
    if(attributes.EndGroup) subtitle += ") \n";
    if(attributes.Plus) subtitle += " combined with";

    return {
        title: title,
        subtitle: subtitle
    };
}



function getButtonDescriptors(inputNodeStructure, attributes) {
    if(!attributes) attributes = {};
    else attributes = clone(attributes);

    switch(inputNodeStructure.type) {
        case "Button": return [{
            button: normalizeButton(inputNodeStructure.button),
            attributes: attributes
        }];
        case "Any":
            return inputNodeStructure.args.map((x,i,a)=>getButtonDescriptors(x,
                (i == a.length - 1) ? attributes : addAttribute(attributes, "Any")
                ));
        case "All":
            return inputNodeStructure.args.map((x,i,a)=>getButtonDescriptors(x,
                (i == a.length - 1) ? attributes : addAttribute(attributes, "All")
                ));
        case "MultiInput":
            return inputNodeStructure.args.map((x,i,a)=>{
                            if(i == 0) return getButtonDescriptors(x, addAttribute(attributes, "StartGroup"));
                            else if(i == a.length - 1) return getButtonDescriptors(x, addAttribute(attributes, "EndGroup"));
                            else return getButtonDescriptors(x, attributes);
                        });
        case "Multiply":
            return getButtonDescriptors(inputNodeStructure.args[0],
                addAttribute(attributes, "Multiplied", inputNodeStructure.args[1])
                );
        case "Plus":
            var ents =  inputNodeStructure.args.map(x=>getButtonDescriptors(x, attributes));
            ents.forEach((x,i,a)=>{
                var flx = x.flat(Infinity)
                var lst = flx[flx.length - 1];
        
                if(i != a.length - 1) setAttribute(lst, "Plus")
                });
            return ents.flat(Infinity);
        case "Scale":
            return getButtonDescriptors(inputNodeStructure.args[0],
                addAttribute(attributes, "Scaled", inputNodeStructure.args[1])
                );
        case "Toggle":
            return getButtonDescriptors(inputNodeStructure.args[0],
                addAttribute(attributes, "Toggle")
                );
        case "If":
            return [
                getButtonDescriptors(inputNodeStructure.args[0], addAttribute(attributes, "IfDeterminator")),
                getButtonDescriptors(inputNodeStructure.args[1], addAttribute(attributes, "IfTrue")),
                getButtonDescriptors(inputNodeStructure.args[2], addAttribute(attributes, "IfFalse"))
            ];
        case "Combo":
            var a = inputNodeStructure.args.map(x=>getButtonDescriptors(x, attributes)).flat(Infinity);
            a.forEach(x=>a.forEach(y=>x==y?0:setAttribute(x, "Combo", y.button)));
            return a;
        case "InputVariable":
        case undefined:
        case "StaticValue":
            return [];
        default: console.error(inputNodeStructure); throw new Error("aa");
    }
}

function normalizeButton(btnName) {

    btnName = btnName.replace(/[\W_]/g, "").toLowerCase();

    btnName = btnName.replace(/^gamepad1?(2?)/, "b$1-");
    if(!/^b\d?-/.test(btnName)) btnName = "b-" + btnName;

    var words = btnName.split("-");
    var gamepad = words[0], button = words[1];

    return gamepad + "-" + normXboxToPs(button);
}

function normXboxToPs(btn) {
    if(btn == "x") return "square";
    else if(btn == "a") return "cross";
    else if(btn == "b") return "circle";
    else if(btn == "y") return "triangle";
    else if(btn == "start") return "options";
    else return btn;
}

function clone(n) {
    var c = {};
    Object.assign(c, n);
    return c;
}

function setAttribute(buttonDescriptor, attr, value) {
    if(typeof buttonDescriptor != "object") return;

    if(!buttonDescriptor.attributes) buttonDescriptor.attributes = {};

    if(!buttonDescriptor.attributes[attr]) buttonDescriptor.attributes[attr] = [];
    buttonDescriptor.attributes[attr].push(value);
}

function addAttribute(attributes, key, value) {
    if(value === undefined) value = true;

    attributes = clone(attributes);

    if(!attributes[key]) attributes[key] = [];
    attributes[key].push(value);

    return attributes;
}