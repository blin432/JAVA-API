package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

public class Main {

	private static HttpURLConnection connection;

	public static void main(String[] args) {
		// Method 1:java.net.HttpURLConnection is sync
		
		BufferedReader reader;
		String line;
		StringBuffer responseContent = new StringBuffer();
		try {
			URL url = new URL("https://jsonplaceholder.typicode.com/albums");
			connection = (HttpURLConnection) url.openConnection();

			// request setup
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);// 5 seconds
			connection.setReadTimeout(5000);
			
			int status = connection.getResponseCode();
			System.out.println(status);
			
			if(status>299) {
				reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				//get error message and still have things to read
				while((line = reader.readLine()) != null) {
					responseContent.append(line);
				}
				reader.close();
				//if connection is successful
			}else {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while((line = reader.readLine()) != null) {
					responseContent.append(line);
				}
				reader.close();
			}
			System.out.println(responseContent.toString());
			
			//parsing data
			parse(responseContent.toString());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			connection.disconnect();
		}
		
		//Method2 : java.net.http.HttpClient will handle async for you//  java 11 feature
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://jsonplaceholder.typicode.com/albums")).build();
		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(HttpResponse:: body)//colon colon is a lambda expresion
			.thenAccept(System.out::pintln)//.thenApply(Main::parse)
			.join();
		
		// method to parse JSON data//need to parse data
		//https://mvnrepository.com/artifact/org.json/json
		public static String parse (String responseBody) {
			JSONArray albums = new JSONArray (responseBody);
			for (int i = 0; i<albums.length(); i++) {
				JSONObject album = albums.getJSONObject(i);
				int id = albums.getInt("id");
				int userId = album.getInt("userId");
				String title = album.getString("title");
				System.out.println(id + " " + title + " "+ userId);
			}
			return null;
		}
	}
}
