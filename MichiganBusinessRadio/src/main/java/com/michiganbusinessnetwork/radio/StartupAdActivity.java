package com.michiganbusinessnetwork.radio;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class StartupAdActivity extends Activity implements Advertisement.OnLoadedCallback {

   private static final String TAG = "StartupAdactivity";
   private Uri mAdvertisementUri = null;

   @Override
   public void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.ad_screen );
      
      Advertisement.loadAsync( Advertisement.MI_BUSINESS_AD_FEED, this );
   }
   
   public void onClickViewAd( View v ) {      
      dismissAd();
      
      if( !mAdvertisementUri.equals( "" ) ) {
         Intent browserIntent = new Intent( Intent.ACTION_VIEW, mAdvertisementUri );
         startActivity( browserIntent );
      }
   }

   public void onDismissAd( View v ) {
      dismissAd();
   }

   private void dismissAd() {
      RadioPlayerActivity.launch( this );
      
      finish();
   }

   @Override
   public void onLoaded( final Advertisement ad ) {
      final ImageView adImage = (ImageView)findViewById( R.id.advertisement_image );
      
      Runnable downloadAndUpdateAdImage = new Runnable() {
         @Override
         public void run() {
            final Bitmap adBitmap = downloadBitmapFromUrl( ad.mFullscreenImageUri );
            
            runOnUiThread( new Runnable() {
               @Override
               public void run() {
                  if( adBitmap != null ) {
                     adImage.setImageBitmap( adBitmap );
                  }
                  else {
                     // Dismiss the ad if the bitmap failed to load.
                     dismissAd();
                  }
               }
            });
         }
      };
      
      new Thread( downloadAndUpdateAdImage ).start();
      
      mAdvertisementUri = ad.mTargetUri;
   }

   private Bitmap downloadBitmapFromUrl( Uri url ) {
      Bitmap imageBitmap = null;
      
      try {
         InputStream imageStream = WebHelper.getResponseStream( url.toString() );
         imageBitmap = BitmapFactory.decodeStream( imageStream );
         imageStream.close();
      }
      catch ( MalformedURLException e ) {
         // Don't bother user if the ad can't load for some reason
      }
      catch ( IOException e ) {
         // Don't bother user if the ad can't load for some reason
      }
      
      return imageBitmap;
   }
   
   @Override
   public void onError( Exception e ) {
      Log.e( TAG, "Failed to load advertisement image.", e );
      
      if( e instanceof MalformedURLException ) {
         throw new RuntimeException( e );
      }
      else if ( e instanceof IllegalArgumentException ) {
         throw new RuntimeException( e );
      }
      
      finish();
   }
}