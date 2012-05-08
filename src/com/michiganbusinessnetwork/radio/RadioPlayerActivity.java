package com.michiganbusinessnetwork.radio;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

public class RadioPlayerActivity extends Activity {

   public static void launch( Context context ) {
      Intent thisIntent = new Intent( context, RadioPlayerActivity.class );
      context.startActivity( thisIntent );
   }
   
   private MediaPlayer mPlayer;
   
   @Override
   public void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      
      setContentView( R.layout.radio_player );
      
      mPlayer = new MediaPlayer();
      try {
         mPlayer.setDataSource( "http://radio.michiganbusinessnetwork.com:8000/;stream.nsv" );
         mPlayer.prepare();
         mPlayer.start();
      }
      catch ( IOException e ) {
         Log.e( "RadioPlayer", "Failed to initialize media mPlayer" );
         e.printStackTrace();
      }
      mPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
   }
   
   @Override
   public void onStop() {
      super.onStop();
      
      if ( mPlayer.isPlaying() ) {
         mPlayer.stop();
         mPlayer.release();
      }
   }
}
