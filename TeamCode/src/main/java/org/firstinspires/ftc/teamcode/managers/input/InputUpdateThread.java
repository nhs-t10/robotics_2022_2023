package org.firstinspires.ftc.teamcode.managers.input;

import android.graphics.drawable.Animatable;

import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.WebServer;

import org.firstinspires.ftc.robotcore.internal.network.DeviceNameManager;
import org.firstinspires.ftc.robotserver.internal.webserver.websockets.FtcWebSocketServer;
import org.firstinspires.ftc.teamcode.auxilary.clocktower.Clocktower;
import org.firstinspires.ftc.teamcode.auxilary.clocktower.ClocktowerCodes;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.nodes.InputManagerInputNode;

public class InputUpdateThread extends Thread {
    private final InputManagerInputNode node;

    public InputUpdateThread(InputManagerInputNode node) {
        this.node = node;
    }

    public void run() {
        try {
            while(FeatureManager.isOpModeRunning) {
                node.update();
                Clocktower.time(ClocktowerCodes.INPUT_NODE_UPDATER_THREAD);
                yield();
            }
        } catch(Throwable t) {
            FeatureManager.logger.log("Silent error in 'InputUpdateThread'");
        }
    }
}
