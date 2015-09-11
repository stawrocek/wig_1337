package datasources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class Webpage {

	String strona;
	public Webpage() throws Exception{

	}
	public String getPage(String url) throws Exception{
		return this.getData(url);
	}
	public String getData(String address) throws Exception {
		URL page = new URL (address);
		HttpURLConnection conn = (HttpURLConnection) page.openConnection();
		conn.connect();
		InputStreamReader in = new InputStreamReader((InputStream) conn.getContent(), "UTF-8");
		BufferedReader buff = new BufferedReader(in);
		String line = buff.readLine();
		StringBuilder line1 = new StringBuilder();
		while (line != null) {
			line1.append(line);
			line = buff.readLine();
		}
		strona = line1.toString();
		return strona;

	}

	public String getDataSSL(String address){
		try {
		URL page = new URL (address);
		HttpsURLConnection conn = (HttpsURLConnection) page.openConnection();
		conn.connect();
		InputStreamReader in = new InputStreamReader((InputStream) conn.getContent(), "UTF-8");
		BufferedReader buff = new BufferedReader(in);
		String line = buff.readLine();
		StringBuilder line1 = new StringBuilder();
		while (line != null) {
			line1.append(line);
			line = buff.readLine();
		}
		strona = line1.toString();

		}
		catch (Exception E) {
			E.printStackTrace();
		}
		return strona;
	}
}
