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


package org.openspcoop2.web.ctrlstat.core;

import java.io.Serializable;

/**
 * Oggetto che si occupa di memorizzare i valori di ricerca per le liste
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Search extends org.openspcoop2.core.commons.Search implements Serializable {

	private static final long serialVersionUID = 1L;

	public Search() {
		super();
	}
	public Search(boolean showAllResult, int initialLimit) {
		super(showAllResult, initialLimit);
	}
	public Search(boolean showAllResult) {
		super(showAllResult);
	}
	
}
