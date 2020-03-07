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



package org.openspcoop2.pdd.core.token;

/**
 * Esito di un processo di validazione token.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractEsitoValidazioneToken extends EsitoToken implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean valido;
	
	private boolean dateValide;
	
	private boolean inCache;

	public boolean isValido() {
		return this.valido;
	}
	public void setValido(boolean valido) {
		this.valido = valido;
	}
	
	public boolean isInCache() {
		return this.inCache;
	}
	public void setInCache(boolean inCache) {
		this.inCache = inCache;
	}
	
	public boolean isDateValide() {
		return this.dateValide;
	}
	public void setDateValide(boolean dateValide) {
		this.dateValide = dateValide;
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		
		bf.append("token valido: ");
		bf.append(this.valido);
		
		bf.append("date valide: ");
		bf.append(this.dateValide);
		
		bf.append(" info in cache: ");
		bf.append(this.inCache);
		
		bf.append(super.toString());
		
		return bf.toString();
	}
}
