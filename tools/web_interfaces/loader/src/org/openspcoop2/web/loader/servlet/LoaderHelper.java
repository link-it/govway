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


package org.openspcoop2.web.loader.servlet;

import java.io.ByteArrayInputStream;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.upload.FormFile;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.ICrypt;
import org.openspcoop2.utils.xml.ValidatoreXSD;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.MenuEntry;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.loader.config.LoaderProperties;
import org.openspcoop2.web.loader.core.Costanti;
import org.openspcoop2.web.loader.core.LoaderCore;
import org.openspcoop2.web.loader.servlet.about.AboutCore;
import org.openspcoop2.web.loader.servlet.archivi.ImportaXML;
import org.slf4j.Logger;

/**
 * OpenSPCoopLoaderHelper
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class LoaderHelper {
	protected HttpServletRequest request;
	protected PageData pd;
	protected HttpSession session;
	protected LoaderCore core;
	protected AboutCore aboutCore;

	protected ICrypt passwordManager;
	public ICrypt getPasswordManager() {
		return this.passwordManager;
	}
	protected ICrypt passwordManager_backwardCompatibility;

	/** Logger utilizzato per debug. */
	private Logger log = null;
	public Logger getLog() {
		return this.log;
	}

	public boolean isEditModeInProgress() throws Exception{
		String editMode = this.request.getParameter(org.openspcoop2.web.lib.mvc.Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		return ServletUtils.isEditModeInProgress(editMode);		
	}

	public boolean isEditModeFinished() throws Exception{
		String editMode = this.request.getParameter(org.openspcoop2.web.lib.mvc.Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		return ServletUtils.isEditModeFinished(editMode);		
	}
	
	public String getPostBackElementName() throws Exception{
		return this.request.getParameter(org.openspcoop2.web.lib.mvc.Costanti.POSTBACK_ELEMENT_NAME);
	}
	
	public LoaderHelper(HttpServletRequest request, PageData pd, HttpSession session) {
		this.request = request;
		this.pd = pd;

		this.session = session;

		this.log = LoggerWrapperFactory.getLogger("govway.loader");
		try {
			this.core = new LoaderCore();
			this.aboutCore = new AboutCore(this.core);
			
			LoaderProperties consoleProperties = LoaderProperties.getInstance();
			CryptConfig config = new CryptConfig(consoleProperties.getConsoleUtenzePassword());
			this.passwordManager = CryptFactory.getCrypt(this.log, config);
			if(config.isBackwardCompatibility()) {
				this.passwordManager_backwardCompatibility = CryptFactory.getOldMD5Crypt(this.log);
			}
			
		} catch (Exception e) {
			this.log.error("Exception LoaderHelper: " + e.getMessage(), e);
		}
	}

	public int getSize() {
		return 50;
	}
	
	// Prepara il menu'
	public void makeMenu() {
		//String logAdm = (String) this.session.getAttribute("Login");

		Vector<MenuEntry> menu = new Vector<MenuEntry>();

		// Preparo il menu per il generalsu
		MenuEntry me1 = new MenuEntry();
		me1.setTitle(Costanti.LABEL_CONFIGURAZIONI_XML);
		String[][] entries1 = new String[2][2];
		entries1[0][0] = Costanti.LABEL_IMPORTA;
		entries1[0][1] = Costanti.SERVLET_NAME_ARCHIVI_IMPORTA_XML;
		entries1[1][0] = Costanti.LABEL_ELIMINA;
		entries1[1][1] = Costanti.SERVLET_NAME_ARCHIVI_ELIMINAZIONE_XML;
		me1.setEntries(entries1);
		menu.addElement(me1);

		this.pd.setMenu(menu);
	}

	public boolean loginCheckData(boolean verificaPassword) throws DriverUsersDBException {
		
		String login = this.request.getParameter(Costanti.PARAMETRO_UTENTE_LOGIN);
		String password = this.request.getParameter(Costanti.PARAMETRO_UTENTE_PASSWORD);

		// Campi obbligatori
		if (login.equals("")) {
			this.pd.setMessage(Costanti.LABEL_LOGIN_DATI_INCOMPLETI_LOGIN,MessageType.ERROR_SINTETICO);
			return false;
		}
		if (verificaPassword) {
			if (password.equals("")) {
				this.pd.setMessage(Costanti.LABEL_LOGIN_DATI_INCOMPLETI_PASSWORD,MessageType.ERROR_SINTETICO);
				return false;
			}
		}

		// FARE PROPRIETA SU FILE PER INDICARE DOVE CERCARE L'UTENTE
		
		// Se tipoCheck = pw, controllo che login e password corrispondano
		// Se tipoCheck = nopw, mi basta che l'utente sia registrato
		boolean trovato = false;
		if(this.core.isSearchUserIntoRegistro()){
			trovato = this.core.existsUserRegistro(login);
		}else{
			trovato = this.core.existsUserConfig(login);
		}

				
		User u = null;
		if(trovato){
			if(this.core.isSearchUserIntoRegistro()){
				u = this.core.getUserRegistro(login);
			}else{
				u = this.core.getUserConfig(login);
			}
		}
		if (trovato && verificaPassword) {
			// Prendo la pw criptata da DB
			String pwcrypt = u.getPassword();
			if ((pwcrypt != null) && (!pwcrypt.equals(""))) {
				// Controlla se utente e password corrispondono
				trovato = this.passwordManager.check(password, pwcrypt);
				if(!trovato && this.passwordManager_backwardCompatibility!=null) {
					trovato = this.passwordManager_backwardCompatibility.check(password, pwcrypt);
				}
			}
		}

		if (!trovato) {
			if (verificaPassword==false) {
				this.pd.setMessage(Costanti.LABEL_LOGIN_ERRATO,MessageType.ERROR_SINTETICO);
			} else {
				this.pd.setMessage(Costanti.LABEL_LOGIN_CON_PASSWORD_ERRATO,MessageType.ERROR_SINTETICO);
			}
			return false;
		}

		if(u.getPermessi().isServizi()==false){
			this.pd.setMessage(Costanti.LABEL_LOGIN_PERMESSI_NON_SUFFICENTI,MessageType.ERROR_SINTETICO);
			return false;
		}
		
		// setto l utente in sessione
		ServletUtils.setUserIntoSession(this.session, u);
		return true;
		
	}

	
	public void addImportaXMLtoDati(Vector<DataElement> dati,String tipoxml){
		
		DataElement de = new DataElement();
		de.setLabel(Costanti.LABEL_IMPORTA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(Costanti.LABEL_TIPOLOGIA_XML);
		de.setType(DataElementType.SELECT);
		String [] stati = new String[2];
		stati[0] = Costanti.TIPOLOGIA_XML_REGISTRO_SERVIZI;
		stati[1] = Costanti.TIPOLOGIA_XML_CONFIGURAZIONE_PDD;
		de.setValues(stati);
		de.setSelected(tipoxml);
		de.setName(Costanti.PARAMETRO_ARCHIVI_TIPO_XML);
		dati.addElement(de);

		de = new DataElement();
		de = new DataElement();
		de.setValue("");
		de.setLabel(Costanti.LABEL_PARAMETRO_ARCHIVI_FILE);
		de.setType(DataElementType.FILE);
		de.setName(Costanti.PARAMETRO_ARCHIVI_FILE);
		de.setSize(this.getSize());
		dati.addElement(de);
	}
	
	public void addEliminazioneXMLtoDati(Vector<DataElement> dati,String tipoxml){
		
		DataElement de = new DataElement();
		de.setLabel(Costanti.LABEL_ELIMINA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(Costanti.LABEL_TIPOLOGIA_XML);
		de.setType(DataElementType.SELECT);
		String [] stati = new String[2];
		stati[0] = Costanti.TIPOLOGIA_XML_REGISTRO_SERVIZI;
		stati[1] = Costanti.TIPOLOGIA_XML_CONFIGURAZIONE_PDD;
		de.setValues(stati);
		de.setSelected(tipoxml);
		de.setName(Costanti.PARAMETRO_ARCHIVI_TIPO_XML);
		dati.addElement(de);

		de = new DataElement();
		de = new DataElement();
		de.setValue("");
		de.setLabel(Costanti.LABEL_PARAMETRO_ARCHIVI_FILE);
		de.setType(DataElementType.FILE);
		de.setName(Costanti.PARAMETRO_ARCHIVI_FILE);
		de.setSize(this.getSize());
		dati.addElement(de);
	}
	
	public boolean validateFileXml(Logger log,FormFile ff,StringBuilder errorBuffer,String tipologiaXML){

		String schemaValidazione = null;
		try{
			ValidatoreXSD validatore = null;
			if(Costanti.TIPOLOGIA_XML_REGISTRO_SERVIZI.equals(tipologiaXML)){
				validatore = new ValidatoreXSD(log,ImportaXML.class.getResourceAsStream("/"+Costanti.SCHEMA_REGISTRO_SERVIZI));
				schemaValidazione = Costanti.SCHEMA_REGISTRO_SERVIZI;
			}else{
				validatore = new ValidatoreXSD(log,ImportaXML.class.getResourceAsStream("/"+Costanti.SCHEMA_CONFIGURAZIONE_PDD));
				schemaValidazione = Costanti.SCHEMA_CONFIGURAZIONE_PDD;
			}
			byte[] data = ff.getFileData();
			
			//log.debug(new String(data));
			ByteArrayInputStream bin = new ByteArrayInputStream(data);

			validatore.valida(bin);

			return true;
		}catch (Exception e) {
			errorBuffer.append("Errore validazione xsd ("+schemaValidazione+"): "+e.getMessage());
		}

		return false;
	}
}
