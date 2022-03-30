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

package org.openspcoop2.pdd.mdb.threads;

import org.openspcoop2.pdd.mdb.GenericMessage;

/**
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessaggioSerializzato implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String idMessaggio;
	private GenericMessage msg;
	
	public MessaggioSerializzato(String id, GenericMessage msg){
		this.idMessaggio=id;
		this.msg=msg;
	}
	
	
	public String getIdMessaggio() {
		return this.idMessaggio;
	}
	public void setIdMessaggio(String id) {
		this.idMessaggio = id;
	}
	public GenericMessage getMsg() {
		return this.msg;
	}
	public void setMsg(GenericMessage msg) {
		this.msg = msg;
	}
	
	
	
}
