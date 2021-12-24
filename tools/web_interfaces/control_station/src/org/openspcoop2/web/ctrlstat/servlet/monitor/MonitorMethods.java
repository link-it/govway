/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.monitor;

import java.util.ArrayList;

/**
 * MonitorMethods
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public enum MonitorMethods {

	LISTA_RICHIESTE_PENDENTI("Lista"), 
	STATO_RICHIESTE("Stato"),
	RICONSEGNA_IMMEDIATA_RICHIESTE_PENDENTI(MonitorCostanti.LABEL_ACTION_RICONSEGNA_IMMEDIATA),
	ELIMINAZIONE_RICHIESTE_PENDENTI("Eliminazione");

	private String nome;

	private static ArrayList<String> methodNames;

	static boolean stato = false; // RIVEDERNE LE FUNZIONALITA
	
	// inizializzo la lista dei nomi
	static {
		MonitorMethods.methodNames = new ArrayList<String>();
		MonitorMethods[] methods = MonitorMethods.values();
		for (MonitorMethods method : methods) {
			if(STATO_RICHIESTE.equals(method) && !stato) {
				continue;
			}
			MonitorMethods.methodNames.add(method.getNome());
		}
	}

	MonitorMethods(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return this.nome;
	}

	public static String[] getMethodsNames() {
		return MonitorMethods.methodNames.toArray(new String[1]);
	}

	public static ArrayList<String> getMethodNames() {
		return new ArrayList<String>(MonitorMethods.methodNames);
	}
}
