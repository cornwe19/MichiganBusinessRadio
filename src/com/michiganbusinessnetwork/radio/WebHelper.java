package com.michiganbusinessnetwork.radio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.xml.sax.InputSource;

public class WebHelper {
	
	public static InputSource getResponse(String request) throws IOException, MalformedURLException {
		InputStreamReader reader = getResponseReader(request);
        InputSource source = new InputSource(reader);
        
        return source;
	}

	public static InputStream getResponseStream( String request ) throws MalformedURLException, IOException {
	    HttpURLConnection connection = getHttpConnection(request);
        
	    return connection.getInputStream();
	}
	
	public static InputStream postGetResponse( String request, String postData ) throws IOException {
	    HttpURLConnection connection = getHttpConnection(request);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        OutputStreamWriter postStreamWriter = new OutputStreamWriter(connection.getOutputStream());

        postStreamWriter.write(postData);
        
	    postStreamWriter.flush();
        
        
        return connection.getInputStream();
	}

    private static HttpURLConnection getHttpConnection(String request) throws MalformedURLException, IOException {
        URL url = new URL( request );
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        return connection;
    }
	
    private static InputStreamReader getResponseReader(String request) throws MalformedURLException, IOException,
            ProtocolException {
        HttpURLConnection connection = getHttpConnection(request);
        connection.setRequestMethod("GET");
        
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        return reader;
    }
	
	public static InputSource getSanitizedResponse( String request ) throws IOException, MalformedURLException {
	    InputStreamReader reader = getResponseReader(request);
	    
	    BufferedReader bufferedReader = new BufferedReader( reader );
	    String response = "";
	    String responseLine;
	    
	    while ( ( responseLine = bufferedReader.readLine() ) != null ) {
	        response += responseLine.replaceAll("&", "&amp;");
	    }
	    
	    StringReader stringReader = new StringReader(response);
	    return new InputSource(stringReader);
	}
	
}
