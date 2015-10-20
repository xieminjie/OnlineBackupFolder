
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import com.google.gson.Gson;

import filesync.SynchronisedFile;


public class Client {
	public static void main(String[] args) throws IOException, Exception{
		InetAddress addr = InetAddress.getByName("localhost"); 
		Path dir = Paths.get("/Users/xieminjie/Documents/javaProject/dropbox/client");
		watchService(dir,addr);
	}
	public static void watchService(Path dir,InetAddress addr) throws Exception {
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			System.out.println("Watch Service registered for dir: "+ dir.getFileName());
			Socket socket = new Socket(addr,8000);;
			while (true) {
				WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException ex) {
					return;
				}
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path fileName = ev.context();
					String toFilename = fileName.toString();
					Path filepath = Paths.get(dir.toString(),toFilename);
					if(toFilename.equals(".DS_Store")!=true){
						String fromFilePath = filepath.toString();
						String eventType = event.kind().toString();
						Thread SyncFileThread = new Thread(new SyncFileSendThread(socket,toFilename,fromFilePath,eventType));
						SyncFileThread.start();
						SyncFileThread.join();
					}
				}
				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

}
