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

package org.openspcoop2.utils.resources;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import freemarker.cache.TemplateLoader;

/**
 *  FreemarkerTemplateLoader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FreemarkerTemplateLoader implements TemplateLoader {
	
	private Map<String, byte[]> templateIncludes;
	
	public FreemarkerTemplateLoader(Map<String, byte[]> templateIncludes) {
		this.templateIncludes = templateIncludes;
	}
	
	@Override
	public Reader getReader(Object content, String name) throws IOException {
		//System.out.println("READER ["+name+"]");
		return new StringReader(new String((byte[])content));
	}
	
	@Override
	public long getLastModified(Object arg0) {
		return 0;
	}
	
	@Override
	public Object findTemplateSource(String name) throws IOException {
		//System.out.println("FIND ["+name+"]");
		if(this.templateIncludes.containsKey(name)) {
			//System.out.println("FIND ["+name+"] RETURN");
			return this.templateIncludes.get(name);
		}
		return null;
	}
	
	@Override
	public void closeTemplateSource(Object name) throws IOException {
		
	}
	
}