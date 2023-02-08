package org.firstinspires.ftc.teamcode.managers.input.nodes;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

public class InversionNode extends InputManagerInputNode {
    private final InputManagerInputNode input;
    private InputManagerInputNode multiplier;
    private final InputManagerNodeResult result = new InputManagerNodeResult();
    private boolean inverted = false;

    /**
     * Multiplies one number by another.
     * @param input The input listed in the MultiplyNode.
     * @param inverted An input that determines whether or not the input should be inverted.
     * @see MinusNode#MinusNode(InputManagerInputNode, InputManagerInputNode) MinusNode
     */
    public InversionNode(InputManagerInputNode input, InputManagerInputNode inverted) {
        this.inverted = inverted.getResult().getBool();
        this.input = input;
    }

    @Override
    public void init(InputManager boss) {
        input.init(boss);
        multiplier.init(boss);
    }

    public void update() {
        input.update();
        multiplier.update();

        float[] f = input.getResult().getFloatArray();
        float[] res = new float[f.length];
        if (inverted) {
            for (int i = 0; i < res.length; i++) {
                res[i] = f[i] * -1;
            }
        }
        result.setFloatArray(res);
    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        return result;
    }

    @Override
    public int complexity() {
        return input.complexity() + multiplier.complexity() + 1;
    }

    @Override
    public String[] getKeysUsed() {
        return PaulMath.concatArrays(input.getKeysUsed(), multiplier.getKeysUsed());
    }

    @Override
    public boolean usesKey(String s) {
        if (input.usesKey(s) || multiplier.usesKey(s)){
            return true;
        }
        return false;
    }
}
