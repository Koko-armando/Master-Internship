package oracle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import csv.CSVUtils;
import csv.SpreadsheetUtils;

/**
 * Extension of previous work from Mathieu ACHER, Inria Rennes-Bretagne Atlantique.
 * 
 * Oracle for all variants of configurator JHipster
 * 	- generate 
 * 	- build
 *  - tests
 * 
 * @author Nuttinck Alexandre
 * @author Axel Halin
 *
 */
public class Oracle {

	private static final Logger _log = Logger.getLogger("Oracle");
	private static final String JHIPSTERS_DIRECTORY = "jhipsters";
	private static final Integer weightFolder = new File(JHIPSTERS_DIRECTORY+"/").list().length;
	private static final String projectDirectory = System.getProperty("user.dir");
	private static final String JS_COVERAGE_PATH = "target/test-results/coverage/report-lcov/lcov-report/index.html";
	private static final String JS_COVERAGE_PATH_GRADLE = "build/test-results/coverage/report-lcov/lcov-report/index.html";
	private static final String DEFAULT_NOT_FOUND_VALUE ="ND";
	private static final String SUCCEED ="OK";
	private static final String FAIL="KO";
	private static final String PROPERTIES_FILE = "System.properties";
	private static final String START_SERVICES = "./startServices.sh";
	private static final String STOP_DATABASE = "./stopDB.sh";
	private static final String STOP_SERVICES = "./StopServices.sh";


	
	private static ResultChecker resultChecker = null;
	private static CSVUtils csvUtils = null;

