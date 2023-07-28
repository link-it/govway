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
package org.openspcoop2.core.tracciamento;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for data complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="data"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="date-time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="sorgente" type="{http://www.openspcoop2.org/core/tracciamento}TipoData" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "data", 
  propOrder = {
  	"dateTime",
  	"sorgente"
  }
)

@XmlRootElement(name = "data")

public class Data extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Data() {
    super();
  }

  public java.util.Date getDateTime() {
    return this.dateTime;
  }

  public void setDateTime(java.util.Date dateTime) {
    this.dateTime = dateTime;
  }

  public TipoData getSorgente() {
    return this.sorgente;
  }

  public void setSorgente(TipoData sorgente) {
    this.sorgente = sorgente;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="date-time",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dateTime;

  @XmlElement(name="sorgente",required=false,nillable=false)
  protected TipoData sorgente;

}
