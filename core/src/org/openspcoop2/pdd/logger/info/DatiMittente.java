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

package org.openspcoop2.pdd.logger.info;

/**
 * DatiMittente
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DatiMittente {

	private String tokenUsername;
	private String tokenSubject;
	private String tokenIssuer;
	private String tokenClientId;
	
	private String trasportoMittente;
	private String tipoTrasportoMittente;
	
	private String servizioApplicativoFruitore;
	
	private String soggettoFruitore;
	private String tipoSoggettoFruitore;
	private String nomeSoggettoFruitore;
	
	private org.openspcoop2.core.transazioni.constants.PddRuolo pddRuolo;
	private String soggettoOperativo;
	
	private String transportClientAddress;
	private String socketClientAddress;
	
	public String getTokenUsername() {
		return this.tokenUsername;
	}
	public void setTokenUsername(String tokenUsername) {
		this.tokenUsername = tokenUsername;
	}
	public String getTokenSubject() {
		return this.tokenSubject;
	}
	public void setTokenSubject(String tokenSubject) {
		this.tokenSubject = tokenSubject;
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
	public String getTrasportoMittente() {
		return this.trasportoMittente;
	}
	public void setTrasportoMittente(String trasportoMittente) {
		this.trasportoMittente = trasportoMittente;
	}
	public String getTipoTrasportoMittente() {
		return this.tipoTrasportoMittente;
	}
	public void setTipoTrasportoMittente(String tipoTrasportoMittente) {
		this.tipoTrasportoMittente = tipoTrasportoMittente;
	}
	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}
	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}
	public String getSoggettoFruitore() {
		return this.soggettoFruitore;
	}
	public void setSoggettoFruitore(String soggettoFruitore) {
		this.soggettoFruitore = soggettoFruitore;
	}
	public String getTipoSoggettoFruitore() {
		return this.tipoSoggettoFruitore;
	}
	public void setTipoSoggettoFruitore(String tipoSoggettoFruitore) {
		this.tipoSoggettoFruitore = tipoSoggettoFruitore;
	}
	public String getNomeSoggettoFruitore() {
		return this.nomeSoggettoFruitore;
	}
	public void setNomeSoggettoFruitore(String nomeSoggettoFruitore) {
		this.nomeSoggettoFruitore = nomeSoggettoFruitore;
	}
	public org.openspcoop2.core.transazioni.constants.PddRuolo getPddRuolo() {
		return this.pddRuolo;
	}
	public void setPddRuolo(org.openspcoop2.core.transazioni.constants.PddRuolo pddRuolo) {
		this.pddRuolo = pddRuolo;
	}
	public String getSoggettoOperativo() {
		return this.soggettoOperativo;
	}
	public void setSoggettoOperativo(String soggettoOperativo) {
		this.soggettoOperativo = soggettoOperativo;
	}	
	public String getTransportClientAddress() {
		return this.transportClientAddress;
	}
	public void setTransportClientAddress(String transportClientAddress) {
		this.transportClientAddress = transportClientAddress;
	}
	public String getSocketClientAddress() {
		return this.socketClientAddress;
	}
	public void setSocketClientAddress(String socketClientAddress) {
		this.socketClientAddress = socketClientAddress;
	}
	
}
