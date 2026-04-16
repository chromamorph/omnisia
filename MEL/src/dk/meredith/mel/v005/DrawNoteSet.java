package dk.meredith.mel.v005;

import processing.core.*;

public class DrawNoteSet extends PApplet {

	private static final long serialVersionUID = 1L;

//	NoteSet noteSet = new BarberSonataBars9To10_03();
//	NoteSet noteSet = new DeutschFeroeFig1_02();
	NoteSet noteSet = new ChopinOp10No1_01();
	
	int maxPitch = max(128,noteSet.getMaxPitch());
	int minPitch = min(noteSet.getMinPitch(),0);
	int maxTime = noteSet.getMaxTime();
	int minTime = noteSet.getMinTime();
	int maxGeneration = noteSet.getMaxGeneration();
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
		int r = (n.generation % 3) * 80;
		int g = (n.generation % 5) * 50;
		int b = (n.generation % 2) * 125;
		stroke(r,g,b);
		fill(r,g,b);
		strokeWeight(2);
		strokeCap(ROUND);
		float x1 = map(n.onset,minTime,maxTime,minX,maxX);
		float y = map(n.pitch,minPitch,maxPitch,maxY,minY);
		float x2 = map(n.onset+n.duration,minTime,maxTime,minX,maxX);
		System.out.println(x1+" "+y+" "+x2);
		ellipse(x1,y,2,2);
		line(x1,y,x2,y);
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[] {"--bgcolor=#FFFFFF", "dk.meredith.mel.v005.DrawNoteSet"});
	}

	public void keyPressed() {
		generation++;
		for(Note n : noteSet.getNotes()) {
			if (n.generation == generation)
				drawNote(n);
		}
	}
}
