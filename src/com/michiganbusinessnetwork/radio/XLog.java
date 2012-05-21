package com.michiganbusinessnetwork.radio;

import android.util.Log;

public class XLog {
   static void d( Object obj, String format, Object... args ) {
      Log.d( obj.getClass().getName(), String.format( format, args ) );
   }
}
