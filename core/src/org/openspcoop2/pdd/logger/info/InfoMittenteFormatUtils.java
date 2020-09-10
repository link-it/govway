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

package org.openspcoop2.pdd.logger.info;

import java.util.Hashtable;
import java.util.List;

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

	public static String getRichiedente(DatiMittente infoDatiMittente) {
		
		// 1) Username del Token
		String sTokenUsername = infoDatiMittente.getTokenUsername();
		if(StringUtils.isNotEmpty(sTokenUsername)) {
			return sTokenUsername;
		}
		
		// 2) Subject/Issuer del Token
		String sTokenSubject = infoDatiMittente.getTokenSubject();
		if(StringUtils.isNotEmpty(sTokenSubject)) {
			
			String sTokenIssuer = infoDatiMittente.getTokenIssuer();
			if(StringUtils.isNotEmpty(sTokenIssuer)) {
				return sTokenSubject + NamingUtils.LABEL_DOMINIO + sTokenIssuer;
			}
			else {
				return sTokenSubject;
			}
		}
		
		// 3) Applicativo Fruitore
		String sApplicativoFruitore = infoDatiMittente.getServizioApplicativoFruitore();
		if(StringUtils.isNotEmpty(sApplicativoFruitore)) {
			return sApplicativoFruitore;
		}
		
		// 4) Credenziali dell'autenticazione di trasporto
		// volutamente uso l'id autenticato.
		// se l'api è pubblica non deve essere visualizzata questa informazione!
		String sTrasportoMittente = infoDatiMittente.getTrasportoMittente();
		String sTipoTrasportoMittente = infoDatiMittente.getTipoTrasportoMittente();
		if(StringUtils.isNotEmpty(sTrasportoMittente) && StringUtils.isNotEmpty(sTipoTrasportoMittente)) {
			if(sTipoTrasportoMittente.endsWith("_"+TipoAutenticazione.SSL.getValue())) {
				try {
					Hashtable<String, List<String>> l = CertificateUtils.getPrincipalIntoHashtable(sTrasportoMittente, PrincipalType.subject);
					if(l!=null && !l.isEmpty()) {
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
						if(cnList!=null && cnList.size()>0) {
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
				}catch(Throwable t) {	
					return sTrasportoMittente;
				}
			}
			else {
				return sTrasportoMittente;
			}
		}
		
		// 5) Client ID, per il caso di ClientCredential
		String sTokenClientId = infoDatiMittente.getTokenClientId();
		if(StringUtils.isNotEmpty(sTokenClientId)) {
			return sTokenClientId;
		}
		
		return null;
		
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
	
	public static String getLabelRichiedenteConFruitore(DatiMittente infoDatiMittente) throws Exception {
		StringBuilder bf = new StringBuilder();
		
		String richiedente = getRichiedente(infoDatiMittente);
		if(StringUtils.isNotEmpty(richiedente)) {
			bf.append(richiedente);	
		}
		
		
		
		String sFruitore = infoDatiMittente.getSoggettoFruitore();
		if(StringUtils.isNotEmpty(sFruitore)) {

			boolean addFruitore = true;
						
			if(org.openspcoop2.core.transazioni.constants.PddRuolo.APPLICATIVA.equals(infoDatiMittente.getPddRuolo())) {
				
				// L'AppId di un soggetto è già il soggetto. L'informazione sarebbe ridondante.
				String sTrasportoMittente = infoDatiMittente.getTrasportoMittente();
				if(richiedente!=null && sTrasportoMittente!=null && richiedente.equals(sTrasportoMittente)) { // se e' stato selezionato l'appId
					String sTipoTrasportoMittente = infoDatiMittente.getTipoTrasportoMittente();
					if(sTipoTrasportoMittente!=null && StringUtils.isNotEmpty(sTipoTrasportoMittente) && 
							sTipoTrasportoMittente.endsWith("_"+TipoAutenticazione.APIKEY.getValue())) {
						// autenticazione api-key
						if(!sTrasportoMittente.contains(ApiKeyUtilities.APPLICATIVO_SOGGETTO_SEPARATOR)) {
							// appId di un soggetto
							bf = new StringBuilder(); // aggiunto solo il soggetto
						}		
					}
				}
				
			}
			else if(org.openspcoop2.core.transazioni.constants.PddRuolo.DELEGATA.equals(infoDatiMittente.getPddRuolo())) {
				
				if(infoDatiMittente.getSoggettoOperativo()!=null && StringUtils.isNotEmpty(infoDatiMittente.getTipoSoggettoFruitore()) && StringUtils.isNotEmpty(infoDatiMittente.getNomeSoggettoFruitore())) {
					IDSoggetto idSoggettoFruitore = new IDSoggetto(infoDatiMittente.getTipoSoggettoFruitore(), infoDatiMittente.getNomeSoggettoFruitore());
					addFruitore = !infoDatiMittente.getSoggettoOperativo().equals(idSoggettoFruitore.toString());
				}
				
			}
			
			if(addFruitore) {
				if(bf.length()>0) {
					bf.append(NamingUtils.LABEL_DOMINIO);
				}
				
				bf.append(sFruitore);	
			}
		}
		
		return bf.toString();
	}
	
}
