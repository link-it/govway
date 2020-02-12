/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
package org.openspcoop2.pdd.core.behaviour.conditional;

import java.util.List;

import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;

/**
 * ConditionalFilterResult
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConditionalFilterResult  {
	
	private boolean byFilter = true;
	private String regola = null;
	private String condition;
	private List<PortaApplicativaServizioApplicativo> listServiziApplicativi;
	
	public boolean isByFilter() {
		return this.byFilter;
	}
	public void setByFilter(boolean byFilter) {
		this.byFilter = byFilter;
	}
	public String getCondition() {
		return this.condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public List<PortaApplicativaServizioApplicativo> getListServiziApplicativi() {
		return this.listServiziApplicativi;
	}
	public void setListServiziApplicativi(List<PortaApplicativaServizioApplicativo> listServiziApplicativi) {
		this.listServiziApplicativi = listServiziApplicativi;
	}
	public String getRegola() {
		return this.regola;
	}
	public void setRegola(String regola) {
		this.regola = regola;
	}
}
