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

package org.openspcoop2.pdd.config;

import java.util.Properties;

import org.openspcoop2.utils.UtilsException;

/**
 * ConfigurazioneCoda
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePriorita {

	private String name;
	private int percentuale;
	private boolean nessunaPriorita;

	private String label;
	
	public ConfigurazionePriorita(String name, Properties p) throws UtilsException {
		this.name = name;
		this.percentuale = getIntProperty(this.name, p, "percentuale");
		this.label = getProperty(this.name, p, "label");
		this.nessunaPriorita = this.percentuale<=0;
	}
	
	private static String getProperty(String coda, Properties p, String name) throws UtilsException {
		String tmp = p.getProperty(name);
		if(tmp==null) {
			throw new UtilsException("[Queue:"+coda+"] Property '"+name+"' not found");
		}
		return tmp.trim();
	}
	private static int getIntProperty(String coda, Properties p, String name) throws UtilsException {
		String tmp = getProperty(coda, p, name);
		try {
			return Integer.valueOf(tmp);
		}catch(Exception e) {
			throw new UtilsException("[Queue:"+coda+"] Property '"+name+"' uncorrect (value:"+tmp+"): "+e.getMessage(),e);
		}
	}
	
	public String getName() {
		return this.name;
	}

	public int getPercentuale() {
		return this.percentuale;
	}

	public String getLabel() {
		return this.label;
	}

	public boolean isNessunaPriorita() {
		return this.nessunaPriorita;
	}

}
