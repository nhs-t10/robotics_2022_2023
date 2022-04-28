package org.firstinspires.ftc.teamcode.managers.input.nodes;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;

import androidx.annotation.NonNull;

public class ScaleNode extends InputManagerInputNode {
    private final InputManagerInputNode node;
    private InputManagerInputNode scale;
    private InputManager boss;

    private final InputManagerNodeResult result = new InputManagerNodeResult();

    /**
     * The same as {@link MultiplyNode}. Use whichever makes more sense.
     * @param scaleBy The input to multiply
     * @param node The number that the input will be multiplied by (it can be a second input, if you want)
     * @see MultiplyNode
     */
    public ScaleNode(InputManagerInputNode scaleBy, InputManagerInputNode node) {
        this.scale = scaleBy;
        this.node = node;
    }

    /**
     * The same as {@link MultiplyNode}. Use whichever makes more sense.
     * @param scaleBy The input to multiply
     * @param node The number that the input will be multiplied by (it can be a second input, if you want)
     * @see MultiplyNode
     */
    public ScaleNode(InputManagerInputNode node, float scaleBy) {
        this.scale = new StaticValueNode(scaleBy);
        this.node = node;
    }
    /**
     * The same as {@link MultiplyNode}. Use whichever makes more sense.
     * @param scaleBy The number that the input will be multiplied by (it can be a second input, if you want)
     * @param node The input to multiply
     * @see MultiplyNode
     */
    public ScaleNode(float scaleBy, InputManagerInputNode node) {
        this.scale = new StaticValueNode(scaleBy);
        this.node = node;
    }

    @Override
    public void init(InputManager boss) {
        this.boss = boss;
        node.init(boss);
        scale.init(boss);
    }

    @Override
    public void update() {
        node.update();
        scale.update();
    }

    @NonNull
    @Override
    public InputManagerNodeResult getResult() {
        float scaleFactor = scale.getResult().getFloat();
        InputManagerNodeResult res = node.getResult();
        float[] resultNumbers = res.getFloatArray();

        float[] scaled = new float[resultNumbers.length];
        for(int i = 0; i < scaled.length; i++) {
            scaled[i] = resultNumbers[i] * scaleFactor;
        }

        result.setFloatArray(scaled);
        return result;
    }

    @Override
    public int complexity() {
        return scale.complexity() + node.complexity() + 1;
    }

    @Override
    public String[] getKeysUsed() {
        return PaulMath.concatArrays(node.getKeysUsed(), scale.getKeysUsed());
    }

    @Override
    public boolean usesKey(String s) {
        return node.usesKey(s) || scale.usesKey(s);
    }
}
