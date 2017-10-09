package wig_1337;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.lang.Thread;

import agents.Agent;
import agents.Bollinger;
import agents.MACD;
import agents.Pivot_point2;
import agents.ROC;
import agents.Supervisor;
import agents.Williams;
// #TODO:
// Fix this shit and all agents

public class ManagerThread extends Thread{
	static int MANAGER_COUNT = 0;
	static String []AGENTS_LIST = {
		"Agent",
		"Williams",
		"Pivot_point2",
		"Bollinger",
		"MACD",
		"ROC",
		"RSI"
	};
	public boolean killMe = false;
	int delay = 0; // delay between subsequent ticks
	int tick = 0;
	int uniqueID = 0;
	int managerID;
	int mode = 0;
	Map<String,Boolean> agentState; //
	Vector<String> sourceURLs; // bankier.pl/BTC-E depending on the mode
	int activeAgentCount = 0;
	Agent [] container;
	Supervisor [] supervisors;

	//modes:
	static public int GPW = 1;
	static public int LTC = 2;

	//flags:
	public boolean silentOutput = false;

	public ManagerThread(int _msdelay, int _tick, int _mode, Vector<String> _sourceURLS, String [] _activeAgentsNames) {
		agentState = new HashMap<String, Boolean>();
		MANAGER_COUNT++;
		managerID = MANAGER_COUNT;
		for (int i = 0; i < MANAGER_COUNT; ++i) {
			uniqueID += 10000;
		}
		for (int i = 0; i < AGENTS_LIST.length; ++i) {
			agentState.put(AGENTS_LIST[i], false);
		}

		delay = _msdelay;
		tick = _tick;
		mode = _mode;
		sourceURLs = _sourceURLS;

		for (int i = 0; i < _activeAgentsNames.length; ++i) {
			agentState.put(_activeAgentsNames[i], true);
		}
		container = new Agent[sourceURLs.size()*_activeAgentsNames.length];
		supervisors = new Supervisor[sourceURLs.size()];
		int URLcounter = 0;
		activeAgentCount = _activeAgentsNames.length;
		System.out.println("Man id = " + managerID + ", activeAgentCount = " + activeAgentCount);

		try {
			for(int i = 0; i < sourceURLs.size()*_activeAgentsNames.length;URLcounter++) {
				String dataURL = sourceURLs.elementAt(URLcounter);
				if (i%_activeAgentsNames.length == 0){
					supervisors[(i/_activeAgentsNames.length)] = new Supervisor(uniqueID);
					supervisors[(i/_activeAgentsNames.length)].setDataSource(dataURL);
				}
				if (agentState.get("Bollinger") == true) {
					container[i] = new Bollinger();
					container[i].setDataSource(dataURL);
					container[i].ID += uniqueID;
					i++;
				}
				if (agentState.get("MACD") == true) {
					container[i] = new MACD();
					container[i].setDataSource(dataURL);
					container[i].ID += uniqueID;
					i++;
				}
				if (agentState.get("Pivot_point2") == true) {
					container[i] = new Pivot_point2();
					container[i].setDataSource(dataURL);
					container[i].ID += uniqueID;
					i++;
				}
				if (agentState.get("ROC") == true) {
					container[i] = new ROC();
					container[i].setDataSource(dataURL);
					container[i].ID += uniqueID;
					i++;
				}
				if (agentState.get("Williams") == true) {
					container[i] = new Williams();
					container[i].setDataSource(dataURL);
					container[i].ID += uniqueID;
					i++;
				}
				if (agentState.get("Agent") == true) {
					container[i] = new Agent();
					container[i].setDataSource(dataURL);
					container[i].ID += uniqueID;
					i++;
				}
				uniqueID++;
			}
		} catch (ArrayIndexOutOfBoundsException ae) {
			System.out.println("ArrayIndexOutOfBoundsException in ManagerThread constructor");
		}
	}

	static private Calendar parseDate(SimpleDateFormat F, String date)
	{
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(0);
		try {
		    cal1.setTime(F.parse(date));
		} catch (Exception e) {
			System.out.println("Critical error in ManagerThread.parseDate()");
		    e.printStackTrace();
		}
		return cal1;
	}

	static private String showDateTime(Calendar c) {
		String s = "";
		s+=c.get(Calendar.YEAR);
		s+=".";
		if (c.get(Calendar.MONTH)<10)
			s+="0";
		s+=c.get(Calendar.MONTH);
		s+=".";
		if (c.get(Calendar.DAY_OF_MONTH)<10)
			s+="0";
		s+=c.get(Calendar.DAY_OF_MONTH);
		s+=" ";
		if(c.get(Calendar.HOUR_OF_DAY) < 10) s+="0";
		s+=c.get(Calendar.HOUR_OF_DAY);
		s+=":";
		if(c.get(Calendar.MINUTE) < 10) s+="0";
		s+=c.get(Calendar.MINUTE);
		s+=":";
		if(c.get(Calendar.SECOND) < 10) s+="0";
		s+=c.get(Calendar.SECOND);
		s+=".";
		if(c.get(Calendar.MILLISECOND) < 100) s+="0";
		if(c.get(Calendar.MILLISECOND) < 10) s+="0";
		s+=c.get(Calendar.MILLISECOND);
		return s;
	}

