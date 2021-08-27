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



package org.openspcoop2.pdd.core.token.attribute_authority;

import org.openspcoop2.pdd.core.token.AbstractEsitoValidazioneToken;

/**
 * Esito di un processo di negoziazione degli attributi verso un Attribute Authority.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoRecuperoAttributi extends AbstractEsitoValidazioneToken implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Informazioni sul token */
	private InformazioniAttributi informazioniAttributi;
		
	public InformazioniAttributi getInformazioniAttributi() {
		return this.informazioniAttributi;
	}
	public void setInformazioniAttributi(InformazioniAttributi informazioniAttributi) {
		this.informazioniAttributi = informazioniAttributi;
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		
		bf.append(super.toString());
		
		return bf.toString();
	}
}
