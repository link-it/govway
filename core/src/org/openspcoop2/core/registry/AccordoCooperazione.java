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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for accordo-cooperazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-cooperazione">
 * 		&lt;sequence>
 * 			&lt;element name="uri-servizi-composti" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="soggetto-referente" type="{http://www.openspcoop2.org/core/registry}id-soggetto" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="elenco-partecipanti" type="{http://www.openspcoop2.org/core/registry}accordo-cooperazione-partecipanti" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="allegato" type="{http://www.openspcoop2.org/core/registry}documento" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="specifica-semiformale" type="{http://www.openspcoop2.org/core/registry}documento" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="super-user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="stato-package" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="privato" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="false"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="versione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accordo-cooperazione", 
  propOrder = {
  	"uriServiziComposti",
  	"soggettoReferente",
  	"elencoPartecipanti",
  	"allegato",
  	"specificaSemiformale"
  }
)

@XmlRootElement(name = "accordo-cooperazione")

public class AccordoCooperazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccordoCooperazione() {
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

  public IDAccordoCooperazione getOldIDAccordoForUpdate() {
    return this.oldIDAccordoForUpdate;
  }

  public void setOldIDAccordoForUpdate(IDAccordoCooperazione oldIDAccordoForUpdate) {
    this.oldIDAccordoForUpdate=oldIDAccordoForUpdate;
  }

  public void addUriServiziComposti(java.lang.String uriServiziComposti) {
    this.uriServiziComposti.add(uriServiziComposti);
  }

  public java.lang.String getUriServiziComposti(int index) {
    return this.uriServiziComposti.get( index );
  }

  public java.lang.String removeUriServiziComposti(int index) {
    return this.uriServiziComposti.remove( index );
  }

  public List<java.lang.String> getUriServiziCompostiList() {
    return this.uriServiziComposti;
  }

  public void setUriServiziCompostiList(List<java.lang.String> uriServiziComposti) {
    this.uriServiziComposti=uriServiziComposti;
  }

  public int sizeUriServiziCompostiList() {
    return this.uriServiziComposti.size();
  }

  public IdSoggetto getSoggettoReferente() {
    return this.soggettoReferente;
  }

  public void setSoggettoReferente(IdSoggetto soggettoReferente) {
    this.soggettoReferente = soggettoReferente;
  }

  public AccordoCooperazionePartecipanti getElencoPartecipanti() {
    return this.elencoPartecipanti;
  }

  public void setElencoPartecipanti(AccordoCooperazionePartecipanti elencoPartecipanti) {
    this.elencoPartecipanti = elencoPartecipanti;
  }

  public void addAllegato(Documento allegato) {
    this.allegato.add(allegato);
  }

  public Documento getAllegato(int index) {
    return this.allegato.get( index );
  }

  public Documento removeAllegato(int index) {
    return this.allegato.remove( index );
  }

  public List<Documento> getAllegatoList() {
    return this.allegato;
  }

  public void setAllegatoList(List<Documento> allegato) {
    this.allegato=allegato;
  }

  public int sizeAllegatoList() {
    return this.allegato.size();
  }

  public void addSpecificaSemiformale(Documento specificaSemiformale) {
    this.specificaSemiformale.add(specificaSemiformale);
  }

  public Documento getSpecificaSemiformale(int index) {
    return this.specificaSemiformale.get( index );
  }

  public Documento removeSpecificaSemiformale(int index) {
    return this.specificaSemiformale.remove( index );
  }

  public List<Documento> getSpecificaSemiformaleList() {
    return this.specificaSemiformale;
  }

  public void setSpecificaSemiformaleList(List<Documento> specificaSemiformale) {
    this.specificaSemiformale=specificaSemiformale;
  }

  public int sizeSpecificaSemiformaleList() {
    return this.specificaSemiformale.size();
  }

  public java.lang.String getSuperUser() {
    return this.superUser;
  }

  public void setSuperUser(java.lang.String superUser) {
    this.superUser = superUser;
  }

  public java.lang.String getStatoPackage() {
    return this.statoPackage;
  }

  public void setStatoPackage(java.lang.String statoPackage) {
    this.statoPackage = statoPackage;
  }

  public Boolean getPrivato() {
    return this.privato;
  }

  public void setPrivato(Boolean privato) {
    this.privato = privato;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.String versione) {
    this.versione = versione;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.registry.model.AccordoCooperazioneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.registry.AccordoCooperazione.modelStaticInstance==null){
  			org.openspcoop2.core.registry.AccordoCooperazione.modelStaticInstance = new org.openspcoop2.core.registry.model.AccordoCooperazioneModel();
	  }
  }
  public static org.openspcoop2.core.registry.model.AccordoCooperazioneModel model(){
	  if(org.openspcoop2.core.registry.AccordoCooperazione.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.registry.AccordoCooperazione.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected IDAccordoCooperazione oldIDAccordoForUpdate;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="uri-servizi-composti",required=true,nillable=false)
  protected List<java.lang.String> uriServiziComposti = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getUriServiziCompostiList
   * @return List<java.lang.String>
  */
  @Deprecated
  public List<java.lang.String> getUriServiziComposti() {
  	return this.uriServiziComposti;
  }

  /**
   * @deprecated Use method setUriServiziCompostiList
   * @param uriServiziComposti List<java.lang.String>
  */
  @Deprecated
  public void setUriServiziComposti(List<java.lang.String> uriServiziComposti) {
  	this.uriServiziComposti=uriServiziComposti;
  }

  /**
   * @deprecated Use method sizeUriServiziCompostiList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeUriServiziComposti() {
  	return this.uriServiziComposti.size();
  }

  @XmlElement(name="soggetto-referente",required=false,nillable=false)
  protected IdSoggetto soggettoReferente;

  @XmlElement(name="elenco-partecipanti",required=false,nillable=false)
  protected AccordoCooperazionePartecipanti elencoPartecipanti;

  @XmlElement(name="allegato",required=true,nillable=false)
  protected List<Documento> allegato = new ArrayList<Documento>();

  /**
   * @deprecated Use method getAllegatoList
   * @return List<Documento>
  */
  @Deprecated
  public List<Documento> getAllegato() {
  	return this.allegato;
  }

  /**
   * @deprecated Use method setAllegatoList
   * @param allegato List<Documento>
  */
  @Deprecated
  public void setAllegato(List<Documento> allegato) {
  	this.allegato=allegato;
  }

  /**
   * @deprecated Use method sizeAllegatoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAllegato() {
  	return this.allegato.size();
  }

  @XmlElement(name="specifica-semiformale",required=true,nillable=false)
  protected List<Documento> specificaSemiformale = new ArrayList<Documento>();

  /**
   * @deprecated Use method getSpecificaSemiformaleList
   * @return List<Documento>
  */
  @Deprecated
  public List<Documento> getSpecificaSemiformale() {
  	return this.specificaSemiformale;
  }

  /**
   * @deprecated Use method setSpecificaSemiformaleList
   * @param specificaSemiformale List<Documento>
  */
  @Deprecated
  public void setSpecificaSemiformale(List<Documento> specificaSemiformale) {
  	this.specificaSemiformale=specificaSemiformale;
  }

  /**
   * @deprecated Use method sizeSpecificaSemiformaleList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSpecificaSemiformale() {
  	return this.specificaSemiformale.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String superUser;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="stato-package",required=false)
  protected java.lang.String statoPackage;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="privato",required=false)
  protected Boolean privato = new Boolean("false");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=false)
  protected java.lang.String versione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

}
