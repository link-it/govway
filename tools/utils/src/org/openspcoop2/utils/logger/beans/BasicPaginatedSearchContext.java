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

package org.openspcoop2.utils.logger.beans;

import java.io.Serializable;

import org.openspcoop2.utils.logger.IPaginatedSearchContext;

/**
 * BasicPaginatedSearchContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicPaginatedSearchContext extends BasicSearchContext implements IPaginatedSearchContext,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer limit;
	private Integer offset;
	
	@Override
	public Integer getLimit() {
		return this.limit;
	}

	@Override
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	@Override
	public Integer getOffset() {
		return this.offset;
	}

	@Override
	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	
}
