package datasources;

import java.util.*;

public class TickerParser {
	String rawData;
	Map< String, Map<String,String> > mapOfMaps;

	public TickerParser() {
		mapOfMaps = new HashMap< String, Map<String,String> >();
	}

	public double getAveragePrice(String URL) {
		double avgPrice = -1;
		try {
			String BUY = "";
			double BUY_d = 0;
			String SELL = "";
			double SELL_d = 0;
			Webpage w = new Webpage();
			rawData = w.getDataSSL(URL);
			/*int depth = 0;
			for(int i = 0; i < rawData.length(); i++) {
				if(rawData.charAt(i) == '{') {
					depth++;
				}
				else if (rawData.charAt(i) == '}') {
					depth--;
				}
				else if (depth == 1) {
					if
					for(;rawData.charAt(i) != '\"'; i++)
				}
			} */

			int ileQ = 0;
			int cntr = 0;
			for(; ileQ < 16; cntr++) {
				if (rawData.charAt(cntr) == '\"') {
					ileQ++;
				}
			}
			cntr++;
			for(; rawData.charAt(cntr) != ','; cntr++) {
				BUY += rawData.charAt(cntr);
			}
			BUY_d = Double.parseDouble(BUY);
			cntr+=8;
			for(; rawData.charAt(cntr) != ','; cntr++) {
				SELL += rawData.charAt(cntr);
			}
			SELL_d = Double.parseDouble(SELL);
			avgPrice = (BUY_d+SELL_d)/2;

			System.out.println("PARSER:");
			System.out.println("BUY: " + BUY);
			System.out.println("SELL: " + SELL);
			System.out.println(avgPrice);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return avgPrice;
	}

}
