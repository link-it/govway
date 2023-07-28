/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
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
 * 			&lt;element name="porta-delegata" type="{http://www.openspcoop2.org/core/config}porta-delegata" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="porta-applicativa" type="{http://www.openspcoop2.org/core/config}porta-applicativa" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="servizio-applicativo" type="{http://www.openspcoop2.org/core/config}servizio-applicativo" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/config}connettore" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="super-user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="dominio-default" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="router" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="pd-url-prefix-rewriter" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="pa-url-prefix-rewriter" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
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
  	"portaDelegata",
  	"portaApplicativa",
  	"servizioApplicativo",
  	"connettore"
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

  public void addPortaDelegata(PortaDelegata portaDelegata) {
    this.portaDelegata.add(portaDelegata);
  }

  public PortaDelegata getPortaDelegata(int index) {
    return this.portaDelegata.get( index );
  }

  public PortaDelegata removePortaDelegata(int index) {
    return this.portaDelegata.remove( index );
  }

  public List<PortaDelegata> getPortaDelegataList() {
    return this.portaDelegata;
  }

  public void setPortaDelegataList(List<PortaDelegata> portaDelegata) {
    this.portaDelegata=portaDelegata;
  }

  public int sizePortaDelegataList() {
    return this.portaDelegata.size();
  }

  public void addPortaApplicativa(PortaApplicativa portaApplicativa) {
    this.portaApplicativa.add(portaApplicativa);
  }

  public PortaApplicativa getPortaApplicativa(int index) {
    return this.portaApplicativa.get( index );
  }

  public PortaApplicativa removePortaApplicativa(int index) {
    return this.portaApplicativa.remove( index );
  }

  public List<PortaApplicativa> getPortaApplicativaList() {
    return this.portaApplicativa;
  }

  public void setPortaApplicativaList(List<PortaApplicativa> portaApplicativa) {
    this.portaApplicativa=portaApplicativa;
  }

  public int sizePortaApplicativaList() {
    return this.portaApplicativa.size();
  }

  public void addServizioApplicativo(ServizioApplicativo servizioApplicativo) {
    this.servizioApplicativo.add(servizioApplicativo);
  }

  public ServizioApplicativo getServizioApplicativo(int index) {
    return this.servizioApplicativo.get( index );
  }

  public ServizioApplicativo removeServizioApplicativo(int index) {
    return this.servizioApplicativo.remove( index );
  }

  public List<ServizioApplicativo> getServizioApplicativoList() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativoList(List<ServizioApplicativo> servizioApplicativo) {
    this.servizioApplicativo=servizioApplicativo;
  }

  public int sizeServizioApplicativoList() {
    return this.servizioApplicativo.size();
  }

  public void addConnettore(Connettore connettore) {
    this.connettore.add(connettore);
  }

  public Connettore getConnettore(int index) {
    return this.connettore.get( index );
  }

  public Connettore removeConnettore(int index) {
    return this.connettore.remove( index );
  }

  public List<Connettore> getConnettoreList() {
    return this.connettore;
  }

  public void setConnettoreList(List<Connettore> connettore) {
    this.connettore=connettore;
  }

  public int sizeConnettoreList() {
    return this.connettore.size();
  }

  public java.lang.String getSuperUser() {
    return this.superUser;
  }

  public void setSuperUser(java.lang.String superUser) {
    this.superUser = superUser;
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

  public boolean isDominioDefault() {
    return this.dominioDefault;
  }

  public boolean getDominioDefault() {
    return this.dominioDefault;
  }

  public void setDominioDefault(boolean dominioDefault) {
    this.dominioDefault = dominioDefault;
  }

  public boolean isRouter() {
    return this.router;
  }

  public boolean getRouter() {
    return this.router;
  }

  public void setRouter(boolean router) {
    this.router = router;
  }

  public java.lang.String getPdUrlPrefixRewriter() {
    return this.pdUrlPrefixRewriter;
  }

  public void setPdUrlPrefixRewriter(java.lang.String pdUrlPrefixRewriter) {
    this.pdUrlPrefixRewriter = pdUrlPrefixRewriter;
  }

  public java.lang.String getPaUrlPrefixRewriter() {
    return this.paUrlPrefixRewriter;
  }

  public void setPaUrlPrefixRewriter(java.lang.String paUrlPrefixRewriter) {
    this.paUrlPrefixRewriter = paUrlPrefixRewriter;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.config.model.SoggettoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.config.Soggetto.modelStaticInstance==null){
  			org.openspcoop2.core.config.Soggetto.modelStaticInstance = new org.openspcoop2.core.config.model.SoggettoModel();
	  }
  }
  public static org.openspcoop2.core.config.model.SoggettoModel model(){
	  if(org.openspcoop2.core.config.Soggetto.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.config.Soggetto.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlTransient
  protected IDSoggetto oldIDSoggettoForUpdate;

  @XmlElement(name="porta-delegata",required=true,nillable=false)
  private List<PortaDelegata> portaDelegata = new ArrayList<>();

  /**
   * Use method getPortaDelegataList
   * @return List&lt;PortaDelegata&gt;
  */
  public List<PortaDelegata> getPortaDelegata() {
  	return this.getPortaDelegataList();
  }

  /**
   * Use method setPortaDelegataList
   * @param portaDelegata List&lt;PortaDelegata&gt;
  */
  public void setPortaDelegata(List<PortaDelegata> portaDelegata) {
  	this.setPortaDelegataList(portaDelegata);
  }

  /**
   * Use method sizePortaDelegataList
   * @return lunghezza della lista
  */
  public int sizePortaDelegata() {
  	return this.sizePortaDelegataList();
  }

  @XmlElement(name="porta-applicativa",required=true,nillable=false)
  private List<PortaApplicativa> portaApplicativa = new ArrayList<>();

  /**
   * Use method getPortaApplicativaList
   * @return List&lt;PortaApplicativa&gt;
  */
  public List<PortaApplicativa> getPortaApplicativa() {
  	return this.getPortaApplicativaList();
  }

  /**
   * Use method setPortaApplicativaList
   * @param portaApplicativa List&lt;PortaApplicativa&gt;
  */
  public void setPortaApplicativa(List<PortaApplicativa> portaApplicativa) {
  	this.setPortaApplicativaList(portaApplicativa);
  }

  /**
   * Use method sizePortaApplicativaList
   * @return lunghezza della lista
  */
  public int sizePortaApplicativa() {
  	return this.sizePortaApplicativaList();
  }

  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  private List<ServizioApplicativo> servizioApplicativo = new ArrayList<>();

  /**
   * Use method getServizioApplicativoList
   * @return List&lt;ServizioApplicativo&gt;
  */
  public List<ServizioApplicativo> getServizioApplicativo() {
  	return this.getServizioApplicativoList();
  }

  /**
   * Use method setServizioApplicativoList
   * @param servizioApplicativo List&lt;ServizioApplicativo&gt;
  */
  public void setServizioApplicativo(List<ServizioApplicativo> servizioApplicativo) {
  	this.setServizioApplicativoList(servizioApplicativo);
  }

  /**
   * Use method sizeServizioApplicativoList
   * @return lunghezza della lista
  */
  public int sizeServizioApplicativo() {
  	return this.sizeServizioApplicativoList();
  }

  @XmlElement(name="connettore",required=true,nillable=false)
  private List<Connettore> connettore = new ArrayList<>();

  /**
   * Use method getConnettoreList
   * @return List&lt;Connettore&gt;
  */
  public List<Connettore> getConnettore() {
  	return this.getConnettoreList();
  }

  /**
   * Use method setConnettoreList
   * @param connettore List&lt;Connettore&gt;
  */
  public void setConnettore(List<Connettore> connettore) {
  	this.setConnettoreList(connettore);
  }

  /**
   * Use method sizeConnettoreList
   * @return lunghezza della lista
  */
  public int sizeConnettore() {
  	return this.sizeConnettoreList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String superUser;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="identificativo-porta",required=false)
  protected java.lang.String identificativoPorta;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="dominio-default",required=false)
  protected boolean dominioDefault = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="router",required=false)
  protected boolean router = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="pd-url-prefix-rewriter",required=false)
  protected java.lang.String pdUrlPrefixRewriter;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="pa-url-prefix-rewriter",required=false)
  protected java.lang.String paUrlPrefixRewriter;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

}
