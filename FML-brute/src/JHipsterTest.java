import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Files;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.prop4j.Node;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import csv.CSVUtils;
import fr.familiar.FMLTest; 
import fr.familiar.operations.featureide.SATFMLFormula;
import fr.familiar.variable.FeatureModelVariable;
import fr.familiar.variable.FeatureVariable;
import fr.familiar.variable.SetVariable;
import fr.familiar.variable.Variable;
import oracle.CucumberResultExtractor;
import selenium.SeleniumTest;

/**
 * Extension of previous work from Mathieu ACHER, Inria Rennes-Bretagne Atlantique.
 * 
 * Generator for all variants of configurator JHipster
 * 
 * @author Mathieu ACHER
 * @author Axel HALIN
 *
 */
public class JHipsterTest extends FMLTest{

	private final static Logger _log = Logger.getLogger("JHipsterTest");
	private static final String JHIPSTERS_DIRECTORY = "jhipsters";
	private static final String DIMACS_FILENAME = "models/model.dimacs";
	private static final String PROJECTDIRECTORY = System.getProperty("user.dir");
	private static final ScriptsBuilder SCRIPT_BUILDER = new ScriptsBuilder();
	
	/**
	 * Modeling of JHipster's Feature Model to FeatureIDE notation.
	 * 
	 * @return The Feature Model of JHipster in a FeatureModelVariable.
	 * @throws Exception If the syntax of the modeling is wrong.
	 */
	private FeatureModelVariable getFMJHipster() throws Exception {
		/*return FM ("jhipster", "FM ( jhipster :  Base ; Base : "
				+ "[InternationalizationSupport] [Database] [Authentication] Generator [Libsass] [SpringWebSockets] [SocialLogin] [ClusteredSession] TestingFrameworks [BackEnd] ; "
				+ "Authentication : (HTTPSession | Uaa | OAuth2 | JWT) ; "
				//TODO
//				+ "TestingFrameworks : [Gatling] [Protractor] [Cucumber] ; "
				+ "TestingFrameworks : Gatling [Protractor] Cucumber ; "
//				+ "Generator : (Server | Application | Client) ; "
				+ "Generator : (Server | Application) ; "
//				+ "Server : (MicroserviceApplication | UaaServer | ServerApp) ; "
				+ "Server : (MicroserviceApplication | UaaServer) ; "
				+ "Application : (MicroserviceGateway | Monolithic) ; "
				+ "Database : (SQL | Cassandra | MongoDB) ; "
				+ "SQL: Development [Hibernate2ndLvlCache] Production [ElasticSearch] ; "
				+ "Development : (Oracle12c | H2 | PostgreSQLDev | MariaDBDev | MySql) ; "
//				+ "Development : (PostgreSQLDev | MariaDBDev | MySql) ; "
				+ "H2 : (DiskBased | InMemory) ; "
				+ "Hibernate2ndLvlCache : (HazelCast | EhCache) ; " 
				+ "Production : (MySQL | Oracle | MariaDB | PostgreSQL) ; "
//				+ "Production : (MySQL | MariaDB | PostgreSQL) ; "
				+ "BackEnd : (Gradle | Maven) ; "
				// Constraints
				+ "(OAuth2 & !SocialLogin & !MicroserviceApplication) -> (SQL | MongoDB) ; "
//				+ "SocialLogin -> ((HTTPSession | JWT) & (ServerApp | Monolithic) & (SQL | MongoDB)) ; "
				+ "SocialLogin -> ((HTTPSession | JWT) & (Monolithic) & (SQL | MongoDB)) ; "
				+ "UaaServer -> Uaa ; "
				+ "Oracle -> (H2 | Oracle12c) ; "
				+ "(!OAuth2 & !SocialLogin & !MicroserviceApplication) -> (SQL | MongoDB | Cassandra) ; "
				+ "Server -> !Protractor ; "
				+ "!Server -> Protractor ; "
				+ "MySQL -> (H2 | MySql) ; "
//				+ "MySQL -> (MySql) ; "
				+ "(MicroserviceApplication | MicroserviceGateway) -> (JWT | Uaa) ; "
				+ "Monolithic -> (JWT | HTTPSession | OAuth2) ; "
				+ "MariaDB -> (H2 | MariaDBDev) ; "
//				+ "MariaDB -> (MariaDBDev) ; "
				+ "PostgreSQL -> (H2 | PostgreSQLDev) ; "
//				+ "PostgreSQL -> (PostgreSQLDev) ; "
				+ "(Server | Application) -> (BackEnd & Authentication) ; "
				+ "(SpringWebSockets | ClusteredSession) -> Application ; "
//				+ "Client -> (!Gatling & !Cucumber & !BackEnd & !Authentication) ; "
//				+ "Libsass -> (Application | Client) ; "
				+ "Libsass -> (Application) ; "
				+ " )");
		
		*/
		
		
		
		return FM ("jhipster", "FM ( Generator : [Server] [Client]  Application   ;" + 
		"Application : [ModuleTobeInstall]  ApplicationType  TestFramework  ;" + 
		"TestFramework : [protractor][cucumber][gatling] ;"+
		"ApplicationType : (monolith|microservice|gateway|uaaApp) ;" + 
		"Client : ClientFrameWork  [LibSass] ModuleNameClient  ;" + 
		"ClientFrameWork : (AngularX|Angular1) ;" + 
		"Server : PackageName  ServerPort  [OptionalItem]  Serveri18n  [ServerSideOption]  uaaBaseName  ;" + 
		"ServerSideOption : AuthenticationType  [HibernateCache]  [DataBaseType]  [ProDataBaseType]  [DevDataBaseType]  Backend  [ServiceDiscoveryType]  ;" + 
		"ServiceDiscoveryType : (consul|eureka|false) ;"+
		"BuildTool : (maven|gradle) ;" + 
		"DevDataBaseType : (nodevdbtype|mysqldev|h2disk|cassandradev|postgresqldev|oracledev|mariadbdev|h2memory|mssqldev|mongodbdev) ;" + 
		"ProDataBaseType : (mssqlprod|noproddbtype|mongodbprod|mariadbprod|mysqlprod|postgresqlprod|cassandraprod|oracleprod) ;" + 
		"DataBaseType : (sql|cassandra|nodbtype|mongodb) ;"+
		"HibernateCache : (ehcache|infinispan|hazelcast|nohbcache) ;" + 
		"AuthenticationType : (session|oauth2|jwt|uaaserver) ;" + 
		"OptionalItem : (enablesocialsignin|elasticsearch|springwebsocket|hazelcastcluster) kafka enableswagegercodegen  ;" +  
		"! DataBaseType -> (sql or cassandra or mongodb);" + 
		"sql -> ProdDataBaseType;" + 
		"(sql and (! gateway)) -> HibernateCache;" + 
		"(microservice | uaaserver | gateway) -> DataBaseType;" + 
		"(monolith -> (!ServiceDiscoveryType | eureka)) ; ((((monolith & !eureka) -> jwt) | session) | oauth2) ; (Server -> uaaApp) ; " + 
		"(mongodb -> ((mongodbdev & mongodbprod) & nohbcache)) ; " + 
		"((oauth2 & ! DataBaseType) -> (sql | mongodb)) ;" + 
		"((sql & postgresqlprod) -> ((h2disk | h2memory) | postgresqldev)) ;" + 
		"(((gateway | microservice) -> jwt) | uaaserver) ; " + 
		"(((!cassandra & monolith) & (session | jwt)) -> enablesocialsignin) ;" + 
		"((sql & mysqlprod) -> ((h2disk | h2memory) | mysqldev)) ;" + 
		"(((microservice | gateway) | uaaApp) -> OptionalItem) ; (sql -> elasticsearch) ;" + 
		"((monolith | gateway) -> springwebsocket) ; (cassandra -> ((cassandraprod & cassandradev) & nohbcache)) ;" + 
		"(sql -> ((((mysqlprod | mariadbprod) | postgresqlprod) | oracleprod) | mssqlprod)) ;" + 
		"((sql & mariadbprod) -> ((h2disk | h2memory) | mariadbdev)) ; " + 
		"(((monolith | gateway) & (nohbcache | hazelcast)) -> hazelcastcluster) ; " + 
		"((Server) -> cucumber | gatling) ;" + 
		"Client-> protractor;" + 
		"((sql & mssqlprod) -> ((h2disk | h2memory) | mssqldev)) ; ((eureka & !uaaApp) -> jwt) ;" + 
		"((sql & oracleprod) -> ((h2disk | h2memory) | oracledev)) ;" + 
		"(Client) -> (protractor) ; " + 
		"(gateway -> hazelcast) ; (nodbtype -> ((sql | mongodb) | cassandra)) ; " + 
		"((gateway | microservice) & uaaserver) -> uaaBaseName;)" + 
		")");
		
		
		
		
		
		
	}
	
	
	/**
	 * Instantiate a JhipsterConfiguration object which attributes values are present in the Set of Strings.
	 * i.e a new JhipsterConfiguration based on a specific configuration of JHipster.
	 * 
	 * In this version, we do not handle jhipster:client nor jhipster:server.
	 * 
	 * @param strConfs Set of all features representing a specific configuration of JHipster
	 * @param fmvJhipster Feature Model of JHipster.
	 * @return JhipsterConfiguration object representing a specific configuration (strConfs)
	 */
	private JhipsterConfiguration toJhipsterConfiguration(Set<String> strConfs, FeatureModelVariable fmvJhipster) {
		// Foo values
		final String BASENAME = "jhipster";
		final String PACKAGENAME = "io.variability.jhipster";
		final String SERVERPORT = "8080";
		final String JHIPSTERVERSION = "4.8.2";
		//final String JHIPREFIX = "jhi";
		//final String SESSIONKEY = "13e6029734fb9984533b9bb5c511bca6d624c6ed";
		//final String JWTKEY = "d8837e4a671d25456432b55b1e4a99fe0356ed07";
		final String UAABASENAME = "uaa";
		
		JhipsterConfiguration jhipsterConf = new JhipsterConfiguration();
		
		// Find application type
		/*switch (get("Generator", strConfs, fmvJhipster)){
			case "Server": 	jhipsterConf.applicationType = get("Server", strConfs, fmvJhipster);
							break;
			case "Application": jhipsterConf.applicationType = get("Application", strConfs, fmvJhipster);
								break;
			case "Client": 	jhipsterConf.applicationType="clientApp";
							break;
		}*/
		jhipsterConf.applicationType = get("ApplicationType", strConfs, fmvJhipster);
		
			// Common attributes
			jhipsterConf.jhipsterVersion = JHIPSTERVERSION;
			jhipsterConf.baseName = BASENAME;
			jhipsterConf.packageName = PACKAGENAME;
			jhipsterConf.packageFolder = PACKAGENAME.replace(".", "/");
			jhipsterConf.serverPort = SERVERPORT;
			
				
			
			if(jhipsterConf.applicationType.equals("monolith")){
				jhipsterConf.servicediscoveryType=get("ServiceDiscoveryType", strConfs, fmvJhipster);
				jhipsterConf.authenticationType = get("AuthenticationType", strConfs, fmvJhipster);
				jhipsterConf.databaseType = get("DataBaseType", strConfs, fmvJhipster);
				jhipsterConf.devDatabaseType = get("DevDataBaseType", strConfs, fmvJhipster);
				jhipsterConf.hibernateCache = get("HibernateCache", strConfs, fmvJhipster);
				jhipsterConf.buildTool = get("BuildTool", strConfs, fmvJhipster);
				jhipsterConf.optionalItems = gets("OptionalItems", strConfs, fmvJhipster);
				jhipsterConf.testFrameworks = gets("TestFramework", strConfs, fmvJhipster);
			
			}
			
			
			
			
		/*	
			// Common to ServerApp and Monolith application
			if(jhipsterConf.applicationType.equals("serverApp") || jhipsterConf.applicationType.equals("monolith") || jhipsterConf.applicationType.equals("gateway")){
				if(isIncluded("ClusteredSession", strConfs).equals("true")) jhipsterConf.clusteredHttpSession = "hazelcast";
				else jhipsterConf.clusteredHttpSession = "no";
				
				if(isIncluded("SpringWebSockets", strConfs).equals("true")) jhipsterConf.websocket = "spring-websocket";
				else jhipsterConf.websocket = "no";
				jhipsterConf.enableSocialSignIn = Boolean.parseBoolean(isIncluded("SocialLogin", strConfs));
			}
			// Common attributes
			jhipsterConf.databaseType = falseByNo(get("Database", strConfs, fmvJhipster));
			if(jhipsterConf.databaseType.equals("mongodb") || jhipsterConf.databaseType.equals("cassandra")){
				jhipsterConf.devDatabaseType = jhipsterConf.databaseType;
				jhipsterConf.prodDatabaseType = jhipsterConf.databaseType;
			} else{
				jhipsterConf.devDatabaseType = falseByNo(get("Development", strConfs, fmvJhipster));
				if (jhipsterConf.devDatabaseType.equals("H2")){jhipsterConf.devDatabaseType=get("H2", strConfs, fmvJhipster);}
				jhipsterConf.prodDatabaseType = falseByNo(get("Production", strConfs, fmvJhipster));
				
			}			
			
			if (Boolean.parseBoolean(isIncluded("ElasticSearch", strConfs))) jhipsterConf.searchEngine = "elasticsearch";
			else jhipsterConf.searchEngine = "no";
					
			jhipsterConf.buildTool = get("BackEnd", strConfs, fmvJhipster);
			// Authentication Key
			if (jhipsterConf.authenticationType.equals("jwt")){jhipsterConf.jwtSecretKey = JWTKEY;}
			else if (jhipsterConf.authenticationType.equals("session")){jhipsterConf.rememberMeKey = SESSIONKEY;}
			else if (jhipsterConf.authenticationType.equals("uaa")){jhipsterConf.uaaBaseName = UAABASENAME;}
			// Not for ServerApp
			if (!jhipsterConf.applicationType.equals("serverApp")){
				jhipsterConf.jhiPrefix = JHIPREFIX;
				jhipsterConf.testFrameworks = gets("TestingFrameworks", strConfs, fmvJhipster);
			}
		}
		
		
		if (jhipsterConf.applicationType.equals("microservice") || jhipsterConf.applicationType.equals("uaa")) 
			jhipsterConf.skipClient = true;
		
		if (jhipsterConf.applicationType.equals("microservice") || 
				(jhipsterConf.applicationType.equals("gateway") && jhipsterConf.authenticationType.equals("uaa"))){
			jhipsterConf.skipUserManagement = true;
		}
		
		if (jhipsterConf.applicationType.equals("monolith") || jhipsterConf.applicationType.equals("clientApp") || jhipsterConf.applicationType.equals("gateway")) 
			jhipsterConf.useSass = Boolean.parseBoolean(isIncluded("Libsass", strConfs));
		
		
		//TODO If internationalization support (we limit ourselves to English for now)
		//jhipsterConf.enableTranslation = false;
		
		if(isIncluded("InternationalizationSupport", strConfs).equals("true")){
			//jhipsterConf.websocket = "spring-websocket";
			jhipsterConf.enableTranslation = true;
			jhipsterConf.nativeLanguage = "en";
			jhipsterConf.languages = new String[]{"en","fr"};
		}
		else {
			jhipsterConf.enableTranslation = false;
			
		}
		
		
		return jhipsterConf;
	}	
	
	/**
	 * Parsing of JHipster's configuration in JSON.
	 * This parsing is to match the expected values of yo-rc.json file. These values vary depending on the type of application.
	 * 
	 * @param jhipsterConf Configuration of JHipster (@see GeneratorJhipsterConfiguration)
	 * @return
	 */
	private String toJSON2(GeneratorJhipsterConfiguration jhipsterConf) {
		Gson gson;
		// If ClientApp or ServerApp, applicationType should not be displayed.
		if(jhipsterConf.generatorJhipster.applicationType.endsWith("App")){gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).setPrettyPrinting().create();}
		else {gson = new GsonBuilder().setPrettyPrinting().create();}
		
		//TODO JWT is wrong because no template with JWT
		/*if (!Utils.testJson(gson.toJsonTree(jhipsterConf), new JsonChecker())){
			System.err.println("JSON parsed is wrong !!!");
		}*/
		
		return gson.toJson(jhipsterConf);	
	}

