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
package org.openspcoop2.utils.jaxb;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * DateTime2Date
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Decimal2String extends XmlAdapter<String, DecimalWrapper>
{
	@Override
	public String marshal(DecimalWrapper v) throws Exception {
		if(v==null){
			return null;
		}
		StringBuffer pattern = new StringBuffer();
		for (int i = 0; i < v.getMaxInteger(); i++) {
			pattern.append("0");
		}
		if(v.getMaxDecimal()>0){
			pattern.append(".");
			for (int i = 0; i < v.getMaxDecimal(); i++) {
				pattern.append("0");
			}	
		}
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US); // per avere la punteggiatura come separatore
		DecimalFormat df = new DecimalFormat(pattern.toString(),dfs);

		//System.out.println("MARSHALL -> ["+pattern.toString()+"] int["+v.getInteger()+"] dec["+v.getDecimal()+"] ["+df.format(v.getObject())+"]");
		String value =  df.format(v.getObject());
		
		if( (v.getMinInteger()!=v.getMaxInteger()) 
				||
				(v.getMinDecimal()!=v.getMaxDecimal())	){
			//System.out.println("VALORE OTTENUTO ["+value+"]");
			if(value.contains(".")){
				String [] split = value.split("\\.");
				String left = split[0];
				String right = split[1];
				if(v.getMinInteger()!=v.getMaxInteger()){
					left = this.getLeftMinString(split[0], v.getMinInteger());
				}
				if(v.getMinDecimal()!=v.getMaxDecimal()){
					right = this.getRightMinString(split[1], v.getMinDecimal());
				}
				value =  left + "." + right ;
			}else{
				value = this.getLeftMinString(value, v.getMinInteger());
			}
			//System.out.println("VALORE CORRETTO ["+value+"]");
			return value;
		}else{
			return value;
		}
	}
	
	private String getLeftMinString(String value,int min){
		StringBuffer bf = new StringBuffer();
		for (int i = (value.length()-1); i >= 0; i--) {
			if(value.charAt(i) != '0' || !this.onlyLeftZero(value, i)){
				bf.append(value.charAt(i));
			}
			else{
				if(bf.length()>=min){
					break;
				}
				else{
					bf.append(value.charAt(i));
				}
			}
		}
		return bf.reverse().toString();
	}
	private String getRightMinString(String value,int min){
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i <value.length(); i++) {
			if(value.charAt(i) != '0' || !this.onlyRightZero(value, i)){
				bf.append(value.charAt(i));
			}
			else{
				if(bf.length()>=min){
					break;
				}
				else{
					bf.append(value.charAt(i));
				}
			}
		}
		return bf.reverse().toString();
	}
	
	private boolean onlyLeftZero(String value, int fromIndex){
		for (int i = fromIndex; i >=0; i--) {
			if(value.charAt(i) != '0'){
				return false;
			}
		}
		return true;
	}
	private boolean onlyRightZero(String value, int fromIndex){
		for (int i = fromIndex; i <value.length(); i++) {
			if(value.charAt(i) != '0'){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public DecimalWrapper unmarshal(String sParam) throws Exception {
		if(sParam==null){
			return null;
		}
		String s = sParam.trim();
		StringBuffer pattern = new StringBuffer();
		DecimalWrapper dw = new DecimalWrapper();
		if(s.contains(".")){
			String [] split = s.split("\\.");
			dw.setMinInteger(split[0].length());
			dw.setMaxInteger(split[0].length());
			for (int i = 0; i < split[0].length(); i++) {
				pattern.append("0");
			}
			pattern.append(".");
			dw.setMinDecimal(split[1].length());
			dw.setMaxDecimal(split[1].length());
			for (int i = 0; i < split[1].length(); i++) {
				pattern.append("0");
			}
		}
		else{
			dw.setMinInteger(s.length());
			dw.setMaxInteger(s.length());
			for (int i = 0; i < s.length(); i++) {
				pattern.append("0");
			}
		}
		
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US); // per avere la punteggiatura come separatore
		DecimalFormat df = new DecimalFormat(pattern.toString(),dfs);
		Object o = df.parseObject(s);
		dw.setObject(o);
		
		//System.out.println("UNMARSHALL -> ["+pattern.toString()+"] string["+s+"] ["+dw.getObject()+"] min["+dw.getInteger()+"] dec["+dw.getDecimal()+"]");
		
		return dw;
	}
}

