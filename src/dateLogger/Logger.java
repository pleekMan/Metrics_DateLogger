package dateLogger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
//import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import controlP5.*;

import oscP5.*;
import processing.core.PFont;

@SuppressWarnings("static-access")
public class Logger {

	Main p5;
	String logName;
	String filePath;
	BufferedReader logReader;
	PrintWriter logWriter;

	Date appInitDate;
	SimpleDateFormat dateFormat;
	SimpleDateFormat dateTimeFormat;
	// NetAddress remoteLocationToSend;

	String[] consoleLines;
	int consoleX, consoleY;

	public ControlP5 gui;
	//public Textarea consoleTextArea;
	//public Println console;
	public Button buttonClear;
	public Textfield nameText;

	public Logger() {
		p5 = getP5();

		consoleLines = new String[10];
		for (int i = 0; i < consoleLines.length; i++) {
			consoleLines[i] = "Line " + i;
		}
		consoleX = 30;
		consoleY = 50;

		
		initialize();

		PFont font = p5.createFont("arial",15);

		gui = new ControlP5(p5);
		buttonClear = gui.addButton("clearLog").setPosition(390, 50).setSize(100, 20).setLabel("Resetear archivo");
		nameText = gui.addTextfield("nameText").setPosition(350, 73).setSize(140, 20).setLabel("Nombre del Archivo").setFont(font);

	}

	public void showEvents() {

		p5.fill(200);
		p5.rect(0, 0, p5.width, consoleY - 12);

		p5.fill(30);
		p5.textSize(15);
		p5.text(logName, consoleX, consoleY - 23);
		p5.textSize(11);

		p5.fill(0, 127);
		p5.noStroke();
		p5.rect(0, consoleY - 12, p5.width, p5.height);
		
		p5.fill(0);
		p5.textAlign(p5.RIGHT);
		p5.text(p5.day() + "/" + p5.month() + "/" + p5.year() + " - " + p5.hour() + ":" + p5.minute() + ":" + p5.second(), p5.width - 10, 15);
		p5.text("OSC address -> localhost:10000 /log", p5.width - 10, 30);
		p5.textAlign(p5.LEFT);

		p5.fill(0);
		p5.stroke(255);
		p5.rect(0, 182, p5.width, 20);
		

		for (int i = 0; i < consoleLines.length; i++) {

			float posY = consoleY + (i * 16.2f);
			p5.fill(200);
			p5.text(consoleLines.length - i, consoleX - 25, posY);

			if (i == consoleLines.length - 1) {
				p5.fill(255);
			} else {
				float gradient = (i + 5f) / consoleLines.length;
				p5.fill(200 * gradient, 200 * gradient, 0);
			}
			p5.text(consoleLines[i], consoleX, posY);
		}

	}

	private void addToEventConsole(String event) {
		for (int i = 0; i < consoleLines.length - 1; i++) {
			consoleLines[i] = consoleLines[i + 1];
			consoleLines[consoleLines.length - 1] = event;
		}
	}

	public void log(OscMessage _message) {

		if (_message.addrPattern().equals("/log")) {
			Date now = new Date();
			String lineData = dateTimeFormat.format(now) + "," + _message.get(0).stringValue();
			logWriter.println(lineData);
			logWriter.flush();

			addToEventConsole(lineData);
		}

	}

	private void initialize() {
		logName = null;
		filePath = null;
		appInitDate = new Date();
		dateFormat = new SimpleDateFormat("yyyy-M-dd");
		dateTimeFormat = new SimpleDateFormat("yyyy-M-dd,HH:mm:ss");

		addToEventConsole("Today is: " + dateFormat.format(appInitDate) + " at: " + dateTimeFormat.format(appInitDate));
		checkFile(dateFormat.format(appInitDate));

	}

	private void checkFile(String todaysDateText) {
		filePath = "data/stats/statsLog_" + todaysDateText + ".txt";
		logReader = p5.createReader(filePath);
		try {
			logName = logReader.readLine();
			//p5.println("---||");
			//p5.println("---|| Stats Log File Exists --> " + logName + " :: " + todaysDateText);
			addToEventConsole("---||");
			addToEventConsole("---|| Stats Log File Exists --> " + logName + " :: " + todaysDateText);
			
			loadDataFromFile();

		} catch (Exception e) {
			//p5.println("---||");
			//p5.println("---|| Stats Log File NOT FOUND. CREATING LOG FILE WITH TODAY'S DATE: " + todaysDateText);
			addToEventConsole("---||");
			addToEventConsole("---|| Stats Log File NOT FOUND. CREATING LOG FILE WITH TODAY'S DATE: " + todaysDateText);
			addToEventConsole("---|| Stats Log File at: data/stats/statsLog_" + todaysDateText + ".txt");

			createDataFile();
		}

	}

	private void createDataFile() {
		logWriter = p5.createWriter(filePath);
		try{
			logName = p5.loadStrings("data/stats/info.txt")[0];
		} catch (Exception e){
			logName = "No Name " + dateFormat.format(appInitDate);
		}
		logWriter.println(logName);
		logWriter.flush();

	}

	private void loadDataFromFile() {
		// FILE ALREADY EXIST.
		// LETS LOAD THE DATA, CREATE FILE-TO-WRITE-TO (CLEARS PREVIOUS FILE)
		// WRITE TO IT
		// AND KEEP THE WRITER OPEN

		ArrayList<String> allLinesBuffer;
		allLinesBuffer = new ArrayList<String>();
		String lineString;

		// LOAD DATA
		try {
			while ((lineString = logReader.readLine()) != null) {
				allLinesBuffer.add(lineString);
			}
		} catch (Exception e) {
			addToEventConsole("---|| FILE IS EMPTY..!! ");
		}

		logWriter = p5.createWriter(filePath);

		// RE WRITE FILE
		logWriter.println(logName);
		for (int i = 0; i < allLinesBuffer.size(); i++) {
			logWriter.println(allLinesBuffer.get(i));
		}
		// UPDATE, BUT KEEP IT OPENED
		logWriter.flush();

	}
	
	private void eraseData(){
		
		PrintWriter eraserLogWriter;
		try {
			
			// NAME REPLACER
			changeName(gui.get(Textfield.class,"nameText").getText());
			
			eraserLogWriter = new PrintWriter(filePath);
			eraserLogWriter.println(logName);
			eraserLogWriter.close();
			addToEventConsole("LogFile Reseted // Name: " + logName);
		} catch (Exception e) {
			addToEventConsole("Could not Reset LogFile");
			//e.printStackTrace();
		}
		

	}
	
	private void changeName(String newName){
		//String newName = new String(logName);
		if (!newName.equals("")) {
			newName = gui.get(Textfield.class,"nameText").getText();
			logName = newName;
			gui.get(Textfield.class,"nameText").clear();

		}
	}

	public PrintWriter getLogWriter() {
		return logWriter;
	}

	public void closeLog() {
		logWriter.close();
	}
	
	public void controlEvent(ControlEvent event) {
		//p5.println("Boton Apretado");

		if (event.isFrom("clearLog")) eraseData();
		
		if(event.isFrom("nameText")){
			changeName(gui.get(Textfield.class,"nameText").getText());
		}

	}

	protected Main getP5() {
		return PAppletSingleton.getInstance().getP5Applet();
	}

}
