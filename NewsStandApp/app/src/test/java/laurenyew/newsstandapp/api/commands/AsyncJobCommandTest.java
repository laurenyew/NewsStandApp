package laurenyew.newsstandapp.api.commands;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import laurenyew.newsstandapp.BuildConfig;

import static org.junit.Assert.assertNull;

/**
 * @author Lauren Yew on 5/9/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AsyncJobCommandTest {
    private AsyncJobCommand command;

    @Before
    public void setup() {
        command = Mockito.spy(new MyAsyncJobCommand());
    }

    @After
    public void teardown() {
        command.cancel();
        command = null;
    }

    /**
     * Given command is cancelled, the job thread should be interrupted`
     */
    @Test
    public void testCommandCancelledThenJobThreadShouldBeInterrupted() {
        /** Arrange **/
        command.execute();

        /** Exercise **/
        command.cancel();

        /** Verify **/
        Thread jobThread = command.job;
        assertNull(jobThread);
    }

    //region Helper Classes
    private class MyAsyncJobCommand extends AsyncJobCommand {
        @Override
        void executeCommandImpl() {
            //Do nothing
        }
    }
    //endregion
}