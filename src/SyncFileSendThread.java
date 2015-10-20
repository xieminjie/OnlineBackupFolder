import java.io.IOException;
import java.net.Socket;

import filesync.SynchronisedFile;


public class SyncFileSendThread implements Runnable{
	Socket socket;
	SynchronisedFile fromFile; // this would be on the Client
	String toFilename;
	String fromFilePath;
	String eventType;
	SyncFileSendThread(Socket socket,String toFilename,String fromFilePath,String eventType){
		this.toFilename = toFilename;
		this.socket = socket;
		this.fromFilePath = fromFilePath;
		this.eventType = eventType;
	}
	@Override
	public void run() {
		try {
			boolean ifSendFile = SyncFileSend.sendType(socket, toFilename, eventType);
			if(ifSendFile){
				try {
					fromFile=new SynchronisedFile(fromFilePath);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
				}
				fromFile.CheckFileState();
				SyncFileSend.sendFile(socket, fromFile);
			}
			Thread.sleep(1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
