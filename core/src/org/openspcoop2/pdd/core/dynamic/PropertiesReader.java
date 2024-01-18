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

package org.openspcoop2.pdd.core.dynamic;

import org.slf4j.Logger;

/**
 * PropertiesReader
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class PropertiesReader {
	
	protected Logger log;
	
	public PropertiesReader(Logger log) {
		this.log = log;
	}
	
	public boolean isExists(String nome) throws DynamicException {
		return exists(nome);
	}
	public boolean exists(String nome) throws DynamicException {
		String v = read(nome);
		return v!=null;
	}
	
	public boolean isEmpty(String nome) throws DynamicException {
		String v = read(nome);
		return v!=null && "".equals(v);
	}
	
	public boolean isNotEmpty(String nome) throws DynamicException {
		String v = read(nome);
		return v!=null && "".equals(v);
	}
	
	public abstract String read(String nome) throws DynamicException;
	
}
