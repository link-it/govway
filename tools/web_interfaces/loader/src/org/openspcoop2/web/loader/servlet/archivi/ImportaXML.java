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


package org.openspcoop2.web.loader.servlet.archivi;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.registry.driver.utils.PdDConfig;
import org.openspcoop2.core.registry.driver.utils.XMLDataConverter;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.loader.core.Costanti;
import org.openspcoop2.web.loader.core.LoaderCore;
import org.openspcoop2.web.loader.servlet.FileUploadForm;
import org.openspcoop2.web.loader.servlet.GeneralHelper;
import org.openspcoop2.web.loader.servlet.LoaderHelper;

/**
 * Importa
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ImportaXML extends Action {

	private String tipoxml;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {


		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try{
			
			LoaderHelper loaderHelper = new LoaderHelper(request,pd,session);
			
			//Eseguo la logica della servlet
			LoaderCore core = new LoaderCore();
			
			this.tipoxml = request.getParameter(Costanti.PARAMETRO_ARCHIVI_TIPO_XML);
			if(this.tipoxml==null){
				this.tipoxml = Costanti.TIPOLOGIA_XML_REGISTRO_SERVIZI;
			}
			
			loaderHelper.makeMenu();

			if(ServletUtils.isEditModeInProgress(request)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(Costanti.LABEL_CONFIGURAZIONI_XML, null),
						new Parameter(Costanti.LABEL_IMPORTA, Costanti.SERVLET_NAME_ARCHIVI_IMPORTA_XML));
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				loaderHelper.addImportaXMLtoDati(dati, this.tipoxml);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, Costanti.OBJECT_NAME_ARCHIVI_IMPORTA_XML, Costanti.TIPO_OPERAZIONE_ARCHIVI_IMPORTA_XML);
			}

			StringBuffer errorBuffer = new StringBuffer();
			FileUploadForm fileUpload = (FileUploadForm) form;
			FormFile ff = fileUpload.getTheFile();

			boolean ok = loaderHelper.validateFileXml(LoaderCore.getLog(),ff,errorBuffer,this.tipoxml);

			if(!ok){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(Costanti.LABEL_CONFIGURAZIONI_XML, null),
						new Parameter(Costanti.LABEL_IMPORTA, Costanti.SERVLET_NAME_ARCHIVI_IMPORTA_XML));
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				loaderHelper.addImportaXMLtoDati(dati, this.tipoxml);

				pd.setDati(dati);

				pd.setMessage(Costanti.LABEL_FILE_NON_VALIDO+errorBuffer.toString());

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, Costanti.OBJECT_NAME_ARCHIVI_IMPORTA_XML, Costanti.TIPO_OPERAZIONE_ARCHIVI_IMPORTA_XML);
			}

			//validazione superata

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			byte[] data = core.readBytes(ff);

			String errore = null;

			if(Costanti.TIPOLOGIA_XML_REGISTRO_SERVIZI.equals(this.tipoxml)){

				AccessoRegistroRegistro accesso = new AccessoRegistroRegistro();
				accesso.setLocation(core.getDataSourceRegistroServizi());
				accesso.setNome("Registro");
				accesso.setTipo(CostantiConfigurazione.REGISTRO_DB);
				accesso.setTipoDatabase(core.getTipoDatabaseRegistroServizi());

				Properties ctx = core.getCtxDatasourceRegistroServizi();
				Enumeration<?> ctxNames = ctx.keys();
				while (ctxNames.hasMoreElements()) {
					String name = (String) ctxNames.nextElement();
					String value = ctx.getProperty(name);
					accesso.getGenericPropertiesMap().put(name, value);
				}

				try{					
					XMLDataConverter dataConverter = new XMLDataConverter(data,accesso,userLogin,core.getProtocolloDefault(),LoaderCore.getLog());		
					PdDConfig pddConfig = new PdDConfig();
					pddConfig.setPddOperativaCtrlstatSinglePdd(core.getNomePdDOperativaCtrlstatSinglePdD());
					pddConfig.setTipoPdd(core.getTipoPdD());
					dataConverter.convertXML(false,pddConfig,core.isMantieniFruitoriServizi(),core.isGestioneSoggetti(),core.getStatoAccordo());

				}catch(Exception e){

					errore = e.getMessage();

					LoaderCore.getLog().error(e.getMessage(),e);
				}

			}else{

				AccessoConfigurazionePdD accesso = new AccessoConfigurazionePdD();
				accesso.setLocation(core.getDataSourceConfigurazionePdD());
				accesso.setTipo(CostantiConfigurazione.REGISTRO_DB.toString());
				accesso.setTipoDatabase(core.getTipoDatabaseConfigurazionePdD());
				accesso.setContext(core.getCtxDatasourceConfigurazionePdD());

				try{

					org.openspcoop2.core.config.driver.utils.XMLDataConverter dataConverter = 
						new org.openspcoop2.core.config.driver.utils.XMLDataConverter(data,accesso,false,false,userLogin,core.getProtocolloDefault(),LoaderCore.getLog());
					dataConverter.convertXML(false,core.isGestioneSoggetti());

				}catch(Exception e){

					errore = e.getMessage();

					LoaderCore.getLog().error(e.getMessage(),e);
				}	
			}


			//logica servlet eseguita

			if(errore!=null)
				pd.setMessage(errore);
			else
				pd.setMessage(Costanti.LABEL_IMPORTAZIONE_EFFETTUATA_CORRETTAMENTE);

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, 
					new Parameter(Costanti.LABEL_CONFIGURAZIONI_XML, null),
					new Parameter(Costanti.LABEL_IMPORTA, Costanti.SERVLET_NAME_ARCHIVI_IMPORTA_XML));

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			loaderHelper.addImportaXMLtoDati(dati, this.tipoxml);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			if(errore!=null)
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, Costanti.OBJECT_NAME_ARCHIVI_IMPORTA_XML, Costanti.TIPO_OPERAZIONE_ARCHIVI_IMPORTA_XML);
			else
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, Costanti.OBJECT_NAME_ARCHIVI_IMPORTA_XML, Costanti.TIPO_OPERAZIONE_ARCHIVI_IMPORTA_XML);
			
		}catch(Exception e){
			return ServletUtils.getStrutsForwardError(LoaderCore.log, e, pd, session, gd, mapping, 
					Costanti.OBJECT_NAME_ARCHIVI_IMPORTA_XML, Costanti.TIPO_OPERAZIONE_ARCHIVI_IMPORTA_XML);
		}
	}




}
