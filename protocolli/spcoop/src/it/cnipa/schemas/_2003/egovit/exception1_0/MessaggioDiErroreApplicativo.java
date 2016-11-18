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
package it.cnipa.schemas._2003.egovit.exception1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for MessaggioDiErroreApplicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessaggioDiErroreApplicativo">
 * 		&lt;sequence>
 * 			&lt;element name="OraRegistrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="IdentificativoPorta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="IdentificativoFunzione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Eccezione" type="{http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/}Eccezione" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "MessaggioDiErroreApplicativo", 
  propOrder = {
  	"oraRegistrazione",
  	"identificativoPorta",
  	"identificativoFunzione",
  	"eccezione"
  }
)

@XmlRootElement(name = "MessaggioDiErroreApplicativo")

public class MessaggioDiErroreApplicativo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MessaggioDiErroreApplicativo() {
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

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getIdentificativoPorta() {
    return this.identificativoPorta;
  }

  public void setIdentificativoPorta(java.lang.String identificativoPorta) {
    this.identificativoPorta = identificativoPorta;
  }

  public java.lang.String getIdentificativoFunzione() {
    return this.identificativoFunzione;
  }

  public void setIdentificativoFunzione(java.lang.String identificativoFunzione) {
    this.identificativoFunzione = identificativoFunzione;
  }

  public Eccezione getEccezione() {
    return this.eccezione;
  }

  public void setEccezione(Eccezione eccezione) {
    this.eccezione = eccezione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static it.cnipa.schemas._2003.egovit.exception1_0.model.MessaggioDiErroreApplicativoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo.modelStaticInstance==null){
  			it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo.modelStaticInstance = new it.cnipa.schemas._2003.egovit.exception1_0.model.MessaggioDiErroreApplicativoModel();
	  }
  }
  public static it.cnipa.schemas._2003.egovit.exception1_0.model.MessaggioDiErroreApplicativoModel model(){
	  if(it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo.modelStaticInstance;
  }


  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="OraRegistrazione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="IdentificativoPorta",required=true,nillable=false)
  protected java.lang.String identificativoPorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="IdentificativoFunzione",required=true,nillable=false)
  protected java.lang.String identificativoFunzione;

  @XmlElement(name="Eccezione",required=true,nillable=false)
  protected Eccezione eccezione;

}
