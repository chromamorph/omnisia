package dk.meredith.mel.v001;

import processing.core.*;

public class DrawNoteSet extends PApplet {

	private static final long serialVersionUID = 1L;

	NoteSet noteSet = new BarberSonataBars9To10_01();
	
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
		textAlign(RIGHT);
		drawAxes();
		for(Note n : noteSet.getNotes())
			drawNote(n);
	}
	
	private void drawAxes() {
		stroke(0);
		fill(0);
		strokeWeight(1);
		strokeCap(SQUARE);
		line(minX,minY,minX,maxY);
		line(minX,maxY,maxX,maxY);
		for(float p = 12 * (1+floor(minPitch/12.0f));p <= maxPitch; p += 12) {
			text(((int)p),margin-5,map(p,minPitch,maxPitch,maxY,minY));
		}
	}
	
	private void drawNote(Note n) {
		stroke(0);
		strokeWeight(1);
		strokeCap(ROUND);
		float x1 = map(n.onset,minTime,maxTime,minX,maxX);
		float y = map(n.pitch,minPitch,maxPitch,maxY,minY);
		float x2 = map(n.onset+n.duration,minTime,maxTime,minX,maxX);
		System.out.println(x1+" "+y+" "+x2);
		line(x1,y,x2,y);
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[] {"--bgcolor=#FFFFFF", "dk.meredith.mel.v001.DrawNoteSet"});
	}
}
