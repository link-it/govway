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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.FileUploadForm;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * serviziAllegatiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaAllegatiChange extends Action {

	@SuppressWarnings("incomplete-switch")
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		try {

			FileUploadForm fileUpload = (FileUploadForm) form;

			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);

			String idAllegato = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ALLEGATO);
			String idServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String nomeDocumento = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_DOCUMENTO);
			int idAllegatoInt = Integer.parseInt(idAllegato);
			int idServizioInt = Integer.parseInt(idServizio);
			String tipoFile = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_FILE);

			FormFile ff = fileUpload.getTheFile();

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			ArchiviCore archiviCore = new ArchiviCore(apsCore);

			// Preparo il menu
			apsHelper.makeMenu();

			// Prendo il nome
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(new Long(idServizioInt));
			Servizio ss = asps.getServizio();

			String tmpTitle = ss.getTipo()+"/"+ss.getNome();
			// aggiorno tmpTitle
			String tmpVersione = asps.getVersione();
			if(apsCore.isShowVersioneAccordoServizioParteSpecifica()==false){
				tmpVersione = null;
			}
			tmpTitle = idAccordoFactory.getUriFromValues(asps.getNome(), 
					ss.getTipoSoggettoErogatore(), ss.getNomeSoggettoErogatore(), 
					tmpVersione);

			Documento doc = archiviCore.getDocumento(idAllegatoInt,false);

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();
				
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI_DI + tmpTitle,
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST, 
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio)
						));
				lstParm.add(new Parameter(nomeDocumento, null));
					
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idServizio, null, null, dati);

				dati = apsHelper.addInfoAllegatiToDati(TipoOperazione.CHANGE, idAllegato, asps, doc, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
						ForwardParams.CHANGE());
			}

			Documento toCheck = new Documento();
			toCheck.setId(doc.getId());
			toCheck.setRuolo(RuoliDocumento.valueOf(doc.getRuolo()).toString());
			toCheck.setByteContenuto(ff.getFileData());
			toCheck.setFile(ff.getFileName());
			toCheck.setIdProprietarioDocumento(asps.getId());
			toCheck.setOraRegistrazione(new Date());
			//tipo file
			switch (RuoliDocumento.valueOf(doc.getRuolo())) {
			case allegato:
				toCheck.setTipo(ff.getFileName().substring(ff.getFileName().lastIndexOf('.')+1, ff.getFileName().length()));
				break;
			case specificaSemiformale:
				toCheck.setTipo(TipiDocumentoSemiformale.toEnumConstant(tipoFile).getNome());
				break;
			case specificaSicurezza:
				toCheck.setTipo(TipiDocumentoSicurezza.toEnumConstant(tipoFile).getNome());
				break;
			case specificaLivelloServizio:
				toCheck.setTipo(TipiDocumentoLivelloServizio.toEnumConstant(tipoFile).getNome());
				break;
			}
			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziAllegatiCheckData(TipoOperazione.CHANGE,ff,toCheck);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();
				
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI_DI + tmpTitle,
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST, 
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio)
						));
				lstParm.add(new Parameter(nomeDocumento, null));
					
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idServizio, null, null, dati);

				dati = apsHelper.addInfoAllegatiToDati(TipoOperazione.CHANGE, idAllegato, asps, doc, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI, 
						ForwardParams.CHANGE());
			}



			switch (RuoliDocumento.valueOf(doc.getRuolo())) {
			case allegato:
				//rimuovo il vecchio doc dalla lista
				for (int i = 0; i < asps.sizeAllegatoList(); i++) {
					Documento documento = asps.getAllegato(i);						
					if(documento.getId().equals(doc.getId())){
						asps.removeAllegato(i);
						break;
					}
				}
				//aggiungo il nuovo
				asps.addAllegato(toCheck);

				break;

			case specificaSemiformale:

				for (int i = 0; i < asps.sizeSpecificaSemiformaleList(); i++) {
					Documento documento = asps.getSpecificaSemiformale(i);						
					if(documento.getId().equals(doc.getId())){
						asps.removeSpecificaSemiformale(i);
						break;
					}
				}
				//aggiungo il nuovo
				asps.addSpecificaSemiformale(toCheck);
				break;
			case specificaSicurezza:
				for (int i = 0; i < asps.sizeSpecificaSicurezzaList(); i++) {
					Documento documento = asps.getSpecificaSicurezza(i);						
					if(documento.getId().equals(doc.getId())){
						asps.removeSpecificaSicurezza(i);
						break;
					}
				}
				//aggiungo il nuovo
				asps.addSpecificaSicurezza(toCheck);
				break;
			case specificaLivelloServizio:
				for (int i = 0; i < asps.sizeSpecificaLivelloServizioList(); i++) {
					Documento documento = asps.getSpecificaLivelloServizio(i);						
					if(documento.getId().equals(doc.getId())){
						asps.removeSpecificaLivelloServizio(i);
						break;
					}
				}
				//aggiungo il nuovo
				asps.addSpecificaLivelloServizio(toCheck);
				break;
			}


			// effettuo le operazioni
			apsCore.performUpdateOperation(userLogin, apsHelper.smista(), asps);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			List<Documento> lista = apsCore.serviziAllegatiList(asps.getId().intValue(), ricerca);

			apsHelper.prepareServiziAllegatiList(asps, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished( mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
					ForwardParams.CHANGE());
		}  
	}

	
}
