/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package it.gov.spcoop.sica.wsbl.driver;


/**
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TemporalConditionTypePredicate {

	C_INVOKE ("C-INVOKE"),
	M_INVOKE ("M-INVOKE");
	
	
	private final String nome;

	TemporalConditionTypePredicate(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return this.nome;
	}
	
	@Override
	public String toString(){
		return this.nome;
	}
	
	public static String[] toStringArray(){
		String[] res = new String[TemporalConditionTypePredicate.values().length];
		int i=0;
		for (TemporalConditionTypePredicate tmp : TemporalConditionTypePredicate.values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[TemporalConditionTypePredicate.values().length];
		int i=0;
		for (TemporalConditionTypePredicate tmp : TemporalConditionTypePredicate.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static TemporalConditionTypePredicate toEnumConstant(String val){
		
		TemporalConditionTypePredicate res = null;
		
		if(TemporalConditionTypePredicate.C_INVOKE.toString().equals(val)){
			res = TemporalConditionTypePredicate.C_INVOKE;
		}else if(TemporalConditionTypePredicate.M_INVOKE.toString().equals(val)){
			res = TemporalConditionTypePredicate.M_INVOKE;
		} 
		return res;
	}
		
}


