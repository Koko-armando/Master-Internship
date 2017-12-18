package oracle;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class ThreadDockerServices implements Runnable{
	private static final Logger _log = Logger.getLogger("ThreadDockerServices");
	private Process process;
	private String PATH;
	private static final String START_SERVICES = "./startServices.sh";
	private static final String STOP_DATABASE = "./stopDB.sh";
	private static final String STOP_SERVICES = "./StopServices.sh";
	
	public ThreadDockerServices(String path){
		this.PATH = path;
	}
	
	public void stop(){
		this.process.destroy();
	}
	
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			ProcessBuilder processBuilder = new ProcessBuilder(START_SERVICES);
			processBuilder.directory(new File(PATH));
			process = processBuilder.start();
			process.waitFor();
		} catch(IOException e){
			_log.error("IOException: "+e.getMessage());
		} catch(InterruptedException e){
			_log.error("InterruptedException: "+e.getMessage());
		} finally{
			try{process.destroy();}
			catch(Exception e){_log.error("Destroy error: "+e.getMessage());}
		}
	}
	
}

