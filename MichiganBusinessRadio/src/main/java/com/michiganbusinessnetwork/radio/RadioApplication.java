package com.michiganbusinessnetwork.radio;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

public class RadioApplication extends Application {
   
   @Override
   public void onCreate() {
      super.onCreate();
      
      Intent radioService = new Intent( this, RadioService.class );
      radioService.setData( Uri.parse( RadioPlayerActivity.MBN_STREAM_URL ) );
      
      startService( radioService );
   }
   
}
