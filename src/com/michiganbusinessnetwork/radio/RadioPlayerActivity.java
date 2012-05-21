package com.michiganbusinessnetwork.radio;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.michiganbusinessnetwork.radio.RadioService.RadioServiceBinder;

public class RadioPlayerActivity extends Activity implements OnPreparedListener, Advertisement.OnLoadedCallback {

   public static void launch( Context context ) {
      Intent thisIntent = new Intent( context, RadioPlayerActivity.class );
      context.startActivity( thisIntent );
   }

   public static final String MBN_STREAM_URL = "http://radio.michiganbusinessnetwork.com:8000/;stream.nsv";
   private static final Uri MBN_HOME_PAGE = Uri.parse( "http://www.michiganbusinessnetwork.com/" );
   
   private ImageButton mPlayPauseButton;
   private Uri mAdvertisementUri;
   
   private RadioService mRadioPlayer;
   private ServiceConnection mConnection = new ServiceConnection() {

      @Override
      public void onServiceConnected( ComponentName className, IBinder binder ) {
         mRadioPlayer = ( (RadioServiceBinder)binder ).getService();
         
         if( mRadioPlayer.isPrepared() ) {
            updatePlayPauseUI();
         }
         else {
            mRadioPlayer.setOnPreparedListener( RadioPlayerActivity.this );
         }
      }

      @Override
      public void onServiceDisconnected( ComponentName className ) {
         mRadioPlayer = null;
      }
      
   };
   
   @Override
   public void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      
      setContentView( R.layout.radio_player );
      
      mPlayPauseButton = (ImageButton)findViewById( R.id.playPauseButton );
      mPlayPauseButton.setEnabled( false );
      
      Advertisement.loadAsync( Advertisement.MI_BUSINESS_AD_FEED, this );
   }
   
   @Override
   public void onStart() {
      super.onStart();
      
      Intent radioService = new Intent( this, RadioService.class );
      
      bindService( radioService, mConnection, Context.BIND_AUTO_CREATE );
   }

   @Override
   public void onStop() {
      super.onStop();
      
      if( mRadioPlayer != null ) {
         unbindService( mConnection );
      }
   }
   
   public void onClickPlayPause( View v ) {
      if( mRadioPlayer != null ) {
         if ( mRadioPlayer.isPlaying() ) {
            mRadioPlayer.stop();
         }
         else {
            mRadioPlayer.play();
         }
      }
      
      updatePlayPauseUI();
   }

   public void onClickLaunchMBNWebsite( View view ) {
      Intent browserIntent = new Intent( Intent.ACTION_VIEW, MBN_HOME_PAGE );
      startActivity( browserIntent );
   }
   
   @Override
   public void onPrepared( MediaPlayer player ) {
      updatePlayPauseUI();
   }

   private void updatePlayPauseUI() {
      View loadingStreamProgress = findViewById( R.id.loadingStreamProgressBar );
      loadingStreamProgress.setVisibility( View.GONE );
      mPlayPauseButton.setEnabled( true );
      
      if( mRadioPlayer.isPlaying() ) {
         mPlayPauseButton.setBackgroundResource( R.drawable.stop_button );
         mPlayPauseButton.setImageResource( R.drawable.stop );
      }
      else {
         mPlayPauseButton.setBackgroundResource( R.drawable.play_button );
         mPlayPauseButton.setImageResource( R.drawable.play );
      }
   }

   @Override
   public void onLoaded( final Advertisement ad ) {
      final ImageView adImage = (ImageView)findViewById( R.id.advertisement_image );
      
      Runnable downloadAndUpdateAdImage = new Runnable() {
         @Override
         public void run() {
            final Bitmap adBitmap = downloadBitmapFromUrl( ad.mBannerImageUri );
            
            runOnUiThread( new Runnable() {
               @Override
               public void run() {
                  if( adBitmap != null ) {
                     adImage.setImageBitmap( adBitmap );
                  }
               }
            });
         }
      };
      
      new Thread( downloadAndUpdateAdImage ).start();
      
      if( mRadioPlayer != null ) {
         mRadioPlayer.setCurrentRadioProgram( ad.mCurrentRadioProgram );
      }
      
      mAdvertisementUri = ad.mTargetUri;
   }

   public void onClickAdvertisement( View v ) {
      Intent launchAdvertisement = new Intent( Intent.ACTION_VIEW, mAdvertisementUri );
      startActivity( launchAdvertisement );
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
      
   }
}
