package fxelements;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class SingletonProcess
{
    private static SingletonProcess INSTANCE;

    private ProgressBar progressBar;
    private ProgressIndicator progressIndicator;


    private SingletonProcess(){}

    public static SingletonProcess getInstance()
    {
        if (INSTANCE == null) {
            synchronized (SingletonProcess.class) {
                if(INSTANCE == null){
                    INSTANCE = new SingletonProcess();
                }
            }
        }
        return INSTANCE;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public void setProgressIndicator(ProgressIndicator progressIndicator) {
        this.progressIndicator = progressIndicator;
    }
}