	/**
	 * Transform "false" String in "no"
	 * 
	 * @param s String to transform
	 * @return "no" if s = "false"; s otherwise.
	 */
	private String falseByNo(String s) {
		if (s.equals("false")) return "no";
		return s;
	}
	
	/**
	 * @param ft
	 * @param strConfs
	 * @param fmvJhipster
	 * @return is "ft" in the configuration?
	 */
	private String isIncluded(String ft, Set<String> strConfs) {
		if (strConfs.contains(ft))
			return "true";
		return "false"; 
		
	}

	/**
	 * @param ft
	 * @param strConfs
	 * @param fmvJhipster
	 * @return value of "ft" in the configuration... otherwise false
	 */
	private String get(String ft, Set<String> strConfs, FeatureModelVariable fmvJhipster) {
		SetVariable chs = fmvJhipster.getFeature(ft).children();
		 for (Variable ch : chs.getVars()) {
			FeatureVariable ftv = (FeatureVariable) ch;
			String ftName = ftv.getFtName();
			if (strConfs.contains(ftName))
				return toRealFtName2(ftName);
		}
		return "false"; // "no" sometimes
	}
	
	/**
	 * @param ft
	 * @param strConfs
	 * @param fmvJhipster
	 * @return values of "ft" in the configuration... (typically OR-groups)
	 */
	private String[] gets(String ft, Set<String> strConfs,
			FeatureModelVariable fmvJhipster) {
		
		ArrayList<String> strs = new ArrayList<String>();
		 SetVariable chs = fmvJhipster.getFeature(ft).children();
		 for (Variable ch : chs.getVars()) {
			FeatureVariable ftv = (FeatureVariable) ch; 
			String ftName = ftv.getFtName();
			if (strConfs.contains(ftName))
				strs.add(toRealFtName2(ftName));
		}
		String[] r = new String[strs.size()];
		return (String[]) strs.toArray(r); 
		 
	}

