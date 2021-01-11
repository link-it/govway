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



package org.openspcoop2.protocol.basic.config;

import java.util.List;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.manifest.IntegrationConfiguration;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * AbstractIntegrationConfiguration
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractIntegrationConfiguration implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected IntegrationConfiguration integrationConfiguration;
	
	protected AbstractIntegrationConfiguration(IntegrationConfiguration integrationConfiguration) {
		this.integrationConfiguration = integrationConfiguration;
	}
	
	protected boolean useInterfaceNameInInvocationURL() {
		return this.integrationConfiguration.getName().isUseInUrl();
	}
	
	protected String getNome(IDServizio idServizio, IDSoggetto idSoggetto, String name, String ruleName,
			List<IntegrationConfigurationElementName> list) throws ProtocolException {
		StringBuilder bf = new StringBuilder();
		for (IntegrationConfigurationElementName integrationConfigurationElementName : list) {
			if(integrationConfigurationElementName.getPrefix()!=null && !"".equals(integrationConfigurationElementName.getPrefix())) {
				bf.append(integrationConfigurationElementName.getPrefix());
			}
			if(integrationConfigurationElementName.getActor()!=null) {
				switch (integrationConfigurationElementName.getActor()) {
				case SUBSCRIBER_TYPE:
					if(idSoggetto==null || idSoggetto.getTipo()==null) {
						throw new ProtocolException("Subscriber type undefined");
					}
					bf.append(idSoggetto.getTipo());
					break;
				case SUBSCRIBER_NAME:
					if(idSoggetto==null || idSoggetto.getNome()==null) {
						throw new ProtocolException("Subscriber name undefined");
					}
					bf.append(idSoggetto.getNome());
					break;
				case PROVIDER_TYPE:
					if(idServizio==null || idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getTipo()==null) {
						throw new ProtocolException("Provider type undefined");
					}
					bf.append(idServizio.getSoggettoErogatore().getTipo());
					break;
				case PROVIDER_NAME:
					if(idServizio==null || idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getNome()==null) {
						throw new ProtocolException("Provider name undefined");
					}
					bf.append(idServizio.getSoggettoErogatore().getNome());
					break;
				case SERVICE_TYPE:
					if(idServizio==null || idServizio.getTipo()==null) {
						throw new ProtocolException("Service type undefined");
					}
					bf.append(idServizio.getTipo());
					break;
				case SERVICE_NAME:
					if(idServizio==null || idServizio.getNome()==null) {
						throw new ProtocolException("Service name undefined");
					}
					bf.append(idServizio.getNome());
					break;
				case SERVICE_VERSION:
					if(idServizio==null || idServizio.getVersione()==null) {
						throw new ProtocolException("Service version undefined");
					}
					bf.append(idServizio.getVersione().intValue()+"");
					break;
				case NAME:
					if(name==null) {
						throw new ProtocolException("Name undefined");
					}
					bf.append(name);
					break;
				case RULE_NAME:
					if(ruleName==null) {
						throw new ProtocolException("Rule name undefined");
					}
					bf.append(ruleName);
					break;
				}
			}
			if(integrationConfigurationElementName.getSuffix()!=null && !"".equals(integrationConfigurationElementName.getSuffix())) {
				bf.append(integrationConfigurationElementName.getSuffix());
			}
		}
		return bf.toString();
	}

}





