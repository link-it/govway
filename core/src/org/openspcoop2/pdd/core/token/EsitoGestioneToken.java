/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.token;

/**
 * Esito di un processo di gestione token.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 13726 $, $Date: 2018-03-12 17:01:57 +0100 (Mon, 12 Mar 2018) $
 */
public abstract class EsitoGestioneToken extends EsitoToken implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean valido;

	/** Informazioni sul token */
	private InformazioniToken informazioniToken;
		
	public boolean isValido() {
		return this.valido;
	}
	public void setValido(boolean valido) {
		this.valido = valido;
	}
	
	public InformazioniToken getInformazioniToken() {
		return this.informazioniToken;
	}
	public void setInformazioniToken(InformazioniToken informazioniToken) {
		this.informazioniToken = informazioniToken;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		
		bf.append("token valido: ");
		bf.append(this.valido);
		
		bf.append(super.toString());
		
		return bf.toString();
	}
}
