package com.michiganbusinessnetwork.radio;

import android.net.Uri;

public class Advertisement {
   public interface OnLoadedCallback {
      void onLoaded( Advertisement ad );
      void onError( Exception e );
   }
   
   public static final String MI_BUSINESS_AD_FEED = "http://player.michiganbusinessnetwork.com/mobile_feed/";

   public Uri mFullscreenImageUri;
   public Uri mBannerImageUri;
   public Uri mTargetUri;
   
   public String mCurrentRadioProgram;
   
   private Advertisement() { }
   
   public static void loadAsync( String feedUrl, OnLoadedCallback callback ) {
      AdvertisementWebLoader loader = new AdvertisementWebLoader( callback );
      loader.execute( feedUrl, new Advertisement() );
   }
}
