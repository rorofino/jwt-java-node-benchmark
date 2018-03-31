// sign with default (HMAC SHA256)
var jwt = require('jsonwebtoken');
var ursa = require('ursa');
var fs = require('fs');
const uuidv4 = require('uuid/v4');

// var priv = fs.readFileSync('privateKey.pem');
// var priv = fs.readFileSync('here');
// var pub = fs.readFileSync('here.pub');

var priv = fs.readFileSync('./key.pem', 'utf8');
var pub = fs.readFileSync('./server.crt', 'utf8');


function getRandomInt(max) {
    return Math.floor(Math.random() * Math.floor(max));
}

var sampleSize = 200000;
var map = [];
var current = 0;
for (var i = 0; i < sampleSize; i++) {
    var token = jwt.sign(
        {
            pId: uuidv4(),
            exp: new Date().getTime() + (getRandomInt(5) * 60000),
            issuer: "auth0"
        }, priv, { algorithm: 'RS256' });

    map.push(token);
    if (i % 2000 == 0) {
        current++;
        console.log("completed " + current + "%");
    }
}

var total = 0;
var bigger = 0;
map.forEach(token => {
    const start = new Date().getTime();
    jwt.verify(token, pub, { algorithms: ['RS256'] });
    const end = new Date().getTime();
    const diff = end-start;
    if (diff > 0) {
        bigger++;
    }
    total += diff;
});

console.log('Tempo total', total);

// var token = jwt.sign({ foo: 'bar' }, cert, { algorithm: 'RS256' });