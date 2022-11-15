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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoCoordinamento;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviHelper;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiAllegatiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneAllegatiAdd extends Action {

	@SuppressWarnings("incomplete-switch")
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);
		
		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = (String) ServletUtils.getUserLoginFromSession(session);

		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);
			
			Boolean isShowAccordiCooperazione = ServletUtils.getObjectFromSession(request, session, Boolean.class, CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_COOPERAZIONE);
			
//			FileUploadForm fileUpload = (FileUploadForm) form;

			String idAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idAccordoInt = Integer.parseInt(idAccordo);
			String ruolo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO);
			String tipoFile = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_TIPO_FILE);
			
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

//			FormFile ff = fileUpload.getTheFile();
			
			List<BinaryParameter> binaryParameterDocumenti = apcHelper.getBinaryParameters(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_DOCUMENTO);
			
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(Long.valueOf(idAccordoInt));
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 
			
			String[] ruoli = null;
			if(isShowAccordiCooperazione){
				ruoli = as.getServizioComposto() ==null ? new String[2] : new String[3];
			}else{
				ruoli = new String[2];
			}
			
			
			ruoli[0]=RuoliDocumento.allegato.toString();
			ruoli[1]=RuoliDocumento.specificaSemiformale.toString();
			
			if(isShowAccordiCooperazione){
				if(as.getServizioComposto()!=null) ruoli[2]=RuoliDocumento.specificaCoordinamento.toString();
			}
			
			String[] tipiAmmessi = null;
			String[] tipiAmmessiLabel = null;
			
			if(ruolo!=null && !"".equals(ruolo)){
				switch (RuoliDocumento.valueOf(ruolo)) {
					case allegato:
						//non ci sono vincoli
						break;
					case specificaCoordinamento:
						tipiAmmessi = TipiDocumentoCoordinamento.toEnumNameArray();
						tipiAmmessiLabel=TipiDocumentoCoordinamento.toStringArray();
						break;
					case specificaSemiformale:
						tipiAmmessi = TipiDocumentoSemiformale.toEnumNameArray();
						tipiAmmessiLabel=TipiDocumentoSemiformale.toStringArray();
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
			
			IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
			String uri = idAccordoFactory.getUriFromAccordo(as);
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
			Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, idAccordo);
			Parameter pNomeAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uri);
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(as.getSoggettoReferente().getTipo());
			
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, request, false).getValue();
			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.ADD, as, tipoAccordo, labelASTitle, null, false);
			
			String labelAllegati = isModalitaVistaApiCustom ? AccordiServizioParteComuneCostanti.LABEL_ALLEGATI : AccordiServizioParteComuneCostanti.LABEL_ALLEGATI + " di " + labelASTitle;
			 
			listaParams.add(new Parameter(labelAllegati, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo));
			listaParams.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));
			
			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(apcHelper.isEditModeInProgress()){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// preparo i campi
				Vector<Object> dati = new Vector<Object>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiAllegatiToDati(dati,TipoOperazione.ADD,idAccordo,
						ruolo,ruoli,tipiAmmessi,tipiAmmessiLabel,tipoAccordo,tipoFile,
						null,null,as,null,null, binaryParameterDocumenti);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = archiviHelper.accordiAllegatiCheckData(TipoOperazione.ADD,binaryParameterDocumenti,as.getId(),ProprietariDocumento.accordoServizio, pf);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// preparo i campi
				Vector<Object> dati = new Vector<Object>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiAllegatiToDati(dati,TipoOperazione.ADD,idAccordo,
						ruolo,ruoli,tipiAmmessi,tipiAmmessiLabel,tipoAccordo,tipoFile,
						null,null,as,null,null, binaryParameterDocumenti);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.ADD());
			}

			//inserimento documenti in accordo
			for (BinaryParameter binaryParameter : binaryParameterDocumenti) {
				Documento documento = new Documento();
				documento.setRuolo(RuoliDocumento.valueOf(ruolo).toString());
				documento.setByteContenuto(binaryParameter.getValue());
				documento.setFile(binaryParameter.getFilename());
				documento.setIdProprietarioDocumento(as.getId());

				switch (RuoliDocumento.valueOf(ruolo)) {
					case allegato:
						documento.setTipo(binaryParameter.getFilename().substring(binaryParameter.getFilename().lastIndexOf('.')+1, binaryParameter.getFilename().length()));
						as.addAllegato(documento);
						break;
					case specificaSemiformale:
						documento.setTipo(TipiDocumentoSemiformale.valueOf(tipoFile).getNome());
						as.addSpecificaSemiformale(documento);
						break;
					case specificaCoordinamento:
						documento.setTipo(TipiDocumentoCoordinamento.valueOf(tipoFile).getNome());
						AccordoServizioParteComuneServizioComposto assc = as.getServizioComposto();
						if(assc==null)
							assc=new AccordoServizioParteComuneServizioComposto();
						assc.addSpecificaCoordinamento(documento);
						as.setServizioComposto(assc);
						break;
				}
			}
			
			
			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			List<Documento> lista = apcCore.accordiAllegatiList(idAccordoInt, ricerca);

			apcHelper.prepareAccordiAllegatiList(as, ricerca, lista, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.ADD());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.ADD());
		}
	}
}
