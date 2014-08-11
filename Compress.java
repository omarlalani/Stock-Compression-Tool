import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

/*
 * Handles the reading and writing of ticks and dictionaries to and from files
 */
public class Compress {
	/*
	 * LinkedList of ticks from uncompressed file (acts as a queue)
	 */
	private static LinkedList<Tick> ticks = new LinkedList<Tick>();

	/*
	 * HashMap of possible tickers, one-to-one relationship where key="ticker" and value=unique integer
	 */
	private static HashMap<String, Integer> tickerDict = new HashMap<String, Integer>();

	/*
	 * Read the uncompressed file and build the dictionary while placing each tick
	 * into a LinkedList acting as a queue. Sends back size of input file in bytes.
	 */
	public long readUncompressedFile(String fileName){
		File input = new File(fileName);
		try{
			FileReader fileReader = new FileReader(input);
			BufferedReader reader = new BufferedReader(fileReader);
			String line = null;
			int uniqueTickers = 0;
			while((line = reader.readLine()) != null){
				//Construct tick from line
				String[] splitTick = line.split(",");
				Tick a = new Tick(splitTick[0], splitTick[1].charAt(0), splitTick[2].charAt(0), splitTick[3].charAt(0), Integer.parseInt(splitTick[4]), Integer.parseInt(splitTick[5]), splitTick[6], Integer.parseInt(splitTick[7]));

				//Add new tickers to tickerDict HashMap
				String[] splitTicker = splitTick[0].split("-");
				for(int i = 0; i < splitTicker.length; i++){
					if(tickerDict.get(splitTicker[i]) == null){
						tickerDict.put(splitTicker[i], uniqueTickers++);
					}
				}

				//Add Compressed Tick to back of ArrayList()
				ticks.addLast(a);
			}
			reader.close();

		} catch(Exception ex){
			System.out.println("Error Reading Uncompressed File");
			ex.printStackTrace();
		}
		return input.length();
	}

	/*
	 * Calls functions to write the Dictionary and to write the compressed ticks from Linked List.
	 * Returns size of final compressed file in bytes.
	 */
	public long writeCompressedFile(String outFile){
		File output = new File(outFile);
		try{
			FileWriter fileWriter = new FileWriter(output);
			BufferedWriter writer = new BufferedWriter(fileWriter);

			//Write dictionary
			writeDictionary(writer);
			System.out.println("Dictionary Size: \t" + output.length() + " bytes");

			//Write compressed ticks
			writeCompressedTicks(writer);

			fileWriter.close();
		} catch(Exception ex){
			System.out.println("Error Writing Compressed File");
			ex.printStackTrace();
		}
		return output.length();
	}

	/*
	 * Writes the dictionary to the file
	 */
	private void writeDictionary(BufferedWriter writer){
		try{
			//First write size of dictionary
			writer.write(tickerDict.size() + "\n");

			//Invert hashmap to array
			String[] dict = new String[tickerDict.size()];
			for(String a:tickerDict.keySet()){
				dict[tickerDict.get(a)] = a;
			}

			//Write array to file
			for(String a:dict)
				writer.write(a + "\n");

			writer.flush();
		} catch(Exception ex){
			System.out.println("Error Writing Dictionary");
			ex.printStackTrace();
		}
	}

	/*
	 * Writes each compressed tick to the file
	 */
	private void writeCompressedTicks(BufferedWriter writer){
		try{
			int prevSTime = 0;
			for(Tick a:ticks){
				//Write Compressed Ticker
				writer.write(a.getCTicker(tickerDict));

				//Write Exchange, side, and condition
				writer.write(" " + a.getESC());

				//Write compressed sendTime and recvTime
				String finalTime = a.getCTimes(prevSTime);
				prevSTime = a.getTime();
				writer.write(finalTime);

				//Write compressed price
				writer.write(" " + a.getCPrice());

				//Write compressed size
				writer.write(" " + a.getCSize());

				//Write nextline
				writer.write("\n");
			}

			writer.flush();
		} catch(Exception ex){
			System.out.println("Error Writing Compressed Ticks");
			ex.printStackTrace();
		}
	}

	/*
	 * Read compressed file and read the necessary information to build dictionary for
	 * decompression
	 */
	public void deCompressFile(String inFile, String outFile){
		try{
			File input = new File(inFile);
			FileReader fileReader = new FileReader(input);
			BufferedReader reader = new BufferedReader(fileReader);
			String line = null;

			//Get Dictionary Size
			int dictSize = 0;
			if((line=reader.readLine()) != null)
				dictSize = Integer.parseInt(line);

			//Build Dictionary
			String[] tickerDict = new String[dictSize];
			for(int i = 0; i < dictSize; i++)
				if((line = reader.readLine()) != null)
					tickerDict[i] = line;

			//Decompress each tick and write straight to file
			try{
				File output = new File(outFile);
				FileWriter fileWriter = new FileWriter(output);
				BufferedWriter writer = new BufferedWriter(fileWriter);
				int prevSTime = 0;

				//Read each tick from file
				while((line = reader.readLine()) != null){
					cTick a = new cTick(line);

					//Write Decompressed Ticker
					writer.write(a.getDTicker(tickerDict));

					//Write Decompressed Exchange, Side, and Condition
					writer.write(a.getDESC());

					//Write Decompressed sendTime and recvTime
					String finalTimes = a.getDTimes(prevSTime);
					prevSTime = Integer.parseInt(finalTimes.substring(0, finalTimes.indexOf(",")));
					writer.write("," + finalTimes);

					//Write Decompressed Price
					writer.write("," + a.getDPrice());

					//Write Decompressed Size
					writer.write("," + a.getDSize() + "\r\n");

				}

				writer.flush();
				fileWriter.close();
				System.out.println("Decompression Complete");
				System.out.println("File Size: " + output.length() + " bytes");
			} catch(Exception ex){
				System.out.println("Error Writing Uncompressed File");
				ex.printStackTrace();
			}
			reader.close();
		} catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Error Decompressing File");
		}
	}

	public static void main(String[] args) {
		Compress a = new Compress();
		if(args[0].equals("-c") && args.length == 3){
			//Perform Compression
			long x = a.readUncompressedFile(args[1]);
			System.out.println("Input File Size: \t" + x + " bytes");
			long y = a.writeCompressedFile(args[2]);
			System.out.println("Output File Size:\t" + y + " bytes (Dictionary Included)");
			System.out.printf("Achieved Compression:\t%.2f percent", (1-(y*1.0/x))*100);
		}
		else if(args[0].equals("-d") && args.length == 3){
			//Perform Decompression
			a.deCompressFile(args[1], args[2]);
		}
		else{
			System.out.println("Usage: Compress [option] [input] [output]");
			System.out.println("Options:\n\t-c\tCompress file");
			System.out.println("\t-d\tDecompress file");
		}
	}
}
