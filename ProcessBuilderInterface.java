import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;


public class ProcessBuilderInterface {
	String jobName;
	String inputFileName;
	String outputFileName;
	String dir;
	int jobTimeout;
	public ProcessBuilderInterface(String jobName,String inputFileName,String outputFileName,String dir,int jobTimeout){
		   this.jobName=jobName;
		   this.inputFileName=inputFileName;
		   this.outputFileName=outputFileName;
		   this.jobTimeout=jobTimeout;
	}
	public String createProcess() throws IOException, InterruptedException{
		ProcessBuilder processBuilder = new ProcessBuilder("java","-jar",jobName, inputFileName, outputFileName);
		processBuilder.directory(new File(dir));
		processBuilder.redirectErrorStream(true);
		File log = new File(dir+File.separator+"log.txt");
		processBuilder.redirectOutput(Redirect.appendTo(log));
		Process process = processBuilder.start();
		String result;
		process.waitFor(jobTimeout, TimeUnit.SECONDS);
		process.destroy();
		process.waitFor();
		int exitValue = process.exitValue();
		if(exitValue==0){
			result = "job finished";
		}else if(exitValue==143){
			result = "job timeout";
		}else{
			result = "job failure";
		}
		return result;
	}
}
