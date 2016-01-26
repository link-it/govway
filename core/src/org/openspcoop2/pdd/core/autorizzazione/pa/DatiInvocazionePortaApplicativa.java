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

package org.openspcoop2.pdd.core.autorizzazione.pa;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.core.autorizzazione.AbstractDatiInvocazione;
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
	private IDPortaApplicativaByNome idPA;
	private PortaApplicativa pa;
	// Nel caso di risposta asincrona simmetrica e per ricevute asincrone.
	private IDPortaDelegata idPD;
	private PortaDelegata pd;
	
	private IDSoggetto idSoggettoFruitore;
	
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

	public IDPortaApplicativaByNome getIdPA() {
		return this.idPA;
	}
	public void setIdPA(IDPortaApplicativaByNome idPA) {
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
		StringBuffer bf = new StringBuffer();
		
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
			if(this.ruoloBusta!=null){
				bf.append(" RuoloBusta(");
				bf.append(this.ruoloBusta.name());
				bf.append(")");
			}
		}
		
		return bf.toString();
	}
}
