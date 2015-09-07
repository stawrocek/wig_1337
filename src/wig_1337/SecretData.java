package wig_1337;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;


public class SecretData {
	private String sqlLogin;
	private String sqlPassword;
	private String sqlUrl;
	private Map<String,String> DATA;

	public SecretData(){
		
		DATA = new HashMap<String, String>();
		int lineCtr=0;
		try  {
			BufferedReader br = new BufferedReader(new FileReader("assets/hiddenData.wig1337"));
			String line;
			while ((line = br.readLine()) != null) {
				addData(line, lineCtr);
				lineCtr++;
			}
		}
		catch (IOException e){
			System.out.println("Lol, IOException in SecretData.java");
		}
		
		sqlLogin = DATA.get("sqlLogin");
		sqlPassword = DATA.get("sqlPassword");
		sqlUrl = DATA.get("sqlUrl");
	}

	private void addData(String str, int lineCtr){
		//System.out.println(str);
		String strKey="";
		String strValue="";
		boolean foundEqual=false;
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) == '='){
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
		//System.out.println(strKey+"@"+strValue);
		DATA.put(strKey, strValue);
	}

	public String getDatabaseLogin(){
		return sqlLogin;
	}

	public String getDatabasePassword(){
		return sqlPassword;
	}

	public String getDatabaseUrl(){
		return sqlUrl;
	}
}
