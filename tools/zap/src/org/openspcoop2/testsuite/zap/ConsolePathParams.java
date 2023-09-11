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
package org.openspcoop2.testsuite.zap;

/**
 * ConsoleReportPathParams
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConsolePathParams {

	public ConsolePathParams(String baseConfigDirName) {
		this.baseConfigDirName = baseConfigDirName;
	}
	public ConsolePathParams(String baseConfigDirName, String tipoTestUrl, String url) {
		this.baseConfigDirName = baseConfigDirName;
		this.tipoTestUrl = tipoTestUrl;
		this.url = url;
	}
	
	private String baseConfigDirName;
	private String tipoTestUrl;
	private String url;
	
	public String getBaseConfigDirName() {
		return this.baseConfigDirName;
	}
	public void setBaseConfigDirName(String baseConfigDirName) {
		this.baseConfigDirName = baseConfigDirName;
	}
	public String getTipoTestUrl() {
		return this.tipoTestUrl;
	}
	public void setTipoTestUrl(String tipoTestUrl) {
		this.tipoTestUrl = tipoTestUrl;
	}
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
