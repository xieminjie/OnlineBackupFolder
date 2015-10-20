import java.awt.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MasterCLI {
	private static final Scanner scanner = new Scanner(System.in);
	public static int Sequence = 0;

	public static void main(String[] args) throws UnknownHostException, SocketException {
		ArrayList<workerNode> hostList = new ArrayList<workerNode>();
		hostList = workerNodeFileRead.readWorker(args[0]);
		System.out.println("Hello ! What would you like to do today?\n");
		while (true) {
			System.out.println("1. Submit a Job.");
			System.out.println("2. Add a new worker.");
			System.out.println("3. List workers and their statuses.");
			System.out.println("4. check job status.");
			System.out.println("5. Exit.");
			System.out.println("\nPlease enter your choice ");

			int option = scanner.nextInt();
			String clientkeystore = "key.cer";
			String password = "xieminjie";
			System.setProperty("javax.net.ssl.trustStore", clientkeystore);
			System.setProperty("javax.net.ssl.trustStorePassword", password);

			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
					.getDefault();
			SSLSocket sslsocket = null;
			switch (option) {
			case 1:
				scanner.nextLine();
				System.out.println("Please enter jobFile name");
				String jobFilename = scanner.nextLine();
				System.out.println("Please enter intputFile name");
				String inputFilename = scanner.nextLine();
				System.out.println("Please enter outputFile name");
				String outputFilename = scanner.nextLine();
				System.out.println("Please enter job deadline");
				int timeout = scanner.nextInt();
				String serverAddress;
				int serverPort;
				if (Sequence < hostList.size() - 1) {
					String checkStatus;
					try {
						checkStatus = MasterOne.checkStatus(sslsocketfactory,
								hostList.get(Sequence).getServerAddress(),
								hostList.get(Sequence).getPort());
					} catch (UnknownHostException e) {
						checkStatus = "DOWN";
						e.printStackTrace();
					} catch (SocketException e) {
						checkStatus = "DOWN";
						e.printStackTrace();
					}
					System.out.println(hostList.get(Sequence).getWorkername()
							+ ":" + checkStatus);
					hostList.get(Sequence).setStatus(checkStatus);
					if (checkStatus.equals("RUNNING")) {
						transferJob(hostList, jobFilename, inputFilename,
								outputFilename, timeout, sslsocketfactory,
								sslsocket);
					}
					Sequence++;
				} else {
					String checkStatus;
					try {
						checkStatus = MasterOne.checkStatus(sslsocketfactory,
						hostList.get(Sequence).getServerAddress(),
						hostList.get(Sequence).getPort());
					} catch (UnknownHostException e) {
						checkStatus = "DOWN";
						e.printStackTrace();
					} catch (SocketException e) {
						checkStatus = "DOWN";
						e.printStackTrace();
					}
					System.out.println(hostList.get(Sequence).getWorkername()
							+ ":" + checkStatus);
					hostList.get(Sequence).setStatus(checkStatus);
					if (hostList.get(Sequence).getStatus().equals("RUNNING")) {
						transferJob(hostList, jobFilename, inputFilename,
								outputFilename, timeout, sslsocketfactory,
								sslsocket);			
					} 
					Sequence = 0;
				}
				break;

			case 2:
				scanner.nextLine();
				System.out.print("\nEnter the name of the new worker: ");
				String workername = scanner.nextLine();
				System.out.print("\nEnter the IP Address of the new worker: ");
				String IPAddress = scanner.nextLine();
				System.out.print("\nEnter the port number: ");
				int port = scanner.nextInt();
				System.out.println("The Ip address you entered is " + IPAddress
						+ " and port is " + port);
				workerNodeFilewrite.addWorker(args[0], workername, IPAddress,
						port, "unknown");
				break;

			case 3:
				System.out.println("workername" + "+" + "serverAddress" + "+"
						+ "port" + "+" + "status");
				for(int i=0;i<hostList.size();i++){
					String check = MasterOne.checkStatus(sslsocketfactory,
							hostList.get(i).getServerAddress(),
							hostList.get(i).getPort());
					System.out.println(hostList.get(i).getWorkername()+" is "+check);
				}
				break;
			case 4:
				System.exit(0);

			default:
				System.out.println("Invalid choice.");
				break;

			}
			System.out.println("Press enter to continue...");
			scanner.nextLine();
			scanner.nextLine();

		}

	}

	private static void transferJob(ArrayList<workerNode> hostList,
			String jobFilename, String inputFilename, String outputFilename,
			int timeout, SSLSocketFactory sslsocketfactory, SSLSocket sslsocket) {
		String serverAddress;
		int serverPort;
		System.out.println("send job to "+ hostList.get(Sequence).getWorkername());
		serverAddress = hostList.get(Sequence).getServerAddress();
		// serverAddress = "localhost";
		serverPort = hostList.get(Sequence).getPort();
		try {
			sslsocket = (SSLSocket) sslsocketfactory.createSocket(
					serverAddress, serverPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread thread = new Thread(new MasterThread(jobFilename, inputFilename,
				timeout, outputFilename, sslsocket));
		thread.start();
	}

}
