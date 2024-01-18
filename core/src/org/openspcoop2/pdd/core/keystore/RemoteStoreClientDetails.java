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
package org.openspcoop2.pdd.core.keystore;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.utils.certificate.remote.RemoteStoreClientInfo;

/**
 * RemoteStoreClientDetails
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreClientDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private RemoteStoreClientInfo clientInfo;
	private Date dataAggiornamento;
	private boolean invalid;
	
	public RemoteStoreClientInfo getClientInfo() {
		return this.clientInfo;
	}
	public void setClientInfo(RemoteStoreClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}
	public Date getDataAggiornamento() {
		return this.dataAggiornamento;
	}
	public void setDataAggiornamento(Date dataAggiornamento) {
		this.dataAggiornamento = dataAggiornamento;
	}
	public boolean isInvalid() {
		return this.invalid;
	}
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
	
}
