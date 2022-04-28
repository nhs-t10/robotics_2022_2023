var crypto = require("crypto");
var fs = require("fs");

var makePersonSvg = require("./make-person-svg");
var familyColors = require("./family-colors");

var builds = require("./generate-build-dataset")();

var BUILD_ICON_SIZE = 180;
var BUILD_ICON_MARGIN_PERCENT = 2;


builds.forEach(x=>x.timeDate = new Date(x.time));

builds = builds.sort((a,b)=> a.timeDate.getTime() - b.timeDate.getTime());

builds.forEach((x,i)=>x.globalBuildNumber = i);

var modernBuilds = builds.filter(x=>isModernBuild(x));

modernBuilds.forEach((x,i)=>x.globalModernBuildNumber = i);

addChildrenAndPartners(modernBuilds);

var greatGrandparents = modernBuilds.filter(x=> !x.relations.hasParent && x.relations.children.length);

var rootPerson = greatGrandparents[0];

var simpleSvg = makePersonAndTheirDescendantsSvg(rootPerson, true);

packageSvgResultIntoFile(simpleSvg, "vis-simple");

var complexSvg = makePersonAndTheirDescendantsSvg(rootPerson, false);

packageSvgResultIntoFile(complexSvg, "vis-complex");

function packageSvgResultIntoFile(svgContent, name) {
    var maxX = svgContent.width, minX = svgContent.xMin, minY = svgContent.yMin, maxY = svgContent.height;


    fs.writeFileSync(__dirname + "/" + name + ".svg", `<svg xmlns="http://www.w3.org/2000/svg" viewBox="${minX} ${minY} ${maxX} ${maxY}">` + svgContent.svg + "</svg>"); //SAFE
}
function makePersonAndTheirDescendantsSvg(person, simple, x, y, dir) {
    //fail-safe
    if(!person) return {
        xMin: x,
        xMax: x,
        yMin: y,
        r1width: 0,
        width: 0,
        height: 0,
        heightWithoutMargin: 0,
        svg: ""
    }
    
    simple = !!simple;
    
    //convert x and y to numbers
    x |= +x;
    y |= +y;

    //make dir 1 or -1
    dir = +(dir > 0) * 2 - 1;

    var width = 0;
    var height = 0;
    var xMin = x;
    var xMax = x;
    var yMin = y;
    
    var svg = "";

    var marginCt = BUILD_ICON_MARGIN_PERCENT * BUILD_ICON_SIZE;

    var p1x = x, 
        p2x = x + marginCt * dir;

    xMin = Math.min(p1x, p2x);
    xMax = Math.max(p1x, p2x);
    
    if(simple) svg += makeAtomicBuildItem(person, p1x, y);
    else svg += atomicBuildProfilePicture(person, p1x, y);
    
    if(person.relations.partner) {
        if(simple) svg += makeAtomicBuildItem(person.relations.partner, p2x, y);
        else svg += atomicBuildProfilePicture(person.relations.partner, p2x, y);
        
        width += marginCt;
    }

    width += marginCt;
    height += marginCt;

    svg += `<path d="M${p1x} ${y}H${p2x}" class="marriage-line"/>`;

    var childrenG = "";

    var nextRowY = y + marginCt;

    var maxChildHeight = 0;

    seededShuffleArray(person.relations.children, person.name);

    var childOffsetLeft = 0, childOffsetRight = marginCt;

    for(var i = 0; i < person.relations.children.length; i++) {
        var child = person.relations.children[i];
        var cSvg;
        if(i % 2) {
            cSvg = makePersonAndTheirDescendantsSvg(child, simple, x + childOffsetRight, nextRowY, 1);
            childOffsetRight += cSvg.r1width;
        } else {
            cSvg = makePersonAndTheirDescendantsSvg(child, simple, x - childOffsetLeft, nextRowY, -1);
            childOffsetLeft += cSvg.r1width;
        }

        xMin = Math.min(xMin, cSvg.xMin);
        xMax = Math.max(xMax, cSvg.xMax);

        maxChildHeight = Math.max(maxChildHeight, cSvg.heightWithoutMargin);
        childrenG += cSvg.svg;

    }

    height += maxChildHeight;

    svg += childrenG;

    //give some top margin
    y -= marginCt;
    
    return {
        xMin: xMin,
        xMax: xMax,
        yMin: y,
        r1width: marginCt * (person.relations.partner ? 2 : 1),
        width: xMax - xMin,
        height: height + marginCt,
        heightWithoutMargin: height,
        svg: svg
    };
}

function initRelations(build) {
    if(!build.relations) build.relations = {};
    if(!build.relations.children) build.relations.children = [];
}

