package com.michiganbusinessnetwork.radio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class StartupAdActivity extends Activity {

   Uri mAdvertisementUri;

   @Override
   public void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.ad_screen );

      mAdvertisementUri = Uri.parse( "http://www.google.com" );
   }

   public void onClickViewAd( View v ) {
      RadioPlayerActivity.launch( this );
      
      Intent browserIntent = new Intent( Intent.ACTION_VIEW, mAdvertisementUri );
      startActivity( browserIntent );
      
      finish();
   }

   public void onDismissAd( View v ) {
      RadioPlayerActivity.launch( this );
      
      finish();
   }
}