/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
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
	
	public void invoke(File file, String idTransazione, String fase) throws HandlerException {
		
		try {
			
			StringBuilder sb = new StringBuilder("====================================== ("+fase+") "+idTransazione+" =================================\n\n");
			
			String contenuto = file.exists() ? FileSystemUtilities.readFile(file) : null;
			sb.append("<"+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+"> Gestione ["+file.getName()+"]; exists["+file.exists()+"] contenuto["+contenuto+"]\n\n");	
			Utilities.sleep(50);
			FileSystemUtilities.writeFile(file, OK.getBytes());
			sb.append("<"+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+"> WRITE OK in ["+file.getName()+"]; contenuto["+FileSystemUtilities.readFile(file)+"]\n\n");			
			
			int limit = 300; // 15 secondi
			int offset = 0;
			while(offset<limit) {
				Utilities.sleep(50);
				String content = null;
				boolean fExists = file.exists();
				if(fExists) {
					content = FileSystemUtilities.readFile(file);
				}
				sb.append("<"+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+"> LETTO ["+file.getName()+"] EXISTS["+fExists+"] (offset:"+offset+"): '"+content+"'\n\n");
				if(file.exists() && content!=null && StringUtils.isNotEmpty(content) && !OK.equals(content)) {
					if(FINE.equals(content)) {
						sb.append("<"+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+"> LETTO ["+file.getName()+"] FINE");
						return;
					}
					else {
						int sleep = 0;
						try {
							sleep = Integer.valueOf(content);
						}catch(Exception e) {
							throw new HandlerException("File '"+file.getAbsolutePath()+"' contiente un contenuto non atteso ("+content+"): "+e.getMessage());
						}
						Utilities.sleep(sleep);
						sb.append("<"+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+"> FILE ["+file.getName()+"] FINE DOPO SLEEP "+sleep+"\n\n");
						return;
					}
				}
				offset++;
			}
			if(offset==limit) {
				throw new HandlerException("Limit raggiunto per file '"+file.getAbsolutePath()+"'\n"+sb.toString());
			}
			
		}catch(Exception e) {
			throw new HandlerException(e.getMessage(),e);
		}
		
	}

}
