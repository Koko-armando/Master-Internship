import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.regex.*;

/*
 * Templates can be retrieved from 	generator-jhipster-xxx/test/templates where xxx is the version number
 */
//TODO also check values of the keys....
public class Utils {
	
		public static boolean testJson(JsonElement json, JsonChecker jsonChecker){
		JsonObject object = json.getAsJsonObject().get("generator-jhipster").getAsJsonObject();
		if(object.has("applicationType")){
			switch(object.get("applicationType").getAsString()){
				case "monolith": 	return monolithCheck(object, jsonChecker);
				case "microservice": return microserviceCheck(object, jsonChecker);
				case "gateway": return gatewayCheck(object, jsonChecker);
				case "uaa": return uaaCheck(object, jsonChecker);
			}
			return false;
		} 
		else return appCheck(object, jsonChecker);
	}
	
	
	private static boolean uaaCheck(JsonObject object, JsonChecker jsonChecker){
		int size = object.entrySet().size();
		if (size == 17 || size==19){
			JsonObject[] templates = jsonChecker.getUaaJson();
			for(JsonObject obj:templates) if(haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		else return false;
	}
	
	private static boolean gatewayCheck(JsonObject object, JsonChecker jsonChecker){
		int size = object.entrySet().size();
		if (size == 20 || size == 22){
			JsonObject[] templates = jsonChecker.getGatewayJson();
			for(JsonObject obj:templates) if (haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		else return false;
	}
	
	private static boolean microserviceCheck(JsonObject object, JsonChecker jsonChecker){
		int size = object.entrySet().size();
		if (size >=19 && size<=22){
			JsonObject[] templates = jsonChecker.getMicroserviceJson();
			for (JsonObject obj : templates) if (haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		else return false;
	}
	
	
	private static boolean monolithCheck(JsonObject object, JsonChecker jsonChecker){
		int size = object.entrySet().size();
		if(size == 19 || size == 21 || size == 22 || size == 23){
			JsonObject[] templates = jsonChecker.getMonolithJson();
			for (JsonObject obj:templates) {
				if (haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			}
			return false;
		}
		else return false;
	}
	
	/**
	 * Checks the validity of the ClientApp and ServerApp yo-rc.json against JHipster templates
	 * 
	 * @param object JsonObject to be tested
	 * @param jsonChecker JsonChecker which holds the templates
	 * @return True if object JSON is valid; False otherwise
	 */
	private static boolean appCheck(JsonObject object, JsonChecker jsonChecker){
		int size = object.entrySet().size();

		// Client
		if(size == 2 || size == 4){
			JsonObject[] templates = jsonChecker.getClientAppJson();
			for(JsonObject obj : templates) if (haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		// Server
		else if (size==15 || size==17 || size==19) {
			JsonObject[] templates = jsonChecker.getServerAppJson();
			for (JsonObject obj : templates) if (haveSameSkeleton(object, obj.get("generator-jhipster").getAsJsonObject())) return true;
			return false;
		}
		// Error
		else return false;
	}
	
	
	/*
	 * Checks if 2 JsonObject have the same skeleton (all the same attributes, no matter their values).
	 */
	private static boolean haveSameSkeleton(JsonObject object1, JsonObject object2){
		boolean res = (object1.entrySet().size() == object2.entrySet().size());
		System.err.println(res);
		if (res){
			for(java.util.Map.Entry<String, JsonElement> entry : object1.entrySet()){
				if (!object2.has(entry.getKey())) return false;
			}
			return true;			
		}
		else return false;
	}
}







class Grep {

    // Charset and decoder for ISO-8859-15
    private static Charset charset = Charset.forName("ISO-8859-15");
    private static CharsetDecoder decoder = charset.newDecoder();

    // Pattern used to parse lines
    private static Pattern linePattern
  = Pattern.compile(".*\r?\n");

    // The input pattern that we're looking for
    private static Pattern pattern;

    // Compile the pattern from the command line
    //
    private static void compile(String pat) {
  try {
      pattern = Pattern.compile(pat);
  } catch (PatternSyntaxException x) {
      System.err.println(x.getMessage());
      System.exit(1);
  }
    }

    // Use the linePattern to break the given CharBuffer into lines, applying
    // the input pattern to each line to see if we have a match
    //
    private static void grep(File f, CharBuffer cb) {
  Matcher lm = linePattern.matcher(cb); // Line matcher
  Matcher pm = null;      // Pattern matcher
  int lines = 0;
  while (lm.find()) {
      lines++;
      CharSequence cs = lm.group();   // The current line
      if (pm == null)
    pm = pattern.matcher(cs);
      else
    pm.reset(cs);
      if (pm.find())
    System.out.print(f + ":" + lines + ":" + cs);
      if (lm.end() == cb.limit())
    break;
  }
    }

    // Search for occurrences of the input pattern in the given file
    //
    private static void grep(File f) throws IOException {

  // Open the file and then get a channel from the stream
  FileInputStream fis = new FileInputStream(f);
  FileChannel fc = fis.getChannel();

  // Get the file's size and then map it into memory
  int sz = (int)fc.size();
  MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

  // Decode the file into a char buffer
  CharBuffer cb = decoder.decode(bb);

  // Perform the search
  grep(f, cb);

  // Close the channel and the stream
  fc.close();
    }

    public static void main(String[] args) {
  if (args.length < 2) {
      System.err.println("Usage: java Grep pattern file...");
      return;
  }
  compile(args[0]);
  for (int i = 1; i < args.length; i++) {
      File f = new File(args[i]);
      try {
    grep(f);
      } catch (IOException x) {
    System.err.println(f + ": " + x);
      }
  }
    }

}