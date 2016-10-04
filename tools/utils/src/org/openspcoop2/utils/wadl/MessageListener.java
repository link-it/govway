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

package org.openspcoop2.utils.wadl;

import org.slf4j.Logger;

/**
 * Message Listener che riceve gli eventi durante la lettura di un wadl
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */

public class MessageListener implements org.jvnet.ws.wadl.util.MessageListener {

	private Logger log;
	private boolean logInfoLevel;
	private boolean logWarningLevel;
	public MessageListener(Logger log){
		this(log, true, true);
	}
	public MessageListener(Logger log,boolean logInfoLevel,boolean logWarningLevel){
		this.log = log;
		this.logInfoLevel = logInfoLevel;
		this.logWarningLevel = logWarningLevel;
	}
	
	@Override
	public void error(String message, Throwable t) {
		if(this.log!=null){
			this.log.error("[WADL] "+message, t);
		}else{
			System.err.println("[WADL] "+message);
			t.printStackTrace(System.err);
		}
	}

	@Override
	public void info(String message) {
		if(this.logInfoLevel){
			if(this.log!=null){
				this.log.info("[WADL] "+message);
			}
			else{
				System.out.println("[WADL] "+message);
			}
		}
	}

	@Override
	public void warning(String message, Throwable t) {
		if(this.logWarningLevel){
			if(this.log!=null){
				this.log.warn("[WADL] "+message, t);
			}else{
				System.out.println("[WADL] "+message);
				t.printStackTrace(System.out);
			}
		}
	}

}
