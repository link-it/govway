/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.autenticazione;

import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * PrincipalUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PrincipalUtilities {

	public static String getPrincipal(TipoAutenticazionePrincipal tipoAutenticazionePrincipal, String nome, String pattern, TipoCredenzialeMittente token,
			InfoConnettoreIngresso infoConnettore, PdDContext pddContext, boolean throwException,
			StringBuilder fullCredential) throws AutenticazioneException {
		
		String principal = null;
		switch (tipoAutenticazionePrincipal) {
		case CONTAINER:
			if(infoConnettore!=null && infoConnettore.getCredenziali()!=null) {
				principal = infoConnettore.getCredenziali().getPrincipal();
			}
			if(principal==null || "".equals(principal)) {
				if(throwException) {
					throw new AutenticazioneException("["+tipoAutenticazionePrincipal+"] Principal non presente all'interno delle credenziali");
				}
			}
			/*
			 * L'info 'container' viene gia' registrata in automatico tra le credenziali come principal.
			if(fullCredential.length()>0) {
				fullCredential.append("\n");
			}
			fullCredential.append("Principal").append(" (container) '").append(principal).append("'");
			*/
			return principal;
		case HEADER:
			if(nome==null && throwException) {
				throw new AutenticazioneException("["+tipoAutenticazionePrincipal+"] Nome dell'header, da cui estrarre il principal, non indicato");
			}
			if(nome!=null && infoConnettore!=null && infoConnettore.getUrlProtocolContext()!=null) {
				principal = infoConnettore.getUrlProtocolContext().getParameterTrasporto(nome);
			}
			if(principal==null || "".equals(principal)) {
				if(throwException) {
					throw new AutenticazioneException("["+tipoAutenticazionePrincipal+"] Principal non presente nell'header http '"+nome+"'");
				}
			}
			if(fullCredential.length()>0) {
				fullCredential.append("\n");
			}
			fullCredential.append("Principal").append(" (http) '").append(nome).append(": ").append(principal).append("'");
			return principal;
		case FORM:
			if(nome==null && throwException) {
				throw new AutenticazioneException("["+tipoAutenticazionePrincipal+"] Nome del parametro della query, da cui estrarre il principal, non indicato");
			}
			if(nome!=null && infoConnettore!=null && infoConnettore.getUrlProtocolContext()!=null) {
				principal = infoConnettore.getUrlProtocolContext().getParameterFormBased(nome);
			}
			if(principal==null || "".equals(principal)) {
				if(throwException) {
					throw new AutenticazioneException("["+tipoAutenticazionePrincipal+"] Principal non presente nel parametro della query '"+nome+"'");
				}
			}
			if(fullCredential.length()>0) {
				fullCredential.append("\n");
			}
			fullCredential.append("Principal").append(" (query) '").append(nome).append(": ").append(principal).append("'");
			return principal;
		case URL:
			if(pattern==null && throwException) {
				throw new AutenticazioneException("["+tipoAutenticazionePrincipal+"] Espressione Regolare, da utilizzare sulla url per estrarre il principal, non indicata");
			}
			String msgErrore = "["+tipoAutenticazionePrincipal+"] Principal non estraibile con l'espressione regolare '"+pattern+"', url non presente";
			if(pattern!=null && infoConnettore!=null && infoConnettore.getUrlProtocolContext()!=null) {
				msgErrore = "["+tipoAutenticazionePrincipal+"] Principal non estraibile con l'espressione regolare '"+pattern+"' dalla url '"+infoConnettore.getUrlProtocolContext().getUrlInvocazione_formBased()+"'";
				try {
					principal = RegularExpressionEngine.getStringMatchPattern(infoConnettore.getUrlProtocolContext().getUrlInvocazione_formBased(), pattern);
				}catch(Throwable t) {
					if(throwException) {
						throw new AutenticazioneException(msgErrore+": "+t.getMessage(), t);
					}
				}
			}
			if(principal==null || "".equals(principal)) {
				if(throwException) {
					throw new AutenticazioneException(msgErrore);
				}
			}
			/*
			 * La url viene gia' registrata in automatico tra le info della transazione
			if(fullCredential.length()>0) {
				fullCredential.append("\n");
			}
			fullCredential.append("Principal").append(" (url) '").append(principal).append("'");
			*/
			return principal;
		case INDIRIZZO_IP:
			if(pddContext!=null && pddContext.containsKey(Costanti.CLIENT_IP_REMOTE_ADDRESS)) {
				principal = PdDContext.getValue(Costanti.CLIENT_IP_REMOTE_ADDRESS, pddContext);
			}
			if(principal==null || "".equals(principal)) {
				if(throwException) {
					throw new AutenticazioneException("["+tipoAutenticazionePrincipal+"] Indirizzo IP del Client non disponibile");
				}
			}
			/*
			 * Il client ip viene gia' registrato in automatico tra le info della transazione
			if(fullCredential.length()>0) {
				fullCredential.append("\n");
			}
			fullCredential.append("Principal").append(" (client-ip) '").append(principal).append("'");
			*/
			return principal;
		case INDIRIZZO_IP_X_FORWARDED_FOR:
			if(pddContext!=null && pddContext.containsKey(Costanti.CLIENT_IP_TRANSPORT_ADDRESS)) {
				principal = PdDContext.getValue(Costanti.CLIENT_IP_TRANSPORT_ADDRESS, pddContext);
			}
			if(principal==null || "".equals(principal)) {
				if(throwException) {
					throw new AutenticazioneException("["+tipoAutenticazionePrincipal+"] Indirizzo IP del Client, tramite 'X-Forwarded-For', non disponibile");
				}
			}
			/*
			 * L'indirizzo ip forwared viene gia' registrato in automatico tra le info della transazione
			if(fullCredential.length()>0) {
				fullCredential.append("\n");
			}
			fullCredential.append("Principal").append(" (forwarded-ip) '").append(principal).append("'");
			*/
			return principal;
		// Ho levato il contenuto, poichè senno devo fare il digest per poterlo poi cachare
//			case CONTENT:
		case TOKEN:
			InformazioniToken informazioniTokenNormalizzate = null;
			if(pddContext!=null && pddContext.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE)) {
				Object oInformazioniTokenNormalizzate = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
	    		if(oInformazioniTokenNormalizzate!=null) {
	    			informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
	    		}
			}
			String nomeClaim = null;
			switch (token) {
			case token_issuer:
				nomeClaim = "issuer";
				if(informazioniTokenNormalizzate!=null) {
					principal = informazioniTokenNormalizzate.getIss();
				}
				break;
			case token_subject:
				nomeClaim = "subject";
				if(informazioniTokenNormalizzate!=null) {
					principal = informazioniTokenNormalizzate.getSub();
				}
				break;
			case token_clientId:
				nomeClaim = "clientId";
				if(informazioniTokenNormalizzate!=null) {
					principal = informazioniTokenNormalizzate.getClientId();
				}
				break;
			case token_username:
				nomeClaim = "username";
				if(informazioniTokenNormalizzate!=null) {
					principal = informazioniTokenNormalizzate.getUsername();
				}
				break;
			case token_eMail:
				nomeClaim = "eMail";
				if(informazioniTokenNormalizzate!=null && informazioniTokenNormalizzate.getUserInfo()!=null) {
					principal = informazioniTokenNormalizzate.getUserInfo().getEMail();
				}
				break;
			case trasporto:
				nomeClaim = nome;
				if(informazioniTokenNormalizzate!=null && informazioniTokenNormalizzate.getClaims()!=null) {
					Object oValueClaim = informazioniTokenNormalizzate.getClaims().get(nomeClaim);
					if(oValueClaim!=null && oValueClaim instanceof String) {
						principal = (String) oValueClaim;
					}
				}
				break;
			case client_address:
			case gruppi:
			case api:
			case eventi:
				// non usati in questo contesto
				break;
			}
			if(principal==null || "".equals(principal)) {
				if(throwException) {
					throw new AutenticazioneException("["+tipoAutenticazionePrincipal+"] Token claim '"+nomeClaim+"' non disponibile");
				}
			}
			/*
			 * Le info sul token vengono gia' registrate in automatico tra le info della transazione
			if(fullCredential.length()>0) {
				fullCredential.append("\n");
			}
			fullCredential.append("Principal").append(" (token) '").append(nomeClaim).append(": ").append(principal).append("'");
			*/
			return principal;
		}
		return null;
	}
	
}
