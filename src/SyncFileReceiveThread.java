import java.io.IOException;
import java.net.Socket;

import filesync.BlockUnavailableException;
import filesync.SynchronisedFile;


public class SyncFileReceiveThread implements Runnable{
	Socket socket;
	SynchronisedFile toFile;
	String toFilePath;
	SyncFileReceiveThread(Socket socket, String toFilePath){
		this.socket = socket;
		this.toFilePath = toFilePath;
	}
	@Override
	public void run() {
		try {
			try {
				toFile=new SynchronisedFile(toFilePath);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			SyncFileReceive.getFile(socket, toFile);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BlockUnavailableException e) {
			e.printStackTrace();
		}
	}
	
}
