package com.michiganbusinessnetwork.radio;

import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class RadioService extends Service implements MediaPlayer.OnPreparedListener, OnAudioFocusChangeListener {

   private int RADIO_NOTIFICATION = 0x1;
   
   private MediaPlayer mRadioPlayer = new MediaPlayer();
   private boolean mIsPrepared = false, mShouldResumeOnAudioFocusGain = false;
   private MediaPlayer.OnPreparedListener mListener;
   private String mCurrentRadioProgramTitle;
   
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
      if ( intent != null ) {
         try {
            XLog.d( this, "Starting service\nPreparing player with URI: %s", intent.getDataString() );
            mRadioPlayer.setOnPreparedListener( this );
            mRadioPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
            mRadioPlayer.setDataSource( intent.getDataString() );
            mRadioPlayer.prepareAsync();
         } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
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

   public void setCurrentRadioProgram( String programName ) {
      mCurrentRadioProgramTitle = programName;
   }
   
   public String getCurrentRadioProgram() {
      return mCurrentRadioProgramTitle;
   }
   
   public void play() {
      if( mIsPrepared ) {
         AudioManager manager = (AudioManager) getSystemService( Activity.AUDIO_SERVICE );
         manager.requestAudioFocus( this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN );
         
         showCurrentRadioStationNotification();
         
         mRadioPlayer.start();
      }
   }
   
   private void showCurrentRadioStationNotification() {
      Builder builder = new NotificationCompat.Builder( this )
              .setOngoing( true )
              .setColor( getResources().getColor( R.color.primary ) )
              .setTicker( mCurrentRadioProgramTitle )
              .setContentTitle( getString( R.string.app_name ) )
              .setContentText( mCurrentRadioProgramTitle )
              .setWhen( System.currentTimeMillis() )
              .setContentIntent( PendingIntent.getActivity( this, 0, new Intent( this, RadioPlayerActivity.class ), 0 ) )
              .setSmallIcon( R.drawable.ic_stat_radio_active );
      
      startForeground( RADIO_NOTIFICATION, builder.build() );
  }
   
   public void stop() {
      if( isPlaying() ) {
         pausePlayer();
      }
      
      AudioManager manager = (AudioManager) getSystemService( Activity.AUDIO_SERVICE );
      manager.abandonAudioFocus( this );
   }
   
   private void pausePlayer() {
      mRadioPlayer.pause();
      
      stopForeground( true );
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
            
            pausePlayer();
         }
      }
   }
}
