/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.statistic;

import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.Utilities;

/**
 * StatisticBean
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticBean {

	private long id;
	private Date dateIntervalLeft;
	private Date dateIntervalRight;
	private Date data;
	private String idPorta;
	private TipoPdD tipoPorta;
	private IDSoggetto mittente;
	private IDSoggetto destinatario;
	private String tipoServizio;
	private String servizio;
	private Integer versioneServizio;
	private String azione;
	private String servizioApplicativo;
	private String trasportoMittente;
	private String tokenIssuer;
	private String tokenClientId;
	private String tokenSubject;
	private String tokenUsername;
	private String tokenMail;
	private Integer esito;
	private String esitoContesto;
	private String gruppo;
	private String api;
	private String clusterId;
	private String clientAddress;
	private long richieste;
	private long bytesBandaTotale;
	private long bytesBandaInterna;
	private long bytesBandaEsterna;
	private long latenzaTotale;
	private long latenzaServizio;
	private long latenzaPorta;
	private String idStatistica;
	private String pluginClassname;

	public String getPluginClassname() {
		return this.pluginClassname;
	}
	public void setPluginClassname(String pluginClassname) {
		this.pluginClassname = pluginClassname;
	}
	public String getIdStatistica() {
		return this.idStatistica;
	}
	public void setIdStatistica(String idStatistica) {
		this.idStatistica = idStatistica;
	}
 	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getData() {
		return this.data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Date getDateIntervalLeft() {
		return this.dateIntervalLeft;
	}
	public void setDateIntervalLeft(Date dateIntervalLeft) {
		this.dateIntervalLeft = dateIntervalLeft;
	}
	public Date getDateIntervalRight() {
		return this.dateIntervalRight;
	}
	public void setDateIntervalRight(Date dateIntervalRight) {
		this.dateIntervalRight = dateIntervalRight;
	}
	public String getIdPorta() {
		return this.idPorta;
	}
	public void setIdPorta(String id_porta) {
		this.idPorta = id_porta;
	}
	public TipoPdD getTipoPorta() {
		return this.tipoPorta;
	}
	public void setTipoPorta(TipoPdD tipo_porta) {
		this.tipoPorta = tipo_porta;
	}
	public IDSoggetto getMittente() {
		return this.mittente;
	}
	public void setMittente(IDSoggetto mittente) {
		this.mittente = mittente;
	}
	public IDSoggetto getDestinatario() {
		return this.destinatario;
	}
	public void setDestinatario(IDSoggetto destinatario) {
		this.destinatario = destinatario;
	}
	public String getTipoServizio() {
		return this.tipoServizio;
	}
	public void setTipoServizio(String tipo_servizio) {
		this.tipoServizio = tipo_servizio;
	}
	public String getServizio() {
		return this.servizio;
	}
	public void setServizio(String servizio) {
		this.servizio = servizio;
	}
	public Integer getVersioneServizio() {
		return this.versioneServizio;
	}
	public void setVersioneServizio(Integer versioneServizio) {
		this.versioneServizio = versioneServizio;
	}
	public String getAzione() {
		return this.azione;
	}
	public void setAzione(String azione) {
		this.azione = azione;
	}
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
	public void setServizioApplicativo(String servizio_applicativo) {
		this.servizioApplicativo = servizio_applicativo;
	}
	public String getTrasportoMittente() {
		return this.trasportoMittente;
	}
	public void setTrasportoMittente(String trasportoMittente) {
		this.trasportoMittente = trasportoMittente;
	}
	public String getTokenIssuer() {
		return this.tokenIssuer;
	}
	public void setTokenIssuer(String tokenIssuer) {
		this.tokenIssuer = tokenIssuer;
	}
	public String getTokenClientId() {
		return this.tokenClientId;
	}
	public void setTokenClientId(String tokenClientId) {
		this.tokenClientId = tokenClientId;
	}
	public String getTokenSubject() {
		return this.tokenSubject;
	}
	public void setTokenSubject(String tokenSubject) {
		this.tokenSubject = tokenSubject;
	}
	public String getTokenUsername() {
		return this.tokenUsername;
	}
	public void setTokenUsername(String tokenUsername) {
		this.tokenUsername = tokenUsername;
	}
	public String getTokenMail() {
		return this.tokenMail;
	}
	public void setTokenMail(String tokenMail) {
		this.tokenMail = tokenMail;
	}
	public Integer getEsito() {
		return this.esito;
	}
	public void setEsito(Integer esito) {
		this.esito = esito;
	}
	public String getEsitoContesto() {
		return this.esitoContesto;
	}
	public void setEsitoContesto(String esitoContesto) {
		this.esitoContesto = esitoContesto;
	}
	
	public String getGruppo() {
		return this.gruppo;
	}
	public void setGruppo(String gruppo) {
		this.gruppo = gruppo;
	}
	public String getApi() {
		return this.api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	public String getClusterId() {
		return this.clusterId;
	}
	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}
	public String getClientAddress() {
		return this.clientAddress;
	}
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}
	
	public long getRichieste() {
		return this.richieste;
	}
	public void setRichieste(long richieste) {
		this.richieste = richieste;
	}
	
	public long getBytesBandaTotale() {
		return this.bytesBandaTotale;
	}
	public void setBytesBandaTotale(long bytesBandaTotale) {
		this.bytesBandaTotale = bytesBandaTotale;
	}
	public long getBytesBandaInterna() {
		return this.bytesBandaInterna;
	}
	public void setBytesBandaInterna(long bytesBandaInterna) {
		this.bytesBandaInterna = bytesBandaInterna;
	}
	public long getBytesBandaEsterna() {
		return this.bytesBandaEsterna;
	}
	public void setBytesBandaEsterna(long bytesBandaEsterna) {
		this.bytesBandaEsterna = bytesBandaEsterna;
	}
	
	public long getLatenzaTotale() {
		return this.latenzaTotale;
	}
	public void setLatenzaTotale(long latenzaTotale) {
		this.latenzaTotale = latenzaTotale;
	}
	public long getLatenzaServizio() {
		return this.latenzaServizio;
	}
	public void setLatenzaServizio(long latenzaServizio) {
		this.latenzaServizio = latenzaServizio;
	}
	public long getLatenzaPorta() {
		return this.latenzaPorta;
	}
	public void setLatenzaPorta(long latenzaPorta) {
		this.latenzaPorta = latenzaPorta;
	}
	
	@Override
	public String toString(){
		
		StringBuilder bf = new StringBuilder();
		
		bf.append("data[");
		bf.append(this.data.toString());
		bf.append("] id-porta[");
		bf.append(this.idPorta);
		bf.append("] tipo-porta[");
		bf.append(this.tipoPorta.getTipo());
		bf.append("] mittente[");
		bf.append(this.mittente.toString());
		bf.append("] destinatario[");
		bf.append(this.destinatario.toString());
		bf.append("] servizio[");
		bf.append(this.tipoServizio);
		bf.append("/");
		bf.append(this.servizio);
		bf.append(":");
		bf.append(this.versioneServizio);
		bf.append("] azione[");
		bf.append(this.azione);
		bf.append("] servizio-applicativo[");
		bf.append(this.servizioApplicativo);
		bf.append("] trasporto-mittente[");
		bf.append(this.trasportoMittente);
		bf.append("] token-issuer[");
		bf.append(this.tokenIssuer);
		bf.append("] token-clientId[");
		bf.append(this.tokenClientId);
		bf.append("] token-subject[");
		bf.append(this.tokenSubject);
		bf.append("] token-username[");
		bf.append(this.tokenUsername);
		bf.append("] token-Mail[");
		bf.append(this.tokenMail);
		bf.append("] clientAddress[");
		bf.append(this.clientAddress);
		bf.append("] gruppo[");
		bf.append(this.gruppo);
		bf.append("] api[");
		bf.append(this.api);
		bf.append("] clusterId[");
		bf.append(this.clusterId);
		bf.append("] esito[");
		bf.append(this.esito);
		bf.append("] esito-contesto[");
		bf.append(this.esitoContesto);
		bf.append("] richieste[");
		bf.append(this.richieste);
		bf.append("] bytes-banda-totale[");
		bf.append(Utilities.convertBytesToFormatString(this.bytesBandaTotale));
		bf.append("] bytes-banda-interna[");
		bf.append(Utilities.convertBytesToFormatString(this.bytesBandaInterna));
		bf.append("] bytes-banda-esterna[");
		bf.append(Utilities.convertBytesToFormatString(this.bytesBandaEsterna));
		bf.append("] latenza-totale[");
		if(this.latenzaTotale>=0){
			bf.append(Utilities.convertSystemTimeIntoString_millisecondi(this.latenzaTotale,true));
		}
		else{
			bf.append("N.D.");
		}
		bf.append("] latenza-servizio[");
		if(this.latenzaServizio>=0){
			bf.append(Utilities.convertSystemTimeIntoString_millisecondi(this.latenzaServizio,true));
		}
		else{
			bf.append("N.D.");
		}
		bf.append("] latenza-porta[");
		if(this.latenzaPorta>=0){
			bf.append(Utilities.convertSystemTimeIntoString_millisecondi(this.latenzaPorta,true));
		}
		else{
			bf.append("N.D.");
		}
		bf.append("]");

		return bf.toString();
	}

	
}
