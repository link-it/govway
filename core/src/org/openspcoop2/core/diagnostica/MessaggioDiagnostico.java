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
package org.openspcoop2.core.diagnostica;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for messaggio-diagnostico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messaggio-diagnostico">
 * 		&lt;sequence>
 * 			&lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="dominio" type="{http://www.openspcoop2.org/core/diagnostica}dominio-diagnostico" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="identificativo-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="identificativo-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="codice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="severita" type="{http://www.openspcoop2.org/core/diagnostica}positiveInteger" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="protocollo" type="{http://www.openspcoop2.org/core/diagnostica}protocollo" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "messaggio-diagnostico", 
  propOrder = {
  	"idTransazione",
  	"dominio",
  	"identificativoRichiesta",
  	"identificativoRisposta",
  	"oraRegistrazione",
  	"codice",
  	"messaggio",
  	"severita",
  	"protocollo"
  }
)

@XmlRootElement(name = "messaggio-diagnostico")

public class MessaggioDiagnostico extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MessaggioDiagnostico() {
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

  public java.lang.String getIdTransazione() {
    return this.idTransazione;
  }

  public void setIdTransazione(java.lang.String idTransazione) {
    this.idTransazione = idTransazione;
  }

  public DominioDiagnostico getDominio() {
    return this.dominio;
  }

  public void setDominio(DominioDiagnostico dominio) {
    this.dominio = dominio;
  }

  public java.lang.String getIdentificativoRichiesta() {
    return this.identificativoRichiesta;
  }

  public void setIdentificativoRichiesta(java.lang.String identificativoRichiesta) {
    this.identificativoRichiesta = identificativoRichiesta;
  }

  public java.lang.String getIdentificativoRisposta() {
    return this.identificativoRisposta;
  }

  public void setIdentificativoRisposta(java.lang.String identificativoRisposta) {
    this.identificativoRisposta = identificativoRisposta;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getCodice() {
    return this.codice;
  }

  public void setCodice(java.lang.String codice) {
    this.codice = codice;
  }

  public java.lang.String getMessaggio() {
    return this.messaggio;
  }

  public void setMessaggio(java.lang.String messaggio) {
    this.messaggio = messaggio;
  }

  public java.lang.Integer getSeverita() {
    return this.severita;
  }

  public void setSeverita(java.lang.Integer severita) {
    this.severita = severita;
  }

  public Protocollo getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(Protocollo protocollo) {
    this.protocollo = protocollo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.diagnostica.model.MessaggioDiagnosticoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.diagnostica.MessaggioDiagnostico.modelStaticInstance==null){
  			org.openspcoop2.core.diagnostica.MessaggioDiagnostico.modelStaticInstance = new org.openspcoop2.core.diagnostica.model.MessaggioDiagnosticoModel();
	  }
  }
  public static org.openspcoop2.core.diagnostica.model.MessaggioDiagnosticoModel model(){
	  if(org.openspcoop2.core.diagnostica.MessaggioDiagnostico.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.diagnostica.MessaggioDiagnostico.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-transazione",required=false,nillable=false)
  protected java.lang.String idTransazione;

  @XmlElement(name="dominio",required=true,nillable=false)
  protected DominioDiagnostico dominio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-richiesta",required=false,nillable=false)
  protected java.lang.String identificativoRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-risposta",required=false,nillable=false)
  protected java.lang.String identificativoRisposta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice",required=true,nillable=false)
  protected java.lang.String codice;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="messaggio",required=true,nillable=false)
  protected java.lang.String messaggio;

  @javax.xml.bind.annotation.XmlSchemaType(name="positiveInteger")
  @XmlElement(name="severita",required=true,nillable=false)
  protected java.lang.Integer severita;

  @XmlElement(name="protocollo",required=false,nillable=false)
  protected Protocollo protocollo;

}
