/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.abstraction;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.abstraction.constants.TipologiaServizio;
import java.io.Serializable;


/** <p>Java class for DatiServizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiServizio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="endpoint" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipologia-servizio" type="{http://www.openspcoop2.org/protocol/abstraction}TipologiaServizio" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="fruitori" type="{http://www.openspcoop2.org/protocol/abstraction}Fruitori" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiServizio", 
  propOrder = {
  	"endpoint",
  	"tipo",
  	"nome",
  	"tipologiaServizio",
  	"fruitori"
  }
)

@XmlRootElement(name = "DatiServizio")

public class DatiServizio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiServizio() {
    super();
  }

  public java.lang.String getEndpoint() {
    return this.endpoint;
  }

  public void setEndpoint(java.lang.String endpoint) {
    this.endpoint = endpoint;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void setTipologiaServizioRawEnumValue(String value) {
    this.tipologiaServizio = (TipologiaServizio) TipologiaServizio.toEnumConstantFromString(value);
  }

  public String getTipologiaServizioRawEnumValue() {
    if(this.tipologiaServizio == null){
    	return null;
    }else{
    	return this.tipologiaServizio.toString();
    }
  }

  public org.openspcoop2.protocol.abstraction.constants.TipologiaServizio getTipologiaServizio() {
    return this.tipologiaServizio;
  }

  public void setTipologiaServizio(org.openspcoop2.protocol.abstraction.constants.TipologiaServizio tipologiaServizio) {
    this.tipologiaServizio = tipologiaServizio;
  }

  public Fruitori getFruitori() {
    return this.fruitori;
  }

  public void setFruitori(Fruitori fruitori) {
    this.fruitori = fruitori;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="endpoint",required=false,nillable=false)
  protected java.lang.String endpoint;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=false,nillable=false)
  protected java.lang.String tipo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=false,nillable=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipologiaServizioRawEnumValue;

  @XmlElement(name="tipologia-servizio",required=false,nillable=false)
  protected TipologiaServizio tipologiaServizio;

  @XmlElement(name="fruitori",required=false,nillable=false)
  protected Fruitori fruitori;

}
