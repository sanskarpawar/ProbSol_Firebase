package com.example.lap.stschat;



import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.app.ProgressDialog;
import android.widget.ProgressBar;
import android.graphics.Bitmap;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

import static android.content.res.Resources.getSystem;


/**
 * A simple {@link Fragment} subclass.
 */
public class AchivementFragment extends Fragment {
    //Object main_tabs_pager;


    public AchivementFragment() {
        // Required empty public constructor
    }

    public WebView mwebView;
    ProgressBar bar;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    //String url ="https://www.pdfdrive.com/";
    //make HTML upload button work in Webview


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_achivement, null);
        bar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) rootView.findViewById(R.id.main_swipe);
        // initialize bar
        final MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.refresh);

        mwebView = (WebView) rootView.findViewById(R.id.webview);
        mwebView.loadUrl("https://www.pdfdrive.com/");

        mwebView.getSettings().setJavaScriptEnabled(true);

        mwebView.setWebViewClient(new MyWebViewClient());




        mwebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
            }
        });
        mwebView.setOnKeyListener(new View.OnKeyListener(){

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && mwebView.canGoBack())
                {
                    handler.sendEmptyMessage(1);

                    return true;
                }

                return false;
            }

        });
        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.BLACK);
       // mWaveSwipeRefreshLayout.setColorSchemeResources();

        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh() {
                // Do work to refresh the list here.
                mwebView.reload();
                new Taska().execute();
                mp.start();
            }
        });



        return rootView;


    }
    private void webViewGoBack(){
        mwebView.goBack();
    }

    private class Taska extends AsyncTask<Void , Void ,String[]>
    {
        @Override
        protected String[] doInBackground(Void... voids)
        {
            return new String[0];
        }

        @Override protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }

    private class MyWebViewClient extends WebViewClient
    {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
            bar.setVisibility(View.VISIBLE);
            // ^^^ use it as it is

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            bar.setVisibility(View.GONE);
            // ^^^ use it as it is
            super.onPageFinished(view, url);


        }


    }


}



