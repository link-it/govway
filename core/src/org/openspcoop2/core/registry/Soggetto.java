/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlTransient;
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
 * &lt;complexType name="soggetto">
 * 		&lt;sequence>
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="protocol-property" type="{http://www.openspcoop2.org/core/registry}protocol-property" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="credenziali" type="{http://www.openspcoop2.org/core/registry}credenziali-soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ruoli" type="{http://www.openspcoop2.org/core/registry}ruoli-soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="accordo-servizio-parte-specifica" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-specifica" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="super-user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="privato" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="false"/>
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="porta-dominio" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/>
 * 		&lt;attribute name="versione-protocollo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="codice-ipa" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
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
  	"protocolProperty",
  	"credenziali",
  	"ruoli",
  	"accordoServizioParteSpecifica"
  }
)

@XmlRootElement(name = "soggetto")

public class Soggetto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Soggetto() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
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

  public CredenzialiSoggetto getCredenziali() {
    return this.credenziali;
  }

  public void setCredenziali(CredenzialiSoggetto credenziali) {
    this.credenziali = credenziali;
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

  @XmlTransient
  private Long id;

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

  @XmlElement(name="protocol-property",required=true,nillable=false)
  protected List<ProtocolProperty> protocolProperty = new ArrayList<ProtocolProperty>();

  /**
   * @deprecated Use method getProtocolPropertyList
   * @return List<ProtocolProperty>
  */
  @Deprecated
  public List<ProtocolProperty> getProtocolProperty() {
  	return this.protocolProperty;
  }

  /**
   * @deprecated Use method setProtocolPropertyList
   * @param protocolProperty List<ProtocolProperty>
  */
  @Deprecated
  public void setProtocolProperty(List<ProtocolProperty> protocolProperty) {
  	this.protocolProperty=protocolProperty;
  }

  /**
   * @deprecated Use method sizeProtocolPropertyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProtocolProperty() {
  	return this.protocolProperty.size();
  }

  @XmlElement(name="credenziali",required=false,nillable=false)
  protected CredenzialiSoggetto credenziali;

  @XmlElement(name="ruoli",required=false,nillable=false)
  protected RuoliSoggetto ruoli;

  @XmlElement(name="accordo-servizio-parte-specifica",required=true,nillable=false)
  protected List<AccordoServizioParteSpecifica> accordoServizioParteSpecifica = new ArrayList<AccordoServizioParteSpecifica>();

  /**
   * @deprecated Use method getAccordoServizioParteSpecificaList
   * @return List<AccordoServizioParteSpecifica>
  */
  @Deprecated
  public List<AccordoServizioParteSpecifica> getAccordoServizioParteSpecifica() {
  	return this.accordoServizioParteSpecifica;
  }

  /**
   * @deprecated Use method setAccordoServizioParteSpecificaList
   * @param accordoServizioParteSpecifica List<AccordoServizioParteSpecifica>
  */
  @Deprecated
  public void setAccordoServizioParteSpecifica(List<AccordoServizioParteSpecifica> accordoServizioParteSpecifica) {
  	this.accordoServizioParteSpecifica=accordoServizioParteSpecifica;
  }

  /**
   * @deprecated Use method sizeAccordoServizioParteSpecificaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAccordoServizioParteSpecifica() {
  	return this.accordoServizioParteSpecifica.size();
  }

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
