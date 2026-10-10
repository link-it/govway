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

package org.openspcoop2.protocol.modipa.example.rest.pdnd_async;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Lettura dei file di proprietà degli esempi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExampleProperties {

	private final Properties properties = new Properties();
	
	public ExampleProperties(String file) throws IOException {
		try(FileInputStream fin = new FileInputStream(file)){
			this.properties.load(fin);
		}
	}
	
	public String getRequired(String name) {
		String v = getOptional(name, null);
		if(v==null) {
			throw new IllegalArgumentException("Property ["+name+"] not defined");
		}
		return v;
	}
	public String getOptional(String name, String defaultValue) {
		String v = this.properties.getProperty(name);
		if(v==null || "".equals(v.trim())) {
			return defaultValue;
		}
		return v.trim();
	}
	public int getInt(String name, int defaultValue) {
		String v = getOptional(name, null);
		return v!=null ? Integer.parseInt(v) : defaultValue;
	}
	public boolean getBoolean(String name, boolean defaultValue) {
		String v = getOptional(name, null);
		return v!=null ? Boolean.parseBoolean(v) : defaultValue;
	}
}
