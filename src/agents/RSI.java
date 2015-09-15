package agents;

import datasources.Webpage;

// DIS AGENT BE BROKEN
// NO USE DIS PLOX
// THX M8S

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

import agents.SQLOperator;

import java.util.*;
import java.sql.Timestamp;

public class RSI extends Agent
{
	static public boolean isActive = false;
	static public String NAME = "RSI";
	public int ID = 6000000;
	public String actKurs;
	public String staryKurs;
	public String akcja="LOTOS";
	public int numerOdczytu;
	double tmp;
	Webpage web;

	protected void setup()
	{

		System.out.println("Jestem Agent " + NAME +" (ID " + ID + ") ju¿ ¿yje");

		addBehaviour(new TickerBehaviour(this, 20000){
			protected void onTick(){
				try{
					Webpage w = new Webpage();
					System.out.println("teraz odczytujê zawartoœæ strony co 60 sekund" + "tick="+getTickCount());
					Document doc = Jsoup.parse(w.getData(dataSource));
					Elements kurs = doc.select("div");
					for (Element src: kurs)
					{
						if (src.attr("class").equals("profilLast"))
						{
							actKurs = src.text();
							System.out.println(actKurs);
						}
					}
					System.out.println("³¹czê z baz¹");
					Class.forName("com.mysql.jdbc.Driver").newInstance();
					Connection conn = SQLOperator.connectToSQL();
					System.out.println("po³¹czono z baz¹");
					Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
					stmt.execute("USE swspiz_11");
					ResultSet rs=stmt.executeQuery("SELECT * FROM Gielda");
					rs = stmt.executeQuery("select a.Notowanie from (select Notowanie from Gielda where id_agenta = 6 order by Id desc limit 20) as a");
					rs.next();
					double[] tab = new double[20];
					double A = 0.0;
					double B = 0.0;
					for (int i = 0; i < 20; i++){
						tmp = rs.getDouble("Notowanie");
						System.out.println("tmp" + tmp);
						if (i%2==0){
							A+=tmp;
						}
						else {
							B+=tmp;
						}
					}
					A = A/20;
					B = B/20;
					double RSI = 100-(100/(1+(A/B)));

					System.out.println("RSI=" + RSI);
					int dec;
					if(RSI >= 70){
						dec = -1;
					}
					if(RSI <= 30){
						dec = 1;
					}
					else{
						dec = 0;
					}

					System.out.println("Decyzja" + dec);

					actKurs = actKurs.replace(",", ".");
					actKurs = actKurs.replace(" z³", "");
					Double tmpKurs=Double.parseDouble(actKurs);
					rs=stmt.executeQuery("SELECT * FROM Gielda");
					Calendar cal = Calendar.getInstance();
					Timestamp data;
					data = new Timestamp(cal.getTimeInMillis());
					rs.moveToInsertRow();
					rs.updateLong("Id_agenta", ID);
					rs.updateTimestamp("Data", data);
					rs.updateLong("Numer_Odczytu", (getTickCount()+8000));
					rs.updateDouble("Notowanie", tmpKurs);
					rs.updateString("Nazwa_Akcji", getSourceName());
					rs.updateDouble("Wartosc_wskaznika", RSI);
					rs.updateInt("Decyzja", dec);
					rs.insertRow();
					rs.close();
					stmt.close();
					conn.close();
				}


				catch (Exception e)
				{
					e.printStackTrace();

				}
			}

		});
	}
}




