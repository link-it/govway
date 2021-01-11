/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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


/** <p>Java class for accordo-servizio-parte-specifica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-specifica"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="port-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-erogatore" type="{http://www.openspcoop2.org/core/commons/search}id-soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-accordo-servizio-parte-comune" type="{http://www.openspcoop2.org/core/commons/search}id-accordo-servizio-parte-comune" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "accordo-servizio-parte-specifica", 
  propOrder = {
  	"tipo",
  	"nome",
  	"versione",
  	"portType",
  	"idErogatore",
  	"idAccordoServizioParteComune"
  }
)

@XmlRootElement(name = "accordo-servizio-parte-specifica")

public class AccordoServizioParteSpecifica extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccordoServizioParteSpecifica() {
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

  public java.lang.Integer getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.Integer versione) {
    this.versione = versione;
  }

  public java.lang.String getPortType() {
    return this.portType;
  }

  public void setPortType(java.lang.String portType) {
    this.portType = portType;
  }

  public IdSoggetto getIdErogatore() {
    return this.idErogatore;
  }

  public void setIdErogatore(IdSoggetto idErogatore) {
    this.idErogatore = idErogatore;
  }

  public IdAccordoServizioParteComune getIdAccordoServizioParteComune() {
    return this.idAccordoServizioParteComune;
  }

  public void setIdAccordoServizioParteComune(IdAccordoServizioParteComune idAccordoServizioParteComune) {
    this.idAccordoServizioParteComune = idAccordoServizioParteComune;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.commons.search.model.AccordoServizioParteSpecificaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica.modelStaticInstance = new org.openspcoop2.core.commons.search.model.AccordoServizioParteSpecificaModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.AccordoServizioParteSpecificaModel model(){
	  if(org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=true,nillable=false)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="versione",required=true,nillable=false)
  protected java.lang.Integer versione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="port-type",required=true,nillable=false)
  protected java.lang.String portType;

  @XmlElement(name="id-erogatore",required=true,nillable=false)
  protected IdSoggetto idErogatore;

  @XmlElement(name="id-accordo-servizio-parte-comune",required=true,nillable=false)
  protected IdAccordoServizioParteComune idAccordoServizioParteComune;

}
