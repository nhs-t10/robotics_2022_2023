package org.firstinspires.ftc.teamcode.managers.input.buttonhandles;

public abstract class ButtonHandle {
    /**
     * Get the current button's value. If it's a joystick or number, this will return the plain value. If it's a simple on/off button, this will return 1 for {@code true} and 0 for {@code false}.
     * @return The value of the ButtonHandle's button
     */
    public abstract float get();
}
