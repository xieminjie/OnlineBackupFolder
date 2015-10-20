import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;

import filesync.BlockUnavailableException;
import filesync.Instruction;
import filesync.InstructionFactory;
import filesync.SynchronisedFile;


public class SyncFileReceive {
	private Socket socket;
	private SynchronisedFile toFile;
	public synchronized static void getFile(Socket socket, SynchronisedFile toFile) throws ClassNotFoundException, IOException, BlockUnavailableException{
		InstructionFactory instFact=new InstructionFactory();
		System.err.println("start receiving file");
		while(true){
			String msg = getMessage(socket);
			System.err.println("msg: "+msg);
			Instruction receivedInst = instFact.FromJSON(msg);
			try {		
				toFile.ProcessInstruction(receivedInst);
				sendMessage(socket,"nextBlock");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1); // just die at the first sign of trouble
			} catch (BlockUnavailableException e) {
				sendMessage(socket,"newBlock");
				String msg2 = getMessage(socket);
				System.err.println("msg2: "+msg2);
				Instruction receivedInst2 = instFact.FromJSON(msg2);
				toFile.ProcessInstruction(receivedInst2);
			}finally{
				if(receivedInst.Type().equals("EndUpdate")){
					System.out.println("server break");
					break;
				}
			}
		}
		System.out.println("receiving file done");
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
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public SynchronisedFile getToFile() {
		return toFile;
	}
	public void setToFile(SynchronisedFile toFile) {
		this.toFile = toFile;
	}
}
