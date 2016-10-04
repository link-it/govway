/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.util.Enumeration;
import java.util.Hashtable;



/**
 * RepositoryConnettori
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RepositoryConnettori {

	private static Hashtable<String, IConnettore> connettori_pd = new Hashtable<String, IConnettore>();
	private static Hashtable<String, IConnettore> connettori_pa = new Hashtable<String, IConnettore>();
	
	
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
	
	public static Hashtable<String, IConnettore> getConnettori_pd() {
		return RepositoryConnettori.connettori_pd;
	}
	public static Hashtable<String, IConnettore> getConnettori_pa() {
		return RepositoryConnettori.connettori_pa;
	}
	
	public static Enumeration<String> getIdentificatoriConnettori_pd() {
		return RepositoryConnettori.connettori_pd.keys();
	}
	public static Enumeration<String> getIdentificatoriConnettori_pa() {
		return RepositoryConnettori.connettori_pa.keys();
	}
}
