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
package org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check;

import java.io.Serializable;

/**
 * HealthCheckConfigurazione
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HealthCheckConfigurazione implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private boolean passiveCheckEnabled = false;
	private Integer passiveHealthCheck_excludeForSeconds = null; 

	public boolean isPassiveCheckEnabled() {
		return this.passiveCheckEnabled;
	}

	public void setPassiveCheckEnabled(boolean passiveCheckEnabled) {
		this.passiveCheckEnabled = passiveCheckEnabled;
	}

	public Integer getPassiveHealthCheck_excludeForSeconds() {
		return this.passiveHealthCheck_excludeForSeconds;
	}

	public void setPassiveHealthCheck_excludeForSeconds(Integer passiveHealthCheck_excludeForSeconds) {
		this.passiveHealthCheck_excludeForSeconds = passiveHealthCheck_excludeForSeconds;
	}
}
