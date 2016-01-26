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


package org.openspcoop2.utils.id;




/**
 * UniversallyUniqueIdentifier
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BaseUniqueIdentifier implements IUniqueIdentifier {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String uniqueId;
	
	public BaseUniqueIdentifier(String uniqueId){
		this.uniqueId = uniqueId;
	}
	public BaseUniqueIdentifier(Long uniqueId){
		this.uniqueId = uniqueId.longValue()+"";
	}
	public BaseUniqueIdentifier(Integer uniqueId){
		this.uniqueId = uniqueId.intValue()+"";
	}
	public BaseUniqueIdentifier(Object uniqueId) throws UniqueIdentifierException{
		if(uniqueId instanceof String){
			this.uniqueId = (String) uniqueId;
		}
		else if(uniqueId instanceof Long){
			this.uniqueId = ((Long) uniqueId).longValue()+"";
		}
		else if(uniqueId instanceof Integer){
			this.uniqueId = ((Integer) uniqueId).intValue()+"";
		}
		else{
			throw new UniqueIdentifierException("Tipo ["+uniqueId.getClass().getName()+"] non supportato");
		}
	}

	
	@Override
	public String getAsString() {
		return this.uniqueId;
	}
		
	@Override
	public String toString(){
		return this.uniqueId;
	}
}
