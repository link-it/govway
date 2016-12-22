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
package it.cnipa.schemas._2003.egovit.exception1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for Eccezione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Eccezione">
 * 		&lt;sequence>
 * 			&lt;element name="EccezioneBusta" type="{http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/}EccezioneBusta" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="EccezioneProcessamento" type="{http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/}EccezioneProcessamento" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "Eccezione", 
  propOrder = {
  	"eccezioneBusta",
  	"eccezioneProcessamento"
  }
)

@XmlRootElement(name = "Eccezione")

public class Eccezione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Eccezione() {
  }

  public EccezioneBusta getEccezioneBusta() {
    return this.eccezioneBusta;
  }

  public void setEccezioneBusta(EccezioneBusta eccezioneBusta) {
    this.eccezioneBusta = eccezioneBusta;
  }

  public EccezioneProcessamento getEccezioneProcessamento() {
    return this.eccezioneProcessamento;
  }

  public void setEccezioneProcessamento(EccezioneProcessamento eccezioneProcessamento) {
    this.eccezioneProcessamento = eccezioneProcessamento;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="EccezioneBusta",required=false,nillable=false)
  protected EccezioneBusta eccezioneBusta;

  @XmlElement(name="EccezioneProcessamento",required=false,nillable=false)
  protected EccezioneProcessamento eccezioneProcessamento;

}
