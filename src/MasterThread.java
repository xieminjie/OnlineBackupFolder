import java.io.IOException;

import javax.net.ssl.SSLSocket;


public class MasterThread implements Runnable {
	SSLSocket socket;
	String job;
	String input;
	String output;
	int timeOut;
	public MasterThread(String j,String i,int t,String o,SSLSocket s ){
		socket=s;
		job=j;
		input=i;
		output=o;
		timeOut=t;
	}
	public void run(){
		try{
			MasterOne.job(job, input, timeOut, output, socket);
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}