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

package org.openspcoop2.pdd.core.autorizzazione.pa;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.pdd.core.autorizzazione.AbstractDatiInvocazione;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;

/**
 * DatiInvocazionePortaApplicativa
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiInvocazionePortaApplicativa extends AbstractDatiInvocazione {

	private Credenziali credenzialiPdDMittente;
	
	private IDServizioApplicativo identitaServizioApplicativoFruitore;
	private String subjectServizioApplicativoFruitoreFromMessageSecurityHeader;
	
	// Richieste normali
	private IDPortaApplicativa idPA;
	private PortaApplicativa pa;
	// Nel caso di risposta asincrona simmetrica e per ricevute asincrone.
	private IDPortaDelegata idPD;
	private PortaDelegata pd;
	
	private IDSoggetto idSoggettoFruitore;
	private Soggetto soggettoFruitore;
	
	private RuoloBusta ruoloBusta;
	
	
	public RuoloBusta getRuoloBusta() {
		return this.ruoloBusta;
	}
	public void setRuoloBusta(RuoloBusta ruoloBusta) {
		this.ruoloBusta = ruoloBusta;
	}
	public Credenziali getCredenzialiPdDMittente() {
		return this.credenzialiPdDMittente;
	}
	public void setCredenzialiPdDMittente(Credenziali credenzialiPdDMittente) {
		this.credenzialiPdDMittente = credenzialiPdDMittente;
	}

	public IDServizioApplicativo getIdentitaServizioApplicativoFruitore() {
		return this.identitaServizioApplicativoFruitore;
	}
	public void setIdentitaServizioApplicativoFruitore(
			IDServizioApplicativo identitaServizioApplicativoFruitore) {
		this.identitaServizioApplicativoFruitore = identitaServizioApplicativoFruitore;
	}

	public String getSubjectServizioApplicativoFruitoreFromMessageSecurityHeader() {
		return this.subjectServizioApplicativoFruitoreFromMessageSecurityHeader;
	}
	public void setSubjectServizioApplicativoFruitoreFromMessageSecurityHeader(
			String subjectServizioApplicativoFruitoreFromMessageSecurityHeader) {
		this.subjectServizioApplicativoFruitoreFromMessageSecurityHeader = subjectServizioApplicativoFruitoreFromMessageSecurityHeader;
	}

	public IDPortaApplicativa getIdPA() {
		return this.idPA;
	}
	public void setIdPA(IDPortaApplicativa idPA) {
		this.idPA = idPA;
	}

	public PortaApplicativa getPa() {
		return this.pa;
	}
	public void setPa(PortaApplicativa pa) {
		this.pa = pa;
	}

	public IDPortaDelegata getIdPD() {
		return this.idPD;
	}
	public void setIdPD(IDPortaDelegata idPD) {
		this.idPD = idPD;
	}

	public PortaDelegata getPd() {
		return this.pd;
	}
	public void setPd(PortaDelegata pd) {
		this.pd = pd;
	}
	
	public IDSoggetto getIdSoggettoFruitore() {
		return this.idSoggettoFruitore;
	}
	public void setIdSoggettoFruitore(IDSoggetto soggettoFruitore) {
		this.idSoggettoFruitore = soggettoFruitore;
	}
	
	public Soggetto getSoggettoFruitore() {
		return this.soggettoFruitore;
	}
	public void setSoggettoFruitore(Soggetto soggettoFruitore) {
		this.soggettoFruitore = soggettoFruitore;
	}
	
	
	@Override
	public String getKeyCache(){
		return this._toString(true);
	}
	
	@Override
	public String toString(){
		return this._toString(false);
	}
	@Override
	public String _toString(boolean keyCache){
		StringBuilder bf = new StringBuilder();
		
		bf.append(super._toString(keyCache));
		
		if(this.credenzialiPdDMittente!=null){
			bf.append(" PdDMittente(");
			bf.append(this.credenzialiPdDMittente.toString());
			bf.append(")");
		}
		
		if(this.identitaServizioApplicativoFruitore!=null){
			bf.append(" IDServizioApplicativoFruitore(");
			bf.append(this.identitaServizioApplicativoFruitore.toString());
			bf.append(")");
		}
		
		if(this.subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null){
			bf.append(" SubjectSAFruitoreFromSecurityHeader(");
			bf.append(this.subjectServizioApplicativoFruitoreFromMessageSecurityHeader);
			bf.append(")");
		}
		
		if(this.idPA!=null){
			bf.append(" IDPortaApplicativa(");
			bf.append(this.idPA.toString());
			bf.append(")");
		}
		
		if(keyCache==false){
			if(this.pa!=null){
				bf.append(" PortaApplicativa:defined");
			}
		}
		
		if(this.idPD!=null){
			bf.append(" IDPortaDelegata(");
			bf.append(this.idPD.toString());
			bf.append(")");
		}
		
		if(keyCache==false){
			if(this.pd!=null){
				bf.append(" PortaDelegata:defined");
			}
		}
		
		if(this.idSoggettoFruitore!=null){
			bf.append(" IDSoggettoFruitore(");
			bf.append(this.idSoggettoFruitore.toString());
			bf.append(")");
		}
		
		if(keyCache==false){
			if(this.soggettoFruitore!=null){
				bf.append(" SoggetoFruitore:defined");
			}
		}
		
		if(keyCache==false){
			if(this.ruoloBusta!=null){
				bf.append(" RuoloBusta(");
				bf.append(this.ruoloBusta.name());
				bf.append(")");
			}
		}
		
		return bf.toString();
	}
}
