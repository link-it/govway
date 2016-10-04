/*
 * OpenSPCoop - Customizable API Gateway 
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
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * serviziAllegatiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaAllegatiAdd extends Action {

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

			String idServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			int idServizioInt = Integer.parseInt(idServizio);
			String ruolo = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RUOLO  );
			String tipoFile = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_FILE  );

			FormFile ff = fileUpload.getTheFile();

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();

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

			String[] ruoli = {RuoliDocumento.allegato.toString(),RuoliDocumento.specificaSemiformale.toString(),RuoliDocumento.specificaSicurezza.toString(),RuoliDocumento.specificaLivelloServizio.toString()};

			String[] tipiAmmessi = null;
			String[] tipiAmmessiLabel = null;

			if(ruolo!=null && !"".equals(ruolo)){
				switch (RuoliDocumento.valueOf(ruolo)) {
				case allegato:
					//non ci sono vincoli
					break;
				case specificaSicurezza:
					tipiAmmessi = TipiDocumentoSicurezza.toEnumNameArray();
					tipiAmmessiLabel=TipiDocumentoSicurezza.toStringArray();
					break;
				case specificaSemiformale:
					tipiAmmessi = TipiDocumentoSemiformale.toEnumNameArray();
					tipiAmmessiLabel=TipiDocumentoSemiformale.toStringArray();
					break;
				case specificaLivelloServizio:
					tipiAmmessi = TipiDocumentoLivelloServizio.toEnumNameArray();
					tipiAmmessiLabel=TipiDocumentoLivelloServizio.toStringArray();
					break;

				}
			}

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
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
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));
					
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idServizio, null, null, dati);

				dati = apsHelper. addTipiAllegatiToDati(TipoOperazione.ADD, idServizio, ruolo, ruoli, tipiAmmessi,
						tipiAmmessiLabel, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
						ForwardParams.ADD());
			}


			Documento documento = new Documento();
			documento.setRuolo(RuoliDocumento.valueOf(ruolo).toString());
			documento.setByteContenuto(ff.getFileData());
			documento.setFile(ff.getFileName());
			switch (RuoliDocumento.valueOf(ruolo)) {
			case allegato:
				documento.setTipo(ff.getFileName().substring(ff.getFileName().lastIndexOf('.')+1, ff.getFileName().length()));
				break;
			case specificaSemiformale:
				documento.setTipo(TipiDocumentoSemiformale.valueOf(tipoFile).getNome());
				break;
			case specificaSicurezza:
				documento.setTipo(TipiDocumentoSicurezza.valueOf(tipoFile).getNome());
				break;
			case specificaLivelloServizio:
				documento.setTipo(TipiDocumentoLivelloServizio.valueOf(tipoFile).getNome());
				break;
			}
			documento.setIdProprietarioDocumento(asps.getId());

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziAllegatiCheckData(TipoOperazione.ADD,ff,documento);
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
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));
					
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idServizio, null, null, dati);

				dati = apsHelper. addTipiAllegatiToDati(TipoOperazione.ADD, idServizio, ruolo, ruoli, tipiAmmessi,
						tipiAmmessiLabel, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI, 
						ForwardParams.ADD());
			}

			//inserimento documento in accordo

			switch (RuoliDocumento.valueOf(ruolo)) {
			case allegato:
				asps.addAllegato(documento);
				break;
			case specificaSemiformale:
				asps.addSpecificaSemiformale(documento);
				break;
			case specificaSicurezza:
				asps.addSpecificaSicurezza(documento);
				break;
			case specificaLivelloServizio:
				asps.addSpecificaLivelloServizio(documento);
				break;
			}


			// effettuo le operazioni
			apsCore.performUpdateOperation(userLogin, apsHelper.smista(), asps);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			List<Documento> lista = apsCore.serviziAllegatiList(idServizioInt, ricerca);

			apsHelper.prepareServiziAllegatiList(asps, ricerca, lista);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished( mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
					ForwardParams.ADD());
		}  
	}

	
}
