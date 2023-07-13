const base = require("./base");
const rules = require("./rules");
// const tabclasses = require("./tab2ngrams/tabclasses");
const tabclasses = require("./tabclasses");

exports.firstParse = firstParse;
function firstParse(TC){
  var thing = {};
  var comments = [];
  var words = [];
  var word = false;
  var commentLevel = 0;
  var wordBegan = 0;
  var nextChar;
  for(var i=0; i<TC.length; i++){
    nextChar = TC.charAt(i);
    if(commentLevel==0 || (commentLevel == 1 && nextChar=="}")){
      switch(nextChar){
        case " ":
        case "\n":
        case "\r":
        case "\t":
          if(word){
            words.push([word, wordBegan, i]);
          }
          wordBegan = false;
          word = false;
          break;
        case "{":
          if(word){
            words.push([word, wordBegan, i]);
          }
          wordBegan = i;
          word = nextChar;
          commentLevel++;
          break;
        case "}":
          commentLevel--;
          word += nextChar;
          words.push([word, wordBegan, (i+1)]);
          comments.push(words.length-1);
          word=false;
          wordBegan=false;
          commentLevel=0;
          break;
        default:
          if(!word){
            wordBegan=i;
            word="";
          }
          word += nextChar;
          break;
      }
    } else {
      word += nextChar;
      if(nextChar=="{"){
        commentLevel++;
      } else if (nextChar=="}"){
        commentLevel--;
      }
    }
  }
  if(word){
    words.push([word, wordBegan, TC.lengh]);
  }
  thing.words = words;
  thing.comments = comments;
  return thing;
}

exports.doubleParse = doubleParse;
function doubleParse(TC){
  // This is less a parsing process than a two-step tokenisation --
  // tokens are dependent on parenthetical comments, so we need to
  // find those first. Then we can find comments with specific
  // meanings, such as pages and system breaks.
  // firstParse provides a list of tokens and a list of indices
  // pointing to which of those tokens are comments.
  var struct = firstParse(TC); 
  struct.pages = [];
  struct.systems = [];
  var checking;
  for(var i=0; i<struct.comments.length;i++){
    if(base.pagep(struct.words[struct.comments[i]][0])) {
      struct.pages.push(struct.comments[i]);
    } else if (base.systemp(struct.words[struct.comments[i]][0])) {
      struct.systems.push(struct.comments[i]);
    }
  }
  return struct;
}
var Min_TabWords = new Array(); // Data structure to be output for analysis
exports.Min_TabWords = Min_TabWords; // For some reason parser.Min_TabWords is undefined??

