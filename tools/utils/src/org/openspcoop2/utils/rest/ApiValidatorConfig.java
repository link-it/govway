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

package org.openspcoop2.utils.rest;

import org.openspcoop2.utils.beans.BaseBean;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig.ADDITIONAL;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;

/**
 * ApiValidatorConfig
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiValidatorConfig extends BaseBean {

	private boolean verbose = false;
	private AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
	private ADDITIONAL policyAdditionalProperties = ADDITIONAL.DEFAULT;
	
	public AbstractXMLUtils getXmlUtils() {
		return this.xmlUtils;
	}
	public void setXmlUtils(AbstractXMLUtils xmlUtils) {
		this.xmlUtils = xmlUtils;
	}
	public boolean isVerbose() {
		return this.verbose;
	}
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public ADDITIONAL getPolicyAdditionalProperties() {
		return this.policyAdditionalProperties;
	}
	public void setPolicyAdditionalProperties(ADDITIONAL policyAdditionalProperties) {
		this.policyAdditionalProperties = policyAdditionalProperties;
	}

}
