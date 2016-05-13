package dateLogger;

import processing.core.*;
import oscP5.*;
import netP5.*;
import java.util.Date;

import controlP5.*;
import java.util.Calendar;

public class Main extends PApplet {
	
	OscP5 osc;
	Logger logger;

	public void settings() {
		size(500, 200);
	}

	public void setup() {
		frameRate(5);
		//noSmooth();
		
		setPAppletSingleton();
		
		osc = new OscP5(this, 10000);
		
		logger = new Logger();

		
	}

	public void draw() {
		background(50);
		//drawBackLines();
		drawMouseCoordinates();
		
		logger.showEvents();
		

	}
	
	public void stop(){
		println("---|| CLOSING APP");
		logger.closeLog();
		super.stop();
	}
	
	void oscEvent(OscMessage message) {

		print("### received an osc message.");
		print(" addrpattern: " + message.addrPattern());
		println(" typetag: " + message.typetag());
		
		logger.log(message);
		

	}

	private void drawBackLines() {
		stroke(200);
		float offset = frameCount % 40;
		for (int i = 0; i < width; i += 40) {
			line(i + offset, 0, i + offset, height);
		}

	}

	private void drawMouseCoordinates() {
		// MOUSE POSITION
		fill(255, 0, 0);
		text("FR: " + frameRate, 20, 20);
		text("X: " + mouseX + " / Y: " + mouseY, mouseX, mouseY);
	}

	public void keyPressed() {
		//println("FC: " + frameCount);
		
		if(keyCode == ESC){
			stop();
		}
	}

	public void mousePressed() {

	}

	public void mouseReleased() {

	}

	public void mouseClicked() {
	}

	public void mouseDragged() {

	}

	public void mouseMoved() {

	}
	
	public void controlEvent(ControlEvent event) {
		logger.guiControlEvent(event);
	}
	

	public static void main(String args[]) {
		/*
		 * if (args.length > 0) { String memorySize = args[0]; }
		 */

		PApplet.main(new String[] { Main.class.getName() });
		// PApplet.main(new String[] {
		// "--present","--hide-stop",Main.class.getName() }); //
		// PRESENT MODE
	}

	private void setPAppletSingleton() {
		PAppletSingleton.getInstance().setP5Applet(this);
	}

}