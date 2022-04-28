var PATHFINDING_GRID_SIZE = 100;

module.exports = function(startPointToEndPoints) {
    var sortedTargets = startPointToEndPoints.sort((a,b) => distance(a.segments[0][0], a.segments[0][1]) - distance(b.segments[0][0], b.segments[0][1]));

    var lines = [];

    return sortedTargets.map(x=>swapCoordsToPathfindNoOverlap(x, lines));
}

function swapCoordsToPathfindNoOverlap(requiredLineRecord, pastLines) {
    var overallLine = requiredLineRecord.segments[0];

    var segments =  pathfindBetween(overallLine, pastLines).map(x=>
        subwayanizeLine(x)
    ).flat(1);

    requiredLineRecord.segments = segments;

    pastLines.push(...segments);
    return requiredLineRecord;
}


//takes a line and makes some output with the same end-coords, but so that its slope can *only* be -1, 1, 0 (horizontal), or undefined (vertical)
//returns an array of 1 or 2 line segments (defined by start/end points).
function subwayanizeLine(linepoints) {
    var start = linepoints[0];
    var end = linepoints[1];

    var yDelta = end[1] - start[1];
    var xDelta = end[0] - start[0];
    var slope = yDelta / xDelta;

    var mid = [];

    if(slope == -1 || slope == 1 || slope == 0 || xDelta == 0) {
        return [linepoints];
    }

    if(Math.abs(slope) > 1) {
        mid = [end[0], start[1] + Math.sign(slope) * xDelta];
    } else {
        mid = [start[0] + Math.sign(slope) * yDelta, end[1]];
    }

    return [
        [start, mid],
        [mid, end]
    ];
}

function pathfindBetween(coords, pastLines) {
    var current = coords[0];
    var endTarget = coords[1];

    var segments = [];

    while(current[0] != endTarget[0] || current[1] != endTarget[1]) {
        var desiredLine = [current, endTarget];
        var intersect = findLineInSystemThatIntersects(desiredLine, pastLines);

        if(!intersect) {
            segments.push(desiredLine);
            current = endTarget;
        } else {
            var offsetGoAround = getCircumnavigation(desiredLine, intersect.line, intersect.intersection);
            segments.push(offsetGoAround.approach, ...offsetGoAround.circumnavigate);
            current = offsetGoAround.newStartingPoint;
        }
    }

    return segments;
}

function getCircumnavigation(desiredLine, blockingLine, intersectionPoint) {
    var branch = offsetFromPointAlongLine(desiredLine, intersectionPoint, -10);

    var goal = desiredLine[1];

    var through = offsetFromPointAlongLine(desiredLine, intersectionPoint, distance(goal, intersectionPoint) / 32);

    return {
        approach: [desiredLine[0], branch],
        circumnavigate: [
            
        ],
        newStartingPoint: through
    };
}

function offsetFromPointAlongLine(line, point, offset) {
    if(line[0][0] == line[1][0]) return offsetVerticalAlongLine(line, point, offset);

    var x1 = point[0];
    var x2 = line[0][0];
    var y1 = point[1];
    var y2 = line[0][1];

    if(x1 == x2 || y1 == y2) {
        x2 = line[1][0];
        y2 = line[1][1];
    }

    var sof = Math.sign(offset);
    var abso = Math.abs(offset);

    var m = (y1 - y2) / (x1 - x2);

    var xsL = (x1 + m*m * x1 + abso * Math.sqrt(m*m + 1)) / (1 + m*m);

    var sod = x1 + Math.sign(x1 - x2) * sof * Math.abs(xsL - x1);

    var y = m * (sod - x1) + y1;

    return [sod, y];
}

function calculateSlope(line) {
    return (line[0][1] - line[1][1]) / (line[0][0] - line[1][0]);
}

function offsetVerticalAlongLine(line, point, offset) {
    //do it differently for up vs down
    if(line[1][1] > line[0][1]) return [point[0], point[1] + offset];
    else return [point[0], point[1] - offset];
}

function findLineInSystemThatIntersects(line, pastLines) {
    for(var i = 0; i < pastLines.length; i++) {
        var intersection = findLineIntersectionPoint(line, pastLines[i]);
        if(intersection != undefined) {
            return {
                intersection: intersection,
                line: pastLines[i]
            };
        }
    }
}

//please ignore my bad variable names
//they're matched with a desmos thing
function findLineIntersectionPoint(linepoints1, linepoints2) {
    var x1 = linepoints1[0][0];
    var x2 = linepoints1[1][0];
    var x3 = linepoints2[0][0];
    var x4 = linepoints2[1][0];

    var y1 = linepoints1[0][1];
    var y2 = linepoints1[1][1];
    var y3 = linepoints2[0][1];
    var y4 = linepoints2[1][1];

    //checks for verticals; those need to be handled specially, because they'll cause a divide-by-0 otherwise
    if(x1 == x2 && x3 == x4) return undefined;
    else if(x1 == x2) return verticalLineIntersection(linepoints1, linepoints2);
    else if(x3 == x4) return verticalLineIntersection(linepoints2, linepoints1);

    var m1 = (y1 - y2) / (x1 - x2);
    var m2 = (y3 - y4) / (x3 - x4);

    if(m1 == m2) return undefined;

    var xt = (y3 - y1 + m1 * x1 - m2 * x3) / (m1 - m2);
    var yt = m2 * (xt - x3) + y3;

    if(xt <= Math.min(x1, x2) || xt >= Math.max(x1, x2)) return undefined;

    

    return [xt, yt];
}

function verticalLineIntersection(vertical, other) {
    var vx = vertical[0][0];
    var vy1 = vertical[0][1];
    var vy2 = vertical[1][1];

    var x1 = other[0][0];
    var x2 = other[1][0];
    var y1 = other[0][1];
    var y2 = other[1][1];

    var m = (y1 - y2) / (x1 - x2);

    var yt = m * (vx - x1) + y1;

    if(!(Math.max(x1, x2) > vx && Math.min(x1, x2) < vx)) return undefined;
    if(yt <= Math.min(vy1, vy2) || yt >= Math.max(vy1, vy2)) return undefined;
    else return [vx, yt];
}

function distance(p1, p2) {
    var dx = p1[0] - p2[0];
    var dy = p1[1] - p2[1];

    return Math.sqrt(dx*dx + dy*dy);
}