package org.firstinspires.ftc.teamcode.auxilary;

import java.lang.ref.WeakReference;

public class UpdatableWeakReference<T> {
    private WeakReference<T> ref;

    public UpdatableWeakReference() {
        this.ref = null;
    }
    public UpdatableWeakReference(T obj) {
        this.ref = new WeakReference<T>(obj);
    }
    public T get() {
        if(this.ref == null) return null;
        else return ref.get();
    }
    public void set(T obj) {
        this.ref = new WeakReference<T>(obj);
    }
}
