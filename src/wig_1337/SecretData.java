package wig_1337;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;


public class SecretData {
	private Map<String,String> DATA;

	public SecretData(){
		//System.out.println("SecretData constructor");
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
}
