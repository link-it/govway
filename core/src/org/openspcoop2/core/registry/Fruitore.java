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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for fruitore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fruitore">
 * 		&lt;sequence>
 * 			&lt;element name="servizio-applicativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="stato-package" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="byte-wsdl-implementativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/>
 * 		&lt;attribute name="byte-wsdl-implementativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/>
 * 		&lt;attribute name="id-soggetto" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="id-servizio" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="wsdl-implementativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="wsdl-implementativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="filtro-duplicati" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/>
 * 		&lt;attribute name="conferma-ricezione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/>
 * 		&lt;attribute name="id-collaborazione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/>
 * 		&lt;attribute name="consegna-in-ordine" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/>
 * 		&lt;attribute name="scadenza" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/>
 * 		&lt;attribute name="versione-protocollo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="client-auth" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fruitore", 
  propOrder = {
  	"servizioApplicativo",
  	"connettore"
  }
)

@XmlRootElement(name = "fruitore")

public class Fruitore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Fruitore() {
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

  public void addServizioApplicativo(java.lang.String servizioApplicativo) {
    this.servizioApplicativo.add(servizioApplicativo);
  }

  public java.lang.String getServizioApplicativo(int index) {
    return this.servizioApplicativo.get( index );
  }

  public java.lang.String removeServizioApplicativo(int index) {
    return this.servizioApplicativo.remove( index );
  }

  public List<java.lang.String> getServizioApplicativoList() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativoList(List<java.lang.String> servizioApplicativo) {
    this.servizioApplicativo=servizioApplicativo;
  }

  public int sizeServizioApplicativoList() {
    return this.servizioApplicativo.size();
  }

  public Connettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(Connettore connettore) {
    this.connettore = connettore;
  }

  public java.lang.String getStatoPackage() {
    return this.statoPackage;
  }

  public void setStatoPackage(java.lang.String statoPackage) {
    this.statoPackage = statoPackage;
  }

  public byte[] getByteWsdlImplementativoErogatore() {
    return this.byteWsdlImplementativoErogatore;
  }

  public void setByteWsdlImplementativoErogatore(byte[] byteWsdlImplementativoErogatore) {
    this.byteWsdlImplementativoErogatore = byteWsdlImplementativoErogatore;
  }

  public byte[] getByteWsdlImplementativoFruitore() {
    return this.byteWsdlImplementativoFruitore;
  }

  public void setByteWsdlImplementativoFruitore(byte[] byteWsdlImplementativoFruitore) {
    this.byteWsdlImplementativoFruitore = byteWsdlImplementativoFruitore;
  }

