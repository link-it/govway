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



package org.openspcoop2.pdd.core.token;

/**
 * Esito di un processo di negoziazione token.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoNegoziazioneToken extends AbstractEsitoValidazioneToken implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Informazioni sul token */
	private InformazioniNegoziazioneToken informazioniNegoziazioneToken;
		
	public InformazioniNegoziazioneToken getInformazioniNegoziazioneToken() {
		return this.informazioniNegoziazioneToken;
	}
	public void setInformazioniNegoziazioneToken(InformazioniNegoziazioneToken informazioniNegoziazioneToken) {
		this.informazioniNegoziazioneToken = informazioniNegoziazioneToken;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		
		bf.append(super.toString());
		
		return bf.toString();
	}
}