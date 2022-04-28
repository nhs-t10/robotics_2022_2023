package org.firstinspires.ftc.teamcode.unitTests.dummy;

import org.firstinspires.ftc.teamcode.managers.input.InputManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManagerNodeResult;
import org.firstinspires.ftc.teamcode.managers.input.nodes.InputManagerInputNode;
import org.junit.Test;

import androidx.annotation.NonNull;

import static org.junit.Assert.assertEquals;

public class DummyInputNode extends InputManagerInputNode {
    private float value;

    private String key;
    private final InputManagerNodeResult result = new InputManagerNodeResult();

    public DummyInputNode() {
        this(0);
    }
    public DummyInputNode(float v) {
        this.value = v;
    }
    public DummyInputNode(float v, String k) {
        this.value = v; this.key = k;
    }
    public DummyInputNode(String k, float v) {
        this.value = v; this.key = k;
    }

    @Override
    public void init(InputManager boss) {

    }

    @Override
    public void update() {

    }

    @NonNull
    @Override
    public final InputManagerNodeResult getResult() {
        result.setFloat(value);
        return result;
    }

    @Override
    public int complexity() {
        return 0;
    }

    @Override
    public String[] getKeysUsed() {
        return new String[] { key };
    }

    @Override
    public boolean usesKey(String s) {
        return s.equals(key);
    }

    public void set(float v) {
        this.value = v;
    }
}
