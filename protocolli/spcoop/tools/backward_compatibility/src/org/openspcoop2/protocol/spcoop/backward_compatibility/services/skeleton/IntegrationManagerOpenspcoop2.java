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
package org.openspcoop2.protocol.spcoop.backward_compatibility.services.skeleton;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.pdd.services.skeleton.IntegrationManagerException;

/**
 * IntegrationManagerOpenspcoop2
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationManagerOpenspcoop2 extends org.openspcoop2.pdd.services.skeleton.IntegrationManager {

	private HttpServletRequest httpReq;
	private HttpServletResponse httpRes;
	
	public IntegrationManagerOpenspcoop2(HttpServletRequest httpReq,HttpServletResponse httpRes){
		this.httpReq = httpReq;
		this.httpRes = httpRes;
	}
	
	@Override
	protected HttpServletRequest getHttpServletRequest()
			throws IntegrationManagerException {
		return this.httpReq;
	}

	@Override
	protected HttpServletResponse getHttpServletResponse()
			throws IntegrationManagerException {
		return this.httpRes;
	}

	

}
