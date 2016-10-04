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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for invocazione-porta-gestione-errore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-porta-gestione-errore">
 * 		&lt;attribute name="fault" type="{http://www.openspcoop2.org/core/config}FaultIntegrazioneTipo" use="optional" default="soap"/>
 * 		&lt;attribute name="fault-actor" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="generic-fault-code" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/>
 * 		&lt;attribute name="prefix-fault-code" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invocazione-porta-gestione-errore")

@XmlRootElement(name = "invocazione-porta-gestione-errore")

public class InvocazionePortaGestioneErrore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public InvocazionePortaGestioneErrore() {
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

  public void set_value_fault(String value) {
    this.fault = (FaultIntegrazioneTipo) FaultIntegrazioneTipo.toEnumConstantFromString(value);
  }

  public String get_value_fault() {
    if(this.fault == null){
    	return null;
    }else{
    	return this.fault.toString();
    }
  }

  public org.openspcoop2.core.config.constants.FaultIntegrazioneTipo getFault() {
    return this.fault;
  }

  public void setFault(org.openspcoop2.core.config.constants.FaultIntegrazioneTipo fault) {
    this.fault = fault;
  }

  public java.lang.String getFaultActor() {
    return this.faultActor;
  }

  public void setFaultActor(java.lang.String faultActor) {
    this.faultActor = faultActor;
  }

  public void set_value_genericFaultCode(String value) {
    this.genericFaultCode = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_genericFaultCode() {
    if(this.genericFaultCode == null){
    	return null;
    }else{
    	return this.genericFaultCode.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getGenericFaultCode() {
    return this.genericFaultCode;
  }

  public void setGenericFaultCode(org.openspcoop2.core.config.constants.StatoFunzionalita genericFaultCode) {
    this.genericFaultCode = genericFaultCode;
  }

  public java.lang.String getPrefixFaultCode() {
    return this.prefixFaultCode;
  }

  public void setPrefixFaultCode(java.lang.String prefixFaultCode) {
    this.prefixFaultCode = prefixFaultCode;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlTransient
  protected java.lang.String _value_fault;

  @XmlAttribute(name="fault",required=false)
  protected FaultIntegrazioneTipo fault = (FaultIntegrazioneTipo) FaultIntegrazioneTipo.toEnumConstantFromString("soap");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="fault-actor",required=false)
  protected java.lang.String faultActor;

  @XmlTransient
  protected java.lang.String _value_genericFaultCode;

  @XmlAttribute(name="generic-fault-code",required=false)
  protected StatoFunzionalita genericFaultCode;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="prefix-fault-code",required=false)
  protected java.lang.String prefixFaultCode;

}
