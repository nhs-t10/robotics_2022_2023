package org.firstinspires.ftc.teamcode.managers.input.nodes;

import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

import androidx.annotation.NonNull;

public class InputVariableNode extends InputManagerInputNode {
    private final String varname;
    private InputManager boss;

    private final InputManagerNodeResult result = new InputManagerNodeResult();

    public InputVariableNode(String s) {
        this.varname = s;
    }

    @Override
    public void init(InputManager boss) {
        this.boss = boss;
    }

    @Override
    public void update() {
        this.result.setFloat(boss.getInputVariable(varname));
    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        return result;
    }

    @Override
    public int complexity() {
        return 0;
    }

    @Override
    public String[] getKeysUsed() {
        return new String[0];
    }

    @Override
    public boolean usesKey(String s) {
        return false;
    }
}
