/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.datamodel;

import org.openspcoop2.generic_project.dao.IServiceSearchWithId;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.utils.LoggerWrapperFactory;

import org.openspcoop2.web.monitor.core.dao.IService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SerializableDataModel;
import org.slf4j.Logger;

/**
 * BaseDataModel
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 * @param <K> Il tipo della chiave (Integer, Long ...)
 * @param <T> Il tipo di dato che rappresenta una righa della tabella
 * @param <D> Il tipo del dataprovider
 */
public abstract class BaseDataModel<K, T , D> extends SerializableDataModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8413193446969383169L;
	private static Logger log = LoggerWrapperFactory.getLogger(BaseDataModel.class);
	private transient D dataProvider;
	protected boolean detached = false;
	protected K currentPk;
	protected Map<K,T> wrappedData = new HashMap<K,T>();
    protected List<K> wrappedKeys = null;
    protected Integer rowCount;
    protected IExpression countFilter= null;
    protected Integer currentPage = 1;
    
    /**
     * The boolean field, detached, starts as false.  
     * It will be set to “true” when SerializableDataModel getSerializableModel(Range range) is called, and back to false when public void update() is called.  
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
    @SuppressWarnings("unchecked")
	@Override
    public void setRowKey(Object key) {
        this.currentPk = (K) key;
    }

    /**
     * This is main part of Visitor pattern. Method called by framework many times during request processing. 
     * 
     * We have two checks to see if we should return cached data, or if we should fetch new data.  
     * The boolean field, detached, starts as false.  
     * It will be set to “true” when SerializableDataModel getSerializableModel(Range range) is called, and back to false when public void update() is called.  
     * In this manner, the model will not be updated when the jsf component is rebuilt on postback, but rather when the new model is being built.
     */
	@Override
	public abstract void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) throws IOException;

	/**
     * This method must return actual data rows count from the Data Provider. It is used by pagination control
     * to determine total number of data items.
     */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public int getRowCount(){
		try {
			
			this.checkDataProvider();
			
			if(this.rowCount==null){
				if(this.dataProvider instanceof IService)
					this.rowCount=_executeTotalCountWithIService((IService) this.dataProvider);
				if(this.dataProvider instanceof IServiceSearchWithId){
					IExpression filter =  null;
					
					if(this.countFilter == null)
						filter = ((IServiceSearchWithId) this.dataProvider).newExpression();
					else 
						filter = this.countFilter;
					
					this.rowCount=(int)((IServiceSearchWithId)this.dataProvider).count(filter).longValue();
				}
			}
			return this.rowCount;
		} catch (Exception e) {
			BaseDataModel.log.error(e.getMessage(), e);
		}
		return 0;
	}

	protected int _executeTotalCountWithIService(IService<?,?> service) throws Exception { 
		return service.totalCount();
	}

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getRowData(){
		try {
			
			this.checkDataProvider();
			
			
			if(this.currentPk==null)
				return null;
			else{
				Object m = this.wrappedData.get(this.currentPk);
				if(m==null){
					if(this.dataProvider instanceof IService){
						m=((IService)this.dataProvider).findById(this.currentPk);
					}
					if(this.dataProvider instanceof IServiceSearchWithId){
						m=((IServiceSearchWithId<T,K>)this.dataProvider).get(this.currentPk);
					}
//					if(this.dataProvider instanceof org.spcoop.web.customer.ospcconsole.core.IService){
//						m=((org.spcoop.web.customer.ospcconsole.core.IService)this.dataProvider).read(this.currentPk);
//					}
					this.wrappedData.put(this.currentPk, (T)m);
				}
				return m;
			}
				
		} catch (Exception e) {
			BaseDataModel.log.error(e.getMessage(), e);
		}
		
		return null;
	}
	 
	/**
     * Unused rudiment from old JSF staff.
     */
	@Override
	public int getRowIndex() {
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
		BaseDataModel.log.debug("called setRowIndex "+arg0);
	}
	/**
     * Unused rudiment from old JSF staff.
     */
	@Override
	public void setWrappedData(Object arg0) {
		throw new UnsupportedOperationException();
	}

	public D getDataProvider(){
		return this.dataProvider; 
	}

    public void setDataProvider(D dataProvider){
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
    
    private void checkDataProvider() throws Exception{
    	
    	if(this.dataProvider instanceof IService) return;
    	if(this.dataProvider instanceof IServiceSearchWithId) return;
    	
    	//data provider sconosciuto
    	throw new Exception("DataProvider ["+this.dataProvider.getClass().getCanonicalName()+"] sconosciuto, non so come gestirlo.");
    }

	public Integer getCurrentPage() {
		return this.currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
    
    
}
