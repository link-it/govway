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

package org.openspcoop2.testsuite.core.asincrono;

/**
 * Raccogli gl id di correlazione
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SetIDCorrelazione{
	protected String idRichiesta;
	protected String idRisposta;
	public SetIDCorrelazione(String value1,String value2){
		this.idRichiesta=value1;
		this.idRisposta=value2;
	}
	
	public boolean contains(Object obj){
		String str=(String)obj;
		if(str.equals(this.idRichiesta)||str.equals(this.idRisposta))return true;
		return false;
	}
	
	@Override
	public String toString(){
		if(this.idRichiesta!=null && this.idRisposta!=null)
			return "idRichiesta:"+this.idRichiesta+" idRisposta:"+this.idRisposta;
		else
			return null;
	}
}