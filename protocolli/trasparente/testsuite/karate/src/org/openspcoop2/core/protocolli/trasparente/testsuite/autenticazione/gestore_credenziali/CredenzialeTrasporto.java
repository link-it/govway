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
package org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.gestore_credenziali;

/**
* CredenzialeTrasporto
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class CredenzialeTrasporto {

	private String credenziali;
	private String tipoCredenziali;
	private String identificativoAutenticato;
	
	public static final CredenzialeTrasporto NULL = null;
	
	public CredenzialeTrasporto(String credenziali, String tipoCredenziali, String identificativoAutenticato) {
		this.credenziali = credenziali;
		this.tipoCredenziali = tipoCredenziali;
		this.identificativoAutenticato = identificativoAutenticato;
	}
	public CredenzialeTrasporto(String credenziali) {
		this.credenziali = credenziali;
	}
	
	public String getCredenziali() {
		return this.credenziali;
	}

	public void setCredenziali(String credenziali) {
		this.credenziali = credenziali;
	}

	public String getTipoCredenziali() {
		return this.tipoCredenziali;
	}

	public void setTipoCredenziali(String tipoCredenziali) {
		this.tipoCredenziali = tipoCredenziali;
	}

	public String getIdentificativoAutenticato() {
		return this.identificativoAutenticato;
	}

	public void setIdentificativoAutenticato(String identificativoAutenticato) {
		this.identificativoAutenticato = identificativoAutenticato;
	}
}
