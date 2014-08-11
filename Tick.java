import java.util.HashMap;

/*
 * This object represents a properly formed uncompressed ticker.
 * This class also contains the methods necessary for compression.
 */
public class Tick {
	private String ticker;
	private char exchange;
	private char side;
	private char condition;
	private int sendTime;
	private int recvTime;
	private String price;
	private int size;
	
	public Tick(String ticker, char exchange, char side, char condition, int sendTime, int recvTime, String price, int size){
		this.ticker = ticker;
		this.exchange = exchange;
		this.side = side;
		this.condition = condition;
		this.sendTime = sendTime;
		this.recvTime = recvTime;
		this.price = price;
		this.size = size;
	}
	
	/*
	 * Returns the sendTime. Used to calculate previous send time.
	 */
	public int getTime(){
		return sendTime;
	}
	
	/*
	 * Returns ESC (exchange, side, condition)
	 * Based on assumptions no compression was done here.
	 */
	public String getESC(){
		return "" + exchange + side + condition;
	}
	
	/*
	 * Returns compressed timestamp by using an offset from the prevSTime and then converting to hexstring.
	 * Also returns the recvTime as an offset only if it is greater than zero.
	 */
	public String getCTimes(int prevSTime){
		int offset = sendTime - prevSTime;
		String finalTime = Integer.toHexString(offset);
		if(recvTime != sendTime)
			finalTime += "+" + (recvTime - sendTime);
		return finalTime;
	}
	
	/*
	 * Returns compressed Ticker by using HashMap of all the tickers created during population of the LinkedList
	 * Uses "-" as a split to catch ticker comprised of two tickers
	 * The compressed Ticker is returned as a hexstring of integers indicating the index of the ticker in the hashmap
	 */
	public String getCTicker(HashMap<String, Integer> tickerDict){
		String compTicker = "";
		String[] splitTicker = ticker.split("-");
		for(int i = 0; i < splitTicker.length; i++){
			if(i > 0)
				compTicker += "-" + Integer.toHexString(tickerDict.get(splitTicker[i]));
			else
				compTicker += Integer.toHexString(tickerDict.get(splitTicker[i]));
		}
		return compTicker;
	}
	
	/*
	 * Returns compressed price
	 * Takes the price string and return the whole number part as a hexstring.
	 */
	public String getCPrice(){
		String finalPrice = "";
		//First deal with negatives
		if(price.charAt(0) == '-'){
			finalPrice += '-';
			price = price.substring(1);
		}
		//Then handle decimals
		if(price.contains(".")){
			String[] pricePart = price.split("\\.");
			finalPrice += Integer.toHexString(Integer.parseInt(pricePart[0])) + "." + pricePart[1];
		}
		else
			finalPrice += Integer.toHexString(Integer.parseInt(price));
		return finalPrice;
	}
	
	/*
	 * Returns compressed size
	 * Take the size and return it as a hexstring
	 */
	public String getCSize(){
		return Integer.toHexString(size);
	}
}
