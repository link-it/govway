package org.openspcoop2.protocol.sdk.properties;

public class AbstractProperty<T> {

	private String id;
	private T value;
	
	protected AbstractProperty(String id, T value){
		this.id = id;
		this.value = value;
	}
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public T getValue() {
		return this.value;
	}
	public void setValue(T value) {
		this.value = value;
	}
}
