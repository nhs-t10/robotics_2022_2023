package org.firstinspires.ftc.teamcode.managers.input.nodes;

import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

import androidx.annotation.NonNull;

public class ToggleNode extends InputManagerInputNode {
    private final InputManagerInputNode node;

    private boolean wasPressed;
    private boolean toggledOn;
    private final InputManagerNodeResult result = new InputManagerNodeResult();

    /**
     * Turns everything inside this node into a toggle. <br>
     * When the button is pressed, it toggles on and will remain acting like it is held until the button is pressed again.
     * @param node The input contained within the ToggleNode
     */
    public ToggleNode(InputManagerInputNode node) {
        this.node = node;
    }

    @Override
    public void init(InputManager boss) {
        node.init(boss);
    }

    @Override
    public void update() {
        node.update();
        boolean isPressed = node.getResult().getBool();
        if(isPressed && wasPressed == false) {
            toggledOn = !toggledOn;
        }
        wasPressed = isPressed;
    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        result.setBool(toggledOn);
        return result;
    }

    @Override
    public int complexity() {
        return node.complexity() + 1;
    }

    @Override
    public String[] getKeysUsed() {
        return node.getKeysUsed();
    }

    @Override
    public boolean usesKey(String s) {
        return node.usesKey(s);
    }
}
