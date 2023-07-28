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
package org.openspcoop2.core.commons.search;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import java.io.Serializable;


/** <p>Java class for porta-dominio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-dominio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo" type="{http://www.openspcoop2.org/core/commons/search}TipoPdD" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "porta-dominio", 
  propOrder = {
  	"nome",
  	"tipo"
  }
)

@XmlRootElement(name = "porta-dominio")

public class PortaDominio extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortaDominio() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void setTipoRawEnumValue(String value) {
    this.tipo = (TipoPdD) TipoPdD.toEnumConstantFromString(value);
  }

  public String getTipoRawEnumValue() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.commons.search.constants.TipoPdD getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.commons.search.constants.TipoPdD tipo) {
    this.tipo = tipo;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.commons.search.model.PortaDominioModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.PortaDominio.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.PortaDominio.modelStaticInstance = new org.openspcoop2.core.commons.search.model.PortaDominioModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.PortaDominioModel model(){
	  if(org.openspcoop2.core.commons.search.PortaDominio.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.PortaDominio.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoRawEnumValue;

  @XmlElement(name="tipo",required=false,nillable=false)
  protected TipoPdD tipo;

}
