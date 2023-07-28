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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;
import java.io.Serializable;


/** <p>Java class for gestione-errore-soap-fault complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gestione-errore-soap-fault"&gt;
 * 		&lt;attribute name="fault-actor" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="fault-code" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="fault-string" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="comportamento" type="{http://www.openspcoop2.org/core/config}GestioneErroreComportamento" use="required"/&gt;
 * 		&lt;attribute name="cadenza-rispedizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
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

public class GestioneErroreSoapFault extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public GestioneErroreSoapFault() {
    super();
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

  public void setComportamentoRawEnumValue(String value) {
    this.comportamento = (GestioneErroreComportamento) GestioneErroreComportamento.toEnumConstantFromString(value);
  }

  public String getComportamentoRawEnumValue() {
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



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="fault-actor",required=false)
  protected java.lang.String faultActor;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="fault-code",required=false)
  protected java.lang.String faultCode;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="fault-string",required=false)
  protected java.lang.String faultString;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String comportamentoRawEnumValue;

  @XmlAttribute(name="comportamento",required=true)
  protected GestioneErroreComportamento comportamento;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="cadenza-rispedizione",required=false)
  protected java.lang.String cadenzaRispedizione;

}
