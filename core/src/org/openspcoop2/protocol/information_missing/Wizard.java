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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for Wizard complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Wizard"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="requisiti" type="{http://www.openspcoop2.org/protocol/information_missing}Requisiti" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="step" type="{http://www.w3.org/2001/XMLSchema}int" use="optional"/&gt;
 * 		&lt;attribute name="step-in-delete" type="{http://www.w3.org/2001/XMLSchema}int" use="optional"/&gt;
 * 		&lt;attribute name="intestazione-originale" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Wizard", 
  propOrder = {
  	"requisiti"
  }
)

@XmlRootElement(name = "Wizard")

public class Wizard extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Wizard() {
  }

  public Requisiti getRequisiti() {
    return this.requisiti;
  }

  public void setRequisiti(Requisiti requisiti) {
    this.requisiti = requisiti;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public int getStep() {
    return this.step;
  }

  public void setStep(int step) {
    this.step = step;
  }

  public int getStepInDelete() {
    return this.stepInDelete;
  }

  public void setStepInDelete(int stepInDelete) {
    this.stepInDelete = stepInDelete;
  }

  public boolean isIntestazioneOriginale() {
    return this.intestazioneOriginale;
  }

  public boolean getIntestazioneOriginale() {
    return this.intestazioneOriginale;
  }

  public void setIntestazioneOriginale(boolean intestazioneOriginale) {
    this.intestazioneOriginale = intestazioneOriginale;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="requisiti",required=false,nillable=false)
  protected Requisiti requisiti;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=true)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlAttribute(name="step",required=false)
  protected int step;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlAttribute(name="step-in-delete",required=false)
  protected int stepInDelete;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="intestazione-originale",required=false)
  protected boolean intestazioneOriginale;

}
