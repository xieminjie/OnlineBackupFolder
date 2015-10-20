import java.awt.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class workerNodeFileRead {
		private static final String COMMA_DELIMITER = ",";
		private static final int WORKERNAME = 0;
		private static final int SERVERADDRESS = 1;
		private static final int PORT = 2;
		private static final int STATUS = 3; 
		public static ArrayList<workerNode> readWorker(String fileName) {

			BufferedReader fileReader = null;
			ArrayList<workerNode> workerList = new ArrayList<workerNode>();       
	        try { 	
	            String line = "";
	            fileReader = new BufferedReader(new FileReader(fileName));
	            fileReader.readLine();
	            while ((line = fileReader.readLine()) != null) {
	                String[] tokens = line.split(COMMA_DELIMITER);
	                if (tokens.length > 0) {
						workerNode aNode = new workerNode(tokens[WORKERNAME], tokens[SERVERADDRESS], Integer.parseInt(tokens[PORT]), tokens[STATUS]);
						workerList.add(aNode);
					}
	            }
	        } 
	        catch (Exception e) {
	        	System.out.println("Error in CsvFileReader !!!");
	            e.printStackTrace();
	        } finally {
	            try {
	                fileReader.close();
	            } catch (IOException e) {
	            	System.out.println("Error while closing fileReader !!!");
	                e.printStackTrace();
	            }
	        }
	        return workerList;
		}
		
}
