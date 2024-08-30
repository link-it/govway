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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			
			// Preparo il menu
			apsHelper.makeMenu();

			String idServizio = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			long idServizioLong = Long.parseLong(idServizio);
			String ruolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RUOLO  );
			String tipoFile = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_FILE  );

			String modificaAPI = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API);
					
			List<BinaryParameter> binaryParameterDocumenti = apsHelper.getBinaryParameters(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_THE_FILE);

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();

			String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null &&
				AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				gestioneFruitori = true;
			}

			// Prendo il nome
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizioLong);

			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			IDSoggetto idSoggettoFruitore = null;
			if(gestioneFruitori) {
				tipoSoggettoFruitore = apsHelper.getParametroTipoSoggetto(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
				nomeSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
				idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
			}
			
			String tipoProtocollo = apsCore.getProtocolloAssociatoTipoServizio(asps.getTipo());
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
			
			String tmpTitle = apsHelper.getLabelServizio(idSoggettoFruitore, gestioneFruitori, asps, tipoProtocollo);

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
				default:
					break;

				}
			}
			
			boolean find = false;
			if(tipoFile!=null && StringUtils.isNotEmpty(tipoFile) && tipiAmmessi!=null && tipiAmmessi.length>0) {
				for (String t : tipiAmmessi) {
					if(tipoFile.equals(t)) {
						find = true;
						break;
					}
				}
			}
			if(!find) {
				tipoFile = null;
			}

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session, request).getValue();
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				if(gestioneFruitori) {
					lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				} else {
					lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				}
				List<Parameter> listErogazioniChange = new ArrayList<>();
				Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
				listErogazioniChange.add(pIdServizio);
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
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (apsHelper.isEditModeInProgress()) {
				
					
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam );
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idServizio, null, null, null, null, tipoSoggettoFruitore, nomeSoggettoFruitore, dati);

				dati = apsHelper. addTipiAllegatiToDati(TipoOperazione.ADD, idServizio, ruolo, ruoli, tipiAmmessi,
						tipiAmmessiLabel, tipoFile, dati, modificaAPI, binaryParameterDocumenti);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziAllegatiCheckData(TipoOperazione.ADD,binaryParameterDocumenti,asps.getId(),pf);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam );

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idServizio, null, null, dati);

				dati = apsHelper. addTipiAllegatiToDati(TipoOperazione.ADD, idServizio, ruolo, ruoli, tipiAmmessi,
						tipiAmmessiLabel, tipoFile, dati, modificaAPI, binaryParameterDocumenti);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI, 
						ForwardParams.ADD());
			}

			//inserimento documento in accordo
			for (BinaryParameter binaryParameter : binaryParameterDocumenti) {
				Documento documento = new Documento();
				documento.setRuolo(RuoliDocumento.valueOf(ruolo).toString());
				documento.setByteContenuto(binaryParameter.getValue());
				documento.setFile(binaryParameter.getFilename());
				documento.setIdProprietarioDocumento(asps.getId());

				switch (RuoliDocumento.valueOf(ruolo)) {
					case allegato:
						documento.setTipo(binaryParameter.getFilename().substring(binaryParameter.getFilename().lastIndexOf('.')+1, binaryParameter.getFilename().length()));
						asps.addAllegato(documento);
						break;
					case specificaSemiformale:
						documento.setTipo(TipiDocumentoSemiformale.valueOf(tipoFile).getNome());
						asps.addSpecificaSemiformale(documento);
						break;
					case specificaSicurezza:
						documento.setTipo(TipiDocumentoSicurezza.valueOf(tipoFile).getNome());
						asps.addSpecificaSicurezza(documento);
						break;
					case specificaLivelloServizio:
						documento.setTipo(TipiDocumentoLivelloServizio.valueOf(tipoFile).getNome());
						asps.addSpecificaLivelloServizio(documento);
						break;
					default:
						break;
				}
			}

			// effettuo le operazioni
			apsCore.performUpdateOperation(userLogin, apsHelper.smista(), asps);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			List<Documento> lista = apsCore.serviziAllegatiList(idServizioLong, ricerca);

			apsHelper.prepareServiziAllegatiList(asps, ricerca, lista);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished( mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI,
					ForwardParams.ADD());
		}  
	}

	
}
