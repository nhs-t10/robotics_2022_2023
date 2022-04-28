package org.firstinspires.ftc.teamcode.managers.input.nodes;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.RobotTime;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

import androidx.annotation.NonNull;

public class ComboNode extends InputManagerInputNode {
    InputManagerInputNode[] conditionals;

    private final InputManagerNodeResult result = new InputManagerNodeResult();
    private long[] risingEdgeTime;
    private long iterId = Long.MIN_VALUE;
    private boolean comboMatch;
    private InputManagerNodeResult lastChildResult;

    /**
     * Detects if an button combo is being pressed.
     * The ComboNode returns true if and only if the buttons are pressed in the order that they are declared in the ComboNode. If they are pressed in any other order, or they are not all pressed, it will return false<br>
     * If you do not want a specific order of inputs, I recommend using an {@link AllNode#AllNode(InputManagerInputNode...) AllNode} instead, which doesn't care about order. <br>
     * @param conditionals An array of the inputs included in this ComboNode
     */
    public ComboNode(InputManagerInputNode... conditionals) {
        this.conditionals = conditionals;

        risingEdgeTime = new long[conditionals.length];
    }
    @Override
    public void init(InputManager boss) {
        for(InputManagerInputNode i : conditionals) i.init(boss);
    }

    @Override
    public void update() {
        InputManagerNodeResult lastResult = null;
        boolean comboMatch = true;

        for(int i = 0; i < conditionals.length; i++) {
            InputManagerInputNode node = conditionals[i];
            node.update();

            InputManagerNodeResult r = node.getResult();
            boolean rBool = r != null && r.getBool();

            if(!rBool) risingEdgeTime[i] = Long.MIN_VALUE;
            else if(risingEdgeTime[i] == Long.MIN_VALUE) risingEdgeTime[i] = iterId;


            if(rBool) lastResult = r;

            //check if the list of times is sorted
            if(risingEdgeTime[i] == Long.MIN_VALUE || (i > 1 && risingEdgeTime[i] <= risingEdgeTime[i - 1])) {
                comboMatch = false;
            }
        }
        this.comboMatch = comboMatch;
        this.lastChildResult = lastResult;
        this.iterId++;
    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        if(comboMatch && lastChildResult != null) {
            this.result.copyValuesFrom(lastChildResult);
        } else {
            this.result.setBool(false);
        }

        return result;
    }

    @Override
    public int complexity() {
        int r = 0;
        for(InputManagerInputNode n : conditionals) r += n.complexity();
        return r + 1;
    }

    @Override
    public String[] getKeysUsed() {
        String[][] keylists = new String[conditionals.length][];
        for(int i = 0; i < conditionals.length; i++) {
            keylists[i] = conditionals[i].getKeysUsed();
        }
        return PaulMath.concatArrays(keylists);
    }

    @Override
    public boolean usesKey(String s) {
        for(InputManagerInputNode n : conditionals) {
            if(n.usesKey(s)) return true;
        }
        return false;
    }
}
