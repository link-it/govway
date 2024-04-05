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

package org.openspcoop2.pdd.logger.info;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.autenticazione.ApiKeyUtilities;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;

/**
 * InfoMittenteFormatUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class InfoMittenteFormatUtils {
	
	private InfoMittenteFormatUtils() {}

	public static String getRichiedente(DatiMittente infoDatiMittente) {
		
		/**
		 * Logica:
		 * - prevale l'utente descritto in forma umana (username);
		 * - altrimenti prevale un eventuale applicativo identificato (registrato su GovWay) dando precedenza ad un applicativo token rispetto ad un applicativo di trasporto;
		 * - altrimenti prevalgono le informazioni di un eventuale token presente rispetto al trasporto; se si tratta di client credentials (subject non presente o client_id=subject) prevale l'informazione sul client-id altrimenti quella sul subject.
		 */
		
		// 1) Username del Token
		String sTokenUsername = infoDatiMittente.getTokenUsername();
		if(StringUtils.isNotEmpty(sTokenUsername)) {
			return sTokenUsername;
		}
		
		// 2) Applicativo Token identificato tramite ClientID
		String sTokenClient = infoDatiMittente.getTokenClient();
		if(StringUtils.isNotEmpty(sTokenClient)) {
			return sTokenClient;
		}
		
		// 2b) Applicativo Token identificato tramite PDND
		String sTokenClientPdndOrganizationName = infoDatiMittente.getPdndOrganizationName();
		if(StringUtils.isNotEmpty(sTokenClientPdndOrganizationName)) {
			return sTokenClientPdndOrganizationName;
		}
		
		// 3) Applicativo Fruitore
		String sApplicativoFruitore = infoDatiMittente.getServizioApplicativoFruitore();
		if(StringUtils.isNotEmpty(sApplicativoFruitore)) {
			return sApplicativoFruitore;
		}
			
		// 4) ClientId/Subject/Issuer del Token
		String sTokenClientId = infoDatiMittente.getTokenClientId();
		String sTokenSubject = infoDatiMittente.getTokenSubject();
		boolean clientCredentialsFlow = false;
		if(StringUtils.isNotEmpty(sTokenClientId)) {
			clientCredentialsFlow = (sTokenSubject==null) || (StringUtils.isEmpty(sTokenSubject)) || (sTokenSubject.equals(sTokenClientId));
		}
				
		// 4a) Client ID, per il caso di ClientCredential
		if(clientCredentialsFlow &&
			StringUtils.isNotEmpty(sTokenClientId)) {
			return sTokenClientId;
		}
		
		// 4b) Subject/Issuer del Token
		if(StringUtils.isNotEmpty(sTokenSubject)) {
			
			String sTokenIssuer = infoDatiMittente.getTokenIssuer();
			if(StringUtils.isNotEmpty(sTokenIssuer)) {
				return sTokenSubject + NamingUtils.LABEL_DOMINIO + sTokenIssuer;
			}
			else {
				return sTokenSubject;
			}
		}
		
		// 4c) Client ID, per il caso diverso da ClientCredential
		if(!clientCredentialsFlow &&
			StringUtils.isNotEmpty(sTokenClientId)) {
			return sTokenClientId;
		}
		
		// 5) Credenziali dell'autenticazione di trasporto
		// volutamente uso l'id autenticato.
		// se l'api è pubblica non deve essere visualizzata questa informazione!
		String sTrasporto = getRichiedenteTrasporto(infoDatiMittente);
		if(sTrasporto!=null) {
			return sTrasporto;
		}
						
		return null;
		
	}
	
	private static String getRichiedenteTrasporto(DatiMittente infoDatiMittente) {
		String sTrasportoMittente = infoDatiMittente.getTrasportoMittente();
		String sTipoTrasportoMittente = infoDatiMittente.getTipoTrasportoMittente();
		if(StringUtils.isNotEmpty(sTrasportoMittente) && StringUtils.isNotEmpty(sTipoTrasportoMittente)) {
			if(sTipoTrasportoMittente.endsWith("_"+TipoAutenticazione.SSL.getValue())) {
				return getRichiedenteTrasportoSSLEngine(sTrasportoMittente);
			}
			else {
				return sTrasportoMittente;
			}
		}
		return null;
	}
	private static String getRichiedenteTrasportoSSLEngine(String sTrasportoMittente) {
		try {
			Map<String, List<String>> l = CertificateUtils.getPrincipalIntoMap(sTrasportoMittente, PrincipalType.SUBJECT);
			if(l!=null && !l.isEmpty()) {
				List<String> cnList = getCNList(l);
				if(cnList!=null && !cnList.isEmpty()) {
					StringBuilder bfList = new StringBuilder();
					for (String s : cnList) {
						if(bfList.length()>0) {
							bfList.append(", ");
						}
						bfList.append(s);
					}
					return bfList.toString();
				}
			}
			return sTrasportoMittente;
		}catch(Exception t) {	
			return sTrasportoMittente;
		}
	}
	private static List<String> getCNList(Map<String, List<String>> l){
		List<String> cnList = l.get("CN");
		if(cnList==null || cnList.isEmpty()) {
			cnList = l.get("cn");
		}
		if(cnList==null || cnList.isEmpty()) {
			cnList = l.get("Cn");
		}
		if(cnList==null || cnList.isEmpty()) {
			cnList = l.get("cN");
		}
		return cnList;
	}
	
	public static String getIpRichiedente(DatiMittente infoDatiMittente) {
		
		String t = infoDatiMittente.getTransportClientAddress();
		if(StringUtils.isNotEmpty(t)) {
			return t;
		}
		
		String s = infoDatiMittente.getSocketClientAddress();
		if(StringUtils.isNotEmpty(s)) {
			return s;
		}
		
		return null;
		
	}
	
	public static String getLabelRichiedenteConFruitore(DatiMittente infoDatiMittente) {
		StringBuilder bf = new StringBuilder();
		
		String richiedente = getRichiedente(infoDatiMittente);
		if(StringUtils.isNotEmpty(richiedente)) {
			bf.append(richiedente);	
		}
		
		String sTokenClient = infoDatiMittente.getTokenClient();
		if(StringUtils.isNotEmpty(sTokenClient)) {
			
			// dominio di un applicativo client
			processLabelRichiedenteConFruitoreTokenClient(infoDatiMittente, bf);
			
		}
		else {
			
			// dominio del soggetto fruitore
			processLabelRichiedenteConFruitore(infoDatiMittente, bf, richiedente);
			
		}
		
		return bf.toString();
	}
	
	private static void processLabelRichiedenteConFruitoreTokenClient(DatiMittente infoDatiMittente, StringBuilder bfParam) {
		if(infoDatiMittente.getTokenClientSoggettoFruitore()!=null &&
				infoDatiMittente.getTokenClientTipoSoggettoFruitore()!=null && infoDatiMittente.getTokenClientNomeSoggettoFruitore()!=null) {
			
			boolean addFruitore = true;
			
			IDSoggetto idSoggettoFruitore = new IDSoggetto(infoDatiMittente.getTokenClientTipoSoggettoFruitore(), infoDatiMittente.getTokenClientNomeSoggettoFruitore());
			
			if(org.openspcoop2.core.transazioni.constants.PddRuolo.DELEGATA.equals(infoDatiMittente.getPddRuolo())) {
				addFruitore = 
						/**(idSoggettoFruitore==null) ||*/ 
						(infoDatiMittente.getSoggettoOperativo()==null) || (!infoDatiMittente.getSoggettoOperativo().equals(idSoggettoFruitore.toString()));
			}
			
			if(addFruitore) {
				if(bfParam.length()>0) {
					bfParam.append(NamingUtils.LABEL_DOMINIO);
				}
				
				bfParam.append(infoDatiMittente.getTokenClientSoggettoFruitore());	
			}
		}
	}
	
	private static void processLabelRichiedenteConFruitore(DatiMittente infoDatiMittente, StringBuilder bfParam, String richiedente) {
		String sFruitore = infoDatiMittente.getSoggettoFruitore();
		if(StringUtils.isNotEmpty(sFruitore)) {

			boolean addFruitore = true;
						
			if(org.openspcoop2.core.transazioni.constants.PddRuolo.APPLICATIVA.equals(infoDatiMittente.getPddRuolo())) {
				
				processLabelRichiedenteConFruitorePortaApplicativa(infoDatiMittente, bfParam, richiedente);
				
			}
			else if(org.openspcoop2.core.transazioni.constants.PddRuolo.DELEGATA.equals(infoDatiMittente.getPddRuolo()) &&
					(infoDatiMittente.getSoggettoOperativo()!=null && StringUtils.isNotEmpty(infoDatiMittente.getTipoSoggettoFruitore()) && StringUtils.isNotEmpty(infoDatiMittente.getNomeSoggettoFruitore())) 
				) {
			
				IDSoggetto idSoggettoFruitore = new IDSoggetto(infoDatiMittente.getTipoSoggettoFruitore(), infoDatiMittente.getNomeSoggettoFruitore());
				addFruitore = !infoDatiMittente.getSoggettoOperativo().equals(idSoggettoFruitore.toString());
			
			}
			
			if(addFruitore) {
				if(bfParam.length()>0) {
					bfParam.append(NamingUtils.LABEL_DOMINIO);
				}
				
				bfParam.append(sFruitore);	
			}
		}
	}
	
	private static void processLabelRichiedenteConFruitorePortaApplicativa(DatiMittente infoDatiMittente, StringBuilder bfParam, String richiedente) {
		// L'AppId di un soggetto è già il soggetto. L'informazione sarebbe ridondante.
		String sTrasportoMittente = infoDatiMittente.getTrasportoMittente();
		if(richiedente!=null && sTrasportoMittente!=null && richiedente.equals(sTrasportoMittente)) { // se e' stato selezionato l'appId
			String sTipoTrasportoMittente = infoDatiMittente.getTipoTrasportoMittente();
			if(sTipoTrasportoMittente!=null && StringUtils.isNotEmpty(sTipoTrasportoMittente) && 
					sTipoTrasportoMittente.endsWith("_"+TipoAutenticazione.APIKEY.getValue()) &&
				// autenticazione api-key
				(!sTrasportoMittente.contains(ApiKeyUtilities.APPLICATIVO_SOGGETTO_SEPARATOR)) 
				&&
				bfParam.length()>0
				){
				// appId di un soggetto
				bfParam.setLength(0); // svuoto il buffer poichè aggiungo solo il soggetto
			}
		}
	}
	
}
