/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.protocol.sdk;

import org.apache.log4j.Logger;
import org.openspcoop2.utils.resources.Loader;

/**
 * ConfigurazionePdD
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdD {

	private long attesaAttivaJDBC;
	private int checkIntervalJDBC;
	private Loader loader;
	private Logger log;
	private String configurationDir;
	
	public String getConfigurationDir() {
		return this.configurationDir;
	}
	public void setConfigurationDir(String configurationDir) {
		this.configurationDir = configurationDir;
	}
	public long getAttesaAttivaJDBC() {
		return this.attesaAttivaJDBC;
	}
	public void setAttesaAttivaJDBC(long attesaAttivaJDBC) {
		this.attesaAttivaJDBC = attesaAttivaJDBC;
	}
	public int getCheckIntervalJDBC() {
		return this.checkIntervalJDBC;
	}
	public void setCheckIntervalJDBC(int checkIntervalJDBC) {
		this.checkIntervalJDBC = checkIntervalJDBC;
	}
	public Loader getLoader() {
		return this.loader;
	}
	public void setLoader(Loader loader) {
		this.loader = loader;
	}
	public Logger getLog() {
		return this.log;
	}
	public void setLog(Logger log) {
		this.log = log;
	}
	
}
