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

package org.openspcoop2.protocol.sdk.state;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.PortaDominio;

/**
 * RequestFruitoreInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequestFruitore implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String key = null;

	private boolean cached = false;

	private IDSoggetto idSoggettoFruitore;
	private org.openspcoop2.core.registry.Soggetto soggettoFruitoreRegistry;
	private org.openspcoop2.core.config.Soggetto soggettoFruitoreConfig;
	private String soggettoFruitoreIdentificativoPorta;
	private Boolean soggettoFruitoreSoggettoVirtuale;
	private String soggettoFruitoreImplementazionePdd;
	private Boolean soggettoFruitorePddReaded;
	private PortaDominio soggettoFruitorePdd;
	private String soggettoFruitoreVersioneProtocollo;
	
	private IDServizioApplicativo idServizioApplicativoFruitore;
	private ServizioApplicativo servizioApplicativoFruitore;
	private String servizioApplicativoFruitoreAnonimo;
	
	private String certificateKey; // per autenticazione modi
	
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public boolean isCached() {
		return this.cached;
	}
	public void setCached(boolean cached) {
		this.cached = cached;
	}
	
	public void clear() {
		this.clearSoggettoFruitore();
		
		this.idServizioApplicativoFruitore = null;
		this.servizioApplicativoFruitore = null;
		this.servizioApplicativoFruitoreAnonimo = null;
	}
	public void clearSoggettoFruitore() {
		this.idSoggettoFruitore = null;
		this.soggettoFruitoreRegistry = null;
		this.soggettoFruitoreConfig = null;
		this.soggettoFruitoreIdentificativoPorta = null;
		this.soggettoFruitoreSoggettoVirtuale = null;
		this.soggettoFruitoreImplementazionePdd = null;
		this.soggettoFruitorePddReaded = null;
		this.soggettoFruitorePdd = null;
		this.soggettoFruitoreVersioneProtocollo = null;
	}
	
	public IDSoggetto getIdSoggettoFruitore() {
		return this.idSoggettoFruitore;
	}
	public void setIdSoggettoFruitore(IDSoggetto idSoggettoFruitore) {
		this.idSoggettoFruitore = idSoggettoFruitore;
	}
	public org.openspcoop2.core.registry.Soggetto getSoggettoFruitoreRegistry() {
		return this.soggettoFruitoreRegistry;
	}
	public void setSoggettoFruitoreRegistry(org.openspcoop2.core.registry.Soggetto soggettoFruitoreRegistry) {
		this.soggettoFruitoreRegistry = soggettoFruitoreRegistry;
	}
	public org.openspcoop2.core.config.Soggetto getSoggettoFruitoreConfig() {
		return this.soggettoFruitoreConfig;
	}
	public void setSoggettoFruitoreConfig(org.openspcoop2.core.config.Soggetto soggettoFruitoreConfig) {
		this.soggettoFruitoreConfig = soggettoFruitoreConfig;
	}
	public String getSoggettoFruitoreIdentificativoPorta() {
		return this.soggettoFruitoreIdentificativoPorta;
	}
	public void setSoggettoFruitoreIdentificativoPorta(String soggettoFruitoreIdentificativoPorta) {
		this.soggettoFruitoreIdentificativoPorta = soggettoFruitoreIdentificativoPorta;
	}
	public Boolean getSoggettoFruitoreSoggettoVirtuale() {
		return this.soggettoFruitoreSoggettoVirtuale;
	}
	public void setSoggettoFruitoreSoggettoVirtuale(Boolean soggettoFruitoreSoggettoVirtuale) {
		this.soggettoFruitoreSoggettoVirtuale = soggettoFruitoreSoggettoVirtuale;
	}
	public String getSoggettoFruitoreImplementazionePdd() {
		return this.soggettoFruitoreImplementazionePdd;
	}
	public void setSoggettoFruitoreImplementazionePdd(String soggettoFruitoreImplementazionePdd) {
		this.soggettoFruitoreImplementazionePdd = soggettoFruitoreImplementazionePdd;
	}
	public Boolean getSoggettoFruitorePddReaded() {
		return this.soggettoFruitorePddReaded;
	}
	public void setSoggettoFruitorePddReaded(Boolean soggettoFruitorePddReaded) {
		this.soggettoFruitorePddReaded = soggettoFruitorePddReaded;
	}
	public PortaDominio getSoggettoFruitorePdd() {
		return this.soggettoFruitorePdd;
	}
	public void setSoggettoFruitorePdd(PortaDominio soggettoFruitorePdd) {
		this.soggettoFruitorePdd = soggettoFruitorePdd;
	}
	public String getSoggettoFruitoreVersioneProtocollo() {
		return this.soggettoFruitoreVersioneProtocollo;
	}
	public void setSoggettoFruitoreVersioneProtocollo(String soggettoFruitoreVersioneProtocollo) {
		this.soggettoFruitoreVersioneProtocollo = soggettoFruitoreVersioneProtocollo;
	}
	
	public IDServizioApplicativo getIdServizioApplicativoFruitore() {
		return this.idServizioApplicativoFruitore;
	}
	public void setIdServizioApplicativoFruitore(IDServizioApplicativo idServizioApplicativoFruitore) {
		this.idServizioApplicativoFruitore = idServizioApplicativoFruitore;
	}
	public ServizioApplicativo getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}
	public void setServizioApplicativoFruitore(ServizioApplicativo servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}
	public String getServizioApplicativoFruitoreAnonimo() {
		return this.servizioApplicativoFruitoreAnonimo;
	}
	public void setServizioApplicativoFruitoreAnonimo(String servizioApplicativoFruitoreAnonimo) {
		this.servizioApplicativoFruitoreAnonimo = servizioApplicativoFruitoreAnonimo;
	}
	
	public String getCertificateKey() {
		return this.certificateKey;
	}
	public void setCertificateKey(String certificateKey) {
		this.certificateKey = certificateKey;
	}
}
