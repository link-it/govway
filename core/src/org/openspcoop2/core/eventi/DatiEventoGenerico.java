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
package org.openspcoop2.core.eventi;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for dati-evento-generico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dati-evento-generico"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-evento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dati-consumati-thread" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="codice-evento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "dati-evento-generico", 
  propOrder = {
  	"idEvento",
  	"data",
  	"descrizione",
  	"datiConsumatiThread",
  	"codiceEvento"
  }
)

@XmlRootElement(name = "dati-evento-generico")

public class DatiEventoGenerico extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public DatiEventoGenerico() {
    super();
  }

  public java.lang.String getIdEvento() {
    return this.idEvento;
  }

  public void setIdEvento(java.lang.String idEvento) {
    this.idEvento = idEvento;
  }

  public java.util.Date getData() {
    return this.data;
  }

  public void setData(java.util.Date data) {
    this.data = data;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public boolean isDatiConsumatiThread() {
    return this.datiConsumatiThread;
  }

  public boolean getDatiConsumatiThread() {
    return this.datiConsumatiThread;
  }

  public void setDatiConsumatiThread(boolean datiConsumatiThread) {
    this.datiConsumatiThread = datiConsumatiThread;
  }

  public java.lang.String getCodiceEvento() {
    return this.codiceEvento;
  }

  public void setCodiceEvento(java.lang.String codiceEvento) {
    this.codiceEvento = codiceEvento;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-evento",required=false,nillable=false)
  protected java.lang.String idEvento;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date data;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=false,nillable=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="dati-consumati-thread",required=false,nillable=false)
  protected boolean datiConsumatiThread;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice-evento",required=false,nillable=false)
  protected java.lang.String codiceEvento;

}
