package edu.byu.cs.tweeter.client.model.service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;

public abstract class SuperService {
    public SuperService() {}

    protected void executeTask(Runnable task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }
    protected void getBackgroundTaskUtils(Runnable runnable) {
         BackgroundTaskUtils.runTask(runnable);
    }

}
