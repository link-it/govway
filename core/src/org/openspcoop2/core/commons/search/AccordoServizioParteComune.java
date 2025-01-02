/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.commons.search;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for accordo-servizio-parte-comune complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-comune"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-referente" type="{http://www.openspcoop2.org/core/commons/search}id-soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="service-binding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="canale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="accordo-servizio-parte-comune-azione" type="{http://www.openspcoop2.org/core/commons/search}accordo-servizio-parte-comune-azione" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="port-type" type="{http://www.openspcoop2.org/core/commons/search}port-type" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="resource" type="{http://www.openspcoop2.org/core/commons/search}resource" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="accordo-servizio-parte-comune-gruppo" type="{http://www.openspcoop2.org/core/commons/search}accordo-servizio-parte-comune-gruppo" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "accordo-servizio-parte-comune", 
  propOrder = {
  	"nome",
  	"versione",
  	"idReferente",
  	"serviceBinding",
  	"canale",
  	"accordoServizioParteComuneAzione",
  	"portType",
  	"resource",
  	"accordoServizioParteComuneGruppo"
  }
)

@XmlRootElement(name = "accordo-servizio-parte-comune")

public class AccordoServizioParteComune extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AccordoServizioParteComune() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.Integer getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.Integer versione) {
    this.versione = versione;
  }

  public IdSoggetto getIdReferente() {
    return this.idReferente;
  }

  public void setIdReferente(IdSoggetto idReferente) {
    this.idReferente = idReferente;
  }

  public java.lang.String getServiceBinding() {
    return this.serviceBinding;
  }

  public void setServiceBinding(java.lang.String serviceBinding) {
    this.serviceBinding = serviceBinding;
  }

  public java.lang.String getCanale() {
    return this.canale;
  }

  public void setCanale(java.lang.String canale) {
    this.canale = canale;
  }

  public void addAccordoServizioParteComuneAzione(AccordoServizioParteComuneAzione accordoServizioParteComuneAzione) {
    this.accordoServizioParteComuneAzione.add(accordoServizioParteComuneAzione);
  }

  public AccordoServizioParteComuneAzione getAccordoServizioParteComuneAzione(int index) {
    return this.accordoServizioParteComuneAzione.get( index );
  }

  public AccordoServizioParteComuneAzione removeAccordoServizioParteComuneAzione(int index) {
    return this.accordoServizioParteComuneAzione.remove( index );
  }

  public List<AccordoServizioParteComuneAzione> getAccordoServizioParteComuneAzioneList() {
    return this.accordoServizioParteComuneAzione;
  }

  public void setAccordoServizioParteComuneAzioneList(List<AccordoServizioParteComuneAzione> accordoServizioParteComuneAzione) {
    this.accordoServizioParteComuneAzione=accordoServizioParteComuneAzione;
  }

  public int sizeAccordoServizioParteComuneAzioneList() {
    return this.accordoServizioParteComuneAzione.size();
  }

  public void addPortType(PortType portType) {
    this.portType.add(portType);
  }

  public PortType getPortType(int index) {
    return this.portType.get( index );
  }

  public PortType removePortType(int index) {
    return this.portType.remove( index );
  }

  public List<PortType> getPortTypeList() {
    return this.portType;
  }

  public void setPortTypeList(List<PortType> portType) {
    this.portType=portType;
  }

  public int sizePortTypeList() {
    return this.portType.size();
  }

  public void addResource(Resource resource) {
    this.resource.add(resource);
  }

  public Resource getResource(int index) {
    return this.resource.get( index );
  }

  public Resource removeResource(int index) {
    return this.resource.remove( index );
  }

  public List<Resource> getResourceList() {
    return this.resource;
  }

  public void setResourceList(List<Resource> resource) {
    this.resource=resource;
  }

  public int sizeResourceList() {
    return this.resource.size();
  }

  public void addAccordoServizioParteComuneGruppo(AccordoServizioParteComuneGruppo accordoServizioParteComuneGruppo) {
    this.accordoServizioParteComuneGruppo.add(accordoServizioParteComuneGruppo);
  }

  public AccordoServizioParteComuneGruppo getAccordoServizioParteComuneGruppo(int index) {
    return this.accordoServizioParteComuneGruppo.get( index );
  }

  public AccordoServizioParteComuneGruppo removeAccordoServizioParteComuneGruppo(int index) {
    return this.accordoServizioParteComuneGruppo.remove( index );
  }

  public List<AccordoServizioParteComuneGruppo> getAccordoServizioParteComuneGruppoList() {
    return this.accordoServizioParteComuneGruppo;
  }

  public void setAccordoServizioParteComuneGruppoList(List<AccordoServizioParteComuneGruppo> accordoServizioParteComuneGruppo) {
    this.accordoServizioParteComuneGruppo=accordoServizioParteComuneGruppo;
  }

  public int sizeAccordoServizioParteComuneGruppoList() {
    return this.accordoServizioParteComuneGruppo.size();
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.AccordoServizioParteComune.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.AccordoServizioParteComune.modelStaticInstance = new org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneModel model(){
	  if(org.openspcoop2.core.commons.search.AccordoServizioParteComune.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.AccordoServizioParteComune.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="versione",required=true,nillable=false)
  protected java.lang.Integer versione;

  @XmlElement(name="id-referente",required=true,nillable=false)
  protected IdSoggetto idReferente;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="service-binding",required=true,nillable=false)
  protected java.lang.String serviceBinding;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="canale",required=true,nillable=false)
  protected java.lang.String canale;

  @XmlElement(name="accordo-servizio-parte-comune-azione",required=true,nillable=false)
  private List<AccordoServizioParteComuneAzione> accordoServizioParteComuneAzione = new ArrayList<>();

  /**
   * Use method getAccordoServizioParteComuneAzioneList
   * @return List&lt;AccordoServizioParteComuneAzione&gt;
  */
  public List<AccordoServizioParteComuneAzione> getAccordoServizioParteComuneAzione() {
  	return this.getAccordoServizioParteComuneAzioneList();
  }

  /**
   * Use method setAccordoServizioParteComuneAzioneList
   * @param accordoServizioParteComuneAzione List&lt;AccordoServizioParteComuneAzione&gt;
  */
  public void setAccordoServizioParteComuneAzione(List<AccordoServizioParteComuneAzione> accordoServizioParteComuneAzione) {
  	this.setAccordoServizioParteComuneAzioneList(accordoServizioParteComuneAzione);
  }

  /**
   * Use method sizeAccordoServizioParteComuneAzioneList
   * @return lunghezza della lista
  */
  public int sizeAccordoServizioParteComuneAzione() {
  	return this.sizeAccordoServizioParteComuneAzioneList();
  }

  @XmlElement(name="port-type",required=true,nillable=false)
  private List<PortType> portType = new ArrayList<>();

  /**
   * Use method getPortTypeList
   * @return List&lt;PortType&gt;
  */
  public List<PortType> getPortType() {
  	return this.getPortTypeList();
  }

  /**
   * Use method setPortTypeList
   * @param portType List&lt;PortType&gt;
  */
  public void setPortType(List<PortType> portType) {
  	this.setPortTypeList(portType);
  }

  /**
   * Use method sizePortTypeList
   * @return lunghezza della lista
  */
  public int sizePortType() {
  	return this.sizePortTypeList();
  }

  @XmlElement(name="resource",required=true,nillable=false)
  private List<Resource> resource = new ArrayList<>();

  /**
   * Use method getResourceList
   * @return List&lt;Resource&gt;
  */
  public List<Resource> getResource() {
  	return this.getResourceList();
  }

  /**
   * Use method setResourceList
   * @param resource List&lt;Resource&gt;
  */
  public void setResource(List<Resource> resource) {
  	this.setResourceList(resource);
  }

  /**
   * Use method sizeResourceList
   * @return lunghezza della lista
  */
  public int sizeResource() {
  	return this.sizeResourceList();
  }

  @XmlElement(name="accordo-servizio-parte-comune-gruppo",required=true,nillable=false)
  private List<AccordoServizioParteComuneGruppo> accordoServizioParteComuneGruppo = new ArrayList<>();

  /**
   * Use method getAccordoServizioParteComuneGruppoList
   * @return List&lt;AccordoServizioParteComuneGruppo&gt;
  */
  public List<AccordoServizioParteComuneGruppo> getAccordoServizioParteComuneGruppo() {
  	return this.getAccordoServizioParteComuneGruppoList();
  }

  /**
   * Use method setAccordoServizioParteComuneGruppoList
   * @param accordoServizioParteComuneGruppo List&lt;AccordoServizioParteComuneGruppo&gt;
  */
  public void setAccordoServizioParteComuneGruppo(List<AccordoServizioParteComuneGruppo> accordoServizioParteComuneGruppo) {
  	this.setAccordoServizioParteComuneGruppoList(accordoServizioParteComuneGruppo);
  }

  /**
   * Use method sizeAccordoServizioParteComuneGruppoList
   * @return lunghezza della lista
  */
  public int sizeAccordoServizioParteComuneGruppo() {
  	return this.sizeAccordoServizioParteComuneGruppoList();
  }

}
