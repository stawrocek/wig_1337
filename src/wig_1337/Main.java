package wig_1337;

import agents.SQLOperator;


public class Main {
	public static void main(String args[]){
		SecretData sec = new SecretData();
		SQLOperator.setup(sec.getDatabaseLogin(), sec.getDatabasePassword(), sec.getDatabaseUrl());


		System.out.println("---Wig_1337---");
	}
}
