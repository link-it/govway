/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.service.fault.jaxrs;

/**	
 * ITypeProducer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TypeStatusGenerator implements ITypeGenerator {

	private String typeStatusTemplate = "@CODE@";
	private String typeTemplate = null;
	
	@Override
	public String getType(int status, Exception e) {
		if(this.typeStatusTemplate==null || this.typeTemplate==null) {
			return null;
		}
		String type = this.typeTemplate;
		while(type.contains(this.typeStatusTemplate)) {
			type = type.replace(this.typeStatusTemplate, status+"");
		}
		return type;
	}
	
	public String getTypeStatusTemplate() {
		return this.typeStatusTemplate;
	}

	public void setTypeStatusTemplate(String typeStatusTemplate) {
		this.typeStatusTemplate = typeStatusTemplate;
	}

	public String getTypeTemplate() {
		return this.typeTemplate;
	}

	public void setTypeTemplate(String typeTemplate) {
		this.typeTemplate = typeTemplate;
	}
}
