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
package org.openspcoop2.core.statistiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.statistiche.constants.TipoPorta;
import java.io.Serializable;


/** <p>Java class for statistica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistica"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="stato-record" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-porta" type="{http://www.openspcoop2.org/core/statistiche}tipo-porta" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-mittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="mittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-destinatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="destinatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="versione-servizio" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="trasporto-mittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="token-issuer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="token-client-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="token-subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="token-username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="token-mail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="esito" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="esito-contesto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="client-address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="gruppi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="uri-api" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="cluster-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="numero-transazioni" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="dimensioni-bytes-banda-complessiva" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dimensioni-bytes-banda-interna" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dimensioni-bytes-banda-esterna" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="latenza-totale" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="latenza-porta" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="latenza-servizio" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "statistica", 
  propOrder = {
  	"data",
  	"statoRecord",
  	"idPorta",
  	"tipoPorta",
  	"tipoMittente",
  	"mittente",
  	"tipoDestinatario",
  	"destinatario",
  	"tipoServizio",
  	"servizio",
  	"versioneServizio",
  	"azione",
  	"servizioApplicativo",
  	"trasportoMittente",
  	"tokenIssuer",
  	"tokenClientId",
  	"tokenSubject",
  	"tokenUsername",
  	"tokenMail",
  	"esito",
  	"esitoContesto",
  	"clientAddress",
  	"gruppi",
  	"uriApi",
  	"clusterId",
  	"numeroTransazioni",
  	"dimensioniBytesBandaComplessiva",
  	"dimensioniBytesBandaInterna",
  	"dimensioniBytesBandaEsterna",
  	"latenzaTotale",
  	"latenzaPorta",
  	"latenzaServizio"
  }
)

@XmlRootElement(name = "statistica")

public class Statistica extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Statistica() {
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

  public java.util.Date getData() {
    return this.data;
  }

  public void setData(java.util.Date data) {
    this.data = data;
  }

  public int getStatoRecord() {
    return this.statoRecord;
  }

  public void setStatoRecord(int statoRecord) {
    this.statoRecord = statoRecord;
  }

  public java.lang.String getIdPorta() {
    return this.idPorta;
  }

  public void setIdPorta(java.lang.String idPorta) {
    this.idPorta = idPorta;
  }

  public void set_value_tipoPorta(String value) {
    this.tipoPorta = (TipoPorta) TipoPorta.toEnumConstantFromString(value);
  }

  public String get_value_tipoPorta() {
    if(this.tipoPorta == null){
    	return null;
    }else{
    	return this.tipoPorta.toString();
    }
  }

  public org.openspcoop2.core.statistiche.constants.TipoPorta getTipoPorta() {
    return this.tipoPorta;
  }

  public void setTipoPorta(org.openspcoop2.core.statistiche.constants.TipoPorta tipoPorta) {
    this.tipoPorta = tipoPorta;
  }

  public java.lang.String getTipoMittente() {
    return this.tipoMittente;
  }

  public void setTipoMittente(java.lang.String tipoMittente) {
    this.tipoMittente = tipoMittente;
  }

  public java.lang.String getMittente() {
    return this.mittente;
  }

  public void setMittente(java.lang.String mittente) {
    this.mittente = mittente;
  }

  public java.lang.String getTipoDestinatario() {
    return this.tipoDestinatario;
  }

  public void setTipoDestinatario(java.lang.String tipoDestinatario) {
    this.tipoDestinatario = tipoDestinatario;
  }

  public java.lang.String getDestinatario() {
    return this.destinatario;
  }

  public void setDestinatario(java.lang.String destinatario) {
    this.destinatario = destinatario;
  }

  public java.lang.String getTipoServizio() {
    return this.tipoServizio;
  }

  public void setTipoServizio(java.lang.String tipoServizio) {
    this.tipoServizio = tipoServizio;
  }

  public java.lang.String getServizio() {
    return this.servizio;
  }

  public void setServizio(java.lang.String servizio) {
    this.servizio = servizio;
  }

  public int getVersioneServizio() {
    return this.versioneServizio;
  }

  public void setVersioneServizio(int versioneServizio) {
    this.versioneServizio = versioneServizio;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  public java.lang.String getServizioApplicativo() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativo(java.lang.String servizioApplicativo) {
    this.servizioApplicativo = servizioApplicativo;
  }

  public java.lang.String getTrasportoMittente() {
    return this.trasportoMittente;
  }

  public void setTrasportoMittente(java.lang.String trasportoMittente) {
    this.trasportoMittente = trasportoMittente;
  }

  public java.lang.String getTokenIssuer() {
    return this.tokenIssuer;
  }

  public void setTokenIssuer(java.lang.String tokenIssuer) {
    this.tokenIssuer = tokenIssuer;
  }

  public java.lang.String getTokenClientId() {
    return this.tokenClientId;
  }

  public void setTokenClientId(java.lang.String tokenClientId) {
    this.tokenClientId = tokenClientId;
  }

  public java.lang.String getTokenSubject() {
    return this.tokenSubject;
  }

  public void setTokenSubject(java.lang.String tokenSubject) {
    this.tokenSubject = tokenSubject;
  }

  public java.lang.String getTokenUsername() {
    return this.tokenUsername;
  }

  public void setTokenUsername(java.lang.String tokenUsername) {
    this.tokenUsername = tokenUsername;
  }

  public java.lang.String getTokenMail() {
    return this.tokenMail;
  }

  public void setTokenMail(java.lang.String tokenMail) {
    this.tokenMail = tokenMail;
  }

  public java.lang.Integer getEsito() {
    return this.esito;
  }

  public void setEsito(java.lang.Integer esito) {
    this.esito = esito;
  }

  public java.lang.String getEsitoContesto() {
    return this.esitoContesto;
  }

  public void setEsitoContesto(java.lang.String esitoContesto) {
    this.esitoContesto = esitoContesto;
  }

  public java.lang.String getClientAddress() {
    return this.clientAddress;
  }

  public void setClientAddress(java.lang.String clientAddress) {
    this.clientAddress = clientAddress;
  }

  public java.lang.String getGruppi() {
    return this.gruppi;
  }

  public void setGruppi(java.lang.String gruppi) {
    this.gruppi = gruppi;
  }

  public java.lang.String getUriApi() {
    return this.uriApi;
  }

  public void setUriApi(java.lang.String uriApi) {
    this.uriApi = uriApi;
  }

  public java.lang.String getClusterId() {
    return this.clusterId;
  }

  public void setClusterId(java.lang.String clusterId) {
    this.clusterId = clusterId;
  }

  public java.lang.Integer getNumeroTransazioni() {
    return this.numeroTransazioni;
  }

  public void setNumeroTransazioni(java.lang.Integer numeroTransazioni) {
    this.numeroTransazioni = numeroTransazioni;
  }

  public java.lang.Long getDimensioniBytesBandaComplessiva() {
    return this.dimensioniBytesBandaComplessiva;
  }

  public void setDimensioniBytesBandaComplessiva(java.lang.Long dimensioniBytesBandaComplessiva) {
    this.dimensioniBytesBandaComplessiva = dimensioniBytesBandaComplessiva;
  }

  public java.lang.Long getDimensioniBytesBandaInterna() {
    return this.dimensioniBytesBandaInterna;
  }

  public void setDimensioniBytesBandaInterna(java.lang.Long dimensioniBytesBandaInterna) {
    this.dimensioniBytesBandaInterna = dimensioniBytesBandaInterna;
  }

  public java.lang.Long getDimensioniBytesBandaEsterna() {
    return this.dimensioniBytesBandaEsterna;
  }

  public void setDimensioniBytesBandaEsterna(java.lang.Long dimensioniBytesBandaEsterna) {
    this.dimensioniBytesBandaEsterna = dimensioniBytesBandaEsterna;
  }

  public java.lang.Long getLatenzaTotale() {
    return this.latenzaTotale;
  }

  public void setLatenzaTotale(java.lang.Long latenzaTotale) {
    this.latenzaTotale = latenzaTotale;
  }

  public java.lang.Long getLatenzaPorta() {
    return this.latenzaPorta;
  }

  public void setLatenzaPorta(java.lang.Long latenzaPorta) {
    this.latenzaPorta = latenzaPorta;
  }

  public java.lang.Long getLatenzaServizio() {
    return this.latenzaServizio;
  }

  public void setLatenzaServizio(java.lang.Long latenzaServizio) {
    this.latenzaServizio = latenzaServizio;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date data;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="stato-record",required=true,nillable=false)
  protected int statoRecord;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-porta",required=true,nillable=false)
  protected java.lang.String idPorta;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoPorta;

  @XmlElement(name="tipo-porta",required=true,nillable=false)
  protected TipoPorta tipoPorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-mittente",required=true,nillable=false)
  protected java.lang.String tipoMittente;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="mittente",required=true,nillable=false)
  protected java.lang.String mittente;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-destinatario",required=true,nillable=false)
  protected java.lang.String tipoDestinatario;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="destinatario",required=true,nillable=false)
  protected java.lang.String destinatario;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-servizio",required=true,nillable=false)
  protected java.lang.String tipoServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio",required=true,nillable=false)
  protected java.lang.String servizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="versione-servizio",required=true,nillable=false)
  protected int versioneServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=true,nillable=false)
  protected java.lang.String azione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  protected java.lang.String servizioApplicativo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="trasporto-mittente",required=true,nillable=false)
  protected java.lang.String trasportoMittente;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-issuer",required=true,nillable=false)
  protected java.lang.String tokenIssuer;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-client-id",required=true,nillable=false)
  protected java.lang.String tokenClientId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-subject",required=true,nillable=false)
  protected java.lang.String tokenSubject;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-username",required=true,nillable=false)
  protected java.lang.String tokenUsername;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-mail",required=true,nillable=false)
  protected java.lang.String tokenMail;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="esito",required=true,nillable=false)
  protected java.lang.Integer esito;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="esito-contesto",required=true,nillable=false)
  protected java.lang.String esitoContesto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="client-address",required=true,nillable=false)
  protected java.lang.String clientAddress;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="gruppi",required=true,nillable=false)
  protected java.lang.String gruppi;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="uri-api",required=true,nillable=false)
  protected java.lang.String uriApi;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="cluster-id",required=true,nillable=false)
  protected java.lang.String clusterId;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="numero-transazioni",required=true,nillable=false)
  protected java.lang.Integer numeroTransazioni;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="dimensioni-bytes-banda-complessiva",required=false,nillable=false)
  protected java.lang.Long dimensioniBytesBandaComplessiva;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="dimensioni-bytes-banda-interna",required=false,nillable=false)
  protected java.lang.Long dimensioniBytesBandaInterna;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="dimensioni-bytes-banda-esterna",required=false,nillable=false)
  protected java.lang.Long dimensioniBytesBandaEsterna;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="latenza-totale",required=false,nillable=false)
  protected java.lang.Long latenzaTotale;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="latenza-porta",required=false,nillable=false)
  protected java.lang.Long latenzaPorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="latenza-servizio",required=false,nillable=false)
  protected java.lang.Long latenzaServizio;

}
