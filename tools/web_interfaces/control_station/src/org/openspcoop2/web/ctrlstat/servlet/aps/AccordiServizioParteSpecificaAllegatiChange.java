/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.FileUploadForm;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
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

		try {

			FileUploadForm fileUpload = (FileUploadForm) form;

			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);

			String idAllegato = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ALLEGATO);
			String idServizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String nomeDocumento = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_DOCUMENTO);
			int idAllegatoInt = Integer.parseInt(idAllegato);
			int idServizioInt = Integer.parseInt(idServizio);
			String tipoFile = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_FILE);

			String modificaAPI = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API);
			
			FormFile ff = fileUpload.getTheFile();

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			ArchiviCore archiviCore = new ArchiviCore(apsCore);

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			// Preparo il menu
			apsHelper.makeMenu();

			// Prendo il nome
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Long.valueOf(idServizioInt));
			
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			IDSoggetto idSoggettoFruitore = null;
			if(gestioneFruitori) {
				tipoSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
				nomeSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
				idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
			}
			
			String tipoProtocollo = apsCore.getProtocolloAssociatoTipoServizio(asps.getTipo());
			
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
			
			String tmpTitle = null;
			if(gestioneFruitori) {
				tmpTitle = apsHelper.getLabelServizioFruizione(tipoProtocollo, idSoggettoFruitore, asps);
			}
			else {
				tmpTitle = apsHelper.getLabelServizioErogazione(tipoProtocollo, asps);
			}
			
			Documento doc = archiviCore.getDocumento(idAllegatoInt,false);
			
			List<Parameter> lstParam = new ArrayList<Parameter>();
			
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				if(gestioneFruitori) {
					lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				} else {
					lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				}
				List<Parameter> listErogazioniChange = new ArrayList<>();
				Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
				Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
				Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
				listErogazioniChange.add(pIdServizio);
				listErogazioniChange.add(pNomeServizio);
				listErogazioniChange.add(pTipoServizio);
				if(gestioneFruitori) {
					Parameter pNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, nomeSoggettoFruitore);
					Parameter pTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, tipoSoggettoFruitore);
					listErogazioniChange.add(pNomeSoggettoFruitore);
					listErogazioniChange.add(pTipoSoggettoFruitore);
				}
				if(modificaAPI!=null) {
					Parameter pModificaAPI = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API, modificaAPI);
					listErogazioniChange.add(pModificaAPI);
				}
				
				lstParam.add(new Parameter(tmpTitle, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, 
						listErogazioniChange.toArray(new Parameter[1])));
				
				lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_MODIFICA_SERVIZIO_INFO_GENERALI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, 
						listErogazioniChange.toArray(new Parameter[1])));
				
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST, 
						listErogazioniChange.toArray(new Parameter[1])));
			} else {
				if(gestioneFruitori) {
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				}
				else {
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				}
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI_DI + tmpTitle,
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST, 
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio)
						));
			}
			lstParam.add(new Parameter(nomeDocumento, null));

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (apsHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idServizio, null, null, null, null, tipoSoggettoFruitore, nomeSoggettoFruitore, dati);

				dati = apsHelper.addInfoAllegatiToDati(TipoOperazione.CHANGE, idAllegato, asps, doc, dati, modificaAPI);

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
			boolean isOk = apsHelper.serviziAllegatiCheckData(TipoOperazione.CHANGE,ff,toCheck,pf);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idServizio, null, null, dati);

				dati = apsHelper.addInfoAllegatiToDati(TipoOperazione.CHANGE, idAllegato, asps, doc, dati, modificaAPI);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI, 
						ForwardParams.CHANGE());
			}



			AccordiServizioParteSpecificaUtilities.sostituisciDocumentoAsps(asps, doc, toCheck);


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
