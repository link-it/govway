/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.core.registry.driver;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
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
	
	private IDAccordoFactory idAccordoFactory = null;
	private IDAccordoCooperazioneFactory(){
		this.idAccordoFactory = IDAccordoFactory.getInstance();
	}
	
	

	@SuppressWarnings("deprecation")
	private IDAccordoCooperazione build(IDAccordo idAccordo){
		IDAccordoCooperazione idAccordoCooperazione = new IDAccordoCooperazione();
		idAccordoCooperazione.setNome(idAccordo.getNome());
		idAccordoCooperazione.setSoggettoReferente(idAccordo.getSoggettoReferente());
		idAccordoCooperazione.setVersione(idAccordo.getVersione());
		return idAccordoCooperazione;
	}
	@SuppressWarnings("deprecation")
	private IDAccordo build(IDAccordoCooperazione idAccordoCooperazione){
		IDAccordo idAccordo = new IDAccordo();
		idAccordo.setNome(idAccordoCooperazione.getNome());
		idAccordo.setSoggettoReferente(idAccordoCooperazione.getSoggettoReferente());
		idAccordo.setVersione(idAccordoCooperazione.getVersione());
		return idAccordo;
	}
	@SuppressWarnings("deprecation")
	private IDAccordo build(String nome, IDSoggetto soggettoReferente, int versione){
		IDAccordo idAccordo = new IDAccordo();
		idAccordo.setNome(nome);
		idAccordo.setSoggettoReferente(soggettoReferente);
		idAccordo.setVersione(versione);
		return idAccordo;
	}
	
	public String normalizeUri(String uri) throws DriverRegistroServiziException{
		// La uri può non contenere la versione, che invece nella 3.0 è obbligatoria.
		// Facendo la doppia conversione, viene aggiunta la versione di default
		IDAccordoCooperazione idAccordo = this.getIDAccordoFromUri(uri);
		return this.getUriFromIDAccordo(idAccordo);
	}
	
	public String getUriFromIDAccordo(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException{
		
		return this.idAccordoFactory.getUriFromIDAccordo(build(idAccordo));
	}
	
	public String getUriFromAccordo(AccordoCooperazione accordo)  throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDAccordo idAccordo = this.build(accordo.getNome(),BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente()),accordo.getVersione());
		return this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
	}
	
	public String getUriFromValues(String nomeAS,String tipoSoggettoReferente,String nomeSoggettoReferente,int ver)  throws DriverRegistroServiziException{
		if(nomeAS==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDSoggetto soggettoReferente = null;
		if(tipoSoggettoReferente!=null && nomeSoggettoReferente!=null)
			soggettoReferente = new IDSoggetto(tipoSoggettoReferente,nomeSoggettoReferente);
		IDAccordo idAccordo = this.build(nomeAS,soggettoReferente,ver);
		return this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
	}
	
	public String getUriFromValues(String nomeAS,IDSoggetto soggettoReferente,int ver)  throws DriverRegistroServiziException{
		if(nomeAS==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		if(soggettoReferente==null){
			return this.getUriFromValues(nomeAS,null,null,ver);
		}else{
			return this.getUriFromValues(nomeAS,soggettoReferente.getTipo(),soggettoReferente.getNome(),ver);
		}
	}
	
	public IDAccordoCooperazione getIDAccordoFromUri(String uriAccordo) throws DriverRegistroServiziException{
		return this.build(this.idAccordoFactory.getIDAccordoFromUri(uriAccordo));
	}
	
	public IDAccordoCooperazione getIDAccordoFromAccordo(AccordoCooperazione accordo) throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDAccordo idAccordo = this.build(accordo.getNome(),BeanUtilities.getSoggettoReferenteID(accordo.getSoggettoReferente()),accordo.getVersione());
		return this.build(idAccordo);
	}
	
	public IDAccordoCooperazione getIDAccordoFromValues(String nomeAS,String tipoSoggettoReferente,String nomeSoggettoReferente,int ver) throws DriverRegistroServiziException{
		if(nomeAS==null){
			throw new DriverRegistroServiziException("Accordo non fornito");
		}
		IDSoggetto soggettoReferente = null;
		if(tipoSoggettoReferente!=null && nomeSoggettoReferente!=null)
			soggettoReferente = new IDSoggetto(tipoSoggettoReferente,nomeSoggettoReferente);
		IDAccordo idAccordo = this.build(nomeAS,soggettoReferente,ver);
		return this.build(idAccordo);
	}
	public IDAccordoCooperazione getIDAccordoFromValuesWithoutCheck(String nomeAS,String tipoSoggettoReferente,String nomeSoggettoReferente,int ver) throws DriverRegistroServiziException{
		IDSoggetto soggettoReferente = new IDSoggetto(tipoSoggettoReferente,nomeSoggettoReferente);
		IDAccordo idAccordo = this.build(nomeAS,soggettoReferente,ver);
		return this.build(idAccordo);
	}
	public IDAccordoCooperazione getIDAccordoFromValues(String nomeAS,IDSoggetto soggettoReferente,int ver) throws DriverRegistroServiziException{
		if(soggettoReferente==null){
			return this.getIDAccordoFromValues(nomeAS,null,null,ver);
		}else{
			return this.getIDAccordoFromValues(nomeAS,soggettoReferente.getTipo(),soggettoReferente.getNome(),ver);
		}
	}
	
	
}
