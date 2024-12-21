package org.openspcoop2.pdd.core.cache;

import java.io.Serializable;
import java.util.Date;

public class CacheEntry<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = 2859773176907918793L;
	T object;
	Long timestamp;
	
	public CacheEntry(T object, Long lifetimeMs) {
		this.timestamp = new Date().getTime() + lifetimeMs;
		this.object = object;
	}
	
	public boolean isValid() {
		return new Date().getTime() < this.timestamp;
	}

	public T getObject() {
		return this.object;
	}
}
