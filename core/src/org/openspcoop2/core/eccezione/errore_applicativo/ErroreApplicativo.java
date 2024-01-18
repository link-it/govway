/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.eccezione.errore_applicativo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for errore-applicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="errore-applicativo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="domain" type="{http://govway.org/integration/fault}dominio" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="service" type="{http://govway.org/integration/fault}dati-cooperazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="exception" type="{http://govway.org/integration/fault}eccezione" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "errore-applicativo", 
  propOrder = {
  	"domain",
  	"timestamp",
  	"service",
  	"exception"
  }
)

@XmlRootElement(name = "fault")

public class ErroreApplicativo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ErroreApplicativo() {
    super();
  }

  public Dominio getDomain() {
    return this.domain;
  }

  public void setDomain(Dominio domain) {
    this.domain = domain;
  }

  public java.util.Date getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(java.util.Date timestamp) {
    this.timestamp = timestamp;
  }

  public DatiCooperazione getService() {
    return this.service;
  }

  public void setService(DatiCooperazione service) {
    this.service = service;
  }

  public Eccezione getException() {
    return this.exception;
  }

  public void setException(Eccezione exception) {
    this.exception = exception;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.eccezione.errore_applicativo.model.ErroreApplicativoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo.modelStaticInstance==null){
  			org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo.modelStaticInstance = new org.openspcoop2.core.eccezione.errore_applicativo.model.ErroreApplicativoModel();
	  }
  }
  public static org.openspcoop2.core.eccezione.errore_applicativo.model.ErroreApplicativoModel model(){
	  if(org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo.modelStaticInstance;
  }


  @XmlElement(name="domain",required=true,nillable=false)
  protected Dominio domain;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="timestamp",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date timestamp;

  @XmlElement(name="service",required=false,nillable=false)
  protected DatiCooperazione service;

  @XmlElement(name="exception",required=true,nillable=false)
  protected Eccezione exception;

}
