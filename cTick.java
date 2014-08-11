/*
 * This object represents a tick in its compressed format
 * This class contains the methods necessary for decompression of a tick
 */
public class cTick {
	private String cTicker;
	private String cESC;
	private String cTimeStamp;
	private String cPrice;
	private String cSize;
	
	/*
	 * Constructor: Takes compressed tick in and breaks it into the necessary parts
	 */
	public cTick(String tick){
		String[] tickPart = tick.split(" ");
		cTicker = tickPart[0];
		cESC = tickPart[1].substring(0, 3);
		cTimeStamp = tickPart[1].substring(3);
		cPrice = tickPart[2];
		cSize = tickPart[3];
	}
	
	/*
	 * Returns Decompressed Ticker
	 * Splits compressed ticker at "-" and then converts the hex strings to integers
	 * Integers are then used against tickerDict (the dictionary read from file) to determine the correct ticker value
	 * which is returned as a string.
	 */
	public String getDTicker(String[] tickerDict){
		String dTicker = "";
		String[] tickerPart = cTicker.split("-");
		for(int i = 0; i < tickerPart.length; i++){
			if(i>0)
				dTicker += "-" + tickerDict[(int) Long.parseLong(tickerPart[i], 16)];
			else
				dTicker += tickerDict[(int) Long.parseLong(tickerPart[i], 16)];
		}
		return dTicker;
	}
	
	/*
	 * Returns Decompressed ESC (Exchange, Side, Condition) codes
	 * Since no compression was used, it simply returns comma separated value.
	 */
	public String getDESC(){
		return "," + cESC.charAt(0) + ',' + cESC.charAt(1) + ',' + cESC.charAt(2);
	}
	
	/*
	 * Returns Decompressed Send and Receive Times
	 * If the timestamp contains a "+", then that indicates that there is a recvTime that differs from the sendTime
	 * The sendTime Offset is first converted back to an integer and then added to the prevSTime send in.
	 * Negative offsets (possible when the prev sendTime is greater than current sendTime) is handled when the
	 * Long is cast back to an Int.
	 */
	public String getDTimes(int prevSTime){
		int recvOffset = 0;
		int sendTime = 0;
		if(cTimeStamp.contains("+")){
			String[] tickTime = cTimeStamp.split("\\+");
			recvOffset = Integer.parseInt(tickTime[1]);
			sendTime = (int) Long.parseLong(tickTime[0], 16);
		}
		else
			sendTime = (int) Long.parseLong(cTimeStamp, 16);
		sendTime += prevSTime;
		int recvTime = sendTime + recvOffset;
		return sendTime + "," + recvTime;
	}
	
	/*
	 * Returns Decompressed Price.
	 * Only the whole number part of the price (if it was a decimal) has been compressed. Therefore
	 * We split the number by "." and turn the hexstring whole number back to an integer.
	 * Then we return the uncompressed whole number with the decimal separated fraction
	 */
	public String getDPrice(){
		String finalPrice = "";
		//Handle Negative
		if(cPrice.contains("-")){
			finalPrice += "-";
			cPrice = cPrice.substring(1);
		}
		//Handle Decimal
		if(cPrice.contains(".")){
			String[] tickPart = cPrice.split("\\.");
			finalPrice += Long.parseLong(tickPart[0], 16) + "." + tickPart[1];
		}
		else
			finalPrice += Long.parseLong(cPrice, 16);
		return finalPrice;
	}
	
	/*
	 * Returns the Decompressed size of the order
	 * Simply converts back to int from hexstring.
	 */
	public Long getDSize(){
		return Long.parseLong(cSize, 16);
	}
}
