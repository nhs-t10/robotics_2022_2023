package org.firstinspires.ftc.teamcode.managers.roadrunner;

public class SequenceInitException extends Throwable {
    public SequenceInitException(String s) throws Exception {
        throw new Exception("Sequence Initialization Error: "+s);

    }
    public SequenceInitException(String s, int index) throws Exception {
        throw new Exception("Sequence Initialization Error at Trajectory: "+s);

    }
}
