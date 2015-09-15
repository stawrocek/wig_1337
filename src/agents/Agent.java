package agents;

public class Agent {
	public int ID = 0;
	static public String NAME = "Agent";
	protected String dataSource = "";
	public int numerOdczytu = 0;

	public Agent() {
		//System.out.println("Jestem agentem " + NAME +" (ID " + ID + ") yeah!");
	}

	public Agent(int IDincrease) {
		ID += IDincrease;
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

	public int go() {
		System.out.println("default_agent, returning...");
		return 0;
	}


}
