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


package org.openspcoop2.utils.serialization;

import java.util.List;

/**	
 * Contiene le informazioni sul filtro da effettuare durante la serializzazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExclusionPropertyFilter  implements net.sf.json.util.PropertyFilter {

	private List<String> exclusion = null;
	
	public ExclusionPropertyFilter(List<String> ex){
		this.exclusion = ex;
	}
	
	@Override
	public boolean apply(Object source, String nomeField, Object value) {
		
		if(this.exclusion!=null && this.exclusion.contains(nomeField)){
			return true;
		}
		
		return false;
	}

}
