package org.openspcoop2.protocol.sdk.properties;

import java.util.ArrayList;
import java.util.List;

public class ProtocolProperties {

	private List<AbstractProperty<?>> list = new ArrayList<AbstractProperty<?>>();
	
	public void addProperty(AbstractProperty<?> p){
		this.list.add(p);
	}
	public void addProperty(String id, String value){
		this.list.add(ProtocolPropertiesFactory.newProperty(id, value));
	}
	public void addProperty(String id, int value){
		this.list.add(ProtocolPropertiesFactory.newProperty(id, value));
	}
	public void addProperty(String id, long value){
		this.list.add(ProtocolPropertiesFactory.newProperty(id, value));
	}
	public void addProperty(String id, byte[] value, String fileName){
		this.list.add(ProtocolPropertiesFactory.newProperty(id, value,fileName));
	}
	
	public AbstractProperty<?> getProperty(int index){
		return this.list.get(index);
	}
	public String getIdProperty(int index){
		return this.list.get(index).getId();
	}
	public Object getValueProperty(int index){
		return this.list.get(index).getValue();
	}
	
	public AbstractProperty<?> removeProperty(int index){
		return this.list.remove(index);
	}
	
	public void clearProperties(){
		this.list.clear();
	}
	
	public int sizeProperties(){
		return this.list.size();
	}
}
