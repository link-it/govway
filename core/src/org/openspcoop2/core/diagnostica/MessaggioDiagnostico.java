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
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
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

  public FiltroInformazioneProtocollo getFiltroInformazioneProtocollo() {
    return this.filtroInformazioneProtocollo;
  }

  public void setFiltroInformazioneProtocollo(FiltroInformazioneProtocollo filtroInformazioneProtocollo) {
    this.filtroInformazioneProtocollo = filtroInformazioneProtocollo;
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

  @javax.xml.bind.annotation.XmlTransient
  protected FiltroInformazioneProtocollo filtroInformazioneProtocollo;

}
