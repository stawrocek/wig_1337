package agents;

public class Agent {
	public int ID = 0;
	static public String NAME = "Agent";

	public Agent()
	{
		System.out.println("Jestem agentem " + NAME +" (ID " + ID + ") yeah!");
	}

	public int go()
	{
		System.out.println("default_agent, returning...");
		return 0;
	}


}
