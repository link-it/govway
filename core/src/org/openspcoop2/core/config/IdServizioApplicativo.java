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
import java.io.Serializable;


/** <p>Java class for id-servizio-applicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-servizio-applicativo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-soggetto" type="{http://www.openspcoop2.org/core/config}id-soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
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

public class IdServizioApplicativo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public IdServizioApplicativo() {
    super();
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

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

}
