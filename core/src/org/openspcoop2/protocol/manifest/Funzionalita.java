/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for funzionalita complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="funzionalita">
 * 		&lt;attribute name="filtroDuplicati" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="confermaRicezione" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="collaborazione" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="consegnaInOrdine" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="scadenza" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="manifestAttachments" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "funzionalita")

@XmlRootElement(name = "funzionalita")

public class Funzionalita extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Funzionalita() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public boolean isFiltroDuplicati() {
    return this.filtroDuplicati;
  }

  public boolean getFiltroDuplicati() {
    return this.filtroDuplicati;
  }

  public void setFiltroDuplicati(boolean filtroDuplicati) {
    this.filtroDuplicati = filtroDuplicati;
  }

  public boolean isConfermaRicezione() {
    return this.confermaRicezione;
  }

  public boolean getConfermaRicezione() {
    return this.confermaRicezione;
  }

  public void setConfermaRicezione(boolean confermaRicezione) {
    this.confermaRicezione = confermaRicezione;
  }

  public boolean isCollaborazione() {
    return this.collaborazione;
  }

  public boolean getCollaborazione() {
    return this.collaborazione;
  }

  public void setCollaborazione(boolean collaborazione) {
    this.collaborazione = collaborazione;
  }

  public boolean isConsegnaInOrdine() {
    return this.consegnaInOrdine;
  }

  public boolean getConsegnaInOrdine() {
    return this.consegnaInOrdine;
  }

  public void setConsegnaInOrdine(boolean consegnaInOrdine) {
    this.consegnaInOrdine = consegnaInOrdine;
  }

  public boolean isScadenza() {
    return this.scadenza;
  }

  public boolean getScadenza() {
    return this.scadenza;
  }

  public void setScadenza(boolean scadenza) {
    this.scadenza = scadenza;
  }

  public boolean isManifestAttachments() {
    return this.manifestAttachments;
  }

  public boolean getManifestAttachments() {
    return this.manifestAttachments;
  }

  public void setManifestAttachments(boolean manifestAttachments) {
    this.manifestAttachments = manifestAttachments;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="filtroDuplicati",required=false)
  protected boolean filtroDuplicati = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="confermaRicezione",required=false)
  protected boolean confermaRicezione = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="collaborazione",required=false)
  protected boolean collaborazione = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="consegnaInOrdine",required=false)
  protected boolean consegnaInOrdine = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="scadenza",required=false)
  protected boolean scadenza = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="manifestAttachments",required=false)
  protected boolean manifestAttachments = false;

}
