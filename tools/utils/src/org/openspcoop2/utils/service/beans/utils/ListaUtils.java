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

package org.openspcoop2.utils.service.beans.utils;

import javax.ws.rs.core.UriBuilder;

import org.openspcoop2.utils.service.beans.Lista;
import org.openspcoop2.utils.service.beans.ListaSenzaTotale;


/**
 * ListaUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ListaUtils {

	public static final Lista costruisciListaPaginata(String requestURI, Integer offset, Integer limit, long total) throws InstantiationException, IllegalAccessException {
		return costruisciListaPaginata(requestURI, offset, limit, total, Lista.class);
	}
	public static final <T extends Lista> T costruisciListaPaginata(String requestURI, Integer offset, Integer limit, long total, Class<T> lclass) throws InstantiationException, IllegalAccessException {
		T l = lclass.newInstance();
		
		l = costruisciLista(requestURI, offset, limit, total, lclass);
		
		if (offset == null || offset < 0) offset = 0;
		if (limit == null || limit <= 0 ) limit = Integer.MAX_VALUE;
		
		if (limit < total - offset) {
			l.setLast(UriBuilder.fromUri(requestURI).queryParam("offset", (total / limit) * limit).build().toString());
		}		
		        
		l.setTotal(total);
		
		return l;
	}
	
	public static final ListaSenzaTotale costruisciLista(String requestURI, Integer offset, Integer limit, long total) throws InstantiationException, IllegalAccessException {
		return costruisciLista(requestURI, offset, limit, total, ListaSenzaTotale.class);
	}
	public static final <T extends ListaSenzaTotale> T costruisciLista(String requestURI, Integer offset, Integer limit, long total, Class<T> lclass) throws InstantiationException, IllegalAccessException {
		T l = lclass.newInstance();
		
		if (total < 0)
			throw new IllegalArgumentException("Il numero totale di elementi deve essere positivo");
		
		if (offset == null || offset < 0) offset = 0;
		if (limit == null || limit <= 0 ) limit = Integer.MAX_VALUE;
		
		if(offset > 0)
			l.setFirst(UriBuilder.fromUri(requestURI).queryParam("offset", 0).build().toString());
		
		if(offset > limit)
        	l.setPrev(UriBuilder.fromUri(requestURI).queryParam("offset", offset - limit).build().toString());
		
		if (limit < total - offset) {
			l.setNext(UriBuilder.fromUri(requestURI).queryParam("offset", offset + limit).build().toString());
		}		
		        
        l.setOffset(offset.longValue());
        l.setLimit(limit == Integer.MAX_VALUE ? 0 : limit);
		
		return l;
	}
	
}
