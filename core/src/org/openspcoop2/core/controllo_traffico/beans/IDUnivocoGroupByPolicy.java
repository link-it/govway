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

package org.openspcoop2.core.controllo_traffico.beans;

import java.io.Serializable;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * IDUnivocoGroupByPolicy 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDUnivocoGroupByPolicy implements IDUnivocoGroupBy<IDUnivocoGroupByPolicy>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String QUALSIASI = "*";
	
	private String ruoloPorta = QUALSIASI;
	private String protocollo = QUALSIASI;
	private String fruitore = QUALSIASI;
	private String servizioApplicativoFruitore = QUALSIASI;
	private String erogatore = QUALSIASI;
	private String servizioApplicativoErogatore = QUALSIASI;
	private String servizio = QUALSIASI;
	private String azione = QUALSIASI;
	private String tipoKey = QUALSIASI;
	private String nomeKey = QUALSIASI;
	private String valoreKey = QUALSIASI;
	private String identificativoAutenticato = QUALSIASI;
	private String tokenSubject = QUALSIASI;
	private String tokenIssuer = QUALSIASI;
	private String tokenUsername = QUALSIASI;
	private String tokenClientId = QUALSIASI;
	private String tokenEMail = QUALSIASI;
	

	
	@Override
	public boolean match(IDUnivocoGroupByPolicy filtro){
		
		return 
				this.ruoloPorta.equals(filtro.getRuoloPorta())
				&&
				this.protocollo.equals(filtro.getProtocollo())
				&&
				this.fruitore.equals(filtro.getFruitore())
				&&
				this.servizioApplicativoFruitore.equals(filtro.getServizioApplicativoFruitore())
				&&
				this.erogatore.equals(filtro.getErogatore())
				&&
				this.servizioApplicativoErogatore.equals(filtro.getServizioApplicativoErogatore())
				&&
				this.servizio.equals(filtro.getServizio())
				&&
				this.azione.equals(filtro.getAzione())
				&&
				this.tipoKey.equals(filtro.getTipoKey())
				&&
				this.nomeKey.equals(filtro.getNomeKey())
				&&
				this.valoreKey.equals(filtro.getValoreKey())
				&&
				this.identificativoAutenticato.equals(filtro.getIdentificativoAutenticato())
				&&
				this.tokenSubject.equals(filtro.getTokenSubject())
				&&
				this.tokenIssuer.equals(filtro.getTokenIssuer())
				&&
				this.tokenClientId.equals(filtro.getTokenClientId())
				&&
				this.tokenUsername.equals(filtro.getTokenUsername())
				&&
				this.tokenEMail.equals(filtro.getTokenEMail())
				;
		
	}
	
	@Override
	public boolean equals(Object param){
		if(param==null || !(param instanceof IDUnivocoGroupByPolicy))
			return false;
		IDUnivocoGroupByPolicy id = (IDUnivocoGroupByPolicy) param;
		return this.match(id);
	}
	
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public String toString(){
		return this.toString(false);
	}
	public String toString(boolean filterGroupByNotSet){
		
		StringBuffer bf = new StringBuffer();
		
		if(!QUALSIASI.equals(this.ruoloPorta) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			bf.append("RuoloPorta:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.ruoloPorta);
		}
		
		if(!QUALSIASI.equals(this.protocollo) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("Protocollo:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.protocollo);
		}
		
		if(!QUALSIASI.equals(this.fruitore) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("Fruitore:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.fruitore);
		}
		
		if(!QUALSIASI.equals(this.servizioApplicativoFruitore) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("Applicativo:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.servizioApplicativoFruitore);
		}	
		
		if(!QUALSIASI.equals(this.erogatore) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("Erogatore:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.erogatore);
		}
		
		if(!QUALSIASI.equals(this.servizioApplicativoErogatore) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("ApplicativoErogatore:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.servizioApplicativoErogatore);
		}
		
		if(!QUALSIASI.equals(this.servizio) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("API:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.servizio);
		}
		
		if(!QUALSIASI.equals(this.azione) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("Azione:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.azione);
		}
		
		if(!QUALSIASI.equals(this.tipoKey) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("TipoKey:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.tipoKey);
		}
		
		if(!QUALSIASI.equals(this.nomeKey) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("NomeKey:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.nomeKey);
		}
		
		if(!QUALSIASI.equals(this.valoreKey) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("ValoreKey:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.valoreKey);
		}
		
		if(!QUALSIASI.equals(this.identificativoAutenticato) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("Credenziali:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.identificativoAutenticato);
		}
		
		if(!QUALSIASI.equals(this.tokenSubject) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("TokenSubject:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.tokenSubject);
		}
		
		if(!QUALSIASI.equals(this.tokenIssuer) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("TokenIssuer:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.tokenIssuer);
		}
		
		if(!QUALSIASI.equals(this.tokenClientId) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("TokenClientId:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.tokenClientId);
		}
		
		if(!QUALSIASI.equals(this.tokenUsername) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("TokenUsername:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.tokenUsername);
		}
		
		if(!QUALSIASI.equals(this.tokenEMail) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("TokenEMail:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.tokenEMail);
		}
		
		if(bf.length()<=0){
			if(filterGroupByNotSet){
				bf.append("\t");
			}
			bf.append("Disabilitato");
		}
		
		return bf.toString();
	}
	
	
	

	
	public String getRuoloPorta() {
		return this.ruoloPorta;
	}
	public TipoPdD getRuoloPortaAsTipoPdD(){
		if(this.ruoloPorta!=null && !this.ruoloPorta.equals(QUALSIASI)){
			return TipoPdD.toTipoPdD(this.ruoloPorta);
		}
		return null;
	}
	
	public void setRuoloPorta(String ruoloPorta) {
		if(ruoloPorta!=null)
			this.ruoloPorta = ruoloPorta;
	}

	public String getProtocollo() {
		return this.protocollo;
	}
	
	public String getProtocolloIfDefined() {
		if(this.protocollo!=null && !this.protocollo.equals(QUALSIASI) ){
			return this.protocollo;
		}
		return null;
	}

	public void setProtocollo(String protocollo) {
		if(protocollo!=null)
			this.protocollo = protocollo;
	}

	public String getFruitore() {
		return this.fruitore;
	}
	
	public IDSoggetto getFruitoreIfDefined(){
		if(this.fruitore!=null && !this.fruitore.equals(QUALSIASI) && this.fruitore.contains("/")){
			String [] tmp = this.fruitore.split("/");
			if(tmp.length==2){
				return new IDSoggetto(tmp[0], tmp[1]);
			}
			return null;
		}
		return null;
	}

	public void setFruitore(String fruitore) {
		if(fruitore!=null)
			this.fruitore = fruitore;
	}

	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}
	
	public String getServizioApplicativoFruitoreIfDefined() {
		if(this.servizioApplicativoFruitore!=null && !this.servizioApplicativoFruitore.equals(QUALSIASI) ){
			return this.servizioApplicativoFruitore;
		}
		return null;
	}

	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		if(servizioApplicativoFruitore!=null)
			this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}
	
	public String getErogatore() {
		return this.erogatore;
	}

	public IDSoggetto getErogatoreIfDefined(){
		if(this.erogatore!=null && !this.erogatore.equals(QUALSIASI) && this.erogatore.contains("/")){
			String [] tmp = this.erogatore.split("/");
			if(tmp.length==2){
				return new IDSoggetto(tmp[0], tmp[1]);
			}
			return null;
		}
		return null;
	}
	
	public void setErogatore(String erogatore) {
		if(erogatore!=null)
			this.erogatore = erogatore;
	}

	public String getServizioApplicativoErogatore() {
		return this.servizioApplicativoErogatore;
	}

	public String getServizioApplicativoErogatoreIfDefined() {
		if(this.servizioApplicativoErogatore!=null && !this.servizioApplicativoErogatore.equals(QUALSIASI) ){
			return this.servizioApplicativoErogatore;
		}
		return null;
	}
	
	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		if(servizioApplicativoErogatore!=null)
			this.servizioApplicativoErogatore = servizioApplicativoErogatore;
	}

	public String getServizio() {
		return this.servizio;
	}

	@SuppressWarnings("deprecation")
	public IDServizio getServizioIfDefined(){
		if(this.servizio!=null && !this.servizio.equals(QUALSIASI) && this.servizio.contains("/")){
			
			// tipo/nome/versione
			
			String [] tmp = this.servizio.split("/");
			if(tmp.length==3){
				IDServizio idServizio = new IDServizio();
				idServizio.setTipo(tmp[0]);
				idServizio.setNome(tmp[1]);
				idServizio.setVersione(Integer.parseInt(tmp[2]));
				return idServizio;
			}
			return null;
		}
		return null;
	}
	
	public void setServizio(String servizio) {
		if(servizio!=null)
			this.servizio = servizio;
	}

	public String getAzione() {
		return this.azione;
	}

	public void setAzione(String azione) {
		if(azione!=null)
			this.azione = azione;
	}

	public String getAzioneIfDefined() {
		if(this.azione!=null && !this.azione.equals(QUALSIASI) ){
			return this.azione;
		}
		return null;
	}
	
	public String getTipoKey() {
		return this.tipoKey;
	}

	public void setTipoKey(String tipoKey) {
		if(tipoKey!=null)
			this.tipoKey = tipoKey;
	}

	public String getNomeKey() {
		return this.nomeKey;
	}

	public void setNomeKey(String nomeKey) {
		if(nomeKey!=null)
			this.nomeKey = nomeKey;
	}

	public String getValoreKey() {
		return this.valoreKey;
	}

	public void setValoreKey(String valoreKey) {
		if(valoreKey!=null)
			this.valoreKey = valoreKey;
	}
	
	public String getIdentificativoAutenticato() {
		return this.identificativoAutenticato;
	}
	
	public String getIdentificativoAutenticatoIfDefined() {
		if(this.identificativoAutenticato!=null && !this.identificativoAutenticato.equals(QUALSIASI) ){
			return this.identificativoAutenticato;
		}
		return null;
	}

	public void setIdentificativoAutenticato(String identificativoAutenticato) {
		if(identificativoAutenticato!=null)
			this.identificativoAutenticato = identificativoAutenticato;
	}
	
	
	public String getTokenSubject() {
		return this.tokenSubject;
	}
	
	public String getTokenSubjectIfDefined() {
		if(this.tokenSubject!=null && !this.tokenSubject.equals(QUALSIASI) ){
			return this.tokenSubject;
		}
		return null;
	}

	public void setTokenSubject(String tokenSubject) {
		if(tokenSubject!=null)
			this.tokenSubject = tokenSubject;
	}
	
	
	public String getTokenIssuer() {
		return this.tokenIssuer;
	}
	
	public String getTokenIssuerIfDefined() {
		if(this.tokenIssuer!=null && !this.tokenIssuer.equals(QUALSIASI) ){
			return this.tokenIssuer;
		}
		return null;
	}

	public void setTokenIssuer(String tokenIssuer) {
		if(tokenIssuer!=null)
			this.tokenIssuer = tokenIssuer;
	}
	
	
	public String getTokenClientId() {
		return this.tokenClientId;
	}
	
	public String getTokenClientIdIfDefined() {
		if(this.tokenClientId!=null && !this.tokenClientId.equals(QUALSIASI) ){
			return this.tokenClientId;
		}
		return null;
	}

	public void setTokenClientId(String tokenClientId) {
		if(tokenClientId!=null)
			this.tokenClientId = tokenClientId;
	}
	
	
	public String getTokenUsername() {
		return this.tokenUsername;
	}
	
	public String getTokenUsernameIfDefined() {
		if(this.tokenUsername!=null && !this.tokenUsername.equals(QUALSIASI) ){
			return this.tokenUsername;
		}
		return null;
	}

	public void setTokenUsername(String tokenUsername) {
		if(tokenUsername!=null)
			this.tokenUsername = tokenUsername;
	}
	
	
	public String getTokenEMail() {
		return this.tokenEMail;
	}
	
	public String getTokenEMailIfDefined() {
		if(this.tokenEMail!=null && !this.tokenEMail.equals(QUALSIASI) ){
			return this.tokenEMail;
		}
		return null;
	}

	public void setTokenEMail(String tokenEMail) {
		if(tokenEMail!=null)
			this.tokenEMail = tokenEMail;
	}
	
	
	
	// **** UTILITIES ****
	
	public static String serialize(IDUnivocoGroupByPolicy id){
		StringBuffer bf = new StringBuffer();
		
		bf.append(id.ruoloPorta);
		bf.append("\n");
		
		bf.append(id.protocollo);
		bf.append("\n");
		
		bf.append(id.fruitore);
		bf.append("\n");
		
		bf.append(id.servizioApplicativoFruitore);
		bf.append("\n");
		
		bf.append(id.erogatore);
		bf.append("\n");
		
		bf.append(id.servizioApplicativoErogatore);
		bf.append("\n");
		
		bf.append(id.servizio);
		bf.append("\n");
		
		bf.append(id.azione);
		bf.append("\n");
		
		bf.append(id.tipoKey);
		bf.append("\n");	
		bf.append(id.nomeKey);
		bf.append("\n");
		bf.append(id.valoreKey);
		bf.append("\n");
		
		bf.append(id.identificativoAutenticato);
		bf.append("\n");
		
		bf.append(id.tokenSubject);
		bf.append("\n");
		
		bf.append(id.tokenIssuer);
		bf.append("\n");
		
		bf.append(id.tokenClientId);
		bf.append("\n");
		
		bf.append(id.tokenUsername);
		bf.append("\n");
		
		bf.append(id.tokenEMail);

		
		return bf.toString();
	}
	
	public static IDUnivocoGroupByPolicy deserialize(String s) throws Exception{
		IDUnivocoGroupByPolicy id = new IDUnivocoGroupByPolicy();
		String [] tmp = s.split("\n");
		if(tmp==null){
			throw new Exception("Wrong Format");
		}
		int oldLength = 11;
		int newLength = oldLength+1+5; // nella 3.1.0 aggiunto idAutenticato e 5 token claims
		if(tmp.length!=oldLength && tmp.length!=newLength){
			throw new Exception("Wrong Format (size: "+tmp.length+")");
		}
		for (int i = 0; i < tmp.length; i++) {
			if(i==0){
				id.ruoloPorta = tmp[i].trim();
			}
			else if(i==1){
				id.protocollo = tmp[i].trim();
			}
			else if(i==2){
				id.fruitore = tmp[i].trim();
			}
			else if(i==3){
				id.servizioApplicativoFruitore = tmp[i].trim();
			}
			else if(i==4){
				id.erogatore = tmp[i].trim();
			}
			else if(i==5){
				id.servizioApplicativoErogatore = tmp[i].trim();
			}
			else if(i==6){
				id.servizio = tmp[i].trim();
			}
			else if(i==7){
				id.azione = tmp[i].trim();
			}
			else if(i==8){
				id.tipoKey = tmp[i].trim();
			}
			else if(i==9){
				id.nomeKey = tmp[i].trim();
			}
			else if(i==10){
				id.valoreKey = tmp[i].trim();
			}
			else if(i==11){
				id.identificativoAutenticato = tmp[i].trim();
			}
			else if(i==12){
				id.tokenSubject = tmp[i].trim();
			}
			else if(i==13){
				id.tokenIssuer = tmp[i].trim();
			}
			else if(i==14){
				id.tokenClientId = tmp[i].trim();
			}
			else if(i==15){
				id.tokenUsername = tmp[i].trim();
			}
			else if(i==16){
				id.tokenEMail = tmp[i].trim();
			}
		}
		return id;
	}
}
