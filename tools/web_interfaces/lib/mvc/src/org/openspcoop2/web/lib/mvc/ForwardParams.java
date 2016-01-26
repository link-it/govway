/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.web.lib.mvc;

/**
 * ForwardParams
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ForwardParams {

	private TipoOperazione tipoOperazione;
	private String otherContext;

	private ForwardParams(TipoOperazione tipoOperazione,String otherContext){
		this.tipoOperazione = tipoOperazione;
		this.otherContext = otherContext;
	}
	private ForwardParams(TipoOperazione tipoOperazione){
		this.tipoOperazione = tipoOperazione;
	}

	public TipoOperazione getTipoOperazione() {
		return this.tipoOperazione;
	}
	public String getOtherContext() {
		return this.otherContext;
	}

	public static ForwardParams ADD(){
		return new ForwardParams(TipoOperazione.ADD);
	}
	public static ForwardParams CHANGE(){
		return new ForwardParams(TipoOperazione.CHANGE);
	}
	public static ForwardParams DEL(){
		return new ForwardParams(TipoOperazione.DEL);
	}
	public static ForwardParams LIST(){
		return new ForwardParams(TipoOperazione.LIST);
	}
	public static ForwardParams LOGIN(){
		return new ForwardParams(TipoOperazione.LOGIN);
	}
	public static ForwardParams LOGOUT(){
		return new ForwardParams(TipoOperazione.LOGOUT);
	}
	public static ForwardParams OTHER(String otherContext){
		return new ForwardParams(TipoOperazione.OTHER,otherContext);
	}
}
