/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for InvocazioneServizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-servizio"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="credenziali" type="{http://www.openspcoop2.org/core/config/management}invocazione-credenziali" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="connettore" type="{http://www.openspcoop2.org/core/config/management}connettore" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="sbustamento-soap" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="sbustamento-informazioni-protocollo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="get-message" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="autenticazione" type="{http://www.openspcoop2.org/core/config}InvocazioneServizioTipoAutenticazione" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="invio-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="risposta-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.ws.server.filter.beans.InvocazioneCredenziali;
import org.openspcoop2.core.config.ws.server.filter.beans.Connettore;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;

/**     
 * InvocazioneServizio
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "invocazione-servizio", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "credenziali",
    "connettore",
    "sbustamentoSoap",
    "sbustamentoInformazioniProtocollo",
    "getMessage",
    "autenticazione",
    "invioPerRiferimento",
    "rispostaPerRiferimento"
})
@javax.xml.bind.annotation.XmlRootElement(name = "invocazione-servizio")
public class InvocazioneServizio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="credenziali",required=false,nillable=false)
	private InvocazioneCredenziali credenziali;
	
	public void setCredenziali(InvocazioneCredenziali credenziali){
		this.credenziali = credenziali;
	}
	
	public InvocazioneCredenziali getCredenziali(){
		return this.credenziali;
	}
	
	
	@XmlElement(name="connettore",required=false,nillable=false)
	private Connettore connettore;
	
	public void setConnettore(Connettore connettore){
		this.connettore = connettore;
	}
	
	public Connettore getConnettore(){
		return this.connettore;
	}
	
	
	@XmlElement(name="sbustamento-soap",required=false,nillable=false)
	private StatoFunzionalita sbustamentoSoap;
	
	public void setSbustamentoSoap(StatoFunzionalita sbustamentoSoap){
		this.sbustamentoSoap = sbustamentoSoap;
	}
	
	public StatoFunzionalita getSbustamentoSoap(){
		return this.sbustamentoSoap;
	}
	
	
	@XmlElement(name="sbustamento-informazioni-protocollo",required=false,nillable=false)
	private StatoFunzionalita sbustamentoInformazioniProtocollo;
	
	public void setSbustamentoInformazioniProtocollo(StatoFunzionalita sbustamentoInformazioniProtocollo){
		this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
	}
	
	public StatoFunzionalita getSbustamentoInformazioniProtocollo(){
		return this.sbustamentoInformazioniProtocollo;
	}
	
	
	@XmlElement(name="get-message",required=false,nillable=false)
	private StatoFunzionalita getMessage;
	
	public void setGetMessage(StatoFunzionalita getMessage){
		this.getMessage = getMessage;
	}
	
	public StatoFunzionalita getGetMessage(){
		return this.getMessage;
	}
	
	
	@XmlElement(name="autenticazione",required=false,nillable=false)
	private InvocazioneServizioTipoAutenticazione autenticazione;
	
	public void setAutenticazione(InvocazioneServizioTipoAutenticazione autenticazione){
		this.autenticazione = autenticazione;
	}
	
	public InvocazioneServizioTipoAutenticazione getAutenticazione(){
		return this.autenticazione;
	}
	
	
	@XmlElement(name="invio-per-riferimento",required=false,nillable=false)
	private StatoFunzionalita invioPerRiferimento;
	
	public void setInvioPerRiferimento(StatoFunzionalita invioPerRiferimento){
		this.invioPerRiferimento = invioPerRiferimento;
	}
	
	public StatoFunzionalita getInvioPerRiferimento(){
		return this.invioPerRiferimento;
	}
	
	
	@XmlElement(name="risposta-per-riferimento",required=false,nillable=false)
	private StatoFunzionalita rispostaPerRiferimento;
	
	public void setRispostaPerRiferimento(StatoFunzionalita rispostaPerRiferimento){
		this.rispostaPerRiferimento = rispostaPerRiferimento;
	}
	
	public StatoFunzionalita getRispostaPerRiferimento(){
		return this.rispostaPerRiferimento;
	}
	
	
	
	
}