	/**
	 * Change feature name as presented in feature model to match expected value in yo-rc.json
	 * 
	 * @param feature Name of the feature (as written in the feature model)
	 * @return The name of the feature as expected by JHipster
	 */
	private static String toRealFtName2(String feature){
		switch (feature){
			case "MicroserviceApplication": return "microservice";
			case "MicroserviceGateway":		return "gateway";
			case "Monolithic":				return "monolith";
			case "UaaServer":				return "uaa";
			case "Maven":					return "maven";
			case "Gradle":					return "gradle";
			case "SQL":						return "sql";
			case "Cassandra":				return "cassandra";
			case "MongoDB":					return "mongodb";
			case "Oracle12c":				return "oracle";
			case "PostgreSQLDev":			return "postgresql";
			case "MariaDB":					return "mariadb";
			case "MySql":					return "mysql";
			case "Oracle":					return "oracle";
			case "MariaDBDev":				return "mariadb";
			case "H2DiskBased":				return "h2Disk";
			case "H2InMemory":				return "h2Memory";
			case "ElasticSearch":			return "elasticsearch";
			case "HazelCast":				return "hazelcast";
			case "EhCache":					return "ehcache";
			case "MySQL":					return "mysql";
			case "PostgreSQL":				return "postgresql";
			case "HTTPSession":				return "session";
			case "Uaa":						return "uaa";
			case "OAuth2":					return "oauth2";
			case "JWT":						return "jwt";
			case "Gatling":					return "gatling";
			case "Protractor":				return "protractor";
			case "Cucumber":				return "cucumber";
			case "ServerApp":				return "serverApp";
			default: 	return feature;
		}
	}
	
