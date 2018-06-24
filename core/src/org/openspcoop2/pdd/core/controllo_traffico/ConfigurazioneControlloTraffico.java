/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.io.Serializable;

/**     
 * ConfigurazioneControlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneControlloTraffico extends org.openspcoop2.core.controllo_traffico.beans.ConfigurazioneControlloTraffico implements Serializable , Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String tipoDatabaseConfig;
	
	private boolean erroreGenerico; // indicazione se al client deve essere ritornato un errore generico
	
	private boolean policyReadedWithDynamicCache;
		
	private boolean notifierEnabled;
	
	private INotify notifier;
	

	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		
		bf.append("tipoDatabaseConfig="+this.tipoDatabaseConfig);
				
		bf.append(", ");
		bf.append("erroreGenerico="+this.erroreGenerico);
		
		bf.append(", ");
		bf.append("policyReadedWithDynamicCache="+this.policyReadedWithDynamicCache);
		
		bf.append(", ");
		bf.append("notifierEnabled="+this.notifierEnabled);
		
		if(this.notifierEnabled) {
			bf.append(", ");
			bf.append("notifier="+this.notifier.getClass().getName());
		}

		bf.append(", ");
		bf.append(super.toString());
		
		return bf.toString();
	}
	
	
	public String getTipoDatabaseConfig() {
		return this.tipoDatabaseConfig;
	}


	public void setTipoDatabaseConfig(String tipoDatabaseConfig) {
		this.tipoDatabaseConfig = tipoDatabaseConfig;
	}


	public boolean isErroreGenerico() {
		return this.erroreGenerico;
	}


	public void setErroreGenerico(boolean erroreGenerico) {
		this.erroreGenerico = erroreGenerico;
	}


	public boolean isPolicyReadedWithDynamicCache() {
		return this.policyReadedWithDynamicCache;
	}


	public void setPolicyReadedWithDynamicCache(boolean policyReadedWithDynamicCache) {
		this.policyReadedWithDynamicCache = policyReadedWithDynamicCache;
	}

	public boolean isNotifierEnabled() {
		return this.notifierEnabled;
	}

	public void setNotifierEnabled(boolean notifierEnabled) {
		this.notifierEnabled = notifierEnabled;
	}

	public INotify getNotifier() {
		return this.notifier;
	}

	public void setNotifier(INotify notifier) {
		this.notifier = notifier;
	}
}
