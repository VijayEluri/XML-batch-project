package com.browsexml.core;


public interface ErrorHandler {
    public abstract void noErrors();
    public abstract void viewErrors(boolean console, String error);
}
