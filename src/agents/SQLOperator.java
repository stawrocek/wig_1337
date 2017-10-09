package agents;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLOperator extends Agent {
	static private String SQLLogin;
	static private String SQLPassword;
	static private String SQLURL;
	static private String SQLDatabase;
	static private String SQLTable;
	static private String SQLTableSupervisor;

	static public void setup(
			String _SQLLogin, String _SQLPassword,
			String _SQLURL, String _SQLDatabase,
			String _SQLTable, String _SQLTableSupervisor) {
		//System.out.println("SQLOperator@setup()");
		SQLLogin = _SQLLogin;
		SQLPassword = _SQLPassword;
		SQLURL = _SQLURL;
		SQLDatabase = _SQLDatabase;
		SQLTable = _SQLTable;
		SQLTableSupervisor = _SQLTableSupervisor;
	}

	static public Connection connectToSQL() {
		Connection conn = null;
		try
		{
			conn = DriverManager.getConnection(SQLURL, SQLLogin, SQLPassword);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}

	static public String getSqlDatabase() {
		return SQLDatabase;
	}

	static public String getSqlTable() {
		return SQLTable;
	}

	static public String getSqlTableSupervisor() {
		return SQLTableSupervisor;
	}

}
