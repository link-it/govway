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

package org.openspcoop2.utils.resources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 *  VelocityTemplateLoader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VelocityTemplateLoader extends ResourceLoader {
	
	private Map<String, byte[]> templateIncludes;
	
	public VelocityTemplateLoader(Map<String, byte[]> templateIncludes) {
		this.templateIncludes = templateIncludes;
	}

	@Override
	public long getLastModified(Resource arg0) {
		return 0;
	}

	@Override
	public InputStream getResourceStream(String name) throws ResourceNotFoundException {
		if(this.templateIncludes.containsKey(name)) {
			return new ByteArrayInputStream(this.templateIncludes.get(name));
		}
		return null;
	}

	@Override
	public void init(ExtendedProperties arg0) {
	}

	@Override
	public boolean isSourceModified(Resource arg0) {
		return false;
	}

	@Override
	public boolean resourceExists(String name) {
		return this.templateIncludes.containsKey(name);
	}
}