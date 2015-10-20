import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;

import filesync.CopyBlockInstruction;
import filesync.Instruction;
import filesync.NewBlockInstruction;
import filesync.SynchronisedFile;


public class SyncFileSend {
	private SynchronisedFile fromFile;
	public synchronized static boolean sendType(Socket socket,String fromFilename,String eventType) throws IOException, ClassNotFoundException{
		boolean ifSendFile = false;
		Message msg = new Message(eventType,fromFilename);
		Gson gson = new Gson();
		sendMessage(socket,gson.toJson(msg));// 1: send event type & file name 
		String msg2 = getMessage(socket);// 2: receive reply
		System.out.println("msg2+ "+msg2);
		if(msg2.equals("sendFile")){
			ifSendFile = true;
		}
		return ifSendFile;
	}
	public synchronized static void sendFile(Socket socket,SynchronisedFile fromFile) throws IOException, Exception{
		Instruction inst;
		System.err.println("start sending file");
		while((inst=fromFile.NextInstruction())!=null){
			String msg=inst.ToJSON();
			System.err.println("Sending: "+msg);
			sendMessage(socket,msg);
			String reply = getMessage(socket);
			System.out.println("reply: "+reply);
			if(reply.equals("newBlock")){
				Instruction upgraded=new NewBlockInstruction((CopyBlockInstruction)inst);
				String msg2 = upgraded.ToJSON();
				System.err.println("Sending: "+msg2);
				sendMessage(socket,msg2);
			}
			if(msg.contains("EndUpdate")){
				System.err.println("client break");
				break;
			}
		}
	}
	public static void sendMessage(Socket socket,String message) throws IOException{
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		out.writeUTF(message);
		out.flush();
	}
	public static String getMessage(Socket socket) throws IOException, ClassNotFoundException{
		DataInputStream in = new DataInputStream(socket.getInputStream());
		String msg = in.readUTF();
		return msg;
	}
	public SynchronisedFile getFromFile() {
		return fromFile;
	}
	public void setFromFile(SynchronisedFile fromFile) {
		this.fromFile = fromFile;
	}
}
