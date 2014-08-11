import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;


public class TickTestDrive {
	
	Tick a = new Tick("TEST", 			'F', 'A', '0', 0, 0, "0", 0);
	Tick b = new Tick("TEST-TWO", 		'F', 'a', '0', 7000, 7001, "-0.045", 16);
	Tick c = new Tick("TEST-TWO-THREE",	'F', 'B', '0', 86399999, 86399999, "-1.06", 25);
	Tick d = new Tick("CO : CL1", 		'N', 'b', '0', 86399999, 12, "-100", 16);
	Tick e = new Tick("6HE4", 			'N', 'T', '0', 0, 0, "100", 17);
	Tick f = new Tick("$*L10", 			'N', 'A', '0', 0, 0, "100.048", 18);
	
	private static final HashMap<String, Integer> tickerDict;
    static
    {
        tickerDict = new HashMap<String, Integer>();
        tickerDict.put("TEST", 1);
        tickerDict.put("TWO", 2);
        tickerDict.put("THREE", 3);
        tickerDict.put("4", 4);
        tickerDict.put("5", 5);
        tickerDict.put("6", 6);
        tickerDict.put("7", 7);
        tickerDict.put("8", 8);
        tickerDict.put("9", 9);
        tickerDict.put("10", 10);
        tickerDict.put("11", 11);
        tickerDict.put("12", 12);
        tickerDict.put("13", 13);
        tickerDict.put("14", 14);
        tickerDict.put("15", 15);
        tickerDict.put("16", 16);
        tickerDict.put("CO : CL1", 17);
        tickerDict.put("6HE4", 18);
        tickerDict.put("$*L10", 19);        
    }

	
	@Test
	public void testGetTime() {
		//Return sendTime as is
		assertTrue(a.getTime() == 0);
		assertTrue(b.getTime() == 7000);
		assertTrue(c.getTime() == 86399999);
		
	}

	@Test
	public void testGetESC() {
		assertTrue(a.getESC().equals("FA0"));
		assertTrue(b.getESC().equals("Fa0"));
		assertTrue(c.getESC().equals("FB0"));
		assertTrue(d.getESC().equals("Nb0"));
		assertTrue(e.getESC().equals("NT0"));
		assertTrue(f.getESC().equals("NA0"));
	}

	@Test
	public void testGetCTimes() {
		//No Offset
		assertTrue(a.getCTimes(0).equals("0"));
		assertTrue(b.getCTimes(0).equals("1b58+1"));
		assertTrue(c.getCTimes(0).equals("5265bff"));
		assertTrue(d.getCTimes(0).equals("5265bff+-86399987"));
		//Positive Offset
		assertTrue(a.getCTimes(1).equals("ffffffff"));
		assertTrue(b.getCTimes(6000).equals("3e8+1"));
		assertTrue(c.getCTimes(86399998).equals("1"));
		assertTrue(d.getCTimes(86399999).equals("0+-86399987"));
		
	}

	@Test
	public void testGetCTicker() {
		//Test functionality
		assertTrue(a.getCTicker(tickerDict).equals("1"));
		assertTrue(b.getCTicker(tickerDict).equals("1-2"));
		assertTrue(c.getCTicker(tickerDict).equals("1-2-3"));
		//Test Hex compression
		assertTrue(d.getCTicker(tickerDict).equals("11"));
		assertTrue(e.getCTicker(tickerDict).equals("12"));
		assertTrue(f.getCTicker(tickerDict).equals("13"));
	}

	@Test
	public void testGetCPrice() {
		//Zero
		assertTrue(a.getCPrice().equals("0"));
		//Negative Decimal
		assertTrue(b.getCPrice().equals("-0.045"));
		//Negative whole + decimal
		assertTrue(c.getCPrice().equals("-1.06"));
		//Negative whole
		assertTrue(d.getCPrice().equals("-64"));
		//Positive whole
		assertTrue(e.getCPrice().equals("64"));
		//Positive whole + decimal
		assertTrue(f.getCPrice().equals("64.048"));
	}

	@Test
	public void testGetCSize() {
		assertTrue(a.getCSize().equals("0"));
		assertTrue(b.getCSize().equals("10"));
		assertTrue(c.getCSize().equals("19"));	
	}

}