exports.Tablature = Tablature;
//function Tablature(TC, SVG, parameters){
function Tablature(TC){
  this.code = TC;
//  this.SVG = SVG;
//  this.parameters = parameters || curParams || TabCodeDocument.parameters;
  this.noteEvents = [];
  this.starts = 0;
  this.finishes = TC.length;
  this.rules = [];
  this.TabWords = [];
  this.commentOffsets = [];
  this.pageOffsets = [];
  this.systemOffsets = [];
  this.tokens = [];
	this.syscounts = new Array();
	this.totalsys = 1;
	this.maxwidths = new Array();
	this.maxwidth = 0;
	this.pixHeight = 0;
	this.pixWidth = 0;
  this.finalFlag = false;
  this.colours = [];
  this.removes = [];
  rule = false;
//  if(!parameters) console.log(TC);
  TabCodeDocument = this;
  // FIXME: Should this really be here?
  curBeamGroup = [];
  // FIXME: belongs this here?
  // svgCSS(this.SVG, "webeditor.css");
  if(this.SVG) svgCSS(this.SVG, "render.css");
  this.removeInvisibles = function(){
    if(this.removes.length){
      var mods = [];
      var start, end, from, to;
      for(var i=0; i<this.removes.length; i++){
        start = this.removes[i].starts;
        end = this.removes[i].starts+2+this.removes[i].TC.length;
        from = this.code.substring(start, end);
        mods.push([start, from, ""]);
      }
//      this.parameters.history.add(new compoundModify(mods, document.getElementById('code'), "initialCleanup"));
    }
  };
  this.finishParse = function(){
    // Turn tokens into parsed tabwords
    var TabWord = false;
    for(var i=0; i<this.tokens.length; i++){
      // We treat comments differently, but this code is inefficient
      // in a way that's only a problem if there are lots of comments
      if(this.commentOffsets.indexOf(i) == -1){
		    TabWord = parseTabWord(this.tokens[i][0],this.tokens[i][1],this.tokens[i][2]);
			  if(TabWord){
		      if(this.starts == false) {
				    this.starts = TabWord.starts;
				  }
          if(base.curTripletGroup && TabWord.tType==="Chord"){
            base.curTripletGroup.addMember(TabWord);
          }
				  this.finishes = TabWord.finishes;
          if(this.TabWords.length > 0) {
            TabWord.prev = this.TabWords[this.TabWords.length-1];
            if(TabWord.prev.tType=="Chord"){
              TabWord.prev.nextStart = TabWord.starts;
            }
            TabWord.prev.next = TabWord;
          }
				  this.TabWords.push(TabWord);
          if(typeof(TabWord.flag != 'undefined') && TabWord.flag){
            this.finalFlag = TabWord.flag; // Needed for subsequent systems in db
          }
			  }
      } else if (this.pageOffsets.indexOf(i) != -1) {
		    this.TabWords.push(new tabclasses.PageBreak(this.tokens[i]));
		  } else if (this.systemOffsets.indexOf(i) != -1) {
		    this.TabWords.push(new tabclasses.SystemBreak(this.tokens[i]));
		  } else if (base.rulesp(this.tokens[i])){
        rule = new rules.Ruleset(this.tokens[i], rule);
        if(i===0){
//          this.parameters.update(rule);
        }
        this.rules.push(rule);
        this.TabWords.push(rule);
      } else if(base.tabxmlp(this.tokens[i])){
        if(!base.curApparatus){
          base.curApparatus = new Apparatus();
        }
        this.TabWords.push(new StructuredComment(this.tokens[i]));
      } else {
        // Yes, it really is just a plain comment
		    this.TabWords.push(new tabclasses.Comment(this.tokens[i]));
		  }
      if(base.curApparatus){
        base.curApparatus.add(this.TabWords[this.TabWords.length-1]);
      }
    }
  };
  this.initialParse = function(){
    // First, find tokens and comments
    // 
    // STRUCTURE is an object with tokenised words, and indexes for
	  // comments, systems and pages. All it needs is for the tokens to
	  // be parsed.
    var structure = doubleParse(TC);
    this.pageOffset = structure.pages;
    this.systemOffsets = structure.systems;
    this.commentOffsets = structure.comments;
    this.tokens = structure.words;
    this.systemOffsets.push(this.finishes);
  };
  this.initialParse();
  this.finishParse();
  this.firstNonComment = function(){
    for(var i=0; i<this.TabWords.length; i++){
      if(this.TabWords[i].fType !=="Ruleset"
         && this.TabWords[i].tType !=="Comment"){
        return this.TabWords[i];
      }
    }
    return false;
  };

  this.play = function(){
    if(!this.noteEvents.length) this.makeMidi();
    var track = new MidiTrack({events: this.noteEvents});
    var song = MidiWriter({tracks: [track]});
    song.save_small();
  };
	this.makeMidi = function() {
		this.noteEvents = new Array();
		curDur = this.parameters.defaultDur() * base.ticksPerCrotchet;
    var duration;
		for(var i=0; i<this.TabWords.length; i++){
			if(typeof(this.TabWords[i].duration) != "undefined") {
				duration = this.TabWords[i].duration();
				if(!duration) duration = 1;
				duration /= tempoFactor; //FIXME: Never do this in MIDI (GSHARP used to do this too :-/)
				this.loadChord(this.TabWords[i].pitches(), duration);
			}
		}
	};
	
// Next function builds a minimal array to be exported for further analysis/voice-separation
// TC 5 Nov 2020	
	this.make_Min_TabWords = function() {

		let countbar=false; // initial barline means nothing
		let barCount=0;
		let chordCount=0;
		let time=0; //time at which a Chord happens - cumulative sum of previous durations
		let lastType="";
//		let Min_TabWords = new Array(); // Data structure to be output for analysis
		Min_TabWords.push({tType:"Start"})
		for(var i=0; i<this.TabWords.length; i++){
			if(this.TabWords[i].tType=="Barline") {
				if(lastType != "Barline") {
					if(countbar) barCount++;
//					console.log("Bar "+barCount);
					Min_TabWords.push({tType:"Barline",barNum:barCount})
				}
			}
			else if(this.TabWords[i].tType=="Chord") {
				countbar=true;
				chordCount++;
				let n = Min_TabWords.push({tType:"Chord",chordNum:chordCount})
				this.TabWords[i].pitches();
				let outstring="";
				let rss="";
				if(this.TabWords[i].mainCourses.length) {
					Min_TabWords[n-1].TabNotes = new Array();
					for(j=0;j<this.TabWords[i].mainCourses.length;j++) {
						if(this.TabWords[i].mainCourses[j]) {
							outstring += "\t"+this.TabWords[i].mainCourses[j].fret+(j+1)+":"+this.TabWords[i].mainCourses[j].pitch(this.TabWords[i].tuning)
							Min_TabWords[n-1].TabNotes.push ({
								fret:this.TabWords[i].mainCourses[j].fret,
								course:(j+1),
								midiPitch:this.TabWords[i].mainCourses[j].pitch(this.TabWords[i].tuning)
							})
						}
						else {
							outstring+="\t";
						}
					}
					
				}
					
// console.log(" lbeams: "+this.TabWords[i].lbeams)
				rss = this.TabWords[i].flag? this.TabWords[i].flag : "- ";
				Min_TabWords[n-1].flag = rss;
				Min_TabWords[n-1].duration = this.TabWords[i].duration();
				Min_TabWords[n-1].beamed = this.TabWords[i].beamed;
				Min_TabWords[n-1].lbeams = this.TabWords[i].lbeams;
				Min_TabWords[n-1].rbeams = this.TabWords[i].rbeams;
				Min_TabWords[n-1].time = time;
				time += this.TabWords[i].duration();

					// FIXME!! Need similar this.TabWords[i].bassCourses loop here!!
// 		!!!!!		GROSSEST POSSIBLE HACK   !!!!
// 					NB!!!!!  THis only works for 7-course lute at present, and single unstopped bass notes
				if(this.TabWords[i].bassCourses.length) {

					Min_TabWords[n-1].TabNotes.push ({
						fret:this.TabWords[i].bassCourses[0].fret,
						course:(7),
						midiPitch:this.TabWords[0].tuning[6]
					})

// 					for(j=0;j<this.TabWords[i].bassCourses.length;j++) {
// // console.log(i+" "+j+" "+this.TabWords[i+j].tuning)
// 						if(this.TabWords[i].bassCourses[j]) {
// 						
// 							outstring += "\t"+this.TabWords[i].bassCourses[j].fret+(1)+":"+this.TabWords[i].bassCourses[0].pitch(this.TabWords[0].tuning)
// 							Min_TabWords[n-1].TabNotes.push ({
// 								fret:this.TabWords[i].bassCourses[0].fret,
// 								course:(j+7),
// 								midiPitch:this.TabWords[i].bassCourses[j].pitch(this.TabWords[0].tuning)
// 							})
// 						
// 						}
// 						else {
// 							outstring+="\t";
// 						}
// 					}
				}
// 					
// 				rss = this.TabWords[i].flag? this.TabWords[i].flag : "- ";
// 				Min_TabWords[n-1].flag = rss;
// 				Min_TabWords[n-1].duration = this.TabWords[i].duration();
// 				Min_TabWords[n-1].time = time;
// 				time += this.TabWords[i].duration();
			}
			if((this.TabWords[i].tType == "Barline")||(this.TabWords[i].tType=="Chord")) {
				lastType = this.TabWords[i].tType;
			}
		}
		Min_TabWords.push({tType:"End"});

	};

function loadChord(pitches, duration) {
    if(pitches.length){
      // It's a chord
      // First add all note no events (without duration)
	    for (var i=0; i<pitches.length; i++) {
		    this.noteEvents.push(MidiEvent.noteOn(pitches[i]));
		  }
      // Then note offs...
	    // It's not at all clear to me why duration needs to be divided by
	    // the number of notes in the chord - maybe a bug in jsmidi.js? -
	    // but it seems to work ...
		  for (i=0; i<pitches.length; i++) {
		    this.noteEvents.push(MidiEvent.noteOff(pitches[i], i==0 ? duration : 0));
		  }
    } else {
      // It's a rest;
	    var note = new Object();
		  note.pitch = 0;
		  note.volume = 0;
		  this.noteEvents.push(MidiEvent.noteOn(note));
		  this.noteEvents.push(MidiEvent.noteOff(note, duration));      
    }
  };
}

