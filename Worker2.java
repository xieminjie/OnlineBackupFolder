import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;


public class Worker2 {
	static String folderID = "6";
	public static void main(String[] args) throws IOException, InterruptedException {
		
		System.setProperty("javax.net.ssl.keyStore","/Users/xieminjie/Documents/javaProject/clientkeystore");
		System.setProperty("javax.net.ssl.keyStorePassword","xieminjie");
		
		SSLServerSocketFactory sslserversocketfactory =(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket sslserversocket =(SSLServerSocket) sslserversocketfactory.createServerSocket(4444);
		while(sslserversocket != null){
	        SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();
	        DataInputStream in = new DataInputStream(sslsocket.getInputStream());
			DataOutputStream out = new DataOutputStream(sslsocket.getOutputStream());
			String folderName = "folder_"+folderID;
			boolean ifDone = createFolder(folderName);
			if(ifDone){
				out.writeUTF("done");
				out.flush();
			}else{
				out.writeUTF("undone");
				out.flush();
			}
	        downloadFile(sslsocket,folderName);
	        downloadFile(sslsocket,folderName);
	        String jobName = in.readUTF();//get jobname
	        String inputFileName = in.readUTF();//get inputFilename
	        String outputFileName = in.readUTF(); //get outputFilename
	        int jobTimeout = Integer.parseInt(in.readUTF());
	        File dir = new File(folderName);
	        String result = createProcess(jobName,inputFileName,outputFileName,dir,jobTimeout);
	        out.writeUTF(result);//return the result
	        out.flush();
	        if(result.equals("job finished")){
	        	File outputFile = new File(folderName+File.separator+outputFileName);
	        	sendFile(outputFile,sslsocket);
	        }else{
	        	File errorFile = new File(folderName+File.separator+"log.txt");
	        	sendFile(errorFile,sslsocket);
	        }
		}
	}
	public static void downloadFile(SSLSocket sslsocket,String folderName) throws IOException{
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(sslsocket.getInputStream()));
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(sslsocket.getOutputStream()));
        File file = new File(dataInputStream.readUTF());//1-get filenames and created the file
        int n = 0;
        byte[]buf = new byte[4092];
        	FileOutputStream fileOutputStream = new FileOutputStream(folderName+File.separator+file.getName());
        	long fileSize = dataInputStream.readLong();
        	while (fileSize > 0 && (n = dataInputStream.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1)
        	{
        	  fileOutputStream.write(buf,0,n);
        	  fileSize -= n;
        	}
        	fileOutputStream.close();
	}
	public synchronized static void sendFile(File file,SSLSocket sslsocket){
		try{			
			DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(sslsocket.getInputStream()));
	        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(sslsocket.getOutputStream()));	        
	        FileInputStream fileInputStream = null;
	        dataOutputStream.writeUTF(file.getName());//send the filename
	       	dataOutputStream.flush();
	        int n = 0;
	        byte[]buf = new byte[4092];
	        dataOutputStream.writeLong(file.length());
	        fileInputStream = new FileInputStream(file);
	         while((n =fileInputStream.read(buf)) != -1){
	            	dataOutputStream.write(buf,0,n);
	            	dataOutputStream.flush();
	            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println("job send fail");
        }		
	}
	public static synchronized boolean createFolder(String folderName) {
		boolean isDone = false;
		File theDir = new File(folderName);
		if (!theDir.exists()) {
			try {
				theDir.mkdir();
				isDone = true;
			} catch (SecurityException se) {
				System.err.println(se.getMessage());
			}
		}
		return isDone;
	}
	public static String createProcess(String jobName, String inputFile,
			String outputFile, File folder, int jobTimeout) throws IOException,
			InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar",
				jobName, inputFile, outputFile);
		processBuilder.directory(folder);
		processBuilder.redirectErrorStream(true);
		Path path = Paths.get(folder.getName(), "log.txt");
		File log = new File(path.toString());
		processBuilder.redirectOutput(Redirect.appendTo(log));
		Process process = processBuilder.start();
		String result;
		process.waitFor(jobTimeout, TimeUnit.SECONDS);
		process.destroy();
		process.waitFor();
		int exitValue = process.exitValue();
		if(exitValue==0){
			result = "job finished";
		}else if(exitValue==143){
			result = "job timeout";
		}else{
			result = "job failure";
		}
		return result;
	}
}
