/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.message.saml;

import java.util.ArrayList;
import java.util.List;

/**
 * SAMLBuilderConfigAttribute
 * 	
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SAMLBuilderConfigAttribute {

	private String configName;
	
	private String qualifiedName;
	private String simpleName;
	private String formatName = SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_ATTRIBUTE_SUFFIX_FORMAT_NAME_VALUE_UNSPECIFIED;
	
	private List<Object> values = new ArrayList<>();
	
	public SAMLBuilderConfigAttribute(String configName){
		this.configName = configName;
	}
	
	public String getConfigName() {
		return this.configName;
	}
	
	public String getQualifiedName() {
		return this.qualifiedName;
	}
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	public String getSimpleName() {
		return this.simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	public String getFormatName() {
		return this.formatName;
	}
	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}
	
	public void addValue(String value){
		this.values.add(value);
	}
	public List<Object> getValues() {
		return this.values;
	}
}
