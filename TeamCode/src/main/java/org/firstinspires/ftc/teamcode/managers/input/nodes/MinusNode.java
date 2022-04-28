package org.firstinspires.ftc.teamcode.managers.input.nodes;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

import androidx.annotation.NonNull;

public class MinusNode extends InputManagerInputNode {
    private final InputManagerInputNode input1;
    private final InputManagerInputNode input2;
    private final InputManagerNodeResult result = new InputManagerNodeResult();

    /**
     * Second array value is always subtracted from the first array value. <br>
     * If the first array is null at a given index, it will be 0 - the second array value. <br>
     * If the second array is null at a given index, it will be the first array value - 0. <br>
     * @param input1 The first input listed in the Minus Node
     * @param input2 The second input listed in the Minus Node (always subtracted from the first input)
     * @see PlusNode#PlusNode(InputManagerInputNode, InputManagerInputNode) PlusNode
     */
    public MinusNode(InputManagerInputNode input1, InputManagerInputNode input2) {
        this.input1 = input1;
        this.input2 = input2;
    }

    @Override
    public void init(InputManager boss) {
        input1.init(boss);
        input2.init(boss);
    }

    public void update() {
        input1.update();
        input2.update();
    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        float[] input1Vals = input1.getResult().getFloatArray();
        float[] input2Vals = input2.getResult().getFloatArray();


        float[] combinedVals = new float[Math.max(input1Vals.length, input2Vals.length)];
        for(int i = 0; i < combinedVals.length; i++) {
            if(i < input1Vals.length) combinedVals[i] += input1Vals[i];
            if(i < input2Vals.length) combinedVals[i] -= input2Vals[i];
        }

        result.setFloatArray(combinedVals);
        return result;
    }

    @Override
    public int complexity() {
        return input1.complexity() + input2.complexity() + 1;
    }

    @Override
    public String[] getKeysUsed() {
        return PaulMath.concatArrays(input1.getKeysUsed(), input2.getKeysUsed());
    }

    @Override
    public boolean usesKey(String s) {
        if (input1.usesKey(s) || input2.usesKey(s)){
            return true;
        }
        return false;
    }
}
