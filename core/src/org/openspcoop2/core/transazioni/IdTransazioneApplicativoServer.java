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
package org.openspcoop2.core.transazioni;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for id-transazione-applicativo-server complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-transazione-applicativo-server"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="connettore-nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "id-transazione-applicativo-server", 
  propOrder = {
  	"idTransazione",
  	"servizioApplicativoErogatore",
  	"connettoreNome"
  }
)

@XmlRootElement(name = "id-transazione-applicativo-server")

public class IdTransazioneApplicativoServer extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public IdTransazioneApplicativoServer() {
    super();
  }

  public java.lang.String getIdTransazione() {
    return this.idTransazione;
  }

  public void setIdTransazione(java.lang.String idTransazione) {
    this.idTransazione = idTransazione;
  }

  public java.lang.String getServizioApplicativoErogatore() {
    return this.servizioApplicativoErogatore;
  }

  public void setServizioApplicativoErogatore(java.lang.String servizioApplicativoErogatore) {
    this.servizioApplicativoErogatore = servizioApplicativoErogatore;
  }

  public java.lang.String getConnettoreNome() {
    return this.connettoreNome;
  }

  public void setConnettoreNome(java.lang.String connettoreNome) {
    this.connettoreNome = connettoreNome;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-transazione",required=true,nillable=false)
  protected java.lang.String idTransazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-erogatore",required=true,nillable=false)
  protected java.lang.String servizioApplicativoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="connettore-nome",required=false,nillable=false)
  protected java.lang.String connettoreNome;

}
