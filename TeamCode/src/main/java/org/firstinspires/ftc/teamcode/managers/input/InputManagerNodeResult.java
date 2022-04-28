package org.firstinspires.ftc.teamcode.managers.input;

import org.firstinspires.ftc.teamcode.managers.input.nodes.InputManagerInputNode;

public class InputManagerNodeResult {
    public static final InputManagerNodeResult FALSE = new InputManagerNodeResult();

    private float value;
    private InputManagerNodeResult[] childs = new InputManagerNodeResult[0];

    public InputManagerNodeResult(float v) {
        this.value = v;
    }


    public InputManagerNodeResult() {
        this.value = 0;
    }

    public float[] getFloatArray() {
        if(this.childs.length == 0) return new float[] {value};

        float[] values = new float[childs.length];
        for(int i = 0; i < values.length; i++) {
            values[i] = childs[i].getFloat();
        }
        return values;
    }
    public void setFloatArray(float[] f) {
        InputManagerNodeResult[] childCopy = new InputManagerNodeResult[f.length];
        for(int i = 0; i < f.length; i++) childCopy[i] = new InputManagerNodeResult(f[i]);
        this.childs = childCopy;
    }

    public float getFloat() {
        if(childs.length == 0) {
            return value;
        } else {
            return childs[0].getFloat();
        }
    }
    public void setFloat(float f) {
        this.childs = new InputManagerNodeResult[0];
        this.value = f;
    }

    public String toString() {
        if(childs == null || childs.length == 0) return "" + value;

        InputManagerNodeResult[] ch = childs;
        StringBuilder r = new StringBuilder("[");
        for(InputManagerNodeResult c : ch) r.append(c).append(",");
        return r.append("]").toString();
    }

    public boolean getBool() {
        return getFloat() != 0;
    }
    public void setBool(boolean b) {
        this.childs = new InputManagerNodeResult[0];
        this.value = b ? 1f : 0f;
    }

    public void copyValuesFrom(InputManagerNodeResult other) {
        InputManagerNodeResult[] oChilds = other.childs;
        this.childs = new InputManagerNodeResult[oChilds.length];
        System.arraycopy(oChilds, 0, childs, 0, oChilds.length);
        this.value = other.value;
    }

    public void setChildren(InputManagerNodeResult[] c) {
        InputManagerNodeResult[] childCopy = new InputManagerNodeResult[c.length];
        for(int i = 0; i < c.length; i++) childCopy[i] = c[i];
        this.childs = childCopy;
    }
}
