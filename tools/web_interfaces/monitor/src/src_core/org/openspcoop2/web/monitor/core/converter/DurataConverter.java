/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.core.converter;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * DurataConverter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DurataConverter implements Converter {

	//	private final static String patternDurata = "dd'g' hh'h' mm'm' ss's'";


	@Override
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {

		if(value!=null){
			if(value instanceof Date){
				//SimpleDateFormat sdf = new SimpleDateFormat(patternDurata);
				//String res = sdf.format(value);
				long t = ((Date)value).getTime();
				if(t >=0)
					return DurataConverter.convertSystemTimeIntoString_millisecondi(t, true);
			}
			if(value instanceof Long){
				long t = ((Long)value).longValue();
				if(t >=0)
					return DurataConverter.convertSystemTimeIntoString_millisecondi(t, true);
			}
		}

		return "";
	}


	public static String convertSystemTimeIntoString_millisecondi(long time,boolean millisecondiCheck){
		//System.out.println("VALORE PASSATO: ["+time+"]");
		long millisecondi = time % 1000;
		//System.out.println("Millisecondi (Valore%1000): ["+millisecondi+"]");
		long diff = (time)/1000;
		//System.out.println("Diff... (valore/1000) ["+diff+"]");
		long ore = diff/3600;
		//System.out.println("Ore... (diff/3600) ["+ore+"]");
		long minuti = (diff%3600) / 60;
		//System.out.println("Minuti... (diff%3600) / 60 ["+minuti+"]");
		long secondi = (diff%3600) % 60;
		//System.out.println("Secondi... (diff%3600) % 60 ["+secondi+"]");

		long giorni = ore/24;
		long oreRimaste = ore%24;


		StringBuffer bf = new StringBuffer();
		/*if(ore==1)
			bf.append(ore+" ora ");
		else if(ore>0)
			bf.append(ore+" ore ");*/
		if(giorni==1)
			bf.append(giorni+" g ");
		else if(giorni>0)
			bf.append(giorni+" g ");
		if(oreRimaste==1)
			bf.append(oreRimaste+" h ");
		else if(oreRimaste>0)
			bf.append(oreRimaste+" h ");
		if(minuti==1)
			bf.append(minuti+" m ");
		else if(minuti>0)
			bf.append(minuti+" m ");
		if(secondi==1)
			bf.append(secondi+" s ");
		else if(secondi>0)
			bf.append(secondi+" s ");
		if(millisecondiCheck){
			if(millisecondi==1)
				bf.append(millisecondi+" ms");
			else if(millisecondi>=0)
				bf.append(millisecondi+" ms");
		}
		if(bf.length()==0){
			bf.append("conversione non riuscita");
		}
		return bf.toString();
	}
}
