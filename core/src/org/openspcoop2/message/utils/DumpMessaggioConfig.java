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


package org.openspcoop2.message.utils;

import java.io.Serializable;

/**
 * DumpMessaggioConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpMessaggioConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4718160136521047108L;
		
	private boolean dumpBody = true;
	private boolean dumpHeaders = true;
	private boolean dumpAttachments = true;
	private boolean dumpMultipartHeaders = true;
	
	public boolean isDumpBody() {
		return this.dumpBody;
	}
	public void setDumpBody(boolean dumpBody) {
		this.dumpBody = dumpBody;
	}
	public boolean isDumpHeaders() {
		return this.dumpHeaders;
	}
	public void setDumpHeaders(boolean dumpHeaders) {
		this.dumpHeaders = dumpHeaders;
	}
	public boolean isDumpAttachments() {
		return this.dumpAttachments;
	}
	public void setDumpAttachments(boolean dumpAttachments) {
		this.dumpAttachments = dumpAttachments;
	}
	public boolean isDumpMultipartHeaders() {
		return this.dumpMultipartHeaders;
	}
	public void setDumpMultipartHeaders(boolean dumpMultipartHeaders) {
		this.dumpMultipartHeaders = dumpMultipartHeaders;
	}	
}
