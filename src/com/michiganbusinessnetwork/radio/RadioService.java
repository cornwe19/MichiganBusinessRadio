package com.michiganbusinessnetwork.radio;

import java.io.IOException;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class RadioService extends Service implements MediaPlayer.OnPreparedListener {

   private MediaPlayer mRadioPlayer = new MediaPlayer();
   private boolean mIsPrepared = false;
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
         mRadioPlayer.start();
      }
   }
   
   public void stop() {
      if( isPlaying() ) {
         mRadioPlayer.pause();
      }
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
}
