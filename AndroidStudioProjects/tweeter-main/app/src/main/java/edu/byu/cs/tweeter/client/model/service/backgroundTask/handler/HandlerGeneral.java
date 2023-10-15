package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;

public class HandlerGeneral extends Handler {


    public HandlerGeneral(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY);

        if (success) {
            handleSuccess(msg);
        } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(BackgroundTask.MESSAGE_KEY);
            handleFailure("Failed: " + message);
        } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(BackgroundTask.EXCEPTION_KEY);
            handleFailure("Failed because of exception: " + ex.getMessage());
        }
    }

    protected void handleSuccess(Message msg) {
        // Override this method in subclasses if needed
    }

    protected void handleFailure(String message) {
        // Override this method in subclasses if needed
    }

    protected void handleException(Exception ex) {
        // Override this method in subclasses if needed
    }


}
