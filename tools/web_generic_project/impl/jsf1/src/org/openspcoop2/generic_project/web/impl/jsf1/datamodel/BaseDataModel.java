/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.web.impl.jsf1.datamodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SerializableDataModel;

/**
 * 
 * Fornisce il supporto al componente di Richfaces DataTable.
 * Si aggancia con il livello dei dati tramite il DataProvider D.
 *
 * @param <KeyType> Il tipo della chiave (Integer, Long ...)
 * @param <BeanType> Il tipo di dato che rappresenta una righa della tabella
 * @param <DataProvider> Il tipo del dataprovider
*
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/

public abstract class BaseDataModel<BeanType, KeyType, DataProvider> extends SerializableDataModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8413193446969383169L;
	private DataProvider dataProvider;
	protected boolean detached = false;
	protected KeyType currentPk;
	protected Map<KeyType,BeanType> wrappedData = new HashMap<KeyType,BeanType>();
    protected List<KeyType> wrappedKeys = null;
    protected Integer rowCount;
//    protected Integer currentPage = 0;
    
    public abstract KeyType getId(BeanType object);
    /**
     * The boolean field, detached, starts as false.  
     * It will be set to â€œtrueâ€� when SerializableDataModel getSerializableModel(Range range) is called, and back to false when public void update() is called.  
     * In this manner, the model will not be updated when the jsf component is rebuilt on postback, but rather when the new model is being built.
     */
	@Override
	public void update() {
		//nothing to do
		this.detached = false;
	}

	 /**
     * This method never called from framework.
     * (non-Javadoc)
     * @see org.ajax4jsf.model.ExtendedDataModel#getRowKey()
     */
    @Override
	public Object getRowKey() {
        return this.currentPk;
    }
    /**
     * This method normally called by Visitor before request Data Row.
     */
    @Override
	@SuppressWarnings("unchecked")
    public void setRowKey(Object key) {
        this.currentPk = (KeyType) key;
    }

    /**
     * This is main part of Visitor pattern. Method called by framework many times during request processing. 
     * 
     * We have two checks to see if we should return cached data, or if we should fetch new data.  
     * The boolean field, detached, starts as false.  
     * It will be set to â€œtrueâ€� when SerializableDataModel getSerializableModel(Range range) is called, and back to false when public void update() is called.  
     * In this manner, the model will not be updated when the jsf component is rebuilt on postback, but rather when the new model is being built.
     */
	@Override
	public abstract void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) throws IOException;

	/**
     * This method must return actual data rows count from the Data Provider. It is used by pagination control
     * to determine total number of data items.
     */
	@Override
	public abstract int getRowCount();

	/**
     * This is main way to obtain data row. It is intensively used by framework. 
     * We strongly recommend use of local cache in that method. 
     * 
     * <pre>
      <code>if (currentPk==null) {
            return null;
        } else {
            User ret = wrappedData.get(currentPk);
            if (ret==null) {
                ret = getDataProvider().getAuctionItemByPk(currentPk);
                wrappedData.put(currentPk, ret);
                return ret;
            } else {
                return ret;
            }
        }</code>
     	</pre>
     * 
     * 
     */
	@Override
	public abstract Object getRowData();
	 
	/**
     * Unused rudiment from old JSF staff.
     */
	@Override
	public int getRowIndex() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
     * Unused rudiment from old JSF staff.
     */
	@Override
	public Object getWrappedData() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isRowAvailable() {
		if(this.wrappedData.isEmpty()){
			return false;
		}else{
			boolean isAvailable = this.wrappedData.containsKey(this.currentPk);
			return isAvailable;
		}
	}
	/**
     * Unused rudiment from old JSF staff.
     */
	@Override
	public void setRowIndex(int arg0) {
		// ignore
	}
	/**
     * Unused rudiment from old JSF staff.
     */
	@Override
	public void setWrappedData(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public DataProvider getDataProvider(){
		return this.dataProvider; 
	}

    public void setDataProvider(DataProvider dataProvider){
    	this.dataProvider = dataProvider;
    }
    
    /**
     * This method suppose to produce SerializableDataModel that will be serialized into View State and used on a post-back.
     * In current implementation we just mark current model as serialized. In more complicated cases we may need to 
     * transform data to actually serialized form.
     */
    @Override
	public  SerializableDataModel getSerializableModel(Range range) {
        if (this.wrappedKeys!=null) {
        	this.detached = true;
            return this; 
        } else {
            return null;
        }
    }
}
