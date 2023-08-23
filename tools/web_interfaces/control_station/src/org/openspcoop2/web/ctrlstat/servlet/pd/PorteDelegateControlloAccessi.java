/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAutorizzazioneToken;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/***
 * 
 * PorteDelegateControlloAccessi
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateControlloAccessi extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		boolean isPortaDelegata = true;

		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);
			Boolean confPers = ServletUtils.getObjectFromSession(request, session, Boolean.class, CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);

			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idSoggFruitore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione= porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";
			
			String autenticazione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE );
			String autenticazioneOpzionale = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE );
			String autenticazionePrincipalTipo = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			TipoAutenticazionePrincipal autenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(autenticazionePrincipalTipo, false);
			List<String> autenticazioneParametroList = porteDelegateHelper.convertFromDataElementValue_parametroAutenticazioneList(autenticazione, autenticazionePrincipal);
			String autenticazioneCustom = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM );
			String autorizzazione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			
			String autorizzazioneAutenticati = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
			String autorizzazioneAutenticatiToken = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_TOKEN);
			String autorizzazioneRuoliToken = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI_TOKEN);
			String autorizzazioneRuoliTipologiaToken = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA_TOKEN);
			String autorizzazioneRuoliMatchToken = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH_TOKEN);
			
			String autorizzazioneContenuti = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI);
			String autorizzazioneContenutiStato = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_STATO);
			String autorizzazioneContenutiProperties = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROPERTIES);


			String applicaModificaS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MODIFICA);
			boolean applicaModifica = ServletUtils.isCheckBoxEnabled(applicaModificaS);

			String gestioneToken = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			String gestioneTokenPolicy = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			String gestioneTokenOpzionale = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE);
			String gestioneTokenValidazioneInput = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
			String gestioneTokenIntrospection = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
			String gestioneTokenUserInfo = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
			String gestioneTokenTokenForward = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			
			String autenticazioneTokenIssuer = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
			String autenticazioneTokenClientId = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			String autenticazioneTokenSubject = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
			String autenticazioneTokenUsername = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
			String autenticazioneTokenEMail = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
			
			String autorizzazioneToken = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
			String autorizzazioneTokenOptions = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			
			BinaryParameter allegatoXacmlPolicy = porteDelegateHelper.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			
			String identificazioneAttributiStato = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_STATO);
			String [] attributeAuthoritySelezionate = porteDelegateHelper.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY);
			String attributeAuthorityAttributi = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI);
			
			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);

			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			if(portaDelegata==null) {
				throw new Exception("PortaDelegata con id '"+idInt+"' non trovata");
			}
			String idporta = portaDelegata.getNome();
			
			List<String> ruoli = new ArrayList<>();
			if(portaDelegata!=null && portaDelegata.getRuoli()!=null && portaDelegata.getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < portaDelegata.getRuoli().sizeRuoloList(); i++) {
					ruoli.add(portaDelegata.getRuoli().getRuolo(i).getNome());
				}
			}
			
			int numRuoli = 0;
			if(portaDelegata.getRuoli()!=null){
				numRuoli = portaDelegata.getRuoli().sizeRuoloList();
			}
			int numScope = 0;
			if(portaDelegata.getScope()!=null){
				numScope = portaDelegata.getScope().sizeScopeList();
			}
			
			int sizeFruitori = 0; 
			if(portaDelegata.getServizioApplicativoList() !=null){
				sizeFruitori = portaDelegata.sizeServizioApplicativoList();
			}
			
			int numAutenticatiToken = 0; 
			if(portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getServiziApplicativi()!=null){
				numAutenticatiToken = portaDelegata.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList();
			}
			
			int numRuoliToken = 0; 
			if(portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getRuoli()!=null){
				numRuoliToken = portaDelegata.getAutorizzazioneToken().getRuoli().sizeRuoloList();
			}
			
			int numAutenticazioneCustomPropertiesList = portaDelegata.sizeProprietaAutenticazioneList();
			int numAutorizzazioneCustomPropertiesList = portaDelegata.sizeProprietaAutorizzazioneList();
			int numAutorizzazioneContenutiCustomPropertiesList = portaDelegata.sizeProprietaAutorizzazioneContenutoList();
			String oldAutorizzazioneContenuto = portaDelegata.getAutorizzazioneContenuto() ;
			String oldAutorizzazioneContenutoStato = StatoFunzionalita.DISABILITATO.getValue();
					
			boolean oldAutenticazioneCustom = portaDelegata.getAutenticazione() != null && !TipoAutenticazione.getValues().contains(portaDelegata.getAutenticazione());
			boolean oldAutorizzazioneContenutiCustom = false;
			if(oldAutorizzazioneContenuto != null) {
				if(oldAutorizzazioneContenuto.equals(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN)) {
					oldAutorizzazioneContenutoStato = StatoFunzionalita.ABILITATO.getValue();
				} else { // custom
					oldAutorizzazioneContenutiCustom = true;
					oldAutorizzazioneContenutoStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM;
				}
			}
			
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getServizio().getTipo(), portaDelegata.getServizio().getNome(), 
					portaDelegata.getSoggettoErogatore().getTipo(), portaDelegata.getSoggettoErogatore().getNome(), 
					portaDelegata.getServizio().getVersione());
			AccordoServizioParteSpecifica asps = apsCore.getServizio(idServizio,false);
			AccordoServizioParteComuneSintetico aspc = apcCore.getAccordoServizioSintetico(porteDelegateHelper.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = porteDelegateCore.toMessageServiceBinding(aspc.getServiceBinding()); 

			boolean isSupportatoAutenticazione = true; // sempre nelle porte delegate
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idSoggFruitore);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			Parameter pTokenAuthorization = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TOKEN_AUTHORIZATION, true+"");

			Parameter[] urlParmsAutorizzazioneAutenticati = {  pId,pIdSoggetto,pIdAsps,pIdFrizione  };
			Parameter urlAutorizzazioneAutenticatiParam= new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST , urlParmsAutorizzazioneAutenticati);
			String urlAutorizzazioneAutenticati = urlAutorizzazioneAutenticatiParam.getValue();
			
			Parameter[] urlParmsAutorizzazioneRuoli = {  pId,pIdSoggetto,pIdAsps,pIdFrizione };
			Parameter urlAutorizzazioneRuoliParam = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RUOLI_LIST , urlParmsAutorizzazioneRuoli);
			String urlAutorizzazioneRuoli = urlAutorizzazioneRuoliParam.getValue();
			
			Parameter[] urlParmsAutorizzazioneAutenticatiToken = {  pId,pIdSoggetto,pIdAsps,pIdFrizione,pTokenAuthorization  };
			Parameter urlAutorizzazioneAutenticatiParamToken= new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST , urlParmsAutorizzazioneAutenticatiToken);
			String urlAutorizzazioneAutenticatiToken = urlAutorizzazioneAutenticatiParamToken.getValue();
			
			Parameter[] urlParmsAutorizzazioneRuoliToken = {  pId,pIdSoggetto,pIdAsps,pIdFrizione,pTokenAuthorization };
			Parameter urlAutorizzazioneRuoliParamToken = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RUOLI_LIST , urlParmsAutorizzazioneRuoliToken);
			String urlAutorizzazioneRuoliToken = urlAutorizzazioneRuoliParamToken.getValue();
			
			Parameter[] urlParmsAutorizzazioneScope = {  pId,pIdSoggetto,pIdAsps,pIdFrizione };
			Parameter urlAutorizzazioneScopeParam = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SCOPE_LIST , urlParmsAutorizzazioneScope);
			String urlAutorizzazioneScope = urlAutorizzazioneScopeParam.getValue();
			
			Parameter[] urlParmsAutenticazioneCustomProperties = { pId,pIdSoggetto,pIdAsps,pIdFrizione };
			Parameter urlAutenticazioneCustomPropertiesParam = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AUTENTICAZIONE_CUSTOM_PROPERTIES_LIST , urlParmsAutenticazioneCustomProperties); 
			String urlAutenticazioneCustomProperties = urlAutenticazioneCustomPropertiesParam.getValue();
			
			Parameter[] urlParmsAutorizzazioneCustomProperties = {  pId,pIdSoggetto,pIdAsps,pIdFrizione }; 
			Parameter urlAutorizzazioneCustomPropertiesParam = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM_PROPERTIES_LIST , urlParmsAutorizzazioneCustomProperties);
			String urlAutorizzazioneCustomProperties = urlAutorizzazioneCustomPropertiesParam.getValue();
			
			Parameter[] urlParmsAutorizzazioneContenutiCustomProperties = {  pId,pIdSoggetto,pIdAsps, pIdFrizione };
			Parameter urlAutorizzazioneContenutiCustomPropertiesParam = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_CUSTOM_PROPERTIES_LIST , urlParmsAutorizzazioneContenutiCustomProperties); 
			String urlAutorizzazioneContenutiCustomPropertiesList = urlAutorizzazioneContenutiCustomPropertiesParam.getValue();
			
			String servletChiamante = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI;
			
			// Token Policy
			List<GenericProperties> gestorePolicyTokenList = confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN, null);
			String [] policyLabels = new String[gestorePolicyTokenList.size() + 1];
			String [] policyValues = new String[gestorePolicyTokenList.size() + 1];
			
			policyLabels[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			policyValues[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			
			for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
			GenericProperties genericProperties = gestorePolicyTokenList.get(i);
				policyLabels[(i+1)] = genericProperties.getNome();
				policyValues[(i+1)] = genericProperties.getNome();
			}
			
			String protocollo = ProtocolFactoryManager.getInstance().getProtocolByServiceType(asps.getTipo());
			
			// La XACML Policy, se definita nella porta delegata può solo essere cambiata, non annullata.
			if(allegatoXacmlPolicy!=null && allegatoXacmlPolicy.getValue()==null &&
				portaDelegata.getXacmlPolicy()!=null && !"".equals(portaDelegata.getXacmlPolicy())) {
				allegatoXacmlPolicy.setValue(portaDelegata.getXacmlPolicy().getBytes());
			}
			
			// AttributeAuthority
			List<GenericProperties> attributeAuthorityList = confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_ATTRIBUTE_AUTHORITY, null);
			String [] attributeAuthorityLabels = new String[attributeAuthorityList.size()];
			String [] attributeAuthorityValues = new String[attributeAuthorityList.size()];
			for (int i = 0; i < attributeAuthorityList.size(); i++) {
				GenericProperties genericProperties = attributeAuthorityList.get(i);
				attributeAuthorityLabels[i] = genericProperties.getNome();
				attributeAuthorityValues[i] = genericProperties.getNome();
			}
			
			// postback
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			if(postBackElementName != null) {
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE) &&
					autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM)) {
					autenticazioneCustom = "";
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_STATO)) {
					if(autorizzazioneContenutiStato.equals(StatoFunzionalita.DISABILITATO.getValue()) || autorizzazioneContenutiStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM)) {
						autorizzazioneContenuti = "";
					}
					if(autorizzazioneContenutiStato.equals(StatoFunzionalita.ABILITATO.getValue())) {
						autorizzazioneContenuti = CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN;
					}
				}
			}
			
			if(	porteDelegateHelper.isEditModeInProgress() && !applicaModifica){

				if (autenticazione == null) {
					autenticazione = portaDelegata.getAutenticazione();
					if (autenticazione != null &&
							!TipoAutenticazione.getValues().contains(autenticazione)) {
						autenticazioneCustom = autenticazione;
						autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
					}
					
					autenticazionePrincipal = porteDelegateCore.getTipoAutenticazionePrincipal(portaDelegata.getProprietaAutenticazioneList());
					autenticazioneParametroList = porteDelegateCore.getParametroAutenticazione(autenticazione, portaDelegata.getProprietaAutenticazioneList());
				}
				if(autenticazioneOpzionale==null){
					autenticazioneOpzionale = "";
					if(portaDelegata.getAutenticazioneOpzionale()!=null &&
						portaDelegata.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
						autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
					}
				}
				if (autorizzazione == null) {
					if (portaDelegata.getAutorizzazione() != null &&
							!TipoAutorizzazione.getAllValues().contains(portaDelegata.getAutorizzazione())) {
						autorizzazioneCustom = portaDelegata.getAutorizzazione();
						autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
					}
					else{
						autorizzazione = AutorizzazioneUtilities.convertToStato(portaDelegata.getAutorizzazione());
						if(TipoAutorizzazione.isAuthenticationRequired(portaDelegata.getAutorizzazione()))
							autorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(portaDelegata.getAutorizzazione()))
							autorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						autorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(portaDelegata.getAutorizzazione()).getValue();
					}
				}
				
				if (ruoloMatch == null &&
					portaDelegata.getRuoli()!=null && portaDelegata.getRuoli().getMatch()!=null){
					ruoloMatch = portaDelegata.getRuoli().getMatch().getValue();
				}
				
				if(autorizzazioneAutenticatiToken==null &&
					portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getAutorizzazioneApplicativi()!=null) {
					autorizzazioneAutenticatiToken = StatoFunzionalita.ABILITATO.equals(portaDelegata.getAutorizzazioneToken().getAutorizzazioneApplicativi()) ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
				}
				
				if(autorizzazioneRuoliToken==null &&
					portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getAutorizzazioneRuoli()!=null) {
					autorizzazioneRuoliToken = StatoFunzionalita.ABILITATO.equals(portaDelegata.getAutorizzazioneToken().getAutorizzazioneRuoli()) ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
				}
				if(autorizzazioneRuoliTipologiaToken==null &&
					portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getTipologiaRuoli()!=null) {
					autorizzazioneRuoliTipologiaToken = portaDelegata.getAutorizzazioneToken().getTipologiaRuoli().getValue();
				}
				if (autorizzazioneRuoliMatchToken == null &&
					portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getRuoli()!=null && portaDelegata.getAutorizzazioneToken().getRuoli().getMatch()!=null){
					autorizzazioneRuoliMatchToken = portaDelegata.getAutorizzazioneToken().getRuoli().getMatch().getValue();
				}
				
				if(autorizzazioneContenutiStato==null){
					autorizzazioneContenuti = portaDelegata.getAutorizzazioneContenuto();
					
					if(autorizzazioneContenuti == null || "".equals(autorizzazioneContenuti)) {
						autorizzazioneContenutiStato = StatoFunzionalita.DISABILITATO.getValue();
					} else if(autorizzazioneContenuti.equals(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN)) {
						autorizzazioneContenutiStato = StatoFunzionalita.ABILITATO.getValue();
						List<Proprieta> proprietaAutorizzazioneContenutoList = portaDelegata.getProprietaAutorizzazioneContenutoList();
						SortedMap<List<String>> map = porteDelegateCore.toSortedListMap(proprietaAutorizzazioneContenutoList);
						autorizzazioneContenutiProperties = PropertiesUtilities.convertSortedListMapToText(map, true);
					} else { // custom
						autorizzazioneContenutiStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM;
					}
				}
				
				if(gestioneToken == null) {
					if(portaDelegata.getGestioneToken() != null) {
						gestioneTokenPolicy = portaDelegata.getGestioneToken().getPolicy();
						if(gestioneTokenPolicy == null) {
							gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
							gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
						} else {
							gestioneToken = StatoFunzionalita.ABILITATO.getValue();
						}
						
						StatoFunzionalita tokenOpzionale = portaDelegata.getGestioneToken().getTokenOpzionale();
						if(tokenOpzionale == null) {
							gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
						}else { 
							gestioneTokenOpzionale = tokenOpzionale.getValue();
						}
						
						StatoFunzionalitaConWarning validazione = portaDelegata.getGestioneToken().getValidazione();
						if(validazione == null) {
							gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
						}else { 
							gestioneTokenValidazioneInput = validazione.getValue();
						}
						
						StatoFunzionalitaConWarning introspection = portaDelegata.getGestioneToken().getIntrospection();
						if(introspection == null) {
							gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
						}else { 
							gestioneTokenIntrospection = introspection.getValue();
						}
						
						StatoFunzionalitaConWarning userinfo = portaDelegata.getGestioneToken().getUserInfo();
						if(userinfo == null) {
							gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
						}else { 
							gestioneTokenUserInfo = userinfo.getValue();
						}
						
						StatoFunzionalita tokenForward = portaDelegata.getGestioneToken().getForward();
						if(tokenForward == null) {
							gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
						}else { 
							gestioneTokenTokenForward = tokenForward.getValue();
						}
						
						autorizzazioneTokenOptions = portaDelegata.getGestioneToken().getOptions();
						if((autorizzazioneTokenOptions!=null && !"".equals(autorizzazioneTokenOptions))) {
							autorizzazioneToken = Costanti.CHECK_BOX_ENABLED;
						}
						else {
							autorizzazioneToken = Costanti.CHECK_BOX_DISABLED;
						}
						
						if(portaDelegata.getGestioneToken().getAutenticazione() != null) {
							
							StatoFunzionalita issuer = portaDelegata.getGestioneToken().getAutenticazione().getIssuer();
							if(issuer == null) {
								autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
							}else { 
								autenticazioneTokenIssuer = issuer.getValue();
							}
							
							StatoFunzionalita clientId = portaDelegata.getGestioneToken().getAutenticazione().getClientId();
							if(clientId == null) {
								autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
							}else { 
								autenticazioneTokenClientId = clientId.getValue();
							}
							
							StatoFunzionalita subject = portaDelegata.getGestioneToken().getAutenticazione().getSubject();
							if(subject == null) {
								autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
							}else { 
								autenticazioneTokenSubject = subject.getValue();
							}
							
							StatoFunzionalita username = portaDelegata.getGestioneToken().getAutenticazione().getUsername();
							if(username == null) {
								autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
							}else { 
								autenticazioneTokenUsername = username.getValue();
							}
							
							StatoFunzionalita mailTmp = portaDelegata.getGestioneToken().getAutenticazione().getEmail();
							if(mailTmp == null) {
								autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
							}else { 
								autenticazioneTokenEMail = mailTmp.getValue();
							}
							
						}
						else {							
							autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
							autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
							autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
							autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
							autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
						}
					}
					else {
						gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
						gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
						gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
						
						gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
						gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
						gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
						gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
						
						autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
						autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
						autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
						autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
						autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
					}
				}
				
				if(autorizzazioneScope == null) {
					if(portaDelegata.getScope() != null) {
						autorizzazioneScope =  portaDelegata.getScope().getStato().equals(StatoFunzionalita.ABILITATO) ? Costanti.CHECK_BOX_ENABLED : ""; 
					} else {
						autorizzazioneScope = "";
					}
				}
				
				if(autorizzazioneScopeMatch == null &&
					portaDelegata.getScope()!=null && portaDelegata.getScope().getMatch()!=null){
					autorizzazioneScopeMatch = portaDelegata.getScope().getMatch().getValue();
				}
				
				if(identificazioneAttributiStato==null) {
					identificazioneAttributiStato = portaDelegata.sizeAttributeAuthorityList()>0 ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
					if(portaDelegata.sizeAttributeAuthorityList()>0) {
						attributeAuthoritySelezionate = porteDelegateCore.buildAuthorityArrayString(portaDelegata.getAttributeAuthorityList());
						attributeAuthorityAttributi = porteDelegateCore.buildAttributesStringFromAuthority(portaDelegata.getAttributeAuthorityList());
					}
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.controlloAccessiGestioneToken(dati, TipoOperazione.OTHER, gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy,gestioneTokenOpzionale, 
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward, portaDelegata,protocollo,isPortaDelegata,
						false);
				
				porteDelegateHelper.controlloAccessiAutenticazione(dati, TipoOperazione.OTHER, servletChiamante,portaDelegata,protocollo,
						autenticazione, autenticazioneCustom, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList, confPers, isSupportatoAutenticazione,isPortaDelegata,
						gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						oldAutenticazioneCustom, urlAutenticazioneCustomProperties, numAutenticazioneCustomPropertiesList,
						false, false);
				
				// Tipo operazione = CHANGE per evitare di aggiungere if, questa e' a tutti gli effetti una servlet di CHANGE
				porteDelegateHelper.controlloAccessiAutorizzazione(dati, TipoOperazione.CHANGE, servletChiamante,portaDelegata,protocollo,
						autenticazione, autenticazioneCustom,
						autorizzazione, autorizzazioneCustom, 
						autorizzazioneAutenticati, urlAutorizzazioneAutenticati, sizeFruitori, null, null,
						autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null, 
						autorizzazioneRuoliTipologia, ruoloMatch,
						confPers, isSupportatoAutenticazione, contaListe, isPortaDelegata, false,autorizzazioneScope,urlAutorizzazioneScope,numScope,null,autorizzazioneScopeMatch,
						gestioneToken, gestioneTokenPolicy, 
						autorizzazioneToken, autorizzazioneTokenOptions,allegatoXacmlPolicy,
						null, 0,
						urlAutorizzazioneCustomProperties, numAutorizzazioneCustomPropertiesList,
						identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
						autorizzazioneAutenticatiToken, urlAutorizzazioneAutenticatiToken, numAutenticatiToken, 
						autorizzazioneRuoliToken,  urlAutorizzazioneRuoliToken, numRuoliToken, autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken
						);
				
				porteDelegateHelper.controlloAccessiAutorizzazioneContenuti(dati, TipoOperazione.OTHER, true, portaDelegata,protocollo, 
						autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties, serviceBinding,
						oldAutorizzazioneContenutiCustom, urlAutorizzazioneContenutiCustomPropertiesList, numAutorizzazioneContenutiCustomPropertiesList,
						confPers); 
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.controlloAccessiCheck(TipoOperazione.OTHER, autenticazione, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList,  
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
					autorizzazioneRuoliTipologia, ruoloMatch, 
					 isSupportatoAutenticazione, isPortaDelegata, portaDelegata, ruoli,gestioneToken, gestioneTokenPolicy, 
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autorizzazioneAutenticatiToken, autorizzazioneRuoliToken, 
						autorizzazioneToken,autorizzazioneTokenOptions,
						autorizzazioneScope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
						autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
						protocollo,
						identificazioneAttributiStato, attributeAuthoritySelezionate, attributeAuthorityAttributi);
			
			if (!isOk) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				porteDelegateHelper.controlloAccessiGestioneToken(dati, TipoOperazione.OTHER, gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy, gestioneTokenOpzionale, 
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward, portaDelegata,protocollo,isPortaDelegata,
						false);
				
				porteDelegateHelper.controlloAccessiAutenticazione(dati, TipoOperazione.OTHER, servletChiamante,portaDelegata,protocollo,
						autenticazione, autenticazioneCustom, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList, confPers, isSupportatoAutenticazione,isPortaDelegata,
						gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						oldAutenticazioneCustom, urlAutenticazioneCustomProperties, numAutenticazioneCustomPropertiesList,
						false, false);
				
				// Tipo operazione = CHANGE per evitare di aggiungere if, questa e' a tutti gli effetti una servlet di CHANGE
				porteDelegateHelper.controlloAccessiAutorizzazione(dati, TipoOperazione.CHANGE, servletChiamante,portaDelegata,protocollo,
						autenticazione, autenticazioneCustom,
						autorizzazione, autorizzazioneCustom, 
						autorizzazioneAutenticati, urlAutorizzazioneAutenticati, sizeFruitori, null, null,
						autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null, 
						autorizzazioneRuoliTipologia, ruoloMatch,
						confPers, isSupportatoAutenticazione, contaListe, isPortaDelegata, false,autorizzazioneScope,urlAutorizzazioneScope,numScope,null,autorizzazioneScopeMatch,
						gestioneToken, gestioneTokenPolicy, 
						autorizzazioneToken, autorizzazioneTokenOptions,allegatoXacmlPolicy,
						null, 0,
						urlAutorizzazioneCustomProperties, numAutorizzazioneCustomPropertiesList,
						identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
						autorizzazioneAutenticatiToken, urlAutorizzazioneAutenticatiToken, numAutenticatiToken, 
						autorizzazioneRuoliToken,  urlAutorizzazioneRuoliToken, numRuoliToken, autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);
				
				porteDelegateHelper.controlloAccessiAutorizzazioneContenuti(dati, TipoOperazione.OTHER, true, portaDelegata,protocollo,
						autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties, serviceBinding,
						oldAutorizzazioneContenutiCustom, urlAutorizzazioneContenutiCustomPropertiesList, numAutorizzazioneContenutiCustomPropertiesList,
						confPers); 
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI,
						ForwardParams.OTHER(""));
			}

			
			if (autenticazione == null || !autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM))
				portaDelegata.setAutenticazione(autenticazione);
			else {
				portaDelegata.setAutenticazione(autenticazioneCustom);
				
				if(!oldAutenticazioneCustom)
					portaDelegata.getProprietaAutenticazioneList().clear();
			}
			if(autenticazioneOpzionale != null){
				if(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale))
					portaDelegata.setAutenticazioneOpzionale(StatoFunzionalita.ABILITATO);
				else 
					portaDelegata.setAutenticazioneOpzionale(StatoFunzionalita.DISABILITATO);
			} else 
				portaDelegata.setAutenticazioneOpzionale(null);
			if (autenticazione == null || !autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM)) {
				portaDelegata.getProprietaAutenticazioneList().clear();
				List<Proprieta> proprietaAutenticazione = porteDelegateCore.convertToAutenticazioneProprieta(autenticazione, autenticazionePrincipal, autenticazioneParametroList);
				if(proprietaAutenticazione!=null && !proprietaAutenticazione.isEmpty()) {
					portaDelegata.getProprietaAutenticazioneList().addAll(proprietaAutenticazione);
				}
			}
			
			if (autorizzazione == null || !autorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM)) {
				portaDelegata.setAutorizzazione(AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(autorizzazione, 
						ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati), 
						ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli), 
						ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticatiToken), 
						ServletUtils.isCheckBoxEnabled(autorizzazioneRuoliToken),
						ServletUtils.isCheckBoxEnabled(autorizzazioneScope),
						autorizzazioneTokenOptions,
						RuoloTipologia.toEnumConstant(autorizzazioneRuoliTipologia)));
				portaDelegata.getProprietaAutorizzazioneList().clear();
			} else {
				portaDelegata.setAutorizzazione(autorizzazioneCustom);
			}
			
			if(autorizzazione != null && autorizzazione.equals(AutorizzazioneUtilities.STATO_XACML_POLICY) && allegatoXacmlPolicy.getValue() != null) {
				portaDelegata.setXacmlPolicy(new String(allegatoXacmlPolicy.getValue()));
			} else {
				portaDelegata.setXacmlPolicy(null);
			}
			
			if(ruoloMatch!=null && !"".equals(ruoloMatch)){
				RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant(ruoloMatch);
				if(tipoRuoloMatch!=null){
					if(portaDelegata.getRuoli()==null){
						portaDelegata.setRuoli(new AutorizzazioneRuoli());
					}
					portaDelegata.getRuoli().setMatch(tipoRuoloMatch);
				}
			}
			
			if(ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticatiToken ) ) {
				if(portaDelegata.getAutorizzazioneToken()==null) {
					portaDelegata.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
				}
				portaDelegata.getAutorizzazioneToken().setAutorizzazioneApplicativi(StatoFunzionalita.ABILITATO);
			}
			else {
				if(portaDelegata.getAutorizzazioneToken()!=null) {
					portaDelegata.getAutorizzazioneToken().setAutorizzazioneApplicativi(StatoFunzionalita.DISABILITATO);
					portaDelegata.getAutorizzazioneToken().setServiziApplicativi(null);
				}
			}
			
			if(ServletUtils.isCheckBoxEnabled(autorizzazioneRuoliToken ) ) {
				if(portaDelegata.getAutorizzazioneToken()==null) {
					portaDelegata.setAutorizzazioneToken(new PortaDelegataAutorizzazioneToken());
				}
				portaDelegata.getAutorizzazioneToken().setAutorizzazioneRuoli(StatoFunzionalita.ABILITATO);
				
				if(autorizzazioneRuoliMatchToken!=null && !"".equals(autorizzazioneRuoliMatchToken)){
					RuoloTipoMatch ruoloTipoMatch = RuoloTipoMatch.toEnumConstant(autorizzazioneRuoliMatchToken);
					if(ruoloTipoMatch!=null){
						if(portaDelegata.getAutorizzazioneToken().getRuoli()==null){
							portaDelegata.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
						}
						portaDelegata.getAutorizzazioneToken().getRuoli().setMatch(ruoloTipoMatch);
					}
					else {
						if(portaDelegata.getAutorizzazioneToken().getRuoli()!=null){
							portaDelegata.getAutorizzazioneToken().getRuoli().setMatch(null);
						}
					}
				}
				else {
					if(portaDelegata.getAutorizzazioneToken().getRuoli()!=null){
						portaDelegata.getAutorizzazioneToken().getRuoli().setMatch(null);
					}
				}
				
				if(autorizzazioneRuoliTipologiaToken!=null && !"".equals(autorizzazioneRuoliTipologiaToken)){
					org.openspcoop2.core.config.constants.RuoloTipologia ruoloTipologia = org.openspcoop2.core.config.constants.RuoloTipologia.toEnumConstant(autorizzazioneRuoliTipologiaToken);
					if(ruoloTipologia!=null){
						portaDelegata.getAutorizzazioneToken().setTipologiaRuoli(ruoloTipologia);
					}
					else {
						portaDelegata.getAutorizzazioneToken().setTipologiaRuoli(null);
					}
				}
				else {
					portaDelegata.getAutorizzazioneToken().setTipologiaRuoli(null);
				}
			}
			else {
				if(portaDelegata.getAutorizzazioneToken()!=null) {
					portaDelegata.getAutorizzazioneToken().setAutorizzazioneRuoli(StatoFunzionalita.DISABILITATO);
					portaDelegata.getAutorizzazioneToken().setRuoli(null);
				}
			}
			
			if(ServletUtils.isCheckBoxEnabled(autorizzazioneScope )) {
				if(portaDelegata.getScope() == null)
					portaDelegata.setScope(new AutorizzazioneScope());
				
				portaDelegata.getScope().setStato(StatoFunzionalita.ABILITATO); 
			}
			else {
				portaDelegata.setScope(null);
			}
			if(autorizzazioneScopeMatch!=null && !"".equals(autorizzazioneScopeMatch)){
				ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant(autorizzazioneScopeMatch);
				if(scopeTipoMatch!=null){
					if(portaDelegata.getScope()==null){
						portaDelegata.setScope(new AutorizzazioneScope());
					}
					portaDelegata.getScope().setMatch(scopeTipoMatch);
				}
			}
			
			if(autorizzazioneContenutiStato.equals(StatoFunzionalita.DISABILITATO.getValue())) {
				portaDelegata.setAutorizzazioneContenuto(null);
				portaDelegata.getProprietaAutorizzazioneContenutoList().clear();
			} else if(autorizzazioneContenutiStato.equals(StatoFunzionalita.ABILITATO.getValue())) {
				portaDelegata.setAutorizzazioneContenuto(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN);
				portaDelegata.getProprietaAutorizzazioneContenutoList().clear();
				// Fix: non rispettava l'ordine
				SortedMap<List<String>> convertTextToProperties = PropertiesUtilities.convertTextToSortedListMap(autorizzazioneContenutiProperties, true);
				porteDelegateCore.addFromSortedListMap(portaDelegata.getProprietaAutorizzazioneContenutoList(), convertTextToProperties);
			} else {
				portaDelegata.setAutorizzazioneContenuto(autorizzazioneContenuti);
				if(!autorizzazioneContenutiStato.equals(oldAutorizzazioneContenutoStato))
					portaDelegata.getProprietaAutorizzazioneContenutoList().clear();
			}
			
			if(portaDelegata.getGestioneToken() == null)
				portaDelegata.setGestioneToken(new GestioneToken());
			
			if(gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) {
				portaDelegata.getGestioneToken().setPolicy(gestioneTokenPolicy);
				if(ServletUtils.isCheckBoxEnabled(gestioneTokenOpzionale)) {
					portaDelegata.getGestioneToken().setTokenOpzionale(StatoFunzionalita.ABILITATO);
				}
				else {
					portaDelegata.getGestioneToken().setTokenOpzionale(StatoFunzionalita.DISABILITATO);
				}
				portaDelegata.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenValidazioneInput));
				portaDelegata.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenIntrospection));
				portaDelegata.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenUserInfo));
				portaDelegata.getGestioneToken().setForward(StatoFunzionalita.toEnumConstant(gestioneTokenTokenForward)); 
				portaDelegata.getGestioneToken().setOptions(autorizzazioneTokenOptions);
				if(portaDelegata.getGestioneToken().getAutenticazione()==null) {
					portaDelegata.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
				}
				portaDelegata.getGestioneToken().getAutenticazione().setIssuer(ServletUtils.isCheckBoxEnabled(autenticazioneTokenIssuer) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenIssuer)); 
				portaDelegata.getGestioneToken().getAutenticazione().setClientId(ServletUtils.isCheckBoxEnabled(autenticazioneTokenClientId) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenClientId)); 
				portaDelegata.getGestioneToken().getAutenticazione().setSubject(ServletUtils.isCheckBoxEnabled(autenticazioneTokenSubject) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenSubject)); 
				portaDelegata.getGestioneToken().getAutenticazione().setUsername(ServletUtils.isCheckBoxEnabled(autenticazioneTokenUsername) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenUsername)); 
				portaDelegata.getGestioneToken().getAutenticazione().setEmail(ServletUtils.isCheckBoxEnabled(autenticazioneTokenEMail) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenEMail)); 
			} else {
				portaDelegata.getGestioneToken().setPolicy(null);
				portaDelegata.getGestioneToken().setTokenOpzionale(StatoFunzionalita.DISABILITATO); 
				portaDelegata.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.DISABILITATO);
				portaDelegata.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.DISABILITATO);
				portaDelegata.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.DISABILITATO);
				portaDelegata.getGestioneToken().setForward(StatoFunzionalita.DISABILITATO); 
				portaDelegata.getGestioneToken().setOptions(null);
				if(portaDelegata.getGestioneToken().getAutenticazione()!=null) {
					portaDelegata.getGestioneToken().setAutenticazione(null);
				}
			}
			
			while (portaDelegata.sizeAttributeAuthorityList()>0) {
				portaDelegata.removeAttributeAuthority(0);
			}
			if(StatoFunzionalita.ABILITATO.getValue().equals(identificazioneAttributiStato) && attributeAuthoritySelezionate!=null && attributeAuthoritySelezionate.length>0) {
				for (String aaName : attributeAuthoritySelezionate) {
					portaDelegata.addAttributeAuthority(porteDelegateCore.buildAttributeAuthority(attributeAuthoritySelezionate.length, aaName, attributeAuthorityAttributi));
				}
			}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			
			// cancello i file temporanei
			porteDelegateHelper.deleteBinaryParameters(allegatoXacmlPolicy);
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			
			portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			if(portaDelegata==null) {
				throw new Exception("PortaDelegata con id '"+idInt+"' non trovata");
			}
			idporta = portaDelegata.getNome();
			
			ruoli = new ArrayList<>();
			if(portaDelegata!=null && portaDelegata.getRuoli()!=null && portaDelegata.getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < portaDelegata.getRuoli().sizeRuoloList(); i++) {
					ruoli.add(portaDelegata.getRuoli().getRuolo(i).getNome());
				}
			}
			
			numRuoli = 0;
			if(portaDelegata.getRuoli()!=null){
				numRuoli = portaDelegata.getRuoli().sizeRuoloList();
			}
			
			numScope = 0;
			if(portaDelegata.getScope()!=null){
				numScope = portaDelegata.getScope().sizeScopeList();
			}
			
			sizeFruitori = 0; 
			if(portaDelegata.getServizioApplicativoList() !=null){
				sizeFruitori = portaDelegata.sizeServizioApplicativoList();
			}
			
			numAutenticatiToken = 0; 
			if(portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getServiziApplicativi()!=null){
				numAutenticatiToken = portaDelegata.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList();
			}
			
			numRuoliToken = 0; 
			if(portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getRuoli()!=null){
				numRuoliToken = portaDelegata.getAutorizzazioneToken().getRuoli().sizeRuoloList();
			}
			
			numAutenticazioneCustomPropertiesList = portaDelegata.sizeProprietaAutenticazioneList();
			numAutorizzazioneCustomPropertiesList = portaDelegata.sizeProprietaAutorizzazioneList();
			numAutorizzazioneContenutiCustomPropertiesList = portaDelegata.sizeProprietaAutorizzazioneContenutoList();
			oldAutorizzazioneContenutiCustom = portaDelegata.getAutorizzazioneContenuto() != null && !portaDelegata.getAutorizzazioneContenuto().equals(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN);
			oldAutenticazioneCustom = portaDelegata.getAutenticazione() != null && !TipoAutenticazione.getValues().contains(portaDelegata.getAutenticazione());
			
			if (autenticazione == null) {
				autenticazione = portaDelegata.getAutenticazione();
				if (autenticazione != null &&
						!TipoAutenticazione.getValues().contains(autenticazione)) {
					autenticazioneCustom = autenticazione;
					autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
				}
				
				autenticazionePrincipal = porteDelegateCore.getTipoAutenticazionePrincipal(portaDelegata.getProprietaAutenticazioneList());
				autenticazioneParametroList = porteDelegateCore.getParametroAutenticazione(autenticazione, portaDelegata.getProprietaAutenticazioneList());
			}
			if(autenticazioneOpzionale==null){
				autenticazioneOpzionale = "";
				if(portaDelegata.getAutenticazioneOpzionale()!=null &&
					portaDelegata.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
					autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
				}
			}
			
			if (autorizzazione == null) {
				if (portaDelegata.getAutorizzazione() != null &&
						!TipoAutorizzazione.getAllValues().contains(portaDelegata.getAutorizzazione())) {
					autorizzazioneCustom = portaDelegata.getAutorizzazione();
					autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
				}
				else{
					autorizzazione = AutorizzazioneUtilities.convertToStato(portaDelegata.getAutorizzazione());
					if(TipoAutorizzazione.isAuthenticationRequired(portaDelegata.getAutorizzazione()))
						autorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
					if(TipoAutorizzazione.isRolesRequired(portaDelegata.getAutorizzazione()))
						autorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
					autorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(portaDelegata.getAutorizzazione()).getValue();
				}
			}
			
			if (ruoloMatch == null &&
				portaDelegata.getRuoli()!=null && portaDelegata.getRuoli().getMatch()!=null){
				ruoloMatch = portaDelegata.getRuoli().getMatch().getValue();
			}
			
			if(autorizzazioneAutenticatiToken==null &&
				portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getAutorizzazioneApplicativi()!=null) {
				autorizzazioneAutenticatiToken = StatoFunzionalita.ABILITATO.equals(portaDelegata.getAutorizzazioneToken().getAutorizzazioneApplicativi()) ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
			}
			
			if(autorizzazioneRuoliToken==null &&
				portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getAutorizzazioneRuoli()!=null) {
				autorizzazioneRuoliToken = StatoFunzionalita.ABILITATO.equals(portaDelegata.getAutorizzazioneToken().getAutorizzazioneRuoli()) ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
			}
			if(autorizzazioneRuoliTipologiaToken==null &&
				portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getTipologiaRuoli()!=null) {
				autorizzazioneRuoliTipologiaToken = portaDelegata.getAutorizzazioneToken().getTipologiaRuoli().getValue();
			}
			if (autorizzazioneRuoliMatchToken == null &&
				portaDelegata.getAutorizzazioneToken()!=null && portaDelegata.getAutorizzazioneToken().getRuoli()!=null && portaDelegata.getAutorizzazioneToken().getRuoli().getMatch()!=null){
				autorizzazioneRuoliMatchToken = portaDelegata.getAutorizzazioneToken().getRuoli().getMatch().getValue();
			}
			
			autorizzazioneContenuti = portaDelegata.getAutorizzazioneContenuto();
			
			if(autorizzazioneContenuti == null) {
				autorizzazioneContenutiStato = StatoFunzionalita.DISABILITATO.getValue();
			} else if(autorizzazioneContenuti.equals(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN)) {
				autorizzazioneContenutiStato = StatoFunzionalita.ABILITATO.getValue();
				List<Proprieta> proprietaAutorizzazioneContenutoList = portaDelegata.getProprietaAutorizzazioneContenutoList();
				SortedMap<List<String>> map = porteDelegateCore.toSortedListMap(proprietaAutorizzazioneContenutoList);
				autorizzazioneContenutiProperties = PropertiesUtilities.convertSortedListMapToText(map, true);
			} else { // custom
				autorizzazioneContenutiStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM;
			}
			
			if(portaDelegata.getGestioneToken() != null) {
				gestioneTokenPolicy = portaDelegata.getGestioneToken().getPolicy();
				if(gestioneTokenPolicy == null) {
					gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
					gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
				} else {
					gestioneToken = StatoFunzionalita.ABILITATO.getValue();
				}
				
				StatoFunzionalita tokenOpzionale = portaDelegata.getGestioneToken().getTokenOpzionale();
				if(tokenOpzionale == null) {
					gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
				}else { 
					gestioneTokenOpzionale = tokenOpzionale.getValue();
				}
				
				StatoFunzionalitaConWarning validazione = portaDelegata.getGestioneToken().getValidazione();
				if(validazione == null) {
					gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
				}else { 
					gestioneTokenValidazioneInput = validazione.getValue();
				}
				
				StatoFunzionalitaConWarning introspection = portaDelegata.getGestioneToken().getIntrospection();
				if(introspection == null) {
					gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
				}else { 
					gestioneTokenIntrospection = introspection.getValue();
				}
				
				StatoFunzionalitaConWarning userinfo = portaDelegata.getGestioneToken().getUserInfo();
				if(userinfo == null) {
					gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
				}else { 
					gestioneTokenUserInfo = userinfo.getValue();
				}
				
				StatoFunzionalita tokenForward = portaDelegata.getGestioneToken().getForward();
				if(tokenForward == null) {
					gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
				}else { 
					gestioneTokenTokenForward = tokenForward.getValue();
				}
				
				autorizzazioneTokenOptions = portaDelegata.getGestioneToken().getOptions();
				
				if(portaDelegata.getGestioneToken().getAutenticazione() != null) {
					
					StatoFunzionalita issuer = portaDelegata.getGestioneToken().getAutenticazione().getIssuer();
					if(issuer == null) {
						autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
					}else { 
						autenticazioneTokenIssuer = issuer.getValue();
					}
					
					StatoFunzionalita clientId = portaDelegata.getGestioneToken().getAutenticazione().getClientId();
					if(clientId == null) {
						autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
					}else { 
						autenticazioneTokenClientId = clientId.getValue();
					}
					
					StatoFunzionalita subject = portaDelegata.getGestioneToken().getAutenticazione().getSubject();
					if(subject == null) {
						autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
					}else { 
						autenticazioneTokenSubject = subject.getValue();
					}
					
					StatoFunzionalita username = portaDelegata.getGestioneToken().getAutenticazione().getUsername();
					if(username == null) {
						autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
					}else { 
						autenticazioneTokenUsername = username.getValue();
					}
					
					StatoFunzionalita mailTmp = portaDelegata.getGestioneToken().getAutenticazione().getEmail();
					if(mailTmp == null) {
						autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
					}else { 
						autenticazioneTokenEMail = mailTmp.getValue();
					}
					
				}
				else {
					autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
					autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
					autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
					autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
					autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
				}
			}
			else {
				gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
				gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
				gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
				
				gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
				gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
				gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
				gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
				
				autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
				autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
				autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
				autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
				autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
			}
			
			if(autorizzazioneScope == null) {
				if(portaDelegata.getScope() != null) {
					autorizzazioneScope =  portaDelegata.getScope().getStato().equals(StatoFunzionalita.ABILITATO) ? Costanti.CHECK_BOX_ENABLED : ""; 
				} else {
					autorizzazioneScope = "";
				}
			}
			
			if(autorizzazioneScopeMatch == null &&
				portaDelegata.getScope()!=null && portaDelegata.getScope().getMatch()!=null){
				autorizzazioneScopeMatch = portaDelegata.getScope().getMatch().getValue();
			}
			
			if(identificazioneAttributiStato==null) {
				identificazioneAttributiStato = portaDelegata.sizeAttributeAuthorityList()>0 ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				if(portaDelegata.sizeAttributeAuthorityList()>0) {
					attributeAuthoritySelezionate = porteDelegateCore.buildAuthorityArrayString(portaDelegata.getAttributeAuthorityList());
					attributeAuthorityAttributi = porteDelegateCore.buildAttributesStringFromAuthority(portaDelegata.getAttributeAuthorityList());
				}
			}
			
			asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(idAsps),true);
			
			porteDelegateHelper.controlloAccessiGestioneToken(dati, TipoOperazione.OTHER, gestioneToken, policyLabels, policyValues, 
					gestioneTokenPolicy, gestioneTokenOpzionale,
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward, portaDelegata,protocollo,isPortaDelegata,
					false);
			
			porteDelegateHelper.controlloAccessiAutenticazione(dati, TipoOperazione.OTHER, servletChiamante,portaDelegata,protocollo,
					autenticazione, autenticazioneCustom, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList, confPers, isSupportatoAutenticazione,isPortaDelegata,
					gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					oldAutenticazioneCustom, urlAutenticazioneCustomProperties, numAutenticazioneCustomPropertiesList,
					false, false);
			
			// Tipo operazione = CHANGE per evitare di aggiungere if, questa e' a tutti gli effetti una servlet di CHANGE
			porteDelegateHelper.controlloAccessiAutorizzazione(dati, TipoOperazione.CHANGE, servletChiamante,portaDelegata,protocollo,
					autenticazione, autenticazioneCustom,
					autorizzazione, autorizzazioneCustom, 
					autorizzazioneAutenticati, urlAutorizzazioneAutenticati, sizeFruitori, null, null,
					autorizzazioneRuoli,  urlAutorizzazioneRuoli, numRuoli, null, 
					autorizzazioneRuoliTipologia, ruoloMatch,
					confPers, isSupportatoAutenticazione, contaListe, isPortaDelegata, false,autorizzazioneScope,urlAutorizzazioneScope,numScope,null,autorizzazioneScopeMatch,
					gestioneToken, gestioneTokenPolicy, 
					autorizzazioneToken, autorizzazioneTokenOptions,allegatoXacmlPolicy,
					null, 0,
					urlAutorizzazioneCustomProperties, numAutorizzazioneCustomPropertiesList,
					identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
					autorizzazioneAutenticatiToken, urlAutorizzazioneAutenticatiToken, numAutenticatiToken, 
					autorizzazioneRuoliToken,  urlAutorizzazioneRuoliToken, numRuoliToken, autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);
			
			porteDelegateHelper.controlloAccessiAutorizzazioneContenuti(dati, TipoOperazione.OTHER, true, portaDelegata,protocollo,
					autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties, serviceBinding,
					oldAutorizzazioneContenutiCustom, urlAutorizzazioneContenutiCustomPropertiesList, numAutorizzazioneContenutiCustomPropertiesList,
					confPers); 
			
			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
					idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
			
			pd.setDati(dati);
			
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, 
					ForwardParams.OTHER(""));
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI , 
					ForwardParams.OTHER(""));
		} 
	}

}
