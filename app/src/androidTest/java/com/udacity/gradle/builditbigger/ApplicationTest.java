package com.udacity.gradle.builditbigger;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.concurrent.CountDownLatch;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private static final int COUNT_LATCH = 1;

    private String mJoke = "";
    private CountDownLatch mSignal = null;
    private EndpointAsyncTask mTask = null;

    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        mSignal = new CountDownLatch(COUNT_LATCH);
        mTask = new EndpointAsyncTask();
    }

    @Override
    protected void tearDown() throws Exception {
        mSignal.countDown();
    }

    public void testGetJokeFromGCE() throws InterruptedException {
        mTask.execute(getContext());
        mTask.setListener(new EndpointAsyncTask.EndpointAsyncTaskListener() {
            @Override
            public void onCompleted(String joke) {
                mJoke = joke;
                mSignal.countDown();
            }
        });

        mSignal.await();
        assertEquals("This is a tiny little joke", mJoke);
    }

}