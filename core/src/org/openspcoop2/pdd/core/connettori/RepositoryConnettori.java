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


package org.openspcoop2.pdd.core.connettori;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * RepositoryConnettori
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RepositoryConnettori {

	private static Map<String, IConnettore> connettori_pd = new ConcurrentHashMap<String, IConnettore>();
	private static Map<String, IConnettore> connettori_pa = new ConcurrentHashMap<String, IConnettore>();
	
	
	public static void salvaConnettorePD(String id,IConnettore connettore){
		RepositoryConnettori.connettori_pd.put(id, connettore);
	}
	public static void salvaConnettorePA(String id,IConnettore connettore){
		RepositoryConnettori.connettori_pa.put(id, connettore);
	}
	
	public static IConnettore getConnettorePD(String id){
		return RepositoryConnettori.connettori_pd.get(id);
	}
	public static IConnettore getConnettorePA(String id){
		return RepositoryConnettori.connettori_pa.get(id);
	}
	
	public static IConnettore removeConnettorePD(String id){
		return RepositoryConnettori.connettori_pd.remove(id);
	}
	public static IConnettore removeConnettorePA(String id){
		return RepositoryConnettori.connettori_pa.remove(id);
	}
	
	public static Map<String, IConnettore> getConnettori_pd() {
		return RepositoryConnettori.connettori_pd;
	}
	public static Map<String, IConnettore> getConnettori_pa() {
		return RepositoryConnettori.connettori_pa;
	}
	
	public static List<String> getIdentificatoriConnettori_pd() {
		List<String> l = new ArrayList<>();
		if(RepositoryConnettori.connettori_pd!=null && !RepositoryConnettori.connettori_pd.isEmpty()) {
			l.addAll(RepositoryConnettori.connettori_pd.keySet());
		}
		return l;
	}
	public static List<String> getIdentificatoriConnettori_pa() {
		List<String> l = new ArrayList<>();
		if(RepositoryConnettori.connettori_pa!=null && !RepositoryConnettori.connettori_pa.isEmpty()) {
			l.addAll(RepositoryConnettori.connettori_pa.keySet());
		}
		return l;
	}
}
