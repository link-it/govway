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
package org.openspcoop2.core.tracciamento;

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
 * 			&lt;element name="mittente" type="{http://www.openspcoop2.org/core/tracciamento}soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="destinatario" type="{http://www.openspcoop2.org/core/tracciamento}soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="profilo-collaborazione" type="{http://www.openspcoop2.org/core/tracciamento}profilo-collaborazione" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio" type="{http://www.openspcoop2.org/core/tracciamento}servizio" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio-correlato" type="{http://www.openspcoop2.org/core/tracciamento}servizio" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="collaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="identificativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="riferimento-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ora-registrazione" type="{http://www.openspcoop2.org/core/tracciamento}data" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="scadenza" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="profilo-trasmissione" type="{http://www.openspcoop2.org/core/tracciamento}profilo-trasmissione" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio-applicativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="digest" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="trasmissioni" type="{http://www.openspcoop2.org/core/tracciamento}trasmissioni" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="riscontri" type="{http://www.openspcoop2.org/core/tracciamento}riscontri" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="eccezioni" type="{http://www.openspcoop2.org/core/tracciamento}eccezioni" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="protocollo" type="{http://www.openspcoop2.org/core/tracciamento}protocollo" minOccurs="1" maxOccurs="1"/>
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
  	"mittente",
  	"destinatario",
  	"profiloCollaborazione",
  	"servizio",
  	"azione",
  	"servizioCorrelato",
  	"collaborazione",
  	"identificativo",
  	"riferimentoMessaggio",
  	"oraRegistrazione",
  	"scadenza",
  	"profiloTrasmissione",
  	"servizioApplicativoFruitore",
  	"servizioApplicativoErogatore",
  	"digest",
  	"trasmissioni",
  	"riscontri",
  	"eccezioni",
  	"protocollo"
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

  public Soggetto getMittente() {
    return this.mittente;
  }

  public void setMittente(Soggetto mittente) {
    this.mittente = mittente;
  }

  public Soggetto getDestinatario() {
    return this.destinatario;
  }

  public void setDestinatario(Soggetto destinatario) {
    this.destinatario = destinatario;
  }

  public ProfiloCollaborazione getProfiloCollaborazione() {
    return this.profiloCollaborazione;
  }

  public void setProfiloCollaborazione(ProfiloCollaborazione profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
  }

  public Servizio getServizio() {
    return this.servizio;
  }

  public void setServizio(Servizio servizio) {
    this.servizio = servizio;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  public Servizio getServizioCorrelato() {
    return this.servizioCorrelato;
  }

  public void setServizioCorrelato(Servizio servizioCorrelato) {
    this.servizioCorrelato = servizioCorrelato;
  }

  public java.lang.String getCollaborazione() {
    return this.collaborazione;
  }

  public void setCollaborazione(java.lang.String collaborazione) {
    this.collaborazione = collaborazione;
  }

  public java.lang.String getIdentificativo() {
    return this.identificativo;
  }

  public void setIdentificativo(java.lang.String identificativo) {
    this.identificativo = identificativo;
  }

  public java.lang.String getRiferimentoMessaggio() {
    return this.riferimentoMessaggio;
  }

  public void setRiferimentoMessaggio(java.lang.String riferimentoMessaggio) {
    this.riferimentoMessaggio = riferimentoMessaggio;
  }

  public Data getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(Data oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.util.Date getScadenza() {
    return this.scadenza;
  }

  public void setScadenza(java.util.Date scadenza) {
    this.scadenza = scadenza;
  }

  public ProfiloTrasmissione getProfiloTrasmissione() {
    return this.profiloTrasmissione;
  }

  public void setProfiloTrasmissione(ProfiloTrasmissione profiloTrasmissione) {
    this.profiloTrasmissione = profiloTrasmissione;
  }

  public java.lang.String getServizioApplicativoFruitore() {
    return this.servizioApplicativoFruitore;
  }

  public void setServizioApplicativoFruitore(java.lang.String servizioApplicativoFruitore) {
    this.servizioApplicativoFruitore = servizioApplicativoFruitore;
  }

  public java.lang.String getServizioApplicativoErogatore() {
    return this.servizioApplicativoErogatore;
  }

  public void setServizioApplicativoErogatore(java.lang.String servizioApplicativoErogatore) {
    this.servizioApplicativoErogatore = servizioApplicativoErogatore;
  }

  public java.lang.String getDigest() {
    return this.digest;
  }

  public void setDigest(java.lang.String digest) {
    this.digest = digest;
  }

  public Trasmissioni getTrasmissioni() {
    return this.trasmissioni;
  }

  public void setTrasmissioni(Trasmissioni trasmissioni) {
    this.trasmissioni = trasmissioni;
  }

  public Riscontri getRiscontri() {
    return this.riscontri;
  }

  public void setRiscontri(Riscontri riscontri) {
    this.riscontri = riscontri;
  }

  public Eccezioni getEccezioni() {
    return this.eccezioni;
  }

  public void setEccezioni(Eccezioni eccezioni) {
    this.eccezioni = eccezioni;
  }

  public Protocollo getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(Protocollo protocollo) {
    this.protocollo = protocollo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="mittente",required=false,nillable=false)
  protected Soggetto mittente;

  @XmlElement(name="destinatario",required=false,nillable=false)
  protected Soggetto destinatario;

  @XmlElement(name="profilo-collaborazione",required=false,nillable=false)
  protected ProfiloCollaborazione profiloCollaborazione;

  @XmlElement(name="servizio",required=false,nillable=false)
  protected Servizio servizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=false,nillable=false)
  protected java.lang.String azione;

  @XmlElement(name="servizio-correlato",required=false,nillable=false)
  protected Servizio servizioCorrelato;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="collaborazione",required=false,nillable=false)
  protected java.lang.String collaborazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo",required=false,nillable=false)
  protected java.lang.String identificativo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="riferimento-messaggio",required=false,nillable=false)
  protected java.lang.String riferimentoMessaggio;

  @XmlElement(name="ora-registrazione",required=false,nillable=false)
  protected Data oraRegistrazione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="scadenza",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date scadenza;

  @XmlElement(name="profilo-trasmissione",required=false,nillable=false)
  protected ProfiloTrasmissione profiloTrasmissione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-fruitore",required=false,nillable=false)
  protected java.lang.String servizioApplicativoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-erogatore",required=false,nillable=false)
  protected java.lang.String servizioApplicativoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="digest",required=false,nillable=false)
  protected java.lang.String digest;

  @XmlElement(name="trasmissioni",required=false,nillable=false)
  protected Trasmissioni trasmissioni;

  @XmlElement(name="riscontri",required=false,nillable=false)
  protected Riscontri riscontri;

  @XmlElement(name="eccezioni",required=false,nillable=false)
  protected Eccezioni eccezioni;

  @XmlElement(name="protocollo",required=true,nillable=false)
  protected Protocollo protocollo;

}
