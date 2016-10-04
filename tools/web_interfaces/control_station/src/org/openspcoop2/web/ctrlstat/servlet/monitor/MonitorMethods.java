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

	LISTA_RICHIESTE_PENDENTI("Lista"), STATO_RICHIESTE("Stato"), ELIMINAZIONE_RICHIESTE_PENDENTI("Eliminazione");

	private String nome;

	private static ArrayList<String> methodNames;

	// inizializzo la lista dei nomi
	static {
		MonitorMethods.methodNames = new ArrayList<String>();
		MonitorMethods[] methods = MonitorMethods.values();
		for (MonitorMethods method : methods) {
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
		return MonitorMethods.methodNames.toArray(new String[MonitorMethods.values().length]);
	}

	public static ArrayList<String> getMethodNames() {
		return new ArrayList<String>(MonitorMethods.methodNames);
	}
}
