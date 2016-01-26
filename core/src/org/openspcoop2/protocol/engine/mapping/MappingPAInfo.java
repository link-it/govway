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

package org.openspcoop2.protocol.engine.mapping;

/**
 * MappingPAInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MappingPAInfo {

	private String tipoSoggetto;
	private String nomeSoggetto;
	private String nomePA;
	private String azione;
	
	public String getTipoSoggetto() {
		return this.tipoSoggetto;
	}
	public void setTipoSoggetto(String tipoSoggetto) {
		this.tipoSoggetto = tipoSoggetto;
	}
	public String getNomeSoggetto() {
		return this.nomeSoggetto;
	}
	public void setNomeSoggetto(String nomeSoggetto) {
		this.nomeSoggetto = nomeSoggetto;
	}
	public String getNomePA() {
		return this.nomePA;
	}
	public void setNomePA(String nomePA) {
		this.nomePA = nomePA;
	}
	public String getAzione() {
		return this.azione;
	}
	public void setAzione(String azione) {
		this.azione = azione;
	}

	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append(this.nomePA);
		
		if(this.azione!=null){
			if(bf.toString().contains("?")){
				bf.append("&");
			}
			else{
				bf.append("?");
			}
			bf.append("azione=");
			bf.append(this.azione);
		}
		
		if(this.tipoSoggetto!=null){
			if(bf.toString().contains("?")){
				bf.append("&");
			}
			else{
				bf.append("?");
			}
			bf.append("tipo_soggetto=");
			bf.append(this.tipoSoggetto);
		}
		
		if(this.nomeSoggetto!=null){
			if(bf.toString().contains("?")){
				bf.append("&");
			}
			else{
				bf.append("?");
			}
			bf.append("soggetto=");
			bf.append(this.nomeSoggetto);
		}
		
		return bf.toString();
	}
}
