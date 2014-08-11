import static org.junit.Assert.*;

import org.junit.Test;


public class cTickTestDrive {
	String[] tickerDict = {
			"ABCD","GEM8","GEU8","6AH8","6AM8","GEZ9","GEM0","ESH8","6JH8","CLH8","CLJ8","6CH8","6CM8",
			"6CU8","6SH8","RYH8","GEH0","GEZ0","NGH8","GEU9","PAH8","6JM8","J7H8","GEZ8","GEH9",
			"6EH8","CLK8","SIH8","NGJ8","NGK8","NGM8","CL:C1 HO",""};

	cTick a = new cTick("18 FA0267 7b 12");
	cTick b = new cTick("19 FB01a4 7b.46 0");
	cTick c = new cTick("7 FT0339 -7b 2b");
	cTick d = new cTick("1F NA0ae+1 -7b.46 1a");
	cTick e = new cTick("20 NB09 -0.046 1");
	cTick f = new cTick("1b-1c Fa04a1 0.046 a");
	cTick g = new cTick("1b-1c Fb00 0 64");
	cTick h = new cTick("2 FB07e3 5a.51 14");
	cTick i = new cTick("2 FB05265c00+-86399988 5a.51 14");

	@Test
	public void testGetDTicker() {
		//Standard 4 letter ticker
		assertTrue(a.getDTicker(tickerDict).equals("GEH9"));
		assertTrue(b.getDTicker(tickerDict).equals("6EH8"));
		assertTrue(c.getDTicker(tickerDict).equals("ESH8"));
		//Slightly Stranger
		assertTrue(d.getDTicker(tickerDict).equals("CL:C1 HO"));
		//Empty
		assertTrue(e.getDTicker(tickerDict).equals(""));
		//Two Tickers
		assertTrue(f.getDTicker(tickerDict).equals("SIH8-NGJ8"));
	}

	@Test
	public void testGetDESC() {
		assertTrue(a.getDESC().equals(",F,A,0"));
		assertTrue(b.getDESC().equals(",F,B,0"));
		assertTrue(c.getDESC().equals(",F,T,0"));
		assertTrue(d.getDESC().equals(",N,A,0"));
		assertTrue(e.getDESC().equals(",N,B,0"));
		assertTrue(f.getDESC().equals(",F,a,0"));
		assertTrue(g.getDESC().equals(",F,b,0"));
	}

	@Test
	public void testGetDTimes() {
		//Standard entries
		assertTrue(a.getDTimes(0).equals("615,615"));
		assertTrue(b.getDTimes(86400000).equals("86400420,86400420"));
		assertTrue(c.getDTimes(75).equals("900,900"));
		//Contains an offset recvTime
		assertTrue(d.getDTimes(0).equals("174,175"));
		assertTrue(i.getDTimes(0).equals("86400000,12"));
	}

	@Test
	public void testGetDPrice() {
		//Whole number only
		assertTrue(a.getDPrice().equals("123"));
		//Double
		assertTrue(b.getDPrice().equals("123.46"));
		//Whole negative
		assertTrue(c.getDPrice().equals("-123"));
		//Negative Double
		assertTrue(d.getDPrice().equals("-123.46"));
		//Small Negative
		assertTrue(e.getDPrice().equals("-0.046"));
		//Small Positive
		assertTrue(f.getDPrice().equals("0.046"));
		//Zero
		assertTrue(g.getDPrice().equals("0"));
	}

	@Test
	public void testGetDSize() {
		//Normal Value
		assertTrue(a.getDSize() == 18);
		//Zero
		assertTrue(b.getDSize() == 0);
	}

}