  public java.lang.Long getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(java.lang.Long idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public java.lang.Long getIdServizio() {
    return this.idServizio;
  }

  public void setIdServizio(java.lang.Long idServizio) {
    this.idServizio = idServizio;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getWsdlImplementativoErogatore() {
    return this.wsdlImplementativoErogatore;
  }

  public void setWsdlImplementativoErogatore(java.lang.String wsdlImplementativoErogatore) {
    this.wsdlImplementativoErogatore = wsdlImplementativoErogatore;
  }

  public java.lang.String getWsdlImplementativoFruitore() {
    return this.wsdlImplementativoFruitore;
  }

  public void setWsdlImplementativoFruitore(java.lang.String wsdlImplementativoFruitore) {
    this.wsdlImplementativoFruitore = wsdlImplementativoFruitore;
  }

  public void set_value_filtroDuplicati(String value) {
    this.filtroDuplicati = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_filtroDuplicati() {
    if(this.filtroDuplicati == null){
    	return null;
    }else{
    	return this.filtroDuplicati.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getFiltroDuplicati() {
    return this.filtroDuplicati;
  }

  public void setFiltroDuplicati(org.openspcoop2.core.registry.constants.StatoFunzionalita filtroDuplicati) {
    this.filtroDuplicati = filtroDuplicati;
  }

  public void set_value_confermaRicezione(String value) {
    this.confermaRicezione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_confermaRicezione() {
    if(this.confermaRicezione == null){
    	return null;
    }else{
    	return this.confermaRicezione.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getConfermaRicezione() {
    return this.confermaRicezione;
  }

  public void setConfermaRicezione(org.openspcoop2.core.registry.constants.StatoFunzionalita confermaRicezione) {
    this.confermaRicezione = confermaRicezione;
  }

  public void set_value_idCollaborazione(String value) {
    this.idCollaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_idCollaborazione() {
    if(this.idCollaborazione == null){
    	return null;
    }else{
    	return this.idCollaborazione.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getIdCollaborazione() {
    return this.idCollaborazione;
  }

  public void setIdCollaborazione(org.openspcoop2.core.registry.constants.StatoFunzionalita idCollaborazione) {
    this.idCollaborazione = idCollaborazione;
  }

  public void set_value_consegnaInOrdine(String value) {
    this.consegnaInOrdine = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_consegnaInOrdine() {
    if(this.consegnaInOrdine == null){
    	return null;
    }else{
    	return this.consegnaInOrdine.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getConsegnaInOrdine() {
    return this.consegnaInOrdine;
  }

  public void setConsegnaInOrdine(org.openspcoop2.core.registry.constants.StatoFunzionalita consegnaInOrdine) {
    this.consegnaInOrdine = consegnaInOrdine;
  }

  public java.lang.String getScadenza() {
    return this.scadenza;
  }

  public void setScadenza(java.lang.String scadenza) {
    this.scadenza = scadenza;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getVersioneProtocollo() {
    return this.versioneProtocollo;
  }

  public void setVersioneProtocollo(java.lang.String versioneProtocollo) {
    this.versioneProtocollo = versioneProtocollo;
  }

  public void set_value_clientAuth(String value) {
    this.clientAuth = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_clientAuth() {
    if(this.clientAuth == null){
    	return null;
    }else{
    	return this.clientAuth.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getClientAuth() {
    return this.clientAuth;
  }

  public void setClientAuth(org.openspcoop2.core.registry.constants.StatoFunzionalita clientAuth) {
    this.clientAuth = clientAuth;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  protected List<java.lang.String> servizioApplicativo = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getServizioApplicativoList
   * @return List<java.lang.String>
  */
  @Deprecated
  public List<java.lang.String> getServizioApplicativo() {
  	return this.servizioApplicativo;
  }

  /**
   * @deprecated Use method setServizioApplicativoList
   * @param servizioApplicativo List<java.lang.String>
  */
  @Deprecated
  public void setServizioApplicativo(List<java.lang.String> servizioApplicativo) {
  	this.servizioApplicativo=servizioApplicativo;
  }

  /**
   * @deprecated Use method sizeServizioApplicativoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioApplicativo() {
  	return this.servizioApplicativo.size();
  }

  @XmlElement(name="connettore",required=false,nillable=false)
  protected Connettore connettore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="stato-package",required=false)
  protected java.lang.String statoPackage;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-wsdl-implementativo-erogatore",required=false)
  protected byte[] byteWsdlImplementativoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-wsdl-implementativo-fruitore",required=false)
  protected byte[] byteWsdlImplementativoFruitore;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idSoggetto;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="wsdl-implementativo-erogatore",required=false)
  protected java.lang.String wsdlImplementativoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="wsdl-implementativo-fruitore",required=false)
  protected java.lang.String wsdlImplementativoFruitore;

  @XmlTransient
  protected java.lang.String _value_filtroDuplicati;

  @XmlAttribute(name="filtro-duplicati",required=false)
  protected StatoFunzionalita filtroDuplicati;

  @XmlTransient
  protected java.lang.String _value_confermaRicezione;

  @XmlAttribute(name="conferma-ricezione",required=false)
  protected StatoFunzionalita confermaRicezione;

  @XmlTransient
  protected java.lang.String _value_idCollaborazione;

  @XmlAttribute(name="id-collaborazione",required=false)
  protected StatoFunzionalita idCollaborazione;

  @XmlTransient
  protected java.lang.String _value_consegnaInOrdine;

  @XmlAttribute(name="consegna-in-ordine",required=false)
  protected StatoFunzionalita consegnaInOrdine;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="scadenza",required=false)
  protected java.lang.String scadenza;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione-protocollo",required=false)
  protected java.lang.String versioneProtocollo;

  @XmlTransient
  protected java.lang.String _value_clientAuth;

  @XmlAttribute(name="client-auth",required=false)
  protected StatoFunzionalita clientAuth;

}
