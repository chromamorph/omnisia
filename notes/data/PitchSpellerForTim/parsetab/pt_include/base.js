// CONSTANTS
var logger = typeof(window)!=='undefined' ? window.console : console;
if(typeof(node)=="undefined") var node = false;
var extraClasses = "";
var locPrefix = "";
var ren_G = [67, 62, 57, 53, 48, 43, 41, 40, 38, 36, 35, 33, 31];
exports.ren_G = ren_G;
var ren_G_abzug = [67, 62, 57, 53, 48, 41, 40, 38, 38, 36, 35, 33, 31];
var ren_A = [69, 64, 59, 55, 50, 45, 43, 42, 40, 38, 37, 35, 33];
var bar_d = [65, 62, 57, 53, 50, 45, 43, 41, 40, 38, 36, 34, 33];
var bar_d_415 = [64, 61, 56, 52, 49, 44, 42, 40, 39, 37, 35, 33, 31];
var ren_guit = [67, 62, 58, 65, 0, 0, 0, 0, 0, 0, 0, 0, 0];
var bandora = [57, 52, 48, 43, 38, 36, 31, 26, 24, 23, 21, 19, 17, 16];
var tunings = [["Renaissance (G)", ren_G], 
  ["Renaissance abzug (G)", ren_G_abzug],
  ["Renaissance guitar", ren_guit],
  ["Baroque D minor", bar_d],
  ["Bandora", bandora]];
var ticksPerCrotchet = 128;
exports.ticksPerCrotchet = ticksPerCrotchet;
var rhythmFlags = "ZYTSEQHWBF";
exports.rhythmFlags = rhythmFlags
var buttonRhythmFlags = "ZYTSEQHF";
var tsbasics = [[1], [2], [3], [4], [5], [6], [8], [12], 
  ["C"], ["C/", "Ç"], ["D", "C", "reverse"], ["D/", "d"], 
  ["O", "0"]];
var tabletters = "abcdefghijklmnopqrstuvwxyz";
exports.tabletters = tabletters
var allOrnaments;
var colours = ["redReading", "blueReading", "greenReading"];

// Variable declarations 
var barCount = 0;
var nextpage = false;
var prevpage = false;
var rule = false;
var fill = true;
var test = true;//false;
var curx = 0;
var cury = 0;
var leftMargin = 24;
//var topMargin = 20;
var topMargin = 40;
var ld = 15; // staff-line-distance;
var lines = 6;
var editable = true;
var mainCourseCount = 6;
var TabCodeDocument = false;
var Extract;
var curBeams = 0;
var curBeamGroup = [];
var curTripletGroup = false;
var curStaves;
var curHistory = false;
var curDur = ticksPerCrotchet;
exports.curDur = curDur;
var curTime = 0;
var curFont;
var curTabType;
var curFontName;
var curApparatus = false;
exports.curApparatus = curApparatus;
var curTuning = ren_G;
exports.curTuning = curTuning;
var uniqueID = false;
var tempoFactor = 1.0;  // 256 ticks per second
var breaks = "stop"; // options are: TRUE (observe breaks), FALSE (ignore them), "stop" (up
                     // to first break)
var breakTypes = {Piece: "{/}", System: "{^}", Page: "{>}"};
var breakOptions = ["Piece"];

exports.advance  = advance ;
function advance (){
  return (4 + lines) * ld;
}

exports.staffHeight = staffHeight;
function staffHeight() {
  return ld*mainCourseCount;
}
exports.systemStep = systemStep;
function systemStep(){
  return ld*(mainCourseCount+2);
}