function atomicBuildProfilePicture(build, x, y) {
    var heraldry = familyColors.fullColorScheme(build);
    
    var background = svgEllipse(x, y, BUILD_ICON_SIZE * 0.5, BUILD_ICON_SIZE * 0.6, heraldry.primaryVDark);
    
    var cLeft = x - BUILD_ICON_SIZE * 0.5,
        cRight = x + BUILD_ICON_SIZE * 0.5,
        cBottom = y + BUILD_ICON_SIZE * 0.6,
        cTop = y - BUILD_ICON_SIZE * 0.6;
    
    var bottomStroke = `<path stroke-linecap="round" d="M${cLeft} ${y} Q ${cLeft} ${cBottom} ${x} ${cBottom} Q ${cRight} ${cBottom} ${cRight} ${y}"` +
        ` stroke-width="${BUILD_ICON_SIZE * 0.1}" stroke="${heraldry.primary}" fill="#00000000"/>`;
    var bottomEllpse = svgEllipse(x, cBottom, BUILD_ICON_SIZE * 0.2, BUILD_ICON_SIZE * 0.2, heraldry.primary);
    
    var bottom = bottomStroke + bottomEllpse + heraldry.crestCircled(x, cBottom, BUILD_ICON_SIZE * 0.3);
    
    var text = `<text style="font-family:'JetBrains Mono';font-size:24px" x="${x}" y="${y + BUILD_ICON_SIZE * 0.95}" text-anchor="middle">${build.name}</text>`
    
    var person = makePersonSvg(build, x, y, BUILD_ICON_SIZE);
    
    return background + person + bottom + text;
}

function svgEllipse(x, y, rx, ry, color) {
    return `<ellipse fill="${color}" rx="${rx}" ry="${ry}" cx="${x}" cy="${y}"/>`;
}

function makeAtomicBuildItem(build, x, y) {

    if(!build) build = {cognomen: "", name: "UNKNOWN"};

    var background = `<ellipse fill="${familyColors.primary(build)}" rx="${BUILD_ICON_SIZE / 2}" ry="${BUILD_ICON_SIZE  / 2}" cx="${x}" cy="${y}"/>`;

    var text = `<text style="font-family:'JetBrains Mono';font-size:24px" x="${x}" y="${y + BUILD_ICON_SIZE * 0.8}" text-anchor="middle">${build.name}</text>`

    return background + text;
}

function curveBetween(pos1, pos2) {
    var midX1 = [pos1[0], pos1[1] + (pos2[1] - pos1[1])];
    var midX2 = [pos2[0], pos1[1] + (pos2[1] - pos1[1])];

    return `M${pos1[0]} ${pos1[1]} C${midX1[0]} ${midX1[1]} ${midX2[0]} ${midX2[1]} ${pos2[0]} ${pos2[1]}`;
}

function randomFrom(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}

function isModernBuild(build) {
    var keys = Object.keys(build);

    return keys.includes("colors") && keys.includes("perceptualHash");
}

function seededShuffleArray(a, s) {
    var rand = seededRandom(s);
    
    for (var i = a.length - 1; i > 0; i--) {
        var swTo = Math.floor(rand() * (i + 1));
        var tmp = a[i];
        a[i] = a[swTo];
        a[swTo] = tmp;
    }
    return a;
}

function seededRandom(seed) {
    seed = parseInt(sha(seed).substring(0,8), 16);
    
    return function mulberry32random() {
        var t = seed += 0x6D2B79F5;
        t = Math.imul(t ^ t >>> 15, t | 1);
        t ^= t + Math.imul(t ^ t >>> 7, t | 61);
        return ((t ^ t >>> 14) >>> 0) / 4294967296;
    }
}

function sha(t) {
    return crypto.createHash("sha256").update(t + "").digest("hex");
}

function transformSvg(svg, x, y) {
    return `<g transform="translate(${x} ${y})">${svg}</g>`
}

function addChildrenAndPartners(builds) {

    builds.forEach(x=>initRelations(x));

    for(var i = 0; i < builds.length; i++) {
        var build = builds[i];
        if(builds[i + 1] && builds[i + 1].cognomen != build.cognomen) {
            build.relations.partner = builds[i + 1];
            builds[i + 1].relations.partner = build;
            build.relations.children = build.relations.partner.relations.children;
        }
    }
    for(var i = 0; i < builds.length; i++) {
        var build = builds[i];
        if(build.relations.partner) {
            var childCognomens = [build.cognomen, build.relations.partner.cognomen];

            for(i += 2; i < builds.length; i++) {
                if(childCognomens.includes(builds[i].cognomen)) {
                    var children = build.relations.children;
                    children.push(builds[i]);
                    builds[i].relations.hasParent = true;
                    if(builds[i].relations.partner) {
                        i--;
                        break;
                    }
                }
            }
        }
    }
}