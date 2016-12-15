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

package org.openspcoop2.protocol.registry;

import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDAzione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaProtocolProperty;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordoAzioni;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaFruizioniServizio;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPortType;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPortTypeAzioni;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.utils.ProtocolPropertiesUtils;
import org.slf4j.Logger;

/**
 *  ArchiveRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class CachedRegistryReader implements IRegistryReader {

	@SuppressWarnings("unused")
	private Logger log;
	private RegistroServiziManager registroServiziManager;
	private IProtocolFactory<?> protocolFactory;
	public CachedRegistryReader(Logger log,IProtocolFactory<?> protocolFactory) throws Exception{
		this(log, protocolFactory, null);
	}
	public CachedRegistryReader(Logger log,IProtocolFactory<?> protocolFactory, IState state) throws Exception{
		this.log = log;
		this.protocolFactory = protocolFactory;
		this.registroServiziManager = RegistroServiziManager.getInstance(state);
	}

	
	// PDD
	
	@Override
	public boolean existsPortaDominio(String nome){
		try{
			FiltroRicerca filtroDriver = new FiltroRicerca();
			filtroDriver.setNome(nome);
			List<String> l = this.registroServiziManager.getAllIdPorteDominio(filtroDriver, null);
			return l!=null && l.size()>0;
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public PortaDominio getPortaDominio(String nome) throws RegistryNotFound{
		try{
			return this.registroServiziManager.getPortaDominio(nome, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public List<String> findIdPorteDominio(Boolean operativo) throws RegistryNotFound{
		try{
			FiltroRicerca filtroDriver = new FiltroRicerca();
			if(operativo!=null){
				if(operativo){
					filtroDriver.setTipo("operativo");
				}
				else{
					filtroDriver.setTipo("esterno");
				}
			}
			return this.registroServiziManager.getAllIdPorteDominio(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	
	
	// SOGGETTI
	
	@Override
	public boolean existsSoggettoByCodiceIPA(String codiceIPA) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public boolean existsSoggetto(IDSoggetto idSoggetto) {
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti();
			filtroDriver.setTipo(idSoggetto.getTipo());
			filtroDriver.setNome(idSoggetto.getNome());
			List<IDSoggetto> l = this.registroServiziManager.getAllIdSoggetti(filtroDriver,null);
			return l!=null && l.size()>0;
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public IDSoggetto getIdSoggettoByCodiceIPA(String codiceIPA)
			throws RegistryNotFound {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public String getCodiceIPA(IDSoggetto idSoggetto) throws RegistryNotFound {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public String getDominio(IDSoggetto idSoggetto) throws RegistryNotFound{
		try{
			return this.registroServiziManager.getDominio(idSoggetto, null, this.protocolFactory);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public Soggetto getSoggetto(IDSoggetto idSoggetto) throws RegistryNotFound {
		try{
			return this.registroServiziManager.getSoggetto(idSoggetto, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public List<IDSoggetto> findIdSoggetti(FiltroRicercaSoggetti filtro) throws RegistryNotFound{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti();
			if(filtro.getTipo()!=null){
				filtroDriver.setTipo(filtro.getTipo());
			}
			if(filtro.getNome()!=null){
				filtroDriver.setNome(filtro.getNome());
			}
			List<FiltroRicercaProtocolProperty> listPP = ProtocolPropertiesUtils.convert(filtro.getProtocolProperties());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolProperties(listPP);
			}	
			return this.registroServiziManager.getAllIdSoggetti(filtroDriver,null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	
	
	// ACCORDI PARTE COMUNE
	
	@Override
	public AccordoServizioParteComune getAccordoServizioParteComune(
			IDAccordo idAccordo) throws RegistryNotFound {
		try{
			return this.registroServiziManager.getAccordoServizioParteComune(idAccordo, null, false);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public AccordoServizioParteComune getAccordoServizioParteComune(
			IDAccordo idAccordo, boolean readAllegati) throws RegistryNotFound {
		try{
			return this.registroServiziManager.getAccordoServizioParteComune(idAccordo, null, readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public List<IDAccordo> findIdAccordiServizioParteComune(FiltroRicercaAccordi filtro) throws RegistryNotFound{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaAccordi filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaAccordi();
			if(filtro.getNome()!=null){
				filtroDriver.setNomeAccordo(filtro.getNome());
			}
			if(filtro.getVersione()!=null){
				filtroDriver.setVersione(filtro.getVersione());
			}
			if(filtro.getSoggetto()!=null){
				if(filtro.getSoggetto().getTipo()!=null){
					filtroDriver.setTipoSoggettoReferente(filtro.getSoggetto().getTipo());
				}
				if(filtro.getSoggetto().getNome()!=null){
					filtroDriver.setNomeSoggettoReferente(filtro.getSoggetto().getNome());
				}
			}
			List<FiltroRicercaProtocolProperty> listPP = ProtocolPropertiesUtils.convert(filtro.getProtocolProperties());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolPropertiesAccordo(listPP);
			}	
			if(filtro.getEscludiServiziComposti()!=null){
				filtroDriver.setServizioComposto(!filtro.getEscludiServiziComposti());
			}
			if(filtro.getEscludiServiziNonComposti()!=null){
				filtroDriver.setServizioComposto(filtro.getEscludiServiziNonComposti());
			}

			return this.registroServiziManager.getAllIdAccordiServizioParteComune(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	
	
	// ELEMENTI INTERNI ALL'ACCORDO PARTE COMUNE
	
	@Override
	public List<IDPortType> findIdPortType(FiltroRicercaPortType filtro) throws RegistryNotFound{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes();
			
			// portType
			if(filtro.getNomePortType()!=null){
				filtroDriver.setNomePortType(filtro.getNomePortType());
			}
			List<FiltroRicercaProtocolProperty> listPP_portTypes = ProtocolPropertiesUtils.convert(filtro.getProtocolPropertiesPortType());
			if(listPP_portTypes!=null && listPP_portTypes.size()>0){
				filtroDriver.setProtocolPropertiesPortType(listPP_portTypes);
			}	
			
			// accordo
			if(filtro.getNome()!=null){
				filtroDriver.setNomeAccordo(filtro.getNome());
			}
			if(filtro.getVersione()!=null){
				filtroDriver.setVersione(filtro.getVersione());
			}
			if(filtro.getSoggetto()!=null){
				if(filtro.getSoggetto().getTipo()!=null){
					filtroDriver.setTipoSoggettoReferente(filtro.getSoggetto().getTipo());
				}
				if(filtro.getSoggetto().getNome()!=null){
					filtroDriver.setNomeSoggettoReferente(filtro.getSoggetto().getNome());
				}
			}
			List<FiltroRicercaProtocolProperty> listPP = ProtocolPropertiesUtils.convert(filtro.getProtocolProperties());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolPropertiesAccordo(listPP);
			}	
			if(filtro.getEscludiServiziComposti()!=null){
				filtroDriver.setServizioComposto(!filtro.getEscludiServiziComposti());
			}
			if(filtro.getEscludiServiziNonComposti()!=null){
				filtroDriver.setServizioComposto(filtro.getEscludiServiziNonComposti());
			}

			return this.registroServiziManager.getAllIdPortType(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public List<IDAzione> findIdAzionePortType(FiltroRicercaPortTypeAzioni filtro) throws RegistryNotFound{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaOperations filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaOperations();
			
			// operation
			if(filtro.getNomeAzione()!=null){
				filtroDriver.setNomeAzione(filtro.getNomeAzione());
			}
			List<FiltroRicercaProtocolProperty> listPP_azioni = ProtocolPropertiesUtils.convert(filtro.getProtocolPropertiesAzione());
			if(listPP_azioni!=null && listPP_azioni.size()>0){
				filtroDriver.setProtocolPropertiesAzione(listPP_azioni);
			}	
			
			// portType
			if(filtro.getNomePortType()!=null){
				filtroDriver.setNomePortType(filtro.getNomePortType());
			}
			List<FiltroRicercaProtocolProperty> listPP_portTypes = ProtocolPropertiesUtils.convert(filtro.getProtocolPropertiesPortType());
			if(listPP_portTypes!=null && listPP_portTypes.size()>0){
				filtroDriver.setProtocolPropertiesPortType(listPP_portTypes);
			}	
			
			// accordo
			if(filtro.getNome()!=null){
				filtroDriver.setNomeAccordo(filtro.getNome());
			}
			if(filtro.getVersione()!=null){
				filtroDriver.setVersione(filtro.getVersione());
			}
			if(filtro.getSoggetto()!=null){
				if(filtro.getSoggetto().getTipo()!=null){
					filtroDriver.setTipoSoggettoReferente(filtro.getSoggetto().getTipo());
				}
				if(filtro.getSoggetto().getNome()!=null){
					filtroDriver.setNomeSoggettoReferente(filtro.getSoggetto().getNome());
				}
			}
			List<FiltroRicercaProtocolProperty> listPP = ProtocolPropertiesUtils.convert(filtro.getProtocolProperties());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolPropertiesAccordo(listPP);
			}	
			if(filtro.getEscludiServiziComposti()!=null){
				filtroDriver.setServizioComposto(!filtro.getEscludiServiziComposti());
			}
			if(filtro.getEscludiServiziNonComposti()!=null){
				filtroDriver.setServizioComposto(filtro.getEscludiServiziNonComposti());
			}

			return this.registroServiziManager.getAllIdAzionePortType(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public List<IDAzione> findIdAzioneAccordo(FiltroRicercaAccordoAzioni filtro) throws RegistryNotFound{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaAzioni filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaAzioni();
			
			// azioni
			if(filtro.getNomeAzione()!=null){
				filtroDriver.setNomeAzione(filtro.getNomeAzione());
			}
			List<FiltroRicercaProtocolProperty> listPP_azioni = ProtocolPropertiesUtils.convert(filtro.getProtocolPropertiesAzione());
			if(listPP_azioni!=null && listPP_azioni.size()>0){
				filtroDriver.setProtocolPropertiesAzione(listPP_azioni);
			}	
			
			// accordo
			if(filtro.getNome()!=null){
				filtroDriver.setNomeAccordo(filtro.getNome());
			}
			if(filtro.getVersione()!=null){
				filtroDriver.setVersione(filtro.getVersione());
			}
			if(filtro.getSoggetto()!=null){
				if(filtro.getSoggetto().getTipo()!=null){
					filtroDriver.setTipoSoggettoReferente(filtro.getSoggetto().getTipo());
				}
				if(filtro.getSoggetto().getNome()!=null){
					filtroDriver.setNomeSoggettoReferente(filtro.getSoggetto().getNome());
				}
			}
			List<FiltroRicercaProtocolProperty> listPP = ProtocolPropertiesUtils.convert(filtro.getProtocolProperties());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolPropertiesAccordo(listPP);
			}	
			if(filtro.getEscludiServiziComposti()!=null){
				filtroDriver.setServizioComposto(!filtro.getEscludiServiziComposti());
			}
			if(filtro.getEscludiServiziNonComposti()!=null){
				filtroDriver.setServizioComposto(filtro.getEscludiServiziNonComposti());
			}

			return this.registroServiziManager.getAllIdAzioneAccordo(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	} 
	
	
	
	// ACCORDI PARTE SPECIFICA
	
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDServizio idServizio) throws RegistryNotFound {
		try{
			return this.registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	

	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDServizio idServizio, boolean readAllegati)
			throws RegistryNotFound {
		try{
			return this.registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public List<IDServizio> findIdAccordiServizioParteSpecifica(FiltroRicercaServizi filtro) throws RegistryNotFound{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaServizi filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaServizi();
			
			// servizio
			if(filtro.getTipoServizio()!=null){
				filtroDriver.setTipo(filtro.getTipoServizio());
			}
			if(filtro.getNomeServizio()!=null){
				filtroDriver.setNome(filtro.getNomeServizio());
			}
			if(filtro.getVersioneServizio()!=null){
				filtroDriver.setVersione(filtro.getVersioneServizio());
			}
			if(filtro.getSoggettoErogatore()!=null){
				if(filtro.getSoggettoErogatore().getTipo()!=null){
					filtroDriver.setTipoSoggettoErogatore(filtro.getSoggettoErogatore().getTipo());
				}
				if(filtro.getSoggettoErogatore().getNome()!=null){
					filtroDriver.setNomeSoggettoErogatore(filtro.getSoggettoErogatore().getNome());
				}
			}
			List<FiltroRicercaProtocolProperty> listPP = ProtocolPropertiesUtils.convert(filtro.getProtocolPropertiesServizi());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolProperties(listPP);
			}

			return this.registroServiziManager.getAllIdServizi(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}


	
	
	// ELEMENTI INTERNI ALL'ACCORDO PARTE SPECIFICA
	
	@Override
	public List<IDFruizione> findIdFruizioni(FiltroRicercaFruizioniServizio filtro) throws RegistryNotFound{
		
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaFruizioniServizio filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaFruizioniServizio();
			
			if(filtro.getSoggettoFruitore()!=null){
				if(filtro.getSoggettoFruitore().getTipo()!=null){
					filtroDriver.setTipoSoggettoFruitore(filtro.getSoggettoFruitore().getTipo());
				}
				if(filtro.getSoggettoFruitore().getNome()!=null){
					filtroDriver.setNomeSoggettoFruitore(filtro.getSoggettoFruitore().getNome());
				}
			}
			List<FiltroRicercaProtocolProperty> listPP_fruitore = ProtocolPropertiesUtils.convert(filtro.getProtocolPropertiesFruizione());
			if(listPP_fruitore!=null && listPP_fruitore.size()>0){
				filtroDriver.setProtocolPropertiesFruizione(listPP_fruitore);
			}
			
			// servizio
			if(filtro.getTipoServizio()!=null){
				filtroDriver.setTipo(filtro.getTipoServizio());
			}
			if(filtro.getNomeServizio()!=null){
				filtroDriver.setNome(filtro.getNomeServizio());
			}
			if(filtro.getVersioneServizio()!=null){
				filtroDriver.setVersione(filtro.getVersioneServizio());
			}
			if(filtro.getSoggettoErogatore()!=null){
				if(filtro.getSoggettoErogatore().getTipo()!=null){
					filtroDriver.setTipoSoggettoErogatore(filtro.getSoggettoErogatore().getTipo());
				}
				if(filtro.getSoggettoErogatore().getNome()!=null){
					filtroDriver.setNomeSoggettoErogatore(filtro.getSoggettoErogatore().getNome());
				}
			}
			List<FiltroRicercaProtocolProperty> listPP = ProtocolPropertiesUtils.convert(filtro.getProtocolPropertiesServizi());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolProperties(listPP);
			}

			return this.registroServiziManager.getAllIdFruizioniServizio(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
		
	} 
	
	
	
	// ACCORDI COOPERAZIONE
	
	@Override
	public AccordoCooperazione getAccordoCooperazione(
			IDAccordoCooperazione idAccordo) throws RegistryNotFound {
		try{
			return this.registroServiziManager.getAccordoCooperazione(idAccordo, null, false);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public AccordoCooperazione getAccordoCooperazione(
			IDAccordoCooperazione idAccordo, boolean readAllegati)
			throws RegistryNotFound {
		try{
			return this.registroServiziManager.getAccordoCooperazione(idAccordo, null, readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public List<IDAccordoCooperazione> findIdAccordiCooperazione(FiltroRicercaAccordi filtro) throws RegistryNotFound{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaAccordi filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaAccordi();
			if(filtro.getNome()!=null){
				filtroDriver.setNomeAccordo(filtro.getNome());
			}
			if(filtro.getVersione()!=null){
				filtroDriver.setVersione(filtro.getVersione());
			}
			if(filtro.getSoggetto()!=null){
				if(filtro.getSoggetto().getTipo()!=null){
					filtroDriver.setTipoSoggettoReferente(filtro.getSoggetto().getTipo());
				}
				if(filtro.getSoggetto().getNome()!=null){
					filtroDriver.setNomeSoggettoReferente(filtro.getSoggetto().getNome());
				}
			}
			List<FiltroRicercaProtocolProperty> listPP = ProtocolPropertiesUtils.convert(filtro.getProtocolProperties());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolPropertiesAccordo(listPP);
			}	
			if(filtro.getEscludiServiziComposti()!=null){
				filtroDriver.setServizioComposto(!filtro.getEscludiServiziComposti());
			}
			if(filtro.getEscludiServiziNonComposti()!=null){
				filtroDriver.setServizioComposto(filtro.getEscludiServiziNonComposti());
			}

			return this.registroServiziManager.getAllIdAccordiCooperazione(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}

}

