/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

package org.openspcoop2.protocol.abstraction.template;

/**     
 * DatiServizioApplicativoFruitore
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiServizioApplicativoFruitore {

	private String nome;
	private String nomePortaDelegata;
	private String autenticazione;
	private String basicUsername;
	private String basicPassword;
	private String sslSubject;
	
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getAutenticazione() {
		return this.autenticazione;
	}
	public void setAutenticazione(String autenticazione) {
		this.autenticazione = autenticazione;
	}
	public String getBasicUsername() {
		return this.basicUsername;
	}
	public void setBasicUsername(String basicUsername) {
		this.basicUsername = basicUsername;
	}
	public String getBasicPassword() {
		return this.basicPassword;
	}
	public void setBasicPassword(String basicPassword) {
		this.basicPassword = basicPassword;
	}
	public String getSslSubject() {
		return this.sslSubject;
	}
	public void setSslSubject(String sslSubject) {
		this.sslSubject = sslSubject;
	}
	public String getNomePortaDelegata() {
		return this.nomePortaDelegata;
	}
	public void setNomePortaDelegata(String nomePortaDelegata) {
		this.nomePortaDelegata = nomePortaDelegata;
	}
	
}
