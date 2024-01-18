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

package org.openspcoop2.utils.service.beans.utils;

import java.util.Iterator;
import java.util.List;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import org.openspcoop2.utils.Utilities;
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

	
	
	public static final Lista costruisciListaPaginata(UriInfo uriInfo, Integer offset, Integer limit, long total) throws InstantiationException, IllegalAccessException {
		return costruisciListaPaginata(uriInfo, offset, limit, total, Lista.class);
	}
	public static final Lista costruisciListaPaginata(String requestURI, Integer offset, Integer limit, long total) throws InstantiationException, IllegalAccessException {
		return costruisciListaPaginata(requestURI, offset, limit, total, Lista.class);
	}
	
	public static final <T extends Lista> T costruisciListaPaginata(UriInfo uriInfo, Integer offset, Integer limit, long total, Class<T> lclass) throws InstantiationException, IllegalAccessException {
		return costruisciListaPaginata(getUrl(uriInfo), offset, limit, total, lclass);
	}
	public static final <T extends Lista> T costruisciListaPaginata(String requestURI, Integer offset, Integer limit, long total, Class<T> lclass) throws InstantiationException, IllegalAccessException {
		T l = Utilities.newInstanceThrowInstantiationException(lclass);
		
		if (total < 0)
			throw new IllegalArgumentException("Il numero totale di elementi deve essere positivo");
		
		l = _costruisciLista(requestURI, offset, limit, total, -1, lclass);
		
		int realOffset = (offset == null || offset < 0) ? 0 : offset;
		int realLimit  = (limit == null || limit <= 0 ) ? -1 : limit;
		
		if(realLimit>0) {
		
			long numeroPagineTotali = (total / realLimit);
			long resto = (total % realLimit);
			if(resto>0) {
				numeroPagineTotali++;
			}
			int paginaCorrente = (realOffset / realLimit) +1;
			
			if (paginaCorrente<numeroPagineTotali)
			{
				long lastOffset = (numeroPagineTotali-1)*realLimit;
				if (limit == null)
					l.setLast(UriBuilder.fromUri(requestURI).queryParam("offset", lastOffset).build().toString());
				else
					l.setLast(UriBuilder.fromUri(requestURI).queryParam("offset", lastOffset).queryParam("limit", limit).build().toString());
			}		
			
		}
		        
		l.setTotal(total);
		
		return l;
	}
	
	
	public static final ListaSenzaTotale costruisciLista(UriInfo uriInfo, Integer offset, Integer limit, long pageCurrentSize) throws InstantiationException, IllegalAccessException {
		return costruisciLista(getUrl(uriInfo), offset, limit, pageCurrentSize, ListaSenzaTotale.class);
	}
	public static final ListaSenzaTotale costruisciLista(String requestURI, Integer offset, Integer limit, long pageCurrentSize) throws InstantiationException, IllegalAccessException {
		return costruisciLista(requestURI, offset, limit, pageCurrentSize, ListaSenzaTotale.class);
	}
	
	public static final <T extends ListaSenzaTotale> T costruisciLista(UriInfo uriInfo, Integer offset, Integer limit, long pageCurrentSize, Class<T> lclass) throws InstantiationException, IllegalAccessException {
		return costruisciLista(getUrl(uriInfo), offset, limit, pageCurrentSize, lclass);
	}
	public static final <T extends ListaSenzaTotale> T costruisciLista(String requestURI, Integer offset, Integer limit, long pageCurrentSize, Class<T> lclass) throws InstantiationException, IllegalAccessException {
		return _costruisciLista(requestURI, offset, limit, null, pageCurrentSize, lclass);
	}
	
	

	
	private static final <T extends ListaSenzaTotale> T _costruisciLista(String requestURI, Integer offset, Integer limit, Long total, long pageCurrentSize, Class<T> lclass) throws InstantiationException, IllegalAccessException {
		T l = Utilities.newInstanceThrowInstantiationException(lclass);
	
		int realOffset = (offset == null || offset < 0) ? 0 : offset;
		int realLimit  = (limit == null || limit <= 0 ) ? -1 : limit;
		
		if(realLimit>0) {
			int paginaCorrente = (realOffset / realLimit) +1;
					
			if(paginaCorrente>1)
			{
				if (limit == null || limit<=0)
					l.setFirst(UriBuilder.fromUri(requestURI).queryParam("offset", 0).build().toString());
				else
					l.setFirst(UriBuilder.fromUri(requestURI).queryParam("offset", 0).queryParam("limit", limit).build().toString());
			}
			
			
			if(paginaCorrente>1)
			{
				if (limit == null || limit<=0)
					l.setPrev(UriBuilder.fromUri(requestURI).queryParam("offset", realOffset - realLimit).build().toString());
				else
					l.setPrev(UriBuilder.fromUri(requestURI).queryParam("offset", realOffset - realLimit).queryParam("limit", limit).build().toString());
			}
			
			if(total!=null && total>0) {
			
				long numeroPagineTotali = (total.longValue() / realLimit);
				long resto = (total.longValue() % realLimit);
				if(resto>0) {
					numeroPagineTotali++;
				}
				
				if (paginaCorrente<numeroPagineTotali)
				{
					if (limit == null || limit<=0)
						l.setNext(UriBuilder.fromUri(requestURI).queryParam("offset", realOffset + realLimit).build().toString());
					else
						l.setNext(UriBuilder.fromUri(requestURI).queryParam("offset", realOffset + realLimit).queryParam("limit", limit).build().toString());
				}		
			}
			else {
				
				if(pageCurrentSize>=realLimit) {
					if (limit == null || limit<=0)
						l.setNext(UriBuilder.fromUri(requestURI).queryParam("offset", realOffset + realLimit).build().toString());
					else
						l.setNext(UriBuilder.fromUri(requestURI).queryParam("offset", realOffset + realLimit).queryParam("limit", limit).build().toString());
				}
				
			}
			
			l.setLimit(realLimit);
	        l.setOffset(Long.valueOf(realOffset));
		}	        
		
		return l;
	}
	
	private static String getUrl(UriInfo uriInfo) {
		StringBuilder sb = new StringBuilder();
		String path = uriInfo.getPath();
		if(path.startsWith("/")==false) {
			sb.append("/");
		}
		sb.append(path);
		UriBuilder urlBuilder = UriBuilder.fromUri(sb.toString());
		if(uriInfo.getQueryParameters()!=null && !uriInfo.getQueryParameters().isEmpty()) {
			Iterator<String> it = uriInfo.getQueryParameters().keySet().iterator();
			while (it.hasNext()) {
				String nome = (String) it.next();
				if("offset".equalsIgnoreCase(nome) || "limit".equalsIgnoreCase(nome)) {
					continue;
				}
				List<String> values = uriInfo.getQueryParameters().get(nome);
				if(values!=null && !values.isEmpty()) {
					for (String value : values) {
						urlBuilder.queryParam(nome, value);						
					}
				}
			}
		}
		return urlBuilder.build().toString();
	}

}
