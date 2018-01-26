/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.protocol.sdk.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * ConsoleConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
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
