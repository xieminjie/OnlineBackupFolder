import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;

import filesync.BlockUnavailableException;
import filesync.SynchronisedFile;

public class Server {
	public static void main(String[] args) throws IOException, ClassNotFoundException, BlockUnavailableException {
		SynchronisedFile toFile = null;
		try (ServerSocket serversocket = new ServerSocket(8000)) {
			Socket socket = serversocket.accept();
			while(true){
				String msg = getMessage(socket);
				Gson gson = new Gson();
				Message m = gson.fromJson(msg, Message.class);//1: receive event type and file name
				String toFilename = m.getToFilename();
				System.out.println("toFilename: "+toFilename);
				Path filepath = Paths.get("/Users/xieminjie/Documents/javaProject/dropbox/server",toFilename);
				String type = m.getActionType();
				if(type.equals("ENTRY_CREATE")){
					System.err.println("createFile");
					boolean ifDone = FileHandler.createFiles(filepath);
					String reply = null;
					if(ifDone){
						reply = "sendFile";
					}else{
						reply = "error";
					}
					sendMessage(socket,reply);//2:reply 
					try {
						toFile=new SynchronisedFile(filepath.toString());
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(-1);
					}
					SyncFileReceive.getFile(socket, toFile);
				}
				if(type.equals("ENTRY_MODIFY")){
					System.err.println("modifyFile");
					File file = new File(filepath.toString());
					if(file.exists()){
						sendMessage(socket,"sendFile");
						try {
							toFile=new SynchronisedFile(filepath.toString());
						} catch (IOException e) {
							e.printStackTrace();
							System.exit(-1);
						}
						SyncFileReceive.getFile(socket, toFile);
					}
				}
				if(type.equals("ENTRY_DELETE")){
					System.err.println("deleteFile");
					boolean ifDone = FileHandler.deleteFiles(filepath);
					String reply = null;
					if(ifDone){
						reply = "deleteFile";
					}else{
						reply = "File is not not exist";
					}
					sendMessage(socket,reply);//2:reply 
				}
				
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
}
