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
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;

/**
 * IDAccordoFactory
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDAccordoFactory {

	
	private static IDAccordoFactory factory = null;
	private static synchronized void init(){
		if(IDAccordoFactory.factory==null){
			IDAccordoFactory.factory = new IDAccordoFactory();
		}
	}
	public static IDAccordoFactory getInstance(){
		if(IDAccordoFactory.factory==null){
			IDAccordoFactory.init();
		}
		return IDAccordoFactory.factory;
	}
	
	
	
	@SuppressWarnings("deprecation")
	private IDAccordo build(String nome,IDSoggetto soggettoReferente,String versione){
		IDAccordo idAccordo = new IDAccordo();
		idAccordo.setNome(nome);
		idAccordo.setSoggettoReferente(soggettoReferente);
		String v = null;
		if(versione!=null && !("".equals(versione))){
			v = versione;
		}
		idAccordo.setVersione(v);
		return idAccordo;
	}
	
	public String getUriFromIDAccordo(IDAccordo idAccordo) throws DriverRegistroServiziException{
		if(idAccordo==null){
			throw new DriverRegistroServiziException("IDAccordo non fornito");
		}
		if(idAccordo.getNome()==null){
			throw new DriverRegistroServiziException("Nome accordo non fornito");
		}
		IDSoggetto soggettoReferente = idAccordo.getSoggettoReferente();
		if(soggettoReferente!=null){
			if(soggettoReferente.getTipo()==null){
				throw new DriverRegistroServiziException("Tipo soggetto referente non fornito?");
			}
			if(soggettoReferente.getNome()==null){
				throw new DriverRegistroServiziException("Nome soggetto referente non fornito?");
			}
		}
		if(soggettoReferente!=null && idAccordo.getVersione()!=null){
			return soggettoReferente.toString() + ":" + idAccordo.getNome() + ":" + idAccordo.getVersione();
		}else if(idAccordo.getVersione()!=null){
			return idAccordo.getNome() + ":" + idAccordo.getVersione();
		}else if(soggettoReferente!=null){
			return soggettoReferente.toString() + ":" + idAccordo.getNome();
		}else{
			return idAccordo.getNome();
		}
	}
	
	public String getUriFromAccordo(AccordoServizioParteComune accordo)  throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDAccordo idAccordo = this.build(accordo.getNome(),BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente()),accordo.getVersione());
		return this.getUriFromIDAccordo(idAccordo);
	}
	public String getUriFromAccordo(AccordoServizioParteSpecifica accordo)  throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDAccordo idAccordo = this.build(accordo.getNome(),BeanUtilities.getSoggettoErogatore(accordo.getServizio()),
				accordo.getVersione());
		return this.getUriFromIDAccordo(idAccordo);
	}
	
	public String getUriFromValues(String nomeAS,String tipoSoggettoReferente,String nomeSoggettoReferente,String ver)  throws DriverRegistroServiziException{
		if(nomeAS==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDSoggetto soggettoReferente = null;
		if(tipoSoggettoReferente!=null && nomeSoggettoReferente!=null)
			soggettoReferente = new IDSoggetto(tipoSoggettoReferente,nomeSoggettoReferente);
		IDAccordo idAccordo = this.build(nomeAS,soggettoReferente,ver);
		return this.getUriFromIDAccordo(idAccordo);
	}
	
	public String getUriFromValues(String nomeAS,IDSoggetto soggettoReferente,String ver)  throws DriverRegistroServiziException{
		if(nomeAS==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		if(soggettoReferente==null){
			return this.getUriFromValues(nomeAS,null,null,ver);
		}else{
			return this.getUriFromValues(nomeAS,soggettoReferente.getTipo(),soggettoReferente.getNome(),ver);
		}
	}
	
	public IDAccordo getIDAccordoFromUri(String uriAccordo) throws DriverRegistroServiziException{
		try{
		
			// possibili casi:
			// nomeAccordo
			// tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo
			// nomeAccordo:versione
			// tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo:versione
			
			if(uriAccordo==null){
				throw new Exception("Uri accordo non fornita");
			}
			int primoMarcatore = uriAccordo.indexOf(":");
			int secondoMarcatore = -1;
			if(primoMarcatore>=0){
				secondoMarcatore = uriAccordo.indexOf(":",primoMarcatore+1);
			}
			int terzoMarcatore = -1;
			if(secondoMarcatore>0){
				terzoMarcatore = uriAccordo.indexOf(":",secondoMarcatore+1);
				if(terzoMarcatore>0){
					throw new Exception("sintassi non corretta, possibili usi: nomeAccordo  tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo  nomeAccordo:versione  tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo:versione");
				}
			}
			
			if(primoMarcatore<0){
				IDAccordo idAccordo = this.build(uriAccordo,null,null);
				return idAccordo;
			}
			
			String tmp1 = null; 
			String tmp2 = null;
			String tmp3 = null;
			if(primoMarcatore>=0 && secondoMarcatore>0){
				tmp1 = uriAccordo.substring(0, primoMarcatore); //soggetto referente
				tmp2 = uriAccordo.substring((primoMarcatore+1), secondoMarcatore); // nome accordo
				tmp3 = uriAccordo.substring((secondoMarcatore+1),uriAccordo.length()); // versione
				
				int divisorioSoggettoReferente = tmp1.indexOf("/");
				if(divisorioSoggettoReferente<=0){
					throw new Exception("sintassi del soggetto referente non corretta, l'uri deve essere definita con la seguente forma: tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo:versione");
				}
				String tipoSoggettoReferente = tmp1.substring(0,divisorioSoggettoReferente);
				String nomeSoggettoReferente = tmp1.substring((divisorioSoggettoReferente+1),tmp1.length());
				IDAccordo idAccordo = this.build(tmp2,new IDSoggetto(tipoSoggettoReferente,nomeSoggettoReferente),tmp3);
				return idAccordo;
			}else {
				tmp1 = uriAccordo.substring(0, primoMarcatore);
				tmp2 = uriAccordo.substring((primoMarcatore+1),uriAccordo.length());
				
				int divisorioSoggettoReferente = tmp1.indexOf("/");
				if(divisorioSoggettoReferente<0){
					// nomeAccordo:versione
					IDAccordo idAccordo = this.build(tmp1,null,tmp2);
					return idAccordo;
				}else if(divisorioSoggettoReferente==0){
					throw new Exception("sintassi non corretta, possibili usi: nomeAccordo  tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo  nomeAccordo:versione  tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo:versione");
				}else{
					// tipoSoggettoReferente/nomeSoggettoReferente:nomeAccordo
					String tipoSoggettoReferente = tmp1.substring(0,divisorioSoggettoReferente);
					String nomeSoggettoReferente = tmp1.substring((divisorioSoggettoReferente+1),tmp1.length());
					IDAccordo idAccordo = this.build(tmp2,new IDSoggetto(tipoSoggettoReferente,nomeSoggettoReferente),null);
					return idAccordo;
				}
			}
		
		}catch(Exception e){
			throw new DriverRegistroServiziException("Parsing uriAccordo["+uriAccordo+"] non riusciuto: "+e.getMessage());
		}
	}
	
	public IDAccordo getIDAccordoFromAccordo(AccordoServizioParteComune accordo) throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDAccordo idAccordo = this.build(accordo.getNome(),BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente()),accordo.getVersione());
		return idAccordo;
	}
	public IDAccordo getIDAccordoFromAccordo(AccordoServizioParteSpecifica accordo) throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDAccordo idAccordo = this.build(accordo.getNome(),
				new IDSoggetto(accordo.getServizio().getTipoSoggettoErogatore(),accordo.getServizio().getNomeSoggettoErogatore()),
				accordo.getVersione());
		return idAccordo;
	}
	
	public IDAccordo getIDAccordoFromValues(String nomeAS,String tipoSoggettoReferente,String nomeSoggettoReferente,String ver) throws DriverRegistroServiziException{
		if(nomeAS==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDSoggetto soggettoReferente = null;
		if(tipoSoggettoReferente!=null && nomeSoggettoReferente!=null)
			soggettoReferente = new IDSoggetto(tipoSoggettoReferente,nomeSoggettoReferente);
		IDAccordo idAccordo = this.build(nomeAS,soggettoReferente,ver);
		return idAccordo;
	}
	public IDAccordo getIDAccordoFromValuesWithoutCheck(String nomeAS,String tipoSoggettoReferente,String nomeSoggettoReferente,String ver) throws DriverRegistroServiziException{
		IDSoggetto soggettoReferente = new IDSoggetto(tipoSoggettoReferente,nomeSoggettoReferente);
		IDAccordo idAccordo = this.build(nomeAS,soggettoReferente,ver);
		return idAccordo;
	}
	public IDAccordo getIDAccordoFromValues(String nomeAS,IDSoggetto soggettoReferente,String ver) throws DriverRegistroServiziException{
		if(soggettoReferente==null){
			return this.getIDAccordoFromValues(nomeAS,null,null,ver);
		}else{
			return this.getIDAccordoFromValues(nomeAS,soggettoReferente.getTipo(),soggettoReferente.getNome(),ver);
		}
	}
	
	public boolean versioneNonDefinita(String value){
		return AccordiUtils.versioneNonDefinita(value);
	}
	
}
