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


package org.openspcoop2.protocol.utils;

import java.util.Iterator;
import java.util.Map;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.regexp.RegExpException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.regexp.RegularExpressionPatternCompileMode;
import org.openspcoop2.utils.transport.TransportUtils;



/**
* EsitiInstanceProperties
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class EsitoTransportContextIdentification  {

	private String name;
	private EsitoTransportContextIdentificationMode mode;
	private String regularExpr;
	private String value;
	private String type;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public EsitoTransportContextIdentificationMode getMode() {
		return this.mode;
	}
	public void setMode(EsitoTransportContextIdentificationMode mode) {
		this.mode = mode;
	}
	public String getRegularExpr() {
		return this.regularExpr;
	}
	public void setRegularExpr(String regularExpr) {
		this.regularExpr = regularExpr;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean match(Map<String, String> p) throws ProtocolException{
		Iterator<String> keys = p.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String valueKey = TransportUtils.get(p, key);
			if(key.equalsIgnoreCase(this.name)){
				
				// trovato header con nome atteso
				switch (this.mode) {
				case EXISTS:
					return true;
				case MATCH:
					try{
						if(RegularExpressionEngine.isMatch(valueKey, this.regularExpr, RegularExpressionPatternCompileMode.CASE_INSENSITIVE)){
							return true;
						}
					}catch(RegExpNotFoundException notFound){	
						continue;
					}catch(RegExpException exp){
						throw new ProtocolException(exp.getMessage(),exp);
					}
					break;
				case CONTAINS:
					if(this.regularExpr==null){
						if(this.value!=null && valueKey!=null && valueKey.toLowerCase().contains(this.value.toLowerCase())){
							return true;
						}
						// else devo iterare sulla prossima key
					}
					else{
						String valueRexExp = null;
						try{
							valueRexExp = RegularExpressionEngine.getStringMatchPattern(valueKey, this.regularExpr, RegularExpressionPatternCompileMode.CASE_INSENSITIVE);
						}catch(RegExpNotFoundException notFound){	
							continue;
						}catch(RegExpException exp){
							throw new ProtocolException(exp.getMessage(),exp);
						}
						if(this.value!=null && valueRexExp!=null && valueRexExp.toLowerCase().contains(this.value.toLowerCase())){
							return true;
						}
						// else devo iterare sulla prossima key
					}
					break;
				case EQUALS:
					if(this.regularExpr==null){
						if(this.value==null){
							if(valueKey==null){
								return true;
							}
							// else devo iterare sulla prossima key
						}
						else{
							if(this.value.equalsIgnoreCase(valueKey)){
								return true;
							}
							// else devo iterare sulla prossima key
						}
					}
					else{
						String valueRexExp = null;
						try{
							valueRexExp = RegularExpressionEngine.getStringMatchPattern(valueKey, this.regularExpr, RegularExpressionPatternCompileMode.CASE_INSENSITIVE);
						}catch(RegExpNotFoundException notFound){	
							continue;
						}catch(RegExpException exp){
							throw new ProtocolException(exp.getMessage(),exp);
						}
						if(this.value==null){
							if(valueRexExp==null){
								return true;
							}
							// else devo iterare sulla prossima key
						}
						else{
							if(this.value.equalsIgnoreCase(valueRexExp)){
								return true;
							}
							// else devo iterare sulla prossima key
						}
					}
				}
				
			}
		}
		return false;
	}
}
