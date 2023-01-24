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
package org.openspcoop2.core.config.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;

/**
 * ConfigUtils 
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigUtils {

	private static final String PROPS_SEPARATOR_VALUES = "!,!";
	public static SortedMap<List<String>> toSortedListMap(List<Proprieta> proprieta) throws UtilsException{
		SortedMap<List<String>> map = new SortedMap<List<String>> ();
		if(proprieta!=null && !proprieta.isEmpty()) {
			for (Proprieta p : proprieta) {
				List<String> l = map.get(p.getNome());
				if(l==null) {
					l = new ArrayList<>();
					map.add(p.getNome(), l);
				}
				String valore = p.getValore();
				if(valore.contains(PROPS_SEPARATOR_VALUES)) {
					String [] split = valore.split(PROPS_SEPARATOR_VALUES);
					if(split==null || split.length<=0) {
						l.add(valore);
					}
					else {
						for (String v : split) {
							l.add(v);
						}
					}
				}
				else {
					l.add(valore);
				}
			}
		}
		return map;
	}
	public static void addFromSortedListMap(List<Proprieta> proprieta, SortedMap<List<String>> map){
		List<String> keys = map.keys();
		if(keys!=null && !keys.isEmpty()) {
			for (String nome : keys) {
				List<String> valori = map.get(nome);
				if(valori!=null && !valori.isEmpty()) {
					if(valori.size()==1) {
						String valore = valori.get(0);
						Proprieta proprietaAutorizzazioneContenuto = new Proprieta();
						proprietaAutorizzazioneContenuto.setNome(nome);
						proprietaAutorizzazioneContenuto.setValore(valore);
						proprieta.add(proprietaAutorizzazioneContenuto);
					}
					else {
						StringBuilder sb = new StringBuilder();
						for (String valore : valori) {
							if(sb.length()>0) {
								sb.append(PROPS_SEPARATOR_VALUES);
							}
							sb.append(valore);
						}	
						Proprieta proprietaAutorizzazioneContenuto = new Proprieta();
						proprietaAutorizzazioneContenuto.setNome(nome);
						proprietaAutorizzazioneContenuto.setValore(sb.toString());
						proprieta.add(proprietaAutorizzazioneContenuto);
					}
				}
			}
		}
	}
	
}
