/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.cache;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.core.GestoreRichieste;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazione;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.GestoreLoadBalancerCaching;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.protocol.registry.RegistroServiziReader;

/**
 * GestoreCacheCleaner
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCacheCleaner {
	
	private GestoreCacheCleaner() {}

	private static List<ICacheCleaner> listCacheCleaner = new ArrayList<>();
	private static DriverRegistroServiziDB driverRegistroDB = null;
	private static DriverConfigurazioneDB driverConfigDB = null;
	
	public static void initialize() throws Exception {
		
		if(GestoreRichieste.isCacheAbilitata()) {
			listCacheCleaner.add(new GestoreRichiesteCacheCleaner());
		}
		if(RegistroServiziReader.isCacheAbilitata()) {
			listCacheCleaner.add(new RegistroServiziCacheCleaner());
		}
		if(ConfigurazionePdDReader.isCacheAbilitata()) {
			listCacheCleaner.add(new ConfigurazioneCacheCleaner());
		}
		if(GestoreAutenticazione.isCacheAbilitata()) {
			listCacheCleaner.add(new AutenticazioneCacheCleaner());
		}
		if(GestoreAutorizzazione.isCacheAbilitata()) {
			listCacheCleaner.add(new AutorizzazioneCacheCleaner());
		}
		if(GestoreToken.isGestioneTokenCacheAbilitata()) {
			listCacheCleaner.add(new GestoreTokenCacheCleaner());
		}
		if(GestoreToken.isAttributeAuthorityCacheAbilitata()) {
			listCacheCleaner.add(new AttributeAuthorityCacheCleaner());
		}
		if(GestoreLoadBalancerCaching.isCacheAbilitata()) {
			listCacheCleaner.add(new GestoreLoadBalancerCacheCleaner());
		}
		
		for (IDriverRegistroServiziGet driver : RegistroServiziReader.getDriverRegistroServizi().values()) {
			if(driver instanceof DriverRegistroServiziDB) {
				driverRegistroDB = (DriverRegistroServiziDB) driver;
				break;
			}
		}
		
		IDriverConfigurazioneGet driver = ConfigurazionePdDReader.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			driverConfigDB = (DriverConfigurazioneDB) driver;
		}
		
	}
	
	public static void removeAccordoCooperazione(long id) throws Exception {
		if(!listCacheCleaner.isEmpty() && driverRegistroDB!=null) {
			AccordoCooperazione ac = driverRegistroDB.getAccordoCooperazione(id);
			if(ac!=null) {
				IDAccordoCooperazione idAccordoCooperazione = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromAccordo(ac);
				for (ICacheCleaner iCacheCleaner : listCacheCleaner) {
					iCacheCleaner.removeAccordoCooperazione(idAccordoCooperazione);
				}
			}
		}
	}
	
	public static void removeAccordoServizioParteComune(long id) throws Exception {
		if(!listCacheCleaner.isEmpty() && driverRegistroDB!=null) {
			AccordoServizioParteComuneSintetico as = driverRegistroDB.getAccordoServizioParteComuneSintetico(id);
			if(as!=null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as);
				for (ICacheCleaner iCacheCleaner : listCacheCleaner) {
					iCacheCleaner.removeApi(idAccordo);
				}
			}
		}
	}
	
	public static void removeErogazione(long id) throws Exception {
		if(!listCacheCleaner.isEmpty() && driverRegistroDB!=null && driverConfigDB!=null) {
			AccordoServizioParteSpecifica as = driverRegistroDB.getAccordoServizioParteSpecifica(id, false);
			if(as!=null) {
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(as);
				List<IDPortaApplicativa> idPorteApplicative = null;
				List<IDConnettore> idConnettori = null;
				List<IDServizioApplicativo> idServiziApplicativiErogatori = null;
				
				Connection con = null;
				try {
					con = driverConfigDB.getConnection("getIDPorteApplicativeAssociate");
					idPorteApplicative = DBMappingUtils.getIDPorteApplicativeAssociate(idServizio, con, driverConfigDB.getTipoDB());
				}finally {
					driverConfigDB.releaseConnection(con);
				}

				if(idPorteApplicative!=null && !idPorteApplicative.isEmpty()) {
					for (IDPortaApplicativa idPortaApplicativa : idPorteApplicative) {
						PortaApplicativa pa = driverConfigDB.getPortaApplicativa(idPortaApplicativa);
						
						if(idServiziApplicativiErogatori==null) {
							idServiziApplicativiErogatori = new ArrayList<>();
						}
						for (PortaApplicativaServizioApplicativo paSa : pa.getServizioApplicativoList()) {
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setIdSoggettoProprietario(idServizio.getSoggettoErogatore());
							idSA.setNome(paSa.getNome());
							idServiziApplicativiErogatori.add(idSA);
						}
						
						if(pa.getBehaviour()!=null && pa.sizeServizioApplicativoList()>0) {
							if(idConnettori==null) {
								idConnettori = new ArrayList<>();
							}
							for (PortaApplicativaServizioApplicativo paSa : pa.getServizioApplicativoList()) {
								IDConnettore idConnettore = new IDConnettore();
								idConnettore.setIdSoggettoProprietario(idServizio.getSoggettoErogatore());
								idConnettore.setNome(paSa.getNome());
								idConnettore.setNomeConnettore(paSa.getDatiConnettore()!=null && paSa.getDatiConnettore().getNome()!=null ? 
										paSa.getDatiConnettore().getNome() : CostantiConfigurazione.NOME_CONNETTORE_DEFAULT);
								idConnettori.add(idConnettore);
							}
						}
					}
				}
				
				for (ICacheCleaner iCacheCleaner : listCacheCleaner) {
					iCacheCleaner.removeErogazione(idServizio);
					if(idPorteApplicative!=null && !idPorteApplicative.isEmpty()) {
						for (IDPortaApplicativa idPortaApplicativa : idPorteApplicative) {
							iCacheCleaner.removePortaApplicativa(idPortaApplicativa);
						}
					}
					if(idServiziApplicativiErogatori!=null && !idServiziApplicativiErogatori.isEmpty()) {
						for (IDServizioApplicativo idSA : idServiziApplicativiErogatori) {
							iCacheCleaner.removeApplicativo(idSA);
						}
					}
					if(idConnettori!=null && !idConnettori.isEmpty()) {
						for (IDConnettore idConnettore : idConnettori) {
							iCacheCleaner.removeConnettore(idConnettore);
						}
					}
				}
			}
		}
	}
	
	public static void removeFruizione(long id) throws Exception {
		if(!listCacheCleaner.isEmpty() && driverRegistroDB!=null && driverConfigDB!=null) {
			Fruitore fruitore = driverRegistroDB.getServizioFruitore(id);
			if(fruitore!=null) {
				AccordoServizioParteSpecifica as = driverRegistroDB.getAccordoServizioParteSpecifica(fruitore.getIdServizio(), false);
				IDSoggetto idFruitore = driverRegistroDB.getIdSoggetto(fruitore.getIdSoggetto());
				if(as!=null) {
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(as);
					List<IDPortaDelegata> idPorteDelegate = null;
					
					Connection con = null;
					try {
						con = driverConfigDB.getConnection("getIDPorteApplicativeAssociate");
						idPorteDelegate = DBMappingUtils.getIDPorteDelegateAssociate(idServizio, idFruitore, con, driverConfigDB.getTipoDB());
					}finally {
						driverConfigDB.releaseConnection(con);
					}
					
					for (ICacheCleaner iCacheCleaner : listCacheCleaner) {
						iCacheCleaner.removeFruizione(idFruitore, idServizio);
						if(idPorteDelegate!=null && !idPorteDelegate.isEmpty()) {
							for (IDPortaDelegata idPortaDelegata : idPorteDelegate) {
								iCacheCleaner.removePortaDelegata(idPortaDelegata);
							}
						}
					}
				}
			}
		}
	}
	
	public static void removeSoggetto(long id) throws Exception {
		if(!listCacheCleaner.isEmpty() && driverRegistroDB!=null) {
			Soggetto soggetto = driverRegistroDB.getSoggetto(id);
			if(soggetto!=null) {
				
				IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
				String portaDominio = soggetto.getPortaDominio();
				
				for (ICacheCleaner iCacheCleaner : listCacheCleaner) {
					iCacheCleaner.removeSoggetto(idSoggetto);
					if(portaDominio!=null) {
						iCacheCleaner.removePdd(portaDominio);
					}
				}
			}
		}
	}
	
	public static void removeApplicativo(long id) throws Exception {
		if(!listCacheCleaner.isEmpty() && driverConfigDB !=null) {
			ServizioApplicativo sa = driverConfigDB.getServizioApplicativo(id);
			if(sa!=null) {
				
				IDSoggetto idSoggetto = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(idSoggetto);
				idSA.setNome(sa.getNome());

				for (ICacheCleaner iCacheCleaner : listCacheCleaner) {
					iCacheCleaner.removeApplicativo(idSA);
				}
			}
		}
	}
	
	public static void removeRuolo(long id) throws Exception {
		if(!listCacheCleaner.isEmpty() && driverRegistroDB!=null) {
			Ruolo ruolo = driverRegistroDB.getRuolo(id);
			if(ruolo!=null) {
				
				IDRuolo idRuolo = new IDRuolo(ruolo.getNome());
				
				for (ICacheCleaner iCacheCleaner : listCacheCleaner) {
					iCacheCleaner.removeRuolo(idRuolo);
				}
			}
		}
	}
	
	public static void removeScope(long id) throws Exception {
		if(!listCacheCleaner.isEmpty() && driverRegistroDB!=null) {
			Scope scope = driverRegistroDB.getScope(id);
			if(scope!=null) {
				
				IDScope idScope = new IDScope(scope.getNome());
				
				for (ICacheCleaner iCacheCleaner : listCacheCleaner) {
					iCacheCleaner.removeScope(idScope);
				}
			}
		}
	}
	
	public static void removeGenericProperties(long id) throws Exception {
		if(!listCacheCleaner.isEmpty() && driverConfigDB !=null) {
			GenericProperties gp = driverConfigDB.getGenericProperties(id);
			if(gp!=null) {
				
				IDGenericProperties idGP = new IDGenericProperties();
				idGP.setTipologia(gp.getTipologia());
				idGP.setNome(gp.getNome());

				for (ICacheCleaner iCacheCleaner : listCacheCleaner) {
					iCacheCleaner.removeGenericProperties(idGP);
				}
			}
		}
	}
}
