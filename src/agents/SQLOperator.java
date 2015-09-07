package agents;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLOperator extends Agent {
	static private String SQLLogin;
	static private String SQLPassword;
	static private String SQLURL;

	static public void setup(String _SQLLogin, String _SQLPassword, String _SQLURL)
	{
		SQLLogin = _SQLLogin;
		SQLPassword = _SQLPassword;
		SQLURL = _SQLURL;
	}

	static public Connection connectToSQL()
	{
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

}
