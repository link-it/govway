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



package org.openspcoop2.pdd.core.autorizzazione;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autorizzazione.container.AutorizzazioneHttpServletRequest;
import org.openspcoop2.pdd.core.autorizzazione.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.token.EsitoGestioneToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.InformazioniTokenUserInfo;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xacml.CachedMapBasedSimplePolicyRepository;
import org.openspcoop2.utils.xacml.MarshallUtilities;
import org.openspcoop2.utils.xacml.PolicyDecisionPoint;
import org.openspcoop2.utils.xacml.PolicyException;
import org.openspcoop2.utils.xacml.XacmlRequest;
import org.slf4j.Logger;


/**
 * XACMLPolicyUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XACMLPolicyUtilities {

	private static PolicyDecisionPoint pdp;
	private static synchronized void initPdD(Logger log) throws PolicyException{
		if(XACMLPolicyUtilities.pdp == null) {
			XACMLPolicyUtilities.pdp = new PolicyDecisionPoint(log);
		}
	}	
	public static PolicyDecisionPoint getPolicyDecisionPoint(Logger log) throws PolicyException{
		if(XACMLPolicyUtilities.pdp == null) {
			XACMLPolicyUtilities.initPdD(log);
		}
		return XACMLPolicyUtilities.pdp;
	}

	// Workaround: Serve nel caso di Porta Delegata per poter utilizzare una policy differente da quella utilizzata nell'erogazione
	private static final String  NOME_POLICY_FRUIZIONE = "fruizioneXacmlPolicy.xml";

	public static void loadPolicy(String xacmlPolicyPorta, IDServizio idServizio, String key, 
			boolean portaDelegata,IDSoggetto fruitore, // fruitore is null se non e' portaDelegata
			Logger log) throws PolicyException{
		byte[] policy = null; 
		if(xacmlPolicyPorta!=null) {
			policy = xacmlPolicyPorta.getBytes();
		}
		else {
			@SuppressWarnings("unused")
			String nomePolicy = null;
			int numeroPolicy = 0;
			@SuppressWarnings("unused")
			boolean numeroPolicyFruizione = false;
			try{
				AccordoServizioParteSpecifica asps = RegistroServiziManager.getInstance().getAccordoServizioParteSpecifica(idServizio, null, true, null); // requestInfo non serve poichè devono essere caricati gli allegati
				for (int i = 0; i < asps.sizeSpecificaSicurezzaList(); i++) {
					Documento d = asps.getSpecificaSicurezza(i);
					if(TipiDocumentoSicurezza.XACML_POLICY.getNome().equals(d.getTipo())){
	
						if(policy == null || (portaDelegata && (fruitore.getNome()+"_"+NOME_POLICY_FRUIZIONE).equals(d.getFile()))){
							if(NOME_POLICY_FRUIZIONE.equals(d.getFile())){
								numeroPolicyFruizione = true;
							}
							else{
								numeroPolicy++;
							}
							if(d.getByteContenuto()!=null){
								policy = d.getByteContenuto();
								nomePolicy = d.getFile();
							}
							else if(d.getFile()!=null){
								if(d.getFile().startsWith("http://") || d.getFile().startsWith("file://")){
									URL url = new URL(d.getFile());
									policy = HttpUtilities.requestHTTPFile(url.toString());
								}
								else{
									File f = new File(d.getFile());
									policy = FileSystemUtilities.readBytesFromFile(f);
								}
							}
						}
					}
				}
				if(numeroPolicy>1){
					throw new PolicyException("Piu di una xacml policy trovata per il servizio "+idServizio.toString());
				}
			}catch(Exception e){
				throw new PolicyException("Errore durante la ricerca delle policies xacml per il servizio "+idServizio.toString()+": "+e.getMessage(),e);
			}
			if(policy== null){
				throw new PolicyException("Nessuna xacml policy trovata trovata per il servizio "+idServizio.toString());
			}
		}

		try{
			// Caricamento in PdP vedendo che la policy non sia gia stata caricata ....
			XACMLPolicyUtilities.getPolicyDecisionPoint(log).addPolicy(MarshallUtilities.unmarshallPolicy(policy), key);
		}catch(Exception e){
			throw new PolicyException("Errore durante il caricamento della xacml policy sul PdD (servizio "+idServizio.toString()+"): "+e.getMessage(),e);
		}
	}

	public static XacmlRequest newXacmlRequest(IProtocolFactory<?> protocolFactory, AbstractDatiInvocazione datiInvocazione, 
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			String policyKey) throws AutorizzazioneException{

		XacmlRequest xacmlRequest = new XacmlRequest();
		URLProtocolContext urlProtocolContext = null;
		if(datiInvocazione.getInfoConnettoreIngresso()==null ||
				datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext()==null){
			throw new AutorizzazioneException("UrlProtocolContext non disponibile; risorsa richiesta dall'autorizzazione");
		}
		urlProtocolContext = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext();

		if(datiInvocazione.getIdServizio()==null || 
				datiInvocazione.getIdServizio().getSoggettoErogatore()==null ||
				datiInvocazione.getIdServizio().getSoggettoErogatore().getTipo()==null ||
				datiInvocazione.getIdServizio().getSoggettoErogatore().getNome()==null ||
				datiInvocazione.getIdServizio().getTipo()==null ||
				datiInvocazione.getIdServizio().getNome() == null){
			throw new AutorizzazioneException("DatiServizio non disponibile; risorsa richiesta dall'autorizzazione");
		}
		String tipoSoggettoErogatore = datiInvocazione.getIdServizio().getSoggettoErogatore().getTipo();
		String nomeSoggettoErogatore = datiInvocazione.getIdServizio().getSoggettoErogatore().getNome();
		String tipoServizio = datiInvocazione.getIdServizio().getTipo();
		String nomeServizio = datiInvocazione.getIdServizio().getNome();
		String azione = datiInvocazione.getIdServizio().getAzione() != null ? datiInvocazione.getIdServizio().getAzione() : "";

		DatiInvocazionePortaDelegata datiPD = null;
		if(datiInvocazione instanceof DatiInvocazionePortaDelegata){
			datiPD = (DatiInvocazionePortaDelegata) datiInvocazione;
		}

		DatiInvocazionePortaApplicativa datiPA = null;
		if(datiInvocazione instanceof DatiInvocazionePortaApplicativa){
			datiPA = (DatiInvocazionePortaApplicativa) datiInvocazione;
		}
	
		PdDContext pddContext = datiInvocazione.getPddContext();
		
		InformazioniToken informazioniTokenNormalizzate = null;
		InformazioniTokenUserInfo informazioniTokenUserInfoNormalizzate = null;
		Map<String, Object> jwtClaims = null;
		Map<String, Object> introspectionClaims = null;
		Map<String, Object> userInfoClaims = null;
		Object oInformazioniTokenNormalizzate = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
		if(oInformazioniTokenNormalizzate!=null) {
			informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
			informazioniTokenUserInfoNormalizzate = informazioniTokenNormalizzate.getUserInfo();
		}
		Object oTmp = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_ESITO_VALIDAZIONE);
		if(oTmp!=null) {
			EsitoGestioneToken esito = (EsitoGestioneToken) oTmp;
			if(esito.getInformazioniToken()!=null &&
					esito.getInformazioniToken().getClaims()!=null &&
					esito.getInformazioniToken().getClaims().size()>0) {
				jwtClaims = esito.getInformazioniToken().getClaims();
			}
		}
		oTmp = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_ESITO_INTROSPECTION);
		if(oTmp!=null) {
			EsitoGestioneToken esito = (EsitoGestioneToken) oTmp;
			if(esito.getInformazioniToken()!=null &&
					esito.getInformazioniToken().getClaims()!=null &&
					esito.getInformazioniToken().getClaims().size()>0) {
				introspectionClaims = esito.getInformazioniToken().getClaims();
			}
		}
		oTmp = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_ESITO_USER_INFO);
		if(oTmp!=null) {
			EsitoGestioneToken esito = (EsitoGestioneToken) oTmp;
			if(esito.getInformazioniToken()!=null &&
					esito.getInformazioniToken().getClaims()!=null &&
					esito.getInformazioniToken().getClaims().size()>0) {
				userInfoClaims = esito.getInformazioniToken().getClaims();
			}
		}
		List<String> tokenRoles = null;
		if(informazioniTokenNormalizzate!=null) {
			tokenRoles = informazioniTokenNormalizzate.getRoles();
		}
		
		List<String> attributeNames = null;
		Map<String, Object> attributes = null;
		boolean multipleAA = false;
		InformazioniAttributi informazioniAttributiNormalizzati = null;
		Object oInformazioniAttributiNormalizzati = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_ATTRIBUTI_INFORMAZIONI_NORMALIZZATE);
		if(oInformazioniAttributiNormalizzati!=null) {
			informazioniAttributiNormalizzati = (InformazioniAttributi) oInformazioniAttributiNormalizzati;
		}
		if(informazioniAttributiNormalizzati!=null) {
			attributeNames = informazioniAttributiNormalizzati.getAttributesNames();
			attributes = informazioniAttributiNormalizzati.getAttributes();
			multipleAA = informazioniAttributiNormalizzati.isMultipleAttributeAuthorities()!=null && informazioniAttributiNormalizzati.isMultipleAttributeAuthorities();
		}

		Map<String, String> apiImplConfig = readConfig(pddContext, org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE);
		Map<String, String> applicativoConfig = readConfig(pddContext, org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO);
		Map<String, String> soggettoFruitoreConfig = readConfig(pddContext, org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE);
		Map<String, String> soggettoErogatoreConfig = readConfig(pddContext, org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_EROGATORE);
		
		HttpServletRequest httpServletRequest = null;
		if(checkRuoloEsterno){
			if(datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest()==null){
				if(tokenRoles==null) {
					throw new AutorizzazioneException("HttpServletRequest non disponibile; risorsa richiesta dall'autorizzazione");
				}
			}
			httpServletRequest = datiInvocazione.getInfoConnettoreIngresso().getUrlProtocolContext().getHttpServletRequest();
		}

		


		// Action

		String azioneId = urlProtocolContext.getRequestURI();
		xacmlRequest.addAction(azioneId); // namespace standard xacml

		xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_PROVIDER_ATTRIBUTE_ID, tipoSoggettoErogatore+"/"+nomeSoggettoErogatore); 
		if(soggettoErogatoreConfig!=null && !soggettoErogatoreConfig.isEmpty()) {
			Iterator<String> keys = soggettoErogatoreConfig.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = soggettoErogatoreConfig.get(key);
				xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_PROVIDER_CONFIG_ATTRIBUTE_PREFIX+key, value);
			}
		}

		xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_SERVICE_ATTRIBUTE_ID, tipoServizio+"/"+nomeServizio);  
		if(apiImplConfig!=null && !apiImplConfig.isEmpty()) {
			Iterator<String> keys = apiImplConfig.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = apiImplConfig.get(key);
				xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_SERVICE_CONFIG_ATTRIBUTE_PREFIX+key, value);
			}
		}

		if(azione!=null){
			xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_ACTION_ATTRIBUTE_ID, azione);
		}

		xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_URL_ATTRIBUTE_ID, urlProtocolContext.getRequestURI());    	

		if(urlProtocolContext.getParameters()!=null && urlProtocolContext.getParameters().size()>0){
			Iterator<String> keys = urlProtocolContext.getParameters().keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = urlProtocolContext.getParameterValues(key);
				if(key!=null && !key.contains(" ") && values!=null && !values.isEmpty()){
					if(values.size()==1) {
						xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_URL_PARAMETER_ATTRIBUTE_ID+key, values.get(0));
					}
					else {
						xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_URL_PARAMETER_ATTRIBUTE_ID+key, values);
					}
				}
			}
		}

		if(urlProtocolContext.getHeaders()!=null && urlProtocolContext.getHeaders().size()>0){
			Iterator<String> keys = urlProtocolContext.getHeaders().keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = urlProtocolContext.getHeaderValues(key);
				if(key!=null && !key.contains(" ") && values!=null && !values.isEmpty()){
					if(values.size()==1) {
						xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_TRANSPORT_HEADER_ATTRIBUTE_ID+key, values.get(0));
					}
					else {
						xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_TRANSPORT_HEADER_ATTRIBUTE_ID+key, values);
					}
				}
			}
		}

		if(urlProtocolContext.getFunction()!=null){
			xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_PDD_SERVICE_ATTRIBUTE_ID, urlProtocolContext.getFunction());    	
		}

		if(datiInvocazione.getInfoConnettoreIngresso().getSoapAction()!=null){
			xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_SOAP_ACTION_ATTRIBUTE_ID, datiInvocazione.getInfoConnettoreIngresso().getSoapAction());    	
		}

		if(protocolFactory!=null){
			xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_PROTOCOL_ATTRIBUTE_ID, protocolFactory.getProtocol());    	
		}
		
		if(informazioniTokenNormalizzate!=null) {
			if(informazioniTokenNormalizzate.getAud()!=null) {
				xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_TOKEN_AUDIENCE_ATTRIBUTE_ID, informazioniTokenNormalizzate.getAud());
			}
			if(informazioniTokenNormalizzate.getScopes()!=null && informazioniTokenNormalizzate.getScopes().size()>0) {
				xacmlRequest.addActionAttribute(XACMLCostanti.XACML_REQUEST_ACTION_TOKEN_SCOPE_ATTRIBUTE_ID, informazioniTokenNormalizzate.getScopes());
			}
		}
		
		if(jwtClaims!=null && jwtClaims.size()>0) {
			Iterator<?> it = jwtClaims.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = jwtClaims.get(key);
				key = normalizeKeyClaim(key);
				setActionAttribute(xacmlRequest, value, 
						XACMLCostanti.XACML_REQUEST_ACTION_TOKEN_JWT_CLAIMS_PREFIX+key);
			}
		}
		
		if(introspectionClaims!=null && introspectionClaims.size()>0) {
			Iterator<?> it = introspectionClaims.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = introspectionClaims.get(key);
				key = normalizeKeyClaim(key);
				setActionAttribute(xacmlRequest, value, 
						XACMLCostanti.XACML_REQUEST_ACTION_TOKEN_INTROSPECTION_CLAIMS_PREFIX+key);
			}
		}

		

		// Subject

		String nomeServizioApplicativo = null;
		IDSoggetto soggettoFruitore = null;
		if(datiPD!=null){
			if(datiPD.getIdServizioApplicativo()!=null){
				nomeServizioApplicativo = datiPD.getIdServizioApplicativo().getNome();
				soggettoFruitore = datiPD.getIdServizioApplicativo().getIdSoggettoProprietario();
			}
			else if(datiPD.getIdPD()!=null && datiPD.getIdPD().getIdentificativiFruizione()!=null &&
					datiPD.getIdPD().getIdentificativiFruizione().getSoggettoFruitore()!=null){
				soggettoFruitore = datiPD.getIdPD().getIdentificativiFruizione().getSoggettoFruitore();
			}
		}
		else if(datiPA!=null){
			if(datiPA.getIdSoggettoFruitore()!=null){
				soggettoFruitore = datiPA.getIdSoggettoFruitore();
			}
			if(datiPA.getIdentitaServizioApplicativoFruitore()!=null){
				nomeServizioApplicativo = datiPA.getIdentitaServizioApplicativoFruitore().getNome();
				if(soggettoFruitore==null){
					soggettoFruitore = datiPA.getIdentitaServizioApplicativoFruitore().getIdSoggettoProprietario();
				}
			}
		}
		
		String credential = null;
		TipoAutenticazione autenticazione = null;
		if(datiPD!=null){
			if(datiPD.getPd()!=null){
				autenticazione = TipoAutenticazione.toEnumConstant(datiPD.getPd().getAutenticazione());
			}
		}
		else if(datiPA!=null){
			if(datiPA.getPa()!=null){
				autenticazione = TipoAutenticazione.toEnumConstant(datiPA.getPa().getAutenticazione());
			}
		}
		if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali()!=null){
			
			if(autenticazione!=null){
				if(TipoAutenticazione.BASIC.equals(autenticazione)){
					if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getUsername()!=null){
						credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getUsername();
					}
				}
				else if(TipoAutenticazione.SSL.equals(autenticazione)){
					if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getSubject()!=null){
						credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getSubject();
					}
				}
				else if(TipoAutenticazione.PRINCIPAL.equals(autenticazione)){
					if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getPrincipal()!=null){
						credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getPrincipal();
					}
				}
			}
						
			if(credential == null){
				if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getPrincipal()!=null){
					credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getPrincipal();
				}
				else if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getSubject()!=null){
					credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getSubject();
				}
				else if(datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getUsername()!=null){
					credential = datiInvocazione.getInfoConnettoreIngresso().getCredenziali().getUsername();
				}
			}
		}
		
		List<String> roles = new ArrayList<>();
		if(checkRuoloRegistro){
			if(datiPD!=null){
				if(datiPD.getServizioApplicativo()==null && !checkRuoloEsterno){
					throw new AutorizzazioneException("Identità servizio applicativo non disponibile; tale informazione è richiesta dall'autorizzazione");
				}
				if(datiPD.getServizioApplicativo()!=null) {
					if(datiPD.getServizioApplicativo().getInvocazionePorta()!=null && 
							datiPD.getServizioApplicativo().getInvocazionePorta().getRuoli()!=null &&
							datiPD.getServizioApplicativo().getInvocazionePorta().getRuoli().sizeRuoloList()>0){
						for (int i = 0; i < datiPD.getServizioApplicativo().getInvocazionePorta().getRuoli().sizeRuoloList(); i++) {
							roles.add(datiPD.getServizioApplicativo().getInvocazionePorta().getRuoli().getRuolo(i).getNome());
						}
					}
				}
			}
			else if(datiPA!=null){
				if(datiPA.getSoggettoFruitore()==null && !checkRuoloEsterno){
					throw new AutorizzazioneException("Identità soggetto fruitore non disponibile; tale informazione è richiesta dall'autorizzazione");
				}
				if(datiPA.getSoggettoFruitore()!=null) {
					if(datiPA.getSoggettoFruitore().getRuoli()!=null &&
							datiPA.getSoggettoFruitore().getRuoli().sizeRuoloList()>0){
						for (int i = 0; i < datiPA.getSoggettoFruitore().getRuoli().sizeRuoloList(); i++) {
							roles.add(datiPA.getSoggettoFruitore().getRuoli().getRuolo(i).getNome());
						}
					}
				}
			}
		}
		if(checkRuoloEsterno){
			try{
				FiltroRicercaRuoli filtroRicerca = new FiltroRicercaRuoli();
				if(datiPD!=null){
					filtroRicerca.setContesto(RuoloContesto.PORTA_DELEGATA);
				}
				else if(datiPA!=null){
					filtroRicerca.setContesto(RuoloContesto.PORTA_APPLICATIVA);
				}
				filtroRicerca.setTipologia(RuoloTipologia.ESTERNO);
				RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance(datiInvocazione.getState());
				List<IDRuolo> list = registroServiziManager.getAllIdRuoli(filtroRicerca, null);
				if(list==null || list.size()<=0){
					throw new DriverRegistroServiziNotFound();
				}
				for (IDRuolo idRuolo : list) {
					String nomeRuoloDaVerificare = idRuolo.getNome();
					try {
						Ruolo ruoloRegistro = registroServiziManager.getRuolo(idRuolo.getNome(), null, datiInvocazione.getRequestInfo());
						if(ruoloRegistro.getNomeEsterno()!=null && !"".equals(ruoloRegistro.getNomeEsterno())) {
							nomeRuoloDaVerificare = ruoloRegistro.getNomeEsterno();
						}
					}catch(Exception e) {
						throw new Exception("Recupero del ruolo '"+idRuolo.getNome()+"' fallito: "+e.getMessage(),e);
					}
					if(httpServletRequest.isUserInRole(nomeRuoloDaVerificare)){
						roles.add(idRuolo.getNome()); // nella xacml policy comunque inserisco l'identificativo del ruolo nel registro della PdD
					}
				}
				if(tokenRoles!=null && tokenRoles.size()>0) {
					roles.addAll(tokenRoles);
				}
			}
			catch(DriverRegistroServiziNotFound notFound){
				if(!checkRuoloRegistro) {
					throw new AutorizzazioneException("Non sono stati registrati sulla PdD ruoli utilizzabili con un'autorizzazione esterna");
				}
			}
			catch(Exception e){
				throw new AutorizzazioneException("E' avvenuto un errore durante la ricerca dei ruoli: "+e.getMessage(),e);
			}
		}

		if(soggettoFruitore!=null){
			String subjectId = soggettoFruitore.toString();
			if(nomeServizioApplicativo!=null){
				subjectId = nomeServizioApplicativo + "." +subjectId;
			}
			xacmlRequest.addSubject(subjectId); // namespace standard xacml
		}
		else if(credential!=null){
			String subjectId = credential;
			xacmlRequest.addSubject(subjectId); // namespace standard xacml
		}

		if(soggettoFruitore!=null){
			xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_ORGANIZATION_ATTRIBUTE_ID, soggettoFruitore.toString());
		}
		if(soggettoFruitoreConfig!=null && !soggettoFruitoreConfig.isEmpty()) {
			Iterator<String> keys = soggettoFruitoreConfig.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = soggettoFruitoreConfig.get(key);
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_ORGANIZATION_CONFIG_ATTRIBUTE_PREFIX+key, value);
			}
		}
		
		if(nomeServizioApplicativo!=null){
			xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_CLIENT_ATTRIBUTE_ID, nomeServizioApplicativo);
		}
		if(applicativoConfig!=null && !applicativoConfig.isEmpty()) {
			Iterator<String> keys = applicativoConfig.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = applicativoConfig.get(key);
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_CLIENT_CONFIG_ATTRIBUTE_PREFIX+key, value);
			}
		}
		
		if(credential!=null){
			xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_CREDENTIAL_ATTRIBUTE_ID, credential);
		}
		
		if(roles!=null && roles.size()>0){
			xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_ROLE_ATTRIBUTE_ID, roles);
		}
				
		if(informazioniTokenUserInfoNormalizzate!=null) {
			if(informazioniTokenUserInfoNormalizzate.getFullName()!=null) {
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_USER_INFO_FULL_NAME_ATTRIBUTE_ID, informazioniTokenUserInfoNormalizzate.getFullName());
			}
			if(informazioniTokenUserInfoNormalizzate.getFirstName()!=null) {
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_USER_INFO_FIRST_NAME_ATTRIBUTE_ID, informazioniTokenUserInfoNormalizzate.getFirstName());
			}
			if(informazioniTokenUserInfoNormalizzate.getMiddleName()!=null) {
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_USER_INFO_MIDDLE_NAME_ATTRIBUTE_ID, informazioniTokenUserInfoNormalizzate.getMiddleName());
			}
			if(informazioniTokenUserInfoNormalizzate.getFamilyName()!=null) {
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_USER_INFO_FAMILY_NAME_ATTRIBUTE_ID, informazioniTokenUserInfoNormalizzate.getFamilyName());
			}
			if(informazioniTokenUserInfoNormalizzate.getEMail()!=null) {
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_USER_INFO_EMAIL_NAME_ATTRIBUTE_ID, informazioniTokenUserInfoNormalizzate.getEMail());
			}
		}
		if(informazioniTokenNormalizzate!=null) {
			if(informazioniTokenNormalizzate.getIss()!=null) {
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_TOKEN_ISSUER_ATTRIBUTE_ID, informazioniTokenNormalizzate.getIss());
			}
			if(informazioniTokenNormalizzate.getSub()!=null) {
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_TOKEN_SUBJECT_ATTRIBUTE_ID, informazioniTokenNormalizzate.getSub());
			}
			if(informazioniTokenNormalizzate.getUsername()!=null) {
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_TOKEN_USERNAME_ATTRIBUTE_ID, informazioniTokenNormalizzate.getUsername());
			}
			if(informazioniTokenNormalizzate.getClientId()!=null) {
				xacmlRequest.addSubjectAttribute(XACMLCostanti.XACML_REQUEST_SUBJECT_TOKEN_CLIENT_ID_ATTRIBUTE_ID, informazioniTokenNormalizzate.getClientId());
			}
		}
		
		if(userInfoClaims!=null && userInfoClaims.size()>0) {
			Iterator<?> it = userInfoClaims.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = userInfoClaims.get(key);
				key = normalizeKeyClaim(key);
				setSubjectAttribute(xacmlRequest, value, 
						XACMLCostanti.XACML_REQUEST_SUBJECT_TOKEN_USERINFO_CLAIMS_PREFIX+key);
			}
		}
		
		if(attributeNames!=null && !attributeNames.isEmpty()) {
			setSubjectAttribute(xacmlRequest, attributeNames, 
					XACMLCostanti.XACML_REQUEST_SUBJECT_ATTRIBUTE_ATTRIBUTE_NAMES_ID);
		}
		if(attributes!=null && attributes.size()>0){
			addAttributes(xacmlRequest, attributes, multipleAA);
		}

		
		
		// Environment
		
		xacmlRequest.createEnvironment();
		
		
		
		// Resource
		
		//solo in caso di pdp locale inizializzo la resource __resource_id___ con il nome del servizio in modo da consentire l'identificazione della policy
		
		xacmlRequest.addResourceAttribute(CachedMapBasedSimplePolicyRepository.RESOURCE_ATTRIBUTE_ID_TO_MATCH, policyKey);
		
		
		// Fill XAML
		if(httpServletRequest!=null && httpServletRequest instanceof AutorizzazioneHttpServletRequest) {
			AutorizzazioneHttpServletRequest authHttpServletRequest = (AutorizzazioneHttpServletRequest) httpServletRequest;
			authHttpServletRequest.fillXacmlRequest(xacmlRequest);
		}
		
		return xacmlRequest;


	}
	
	@SuppressWarnings("unchecked")
	private static Map<String,String> readConfig(PdDContext pddContext, MapKey<String> pName){
		Object o = pddContext.getObject(pName);
		if(o!=null && o instanceof Map<?, ?>) {
			return (Map<String, String>) o;
		}
		return null;
	}
	
	private static void addAttributes(XacmlRequest xacmlRequest, Map<String, Object> attributesParam, boolean multipleAA) {
		if(attributesParam!=null && !attributesParam.isEmpty()) {
			String logAA = "";
			if(multipleAA) {
				// informazioni normalizzate, devo scendere di un livello, senno al primo degli attributi ci sono le attribute authority
				for (String attrAuthName : attributesParam.keySet()) {
					Object o = attributesParam.get(attrAuthName);
					if(o instanceof Map) {
						try {
							List<String> attributesNames = new ArrayList<String>();
							@SuppressWarnings("unchecked")
							Map<String, Object> attributes = (Map<String, Object>) o;
							if(attributes!=null && !attributes.isEmpty()) {
								for (String attrName : attributes.keySet()) {
									if(!attributesNames.contains(attrName)) {
										attributesNames.add(attrName);
									}
								}
							}
							Collections.sort(attributesNames);
							String xacmlIdPrefix = XACMLCostanti.XACML_REQUEST_SUBJECT_ATTRIBUTE_AUTHORITY_ATTRIBUTE_PREFIX+
									attrAuthName+
									XACMLCostanti._XACML_REQUEST_SUBJECT_ATTRIBUTE_ATTRIBUTE_PREFIX;
							JSONUtils jsonUtils = JSONUtils.getInstance();
							logAA = " (A.A. "+attrAuthName+")";
							for (String attributeName : attributesNames) {
								addAttribute(xacmlRequest, xacmlIdPrefix, attributes, attributeName,
										jsonUtils, logAA);
							}
						}catch(Throwable t) {
							OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("addAttributes failed (A.A. "+attrAuthName+"): "+t.getMessage(),t);
						}
					}
				}
				
			}
			else {
				List<String> attributesNames = new ArrayList<String>();
				for (String attrName : attributesParam.keySet()) {
					attributesNames.add(attrName);
				}
				Collections.sort(attributesNames);
				String xacmlIdPrefix = XACMLCostanti.XACML_REQUEST_SUBJECT_ATTRIBUTE_ATTRIBUTE_PREFIX;
				JSONUtils jsonUtils = JSONUtils.getInstance();
				for (String attributeName : attributesNames) {
					addAttribute(xacmlRequest, xacmlIdPrefix, attributesParam, attributeName,
							jsonUtils, logAA);
				}
			}
		}
	}
	private static void addAttribute(XacmlRequest xacmlRequest, String xacmlIdPrefix, Map<String, Object> attributes, String attributeName,
			JSONUtils jsonUtils, String logAA) {
		Object oValue = attributes.get(attributeName);
		if(oValue!=null) {
			try {
				String key = normalizeKeyClaim(attributeName);
				setSubjectAttribute(xacmlRequest, oValue, 
						xacmlIdPrefix+key);
				
//				//System.out.println("TIPO: "+oValue.getClass().getName());
//				if(oValue instanceof String) {
//					String value = (String) oValue;
//					xacmlRequest.addSubjectAttribute(xacmlIdPrefix+attributeName, value);
//				}
//				else if(oValue instanceof List<?>) {
//					List<?> list = (List<?>) oValue;
//					if(list!=null && !list.isEmpty()) {
//						List<String> lValues = new ArrayList<String>();
//						for (Object object : list) {
//							if(object!=null && object instanceof String) {
//								lValues.add((String)object);
//							}
//						}
//						if(!lValues.isEmpty()) {
//							xacmlRequest.addSubjectAttribute(xacmlIdPrefix+attributeName, lValues);
//						}
//					}
//				}
//				else {
//					throw new Exception("type '"+oValue.getClass().getName()+"' unmanaged");
//				}
			}catch(Throwable t) {
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("addAttribute '"+attributeName+"' failed"+logAA+": "+t.getMessage(),t);
			}
		}
	}
	
	private static String normalizeKeyClaim(String keyParam) {
		String key = keyParam;
		if(key.contains(".")) {
			while(key.contains(".")) {
				key = key.replace(".", ":");
			}
		}
		return key;
	}
	
	private static void setActionAttribute(XacmlRequest xacmlRequest, Object value, String claim) {
		setAttribute(xacmlRequest, value, claim, true);
	}
	private static void setSubjectAttribute(XacmlRequest xacmlRequest, Object value, String claim) {
		setAttribute(xacmlRequest, value, claim, false);
	}
	private static void setAttribute(XacmlRequest xacmlRequest, Object value, String claim, boolean action) {
		if(value!=null) {
			List<String> l = TokenUtilities.getClaimValues(value);
			if(l!=null && !l.isEmpty()) {
				if(l.size()>1) {
					if(action) {
						xacmlRequest.addActionAttribute(claim, l);
					}
					else {
						xacmlRequest.addSubjectAttribute(claim, l);
					}
				}
				else {
					if(action) {
						xacmlRequest.addActionAttribute(claim, l.get(0));
					}
					else {
						xacmlRequest.addSubjectAttribute(claim, l.get(0));
					}
				}
			}
		}
	}

}
