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
package it.gov.spcoop.sica.wsbl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for temporalConditionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="temporalConditionType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="predicate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="boolop" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="data" type="{http://spcoop.gov.it/sica/wsbl}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "temporalConditionType", 
  propOrder = {
  	"predicate",
  	"boolop",
  	"data",
  	"description"
  }
)

@XmlRootElement(name = "temporalConditionType")

public class TemporalConditionType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TemporalConditionType() {
  }

  public java.lang.String getPredicate() {
    return this.predicate;
  }

  public void setPredicate(java.lang.String predicate) {
    this.predicate = predicate;
  }

  public java.lang.String getBoolop() {
    return this.boolop;
  }

  public void setBoolop(java.lang.String boolop) {
    this.boolop = boolop;
  }

  public java.lang.String getData() {
    return this.data;
  }

  public void setData(java.lang.String data) {
    this.data = data;
  }

  public java.lang.String getDescription() {
    return this.description;
  }

  public void setDescription(java.lang.String description) {
    this.description = description;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="predicate",required=true,nillable=false)
  protected java.lang.String predicate;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="boolop",required=true,nillable=false)
  protected java.lang.String boolop;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="data",required=true,nillable=false)
  protected java.lang.String data;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="description",required=false,nillable=false)
  protected java.lang.String description;

}
