package wig_1337;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.sql.*;

import agents.SQLOperator;

public class ROE {
	Calendar calBegin;
	Calendar calEnd;
	Vector<String> data;
	ROE () {
		calBegin.setTimeInMillis(0);
		calEnd.setTimeInMillis(Long.MAX_VALUE);
		data = new Vector<String>();
	}

	void setBegin(Calendar c) {
		calBegin = c;
	}

	void setEnd(Calendar c) {
		calEnd = c;
	}

	void getData() {
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = SQLOperator.connectToSQL();
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			stmt.execute("USE " + SQLOperator.getSqlDatabase());
			ResultSet rs=stmt.executeQuery("SELECT * FROM " + SQLOperator.getSqlTableSupervisor());
		}
		catch(Exception e) {
			System.out.println("TL;DR Exception in ROE");
			e.printStackTrace();
		}

	}

	void print() {

	}

}
