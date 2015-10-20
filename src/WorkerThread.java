import java.io.IOException;

import javax.net.ssl.SSLSocket;


public class WorkerThread implements Runnable {
	SSLSocket socket;
	
	public WorkerThread(SSLSocket s){
		socket=s;
	}
	
	public void run(){
		try{
			WorkerOne.job(socket);
		}catch(IOException|InterruptedException e){
			e.printStackTrace();
		}
	}

}
