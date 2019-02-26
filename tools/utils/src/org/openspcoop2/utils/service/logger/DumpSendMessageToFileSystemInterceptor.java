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

package org.openspcoop2.utils.service.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;

/**	
 * DumpSendMessageToFileSystemInterceptor
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 **/
@SuppressWarnings("rawtypes")
public class DumpSendMessageToFileSystemInterceptor extends AbstractPhaseInterceptor {

	static final Logger log = org.slf4j.LoggerFactory.getLogger(DumpSendMessageToFileSystemInterceptor.class);
	
	public DumpSendMessageToFileSystemInterceptor() {
		super(Phase.PRE_STREAM);
	}

	@Override
	public void handleMessage(Message message) {
		String msgAsString = message.toString();
		String logMsg = String.format("SEND message %s", msgAsString);
		log.info(logMsg);
		message.put(Message.ENCODING, "UTF-8");
		OutputStream outputStream = message.getContent(OutputStream.class);
		if(outputStream!=null) {
			final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(outputStream);
	        message.setContent(OutputStream.class, newOut);
	        newOut.registerCallback(new LoggingCallback());
		}	
    }

}
class LoggingCallback implements CachedOutputStreamCallback {
	
    @Override
	public void onFlush(CachedOutputStream cos) {
    }

    @Override
	public void onClose(CachedOutputStream cos) {
       	File f = null;
		try {
			f = File.createTempFile("SendMessage", ".dump");
			FileOutputStream fos =new FileOutputStream(f);
			cos.writeCacheTo(fos);
			fos.flush();
			cos.flush();
			fos.close();
			String logMsg = String.format("Serialized in [%s]",f.getAbsolutePath());
			DumpSendMessageToFileSystemInterceptor.log.info(logMsg);
		}catch(Exception e) {
			DumpSendMessageToFileSystemInterceptor.log.error(e.getMessage(),e);
		}
    }
}
		




