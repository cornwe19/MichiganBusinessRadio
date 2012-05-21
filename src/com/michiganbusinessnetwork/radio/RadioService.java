package com.michiganbusinessnetwork.radio;

import java.io.IOException;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class RadioService extends Service implements MediaPlayer.OnPreparedListener, OnAudioFocusChangeListener {

   private MediaPlayer mRadioPlayer = new MediaPlayer();
   private boolean mIsPrepared = false, mShouldResumeOnAudioFocusGain = false;
   private MediaPlayer.OnPreparedListener mListener;
   
   public class RadioServiceBinder extends Binder {
      public RadioService getService() {
         return RadioService.this;
      }
   }
   
   public void setOnPreparedListener( MediaPlayer.OnPreparedListener listener ) {
      mListener = listener;
   }
   
   @Override
   public int onStartCommand( Intent intent, int flags, int startId ) {
      try {
         XLog.d( this, "Starting service\nPreparing player with URI: %s", intent.getDataString() );
         mRadioPlayer.setOnPreparedListener( this );
         mRadioPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
         mRadioPlayer.setDataSource( intent.getDataString() );
         mRadioPlayer.prepareAsync();
      }
      catch ( IOException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return START_STICKY;
   }
   
   private IBinder mServiceBinder = new RadioServiceBinder();

   public MediaPlayer getMediaPlayer() {
      return mRadioPlayer;
   }
   
   @Override
   public IBinder onBind( Intent intent ) {
      return mServiceBinder;
   }
   
   public boolean isPrepared() {
      return mIsPrepared;
   }

   public void play() {
      if( mIsPrepared ) {
         AudioManager manager = (AudioManager) getSystemService( Activity.AUDIO_SERVICE );
         manager.requestAudioFocus( this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN );
         mRadioPlayer.start();
      }
   }
   
   public void stop() {
      if( isPlaying() ) {
         mRadioPlayer.pause();
      }
      
      AudioManager manager = (AudioManager) getSystemService( Activity.AUDIO_SERVICE );
      manager.abandonAudioFocus( this );
   }
   
   public boolean isPlaying() {
      return mRadioPlayer.isPlaying();
   }
   
   @Override
   public void onPrepared( MediaPlayer mp ) {
      if( mListener != null ) {
         mListener.onPrepared( mp );
      }
      
      mIsPrepared = true;
   }

   @Override
   public void onAudioFocusChange( int event ) {
      switch( event ) {
      case AudioManager.AUDIOFOCUS_GAIN:
      case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
         if( isPrepared() && mShouldResumeOnAudioFocusGain ) {
            play();
         }
         break;
      case AudioManager.AUDIOFOCUS_LOSS:
         if ( isPlaying() ) {
            mShouldResumeOnAudioFocusGain = true;
            
            mRadioPlayer.pause();
         }
      }
   }
}
