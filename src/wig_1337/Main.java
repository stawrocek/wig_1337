package wig_1337;

import agents.*;
import datasources.*;
import java.io.*;
import javax.xml.datatype.Duration;
import java.util.Date;


public class Main {
	static private int activeAgents; // excluding supervisor, counted when initializing agents using isActive method

	public static void main(String args[]){
	try {
		SecretData sec = new SecretData();
		SQLOperator.setup(
				sec.getDatabaseLogin(), sec.getDatabasePassword(),
				sec.getDatabaseUrl(), sec.getDatabaseDatabase(),
				sec.getDatabaseTableName(), sec.getDatabaseTableSupervisorName() );
		//System.out.println(sec.getDatabaseLogin());
		//System.out.println(sec.getDatabasePassword());
		//System.out.println(sec.getDatabaseUrl());
		//System.out.println(sec.getDatabaseDatabase());
		//System.out.println(sec.getDatabaseTableName());
		//System.out.println(sec.getDatabaseTableSupervisorName());
		//TickerParser TP = new TickerParser();
		//TP.getAveragePrice("https://btc-e.com/api/2/ltc_usd/ticker/");
		System.out.flush();
		//HttpsClient hpee = new HttpsClient();
		//hpee.testIt();

		// INIT ALL AGENTS
		activeAgents = 0;
		if (Bollinger.isActive == true) 	activeAgents++;
		if (MACD.isActive == true) 			activeAgents++;
		if (Pivot_point2.isActive == true) 	activeAgents++; // NOPE
		if (ROC.isActive == true)			activeAgents++;
		if (Williams.isActive == true)		activeAgents++;
		//if (RSI.isActive == true)					activeAgents++;
		Agent [] container = new Agent[sec.getUrlSourceSize()*activeAgents];
		Supervisor [] supervisors = new Supervisor[sec.getUrlSourceSize()];
		int uniqueID = 0;
		String dataURL = "";

		//System.out.println(sec.getUrlSourceSize());

		for(int i = 0; i < sec.getUrlSourceSize()*activeAgents;) {
			dataURL = sec.nextURL();
			if (i%activeAgents == 0){
				supervisors[(i/activeAgents)] = new Supervisor(uniqueID);
				supervisors[(i/activeAgents)].setDataSource(dataURL);
			}
			if (Bollinger.isActive == true) {
				container[i] = new Bollinger();
				container[i].setDataSource(dataURL);
				container[i].ID += uniqueID;
				i++;
			}
			if (MACD.isActive == true) {
				container[i] = new MACD();
				container[i].setDataSource(dataURL);
				container[i].ID += uniqueID;
				i++;
			}
			if (Pivot_point2.isActive == true) 	{
				container[i] = new Pivot_point2();
				container[i].setDataSource(dataURL);
				container[i].ID += uniqueID;
				i++;
			}
			if (ROC.isActive == true) {
				container[i] = new ROC();
				container[i].setDataSource(dataURL);
				container[i].ID += uniqueID;
				i++;
			}
			if (Williams.isActive == true) {
				container[i] = new Williams();
				container[i].setDataSource(dataURL);
				container[i].ID += uniqueID;
				i++;
			}
			uniqueID++;
		}
		System.out.flush();

		// BEGIN DORITO SUPREME PROGRAM LOOP
		File f = new File("assets/numerOdczytu.wig1337");
		int odczyt = 0;
		if (f.isFile()) {
			BufferedReader br = new BufferedReader(new FileReader("assets/numerOdczytu.wig1337"));
			String line = br.readLine();
			if (line != null)
				odczyt = Integer.parseInt(line);
		}
		boolean terminateMe = false; // PLS IMPLEMENT SMTH
		Date t1, t2;
		Duration t;
		for (int loop = odczyt; terminateMe == false; loop++) {
			t1 = new Date();
			for (int i = 0; i < sec.getUrlSourceSize()*activeAgents; i++){
				container[i].numerOdczytu = loop;
				container[i].go();
				if (loop >= 0 && (i+1) % activeAgents == 0) {
					supervisors[(i/activeAgents)].numerOdczytu = loop;
					supervisors[(i/activeAgents)].go();
				}
			}
			PrintWriter writer = new PrintWriter("assets/numerOdczytu.wig1337", "UTF-8");
			writer.println(loop);
			writer.close();
			t2 = new Date();
			Thread.sleep(270000);

		}

		System.out.println("---Wig_1337---");
		System.out.flush();
	}
	catch (Exception ex) {
		ex.printStackTrace();
	}
	}
}
