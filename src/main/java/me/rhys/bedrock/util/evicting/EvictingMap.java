package me.rhys.bedrock.util.evicting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@RequiredArgsConstructor
public class EvictingMap<K, V> extends HashMap<K, V> {

    @Getter
    private final int size;

    private final Deque<K> storedKeys = new LinkedList<>();

    @Override
    public boolean remove(Object key, Object value) {
        storedKeys.remove(key);
        return super.remove(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if(!storedKeys.contains(key) || !get(key).equals(value))
            checkAndRemove();
        return super.putIfAbsent(key, value);
    }

    @Override
    public V put(K key, V value) {
        checkAndRemove();
        storedKeys.addLast(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        storedKeys.clear();
        super.clear();
    }

    @Override
    public V remove(Object key) {
        storedKeys.remove(key);
        return super.remove(key);
    }

    private void checkAndRemove() {
        if(storedKeys.size() >= size) {
            storedKeys.removeFirst();
        }
    }
}