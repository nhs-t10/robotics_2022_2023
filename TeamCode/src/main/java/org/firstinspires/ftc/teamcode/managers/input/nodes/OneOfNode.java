package org.firstinspires.ftc.teamcode.managers.input.nodes;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

import androidx.annotation.NonNull;

public class OneOfNode extends InputManagerInputNode {
    private final InputManagerInputNode[] inputs;
    private InputManager boss;

    private final InputManagerNodeResult result = new InputManagerNodeResult();

    /**
     * Takes multiple inputs and returns the value of the first nonzero input. <br>
     * Example: The OneOfNode has the B button, X button, and the left stick X direction declared in that order. <br>
     * The B button is not pressed, the X button is pressed, and the left stick is held fully left. The OneOfNode returns 1, as the X button is pressed. <br>
     * The B button is not pressed, the X button is also not pressed, and the left stick is held fully left. The OneOfNode returns -1, as the left stick is held fully left. <br>
     * @param inputs The list of inputs included in the OneOfNode
     */
    public OneOfNode(InputManagerInputNode... inputs) {
        this.inputs = inputs;
    }

    @Override
    public void init(InputManager boss) {
        this.boss = boss;
        for(InputManagerInputNode node : inputs) node.init(boss);
    }

    @Override
    public void update() {
        this.boss = boss;
        for(InputManagerInputNode n : inputs) n.update();
    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        InputManagerNodeResult output = InputManagerNodeResult.FALSE;
        for(InputManagerInputNode b : inputs) {
            if(b.getResult().getBool()){
                output = b.getResult();
                break;
            }
        }
        result.copyValuesFrom(output);
        return result;
    }

    @Override
    public int complexity() {
        int r = 0;
        for (InputManagerInputNode n : inputs) r += n.complexity();
        return r + 1;
    }

    @Override
    public String[] getKeysUsed() {
        String[][] keylists = new String[inputs.length][];
        for(int i = 0; i < inputs.length; i++) {
            keylists[i] = inputs[i].getKeysUsed();
        }
        return PaulMath.concatArrays(keylists);
    }

    @Override
    public boolean usesKey(String s) {
        for(InputManagerInputNode n : inputs) {
            if(n.usesKey(s)) return true;
        }
        return false;
    }
}
