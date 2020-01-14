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



package org.openspcoop2.pdd.core.autenticazione;

/**
 * ParametriAutenticazioneBasic
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParametriAutenticazioneBasic extends ParametriAutenticazione implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String CLEAN_HEADER_AUTHORIZATION = "cleanAuthorizationHeader";
	public static final String CLEAN_HEADER_AUTHORIZATION_TRUE = "true";
	public static final String CLEAN_HEADER_AUTHORIZATION_FALSE = "false";

	public ParametriAutenticazioneBasic(ParametriAutenticazione parametri) {
		super(parametri);
	}
	
	public Boolean getCleanHeaderAuthorization() {
		String valore = this.get(CLEAN_HEADER_AUTHORIZATION);
		if(valore==null || "".equals(valore)) {
			return null;
		}
		if(CLEAN_HEADER_AUTHORIZATION_FALSE.equalsIgnoreCase(valore)) {
			return false;
		}
		else if(CLEAN_HEADER_AUTHORIZATION_TRUE.equalsIgnoreCase(valore)) {
			return true;
		}
		return null;
	}
}
