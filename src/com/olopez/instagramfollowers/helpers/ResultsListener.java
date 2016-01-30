package com.olopez.instagramfollowers.helpers;

public interface ResultsListener<T> {

    public void onSuccess(T result);
    public void onFailure(Throwable e);
    
}