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

package org.openspcoop2.utils.security;

import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ProviderUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ProviderUtils {
	
	private ProviderUtils(){}

	public static void addBouncyCastle() {
		add(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	public static void add(java.security.Provider provider) {
		java.security.Security.addProvider(provider);
	}
	
	public static void addBouncyCastleAfterSun(boolean overrideIfExists) {
		/**Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());*/
		if(overrideIfExists) {
			addOverrideIfExists(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		}
		else {
			addIfNotExists(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		}
	}
	
	public static void addIfNotExists(java.security.Provider provider, int position) {
		if(Security.getProvider(provider.getName())==null) {
			Security.insertProviderAt(provider, position); 
		}
	}
	// NOTA: utility Security.insertProviderAt utilizza una posizione vera e non da programmatore che parte da 0!!!!!!!!!!!
	public static void addOverrideIfExists(java.security.Provider provider, int position) { 
		if(Security.getProvider(provider.getName())!=null) {
			List<java.security.Provider> l = getProviders();
			if(!l.isEmpty() && l.size()>(position-1) && l.get((position-1)).getName().equals(provider.getName())) {
				return; // gia presente alla posizione due
			}			
			Security.removeProvider(provider.getName());
		}
		Security.insertProviderAt(provider, position); 
	}
	
	public static boolean existsBouncyCastle() {
		return exists(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	public static boolean exists(java.security.Provider provider) {
		return Security.getProvider(provider.getName())!=null;
	}
	
	public static boolean existsBouncyCastle(int position) {
		return exists(new org.bouncycastle.jce.provider.BouncyCastleProvider(), position);
	}
	public static boolean exists(java.security.Provider provider, int position) {
		List<java.security.Provider> l = getProviders();
		if(l.isEmpty() || l.size()<position) {
			return false;
		}
		return l.get(position).getName().equals(provider.getName());
	}
	
	public static void removeBouncyCastle() {
		remove(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	public static void remove(java.security.Provider provider) {
		Security.removeProvider(provider.getName());
	}
	
	public static List<java.security.Provider> getProviders(){
		List<java.security.Provider> l = new ArrayList<>();
		java.security.Provider [] p = Security.getProviders();
		if(p!=null && p.length>0) {
			l.addAll(Arrays.asList(p));
		}
		return l;
	}
	public static List<String> getProviderNames(){
		List<String> l = new ArrayList<>();
		List<java.security.Provider> lp = getProviders();
		if(!lp.isEmpty()) {
			for (java.security.Provider provider : lp) {
				l.add(provider.getName());
			}
		}
		return l;
	}
 }
