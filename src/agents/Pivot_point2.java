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
import java.util.Calendar;
import java.sql.Timestamp;

public class Pivot_point2 extends Agent{
	static public boolean isActive = false;
	public int dzien=7000;
	public String actKurs;
	public String staryKurs;
	public String akcja="JSW";
	public int dec=0, acttick, kurzamk, x;
	public double wart=0.0, stkurs, RS[]=new double[8];
	static public String NAME = "Pivot_point2";
	public int ostDecyzja;
	public Pivot_point2(){
		System.out.println("Hello World " + NAME +" (ID " + ID + "): OK");
		numerOdczytu = 0;
		ID=2000000;
		}
	public int go()
	{
		try{
			Webpage w = new Webpage();
			System.out.println("p: co 60s odczyt "+"tick="+numerOdczytu);
			Document doc = Jsoup.parse(w.getData(dataSource));
			Elements kurs=doc.select("div");
			for(Element src : kurs){
				if(src.attr("class").equals("profilLast")){
					actKurs=src.text();
					System.out.println("laacze z baza");
					Class.forName("com.mysql.jdbc.Driver").newInstance();
					Connection conn = SQLOperator.connectToSQL();
					System.out.println("polaczono z baza");
					Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
					stmt.execute("USE swspiz_11");
					ResultSet rs=stmt.executeQuery("SELECT * FROM Gielda");
					acttick=numerOdczytu/60;
					acttick*=60;
					acttick+=dzien;
					rs = stmt.executeQuery("select max(a.Notowanie), min(a.Notowanie)from(select Notowanie from Gielda where id_agenta = 2 AND Numer_Odczytu<"+acttick+" AND Numer_Odczytu>="+(acttick-60)+" order by Id desc limit 60) as a");
					rs.next();
					Double maxn=rs.getDouble(1);
					Double minn=rs.getDouble(2);
					rs.close();
					rs=stmt.executeQuery("select Notowanie from Gielda where Numer_Odczytu="+(acttick-1));
					rs.next();
					stkurs=rs.getDouble(1);
					rs.close();
					rs=stmt.executeQuery("select Notowanie from Gielda where Numer_Odczytu="+(numerOdczytu-1+dzien));
					rs.next();
					Double tmpkurs2=rs.getDouble(1);
					//rs.close();
					actKurs=actKurs.replace(",", ".");
					actKurs=actKurs.replace(" z³", "");
					Double tmpKurs= Double.parseDouble(actKurs);
					System.out.println("maxn=" + maxn);
					System.out.println("minn=" + minn);
					wart=(maxn+minn+stkurs)/3;
					System.out.println("p: " + wart);
					RS[0]=0;
					RS[1]=minn-2*(maxn-wart);
					RS[2]=wart-(maxn-minn);
					RS[3]=(2*wart)-maxn;
					RS[4]=(2*wart)-minn;
					RS[5]=wart+(maxn-minn);
					RS[6]=maxn+2*(wart-minn);
					RS[7]=1000000;
					System.out.println("S3=" + RS[1]);
					System.out.println("S2=" + RS[2]);
					System.out.println("S1=" + RS[3]);
					System.out.println("R1=" + RS[4]);
					System.out.println("R2=" + RS[5]);
					System.out.println("R3=" + RS[6]);
					for(int i=1; i<7; i++){
						if(RS[i]<tmpkurs2 && RS[i+1]>tmpkurs2){
							x=i;
							break;}
					}
					if(tmpKurs>RS[x+1]+0.005*RS[x+1]){
						dec=1;
						if(tmpKurs>RS[x+2]-0.01*RS[x+2])
						dec=-1;
					}
					else{
					if(tmpKurs<RS[x]-0.01*RS[x]){
						dec=-1;
						if(tmpKurs<RS[x-1])
						dec=1;
						}
					else
						dec=0;
					}
					if(numerOdczytu+dzien<60)
						dec=0;
					rs=stmt.executeQuery("SELECT * FROM " + SQLOperator.getSqlTable() + " WHERE 1=2");
					Calendar cal = Calendar.getInstance();
					Timestamp data;
					data = new Timestamp(cal.getTimeInMillis());
					rs.moveToInsertRow();
					rs.updateLong("Id_agenta", ID);
					rs.updateTimestamp("Data", data);
					rs.updateLong("Numer_Odczytu", (numerOdczytu+dzien));
					rs.updateDouble("Notowanie", tmpKurs);
					rs.updateString("Nazwa_Akcji", getSourceName());
					rs.updateDouble("Wartosc_wskaznika", wart);
					rs.updateInt("Decyzja", dec);
					rs.insertRow();
					rs.close();
					stmt.close();
					conn.close();
					ostDecyzja = dec;
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