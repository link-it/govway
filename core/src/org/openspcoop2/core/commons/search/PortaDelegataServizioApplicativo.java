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
package org.openspcoop2.core.commons.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for porta-delegata-servizio-applicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-delegata-servizio-applicativo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-servizio-applicativo" type="{http://www.openspcoop2.org/core/commons/search}id-servizio-applicativo" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "porta-delegata-servizio-applicativo", 
  propOrder = {
  	"idServizioApplicativo"
  }
)

@XmlRootElement(name = "porta-delegata-servizio-applicativo")

public class PortaDelegataServizioApplicativo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortaDelegataServizioApplicativo() {
    super();
  }

  public IdServizioApplicativo getIdServizioApplicativo() {
    return this.idServizioApplicativo;
  }

  public void setIdServizioApplicativo(IdServizioApplicativo idServizioApplicativo) {
    this.idServizioApplicativo = idServizioApplicativo;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="id-servizio-applicativo",required=true,nillable=false)
  protected IdServizioApplicativo idServizioApplicativo;

}