	/**
	 * Create a directory in the folder {@link #JIHPSTERS_DIRECTORY}.
	 * 
	 * @param jDirectory Name of the folder to create.
	 */
	private void mkdirJhipster(String jDirectory) {
		File dir = new File(getjDirectory(jDirectory));
    	if (!dir.exists()) {
    		_log.info("Creating folder "+jDirectory+" ...");
		    try {dir.mkdir();} 
		    catch(SecurityException se){_log.error(se.getMessage());}        
		}
    	else{_log.info("Folder "+jDirectory+" already exists, no need to create it...");}
	}

	
	/**
	 * Return the path to folder jDirectory (which is in the relative path JHIPSTERS_DIRECTORY/)
	 * 
	 * @param jDirectory Name of the folder
	 * @return The relative path to folder with name jDirectory.
	 */
	private String getjDirectory(String jDirectory) {
		return JHIPSTERS_DIRECTORY + "/" + jDirectory + "/";
	}
	
	
	/**
	 * Extract the features of a specific configuration and return them in an Set of String.
	 * 
	 * @param configuration JHipster's configuration to work on.
	 * @return All features of configuration in a Set of String.
	 */
	private Set<String> extractFeatures(Variable configuration){
		SetVariable svconf = (SetVariable) configuration;
		Set<Variable> fts = svconf.getVars();
		Set<String> strConfs = new HashSet<String>();
		for (Variable ft : fts) {
			FeatureVariable ftv = (FeatureVariable) ft;
			strConfs.add(ftv.getFtName());
		}
		return strConfs;
	}
	
	
	private int nbClauses(Node node) {
		return node.toCNF().getChildren().length;
	}
	
