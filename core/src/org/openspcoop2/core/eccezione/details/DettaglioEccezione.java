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
package org.openspcoop2.core.eccezione.details;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for dettaglio-eccezione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dettaglio-eccezione">
 * 		&lt;sequence>
 * 			&lt;element name="dominio" type="{http://www.openspcoop2.org/core/eccezione/details}dominio" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="eccezioni" type="{http://www.openspcoop2.org/core/eccezione/details}eccezioni" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="dettagli" type="{http://www.openspcoop2.org/core/eccezione/details}dettagli" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "dettaglio-eccezione", 
  propOrder = {
  	"dominio",
  	"oraRegistrazione",
  	"eccezioni",
  	"dettagli"
  }
)

@XmlRootElement(name = "dettaglio-eccezione")

public class DettaglioEccezione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DettaglioEccezione() {
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

  public Dominio getDominio() {
    return this.dominio;
  }

  public void setDominio(Dominio dominio) {
    this.dominio = dominio;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public Eccezioni getEccezioni() {
    return this.eccezioni;
  }

  public void setEccezioni(Eccezioni eccezioni) {
    this.eccezioni = eccezioni;
  }

  public Dettagli getDettagli() {
    return this.dettagli;
  }

  public void setDettagli(Dettagli dettagli) {
    this.dettagli = dettagli;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

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


  @XmlElement(name="dominio",required=true,nillable=false)
  protected Dominio dominio;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date oraRegistrazione;

  @XmlElement(name="eccezioni",required=true,nillable=false)
  protected Eccezioni eccezioni;

  @XmlElement(name="dettagli",required=false,nillable=false)
  protected Dettagli dettagli;

}
