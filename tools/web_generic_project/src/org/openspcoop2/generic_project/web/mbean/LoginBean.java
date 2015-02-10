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
package org.openspcoop2.generic_project.web.mbean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.web.core.Utils;
import org.openspcoop2.generic_project.web.dao.DummyLoginDAO;
import org.openspcoop2.generic_project.web.dao.ILoginDAO;
import org.openspcoop2.generic_project.web.form.LanguageForm;


/**
 * LoginBean bean di sessione per la gestione del login dell'utente.
 * Gestisce l'autenticazione base con username e password.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoginBean{

	private String username;
	private String password;
	private Boolean isLoggedIn=false;

	private Locale currentLocal = null;

	private String currentLang = null;

	// Serve per indicare se il Bean deve inizializzare da solo il LoginDAO, se false viene inizializzato da Faces...
	private boolean initDao =false;

	private boolean noPasswordLogin = false;

	private List<SelectItem> listaLingueSupportate;

	private LanguageForm languageForm = null;


	public LoginBean() {
		this(false);
	}

	public LoginBean(boolean initDao) {

		this.languageForm = new LanguageForm();
		this.languageForm.setmBean(this);

		this.languageForm.setRendered(true);

		try{
			this.currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		}catch(Exception e){
			this.currentLocal = Locale.getDefault();
		}

		this.languageForm.getLingua().setElencoSelectItems(this.getListaLingueSupportate());

		if(this.currentLocal.getLanguage().equals(Locale.ITALIAN.getLanguage()))
			this.languageForm.getLingua().setValue(new org.openspcoop2.generic_project.web.form.field.SelectItem(Locale.ITALIAN.getLanguage(),
					Utils.getMessageFromCommonsResourceBundle("lingua."+Locale.ITALIAN.getLanguage(),this.currentLocal)));
		if(this.currentLocal.getLanguage().equals(Locale.ENGLISH.getLanguage()))
			this.languageForm.getLingua().setValue(new org.openspcoop2.generic_project.web.form.field.SelectItem(Locale.ENGLISH.getLanguage(),
					Utils.getMessageFromCommonsResourceBundle("lingua."+Locale.ENGLISH.getLanguage(),this.currentLocal)));
		if(this.currentLocal.getLanguage().equals(Locale.GERMAN.getLanguage()))
			this.languageForm.getLingua().setValue(new org.openspcoop2.generic_project.web.form.field.SelectItem(Locale.GERMAN.getLanguage(),
					Utils.getMessageFromCommonsResourceBundle("lingua."+Locale.GERMAN.getLanguage(),this.currentLocal)));

		this.currentLang = this.currentLocal.getLanguage();

		this.initDao = initDao;

		init();
	}

	protected void init(){
		if(this.initDao){
			this.loginDao = new DummyLoginDAO();
		}
	}

	public boolean isInitDao() {
		return this.initDao;
	}

	public void setInitDao(boolean initDao) {
		this.initDao = initDao;
	}

	private ILoginDAO loginDao;

	public void setLoginDao(ILoginDAO loginDao) {
		this.loginDao = loginDao;
	}

	public ILoginDAO getLoginDao() {
		return this.loginDao;
	}

	public Boolean getIsLoggedIn() {
		return this.isLoggedIn;
	}

	public String getIsLoggedInAsString() {
		return this.isLoggedIn.toString();
	}

	public void setIsLoggedIn(Boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public String logout(){
		try{
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getExternalContext().getSessionMap().put("loginBean", null);
			HttpSession session = (HttpSession)fc.getExternalContext().getSession(false);
			session.invalidate();
		}catch(Exception e){}
		return "login";
	}

	public String login(){

		if(null == this.username && this.password == null){		
			return "login";
		}

		try{
			if(this.loginDao.login(this.username,this.password)){
				//			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				//			HttpSession session = (HttpSession) ec.getSession(true);
				//			session.setAttribute("logged", true);
				this.isLoggedIn = true;
				return "loginSuccess";
			}else{
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Errore: username non valido.",null));
			}
		}catch(ServiceException e){
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Si e' verificato un errore durante l'esecuzione del Login: impossibile accedere all'applicazione.",null));
		}

		return "login";   
	}

	public void setPassword(String password) {this.password = password;}

	public String getPassword(){return this.password;}

	public String getUsername(){return this.username;}

	public void setUsername(String username){this.username = username;}

	public void cambiaLinguaListener(ActionEvent event){

		org.openspcoop2.generic_project.web.form.field.SelectItem newValue = this.languageForm.getLingua().getValue();

		String value = newValue.getValue();

		if(value.equals(Locale.ITALIAN.getLanguage()))
			this.currentLocal = Locale.ITALIAN;
		else 
			if(value.equals(Locale.GERMAN.getLanguage()))
				this.currentLocal = Locale.GERMAN;
			else 
				if(value.equals(Locale.ENGLISH.getLanguage()))
					this.currentLocal = Locale.ENGLISH;

		if(this.currentLocal.getLanguage().equals(Locale.ITALIAN.getLanguage()))
			newValue.setLabel(Utils.getMessageFromCommonsResourceBundle("lingua."+Locale.ITALIAN.getLanguage(),this.currentLocal));
		if(this.currentLocal.getLanguage().equals(Locale.GERMAN.getLanguage()))
			newValue.setLabel(Utils.getMessageFromCommonsResourceBundle("lingua."+Locale.GERMAN.getLanguage(),this.currentLocal));
		if(this.currentLocal.getLanguage().equals(Locale.ENGLISH.getLanguage()))
			newValue.setLabel(Utils.getMessageFromCommonsResourceBundle("lingua."+Locale.ENGLISH.getLanguage(),this.currentLocal));

		this.currentLang = this.currentLocal.getLanguage();

		this.languageForm.getLingua().setElencoSelectItems(this.getListaLingueSupportate());
	}

	public Locale getCurrentLocal() {
		return this.currentLocal;
	}

	public void setCurrentLocal(Locale currentLocal) {
		this.currentLocal = currentLocal;
	}
	public boolean isNoPasswordLogin() {
		return this.noPasswordLogin;
	}

	public void setNoPasswordLogin(boolean noPasswordLogin) {
		this.noPasswordLogin = noPasswordLogin;
	}

	public List<SelectItem> getListaLingueSupportate() {
		this.listaLingueSupportate = new ArrayList<SelectItem>();

		this.listaLingueSupportate.add(new SelectItem(
				new org.openspcoop2.generic_project.web.form.field.SelectItem(Locale.ITALIAN.getLanguage(),Utils.getMessageFromCommonsResourceBundle("lingua."+Locale.ITALIAN.getLanguage(),this.currentLocal))));
		this.listaLingueSupportate.add(new SelectItem(
				new org.openspcoop2.generic_project.web.form.field.SelectItem(Locale.ENGLISH.getLanguage(),Utils.getMessageFromCommonsResourceBundle("lingua."+Locale.ENGLISH.getLanguage(),this.currentLocal))));
		this.listaLingueSupportate.add(new SelectItem(
				new org.openspcoop2.generic_project.web.form.field.SelectItem(Locale.GERMAN.getLanguage(),Utils.getMessageFromCommonsResourceBundle("lingua."+Locale.GERMAN.getLanguage(),this.currentLocal))));

		return this.listaLingueSupportate;
	}

	public void setListaLingueSupportate(List<SelectItem> listaLingueSupportate) {
		this.listaLingueSupportate = listaLingueSupportate;
	}

	public LanguageForm getLanguageForm() {
		return this.languageForm;
	}

	public void setLanguageForm(LanguageForm languageForm) {
		this.languageForm = languageForm;
	}

	public String getCurrentLang() {
		return this.currentLang;
	}

	public void setCurrentLang(String currentLang) {
		this.currentLang = currentLang;
	}


}
