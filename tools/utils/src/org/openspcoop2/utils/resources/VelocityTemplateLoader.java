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

package org.openspcoop2.utils.resources;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ExtProperties;

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
	public boolean isSourceModified(Resource arg0) {
		return false;
	}

	@Override
	public boolean resourceExists(String name) {
		return this.templateIncludes.containsKey(name);
	}

	@Override
	public Reader getResourceReader(String name, String encoding) throws ResourceNotFoundException {
		if(this.templateIncludes.containsKey(name)) {
			try {
				return new StringReader(new String(this.templateIncludes.get(name),encoding));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		return null;
	}

	@Override
	public void init(ExtProperties arg0) {
		
	}
}