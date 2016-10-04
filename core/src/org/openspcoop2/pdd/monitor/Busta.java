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
package org.openspcoop2.pdd.monitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for busta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="busta">
 * 		&lt;sequence>
 * 			&lt;element name="attesa-riscontro" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="false"/>
 * 			&lt;element name="mittente" type="{http://www.openspcoop2.org/pdd/monitor}busta-soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="destinatario" type="{http://www.openspcoop2.org/pdd/monitor}busta-soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio" type="{http://www.openspcoop2.org/pdd/monitor}busta-servizio" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="profilo-collaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="collaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="riferimento-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "busta", 
  propOrder = {
  	"attesaRiscontro",
  	"mittente",
  	"destinatario",
  	"servizio",
  	"azione",
  	"profiloCollaborazione",
  	"collaborazione",
  	"riferimentoMessaggio"
  }
)

@XmlRootElement(name = "busta")

public class Busta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Busta() {
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

  public boolean isAttesaRiscontro() {
    return this.attesaRiscontro;
  }

  public boolean getAttesaRiscontro() {
    return this.attesaRiscontro;
  }

  public void setAttesaRiscontro(boolean attesaRiscontro) {
    this.attesaRiscontro = attesaRiscontro;
  }

  public BustaSoggetto getMittente() {
    return this.mittente;
  }

  public void setMittente(BustaSoggetto mittente) {
    this.mittente = mittente;
  }

  public BustaSoggetto getDestinatario() {
    return this.destinatario;
  }

  public void setDestinatario(BustaSoggetto destinatario) {
    this.destinatario = destinatario;
  }

  public BustaServizio getServizio() {
    return this.servizio;
  }

  public void setServizio(BustaServizio servizio) {
    this.servizio = servizio;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  public java.lang.String getProfiloCollaborazione() {
    return this.profiloCollaborazione;
  }

  public void setProfiloCollaborazione(java.lang.String profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
  }

  public java.lang.String getCollaborazione() {
    return this.collaborazione;
  }

  public void setCollaborazione(java.lang.String collaborazione) {
    this.collaborazione = collaborazione;
  }

  public java.lang.String getRiferimentoMessaggio() {
    return this.riferimentoMessaggio;
  }

  public void setRiferimentoMessaggio(java.lang.String riferimentoMessaggio) {
    this.riferimentoMessaggio = riferimentoMessaggio;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="attesa-riscontro",required=false,nillable=false,defaultValue="false")
  protected boolean attesaRiscontro = false;

  @XmlElement(name="mittente",required=false,nillable=false)
  protected BustaSoggetto mittente;

  @XmlElement(name="destinatario",required=false,nillable=false)
  protected BustaSoggetto destinatario;

  @XmlElement(name="servizio",required=false,nillable=false)
  protected BustaServizio servizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=false,nillable=false)
  protected java.lang.String azione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="profilo-collaborazione",required=false,nillable=false)
  protected java.lang.String profiloCollaborazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="collaborazione",required=false,nillable=false)
  protected java.lang.String collaborazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="riferimento-messaggio",required=false,nillable=false)
  protected java.lang.String riferimentoMessaggio;

}
