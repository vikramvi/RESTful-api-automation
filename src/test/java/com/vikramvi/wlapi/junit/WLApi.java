/*
 * 
 * Wunderlist Public API Automation using simply awesome "REST-assured" framework
 * 
 */

package com.vikramvi.wlapi.junit;

import org.junit.Test;
import com.jayway.restassured.path.json.JsonPath;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.http.ContentType.JSON;
import java.util.HashMap;
import java.util.Map;

public class WLApi {
 
	//https://developer.wunderlist.com/apps
	private String access_Token = "dummy";
	private String client_ID = "dummy";
	
	//https://developer.wunderlist.com/documentation
	private String APIUrl = "https://a.wunderlist.com/api/v1";
	
	static private String list_id = ""; 
	static private String task_id = "";
	
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
		   given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
	       when().
                  get(APIUrl + "/lists").
           then().
                  statusCode(200);
	}
	
	@Test
	public void createList(){
		   Map<String, Object>  jsonAsMap = new HashMap<>();
		   jsonAsMap.put("title", "ECLIPSE-list");
		
	       final String body = with().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
			                          contentType("application/json; charset=utf-8").
			                          body("{\"title\":\"Eclipse-POST\"}").when().
	                                  post(APIUrl + "/lists").asString();
		
	       //System.out.println(body);
	       final JsonPath jsonPath = new JsonPath(body);
	       list_id = jsonPath.getString("id");
	       System.out.println ( "list_id -> " + list_id );

		
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
	      /* given().headers("X-Access-Token",access_Token,"X-Client-ID",client_ID).
	               contentType(JSON).
	               body(jsonAsMap).
	       when().
                   post(APIUrl + "/lists").
           then().
                   statusCode(201);*/
	}
	
	@Test
	public void createTask(){
		 //list_id = "229958818";
		
		   final String body = with().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
				                      contentType("application/json; charset=utf-8").
				                      body("{\"list_id\":"+list_id+",\"title\":\"POST-TASK\"}").
				               when().
		                              post(APIUrl + "/tasks").asString();
			
		   //System.out.println(body);
		   final JsonPath jsonPath = new JsonPath(body);
		   task_id = jsonPath.getString("id");
		   System.out.println ( "task_id -> " + task_id );
		
		   //Approach - 1
		   /*given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
                    contentType("application/json; charset=utf-8").	
                    //body(String.format("{\"list_id\": %s,\"title\":\"POST-TASK\"}", list_id)).
                    body("{\"list_id\":"+list_id+",\"title\":\"POST-TASK\"}").
             when().
                    post(APIUrl + "/tasks").
             then().
                    statusCode(201);*/
	}
	
	@Test
	public void EditTask(){
		   //task_id = "1547093272";
		
		   given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
                   contentType("application/json; charset=utf-8").	
                   body("{\"revision\":1,\"title\":\"PATCH-REQUEST-TASK\"}").
           when().
                   patch(APIUrl + "/tasks/"+ task_id).
           then().
                   statusCode(200);
	}
	
	@Test
	public void DeleteTask(){
		   //task_id = "1547093272";
		
		   given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
		           contentType("application/json; charset=utf-8").	
		   when().
		          delete(APIUrl + "/tasks/"+ task_id +"?revision=2").
		   then().
		          statusCode(204);
	}
	
	@Test
	public void EditList(){
		   //list_id = "229967024";
		
		   //debug purpose only - get revision number run time ??
		   
		   final String body = with().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
	                                  contentType("application/json; charset=utf-8").
	                                  get(APIUrl + "/lists/" + list_id).asString();

           System.out.println(body);

		   
		   //Map<String, Object>  jsonAsMap = new HashMap<>();
		   //jsonAsMap.put("revision", "1");
	       //jsonAsMap.put("title", "ZZZZZZ");
	    
	       given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
	               contentType("application/json; charset=utf-8").	
	               //body(jsonAsMap).
	               body("{\"revision\":4,\"title\":\"PATCH-REQUEST\"}").
           when().
                   patch(APIUrl + "/lists/"+ list_id).
           then().
                   statusCode(200);
	}
	
	@Test
	public void DeleteList(){
		   //list_id = "229932841";
		   
		   given().headers("X-Access-Token",access_Token, "X-Client-ID",client_ID).
                   contentType("application/json; charset=utf-8").	
           when().
                  delete(APIUrl + "/lists/"+ list_id +"?revision=5").
           then().
                  statusCode(204);
	}
	
}



