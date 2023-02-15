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

package org.openspcoop2.core.protocolli.trasparente.testsuite.plugin.classes;

import java.io.File;
import java.util.List;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* Utilities
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Utilities {

	public static String readIdentificativoTest() throws Exception {
		File f = new File("/tmp/govway.plugins.testsuite.id");
		return FileSystemUtilities.readFile(f);
	}
	public static String readTipoTest(List<Proprieta> list) throws Exception {
		return readTipoTest(list, false);
	}
	public static String readTipoTest(List<Proprieta> list, boolean remove) throws Exception {
		return _read("tipoTest", list, remove);
	}
	public static void writeIdentificativoTest(String id, String tipo) throws Exception {
		File f = new File("/tmp/"+id+"."+tipo+".result");
		FileSystemUtilities.writeFile(f, "OK".getBytes());
	}
	
	public static String readNomeApplicativo(List<Proprieta> list) throws Exception {
		return readNomeApplicativo(list, false);
	}
	public static String readNomeApplicativo(List<Proprieta> list, boolean remove) throws Exception {
		return _read("applicativo", list, remove);
	}
	
	
	private static String _read(String nome, List<Proprieta> list, boolean remove) throws Exception {
		if(list!=null && !list.isEmpty()) {
			int index = -1;
			Proprieta proprieta = null;
			for (int i = 0; i < list.size(); i++) {
				Proprieta tmp = list.get(i);
				if(nome.equals(tmp.getNome())) {
					proprieta = tmp;
					index = i;
					break;
				}
			}
			if(remove) {
				list.remove(index);
			}
			return proprieta.getValore();
		}
		throw new Exception("Proprietà '"+nome+"' non definita");
	}
	
	
	public static void addRegolaAutorizzazioneContenutoBuiltIn(List<Proprieta> list) {
		Proprieta p = new Proprieta();
		p.setNome("${header:test-plugins-autorizzazione-contenuto}");
		p.setValore("govway-testsuite-plugins-authz");
		list.add(p);
	}
}
