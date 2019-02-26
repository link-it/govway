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
 * Esito di un processo di gestione token.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class EsitoGestioneToken extends EsitoToken implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean valido;
	
	private boolean inCache;

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
	
	public boolean isInCache() {
		return this.inCache;
	}
	public void setInCache(boolean inCache) {
		this.inCache = inCache;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		
		bf.append("token valido: ");
		bf.append(this.valido);
		
		bf.append(" info in cache: ");
		bf.append(this.inCache);
		
		bf.append(super.toString());
		
		return bf.toString();
	}
}