	/**
	 * Generate the DIMAC file corresponding to the Feature Model.
	 * 
	 * @param fmvJhipster Feature Model
	 */
	private void generateDimacs(FeatureModelVariable fmvJhipster){
		String dimacsHipster = fmvJhipster.convert(org.xtext.example.mydsl.fml.FMFormat.DIMACS);
		Node n = new SATFMLFormula(fmvJhipster).getNode();
		Files.writeStringIntoFile(DIMACS_FILENAME, dimacsHipster.replace("XXXXXX", "" + nbClauses(n)));
	}

	
	public static void main(String[] args) {
        JUnitCore jCore = new JUnitCore();
        jCore.run(JHipsterTest.class);
    }
	
	/**
	 * Generates all variants of JHipster 3.6.1 to test them afterwards. 
	 */
	@Test
	public void testJHipsterGeneration() throws Exception{
				
		_log.info("Extracting Feature Model...");
		FeatureModelVariable fmvJhipster = getFMJHipster();
		
		_log.info("Checking validity of Feature Model...");
		assertTrue(fmvJhipster.isValid());
		_log.info("Feature Model is valid !");
		
		_log.info("The feature model has: "+fmvJhipster.counting()+" valid configuration(s).");
		_log.info("The feature model has: "+fmvJhipster.nbFeatures()+ " feature(s).");
		_log.info("The feature model has: "+fmvJhipster.cores().size()+" core feature(s).");
		_log.info("The feature model has: "+fmvJhipster.deads().size()+" dead feature(s).");
		_log.info("The feature model has: "+fmvJhipster.falseOptionalFeatures().size()+" false optional feature(s).");
		_log.info("The feature model has: "+fmvJhipster.fullMandatoryFeatures().size()+" full mandatory feature(s).");
		_log.info("The feature model has: "+fmvJhipster.getAllConstraints().size()+" constraint(s).");
		_log.info("The feature model has a depth of "+fmvJhipster.depth());
		
		_log.info("Extracting configurations...");
		Set<Variable> confs = fmvJhipster.configs();
		_log.info("Extraction done !");
				
		_log.info("Generating DIMACS...");
		generateDimacs(getFMJHipster());
		
		SCRIPT_BUILDER.generateStopDatabaseScript(PROJECTDIRECTORY);
		 
		// Transform to list for shuffling
		List<Variable> list = new ArrayList<Variable>(confs);
		Collections.shuffle(list);	
		
		int i = 0;
		for (Variable configuration : list){

			_log.info("Extracting features from the configuration...");
			Set<String> strConfs = extractFeatures(configuration);
			
			JhipsterConfiguration jConf = toJhipsterConfiguration(strConfs, getFMJHipster());
					
			// TODO: Nevermind Oracle, H2, ClientApp & ServerApp for now.
			if((jConf.applicationType.endsWith("App"))|(jConf.devDatabaseType.equals("oracle"))|(jConf.prodDatabaseType.equals("oracle"))){}
			else{
				i++;
				String jDirectory = "jhipster" + i;
				mkdirJhipster(jDirectory);
			 	
				_log.info("Parsing JSON...");
	
				GeneratorJhipsterConfiguration jhipGen = new GeneratorJhipsterConfiguration();
				jhipGen.generatorJhipster = jConf;
				String yorc = toJSON2(jhipGen);
				Files.writeStringIntoFile(getjDirectory(jDirectory) + ".yo-rc.json", yorc);
				_log.info("JSON generated...");
				
				_log.info("Generating scripts...");
				SCRIPT_BUILDER.generateScripts(jConf, jDirectory);
				_log.info("Scripts generated...");
		
				_log.info("Configuration "+i+", "+jConf.applicationType+", is done");
			}
		}
	}
}