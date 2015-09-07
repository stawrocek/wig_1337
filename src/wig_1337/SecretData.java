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
	}

	private void addData(String str, int line)
	/* Reads line from input, text after ## is discarded
	 * format:
	 *
	 * ## example comment
	 * ExampleKey=ExampleValue
	 * ExampleKey2=$$Value55%	##example comment
	 * Key3 = "Elastische \"Schlussel\" Kappa"
	 *
	 */
	{
		try {
			boolean isKeyBuild = false;
			int q_counter = 0; // " counter
			boolean isLineComment = false;
			boolean isLineCorrupt = true;
			StringBuilder _Key = new StringBuilder();
			StringBuilder _Value = new StringBuilder();
			String Key = new String();
			String Value = new String();
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) == '#' && str.charAt(i+1) == '#' && q_counter != 1) {
					if(isKeyBuild == false) {
						isLineComment = true;
					}
					break;
				}
				else if (isKeyBuild = false) {
					if(str.charAt(i) == '=') {
						isKeyBuild = true;
						isLineCorrupt = false;
					}
					else {
						_Key.append(str.charAt(i));
					}
				}
				else {
					isLineCorrupt = false;
					if(str.charAt(i) == '\"' && str.charAt(i-1) != '\\')
					{
						_Value.append(str.charAt(i));
						q_counter++;
						if(q_counter == 2)
							break;
					}
					else
						_Value.append(str.charAt(i));
				}
			}
			if (isLineCorrupt == false && isLineComment == false) {
				Key = _Key.toString();
				Value = _Value.toString();
				Key.replaceAll("\\s+", "");

				String ValueCpy = Value;
				q_counter = 0;
				for(int i = 0; i < ValueCpy.length(); i++)
				{
					if(ValueCpy.charAt(i) == '\"')
					{
						q_counter++;
						Value.replaceFirst("\"", "");
					}
					else if(q_counter == 0)
					{
						Value.replaceFirst("\\s", "");
					}
				}
				DATA.put(Key, Value);
			}
		}
		catch(Exception E)
		{
			E.printStackTrace();
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
