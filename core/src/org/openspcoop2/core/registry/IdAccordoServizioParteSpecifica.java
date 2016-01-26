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


/** <p>Java class for id-accordo-servizio-parte-specifica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-accordo-servizio-parte-specifica">
 * 		&lt;sequence>
 * 			&lt;element name="soggetto-erogatore" type="{http://www.openspcoop2.org/core/registry}id-soggetto" minOccurs="0" maxOccurs="1"/>
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



  public IdAccordoServizioParteSpecifica(org.openspcoop2.core.id.IDAccordo idAccordo){
  	if(idAccordo!=null){
  		this.nome = idAccordo.getNome();
  		this.versione = idAccordo.getVersione();
  		if(idAccordo.getSoggettoReferente()!=null){
  			this.soggettoErogatore = new IdSoggetto();
  			this.soggettoErogatore.setNome(idAccordo.getSoggettoReferente().getNome());
  			this.soggettoErogatore.setTipo(idAccordo.getSoggettoReferente().getTipo());
  		}
  	}
  }

  @SuppressWarnings("deprecation")
  public org.openspcoop2.core.id.IDAccordo toIDAccordo() throws org.openspcoop2.core.commons.CoreException{
  	if(this.nome==null){
  		throw new org.openspcoop2.core.commons.CoreException("Nome undefined");
  	}
  	try{
  		org.openspcoop2.core.id.IDAccordo id = new org.openspcoop2.core.id.IDAccordo();
  		id.setNome(this.nome);
  		id.setVersione(this.versione);
  		if(this.soggettoErogatore!=null){
  			id.setSoggettoReferente(new org.openspcoop2.core.id.IDSoggetto(this.soggettoErogatore.getTipo(),this.soggettoErogatore.getNome()));
  		}
  		return id;
  	}catch(Exception e){
  		throw new org.openspcoop2.core.commons.CoreException(e.getMessage(),e);
  	}
  }

  @XmlElement(name="soggetto-erogatore",required=false,nillable=false)
  protected IdSoggetto soggettoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=false)
  protected java.lang.String versione;

}
