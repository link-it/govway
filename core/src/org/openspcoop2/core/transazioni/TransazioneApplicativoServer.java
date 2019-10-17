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
package org.openspcoop2.core.transazioni;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for transazione-applicativo-server complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transazione-applicativo-server">
 * 		&lt;sequence>
 * 			&lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="data-uscita-richiesta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-accettazione-risposta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-ingresso-risposta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="richiesta-uscita-bytes" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="risposta-ingresso-bytes" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="codice-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-primo-tentativo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="codice-risposta-ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ultimo-errore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="numero-tentativi" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1" default="0"/>
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
@XmlType(name = "transazione-applicativo-server", 
  propOrder = {
  	"idTransazione",
  	"servizioApplicativoErogatore",
  	"dataUscitaRichiesta",
  	"dataAccettazioneRisposta",
  	"dataIngressoRisposta",
  	"richiestaUscitaBytes",
  	"rispostaIngressoBytes",
  	"codiceRisposta",
  	"dataPrimoTentativo",
  	"dataUltimoErrore",
  	"codiceRispostaUltimoErrore",
  	"ultimoErrore",
  	"numeroTentativi"
  }
)

@XmlRootElement(name = "transazione-applicativo-server")

public class TransazioneApplicativoServer extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TransazioneApplicativoServer() {
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

  public java.lang.String getServizioApplicativoErogatore() {
    return this.servizioApplicativoErogatore;
  }

  public void setServizioApplicativoErogatore(java.lang.String servizioApplicativoErogatore) {
    this.servizioApplicativoErogatore = servizioApplicativoErogatore;
  }

  public java.util.Date getDataUscitaRichiesta() {
    return this.dataUscitaRichiesta;
  }

  public void setDataUscitaRichiesta(java.util.Date dataUscitaRichiesta) {
    this.dataUscitaRichiesta = dataUscitaRichiesta;
  }

  public java.util.Date getDataAccettazioneRisposta() {
    return this.dataAccettazioneRisposta;
  }

  public void setDataAccettazioneRisposta(java.util.Date dataAccettazioneRisposta) {
    this.dataAccettazioneRisposta = dataAccettazioneRisposta;
  }

  public java.util.Date getDataIngressoRisposta() {
    return this.dataIngressoRisposta;
  }

  public void setDataIngressoRisposta(java.util.Date dataIngressoRisposta) {
    this.dataIngressoRisposta = dataIngressoRisposta;
  }

  public java.lang.Long getRichiestaUscitaBytes() {
    return this.richiestaUscitaBytes;
  }

  public void setRichiestaUscitaBytes(java.lang.Long richiestaUscitaBytes) {
    this.richiestaUscitaBytes = richiestaUscitaBytes;
  }

  public java.lang.Long getRispostaIngressoBytes() {
    return this.rispostaIngressoBytes;
  }

  public void setRispostaIngressoBytes(java.lang.Long rispostaIngressoBytes) {
    this.rispostaIngressoBytes = rispostaIngressoBytes;
  }

  public java.lang.String getCodiceRisposta() {
    return this.codiceRisposta;
  }

  public void setCodiceRisposta(java.lang.String codiceRisposta) {
    this.codiceRisposta = codiceRisposta;
  }

  public java.util.Date getDataPrimoTentativo() {
    return this.dataPrimoTentativo;
  }

  public void setDataPrimoTentativo(java.util.Date dataPrimoTentativo) {
    this.dataPrimoTentativo = dataPrimoTentativo;
  }

  public java.util.Date getDataUltimoErrore() {
    return this.dataUltimoErrore;
  }

  public void setDataUltimoErrore(java.util.Date dataUltimoErrore) {
    this.dataUltimoErrore = dataUltimoErrore;
  }

  public java.lang.String getCodiceRispostaUltimoErrore() {
    return this.codiceRispostaUltimoErrore;
  }

  public void setCodiceRispostaUltimoErrore(java.lang.String codiceRispostaUltimoErrore) {
    this.codiceRispostaUltimoErrore = codiceRispostaUltimoErrore;
  }

  public java.lang.String getUltimoErrore() {
    return this.ultimoErrore;
  }

  public void setUltimoErrore(java.lang.String ultimoErrore) {
    this.ultimoErrore = ultimoErrore;
  }

  public int getNumeroTentativi() {
    return this.numeroTentativi;
  }

  public void setNumeroTentativi(int numeroTentativi) {
    this.numeroTentativi = numeroTentativi;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.transazioni.model.TransazioneApplicativoServerModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.transazioni.TransazioneApplicativoServer.modelStaticInstance==null){
  			org.openspcoop2.core.transazioni.TransazioneApplicativoServer.modelStaticInstance = new org.openspcoop2.core.transazioni.model.TransazioneApplicativoServerModel();
	  }
  }
  public static org.openspcoop2.core.transazioni.model.TransazioneApplicativoServerModel model(){
	  if(org.openspcoop2.core.transazioni.TransazioneApplicativoServer.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.transazioni.TransazioneApplicativoServer.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-transazione",required=true,nillable=false)
  protected java.lang.String idTransazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-erogatore",required=true,nillable=false)
  protected java.lang.String servizioApplicativoErogatore;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-uscita-richiesta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataUscitaRichiesta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-accettazione-risposta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataAccettazioneRisposta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-ingresso-risposta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataIngressoRisposta;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="richiesta-uscita-bytes",required=false,nillable=false)
  protected java.lang.Long richiestaUscitaBytes;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="risposta-ingresso-bytes",required=false,nillable=false)
  protected java.lang.Long rispostaIngressoBytes;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice-risposta",required=false,nillable=false)
  protected java.lang.String codiceRisposta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-primo-tentativo",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataPrimoTentativo;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-ultimo-errore",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataUltimoErrore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice-risposta-ultimo-errore",required=false,nillable=false)
  protected java.lang.String codiceRispostaUltimoErrore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="ultimo-errore",required=false,nillable=false)
  protected java.lang.String ultimoErrore;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="numero-tentativi",required=false,nillable=false,defaultValue="0")
  protected int numeroTentativi = 0;

}
