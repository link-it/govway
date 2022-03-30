/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class EsitoGestioneToken extends AbstractEsitoValidazioneToken implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Informazioni sul token */
	private InformazioniToken informazioniToken;
		
	
	public InformazioniToken getInformazioniToken() {
		return this.informazioniToken;
	}
	public void setInformazioniToken(InformazioniToken informazioniToken) {
		this.informazioniToken = informazioniToken;
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		
		bf.append(super.toString());
		
		return bf.toString();
	}
}
