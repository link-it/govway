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

package org.openspcoop2.web.ctrlstat.core;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.registry.constants.RuoloTipologia;

/**
 * AutorizzazioneUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class AutorizzazioneUtilities {

	public static final String STATO_ABILITATO = StatoFunzionalita.ABILITATO.getValue();
	public static final String STATO_DISABILITATO = StatoFunzionalita.DISABILITATO.getValue();
	public static final String STATO_XACML_POLICY = "xacml-Policy";
	public static List<String> getStati(){
		List<String> l = new ArrayList<>();
		l.add(STATO_DISABILITATO);
		l.add(STATO_ABILITATO);
		l.add(STATO_XACML_POLICY);
		return l;
	}
	public static String convertToStato(String autorizzazione){
		return convertToStato(TipoAutorizzazione.toEnumConstant(autorizzazione));
	}
	public static String convertToStato(TipoAutorizzazione autorizzazione){
		if(TipoAutorizzazione.isXacmlPolicyRequired(autorizzazione)){
			return STATO_XACML_POLICY;
		}
		else if(TipoAutorizzazione.DISABILITATO.equals(autorizzazione)){
			return STATO_DISABILITATO;
		}
		else if(TipoAutorizzazione.TOKEN.equals(autorizzazione)){
			return STATO_ABILITATO;
		}
		else{
			return STATO_ABILITATO;
		}
	}
	
	public static RuoloTipologia convertToRuoloTipologia(String autorizzazione){
		return convertToRuoloTipologia(TipoAutorizzazione.toEnumConstant(autorizzazione));
	}
	public static RuoloTipologia convertToRuoloTipologia(TipoAutorizzazione autorizzazione){
		if(autorizzazione==null){
			return RuoloTipologia.QUALSIASI;
		}
		switch (autorizzazione) {
		case AUTHENTICATED_EXTERNAL_ROLES:
		case EXTERNAL_ROLES:
		case EXTERNAL_XACML_POLICY:
			return RuoloTipologia.ESTERNO;
		case AUTHENTICATED_INTERNAL_ROLES:
		case INTERNAL_ROLES:
		case INTERNAL_XACML_POLICY:
			return RuoloTipologia.INTERNO;
		case AUTHENTICATED_ROLES:
		case ROLES:
		case XACML_POLICY:
			return RuoloTipologia.QUALSIASI;
		default:
			return RuoloTipologia.QUALSIASI;
		}
		
	}
	public static TipoAutorizzazione convertToTipoAutorizzazione(String stato,boolean authenticated,boolean roles,
			boolean tokenAuthenticated, boolean tokenRoles,
			boolean scopes,String autorizzazione_tokenOptions,
			RuoloTipologia tipologia){
		if(STATO_DISABILITATO.equals(stato)){
			return TipoAutorizzazione.DISABILITATO;
		}
		else if(STATO_XACML_POLICY.equals(stato)){
			switch (tipologia) {
			case ESTERNO:
				return TipoAutorizzazione.EXTERNAL_XACML_POLICY;
			case INTERNO:
				return TipoAutorizzazione.INTERNAL_XACML_POLICY;
			case QUALSIASI:
				return TipoAutorizzazione.XACML_POLICY;
			}
		}
		else {
			if(roles){
				switch (tipologia) {
				case ESTERNO:
					if(authenticated){
						return TipoAutorizzazione.AUTHENTICATED_EXTERNAL_ROLES;
					}
					else{
						return TipoAutorizzazione.EXTERNAL_ROLES;
					}
				case INTERNO:
					if(authenticated){
						return TipoAutorizzazione.AUTHENTICATED_INTERNAL_ROLES;
					}
					else{
						return TipoAutorizzazione.INTERNAL_ROLES;
					}
				case QUALSIASI:
					if(authenticated){
						return TipoAutorizzazione.AUTHENTICATED_ROLES;
					}
					else{
						return TipoAutorizzazione.ROLES;
					}
				}
			}
			else if(authenticated){
				return TipoAutorizzazione.AUTHENTICATED;
			}
			else if(tokenAuthenticated || tokenRoles || scopes || (autorizzazione_tokenOptions!=null && !"".equals(autorizzazione_tokenOptions))) {
				return TipoAutorizzazione.TOKEN;
			}
		}
		return TipoAutorizzazione.DISABILITATO; // ??
	}
	public static String convertToTipoAutorizzazioneAsString(String stato,boolean authenticated,boolean roles,
			boolean tokenAuthenticated, boolean tokenRoles,
			boolean scopes,String autorizzazione_tokenOptions, 
			RuoloTipologia tipologia){
		return convertToTipoAutorizzazione(stato, authenticated, roles, 
				tokenAuthenticated, tokenRoles,
				scopes, autorizzazione_tokenOptions, 
				tipologia).getValue();
	}
	
}
