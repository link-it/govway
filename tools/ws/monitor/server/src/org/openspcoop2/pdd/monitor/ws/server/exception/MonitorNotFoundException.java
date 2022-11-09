/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.monitor.ws.server.exception;

/**
 * <p>Java class for MonitorNotFoundException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="monitor-not-found-exception"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="methodName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="1" /&gt;
 *         &lt;element name="objectId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="errorMessage" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="1" /&gt;
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="errorStackTrace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;

/**     
 * MonitorNotFoundException
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "monitor-not-found-exception", namespace="http://www.openspcoop2.org/pdd/monitor/management", propOrder = {
    "methodName",
    "objectId",
    "errorMessage",
    "errorCode",
    "errorStackTrace"
})
public class MonitorNotFoundException extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="methodName",required=true,nillable=false)
	private String methodName;
	
	public void setMethodName(String methodName){
		this.methodName = methodName;
	}
	
	public String getMethodName(){
		return this.methodName;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="objectId",required=false,nillable=false)
	private String objectId;
	
	public void setObjectId(String objectId){
		this.objectId = objectId;
	}
	
	public String getObjectId(){
		return this.objectId;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="errorMessage",required=true,nillable=false)
	private String errorMessage;
	
	public void setErrorMessage(String errorMessage){
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="errorCode",required=false,nillable=false)
	private String errorCode;
	
	public void setErrorCode(String errorCode){
		this.errorCode = errorCode;
	}
	
	public String getErrorCode(){
		return this.errorCode;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="errorStackTrace",required=false,nillable=false)
	private String errorStackTrace;
	
	public void setErrorStackTrace(String errorStackTrace){
		this.errorStackTrace = errorStackTrace;
	}
	
	public String getErrorStackTrace(){
		return this.errorStackTrace;
	}
	
	
	
	
	public void setObjectId(Object id){
		try{
			if(id==null){
				return;
			}
			java.lang.reflect.Method m = id.getClass().getMethod("toJson");
			String json = (String) m.invoke(id);
			this.objectId = json;
		}catch(Exception e){
			// ignore
		}
	}	
}