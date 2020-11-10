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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * AccordiServizioParteComuneResourcesRisposteChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneResourcesRisposteChange extends Action {

	private String editMode = null;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE; 

		try {
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			this.editMode = apcHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idInt = Integer.parseInt(id);
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			String nomeAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME);
			String nomeRisorsa = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_NOME);
			String descr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_RESPONSE_DESCRIZIONE);
			if (descr == null) {
				descr = "";
			}
			
			String statusS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_RESPONSE_STATUS);
			int status = -1;
			if(statusS==null || "".equals(statusS)) {
				status = ApiResponse.getDefaultHttpReturnCode();
			}
			else {
				try {
					status = Integer.parseInt(statusS);
				} catch(Exception e) {}
			}
			
			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(Long.valueOf(idInt));
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 

			ResourceResponse resourceResponseOLD = null;
			Resource risorsa = null;
			for (int i = 0; i < as.sizeResourceList(); i++) {
				Resource res = as.getResource(i);
				if (nomeRisorsa.equals(res.getNome())) {
					risorsa = res;
					break;
				}
			}
			
			if(risorsa.getResponseList() != null) {
				for (int i = 0; i < risorsa.getResponseList().size(); i++) {
					ResourceResponse rr = risorsa.getResponse(i);
					if(rr.getStatus() == status) {
						resourceResponseOLD = rr;
						break;
					}
				}
			}
			
			IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
			String uriAS = idAccordoFactory.getUriFromAccordo(as);
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
			Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id);
			Parameter pNomeAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uriAS);
			Parameter pIdRisorsa = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_ID, risorsa.getId()+"");
			Parameter pNomeRisorsa = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_NOME, nomeRisorsa);
			
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, false);
			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.LIST, as, tipoAccordo, labelASTitle, null, false);
			
			String labelRisorse = isModalitaVistaApiCustom ? AccordiServizioParteComuneCostanti.LABEL_RISORSE : AccordiServizioParteComuneCostanti.LABEL_RISORSE + " di " + labelASTitle;
			
			listaParams.add(new Parameter(labelRisorse, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo));
			
			String labelRisorsa = NamingUtils.getLabelResource(risorsa);
			listaParams.add(new Parameter(labelRisorsa, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_CHANGE, pIdAccordo, pNomeAccordo, pTipoAccordo,pIdRisorsa));
			
			String labelResponse = AccordiServizioParteComuneCostanti.LABEL_RISPOSTE ;
			listaParams.add(new Parameter(labelResponse, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_RISPOSTE_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo,pNomeRisorsa));
			
			String labelRisposta = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_STATUS+" "+
					(ApiResponse.isDefaultHttpReturnCode(status)? AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_STATUS_DEFAULT : statusS);
			listaParams.add(new Parameter(labelRisposta, null));

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(ServletUtils.isEditModeInProgress(this.editMode)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// Prendo i dati dell'accordo
				if(resourceResponseOLD != null){
					descr = resourceResponseOLD.getDescrizione();
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apcHelper.addAccordiResourceResponseToDati(tipoOp, dati, id, nomeAccordo, tipoAccordo, as.getStatoPackage(), nomeRisorsa, descr, statusS);

				pd.setDati(dati);

				if(  apcHelper.isModalitaStandard() || (apcHelper.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())) ){
					pd.disableEditMode();
				}

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_RISPOSTE, ForwardParams.CHANGE());

			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiResourceResponseCheckData(tipoOp, id, risorsa.getId().intValue(), nomeRisorsa, statusS, descr);

			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati = apcHelper.addAccordiResourceResponseToDati(tipoOp, dati, id, nomeAccordo, tipoAccordo, as.getStatoPackage(), nomeRisorsa, descr, statusS);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_RISPOSTE, ForwardParams.CHANGE());
			}

			// Modifico i dati del port-type nel db
			Resource res = null;
			for (int i = 0; i < as.sizeResourceList(); i++) {
				res = as.getResource(i);
				if (nomeRisorsa.equals(res.getNome())) {
					break;
				}
			}
			
			ResourceResponse resourceResponse = null;
			
			if(res.getResponseList() != null) {
				for (int i = 0; i < res.getResponseList().size(); i++) {
					ResourceResponse rr = res.getResponse(i);
					if(rr.getStatus() == status) {
						resourceResponse = rr;
						res.removeResponse(i);
						break;
					}
				}
			}
			
			ResourceResponse newResponse = new ResourceResponse();
			newResponse.setStatus(status);
			newResponse.setDescrizione(descr);
			newResponse.setParameterList(resourceResponse.getParameterList());
			newResponse.setRepresentationList(resourceResponse.getRepresentationList());
						
			res.addResponse(newResponse);

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			// Devo rileggere l'accordo dal db, perche' altrimenti
			// manca l'id dei nuovi port-type
			as = apcCore.getAccordoServizioFull(Long.valueOf(idInt));
			
			risorsa = null;
			for (int j = 0; j < as.sizeResourceList(); j++) {
				risorsa = as.getResource(j);
				if (nomeRisorsa.equals(risorsa.getNome())) {
					break;
				}
			}
			
			List<ResourceResponse> lista = apcCore.accordiResourceResponseList(risorsa.getId(), ricerca);

			apcHelper.prepareAccordiResourcesResponseList(ricerca, lista, id, as, tipoAccordo, risorsa);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_RISPOSTE, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_RISPOSTE, ForwardParams.CHANGE());
		} 
	}
}
