package org.firstinspires.ftc.teamcode.auxilary.integratedasync;

import org.firstinspires.ftc.teamcode.auxilary.clocktower.Clocktower;
import org.firstinspires.ftc.teamcode.auxilary.clocktower.ClocktowerCodes;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

public class PriorityAsyncOpmodeComponent {
    public static void start(Runnable r) {
        (new AsyncRunnerThread(r)).start();
    }
    private static class AsyncRunnerThread extends Thread {
        private final Runnable r;

        public AsyncRunnerThread(Runnable r) {
            this.r = r;
        }

        @Override
        public void run() {
            try {
                while (FeatureManager.isOpModeRunning) {
                    r.run();
                    Thread.yield();
                    Clocktower.time(ClocktowerCodes.ASYNC_OPMODE_COMPONENT);
                }
            } catch(Throwable t) {
                FeatureManager.logger.log("Silent error in 'PriorityAsyncOpmodeComponent'");
            }
        }
    }
}
