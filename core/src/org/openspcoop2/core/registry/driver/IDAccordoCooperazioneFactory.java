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

package org.openspcoop2.core.registry.driver;

import org.openspcoop2.core.commons.AccordiUtils;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazione;

/**
 * IDAccordoCooperazioneFactory
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDAccordoCooperazioneFactory {

	
	private static IDAccordoCooperazioneFactory factory = null;
	private static synchronized void init(){
		if(IDAccordoCooperazioneFactory.factory==null){
			IDAccordoCooperazioneFactory.factory = new IDAccordoCooperazioneFactory();
		}
	}
	public static IDAccordoCooperazioneFactory getInstance(){
		if(IDAccordoCooperazioneFactory.factory==null){
			IDAccordoCooperazioneFactory.init();
		}
		return IDAccordoCooperazioneFactory.factory;
	}
	
	
	@SuppressWarnings("deprecation")
	private IDAccordoCooperazione build(String nome,String versione){
		IDAccordoCooperazione idAccordoCooperazione = new IDAccordoCooperazione();
		idAccordoCooperazione.setNome(nome);
		String v = null;
		if(versione!=null && !("".equals(versione))){
			v = versione;
		}
		idAccordoCooperazione.setVersione(v);
		return idAccordoCooperazione;
	}
	
	public String getUriFromIDAccordo(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException{
		if(idAccordo==null){
			throw new DriverRegistroServiziException("IDAccordo non fornito");
		}
		if(idAccordo.getNome()==null){
			throw new DriverRegistroServiziException("Nome accordo non fornito");
		}
		if(idAccordo.getVersione()!=null){
			return idAccordo.getNome() + ":" + idAccordo.getVersione();
		}else{
			return idAccordo.getNome();
		}
	}
	
	public String getUriFromAccordo(AccordoCooperazione accordo)  throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDAccordoCooperazione idAccordo = this.build(accordo.getNome(),accordo.getVersione());
		return this.getUriFromIDAccordo(idAccordo);
	}
	
	public String getUriFromValues(String nomeAS,String ver)  throws DriverRegistroServiziException{
		if(nomeAS==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDAccordoCooperazione idAccordo = this.build(nomeAS,ver);
		return this.getUriFromIDAccordo(idAccordo);
	}
	
	public IDAccordoCooperazione getIDAccordoFromUri(String uriAccordo) throws DriverRegistroServiziException{
		try{
		
			// possibili casi:
			// nomeAccordo
			// nomeAccordo:versione
			
			if(uriAccordo==null){
				throw new Exception("Uri accordo non fornita");
			}
			int primoMarcatore = uriAccordo.indexOf(":");
			int secondoMarcatore = -1;
			if(primoMarcatore>=0){
				secondoMarcatore = uriAccordo.indexOf(":",primoMarcatore+1);
				if(secondoMarcatore>0){
					throw new Exception("sintassi non corretta, possibili usi: nomeAccordo  nomeAccordo:versione");
				}
			}
						
			if(primoMarcatore<0){
				IDAccordoCooperazione idAccordo = this.build(uriAccordo,null);
				return idAccordo;
			}
			
			String tmp1 = uriAccordo.substring(0, primoMarcatore);
			String tmp2 = uriAccordo.substring((primoMarcatore+1),uriAccordo.length());
			// nomeAccordo:versione
			IDAccordoCooperazione idAccordo = this.build(tmp1,tmp2);
			return idAccordo;
		
		}catch(Exception e){
			throw new DriverRegistroServiziException("Parsing uriAccordo["+uriAccordo+"] non riusciuto: "+e.getMessage());
		}
	}
	
	public IDAccordoCooperazione getIDAccordoFromAccordo(AccordoCooperazione accordo) throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDAccordoCooperazione idAccordo = this.build(accordo.getNome(),accordo.getVersione());
		return idAccordo;
	}
	
	public IDAccordoCooperazione getIDAccordoFromValues(String nomeAS,String ver) throws DriverRegistroServiziException{
		if(nomeAS==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDAccordoCooperazione idAccordo = this.build(nomeAS,ver);
		return idAccordo;
	}
	
	public boolean versioneNonDefinita(String value){
		return AccordiUtils.versioneNonDefinita(value);
	}
	
}
