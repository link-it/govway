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





package org.openspcoop2.core.commons;

/**
 * SearchUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SearchUtils
{
	public static void clearFilter(ISearch ricerca, int idLista, String filterName) {
		ricerca.clearFilter(idLista, filterName);
	}
	
	public static String getFilter(ISearch ricerca, int idLista, String filterName) {
		return SearchUtils.getFilter(ricerca, idLista, filterName, "");
	}
	public static String getFilter(ISearch ricerca, int idLista, String filterName, String defaultValue) {
		if(ricerca==null) {
			throw new RuntimeException("Param ricerca is null");
		}
		String filter = ricerca.getFilter(idLista, filterName);
		filter = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filter) || (filter==null)) ? defaultValue : filter;
		return filter;
	}
}
