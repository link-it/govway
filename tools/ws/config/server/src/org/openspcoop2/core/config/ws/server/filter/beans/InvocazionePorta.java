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
 * <p>Java class for InvocazionePorta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-porta"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="gestione-errore" type="{http://www.openspcoop2.org/core/config/management}invocazione-porta-gestione-errore" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="invio-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="sbustamento-informazioni-protocollo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.ws.server.filter.beans.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.constants.StatoFunzionalita;

/**     
 * InvocazionePorta
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "invocazione-porta", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "gestioneErrore",
    "invioPerRiferimento",
    "sbustamentoInformazioniProtocollo"
})
@javax.xml.bind.annotation.XmlRootElement(name = "invocazione-porta")
public class InvocazionePorta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="gestione-errore",required=false,nillable=false)
	private InvocazionePortaGestioneErrore gestioneErrore;
	
	public void setGestioneErrore(InvocazionePortaGestioneErrore gestioneErrore){
		this.gestioneErrore = gestioneErrore;
	}
	
	public InvocazionePortaGestioneErrore getGestioneErrore(){
		return this.gestioneErrore;
	}
	
	
	@XmlElement(name="invio-per-riferimento",required=false,nillable=false)
	private StatoFunzionalita invioPerRiferimento;
	
	public void setInvioPerRiferimento(StatoFunzionalita invioPerRiferimento){
		this.invioPerRiferimento = invioPerRiferimento;
	}
	
	public StatoFunzionalita getInvioPerRiferimento(){
		return this.invioPerRiferimento;
	}
	
	
	@XmlElement(name="sbustamento-informazioni-protocollo",required=false,nillable=false)
	private StatoFunzionalita sbustamentoInformazioniProtocollo;
	
	public void setSbustamentoInformazioniProtocollo(StatoFunzionalita sbustamentoInformazioniProtocollo){
		this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
	}
	
	public StatoFunzionalita getSbustamentoInformazioniProtocollo(){
		return this.sbustamentoInformazioniProtocollo;
	}
	
	
	
	
}