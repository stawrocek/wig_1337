package wig_1337;

import agents.*;

public class Main {
	public static void main(String args[]){
		SecretData sec = new SecretData();
		System.out.println("UTWORZONO");
		SQLOperator.setup(sec.getDatabaseLogin(), sec.getDatabasePassword(), sec.getDatabaseUrl());
		System.out.println("UTWORZONO");
		Agent M = new MACD();
		System.out.println("UTWORZONO");
		M.go();
		System.out.println("---Wig_1337---");
	}
}
