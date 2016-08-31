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
package org.openspcoop2.generic_project.web.impl.jsf1.mbean;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactoryManager;
import org.openspcoop2.generic_project.web.form.Form;
import org.openspcoop2.generic_project.web.form.SearchForm;
import org.openspcoop2.generic_project.web.impl.jsf1.CostantiJsf1Impl;
import org.openspcoop2.generic_project.web.impl.jsf1.mbean.utils.NavigationManager;
import org.openspcoop2.generic_project.web.impl.jsf1.utils.MessageUtils;
import org.openspcoop2.generic_project.web.iservice.IBaseService;
import org.openspcoop2.generic_project.web.mbean.IManagedBean;
import org.openspcoop2.generic_project.web.mbean.exception.AnnullaException;
import org.openspcoop2.generic_project.web.mbean.exception.DeleteException;
import org.openspcoop2.generic_project.web.mbean.exception.DettaglioException;
import org.openspcoop2.generic_project.web.mbean.exception.FiltraException;
import org.openspcoop2.generic_project.web.mbean.exception.InviaException;
import org.openspcoop2.generic_project.web.mbean.exception.MenuActionException;
import org.openspcoop2.generic_project.web.mbean.exception.ModificaException;
import org.openspcoop2.generic_project.web.mbean.exception.NuovoException;
import org.openspcoop2.generic_project.web.mbean.exception.ResetException;
import org.openspcoop2.generic_project.web.mbean.exception.RestoreSearchException;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * BaseFormMBean classe generica che fornisce il supporto ad una form.
 * 
 * service: interfaccia con il livello service per l'accesso ai dati.
 * selectedElement: in caso di visualizzazione di tipo Elenco -> Dettaglio, 
 * fornisce il supporto per la gestione della selezione di un elemento da parte dell'utente.
 * form: Bean del form.
 * 
 * @param <BeanType> tipo dell'oggetto
 * @param <KeyType> tipo della chiave dell'oggetto
 * @param <FormType> tipo del form.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BaseFormMBean<BeanType,KeyType,FormType extends Form> implements IManagedBean<SearchForm, FormType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	protected IBaseService<BeanType,KeyType,FormType> service;
	protected BeanType selectedElement;
	protected FormType form;
	protected BeanType metadata;
	protected transient Logger log= null;
	protected WebGenericProjectFactory factory;

	protected NavigationManager navigationManager= null;

	public BaseFormMBean(){
		this(null);
	}
	
	public BaseFormMBean(Logger log) {
		try {
			this.log = log;
			this.navigationManager = new NavigationManager();
			this.factory = WebGenericProjectFactoryManager.getInstance().getWebGenericProjectFactoryByName(CostantiJsf1Impl.FACTORY_NAME);
			init();
			initNavigationManager();
		} catch (Exception e) {
			this.getLog().error(e.getMessage(),e);
		}
	}
	
	public Logger getLog(){
		if(this.log == null)
			this.log = LoggerWrapperFactory.getLogger(BaseFormMBean.class);
		
		return this.log;
	}
	
	public abstract void initNavigationManager() throws Exception;
	
	public void setService(IBaseService<BeanType, KeyType,FormType> service) {
		this.service = service;
	}

	@SuppressWarnings("unchecked")
	public BeanType getSelectedElement(){
		if(this.selectedElement==null){
			try{
				ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
				this.selectedElement = ((Class<BeanType>)parameterizedType.getActualTypeArguments()[0]).newInstance();
			}catch (Exception e) {
				this.getLog().error(e.getMessage(),e);
			}
		}
		return this.selectedElement;
	}

	public void setSelectedElement(BeanType selectedElement) {
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

	@Override
	public FormType getForm() {
		return this.form;
	}

	@Override
	public void setForm(FormType form) {
		this.form = form;
	}


	@SuppressWarnings("unchecked")
	public BeanType getMetadata(){
		if(this.metadata==null){
			try{
				ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
				this.metadata = ((Class<BeanType>)parameterizedType.getActualTypeArguments()[0]).newInstance();
			}catch (Exception e) {
				this.getLog().error(e.getMessage(),e);
			}
		}
		return this.metadata;
	}

	public void setMetadata(BeanType metadata) {
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

	
	
	/** Metodi che vengono utilizzati all'interno della pagina*/

	public String invia(){
		try{
			return azioneInvia();
		}catch (InviaException e){
			this.getLog().error("Si e' verificato un errore durante l'esecuzione del metodo invia: "+ e.getMessage(),e);
			MessageUtils.addErrorMsg(e.getMessage());
			return null;
		}
	}
	
	@Override
	public String azioneInvia() throws InviaException {
		try{
			this.service.store(this.selectedElement);
		}catch(Exception e){
			throw new InviaException(e);
		}
		return this.getNavigationManager().getInviaOutcome();
	}


	public String modifica(){
		try{
			return azioneModifica();
		}catch (ModificaException e){
			this.getLog().error("Si e' verificato un errore durante l'esecuzione del metodo modifica: "+ e.getMessage(),e);
			MessageUtils.addErrorMsg(e.getMessage());
			return null;
		}
	}
	
	@Override
	public String azioneModifica() throws ModificaException {
		return this.getNavigationManager().getModificaOutcome();
	}
	public String delete(){
		try{
			return azioneDelete();
		}catch (DeleteException e){
			this.getLog().error("Si e' verificato un errore durante l'esecuzione del metodo delete: "+ e.getMessage(),e);
			MessageUtils.addErrorMsg(e.getMessage());
			return null;
		}
	}
	
	@Override
	public String azioneDelete() throws DeleteException {
		try{
			// do nothing
		}catch(Exception e){
			throw new DeleteException(e);
		}
		return this.getNavigationManager().getDeleteOutcome();
	}

	public String dettaglio(){
		try{
			return azioneDettaglio();
		}catch (DettaglioException e){
			this.getLog().error("Si e' verificato un errore durante l'esecuzione del metodo dettaglio: "+ e.getMessage(),e);
			MessageUtils.addErrorMsg(e.getMessage());
			return null;
		}
	}
	
	@Override
	public String azioneDettaglio() throws DettaglioException {
		try{
			// do nothing
		}catch(Exception e){
			throw new DettaglioException(e);
		}
		return this.getNavigationManager().getDettaglioOutcome();
	}


	public String nuovo(){
		try{
			return azioneNuovo();
		}catch (NuovoException e){
			this.getLog().error("Si e' verificato un errore durante l'esecuzione del metodo nuovo: "+ e.getMessage(),e);
			MessageUtils.addErrorMsg(e.getMessage());
			return null;
		}
	}
	@Override
	public String azioneNuovo() throws NuovoException {
		try{
			// do nothing
		}catch(Exception e){
			throw new NuovoException(e);
		}
		return this.getNavigationManager().getNuovoOutcome();
	}


	public String annulla(){
		try{
			return azioneAnnulla();
		}catch (AnnullaException e){
			this.getLog().error("Si e' verificato un errore durante l'esecuzione del metodo annulla: "+ e.getMessage(),e);
			MessageUtils.addErrorMsg(e.getMessage());
			return null;
		}
	}
	
	@Override
	public String azioneAnnulla() throws AnnullaException {
		try{
			// do nothing
		}catch(Exception e){
			throw new AnnullaException(e);
		}
		return this.getNavigationManager().getAnnullaOutcome();
	}


	public String menuAction(){
		try{
			return azioneMenuAction();
		}catch (MenuActionException e){
			this.getLog().error("Si e' verificato un errore durante l'esecuzione del metodo menu' action: "+ e.getMessage(),e);
			MessageUtils.addErrorMsg(e.getMessage());
			return null;
		}
	}
	@Override
	public String azioneMenuAction() throws MenuActionException {
		try{
			// do nothing
		}catch(Exception e){
			throw new MenuActionException(e);
		}
		return this.getNavigationManager().getMenuActionOutcome();
	}


	public String reset(){
		try{
			return azioneReset();
		}catch (ResetException e){
			this.getLog().error("Si e' verificato un errore durante l'esecuzione del metodo reset: "+ e.getMessage(),e);
			MessageUtils.addErrorMsg(e.getMessage());
			return null;
		}
	}
	@Override
	public String azioneReset() throws ResetException {
		try{
			// do nothing
		}catch(Exception e){
			throw new ResetException(e);
		}
		return this.getNavigationManager().getResetOutcome();
	}
	
	public NavigationManager getNavigationManager() {
		return this.navigationManager;
	}

	public void setNavigationManager(NavigationManager navigationManager) {
		this.navigationManager = navigationManager;
	}

	@Override
	public SearchForm getSearch() {
		return null;
	}

	@Override
	public void setSearch(SearchForm search) {
	}

	@Override
	public WebGenericProjectFactory getFactory() {
		return this.factory;
	}

	@Override
	public void setFactory(WebGenericProjectFactory factory) {
		this.factory = factory;
	}



	@Override
	public String azioneFiltra() throws FiltraException {
		return null;
	}

	@Override
	public String azioneRestoreSearch() throws RestoreSearchException {
		return null;
	}
	

	
}
