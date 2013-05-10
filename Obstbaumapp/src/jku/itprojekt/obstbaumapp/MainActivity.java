package jku.itprojekt.obstbaumapp;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.GeolocationPermissions;

public class MainActivity extends Activity {

	WebView mWebView; // Webview which handles the main app
	TextView txterror;
	Button btnerror;
	String url;
	ProgressBar pbProgress;

	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pbProgress = (ProgressBar) findViewById(R.id.pbProgress1);

		startBrowser();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		// url = "http://linz.pflueckt.at/";
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.it_neuLaden:
			reloadPage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void reloadPage() {
		mWebView.loadUrl("http://linz.pflueckt.at/");
	}

	public void setSettings() {

		btnerror = (Button) findViewById(R.id.button1);
		btnerror.setVisibility(View.GONE);
		btnerror.setText("Neu laden");

		mWebView = (WebView) findViewById(R.id.webView1);
		// allow Javascript and Geolocation
		mWebView.getSettings().setJavaScriptEnabled(true);
		// mWebView.getSettings().setGeolocationEnabled(true);
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.getSettings().setDatabaseEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
					GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
			}
			
			//openFileChooser Methoden werden als unused gekennzeichnet, funktionieren aber
			// For Android 3.0+
	        @SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {  

	            mUploadMessage = uploadMsg;  
	            Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
	            i.addCategory(Intent.CATEGORY_OPENABLE);  
	            i.setType("image/*");  
	            MainActivity.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);  

	           }

	        // For Android 3.0+
	           @SuppressWarnings("unused")
			public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
	           mUploadMessage = uploadMsg;
	           Intent i = new Intent(Intent.ACTION_GET_CONTENT);
	           i.addCategory(Intent.CATEGORY_OPENABLE);
	           i.setType("*/*");
	           MainActivity.this.startActivityForResult(
	           Intent.createChooser(i, "File Browser"),
	           FILECHOOSER_RESULTCODE);
	           }

	        //For Android 4.1
	           @SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
	               mUploadMessage = uploadMsg;  
	               Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
	               i.addCategory(Intent.CATEGORY_OPENABLE);  
	               i.setType("image/*");  
	               MainActivity.this.startActivityForResult( Intent.createChooser( i, "File Chooser" ), MainActivity.FILECHOOSER_RESULTCODE );

	           }

		});
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				pbProgress.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				pbProgress.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {

				Toast.makeText(MainActivity.this, description,
						Toast.LENGTH_SHORT);
				// view.loadData("Keine Verbindung vorhanden",
				// "text/html","utf-8");
				mWebView.loadData(
						"<p><h2>Keine Internetverbindung</h2></p>"
								+ "<p><h3>Derzeit kann nicht auf die Applikation zugegriffen werden.</h3></p>"
								+ "<p><h4>Hier einige Tipps:</h4></p>"
								+ "<p><li>Stellen Sie sicher, dass sie mit dem Wlan verbunden sind.</li></p>"
								+ "<p><li>Versuchen Sie es sp&auml;ter erneut.</li></p>",
						"text/html", "utf-8");

				btnerror.setVisibility(View.VISIBLE);
				btnerror.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						reloadPage();
						btnerror.setVisibility(View.GONE);
						// showToast(MainActivity.this, "click",
						// Toast.LENGTH_SHORT);
						// ((WebView) v).loadUrl("http://obst.linzwiki.at/");
					}
				});
			}

		});
	}

	/**
	 * starting the Browser //main input
	 */
	public void startBrowser() {
		setSettings();
		reloadPage();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (mWebView.canGoBack())
			mWebView.goBack();
		else
			finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != RESULT_OK ? null
					: intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;

		}
	}
}
