/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.registry;

import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaProtocolPropertyRegistry;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaAccordoAzioni;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaFruizioniServizio;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaGruppi;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPortType;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPortTypeAzioni;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaRisorse;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaRuoli;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaScope;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaSoggetti;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.utils.ProtocolUtils;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.slf4j.Logger;

/**
 *  ArchiveRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CachedRegistryReader implements IRegistryReader {

	@SuppressWarnings("unused")
	private Logger log;
	private RegistroServiziManager registroServiziManager;
	private IProtocolFactory<?> protocolFactory;
	private RequestInfo requestInfo;
	/*public CachedRegistryReader(Logger log,IProtocolFactory<?> protocolFactory) throws Exception{
		this.log = log;
		this.protocolFactory = protocolFactory;
		this.registroServiziManager = RegistroServiziManager.getInstance();
	}*/
	public CachedRegistryReader(Logger log,IProtocolFactory<?> protocolFactory, IState state, RequestInfo requestInfo) throws Exception{
		this.log = log;
		this.protocolFactory = protocolFactory;
		this.registroServiziManager = RegistroServiziManager.getInstance(state);
		this.requestInfo = requestInfo;
	}
	public CachedRegistryReader(Logger log,IProtocolFactory<?> protocolFactory, RegistroServiziManager registroServiziManager, RequestInfo requestInfo) throws Exception{
		this.log = log;
		this.protocolFactory = protocolFactory;
		this.registroServiziManager = registroServiziManager;
		this.requestInfo = requestInfo;
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
	public PortaDominio getPortaDominio(String nome) throws RegistryNotFound,RegistryException{
		try{
			return this.registroServiziManager.getPortaDominio(nome, null, this.requestInfo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<String> findIdPorteDominio(Boolean operativo) throws RegistryNotFound,RegistryException{
		try{
			FiltroRicerca filtroDriver = new FiltroRicerca();
			if(operativo!=null){
				if(operativo){
					filtroDriver.setTipo(PddTipologia.OPERATIVO.toString());
				}
				else{
					filtroDriver.setTipo(PddTipologia.ESTERNO.toString());
				}
			}
			return this.registroServiziManager.getAllIdPorteDominio(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
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
			throws RegistryNotFound,RegistryException {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public String getCodiceIPA(IDSoggetto idSoggetto) throws RegistryNotFound,RegistryException {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public IDSoggetto getIdSoggettoDefault(String tipoSoggettoDefault) throws RegistryNotFound,RegistryException{
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public String getDominio(IDSoggetto idSoggetto) throws RegistryNotFound,RegistryException{
		try{
			return this.registroServiziManager.getDominio(idSoggetto, null, this.protocolFactory, this.requestInfo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public Soggetto getSoggetto(IDSoggetto idSoggetto) throws RegistryNotFound,RegistryException {
		try{
			return this.registroServiziManager.getSoggetto(idSoggetto, null, this.requestInfo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsSoggettoByCredenzialiBasic(String username, String password, CryptConfig cryptConfig){
		try{
			return this.registroServiziManager.getIdSoggettoByCredenzialiBasic(username, password, cryptConfig, null)!=null;
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiBasic(String username, String password, CryptConfig cryptConfig) throws RegistryNotFound,RegistryException{
		try{
			return this.registroServiziManager.getSoggettoByCredenzialiBasic(username, password, cryptConfig, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsSoggettoByCredenzialiSsl(String subject, String issuer){
		try{
			return this.registroServiziManager.getIdSoggettoByCredenzialiSsl(subject, issuer, null)!=null;
		}catch(Exception e){
			return false;
		}	
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiSsl(String subject, String issuer) throws RegistryNotFound,RegistryException{
		try{
			return this.registroServiziManager.getSoggettoByCredenzialiSsl(subject, issuer, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsSoggettoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier){
		try{
			return this.registroServiziManager.getIdSoggettoByCredenzialiSsl(certificate, strictVerifier, null)!=null;
		}catch(Exception e){
			return false;
		}	
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws RegistryNotFound,RegistryException{
		try{
			return this.registroServiziManager.getSoggettoByCredenzialiSsl(certificate, strictVerifier, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsSoggettoByCredenzialiPrincipal(String principal){
		try{
			return this.registroServiziManager.getIdSoggettoByCredenzialiPrincipal(principal, null)!=null;
		}catch(Exception e){
			return false;
		}	
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiPrincipal(String principal) throws RegistryNotFound,RegistryException{
		try{
			return this.registroServiziManager.getSoggettoByCredenzialiPrincipal(principal, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<IDSoggetto> findIdSoggetti(ProtocolFiltroRicercaSoggetti filtro) throws RegistryNotFound,RegistryException{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti();
			if(filtro.getTipo()!=null){
				filtroDriver.setTipo(filtro.getTipo());
			}
			if(filtro.getNome()!=null){
				filtroDriver.setNome(filtro.getNome());
			}
			if(filtro.getNomePdd()!=null){
				filtroDriver.setNomePdd(filtro.getNomePdd());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP = ProtocolUtils.convert(filtro.getProtocolProperties());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolProperties(listPP);
			}
			if(filtro.getProprieta()!=null && !filtro.getProprieta().isEmpty()) {
				filtroDriver.setProprieta(filtro.getProprieta());
			}
			return this.registroServiziManager.getAllIdSoggetti(filtroDriver,null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean inUso(IDSoggetto idSoggetto, boolean verificaRuoli) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public String getDettagliInUso(IDSoggetto idSoggetto, boolean verificaRuoli) throws RegistryException {
		throw new RuntimeException("Not Implemented");
	}
	
	
	
	// ACCORDI PARTE COMUNE
	
	@Override
	public AccordoServizioParteComune getAccordoServizioParteComune(
			IDAccordo idAccordo) throws RegistryNotFound,RegistryException {
		try{
			return this.registroServiziManager.getAccordoServizioParteComune(idAccordo, null, false, false, this.requestInfo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}

	@Override
	public AccordoServizioParteComune getAccordoServizioParteComune(
			IDAccordo idAccordo, boolean readAllegati,boolean readDatiRegistro) throws RegistryNotFound,RegistryException {
		try{
			return this.registroServiziManager.getAccordoServizioParteComune(idAccordo, null, readAllegati, readDatiRegistro, this.requestInfo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<IDAccordo> findIdAccordiServizioParteComune(ProtocolFiltroRicercaAccordi filtro) throws RegistryNotFound,RegistryException{
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
			if(filtro.getServiceBinding()!=null) {
				filtroDriver.setServiceBinding(filtro.getServiceBinding());
			}
			if(filtro.getIdGruppo()!=null) {
				filtroDriver.setIdGruppo(filtro.getIdGruppo());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP = ProtocolUtils.convert(filtro.getProtocolProperties());
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
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getAccordoServizioParteComuneSoap(IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchema, boolean readDatiRegistro) throws RegistryNotFound,RegistryException{
		try{
			return this.registroServiziManager.getWsdlAccordoServizio(idService, infoWsdlSource, buildSchema, readDatiRegistro, this.requestInfo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public org.openspcoop2.core.registry.rest.AccordoServizioWrapper getAccordoServizioParteComuneRest(IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchema, boolean processIncludeForOpenApi, boolean readDatiRegistro) throws RegistryNotFound,RegistryException{
		try{
			return this.registroServiziManager.getRestAccordoServizio(idService, infoWsdlSource, buildSchema, processIncludeForOpenApi, readDatiRegistro, this.requestInfo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean inUso(IDAccordo idAccordo) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public String getDettagliInUso(IDAccordo idAccordo) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	
	
	
	
	// ELEMENTI INTERNI ALL'ACCORDO PARTE COMUNE
	
	@Override
	public PortType getPortType(IDPortType id) throws RegistryNotFound,RegistryException{
		try{
			AccordoServizioParteComune as = this.registroServiziManager.getAccordoServizioParteComune(id.getIdAccordo(), null, false, false, this.requestInfo);
			for (PortType pt : as.getPortTypeList()) {
				if(pt.getNome().equals(id.getNome())){
					return pt;
				}
			}
			throw new DriverRegistroServiziNotFound("PortType ["+id.getNome()+"] non trovato nell'accordo ["+IDAccordoFactory.getInstance().getUriFromIDAccordo(id.getIdAccordo())+"]");
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<IDPortType> findIdPortType(ProtocolFiltroRicercaPortType filtro) throws RegistryNotFound,RegistryException{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes();
			
			// portType
			if(filtro.getNomePortType()!=null){
				filtroDriver.setNomePortType(filtro.getNomePortType());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP_portTypes = ProtocolUtils.convert(filtro.getProtocolPropertiesPortType());
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
			if(filtro.getServiceBinding()!=null) {
				filtroDriver.setServiceBinding(filtro.getServiceBinding());
			}
			if(filtro.getIdGruppo()!=null) {
				filtroDriver.setIdGruppo(filtro.getIdGruppo());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP = ProtocolUtils.convert(filtro.getProtocolProperties());
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
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean inUso(IDPortType id) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public String getDettagliInUso(IDPortType id) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
		
	@Override
	public Operation getAzionePortType(IDPortTypeAzione id) throws RegistryNotFound,RegistryException{
		PortType portType = this.getPortType(id.getIdPortType());
		for (Operation opCheck : portType.getAzioneList()) {
			if(opCheck.getNome().equals(id.getNome())){
				return opCheck;
			}
		}

		String uriAccordo = null;
		try{
			uriAccordo = IDAccordoFactory.getInstance().getUriFromIDAccordo(id.getIdPortType().getIdAccordo());
		}catch(Exception e){
			uriAccordo = id.getIdPortType().getIdAccordo().toString();
		}
		throw new RegistryNotFound("Azione ["+id.getNome()+"] non trovata all'interno del PortType ["+id.getIdPortType().getNome()+
					"] dell'accordo ["+uriAccordo+"]");
	}
	
	@Override
	public List<IDPortTypeAzione> findIdAzionePortType(ProtocolFiltroRicercaPortTypeAzioni filtro) throws RegistryNotFound,RegistryException{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaOperations filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaOperations();
			
			// operation
			if(filtro.getNomeAzione()!=null){
				filtroDriver.setNomeAzione(filtro.getNomeAzione());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP_azioni = ProtocolUtils.convert(filtro.getProtocolPropertiesAzione());
			if(listPP_azioni!=null && listPP_azioni.size()>0){
				filtroDriver.setProtocolPropertiesAzione(listPP_azioni);
			}	
			
			// portType
			if(filtro.getNomePortType()!=null){
				filtroDriver.setNomePortType(filtro.getNomePortType());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP_portTypes = ProtocolUtils.convert(filtro.getProtocolPropertiesPortType());
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
			if(filtro.getServiceBinding()!=null) {
				filtroDriver.setServiceBinding(filtro.getServiceBinding());
			}
			if(filtro.getIdGruppo()!=null) {
				filtroDriver.setIdGruppo(filtro.getIdGruppo());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP = ProtocolUtils.convert(filtro.getProtocolProperties());
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
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean inUso(IDPortTypeAzione id) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	@Override
	public String getDettagliInUso(IDPortTypeAzione id) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public Azione getAzioneAccordo(IDAccordoAzione id) throws RegistryNotFound,RegistryException{
		AccordoServizioParteComune as = this.getAccordoServizioParteComune(id.getIdAccordo());
		for (Azione azCheck : as.getAzioneList()) {
			if(azCheck.getNome().equals(id.getNome())){
				return azCheck;
			}
		}

		String uriAccordo = null;
		try{
			uriAccordo = IDAccordoFactory.getInstance().getUriFromIDAccordo(id.getIdAccordo());
		}catch(Exception e){
			uriAccordo = id.getIdAccordo().toString();
		}
		throw new RegistryNotFound("Azione ["+id.getNome()+"] non trovata all'interno dell'accordo ["+uriAccordo+"]");
	}
	
	@Override
	public List<IDAccordoAzione> findIdAzioneAccordo(ProtocolFiltroRicercaAccordoAzioni filtro) throws RegistryNotFound,RegistryException{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaAzioni filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaAzioni();
			
			// azioni
			if(filtro.getNomeAzione()!=null){
				filtroDriver.setNomeAzione(filtro.getNomeAzione());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP_azioni = ProtocolUtils.convert(filtro.getProtocolPropertiesAzione());
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
			if(filtro.getServiceBinding()!=null) {
				filtroDriver.setServiceBinding(filtro.getServiceBinding());
			}
			if(filtro.getIdGruppo()!=null) {
				filtroDriver.setIdGruppo(filtro.getIdGruppo());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP = ProtocolUtils.convert(filtro.getProtocolProperties());
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
			throw new RegistryException(e.getMessage(),e);
		}
	} 
	
	@Override
	public Resource getResourceAccordo(IDResource id) throws RegistryNotFound,RegistryException{
		AccordoServizioParteComune as = this.getAccordoServizioParteComune(id.getIdAccordo());
		for (Resource resourceCheck : as.getResourceList()) {
			if(resourceCheck.getNome().equals(id.getNome())){
				return resourceCheck;
			}
		}

		String uriAccordo = null;
		try{
			uriAccordo = IDAccordoFactory.getInstance().getUriFromIDAccordo(id.getIdAccordo());
		}catch(Exception e){
			uriAccordo = id.getIdAccordo().toString();
		}
		throw new RegistryNotFound("Risorsa ["+id.getNome()+"] non trovata all'interno dell'accordo ["+uriAccordo+"]");
	}
	@Override
	public List<IDResource> findIdResourceAccordo(ProtocolFiltroRicercaRisorse filtro) throws RegistryNotFound,RegistryException{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaResources filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaResources();
			
			// azioni
			if(filtro.getNomeRisorsa()!=null){
				filtroDriver.setResourceName(filtro.getNomeRisorsa());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP_resources = ProtocolUtils.convert(filtro.getProtocolPropertiesRisorsa());
			if(listPP_resources!=null && listPP_resources.size()>0){
				filtroDriver.setProtocolPropertiesResources(listPP_resources);
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
			if(filtro.getServiceBinding()!=null) {
				filtroDriver.setServiceBinding(filtro.getServiceBinding());
			}
			if(filtro.getIdGruppo()!=null) {
				filtroDriver.setIdGruppo(filtro.getIdGruppo());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP = ProtocolUtils.convert(filtro.getProtocolProperties());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolPropertiesAccordo(listPP);
			}	
			if(filtro.getEscludiServiziComposti()!=null){
				filtroDriver.setServizioComposto(!filtro.getEscludiServiziComposti());
			}
			if(filtro.getEscludiServiziNonComposti()!=null){
				filtroDriver.setServizioComposto(filtro.getEscludiServiziNonComposti());
			}

			return this.registroServiziManager.getAllIdResource(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	} 
	
	@Override
	public boolean inUso(IDResource id) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	@Override
	public String getDettagliInUso(IDResource id) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	
	
	// ACCORDI PARTE SPECIFICA
	
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDServizio idServizio) throws RegistryNotFound,RegistryException {
		try{
			return this.registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false, this.requestInfo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	

	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDServizio idServizio, boolean readAllegati)
			throws RegistryNotFound,RegistryException {
		try{
			return this.registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, readAllegati, this.requestInfo);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}

	@Override
	public List<IDServizio> findIdAccordiServizioParteSpecifica(ProtocolFiltroRicercaServizi filtro) throws RegistryNotFound,RegistryException{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaServizi filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaServizi();
			
			// servizio
			if(filtro.getIdAccordoServizioParteComune()!=null){
				filtroDriver.setIdAccordoServizioParteComune(filtro.getIdAccordoServizioParteComune());
			}
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
			if(filtro.getPortType()!=null){
				filtroDriver.setPortType(filtro.getPortType());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP = ProtocolUtils.convert(filtro.getProtocolPropertiesServizi());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolProperties(listPP);
			}

			return this.registroServiziManager.getAllIdServizi(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}


	
	
	// ELEMENTI INTERNI ALL'ACCORDO PARTE SPECIFICA
	
	@Override
	public List<IDFruizione> findIdFruizioni(ProtocolFiltroRicercaFruizioniServizio filtro) throws RegistryNotFound,RegistryException{
		
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
			List<FiltroRicercaProtocolPropertyRegistry> listPP_fruitore = ProtocolUtils.convert(filtro.getProtocolPropertiesFruizione());
			if(listPP_fruitore!=null && listPP_fruitore.size()>0){
				filtroDriver.setProtocolPropertiesFruizione(listPP_fruitore);
			}
			
			// servizio
			if(filtro.getIdAccordoServizioParteComune()!=null){
				filtroDriver.setIdAccordoServizioParteComune(filtro.getIdAccordoServizioParteComune());
			}
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
			if(filtro.getPortType()!=null){
				filtroDriver.setPortType(filtro.getPortType());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP = ProtocolUtils.convert(filtro.getProtocolPropertiesServizi());
			if(listPP!=null && listPP.size()>0){
				filtroDriver.setProtocolProperties(listPP);
			}

			return this.registroServiziManager.getAllIdFruizioniServizio(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
		
	} 
	
	
	
	// ACCORDI COOPERAZIONE
	
	@Override
	public AccordoCooperazione getAccordoCooperazione(
			IDAccordoCooperazione idAccordo) throws RegistryNotFound,RegistryException {
		try{
			return this.registroServiziManager.getAccordoCooperazione(idAccordo, null, false);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}

	@Override
	public AccordoCooperazione getAccordoCooperazione(
			IDAccordoCooperazione idAccordo, boolean readAllegati)
			throws RegistryNotFound,RegistryException {
		try{
			return this.registroServiziManager.getAccordoCooperazione(idAccordo, null, readAllegati);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<IDAccordoCooperazione> findIdAccordiCooperazione(ProtocolFiltroRicercaAccordi filtro) throws RegistryNotFound,RegistryException{
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
			if(filtro.getServiceBinding()!=null) {
				filtroDriver.setServiceBinding(filtro.getServiceBinding());
			}
			if(filtro.getIdGruppo()!=null) {
				filtroDriver.setIdGruppo(filtro.getIdGruppo());
			}
			List<FiltroRicercaProtocolPropertyRegistry> listPP = ProtocolUtils.convert(filtro.getProtocolProperties());
			if(listPP!=null && !listPP.isEmpty()){
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
			throw new RegistryException(e.getMessage(),e);
		}
	}

	
	

	
	// GRUPPI
	@Override
	public List<IDGruppo> findIdGruppi(ProtocolFiltroRicercaGruppi filtro) throws RegistryNotFound,RegistryException{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaGruppi filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaGruppi();
			if(filtro.getMinDate()!=null) {
				filtroDriver.setMinDate(filtro.getMinDate());
			}
			if(filtro.getMaxDate()!=null) {
				filtroDriver.setMaxDate(filtro.getMaxDate());
			}
			filtroDriver.setOrdinaDataRegistrazione(filtro.isOrdinaDataRegistrazione());
			if(filtro.getNome()!=null){
				filtroDriver.setNome(filtro.getNome());
			}
			if(filtro.getTipo()!=null){
				filtroDriver.setTipo(filtro.getTipo());
			}
			if(filtro.getServiceBinding()!=null) {
				filtroDriver.setServiceBinding(filtro.getServiceBinding());
			}
			if(filtro.getProtocollo()!=null) {
				filtroDriver.setProtocollo(filtro.getProtocollo());
			}
			if(filtro.getProtocolli()!=null){
				filtroDriver.setProtocolli(filtro.getProtocolli());
			}

			return this.registroServiziManager.getAllIdGruppi(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	// RUOLI
	@Override
	public List<IDRuolo> findIdRuoli(ProtocolFiltroRicercaRuoli filtro) throws RegistryNotFound,RegistryException{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaRuoli filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaRuoli();
			if(filtro.getMinDate()!=null) {
				filtroDriver.setMinDate(filtro.getMinDate());
			}
			if(filtro.getMaxDate()!=null) {
				filtroDriver.setMaxDate(filtro.getMaxDate());
			}
			if(filtro.getNome()!=null){
				filtroDriver.setNome(filtro.getNome());
			}
			if(filtro.getTipo()!=null){
				filtroDriver.setTipo(filtro.getTipo());
			}
			if(filtro.getTipologia()!=null) {
				filtroDriver.setTipologia(filtro.getTipologia());
			}
			if(filtro.getContesto()!=null) {
				filtroDriver.setContesto(filtro.getContesto());
			}

			return this.registroServiziManager.getAllIdRuoli(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	// SCOPE
	@Override
	public List<IDScope> findIdScope(ProtocolFiltroRicercaScope filtro) throws RegistryNotFound,RegistryException{
		try{
			org.openspcoop2.core.registry.driver.FiltroRicercaScope filtroDriver = new org.openspcoop2.core.registry.driver.FiltroRicercaScope();
			if(filtro.getMinDate()!=null) {
				filtroDriver.setMinDate(filtro.getMinDate());
			}
			if(filtro.getMaxDate()!=null) {
				filtroDriver.setMaxDate(filtro.getMaxDate());
			}
			if(filtro.getNome()!=null){
				filtroDriver.setNome(filtro.getNome());
			}
			if(filtro.getTipo()!=null){
				filtroDriver.setTipo(filtro.getTipo());
			}
			if(filtro.getTipologia()!=null) {
				filtroDriver.setTipologia(filtro.getTipologia());
			}
			if(filtro.getContesto()!=null) {
				filtroDriver.setContesto(filtro.getContesto());
			}

			return this.registroServiziManager.getAllIdScope(filtroDriver, null);
		} catch (DriverRegistroServiziNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
}

