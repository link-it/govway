/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk;

import java.io.Serializable;

import org.slf4j.Logger;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.resources.Loader;

/**
 * ConfigurazionePdD
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long attesaAttivaJDBC;
	private int checkIntervalJDBC;
	private transient Loader loader;
	private transient Logger log;
	private String configurationDir;
	private TipiDatabase tipoDatabase;
	
	public TipiDatabase getTipoDatabase() {
		return this.tipoDatabase;
	}
	public void setTipoDatabase(TipiDatabase tipoDatabase) {
		this.tipoDatabase = tipoDatabase;
	}
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
