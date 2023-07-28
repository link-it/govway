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
package org.openspcoop2.core.transazioni;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for transazione-esiti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transazione-esiti"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="govway_status" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="govway_status_key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="govway_status_detail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="govway_status_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="govway_status_class" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transazione-esiti", 
  propOrder = {
  	"govwayStatus",
  	"govwayStatusKey",
  	"govwayStatusDetail",
  	"govwayStatusDescription",
  	"govwayStatusClass"
  }
)

@XmlRootElement(name = "transazione-esiti")

public class TransazioneEsiti extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TransazioneEsiti() {
    super();
  }

  public int getGovwayStatus() {
    return this.govwayStatus;
  }

  public void setGovwayStatus(int govwayStatus) {
    this.govwayStatus = govwayStatus;
  }

  public java.lang.String getGovwayStatusKey() {
    return this.govwayStatusKey;
  }

  public void setGovwayStatusKey(java.lang.String govwayStatusKey) {
    this.govwayStatusKey = govwayStatusKey;
  }

  public java.lang.String getGovwayStatusDetail() {
    return this.govwayStatusDetail;
  }

  public void setGovwayStatusDetail(java.lang.String govwayStatusDetail) {
    this.govwayStatusDetail = govwayStatusDetail;
  }

  public java.lang.String getGovwayStatusDescription() {
    return this.govwayStatusDescription;
  }

  public void setGovwayStatusDescription(java.lang.String govwayStatusDescription) {
    this.govwayStatusDescription = govwayStatusDescription;
  }

  public int getGovwayStatusClass() {
    return this.govwayStatusClass;
  }

  public void setGovwayStatusClass(int govwayStatusClass) {
    this.govwayStatusClass = govwayStatusClass;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="govway_status",required=true,nillable=false)
  protected int govwayStatus;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="govway_status_key",required=true,nillable=false)
  protected java.lang.String govwayStatusKey;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="govway_status_detail",required=true,nillable=false)
  protected java.lang.String govwayStatusDetail;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="govway_status_description",required=true,nillable=false)
  protected java.lang.String govwayStatusDescription;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="govway_status_class",required=true,nillable=false)
  protected int govwayStatusClass;

}
