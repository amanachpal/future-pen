package com.example.smartpen;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {
    
	TextView text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int w = size.x;
        final int h = size.y;
        
		LinearLayout mrl = new LinearLayout(this);
		WebView wv = new WebView(this);
		mrl.addView(wv);
		wv.loadUrl("http://192.168.43.6/uploads/image.png");
		LinearLayout.LayoutParams lp = (LayoutParams) wv.getLayoutParams();
		lp.height=3*h/4;
		WebView wv2 = new WebView(this);
		wv2.loadUrl("http://192.168.43.6/uploads/output.txt");
		mrl.addView(wv2);
		setContentView(mrl);
	}
  }