exports.parseTabWord = parseTabWord;
function parseTabWord(TC, start, finish){
  if(TC.length>0) {
    var firstchar = TC.charAt(0);
		if(firstchar.match(/[a-z]/)||firstchar=="X"||firstchar=="-"||firstchar=="."){
			// Chord without rhythm flag
			return FlaglessChord(TC, start, finish, base.curBeams);
		} else if (base.rhythmFlags.indexOf(firstchar)>-1){
			// Chord with flag
      base.curBeams = 0;
			return FlaggedChord(TC, start, finish, base.curBeams);
		} else if (firstchar=="[" || firstchar=="]") {
      if(TC.charAt(1)==="3"){
        // Editorial triplet
        return TripletChord(TC, start, finish);
      }
			// Explicitly-beamed chord
// console.log("beamed chord here: "+TC+" start: "+start+" finish: "+finish)
			return BeamedChord(TC, start, finish);
		} else if (firstchar === "M") {
			// Timesig
			return new tabclasses.Meter(TC, start, finish);
		} else if (firstchar === "3") {
			// Triplet. FIXME: ignore.
			return TripletChord(TC, start, finish);
		} else if (firstchar===":" || firstchar === "|") {
			return new tabclasses.Barline(TC, start, finish);
		} else {
			return false;
		}
	} else
		return false;
}

