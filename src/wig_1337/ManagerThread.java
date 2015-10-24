package wig_1337;

import java.util.Calendar;
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
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
	int mode = 0;
	Map<String,Boolean> agentState; //
	Vector<String> sourceURLs; // bankier.pl/BTC-E depending on the mode
	int activeAgentCount = 0;
	Agent [] container;
	Supervisor [] supervisors;

	//modes:
	static public int GPW = 1;
	static public int LTC = 2;

	public ManagerThread(int _msdelay, int _tick, int _mode, Vector<String> _sourceURLS, String [] _activeAgentsNames) {
		agentState = new HashMap<String, Boolean>();
		MANAGER_COUNT++;
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
	}

	public void run() {
		ticker();
	}
	private void ticker() {
		Calendar t1,t2,timeNow;
		Calendar calStart, calEnd;

		long diffTime = 0;
		for (int loop = tick+1; killMe == false; loop++) {
			t1 = Calendar.getInstance();
			/*if (t1.after(calEnd)) {
				while (t1.after(calEnd))
					t1 = Calendar.getInstance();
			} */
			timeNow = Calendar.getInstance();
			if (timeNow.after(calEnd)) {
				System.out.print("LOOP end@");
				System.out.println(showDateTime(timeNow));
				System.out.println("loop == " + loop + " (-1)");
			}
			for (int i = 0; i < sourceURLs.size()*activeAgentCount; i++){
				container[i].numerOdczytu = loop;
				container[i].go();
				if (loop >= 0 && (i+1) % activeAgentCount == 0) {
					supervisors[(i/activeAgentCount)].numerOdczytu = loop;
					supervisors[(i/activeAgentCount)].go();
				}
			}
			if (!killMe) {
			}
			t2 = Calendar.getInstance();
			diffTime = t2.getTimeInMillis() - t1.getTimeInMillis();
			while (diffTime < delay) {
				if (killMe)
					break;
				Thread.sleep(999);
			}

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
