package wig_1337;

import agents.*;
import datasources.*;
import java.io.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;


public class Main {
	static private int activeAgents; // excluding supervisor, counted when initializing agents using isActive method
	static private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	static String sStart = "07:00:00"; // UTC
	static String sEnd   = "15:00:00"; // UTC
	static long lDelay = 1200000;
	static Calendar calStart;
	static Calendar calEnd;

	static private Calendar parseDate(SimpleDateFormat F, String date)
	{
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(0);
		try {
		    cal1.setTime(F.parse(date));
		} catch (Exception e) {
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
		System.out.println("Successfully created all agents");
		System.out.flush();

		calStart = parseDate(format,sStart);
		calEnd   = parseDate(format,sEnd);
		Calendar timeNow = Calendar.getInstance();
		/*  only if testing multiple times per day */
			calStart.set(timeNow.get(Calendar.YEAR), timeNow.get(Calendar.MONTH), timeNow.get(Calendar.DAY_OF_MONTH));
			calEnd.set(timeNow.get(Calendar.YEAR), timeNow.get(Calendar.MONTH), timeNow.get(Calendar.DAY_OF_MONTH));
		/*-----------------------------------------*/
		System.out.println("Current time: " + showDateTime(timeNow));
		System.out.println("Start: " + showDateTime(calStart));
		System.out.println("Est. end: " + showDateTime(calEnd));
		if (timeNow.before(calStart))
			Thread.sleep(calStart.getTimeInMillis() - timeNow.getTimeInMillis());

		File f = new File("assets/numerOdczytu.wig1337");
		int odczyt = 0;
		if (f.isFile()) {
			BufferedReader br = new BufferedReader(new FileReader("assets/numerOdczytu.wig1337"));
			String line = br.readLine();
			if (line != null)
				odczyt = Integer.parseInt(line);
		}
		boolean terminateMe = false;
		Calendar t1, t2;
		long diffTime = 0;
		// BEGIN DORITO SUPREME PROGRAM LOOP
		for (int loop = odczyt+1; terminateMe == false; loop++) {
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
				terminateMe = true;
			}
			for (int i = 0; i < sec.getUrlSourceSize()*activeAgents && !terminateMe; i++){
				container[i].numerOdczytu = loop;
				container[i].go();
				if (loop >= 0 && (i+1) % activeAgents == 0) {
					supervisors[(i/activeAgents)].numerOdczytu = loop;
					supervisors[(i/activeAgents)].go();
				}
			}
			if (!terminateMe) {
				PrintWriter writer = new PrintWriter("assets/numerOdczytu.wig1337", "UTF-8");
				writer.println(loop);
				writer.close();
			}
			t2 = Calendar.getInstance();
			diffTime = t2.getTimeInMillis() - t1.getTimeInMillis();
			if (diffTime < lDelay && !terminateMe)
				Thread.sleep(lDelay-diffTime);

		}

		System.out.println("---Wig_1337---");
		System.out.flush();
	}
	catch (Exception ex) {
		ex.printStackTrace();
	}
	}
}
