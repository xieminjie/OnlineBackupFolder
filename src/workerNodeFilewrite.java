import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class workerNodeFilewrite {
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final String FILE_HEADER = "WORKER,SERVERADDRESS,PORT,STATUS";
	public synchronized static void addWorker(String file,String workername,String serverAddress,int port,String status){
		ArrayList<workerNode> hostList = new ArrayList<workerNode>();  
		hostList = workerNodeFileRead.readWorker(file);
		workerNode newNode = new workerNode(workername,serverAddress,port,status);
		hostList.add(newNode);
		FileWriter fileWriter = null;	
		try {
			fileWriter = new FileWriter(file);
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);		
			for (workerNode aworker : hostList) {
				fileWriter.append(aworker.getWorkername());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(aworker.getServerAddress());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(aworker.getPort()));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(aworker.getStatus());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			System.out.println("add new worker list");
			
		} catch (Exception e) {
			System.err.println("add new worker failure");
			e.printStackTrace();
		} finally {			
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
	}
	
}
