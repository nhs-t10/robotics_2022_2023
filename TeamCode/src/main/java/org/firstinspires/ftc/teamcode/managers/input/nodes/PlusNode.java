package org.firstinspires.ftc.teamcode.managers.input.nodes;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

import androidx.annotation.NonNull;

public class PlusNode extends InputManagerInputNode {
    private final InputManagerInputNode input1;
    private final InputManagerInputNode input2;
    private final InputManagerNodeResult result = new InputManagerNodeResult();

    /**
     * Combines two values or two arrays. <br>
     * Array example: The value at index 0 in array 1 + the value of index 0 in array 2, is placed into index 0 of array 3. Array 3 is returned from PlusNode. <br>
     * If there is no value at a certain index in one of the arrays, it defaults to 0
     * @param input1 The first input listed in the PlusNode
     * @param input2 The second input listed in the PlusNode
     * @see MinusNode#MinusNode(InputManagerInputNode, InputManagerInputNode) MinusNode
     */
    public PlusNode(InputManagerInputNode input1, InputManagerInputNode input2) {
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
            if(i < input2Vals.length) combinedVals[i] += input2Vals[i];
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
