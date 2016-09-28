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



package org.openspcoop2.pdd.config;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * Classe utilizzata per raccogliere le informazioni su servizi applicativi e 
 * soggetti reali associati ad un unico soggetto virtuale
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SoggettoVirtuale  {

	/** Nome dei servizi reali */
	private IDSoggetto [] soggettiReali;
	/** Nome dei servizi applicativi associato al soggetto Virtuale*/
	private String [] serviziApplicativi;
	/** Nome della PA da cui sono stati estratti i servizi applicativi associato al soggetto Virtuale*/
	private PortaApplicativa [] porteApplicative;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public SoggettoVirtuale(){
	}





	/* ********  S E T T E R   ******** */
	/**
	 * Imposta il nome dei soggetti reali
	 *
	 * @param nomi Nome dei soggetti reali
	 * 
	 */    
	public void setSoggettiReali(IDSoggetto [] nomi) {
		this.soggettiReali = nomi;
	}
	/**
	 * Imposta il nome dei servizi applicativi
	 *
	 * @param nomi Nome dei servizi applicativi
	 * 
	 */    
	public void setServiziApplicativi(String [] nomi) {
		this.serviziApplicativi = nomi;
	}
	public void setPorteApplicative(PortaApplicativa[] porteApplicative) {
		this.porteApplicative = porteApplicative;
	}






	/* ********  G E T T E R   ******** */
	/**
	 * Ritorna il nome dei soggetti reali
	 *
	 * @return Nome dei soggetti reali
	 * 
	 */    
	public IDSoggetto[] getSoggettiReali() {
		return this.soggettiReali;
	}
	/**
	 * Ritorna il nome dei soggetti reali in singola copia
	 *
	 * @return Nome dei soggetti reali
	 * 
	 */    
	public IDSoggetto[] getSoggettiRealiSenzaDuplicati() {
		java.util.Vector<IDSoggetto> soggettiSenzaDuplicati = new java.util.Vector<IDSoggetto>();
		for(int i=0;i<this.soggettiReali.length;i++){
			boolean find = false;
			for(int j=0;j<soggettiSenzaDuplicati.size();j++){
				if( (this.soggettiReali[i].getTipo().equals(soggettiSenzaDuplicati.get(j).getTipo())) &&
						(this.soggettiReali[i].getNome().equals(soggettiSenzaDuplicati.get(j).getNome())) ){
					find = true;
					break;
				}
			}
			if(find == false){
				soggettiSenzaDuplicati.add(this.soggettiReali[i]);
			}
		}

		IDSoggetto[] soggettiSenzaDuplicatiArray = new IDSoggetto[soggettiSenzaDuplicati.size()];
		soggettiSenzaDuplicatiArray = soggettiSenzaDuplicati.toArray(soggettiSenzaDuplicatiArray);
		return soggettiSenzaDuplicatiArray;
	}
	/**
	 * Ritornaa il nome dei servizi applicativi
	 *
	 * @return Nome dei servizi applicativi
	 * 
	 */    
	public String[] getServiziApplicativi() {
		return this.serviziApplicativi;
	}
	
	public IDSoggetto getSoggettoReale(String servizioApplicativo){
		for (int i = 0; i < this.serviziApplicativi.length; i++) {
			if(this.serviziApplicativi[i].equals(servizioApplicativo)){
				return this.soggettiReali[i];
			}
		}
		return null;
	}
	
	public PortaApplicativa[] getPorteApplicative() {
		return this.porteApplicative;
	}
	
	public IDPortaApplicativaByNome getIdentificativoPA(String servizioApplicativo) {
		for (int i = 0; i < this.serviziApplicativi.length; i++) {
			if(this.serviziApplicativi[i].equals(servizioApplicativo)){
				PortaApplicativa pa = this.porteApplicative[i];
				IDPortaApplicativaByNome idPA = new IDPortaApplicativaByNome();
				idPA.setNome(pa.getNome());
				idPA.setSoggetto(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
				return idPA;
			}
		}
		return null;
	}
	
	public PortaApplicativa getPortaApplicativa(String servizioApplicativo) {
		for (int i = 0; i < this.serviziApplicativi.length; i++) {
			if(this.serviziApplicativi[i].equals(servizioApplicativo)){
				return this.porteApplicative[i];
			}
		}
		return null;
	}
}
