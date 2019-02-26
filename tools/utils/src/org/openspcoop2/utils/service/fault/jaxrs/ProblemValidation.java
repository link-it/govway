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

package org.openspcoop2.utils.service.fault.jaxrs;

import java.util.ArrayList;
import java.util.List;
/**	
 * ProblemValidation
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProblemValidation extends Problem {
	
	public ProblemValidation() {}
	public ProblemValidation(Problem problem) {
		super();
		this.setDetail(problem.getDetail());
		this.setInstance(problem.getInstance());
		this.setStatus(problem.getStatus());
		this.setType(problem.getType());
		this.setTitle(problem.getTitle());
		this.invalidParams = new ArrayList<ProblemValidation.InvalidParam>();
	}
	
	public class InvalidParam {
		private String name;
		private String reason;
		private String value;

		public String getName() {
			return this.name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getReason() {
			return this.reason;
		}
		public void setReason(String reason) {
			this.reason = reason;
		}
		public String getValue() {
			return this.value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	private List<InvalidParam> invalidParams;

	public List<InvalidParam> getInvalidParams() {
		return this.invalidParams;
	}

	public void setInvalidParams(List<InvalidParam> invalidParams) {
		this.invalidParams = invalidParams;
	}
	
	public void addInvalidParam(String name, String reason, Object value) {
		InvalidParam ip = new InvalidParam();
		ip.setName(name);
		ip.setReason(reason);
		ip.setValue(value != null ? value.toString() : null);
		this.invalidParams.add(ip);
	}
}
