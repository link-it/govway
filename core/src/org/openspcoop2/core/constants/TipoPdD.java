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


package org.openspcoop2.core.constants;

import java.io.Serializable;

/**
 * Contiene i tipi di una porta di dominio
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum TipoPdD implements Serializable {

	DELEGATA ("delegata"),
	APPLICATIVA ("applicativa"),
	INTEGRATION_MANAGER ("integrationManager"), // servizio di Message Box
	ROUTER ("router");
	
	
	private final String tipo;

	TipoPdD(String tipo)
	{
		this.tipo = tipo;
	}

	public String getTipo()
	{
		return this.tipo;
	}
	
	public boolean equals(TipoPdD tipoPdD){
		return this.tipo.equals(tipoPdD.getTipo());
	}
	
	public static TipoPdD toTipoPdD(String value){
		if(DELEGATA.getTipo().equals(value)){
			return DELEGATA;
		}
		else if(APPLICATIVA.getTipo().equals(value)){
			return APPLICATIVA;
		}
		else if(INTEGRATION_MANAGER.getTipo().equals(value)){
			return INTEGRATION_MANAGER;
		}
		else if(ROUTER.getTipo().equals(value)){
			return ROUTER;
		}
		else{
			return null;
		}
	}
}

