/**
 * 
 */
package uy.com.s4b.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.HttpMethod;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.restfb.json.Json;
import com.restfb.json.JsonObject;
import com.restfb.json.JsonValue;

/***
 * 
 * @author arpela
 *
 */
public class UtilOAuth {

	private static final Logger log = Logger.getLogger(OauthServletInit.class);

	public String appID = "app_prueba";
	public String appSecret = "clientSecret";
	public String URL_APP = "";
	private String urlAccessToken = "";
	private String urlProfile = "";

	
	/**
	 * 
	 */
	public UtilOAuth() {

	}

	public UtilOAuth(String appID, String appSecret, String urlAccessToken, String urlProfile, String redirect_uri) {
		this.appID = appID;
		this.appSecret = appSecret;
		this.URL_APP = redirect_uri;
		this.urlAccessToken = urlAccessToken;
		this.urlProfile = urlProfile;
	}

	
	
	public String getAccessToken(String code) throws Exception {

		log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		log.info("%%%%%%%%%% getAccessToken %%%%%%%%%%%%%");

		String auth = appID.concat(":").concat(appSecret);
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);

		URL url = new URL(urlAccessToken.trim());

		Map<String, String> parametter = new HashMap<>();
		parametter.put("grant_type", "authorization_code");
		parametter.put("client_id", appID);
		parametter.put("redirect_uri", URL_APP);
		parametter.put("client_secret", appSecret);
		parametter.put("code", code);

		String response = fetchURL2(url, authHeaderValue, parametter, appID, appSecret);
		
		log.info("========>  " + response);
		
		JsonObject jsonObject = Json.parse(response).asObject();
		JsonValue retornoToken = jsonObject.get("access_token");
		JsonValue refreshToken = jsonObject.get("refresh_token");
		
		
		log.info("Token ========>  " + retornoToken);
		log.info("Refresh Token ========>  " + refreshToken);

		log.info("%%%%%%%%%%%%%%%%%%%%%%%%");
		log.info("%%%%%%%%%%%%%%%%%%%%%%%%");
		log.info("%%%%%%%%%%%%%%%%%%%%%%%%");
		return retornoToken.asString();

	}

	/* obtener perfil del usuario */
	public String getUserProfile(String token) throws IOException {
		log.info("%%%%%%%%%%% getUserProfile %%%%%%%%%%%%%");
		log.info("Token recuperado: " + token);
		URL url = new URL(urlProfile.trim().concat("?access_token=" + token));
		log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		return fetchURL(url, HttpMethod.GET, null);
	}

	
	private String fetchURL2(URL url, String authHeaderValue, Map<String, String> listPart, 
			String clientId, String clienteSecre) throws Exception {
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(clientId, clienteSecre));
        
        CloseableHttpClient httpclient = HttpClients.custom().build();
		RequestBuilder req = RequestBuilder.post().setUri(url.toURI());

		listPart.forEach((key, value) -> {
			log.info("Key: " + key + " -- Value: " + value);
			req.addParameter(key, value);
		});

		
		HttpUriRequest login = req.build();

		CloseableHttpResponse response2 = httpclient.execute(login);
		
	    log.info("HTTP Response (Code): " + response2.getStatusLine().getStatusCode());
		log.info("HTTP getReasonPhrase: " + response2.getStatusLine().getReasonPhrase());
		HttpEntity entity = response2.getEntity();

		
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		EntityUtils.consume(entity);
		
		return builder.toString();
	}

	private String fetchURL(URL url, String metodo, String authHeaderValue) throws IOException {
		String line;
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setInstanceFollowRedirects(true);
		if (authHeaderValue != null) {
			connection.setRequestProperty("Authorization", authHeaderValue);
		}
//		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Type", "multipart/form-data");
		connection.setUseCaches(false);
		connection.setRequestMethod(metodo);
		log.info("HTTP Response (Code): " + connection.getResponseCode());
		log.info("HTTP Response (Message): " + connection.getResponseMessage());

		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		return builder.toString();
	}
}
