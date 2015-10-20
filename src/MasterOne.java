import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MasterOne {
	static ArrayList<File> filesArrayList;

	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {
		String clientkeystore = "/Users/xieminjie/Documents/javaProject/FileTransmission/key.cer";
		String password = "xieminjie";
		String serverAddress = "localhost";
		int serverPort = 8000;
		System.setProperty("javax.net.ssl.trustStore", clientkeystore);
		System.setProperty("javax.net.ssl.trustStorePassword", password);

		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
				.getDefault();
		String check = checkStatus(sslsocketfactory,serverAddress,serverPort); 
		System.out.println(check);
		
		// String serverAddress = "146.118.96.255";
	/*	String serverAddress = "localhost";
		int serverPort = 8000;
		worker2,146.118.97.134,8000,unknown
		String jobFilename = "/Users/xieminjie/Documents/javaProject/wordcount.jar";
		String inputFilename2 = "/Users/xieminjie/Documents/javaProject/sample-input2.txt";
		String inputFilename = "/Users/xieminjie/Documents/javaProject/sample-input.txt";
		String outputFilename = "output.txt";
		String outputFilename2 = "output2.txt";
		String outputFilename3 = "output3.txt";
		int timeout = 100;

		String clientkeystore = "key.cer";
		String password = "xieminjie";

		System.setProperty("javax.net.ssl.trustStore", clientkeystore);
		System.setProperty("javax.net.ssl.trustStorePassword", password);

		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
				.getDefault();
		SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(
				serverAddress, serverPort);
				
		Thread t1 = new Thread(new MasterThread(jobFilename, inputFilename2,
				10, outputFilename, sslsocket));
		t1.start();

		SSLSocket sslsocket2 = (SSLSocket) sslsocketfactory.createSocket(
				serverAddress, serverPort);
		Thread t2 = new Thread(new MasterThread(jobFilename, inputFilename, 5,
				outputFilename2, sslsocket2));
		t2.start();
		SSLSocket sslsocket3 = (SSLSocket) sslsocketfactory.createSocket(
				serverAddress, serverPort);
		Thread t3 = new Thread(new MasterThread(jobFilename, inputFilename2,
				timeout, outputFilename3, sslsocket3));
		t3.start();*/

	}

	public static void job(String jobFilename, String inputFilename,
			int timeout, String outputFilename, SSLSocket sslsocket)
			throws IOException {
		File jobFile = new File(jobFilename);
		File inputFile = new File(inputFilename);
		File outputFile = new File(outputFilename);
		DataInputStream in = new DataInputStream(sslsocket.getInputStream());
		DataOutputStream out = new DataOutputStream(sslsocket.getOutputStream());
		
		out.writeUTF("job");
		out.flush();
		
		sendFile(jobFile, sslsocket);// send job file
		sendFile(inputFile, sslsocket);// send input file
		out.writeUTF(jobFile.toPath().getFileName().toString());
		out.flush();
		out.writeUTF(inputFile.toPath().getFileName().toString());
		out.flush();
		out.writeUTF(outputFile.toPath().getFileName().toString());
		out.flush();
		out.writeUTF(Integer.toString(timeout));
		out.flush();
		String result = in.readUTF();
		System.out.println(result);
		downloadFile(sslsocket, "output");
	}

	public static void downloadFile(SSLSocket sslsocket, String folderName)
			throws IOException {
		DataInputStream dataInputStream = new DataInputStream(
				new BufferedInputStream(sslsocket.getInputStream()));
		DataOutputStream dataOutputStream = new DataOutputStream(
				new BufferedOutputStream(sslsocket.getOutputStream()));
		String filename = dataInputStream.readUTF();
		System.out.println("MasterOne get file" + filename);
		File file = new File(filename);// 1-get filenames and created the file
		int n = 0;
		byte[] buf = new byte[4092];
		FileOutputStream fileOutputStream = new FileOutputStream(folderName
				+ File.separator + file.getName());
		long fileSize = dataInputStream.readLong();
		while (fileSize > 0
				&& (n = dataInputStream.read(buf, 0,
						(int) Math.min(buf.length, fileSize))) != -1) {
			fileOutputStream.write(buf, 0, n);
			fileSize -= n;
		}
		fileOutputStream.close();
	}

	public synchronized static void sendFile(File file, SSLSocket sslsocket) {
		try {
			DataInputStream dataInputStream = new DataInputStream(
					new BufferedInputStream(sslsocket.getInputStream()));
			DataOutputStream dataOutputStream = new DataOutputStream(
					new BufferedOutputStream(sslsocket.getOutputStream()));
			FileInputStream fileInputStream = null;
			dataOutputStream.writeUTF(file.toPath().getFileName().toString());// send
																				// the
																				// filename
			dataOutputStream.flush();
			int n = 0;
			byte[] buf = new byte[4092];
			dataOutputStream.writeLong(file.length());
			fileInputStream = new FileInputStream(file);
			while ((n = fileInputStream.read(buf)) != -1) {
				dataOutputStream.write(buf, 0, n);
				dataOutputStream.flush();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			System.err.println("job send fail");
		}
	}
	public static String checkStatus(SocketFactory factory, String serverAddress,
			int serverPort) throws  UnknownHostException,
			SocketException {		
		try {
			Socket sslsocket = factory.createSocket(serverAddress,serverPort);
			sslsocket.setSoTimeout(5000);
			DataInputStream in = new DataInputStream(sslsocket.getInputStream());
			DataOutputStream out = new DataOutputStream(sslsocket.getOutputStream());
			out.writeUTF("check");
			out.flush();
			String msg=in.readUTF();
			return(msg);
		} catch (IOException e) {
			return("DOWN");
		}
	}
}
