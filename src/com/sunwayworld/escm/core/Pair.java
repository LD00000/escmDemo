package com.sunwayworld.escm.core;

import java.util.Map.Entry;

public class Pair<K, V> implements Entry<K, V> {
	private K key;
	private V value;
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		
		return this.value;
	}
}
