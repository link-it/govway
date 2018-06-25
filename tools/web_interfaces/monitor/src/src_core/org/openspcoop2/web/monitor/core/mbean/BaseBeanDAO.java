/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.web.monitor.core.mbean;

import org.openspcoop2.generic_project.dao.IServiceWithId;
import org.openspcoop2.utils.LoggerWrapperFactory;

import org.openspcoop2.web.monitor.core.core.Utils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;

public abstract class BaseBeanDAO<T,K, ServiceType extends IServiceWithId<T, K>> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	protected transient ServiceType service;
	protected T selectedElement;
	protected Map<T, Boolean> selectedIds = new HashMap<T, Boolean>();
	protected ArrayList<T> toRemove;
	private static Logger log = LoggerWrapperFactory.getLogger(BaseBeanDAO.class);
	private boolean selectedAll = false;
	
	public abstract void setService(ServiceType service);
	
	public void isAddRequest(ActionEvent ae){
		//e' una richiesta add allora azzero il selected element
		//in modo da 'scordarmi' il valore eventualmente nel keepAlive
		this.selectedElement = null;
	}
	
	@SuppressWarnings("unchecked")
	public T getSelectedElement(){
		if(this.selectedElement==null){
			try{
				ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
				this.selectedElement = ((Class<T>)parameterizedType.getActualTypeArguments()[0]).newInstance();
			}catch (Exception e) {
				BaseBeanDAO.log.error("errore cercando di istanziare il selectedElement ", e);
			}
		}
		return this.selectedElement;
	}
	
	public void setSelectedElement(T selectedElement) {
		this.selectedElement = selectedElement;
	}
	
	public Map<T, Boolean> getSelectedIds() {
		return this.selectedIds;
	}
	
	public void setSelectedIds(Map<T, Boolean> selectedIds) {
		this.selectedIds = selectedIds;
	}
	
	public String delete(){
		this.toRemove = new ArrayList<T>();
		Iterator<T> it = this.selectedIds.keySet().iterator();
		while (it.hasNext()) {
			T elem = it.next();
			if(this.selectedIds.get(elem).booleanValue()){
				this.toRemove.add(elem);
				it.remove();
			}
		}
				
		for (T elem : this.toRemove) {
			try{
				this.service.delete(elem);
			}catch (Exception e) {
				FacesContext ctx = FacesContext.getCurrentInstance();
				String m = Utils.getMessageFromResourceBundle(ctx.getApplication().getMessageBundle(), "DELETE_ERROR", new String[]{elem.toString()}, ctx.getViewRoot().getLocale());
				ctx.addMessage(null, new FacesMessage(m,e.getLocalizedMessage()));
				Iterator<FacesMessage> ite = ctx.getMessages();
				while (ite.hasNext()) {
					FacesMessage t = ite.next();
					BaseBeanDAO.log.debug("Detail:"+t.getDetail());
					BaseBeanDAO.log.debug("Summary:"+t.getSummary());
				}
			}
			
		}
		
		
		return null;
	}
	
	public List<FacesMessage> getMessages(){
		FacesContext ctx = FacesContext.getCurrentInstance();
		Iterator<FacesMessage> ite = ctx.getMessages();
		ArrayList<FacesMessage> list = new ArrayList<FacesMessage>();
		while (ite.hasNext()) {
			list.add(ite.next());
		}
		return list;
	}

	public boolean isSelectedAll() {
		return this.selectedAll;
	}

	public void setSelectedAll(boolean selectedAll) {
		this.selectedAll = selectedAll;
	}
	
	public void addNewListener(ActionEvent ae){
		this.selectedElement = null;
	}
}
