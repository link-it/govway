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

package org.openspcoop2.utils.openapi;

import java.util.Map;

import org.openspcoop2.utils.rest.ApiFormats;

/**
 * UniqueInterfaceGeneratorConfig
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UniqueInterfaceGeneratorConfig {

	protected ApiFormats format;
	protected boolean yaml;
	protected String master;
	protected Map<String, String> attachments;
		
	
	public ApiFormats getFormat() {
		return this.format;
	}
	public void setFormat(ApiFormats format) {
		this.format = format;
	}
	public boolean isYaml() {
		return this.yaml;
	}
	public void setYaml(boolean yaml) {
		this.yaml = yaml;
	}
	public String getMaster() {
		return this.master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public Map<String, String> getAttachments() {
		return this.attachments;
	}
	public void setAttachments(Map<String, String> attachments) {
		this.attachments = attachments;
	}
}