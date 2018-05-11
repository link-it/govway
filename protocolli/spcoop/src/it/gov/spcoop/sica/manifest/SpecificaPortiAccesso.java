/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package it.gov.spcoop.sica.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for SpecificaPortiAccesso complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificaPortiAccesso">
 * 		&lt;sequence>
 * 			&lt;element name="portiAccessoFruitore" type="{http://spcoop.gov.it/sica/manifest}DocumentoInterfaccia" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="portiAccessoErogatore" type="{http://spcoop.gov.it/sica/manifest}DocumentoInterfaccia" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpecificaPortiAccesso", 
  propOrder = {
  	"portiAccessoFruitore",
  	"portiAccessoErogatore"
  }
)

@XmlRootElement(name = "SpecificaPortiAccesso")

public class SpecificaPortiAccesso extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SpecificaPortiAccesso() {
  }

  public DocumentoInterfaccia getPortiAccessoFruitore() {
    return this.portiAccessoFruitore;
  }

  public void setPortiAccessoFruitore(DocumentoInterfaccia portiAccessoFruitore) {
    this.portiAccessoFruitore = portiAccessoFruitore;
  }

  public DocumentoInterfaccia getPortiAccessoErogatore() {
    return this.portiAccessoErogatore;
  }

  public void setPortiAccessoErogatore(DocumentoInterfaccia portiAccessoErogatore) {
    this.portiAccessoErogatore = portiAccessoErogatore;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="portiAccessoFruitore",required=false,nillable=false)
  protected DocumentoInterfaccia portiAccessoFruitore;

  @XmlElement(name="portiAccessoErogatore",required=false,nillable=false)
  protected DocumentoInterfaccia portiAccessoErogatore;

}
