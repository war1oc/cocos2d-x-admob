/****************************************************************************
Copyright (c) 2008-2010 Ricardo Quesada
Copyright (c) 2010-2012 cocos2d-x.org
Copyright (c) 2011      Zynga Inc.
Copyright (c) 2013-2014 Chukong Technologies Inc.

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/
package org.cocos2dx.cpp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.war1oc.admobtutorial.R;

public class AppActivity extends Cocos2dxActivity {
	Cocos2dxGLSurfaceView glSurfaceView;

	private static AppActivity instance;

	private AdRequest  adRequest;
	private static AdView adView;
	private boolean adLoaded;
	private boolean adVisible;

	private static String  AD_APPID  =  "";

	private Point getDisplaySize(Display d)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			return getDisplaySizeGE11(d);
		}
		return getDisplaySizeLT11(d);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private Point getDisplaySizeGE11(Display d)
	{
		Point p = new Point(0, 0);
		d.getSize(p);
		return p;
	}
	private Point getDisplaySizeLT11(Display d)
	{
		try
		{
			Method getWidth = Display.class.getMethod("getWidth", new Class[] {});
			Method getHeight = Display.class.getMethod("getHeight", new Class[] {});
			return new Point(((Integer) getWidth.invoke(d, (Object[]) null)).intValue(), ((Integer) getHeight.invoke(d, (Object[]) null)).intValue());
		}
		catch (NoSuchMethodException e2)
		{
			return new Point(-1, -1);
		}
		catch (IllegalArgumentException e2)
		{
			return new Point(-2, -2);
		}
		catch (IllegalAccessException e2)
		{
			return new Point(-3, -3);
		}
		catch (InvocationTargetException e2)
		{
			return new Point(-4, -4);
		}
	}

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		instance = this;

		AD_APPID = getString(R.string.adapp_id);

		adLoaded = false;
		adVisible = false;

		adView = new AdView(this);
		adView.setAdUnitId(AD_APPID);
		adView.setAdSize(AdSize.SMART_BANNER);

		adView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				adLoaded = true;
			}

			@Override
			public void onAdFailedToLoad(int error) {
				adLoaded = false;
			}
		});

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		int width = getDisplaySize(getWindowManager().getDefaultDisplay()).x;

		FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(
				width,
				FrameLayout.LayoutParams.WRAP_CONTENT);

		adParams.gravity = Gravity.TOP | Gravity.CENTER;

		adRequest  =  new  AdRequest.Builder()
		.addTestDevice ( AdRequest . DEVICE_ID_EMULATOR )
		.addTestDevice("Your test device ID")
		.build ();

		adView.loadAd ( adRequest );

		adView.setBackgroundColor(Color.BLACK);

		addContentView(adView,adParams);

		adView.setBackgroundColor(0);
	} 

	public Cocos2dxGLSurfaceView onCreateView() 
	{
		glSurfaceView = new Cocos2dxGLSurfaceView(this);
		glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);

		return glSurfaceView;
	}

	static {
		System.loadLibrary("cocos2dcpp");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() 
	{
		if(adView != null) 
		{
			adView.pause();
		}
		super.onPause();
	}

	@Override 
	public  void  onResume ()  
	{ 
		super.onResume(); 

		if(adView != null) 
		{
			adView.resume();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean focus)
	{
		if(focus)
		{    
			this.glSurfaceView.onResume();
		}
		else
		{
			this.glSurfaceView.onPause();
		}
	}

	@Override
	public  void  onDestroy()  
	{ 
		adView.destroy();
		super.onDestroy();
	}

	public static void showAd() {
		instance._showAd();
	}

	public void _showAd() {
		if(isConnected(getApplicationContext())) {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(adView.getVisibility() == View.INVISIBLE)
					{
						adView.setVisibility(View.VISIBLE);
						adView.bringToFront();

						if(adLoaded != true) 
						{
							adView.loadAd(adRequest);
						}
					}
				}
			});
		}
	}

	public static void hideAd() 
	{
		instance._hideAd();
	}

	public void _hideAd(){
		this.runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				if(adView.getVisibility() != View.INVISIBLE){
					adView.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	/**
	 * Check the network state
	 * @param context context of application
	 * @return true if the phone is connected
	 */
	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
