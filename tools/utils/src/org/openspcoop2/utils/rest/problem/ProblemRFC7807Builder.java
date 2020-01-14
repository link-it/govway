/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.rest.problem;

import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * ProblemRFC7808Utilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProblemRFC7807Builder {

	private String typeFormat = null;
	
	public ProblemRFC7807Builder(String typeFormat) {
		this.typeFormat = typeFormat;
	}
	public ProblemRFC7807Builder(boolean setType) {
		if(setType) {
			this.typeFormat = "https://httpstatuses.com/%d";
		}
	}
	
	public ProblemRFC7807 buildProblem(int status) {
		ProblemRFC7807 base = new ProblemRFC7807();
		if(this.typeFormat!=null) {
			base.setType(String.format(this.typeFormat, status));
		}
		base.setTitle(HttpUtilities.getHttpReason(status));
		base.setStatus(status);
		return base;
	}
	
}
