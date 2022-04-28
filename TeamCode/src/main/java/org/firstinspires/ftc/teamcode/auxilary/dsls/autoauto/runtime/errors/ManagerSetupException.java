package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors;

public class ManagerSetupException extends RuntimeException {
    private final String msg;

    public ManagerSetupException(String s) {
        this.msg = s;
    }
    public String toString() {
        return msg;
    }
}
