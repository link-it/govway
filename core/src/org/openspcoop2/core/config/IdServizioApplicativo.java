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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for id-servizio-applicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-servizio-applicativo">
 * 		&lt;sequence>
 * 			&lt;element name="id-soggetto" type="{http://www.openspcoop2.org/core/config}id-soggetto" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "id-servizio-applicativo", 
  propOrder = {
  	"idSoggetto"
  }
)

@XmlRootElement(name = "id-servizio-applicativo")

public class IdServizioApplicativo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IdServizioApplicativo() {
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

  public IdSoggetto getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(IdSoggetto idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  public IdServizioApplicativo(org.openspcoop2.core.id.IDServizioApplicativo idServizioApplicativo){
  	if(idServizioApplicativo!=null){
  		this.nome = idServizioApplicativo.getNome();
  		if(idServizioApplicativo.getIdSoggettoProprietario()!=null){
  			this.idSoggetto = new IdSoggetto();
  			this.idSoggetto.setNome(idServizioApplicativo.getIdSoggettoProprietario().getNome());
  			this.idSoggetto.setTipo(idServizioApplicativo.getIdSoggettoProprietario().getTipo());
  		}
  	}
  }

  public org.openspcoop2.core.id.IDServizioApplicativo toIDServizioApplicativo() throws org.openspcoop2.core.commons.CoreException{
  	if(this.nome==null){
  		throw new org.openspcoop2.core.commons.CoreException("Nome undefined");
  	}
  	if(this.idSoggetto==null){
  		throw new org.openspcoop2.core.commons.CoreException("IdSoggetto undefined");
  	}
  	if(this.idSoggetto.getTipo()==null){
  		throw new org.openspcoop2.core.commons.CoreException("IdSoggetto.tipo undefined");
  	}
  	if(this.idSoggetto.getNome()==null){
  		throw new org.openspcoop2.core.commons.CoreException("IdSoggetto.nome undefined");
  	}
  	org.openspcoop2.core.id.IDServizioApplicativo idSA = new org.openspcoop2.core.id.IDServizioApplicativo();
  	idSA.setNome(this.nome);
  	idSA.setIdSoggettoProprietario(this.idSoggetto.toIDSoggetto());
  	return idSA;
  }

  @XmlElement(name="id-soggetto",required=true,nillable=false)
  protected IdSoggetto idSoggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

}
