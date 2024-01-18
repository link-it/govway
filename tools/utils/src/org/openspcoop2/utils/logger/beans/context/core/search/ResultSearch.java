/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.logger.beans.context.core.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.logger.constants.context.Result;

/**
 * ResultSearch
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResultSearch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Result> results = new ArrayList<Result>();
	private boolean and = true;
	
	public List<Result> getResults() {
		return this.results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	public void addResult(Result result){
		this.results.add(result);
	}
	public boolean isAnd() {
		return this.and;
	}
	public void setAnd(boolean and) {
		this.and = and;
	}
	
}
