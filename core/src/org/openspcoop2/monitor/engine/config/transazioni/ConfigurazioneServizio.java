/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config.transazioni;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-servizio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="MAPPING" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-servizio", 
  propOrder = {
  	"mapping"
  }
)

@XmlRootElement(name = "configurazione-servizio")

public class ConfigurazioneServizio extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneServizio() {
    super();
  }

  public java.lang.String getMapping() {
    return this.mapping;
  }

  public void setMapping(java.lang.String mapping) {
    this.mapping = mapping;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="MAPPING",required=true,nillable=false)
  protected java.lang.String mapping;

}
