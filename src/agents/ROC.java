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


public class ROC extends Agent{
	static public boolean isActive = true;
	public String actKurs;
	public Double prevRoc=0.0;
	static public String NAME = "ROC";
	public int ostDecyzja;

	public ROC(){

		//System.out.println("Jestem agentem " + NAME +" (ID " + ID + "), juø øyje");
		numerOdczytu = 0;
		ID = 5000000;
	}
	public int go()
	{
		try
		{
			Webpage w = new Webpage();

			System.out.println("tick="+numerOdczytu);

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
					ResultSet rs=stmt.executeQuery("select a.Notowanie from (select Notowanie from "+ SQLOperator.getSqlTable() +" where id_agenta = "+ ID +" order by Id desc limit 10) as a");
					Double nminusk=0.0;
					while(rs.next()){
						nminusk = rs.getDouble("Notowanie");
					}

					System.out.print("notowanienminusk= " + nminusk + "\n");


					actKurs=actKurs.replace(",",".");
					actKurs=actKurs.replace(" z≥","");
					//System.out.println(actKurs);
					Double tmpKurs=Double.parseDouble(actKurs);
					System.out.print("aktualnykurs= " + tmpKurs + "\n");
					rs.close();
					Double ROC=0.0;
					if(nminusk !=0.0)
					{
						ROC=(tmpKurs-nminusk)/nminusk;

					}
					System. out.println("ROC: " + ROC);



					//wyznacznmik decyzji
					int decyzja=0;
					if((prevRoc<0)&&(ROC>0))
					{
						decyzja=-1;
					}
					if((prevRoc>0)&&(ROC<0))
					{
						decyzja=1;
					}

					prevRoc=ROC;
					rs=stmt.executeQuery("SELECT * FROM " + SQLOperator.getSqlTable() + " WHERE 1=2");






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
					rs.updateDouble("Wartosc_wskaznika",0.0);
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