/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.testsuite.units;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.Inoltro;

/**
 * UnitsTestSuiteProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CooperazioneBaseInformazioni {


	private IDSoggetto mittente;
	private IDSoggetto destinatario;
	private boolean confermaRicezione;
	private String inoltro;
	private Inoltro inoltroSdk;
	
	private int servizio_versioneDefault = 1;
	
	private String profiloCollaborazione_protocollo_oneway;
	private String profiloCollaborazione_protocollo_sincrono;
	private String profiloCollaborazione_protocollo_asincronoAsimmetrico;
	private String profiloCollaborazione_protocollo_asincronoSimmetrico;
	
	
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
	public boolean isConfermaRicezione() {
		return this.confermaRicezione;
	}
	public void setConfermaRicezione(boolean confermaRicezione) {
		this.confermaRicezione = confermaRicezione;
	}
	public String getInoltro() {
		return this.inoltro;
	}
	public void setInoltro(String inoltro) {
		this.inoltro = inoltro;
	}
	public Inoltro getInoltroSdk() {
		return this.inoltroSdk;
	}
	public void setInoltroSdk(Inoltro inoltroSdk) {
		this.inoltroSdk = inoltroSdk;
	}
	public int getServizio_versioneDefault() {
		return this.servizio_versioneDefault;
	}
	public void setServizio_versioneDefault(int servizio_versioneDefault) {
		this.servizio_versioneDefault = servizio_versioneDefault;
	}
	public String getProfiloCollaborazione_protocollo_oneway() {
		return this.profiloCollaborazione_protocollo_oneway;
	}
	public void setProfiloCollaborazione_protocollo_oneway(
			String profiloCollaborazione_protocollo_oneway) {
		this.profiloCollaborazione_protocollo_oneway = profiloCollaborazione_protocollo_oneway;
	}
	public String getProfiloCollaborazione_protocollo_sincrono() {
		return this.profiloCollaborazione_protocollo_sincrono;
	}
	public void setProfiloCollaborazione_protocollo_sincrono(
			String profiloCollaborazione_protocollo_sincrono) {
		this.profiloCollaborazione_protocollo_sincrono = profiloCollaborazione_protocollo_sincrono;
	}
	public String getProfiloCollaborazione_protocollo_asincronoAsimmetrico() {
		return this.profiloCollaborazione_protocollo_asincronoAsimmetrico;
	}
	public void setProfiloCollaborazione_protocollo_asincronoAsimmetrico(
			String profiloCollaborazione_protocollo_asincronoAsimmetrico) {
		this.profiloCollaborazione_protocollo_asincronoAsimmetrico = profiloCollaborazione_protocollo_asincronoAsimmetrico;
	}
	public String getProfiloCollaborazione_protocollo_asincronoSimmetrico() {
		return this.profiloCollaborazione_protocollo_asincronoSimmetrico;
	}
	public void setProfiloCollaborazione_protocollo_asincronoSimmetrico(
			String profiloCollaborazione_protocollo_asincronoSimmetrico) {
		this.profiloCollaborazione_protocollo_asincronoSimmetrico = profiloCollaborazione_protocollo_asincronoSimmetrico;
	}
}