	private static Thread threadRegistry;
	private static Thread threadUAA;
	private static Thread threadService;
	private static void startProcess(String fileName, String desiredDirectory){
		Process process = null;
		try{
			ProcessBuilder processBuilder = new ProcessBuilder(fileName);
			processBuilder.directory(new File(projectDirectory +"/"+ desiredDirectory));
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

	private static void runCommand(String command,String[] env, File path){
		try{
			Process p = Runtime.getRuntime().exec(command,env,path);
			p.waitFor();
			_log.info("Done running command");
		} catch (Exception e) { 
			_log.error("An error occured");
			_log.error(e.getMessage());}
	}
	
	/**
	 * Generate the App from the yo-rc.json.
	 * 
	 * @param jDirectory Name of the folder
	 * @param system boolean type of the system (linux then true, else false)
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private static void generateApp(String jDirectory) throws InterruptedException, IOException{
		startProcess("./generate.sh",JHIPSTERS_DIRECTORY+"/"+jDirectory+"/");
	}

	/**
	 * Compile the App from the yo-rc.json.
	 * 
	 * @param jDirectory Name of the folder
	 * @param system boolean type of the system (linux then true, else false)
	 */
	private static void compileApp(String jDirectory){
		
		startProcess("./compile.sh", JHIPSTERS_DIRECTORY+"/"+jDirectory);
	}

	/**
	 * Build the App which is generated successfully
	 * 
	 * @param jDirectory Name of the folder
	 * @param system boolean type of the system (linux then true, else false)
	 * @throws InterruptedException 
	 */
	private static void buildApp(String jDirectory) throws InterruptedException{
		startProcess("./build.sh", getjDirectory(jDirectory));
	}

	/**
	 * Launch UnitTests on the App is compiled successfully
	 * 
	 * @param jDirectory Name of the folder
	 * @throws InterruptedException 
	 */
	private static void unitTestsApp(String jDirectory) throws InterruptedException{
		startProcess("./unitTest.sh", JHIPSTERS_DIRECTORY+"/"+jDirectory);
	}
	
	private static void generateEntities(String jDirectory){
		_log.info("Starting entity JDL import");
		try{
			startProcess("./generateJDL.sh", JHIPSTERS_DIRECTORY+"/"+jDirectory);
			_log.info("Entities created !");
		} catch (Exception e){
			_log.error("Exception: "+e.getMessage());
		}
	}
	
	/**
	 * Return the path to folder jDirectory (which is in the relative path JHIPSTERS_DIRECTORY/)
	 * 
	 * @param jDirectory Name of the folder
	 * @return The relative path to folder with name jDirectory.
	 */
	private static String getjDirectory(String jDirectory) {
		return JHIPSTERS_DIRECTORY + "/" + jDirectory + "/";
	}

	/**
	 * Launch initialization scripts:\n
	 * 		- Start Uaa Server (in case of Uaa authentication)
	 * 		- Start Jhipster-Registry (in case of Microservices)
	 *  
	 */
	private static void initialization(boolean docker, String applicationType, String authentication,String jDirectory){
		_log.info("Starting intialization scripts...");
		if(!docker){
			
			
			
			if (applicationType.equals("\"gateway\"") || applicationType.equals("\"microservice\"") || applicationType.equals("\"uaa\"")){
				
				// Start Jhipster Registry
				threadRegistry = new Thread(new ThreadRegistry(projectDirectory+"/JHipster-Registry/"));
				threadRegistry.start();
				_log.info("Starting JHipster-Registry.....");
				try{
					Thread.sleep(50000);}
				catch(Exception e){_log.error(e.getMessage());}
				if(authentication.equals("\"uaa\"")){
					
					// Start UAA Server
					threadUAA = new Thread(new ThreadUAA(projectDirectory+"/"+JHIPSTERS_DIRECTORY+"/uaa/"));
					threadUAA.start();
					 _log.info("Starting Uaa Server .... ");
					try{Thread.sleep(5000);}
					catch(Exception e){_log.error(e.getMessage());}
				}
				
			}
		/*} else{
			// STOP DB FOR DOCKER
			startProcess("./stopDB.sh","");
		}*/
					}
		_log.info("Start services used by application......");
		
		threadService = new Thread(new ThreadDockerServices(getjDirectory(jDirectory)));
		threadService.start();
		_log.info("services Started succefully !");


	}
	
		
	/**
	 * Terminate the Oracle by ending JHipster Registry and UAA servers.
	 */
	private static void termination(String authentication){
		try{
		//	threadRegistry.interrupt();
			threadService.interrupt();
			if(authentication.equals("\"uaa\"")){
			threadUAA.interrupt();
			}
		} catch (Exception e){
			_log.error(e.getMessage());
		}
	}
	
	private static void publish(String jDirectory){
		startProcess("./publish.sh",getjDirectory(jDirectory));
	}

	private static void cleanUp(String jDirectory, boolean docker){
		if (docker) startProcess("./dockerStop.sh", getjDirectory(jDirectory));
		else startProcess("./killScript.sh", getjDirectory(jDirectory));
	}

	private static void dockerCompose(String jDirectory){
		// Run the App
		startProcess("./dockerStart.sh",getjDirectory(jDirectory));
	}
	
/*	private static void oracleCopyJAR(String jDirectory){
		// Copy files for oracle database
		startProcess("./oracleCopyJAR.sh",getjDirectory(jDirectory));
	}
	*/
	/**
	 * Check If the file exist
	 *  
	 * @param path of the file to check
	 * @return true if the file exist
	 */
	private static boolean checkIfFileNotExist(String file){
		File f = new File(file);
		if(!f.exists()) 
			{return true;}
		else {return false;}
	}	
	
	/**
	 * Retrieve all properties from specified property file.
	 * 
	 * @param propFileName Property file to retrieve
	 * @return All properties included in propFileName
	 */
	private static Properties getProperties(String propFileName) {
		InputStream inputStream = null;
		Properties prop = new Properties();

		try {
			// non static method
			// inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			
			// static method 
			inputStream = Oracle.class.getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) prop.load(inputStream);
			else throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try{inputStream.close();}
			catch (IOException e){e.printStackTrace();}
		}
		return prop; // default linux
	}

	
	private static boolean infrastructureBug(String logFile,String path){
		String text = Files.readFileIntoString(path+logFile);
		Matcher m = Pattern.compile("((.*?)Communications link failure)").matcher(text);
		while(m.find()) return true;
		return false;
	}
	
	
	/**
	 * Generate & Build & Tests all variants of JHipster 4.8.2. 
	 */
	public static void main(String[] args) throws Exception{
		
		Integer i = Integer.parseInt(args[0]);
        String CSV_adress=args[1];
		
		
		
		//GET ID OF SPREADSHEETS
		Properties property = getProperties(PROPERTIES_FILE);
		
			_log.info("Starting treatment of JHipster n° "+i);

			String jDirectory = "jhipster"+i;
			resultChecker = new ResultChecker(getjDirectory(jDirectory));

			//ID used for jhipster,coverage,cucumber .csv
			String Id = DEFAULT_NOT_FOUND_VALUE;
			// generate a new ID -> depend of the timestamp
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			//return number of milliseconds since January 1, 1970, 00:00:00 GMT
			Id = String.valueOf(timestamp.getTime());

			// Docker build file
			String dockerLogs = "buildDocker.log";
			
			//Strings used for the csv
			String generation = DEFAULT_NOT_FOUND_VALUE;
			String generationTime = DEFAULT_NOT_FOUND_VALUE;
			String stacktracesGen = DEFAULT_NOT_FOUND_VALUE;
			String compile = FAIL;
			String compileTime = DEFAULT_NOT_FOUND_VALUE;
			String stacktracesCompile = DEFAULT_NOT_FOUND_VALUE;
			StringBuilder build = new StringBuilder(FAIL);
			String stacktracesBuild = DEFAULT_NOT_FOUND_VALUE;
			String buildTime = DEFAULT_NOT_FOUND_VALUE;
			StringBuilder buildWithDocker = new StringBuilder(FAIL);
			String stacktracesBuildWithDocker = DEFAULT_NOT_FOUND_VALUE;
			String buildTimeWithDocker = DEFAULT_NOT_FOUND_VALUE;
			String buildTimeWithDockerPackage = DEFAULT_NOT_FOUND_VALUE;
			//jsonStrings
			String applicationType = DEFAULT_NOT_FOUND_VALUE;
			String authenticationType = DEFAULT_NOT_FOUND_VALUE;
			String hibernateCache = DEFAULT_NOT_FOUND_VALUE;
			String clusteredHttpSession = DEFAULT_NOT_FOUND_VALUE;
			String websocket = DEFAULT_NOT_FOUND_VALUE;
			String databaseType= DEFAULT_NOT_FOUND_VALUE;
			String devDatabaseType= DEFAULT_NOT_FOUND_VALUE;
			String prodDatabaseType= DEFAULT_NOT_FOUND_VALUE;
			String buildTool = DEFAULT_NOT_FOUND_VALUE;
			String searchEngine= DEFAULT_NOT_FOUND_VALUE;
			String enableSocialSignIn= DEFAULT_NOT_FOUND_VALUE;
			String useSass= DEFAULT_NOT_FOUND_VALUE;
			String enableTranslation = DEFAULT_NOT_FOUND_VALUE;
			String testFrameworks =DEFAULT_NOT_FOUND_VALUE;
            String messageBroker=DEFAULT_NOT_FOUND_VALUE;
            String serviceDiscoveryType=DEFAULT_NOT_FOUND_VALUE;
            String enableSwaggerCodegen=DEFAULT_NOT_FOUND_VALUE;
            String clientFramework=DEFAULT_NOT_FOUND_VALUE;
			//Tests part
			String resultsTest= DEFAULT_NOT_FOUND_VALUE;
			String cucumber= DEFAULT_NOT_FOUND_VALUE;
			String karmaJS= DEFAULT_NOT_FOUND_VALUE;
			String protractor = DEFAULT_NOT_FOUND_VALUE;
			String protractorDocker = DEFAULT_NOT_FOUND_VALUE;
			StringBuilder imageSize = new StringBuilder(DEFAULT_NOT_FOUND_VALUE);
			String coverageInstuctions= DEFAULT_NOT_FOUND_VALUE;
			String coverageBranches= DEFAULT_NOT_FOUND_VALUE;
			String coverageJSStatements = DEFAULT_NOT_FOUND_VALUE;
			String coverageJSBranches = DEFAULT_NOT_FOUND_VALUE;

			//Get Json strings used for the csv
			JsonParser jsonParser = new JsonParser();
			JsonObject objectGen = jsonParser.parse(Files.readFileIntoString(getjDirectory(jDirectory)+".yo-rc.json")).getAsJsonObject();
			JsonObject object = (JsonObject) objectGen.get("generator-jhipster");
			if (object.get("applicationType") != null) applicationType = object.get("applicationType").toString();
			if (object.get("serviceDiscoveryType") != null) serviceDiscoveryType = object.get("serviceDiscoveryType").toString();
			if (object.get("authenticationType") != null) authenticationType = object.get("authenticationType").toString();
			if (object.get("hibernateCache") != null) hibernateCache = object.get("hibernateCache").toString();
			if (object.get("clusteredHttpSession") != null) clusteredHttpSession = object.get("clusteredHttpSession").toString();
			if (object.get("websocket") != null) websocket = object.get("websocket").toString();
			if (object.get("messageBroker") != null) messageBroker = object.get("messageBroker").toString();
			if (object.get("databaseType") != null) databaseType = object.get("databaseType").toString();
			if (object.get("devDatabaseType") != null) devDatabaseType = object.get("devDatabaseType").toString();
			if (object.get("prodDatabaseType") != null) prodDatabaseType = object.get("prodDatabaseType").toString();
			if (object.get("buildTool") != null) buildTool = object.get("buildTool").toString();
			if (object.get("searchEngine") != null) searchEngine = object.get("searchEngine").toString();
			if (object.get("enableSocialSignIn") != null) enableSocialSignIn = object.get("enableSocialSignIn").toString();
			if (object.get("enableSwaggerCodegen") != null) enableSwaggerCodegen = object.get("enableSwaggerCodegen").toString();
			if (object.get("clientFramework") != null) clientFramework = object.get("clientFramework").toString();
			if (object.get("useSass") != null) useSass = object.get("useSass").toString();
			if (object.get("enableTranslation") != null) enableTranslation = object.get("enableTranslation").toString();
			if (object.get("testFrameworks") != null) testFrameworks = object.get("testFrameworks").toString();			

			_log.info("Check if this config isn't done yet...");

			String[] yorc = {applicationType,serviceDiscoveryType,authenticationType,hibernateCache,clusteredHttpSession,
					websocket,messageBroker,databaseType,devDatabaseType,prodDatabaseType,buildTool, searchEngine,enableSocialSignIn,enableSwaggerCodegen,clientFramework,useSass,enableTranslation,testFrameworks};
			
			if(!devDatabaseType.equals("\"oracle\"") && !prodDatabaseType.equals("\"oracle\""))
			{
				//check if the variant is present or not in the csv and return the number of lines
				boolean existconfgs = CSVUtils.CheckNotExistLineCSV(CSV_adress+"/jhipster.csv", yorc);
				// false : the yorc is already present
				if(existconfgs != false)
				{   
					
					
					
					_log.info("Copying node_modules...");
					startProcess("./init.sh", getjDirectory(jDirectory));
					_log.info("Generating the App..."); 
					long millis = System.currentTimeMillis();
					generateApp(jDirectory);
					long millisAfterGenerate = System.currentTimeMillis();
					_log.info("Generation done!");
	
				
         				if(resultChecker.checkGenerateApp("generate.log")){
						generation =SUCCEED;
						_log.info("After Checking : Generation is  "+generation);

						// Time to Generate
						Long generationTimeLong = millisAfterGenerate - millis;
						Double generationTimeDouble = generationTimeLong/1000.0;
						generationTime = generationTimeDouble.toString();
						//stacktracesGen = resultChecker.extractStacktraces("generate.log");
						_log.info("Generation complete ! Trying to compile the App...");
						
						compileApp(jDirectory);
	
						if(resultChecker.checkCompileApp("compile.log")){
							compile =SUCCEED;
							compileTime = resultChecker.extractTime("compile.log","compile");
							String[] partsCompile = compileTime.split(";");
							compileTime = partsCompile[0]; // delete the ";" used for Docker
											
			               //stacktracesCompile = resultChecker.extractStacktraces("compile.log");
	
						 	generateEntities(jDirectory);
							_log.info("Compilation success ! Launch Unit Tests...");
					//unitTestsApp(jDirectory);
	
					//resultsTest = resultChecker.extractResultsTest("test.log");
					//karmaJS = resultChecker.extractKarmaJS("testKarmaJS.log");
				    //cucumber= resultChecker.extractCucumber("test.log");
	
							CSVUtils csvutils = new CSVUtils(getjDirectory(jDirectory));

							// JACOCO Coverage results are only available with Maven
							if(buildTool.equals("\"maven\"")){
								_log.info("maven Coverage");
								

				//coverageInstuctions= resultChecker.extractCoverageIntstructions("index.html");
				//coverageBranches = resultChecker.extractCoverageBranches("index.html");
				//coverageJSBranches = resultChecker.extractJSCoverageBranches(JS_COVERAGE_PATH);
				//coverageJSStatements = resultChecker.extractJSCoverageStatements(JS_COVERAGE_PATH);
							} else{
								_log.info("gradle Coverage");
					//coverageJSBranches = resultChecker.extractJSCoverageBranches(JS_COVERAGE_PATH_GRADLE);
					//coverageJSStatements = resultChecker.extractJSCoverageStatements(JS_COVERAGE_PATH_GRADLE);
							}
	
					//csvutils.writeLinesCoverageCSV("jacoco.csv", CSV_adress+"/coverage.csv", jDirectory, Id);
	
							_log.info("Compilation success ! Trying to build the App...");
	
							// Building without Docker
							initialization(false, applicationType, authenticationType,jDirectory);
							ThreadCheckBuild t2 = new ThreadCheckBuild(getjDirectory(jDirectory), false, "build.log",imageSize,build, prodDatabaseType);
							t2.start();
							_log.info("Trying to build the App without Docker...");
							//build WITHOUT docker
							buildApp(jDirectory);
							Thread.sleep(3000);
							t2.done();
						
                            cleanUp(jDirectory,false);
	
							if(build.toString().equals(FAIL))  stacktracesBuild = resultChecker.extractStacktraces("build.log");
							else {
								
								
					          // protractor = resultChecker.extractProtractor("testProtractor.log");

						//		String[] cucumberResults = (String[])ArrayUtils.addAll(new String[]{Id,jDirectory}, new CucumberResultExtractor(getjDirectory(jDirectory),buildTool.replace("\"","")).extractEntityCucumberTest());
						//		CSVUtils.writeNewLineCSV(CSV_adress+"/cucumber.csv",cucumberResults);
								
								//String[] oracleResults = (String[])ArrayUtils.addAll(new String[]{Id,jDirectory,"false"}, new GatlingResultExtractor(getjDirectory(jDirectory),buildTool.replace("\"","")).extractResultsGatlingTest());
								//CSVUtils.writeNewLineCSV(CSV_adress+"/gatling.csv", oracleResults);
								buildTime = resultChecker.extractTime("build.log","build");	
								String[] partsBuildWithoutDocker = buildTime.split(";");
								buildTime = partsBuildWithoutDocker[0]; // only two parts with Docker
							}
							_log.info("Try to stop Doker Services....");	
							startProcess(STOP_SERVICES,getjDirectory(jDirectory));
							_log.info("Doker Services are stopped !");	
							
							
							
							
							
							
						//	_log.info("Trying to build the App with Docker...");
										
					/* Code Docker		
							
							initialization(true, applicationType, authenticationType);
							imageSize = new StringBuilder();
							ThreadCheckBuild t1 = new ThreadCheckBuild(getjDirectory(jDirectory), true, "buildDocker.log",imageSize, buildWithDocker, prodDatabaseType);
							t1.start();
							//build WITH docker
							dockerCompose(jDirectory);
							Thread.sleep(5000);
							t1.done();
							_log.info("Done");
							//TODO set up distinct log file for 2nd try
							// If infrastructure bug, retry 
							if(buildWithDocker.toString().equals(FAIL)){
								_log.info("Build failed... checking the bug");
								if(infrastructureBug("buildDocker.log",getjDirectory(jDirectory))){
									_log.info("Infrastructure bug!!");
									ThreadCheckBuild t3 = new ThreadCheckBuild(getjDirectory(jDirectory), true, "buildDocker2.log",imageSize, buildWithDocker, prodDatabaseType);
									t3.start();
									_log.info("Thread started");
									// launch Docker compose
									//runCommand("docker-compose -f src/main/docker/app.yml up >> buildDocker2.log 2>&1",null,new File(getjDirectory(jDirectory)));
									startProcess("./dockerRetry.sh",getjDirectory(jDirectory));
									Thread.sleep(3000);
									t3.done();
									dockerLogs = "dockerBuild2.log";
									_log.info("Done");
								}
								_log.info("Not infrastructure related");
								startProcess("./dockerStop.sh",getjDirectory(jDirectory));
							}					
	
							if(imageSize.toString().equals("")){
								imageSize.delete(0, 5);
								imageSize.append(DEFAULT_NOT_FOUND_VALUE);
							}
	
							if(buildWithDocker.toString().equals(FAIL)) stacktracesBuildWithDocker = resultChecker.extractStacktraces(dockerLogs);
							else {
								String buildTimeWithDockerVar = resultChecker.extractTime("buildDocker.log");
								String[] partsBuildWithDocker = buildTimeWithDockerVar.split(";");
								buildTimeWithDockerPackage = partsBuildWithDocker[0]; 
								if(partsBuildWithDocker.length>1) buildTimeWithDocker = partsBuildWithDocker[1]; 
								protractorDocker = resultChecker.extractProtractor("testDockerProtractor.log");
								String[] cucumberResults = (String[])ArrayUtils.addAll(new String[]{Id,jDirectory}, new CucumberResultExtractor(getjDirectory(jDirectory),buildTool.replace("\"","")).extractEntityCucumberTest());
								//SpreadsheetUtils.AddLineSpreadSheet(idSpreadsheet_cucumberDocker, cucumberResults, i);
								CSVUtils.writeNewLineCSV(CSV_adress+"/cucumber.csv", new CucumberResultExtractor(getjDirectory(jDirectory),buildTool.replace("\"","")).extractEntityCucumberTest());
								// à banir cas oracle
								String[] oracleResults = (String[])ArrayUtils.addAll(new String[]{Id,jDirectory,"true"}, new GatlingResultExtractor(getjDirectory(jDirectory),buildTool.replace("\"","")).extractResultsGatlingTest());
								SpreadsheetUtils.AddLineSpreadSheet(idSpreadsheet_oracle, oracleResults, i*2-1);
							}
							
							_log.info("Cleaning up... Docker");
							cleanUp(jDirectory,true);		
							
							*/
							
									
							
							
						} else{
							_log.error("App Compilation Failed ...");
							compile = FAIL;
							compileTime = FAIL;
							stacktracesBuild = "COMPILATION ERROR";
							stacktracesCompile = resultChecker.extractStacktraces("compile.log");
							_log.info(""+stacktracesCompile);

							
						}
					} else{
						_log.error("App Generation Failed...");
						generation =FAIL;
						stacktracesBuild = "GENERATION ERROR";
						stacktracesGen = resultChecker.extractStacktraces("generate.log");
						
					}
	
					publish(jDirectory);
					_log.info("Writing into jhipster.csv");

					//WITH DOCKER
					String docker = "true";
	/*
					if (stacktracesCompile.length() > 48000) stacktracesCompile = stacktracesCompile.substring(0, 48000);
					if (stacktracesBuildWithDocker.length() > 48000) stacktracesBuildWithDocker = stacktracesBuildWithDocker.substring(0, 48000);
					if (stacktracesBuild.length() > 48000) stacktracesBuild = stacktracesBuild.substring(0, 48000);
					
					//New line for file csv With Docker
					String[] line = {Id,jDirectory,docker,applicationType,serviceDiscoveryType,authenticationType,hibernateCache,clusteredHttpSession,
							websocket,messageBroker,databaseType,devDatabaseType,prodDatabaseType,buildTool,searchEngine,enableSocialSignIn,enableSwaggerCodegen,clientFramework,useSass,enableTranslation,testFrameworks,
							generation,stacktracesGen,generationTime,compile,stacktracesCompile,compileTime,buildWithDocker.toString(),
							stacktracesBuildWithDocker,buildTimeWithDockerPackage,buildTimeWithDocker,imageSize.toString(),
							resultsTest,cucumber,karmaJS,protractorDocker,coverageInstuctions,coverageBranches,
							coverageJSStatements, coverageJSBranches};
	
					//write into CSV file
					CSVUtils.writeNewLineCSV(CSV_adress+"/jhipster.csv",line);
					
	*/
					
					
					//WITHOUT DOCKER
					docker = "false";
				
					//New line for file csv without Docker
					String[] line2 = {Id,jDirectory,docker,applicationType,serviceDiscoveryType,authenticationType,hibernateCache,clusteredHttpSession,
							websocket,messageBroker,databaseType,devDatabaseType,prodDatabaseType,buildTool,searchEngine,enableSocialSignIn,enableSwaggerCodegen,clientFramework,useSass,enableTranslation,testFrameworks,
							generation,stacktracesGen,generationTime,compile,stacktracesCompile,compileTime,build.toString(),stacktracesBuild,"NOTDOCKER",
							buildTime,"NOTDOCKER",resultsTest,cucumber,karmaJS,protractor,
							coverageInstuctions,coverageBranches, coverageJSStatements, coverageJSBranches};
						
					//write into CSV file
					CSVUtils.writeNewLineCSV(CSV_adress+"/jhipster.csv",line2);
					
						
				}
				
				
				else {
					_log.info("This configuration has been already tested");
					try{
						
					} catch (Exception e){
						_log.error("Exception: "+e.getMessage());
					}
				}
			}
		//}
		_log.info("Termination...");
		termination(authenticationType);
	}

	/**
	 * Create CSV BUGS 
	 */
	@Test
	public void writeCSVBugs() throws Exception{
		//boolean false = not check doublon , true yes
		//CSVUtils.createBugsCSV("jhipster.csv", "bugs.csv",true);
	}
	
	@Test
	public void testCSVmethod() throws Exception{
		CSVUtils.createCSVFileJHipster("2wise.csv");
		CSVUtils.createNwiseCSV("jhipster.csv","FM-3.6.1-refined.m.ca2.csv","2wise.csv");
	}
}
