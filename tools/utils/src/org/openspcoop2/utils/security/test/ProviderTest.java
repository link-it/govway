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
package org.openspcoop2.utils.security.test;

import java.security.Provider;
import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.security.ProviderUtils;

/**	
 * ProviderTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProviderTest {

	public static void main(String[] args) throws UtilsException {
		test(false);
		
		test(true);
	}
	public static void test(boolean useBCFacility) throws UtilsException {
		
		System.out.println("=========================\n");
		
		Provider bc = new org.bouncycastle.jce.provider.BouncyCastleProvider();
		
		List<String> names = ProviderUtils.getProviderNames();
		System.out.println("Providers attuali: "+names);
		
		boolean castle = useBCFacility ? ProviderUtils.existsBouncyCastle() : ProviderUtils.exists(bc);
		if(castle) {
			throw new UtilsException("Provider bouncy castle non atteso");
		}
		
		if(useBCFacility) {
			ProviderUtils.addBouncyCastle();
		}
		else{
			ProviderUtils.add(bc);
		}
		
		List<String> newNames = ProviderUtils.getProviderNames();
		System.out.println("Providers (dopo add BC): "+newNames);
		if(newNames.size()!=(names.size()+1)) {
			throw new UtilsException("Atteso un provider in più");
		}
		if(!bc.getName().equals(newNames.get(newNames.size()-1))) {
			throw new UtilsException("Atteso provider BC in fondo");
		}
		
		castle = useBCFacility ? ProviderUtils.existsBouncyCastle() : ProviderUtils.exists(bc);
		if(!castle) {
			throw new UtilsException("Provider bouncy castle atteso");
		}
		
		if(useBCFacility) {
			ProviderUtils.addBouncyCastleAfterSun(false); 
		}
		else {
			ProviderUtils.addIfNotExists(bc, 2); // non dovrebbe aggiungere nulla
		}
		
		List<String> newNames2 = ProviderUtils.getProviderNames();
		System.out.println("Providers (dopo add BC if not exists): "+newNames2);
		if(newNames.size()!=newNames2.size()) {
			throw new UtilsException("Attesi identici provider");
		}
		if(!bc.getName().equals(newNames2.get(newNames2.size()-1))) {
			throw new UtilsException("Atteso provider BC in fondo");
		}
		if(bc.getName().equals(newNames2.get(1))) {
			throw new UtilsException("Non atteso provider BC alla posizione 2");
		}
		
		if(useBCFacility) {
			ProviderUtils.addBouncyCastleAfterSun(true); 
		}
		else {
			ProviderUtils.addOverrideIfExists(bc, 2);
		}
		newNames2 = ProviderUtils.getProviderNames();
		System.out.println("Providers (dopo add BC override): "+newNames2);
		if(newNames.size()!=newNames2.size()) {
			throw new UtilsException("Attesi identici provider");
		}
		if(bc.getName().equals(newNames2.get(newNames2.size()-1))) {
			throw new UtilsException("Non atteso provider BC in fondo");
		}
		if(!bc.getName().equals(newNames2.get(1))) {
			throw new UtilsException("Atteso provider BC alla posizione 2");
		}
		
		// ripeto: essendo già presente alla stessa posizione non viene aggiunto
		if(useBCFacility) {
			ProviderUtils.addBouncyCastleAfterSun(true); 
		}
		else {
			ProviderUtils.addOverrideIfExists(bc, 2);
		}
		newNames2 = ProviderUtils.getProviderNames();
		System.out.println("Providers (dopo add BC override): "+newNames2);
		if(newNames.size()!=newNames2.size()) {
			throw new UtilsException("Attesi identici provider");
		}
		if(bc.getName().equals(newNames2.get(newNames2.size()-1))) {
			throw new UtilsException("Non atteso provider BC in fondo");
		}
		if(!bc.getName().equals(newNames2.get(1))) {
			throw new UtilsException("Atteso provider BC alla posizione 2");
		}
		
		
		if(useBCFacility) {
			ProviderUtils.removeBouncyCastle();
		}
		else {
			ProviderUtils.remove(bc);
		}
		newNames2 = ProviderUtils.getProviderNames();
		System.out.println("Providers (dopo remove): "+newNames2);
		if(names.size()!=newNames2.size()) {
			throw new UtilsException("Attesi identici provider come all'inizio");
		}
		if(bc.getName().equals(newNames2.get(newNames2.size()-1))) {
			throw new UtilsException("Non atteso provider BC in fondo");
		}
		if(bc.getName().equals(newNames2.get(1))) {
			throw new UtilsException("Non atteso provider BC alla posizione 2");
		}
		
		System.out.println("Testsuite completata (useBCFacility:"+useBCFacility+")");
	}

}
