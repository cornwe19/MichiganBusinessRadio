package com.michiganbusinessnetwork.radio;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import android.net.Uri;
import android.os.AsyncTask;

public class AdvertisementWebLoader extends AsyncTask<Object,Void,Exception> {

   private static final String ROOT_NODE = "root";
   private static final String AD_NODE = "ad";
   private static final String NOW_PLAYING_NODE = "nowPlaying";
   private static final String FULL_SCREEN_IMAGE_NODE = "mobile";
   private static final String BANNER_IMAGE_NODE = "mobileBanner";
   private static final String TARGET_URI_NODE = "url";
   
   Advertisement mLoadedAd;
   Advertisement.OnLoadedCallback mCallback;
   
   public AdvertisementWebLoader( Advertisement.OnLoadedCallback callback ) {
      mCallback = callback;
   }
   
   @Override
   protected Exception doInBackground( Object... params ) {
      Exception caughtException = null;
      String request;
      
      try {
         request = (String)params[0];
         mLoadedAd = (Advertisement)params[1];
      }
      catch ( ClassCastException e ){
         return new IllegalArgumentException( "Must pass a string request and an advertisement to load." );
      }
      catch ( ArrayIndexOutOfBoundsException e ) {
         return new IllegalArgumentException( "Must pass a string request and an advertisement to load." );
      }
      
      try {
         InputSource source = WebHelper.getResponse( request );
         
         XPathFactory xPathFactory = XPathFactory.newInstance();
         XPath xPath = xPathFactory.newXPath();
         
         Node rootNode = (Node)xPath.evaluate( ROOT_NODE, source, XPathConstants.NODE );
         
         loadAdvertisementXML( mLoadedAd, xPath, rootNode );
         loadRadioFeedIntoAdvertisement( mLoadedAd, xPath, rootNode );
      }
      catch ( MalformedURLException e ) {
         caughtException = e;
      }
      catch ( IOException e ) {
         caughtException = e;
      }
      catch ( XPathExpressionException e ) {
         caughtException = e;
      }
      
      return caughtException;
   }

   @Override
   protected void onPostExecute( Exception e ) {
      if( mCallback != null ) {
         if( e == null ) {
            mCallback.onLoaded( mLoadedAd );
         }
         else {
            mCallback.onError( e );
         }
      }
   }
   
   private void loadAdvertisementXML( Advertisement advertisement, XPath xPath, Node rootNode ) throws XPathExpressionException {
      Node advertisementInfo = (Node)xPath.evaluate( AD_NODE, rootNode, XPathConstants.NODE );

      advertisement.mFullscreenImageUri = Uri.parse( getNodeString( xPath, advertisementInfo, FULL_SCREEN_IMAGE_NODE ) );
      advertisement.mBannerImageUri = Uri.parse( getNodeString( xPath, advertisementInfo, BANNER_IMAGE_NODE ) );
      advertisement.mTargetUri = Uri.parse( getNodeString( xPath, advertisementInfo, TARGET_URI_NODE ) );
   }
   
   private void loadRadioFeedIntoAdvertisement( Advertisement advertisement, XPath xPath, Node rootNode ) throws XPathExpressionException {
      Node nowPlayingTitle = (Node)xPath.evaluate( NOW_PLAYING_NODE, rootNode, XPathConstants.NODE );
      
      advertisement.mCurrentRadioProgram = getNodeValueOrEmptyString( nowPlayingTitle );
   }
   
   private String getNodeString( XPath xpath, Node parent, String nodeName ) throws XPathExpressionException { 
      Node node = (Node)xpath.evaluate( nodeName, parent, XPathConstants.NODE );
      
      String returnString = getNodeValueOrEmptyString( node );
      
      return returnString;
   }

   // Note: safeguards against any bad XML that could be generated in the web feed
   //  - This could potentially happen when no radio station is playing
   private String getNodeValueOrEmptyString( Node node ) {
      String returnString = "";
      
      Node nodeText = null;
      if( node != null ) {
         nodeText = node.getFirstChild();   
      }
      
      if( nodeText != null ) {
         returnString = nodeText.getNodeValue();
      }
      
      return returnString;
   }
}
