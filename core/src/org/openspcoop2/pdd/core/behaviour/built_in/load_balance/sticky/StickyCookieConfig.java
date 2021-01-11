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
package org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky;

import org.apache.cxf.common.util.StringUtils;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;

/**
 * Costanti
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StickyCookieConfig  {

	private String name;
	private String domain;
	private String path;

	public StickyCookieConfig(String cookie) throws BehaviourException {
		
		String formato = "name[[;domain=<domainValue>][;path=<pathValue>]]";
		
		if(cookie.contains(";")) {
			String [] split = cookie.split(";");
			if(split==null || split.length<=0) {
				throw new BehaviourException("Formato cookie fornito non valido (cookie:'"+cookie+"' errCode:1); il formato atteso è '"+formato+"'");
			}
			if(split.length>3) {
				throw new BehaviourException("Formato cookie fornito non valido (cookie:'"+cookie+"' error:'troppe parti ("+split.length+")'); il formato atteso è '"+formato+"'");
			}
			for (int i = 0; i < split.length; i++) {
				String v = split[i].trim();
				if(StringUtils.isEmpty(v)) {
					throw new BehaviourException("Formato cookie fornito non valido (cookie:'"+cookie+"' part:'"+v+"' error:'empty'); il formato atteso è '"+formato+"'");
				}
				if(i==0) {
					if(v.contains(";")) {
						throw new BehaviourException("Formato cookie fornito non valido (cookie:'"+cookie+"' part:'"+v+"' error:'; non atteso'); il formato atteso è '"+formato+"'");
					}
					this.name = v;
				}
				else {
					if(v.contains("=") == false) {
						throw new BehaviourException("Formato cookie fornito non valido (cookie:'"+cookie+"' part:'"+v+"' error:'= non trovato'); il formato atteso è '"+formato+"'");
					}
					String [] internalSplit = v.split("=");
					if(internalSplit==null || internalSplit.length!=2) {
						throw new BehaviourException("Formato cookie fornito non valido (cookie:'"+cookie+"' part:'"+v+"' error:'struttura non valida (split:"+internalSplit.length+")'); il formato atteso è '"+formato+"'");
					}
					if("domain".equals(internalSplit[0].trim())) {
						this.domain = internalSplit[1];
						if(this.domain!=null) {
							this.domain = this.domain.trim();
						}
						if(StringUtils.isEmpty(this.domain)) {
							this.domain = null;
						}
					}
					else if("path".equals(internalSplit[0].trim())) {
						this.path = internalSplit[1];
						if(this.path!=null) {
							this.path = this.path.trim();
						}
						if(StringUtils.isEmpty(this.path)) {
							this.path = null;
						}
					}
					else {
						throw new BehaviourException("Formato cookie fornito non valido (cookie:'"+cookie+"' part:'"+v+"' error:'elemento "+internalSplit[0]+" sconosciuto'); il formato atteso è '"+formato+"'");
					}
				}
			}
		}
		else {
			this.name = cookie;
		}
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDomain() {
		return this.domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getPath() {
		return this.path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
}
