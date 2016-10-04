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


package org.openspcoop2.core.registry.driver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.RegistroServizi;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoCoordinamento;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;

/**
 * Utility sui bean del package
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BeanUtilities implements IDriverRegistroServiziGet {

	
	private static final String DRIVER_REGISTRO_SERVIZI_DB = "org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB";
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idAccordo
	 * @param accordoCooperazione
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaAccordoCooperazione(IDAccordoCooperazione idAccordo,AccordoCooperazione accordoCooperazione)throws DriverRegistroServiziException {
		return verificaAccordoCooperazione(idAccordo,accordoCooperazione,true);
	}
	@Override
	public boolean verificaAccordoCooperazione(IDAccordoCooperazione idAccordo,AccordoCooperazione accordoCooperazione,boolean checkID)throws DriverRegistroServiziException{
		AccordoCooperazione beanRegistro = null;
		try{
			if(this.getClass().getName().equals(BeanUtilities.DRIVER_REGISTRO_SERVIZI_DB)){
				Method m = this.getClass().getMethod("getAccordoCooperazione", IDAccordoCooperazione.class,boolean.class);
				beanRegistro = (AccordoCooperazione) m.invoke(this, idAccordo, true);
			}
			else
				beanRegistro =  this.getAccordoCooperazione(idAccordo);
		}catch(DriverRegistroServiziNotFound dNotFound){}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		if(beanRegistro==null){
			if(accordoCooperazione==null)
				return true;
			else
				return false;
		}else{
			if(accordoCooperazione==null)
				return false;
			else
				return beanRegistro.equals(accordoCooperazione,checkID);
		}
	}
	
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idAccordo
	 * @param accordoServizioParteComune
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaAccordoServizioParteComune(IDAccordo idAccordo,AccordoServizioParteComune accordoServizioParteComune)throws DriverRegistroServiziException {
		return verificaAccordoServizioParteComune(idAccordo,accordoServizioParteComune,true);
	}
	@Override
	public boolean verificaAccordoServizioParteComune(IDAccordo idAccordo,AccordoServizioParteComune accordoServizioParteComune,boolean checkID)throws DriverRegistroServiziException{
		AccordoServizioParteComune beanRegistro = null;
		try{
			if(this.getClass().getName().equals(BeanUtilities.DRIVER_REGISTRO_SERVIZI_DB)){
				Method m = this.getClass().getMethod("getAccordoServizioParteComune", IDAccordo.class,boolean.class);
				beanRegistro = (AccordoServizioParteComune) m.invoke(this, idAccordo, true);
			}
			else
				beanRegistro =  this.getAccordoServizioParteComune(idAccordo);
		}catch(DriverRegistroServiziNotFound dNotFound){}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		if(beanRegistro==null){
			if(accordoServizioParteComune==null)
				return true;
			else
				return false;
		}else{
			if(accordoServizioParteComune==null)
				return false;
			else
				return beanRegistro.equals(accordoServizioParteComune,checkID);
		}
	}
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param nomePdd
	 * @param pdd
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaPortaDominio(String nomePdd,PortaDominio pdd)throws DriverRegistroServiziException{
		return this.verificaPortaDominio(nomePdd, pdd,true);
	}
	@Override
	public boolean verificaPortaDominio(String nomePdd,PortaDominio pdd,boolean checkID)throws DriverRegistroServiziException{
		PortaDominio beanRegistro = null;
		try{
			beanRegistro = this.getPortaDominio(nomePdd);
		}catch(DriverRegistroServiziNotFound dNotFound){}
		if(beanRegistro==null){
			if(pdd==null)
				return true;
			else
				return false;
		}else{
			if(pdd==null)
				return false;
			else
				return beanRegistro.equals(pdd,checkID);
		}
	}
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idSoggetto
	 * @param soggetto
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public boolean verificaSoggetto(IDSoggetto idSoggetto,Soggetto soggetto)throws DriverRegistroServiziException {
		return verificaSoggetto(idSoggetto, soggetto,true);
	}
	@Override
	public boolean verificaSoggetto(IDSoggetto idSoggetto,Soggetto soggetto,boolean checkID)throws DriverRegistroServiziException{
		Soggetto beanRegistro = null;
		try{
			beanRegistro = this.getSoggetto(idSoggetto);
		}catch(DriverRegistroServiziNotFound dNotFound){}
		if(beanRegistro==null){
			if(soggetto==null)
				return true;
			else
				return false;
		}else{
			if(soggetto==null)
				return false;
			else
				return beanRegistro.equals(soggetto,checkID);
		}
	}

	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idServizio
	 * @param accordoServizioParteSpecifica
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public boolean verificaAccordoServizioParteSpecifica(IDServizio idServizio,AccordoServizioParteSpecifica accordoServizioParteSpecifica)throws DriverRegistroServiziException {
		return verificaAccordoServizioParteSpecifica(idServizio,accordoServizioParteSpecifica,true);
	}
	@Override
	public boolean verificaAccordoServizioParteSpecifica(IDServizio idServizio,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean checkID)throws DriverRegistroServiziException{
		AccordoServizioParteSpecifica beanRegistro = null;
		try{
			if(this.getClass().getName().equals(BeanUtilities.DRIVER_REGISTRO_SERVIZI_DB)){
				Method m = this.getClass().getMethod("getAccordoServizioParteSpecifica", IDServizio.class,boolean.class);
				beanRegistro = (AccordoServizioParteSpecifica) m.invoke(this, idServizio, true);
			}
			else
				beanRegistro =  this.getAccordoServizioParteSpecifica(idServizio);
		}catch(DriverRegistroServiziNotFound dNotFound){}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		if(beanRegistro==null){
			if(accordoServizioParteSpecifica==null)
				return true;
			else
				return false;
		}else{
			if(accordoServizioParteSpecifica==null)
				return false;
			else
				return beanRegistro.equals(accordoServizioParteSpecifica,checkID);
		}
	}
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idAccordo
	 * @param accordoServizioParteSpecifica
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public boolean verificaAccordoServizioParteSpecifica(IDAccordo idAccordo,AccordoServizioParteSpecifica accordoServizioParteSpecifica)throws DriverRegistroServiziException {
		return verificaAccordoServizioParteSpecifica(idAccordo,accordoServizioParteSpecifica,true);
	}
	@Override
	public boolean verificaAccordoServizioParteSpecifica(IDAccordo idAccordo,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean checkID)throws DriverRegistroServiziException{
		AccordoServizioParteSpecifica beanRegistro = null;
		try{
			if(this.getClass().getName().equals(BeanUtilities.DRIVER_REGISTRO_SERVIZI_DB)){
				Method m = this.getClass().getMethod("getAccordoServizioParteSpecifica", IDAccordo.class,boolean.class);
				beanRegistro = (AccordoServizioParteSpecifica) m.invoke(this, idAccordo, true);
			}
			else
				beanRegistro =  this.getAccordoServizioParteSpecifica(idAccordo);
		}catch(DriverRegistroServiziNotFound dNotFound){}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		if(beanRegistro==null){
			if(accordoServizioParteSpecifica==null)
				return true;
			else
				return false;
		}else{
			if(accordoServizioParteSpecifica==null)
				return false;
			else
				return beanRegistro.equals(accordoServizioParteSpecifica,checkID);
		}
	}
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @param idAccordoServizioParteComune ID dell'accordo che deve implementare il servizio correlato
	 * @param accordoServizioParteSpecifica
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public boolean verificaAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto, IDAccordo idAccordoServizioParteComune,AccordoServizioParteSpecifica accordoServizioParteSpecifica)throws DriverRegistroServiziException {
		return verificaAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto,idAccordoServizioParteComune,accordoServizioParteSpecifica,true);
	}
	@Override
	public boolean verificaAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto, IDAccordo idAccordoServizioParteComune,AccordoServizioParteSpecifica accordoServizioParteSpecifica,boolean checkID)throws DriverRegistroServiziException{
		AccordoServizioParteSpecifica beanRegistro = null;
		try{
			if(this.getClass().getName().equals(BeanUtilities.DRIVER_REGISTRO_SERVIZI_DB)){
				Method m = this.getClass().getMethod("getAccordoServizioParteSpecifica_ServizioCorrelato", IDSoggetto.class,IDAccordo.class,boolean.class);
				beanRegistro = (AccordoServizioParteSpecifica) m.invoke(this, idSoggetto, idAccordoServizioParteComune, true);
			}
			else
				beanRegistro =  this.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto,idAccordoServizioParteComune);
		}catch(DriverRegistroServiziNotFound dNotFound){}
		catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		if(beanRegistro==null){
			if(accordoServizioParteSpecifica==null)
				return true;
			else
				return false;
		}else{
			if(accordoServizioParteSpecifica==null)
				return false;
			else
				return beanRegistro.equals(accordoServizioParteSpecifica,checkID);
		}
	}
	
	
	
	
	public static IDSoggetto getSoggettoReferenteID(IdSoggetto ass){
		if(ass!=null && ass.getTipo()!=null && ass.getNome()!=null){
			return new IDSoggetto(ass.getTipo(),ass.getNome());
		}else{
			return null;
		}
	}

	public static IDSoggetto getSoggettoErogatore(Servizio asps){
		if(asps!=null && asps.getTipoSoggettoErogatore()!=null && asps.getNomeSoggettoErogatore()!=null){
			return new IDSoggetto(asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore());
		}else{
			return null;
		}
	}
	
	
	public static void validateTipoRuolo(String tipo,String ruolo)throws DriverRegistroServiziException{
		if(RuoliDocumento.allegato.toString().equals(ruolo)){
			// qualsiasi tipo e' accettato
			return;
		}else if(RuoliDocumento.specificaSemiformale.toString().equals(ruolo)){
			if(TipiDocumentoSemiformale.HTML.toString().equals(tipo)==false &&
					TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString().equals(tipo)==false &&
					TipiDocumentoSemiformale.UML.toString().equals(tipo)==false &&
					TipiDocumentoSemiformale.XML.toString().equals(tipo)==false){
				throw new DriverRegistroServiziException("Tipo["+tipo+"] non accettato per il Ruolo["+ruolo+"], valori ammessi sono: "+
						TipiDocumentoSemiformale.HTML.toString()+","
						+TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString()+","
						+TipiDocumentoSemiformale.UML.toString()+","
						+TipiDocumentoSemiformale.XML.toString());
			}
		}else if(RuoliDocumento.specificaLivelloServizio.toString().equals(ruolo)){
			if(TipiDocumentoLivelloServizio.WSAGREEMENT.toString().equals(tipo)==false &&
					TipiDocumentoLivelloServizio.WSLA.toString().equals(tipo)==false){
				throw new DriverRegistroServiziException("Tipo["+tipo+"] non accettato per il Ruolo["+ruolo+"], valori ammessi sono: "+
						TipiDocumentoLivelloServizio.WSAGREEMENT.toString()+","
						+TipiDocumentoLivelloServizio.WSLA.toString());
			}
		}else if(RuoliDocumento.specificaSicurezza.toString().equals(ruolo)){
			if(TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString().equals(tipo)==false &&
					TipiDocumentoSicurezza.WSPOLICY.toString().equals(tipo)==false){
				throw new DriverRegistroServiziException("Tipo["+tipo+"] non accettato per il Ruolo["+ruolo+"], valori ammessi sono: "+
						TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString()+","
						+TipiDocumentoSicurezza.WSPOLICY.toString());
			}
		}else if(RuoliDocumento.specificaCoordinamento.toString().equals(ruolo)){
			if(TipiDocumentoCoordinamento.BPEL.toString().equals(tipo)==false &&
					TipiDocumentoCoordinamento.WSCDL.toString().equals(tipo)==false){
				throw new DriverRegistroServiziException("Tipo["+tipo+"] non accettato per il Ruolo["+ruolo+"], valori ammessi sono: "+
						TipiDocumentoCoordinamento.BPEL.toString()+","
						+TipiDocumentoCoordinamento.WSCDL.toString());
			}
		}else{
			throw new DriverRegistroServiziException("Ruolo non conosciuto");
		}
	}
	
	/**
	 * Dato un bean {@link AccordoServizioParteComune}, {@link AccordoCooperazione}, {@link AccordoServizioParteSpecifica} ritorna la lista
	 * di tutti i {@link Documento} contenuti nel bean, recuperandoli dagli array interni al bean.
	 * @param <T> -{@link AccordoServizioParteComune}, {@link AccordoCooperazione}, {@link AccordoServizioParteSpecifica}
	 * @param o -Il bean del quale si vogliono tutti i documenti
	 * @return La lista dei documenti. La lista non e' mai null.
	 */
	public static <T> ArrayList<Documento> getAllDocumenti(T o){
		ArrayList<Documento> lista = new ArrayList<Documento>();
		
		if(o instanceof AccordoServizioParteComune){
			AccordoServizioParteComune b = (AccordoServizioParteComune)o;
			
			if(b.sizeAllegatoList()>0){
				for (Documento documento : b.getAllegatoList()) {
					lista.add(documento);
				}
			}
			if(b.sizeSpecificaSemiformaleList()>0){
				for (Documento documento : b.getSpecificaSemiformaleList()) {
					lista.add(documento);
				}
			}
		}else if (o instanceof AccordoCooperazione){
			AccordoCooperazione b = (AccordoCooperazione)o;
			
			if(b.sizeAllegatoList()>0){
				for (Documento documento : b.getAllegatoList()) {
					lista.add(documento);
				}
			}
			if(b.sizeSpecificaSemiformaleList()>0){
				for (Documento documento : b.getSpecificaSemiformaleList()) {
					lista.add(documento);
				}
			}
			
		}else if(o instanceof AccordoServizioParteSpecifica){
			AccordoServizioParteSpecifica b = (AccordoServizioParteSpecifica)o;
			
			if(b.sizeAllegatoList()>0){
				for (Documento documento : b.getAllegatoList()) {
					lista.add(documento);
				}
			}
			if(b.sizeSpecificaSemiformaleList()>0){
				for (Documento documento : b.getSpecificaSemiformaleList()) {
					lista.add(documento);
				}
			}
			if(b.sizeSpecificaLivelloServizioList()>0){
				for (Documento documento : b.getSpecificaLivelloServizioList()) {
					lista.add(documento);
				}
			}
			if(b.sizeSpecificaSicurezzaList()>0){
				for (Documento documento : b.getSpecificaSicurezzaList()) {
					lista.add(documento);
				}
			}
		}
		
		//lista vuota
		return lista;
		
	}
	
	
	
	
	
	
	public RegistroServizi getImmagineCompletaRegistroServizi() throws DriverRegistroServiziException{

		RegistroServizi registroServizi = new RegistroServizi();
		
		// Porte di Dominio
		List<String> idPdd = null;
		try{
			idPdd = this.getAllIdPorteDominio(new FiltroRicerca());
		}catch(DriverRegistroServiziNotFound dNotFound){}
		if(idPdd!=null){
			for(int i=0; i<idPdd.size(); i++){
				try{
					registroServizi.addPortaDominio(this.getPortaDominio(idPdd.get(i)));
				}catch(DriverRegistroServiziNotFound dNotfound){
					throw new DriverRegistroServiziException("Porta di Dominio ["+idPdd.get(i)+"] trovato tramite getAllId ma poi non recuperabile: "+dNotfound.getMessage(),dNotfound);
				}
			}
		}
		
		// Accordi di Cooperazione
		List<IDAccordoCooperazione> idAccordiCooperazione = null;
		try{
			idAccordiCooperazione = this.getAllIdAccordiCooperazione(new FiltroRicercaAccordi());
		}catch(DriverRegistroServiziNotFound dNotFound){}
		if(idAccordiCooperazione!=null){
			for(int i=0; i<idAccordiCooperazione.size(); i++){
				try{
					registroServizi.addAccordoCooperazione(this.getAccordoCooperazione(idAccordiCooperazione.get(i)));
				}catch(DriverRegistroServiziNotFound dNotfound){
					throw new DriverRegistroServiziException("Accordo di cooperazione ["+idAccordiCooperazione.get(i)+"] trovato tramite getAllId ma poi non recuperabile: "+dNotfound.getMessage(),dNotfound);
				}
			}
		}
		
		// Accordi di Servizio
		List<IDAccordo> idAccordiServizio = null;
		try{
			idAccordiServizio = this.getAllIdAccordiServizioParteComune(new FiltroRicercaAccordi());
		}catch(DriverRegistroServiziNotFound dNotFound){}
		if(idAccordiServizio!=null){
			for(int i=0; i<idAccordiServizio.size(); i++){
				try{
					registroServizi.addAccordoServizioParteComune(this.getAccordoServizioParteComune(idAccordiServizio.get(i)));
				}catch(DriverRegistroServiziNotFound dNotfound){
					throw new DriverRegistroServiziException("Accordo di servizio parte comune ["+idAccordiServizio.get(i)+"] trovato tramite getAllId ma poi non recuperabile: "+dNotfound.getMessage(),dNotfound);
				}
			}
		}
		
		// Soggetti
		List<IDSoggetto> idSoggetti = null;
		try{
			idSoggetti = this.getAllIdSoggetti(new FiltroRicercaSoggetti());
		}catch(DriverRegistroServiziNotFound dNotFound){}
		if(idSoggetti!=null){
			for(int i=0; i<idSoggetti.size(); i++){
				try{
					Soggetto soggetto = this.getSoggetto(idSoggetti.get(i));
					
					// I servizi possono contenere solo meta-informazioni (es. Driver DB)
					while(soggetto.sizeAccordoServizioParteSpecificaList()>0){
						soggetto.removeAccordoServizioParteSpecifica(0);
					}
					
					FiltroRicercaServizi filtro = new FiltroRicercaServizi();
					filtro.setTipoSoggettoErogatore(soggetto.getTipo());
					filtro.setNomeSoggettoErogatore(soggetto.getNome());
					
					// Recupero servizi con informazioni complete
					List<IDServizio> idServizi = null;
					try{
						idServizi = this.getAllIdServizi(filtro);
					}catch(DriverRegistroServiziNotFound dNotFound){}
					if(idServizi!=null){
						for(int j=0; j<idServizi.size(); j++){
							try{
								AccordoServizioParteSpecifica s = this.getAccordoServizioParteSpecifica(idServizi.get(j));
								soggetto.addAccordoServizioParteSpecifica(s);
							}catch(DriverRegistroServiziNotFound dNotfound){
								throw new DriverRegistroServiziException("Accordo Servizio Parte Specifica ["+idServizi.get(j)+"] trovato tramite getAllId ma poi non recuperabile: "+dNotfound.getMessage(),dNotfound);
							}
						}
					}
					
					registroServizi.addSoggetto(soggetto);
				}catch(DriverRegistroServiziNotFound dNotfound){
					throw new DriverRegistroServiziException("Soggetto ["+idSoggetti.get(i)+"] trovato tramite getAllId ma poi non recuperabile: "+dNotfound.getMessage(),dNotfound);
				}
			}
		}
		
		return registroServizi;
	}
}
