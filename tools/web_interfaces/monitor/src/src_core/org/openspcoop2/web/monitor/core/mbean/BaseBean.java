/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;

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

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**
 * BaseBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
@SuppressWarnings("rawtypes")
public abstract class BaseBean<T,K, ServiceType extends IService> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	protected transient ServiceType service;
	protected T selectedElement;
	protected Map<T, Boolean> selectedIds = new HashMap<T, Boolean>();
	protected ArrayList<T> toRemove;
	private static Logger log = LoggerWrapperFactory.getLogger(BaseBean.class);
	private boolean selectedAll = false;
	protected String elencoID = null;
	protected String exportErrorMessage= null;
	
	public abstract void setService(ServiceType service);
	
	public IService getService(){
		return this.service;
	}
	
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
				this.selectedElement = Utilities.newInstance((Class<T>)parameterizedType.getActualTypeArguments()[0]);
			}catch (Exception e) {
				BaseBean.log.error("errore cercando di istanziare il selectedElement ", e);
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

	
	@SuppressWarnings("unchecked")
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
//			StringBuilder bf = new StringBuilder();
			try{
				this.service.delete(elem);
			}catch (Exception e) {
//				if(bf.length()<=0){
//					FacesContext ctx = FacesContext.getCurrentInstance();
//					String m = org.openspcoop2.web.monitor.core.core.Utils.getMessageFromResourceBundle(ctx.getApplication().getMessageBundle(), "DELETE_ERROR", new String[]{elem.toString()}, ctx.getViewRoot().getLocale());
//					bf.append(m);
//				}
//				bf.append("<br/>");
//				bf.append(e.getMessage());
				MessageUtils.addErrorMsg(e.getMessage());
				BaseBean.log.debug(e.getMessage(),e);
			}
//			if(bf.length()>0){
//				MessageUtils.addErrorMsg(bf.toString());
//			}
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

	public String getElencoID() {
		return this.elencoID;
	}

	public void setElencoID(String elencoID) {
		this.elencoID = elencoID;
	}
	
	public void initExportListener(ActionEvent ae){}

	public String getExportErrorMessage() {
		return this.exportErrorMessage;
	}

	public void setExportErrorMessage(String exportErrorMessage) {
		this.exportErrorMessage = exportErrorMessage;
	}
	
	
}
