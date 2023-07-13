// Parse a TabCode file and output minimal JSON data-structure
// for further analysis/voice-separation

const base = require("./pt_include/base");
const tabclasses = require("./pt_include/tabclasses");
const parser = require("./pt_include/parser");
const rules = require("./pt_include/rules");

const fs = require('fs');
var tc_file = 'test.tc'; //default

if(process.argv.length > 2) tc_file = process.argv[2];
var data = fs.readFileSync(tc_file, 'utf8');
// if argv[2 is "-s", this means 'silent' output - for debugging only]

var debug = false;
 if(process.argv[3]=="-d") debug = true;
// console.log(debug)

base.curBeams = 0;

var TCDoc = parseTCDoc(data,debug);
TCDoc.make_Min_TabWords();

// console.log(parser.Min_TabWords)
if(tc_file != "-s") console.log(JSON.stringify(parser.Min_TabWords))

function parseTCDoc(TC,debug){
    TabCodeDocument = new parser.Tablature(TC,debug);
    return TabCodeDocument;
}
