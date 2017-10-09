package wig_1337;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;


public class SecretData {
	private Map<String,String> DATA; // SQL stuff
	private Vector<String> URLS; // List of URLS to use
	private int urlCounter = 0;

	public SecretData(){
		//System.out.println("SecretData constructor");
		urlCounter = 0;
		DATA = new HashMap<String, String>();
		URLS = new Vector<String>();
		int lineCtr=0;
		System.out.println("----HIDDEN DATA----");
		try  {
			BufferedReader br = new BufferedReader(new FileReader("assets/hiddenData.wig1337"));
			String line;
			while ((line = br.readLine()) != null) {
				addData(line, lineCtr);
				lineCtr++;
			}
			br.close();
			System.out.println("----HIDDEN DATA END----");
		}
		catch (IOException e){
			System.out.println("Lol, IOException in SecretData.java");
		}

		try  {
			BufferedReader br = new BufferedReader(new FileReader("assets/URLS.wig1337"));
			String line;
			System.out.println("----URLS----");
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				URLS.addElement(line);
				lineCtr++;
			}
			System.out.println("----URLS END----");
			br.close();
		}
		catch (IOException e){
			System.out.println("Lol, IOException in SecretData.java");
		}
	}

	private void addData(String str, int lineCtr){
		//System.out.println(str);
		String strKey="";
		String strValue="";
		boolean foundEqual=false;
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) == '=' && foundEqual == false){
				foundEqual=true;
			}
			else{
				if(foundEqual){
					strValue += str.charAt(i);
				}
				else{
					strKey += str.charAt(i);
				}
			}
		}
		System.out.println(strKey+"@"+strValue);
		DATA.put(strKey, strValue);
	}

	public String getDatabaseLogin(){
		return DATA.get("sqlLogin");
	}

	public String getDatabasePassword(){
		return DATA.get("sqlPassword");
	}

	public String getDatabaseUrl(){
		return DATA.get("sqlUrl");
	}

	public String getDatabaseDatabase(){
		return DATA.get("sqlDatabase");
	}

	public String getDatabaseTableName() {
		return DATA.get("sqlTable");
	}

	public String getDatabaseTableSupervisorName(){
		return DATA.get("sqlTableSupervisor");
	}

	public String nextURL() {
		if (urlCounter >= URLS.size()) {
			return new String("");
		}
		urlCounter++;
		return (String) URLS.elementAt(urlCounter-1);
	}
	
	public Vector<String> getAllURL() {
		return URLS;
	}

	public void resetURLCounter() {
		urlCounter = 0;
	}

	public int getUrlSourceSize() {
		return URLS.size();
	}
}
