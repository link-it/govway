/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.dynamic;

import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.slf4j.Logger;

/**
 * SystemPropertiesReader
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SystemPropertiesReader extends PropertiesReader {
	
	private SystemProperties systemProperties;
	
	public SystemPropertiesReader(Logger log, RequestInfo requestInfo, boolean initFromJmx) throws DynamicException{
		super(log);
		try {
			if(ConfigurazionePdDManager.getInstance()!=null && ConfigurazionePdDManager.getInstance().isInitializedConfigurazionePdDReader()) {
				if(initFromJmx) {
					this.systemProperties = ConfigurazionePdDManager.getInstance().getSystemPropertiesPdDNoCached(true);
				}
				else {
					this.systemProperties = ConfigurazionePdDManager.getInstance().getSystemPropertiesPdDCached(requestInfo);
				}
			}
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}		
	}
		
	@Override
	public String read(String nome) throws DynamicException{
		if(this.systemProperties==null || this.systemProperties.sizeSystemPropertyList()<=0) {
			return null;
		}
		for (Property p : this.systemProperties.getSystemPropertyList()) {
			if(p!=null && p.getNome()!=null && p.getNome().equalsIgnoreCase(nome)) {
				return p.getValore();
			}
		}
		return null;
	}
	
}
