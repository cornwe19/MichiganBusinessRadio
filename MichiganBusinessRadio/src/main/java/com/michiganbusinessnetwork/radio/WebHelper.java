package com.michiganbusinessnetwork.radio;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xml.sax.InputSource;

public class WebHelper {

   public static InputSource getResponse( String request ) throws IOException {
      InputStreamReader reader = getResponseReader( request );

      return new InputSource( reader );
   }

   public static InputStream getResponseStream( String request ) throws IOException {
      HttpURLConnection connection = getHttpConnection( request );

      return connection.getInputStream();
   }

   private static HttpURLConnection getHttpConnection( String request ) throws IOException {
      URL url = new URL( request );
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput( true );
      return connection;
   }

   private static InputStreamReader getResponseReader( String request ) throws IOException {
      HttpURLConnection connection = getHttpConnection( request );
      connection.setRequestMethod( "GET" );

      return new InputStreamReader( connection.getInputStream() );
   }

}
