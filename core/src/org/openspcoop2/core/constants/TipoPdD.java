/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

