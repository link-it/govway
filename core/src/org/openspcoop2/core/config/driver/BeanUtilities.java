/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


package org.openspcoop2.core.config.driver;

import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.Openspcoop2;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;


/**
 * Utility sui bean del package
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BeanUtilities implements IDriverConfigurazioneGet {

	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idSoggetto
	 * @param soggetto
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaSoggetto(IDSoggetto idSoggetto,Soggetto soggetto)throws DriverConfigurazioneException {
		return verificaSoggetto(idSoggetto, soggetto,true);
	}
	@Override
	public boolean verificaSoggetto(IDSoggetto idSoggetto,Soggetto soggetto,boolean checkID)throws DriverConfigurazioneException{
		Soggetto beanRegistro = null;
		try{
			beanRegistro = this.getSoggetto(idSoggetto);
		}catch(DriverConfigurazioneNotFound dNotFound){}
		if(beanRegistro==null){
			if(soggetto==null){
				return true;
			}else{
				return false;
			}
		}else{
			if(soggetto==null){
				return false;
			}
			else{
				return beanRegistro.equals(soggetto,checkID);
			}
		}
	}
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param soggetto
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaRouter(Soggetto soggetto)throws DriverConfigurazioneException {
		return verificaRouter(soggetto,true);
	}
	@Override
	public boolean verificaRouter(Soggetto soggetto,boolean checkID)throws DriverConfigurazioneException{
		Soggetto beanRegistro = null;
		try{
			beanRegistro = this.getRouter();
		}catch(DriverConfigurazioneNotFound dNotFound){}
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
	 * @param idPD
	 * @param pd
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaPortaDelegata(IDPortaDelegata idPD,PortaDelegata pd)throws DriverConfigurazioneException {
		return verificaPortaDelegata(idPD,pd,true);
	}
	@Override
	public boolean verificaPortaDelegata(IDPortaDelegata idPD,PortaDelegata pd,boolean checkID)throws DriverConfigurazioneException{
		PortaDelegata beanRegistro = null;
		try{
			beanRegistro = this.getPortaDelegata(idPD);
		}catch(DriverConfigurazioneNotFound dNotFound){}
		if(beanRegistro==null){
			if(pd==null)
				return true;
			else
				return false;
		}else{
			if(pd==null)
				return false;
			else
				return beanRegistro.equals(pd,checkID);
		}
	}
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * Utilizza la porta applicativa identificata da <var>idPA</var>
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, viene ricercata
	 * una Porta Applicativa che non possegga l'azione
	 * 
	 * @param idPA
	 * @param pa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaPortaApplicativa(IDPortaApplicativa idPA,PortaApplicativa pa)throws DriverConfigurazioneException {
		return verificaPortaApplicativa(idPA,pa,true);
	}
	@Override
	public boolean verificaPortaApplicativa(IDPortaApplicativa idPA,PortaApplicativa pa,boolean checkID)throws DriverConfigurazioneException{
		PortaApplicativa beanRegistro = null;
		try{
			beanRegistro = this.getPortaApplicativa(idPA);
		}catch(DriverConfigurazioneNotFound dNotFound){}
		if(beanRegistro==null){
			if(pa==null)
				return true;
			else
				return false;
		}else{
			if(pa==null)
				return false;
			else
				return beanRegistro.equals(pa,checkID);
		}
	}
	
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param idSA
	 * @param sa
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaServizioApplicativo(IDServizioApplicativo idSA,ServizioApplicativo sa)throws DriverConfigurazioneException {
		return verificaServizioApplicativo(idSA,sa,true);
	}
	@Override
	public boolean verificaServizioApplicativo(IDServizioApplicativo idSA,ServizioApplicativo sa,boolean checkID)throws DriverConfigurazioneException{
		ServizioApplicativo beanRegistro = null;
		try{
			beanRegistro = this.getServizioApplicativo(idSA);
		}catch(DriverConfigurazioneNotFound dNotFound){}
		if(beanRegistro==null){
			if(sa==null)
				return true;
			else
				return false;
		}else{
			if(sa==null)
				return false;
			else
				return beanRegistro.equals(sa,checkID);
		}
	}
	

	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param rt
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaRoutingTable(RoutingTable rt)throws DriverConfigurazioneException {
		return verificaRoutingTable(rt,true);
	}
	@Override
	public boolean verificaRoutingTable(RoutingTable rt,boolean checkID)throws DriverConfigurazioneException{
		RoutingTable beanRegistro = this.getRoutingTable();
		if(beanRegistro==null){
			if(rt==null)
				return true;
			else
				return false;
		}else{
			if(rt==null)
				return false;
			else
				return beanRegistro.equals(rt,checkID);
		}
	}
	
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param ar
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaAccessoRegistro(AccessoRegistro ar)throws DriverConfigurazioneException {
		return verificaAccessoRegistro(ar,true);
	}
	@Override
	public boolean verificaAccessoRegistro(AccessoRegistro ar,boolean checkID)throws DriverConfigurazioneException{
		AccessoRegistro beanRegistro = null;
		try{
			beanRegistro = this.getAccessoRegistro();
		}catch(DriverConfigurazioneNotFound dNotFound){}
		if(beanRegistro==null){
			if(ar==null)
				return true;
			else
				return false;
		}else{
			if(ar==null)
				return false;
			else
				return beanRegistro.equals(ar,checkID);
		}
	}
	
	/**
	 * Controlla che il bean presente, sia uguale al bean passato come parametro
	 * 
	 * @param ac
	 * @return true se il bean presente, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaAccessoConfigurazione(AccessoConfigurazione ac)throws DriverConfigurazioneException {
		return verificaAccessoConfigurazione(ac,true);
	}
	@Override
	public boolean verificaAccessoConfigurazione(AccessoConfigurazione ac,boolean checkID)throws DriverConfigurazioneException{
		AccessoConfigurazione bean = null;
		try{
			bean = this.getAccessoConfigurazione();
		}catch(DriverConfigurazioneNotFound dNotFound){}
		if(bean==null){
			if(ac==null)
				return true;
			else
				return false;
		}else{
			if(ac==null)
				return false;
			else
				return bean.equals(ac,checkID);
		}
	}
	
	/**
	 * Controlla che il bean presente, sia uguale al bean passato come parametro
	 * 
	 * @param ad
	 * @return true se il bean presente, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaAccessoDatiAutorizzazione(AccessoDatiAutorizzazione ad)throws DriverConfigurazioneException {
		return verificaAccessoDatiAutorizzazione(ad,true);
	}
	@Override
	public boolean verificaAccessoDatiAutorizzazione(AccessoDatiAutorizzazione ad,boolean checkID)throws DriverConfigurazioneException{
		AccessoDatiAutorizzazione bean = null;
		try{
			bean = this.getAccessoDatiAutorizzazione();
		}catch(DriverConfigurazioneNotFound dNotFound){}
		if(bean==null){
			if(ad==null)
				return true;
			else
				return false;
		}else{
			if(ad==null)
				return false;
			else
				return bean.equals(ad,checkID);
		}
	}
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param gr
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaGestioneErroreComponenteCooperazione(GestioneErrore gr)throws DriverConfigurazioneException {
		return verificaGestioneErroreComponenteCooperazione(gr,true);
	}
	@Override
	public boolean verificaGestioneErroreComponenteCooperazione(GestioneErrore gr,boolean checkID)throws DriverConfigurazioneException{
		GestioneErrore beanRegistro = null;
		try{
			beanRegistro = this.getGestioneErroreComponenteCooperazione();
		}catch(DriverConfigurazioneNotFound dNotFound){}
		if(beanRegistro==null){
			if(gr==null)
				return true;
			else
				return false;
		}else{
			if(gr==null)
				return false;
			else
				return beanRegistro.equals(gr,checkID);
		}
	}
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param gr
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaGestioneErroreComponenteIntegrazione(GestioneErrore gr)throws DriverConfigurazioneException {
		return verificaGestioneErroreComponenteIntegrazione(gr,true);
	}
	@Override
	public boolean verificaGestioneErroreComponenteIntegrazione(GestioneErrore gr,boolean checkID)throws DriverConfigurazioneException{
		GestioneErrore beanRegistro = null;
		try{
			beanRegistro = this.getGestioneErroreComponenteIntegrazione();
		}catch(DriverConfigurazioneNotFound dNotFound){}
		if(beanRegistro==null){
			if(gr==null)
				return true;
			else
				return false;
		}else{
			if(gr==null)
				return false;
			else
				return beanRegistro.equals(gr,checkID);
		}
	}
	
	/**
	 * Controlla che il bean presente nel registro, sia uguale al bean passato come parametro
	 * 
	 * @param configurazione
	 * @return true se il bean presente nel registro, sia uguale al bean passato come parametro
	 */
	@Override
	public boolean verificaConfigurazione(Configurazione configurazione)throws DriverConfigurazioneException {
		return verificaConfigurazione(configurazione,true);
	}
	@Override
	public boolean verificaConfigurazione(Configurazione configurazione,boolean checkID)throws DriverConfigurazioneException{
		Configurazione beanRegistro = null;
		try{
			beanRegistro = this.getConfigurazioneGenerale();
		}catch(DriverConfigurazioneNotFound dNotFound){}
		if(beanRegistro==null){
			if(configurazione==null)
				return true;
			else
				return false;
		}else{
			if(configurazione==null)
				return false;
			else
				return beanRegistro.equals(configurazione,checkID);
		}
	}
	
	
	
	public abstract Openspcoop2 getImmagineCompletaConfigurazionePdD() throws DriverConfigurazioneException;
}
