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

package org.openspcoop2.protocol.as4.pmode.beans;

import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Fruitore {

	private org.openspcoop2.core.registry.Fruitore base;
	private String cn;
	
	private String ebmsSecurityProfile;
	private Boolean ebmsReliabilityNonRepudiation;
	private String ebmsReliabilityReplyPattern;
	
	public Fruitore(org.openspcoop2.core.registry.Fruitore base, AccordoServizioParteSpecifica asps, String cn) throws Exception {
		
		this.base = base;
		this.cn = cn;
		
		for(ProtocolProperty prop: base.getProtocolPropertyList()) {
			if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE)) {
				this.ebmsSecurityProfile = prop.getValue();
			}
			else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_NON_REPUDIATION)) {
				if(prop.getBooleanValue()!=null) {
					this.ebmsReliabilityNonRepudiation = prop.getBooleanValue();
				}
				else if(prop.getValue()!=null) {
					this.ebmsReliabilityNonRepudiation = Boolean.parseBoolean(prop.getValue());
				}
			}
			else if(prop.getName().equals(AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_REPLY_PATTERN)) {
				this.ebmsReliabilityReplyPattern = prop.getValue();
			}
		}
		
		String oggetto = "fruizione da parte del soggetto '"+base.getNome()+"' dell'api '"+asps.getNome()+"' erogata dal soggetto '"+asps.getNomeSoggettoErogatore()+"'";
	      		
		if(this.ebmsSecurityProfile == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_SECURITY_PROFILE+" non definita per la "+oggetto);
		if(this.ebmsReliabilityNonRepudiation == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_NON_REPUDIATION+" non definita la "+oggetto);
		if(this.ebmsReliabilityReplyPattern == null)
			throw new Exception("Property "+AS4Costanti.AS4_PROTOCOL_PROPERTIES_RELIABILITY_REPLY_PATTERN+" non definita per la "+oggetto);
				
	}
	
	public org.openspcoop2.core.registry.Fruitore getBase() {
		return this.base;
	}
	public void setBase(org.openspcoop2.core.registry.Fruitore base) {
		this.base = base;
	}
	public String getCn() {
		return this.cn;
	}
	public void setCn(String cn) {
		this.cn = cn;
	}
	public String getEbmsSecurityProfile() {
		return this.ebmsSecurityProfile;
	}
	public void setEbmsSecurityProfile(String ebmsSecurityProfile) {
		this.ebmsSecurityProfile = ebmsSecurityProfile;
	}
	public Boolean getEbmsReliabilityNonRepudiation() {
		return this.ebmsReliabilityNonRepudiation;
	}
	public void setEbmsReliabilityNonRepudiation(Boolean ebmsReliabilityNonRepudiation) {
		this.ebmsReliabilityNonRepudiation = ebmsReliabilityNonRepudiation;
	}
	public String getEbmsReliabilityReplyPattern() {
		return this.ebmsReliabilityReplyPattern;
	}
	public void setEbmsReliabilityReplyPattern(String ebmsReliabilityReplyPattern) {
		this.ebmsReliabilityReplyPattern = ebmsReliabilityReplyPattern;
	}
}
