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
package org.openspcoop2.core.diagnostica;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.diagnostica.constants.TipoPdD;
import java.io.Serializable;


/** <p>Java class for filtro-informazione-protocollo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filtro-informazione-protocollo">
 * 		&lt;sequence>
 * 			&lt;element name="tipo-porta" type="{http://www.openspcoop2.org/core/diagnostica}TipoPdD" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ricerca-solo-messaggi-correlati-informazioni-protocollo" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="false"/>
 * 			&lt;element name="fruitore" type="{http://www.openspcoop2.org/core/diagnostica}soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="erogatore" type="{http://www.openspcoop2.org/core/diagnostica}soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio" type="{http://www.openspcoop2.org/core/diagnostica}servizio" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="identificativo-correlazione-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="identificativo-correlazione-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="correlazione-applicativa-and-match" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="false"/>
 * 			&lt;element name="servizio-applicativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "filtro-informazione-protocollo", 
  propOrder = {
  	"tipoPorta",
  	"nomePorta",
  	"ricercaSoloMessaggiCorrelatiInformazioniProtocollo",
  	"fruitore",
  	"erogatore",
  	"servizio",
  	"azione",
  	"identificativoCorrelazioneRichiesta",
  	"identificativoCorrelazioneRisposta",
  	"correlazioneApplicativaAndMatch",
  	"servizioApplicativo"
  }
)

@XmlRootElement(name = "filtro-informazione-protocollo")

public class FiltroInformazioneProtocollo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FiltroInformazioneProtocollo() {
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

  public void set_value_tipoPorta(String value) {
    this.tipoPorta = (TipoPdD) TipoPdD.toEnumConstantFromString(value);
  }

  public String get_value_tipoPorta() {
    if(this.tipoPorta == null){
    	return null;
    }else{
    	return this.tipoPorta.toString();
    }
  }

  public org.openspcoop2.core.diagnostica.constants.TipoPdD getTipoPorta() {
    return this.tipoPorta;
  }

  public void setTipoPorta(org.openspcoop2.core.diagnostica.constants.TipoPdD tipoPorta) {
    this.tipoPorta = tipoPorta;
  }

  public java.lang.String getNomePorta() {
    return this.nomePorta;
  }

  public void setNomePorta(java.lang.String nomePorta) {
    this.nomePorta = nomePorta;
  }

  public boolean isRicercaSoloMessaggiCorrelatiInformazioniProtocollo() {
    return this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
  }

  public boolean getRicercaSoloMessaggiCorrelatiInformazioniProtocollo() {
    return this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
  }

  public void setRicercaSoloMessaggiCorrelatiInformazioniProtocollo(boolean ricercaSoloMessaggiCorrelatiInformazioniProtocollo) {
    this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo = ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
  }

  public Soggetto getFruitore() {
    return this.fruitore;
  }

  public void setFruitore(Soggetto fruitore) {
    this.fruitore = fruitore;
  }

  public Soggetto getErogatore() {
    return this.erogatore;
  }

  public void setErogatore(Soggetto erogatore) {
    this.erogatore = erogatore;
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

  public java.lang.String getIdentificativoCorrelazioneRichiesta() {
    return this.identificativoCorrelazioneRichiesta;
  }

  public void setIdentificativoCorrelazioneRichiesta(java.lang.String identificativoCorrelazioneRichiesta) {
    this.identificativoCorrelazioneRichiesta = identificativoCorrelazioneRichiesta;
  }

  public java.lang.String getIdentificativoCorrelazioneRisposta() {
    return this.identificativoCorrelazioneRisposta;
  }

  public void setIdentificativoCorrelazioneRisposta(java.lang.String identificativoCorrelazioneRisposta) {
    this.identificativoCorrelazioneRisposta = identificativoCorrelazioneRisposta;
  }

  public boolean isCorrelazioneApplicativaAndMatch() {
    return this.correlazioneApplicativaAndMatch;
  }

  public boolean getCorrelazioneApplicativaAndMatch() {
    return this.correlazioneApplicativaAndMatch;
  }

  public void setCorrelazioneApplicativaAndMatch(boolean correlazioneApplicativaAndMatch) {
    this.correlazioneApplicativaAndMatch = correlazioneApplicativaAndMatch;
  }

  public java.lang.String getServizioApplicativo() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativo(java.lang.String servizioApplicativo) {
    this.servizioApplicativo = servizioApplicativo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlTransient
  protected java.lang.String _value_tipoPorta;

  @XmlElement(name="tipo-porta",required=false,nillable=false)
  protected TipoPdD tipoPorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-porta",required=false,nillable=false)
  protected java.lang.String nomePorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="ricerca-solo-messaggi-correlati-informazioni-protocollo",required=false,nillable=false,defaultValue="false")
  protected boolean ricercaSoloMessaggiCorrelatiInformazioniProtocollo = false;

  @XmlElement(name="fruitore",required=false,nillable=false)
  protected Soggetto fruitore;

  @XmlElement(name="erogatore",required=false,nillable=false)
  protected Soggetto erogatore;

  @XmlElement(name="servizio",required=false,nillable=false)
  protected Servizio servizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=false,nillable=false)
  protected java.lang.String azione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-correlazione-richiesta",required=false,nillable=false)
  protected java.lang.String identificativoCorrelazioneRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-correlazione-risposta",required=false,nillable=false)
  protected java.lang.String identificativoCorrelazioneRisposta;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="correlazione-applicativa-and-match",required=false,nillable=false,defaultValue="false")
  protected boolean correlazioneApplicativaAndMatch = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo",required=false,nillable=false)
  protected java.lang.String servizioApplicativo;

}
