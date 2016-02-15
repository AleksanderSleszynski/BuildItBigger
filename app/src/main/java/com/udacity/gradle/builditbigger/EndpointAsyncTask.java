package com.udacity.gradle.builditbigger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.julain.myapplication.backend.myApi.MyApi;
import com.example.julian.androidjokes.JokeActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

public class EndpointAsyncTask extends AsyncTask<Context, Void, String>{
    private static MyApi myApiService = null;
    private EndpointAsyncTaskListener mListener = null;
    private Context mContext;
    private ProgressBar mProgressBar = null;

    @Override
    protected String doInBackground(Context... contexts) {
        if(myApiService == null){
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            myApiService = builder.build();
        }

        mContext = contexts[0];

        try {
            return myApiService.sayJoke().execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public EndpointAsyncTask(Activity activity){
        super();

        if(activity != null){
            mProgressBar = (ProgressBar) activity.findViewById(R.id.progress_bar);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(mProgressBar != null){
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(mProgressBar != null){
            mProgressBar.setVisibility(View.GONE);
        }

        if(mListener != null) {
            mListener.onCompleted(s);
        } else {
            Intent intent = new Intent(mContext, JokeActivity.class);
            intent.putExtra(JokeActivity.EXTRA_KEY_JOKE, s);
            mContext.startActivity(intent);
        }
    }

    public EndpointAsyncTask setListener(EndpointAsyncTaskListener listener) {
        mListener = listener;
        return this;
    }

    public interface EndpointAsyncTaskListener {
        void onCompleted(String joke);
    }
}
