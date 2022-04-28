var http = require("http");
var fs = require("fs");

var server = http.createServer((req, res) => {
    if(req.url.includes("/stream")) {
        res.setHeader("Content-Type", "text/plain;charset=UTF-8");
        res.writeHead(200);
        var fakeLatencyBuffer = "";
        setInterval(function() {
            var message = "";
            if(!newData()) message = fakeLatencyBuffer + "h\n";
            else message = fakeLatencyBuffer + createSampleObj() + "\n";

            var length = Buffer.from(message).length;
            res.write(length.toString(16).toUpperCase() + "\r\n" + message + "\r\n");
        }, 1000/30);
    } else if(req.url.includes("/fallible-hardware-devices")) {
        res.setHeader("Content-Type", "application/json");
        res.writeHead(200);
        res.write(JSON.stringify({
            "foo": {
                capabilities: []
            },
            "bar": {
                capabilities: []
            }
        }));
        res.end();
    } else if(req.url == "/") {
        res.setHeader("Content-Type", "text/html;charset=UTF-8");
        res.writeHead(200);
        res.write(fs.readFileSync("./index.html"));
        res.end();
    } else {
        var file = __dirname + req.url.replace(/\.\./g, ".");
        if(fs.existsSync(file)) {
            res.setHeader("Content-Type", "application/json");
            res.writeHead(200);
            res.write(fs.readFileSync(file));
            res.end();
        } else {
            res.writeHead(404);
            res.write("404");
            res.end();
        }
    }
});

server.listen(5564);

function newData() {
    return Math.random() > 0.2;
}

function createSampleObj() {
    return JSON.stringify({
        status: 200,
        log: generateRandomString(10 + Math.round(Math.random() * 6)),
        time: Date.now(),
        fields: {
            "fl power": Math.random(),
            "bl power": Math.random(),
            "br power": Math.random(),
            "fr power": Math.random(),
            "this one's almost always 0.1": Math.random() > 0.005 ? 0.1 : Math.random() * 7,
            "this one's rarely not 0": Math.random() > 0.01 ? 0 : Math.random(),
            "This one's -1 to 1": Math.random() * 2 - 1,
            "sine wave": Math.sin(Date.now() / 500),
            "this one's big": Math.random() * 100,
            "flywheel power": Math.random(),
            "name": generateRandomString(240),
            "spinny thing up top power": Math.random()
        },
        gamepad1: {
            //booleans
            dpad_up: Math.random()>0.5, dpad_down: Math.random()>0.5, dpad_left: Math.random()>0.5, dpad_right: Math.random()>0.5, a: Math.random()>0.5, b: Math.random()>0.5, x: Math.random()>0.5, y: Math.random()>0.5, guide: Math.random()>0.5, start: Math.random()>0.5, back: Math.random()>0.5, left_bumper: Math.random()>0.5, right_bumper: Math.random()>0.5, left_stick_button: Math.random()>0.5, right_stick_button: Math.random()>0.5, circle: Math.random()>0.5, cross: Math.random()>0.5, triangle: Math.random()>0.5, square: Math.random()>0.5, share: Math.random()>0.5, options: Math.random()>0.5, touchpad: Math.random()>0.5, ps: Math.random()>0.5, 
            //floats (-1 - 1)
            left_stick_x: Math.random()*2-1, left_stick_y: Math.random()*2-1, right_stick_x: Math.random()*2-1, right_stick_y: Math.random()*2-1,
            //floats (0 - 1)
            left_trigger: Math.random(), right_trigger: Math.random()
        },
        gamepad2: {
            //booleans
            dpad_up: Math.random()>0.5, dpad_down: Math.random()>0.5, dpad_left: Math.random()>0.5, dpad_right: Math.random()>0.5, a: Math.random()>0.5, b: Math.random()>0.5, x: Math.random()>0.5, y: Math.random()>0.5, guide: Math.random()>0.5, start: Math.random()>0.5, back: Math.random()>0.5, left_bumper: Math.random()>0.5, right_bumper: Math.random()>0.5, left_stick_button: Math.random()>0.5, right_stick_button: Math.random()>0.5, circle: Math.random()>0.5, cross: Math.random()>0.5, triangle: Math.random()>0.5, square: Math.random()>0.5, share: Math.random()>0.5, options: Math.random()>0.5, touchpad: Math.random()>0.5, ps: Math.random()>0.5, 
            //floats (-1 - 1)
            left_stick_x: Math.random()*2-1, left_stick_y: Math.random()*2-1, right_stick_x: Math.random()*2-1, right_stick_y: Math.random()*2-1,
            //floats (0 - 1)
            left_trigger: Math.random(), right_trigger: Math.random()
        }
    });
}

function generateRandomString(len) {
    len = +len || 12;
    var r = "";
    for(var i = 0; i < len; i++) r += String.fromCharCode(65 + Math.floor(Math.random() * 33));
    return r;
}