exports.BassChord = BassChord;
function BassChord(TC, flag, dotted, mainCourses, lbeams, rbeams, rFinish, bstart){
  // We have almost all we need to build a Chord -- just parse the
  // bass courses
 
  var bassCourses = [false, false, false, false, false, false, false, false];
  var fret = false;
  var prev = false;
  for(var i=0; i<TC.length; i++){
    var nextchar = TC.charAt(i);
    if(/[a-z]/.test(nextchar)){
      // FIXME: I *think* that this is based on a count of slashes, but I
      // haven't checked
      if(prev || prev===0) {
        alert([prev, i]);
        bassCourses[i-prev-1] = new bassNote(fret, TC.substring(prev, i), i-prev-1, bstart+i, false);
      }
      fret = nextchar;
      prev = i;
    } else if (/[1-9]/.test(nextchar)){
      //FIXME: CHECK -- this is old code, and seems wrong to me.
      //bassCourses[0] = new bassNote(nextchar, 0);
      bassCourses[nextchar] = new tabclasses.bassNote('a', TC.substring(prev, i), nextchar-1, bstart+i, true);
      break;
    }
  }
  if(fret) bassCourses[i-prev-1] = new tabclasses.bassNote(fret, TC.substring(prev, i), i-prev-1, false);

//  console.log("BassChord lbeams "+lbeams+" rbeams "+rbeams)
 
  return new tabclasses.Chord(flag, dotted, mainCourses, bassCourses, 0,0, lbeams, rbeams, rFinish);
}

exports.MainChord = MainChord;
function MainChord(TC, flag, dotted, start, finish, lbeams, rbeams, localStart, rFinish) {

// console.log("MainChord: lbeams "+lbeams+" rbeams "+rbeams)

  // We've got any rhythmic info we need, so now we parse
  // mainCourses.
	var mainCourses = [false, false, false, false, false, false];
	var curNote = false;
	var extras = "";
	var curchar;
	for(var maini=0; maini<TC.length; maini++) {
		curchar = TC.charAt(maini);
		if(maini!=TC.length - 1 && 
       ((base.tabletters.indexOf(curchar)>=0 && !isNaN(TC.charAt(maini+1)))
        ||
        (curchar == "-" && !isNaN(TC.charAt(maini+1))))) {
			// This is a fret/course pair
			var course = Number(TC.charAt(maini+1)) -1;
			curNote = new tabclasses.TabNote(curchar, "", maini+localStart, course);
      if(mainCourses[course]){
        TabCodeDocument.removes.push(mainCourses[course]);
      }
			mainCourses[course] = curNote;
			maini++;
		} else if(TC.charAt(maini)=="X") {
			return BassChord(TC.substr(maini+1), flag, dotted, mainCourses, lbeams, rbeams, rFinish, maini+1);
			break;
		} else if(curNote) {
/*
      // Ornaments, fingerings, lines, etc.
      var ex = curNote.extras.length;
      curNote.extendExtras(curchar);
			// and to save us from parenthetical brokenness
			if(curchar=="(") {
				while(curchar!=")") {
          if(curNote.extras.length > ex) curNote.extras.pop();
					maini++;
					if(maini==TC.length) break;
					curchar = TC.charAt(maini);
					curNote.extendExtras(curchar);
				}
			}
*/
		}
	}
	return new tabclasses.Chord(flag, dotted, mainCourses, [], start, finish, lbeams, rbeams, rFinish);
}

