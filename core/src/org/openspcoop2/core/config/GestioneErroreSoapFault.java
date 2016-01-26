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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;
import java.io.Serializable;


/** <p>Java class for gestione-errore-soap-fault complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gestione-errore-soap-fault">
 * 		&lt;attribute name="fault-actor" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="fault-code" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="fault-string" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="comportamento" type="{http://www.openspcoop2.org/core/config}GestioneErroreComportamento" use="required"/>
 * 		&lt;attribute name="cadenza-rispedizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gestione-errore-soap-fault")

@XmlRootElement(name = "gestione-errore-soap-fault")

public class GestioneErroreSoapFault extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public GestioneErroreSoapFault() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public java.lang.String getFaultActor() {
    return this.faultActor;
  }

  public void setFaultActor(java.lang.String faultActor) {
    this.faultActor = faultActor;
  }

  public java.lang.String getFaultCode() {
    return this.faultCode;
  }

  public void setFaultCode(java.lang.String faultCode) {
    this.faultCode = faultCode;
  }

  public java.lang.String getFaultString() {
    return this.faultString;
  }

  public void setFaultString(java.lang.String faultString) {
    this.faultString = faultString;
  }

  public void set_value_comportamento(String value) {
    this.comportamento = (GestioneErroreComportamento) GestioneErroreComportamento.toEnumConstantFromString(value);
  }

  public String get_value_comportamento() {
    if(this.comportamento == null){
    	return null;
    }else{
    	return this.comportamento.toString();
    }
  }

  public org.openspcoop2.core.config.constants.GestioneErroreComportamento getComportamento() {
    return this.comportamento;
  }

  public void setComportamento(org.openspcoop2.core.config.constants.GestioneErroreComportamento comportamento) {
    this.comportamento = comportamento;
  }

  public java.lang.String getCadenzaRispedizione() {
    return this.cadenzaRispedizione;
  }

  public void setCadenzaRispedizione(java.lang.String cadenzaRispedizione) {
    this.cadenzaRispedizione = cadenzaRispedizione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="fault-actor",required=false)
  protected java.lang.String faultActor;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="fault-code",required=false)
  protected java.lang.String faultCode;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="fault-string",required=false)
  protected java.lang.String faultString;

  @XmlTransient
  protected java.lang.String _value_comportamento;

  @XmlAttribute(name="comportamento",required=true)
  protected GestioneErroreComportamento comportamento;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="cadenza-rispedizione",required=false)
  protected java.lang.String cadenzaRispedizione;

}
