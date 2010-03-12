package com.browsexml.core;

import javax.swing.JFrame;

public abstract class ErrorHandler extends JFrame {
    public abstract void noErrors();
    public abstract void viewErrors(boolean console, String error);
}
