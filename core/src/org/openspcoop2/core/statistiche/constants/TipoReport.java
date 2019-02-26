/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.core.statistiche.constants;

import java.io.Serializable;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * TipoReport
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoReport  implements IEnumeration , Serializable , Cloneable{

	BAR_CHART ("bar_chart"), PIE_CHART ("pie_chart"), TABELLA ("tabella"),  LINE_CHART ("line_chart"),
		ANDAMENTO_TEMPORALE ("andamentoTemporale");

	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}

	/** Official Constructor */
	TipoReport(String value)
	{
		this.value = value;
	}

	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoReport object){
		if(object==null)
			return false;
		if(object.getValue()==null)
			return false;
		return object.getValue().equals(this.getValue());	
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}

	/** Utilities */

	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoReport tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoReport tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoReport tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static TipoReport toEnumConstant(String value){
		TipoReport res = null;
		if(TipoReport.BAR_CHART.getValue().equals(value)){
			res = TipoReport.BAR_CHART;
		}else if(TipoReport.PIE_CHART.getValue().equals(value)){
			res = TipoReport.PIE_CHART;
		}else if(TipoReport.LINE_CHART.getValue().equals(value)){
			res = TipoReport.LINE_CHART;
		}else if(TipoReport.TABELLA.getValue().equals(value)){
			res = TipoReport.TABELLA;
		}else if(TipoReport.ANDAMENTO_TEMPORALE.getValue().equals(value)){
			res = TipoReport.ANDAMENTO_TEMPORALE;
		}  


		return res;
	}

	public static IEnumeration toEnumConstantFromString(String value){
		TipoReport res = null;
		if(TipoReport.BAR_CHART.toString().equals(value)){
			res = TipoReport.BAR_CHART;
		}else if(TipoReport.PIE_CHART.toString().equals(value)){
			res = TipoReport.PIE_CHART;
		}else if(TipoReport.LINE_CHART.toString().equals(value)){
			res = TipoReport.LINE_CHART;
		}else if(TipoReport.TABELLA.toString().equals(value)){
			res = TipoReport.TABELLA;
		}else if(TipoReport.ANDAMENTO_TEMPORALE.toString().equals(value)){
			res = TipoReport.ANDAMENTO_TEMPORALE;
		}  
		return res;
	}

}
