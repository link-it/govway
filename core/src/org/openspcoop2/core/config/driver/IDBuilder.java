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


package org.openspcoop2.core.config.driver;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.utils.serialization.IOException;





/**
 * Classe utilizzata per generare gli identificatori degli oggetti presenti nella configurazione della PdD.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDBuilder implements org.openspcoop2.utils.serialization.IDBuilder {

	protected boolean prefix = false;
	
	public IDBuilder(boolean insertClassNamePrefix){
		this.prefix = insertClassNamePrefix; 
	}
	public IDBuilder(){
		this.prefix = false;
	}
	
	public static IDBuilder getIDBuilder(){
		return new IDBuilder();
	}
	
	@Override
	public String toID(Object o) throws IOException {
		
		if(o==null)
			throw new IOException("Oggetto is null");
		
		try{
		
			if(o instanceof Soggetto){
				Soggetto s = (Soggetto) o;
				String id = s.getTipo()+"/"+s.getNome();
				if(this.prefix){
					return "[Soggetto] "+ id;
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
						
		}catch(Exception e){
			throw new IOException("Trasformazione non riuscita: "+e.getMessage(),e);
		}
		
		throw new IOException("Tipo di Oggetto non gestito ["+o.getClass().getName()+"]");
	}

	@Override
	public String toID(Object o, String field) throws IOException {
		return this.toID(o) + "." + field;
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
		
			if(o instanceof Soggetto){
				Soggetto s = (Soggetto) o;
				if(s.getOldIDSoggettoForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = s.getOldIDSoggettoForUpdate().getTipo()+"/"+s.getOldIDSoggettoForUpdate().getNome();
				if(this.prefix){
					return "[Soggetto] "+ id;
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
		List<String> oggetti = new ArrayList<String>();
		
		if(simpleName){
			oggetti.add(Soggetto.class.getSimpleName());
			oggetti.add(ServizioApplicativo.class.getSimpleName());
			oggetti.add(PortaDelegata.class.getSimpleName());
			oggetti.add(PortaApplicativa.class.getSimpleName());
			oggetti.add(RoutingTable.class.getSimpleName());
			oggetti.add(GestioneErrore.class.getSimpleName());
			oggetti.add(Configurazione.class.getSimpleName());
			oggetti.add(AccessoRegistro.class.getSimpleName());
			// non serve come simple name: oggetti.add(AccessoRegistroRegistro.class.getSimpleName());
			oggetti.add(AccessoConfigurazione.class.getSimpleName());
			oggetti.add(AccessoDatiAutenticazione.class.getSimpleName());
			oggetti.add(AccessoDatiAutorizzazione.class.getSimpleName());
			oggetti.add(SystemProperties.class.getSimpleName());
			oggetti.add(RegistroPlugin.class.getSimpleName());
			oggetti.add(RegistroPluginArchivio.class.getSimpleName());
		}
		else{
			oggetti.add(Soggetto.class.getName());
			oggetti.add(ServizioApplicativo.class.getName());
			oggetti.add(PortaDelegata.class.getName());
			oggetti.add(PortaApplicativa.class.getName());
			oggetti.add(RoutingTable.class.getName());
			oggetti.add(GestioneErrore.class.getName());
			oggetti.add(Configurazione.class.getName());
			oggetti.add(AccessoRegistro.class.getName());
			oggetti.add(AccessoRegistroRegistro.class.getName());
			oggetti.add(AccessoConfigurazione.class.getName());
			oggetti.add(AccessoDatiAutenticazione.class.getName());
			oggetti.add(AccessoDatiAutorizzazione.class.getName());
			oggetti.add(SystemProperties.class.getName());
			oggetti.add(RegistroPlugin.class.getName());
			oggetti.add(RegistroPluginArchivio.class.getName());			
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
		
		if(o instanceof AccessoRegistroRegistro){
			return AccessoRegistro.class.getSimpleName();
		}
		else{
			return o.getClass().getSimpleName();
		}
		
	}

}
