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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.tracciamento.constants.TipoEsitoElaborazione;
import java.io.Serializable;


/** <p>Java class for traccia-esito-elaborazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="traccia-esito-elaborazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="dettaglio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/tracciamento}TipoEsitoElaborazione" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "traccia-esito-elaborazione", 
  propOrder = {
  	"dettaglio"
  }
)

@XmlRootElement(name = "traccia-esito-elaborazione")

public class TracciaEsitoElaborazione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TracciaEsitoElaborazione() {
    super();
  }

  public java.lang.String getDettaglio() {
    return this.dettaglio;
  }

  public void setDettaglio(java.lang.String dettaglio) {
    this.dettaglio = dettaglio;
  }

  public void setTipoRawEnumValue(String value) {
    this.tipo = (TipoEsitoElaborazione) TipoEsitoElaborazione.toEnumConstantFromString(value);
  }

  public String getTipoRawEnumValue() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.tracciamento.constants.TipoEsitoElaborazione getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.tracciamento.constants.TipoEsitoElaborazione tipo) {
    this.tipo = tipo;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="dettaglio",required=false,nillable=false)
  protected java.lang.String dettaglio;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoRawEnumValue;

  @XmlAttribute(name="tipo",required=true)
  protected TipoEsitoElaborazione tipo;

}
