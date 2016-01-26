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
import java.io.Serializable;


/** <p>Java class for id-accordo-cooperazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-accordo-cooperazione">
 * 		&lt;sequence>
 * 			&lt;element name="soggetto-referente" type="{http://www.openspcoop2.org/core/registry}id-soggetto" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="versione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "id-accordo-cooperazione", 
  propOrder = {
  	"soggettoReferente"
  }
)

@XmlRootElement(name = "id-accordo-cooperazione")

public class IdAccordoCooperazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IdAccordoCooperazione() {
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

  public IdSoggetto getSoggettoReferente() {
    return this.soggettoReferente;
  }

  public void setSoggettoReferente(IdSoggetto soggettoReferente) {
    this.soggettoReferente = soggettoReferente;
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  public IdAccordoCooperazione(org.openspcoop2.core.id.IDAccordoCooperazione idAccordo){
  	if(idAccordo!=null){
  		this.nome = idAccordo.getNome();
  		this.versione = idAccordo.getVersione();
  	}
  }

  @SuppressWarnings("deprecation")
  public org.openspcoop2.core.id.IDAccordoCooperazione toIDAccordoCooperazione() throws org.openspcoop2.core.commons.CoreException{
  	if(this.nome==null){
  		throw new org.openspcoop2.core.commons.CoreException("Nome undefined");
  	}
  	try{
  		org.openspcoop2.core.id.IDAccordoCooperazione id = new org.openspcoop2.core.id.IDAccordoCooperazione();
  		id.setNome(this.nome);
  		id.setVersione(this.versione);
  		return id;
  	}catch(Exception e){
  		throw new org.openspcoop2.core.commons.CoreException(e.getMessage(),e);
  	}
  }

  @XmlElement(name="soggetto-referente",required=false,nillable=false)
  protected IdSoggetto soggettoReferente;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=false)
  protected java.lang.String versione;

}
