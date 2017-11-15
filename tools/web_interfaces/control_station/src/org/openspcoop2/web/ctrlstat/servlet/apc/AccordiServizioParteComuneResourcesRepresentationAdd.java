/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceRepresentation;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.RepresentationType;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.archive.APIUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiPorttypeAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author: pintori $
 * @version $Rev: 12608 $, $Date: 2017-01-18 16:42:09 +0100(mer, 18 gen 2017) $
 * 
 */
public final class AccordiServizioParteComuneResourcesRepresentationAdd extends Action {

	private IProtocolFactory<?> protocolFactory= null;
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

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD; 

		try {
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);

			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			this.editMode = apcHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

			String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idInt = Integer.parseInt(id);
			String nomeRisorsa = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_NOME);
			if (nomeRisorsa == null) {
				nomeRisorsa = "";
			}
			String statusS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_RESPONSE_STATUS);
			Integer status = null;
			try {
				if(statusS!=null)
					status = Integer.parseInt(statusS);
			} catch(Exception e) {}
			String isReq = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCE_REQUEST);
			boolean isRequest = ServletUtils.isCheckBoxEnabled(isReq);
			
			String descr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_REPRESENTATION_DESCRIZIONE);
			if (descr == null) {
				descr = "";
			}

			String messageProcessorS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_REPRESENTATION_MESSAGE_TYPE);
			MessageType messageType = (StringUtils.isNotEmpty(messageProcessorS) && !messageProcessorS.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT)) ? MessageType.valueOf(messageProcessorS) : null;
		
			String mediaType = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_REPRESENTATION_MEDIA_TYPE);
			String tipoS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_REPRESENTATION_TIPO);
			RepresentationType tipo = StringUtils.isNotEmpty(tipoS) ? RepresentationType.toEnumConstant(tipoS) : null;
			
			
			
			String path = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_PATH);
			if (path == null) {
				path = "";
			}
			String httpMethod = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_HTTP_METHOD); 
			
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome
			AccordoServizioParteComune as = apcCore.getAccordoServizio(new Long(idInt));
			String uriAS = idAccordoFactory.getUriFromAccordo(as);
			IDAccordo idAs = idAccordoFactory.getIDAccordoFromAccordo(as);

			String protocollo = null;
			//calcolo del protocollo implementato dall'accordo
			if(as != null){
				IdSoggetto soggettoReferente = as.getSoggettoReferente();
				String tipoSoggettoReferente = soggettoReferente.getTipo();
				protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoReferente);
			} else {
				protocollo = apcCore.getProtocolloDefault();
			}

			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			
			ServiceBinding serviceBinding = null;
			//calcolo del serviceBinding dall'accordo
			if(as != null){
				serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			} else {
				serviceBinding = apcCore.getDefaultServiceBinding(this.protocolFactory);
			}

			IDResource idRisorsa = new IDResource();
			idRisorsa.setIdAccordo(idAs);
			idRisorsa.setNome(nomeRisorsa);

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(ServletUtils.isEditModeInProgress(this.editMode)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(AccordiServizioParteComuneCostanti.LABEL_RISORSE + " di " + uriAS, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_LIST+"?"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

//				dati = apcHelper.addAccordiResourceToDati(tipoOp, dati, id, nomeRisorsa, descr, path,httpMethod, messageType,  as.getStatoPackage(),tipoAccordo,protocollo, 
//						this.protocolFactory,serviceBinding,messageTypeRequest,messageTypeResponse);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES, ForwardParams.ADD());
			}

			// 1. se il path non inizia per '/' aggiungo all'inizio della stringa
			String pathNormalizzato = path.trim();
			if(!pathNormalizzato.startsWith("/"))
				pathNormalizzato = "/" + pathNormalizzato;
			
			// 2. se il nome non e; stato impostato allora genero un nome automatico
			String nomeRisorsaProposto = nomeRisorsa;
			if(StringUtils.isEmpty(nomeRisorsaProposto)) {
				nomeRisorsaProposto = APIUtils.normalizeResourceName(HttpMethod.toEnumConstant(httpMethod), pathNormalizzato);
			}
			
			// Controlli sui campi immessi
			boolean isOk = false; // apcHelper.accordiResourceCheckData(tipoOp, id, nomeRisorsa, nomeRisorsaProposto, pathNormalizzato, httpMethod, messageType);

			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(AccordiServizioParteComuneCostanti.LABEL_RISORSE + " di " + uriAS, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_LIST+"?"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

//				dati = apcHelper.addAccordiResourceToDati(tipoOp, dati, id, nomeRisorsa, descr,path,httpMethod, messageType, as.getStatoPackage(),tipoAccordo,protocollo, this.protocolFactory,serviceBinding,messageTypeRequest,messageTypeResponse);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES, ForwardParams.ADD());
			}

			// Inserisco la risorsa nel db

			Resource newRes = new Resource();
			newRes.setNome(nomeRisorsaProposto);
			newRes.setDescrizione(descr);
			newRes.setPath(pathNormalizzato);
			newRes.set_value_method(httpMethod);
			newRes.setMessageType(apcCore.fromMessageMessageType(messageType));
			
			
//			ResourceRepresentation newRepresentation = new ResourceRepresentation();
//			newRepresentation.setMessageType(apcCore.fromMessageMessageType(messageType));
//			newRepresentation.setDescrizione(descr);
//			newRepresentation.setJson(json);
//			newRepresentation.setXml(xml);
//			newRepresentation.setNome(nome);
//			newRepresentation.setMediaType(mediaType);
			
			
		
			as.addResource(newRes);
			
			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<Resource> lista = apcCore.accordiResourceList(idInt, ricerca);

			// Devo rileggere l'accordo dal db, perche' altrimenti
			// manca l'id delle risorse
			as = apcCore.getAccordoServizio(new Long(idInt));

			apcHelper.prepareAccordiResourcesList(id,as, lista, ricerca,tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES, ForwardParams.ADD());
		}
	}
}
