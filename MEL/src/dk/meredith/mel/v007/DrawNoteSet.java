package dk.meredith.mel.v007;

import processing.core.*;

public class DrawNoteSet extends PApplet {

	private static final long serialVersionUID = 1L;

//	NoteSet noteSet = new BarberSonataBars9To10_03();
//	NoteSet noteSet = new DeutschFeroeFig1_02();
	MEL noteSet = new ChopinOp10No5Bar1RH();
	
	int maxPitch = max(128,noteSet.getMaxPitch());
	int minPitch = min(noteSet.getMinPitch(),0);
	int maxTime = noteSet.getMaxTime();
	int minTime = noteSet.getMinTime();
	int w;
	int h;
	float minX;
	float maxX;
	float minY;
	float maxY;
	float margin = 60;
	PFont font;
	int generation = 0;
	float theta = 0f;
	
	public void setup() {
		w = floor(screenWidth*.75f);
		h = floor(screenHeight*.75f);
		minX = margin;
		maxX = w - margin;
		minY = margin;
		maxY = h - margin;
		size(w,h);
		smooth();
		background(255);
		font = createFont("Arial", 12);
		textFont(font);
		drawAxes();
	}
	
	public void draw() {
		for(Point n : noteSet.getPoints()) {
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
	
	private void drawNote(Point n) {
		stroke(0);
		fill(0);
		strokeWeight(2);
		strokeCap(ROUND);
		float x = map(n.getCoord(0),minTime,maxTime,minX,maxX);
		float y = map(n.getCoord(1),minPitch,maxPitch,maxY,minY);
		ellipse(x,y,2,2);
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[] {"--bgcolor=#FFFFFF", "dk.meredith.mel.v007.DrawNoteSet"});
	}
}
