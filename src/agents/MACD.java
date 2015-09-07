package agents;

import datasources.Webpage;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;
import java.sql.Timestamp;

public class MACD extends Agent{
	public String actKurs;
	static public String NAME = "MACD";
	public int ID = 4;
	public int dlugiTermin = 26;
	public int krotkiTermin = 12;
	public int srednia = 9;
	public double alpha = 0.666666;
	public double prev_MACD = -999999;
	public double prev_signal = -999999;
	public int numerOdczytu = 0;
	public int ostatniaDecyzja; //ostatnia

	public MACD(){

		System.out.println("Jestem agentem " + NAME +" (ID " + ID + ") ju¿ ¿yje");



	}
	public int go()
	{
		try
		{

			Webpage w = new Webpage();

			System.out.println("tick = "+numerOdczytu);

			if (dlugiTermin <= krotkiTermin)
			{
				System.out.println("BLAD, dlugiTermin <= krotkiTermin");
				return -999;
			}
			numerOdczytu++;
			if(numerOdczytu < dlugiTermin)
				System.out.println("Zbieranie danych: " + numerOdczytu +"/"+dlugiTermin);

			//String page;

			Document doc = Jsoup.parse
			(w.getData("http://www.bankier.pl/inwestowanie/profile/quote.html?symbol=JSW"));
			Elements kurs = doc.select("div");
			for(Element src : kurs) {
				if (src.attr("class").equals("profilLast"))
				{
					actKurs=src.text();

					Class.forName("com.mysql.jdbc.Driver").newInstance();

					Connection conn = SQLOperator.connectToSQL();

					System.out.println("po¹czono z baz¹");
					Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					stmt.execute("USE swspiz_11");
					//WSKAZNIK MACD
					ResultSet rs;
					double MACD_value = -0.0;
					double signal_line = -0.0;
					//if (!(getTickCount() < dlugiTermin))
					{
						rs=stmt.executeQuery("select a.Notowanie from(select Notowanie from Gielda where id_agenta = 4 order by Id desc limit "+(dlugiTermin-1)+") as a");
						double[] dane = new double[dlugiTermin+1];
						int dane_size = 0;
						for (int i = 1; i <= dlugiTermin-1 && rs.next(); i++)
						{
							dane[i] = rs.getDouble(1);
							dane_size=i;
						}

						// MSA w dlugim i krotkim okresie
						double Y, S, S_dlugie, S_krotkie = 0.0;
						S = dane[1];
						Y = dane[1];
						for(int i = 2; i <= dlugiTermin-1 && i <= dane_size;i++)
						{
							Y = dane[i];
							S = alpha * Y + (1.0-alpha) * S;
							if(i == krotkiTermin)
								S_krotkie = S;
						}
						S_dlugie = S;
						// MACD
						MACD_value = (S_krotkie-S_dlugie)*100;
						rs.close(); //close
						String query1="select a.Wartosc_wskaznika from(select Wartosc_wskaznika from Gielda where id_agenta = 4 order by Id desc limit "+(srednia-1)+") as a";
						rs=stmt.executeQuery(query1);
						dane = new double[srednia+1];
						dane_size = 0;
						for (int i = 1; i <= srednia-1 && rs.next(); i++)
						{
							dane[i] = rs.getDouble(1);
							dane_size = i;
						}

						Y = dane[1];
						S = dane[1];
						for(int i = 2; i <= srednia-1 && i <= dane_size;i++)
						{
							Y = dane[i];
							S = alpha * Y + (1.0-alpha) * S;
						}
						signal_line = S;
						rs.close();

					}
					rs=stmt.executeQuery("SELECT * FROM Gielda");
					actKurs=actKurs.replace(",", ".");
					actKurs=actKurs.replace(" z³", "");
					actKurs=actKurs.replace(" ", "");
					actKurs=actKurs.replace("&nbsp;", "");
					System.out.println("ActKurs: " + actKurs);
					Double tmpKurs = Double.parseDouble(actKurs);
					int decyzja = 0;
					//if(!(getTickCount() < dlugiTermin))
					{
						System.out.println("Wskaznik MACD: " + MACD_value);
						System.out.println("Linia sygnalu" + signal_line);
						if (prev_MACD > -999990 && numerOdczytu >= (dlugiTermin+krotkiTermin)/2)
						{
							if (signal_line > MACD_value && prev_signal <= prev_MACD)
								decyzja = 1;
							else if (signal_line < MACD_value && prev_signal >= prev_MACD)
								decyzja = -1;
						}
					}
					prev_signal=signal_line;
					prev_MACD=MACD_value;

					Calendar cal = Calendar.getInstance();
					Timestamp data;
					data = new Timestamp(cal.getTimeInMillis());
					rs.moveToInsertRow();
					rs.updateLong("Id_agenta", ID);
					rs.updateTimestamp("Data",data);
					rs.updateLong("Numer_Odczytu", numerOdczytu);
					rs.updateDouble("Notowanie", tmpKurs);
					rs.updateString("Nazwa_akcji", "JSW");
					rs.updateDouble("Wartosc_wskaznika", MACD_value);
					rs.updateInt("Decyzja", decyzja);
					String nazwa_akcji=rs.getString("Nazwa_akcji");
					System.out.println("nazwa_akcji: "+nazwa_akcji);
					rs.insertRow();
					//rs.next();




					rs.close();
					stmt.close();
					conn.close();
					ostatniaDecyzja = decyzja;
				}
			}



			//System.out.println(page);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ostatniaDecyzja;
	}
}