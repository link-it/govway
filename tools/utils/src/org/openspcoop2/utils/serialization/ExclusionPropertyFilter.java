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


package org.openspcoop2.utils.serialization;


/**	
 * Contiene le informazioni sul filtro da effettuare durante la serializzazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExclusionPropertyFilter  implements net.sf.json.util.PropertyFilter {

	private String[] exclusion = null;
	
	public ExclusionPropertyFilter(String [] ex){
		this.exclusion = ex;
	}
	
	@Override
	public boolean apply(Object source, String nomeField, Object value) {
		
		if(this.exclusion!=null){
			//System.out.println("OBJECT ["+source.getClass().getName()+"] arg1["+nomeField+"]arg2 ["+value+"]");
			for (int i = 0; i < this.exclusion.length; i++) {
				if(this.exclusion[i].equals(nomeField)){
					return true;
				}
			}
		}
		
		return false;
	}

}
