
package agents;

import datasources.Webpage;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Williams extends Agent{
	static public boolean isActive = true;
	public String actKurs;
	static public String NAME = "Williams";
	public int ostDecyzja;
	public String nazwaAkcji;

	public Williams()
	{
		//System.out.println("Jestem agentem " + NAME +" (ID " + ID + "), juø øyje");
		numerOdczytu = 0;
		ID = 1000000;
	}
	public int go()
	{
		try
		{
			Webpage w = new Webpage();

		System.out.println("tick= "+numerOdczytu+1);

		//String page;

		Document doc = Jsoup.parse
		(w.getData(dataSource));
		Elements kurs = doc.select("div");
			for(Element src : kurs) {
				if (src.attr("class").equals("profilLast"))
				{
					actKurs=src.text();

					Class.forName("com.mysql.jdbc.Driver").newInstance();

					Connection conn = SQLOperator.connectToSQL();
					System.out.println("connected");
					Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					stmt.execute("USE swspiz_11");

					//OBLICZANIE WSKAèNIKA WILLIAMSA
					ResultSet rs=stmt.executeQuery("select max(a.Notowanie), min(a.Notowanie) from (select Notowanie from "+ SQLOperator.getSqlTable() +" where id_agenta = "+ ID +" order by Id desc limit 10) as a");
					double Williams=0.0,tmpKurs=0.0;
					int decyzja=0;
					if(rs.next())
					{
					Double maxn=rs.getDouble(1); //pierwsza kolumna
					Double minn=rs.getDouble(2);

					System.out.print("maxn=" + maxn + "\n");
					System.out.print("minn=" + minn + "\n");

					actKurs=actKurs.replace(",",".");
					actKurs=actKurs.replace(" z≥","");
					//System.out.println(actKurs);
					tmpKurs=Double.parseDouble(actKurs);
					rs.close();
					Williams=0.0;
					if((maxn-minn)!=0.0)
						{
							Williams=(tmpKurs-maxn)/(maxn-minn)*100;

						}
					System. out.println("Williams: " + Williams);



					//wyznacznmik decyzji
					decyzja=0;
					if(Williams<-80)
						{
							decyzja=1;
						}
					if(Williams>-20)
						{
							decyzja=-1;
						}


					rs=stmt.executeQuery("SELECT * FROM " + SQLOperator.getSqlTable());

					}




					Calendar cal = Calendar.getInstance();
					Timestamp data;
					data = new Timestamp(cal.getTimeInMillis());
					//rs.next();
					rs.moveToInsertRow();
					rs.updateLong("Id_agenta", ID);
					rs.updateTimestamp("Data", data);
					rs.updateLong("Numer_Odczytu",numerOdczytu);
					rs.updateDouble("Notowanie", tmpKurs);
					rs.updateString("Nazwa_akcji", getSourceName());
					rs.updateDouble("Wartosc_wskaznika",Williams);
					rs.updateInt("Decyzja",decyzja);
					//rs.updateString("Kurs", actKurs);
					//rs.updateDate("Data", )
					rs.insertRow();

					String nazwa_akcji=rs.getString("Nazwa_akcji");
					System.out.println("nazwa_akcji:"+nazwa_akcji);
					//rs.close();
					//stmt.close();
					//conn.close();
					System.out.println(actKurs);



						rs.close();
						stmt.close();
					conn.close();
					ostDecyzja = decyzja;
				}
			}



		//System.out.println(page);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ostDecyzja;
	}
}
