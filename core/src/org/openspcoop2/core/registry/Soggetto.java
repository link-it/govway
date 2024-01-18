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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.id.IDSoggetto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for soggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="soggetto"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/core/registry}proprieta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="protocol-property" type="{http://www.openspcoop2.org/core/registry}protocol-property" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="credenziali" type="{http://www.openspcoop2.org/core/registry}credenziali-soggetto" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="ruoli" type="{http://www.openspcoop2.org/core/registry}ruoli-soggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="accordo-servizio-parte-specifica" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-specifica" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="proprieta-oggetto" type="{http://www.openspcoop2.org/core/registry}proprieta-oggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="super-user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="privato" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="false"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="porta-dominio" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * 		&lt;attribute name="versione-protocollo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="codice-ipa" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "soggetto", 
  propOrder = {
  	"connettore",
  	"proprieta",
  	"protocolProperty",
  	"credenziali",
  	"ruoli",
  	"accordoServizioParteSpecifica",
  	"proprietaOggetto"
  }
)

@XmlRootElement(name = "soggetto")

public class Soggetto extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Soggetto() {
    super();
  }

  public IDSoggetto getOldIDSoggettoForUpdate() {
    return this.oldIDSoggettoForUpdate;
  }

  public void setOldIDSoggettoForUpdate(IDSoggetto oldIDSoggettoForUpdate) {
    this.oldIDSoggettoForUpdate=oldIDSoggettoForUpdate;
  }

  public Connettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(Connettore connettore) {
    this.connettore = connettore;
  }

  public void addProprieta(Proprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public Proprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public Proprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<Proprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<Proprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
  }

  public void addProtocolProperty(ProtocolProperty protocolProperty) {
    this.protocolProperty.add(protocolProperty);
  }

  public ProtocolProperty getProtocolProperty(int index) {
    return this.protocolProperty.get( index );
  }

  public ProtocolProperty removeProtocolProperty(int index) {
    return this.protocolProperty.remove( index );
  }

  public List<ProtocolProperty> getProtocolPropertyList() {
    return this.protocolProperty;
  }

  public void setProtocolPropertyList(List<ProtocolProperty> protocolProperty) {
    this.protocolProperty=protocolProperty;
  }

  public int sizeProtocolPropertyList() {
    return this.protocolProperty.size();
  }

  public void addCredenziali(CredenzialiSoggetto credenziali) {
    this.credenziali.add(credenziali);
  }

  public CredenzialiSoggetto getCredenziali(int index) {
    return this.credenziali.get( index );
  }

  public CredenzialiSoggetto removeCredenziali(int index) {
    return this.credenziali.remove( index );
  }

  public List<CredenzialiSoggetto> getCredenzialiList() {
    return this.credenziali;
  }

  public void setCredenzialiList(List<CredenzialiSoggetto> credenziali) {
    this.credenziali=credenziali;
  }

  public int sizeCredenzialiList() {
    return this.credenziali.size();
  }

  public RuoliSoggetto getRuoli() {
    return this.ruoli;
  }

  public void setRuoli(RuoliSoggetto ruoli) {
    this.ruoli = ruoli;
  }

  public void addAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) {
    this.accordoServizioParteSpecifica.add(accordoServizioParteSpecifica);
  }

  public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(int index) {
    return this.accordoServizioParteSpecifica.get( index );
  }

  public AccordoServizioParteSpecifica removeAccordoServizioParteSpecifica(int index) {
    return this.accordoServizioParteSpecifica.remove( index );
  }

  public List<AccordoServizioParteSpecifica> getAccordoServizioParteSpecificaList() {
    return this.accordoServizioParteSpecifica;
  }

  public void setAccordoServizioParteSpecificaList(List<AccordoServizioParteSpecifica> accordoServizioParteSpecifica) {
    this.accordoServizioParteSpecifica=accordoServizioParteSpecifica;
  }

  public int sizeAccordoServizioParteSpecificaList() {
    return this.accordoServizioParteSpecifica.size();
  }

  public ProprietaOggetto getProprietaOggetto() {
    return this.proprietaOggetto;
  }

  public void setProprietaOggetto(ProprietaOggetto proprietaOggetto) {
    this.proprietaOggetto = proprietaOggetto;
  }

  public java.lang.String getSuperUser() {
    return this.superUser;
  }

  public void setSuperUser(java.lang.String superUser) {
    this.superUser = superUser;
  }

  public Boolean getPrivato() {
    return this.privato;
  }

  public void setPrivato(Boolean privato) {
    this.privato = privato;
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

  public java.lang.String getIdentificativoPorta() {
    return this.identificativoPorta;
  }

  public void setIdentificativoPorta(java.lang.String identificativoPorta) {
    this.identificativoPorta = identificativoPorta;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getPortaDominio() {
    return this.portaDominio;
  }

  public void setPortaDominio(java.lang.String portaDominio) {
    this.portaDominio = portaDominio;
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

  public java.lang.String getCodiceIpa() {
    return this.codiceIpa;
  }

  public void setCodiceIpa(java.lang.String codiceIpa) {
    this.codiceIpa = codiceIpa;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.registry.model.SoggettoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.registry.Soggetto.modelStaticInstance==null){
  			org.openspcoop2.core.registry.Soggetto.modelStaticInstance = new org.openspcoop2.core.registry.model.SoggettoModel();
	  }
  }
  public static org.openspcoop2.core.registry.model.SoggettoModel model(){
	  if(org.openspcoop2.core.registry.Soggetto.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.registry.Soggetto.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected IDSoggetto oldIDSoggettoForUpdate;

  @XmlElement(name="connettore",required=false,nillable=false)
  protected Connettore connettore;

  @XmlElement(name="proprieta",required=true,nillable=false)
  private List<Proprieta> proprieta = new ArrayList<>();

  /**
   * Use method getProprietaList
   * @return List&lt;Proprieta&gt;
  */
  public List<Proprieta> getProprieta() {
  	return this.getProprietaList();
  }

  /**
   * Use method setProprietaList
   * @param proprieta List&lt;Proprieta&gt;
  */
  public void setProprieta(List<Proprieta> proprieta) {
  	this.setProprietaList(proprieta);
  }

  /**
   * Use method sizeProprietaList
   * @return lunghezza della lista
  */
  public int sizeProprieta() {
  	return this.sizeProprietaList();
  }

  @XmlElement(name="protocol-property",required=true,nillable=false)
  private List<ProtocolProperty> protocolProperty = new ArrayList<>();

  /**
   * Use method getProtocolPropertyList
   * @return List&lt;ProtocolProperty&gt;
  */
  public List<ProtocolProperty> getProtocolProperty() {
  	return this.getProtocolPropertyList();
  }

  /**
   * Use method setProtocolPropertyList
   * @param protocolProperty List&lt;ProtocolProperty&gt;
  */
  public void setProtocolProperty(List<ProtocolProperty> protocolProperty) {
  	this.setProtocolPropertyList(protocolProperty);
  }

  /**
   * Use method sizeProtocolPropertyList
   * @return lunghezza della lista
  */
  public int sizeProtocolProperty() {
  	return this.sizeProtocolPropertyList();
  }

  @XmlElement(name="credenziali",required=true,nillable=false)
  private List<CredenzialiSoggetto> credenziali = new ArrayList<>();

  /**
   * Use method getCredenzialiList
   * @return List&lt;CredenzialiSoggetto&gt;
  */
  public List<CredenzialiSoggetto> getCredenziali() {
  	return this.getCredenzialiList();
  }

  /**
   * Use method setCredenzialiList
   * @param credenziali List&lt;CredenzialiSoggetto&gt;
  */
  public void setCredenziali(List<CredenzialiSoggetto> credenziali) {
  	this.setCredenzialiList(credenziali);
  }

  /**
   * Use method sizeCredenzialiList
   * @return lunghezza della lista
  */
  public int sizeCredenziali() {
  	return this.sizeCredenzialiList();
  }

  @XmlElement(name="ruoli",required=false,nillable=false)
  protected RuoliSoggetto ruoli;

  @XmlElement(name="accordo-servizio-parte-specifica",required=true,nillable=false)
  private List<AccordoServizioParteSpecifica> accordoServizioParteSpecifica = new ArrayList<>();

  /**
   * Use method getAccordoServizioParteSpecificaList
   * @return List&lt;AccordoServizioParteSpecifica&gt;
  */
  public List<AccordoServizioParteSpecifica> getAccordoServizioParteSpecifica() {
  	return this.getAccordoServizioParteSpecificaList();
  }

  /**
   * Use method setAccordoServizioParteSpecificaList
   * @param accordoServizioParteSpecifica List&lt;AccordoServizioParteSpecifica&gt;
  */
  public void setAccordoServizioParteSpecifica(List<AccordoServizioParteSpecifica> accordoServizioParteSpecifica) {
  	this.setAccordoServizioParteSpecificaList(accordoServizioParteSpecifica);
  }

  /**
   * Use method sizeAccordoServizioParteSpecificaList
   * @return lunghezza della lista
  */
  public int sizeAccordoServizioParteSpecifica() {
  	return this.sizeAccordoServizioParteSpecificaList();
  }

  @XmlElement(name="proprieta-oggetto",required=false,nillable=false)
  protected ProprietaOggetto proprietaOggetto;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String superUser;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="privato",required=false)
  protected Boolean privato = Boolean.valueOf("false");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="identificativo-porta",required=false)
  protected java.lang.String identificativoPorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="porta-dominio",required=false)
  protected java.lang.String portaDominio;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione-protocollo",required=false)
  protected java.lang.String versioneProtocollo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="codice-ipa",required=false)
  protected java.lang.String codiceIpa;

}
