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
        return FM ("jhipster", "FM (jhipster : "
        + "Server Application [Client] ; "
        + "Server:  [SocialLogIn] [ClusteredHttpSession]  [SwaggerCodegen] [MessageBroker] [WebSocket] ServerSideOption [SearchEngine] ; "
        + "Application: ApplicationType TestFramework ; "
        + "Client: ClientFrameWork [LibSass]; "
        + "ServerSideOption: AuthenticationType HibernateCache DataBaseType [ProDataBaseType] [DevDataBaseType] BuildTool ServiceDiscoveryType ; "
        + "ApplicationType: (MicroserviceGateway|Monolithic|MicroserviceApplication|UaaServer) ; "
        + "TestFramework: [Cucumber] [Protractor] [Gatling] ; "
        + "ClientFrameWork: (AngularJS|Angular4) ; "
        + "AuthenticationType: (HTTPSession|Uaa|Oauth2|JWT) ; "
        + "HibernateCache: (Infinispan|HazelCast|EhCache|NoHB) ; "
        + "DataBaseType: (SQL|NoDB|Cassandra|MongoDB) ; "
        + "ProDataBaseType: (MySQL|MsSQL|Oracle|MariaDB|PostgreSQL) ; "
        + "DevDataBaseType: (Oracle12c|H2InMemory|PostgreSQLDev|MariaDBDev|MsSql|MySql|H2DiskBased) ; "
        + "BuildTool: (Gradle|Maven) ; "
        + "ServiceDiscoveryType: (Consul|Eureka|False) ; "
        //+ "(Monolithic -> (False|Eureka)) ;"
        //+ "((Monolithic & !Eureka) -> (JWT|HTTPSession|Oauth2)) ;"
       // + "(((MicroserviceGateway | MicroserviceApplication)) -> (JWT | Uaa)) ;"
       // + "(!MicroserviceApplication & (!Uaa | !MicroserviceGateway ))-> !NoDB;"
       // + "!DataBaseType -> (SQL|Cassandra|MongoDB);"
        + "(Monolithic -> (False|Eureka)) ;"
        + "(Monolithic -> !Uaa) ;"
        + "(((MicroserviceGateway | MicroserviceApplication)) -> (JWT | Uaa)) ;"
        + "(!MicroserviceApplication & (!Uaa | !MicroserviceGateway ))-> !NoDB;"
        + " Oauth2 -> (SQL | MongoDB);"
        + "SQL -> ProDataBaseType;"
        + "(SQL & MariaDB) -> (H2DiskBased | H2InMemory | MariaDBDev) ; "
        + "(SQL & PostgreSQL) -> (H2DiskBased | H2InMemory | PostgreSQLDev) ; "
        + "(SQL & Oracle) -> (H2DiskBased | H2InMemory | Oracle12c) ; "
        + "(SQL & MsSQL) -> (H2DiskBased | H2InMemory | MsSql) ;"
        + "(SQL & MySQL) -> (H2DiskBased | H2InMemory | MySql) ;"
        //+ "(SQL &  !MicroserviceGateway) -> HibernateCache; "
        + "(Eureka & !Uaa) -> JWT ; "
        + "UaaServer-> Uaa;"
        + "  (MongoDB |Cassandra|NoDB )->  (!DevDataBaseType & !ProDataBaseType);"
        + "  (((MongoDB |Cassandra)& !MicroserviceGateway)|NoDB )->  (NoHB);"

        + " (MicroserviceGateway -> HazelCast) ; "
        + "(Cassandra | !Monolithic | (!HTTPSession & !JWT)) -> !SocialLogIn ; "
        + "!SQL-> !SearchEngine;"
        + "  ((!Monolithic & !MicroserviceGateway) | (!NoHB & !HazelCast)) -> !ClusteredHttpSession ;"
        + "(!Monolithic & !MicroserviceGateway) -> !WebSocket ; "

         // +++++++++++++++++++++++++++++++++++imposé++++++++++++++++++++++++++++++

         +"(MicroserviceApplication | UaaServer) -> (!Client & Cucumber & Gatling & !Protractor);"
         +"(Monolithic|MicroserviceGateway)->(Client & Cucumber & Protractor & Gatling );"

         //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

         //+++++++++++++++++++++++++++++ vrai contrainte+++++++++++++++++++++++++++++

       // +"(MicroserviceApplication | UaaServer) -> (!Client & !Protractor);"
       // +"(Monolithic|MicroserviceGateway)->Client ;"
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
             // +"!SQL;"
              //+"!Client;"
             //+"!Eureka;"
              //+"!Consul;"
             //+"!MessageBroker;"
             //+"!MongoDB;"
              //+"!Cassandra;"
              //+"!NoDB;"
              +"!Oracle;"
              +"!Oracle12c;"
              //+"!MariaDB;"
              //+"!MariaDBDev;"
              //+"!PostgreSQL;"
              //+"!PostgreSQLDev;"
              //+"!MsSQL;"
              //+"!MsSql;"
              //+"!MySQL;"
              //+"!MySql;"
                
                +")");
         
         
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
        final String JHIPREFIX = "jhi";
        final String SESSIONKEY = "13e6029734fb9984533b9bb5c511bca6d624c6ed";
        final String JWTKEY = "d8837e4a671d25456432b55b1e4a99fe0356ed07";
        final String UAABASENAME = "uaa";
         
        JhipsterConfiguration jhipsterConf = new JhipsterConfiguration();
         
             
         jhipsterConf.applicationType = get("ApplicationType", strConfs, fmvJhipster);
         
 
          
          
            // Common attributes
            jhipsterConf.jhipsterVersion = JHIPSTERVERSION;
            jhipsterConf.baseName = BASENAME;
            jhipsterConf.packageName = PACKAGENAME;
            jhipsterConf.packageFolder = PACKAGENAME.replace(".", "/");
            if(!jhipsterConf.applicationType.equals("monolith")) jhipsterConf.serverPort = SERVERPORT;
            //jhipsterConf.serviceDiscoveryType=null;= new serviceDiscovery(true);
            
                  		  
            jhipsterConf.getServiceDiscoveryType(get("ServiceDiscoveryType", strConfs, fmvJhipster)  ); 
            jhipsterConf.authenticationType = get("AuthenticationType", strConfs, fmvJhipster);
     
            if ((jhipsterConf.applicationType.equals("gateway") || jhipsterConf.applicationType.equals("microservice")) && jhipsterConf.authenticationType.equals("uaa"))
                jhipsterConf.uaaBaseName=UAABASENAME;
         
 
             
             
            jhipsterConf.databaseType = falseByNo(get("DataBaseType", strConfs, fmvJhipster));

            if(jhipsterConf.databaseType.equals("mongodb") || jhipsterConf.databaseType.equals("cassandra")){
                jhipsterConf.devDatabaseType = jhipsterConf.databaseType;
                jhipsterConf.prodDatabaseType = jhipsterConf.databaseType;
                jhipsterConf.hibernateCache = falseByNo(get("HibernateCache", strConfs, fmvJhipster));

            } else{   
                if(jhipsterConf.databaseType.equals("no")) {
                    jhipsterConf.devDatabaseType  = "no";
                    jhipsterConf.prodDatabaseType = "no";
                    jhipsterConf.hibernateCache   = "no";

                    }
                 
                 
                else {
                jhipsterConf.prodDatabaseType = (get("ProDataBaseType", strConfs, fmvJhipster));

                jhipsterConf.devDatabaseType = (get("DevDataBaseType", strConfs, fmvJhipster));
                jhipsterConf.hibernateCache=falseByNo(get("HibernateCache", strConfs, fmvJhipster));
                         
            } }         
             
         
 
             
             
            jhipsterConf.buildTool = get("BuildTool", strConfs, fmvJhipster);
             
             
 
 
            if (jhipsterConf.authenticationType.equals("jwt")|| jhipsterConf.applicationType.equals("microservice")  ){jhipsterConf.jwtSecretKey = JWTKEY;}
            else if (jhipsterConf.authenticationType.equals("session")){jhipsterConf.rememberMeKey = SESSIONKEY;}
                    else if (jhipsterConf.applicationType.equals("gateway")  && jhipsterConf.authenticationType.equals("uaa")){jhipsterConf.skipUserManagement =true;}
                     
 
            if(isIncluded("ClusteredHttpSession", strConfs).equals("true"))jhipsterConf.getClustered("hazelcast");
            else jhipsterConf.getClustered("false");
             
 
 
            if(isIncluded("WebSocket", strConfs).equals("true")) jhipsterConf.getWebSocket("spring-websocket");
            else jhipsterConf.getWebSocket("false");
             
 
                 
             if(isIncluded("SocialLogIn", strConfs).equals("true")) jhipsterConf.enableSocialSignIn =true;
             else jhipsterConf.enableSocialSignIn = false;
              
 
            if ((isIncluded("MessageBroker", strConfs)).equals("true")) jhipsterConf.getMessageBroker("kafka");
            else jhipsterConf.getMessageBroker("false");
             
 
 
            if(isIncluded("SwaggerCodegen", strConfs).equals("true")) jhipsterConf.enableSwaggerCodegen = true;
            else jhipsterConf.enableSwaggerCodegen = false;
             
 
            if(isIncluded("SearchEngine", strConfs).equals("true")) jhipsterConf.getSearchEngine("elasticsearch");
            else jhipsterConf.getSearchEngine("false"); 
            
                  
        if (jhipsterConf.applicationType.equals("microservice") || jhipsterConf.applicationType.equals("uaa")) {
            jhipsterConf.skipClient = true;
            jhipsterConf.skipServer = false;
        }
           
         
        if (jhipsterConf.applicationType.equals("monolith") || jhipsterConf.applicationType.equals("gateway")) {
            jhipsterConf.skipClient = false;
            jhipsterConf.skipServer = false;
                         
        }
 

         
        if(!jhipsterConf.skipClient) {
        jhipsterConf.clientFramework= get("ClientFrameWork", strConfs, fmvJhipster);
        jhipsterConf.useSass = Boolean.parseBoolean(isIncluded("LibSass", strConfs));
     
        } else {jhipsterConf.clientFramework= "no";jhipsterConf.useSass=false;}
         
 
 
         
        //TODO If internationalization support (we limit ourselves to English for now)
         
   //     if(isIncluded("InternationalizationSupport", strConfs).equals("true")){
            jhipsterConf.enableTranslation = true;
            jhipsterConf.nativeLanguage = "en";
            jhipsterConf.languages = new String[]{"en","fr"};
     /*   }
        else {
            jhipsterConf.enableTranslation = false;
             
        }*/
         
         
 
        jhipsterConf.testFrameworks = gets("TestFramework", strConfs, fmvJhipster);
         
                String strTestFrameworks = "[";
                if(jhipsterConf.testFrameworks!=null){
                for (int i = 0; i < jhipsterConf.testFrameworks.length; i++) {
                    strTestFrameworks += jhipsterConf.testFrameworks[i];
                    if (i < (jhipsterConf.testFrameworks.length - 1))
                        strTestFrameworks += ",";
                }
                strTestFrameworks += "]";
                };
                 
 
         
         
        return jhipsterConf;
    }   
     
     
     
     
     
     
    private static String replace(String originalText,String subStringToFind, String subStringToReplaceWith) {
        int s = 0; int e = 0;
      StringBuffer newText = new StringBuffer();
      while ((e = originalText.indexOf(subStringToFind, s)) >= 0) {
                    newText.append(originalText.substring(s, e));
                    newText.append(subStringToReplaceWith);
                    s = e + subStringToFind.length();
                 }

                 newText.append(originalText.substring(s));
                 return newText.toString();

                } // end replace(String, String, String)
 
    /*
     * Give the real name of every feature depending of her values( string or boolean)
     * 
     */
 
 private static String replaceALL(String chaine, JhipsterConfiguration jConf) {
 	    	
  if (jConf.serviceDiscoveryBooleanValue==null)
 	     	 
		 chaine=replace(chaine, "serviceDiscoveryStringValue", "serviceDiscoveryType"); 
         else 
        	 chaine= replace(chaine,"serviceDiscoveryBooleanValue", "serviceDiscoveryType");   
         
         if (jConf.BrokerBooleanValue==null)
        	 chaine= replace(chaine,"BrokerStringValue", "messageBroker"); 
             else 
            	 chaine=replace(chaine,"BrokerBooleanValue", "messageBroker"); 
         
         if (jConf.cluteredBooleanValue==null)
        	 chaine=replace(chaine,"cluteredStringValue", "clusteredHttpSession"); 
             else 
            	 chaine=replace(chaine,"cluteredBooleanValue", "clusteredHttpSession"); 
         
         
         if (jConf.websocketBooleanValue==null)
        	 chaine=replace(chaine,"websocketStringValue", "websocket"); 
             else 
            	 chaine=replace(chaine,"websocketBooleanValue", "websocket"); 
         
         if (jConf.SearchEngineBooleanValue==null)
        	 chaine=replace(chaine,"SearchEngineStringValue", "searchEngine"); 
             else 
            	 chaine=replace(chaine,"SearchEngineBooleanValue", "searchEngine"); 
        	   	
         return chaine;
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
        
        
        return (replaceALL(gson.toJson(jhipsterConf), jhipsterConf.generatorJhipster));
        
       
        
       
        
       
    }
 
    /**
     * Transform "false" String in "no"
     * 
     * @param s String to transform
     * @return "no" if s = "false"; s otherwise.
     */
    private String falseByNo(String s) {
        if (s.equals("false")) return "no";
        if(s.equals("NoDB"))return "no";
        if(s.equals("NoHB"))return "no";
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
            case "MicroserviceGateway":     return "gateway";
            case "Monolithic":              return "monolith";
            case "UaaServer":               return "uaa";
            case "Maven":                   return "maven";
            case "Gradle":                  return "gradle";
            case "SQL":                     return "sql";
            case "Cassandra":               return "cassandra";
            case "MongoDB":                 return "mongodb";
            case "Oracle12c":               return "oracle";
            case "PostgreSQLDev":           return "postgresql";
            case "MariaDB":                 return "mariadb";
            case "MySql":                   return "mysql";
            case "Oracle":                  return "oracle";
            case "MariaDBDev":              return "mariadb";
            case "H2DiskBased":             return "h2Disk";
            case "H2InMemory":              return "h2Memory";
            case "False":                   return "false";
            case "SearchEngine":            return "searchEngine";
            case "SocialLogIn":             return "enableSocialSignIn";
            case "HazelCast":               return "hazelcast";
            case "EhCache":                 return "ehcache";
            case "Infinispan":              return "infinispan";
            case "MySQL":                   return "mysql";
            case "MsSQL":                    return "mssql";
            case "MsSql":                   return "mssql";
            case "PostgreSQL":              return "postgresql";
            case "HTTPSession":             return "session";
            case "Uaa":                     return "uaa";
            case "Oauth2":                  return "oauth2";
            case "JWT":                     return "jwt";
            case "Gatling":                 return "gatling";
            case "Protractor":              return "protractor";
            case "Cucumber":                return "cucumber";
            case "Angular4":                return "angularX";
            case "AngularJS":               return "angular1";
            case "Eureka":                  return "eureka";
            case "Consul":                  return "consul";
             
            //case "ServerApp":             return "serverApp";
            default:    return feature;
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
     * Generates all variants of JHipster 4.8.2 to test them afterwards. 
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
             
         
    for (int i=0; i<1001; i++) {
        _log.info("Extracting features from the configuration...");
        Set<String> strConfs = extractFeatures(list.get(i));
 
        JhipsterConfiguration jConf = toJhipsterConfiguration(strConfs, getFMJHipster());
         
 
        // TODO: Nevermind Oracle, H2, ClientApp & ServerApp for now.
    //  if((jConf.applicationType.endsWith("App"))|(jConf.devDatabaseType.equals("oracle"))|(jConf.prodDatabaseType.equals("oracle"))){}
    //  else{
          
            String jDirectory = "jhipster" + i;
            mkdirJhipster(jDirectory);
             
            _log.info("Parsing JSON...");
 
            GeneratorJhipsterConfiguration jhipGen = new GeneratorJhipsterConfiguration();
            jhipGen.generatorJhipster = jConf;
            String yorc = toJSON2(jhipGen);
            
     
            
            
            _log.info(""+yorc);

            
            Files.writeStringIntoFile(getjDirectory(jDirectory) + ".yo-rc.json", yorc);
            _log.info("JSON generated...");
             
            _log.info("Generating scripts...");
           SCRIPT_BUILDER.generateScripts(jConf, jDirectory);
            _log.info("Scripts generated...");

            _log.info("Configuration "+i+", "+jConf.applicationType+", is done"); 
            _log.info("Observation: "+ jConf.toString() );


             
        }       
	CSVUtils.createCSVFileJHipster("jhipster.csv");
   
         
         
         
    }
}