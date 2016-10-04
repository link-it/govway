/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.datasource;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.resources.CostantiJMX;

/**
 * DatasourceParams
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataSourceParams {

	private TipiDatabase databaseType;
	private boolean wrapOriginalMethods = true;
	private String applicativeId;
	
	private boolean bindJmx = true;
	private String jmxDomain = "org.openspcoop2.utils";
	private String jmxType = CostantiJMX.JMX_TYPE;
	private String jmxName = "Datasources";
	
	public TipiDatabase getDatabaseType() {
		return this.databaseType;
	}
	public void setDatabaseType(TipiDatabase tipoDatabase) {
		this.databaseType = tipoDatabase;
	}
	public boolean isWrapOriginalMethods() {
		return this.wrapOriginalMethods;
	}
	public void setWrapOriginalMethods(boolean wrapOriginalMethods) {
		this.wrapOriginalMethods = wrapOriginalMethods;
	}
	public String getApplicativeId() {
		return this.applicativeId;
	}
	public void setApplicativeId(String applicativeId) {
		this.applicativeId = applicativeId;
	}
	public boolean isBindJmx() {
		return this.bindJmx;
	}
	public void setBindJmx(boolean bindJmx) {
		this.bindJmx = bindJmx;
	}
	public String getJmxDomain() {
		return this.jmxDomain;
	}
	public void setJmxDomain(String jmxDomain) {
		this.jmxDomain = jmxDomain;
	}
	public String getJmxType() {
		return this.jmxType;
	}
	public void setJmxType(String jmxType) {
		this.jmxType = jmxType;
	}
	public String getJmxName() {
		return this.jmxName;
	}
	public void setJmxName(String jmxName) {
		this.jmxName = jmxName;
	}
}
