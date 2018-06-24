/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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


/** <p>Java class for fruitore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fruitore">
 * 		&lt;sequence>
 * 			&lt;element name="id-fruitore" type="{http://www.openspcoop2.org/core/commons/search}id-soggetto" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="id-accordo-servizio-parte-specifica" type="{http://www.openspcoop2.org/core/commons/search}id-accordo-servizio-parte-specifica" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "fruitore", 
  propOrder = {
  	"idFruitore",
  	"idAccordoServizioParteSpecifica",
  	"oraRegistrazione"
  }
)

@XmlRootElement(name = "fruitore")

public class Fruitore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Fruitore() {
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

  public IdSoggetto getIdFruitore() {
    return this.idFruitore;
  }

  public void setIdFruitore(IdSoggetto idFruitore) {
    this.idFruitore = idFruitore;
  }

  public IdAccordoServizioParteSpecifica getIdAccordoServizioParteSpecifica() {
    return this.idAccordoServizioParteSpecifica;
  }

  public void setIdAccordoServizioParteSpecifica(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) {
    this.idAccordoServizioParteSpecifica = idAccordoServizioParteSpecifica;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.commons.search.model.FruitoreModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.Fruitore.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.Fruitore.modelStaticInstance = new org.openspcoop2.core.commons.search.model.FruitoreModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.FruitoreModel model(){
	  if(org.openspcoop2.core.commons.search.Fruitore.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.Fruitore.modelStaticInstance;
  }


  @XmlElement(name="id-fruitore",required=true,nillable=false)
  protected IdSoggetto idFruitore;

  @XmlElement(name="id-accordo-servizio-parte-specifica",required=true,nillable=false)
  protected IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date oraRegistrazione;

}
