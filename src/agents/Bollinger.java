package agents;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datasources.Webpage;


public class Bollinger extends Agent{ // AGENT 3
	static public boolean isActive = true;
	public String actKurs;
	static public String NAME = "Bollinger";
	public int ostDecyzja;
	public Bollinger(){

		System.out.println("Jestem agentem " + NAME +" (ID " + ID + ") yeah");
		numerOdczytu = 0;
		ID = 3000000;
		}
	public int go()
	{
		try
		{
		Webpage w = new Webpage();

		Document doc = Jsoup.parse
		(w.getData(dataSource));
		Elements kurs = doc.select("div");
			for(Element src : kurs) {
				if (src.attr("class").equals("profilLast"))
				{
					System.out.println("connected");

					actKurs=src.text();

					Class.forName("com.mysql.jdbc.Driver").newInstance();

					Connection conn = SQLOperator.connectToSQL();

					Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);

					stmt.execute("USE swspiz_11");


					ResultSet rs=stmt.executeQuery("SELECT * FROM Gielda");


					//obliczanie wskaznika williamsa
					//rs=stmt.executeQuery("select max(a.Notowanie), min(a.Notowanie) from (select Notowanie from Gielda where Id_agenta = -1 order by Id desc limit 9) as a");
					rs=stmt.executeQuery("select a.Notowanie from (select Notowanie from "+ SQLOperator.getSqlTable() +" where Id_agenta = "+ ID +" order by Id desc limit 20) as a");
					double[] tab = new double[20];
					double srednia=0.0, maxi=0.0, mini=0.0;
					if(rs.next())
					{

						double suma = 0;



						for(int i = 0; rs.next(); i++){
							double tmp = rs.getDouble("Notowanie");
							//System.out.println("i: " + i + " ---> " + tmp);
							tab[i]=tmp;
							suma += tmp;
						}

						srednia = suma/20.0;
						System.out.println(suma + " , " + srednia);


						double sumaOdchylenie = 0.0;

						for(int i = 0; i < 20; i++){
							sumaOdchylenie += (tab[i]-srednia)*(tab[i]-srednia);
						}

						double odchylenieStandardowe = Math.sqrt(sumaOdchylenie/20.0);

						maxi = srednia + 2.0*odchylenieStandardowe;
						mini = srednia - 2.0*odchylenieStandardowe;

						System.out.println("odchylenie: " + odchylenieStandardowe + " mini: " + mini + " maxi: " + maxi);


						//if(1==1)
						//return;
					}
					actKurs = actKurs.replace(",", ".");
					actKurs = actKurs.replace(" z³", "");

					Double tmpKurs = Double.parseDouble(actKurs);

					Calendar cal = Calendar.getInstance();
					Timestamp data = new Timestamp(cal.getTimeInMillis());
					rs=stmt.executeQuery("SELECT * FROM " + SQLOperator.getSqlTable());
					rs.moveToInsertRow();
					rs.updateLong("Id_agenta", ID);
					rs.updateTimestamp("Data", data);
					rs.updateLong("Numer_Odczytu", numerOdczytu);
					rs.updateDouble("Notowanie", tmpKurs);
					rs.updateString("Nazwa_akcji", getSourceName());
					rs.updateDouble("Wartosc_wskaznika", srednia);

					double last = tab[0];
					int decyzja=0;
					if(last < maxi && tmpKurs > maxi){
						//sprzedaj!
						decyzja = -1;
					}
					if(last > mini && tmpKurs < mini){
						//kupuj!
						decyzja = 1;
					}

					rs.updateInt("Decyzja", decyzja);

					rs.insertRow();

					String nazwa_akcji=rs.getString("Nazwa_akcji");
					System.out.println("nazwa_akcji:"+nazwa_akcji);
					rs.close();
					stmt.close();
					conn.close();
					ostDecyzja = decyzja;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ostDecyzja;
	}
}
