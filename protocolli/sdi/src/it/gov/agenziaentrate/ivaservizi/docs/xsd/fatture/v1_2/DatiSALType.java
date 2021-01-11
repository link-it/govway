/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiSALType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiSALType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="RiferimentoFase" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}integer" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiSALType", 
  propOrder = {
  	"riferimentoFase"
  }
)

@XmlRootElement(name = "DatiSALType")

public class DatiSALType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiSALType() {
  }

  public java.math.BigInteger getRiferimentoFase() {
    return this.riferimentoFase;
  }

  public void setRiferimentoFase(java.math.BigInteger riferimentoFase) {
    this.riferimentoFase = riferimentoFase;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="RiferimentoFase",required=true,nillable=false)
  protected java.math.BigInteger riferimentoFase;

}