	public void run() {
		ticker();
	}
	private void ticker() {
		Calendar t1,t2,timeNow;
		Calendar calStart, calEnd;
		long diffTime = 0;

		if (mode == 1) { try {
			SimpleDateFormat sdfWIG = new SimpleDateFormat("HH:mm:ss"); // Setup simple date format for current preset
			String sWIGStart = "07:55:00"; //UTC daily start time
			String sWIGEnd =   "16:00:00"; //UTC
			calStart = parseDate(sdfWIG,sWIGStart);
			calEnd   = parseDate(sdfWIG,sWIGEnd);
			timeNow = Calendar.getInstance();
			calStart.set(timeNow.get(Calendar.YEAR), timeNow.get(Calendar.MONTH), timeNow.get(Calendar.DAY_OF_MONTH));
			calEnd.set(timeNow.get(Calendar.YEAR), timeNow.get(Calendar.MONTH), timeNow.get(Calendar.DAY_OF_MONTH));
			System.out.println("Current time: " + showDateTime(timeNow));
			System.out.println("Start: " + showDateTime(calStart));
			System.out.println("Est. end: " + showDateTime(calEnd));
			if (timeNow.before(calStart))
				Thread.sleep(calStart.getTimeInMillis() - timeNow.getTimeInMillis());

			boolean skipLoop = false;
			for (int loop = tick; killMe == false;skipLoop = false) {
				t1 = Calendar.getInstance();

				if (t1.getTimeInMillis()+diffTime > calEnd.getTimeInMillis() + 10000 ||
					t1.getTimeInMillis() < calStart.getTimeInMillis() ||
					t1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || // WIG specific
					t1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) // WIG specific
					{ skipLoop = true; }

				if (skipLoop == false) {
					++loop;
					for (int i = 0; i < sourceURLs.size()*activeAgentCount; i++){
						container[i].numerOdczytu = loop;
						container[i].go();
						if ((i+1) % activeAgentCount == 0) {
							supervisors[(i/activeAgentCount)].numerOdczytu = loop;
							supervisors[(i/activeAgentCount)].go();
						}
					}
				}
				timeNow = Calendar.getInstance();
				if (timeNow.after(calEnd)) {
					System.out.print("Manager ID = " + managerID +"\nLOOP end@");
					System.out.println(showDateTime(timeNow) + " ##### loop == " + loop);
					int numberOfDaysToAdd = 1;
					if (timeNow.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) // WIG specific
						numberOfDaysToAdd = 3;
					else if (timeNow.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) // WIG specific
						numberOfDaysToAdd = 2;
					calStart.add(Calendar.DAY_OF_MONTH, numberOfDaysToAdd);
					calEnd.add(Calendar.DAY_OF_MONTH, numberOfDaysToAdd);
					while (timeNow.getTimeInMillis() < calStart.getTimeInMillis()) { // Waits until new calStart (usually next day)
						if (killMe == true)
							break;
						Thread.sleep(1000);
						timeNow = Calendar.getInstance();
					}
				}
				t2 = Calendar.getInstance();
				/*
				if (t2.getTimeInMillis()+diffTime > calEnd.getTimeInMillis() + 1000) {
					diffTime = delay - (calEnd.getTimeInMillis() - t2.getTimeInMillis()); // This forces upcomming while loop to wait until time specified by calEnd
					t2.setTimeInMillis(calEnd.getTimeInMillis());
				}
				else
					diffTime = t2.getTimeInMillis() - t1.getTimeInMillis();
				while (timeNow.getTimeInMillis() < delay) {
					if (killMe)
						break;
					timeNow = Calendar.getInstance();
					diffTime = timeNow.getTimeInMillis() - t2.getTimeInMillis();
					Thread.sleep(1000);
				} */
			} //endfor
		} //endtry
		catch (Exception e) {
			System.out.println("Critical exception in ManagerThread.ticker() id=" + managerID);
			e.printStackTrace();
		}
		} //endif
		if (mode == 2 ) {
			System.out.println("LTC mode not yet implemented.\tRIP in Pasta");
		}
	}

	public int getTick() {
		return tick;
	}

	public int terminate() {
		killMe = true;
		return getTick();
	}
}
