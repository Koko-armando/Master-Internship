import org.apache.log4j.Logger;

/**
 * Represents a specific configuration of JHipster.
 * Each attribute stands for a field in the yo-rc.json that will be generated.
 * 
 * edited by Axel on September 20th, 2016.
 */
public class JhipsterConfiguration {
	String baseName;
	String packageName;
	String packageFolder;
	String serverPort;
	String authenticationType;
	String hibernateCache;
	String databaseType;
	String devDatabaseType;
	String prodDatabaseType;
	String buildTool;
	String jwtSecretKey;
	protected String applicationType; // gateway | microservice | monolith | uaa; protected so that it is omitted when Client/Server standalones, see JHipsterTest.java
	String[] testFrameworks;
	String jhiPrefix;
	String jhipsterVersion;
	String clientFramework;
	Boolean installModules=false;
	Boolean useSass = false;
	boolean enableTranslation=false; 
	String nativeLanguage;
	String[] languages = new String[2];
	String[] othersgenerator;
	Boolean skipClient;
	Boolean skipServer;
	Boolean skipUserManagement;
	Boolean enableSocialSignIn;
	Boolean enableSwaggerCodegen;
    String rememberMeKey;
    String uaaBaseName;

		
    String  serviceDiscoveryStringValue=null;
    Boolean serviceDiscoveryBooleanValue=null;
		
	String  BrokerStringValue=null;
    Boolean BrokerBooleanValue=null;
    
    Boolean SearchEngineBooleanValue=null;
    String  SearchEngineStringValue=null;

    String  websocketStringValue=null;
    Boolean websocketBooleanValue=null;
    String clientPackageManager="npm";
    String  cluteredStringValue=null;
    Boolean cluteredBooleanValue=null;
    
	
	
		// TODO update toString and equals.
	
				
				
	  public void getServiceDiscoveryType(String serviceDiscoveryType) {
  	    if (serviceDiscoveryType.equals("false")) 
  	    	serviceDiscoveryBooleanValue= Boolean.parseBoolean(serviceDiscoveryType);
  	    else serviceDiscoveryStringValue= serviceDiscoveryType;
 	 
  	 
   }	
	  
	  
	  public void getMessageBroker(String messageBroker) {
	  	    if (messageBroker.equals("false")) 
	  	    	BrokerBooleanValue= Boolean.parseBoolean(messageBroker);
	  	    else BrokerStringValue= messageBroker;
	  	  	 
	   }	
				
	  public void getSearchEngine(String searchengine) {
	  	    if (searchengine.equals("false")) 
	  	    	SearchEngineBooleanValue= Boolean.parseBoolean(searchengine);
	  	    else SearchEngineStringValue= searchengine;
	  	 
	   }	
		
	  public void getWebSocket(String websocket) {
	  	    if (websocket.equals("false")) 
	  	    	websocketBooleanValue= Boolean.parseBoolean(websocket);
	  	    else websocketStringValue= websocket;
	  	 
	   }	
		
	    public void getClustered(String clustered) {
	  	    if (clustered.equals("false")) 
	  	    	cluteredBooleanValue= Boolean.parseBoolean(clustered);
	  	    else cluteredStringValue= clustered;
	  	 
	   }	
	  
	  
	  
		
		

}