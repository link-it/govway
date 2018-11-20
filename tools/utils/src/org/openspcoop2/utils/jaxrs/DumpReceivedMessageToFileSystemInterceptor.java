/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.jaxrs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;

/**	
 * DumpReceivedMessageToFileSystemInterceptor
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@SuppressWarnings("rawtypes")
public class DumpReceivedMessageToFileSystemInterceptor extends AbstractPhaseInterceptor {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(DumpReceivedMessageToFileSystemInterceptor.class);
	
	public DumpReceivedMessageToFileSystemInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) {
		String msgAsString = message.toString();
		String logMsg = String.format("RECEIVE message %s", msgAsString);
		log.info(logMsg);
		message.put(Message.ENCODING, "UTF-8");
		InputStream paramInputStream = message.getContent(InputStream.class);

		if(paramInputStream!=null) {
			File f = null;
			try {
				f = File.createTempFile("ReceivedMessage", ".dump");
			}catch(Exception e) {
				log.error(e.getMessage(),e);
				return;
			}
			try (FileOutputStream fos = new FileOutputStream(f);){
				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = paramInputStream.read(buffer);
					if (bytesRead == -1) break;
					fos.write(buffer, 0, bytesRead);
				}
				fos.flush();
				paramInputStream.close();
			}catch(Exception e) {
				log.error(e.getMessage(),e);
				return;
			}
	
			logMsg = String.format("Serialized in [%s]",f.getAbsolutePath());
			log.info(logMsg);
			try {
				message.setContent(InputStream.class, new FileInputStream(f));
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}

	}

}
