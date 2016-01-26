/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for invocazione-servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-servizio">
 * 		&lt;sequence>
 * 			&lt;element name="credenziali" type="{http://www.openspcoop2.org/core/config}credenziali" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/config}connettore" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="gestione-errore" type="{http://www.openspcoop2.org/core/config}gestione-errore" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="sbustamento-soap" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="sbustamento-informazioni-protocollo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="get-message" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="autenticazione" type="{http://www.openspcoop2.org/core/config}InvocazioneServizioTipoAutenticazione" use="optional" default="none"/>
 * 		&lt;attribute name="invio-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="risposta-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * &lt;/complexType>
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

public class InvocazioneServizio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public InvocazioneServizio() {
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

  public Credenziali getCredenziali() {
    return this.credenziali;
  }

  public void setCredenziali(Credenziali credenziali) {
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

  public void set_value_sbustamentoSoap(String value) {
    this.sbustamentoSoap = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_sbustamentoSoap() {
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

  public void set_value_sbustamentoInformazioniProtocollo(String value) {
    this.sbustamentoInformazioniProtocollo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_sbustamentoInformazioniProtocollo() {
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

  public void set_value_getMessage(String value) {
    this.getMessage = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_getMessage() {
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

  public void set_value_autenticazione(String value) {
    this.autenticazione = (InvocazioneServizioTipoAutenticazione) InvocazioneServizioTipoAutenticazione.toEnumConstantFromString(value);
  }

  public String get_value_autenticazione() {
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

  public void set_value_invioPerRiferimento(String value) {
    this.invioPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_invioPerRiferimento() {
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

  public void set_value_rispostaPerRiferimento(String value) {
    this.rispostaPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_rispostaPerRiferimento() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="credenziali",required=false,nillable=false)
  protected Credenziali credenziali;

  @XmlElement(name="connettore",required=false,nillable=false)
  protected Connettore connettore;

  @XmlElement(name="gestione-errore",required=false,nillable=false)
  protected GestioneErrore gestioneErrore;

  @XmlTransient
  protected java.lang.String _value_sbustamentoSoap;

  @XmlAttribute(name="sbustamento-soap",required=false)
  protected StatoFunzionalita sbustamentoSoap = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_sbustamentoInformazioniProtocollo;

  @XmlAttribute(name="sbustamento-informazioni-protocollo",required=false)
  protected StatoFunzionalita sbustamentoInformazioniProtocollo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @XmlTransient
  protected java.lang.String _value_getMessage;

  @XmlAttribute(name="get-message",required=false)
  protected StatoFunzionalita getMessage = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_autenticazione;

  @XmlAttribute(name="autenticazione",required=false)
  protected InvocazioneServizioTipoAutenticazione autenticazione = (InvocazioneServizioTipoAutenticazione) InvocazioneServizioTipoAutenticazione.toEnumConstantFromString("none");

  @XmlTransient
  protected java.lang.String _value_invioPerRiferimento;

  @XmlAttribute(name="invio-per-riferimento",required=false)
  protected StatoFunzionalita invioPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_rispostaPerRiferimento;

  @XmlAttribute(name="risposta-per-riferimento",required=false)
  protected StatoFunzionalita rispostaPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
