/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.utils.serialization.IOException;



/**
 * Classe utilizzata per generare gli identificatori degli oggetti presenti nel registro dei Servizi.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDBuilder implements org.openspcoop2.utils.serialization.IDBuilder {

	protected boolean prefix = false;

	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
	private IDServizioFactory idServizioFactory = IDServizioFactory.getInstance();
	
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
		
			if(o instanceof AccordoCooperazione){
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
					return "[AS] "+ id;
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
			}
			else if(o instanceof PortaDominio){
				PortaDominio p = (PortaDominio) o;
				String id = p.getNome();
				if(this.prefix){
					return "[PdD] "+ id;
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
			else if(o instanceof Ruolo){
				Ruolo r = (Ruolo) o;
				String id = r.getNome();
				if(this.prefix){
					return "[Ruolo] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof Scope){
				Scope s = (Scope) o;
				String id = s.getNome();
				if(this.prefix){
					return "[Scope] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof AccordoServizioParteSpecifica){
				AccordoServizioParteSpecifica s = (AccordoServizioParteSpecifica) o;
				String id = this.idServizioFactory.getUriFromAccordo(s);
				if(this.prefix){
					return "[Servizio] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof Fruitore){
				Fruitore fr = (Fruitore) o;
				String id = fr.getTipo()+"/"+fr.getNome();
				if(fr.getIdServizio()!=null && fr.getIdServizio()>0)
					id=id+"#idServizio:"+fr.getIdServizio();
				if(this.prefix){
					return "[FruitoreServizio] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof Soggetto){
				Soggetto s = (Soggetto) o;
				String id = s.getTipo()+"/"+s.getNome();
				if(this.prefix){
					return "[Soggetto] "+ id;
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
		
			if(o instanceof AccordoCooperazione){
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
					return "[AS] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof PortType){
				return null; // oggetto non modificabile nei dati identificativi
			}
			else if(o instanceof Documento){
				return null; // oggetto non modificabile nei dati identificativi
			}
			else if(o instanceof PortaDominio){
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
			else if(o instanceof Ruolo){
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
			else if(o instanceof Scope){
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
			else if(o instanceof AccordoServizioParteSpecifica){
				AccordoServizioParteSpecifica as = (AccordoServizioParteSpecifica) o;
				if(as.getOldIDServizioForUpdate()==null){
					return null; // non lancio un errore
				}
				
				String id = this.idServizioFactory.getUriFromIDServizio(as.getOldIDServizioForUpdate());
				if(this.prefix){
					return "[Servizio] "+ id;
				}else{
					return id;
				}
			}
			else if(o instanceof Soggetto){
				Soggetto s = (Soggetto) o;
				if(s.getOldIDSoggettoForUpdate()==null){
					return null; // non lancio un errore
				}
				String id = s.getOldIDSoggettoForUpdate().toString();
				if(this.prefix){
					return "[Soggetto] "+ id;
				}else{
					return id;
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
		List<String> oggetti = new ArrayList<String>();
		
		if(simpleName){
			oggetti.add(AccordoCooperazione.class.getSimpleName());
			oggetti.add(AccordoServizioParteComune.class.getSimpleName());
			oggetti.add(PortType.class.getSimpleName());
			oggetti.add(Documento.class.getSimpleName());
			oggetti.add(PortaDominio.class.getSimpleName());
			oggetti.add(Gruppo.class.getSimpleName());
			oggetti.add(Ruolo.class.getSimpleName());
			oggetti.add(Scope.class.getSimpleName());
			oggetti.add(AccordoServizioParteSpecifica.class.getSimpleName());
			oggetti.add(Fruitore.class.getSimpleName());
			oggetti.add(Soggetto.class.getSimpleName());
		}
		else{
			oggetti.add(AccordoCooperazione.class.getName());
			oggetti.add(AccordoServizioParteComune.class.getName());
			oggetti.add(PortType.class.getName());
			oggetti.add(Documento.class.getName());
			oggetti.add(PortaDominio.class.getName());
			oggetti.add(Gruppo.class.getName());
			oggetti.add(Ruolo.class.getName());
			oggetti.add(Scope.class.getName());
			oggetti.add(AccordoServizioParteSpecifica.class.getName());
			oggetti.add(Fruitore.class.getName());
			oggetti.add(Soggetto.class.getName());
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
		return o.getClass().getSimpleName();
	}
}
