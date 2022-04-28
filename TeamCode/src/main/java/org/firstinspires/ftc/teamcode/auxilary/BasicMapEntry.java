package org.firstinspires.ftc.teamcode.auxilary;

import java.util.Map;

public class BasicMapEntry<K,V> implements Map.Entry<K,V> {
    private V v;
    private final K k;

    public BasicMapEntry(K k, V v) {
        this.k = k;
        this.v = v;
    }
    @Override
    public K getKey() {
        return k;
    }

    @Override
    public V getValue() {
        return v;
    }

    public V setValue(V value) {
        v = value;
        return value;
    }
}
