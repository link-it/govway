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

package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.classes;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* AbstractTracciamentoHandler
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AbstractTracciamentoHandler {

	public static final String OK = "OK";
	public static final String FINE = "FINE";
	
	public static final String REQ_FILE = "GovWay-TestSuite-Credenziale-RequestFile";
	public static final String RES_FILE = "GovWay-TestSuite-Credenziale-ResponseFile";
	public static final String POST_RES_FILE = "GovWay-TestSuite-Credenziale-PostResponseFile";
	
	public void invoke(File file) throws HandlerException {
		
		try {
			
			FileSystemUtilities.writeFile(file, OK.getBytes());
			
			int limit = 300; // 15 secondi
			int offset = 0;
			while(offset<limit) {
				Utilities.sleep(50);
				String content = null;
				if(file.exists()) {
					content = FileSystemUtilities.readFile(file);
				}
				/**System.out.println("LETTO ["+file.getName()+"] (offset:"+offset+"): '"+content+"'");*/
				if(file.exists() && content!=null && StringUtils.isNotEmpty(content) && !OK.equals(content)) {
					if(FINE.equals(content)) {
						/**System.out.println("LETTO ["+file.getName()+"] FINE");*/
						return;
					}
					else {
						int sleep = 0;
						try {
							sleep = Integer.valueOf(content);
						}catch(Exception e) {
							throw new Exception("File '"+file.getAbsolutePath()+"' contiente un contenuto non atteso ("+content+"): "+e.getMessage());
						}
						Utilities.sleep(sleep);
						/**System.out.println("LETTO ["+file.getName()+"] FINE DOPO SLEEP "+sleep);*/
						return;
					}
				}
				offset++;
			}
			if(offset==limit) {
				throw new Exception("Limit raggiunto per file '"+file.getAbsolutePath()+"'");
			}
			
		}catch(Exception e) {
			throw new HandlerException(e.getMessage(),e);
		}
		
	}

}
