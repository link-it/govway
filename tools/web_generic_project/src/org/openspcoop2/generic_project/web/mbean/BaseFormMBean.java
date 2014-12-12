/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
package org.openspcoop2.generic_project.web.mbean;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.openspcoop2.generic_project.web.form.BaseForm;
import org.openspcoop2.generic_project.web.iservice.IBaseService;

/**
 * BaseFormMBean classe generica che fornisce il supporto ad una form.
 * 
 * service: interfaccia con il livello service per l'accesso ai dati.
 * selectedElement: in caso di visualizzazione di tipo Elenco -> Dettaglio, 
 * fornisce il supporto per la gestione della selezione di un elemento da parte dell'utente.
 * form: Bean del form.
 * 
 * @param <T> tipo dell'oggetto
 * @param <K> tipo della chiave dell'oggetto
 * @param <F> tipo del form.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BaseFormMBean<T,K,F extends BaseForm> {

	protected IBaseService<T,K,F> service;
	protected T selectedElement;
	protected F form;
	protected T metadata;
	
	public void setService(IBaseService<T, K,F> service) {
		this.service = service;
	}

	@SuppressWarnings("unchecked")
	public T getSelectedElement(){
		if(this.selectedElement==null){
			try{
				ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
				this.selectedElement = ((Class<T>)parameterizedType.getActualTypeArguments()[0]).newInstance();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.selectedElement;
	}

	public void setSelectedElement(T selectedElement) {
		this.selectedElement = selectedElement;
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
	
	public F getForm() {
		return this.form;
	}

	public void setForm(F form) {
		this.form = form;
	}
	
	
	@SuppressWarnings("unchecked")
	public T getMetadata(){
		if(this.metadata==null){
			try{
				ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
				this.metadata = ((Class<T>)parameterizedType.getActualTypeArguments()[0]).newInstance();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.metadata;
	}

	public void setMetadata(T metadata) {
		this.metadata = metadata;
	}
	
	/**
	 * Listener eseguito prima di aggiungere un nuovo ricerca, setta a null il selectedElement
	 * in modo da "scordarsi" i valori gia' impostati.
	 * @param ae
	 */
	public void addNewListener(ActionEvent ae){
		this.selectedElement = null;
	}
}
