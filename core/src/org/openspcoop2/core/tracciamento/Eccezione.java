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
package org.openspcoop2.core.tracciamento;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for eccezione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eccezione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="codice" type="{http://www.openspcoop2.org/core/tracciamento}CodiceEccezione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="contesto-codifica" type="{http://www.openspcoop2.org/core/tracciamento}ContestoCodificaEccezione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="rilevanza" type="{http://www.openspcoop2.org/core/tracciamento}RilevanzaEccezione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="modulo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "eccezione", 
  propOrder = {
  	"codice",
  	"contestoCodifica",
  	"descrizione",
  	"rilevanza",
  	"modulo"
  }
)

@XmlRootElement(name = "eccezione")

public class Eccezione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Eccezione() {
    super();
  }

  public CodiceEccezione getCodice() {
    return this.codice;
  }

  public void setCodice(CodiceEccezione codice) {
    this.codice = codice;
  }

  public ContestoCodificaEccezione getContestoCodifica() {
    return this.contestoCodifica;
  }

  public void setContestoCodifica(ContestoCodificaEccezione contestoCodifica) {
    this.contestoCodifica = contestoCodifica;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public RilevanzaEccezione getRilevanza() {
    return this.rilevanza;
  }

  public void setRilevanza(RilevanzaEccezione rilevanza) {
    this.rilevanza = rilevanza;
  }

  public java.lang.String getModulo() {
    return this.modulo;
  }

  public void setModulo(java.lang.String modulo) {
    this.modulo = modulo;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="codice",required=false,nillable=false)
  protected CodiceEccezione codice;

  @XmlElement(name="contesto-codifica",required=false,nillable=false)
  protected ContestoCodificaEccezione contestoCodifica;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=false,nillable=false)
  protected java.lang.String descrizione;

  @XmlElement(name="rilevanza",required=false,nillable=false)
  protected RilevanzaEccezione rilevanza;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="modulo",required=false,nillable=false)
  protected java.lang.String modulo;

}
