package com.chromamorph.points022;

import java.util.ArrayList;


public class Viewpoint {
	private String viewpoint;

	public String getString(){
		return viewpoint;
	}
	
	public Viewpoint(){
		viewpoint = "basic";
	}
	
	public Viewpoint(String view){
		viewpoint = view;
	}
	
	public final ArrayList<Point> applyViewpoint(ArrayList<Point> input){
		if(viewpoint == "basic"){
			return input;
		}
		else if(viewpoint == "int"){
			return viewpointInt(input);
		}
		else  if(viewpoint == "int0"){
			return viewpointInt0(input);
		}
		else  if(viewpoint == "intb"){
			return viewpointIntb(input);
		}
		else if(viewpoint == "ioi"){
			return viewpointIoi(input);
		}
		else  if(viewpoint == "ioib"){
			return viewpointIoib(input);
		}
		else if(viewpoint == "intioi"){
			return viewpointInt(viewpointIoi(input));
		}
		else if(viewpoint == "int0ioi"){
			return viewpointInt0(viewpointIoi(input));
		}
		else if(viewpoint == "intbioi"){
			return viewpointIntb(viewpointIoi(input));
		}
		else if(viewpoint == "intioib"){
			return viewpointInt(viewpointIoib(input));
		}
		else if(viewpoint == "int0ioib"){
			return viewpointInt0(viewpointIoib(input));
		}
		else if(viewpoint == "intbioib"){
			return viewpointIntb(viewpointIoib(input));
		}
		else if(viewpoint == "ioiioib"){
			return viewpointIoi(viewpointIoib(input));
		}
		else if(viewpoint == "ioiioi"){
			return viewpointIoi(viewpointIoi(input));
		}
		else if(viewpoint == "intioiioi"){
			return viewpointIoi(viewpointIoi(viewpointInt(input)));
		}
		else return input;
	}
	
	public final boolean isIntervalViewpoint(){
		return (viewpoint == "int") || (viewpoint == "int0") || (viewpoint == "intb");
	}
	
	
	private static ArrayList<Point> viewpointInt(ArrayList<Point> input){
		ArrayList<Point> trans = new ArrayList<Point>(input);
		Point p;
		for(int i = 1; i < input.size(); i++){
			p = new Point(input.get(i).getX(), input.get(i).getY()-input.get(i-1).getY());
			trans.set(i,p);
		}
		return trans;
	}
	
	private static ArrayList<Point> viewpointInt0(ArrayList<Point> input){
		ArrayList<Point> trans = new ArrayList<Point>(input);
		Point p;
		for(int i = 1; i < input.size(); i++){
			p = new Point(input.get(i).getX(), input.get(i).getY() - input.get(0).getY());
			trans.set(i,p);
		}
		return trans;
	}
	
	private static ArrayList<Point> viewpointIntb(ArrayList<Point> input){
		ArrayList<Point> trans = new ArrayList<Point>(input);
		Point p;
		for(int i = 1; i < input.size(); i++){
			int j = i-1;
			while(j>=0 && input.get(i).getY() != input.get(j).getY()){
				j--;
			}
			if (j < 0){
				p = input.get(i);
			}
			else {
				p = new Point(input.get(i).getX(), j-i);
			}
			trans.set(i,p);
		}
		return trans;
	}
	
	private static ArrayList<Point> viewpointIoi(ArrayList<Point> input){
		ArrayList<Point> trans = new ArrayList<Point>(input);
		Point p;
		for(int i = 1; i < input.size(); i++){
			p = new Point(input.get(i).getX()-input.get(i-1).getX(), input.get(i).getY());
			trans.set(i,p);
		}
		return trans;
	}

	private static ArrayList<Point> viewpointIoib(ArrayList<Point> input){
		ArrayList<Point> trans1 = new ArrayList<Point>(input);
		Point p;
		for(int i = 1; i < input.size(); i++){
			p = new Point(input.get(i).getX()-input.get(i-1).getX(), input.get(i).getY());
			trans1.set(i,p);
		}
		ArrayList<Point> trans = new ArrayList<Point>(trans1);
		for(int i = 1; i < trans.size(); i++){
			int j = i-1;
			while(j>=0 && trans1.get(i).getX() != trans1.get(j).getX()){
				j--;
			}
			if (j < 0){
				p = trans1.get(i);
			}
			else {
				p = new Point(j-i, trans1.get(i).getY());
			}
			trans.set(i,p);
		}
		return trans;
	}

}
