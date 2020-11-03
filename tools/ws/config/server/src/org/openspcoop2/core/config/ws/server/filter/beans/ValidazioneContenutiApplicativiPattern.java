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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for ValidazioneContenutiApplicativiPattern complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi-pattern"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="and" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="not" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;

/**     
 * ValidazioneContenutiApplicativiPattern
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "validazione-contenuti-applicativi-pattern", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "and",
    "not"
})
@javax.xml.bind.annotation.XmlRootElement(name = "validazione-contenuti-applicativi-pattern")
public class ValidazioneContenutiApplicativiPattern extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="and",required=false,nillable=false)
	private Boolean and;
	
	public void setAnd(Boolean and){
		this.and = and;
	}
	
	public Boolean getAnd(){
		return this.and;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="not",required=false,nillable=false)
	private Boolean not;
	
	public void setNot(Boolean not){
		this.not = not;
	}
	
	public Boolean getNot(){
		return this.not;
	}
	
	
	
	
}