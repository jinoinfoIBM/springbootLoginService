package org.login.service.springbootLoginService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin 
@ServletComponentScan
@SpringBootApplication
@RestController
public class SpringbootLoginServiceApplication {

	Map<String, Object> userCache =null;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	private static Class<SpringbootLoginServiceApplication> applicationClass = SpringbootLoginServiceApplication.class;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringbootLoginServiceApplication.class, args);
	}
	
	 protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(applicationClass);
	    }
	
	
	@SuppressWarnings("unchecked")
	//@RequestMapping (value ="/login")

	@RequestMapping(path = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getAuthentication(@RequestBody Map<String, String> credMap) {
		
	
		String mdn= credMap.get("phoneNumber");
		String pin = credMap.get("password");
		System.out.println ("mdn is ..  "+mdn);
		
		userCache= new ConcurrentHashMap<String, Object>();
		
		JSONObject responseObj = new JSONObject();
		boolean authSuccess =false;
		
		//loading the device json file 
		loadUserJson();
		
		JSONObject userListObj = (JSONObject)userCache.get("users");
		
		//System.out.println ("deviceObj is  "+deviceObj);
		
		Iterator<String> keys = userListObj.keySet().iterator();
		JSONObject userObj =null;
		
		while(keys.hasNext()) {
		    String key = keys.next();
		    System.out.println(" key is "+key);
		    if (userListObj.get(key) instanceof JSONObject) {

		    	userObj=(JSONObject) userListObj.get(key);
		    	
		    	
		    	System.out.println(" phone is "+userObj.get("phone") +"AAmdn is"+mdn+"BB");	
		    	if(userObj.get("phone") !=null && userObj.get("phone").toString().equalsIgnoreCase(mdn)) {
		    		
		    		System.out.println ("userObj.get(\"pin\") "+userObj.get("pin"));
		    		System.out.println ("Pin is  "+pin);
		    		if (userObj.get("pin") !=null  && userObj.get("pin").toString().equalsIgnoreCase(pin)) {
		    			
		    			System.out.println("inide pwd...");
		    			responseObj.put("valid", true);
		    			System.out.println("device image is i"+userObj.get("deviceImage"));
		    			responseObj.put("deviceImage", userObj.get("deviceImage"));
		    			authSuccess = true;
		    			
		    			return responseObj.toString();
		    			
		    		}
		    		
		    		
		    	}
		        
		    }
		}
			
			if( authSuccess) {
				 return responseObj.toString();
			}
			else {
				 responseObj.put("valid", false);
				 return responseObj.toString();
			}
			
			
		
		
	
	}

	private void loadUserJson() {
		

		JSONParser jsonParser = new JSONParser();
		
		try {
			
	
		  Resource resource=resourceLoader.getResource("classpath:./user.json");
			
		  System.out.println("resource .."+resource);
	      InputStream stream= resource.getInputStream();
	      String jsonData ="";
	      BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(stream));

	      System.out.println("stream.."+stream);
	      System.out.println("reader.."+bufferedReader.toString());
	    
	      Object obj=  jsonParser.parse(bufferedReader);
	      
	      System.out.println("obj.."+obj);
	      
	    
	      JSONObject jsonObj = (org.json.simple.JSONObject)obj;
	      
	  
	      JSONObject userObject =  (JSONObject) jsonObj.get("users");
	  				
		  System.out.println("user object is .."+userObject);
			
		  userCache.put("users", userObject);
			
				
		}catch(Exception ex) {
			
			ex.printStackTrace();
		}
		
	    
		
	}



}
