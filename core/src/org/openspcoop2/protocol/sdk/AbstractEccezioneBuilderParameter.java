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

package org.openspcoop2.protocol.sdk;

import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.config.ConfigurationRFC7807;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.soap.SOAPFaultCode;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.utils.date.DateManager;

/**
 * AbstractEccezioneBuilderParameter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractEccezioneBuilderParameter {

	private ProprietaErroreApplicativo proprieta;
	private IDSoggetto dominioPorta;
	private IDSoggetto mittente;
	private IDServizio servizio;
	private DettaglioEccezione dettaglioEccezionePdD;
	private MessageType messageType;
	private String idFunzione;
	private String servizioApplicativo;
	private TipoPdD tipoPorta;
	private Date oraRegistrazione = DateManager.getDate();
	private ParseException parseException;
	private ConfigurationRFC7807 rfc7807;
	private int httpStatus;
	private String nomePorta;
	private String transactionId;
	
	public ParseException getParseException() {
		return this.parseException;
	}
	public void setParseException(ParseException parseException) {
		this.parseException = parseException;
	}
	public Date getOraRegistrazione() {
		return this.oraRegistrazione;
	}
	public void setOraRegistrazione(Date oraRegistrazione) {
		this.oraRegistrazione = oraRegistrazione;
	}
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	public TipoPdD getTipoPorta() {
		return this.tipoPorta;
	}
	public void setTipoPorta(TipoPdD tipoPorta) {
		this.tipoPorta = tipoPorta;
	}
	public String getIdFunzione() {
		return this.idFunzione;
	}
	public void setIdFunzione(String idFunzione) {
		this.idFunzione = idFunzione;
	}
	public ProprietaErroreApplicativo getProprieta() {
		return this.proprieta;
	}
	public void setProprieta(ProprietaErroreApplicativo proprieta) {
		this.proprieta = proprieta;
	}
	public IDSoggetto getDominioPorta() {
		return this.dominioPorta;
	}
	public void setDominioPorta(IDSoggetto dominioPorta) {
		this.dominioPorta = dominioPorta;
	}
	public DettaglioEccezione getDettaglioEccezionePdD() {
		return this.dettaglioEccezionePdD;
	}
	public void setDettaglioEccezionePdD(DettaglioEccezione dettaglioEccezionePdD) {
		this.dettaglioEccezionePdD = dettaglioEccezionePdD;
	}
	public MessageType getMessageType() {
		return this.messageType;
	}
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	public IDSoggetto getMittente() {
		return this.mittente;
	}
	public void setMittente(IDSoggetto mittente) {
		this.mittente = mittente;
	}
	public IDServizio getServizio() {
		return this.servizio;
	}
	public void setServizio(IDServizio servizio) {
		this.servizio = servizio;
	}
	public ConfigurationRFC7807 getRfc7807() {
		return this.rfc7807;
	}
	public void setRfc7807(ConfigurationRFC7807 rfc7807) {
		this.rfc7807 = rfc7807;
	}
	public int getHttpStatus() {
		return this.httpStatus;
	}
	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
	public String getNomePorta() {
		return this.nomePorta;
	}
	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}
	public String getTransactionId() {
		return this.transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public abstract SOAPFaultCode getSoapFaultCode();
}
