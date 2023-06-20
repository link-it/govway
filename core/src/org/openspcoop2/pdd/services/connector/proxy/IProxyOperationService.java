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

package org.openspcoop2.pdd.services.connector.proxy;

import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.slf4j.Logger;

/**
 * IProxyOperationService
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IProxyOperationService {
	
	public void init(Logger log) throws CoreException;
	
	public void save(ProxyOperation operation) throws CoreException;
	
	public List<ProxyOperation> next(Date recTimeGreaterThan, Date now, int offset, int limit) throws CoreException;

	public int clear(Date olderThan) throws CoreException;
}
