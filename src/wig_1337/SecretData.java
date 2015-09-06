package wig_1337;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class SecretData {
	private String sqlLogin;
	private String sqlPassword;
	private String sqlUrl;
	
	public SecretData(){
		int lineCtr=0;
		try (BufferedReader br = new BufferedReader(new FileReader("assets/hiddenData.wig1337"))) {
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
	
	private void addData(String str, int line){
		if(line == 0){
			sqlLogin = str;
		}
		if(line == 1){
			sqlPassword = str;
		}
		if(line == 2){
			sqlUrl = str;
		}
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
