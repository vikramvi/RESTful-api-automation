/*
 * 
 * Wunderlist Public API Automation using simply awesome "REST-assured" framework
 * 
 */

package com.vikramvi.wlapi.junit;

import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import static com.jayway.restassured.config.EncoderConfig.encoderConfig;
import static com.jayway.restassured.RestAssured.config;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.http.ContentType.JSON;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WLApiTest {
 
	//https://developer.wunderlist.com/apps
	private String access_Token = "aaa1a1346e49c73fd0a189f604ea04a9e4b1ee4b044102048ce3ff0ce48d";
	private String client_ID = "b932dc1dbb789fe275ea";
	
	//https://developer.wunderlist.com/documentation
	private String APIUrl = "https://a.wunderlist.com/api/v1";
	
	static private int list_id = 0 ; 
	static private int task_id = 0 ;
	
	@Test
	public void oAuth2(){
		   given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).    
		           auth().oauth2(access_Token).
		   when().
                  get(APIUrl + "/user").
           then().
                  statusCode(200);
	}
	
	@Test
	public void getLists(){
		   given().
		           headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
	       when().
                   get(APIUrl + "/lists").
           then().
                  statusCode(200);
	}
	
	@Test
	public void createList(){
		   SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	       Date now = new Date();
	       String strDate = sdfDate.format(now);
		   String listName = "ECLIPSE-list-" + strDate ; 
	       
		   Map<String, Object>  jsonAsMap = new HashMap<>();
		   jsonAsMap.put("title", listName );
		
		   /*final String body = with().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
			                          //contentType("application/json; charset=utf-8").
			                          //body("{\"title\":"+listName+"}"). ?? Bad Request - to be Fixed
	    		                      contentType(JSON).
	                                  body(jsonAsMap).
			                    when().
			                          post(APIUrl + "/lists").asString();
		
	       System.out.println("createList "+ body);
	       
	       final JsonPath jsonPath = new JsonPath(body);
	       list_id = jsonPath.getString("id");
	       System.out.println ( "list_id -> " + list_id );*/

		
	       //Approach - 1
	       /*given().headers("X-Access-Token",access_Token,"X-Client-ID",client_ID).
			       contentType("application/json; charset=utf-8").	
	               body("{\"title\":\"ECLIPSE-list-1111\"}").
	       when().
                   post(APIUrl + "/lists").
           then().
                   statusCode(201);*/
	       
	      //Approach - 2 
	      //https://github.com/jayway/rest-assured/issues/626
	      //https://github.com/jayway/rest-assured/wiki/Usage#object-mapping
	      //Create JSON from a HashMap
	       given().headers("X-Access-Token",access_Token,"X-Client-ID",client_ID).
	               contentType(JSON).
	               body(jsonAsMap).
	       when().
                   post(APIUrl + "/lists").
           then().
                   statusCode(201);
	       
	       
	        final String body = with().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
                                      //contentType("application/json; charset=utf-8").
                                      //body("{\"title\":"+listName+"}"). ?? Bad Request - to be Fixed
                                      contentType(JSON).
                                when().
                                      get(APIUrl + "/lists").asString();

             System.out.println("createList "+ body);
             
             
             final JsonPath jsonPath = new JsonPath(body);
             final List <Integer> lists = jsonPath.getList("id");
             list_id =  lists.get( lists.size() - 1);
             System.out.println ("Newly Created List Id: " + list_id );
    }
	
	@Test
	public void createTask(){
		      //list_id = 231294978;
		      //String taskName1 = "POST-TASKasdsad";
		      
		      //Map<String, Object>  jsonAsMap = new HashMap<>();
		      //jsonAsMap.put("list_id", list_id );
		      //jsonAsMap.put("title", taskName1  );
		
		     //Approach - 1
		     given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
                     contentType("application/json; charset=utf-8").	
                     //body(String.format("{\"list_id\": %s,\"title\":\"POST-TASK\"}", list_id)).
                     body("{\"list_id\":"+list_id+",\"title\":\"POST-TASK\"}").
		             //contentType(JSON).
                     //body(jsonAsMap).
             when().
                    post(APIUrl + "/tasks").
             then().
                    statusCode(201);
		     
		     
		     final String body = with().
		    		                   headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
                                       contentType("application/json; charset=utf-8").
                                 when().
                                        get(APIUrl + "/tasks?list_id="+ list_id).asString();

             System.out.println("createTask  "+ body);

             final JsonPath jsonPath = new JsonPath(body);
             final List <Integer> tasks = jsonPath.getList("id");
             task_id =  tasks.get( tasks.size() - 1);
             System.out.println ("Newly Created Task Id: " + task_id );
     }
	
	@Test
	//https://github.com/jayway/rest-assured/issues/627
	public void fileUploadToTask(){
		try{
				//task_id = 1565307319;
				
				Map<String, Object> params = new HashMap<>();
				params.put("content_type", "image/jpg");
				params.put("file_name", "Rocky.jpg");
				params.put("file_size", "236726");
				
				//API - 1
				final String body = 
						with().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
		                        contentType("application/json; charset=utf-8").
		                        body(params).
		                 when().
		                        post(APIUrl + "/uploads").asString();
		
		               System.out.println("fileUploadToTask  1 - >"+ body);
		               
		               final JsonPath jsonPath = new JsonPath(body);
		               
		               //nested info
		               //http://www.joecolantonio.com/2014/02/26/rest-testing-with-java-part-two-getting-started-with-rest-assured/
		               String upload_id     = jsonPath.getString("id");
		    		   String awsUpload_URL = jsonPath.getString("part.url");
		    		   String awsAuth       = jsonPath.getString("part.authorization");
		    		   String awsTime       = jsonPath.getString("part.date");
		    		   
		    		   System.out.println("uploadID ---> " + upload_id);
		    		   System.out.println("amazongURL ---> " + awsUpload_URL);
		    		   System.out.println("awsAuth ---> " + awsAuth);
		    		   System.out.println("awsTime ---> " + awsTime);
		    		   System.out.println("------------------------------");
		    		  
		    		//API - 2
		    		     
		    		  /* final String body2 = 
		    					 with().
		    					        headers("Authorization",awsAuth, "x-amz-date",awsTime,"Content-type","").
		    					        body(new File("/Users/vikram-anna/Desktop/Rocky.jpg")).
		    					 when().
		    	                        put(awsUpload_URL).asString();
		               System.out.println(body2);*/   
		    		   //Error charset=ISO-8859-1 
		    		   
		    		   
		    		 //doesnt_add_default_charset_to_content_type_if_configured_not_to_do_so()  
		    		 final RequestSpecification request =  given().
		    				   config(config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
		    				   with().
		    				   		headers("Authorization",awsAuth, "x-amz-date",awsTime,"Content-Type","").
							        body(new File("./src/test/resources/Rocky.jpg"));
		    		   final Response response = request.put(awsUpload_URL);
		               System.out.print(response.statusCode());
		               System.out.println(response.asString()); 
		    		 
		               
		               
		    	     //API 3
		               given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
		                         contentType("application/json; charset=utf-8").	
		                         body("{\"state\":\"finished\"}").
		               when().
		                     patch(APIUrl + "/uploads/" + upload_id).
		               then().
		                      statusCode(200);
		               
		    
		             //API 4
		             /* given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
		                       contentType("application/json; charset=utf-8").	
		                       body("{\"upload_id\":"+upload_id+",\"task_id\":"+task_id+"}").
		              when().
		                      post(APIUrl + "/files").
		              then().
		                    statusCode(201);      */  
		              
		              final String body3 = 
		      				with().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
		                              contentType("application/json; charset=utf-8").
		                              body("{\"upload_id\":"+upload_id+",\"task_id\":"+task_id+"}").
		                       when().
		                             post(APIUrl + "/files").asString();
		
		                     System.out.println("fileUploadToTask 2 -> "+ body3);
		                     
		               Thread.sleep(5000);      
		}catch(Exception e){
			e.printStackTrace();
		}
    	               
	}
	
	@Test
	public void EditTask(){
				//task_id = 1563559545;
				
				/* given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
		                   contentType("application/json; charset=utf-8").	
		                   body("{\"revision\":1,\"title\":\"PATCH-REQUEST-TASK\"}").
		           when().
		                   patch(APIUrl + "/tasks/" + task_id).
		           then().
		                   statusCode(200); */
		
		           //debug purpose to check revision
				   final String body = with().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
		                                      contentType("application/json; charset=utf-8").
		                                      get(APIUrl + "/tasks?list_id="+ list_id).asString();
		
		           System.out.println("EditTask  " + body);
		
		   
                   given().
                          headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
                          contentType("application/json; charset=utf-8").
                          body("{\"revision\":2,\"title\":\"PATCH-REQUEST-TASK\"}").
                   when().
                          patch(APIUrl + "/tasks/" + task_id).
                   then().       
                          statusCode(200);       
    }
	
	@Test
	public void DeleteTask(){
		   //task_id = 1565307319;
		   //list_id = 231294978;
		
		        //debug purpose to check revision
		        final String body = with().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
                                           contentType("application/json; charset=utf-8").
                                    when().
                                           get(APIUrl + "/tasks?list_id="+ list_id).asString();
                System.out.println("DeleteTask  "+ body);
		
		         
                given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
		                contentType("application/json; charset=utf-8").	
		        when().
		                delete(APIUrl + "/tasks/"+ task_id +"?revision=3").
		        then().
		                statusCode(204);
   }
	
	@Test
	public void EditList(){
		   //list_id = 231475725;
		
		   //debug purpose to check revision
		   final String body = with().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
	                                  contentType("application/json; charset=utf-8").
	                                  get(APIUrl + "/lists/" + list_id).asString();

           System.out.println("EditList  " + body);

		   
		   Map<String, Object>  jsonAsMap = new HashMap<>();
		   jsonAsMap.put("revision", 6);
	       jsonAsMap.put("title", "NEW-NAME-OF-LIST");
	    
	       given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
	               //contentType("application/json; charset=utf-8").
	               //body("{\"revision\":5,\"title\":\"PATCH-REQUEST\"}").   //debug purpose only - get revision number run time ??
	               contentType(JSON).
	               body(jsonAsMap).
	       when().
                   patch(APIUrl + "/lists/"+ list_id).
           then().
                   statusCode(200);
	}
	
	@Test
	public void DeleteList(){
		   //list_id = 229932841;
		   
		   //debug purpose to check revision
		   final String body = with().
				                      headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
                                      contentType("application/json; charset=utf-8").
                               get(APIUrl + "/lists/" + list_id).asString();

           System.out.println("DeleteList  " + body);
		   
		   given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
                   contentType("application/json; charset=utf-8").	
           when().
                  delete(APIUrl + "/lists/"+ list_id +"?revision=7").
           then().
                  statusCode(204);
	}
	
}



