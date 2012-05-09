package com.michiganbusinessnetwork.radio;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class RadioPlayerActivity extends Activity implements OnPreparedListener {

   public static void launch( Context context ) {
      Intent thisIntent = new Intent( context, RadioPlayerActivity.class );
      context.startActivity( thisIntent );
   }

   private static final String MBN_STREAM_URL = "http://radio.michiganbusinessnetwork.com:8000/;stream.nsv";
   
   private MediaPlayer mPlayer = null;
   private ImageButton mPlayPauseButton;
   
   @Override
   public void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      
      setContentView( R.layout.radio_player );
      
      mPlayPauseButton = (ImageButton)findViewById( R.id.playPauseButton );
      
      MediaPlayer player = new MediaPlayer();
      try {
         player.setDataSource( MBN_STREAM_URL );
         player.setOnPreparedListener( this );
         player.prepareAsync();
      }
      catch ( IOException e ) {
         Log.e( "RadioPlayer", "Failed to initialize media mPlayer" );
         e.printStackTrace();
      }
   }
   
   public void onClickPlayPause( View v ) {
      if( mPlayer != null ) {
         if ( mPlayer.isPlaying() ) {
            mPlayer.pause();
            mPlayPauseButton.setImageResource( android.R.drawable.ic_media_play );
         }
         else {
            mPlayer.start();
            mPlayPauseButton.setImageResource( android.R.drawable.ic_media_pause );
         }
      }
   }
   
   @Override
   public void onStop() {
      super.onStop();
      
      if ( mPlayer.isPlaying() ) {
         mPlayer.stop();
         mPlayer.release();
      }
   }

   @Override
   public void onPrepared( MediaPlayer player ) {
      View loadingStreamProgress = findViewById( R.id.loadingAudioStreamProgress );
      loadingStreamProgress.setVisibility( View.GONE );
      
      player.setAudioStreamType( AudioManager.STREAM_MUSIC );
      mPlayer = player;
   }
}
