/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceParameter;
import org.openspcoop2.core.registry.ResourceRequest;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.constants.ParameterType;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * AccordiServizioParteComuneResourcesParametersChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneResourcesParametersChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		// Inizializzo GeneralData
		GeneralHelper generalHelper = new GeneralHelper(session);

		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			
			// Preparo il menu
			apcHelper.makeMenu();
			
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();

			String editMode = apcHelper.getParametroEditMode(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			
			String id = apcHelper.getParametroLong(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			long idAccordoLong = Long.parseLong(id);
			String nomeRisorsa = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_NOME);
			if (nomeRisorsa == null) {
				nomeRisorsa = "";
			}
			String statusS = apcHelper.getParametroInteger(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_RESPONSE_STATUS);
			Integer status = null;
			try {
				if(statusS!=null)
					status = Integer.parseInt(statusS);
			} catch(Exception e) {
				// ignore
			}
			String isReq = apcHelper.getParametroBoolean(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCE_REQUEST);
			boolean isRequest = ServletUtils.isCheckBoxEnabled(isReq);
			
			String descr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_PARAMETER_DESCRIZIONE);
			if (descr == null) {
				descr = "";
			}
			
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			String nome = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_PARAMETER_NOME);
			String tipo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_PARAMETER_TIPO);
			String restrizioni = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_PARAMETER_RESTRIZIONI);
			String tipoParametroS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_PARAMETER_TIPO_PARAMETRO);
			ParameterType tipoParametro =  StringUtils.isNotEmpty(tipoParametroS) ? ParameterType.toEnumConstant(tipoParametroS) : null;
			String requiredS = apcHelper.getParametroBoolean(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_PARAMETER_REQUIRED);
			boolean required = ServletUtils.isCheckBoxEnabled(requiredS);
			
			String idPar = apcHelper.getParametroInteger(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_PARAMETER_ID);
			int idParInt = Integer.parseInt(idPar);
			
			// Prendo il nome
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAccordoLong);
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 

			Resource risorsa = null;
			for (int j = 0; j < as.sizeResourceList(); j++) {
				risorsa = as.getResource(j);
				if (nomeRisorsa.equals(risorsa.getNome())) {
					break;
				}
			}
			
			if(risorsa==null) {
				throw new Exception("Risorsa con nome '"+nomeRisorsa+"' non trovata nell'accordo con id '"+idAccordoLong+"'");
			}
			
			Long idResponse = null;
			Long idResource = null;
			ResourceRequest resourceRequest = null;
			ResourceResponse resourceResponse = null;
			ResourceParameter resourceParameterOLD = null;
			List<ResourceParameter> parameterList = null;
			if(isRequest) {
				resourceRequest = risorsa.getRequest();
				idResource = risorsa.getId();
				if(resourceRequest != null)
					parameterList = resourceRequest.getParameterList();
			} else {
				if(risorsa.getResponseList() != null) {
					for (int i = 0; i < risorsa.getResponseList().size(); i++) {
						resourceResponse = risorsa.getResponse(i);
						if (resourceResponse.getStatus() == status) {
							idResponse = resourceResponse.getId();
							break;
						}
					}
					if(resourceResponse != null)
						parameterList = resourceResponse.getParameterList();
				}
			}
			
			if(parameterList != null && !parameterList.isEmpty()) {
				for (ResourceParameter resourceParameter : parameterList) {
					if(resourceParameter.getId().intValue() == idParInt) {
						resourceParameterOLD = resourceParameter;
						break;
					}
				}
			}
			
			String oldNome = null;
			ParameterType oldTipoParametro = null;
			if(resourceParameterOLD!=null) {
				oldNome = resourceParameterOLD.getNome();
				oldTipoParametro = resourceParameterOLD.getParameterType();
			}
			
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
			Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id);
			Parameter pNomeRisorsa = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_NOME, risorsa.getNome());
			Parameter pIsRequest = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCE_REQUEST, isRequest+"");
			Parameter pIdRisorsa = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_ID, risorsa.getId()+"");
			Parameter pResponseStatus = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_RESPONSE_STATUS, statusS);
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, request, false).getValue();
			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.CHANGE, as, tipoAccordo, labelASTitle, null, false);
			
			String labelRisorse = (isModalitaVistaApiCustom!=null && isModalitaVistaApiCustom.booleanValue()) ? AccordiServizioParteComuneCostanti.LABEL_RISORSE : AccordiServizioParteComuneCostanti.LABEL_RISORSE + " di " + labelASTitle;
			
			listaParams.add(new Parameter(labelRisorse, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_LIST, pIdAccordo, pTipoAccordo));
			
			String labelRisorsa = NamingUtils.getLabelResource(risorsa); 
			listaParams.add(new Parameter(labelRisorsa, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_CHANGE, pIdAccordo, pTipoAccordo,pIdRisorsa));
			
			if(!isRequest) {
				String labelResponse = AccordiServizioParteComuneCostanti.LABEL_RISPOSTE;
				listaParams.add(new Parameter(labelResponse, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_RISPOSTE_LIST, pIdAccordo, pTipoAccordo,pNomeRisorsa));
				
				String labelRisposta = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_STATUS +" "+
						(ApiResponse.isDefaultHttpReturnCode(status)? AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_RESPONSE_STATUS_DEFAULT : statusS);
				
				listaParams.add(new Parameter(labelRisposta, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_RISPOSTE_CHANGE, pIdAccordo, pTipoAccordo, pNomeRisorsa,pResponseStatus));
			}
			
			String labelParameters = AccordiServizioParteComuneCostanti.LABEL_PARAMETERS;
			
			listaParams.add(new Parameter(labelParameters, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_PARAMETERS_LIST, pIdAccordo, pTipoAccordo,pNomeRisorsa,pIsRequest, pResponseStatus));
			listaParams.add(new Parameter(oldNome, null));
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
		
			if(ServletUtils.isEditModeInProgress(editMode)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,listaParams); 

				// Prendo i dati dell'accordo
				if(nome == null){
					nome = resourceParameterOLD.getNome();
					descr = resourceParameterOLD.getDescrizione();
					required = resourceParameterOLD.isRequired();
					tipoParametro = oldTipoParametro;
					tipo = resourceParameterOLD.getTipo();
					restrizioni = resourceParameterOLD.getRestrizioni();
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = apcHelper.addAccordiResourceParameterToDati(tipoOp, dati, id, as.getStatoPackage(),tipoAccordo,
						 nomeRisorsa, isRequest, statusS, idParInt, nome, descr,  tipoParametro, tipo, restrizioni, required);

				pd.setDati(dati);

				if( apcHelper.isModalitaStandard() || (apcHelper.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())) ){
					pd.disableEditMode();
				}

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_PARAMETERS, ForwardParams.CHANGE());

			}
			
			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiResourceParameterCheckData(tipoOp, id, nomeRisorsa, isRequest, statusS,  nome, descr, tipoParametro, 
					tipo, restrizioni, required, idResource,idResponse,
					oldTipoParametro,oldNome);

			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,listaParams); 

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = apcHelper.addAccordiResourceParameterToDati(tipoOp, dati, id, as.getStatoPackage(),tipoAccordo,
						 nomeRisorsa, isRequest, statusS, idParInt, nome, descr,  tipoParametro, tipo, restrizioni, required);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_PARAMETERS, ForwardParams.CHANGE());
			}

			// Modifico i dati del parameter nel db
			ResourceParameter newParameter = new ResourceParameter();
			newParameter.setParameterType(tipoParametro);
			newParameter.setNome(nome);
			newParameter.setTipo(tipo);
			if(restrizioni!=null && !"".equals(restrizioni)) {
				newParameter.setRestrizioni(restrizioni);
			}
			newParameter.setDescrizione(descr);
			newParameter.setRequired(required);
			
			int idx = -1;			
			if(parameterList != null && !parameterList.isEmpty()) {
				for (int i = 0; i < parameterList.size(); i++) {
					ResourceParameter resourceParameter = parameterList.get(i);
					if(resourceParameter.getId().intValue() == idParInt) {
						idx = i;
						break;
					}
				}
				if(idx > -1) {
					parameterList.remove(idx);
					parameterList.add(idx, newParameter);
				}
			}
			
			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Devo rileggere l'accordo dal db, perche' altrimenti
			// manca l'id dei nuovi port-type
			as = apcCore.getAccordoServizioFull(idAccordoLong);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			risorsa = null;
			for (int j = 0; j < as.sizeResourceList(); j++) {
				risorsa = as.getResource(j);
				if (nomeRisorsa.equals(risorsa.getNome())) {
					break;
				}
			}
			
			if(risorsa==null) {
				throw new Exception("Risorsa con nome '"+nomeRisorsa+"' non trovata nell'accordo con id '"+idAccordoLong+"'");
			}
			
			idResponse = null;
			idResource = null;
			resourceRequest = null;
			resourceResponse = null;
			if(isRequest) {
				resourceRequest = risorsa.getRequest();
				idResource = risorsa.getId();
			} else {
				if(risorsa.getResponseList() != null) {
					for (int i = 0; i < risorsa.getResponseList().size(); i++) {
						resourceResponse = risorsa.getResponse(i);
						if (resourceResponse.getStatus() == status) {
							idResponse = resourceResponse.getId();
							break;
						}
					}
				}
			}
			

			List<ResourceParameter> lista = apcCore.accordiResourceParametersList(idResource, isRequest, idResponse, ricerca);

			apcHelper.prepareAccordiResourcesParametersList(id, as, lista, ricerca, tipoAccordo, isRequest, risorsa, resourceRequest, resourceResponse);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_PARAMETERS, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_PARAMETERS, ForwardParams.CHANGE());
		} 
	}
}
