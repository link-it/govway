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


package org.openspcoop2.web.ctrlstat.driver;

import java.util.Vector;

import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.pdd.monitor.driver.FilterSearch;
import org.openspcoop2.utils.serialization.IOException;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.PoliticheSicurezza;
import org.openspcoop2.web.ctrlstat.dao.Ruolo;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.lib.audit.dao.Filtro;
import org.openspcoop2.web.lib.audit.dao.Operation;
import org.openspcoop2.web.lib.users.dao.User;



/**
 * Classe utilizzata per generare gli identificatori degli oggetti presenti nella pddConsole.
 *
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class IDBuilder implements org.openspcoop2.utils.serialization.IDBuilder {

	private boolean prefix = false;
	
	private IDAccordoFactory idAccordoFactory = null;
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = null;
	
	public IDBuilder(boolean insertClassNamePrefix){
		this.prefix = insertClassNamePrefix; 
		this.idAccordoFactory = IDAccordoFactory.getInstance();
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
	}
	public IDBuilder(){
		this(false);
	}
	
	public static IDBuilder getIDBuilder(){
		return new IDBuilder();
	}
	
	@Override
	public String toID(Object o) throws IOException {
		
		if(o==null)
			throw new IOException("Oggetto is null");
		
		try{
		
			
			// ControlStation
			if (o instanceof PdDControlStation) {
				PdDControlStation p = (PdDControlStation) o;
				String id = p.getNome();
				if(this.prefix){
					return "[PdDControlStation] "+ id;
				}else{
					return id;
				}
			}
			else if (o instanceof Ruolo) {
				Ruolo r = (Ruolo) o;
				String id = r.getNome();
				if(r.isCorrelato()){
					id = id+" (correlato)";
				}else{
					id = id+" (normale)";
				}
				if(this.prefix){
					return "[Ruolo] "+ id;
				}else{
					return id;
				}
			}
			else if (o instanceof PoliticheSicurezza) {
				PoliticheSicurezza p = (PoliticheSicurezza) o;
				StringBuffer bf = new StringBuffer();
				bf.append("FR[");
				bf.append(p.getIdFruitore());
				bf.append("] ER[");
				bf.append(p.getIdServizio());
				bf.append("] SA["+p.getIdServizioApplicativo());
				bf.append("] "+p.getNomeServizioApplicativo());
				if(this.prefix){
					return "[PoliticheSicurezza] "+ bf.toString();
				}else{
					return bf.toString();
				}
			}
			else if (o instanceof SoggettoCtrlStat) {
				SoggettoCtrlStat s = (SoggettoCtrlStat) o;
				String id = s.getTipo()+"/"+s.getNome();
				if(this.prefix){
					return "[SoggettoCtrlStat] "+ id;
				}else{
					return id;
				}
			}
			
			// RegistroServizi
			
			else if(o instanceof org.openspcoop2.core.registry.Soggetto){
				org.openspcoop2.core.registry.Soggetto s = (org.openspcoop2.core.registry.Soggetto) o;
				String id = s.getTipo()+"/"+s.getNome();
				if(this.prefix){
					return "[SoggettoRegistro] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof AccordoCooperazione){
				AccordoCooperazione ac = (AccordoCooperazione) o;
				String id = this.idAccordoCooperazioneFactory.getUriFromAccordo(ac);
				if(this.prefix){
					return "[AccordoCooperazione] "+id;
				}else{
					return id;
				}
			}
			else if(o instanceof AccordoServizioParteComune){
				AccordoServizioParteComune as = (AccordoServizioParteComune) o;
				String id = this.idAccordoFactory.getUriFromAccordo(as);
				if(this.prefix){
					return "[AccordoServizioParteComune] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof PortType){
				PortType p = (PortType) o;
				String id = "IDAccordo["+p.getIdAccordo()+"]_"+  p.getNome();
				if(this.prefix){
					return "[PortType] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof Documento){
				Documento d = (Documento) o;
				String id = "["+d.getRuolo()+"]["+d.getTipo()+"]"+" "+d.getFile();
				if(this.prefix){
					return "[Documento] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof PortaDominio){
				PortaDominio p = (PortaDominio) o;
				String id = p.getNome();
				if(this.prefix){
					return "[PdD] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof AccordoServizioParteSpecifica){
				AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) o;
				Servizio s = asps.getServizio();
				String id = s.getTipoSoggettoErogatore()+"/"+s.getNomeSoggettoErogatore()+"#"+
					s.getTipo()+"/"+s.getNome();
				if(this.prefix){
					return "[AccordoServizioParteSpecifica] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof Soggetto){
				Soggetto s = (Soggetto) o;
				String id = s.getTipo()+"/"+s.getNome();
				if(this.prefix){
					return "[Soggetto] "+ id;
				}else{
					return id;
				}
			}
			
			// Configurazione
			else if(o instanceof org.openspcoop2.core.config.Soggetto){
				org.openspcoop2.core.config.Soggetto s = (org.openspcoop2.core.config.Soggetto) o;
				String id = s.getTipo()+"/"+s.getNome();
				if(this.prefix){
					return "[SoggettoConfigurazione] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof ServizioApplicativo){
				ServizioApplicativo s = (ServizioApplicativo) o;
				String id = s.getTipoSoggettoProprietario()+"/"+s.getNomeSoggettoProprietario()+"_"+s.getNome();
				if(this.prefix){
					return "[ServizioApplicativo] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof PortaDelegata){
				PortaDelegata pd = (PortaDelegata) o;
				String id = pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario()+"_"+pd.getNome();
				if(this.prefix){
					return "[PortaDelegata] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof PortaApplicativa){
				PortaApplicativa pa = (PortaApplicativa) o;
				String id = pa.getTipoSoggettoProprietario()+"/"+pa.getNomeSoggettoProprietario()+"_"+pa.getNome();
				if(this.prefix){
					return "[PortaApplicativa] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof RoutingTable){
				return "RoutingTable";
			}else if(o instanceof GestioneErrore){
				return "GestioneErrore";
			}else if(o instanceof Configurazione){
				return "Configurazione";
			}else if(o instanceof AccessoRegistro){
				return "ConfigurazioneRegistroServizi";
			}else if(o instanceof AccessoRegistroRegistro){
				AccessoRegistroRegistro registro = (AccessoRegistroRegistro) o;
				String id = registro.getNome();
				if(this.prefix){
					return "[ConfigurazioneRegistroServizi] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof AccessoConfigurazione){
				return "ConfigurazioneAccessoDati";
			}else if(o instanceof AccessoDatiAutorizzazione){
				return "ConfigurazioneAccessoDatiAutorizzazione";
			}else if(o instanceof SystemProperties){
				return "ProprietàDiSistema";
			}
			
			
			// Users
			else if(o instanceof User){
				User u = (User) o;
				String id = u.getLogin(); 
				if(this.prefix){
					return "[User] "+ id;
				}else{
					return id;
				}
			}
			
			
			// Auditing
			else if(o instanceof org.openspcoop2.web.lib.audit.dao.Configurazione){
				return "Configurazione Auditing";
			}
			
			else if(o instanceof Filtro){
				Filtro f = (Filtro) o;
				String id = f.toString();
				if(this.prefix){
					return "[Filtro] "+ id;
				}else{
					return id;
				}
			}
			
			// Auditing: eliminazione record salvato
			else if(o instanceof org.openspcoop2.web.lib.audit.dao.Operation){
				org.openspcoop2.web.lib.audit.dao.Operation op = (org.openspcoop2.web.lib.audit.dao.Operation) o;
				if(this.prefix){
					return "[OperazioneRegistrataTramiteAuditing] "+ op.getId();
				}else{
					return op.getId()+"";
				}
			}
			
			
			// Monitoraggio Applicativo
			else if(o instanceof FilterSearch){
				FilterSearch f = (FilterSearch) o;
				String id = f.toString();
				if(this.prefix){
					return "[EliminazioneMessaggiTramiteMonitoraggio] "+ id;
				}else{
					return id;
				}
			}
			
			
			// IExtendedBean
			else if(o instanceof IExtendedBean){
				IExtendedBean w = (IExtendedBean) o;
				if(this.prefix){
					return "[ExtendedBean-"+w.getClass().getSimpleName()+"] "+ w.getHumanId();
				}else{
					return w.getHumanId();
				}
			}
			
			
						
		}catch(Exception e){
			throw new IOException("Trasformazione non riuscita: "+e.getMessage(),e);
		}
		
		throw new IOException("Tipo di Oggetto non gestito ["+o.getClass().getName()+"]");
	}

	@Override
	public String toID(Object o, String field) throws IOException {
		if(o!=null && o instanceof Documento){
			return this.toID(o);
		}else{
			return this.toID(o) + "." + field;
		}
	}
	
	
	/**
	 * Genera il vecchio identificatore che identificava l'oggetto passato come parametro prima di un update in corso
	 * L'oggetto in corso deve essere valorizzato negli elementi old_XXX
	 * 
	 * @param o Oggetto su cui generare un identificatore univoco
	 * @return identificatore univoco
	 */
	@Override
	public String toOldID(Object o) throws IOException{
	
		
		if(o==null)
			throw new IOException("Oggetto is null");
		
		try{
		
			
			// ControlStation
			if (o instanceof PdDControlStation) {
				PdDControlStation p = (PdDControlStation) o;
				if(p.getOldNomeForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = p.getOldNomeForUpdate();
				if(this.prefix){
					return "[PdDControlStation] "+ id;
				}else{
					return id;
				}
			}
			else if (o instanceof Ruolo) {
				Ruolo r = (Ruolo) o;
				if(r.getOldNomeForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = r.getOldNomeForUpdate();
				if(this.prefix){
					return "[Ruolo] "+ id;
				}else{
					return id;
				}
			}
			else if (o instanceof PoliticheSicurezza) {
				return null; // oggetto non modificabile nei dati identificativi
			}
			else if (o instanceof SoggettoCtrlStat) {
				SoggettoCtrlStat s = (SoggettoCtrlStat) o;
				if(s.getOldTipoForUpdate()==null || s.getOldNomeForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = s.getOldTipoForUpdate()+"/"+s.getOldNomeForUpdate();
				if(this.prefix){
					return "[SoggettoCtrlStat] "+ id;
				}else{
					return id;
				}
			}
			
			// RegistroServizi
			
			else if(o instanceof org.openspcoop2.core.registry.Soggetto){
				org.openspcoop2.core.registry.Soggetto s = (org.openspcoop2.core.registry.Soggetto) o;
				if(s.getOldTipoForUpdate()==null || s.getOldNomeForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = s.getOldTipoForUpdate()+"/"+s.getOldNomeForUpdate();
				if(this.prefix){
					return "[SoggettoRegistro] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof AccordoCooperazione){
				AccordoCooperazione ac = (AccordoCooperazione) o;
				IDAccordoCooperazione idOLD = ac.getOldIDAccordoForUpdate();
				if(idOLD==null){
					return null; // non lancio un errore
				}
				String id = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idOLD);
				if(this.prefix){
					return "[AccordoCooperazione] "+id;
				}else{
					return id;
				}
			}
			else if(o instanceof AccordoServizioParteComune){
				AccordoServizioParteComune as = (AccordoServizioParteComune) o;
				IDAccordo idOLD = as.getOldIDAccordoForUpdate();
				if(idOLD==null){
					return null; // non lancio un errore
				}
				String id = this.idAccordoFactory.getUriFromIDAccordo(idOLD);
				if(this.prefix){
					return "[AccordoServizioParteComune] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof PortType){
				return null; // oggetto non modificabile nei dati identificativi
			}
			else if(o instanceof Documento){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof PortaDominio){
				PortaDominio p = (PortaDominio) o;
				if(p.getOldNomeForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = p.getOldNomeForUpdate();
				if(this.prefix){
					return "[PdD] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof AccordoServizioParteSpecifica){
				AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) o;
				Servizio s = asps.getServizio();
				if(s.getOldTipoSoggettoErogatoreForUpdate()==null && s.getOldNomeSoggettoErogatoreForUpdate()==null &&
						s.getOldTipoForUpdate()==null && s.getOldNomeForUpdate()==null){
					return null; // non lancio un errore
				}
				
				String id = null;
				if(s.getOldTipoSoggettoErogatoreForUpdate()!=null && s.getOldNomeSoggettoErogatoreForUpdate()!=null &&
						s.getOldTipoForUpdate()!=null && s.getOldNomeForUpdate()!=null){
					id = s.getOldTipoSoggettoErogatoreForUpdate()+"/"+s.getOldNomeSoggettoErogatoreForUpdate()+"#"+
						s.getOldTipoForUpdate()+"/"+s.getOldNomeForUpdate();
				}
				else if(s.getOldTipoSoggettoErogatoreForUpdate()==null && s.getOldNomeSoggettoErogatoreForUpdate()==null){
					id = s.getTipoSoggettoErogatore()+"/"+s.getNomeSoggettoErogatore()+"#"+
					s.getOldTipoForUpdate()+"/"+s.getOldNomeForUpdate();
				}
				else{
					id = s.getOldTipoSoggettoErogatoreForUpdate()+"/"+s.getOldNomeSoggettoErogatoreForUpdate()+"#"+
					s.getTipo()+"/"+s.getNome();
				}
				if(this.prefix){
					return "[AccordoServizioParteSpecifica] "+ id;
				}else{
					return id;
				}
			}
			
			// Configurazione
			else if(o instanceof org.openspcoop2.core.config.Soggetto){
				org.openspcoop2.core.config.Soggetto s = (org.openspcoop2.core.config.Soggetto) o;
				if(s.getOldTipoForUpdate()==null || s.getOldNomeForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = s.getOldTipoForUpdate()+"/"+s.getOldNomeForUpdate();
				if(this.prefix){
					return "[SoggettoConfigurazione] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof ServizioApplicativo){
				ServizioApplicativo s = (ServizioApplicativo) o;
				if(s.getOldTipoSoggettoProprietarioForUpdate()==null && s.getOldNomeSoggettoProprietarioForUpdate()==null && s.getOldNomeForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = null;
				if(s.getOldTipoSoggettoProprietarioForUpdate()!=null && s.getOldNomeSoggettoProprietarioForUpdate()!=null && s.getOldNomeForUpdate()!=null){
					id = s.getOldTipoSoggettoProprietarioForUpdate()+"/"+s.getOldNomeSoggettoProprietarioForUpdate()+"_"+s.getOldNomeForUpdate();
				}
				else if(s.getOldNomeForUpdate()!=null){
					id = s.getTipoSoggettoProprietario()+"/"+s.getNomeSoggettoProprietario()+"_"+s.getOldNomeForUpdate();
				}
				else if(s.getOldTipoSoggettoProprietarioForUpdate()==null || s.getOldNomeSoggettoProprietarioForUpdate()==null){
					throw new DriverConfigurazioneException("Oggetto in modifica non correttamente valorizzato");
				}
				else{
					id = s.getOldTipoSoggettoProprietarioForUpdate()+"/"+s.getOldNomeSoggettoProprietarioForUpdate()+"_"+s.getNome();
				}				
				if(this.prefix){
					return "[ServizioApplicativo] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof PortaDelegata){
				PortaDelegata pd = (PortaDelegata) o;
				if(pd.getOldTipoSoggettoProprietarioForUpdate()==null && pd.getOldNomeSoggettoProprietarioForUpdate()==null && pd.getOldNomeForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = null;
				if(pd.getOldTipoSoggettoProprietarioForUpdate()!=null && pd.getOldNomeSoggettoProprietarioForUpdate()!=null && pd.getOldNomeForUpdate()!=null){
					id = pd.getOldTipoSoggettoProprietarioForUpdate()+"/"+pd.getOldNomeSoggettoProprietarioForUpdate()+"_"+pd.getOldNomeForUpdate();
				}
				else if(pd.getOldNomeForUpdate()!=null){
					id = pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario()+"_"+pd.getOldNomeForUpdate();
				}
				else if(pd.getOldTipoSoggettoProprietarioForUpdate()==null || pd.getOldNomeSoggettoProprietarioForUpdate()==null){
					throw new DriverConfigurazioneException("Oggetto in modifica non correttamente valorizzato");
				}
				else{
					id = pd.getOldTipoSoggettoProprietarioForUpdate()+"/"+pd.getOldNomeSoggettoProprietarioForUpdate()+"_"+pd.getNome();
				}
				if(this.prefix){
					return "[PortaDelegata] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof PortaApplicativa){
				PortaApplicativa pa = (PortaApplicativa) o;
				if(pa.getOldTipoSoggettoProprietarioForUpdate()==null && pa.getOldNomeSoggettoProprietarioForUpdate()==null && pa.getOldNomeForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = null;
				if(pa.getOldTipoSoggettoProprietarioForUpdate()!=null && pa.getOldNomeSoggettoProprietarioForUpdate()!=null && pa.getOldNomeForUpdate()!=null){
					id = pa.getOldTipoSoggettoProprietarioForUpdate()+"/"+pa.getOldNomeSoggettoProprietarioForUpdate()+"_"+pa.getOldNomeForUpdate();
				}
				else if(pa.getOldNomeForUpdate()!=null){
					id = pa.getTipoSoggettoProprietario()+"/"+pa.getNomeSoggettoProprietario()+"_"+pa.getOldNomeForUpdate();
				}
				else if(pa.getOldTipoSoggettoProprietarioForUpdate()==null || pa.getOldNomeSoggettoProprietarioForUpdate()==null){
					throw new DriverConfigurazioneException("Oggetto in modifica non correttamente valorizzato");
				}
				else{
					id = pa.getOldTipoSoggettoProprietarioForUpdate()+"/"+pa.getOldNomeSoggettoProprietarioForUpdate()+"_"+pa.getNome();
				}
				if(this.prefix){
					return "[PortaApplicativa] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof RoutingTable){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof GestioneErrore){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof Configurazione){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof AccessoRegistro){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof AccessoRegistroRegistro){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof AccessoConfigurazione){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof AccessoDatiAutorizzazione){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof SystemProperties){
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			
			// Users
			else if(o instanceof User){
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			
			
			// Auditing
			else if(o instanceof org.openspcoop2.web.lib.audit.dao.Configurazione){
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			else if(o instanceof Filtro){
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			// Auditing: eliminazione record salvato
			else if(o instanceof org.openspcoop2.web.lib.audit.dao.Operation){
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			
			// Monitoraggio Applicativo
			else if(o instanceof FilterSearch){
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			
			// IExtendedBean
			else if(o instanceof IExtendedBean){
				IExtendedBean w = (IExtendedBean) o;
				if(w.getOldHumanId()==null){
					return null; // non lancio un errore
				}
				if(this.prefix){
					return "[ExtendedBean-"+w.getClass().getSimpleName()+"] "+ w.getOldHumanId();
				}else{
					return w.getOldHumanId();
				}
			}
			
						
		}catch(Exception e){
			throw new IOException("Trasformazione non riuscita: "+e.getMessage(),e);
		}
		
		throw new IOException("Tipo di Oggetto non gestito ["+o.getClass().getName()+"]");
		
	}

	
	/**
	 * Ritorna gli oggetti gestiti
	 * 
	 * @return oggetti gestiti
	 * @throws DriverException
	 */
	@Override
	public String[] getManagedObjects(boolean simpleName) throws IOException{
		Vector<String> oggetti = new Vector<String>();
		
		
		if(simpleName){
			
			// ControlStation
			oggetti.add(PortaDominio.class.getSimpleName()); // Al posto di PdDControlStation
			oggetti.add(Ruolo.class.getSimpleName());
			oggetti.add(PoliticheSicurezza.class.getSimpleName());
			oggetti.add(Soggetto.class.getSimpleName()); // Al posto di SoggettoControlStation
			
			// RegistroServizi
			oggetti.add(org.openspcoop2.core.registry.Soggetto.class.getSimpleName());
			oggetti.add(AccordoCooperazione.class.getSimpleName());
			oggetti.add(AccordoServizioParteComune.class.getSimpleName());
			oggetti.add(PortType.class.getSimpleName());
			oggetti.add(Documento.class.getSimpleName());
			oggetti.add(AccordoServizioParteSpecifica.class.getSimpleName());
						
			// Configurazione
			oggetti.add(org.openspcoop2.core.config.Soggetto.class.getSimpleName());
			oggetti.add(ServizioApplicativo.class.getSimpleName());
			oggetti.add(PortaDelegata.class.getSimpleName());
			oggetti.add(PortaApplicativa.class.getSimpleName());
			oggetti.add(RoutingTable.class.getSimpleName());
			oggetti.add(GestioneErrore.class.getSimpleName());
			oggetti.add("ConfigurazionePdD");
			oggetti.add(AccessoRegistro.class.getSimpleName());
			// non serve come simple name: oggetti.add(AccessoRegistroRegistro.class.getName());
			oggetti.add(AccessoConfigurazione.class.getSimpleName());
			oggetti.add(AccessoDatiAutorizzazione.class.getSimpleName());
			oggetti.add(SystemProperties.class.getSimpleName());
			
			// Username
			oggetti.add(User.class.getSimpleName());
			
			// Auditing
			oggetti.add("ConfigurazioneAuditing");
			oggetti.add("FiltroAuditing");
			oggetti.add("OperazioneRegistrataTramiteAuditing");
			
			// Monitoraggio Applicativo
			oggetti.add("EliminazioneMessaggiTramiteMonitoraggio");
			
			// IExtendedBean
			oggetti.add("ExtendedBean");
			
		}
		else{
		
			// ControlStation
			oggetti.add(PdDControlStation.class.getName());
			oggetti.add(Ruolo.class.getName());
			oggetti.add(PoliticheSicurezza.class.getName());
			oggetti.add(SoggettoCtrlStat.class.getName());
			
			// RegistroServizi
			oggetti.add(org.openspcoop2.core.registry.Soggetto.class.getName());
			oggetti.add(AccordoCooperazione.class.getName());
			oggetti.add(AccordoServizioParteComune.class.getName());
			oggetti.add(PortType.class.getName());
			oggetti.add(Documento.class.getName());
			oggetti.add(AccordoServizioParteSpecifica.class.getName());
			oggetti.add(Soggetto.class.getName());
			
			// Configurazione
			oggetti.add(org.openspcoop2.core.config.Soggetto.class.getName());
			oggetti.add(ServizioApplicativo.class.getName());
			oggetti.add(PortaDelegata.class.getName());
			oggetti.add(PortaApplicativa.class.getName());
			oggetti.add(RoutingTable.class.getName());
			oggetti.add(GestioneErrore.class.getName());
			oggetti.add(Configurazione.class.getName());
			oggetti.add(AccessoRegistro.class.getName());
			oggetti.add(AccessoRegistroRegistro.class.getName());
			oggetti.add(AccessoConfigurazione.class.getName());
			oggetti.add(AccessoDatiAutorizzazione.class.getName());
			oggetti.add(SystemProperties.class.getName());
			
			// Username
			oggetti.add(User.class.getName());
			
			// Auditing
			oggetti.add(org.openspcoop2.web.lib.audit.dao.Configurazione.class.getName());
			oggetti.add(Filtro.class.getName());
			oggetti.add(Operation.class.getName());
			
			// Monitoraggio Applicativo
			oggetti.add(FilterSearch.class.getName());
			
			// IExtendedBean
			oggetti.add(IExtendedBean.class.getName());
		}
		
		String[]tmp = new String[1];
		return oggetti.toArray(tmp);
	}
	
	/**
	 * Ritorna un nome descrittivo dell'oggetto.
	 * 
	 * @param o
	 * @return nome descrittivo dell'oggetto.
	 * @throws DriverException
	 */
	@Override
	public String getSimpleName(Object o) throws IOException{
		
		
		if(o instanceof PdDControlStation){
			return PortaDominio.class.getSimpleName();
		}
		else if(o instanceof SoggettoCtrlStat){
			return Soggetto.class.getSimpleName();
		}
		else if(o instanceof Configurazione){
			return "ConfigurazionePdD";
		}
		else if(o instanceof AccessoRegistroRegistro){
			return AccessoRegistro.class.getSimpleName();
		}
		else if(o instanceof org.openspcoop2.web.lib.audit.dao.Configurazione){
			return "ConfigurazioneAuditing";
		}
		else if(o instanceof Filtro){
			return "FiltroAuditing";
		}
		else if(o instanceof Operation){
			return "OperazioneRegistrataTramiteAuditing";
		}
		else if(o instanceof FilterSearch){
			return "EliminazioneMessaggiTramiteMonitoraggio";
		}
		else if(o instanceof IExtendedBean){
			return "ExtendedBean-"+o.getClass().getSimpleName();
		}
		else{
			return o.getClass().getSimpleName();
		}
		
	}
}