exports.makeTriplet = makeTriplet;
function makeTriplet(TC){
  curTripletGroup = new Triplet();
  if(TC.charAt(0)==="[") {
    curTripletGroup.editorial = true;
    TC = TC.substring(1);
  }
  var num = /^[0-9]+/.exec(TC);
  if(num){
    curTripletGroup.numerator = Number(num[0]);
    TC = TC.substring(num[0].length);
  }
  if(TC.charAt(0)==="]") TC = TC.substring(1);
  if(TC.charAt(0)==="("){
    TC = TC.substring(1);
    var den = /^[0-9]+/.exec(TC);
    if(den){
      curTripletGroup.denominator = Number(den[0]);
      TC = TC.substring(den[0].length);
    }
    if(rhythmFlags.indexOf(TC.charAt(0))>-1){
      curTripletGroup.unit = TC.charAt(0);
      TC = TC.substring(1);
    }
    if(TC.charAt(0)===")") TC = TC.substring(1);
  }
  return TC;
}

exports.TripletChord = TripletChord;
function TripletChord(TC, start, finish){
  TC = makeTriplet(TC);
  if(TC.length===0) return false;
  var firstchar = TC.charAt(0);
  if(firstchar.match(/[a-z]/)||firstchar=="X"||firstchar=="-"||firstchar=="."){
    return FlaglessChord(TC, start, finish, base.curBeams);
  } else if(firstchar==="[" || firstchar==="]"){
    return BeamedChord(TC, start, finish);
  } else if (rhythmFlags.indexOf(firstchar)>-1){
    return FlaggedChord(TC, start, finish, 0);
  }
  return false;
}

exports.FlaggedChord = FlaggedChord;
function FlaggedChord(TC, start, finish, lbeams){
  // If there are beams still open, then there is a mistake. Options are:
  //   * close them
  //   * allow them to go `through' the flag
  // Since this can only happen when there's an error, I'll do what's easiest.
	var flag = TC.charAt(0);
	if(TC.charAt(1)==="."){
		return MainChord(TC.substr(2), flag, true, start, finish, lbeams, lbeams, start+2, start+2);
	} else {
		return MainChord(TC.substr(1), flag, false, start, finish, lbeams, lbeams, start+1, start+1);
	}  
}

exports.FlaglessChord = FlaglessChord;
function FlaglessChord(TC, start, finish, lbeams){
  if(TC.charAt(0)=="."){
    return MainChord(TC.substr(1), false, true, start, finish, lbeams, lbeams, start, start);
  } else {
    return MainChord(TC, false, false, start, finish, lbeams, lbeams, start, start);
  }
}

exports.BeamedChord = BeamedChord;
function BeamedChord(TC, start, finish){
  // lbeams must match previous note, but rbeams must be altered for
  // how many new beams start and old ones end. Partial beams are
  // assumed, I think, not to exist in tablature.
  var beaminfo = /^[\[\]]*/.exec(TC)[0];
  var opens = (beaminfo.split("[").length -1);
  var closes = (beaminfo.split("]").length - 1);
  var partial = opens > 0 && closes > 0;
  var lpartial = partial && beaminfo.indexOf("[") < beaminfo.indexOf("]");
  var lbeams = lpartial ? base.curBeams + opens : base.curBeams;
  if(partial){
    var rbeams = lpartial ? lbeams - closes : base.curBeams + opens;
  } else {
    var rbeams = base.curBeams + opens - closes;
  }
// console.log("\tlbeams: "+lbeams+" rbeams: "+rbeams)
// console.log(TC+" : beaminfo: "+beaminfo+" lbeams: "+lbeams+" rbeams: "+rbeams)
  
  // FIXME: hacky guess
  //var beamends = start+opens+closes;
  var beamends = start+beaminfo.length;
  base.curBeams += opens - closes;
  var dotPos = TC.indexOf(".");
//  if(dotPos!=-1 && dotPos<TC.search(/[a-z]/)) {
  if(dotPos!=-1 && dotPos<=(beaminfo.length+1)){
    // rhythmic dot
    return MainChord(TC, false, true, start, finish, lbeams, rbeams, start, beamends+1);
  } else {
    return MainChord(TC, false, false, start, finish, lbeams, rbeams, start, beamends);
  }
}

