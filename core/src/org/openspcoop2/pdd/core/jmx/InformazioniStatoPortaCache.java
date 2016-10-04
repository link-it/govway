/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.pdd.core.jmx;

/**
 * InformazioniStatoPortaCache
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniStatoPortaCache {

	private String nomeCache = null;
	private boolean enabled = false;
	private String statoCache = null;
	
	public InformazioniStatoPortaCache(String nomeCache,boolean enabled){
		this.nomeCache = nomeCache;
		this.enabled = enabled;
	}
	
	public String getNomeCache() {
		return this.nomeCache;
	}

	public void setNomeCache(String nomeCache) {
		this.nomeCache = nomeCache;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getStatoCache() {
		return this.statoCache;
	}

	public void setStatoCache(String statoCache) {
		this.statoCache = statoCache;
	}
}
