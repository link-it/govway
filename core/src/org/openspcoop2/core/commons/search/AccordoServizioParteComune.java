/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for accordo-servizio-parte-comune complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-comune">
 * 		&lt;sequence>
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="id-referente" type="{http://www.openspcoop2.org/core/commons/search}id-soggetto" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="accordo-servizio-parte-comune-azione" type="{http://www.openspcoop2.org/core/commons/search}accordo-servizio-parte-comune-azione" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="port-type" type="{http://www.openspcoop2.org/core/commons/search}port-type" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "accordo-servizio-parte-comune", 
  propOrder = {
  	"nome",
  	"versione",
  	"idReferente",
  	"accordoServizioParteComuneAzione",
  	"portType"
  }
)

@XmlRootElement(name = "accordo-servizio-parte-comune")

public class AccordoServizioParteComune extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccordoServizioParteComune() {
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

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.String versione) {
    this.versione = versione;
  }

  public IdSoggetto getIdReferente() {
    return this.idReferente;
  }

  public void setIdReferente(IdSoggetto idReferente) {
    this.idReferente = idReferente;
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

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


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="versione",required=true,nillable=false)
  protected java.lang.String versione;

  @XmlElement(name="id-referente",required=true,nillable=false)
  protected IdSoggetto idReferente;

  @XmlElement(name="accordo-servizio-parte-comune-azione",required=true,nillable=false)
  protected List<AccordoServizioParteComuneAzione> accordoServizioParteComuneAzione = new ArrayList<AccordoServizioParteComuneAzione>();

  /**
   * @deprecated Use method getAccordoServizioParteComuneAzioneList
   * @return List<AccordoServizioParteComuneAzione>
  */
  @Deprecated
  public List<AccordoServizioParteComuneAzione> getAccordoServizioParteComuneAzione() {
  	return this.accordoServizioParteComuneAzione;
  }

  /**
   * @deprecated Use method setAccordoServizioParteComuneAzioneList
   * @param accordoServizioParteComuneAzione List<AccordoServizioParteComuneAzione>
  */
  @Deprecated
  public void setAccordoServizioParteComuneAzione(List<AccordoServizioParteComuneAzione> accordoServizioParteComuneAzione) {
  	this.accordoServizioParteComuneAzione=accordoServizioParteComuneAzione;
  }

  /**
   * @deprecated Use method sizeAccordoServizioParteComuneAzioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAccordoServizioParteComuneAzione() {
  	return this.accordoServizioParteComuneAzione.size();
  }

  @XmlElement(name="port-type",required=true,nillable=false)
  protected List<PortType> portType = new ArrayList<PortType>();

  /**
   * @deprecated Use method getPortTypeList
   * @return List<PortType>
  */
  @Deprecated
  public List<PortType> getPortType() {
  	return this.portType;
  }

  /**
   * @deprecated Use method setPortTypeList
   * @param portType List<PortType>
  */
  @Deprecated
  public void setPortType(List<PortType> portType) {
  	this.portType=portType;
  }

  /**
   * @deprecated Use method sizePortTypeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePortType() {
  	return this.portType.size();
  }

}
