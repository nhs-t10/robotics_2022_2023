package org.firstinspires.ftc.teamcode.managers.input.nodes;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;
import org.firstinspires.ftc.teamcode.managers.input.buttonhandles.ButtonHandle;

import androidx.annotation.NonNull;

public class ButtonNode extends InputManagerInputNode {
    private ButtonHandle keyHandle;
    private final String key;
    private InputManager boss;
    private final InputManagerNodeResult result = new InputManagerNodeResult();

    /**
     * Takes the name of a specified button on the controller. <br>
     * If the button is pressed, it returns true. If the button is not pressed, it returns false. <br>
     * @param key The name of the button, in any format. It's not picky.
     */
    public ButtonNode(String key) {
        this.key = key;
    }

    @Override
    public void init(InputManager boss) {
        this.keyHandle = boss.getButtonHandle(key);
    }

    @Override
    public void update() {

    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        if(keyHandle == null) {
            FeatureManager.logger.log(key);
        }
        this.result.setFloat(keyHandle.get());
        return result;
    }

    @Override
    public int complexity() {
        return 0;
    }

    @Override
    public String[] getKeysUsed() {
        return new String[] {key};
    }

    @Override
    public boolean usesKey(String s) {
        return key.equals(s);
    }
}
