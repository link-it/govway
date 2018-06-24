/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

package org.openspcoop2.pdd.core.controllo_traffico;

/**     
 * DatiTempiRisposta
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiTempiRisposta {

	private Integer connectionTimeout;
	private Integer readConnectionTimeout;
	private Integer avgResponseTime;


	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append("connectionTimeout[").append(this.connectionTimeout).append("]");
		bf.append(" readConnectionTimeout[").append(this.readConnectionTimeout).append("]");
		bf.append(" avgResponseTime[").append(this.avgResponseTime).append("]");
		return bf.toString();
	}
	
	public Integer getConnectionTimeout() {
		return this.connectionTimeout;
	}
	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public Integer getReadConnectionTimeout() {
		return this.readConnectionTimeout;
	}
	public void setReadConnectionTimeout(Integer readConnectionTimeout) {
		this.readConnectionTimeout = readConnectionTimeout;
	}
	public Integer getAvgResponseTime() {
		return this.avgResponseTime;
	}
	public void setAvgResponseTime(Integer avgResponseTime) {
		this.avgResponseTime = avgResponseTime;
	}
}
