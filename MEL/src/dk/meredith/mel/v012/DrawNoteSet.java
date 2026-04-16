package dk.meredith.mel.v012;

import processing.core.PApplet;
import processing.core.PFont;
import dk.meredith.mel.v012.MEL.Note;

public class DrawNoteSet extends PApplet {

	private static final long serialVersionUID = 1L;

	public static MEL mel = null;
	public static int w = 800;
	public static int h = 600;

	PFont font;
	float margin = 60;
	int maxPitch;
	int maxTime;
	float maxX;
	float maxY;
	int minPitch;
	int minTime;
	float minX;
	float minY;
	float theta = 0f;

	public DrawNoteSet(MEL mel) {
		super();
		DrawNoteSet.mel = mel;
		maxPitch = max(128,DrawNoteSet.mel.getMaxPitch());
		maxTime = DrawNoteSet.mel.getMaxTime();
		minPitch = min(DrawNoteSet.mel.getMinPitch(),0);
		minTime = DrawNoteSet.mel.getMinTime();
	}
	
	public void draw() {
		background(255);
		drawAxes();
		for(Note n : mel.getNotes().getNotes()) {
			drawNote(n);
		}			
	}

	private void drawAxes() {
		stroke(0);
		fill(0);
		strokeWeight(1);
		strokeCap(SQUARE);
		line(minX,minY,minX,maxY);
		line(minX,maxY,maxX,maxY);
		textAlign(RIGHT,CENTER);
		for(float p = 0;p <= maxPitch; p += 12) {
			float y = map(p,minPitch,maxPitch,maxY,minY);
			text(((int)p),margin-10,y);
			line(margin-10,y,minX,y);
			line(minX,y,maxX,y);
		}
		textAlign(CENTER,TOP);
		for(float t = 0;t <= maxTime; t += 10) {
			float x = map(t,minTime,maxTime,minX,maxX);
			text(((int)t),x,maxY+10);
			line(x,maxY,x,maxY+10);
		}
		pushMatrix();
		translate(margin/3,h/2);
		rotate(-PI/2);
		textAlign(CENTER,CENTER);
		text("Pitch",0,0);
		popMatrix();
		pushMatrix();
		translate(w/2,maxY+30);
		textAlign(CENTER,CENTER);
		text("Time",0,0);
		popMatrix();

	}

	private void drawNote(Note n) {
		stroke(0);
		fill(0);
		strokeWeight(2);
		strokeCap(ROUND);
		float x = map(mel.time(n),minTime,maxTime,minX,maxX);
		float y = map(mel.pitch(n),minPitch,maxPitch,maxY,minY);
		ellipse(x,y,2,2);
	}

	public void setup() {
		minX = margin;
		maxX = w - margin;
		minY = margin;
		maxY = h - margin;
		size(w,h);
		smooth();
		background(255);
		font = createFont("Arial", 14);
		textFont(font);
	}
}
