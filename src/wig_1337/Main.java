package wig_1337;

import agents.*;
import datasources.TickerParser;


public class Main {
	public static void main(String args[]){
		SecretData sec = new SecretData();
		SQLOperator.setup(
				sec.getDatabaseLogin(), sec.getDatabasePassword(),
				sec.getDatabaseUrl(), sec.getDatabaseDatabase(),
				sec.getDatabaseTableName(), sec.getDatabaseTableSupervisorName() );
		//System.out.println(sec.getDatabaseLogin());
		//System.out.println(sec.getDatabasePassword());
		//System.out.println(sec.getDatabaseUrl());
		//System.out.println(sec.getDatabaseDatabase());
		//System.out.println(sec.getDatabaseTableName());
		//System.out.println(sec.getDatabaseTableSupervisorName());
		TickerParser TP = new TickerParser();
		TP.getAveragePrice("https://btc-e.com/api/2/ltc_usd/ticker");

		System.out.println("---Wig_1337---");
		System.out.flush();
	}
}
