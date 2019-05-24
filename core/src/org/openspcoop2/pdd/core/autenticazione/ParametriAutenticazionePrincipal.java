/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;

/**
 * ParametriAutenticazionePrincipal
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParametriAutenticazionePrincipal extends ParametriAutenticazione implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TIPO_AUTENTICAZIONE = "tipoAutenticazione";
	public static final String NOME = "nome";
	public static final String PATTERN = "pattern";
	
	public static final String CLEAN_PRINCIPAL = "cleanPrincipal";
	public static final String CLEAN_PRINCIPAL_TRUE = "true";
	public static final String CLEAN_PRINCIPAL_FALSE = "false";
	
	public static final String TOKEN_CLAIM = "claim";
	public static final String TOKEN_CLAIM_ISSUER = "issuer";
	public static final String TOKEN_CLAIM_SUBJECT = "subject";
	public static final String TOKEN_CLAIM_CLIENT_ID = "clientId";
	public static final String TOKEN_CLAIM_USERNAME = "username";
	public static final String TOKEN_CLAIM_EMAIL = "eMail";
	public static final String TOKEN_CLAIM_CUSTOM = "custom";
	
	public ParametriAutenticazionePrincipal(ParametriAutenticazione parametri) {
		super(parametri);
	}
	
	public TipoAutenticazionePrincipal getTipoAutenticazione() {
		String valore = this.get(TIPO_AUTENTICAZIONE);
		if(valore==null || "".equals(valore)) {
			return null;
		}
		return TipoAutenticazionePrincipal.toEnumConstant(valore);
	}
	
	public String getNome() {
		String valore = this.get(NOME);
		if(valore==null || "".equals(valore)) {
			return null;
		}
		return valore;
	}
	
	public String getPattern() {
		String valore = this.get(PATTERN);
		if(valore==null || "".equals(valore)) {
			return null;
		}
		return valore;
	}
	
	public Boolean getCleanPrincipal() {
		String valore = this.get(CLEAN_PRINCIPAL);
		if(valore==null || "".equals(valore)) {
			return null;
		}
		if(CLEAN_PRINCIPAL_FALSE.equalsIgnoreCase(valore)) {
			return false;
		}
		else if(CLEAN_PRINCIPAL_TRUE.equalsIgnoreCase(valore)) {
			return true;
		}
		return null;
	}
	
	public TipoCredenzialeMittente getTokenClaim() {
		String valore = this.get(TOKEN_CLAIM);
		if(valore==null || "".equals(valore)) {
			return null;
		}
		if(TOKEN_CLAIM_ISSUER.equalsIgnoreCase(valore)) {
			return TipoCredenzialeMittente.token_issuer;
		}
		else if(TOKEN_CLAIM_SUBJECT.equalsIgnoreCase(valore)) {
			return TipoCredenzialeMittente.token_subject;
		}
		else if(TOKEN_CLAIM_CLIENT_ID.equalsIgnoreCase(valore)) {
			return TipoCredenzialeMittente.token_clientId;
		}
		else if(TOKEN_CLAIM_USERNAME.equalsIgnoreCase(valore)) {
			return TipoCredenzialeMittente.token_username;
		}
		else if(TOKEN_CLAIM_EMAIL.equalsIgnoreCase(valore)) {
			return TipoCredenzialeMittente.token_eMail;
		}
		else if(TOKEN_CLAIM_CUSTOM.equalsIgnoreCase(valore)) {
			return TipoCredenzialeMittente.trasporto; // uso trasporto come custom
		}
		return null;
	}
}
