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


package org.openspcoop2.web.ctrlstat.driver;

import java.util.Vector;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.pdd.monitor.driver.FilterSearch;
import org.openspcoop2.utils.serialization.IOException;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.lib.audit.dao.Filtro;
import org.openspcoop2.web.lib.audit.log.Operation;
import org.openspcoop2.web.lib.users.dao.User;



/**
 * Classe utilizzata per generare gli identificatori degli oggetti presenti nella govwayConsole.
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
	private IDServizioFactory idServizioFactory = null;
	
	public IDBuilder(boolean insertClassNamePrefix){
		this.prefix = insertClassNamePrefix; 
		this.idAccordoFactory = IDAccordoFactory.getInstance();
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idServizioFactory = IDServizioFactory.getInstance();
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
			else if(o instanceof MappingFruizionePortaDelegata){
				MappingFruizionePortaDelegata mapping = (MappingFruizionePortaDelegata) o;
				StringBuilder bf = new StringBuilder();
				bf.append("FR[");
				bf.append(mapping.getIdFruitore().getTipo()+"/"+mapping.getIdFruitore().getNome());
				bf.append("] SERV[");
				bf.append(mapping.getIdServizio().toString());
				bf.append("] PD["+mapping.getIdPortaDelegata().getNome());
				bf.append("]");
				if(this.prefix){
					return "[MappingFruizionePortaDelegata] "+ bf.toString();
				}else{
					return bf.toString();
				}
			}
			else if(o instanceof MappingErogazionePortaApplicativa){
				MappingErogazionePortaApplicativa mapping = (MappingErogazionePortaApplicativa) o;
				StringBuilder bf = new StringBuilder();
				bf.append("SERV[");
				bf.append(mapping.getIdServizio().toString());
				bf.append("] PA["+mapping.getIdPortaApplicativa().getNome());
				bf.append("]");
				if(this.prefix){
					return "[MappingErogazionePortaApplicativa] "+ bf.toString();
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
			}
			else if (o instanceof Gruppo) {
				Gruppo g = (Gruppo) o;
				String id = g.getNome();
				if(this.prefix){
					return "[Gruppo] "+ id;
				}else{
					return id;
				}
			}
			else if (o instanceof Ruolo) {
				Ruolo r = (Ruolo) o;
				String id = r.getNome();
				if(this.prefix){
					return "[Ruolo] "+ id;
				}else{
					return id;
				}
			}
			else if (o instanceof Scope) {
				Scope s = (Scope) o;
				String id = s.getNome();
				if(this.prefix){
					return "[Scope] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof AccordoCooperazione){
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
				String id = this.idServizioFactory.getUriFromAccordo(asps);
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
			}else if(o instanceof AccessoDatiAutenticazione){
				return "ConfigurazioneAccessoDatiAutenticazione";
			}else if(o instanceof AccessoDatiAutorizzazione){
				return "ConfigurazioneAccessoDatiAutorizzazione";
			}else if(o instanceof SystemProperties){
				return "ProprietàDiSistema";
			}else if(o instanceof RegistroPlugin){
				RegistroPlugin plugin = (RegistroPlugin) o;
				String id = plugin.getNome();
				if(this.prefix){
					return "[RegistroPlugin] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof RegistroPluginArchivio){
				RegistroPluginArchivio plugin = (RegistroPluginArchivio) o;
				String id = plugin.getNome();
				if(this.prefix){
					return "[RegistroPluginArchivio] "+ plugin.getNomePlugin()+"-"+id;
				}else{
					return id;
				}
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
			else if(o instanceof org.openspcoop2.web.lib.audit.log.Operation){
				org.openspcoop2.web.lib.audit.log.Operation op = (org.openspcoop2.web.lib.audit.log.Operation) o;
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
			
			// Configurazione controllo del traffico
			else if(o instanceof ConfigurazioneGenerale) {
				return "ConfigurazioneControlloTraffico";
			}

			// Configurazione Policy
			else if(o instanceof ConfigurazionePolicy) {
				ConfigurazionePolicy policy = (ConfigurazionePolicy) o;
				String id = policy.getIdPolicy();
				if(this.prefix){
					return "[ConfigurazionePolicy] "+ id;
				}else{
					return id;
				}
			}
			
			// Attivazione Policy
			else if(o instanceof AttivazionePolicy) {
				AttivazionePolicy policy = (AttivazionePolicy) o;
				String id = policy.getIdActivePolicy();
				if(this.prefix){
					return "[AttivazionePolicy] "+ id;
				}else{
					return id;
				}
			}
			
			// Generic Properties
			else if(o instanceof GenericProperties) {
				GenericProperties genericProperties = (GenericProperties) o;
				String id = genericProperties.getNome();
				if(this.prefix){
					return "[GenericProperties] "+ id;
				}else{
					return id;
				}
			}
			
			// Plugins
			else if(o instanceof Plugin) {
				Plugin plugin = (Plugin) o;
				String id = plugin.getTipoPlugin()+"-"+plugin.getTipo();
				if(this.prefix){
					return "[Plugin] "+ id;
				}else{
					return id;
				}
			}
			
			// Allarme
			else if(o instanceof Allarme) {
				Allarme allarme = (Allarme) o;
				String id = allarme.getNome()+"-"+allarme.getTipo();
				if(this.prefix){
					return "[Allarme] "+ id;
				}else{
					return id;
				}
			}
			
			// Allarme History
			else if(o instanceof AllarmeHistory) {
				AllarmeHistory history = (AllarmeHistory) o;
				String id = history.getIdAllarme().getNome();
				if(this.prefix){
					return "[AllarmeHistory] "+ id;
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
			else if(o instanceof MappingFruizionePortaDelegata){
				return null; // oggetto non modificabile nei dati identificativi
			}
			else if(o instanceof MappingErogazionePortaApplicativa){
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
				if(s.getOldIDSoggettoForUpdate()==null || s.getOldIDSoggettoForUpdate().getTipo()==null || s.getOldIDSoggettoForUpdate().getNome()==null){
					return null; // non lancio un errore
				}
				String id = s.getOldIDSoggettoForUpdate().getTipo()+"/"+s.getOldIDSoggettoForUpdate().getNome();
				if(this.prefix){
					return "[SoggettoRegistro] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof Gruppo){
				Gruppo g = (Gruppo) o;
				if(g.getOldIDGruppoForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = g.getOldIDGruppoForUpdate().getNome();
				if(this.prefix){
					return "[Gruppo] "+ id;
				}else{
					return id;
				}
			}
			else if (o instanceof Ruolo) {
				Ruolo r = (Ruolo) o;
				if(r.getOldIDRuoloForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = r.getOldIDRuoloForUpdate().getNome();
				if(this.prefix){
					return "[Ruolo] "+ id;
				}else{
					return id;
				}
			}
			else if (o instanceof Scope) {
				Scope s = (Scope) o;
				if(s.getOldIDScopeForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = s.getOldIDScopeForUpdate().getNome();
				if(this.prefix){
					return "[Scope] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof AccordoCooperazione){
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
				if(asps.getOldIDServizioForUpdate()==null){
					return null; // non lancio un errore
				}	
				String id = this.idServizioFactory.getUriFromIDServizio(asps.getOldIDServizioForUpdate());
				if(this.prefix){
					return "[AccordoServizioParteSpecifica] "+ id;
				}else{
					return id;
				}
			}
			
			// Configurazione
			else if(o instanceof org.openspcoop2.core.config.Soggetto){
				org.openspcoop2.core.config.Soggetto s = (org.openspcoop2.core.config.Soggetto) o;
				if(s.getOldIDSoggettoForUpdate()==null || s.getOldIDSoggettoForUpdate().getTipo()==null || s.getOldIDSoggettoForUpdate().getNome()==null){
					return null; // non lancio un errore
				}
				String id = s.getOldIDSoggettoForUpdate().getTipo()+"/"+s.getOldIDSoggettoForUpdate().getNome();
				if(this.prefix){
					return "[SoggettoConfigurazione] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof ServizioApplicativo){
				ServizioApplicativo s = (ServizioApplicativo) o;
				if( (s.getOldIDServizioApplicativoForUpdate()==null)) {
					return null; // non lancio un errore
				}
				if( (s.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario()==null || 
						s.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getTipo()==null ||
						s.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getNome()==null) 
						&& s.getOldIDServizioApplicativoForUpdate().getNome()==null) {
					return null; // non lancio un errore
				}
				String id = null;
				if(s.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario()!=null && s.getOldIDServizioApplicativoForUpdate().getNome()!=null){
					id = s.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getTipo()+"/"+
							s.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getNome()+"_"+
							s.getOldIDServizioApplicativoForUpdate().getNome();
				}
				else if(s.getOldIDServizioApplicativoForUpdate().getNome()!=null){
					id = s.getTipoSoggettoProprietario()+"/"+s.getNomeSoggettoProprietario()+"_"+
							s.getOldIDServizioApplicativoForUpdate().getNome();
				}
				else if(s.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario()==null){
					throw new DriverConfigurazioneException("Oggetto in modifica non correttamente valorizzato");
				}
				else{
					id = s.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getTipo()+"/"+
							s.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getNome()+"_"+
							"_"+s.getNome();
				}				
				if(this.prefix){
					return "[ServizioApplicativo] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof PortaDelegata){
				PortaDelegata pd = (PortaDelegata) o;
				if(pd.getOldIDPortaDelegataForUpdate()==null || pd.getOldIDPortaDelegataForUpdate().getNome()==null){
					return null; // non lancio un errore
				}
				String id = pd.getOldIDPortaDelegataForUpdate().getNome();
				if(this.prefix){
					return "[PortaDelegata] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof PortaApplicativa){
				PortaApplicativa pa = (PortaApplicativa) o;
				if(pa.getOldIDPortaApplicativaForUpdate()==null || pa.getOldIDPortaApplicativaForUpdate().getNome()==null){
					return null; // non lancio un errore
				}
				String id = pa.getOldIDPortaApplicativaForUpdate().getNome();
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
			}else if(o instanceof AccessoDatiAutenticazione){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof AccessoDatiAutorizzazione){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof SystemProperties){
				return null; // oggetto non modificabile nei dati identificativi
			}else if(o instanceof RegistroPlugin){
				RegistroPlugin plugin = (RegistroPlugin) o;
				if(plugin.getOldNome()==null) {
					return null; // non lancio un errore
				}
				String id = plugin.getOldNome();
				if(this.prefix){
					return "[RegistroPlugin] "+ id;
				}else{
					return id;
				}
			}else if(o instanceof RegistroPluginArchivio){
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
			else if(o instanceof org.openspcoop2.web.lib.audit.log.Operation){
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			
			// Monitoraggio Applicativo
			else if(o instanceof FilterSearch){
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			// Configurazione controllo del traffico
			else if(o instanceof ConfigurazioneGenerale) {
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			// Configurazione Policy
			else if(o instanceof ConfigurazionePolicy) {
				ConfigurazionePolicy policy = (ConfigurazionePolicy) o;
				if(policy.getOldIdPolicy()==null || policy.getOldIdPolicy().getNome()==null){
					return null; // non lancio un errore
				}
				String id = policy.getOldIdPolicy().getNome();
				if(this.prefix){
					return "[ConfigurazionePolicy] "+ id;
				}else{
					return id;
				}
			}
			
			// Attivazione Policy
			else if(o instanceof AttivazionePolicy) {
				AttivazionePolicy policy = (AttivazionePolicy) o;
				if(policy.getOldIdActivePolicy()==null || policy.getOldIdActivePolicy().getNome()==null){
					return null; // non lancio un errore
				}
				String id = policy.getOldIdActivePolicy().getNome();
				if(this.prefix){
					return "[ConfigurazionePolicy] "+ id;
				}else{
					return id;
				}
			}
			
			// Generic Properties
			else if(o instanceof GenericProperties) {
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			// Plugins
			else if(o instanceof Plugin) {
				Plugin plugin = (Plugin) o;
				if(plugin.getOldIdPlugin()==null || plugin.getOldIdPlugin().getTipoPlugin()==null || plugin.getOldIdPlugin().getTipo()==null){
					return null; // non lancio un errore
				}
				String id = plugin.getTipoPlugin()+"-"+plugin.getTipo();
				if(this.prefix){
					return "[Plugin] "+ id;
				}else{
					return id;
				}
			}
			
			// Allarme
			else if(o instanceof Allarme) {
				return null; // oggetto non modificabile nei dati identificativi
			}
			
			// Allarme History
			else if(o instanceof AllarmeHistory) {
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
			oggetti.add(MappingFruizionePortaDelegata.class.getSimpleName());
			oggetti.add(MappingErogazionePortaApplicativa.class.getSimpleName());
			oggetti.add(Soggetto.class.getSimpleName()); // Al posto di SoggettoControlStation
			
			// RegistroServizi
			oggetti.add(org.openspcoop2.core.registry.Soggetto.class.getSimpleName());
			oggetti.add(Gruppo.class.getSimpleName());
			oggetti.add(Ruolo.class.getSimpleName());
			oggetti.add(Scope.class.getSimpleName());
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
			oggetti.add(AccessoDatiAutenticazione.class.getSimpleName());
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
			
			// Configurazione Controllo Traffico
			oggetti.add("ConfigurazioneControlloTraffico");
			oggetti.add(ConfigurazionePolicy.class.getSimpleName());
			oggetti.add(AttivazionePolicy.class.getSimpleName());
			
			// Generic Properties
			oggetti.add(GenericProperties.class.getSimpleName());
			
			// RegistroPlugins
			oggetti.add(RegistroPlugin.class.getSimpleName());
			oggetti.add(RegistroPluginArchivio.class.getSimpleName());
			
			// IExtendedBean
			oggetti.add("ExtendedBean");
			
		}
		else{
		
			// ControlStation
			oggetti.add(PdDControlStation.class.getName());
			oggetti.add(MappingFruizionePortaDelegata.class.getName());
			oggetti.add(MappingErogazionePortaApplicativa.class.getName());
			oggetti.add(SoggettoCtrlStat.class.getName());
			
			// RegistroServizi
			oggetti.add(org.openspcoop2.core.registry.Soggetto.class.getName());
			oggetti.add(Gruppo.class.getName());
			oggetti.add(Ruolo.class.getName());
			oggetti.add(Scope.class.getName());
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
			
			// Configurazione Controllo del Traffico
			oggetti.add(ConfigurazioneGenerale.class.getName());
			oggetti.add(ConfigurazionePolicy.class.getName());
			oggetti.add(AttivazionePolicy.class.getName());
			
			// Generic Properties
			oggetti.add(GenericProperties.class.getName());
			
			// RegistroPlugins
			oggetti.add(RegistroPlugin.class.getName());
			oggetti.add(RegistroPluginArchivio.class.getName());
			
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
		else if(o instanceof ConfigurazioneGenerale){
			return "ConfigurazioneControlloTraffico";
		}
		else if(o instanceof IExtendedBean){
			return "ExtendedBean-"+o.getClass().getSimpleName();
		}
		else{
			return o.getClass().getSimpleName();
		}
		
	}
}
