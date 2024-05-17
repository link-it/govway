/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.config;

import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.ConfigurazioneSistema;
import org.openspcoop2.pdd.core.jmx.StatoServiziJMXResource;
import org.openspcoop2.utils.jmx.CostantiJMX;

/**
 * ConfigurazioneNodiRuntimeProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneNodiRuntimeBYOKRemoteConfig {

	private String type = CostantiJMX.JMX_TYPE;
	
	private String resourceStatoServiziPdd = CostantiPdD.JMX_STATO_SERVIZI_PDD;
	private String attributeComponentePD = StatoServiziJMXResource.COMPONENTE_PD;
	
	private String resourceConfigurazioneSistema = CostantiPdD.JMX_CONFIGURAZIONE_SISTEMA;
	private String methodWrap = ConfigurazioneSistema.BYOK_WRAP_BASE64;
	private String methodUnwrap = ConfigurazioneSistema.BYOK_UNWRAP_BASE64;

	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getResourceStatoServiziPdd() {
		return this.resourceStatoServiziPdd;
	}
	public void setResourceStatoServiziPdd(String resourceStatoServiziPdd) {
		this.resourceStatoServiziPdd = resourceStatoServiziPdd;
	}
	public String getAttributeComponentePD() {
		return this.attributeComponentePD;
	}
	public void setAttributeComponentePD(String attributeComponentePD) {
		this.attributeComponentePD = attributeComponentePD;
	}

	public String getResourceConfigurazioneSistema() {
		return this.resourceConfigurazioneSistema;
	}
	public void setResourceConfigurazioneSistema(String resourceConfigurazioneSistema) {
		this.resourceConfigurazioneSistema = resourceConfigurazioneSistema;
	}
	public String getMethodWrap() {
		return this.methodWrap;
	}
	public void setMethodWrap(String methodWrap) {
		this.methodWrap = methodWrap;
	}
	public String getMethodUnwrap() {
		return this.methodUnwrap;
	}
	public void setMethodUnwrap(String methodUnwrap) {
		this.methodUnwrap = methodUnwrap;
	}
}
