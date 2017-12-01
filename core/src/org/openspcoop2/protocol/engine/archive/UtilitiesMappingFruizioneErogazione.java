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

package org.openspcoop2.protocol.engine.archive;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBMappingUtils;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.slf4j.Logger;


/**
 *  UtilitiesMappingFruizionePD
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtilitiesMappingFruizioneErogazione  {

	private DriverConfigurazioneDB driverConfigurazione;
	private DriverRegistroServiziDB driverRegistroServizi;
	private Logger log;
	
	public UtilitiesMappingFruizioneErogazione(DriverConfigurazioneDB driverConfigurazione,
			DriverRegistroServiziDB driverRegistroServizi,
			Logger log){
		this.driverConfigurazione = driverConfigurazione;
		this.driverRegistroServizi = driverRegistroServizi;
		this.log = log;
	}
	
	public void initMappingFruizione() throws ProtocolException {
		this._initMapping(false);
	}
	public void initMappingErogazione() throws ProtocolException {
		this._initMapping(true);
	}
	private void _initMapping(boolean erogazione) throws ProtocolException {
		
		try {

			List<String> tipiPdd = new ArrayList<String>();
			tipiPdd.add(PddTipologia.OPERATIVO.toString());
			tipiPdd.add(PddTipologia.NONOPERATIVO.toString());
			
			for (String tipoPdd : tipiPdd) {
				
				this.log.debug("Pdd (tipo:"+tipoPdd+") ricerca in corso...");
				FiltroRicerca filtroPdd = new FiltroRicerca();
				filtroPdd.setTipo(tipoPdd);
				List<String> listPdD = null;
				try{
					listPdD = this.driverRegistroServizi.getAllIdPorteDominio(filtroPdd);
				}catch(DriverRegistroServiziNotFound notFound){}
				if(listPdD==null){
					listPdD = new ArrayList<>();
				}
				this.log.debug("Pdd (tipo:"+tipoPdd+") trovate: "+listPdD.size());
				
				if(listPdD.size()>0){
					
					for (String nomePdd : listPdD) {
						
						this.log.debug("Soggetti (pdd:"+nomePdd+") ricerca in corso...");
						FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
						filtroSoggetti.setNomePdd(nomePdd);
						List<IDSoggetto> listSoggetti = null;
						try{
							listSoggetti = this.driverRegistroServizi.getAllIdSoggetti(filtroSoggetti);
						}catch(DriverRegistroServiziNotFound notFound){}
						if(listSoggetti==null){
							listSoggetti = new ArrayList<>();
						}
						this.log.debug("Soggetti (pdd:"+nomePdd+") trovati: "+listSoggetti.size());
						
						if(listSoggetti.size()>0){
						
							for (IDSoggetto idSoggetto : listSoggetti) {
								
								if(erogazione){
								
									// erogazione
									
									this.log.debug("Servizi (soggetto:"+idSoggetto+") ricerca in corso...");
									FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
									filtroServizi.setTipoSoggettoErogatore(idSoggetto.getTipo());
									filtroServizi.setNomeSoggettoErogatore(idSoggetto.getNome());
									List<IDServizio> listServizi = null;
									try{
										listServizi = this.driverRegistroServizi.getAllIdServizi(filtroServizi);
									}catch(DriverRegistroServiziNotFound notFound){}
									if(listServizi==null){
										listServizi = new ArrayList<>();
									}
									this.log.debug("Servizi (soggetto:"+idSoggetto+") trovati: "+listServizi.size());
									
									if(listServizi.size()>0){
										
										for (IDServizio idServizio : listServizi) {
												
											Connection con = null;
											try{
												con = this.driverRegistroServizi.getConnection("UtilitiesMappingFruizioneErogazione.existsIDPortaApplicativaAssociata");
												if(DBMappingUtils.existsIDPortaApplicativaAssociata(idServizio, con, this.driverRegistroServizi.getTipoDB())){
													this.log.debug("PortaApplicativa (soggetto:"+idSoggetto+" servizio:"+
															idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+") mapping già esistente");
													continue;
												}
											}finally{
												try{
													this.driverRegistroServizi.releaseConnection(con);
												}catch(Throwable t){}
											}
											
											this.log.debug("PorteApplicative (soggetto:"+idSoggetto+" servizio:"+
													idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+") ricerca in corso...");
											FiltroRicercaPorteApplicative filtroPA = new FiltroRicercaPorteApplicative();
											filtroPA.setTipoServizio(idServizio.getTipo());
											filtroPA.setNomeServizio(idServizio.getNome());
											filtroPA.setTipoSoggetto(idSoggetto.getTipo());
											filtroPA.setNomeSoggetto(idSoggetto.getNome());
											List<IDPortaApplicativa> listPA = null;
											try{
												listPA = this.driverConfigurazione.getAllIdPorteApplicative(filtroPA);
											}catch(DriverConfigurazioneNotFound notFound){}
											if(listPA==null){
												listPA = new ArrayList<>();
											}
											this.log.debug("PorteApplicative (soggetto:"+idSoggetto+" servizio:"+
													idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+") trovate: "+listPA.size());
											
											String nomeMapping = Costanti.MAPPING_EROGAZIONE_PA_NOME_DEFAULT;
											boolean isDefault = true;
											if(listPA.size()==1){
												IDPortaApplicativa idPA = listPA.get(0);
												this.log.debug("Creazione Mapping Erogazione soggetto:"+idSoggetto+" servizio:"+
													idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+" con pa:"+idPA.getNome()+" in corso...");
												con = null;
												try{
													con = this.driverRegistroServizi.getConnection("UtilitiesMappingFruizioneErogazione.createMappingErogazione");
													
													DBMappingUtils.createMappingErogazione(nomeMapping, isDefault, idServizio, idPA, con, this.driverRegistroServizi.getTipoDB());
												}finally{
													try{
														this.driverRegistroServizi.releaseConnection(con);
													}catch(Throwable t){}
												}
												this.log.debug("Creazione Mapping Erogazione soggetto:"+idSoggetto+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+" con pa:"+idPA.getNome()+" effettuata con successo");
											}
											else if(listPA.size()>1){
												// cerco se esiste una PA che possiede una azione '*'
												this.log.debug("PorteApplicative (soggetto:"+idSoggetto+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+") search PA con azione * ...");
												IDPortaApplicativa idPA = null;
												for (IDPortaApplicativa idPACheck : listPA) {
													PortaApplicativa pa = this.driverConfigurazione.getPortaApplicativa(idPACheck);
													if(pa.getAzione()==null || pa.getAzione().getNome()==null || "".equals(pa.getAzione().getNome())){
														idPA = idPACheck;
														break;
													}
												}
												this.log.debug("PorteApplicative (soggetto:"+idSoggetto+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+") search PA con azione search completato (trovato:"+(idPA!=null)+")");
												if(idPA!=null){
													this.log.debug("Creazione Mapping Erogazione soggetto:"+idSoggetto+" servizio:"+
															idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+" con pa:"+idPA.getNome()+" in corso...");
													con = null;
													try{
														con = this.driverRegistroServizi.getConnection("UtilitiesMappingFruizioneErogazione.createMappingErogazione.sizeMoreThenOne");
														DBMappingUtils.createMappingErogazione(nomeMapping, isDefault, idServizio, idPA, con, this.driverRegistroServizi.getTipoDB());
													}finally{
														try{
															this.driverRegistroServizi.releaseConnection(con);
														}catch(Throwable t){}
													}
													this.log.debug("Creazione Mapping Erogazione soggetto:"+idSoggetto+" servizio:"+
															idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+" con pa:"+idPA.getNome()+" effettuata con successo");
												}
											}
											
										}
										
									}
								}
								else{
									
									// fruizione
									
									this.log.debug("Servizi (fruitore:"+idSoggetto+") ricerca in corso...");
									FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
									filtroServizi.setTipoSoggettoFruitore(idSoggetto.getTipo());
									filtroServizi.setNomeSoggettoFruitore(idSoggetto.getNome());
									List<IDServizio> listServizi = null;
									try{
										listServizi = this.driverRegistroServizi.getAllIdServizi(filtroServizi);
									}catch(DriverRegistroServiziNotFound notFound){}
									if(listServizi==null){
										listServizi = new ArrayList<>();
									}
									this.log.debug("Servizi (fruitore:"+idSoggetto+") trovati: "+listServizi.size());
									
									if(listServizi.size()>0){
										
										for (IDServizio idServizio : listServizi) {
										
											Connection con = null;
											try{
												con = this.driverRegistroServizi.getConnection("UtilitiesMappingFruizioneErogazione.existsIDPortaDelegataAssociata");		
												if(DBMappingUtils.existsIDPortaDelegataAssociata(idServizio, idSoggetto, con, this.driverRegistroServizi.getTipoDB())){
													this.log.debug("PortaDelegata (soggetto-fruitore:"+idSoggetto+
															" soggetto-erogatore:"+idServizio.getSoggettoErogatore()+" servizio:"+
															idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+") mapping già esistente");
													continue;
												}
											}finally{
												try{
													this.driverRegistroServizi.releaseConnection(con);
												}catch(Throwable t){}
											}
											
											this.log.debug("PorteDelegate (soggetto-fruitore:"+idSoggetto+
														" soggetto-erogatore:"+idServizio.getSoggettoErogatore()+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+") ricerca in corso...");
											FiltroRicercaPorteDelegate filtroPD = new FiltroRicercaPorteDelegate();
											filtroPD.setTipoSoggetto(idSoggetto.getTipo());
											filtroPD.setNomeSoggetto(idSoggetto.getNome());
											filtroPD.setTipoSoggettoErogatore(idServizio.getSoggettoErogatore().getTipo());
											filtroPD.setNomeSoggettoErogatore(idServizio.getSoggettoErogatore().getNome());
											filtroPD.setTipoServizio(idServizio.getTipo());
											filtroPD.setNomeServizio(idServizio.getNome());
											List<IDPortaDelegata> listPD = null;
											try{
												listPD = this.driverConfigurazione.getAllIdPorteDelegate(filtroPD);
											}catch(DriverConfigurazioneNotFound notFound){}
											if(listPD==null){
												listPD = new ArrayList<>();
											}
											this.log.debug("PorteDelegate (soggetto-fruitore:"+idSoggetto+
														" soggetto-erogatore:"+idServizio.getSoggettoErogatore()+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+") trovate: "+listPD.size());
											
											String nomeMapping = Costanti.MAPPING_FRUIZIONE_PD_NOME_DEFAULT;
											boolean isDefault = true;
											if(listPD.size()==1){
												IDPortaDelegata idPD = listPD.get(0);
												this.log.debug("Creazione Mapping Fruizione soggetto-fruitore:"+idSoggetto+
														" soggetto-erogatore:"+idServizio.getSoggettoErogatore()+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+" con pd:"+idPD.getNome()+" in corso...");
												con = null;
												try{
													con = this.driverRegistroServizi.getConnection("UtilitiesMappingFruizioneErogazione.createMappingFruizione");		
													DBMappingUtils.createMappingFruizione(nomeMapping, isDefault, idServizio, idSoggetto, idPD, con, this.driverRegistroServizi.getTipoDB());
												}finally{
													try{
														this.driverRegistroServizi.releaseConnection(con);
													}catch(Throwable t){}
												}
												this.log.debug("Creazione Mapping Fruizione soggetto-fruitore:"+idSoggetto+
														" soggetto-erogatore:"+idServizio.getSoggettoErogatore()+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+" con pd:"+idPD.getNome()+" effettuata con successo");
											}
											else if(listPD.size()>1){
												// cerco se esiste una PD che possiede una azione '*'
												this.log.debug("PorteDelegate (soggetto-fruitore:"+idSoggetto+
														" soggetto-erogatore:"+idServizio.getSoggettoErogatore()+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+") search PD con azione * ...");
												IDPortaDelegata idPD = null;
												for (IDPortaDelegata idPDCheck : listPD) {
													PortaDelegata pd = this.driverConfigurazione.getPortaDelegata(idPDCheck);
													if(pd.getAzione()==null || pd.getAzione().getNome()==null || "".equals(pd.getAzione().getNome())){
														idPD = idPDCheck;
														break;
													}
												}
												this.log.debug("PorteDelegate (soggetto-fruitore:"+idSoggetto+
														" soggetto-erogatore:"+idServizio.getSoggettoErogatore()+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+") search PD con azione search completato (trovato:"+(idPD!=null)+")");
												if(idPD!=null){
													this.log.debug("Creazione Mapping Fruizione soggetto-fruitore:"+idSoggetto+
														" soggetto-erogatore:"+idServizio.getSoggettoErogatore()+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+" con pd:"+idPD.getNome()+" in corso...");
													con = null;
													try{
														con = this.driverRegistroServizi.getConnection("UtilitiesMappingFruizioneErogazione.createMappingFruizione.sizeMoreThenOne");		
														DBMappingUtils.createMappingFruizione(nomeMapping, isDefault, idServizio, idSoggetto, idPD, con, this.driverRegistroServizi.getTipoDB());
													}finally{
														try{
															this.driverRegistroServizi.releaseConnection(con);
														}catch(Throwable t){}
													}
													this.log.debug("Creazione Mapping Fruizione soggetto-fruitore:"+idSoggetto+
														" soggetto-erogatore:"+idServizio.getSoggettoErogatore()+" servizio:"+
														idServizio.getTipo()+"/"+idServizio.getNome()+" v"+idServizio.getVersione()+" con pd:"+idPD.getNome()+" effettuata con successo");
												}
											}
											
										}
									}
									
								}
							}
							
						}
					}
					
				}
	
			}			
			

		} catch (Throwable se) {
			throw new ProtocolException(se.getMessage(),se);
		} 
		
	}
	
}
