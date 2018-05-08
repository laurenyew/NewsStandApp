package laurenyew.newsstandapp.api.commands;

import android.support.annotation.VisibleForTesting;

/**
 * @author Lauren Yew on 05/07/2018.
 * <p>
 * Base abstract class to run commands
 * <p>
 * Execute will run the command implementation in a background runnable thread
 * It's then up to the command handle update logic / actual logic
 * <p>
 * Splitting up as base class for use in unit tests
 */
public abstract class AsyncJobCommand {
    @VisibleForTesting
    public Thread job = null;

    public void execute() {
        job = new Thread(() -> executeCommandImpl());
        job.start();
    }

    abstract void executeCommandImpl();

    public void cancel() {
        if (job != null) {
            job.interrupt();
            job = null;
        }
    }
}
