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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for InvocazionePorta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-porta">
 *     &lt;sequence>
 *         &lt;element name="gestione-errore" type="{http://www.openspcoop2.org/core/config/management}invocazione-porta-gestione-errore" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="invio-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" default="(StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato")" />
 *         &lt;element name="sbustamento-informazioni-protocollo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" default="(StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato")" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.ws.server.filter.beans.InvocazionePortaGestioneErrore;

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
	
	
	@XmlElement(name="invio-per-riferimento",required=false,nillable=false,defaultValue="disabilitato")
	private StatoFunzionalita invioPerRiferimento = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");
	
	public void setInvioPerRiferimento(StatoFunzionalita invioPerRiferimento){
		this.invioPerRiferimento = invioPerRiferimento;
	}
	
	public StatoFunzionalita getInvioPerRiferimento(){
		return this.invioPerRiferimento;
	}
	
	
	@XmlElement(name="sbustamento-informazioni-protocollo",required=false,nillable=false,defaultValue="abilitato")
	private StatoFunzionalita sbustamentoInformazioniProtocollo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");
	
	public void setSbustamentoInformazioniProtocollo(StatoFunzionalita sbustamentoInformazioniProtocollo){
		this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
	}
	
	public StatoFunzionalita getSbustamentoInformazioniProtocollo(){
		return this.sbustamentoInformazioniProtocollo;
	}
	
	
	
	
}