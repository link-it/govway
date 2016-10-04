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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.tracciamento.constants.TipoTraccia;
import java.io.Serializable;


/** <p>Java class for traccia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="traccia">
 * 		&lt;sequence>
 * 			&lt;element name="dominio" type="{http://www.openspcoop2.org/core/tracciamento}dominio" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="esito-elaborazione" type="{http://www.openspcoop2.org/core/tracciamento}traccia-esito-elaborazione" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="identificativo-correlazione-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="identificativo-correlazione-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="busta" type="{http://www.openspcoop2.org/core/tracciamento}busta" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="busta-xml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="allegati" type="{http://www.openspcoop2.org/core/tracciamento}allegati" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/tracciamento}TipoTraccia" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "traccia", 
  propOrder = {
  	"dominio",
  	"oraRegistrazione",
  	"esitoElaborazione",
  	"identificativoCorrelazioneRichiesta",
  	"identificativoCorrelazioneRisposta",
  	"location",
  	"busta",
  	"bustaXml",
  	"allegati"
  }
)

@XmlRootElement(name = "traccia")

public class Traccia extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Traccia() {
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

  public Dominio getDominio() {
    return this.dominio;
  }

  public void setDominio(Dominio dominio) {
    this.dominio = dominio;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public TracciaEsitoElaborazione getEsitoElaborazione() {
    return this.esitoElaborazione;
  }

  public void setEsitoElaborazione(TracciaEsitoElaborazione esitoElaborazione) {
    this.esitoElaborazione = esitoElaborazione;
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

  public java.lang.String getLocation() {
    return this.location;
  }

  public void setLocation(java.lang.String location) {
    this.location = location;
  }

  public Busta getBusta() {
    return this.busta;
  }

  public void setBusta(Busta busta) {
    this.busta = busta;
  }

  public boolean isRicercaSoloBusteErrore() {
    return this.ricercaSoloBusteErrore;
  }

  public boolean getRicercaSoloBusteErrore() {
    return this.ricercaSoloBusteErrore;
  }

  public void setRicercaSoloBusteErrore(boolean ricercaSoloBusteErrore) {
    this.ricercaSoloBusteErrore = ricercaSoloBusteErrore;
  }

  public java.lang.String getBustaXml() {
    return this.bustaXml;
  }

  public void setBustaXml(java.lang.String bustaXml) {
    this.bustaXml = bustaXml;
  }

  public Allegati getAllegati() {
    return this.allegati;
  }

  public void setAllegati(Allegati allegati) {
    this.allegati = allegati;
  }

  public void set_value_tipo(String value) {
    this.tipo = (TipoTraccia) TipoTraccia.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.tracciamento.constants.TipoTraccia getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.tracciamento.constants.TipoTraccia tipo) {
    this.tipo = tipo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.tracciamento.model.TracciaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.tracciamento.Traccia.modelStaticInstance==null){
  			org.openspcoop2.core.tracciamento.Traccia.modelStaticInstance = new org.openspcoop2.core.tracciamento.model.TracciaModel();
	  }
  }
  public static org.openspcoop2.core.tracciamento.model.TracciaModel model(){
	  if(org.openspcoop2.core.tracciamento.Traccia.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.tracciamento.Traccia.modelStaticInstance;
  }


  @XmlElement(name="dominio",required=true,nillable=false)
  protected Dominio dominio;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date oraRegistrazione;

  @XmlElement(name="esito-elaborazione",required=true,nillable=false)
  protected TracciaEsitoElaborazione esitoElaborazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-correlazione-richiesta",required=false,nillable=false)
  protected java.lang.String identificativoCorrelazioneRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-correlazione-risposta",required=false,nillable=false)
  protected java.lang.String identificativoCorrelazioneRisposta;

  @javax.xml.bind.annotation.XmlTransient
  protected boolean correlazioneApplicativaAndMatch = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="location",required=true,nillable=false)
  protected java.lang.String location;

  @XmlElement(name="busta",required=true,nillable=false)
  protected Busta busta;

  @javax.xml.bind.annotation.XmlTransient
  protected boolean ricercaSoloBusteErrore = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="busta-xml",required=false,nillable=false)
  protected java.lang.String bustaXml;

  @XmlElement(name="allegati",required=false,nillable=false)
  protected Allegati allegati;

  @XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=false)
  protected TipoTraccia tipo;

}
