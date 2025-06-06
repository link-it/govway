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
package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.PDNDTokenInfo;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.slf4j.Logger;

/**     
 * InterceptorPolicyUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterceptorPolicyUtilities {
	
	private InterceptorPolicyUtilities() {}


	public static IDUnivocoGroupByPolicy convertToID(Logger log,DatiTransazione datiTransazione, AttivazionePolicyRaggruppamento policyGroupBy,
			InRequestProtocolContext context) throws Exception{
		
		final String nonDisponibile = "-"; /**"n.d.";*/
		
		IDUnivocoGroupByPolicy groupBy = new IDUnivocoGroupByPolicy();
		
		if(policyGroupBy.isRuoloPorta()){
			groupBy.setRuoloPorta(datiTransazione.getTipoPdD().getTipo());
		}
		
		if(policyGroupBy.isProtocollo()){
			groupBy.setProtocollo(datiTransazione.getProtocollo());
		}
		
		if(policyGroupBy.isFruitore()){
			if(datiTransazione.getSoggettoFruitore()!=null) {
				groupBy.setFruitore(datiTransazione.getSoggettoFruitore().toString());
			}
			else {
				groupBy.setFruitore(nonDisponibile);
			}
		}
		
		if(policyGroupBy.isServizioApplicativoFruitore()){
			groupBy.setServizioApplicativoFruitore(datiTransazione.getServizioApplicativoFruitore());
			if(datiTransazione.getIdServizioApplicativoToken()!=null) {
				groupBy.setServizioApplicativoToken(datiTransazione.getIdServizioApplicativoToken());
			}
		}
				
		if(policyGroupBy.isErogatore()){
			groupBy.setErogatore(datiTransazione.getIdServizio().getSoggettoErogatore().toString());
		}
		
		if(TipoPdD.APPLICATIVA.equals(datiTransazione.getTipoPdD()) &&
			policyGroupBy.isServizioApplicativoErogatore()){
			groupBy.setServizioApplicativoErogatore(datiTransazione.getServiziApplicativiErogatoreAsString());
		}
		
		if(policyGroupBy.isServizio()){
			groupBy.setServizio(datiTransazione.getIdServizio().getTipo()+"/"+datiTransazione.getIdServizio().getNome()+"/"+datiTransazione.getIdServizio().getVersione());
		}
		
		if(policyGroupBy.isAzione()){
			groupBy.setAzione(datiTransazione.getIdServizio().getAzione());
		}
				
		if(policyGroupBy.isInformazioneApplicativaEnabled()){
			String valorePresente = PolicyFiltroApplicativoUtilities.getValore(log, policyGroupBy.getInformazioneApplicativaTipo(), 
					policyGroupBy.getInformazioneApplicativaNome(), 
					context, datiTransazione, false);
			if(valorePresente==null){
				valorePresente = nonDisponibile;
			}
			groupBy.setTipoKey(policyGroupBy.getInformazioneApplicativaTipo());
			groupBy.setNomeKey(policyGroupBy.getInformazioneApplicativaNome());
			groupBy.setValoreKey(valorePresente);
		}
		
		if(policyGroupBy.isIdentificativoAutenticato()){
			groupBy.setIdentificativoAutenticato(datiTransazione.getIdentificativoAutenticato()!=null ? datiTransazione.getIdentificativoAutenticato() : nonDisponibile);
		}
		
		if(policyGroupBy.getToken()!=null){
			String [] token = null;
			if(policyGroupBy.getToken().contains(",")) {
				token = policyGroupBy.getToken().split(",");
			}
			else {
				token = new String[] {policyGroupBy.getToken()};
			}
			if(token!=null && token.length>0) {
				for (int i = 0; i < token.length; i++) {
					TipoCredenzialeMittente claim = TipoCredenzialeMittente.toEnumConstant(token[i], true);
					switch (claim) {
					case TOKEN_SUBJECT:
						if(datiTransazione.getTokenSubject()!=null) {
							groupBy.setTokenSubject(datiTransazione.getTokenSubject());
						}
						else {
							groupBy.setTokenSubject(nonDisponibile);
						}
						break;
					case TOKEN_ISSUER:
						if(datiTransazione.getTokenIssuer()!=null) {
							groupBy.setTokenIssuer(datiTransazione.getTokenIssuer());
						}
						else {
							groupBy.setTokenIssuer(nonDisponibile);
						}
						break;
					case TOKEN_CLIENT_ID:
						if(datiTransazione.getTokenClientId()!=null) {
							groupBy.setTokenClientId(datiTransazione.getTokenClientId());
						}
						else {
							groupBy.setTokenClientId(nonDisponibile);
						}
						break;
					case TOKEN_USERNAME:
						if(datiTransazione.getTokenUsername()!=null) {
							groupBy.setTokenUsername(datiTransazione.getTokenUsername());
						}
						else {
							groupBy.setTokenUsername(nonDisponibile);
						}
						break;
					case TOKEN_EMAIL:
						if(datiTransazione.getTokenEMail()!=null) {
							groupBy.setTokenEMail(datiTransazione.getTokenEMail());
						}
						else {
							groupBy.setTokenEMail(nonDisponibile);
						}
						break;
					case PDND_ORGANIZATION_NAME:
						if(datiTransazione.getPdndOrganizationName()!=null && StringUtils.isNotEmpty(datiTransazione.getPdndOrganizationName())) {
							groupBy.setPdndOrganizationName(datiTransazione.getPdndOrganizationName());
						}
						else {
							if(abortTransactionPdndOrganizationName(log, context.getPddContext())) {
								throw new CoreException("PDND Organization name not available");
							}
							else {
								groupBy.setPdndOrganizationName(nonDisponibile);
							}
						}
						break;
					case PDND_ORGANIZATION_EXTERNAL_ID:
						if(datiTransazione.getPdndOrganizationJson()!=null && StringUtils.isNotEmpty(datiTransazione.getPdndOrganizationJson())) {
							groupBy.setPdndOrganizationExternalId(readPdndOrganizationExternalId(log, datiTransazione.getPdndOrganizationJson(), 
									abortTransactionPdndOrganizationName(log, context.getPddContext()), nonDisponibile));
						}
						else {
							if(abortTransactionPdndOrganizationName(log, context.getPddContext())) {
								throw new CoreException("PDND Organization external id not available");
							}
							else {
								groupBy.setPdndOrganizationExternalId(nonDisponibile);
							}
						}
						break;
					case PDND_ORGANIZATION_CONSUMER_ID:
						if( (datiTransazione.getPdndOrganizationJson()!=null && StringUtils.isNotEmpty(datiTransazione.getPdndOrganizationJson()))
								||
								(datiTransazione.getTokenClaims()!=null && !datiTransazione.getTokenClaims().isEmpty())
								) {
							groupBy.setPdndOrganizationConsumerId(readPdndOrganizationConsumerId(log, datiTransazione.getTokenClaims(), datiTransazione.getPdndOrganizationJson(), 
									abortTransactionPdndOrganizationName(log, context.getPddContext()), nonDisponibile));
						}
						else {
							if(abortTransactionPdndOrganizationName(log, context.getPddContext())) {
								throw new CoreException("PDND Organization consumer id not available");
							}
							else {
								groupBy.setPdndOrganizationName(nonDisponibile);
							}
						}
						break;						
					default:
						break;
					}
				}
			}
		}
		
		return groupBy;
	}
	private static String readPdndOrganizationExternalId(Logger log, String json, boolean throwException, String nonDisponibile) throws CoreException {
		try {
			String origin = PDNDTokenInfo.readOrganizationExternalOriginFromJson(log, json);
			String id = PDNDTokenInfo.readOrganizationExternalIdFromJson(log, json);
			String pdndOrganizationExternalId = null;
			if(origin!=null && StringUtils.isNotEmpty(origin) &&
					id!=null && StringUtils.isNotEmpty(id)) {
				pdndOrganizationExternalId = origin + " "+id;
			}
			else if(origin!=null && StringUtils.isNotEmpty(origin)) {
				pdndOrganizationExternalId = origin;
			}
			else if(id!=null && StringUtils.isNotEmpty(id)) {
				pdndOrganizationExternalId = id;
			}
			if(pdndOrganizationExternalId!=null && StringUtils.isNotEmpty(pdndOrganizationExternalId)){
				return pdndOrganizationExternalId;
			}
			else if(throwException) {
				throw new CoreException("PDND Organization externalId not available");
			}
		}catch(Exception e) {
			if(throwException) {
				throw new CoreException("PDND Organization externalId not available",e);
			}
			else {
				log.error("PDND Organization externalId not available: "+e.getMessage(),e);
			}
		}
		return nonDisponibile;
	}
	private static String readPdndOrganizationConsumerId(Logger log, Map<String, String> tokenClaims, String json, boolean throwException, String nonDisponibile) throws CoreException {
		try {
			String idFromToken = readPdndOrganizationConsumerId(tokenClaims) ;
			if(idFromToken!=null) {
				return idFromToken;
			}
			
			String pdndOrganizationlId = null;
			if(json!=null && StringUtils.isNotEmpty(json)) {
				String id = PDNDTokenInfo.readOrganizationIdFromJson(log, json);
				if(id!=null && StringUtils.isNotEmpty(id)) {
					pdndOrganizationlId = id;
				}
			}
			if(pdndOrganizationlId!=null && StringUtils.isNotEmpty(pdndOrganizationlId)){
				return pdndOrganizationlId;
			}
			else if(throwException) {
				throw new CoreException("PDND Organization consumerId not available");
			}
		}catch(Exception e) {
			if(throwException) {
				throw new CoreException("PDND Organization consumerId not available",e);
			}
			else {
				log.error("PDND Organization consumerId not available: "+e.getMessage(),e);
			}
		}
		return nonDisponibile;
	}
	private static String readPdndOrganizationConsumerId(Map<String, String> tokenClaims) throws CoreException {
		if(tokenClaims!=null && !tokenClaims.isEmpty() && tokenClaims.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDND_CONSUMER_ID)) {
			String id = tokenClaims.get(org.openspcoop2.pdd.core.token.Costanti.PDND_CONSUMER_ID);
			if(id!=null && StringUtils.isNotEmpty(id)){
				return id;
			}
		}
		return null;
	}
	
	private static boolean abortTransactionPdndOrganizationName(Logger log, Context context) throws CoreException {
		
		boolean abort = OpenSPCoop2Properties.getInstance().isGestoreChiaviPDNDRateLimitingInfoNotAvailableAbortTransaction();
		
		RequestInfo requestInfo = null;
		if(context!=null && context.containsKey(Costanti.REQUEST_INFO)) {
			Object o = context.get(Costanti.REQUEST_INFO);
			if(o instanceof RequestInfo) {
				requestInfo = (RequestInfo) o;
			}
		}
		
		if(requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getInterfaceName()!=null && 
				StringUtils.isNotEmpty(requestInfo.getProtocolContext().getInterfaceName())) {
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(requestInfo.getProtocolContext().getInterfaceName());
			try {
				PortaApplicativa pa = ConfigurazionePdDManager.getInstance().getPortaApplicativaSafeMethod(idPA, requestInfo);
				if(pa!=null && pa.sizeProprieta()>0) {
					abort = CostantiProprieta.isPdndRateLimitingByOrganizationInfoNotAvailableAbortTransaction(pa.getProprieta(), abort);
				}
			}catch(Exception e) {
				log.error("Accesso porta applicativa ["+requestInfo.getProtocolContext().getInterfaceName()+"] fallito: "+e.getMessage(),e);
			}
		}
		return abort;
		
	}
	
	public static DatiTransazione readDatiTransazione(InRequestProtocolContext context){
		TipoPdD tipoPdD = context.getTipoPorta();
		String idModulo = context.getIdModulo();
		PdDContext pddContext = context.getPddContext();
		URLProtocolContext urlProtocolContext = null;
		if(context.getConnettore()!=null && context.getConnettore().getUrlProtocolContext()!=null) {
			urlProtocolContext = context.getConnettore().getUrlProtocolContext();
		}
		IProtocolFactory<?> protocolFactory = context.getProtocolFactory();
		IState state = context.getStato();
		Logger log = context.getLogCore();
		
		IDSoggetto idDominio = null;
		IDSoggetto soggettoFruitore = null;
		IDServizio idServizio = null;
		IDAccordo idAccordo = null;
		if(context.getProtocollo()!=null) {
			idDominio = context.getProtocollo().getDominio();
			soggettoFruitore = context.getProtocollo().getFruitore();
			
			IDSoggetto soggettoErogatore = context.getProtocollo().getErogatore();
			String soggettoErogatoreTipo = null;
			String soggettoErogatoreNome = null;
			if(soggettoErogatore!=null) {
				soggettoErogatoreTipo = soggettoErogatore.getTipo();
				soggettoErogatoreNome = soggettoErogatore.getNome();
			}
			int versione = 1;
			if(context.getProtocollo().getVersioneServizio()!=null) {
				versione = context.getProtocollo().getVersioneServizio();
			}
			idServizio = IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(context.getProtocollo().getTipoServizio(), 
					context.getProtocollo().getServizio(),
					soggettoErogatoreTipo, soggettoErogatoreNome, 
					versione); 
			idServizio.setAzione(context.getProtocollo().getAzione());
			
			idAccordo = context.getProtocollo().getIdAccordo();
		}
		
		IDPortaDelegata idPD = null;
		IDPortaApplicativa idPA = null;
		String servizioApplicativoFruitore = null;
		List<String> serviziApplicativiErogatori = null;
		if(context.getIntegrazione()!=null && context.getIntegrazione().getIdPD()!=null){
			idPD = context.getIntegrazione().getIdPD();
			servizioApplicativoFruitore = context.getIntegrazione().getServizioApplicativoFruitore();
		}
		else if(context.getIntegrazione()!=null && context.getIntegrazione().getIdPA()!=null){
			idPA = context.getIntegrazione().getIdPA();
			if(context.getIntegrazione().sizeServiziApplicativiErogatori()>0){
				serviziApplicativiErogatori = new ArrayList<>();
				for (int i = 0; i < context.getIntegrazione().sizeServiziApplicativiErogatori(); i++) {
					serviziApplicativiErogatori.add(context.getIntegrazione().getServizioApplicativoErogatore(i));
				}
			}
			servizioApplicativoFruitore = context.getIntegrazione().getServizioApplicativoFruitore();
		}
		
		return readDatiTransazione(tipoPdD, idModulo, 
				pddContext, urlProtocolContext,
				protocolFactory, state, log,
				idDominio, soggettoFruitore, idServizio, idAccordo,
				idPD, idPA,
				servizioApplicativoFruitore, serviziApplicativiErogatori);
	}
	public static DatiTransazione readDatiTransazione(TipoPdD tipoPdD, String idModulo, 
			PdDContext pddContext, URLProtocolContext urlProtocolContext,
			IProtocolFactory<?> protocolFactory, IState state, Logger log,
			IDSoggetto idDominio, IDSoggetto soggettoFruitore, IDServizio idServizio, IDAccordo idAccordo,
			IDPortaDelegata idPD, IDPortaApplicativa idPA,
			String servizioApplicativoFruitore, List<String> serviziApplicativiErogatori){
		DatiTransazione datiTransazione = new DatiTransazione();
		
		datiTransazione.setTipoPdD(tipoPdD);
		datiTransazione.setModulo(idModulo);
		
		if(pddContext!=null) {
			String idTransazione = (String) pddContext.getObject(Costanti.ID_TRANSAZIONE);
			datiTransazione.setIdTransazione(idTransazione);
		}
		
		if(urlProtocolContext!=null) {
			datiTransazione.setNomePorta(urlProtocolContext.getInterfaceName());
		}
		
		if(protocolFactory!=null)
			datiTransazione.setProtocollo(protocolFactory.getProtocol());
		
		// Controllo sui dati del Fruitore e del Servizio (Erogatore)
		datiTransazione.setDominio(idDominio);
		datiTransazione.setSoggettoFruitore(soggettoFruitore);
		datiTransazione.setIdServizio(idServizio);
		
		RequestInfo requestInfo = null;
		if(pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
			requestInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		}
		
		RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance(state);
		if(idAccordo!=null) {
			datiTransazione.setIdAccordoServizioParteComune(idAccordo);
		}
		else {
			try {
				AccordoServizioParteSpecifica asps = registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false, requestInfo);
				datiTransazione.setIdAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			}catch(Exception e) {
				/**System.out.println("["+(String) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)+"] Errore: "+e.getMessage());
				e.printStackTrace(System.out);*/
				log.debug("Lettura AccordoServizioParteSpecifica ("+idServizio+") non riuscita: "+e.getMessage(),e);
			}				
		}
		if(datiTransazione.getIdAccordoServizioParteComune()!=null) {
			try {
				AccordoServizioParteComune aspc = registroServiziManager.getAccordoServizioParteComune(datiTransazione.getIdAccordoServizioParteComune(), null, false, false, requestInfo);
				if(aspc.getGruppi()!=null && aspc.getGruppi().sizeGruppoList()>0) {
					List<String> tags = new ArrayList<>();
					for (GruppoAccordo gruppoAccordo : aspc.getGruppi().getGruppoList()) {
						tags.add(gruppoAccordo.getNome());
					}
					datiTransazione.setTagsAccordoServizioParteComune(tags);
				}
			}catch(Exception e) {
				/**System.out.println("["+(String) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)+"] Errore: "+e.getMessage());
				e.printStackTrace(System.out);*/
				log.debug("Lettura AccordoServizioParteSpecifica ("+idServizio+") non riuscita: "+e.getMessage(),e);
			}	
		}
		
		if(idPD!=null){

			if(servizioApplicativoFruitore!=null){
				datiTransazione.setServizioApplicativoFruitore(servizioApplicativoFruitore);
			}
			else{
				datiTransazione.setServizioApplicativoFruitore(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO);
			}

		}
		else if(idPA!=null){
			
			if(serviziApplicativiErogatori!=null && !serviziApplicativiErogatori.isEmpty()){
				for (int i = 0; i < serviziApplicativiErogatori.size(); i++) {
					datiTransazione.getListServiziApplicativiErogatori().add(serviziApplicativiErogatori.get(i));
				}
			}
			
			if(servizioApplicativoFruitore!=null){
				datiTransazione.setServizioApplicativoFruitore(servizioApplicativoFruitore);
			}
			
		}
		
		if(pddContext!=null) {
			
			if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.IDENTIFICATIVO_AUTENTICATO)) {
				String id = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.IDENTIFICATIVO_AUTENTICATO);
				datiTransazione.setIdentificativoAutenticato(id);
			}
			
			if(pddContext.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE)) {
				InformazioniToken informazioniTokenNormalizzate = (InformazioniToken) pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
				if(informazioniTokenNormalizzate!=null) {
					datiTransazione.setTokenSubject(informazioniTokenNormalizzate.getSub());
					datiTransazione.setTokenIssuer(informazioniTokenNormalizzate.getIss());
					datiTransazione.setTokenClientId(informazioniTokenNormalizzate.getClientId());
					datiTransazione.setTokenUsername(informazioniTokenNormalizzate.getUsername());
					if(informazioniTokenNormalizzate.getUserInfo()!=null) {
						datiTransazione.setTokenEMail(informazioniTokenNormalizzate.getUserInfo().getEMail());
					}
					datiTransazione.setTokenClaims(informazioniTokenNormalizzate.getClaims());
					
					SecurityToken securityToken = SecurityTokenUtilities.readSecurityToken(pddContext);
					if(securityToken!=null && securityToken.getPdnd()!=null) {
						datiTransazione.setPdndClientJson(securityToken.getPdnd().getClientJson());
						datiTransazione.setPdndOrganizationJson(securityToken.getPdnd().getOrganizationJson());
						try {
							datiTransazione.setPdndOrganizationName(securityToken.getPdnd().getOrganizationName());
						}catch(Exception e) {
							/**System.out.println("["+(String) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)+"] Errore: "+e.getMessage());
							e.printStackTrace(System.out);*/
							log.debug("Lettura 'PDND Organization name' non riuscita: "+e.getMessage(),e);
						}	
					}
					
				}
			}
			
			if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN)) {
				IDServizioApplicativo servizioApplicativoToken = (IDServizioApplicativo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN);
				datiTransazione.setIdServizioApplicativoToken(servizioApplicativoToken);
	    	}
			
		}
		
		return datiTransazione;
	}
	
	public static boolean checkRegisterThread(DatiTransazione datiTransazione){
		if(datiTransazione.getDominio()==null || 
				datiTransazione.getDominio().getTipo()==null ||
						datiTransazione.getDominio().getNome()==null || 
						datiTransazione.getDominio().getCodicePorta()==null ){
			return false; // i dati sul dominio ci devono essere
		}
		if(datiTransazione.getModulo()==null){
			return false; // i dati sul modulo ci devono essere
		}
		if(datiTransazione.getIdTransazione()==null){
			return false; // i dati sull'identificativo transazione ci devono essere
		}
		if(datiTransazione.getNomePorta()==null) {
			return false; // i dati sul nome della porta ci deve essere
		}
		if(
				TipoPdD.DELEGATA.equals(datiTransazione.getTipoPdD()) 
				&&
				(datiTransazione.getSoggettoFruitore()==null 
					|| 
				datiTransazione.getSoggettoFruitore().getTipo()==null 
					||
				datiTransazione.getSoggettoFruitore().getNome()==null)
				){
			return false; // i dati sul fruitore ci devono essere
		}
		if(datiTransazione.getIdServizio()==null ||
				datiTransazione.getIdServizio().getSoggettoErogatore()==null || 
				datiTransazione.getIdServizio().getSoggettoErogatore().getTipo()==null ||
				datiTransazione.getIdServizio().getSoggettoErogatore().getNome()==null){
			return false; // i dati sull'erogatore ci devono essere
		}
		if(datiTransazione.getIdServizio()==null ||
				datiTransazione.getIdServizio().getTipo()==null || datiTransazione.getIdServizio().getNome()==null || datiTransazione.getIdServizio().getVersione()==null){
			return false; // i dati sul servizio ci devono essere
		}
		if(datiTransazione.getServizioApplicativoFruitore()==null && 
				(datiTransazione.getListServiziApplicativiErogatori()==null || datiTransazione.getListServiziApplicativiErogatori().isEmpty())){
			return false; // i dati sul servizio applicativo ci devono essere
		}
		return true;
	}
	
	public static boolean filter(AttivazionePolicyFiltro filtro, DatiTransazione datiTransazione, IState state, RequestInfo requestInfo) throws Exception{
		
		if(filtro.isEnabled()){
						
			if(filtro.getProtocollo()!=null && !"".equals(filtro.getProtocollo()) &&
				!filtro.getProtocollo().equals(datiTransazione.getProtocollo())){
				return false;
			}
			
			if(filtro.getRuoloPorta()!=null && !"".equals(filtro.getRuoloPorta().getValue()) && 
					!RuoloPolicy.ENTRAMBI.equals(filtro.getRuoloPorta())){
				
				if(RuoloPolicy.DELEGATA.equals(filtro.getRuoloPorta()) &&
					!TipoPdD.DELEGATA.equals(datiTransazione.getTipoPdD())){
					return false;
				}
				if(RuoloPolicy.APPLICATIVA.equals(filtro.getRuoloPorta()) &&
					!TipoPdD.APPLICATIVA.equals(datiTransazione.getTipoPdD())){
					return false;
				}
				
			}
			
			boolean policyGlobale = true;
			if(filtro.getNomePorta()!=null && !"".equals(filtro.getNomePorta())){
				policyGlobale = false;
				if(datiTransazione.getNomePorta()==null){
					return false;
				}
				if(!filtro.getNomePorta().equals(datiTransazione.getNomePorta())){
					return false;
				}
			}
			
			if(filtro.getRuoloFruitore()!=null && !"".equals(filtro.getRuoloFruitore())){
					
				/*
				 * Se policyGlobale:
				 *    si controlla sia il fruitore che l'applicativo. Basta che uno sia soddisfatto.
				 * else
				 * 	  nel caso di delegata si controlla solo l'applicativo.
				 *    nel caso di applicativa entrambi, e basta che uno sia soddisfatto.
				 **/
				
				// diventa policy richiedente
				
				boolean ruoloSoggetto = false;
				boolean ruoloApplicativo = false;
				
				if(datiTransazione.getSoggettoFruitore()!=null && 
						datiTransazione.getSoggettoFruitore().getTipo()!=null && 
						datiTransazione.getSoggettoFruitore().getNome()!=null){
					
					IDSoggetto idFruitore = new IDSoggetto(datiTransazione.getSoggettoFruitore().getTipo(), datiTransazione.getSoggettoFruitore().getNome());
				
					if(policyGlobale || TipoPdD.APPLICATIVA.equals(datiTransazione.getTipoPdD())){
						RegistroServiziManager registroManager = RegistroServiziManager.getInstance(state);
						Soggetto soggetto = null;
						try {
							soggetto = registroManager.getSoggetto(idFruitore, null, requestInfo);
						}catch(DriverRegistroServiziNotFound notFound) {
							// ignore
						}
						if(soggetto!=null && soggetto.getRuoli()!=null) {
							for (int i = 0; i < soggetto.getRuoli().sizeRuoloList(); i++) {
								if(soggetto.getRuoli().getRuolo(i).getNome().equals(filtro.getRuoloFruitore())) {
									ruoloSoggetto = true;
									break;
								}
							}
						}
					}
					
					if(datiTransazione.getServizioApplicativoFruitore()!=null && !CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(datiTransazione.getServizioApplicativoFruitore())) {
						
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setIdSoggettoProprietario(idFruitore);
						idSA.setNome(datiTransazione.getServizioApplicativoFruitore());
					
						ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(state);
						ServizioApplicativo sa = null;
						try {
							sa = configPdDManager.getServizioApplicativo(idSA, requestInfo);
						}catch(DriverConfigurazioneNotFound nofFound) {
							// ignore
						}
						if(sa!=null && sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().getRuoli()!=null) {
							for (int i = 0; i < sa.getInvocazionePorta().getRuoli().sizeRuoloList(); i++) {
								if(sa.getInvocazionePorta().getRuoli().getRuolo(i).getNome().equals(filtro.getRuoloFruitore())) {
									ruoloApplicativo = true;
									break;
								}
							}
						}
						
					}

				}
				
				if(!ruoloSoggetto && !ruoloApplicativo && datiTransazione.getIdServizioApplicativoToken()!=null) {
						
					if(policyGlobale || TipoPdD.APPLICATIVA.equals(datiTransazione.getTipoPdD())){
						RegistroServiziManager registroManager = RegistroServiziManager.getInstance(state);
						Soggetto soggetto = null;
						try {
							soggetto = registroManager.getSoggetto(datiTransazione.getIdServizioApplicativoToken().getIdSoggettoProprietario(), null, requestInfo);
						}catch(DriverRegistroServiziNotFound notFound) {
							// ignore
						}
						if(soggetto!=null && soggetto.getRuoli()!=null) {
							for (int i = 0; i < soggetto.getRuoli().sizeRuoloList(); i++) {
								if(soggetto.getRuoli().getRuolo(i).getNome().equals(filtro.getRuoloFruitore())) {
									ruoloSoggetto = true;
									break;
								}
							}
						}
					}
					
					ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(state);
					ServizioApplicativo sa = null;
					try {
						sa = configPdDManager.getServizioApplicativo(datiTransazione.getIdServizioApplicativoToken(), requestInfo);
					}catch(DriverConfigurazioneNotFound nofFound) {
						// ignore
					}
					if(sa!=null && sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().getRuoli()!=null) {
						for (int i = 0; i < sa.getInvocazionePorta().getRuoli().sizeRuoloList(); i++) {
							if(sa.getInvocazionePorta().getRuoli().getRuolo(i).getNome().equals(filtro.getRuoloFruitore())) {
								ruoloApplicativo = true;
								break;
							}
						}
					}
						
				}
				
				if(!ruoloSoggetto && !ruoloApplicativo) {
					return false;
				}
					
			}
			
			boolean checkDatiFruitore = true;
			
			if(datiTransazione.getIdServizioApplicativoToken()!=null && datiTransazione.getIdServizioApplicativoToken().getIdSoggettoProprietario()!=null &&
					filtro.getTipoFruitore()!=null && !"".equals(filtro.getTipoFruitore()) &&
					filtro.getNomeFruitore()!=null && !"".equals(filtro.getNomeFruitore()) &&
					filtro.getServizioApplicativoFruitore()!=null && !"".equals(filtro.getServizioApplicativoFruitore()) &&
				filtro.getTipoFruitore().equals(datiTransazione.getIdServizioApplicativoToken().getIdSoggettoProprietario().getTipo()) &&
				filtro.getNomeFruitore().equals(datiTransazione.getIdServizioApplicativoToken().getIdSoggettoProprietario().getNome()) &&
				filtro.getServizioApplicativoFruitore().equals(datiTransazione.getIdServizioApplicativoToken().getNome())) {
				checkDatiFruitore = false; // match con l'applicativo token
			}
			
			if(checkDatiFruitore && filtro.getTipoFruitore()!=null && !"".equals(filtro.getTipoFruitore())){
				if(datiTransazione.getSoggettoFruitore()==null){
					return false;
				}
				if(!filtro.getTipoFruitore().equals(datiTransazione.getSoggettoFruitore().getTipo())){
					return false;
				}
			}
			if(checkDatiFruitore && filtro.getNomeFruitore()!=null && !"".equals(filtro.getNomeFruitore())){
				if(datiTransazione.getSoggettoFruitore()==null){
					return false;
				}
				if(!filtro.getNomeFruitore().equals(datiTransazione.getSoggettoFruitore().getNome())){
					return false;
				}
			}
			
			if(checkDatiFruitore && filtro.getServizioApplicativoFruitore()!=null && !"".equals(filtro.getServizioApplicativoFruitore()) &&
				!filtro.getServizioApplicativoFruitore().equals(datiTransazione.getServizioApplicativoFruitore())){
				return false;
			}
						
			if(filtro.getRuoloErogatore()!=null && !"".equals(filtro.getRuoloErogatore())){
				if(datiTransazione.getIdServizio() == null ||
						datiTransazione.getIdServizio().getSoggettoErogatore()==null || 
						datiTransazione.getIdServizio().getSoggettoErogatore().getTipo()==null || 
						datiTransazione.getIdServizio().getSoggettoErogatore().getNome()==null){
					return false;
				}
				IDSoggetto idErogatore = new IDSoggetto(datiTransazione.getIdServizio().getSoggettoErogatore().getTipo(), datiTransazione.getIdServizio().getSoggettoErogatore().getNome());
				RegistroServiziManager registroManager = RegistroServiziManager.getInstance(state);
				Soggetto soggetto = registroManager.getSoggetto(idErogatore, null, requestInfo);
				boolean foundRuolo = false;
				if(soggetto.getRuoli()!=null) {
					for (int i = 0; i < soggetto.getRuoli().sizeRuoloList(); i++) {
						if(soggetto.getRuoli().getRuolo(i).getNome().equals(filtro.getRuoloErogatore())) {
							foundRuolo = true;
							break;
						}
					}
				}
				if(!foundRuolo) {
					return false;
				}
			}
			
			if(filtro.getTipoErogatore()!=null && !"".equals(filtro.getTipoErogatore())){
				if(datiTransazione.getIdServizio()==null){
					return false;
				}
				if(datiTransazione.getIdServizio().getSoggettoErogatore()==null){
					return false;
				}
				if(!filtro.getTipoErogatore().equals(datiTransazione.getIdServizio().getSoggettoErogatore().getTipo())){
					return false;
				}
			}
			if(filtro.getNomeErogatore()!=null && !"".equals(filtro.getNomeErogatore())){
				if(datiTransazione.getIdServizio()==null){
					return false;
				}
				if(datiTransazione.getIdServizio().getSoggettoErogatore()==null){
					return false;
				}
				if(!filtro.getNomeErogatore().equals(datiTransazione.getIdServizio().getSoggettoErogatore().getNome())){
					return false;
				}
			}
			
			if(filtro.getServizioApplicativoErogatore()!=null && !"".equals(filtro.getServizioApplicativoErogatore())){
				if(datiTransazione.getListServiziApplicativiErogatori()==null || datiTransazione.getListServiziApplicativiErogatori().isEmpty()){
					return false;
				}				
				if(!datiTransazione.getListServiziApplicativiErogatori().contains(filtro.getServizioApplicativoErogatore())){
					return false;
				}
			}
			
			if(filtro.getTag()!=null && !"".equals(filtro.getTag())){
				if(datiTransazione.getTagsAccordoServizioParteComune()==null || datiTransazione.getTagsAccordoServizioParteComune().isEmpty()){
					return false;
				}
				boolean find = false;
				for (String tag : datiTransazione.getTagsAccordoServizioParteComune()) {
					if(filtro.getTag().equals(tag)){
						find = true;
						break;
					}
				}
				if(!find){
					return false;
				}
			}
			
			if(filtro.getTipoServizio()!=null && !"".equals(filtro.getTipoServizio())){
				if(datiTransazione.getIdServizio()==null){
					return false;
				}
				if(!filtro.getTipoServizio().equals(datiTransazione.getIdServizio().getTipo())){
					return false;
				}
			}
			if(filtro.getNomeServizio()!=null && !"".equals(filtro.getNomeServizio())){
				if(datiTransazione.getIdServizio()==null){
					return false;
				}
				if(!filtro.getNomeServizio().equals(datiTransazione.getIdServizio().getNome())){
					return false;
				}
			}
			if(filtro.getVersioneServizio()!=null){
				if(datiTransazione.getIdServizio()==null || datiTransazione.getIdServizio().getVersione()==null){
					return false;
				}
				if(filtro.getVersioneServizio().intValue() != datiTransazione.getIdServizio().getVersione().intValue()){
					return false;
				}
			}
			
			if(filtro.getAzione()!=null && !"".equals(filtro.getAzione())){
				if(datiTransazione.getIdServizio()==null){
					return false;
				}
				if(datiTransazione.getIdServizio().getAzione()==null){
					return false;
				}
				String [] tmp = filtro.getAzione().split(",");
				if(tmp!=null && tmp.length>0) {
					boolean found = false;
					for (String az : tmp) {
						if(az.equals(datiTransazione.getIdServizio().getAzione())){
							found=true;
							break;
						}			
					}
					if(!found) {
						return false;
					}
				}
			}
			
			if(filtro.getTokenClaims()!=null && !"".equals(filtro.getTokenClaims())){
				Properties properties = PropertiesUtilities.convertTextToProperties(filtro.getTokenClaims());
				boolean isOk = filter(properties, datiTransazione.getTokenClaims());
				if(!isOk) {
					return false;
				}
			}
						
		}
		
		return true;
		
	}
	
	private static boolean filter(Properties properties, Map<String, String> datiTransazioneClaims) {
		if(properties!=null && properties.size()>0) {
			for (Object o : properties.keySet()) {
				if(o instanceof String) {
					String key = (String) o;
					Boolean v = filterPropertyClaim(key, properties, datiTransazioneClaims);
					if(v!=null) {
						return v.booleanValue();
					}
				}
			}
		}
		return true;
	}
	private static Boolean filterPropertyClaim(String key, Properties properties, Map<String, String> datiTransazioneClaims) {
		Boolean b = null;
		String value = properties.getProperty(key);
		if(datiTransazioneClaims==null || datiTransazioneClaims.isEmpty()) {
			return false;
		}
		if(!datiTransazioneClaims.containsKey(key)) {
			return false;
		}
		String v = datiTransazioneClaims.get(key);
		if(value==null || "".equals(value)) {
			if (! (v == null || "".equals(v)) ) {
				return false;
			}
		}
		else {
			if(!value.equals(v)) {
				return false;
			}
		}
		return b;
	}
	
}
