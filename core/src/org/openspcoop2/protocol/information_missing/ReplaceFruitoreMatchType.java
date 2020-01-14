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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for replaceFruitoreMatchType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replaceFruitoreMatchType">
 * 		&lt;sequence>
 * 			&lt;element name="nome" type="{http://www.openspcoop2.org/protocol/information_missing}ReplaceMatchFieldType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tipo" type="{http://www.openspcoop2.org/protocol/information_missing}ReplaceMatchFieldType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="nome-servizio" type="{http://www.openspcoop2.org/protocol/information_missing}ReplaceMatchFieldType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tipo-servizio" type="{http://www.openspcoop2.org/protocol/information_missing}ReplaceMatchFieldType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="nome-erogatore" type="{http://www.openspcoop2.org/protocol/information_missing}ReplaceMatchFieldType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tipo-erogatore" type="{http://www.openspcoop2.org/protocol/information_missing}ReplaceMatchFieldType" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "replaceFruitoreMatchType", 
  propOrder = {
  	"nome",
  	"tipo",
  	"nomeServizio",
  	"tipoServizio",
  	"nomeErogatore",
  	"tipoErogatore"
  }
)

@XmlRootElement(name = "replaceFruitoreMatchType")

public class ReplaceFruitoreMatchType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ReplaceFruitoreMatchType() {
  }

  public ReplaceMatchFieldType getNome() {
    return this.nome;
  }

  public void setNome(ReplaceMatchFieldType nome) {
    this.nome = nome;
  }

  public ReplaceMatchFieldType getTipo() {
    return this.tipo;
  }

  public void setTipo(ReplaceMatchFieldType tipo) {
    this.tipo = tipo;
  }

  public ReplaceMatchFieldType getNomeServizio() {
    return this.nomeServizio;
  }

  public void setNomeServizio(ReplaceMatchFieldType nomeServizio) {
    this.nomeServizio = nomeServizio;
  }

  public ReplaceMatchFieldType getTipoServizio() {
    return this.tipoServizio;
  }

  public void setTipoServizio(ReplaceMatchFieldType tipoServizio) {
    this.tipoServizio = tipoServizio;
  }

  public ReplaceMatchFieldType getNomeErogatore() {
    return this.nomeErogatore;
  }

  public void setNomeErogatore(ReplaceMatchFieldType nomeErogatore) {
    this.nomeErogatore = nomeErogatore;
  }

  public ReplaceMatchFieldType getTipoErogatore() {
    return this.tipoErogatore;
  }

  public void setTipoErogatore(ReplaceMatchFieldType tipoErogatore) {
    this.tipoErogatore = tipoErogatore;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="nome",required=false,nillable=false)
  protected ReplaceMatchFieldType nome;

  @XmlElement(name="tipo",required=false,nillable=false)
  protected ReplaceMatchFieldType tipo;

  @XmlElement(name="nome-servizio",required=false,nillable=false)
  protected ReplaceMatchFieldType nomeServizio;

  @XmlElement(name="tipo-servizio",required=false,nillable=false)
  protected ReplaceMatchFieldType tipoServizio;

  @XmlElement(name="nome-erogatore",required=false,nillable=false)
  protected ReplaceMatchFieldType nomeErogatore;

  @XmlElement(name="tipo-erogatore",required=false,nillable=false)
  protected ReplaceMatchFieldType tipoErogatore;

}
