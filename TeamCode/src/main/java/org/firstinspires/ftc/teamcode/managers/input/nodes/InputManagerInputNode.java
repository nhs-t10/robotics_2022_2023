package org.firstinspires.ftc.teamcode.managers.input.nodes;

import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;
import org.firstinspires.ftc.teamcode.managers.input.InputOverlapResolutionMethod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class InputManagerInputNode {
    public InputManagerInputNode[] overlappingNodes = new InputManagerInputNode[0];

    public abstract void init(InputManager boss);
    public abstract void update();
    @NotNull
    public abstract InputManagerNodeResult getResult();

    public abstract int complexity();
    public abstract String[] getKeysUsed();
    public abstract boolean usesKey(String s);

    public String name;

    public void setName(String name) {
        this.name = name;
    }

    public void updateOverlaps(InputOverlapResolutionMethod newMethod, InputManagerInputNode[] allRootNodes) {
        if(newMethod == InputOverlapResolutionMethod.BOTH_CHILDREN_CAN_SPEAK) {
            //nothing
            this.overlappingNodes = new InputManagerInputNode[0];
        } else {
            String[] keysUsed = getKeysUsed();
            ArrayList<InputManagerInputNode> overlappingNodeList = new ArrayList<>();
            for(InputManagerInputNode comparedNode : allRootNodes) {
                if(comparedNode == this) continue;

                for(String key : keysUsed) {
                    if(comparedNode.usesKey(key)) {
                        if(newMethod == InputOverlapResolutionMethod.LEAST_COMPLEX_ARE_THE_FAVOURITE_CHILD &&comparedNode.complexity() < complexity()) overlappingNodeList.add(comparedNode);
                        else if(newMethod == InputOverlapResolutionMethod.MOST_COMPLEX_ARE_THE_FAVOURITE_CHILD && comparedNode.complexity() > complexity()) overlappingNodeList.add(comparedNode);
                        break;
                    }
                }
            }
            this.overlappingNodes = overlappingNodeList.toArray(new InputManagerInputNode[0]);
        }
    }
    public boolean isOverlapped() {
        for(InputManagerInputNode n : overlappingNodes) {
            if(n.getResult().getBool()) return true;
        }
        return false;
    }
    public InputManagerNodeResult getOverlappedResult() {
        if(isOverlapped()) return InputManagerNodeResult.FALSE;
        else return getResult();
    }
}
