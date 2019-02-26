/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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


/** <p>Java class for soggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="soggetto">
 * 		&lt;sequence>
 * 			&lt;element name="nome-soggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="tipo-soggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="server" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "soggetto", 
  propOrder = {
  	"nomeSoggetto",
  	"tipoSoggetto",
  	"server",
  	"identificativoPorta"
  }
)

@XmlRootElement(name = "soggetto")

public class Soggetto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Soggetto() {
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

  public java.lang.String getNomeSoggetto() {
    return this.nomeSoggetto;
  }

  public void setNomeSoggetto(java.lang.String nomeSoggetto) {
    this.nomeSoggetto = nomeSoggetto;
  }

  public java.lang.String getTipoSoggetto() {
    return this.tipoSoggetto;
  }

  public void setTipoSoggetto(java.lang.String tipoSoggetto) {
    this.tipoSoggetto = tipoSoggetto;
  }

  public java.lang.String getServer() {
    return this.server;
  }

  public void setServer(java.lang.String server) {
    this.server = server;
  }

  public java.lang.String getIdentificativoPorta() {
    return this.identificativoPorta;
  }

  public void setIdentificativoPorta(java.lang.String identificativoPorta) {
    this.identificativoPorta = identificativoPorta;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.commons.search.model.SoggettoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.Soggetto.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.Soggetto.modelStaticInstance = new org.openspcoop2.core.commons.search.model.SoggettoModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.SoggettoModel model(){
	  if(org.openspcoop2.core.commons.search.Soggetto.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.Soggetto.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-soggetto",required=true,nillable=false)
  protected java.lang.String nomeSoggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-soggetto",required=true,nillable=false)
  protected java.lang.String tipoSoggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="server",required=false,nillable=false)
  protected java.lang.String server;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-porta",required=false,nillable=false)
  protected java.lang.String identificativoPorta;

}
