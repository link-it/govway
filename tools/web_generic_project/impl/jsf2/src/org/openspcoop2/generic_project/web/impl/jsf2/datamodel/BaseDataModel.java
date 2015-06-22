/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.web.impl.jsf2.datamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;

/***
 * 
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$ 
 *
 * @param <T> Tipo degli oggetti da visualizzare
 * @param <K> Tipo delle chiavi
 * @param <D> Data Provider 
 */
public abstract class  BaseDataModel<T,K,D> extends LazyDataModel<T>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1058139500366295008L;
	
	private D dataProvider;
	protected Map<K,T> wrappedData = new HashMap<K,T>();
    protected List<K> wrappedKeys = null;
	
	public BaseDataModel(){
		super();
	}
	
	public D getDataProvider(){
		return this.dataProvider; 
	}

    public void setDataProvider(D dataProvider){
    	this.dataProvider = dataProvider;
    }
    
}