exports.tabxmlp = tabxmlp;
function tabxmlp(comment){
  return /^{<\/?(app|rdg)[^>]*>/i.test(comment[0]);
}

exports.rulesp = rulesp;
function rulesp(comment){
  // This tells me that it's intended as a rule, but not that it's
  // legal
  return /{<rules>[\s\S]*<\/rules>}/.test(comment[0]);
}

exports.pagep = pagep;
function pagep(code){
  // Did TC devise a numbered variant that breaks this?
	return(/{\>}/.test(code));
}
exports.systemp = systemp;
function systemp(code) {
	return(/{\^}/.test(code));
}

exports.FlagDur = FlagDur;
function FlagDur(rhythm) {
  // Return a duration in multiples of crotchets given a flag
  // FIXME: add scaling factor?
	var pos = rhythmFlags.indexOf(rhythm);
	if (pos>7){
		pos--;
	}
	return Math.pow(2, (pos - 5));
}

exports.letterPitch = letterPitch;
function letterPitch(fretChar){
	var pos = tabletters.indexOf(fretChar);
	if(pos>20){
		pos -= 2;
	}else if(pos>8){
		pos--;
	}
	return pos;
}
exports.tabChar = tabChar;
function tabChar(tabLetter){
  if(curTabType == "Italian"){
    return letterPitch(tabLetter);
  } else {
    return tabLetter;
  }
}

exports.yOffset = yOffset;
function yOffset(course){
  if(curTabType == "Italian"){
    // return topMargin+(ld*(6-course)) - 9.5; // ??!
    return (ld*(6-course+0.75));
  } else {
    // return topMargin+(ld*course);
    return ld*(course+1.35);
    return cury+(ld*course);
  }
}
exports.flagy = flagy;
function flagy(){
  // if(curTabType == "Italian"){
    return cury - (ld * 2/5);
  // }
  // return cury;
}

////////////////////////////////
// 
// Drawing utility functions
//

exports.drawRepeat = drawRepeat;
function drawRepeat(svg, x){
  for(var i=0; i<3; i++){
    svgCircle(svg, x+(ld/7), cury+(ld*(i+(17/6))), ld/7, "repeatdot", false);
  }
  curx+=ld*2/5;
}

exports.drawDashedBarline = drawDashedBarline;
function drawDashedBarline(svg, x){
  for(var i=0; i<lines; i++){
    svgCircle(svg, x+ld/10, cury+(ld*(5/6*i))+(7/4*ld), ld/10, "barlinedots", false);
  }
}

exports.drawBarline = drawBarline;
function drawBarline(svg, x, dashed, dble){
  if(dashed){
    drawDashedBarline(svg, x);
    curx -= ld/5;
  } else {
    svgLine(svg, x, cury+(ld*4/3), x, cury+(ld*4/3)+(ld*(lines-1)), "barlineline", false);
    curx -= ld/2; // why?!
  }
  if(dble) {
    drawBarline(svg, x+(ld/3), dashed, false);
    curx += ld/2;
  }
  else {
    curx += ld/3;
  }
}
/*
}
  curx += ld;
}
*/

exports.drawTSC = drawTSC;
function drawTSC(svg, TS, i, j){
  var alone = j==0 && TS.components[i].length==1;
  var el = svgGroup(svg, "tscomponent i"+i+"j"+j, false);
  var symbol = TS.components[i][j];
  var slashed = /\//.test(symbol);
  var compClass = alone ? "lone" : "stacked";
  var ystep = ld * (alone ? 4 : (j ? 5 : 2.75));
  $(el).data("word", TS);
  $(el).data("i", i);
  $(el).data("j", j);
  switch(symbol.charAt(0)){
    case "C":
      svgText(el, curx, cury+ystep, compClass+" tsc", false, false, (slashed ? "Ç" : "C"));
      slashed = false;
      break;
    case "D":
      svgText(el, curx, cury+ystep, compClass+" tsc", false, false, (slashed ? "d" : "D"));
      slashed = false;
      break;
    case "O":
      svgText(el, curx, cury+ystep, compClass+" tsc", false, false, "0");
      break;
    default:
      // Not a symbol sig (most likely a number)
      // FIXME: If it's not a number, then this won't work
      svgText(el, curx+(ld/4), cury+ystep, compClass+" tsc tstext", false, false,
        (/[\/\.]/.test(symbol) ? symbol.substring(0,symbol.search(/[\/\.]/)) : symbol));
  }
  if(slashed){
    // If were here, it has a slash that we don't have a glyph for
    svgLine(el, curx+(el.getBBox().width/2), cury+2*ld,
        curx+(el.getBBox().width/2), cury+6*ld,
        "tsslash", false);    
  }
  if (/\./.test(symbol)){
    svgCircle(el, curx+(el.getBBox().width/2), cury+ystep, ld/6, "tsdot", false);
  }
  return el;
}

exports.drawTSComponent = drawTSComponent;
function drawTSComponent(svg, symbol, minStep, i, j, TS){
  // FIXME: obsolete if I parse TS properly
  var compClass = minStep===0 ? "lone" : "stacked";
  var ystep = minStep === 0 ? ld * 4 :( minStep ? ld * 5 : ld * 3);// FIXME: ??
  var el = svgGroup(svg, "tscomponent i"+i+"j"+j, false);;
  var skipSlash = false;
  $(el).data("word", TS);
  $(el).data("i", i);
  $(el).data("j", j);
  switch(symbol.charAt(0)){
    case "C":
      if(/\//.test(symbol)) {
        svgText(el, curx, cury + ystep, compClass+" tsc", false, false, "Ç");
        skipSlash = true;
      } else {
        svgText(el, curx, cury + ystep, compClass+" tsc", false, false, "C");
      }
      break;
    case "D":
      // draw at origin first
      // el.setAttributeNS(null, "transform", "scale(-1 1) translate("+(-curx-ld*3)
      //              +", "+(cury+ystep)+")");
      // svgText(el, 0, 0, compClass+" tsd", false, false, "C");
      if(/\//.test(symbol)){
        svgText(el, curx, cury+ystep, compClass+" tsc dslash", false, false, "d");
        skipSlash= true;
      } else {
        svgText(el, curx, cury+ystep, compClass+" tsc tsc-D", false, false, "D");
      }
      // then flip
//      el.setAttributeNS(null, "transform", "scale(-1, 1)");
      // then move into pos (FIXME: won't start centred about origin, so a bit crooked)
      // el.setAttributeNS(null, "transform", "translate("+(curx+el.getBBox().width)
      //                   +", "+(cury+ystep)+")");
      break;
    case "O":
      svgText(el, curx, cury + ystep, compClass+" tsc tso", false, false, "0");
      break;
    default:
      // Not C, D, or O (probably means is a number)
      // Print verbatim, but for multichar symbol need to stop BEFORE any . or /
      var tmpsymbol = symbol;
      if(symbol.search(/[\/\.]/)!=-1) {
        tmpsymbol = symbol.substring(0,symbol.search(/[\/\.]/));
      }
      svgText(el, curx-ld, cury+ystep, compClass+" tsc tstext", 
        false, false, tmpsymbol);
  }  
  // Slashes (FIXME: a bit crude, and lacking +)
  if(!skipSlash){
    if(/\//.test(symbol)){
      // FIXME: fix this
      svgLine(el, curx+(el.getBBox().width/2), cury+2*ld,
        curx+(el.getBBox().width/2), cury+6*ld,
        "tsslash", false);
    } else if(/\./.test(symbol)){
      svgCircle(el, curx+(el.getBBox().width/2), cury+ystep, ld/6, "tsdot", false);
    }
  }
  if(minStep || minStep === 0) {
    curx += Math.max(el.getBBox().width, minStep);
  }
  return el.getBBox().width; // el.width;
}

exports.drawSlashes = drawSlashes;
function drawSlashes(svgEl, n) {
  var starty = cury+systemStep()-(ld*3/4);
  for(var i=0; i<n; i++){
    svgLine(svgEl, curx, starty, curx+ld, starty-(ld/2), "bassslash", false);
    starty += 3;
  }
}

exports.drawItalianSlashes = drawItalianSlashes;
function drawItalianSlashes(svgEl, n) {
  var starty = cury+(ld*4/5);
  for(var i=0; i<n; i++){
    svgLine(svgEl, curx, starty, curx+ld, starty-(ld/2), "bassslash", false);
    starty -= 3;
  }
}

exports.drawInsertBox = drawInsertBox;
function drawInsertBox(x, y, i, svgEl){
//  var el = svgRoundedRect(svgEl, x-ld/2, y, 1/3*ld, 7*ld, ld/4, ld/4, "missingChord", "I-"+i);
  var el;
  var voff;
  for(var j=0; j<lines+2; j++){
    if(j==lines+1){
      // FIXME: No bass courses please, we can't do 'em yet
      continue;
    }
    voff = j ? ld * (j-1/4) : -ld;
    // thinned this to avoid blocking barlines
    el = svgRoundedRect(svgEl, x-(3/4*ld), y+voff, 8/16*ld, j ? ld : 7/4* ld, ld/4, ld/4, "missingChord", "n"+i+"p"+j);
    $(el).data("follows", i);
    $(el).data("precedes", i+1);
    $(el).data("pos", j);
  }
}

exports.drawMetricalInsertBoxes = drawMetricalInsertBoxes;
function drawMetricalInsertBoxes(i, word, svgEl){
  var ela = svgRoundedRect(svgEl, curx-ld, cury+3/2*ld, ld*2, ld, 
    ld/4, ld/4, "missingTSC above", false);
  var elb = svgRoundedRect(svgEl, curx-ld, cury+11/2*ld, ld*2, ld,  
    ld/4, ld/4, "missingTSC below", false);
  $(ela).data("word", word);
  $(elb).data("word", word);
  $(ela).data("i", i);
  $(elb).data("i", i);
}
exports.drawMetricalInsertBox = drawMetricalInsertBox;
function drawMetricalInsertBox(i, type, word, svgEl){
  var el = svgRoundedRect(svgEl, curx-ld/2, cury+2*ld, ld/2, 4*ld,
    ld/4, ld/4, "missingTSC "+type, false);
  $(el).data("word", word);
  $(el).data("i", i);
}
