package com.michiganbusinessnetwork.radio;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class RadioPlayerActivity extends Activity implements OnPreparedListener, Advertisement.OnLoadedCallback {

   public static void launch( Context context ) {
      Intent thisIntent = new Intent( context, RadioPlayerActivity.class );
      context.startActivity( thisIntent );
   }

   private static final String MBN_STREAM_URL = "http://radio.michiganbusinessnetwork.com:8000/;stream.nsv";
   private static final Uri MBN_HOME_PAGE = Uri.parse( "http://www.michiganbusinessnetwork.com/" );
   
   private MediaPlayer mPlayer = null;
   private ImageButton mPlayPauseButton;
   private Uri mAdvertisementUri;
   
   @Override
   public void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      
      setContentView( R.layout.radio_player );
      
      mPlayPauseButton = (ImageButton)findViewById( R.id.playPauseButton );
      mPlayPauseButton.setEnabled( false );
      
      MediaPlayer player = new MediaPlayer();
      try {
         player.setAudioStreamType( AudioManager.STREAM_MUSIC );
         player.setDataSource( MBN_STREAM_URL );
         player.setOnPreparedListener( this );
         player.prepareAsync();
      }
      catch ( IOException e ) {
         Log.e( "RadioPlayer", "Failed to initialize media mPlayer" );
         e.printStackTrace();
      }
      
      Advertisement.loadAsync( Advertisement.MI_BUSINESS_AD_FEED, this );
   }
   
   public void onClickPlayPause( View v ) {
      if( mPlayer != null ) {
         AudioManager manager = (AudioManager) getSystemService( Activity.AUDIO_SERVICE );
         if ( mPlayer.isPlaying() ) {
            manager.abandonAudioFocus( null );
            mPlayer.pause();
            mPlayPauseButton.setImageResource( R.drawable.play );
            mPlayPauseButton.setBackgroundResource( R.drawable.play_button );
         }
         else {
            manager.requestAudioFocus( null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN );
            mPlayer.start();
            mPlayPauseButton.setImageResource( R.drawable.stop );
            mPlayPauseButton.setBackgroundResource( R.drawable.stop_button );
         }
      }
   }

   public void onClickLaunchMBNWebsite( View view ) {
      Intent browserIntent = new Intent( Intent.ACTION_VIEW, MBN_HOME_PAGE );
      startActivity( browserIntent );
   }
   
   @Override
   public void onPause() {
      super.onPause();
      
      if ( mPlayer != null ) {
         if ( mPlayer.isPlaying() ) {
            mPlayer.stop();
         }
         
         mPlayer.reset();
         mPlayer.release();
      }
   }

   @Override
   public void onPrepared( MediaPlayer player ) {
      View loadingStreamProgress = findViewById( R.id.loadingStreamProgressBar );
      loadingStreamProgress.setVisibility( View.GONE );
      mPlayPauseButton.setEnabled( true );
      mPlayPauseButton.setImageResource( R.drawable.play );
      
      mPlayer = player;
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
