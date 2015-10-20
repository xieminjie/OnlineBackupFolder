
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import filesync.SynchronisedFile;

public class FileHandler {
	private String toFilename;
	FileHandler(String toFilename){
		this.toFilename = toFilename;
	}
	public String getToFilename() {
		return toFilename;
	}
	public void setToFilename(String toFilename) {
		this.toFilename = toFilename;
	}
	public static SynchronisedFile checkFile(String folderPath, String toFilename){
		Path path = Paths.get(folderPath,toFilename);
		File file = new File(path.toString());
		SynchronisedFile toFile = null;
		System.out.println("file location: "+file.toString());
		if(file.exists()!=true){
			createFiles(path);
		}
		try {
			Path filePath = Paths.get(folderPath, toFilename);
			toFile=new SynchronisedFile(filePath.toString());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return toFile;
	}
	public static synchronized boolean deleteFiles(Path path){
		boolean ifDone = false;
        if(path.toFile().delete()){
            System.out.println("the file is deleted");
            ifDone = true;
        }else {
        	System.out.println("the file doesn't exists");
        }
        return ifDone;
	}
	public static synchronized boolean createFiles(Path path){
		boolean ifDone = false;
		PrintWriter createFileStream = null;
		try{
			createFileStream = new PrintWriter(new FileOutputStream(path.toString()));
			createFileStream.println("");
			createFileStream.close();
			ifDone = true;
		}
		catch(FileNotFoundException e){
			System.exit(0);
		}
		return ifDone;
	}
}
