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
package org.openspcoop2.core.controllo_traffico;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for tempi-risposta-erogazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tempi-risposta-erogazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="connection-timeout" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="read-timeout" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tempo-medio-risposta" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "tempi-risposta-erogazione", 
  propOrder = {
  	"connectionTimeout",
  	"readTimeout",
  	"tempoMedioRisposta"
  }
)

@XmlRootElement(name = "tempi-risposta-erogazione")

public class TempiRispostaErogazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TempiRispostaErogazione() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public java.lang.Integer getConnectionTimeout() {
    return this.connectionTimeout;
  }

  public void setConnectionTimeout(java.lang.Integer connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public java.lang.Integer getReadTimeout() {
    return this.readTimeout;
  }

  public void setReadTimeout(java.lang.Integer readTimeout) {
    this.readTimeout = readTimeout;
  }

  public java.lang.Integer getTempoMedioRisposta() {
    return this.tempoMedioRisposta;
  }

  public void setTempoMedioRisposta(java.lang.Integer tempoMedioRisposta) {
    this.tempoMedioRisposta = tempoMedioRisposta;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="connection-timeout",required=false,nillable=false)
  protected java.lang.Integer connectionTimeout;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="read-timeout",required=false,nillable=false)
  protected java.lang.Integer readTimeout;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="tempo-medio-risposta",required=false,nillable=false)
  protected java.lang.Integer tempoMedioRisposta;

}
