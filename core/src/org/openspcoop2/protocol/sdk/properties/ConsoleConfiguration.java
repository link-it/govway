package org.openspcoop2.protocol.sdk.properties;

import java.util.ArrayList;
import java.util.List;

public class ConsoleConfiguration {

	private List<BaseConsoleItem> consoleItem = new ArrayList<BaseConsoleItem>();
	
	public void clearItems(){
		this.consoleItem.clear();
	}
	public int sizeItems(){
		return this.consoleItem.size();
	}
	public List<BaseConsoleItem> getConsoleItem() {
		return this.consoleItem;
	}
	public void addConsoleItem(BaseConsoleItem item){
		this.consoleItem.add(item);
	}
	public BaseConsoleItem getConsoleItem(int index){
		return this.consoleItem.get(index);
	}
	public BaseConsoleItem removeConsoleItem(int index){
		return this.consoleItem.remove(index);
	}
	
}
