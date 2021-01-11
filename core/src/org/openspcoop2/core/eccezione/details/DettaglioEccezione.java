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
package org.openspcoop2.core.eccezione.details;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for dettaglio-eccezione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dettaglio-eccezione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="domain" type="{http://govway.org/integration/fault/details}dominio" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="exceptions" type="{http://govway.org/integration/fault/details}eccezioni" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="details" type="{http://govway.org/integration/fault/details}dettagli" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "dettaglio-eccezione", 
  propOrder = {
  	"domain",
  	"timestamp",
  	"exceptions",
  	"details"
  }
)

@XmlRootElement(name = "fault-details")

public class DettaglioEccezione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DettaglioEccezione() {
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

  public Eccezioni getExceptions() {
    return this.exceptions;
  }

  public void setExceptions(Eccezioni exceptions) {
    this.exceptions = exceptions;
  }

  public Dettagli getDetails() {
    return this.details;
  }

  public void setDetails(Dettagli details) {
    this.details = details;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.eccezione.details.model.DettaglioEccezioneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.eccezione.details.DettaglioEccezione.modelStaticInstance==null){
  			org.openspcoop2.core.eccezione.details.DettaglioEccezione.modelStaticInstance = new org.openspcoop2.core.eccezione.details.model.DettaglioEccezioneModel();
	  }
  }
  public static org.openspcoop2.core.eccezione.details.model.DettaglioEccezioneModel model(){
	  if(org.openspcoop2.core.eccezione.details.DettaglioEccezione.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.eccezione.details.DettaglioEccezione.modelStaticInstance;
  }


  @XmlElement(name="domain",required=true,nillable=false)
  protected Dominio domain;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="timestamp",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date timestamp;

  @XmlElement(name="exceptions",required=true,nillable=false)
  protected Eccezioni exceptions;

  @XmlElement(name="details",required=false,nillable=false)
  protected Dettagli details;

}
