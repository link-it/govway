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
import java.io.Serializable;


/** <p>Java class for id-accordo-servizio-parte-specifica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-accordo-servizio-parte-specifica">
 * 		&lt;sequence>
 * 			&lt;element name="soggetto-erogatore" type="{http://www.openspcoop2.org/core/registry}id-soggetto" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="versione" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" use="optional" default="1"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "id-accordo-servizio-parte-specifica", 
  propOrder = {
  	"soggettoErogatore"
  }
)

@XmlRootElement(name = "id-accordo-servizio-parte-specifica")

public class IdAccordoServizioParteSpecifica extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IdAccordoServizioParteSpecifica() {
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

  public IdSoggetto getSoggettoErogatore() {
    return this.soggettoErogatore;
  }

  public void setSoggettoErogatore(IdSoggetto soggettoErogatore) {
    this.soggettoErogatore = soggettoErogatore;
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  public IdAccordoServizioParteSpecifica(org.openspcoop2.core.id.IDServizio idServizio){
  	if(idServizio!=null){
  		this.tipo = idServizio.getTipo();
  		this.nome = idServizio.getNome();
  		this.versione = idServizio.getVersione();
  		if(idServizio.getSoggettoErogatore()!=null){
  			this.soggettoErogatore = new IdSoggetto();
  			this.soggettoErogatore.setNome(idServizio.getSoggettoErogatore().getNome());
  			this.soggettoErogatore.setTipo(idServizio.getSoggettoErogatore().getTipo());
  		}
  	}
  }

  @SuppressWarnings("deprecation")
  public org.openspcoop2.core.id.IDServizio toIDServizio() throws org.openspcoop2.core.commons.CoreException{
  	if(this.tipo==null){
  		throw new org.openspcoop2.core.commons.CoreException("Tipo undefined");
  	}
  	if(this.nome==null){
  		throw new org.openspcoop2.core.commons.CoreException("Nome undefined");
  	}
  	if(this.versione==null){
  		throw new org.openspcoop2.core.commons.CoreException("Versione undefined");
  	}
  	if(this.soggettoErogatore==null){
  		throw new org.openspcoop2.core.commons.CoreException("SoggettoErogatore undefined");
  	}
  	if(this.soggettoErogatore.getTipo()==null){
  		throw new org.openspcoop2.core.commons.CoreException("TipoSoggettoErogatore undefined");
  	}
  	if(this.soggettoErogatore.getNome()==null){
  		throw new org.openspcoop2.core.commons.CoreException("NomeSoggettoErogatore undefined");
  	}
  	try{
  		org.openspcoop2.core.id.IDServizio id = new org.openspcoop2.core.id.IDServizio();
  		id.setTipo(this.tipo);
  		id.setNome(this.nome);
  		id.setVersione(this.versione);
  		if(this.soggettoErogatore!=null){
  			id.setSoggettoErogatore(new org.openspcoop2.core.id.IDSoggetto(this.soggettoErogatore.getTipo(),this.soggettoErogatore.getNome()));
  		}
  		return id;
  	}catch(Exception e){
  		throw new org.openspcoop2.core.commons.CoreException(e.getMessage(),e);
  	}
  }

  @XmlElement(name="soggetto-erogatore",required=true,nillable=false)
  protected IdSoggetto soggettoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlAttribute(name="versione",required=false)
  protected java.lang.Integer versione = new java.lang.Integer("1");

}
