/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

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

	private static final long serialVersionUID = 1L;
	
	private boolean verbose = false;
	private transient AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
	private ADDITIONAL policyAdditionalProperties = ADDITIONAL.DEFAULT;
	private boolean emitLogError = true;

	/**
	 * Mappa interna popolata dalle sottoclassi tramite {@link #setProperty(String, String)}.
	 * Permette di esportare lo stato del config come coppie chiave/valore e di ricostruire
	 * un config equivalente passando la mappa come provider a {@link #readProperties(UnaryOperator)}.
	 */
	private final Map<String, String> properties = new HashMap<>();

	protected void setProperty(String name, String value) {
		this.properties.put(name, value);
	}

	/**
	 * @return la mappa delle proprietà raccolte (read-only).
	 *         La mappa può essere passata come provider a {@link #readProperties(UnaryOperator)}
	 *         su una nuova istanza per ottenere un config equivalente.
	 */
	public Map<String, String> mapProperties() {
		return Collections.unmodifiableMap(this.properties);
	}
	
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

	public boolean isEmitLogError() {
		return this.emitLogError;
	}
	public void setEmitLogError(boolean emitLogError) {
		this.emitLogError = emitLogError;
	}
	
	public void readProperties(UnaryOperator<String> propertyProvider) {
		
	}
}
