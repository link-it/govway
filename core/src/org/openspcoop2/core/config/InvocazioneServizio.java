/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for invocazione-servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-servizio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="credenziali" type="{http://www.openspcoop2.org/core/config}invocazione-credenziali" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/config}connettore" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="gestione-errore" type="{http://www.openspcoop2.org/core/config}gestione-errore" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="sbustamento-soap" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="sbustamento-informazioni-protocollo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="get-message" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="autenticazione" type="{http://www.openspcoop2.org/core/config}InvocazioneServizioTipoAutenticazione" use="optional" default="none"/&gt;
 * 		&lt;attribute name="invio-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="risposta-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invocazione-servizio", 
  propOrder = {
  	"credenziali",
  	"connettore",
  	"gestioneErrore"
  }
)

@XmlRootElement(name = "invocazione-servizio")

public class InvocazioneServizio extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public InvocazioneServizio() {
    super();
  }

  public InvocazioneCredenziali getCredenziali() {
    return this.credenziali;
  }

  public void setCredenziali(InvocazioneCredenziali credenziali) {
    this.credenziali = credenziali;
  }

  public Connettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(Connettore connettore) {
    this.connettore = connettore;
  }

  public GestioneErrore getGestioneErrore() {
    return this.gestioneErrore;
  }

  public void setGestioneErrore(GestioneErrore gestioneErrore) {
    this.gestioneErrore = gestioneErrore;
  }

  public void setSbustamentoSoapRawEnumValue(String value) {
    this.sbustamentoSoap = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getSbustamentoSoapRawEnumValue() {
    if(this.sbustamentoSoap == null){
    	return null;
    }else{
    	return this.sbustamentoSoap.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getSbustamentoSoap() {
    return this.sbustamentoSoap;
  }

  public void setSbustamentoSoap(org.openspcoop2.core.config.constants.StatoFunzionalita sbustamentoSoap) {
    this.sbustamentoSoap = sbustamentoSoap;
  }

  public void setSbustamentoInformazioniProtocolloRawEnumValue(String value) {
    this.sbustamentoInformazioniProtocollo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getSbustamentoInformazioniProtocolloRawEnumValue() {
    if(this.sbustamentoInformazioniProtocollo == null){
    	return null;
    }else{
    	return this.sbustamentoInformazioniProtocollo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getSbustamentoInformazioniProtocollo() {
    return this.sbustamentoInformazioniProtocollo;
  }

  public void setSbustamentoInformazioniProtocollo(org.openspcoop2.core.config.constants.StatoFunzionalita sbustamentoInformazioniProtocollo) {
    this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
  }

  public void setGetMessageRawEnumValue(String value) {
    this.getMessage = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getGetMessageRawEnumValue() {
    if(this.getMessage == null){
    	return null;
    }else{
    	return this.getMessage.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getGetMessage() {
    return this.getMessage;
  }

  public void setGetMessage(org.openspcoop2.core.config.constants.StatoFunzionalita getMessage) {
    this.getMessage = getMessage;
  }

  public void setAutenticazioneRawEnumValue(String value) {
    this.autenticazione = (InvocazioneServizioTipoAutenticazione) InvocazioneServizioTipoAutenticazione.toEnumConstantFromString(value);
  }

  public String getAutenticazioneRawEnumValue() {
    if(this.autenticazione == null){
    	return null;
    }else{
    	return this.autenticazione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione getAutenticazione() {
    return this.autenticazione;
  }

  public void setAutenticazione(org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione autenticazione) {
    this.autenticazione = autenticazione;
  }

  public void setInvioPerRiferimentoRawEnumValue(String value) {
    this.invioPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getInvioPerRiferimentoRawEnumValue() {
    if(this.invioPerRiferimento == null){
    	return null;
    }else{
    	return this.invioPerRiferimento.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getInvioPerRiferimento() {
    return this.invioPerRiferimento;
  }

  public void setInvioPerRiferimento(org.openspcoop2.core.config.constants.StatoFunzionalita invioPerRiferimento) {
    this.invioPerRiferimento = invioPerRiferimento;
  }

  public void setRispostaPerRiferimentoRawEnumValue(String value) {
    this.rispostaPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getRispostaPerRiferimentoRawEnumValue() {
    if(this.rispostaPerRiferimento == null){
    	return null;
    }else{
    	return this.rispostaPerRiferimento.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRispostaPerRiferimento() {
    return this.rispostaPerRiferimento;
  }

  public void setRispostaPerRiferimento(org.openspcoop2.core.config.constants.StatoFunzionalita rispostaPerRiferimento) {
    this.rispostaPerRiferimento = rispostaPerRiferimento;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="credenziali",required=false,nillable=false)
  protected InvocazioneCredenziali credenziali;

  @XmlElement(name="connettore",required=false,nillable=false)
  protected Connettore connettore;

  @XmlElement(name="gestione-errore",required=false,nillable=false)
  protected GestioneErrore gestioneErrore;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String sbustamentoSoapRawEnumValue;

  @XmlAttribute(name="sbustamento-soap",required=false)
  protected StatoFunzionalita sbustamentoSoap = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String sbustamentoInformazioniProtocolloRawEnumValue;

  @XmlAttribute(name="sbustamento-informazioni-protocollo",required=false)
  protected StatoFunzionalita sbustamentoInformazioniProtocollo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String getMessageRawEnumValue;

  @XmlAttribute(name="get-message",required=false)
  protected StatoFunzionalita getMessage = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String autenticazioneRawEnumValue;

  @XmlAttribute(name="autenticazione",required=false)
  protected InvocazioneServizioTipoAutenticazione autenticazione = (InvocazioneServizioTipoAutenticazione) InvocazioneServizioTipoAutenticazione.toEnumConstantFromString("none");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String invioPerRiferimentoRawEnumValue;

  @XmlAttribute(name="invio-per-riferimento",required=false)
  protected StatoFunzionalita invioPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String rispostaPerRiferimentoRawEnumValue;

  @XmlAttribute(name="risposta-per-riferimento",required=false)
  protected StatoFunzionalita rispostaPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
