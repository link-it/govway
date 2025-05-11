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
package org.openspcoop2.protocol.sdk.registry;

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
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;

/**
 * IRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IRegistryReader extends IRegistryReaderInUso {

	// PDD
	
	public boolean existsPortaDominio(String nome);
	public PortaDominio getPortaDominio(String nome) throws RegistryNotFound,RegistryException;
	public List<String> findIdPorteDominio(Boolean operativo) throws RegistryNotFound,RegistryException;
	
	
	// SOGGETTI
	
	public boolean existsSoggettoByCodiceIPA(String codiceIPA);
	
	public boolean existsSoggetto(IDSoggetto idSoggetto);
	
	public IDSoggetto getIdSoggettoByCodiceIPA(String codiceIPA) throws RegistryNotFound,RegistryException;
	
	public IDSoggetto getIdSoggettoDefault(String tipoSoggettoDefault) throws RegistryNotFound,RegistryException;
	
	public String getCodiceIPA(IDSoggetto idSoggetto) throws RegistryNotFound,RegistryException;
	
	public String getDominio(IDSoggetto idSoggetto) throws RegistryNotFound,RegistryException;
	
	public Soggetto getSoggetto(IDSoggetto idSoggetto) throws RegistryNotFound,RegistryException;

	public boolean existsSoggettoByCredenzialiBasic(String username, String password, CryptConfig cryptConfig);
	public Soggetto getSoggettoByCredenzialiBasic(String username, String password, CryptConfig cryptConfig) throws RegistryNotFound,RegistryException;
	
	public boolean existsSoggettoByCredenzialiSsl(String subject, String issuer);
	public Soggetto getSoggettoByCredenzialiSsl(String subject, String issuer) throws RegistryNotFound,RegistryException;
	
	public boolean existsSoggettoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier);
	public Soggetto getSoggettoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws RegistryNotFound,RegistryException;
	
	public boolean existsSoggettoByCredenzialiPrincipal(String principal);
	public Soggetto getSoggettoByCredenzialiPrincipal(String principal) throws RegistryNotFound,RegistryException;
	
	public List<IDSoggetto> findIdSoggetti(ProtocolFiltroRicercaSoggetti filtro) throws RegistryNotFound,RegistryException;
	
	// ACCORDI PARTE COMUNE
	
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo) throws RegistryNotFound,RegistryException;
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo,boolean readAllegati,boolean readDatiRegistro) throws RegistryNotFound,RegistryException;
	public List<IDAccordo> findIdAccordiServizioParteComune(ProtocolFiltroRicercaAccordi filtro) throws RegistryNotFound,RegistryException; 
	public org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getAccordoServizioParteComuneSoap(IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchema, boolean readDatiRegistro) throws RegistryNotFound,RegistryException;
	public org.openspcoop2.core.registry.rest.AccordoServizioWrapper getAccordoServizioParteComuneRest(IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchema, boolean processIncludeForOpenApi, boolean readDatiRegistro) throws RegistryNotFound,RegistryException;
	
	// ELEMENTI INTERNI ALL'ACCORDO PARTE COMUNE
	
	public PortType getPortType(IDPortType id) throws RegistryNotFound,RegistryException; 
	public List<IDPortType> findIdPortType(ProtocolFiltroRicercaPortType filtro) throws RegistryNotFound,RegistryException;

	public Operation getAzionePortType(IDPortTypeAzione id) throws RegistryNotFound,RegistryException; 
	public List<IDPortTypeAzione> findIdAzionePortType(ProtocolFiltroRicercaPortTypeAzioni filtro) throws RegistryNotFound,RegistryException; 

	public Azione getAzioneAccordo(IDAccordoAzione id) throws RegistryNotFound,RegistryException; 
	public List<IDAccordoAzione> findIdAzioneAccordo(ProtocolFiltroRicercaAccordoAzioni filtro) throws RegistryNotFound,RegistryException; 
	
	public Resource getResourceAccordo(IDResource id) throws RegistryNotFound,RegistryException; 
	public List<IDResource> findIdResourceAccordo(ProtocolFiltroRicercaRisorse filtro) throws RegistryNotFound,RegistryException; 
	
	// ACCORDI PARTE SPECIFICA
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio) throws RegistryNotFound,RegistryException;
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,boolean readAllegati) throws RegistryNotFound,RegistryException;
	public List<IDServizio> findIdAccordiServizioParteSpecifica(ProtocolFiltroRicercaServizi filtro) throws RegistryNotFound,RegistryException; 
	
	// ELEMENTI INTERNI ALL'ACCORDO PARTE SPECIFICA
	
	public List<IDFruizione> findIdFruizioni(ProtocolFiltroRicercaFruizioniServizio filtro) throws RegistryNotFound,RegistryException; 
		
	// ACCORDI COOPERAZIONE
	
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo) throws RegistryNotFound,RegistryException;
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo,boolean readAllegati) throws RegistryNotFound,RegistryException;
	public List<IDAccordoCooperazione> findIdAccordiCooperazione(ProtocolFiltroRicercaAccordi filtro) throws RegistryNotFound,RegistryException; 
	
	// GRUPPI
	public List<IDGruppo> findIdGruppi(ProtocolFiltroRicercaGruppi filtro) throws RegistryNotFound,RegistryException; 
	
	// RUOLI
	public List<IDRuolo> findIdRuoli(ProtocolFiltroRicercaRuoli filtro) throws RegistryNotFound,RegistryException; 
	
	// SCOPE
	public List<IDScope> findIdScope(ProtocolFiltroRicercaScope filtro) throws RegistryNotFound,RegistryException; 
}
