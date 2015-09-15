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


public class Supervisor {
	public String actKurs;
	int numerNotowania=0;
	int decyzja=0;
	public int numerOdczytu;
	public static String NAME = "Supervisor";
	public int ID = 2000000000; // 2 billion
	private String dataSource = "";
	public Supervisor(){

		System.out.println("Jestem agentem " + NAME + " yeah");
		numerOdczytu = 0;
	}
	public Supervisor(int _ID){

		System.out.println("Jestem agentem " + NAME + " yeah");
		numerOdczytu = 0;
		ID+=_ID;
	}

	public void setDataSource(String _URL) {
		dataSource = _URL;
	}

	public String getSourceName() {
		String out = "";
		try {
			int at = 0;
			for (int i = dataSource.length()-1; i >= 0 && dataSource.charAt(i) != '='; i--) {
				at = i;
			}
			for (int i = at; i < dataSource.length(); i++) {
				out += dataSource.charAt(i);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	public void go()
	{
		try
		{
			//numerOdczytu++;

			//System.out.println("tick="+numerOdczytu);
			numerNotowania = numerOdczytu;

			Class.forName("com.mysql.jdbc.Driver").newInstance();

			Connection conn = SQLOperator.connectToSQL();

			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			stmt.execute("USE swspiz_11");
			String query;

			query = "select Decyzja, Notowanie, Data from "+ SQLOperator.getSqlTable() +" WHERE Numer_Odczytu=" + numerNotowania + " AND Nazwa_akcji=\'" + getSourceName() +"\'";
			ResultSet rs=stmt.executeQuery(query);

			int ileMinus=0, ileZero=0, ilePlus=0;
			decyzja=0;
			double notowanie=0;
			Calendar cal = Calendar.getInstance();
			Timestamp data = new Timestamp(cal.getTimeInMillis());

			while(rs.next()){
				notowanie = rs.getDouble("Notowanie");
				data=rs.getTimestamp("Data");

				System.out.println("Pobrane: " + rs.getInt("Decyzja"));

				if(rs.getInt("Decyzja") == 1){
					ilePlus++;
				}
				else if(rs.getInt("Decyzja") == 0){
					ileZero++;
				}
				else{
					ileMinus++;
				}
			}




			if(ilePlus >= ileZero && ilePlus >= ileMinus){
				decyzja=1;
			}
			if(ileZero > ilePlus && ileZero > ileMinus){
				decyzja=0;
			}
			if(ileMinus >= ileZero && ileMinus > ilePlus){
				decyzja=-1;
			}

			System.out.println("Decyzja: " + decyzja);

			if (decyzja != 0) {
				data = new Timestamp(cal.getTimeInMillis());
				rs=stmt.executeQuery("SELECT * FROM " + SQLOperator.getSqlTableSupervisor());
				rs.moveToInsertRow();
				rs.updateLong("Id_agenta", ID);
				rs.updateTimestamp("Data", data);
				rs.updateLong("Numer_Odczytu", numerNotowania);
				rs.updateString("Nazwa_akcji", getSourceName());
				rs.updateDouble("Notowanie", notowanie);
				rs.updateDouble("Wolumen", 1.0);
				rs.updateInt("Decyzja", decyzja);

				rs.insertRow();

				rs.close();
			}
			stmt.close();
			conn.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
