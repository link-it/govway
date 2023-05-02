/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.basic.archive.abstraction;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.protocol.abstraction.Erogazione;
import org.openspcoop2.protocol.abstraction.Soggetto;
import org.openspcoop2.protocol.abstraction.constants.CostantiAbstraction;
import org.openspcoop2.protocol.abstraction.constants.Tipologia;
import org.openspcoop2.protocol.abstraction.template.DatiServizio;
import org.openspcoop2.protocol.abstraction.template.DatiServizioApplicativoErogatore;
import org.openspcoop2.protocol.abstraction.template.DatiSoggetto;
import org.openspcoop2.protocol.abstraction.template.IdSoggetto;
import org.openspcoop2.protocol.abstraction.template.TemplateErogazione;
import org.openspcoop2.protocol.basic.archive.ZIPReadUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.constants.ArchiveVersion;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.utils.ManagerUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.TemplateUtils;
import org.slf4j.Logger;

/**     
 * ErogazioneConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErogazioneConverter extends AbstractConverter {

	// ----- Static method -----
	
	public static synchronized ArchiveIdCorrelazione generateIdCorrelazione(String tipoSoggettoErogatore,String nomeSoggettoErogatore,
			String tipoServizio, String nomeServizio,
			String descrizione) throws ProtocolException{
		
		// Identificativo Unico di Correlazione
		String uuid = "Erogazione_"+tipoSoggettoErogatore+"/"+nomeSoggettoErogatore+"_"+tipoServizio+"/"+nomeServizio+"_"+System.currentTimeMillis();
		
		Utilities.sleep(1); // per rendere univco il prossimo uuid
		
		ArchiveIdCorrelazione idCorrelazione = new ArchiveIdCorrelazione(uuid);
		
		String d = null;
		if(descrizione!=null && !"".equals(descrizione)){
			d = descrizione;
		}
		else{
			d = "Erogazione servizio:"+tipoServizio+"/"+nomeServizio+" erogatore:"+tipoSoggettoErogatore+"/"+nomeSoggettoErogatore;
		}
		idCorrelazione.setDescrizione(d);

		return idCorrelazione;
	}
	
	
	
	
	// ----- Instance method -----

	public ErogazioneConverter(Logger log,ZIPReadUtils zipReader) throws ProtocolException{
		super(log, zipReader);
	}
	
	// ritorna l'identificativo di correlazione.
	public ArchiveIdCorrelazione fillArchive(Archive archive, Erogazione erogazione, TemplateErogazione templateErogazione,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, boolean validationDocuments) throws ProtocolException{
		
		try{
			
			// Cerco identificare accordo di servizio parte comune
			IDAccordo idAccordoServizioParteComune = null;
			if(erogazione.getAccordoServizioParteComune().getUri()!=null){
				idAccordoServizioParteComune = this.idAccordoFactory.getIDAccordoFromUri(erogazione.getAccordoServizioParteComune().getUri());
				if(this.existsAccordoServizioParteComune(archive, registryReader, idAccordoServizioParteComune)==false){
					throw new ProtocolException("Accordo di Servizio Parte Comune ["+idAccordoServizioParteComune.toString()+"] non esistente");
				}
			}
			else {
				if(erogazione.getAccordoServizioParteComune().getIdAccordo().getSoggetto()!=null && 
						erogazione.getAccordoServizioParteComune().getIdAccordo().getVersione()!=null){
					idAccordoServizioParteComune = 
							this.idAccordoFactory.getIDAccordoFromValues(
									erogazione.getAccordoServizioParteComune().getIdAccordo().getNome(), 
									erogazione.getAccordoServizioParteComune().getIdAccordo().getSoggetto().getTipo(), 
									erogazione.getAccordoServizioParteComune().getIdAccordo().getSoggetto().getNome(), 
									erogazione.getAccordoServizioParteComune().getIdAccordo().getVersione());
					if(this.existsAccordoServizioParteComune(archive, registryReader, idAccordoServizioParteComune)==false){
						throw new ProtocolException("Accordo di Servizio Parte Comune ["+idAccordoServizioParteComune.toString()+"] non esistente");
					}
				}
				else{
					idAccordoServizioParteComune = this.findIdAccordoServizioParteComune(archive, registryReader, 
							erogazione.getAccordoServizioParteComune().getIdAccordo().getNome(), 
							erogazione.getAccordoServizioParteComune().getIdAccordo().getSoggetto(), 
							erogazione.getAccordoServizioParteComune().getIdAccordo().getVersione());
				}
			}
			
			// IdSoggetto
			IdSoggetto soggettoErogatore = new IdSoggetto();
			soggettoErogatore.setTipo(erogazione.getSoggettoErogatore().getIdSoggetto().getTipo());
			soggettoErogatore.setNome(erogazione.getSoggettoErogatore().getIdSoggetto().getNome());
			
			// Protocollo
			String protocollo = ManagerUtils.getProtocolByOrganizationType(soggettoErogatore.getTipo());
			
			// Nome Servizio
			String nomeServizio = null;
			if(erogazione.getServizio()!=null && erogazione.getServizio().getNome()!=null){
				nomeServizio = erogazione.getServizio().getNome();
			}
			else if(erogazione.getAccordoServizioParteComune().getServizio()!=null){
				nomeServizio = erogazione.getAccordoServizioParteComune().getServizio();
			}
			else {
				nomeServizio = idAccordoServizioParteComune.getNome();
			}
			
			// TipoServizio
			String tipoServizio = null;
			if(erogazione.getServizio()!=null && erogazione.getServizio().getTipo()!=null){
				tipoServizio = erogazione.getServizio().getTipo();
			}
			else{
				tipoServizio = ManagerUtils.getDefaultServiceType(protocollo);
			}
			
			// Dati Servizio
			DatiServizio datiServizio = new DatiServizio();
			datiServizio.setUriAccordoServizioParteComune(this.idAccordoFactory.getUriFromIDAccordo(idAccordoServizioParteComune));
			datiServizio.setPortType(erogazione.getAccordoServizioParteComune().getServizio());
			datiServizio.setTipo(tipoServizio);
			datiServizio.setNome(nomeServizio);
			if(erogazione.getServizio()!=null){
				datiServizio.setEndpoint(erogazione.getServizio().getEndpoint());
				if(erogazione.getServizio().getTipologiaServizio()!=null){
					datiServizio.setTipologiaServizio(erogazione.getServizio().getTipologiaServizio().getValue());
				}
			}
			datiServizio.setErogatore(soggettoErogatore);

			// Dati Applicativo Erogatore
			boolean createApplicativoErogatore = false;
			boolean createPortaApplicativa = false;
			DatiServizioApplicativoErogatore datiApplicativiErogatore = null;
			if(Tipologia.INTERNA.equals(erogazione.getTipologia())){
				
				createPortaApplicativa = true;
				
				if(erogazione.getServizioApplicativo()==null){
					throw new ProtocolException("Servizio Applicativo (Nome o DatiApplicativi) non indicati. Questi dati sono obbligatori per una erogazione di tipologia 'interna'");
				}
				
				datiApplicativiErogatore = new DatiServizioApplicativoErogatore();
				datiApplicativiErogatore.setNome(erogazione.getServizioApplicativo().getNome());
				datiApplicativiErogatore.setNomePortaApplicativa(erogazione.getServizioApplicativo().getNomePortaApplicativa());
								
				if(datiApplicativiErogatore.getNome()!=null){
					boolean existsSA = this.existsServizioApplicativo(archive, configIntegrationReader, erogazione.getSoggettoErogatore().getIdSoggetto(), datiApplicativiErogatore.getNome());
					if(!existsSA){
						if(erogazione.getServizioApplicativo().getDatiApplicativi()==null){
							throw new ProtocolException("Servizio Applicativo (DatiApplicativi) non indicati. Questi dati sono obbligatori per una erogazione di tipologia 'interna', quando viene indicato un servizio applicativo non esistente");
						}
						if(erogazione.getServizioApplicativo().getDatiApplicativi().getEndpoint()==null){
							throw new ProtocolException("Servizio Applicativo (Endpoint) non indicato. Questo dato è obbligatorio per una erogazione di tipologia 'interna', quando viene indicato un servizio applicativo non esistente");
						}
						createApplicativoErogatore = true;
					}
					else{
						// Se cmq ho indicato dei dati applicativi, allora magari desidero fare un update
						if(erogazione.getServizioApplicativo().getDatiApplicativi()!=null &&
								erogazione.getServizioApplicativo().getDatiApplicativi().getEndpoint()!=null){
							createApplicativoErogatore = true;
						}
					}
				}
				else{
					if(erogazione.getServizioApplicativo().getDatiApplicativi()==null){
						throw new ProtocolException("Servizio Applicativo (Nome o DatiApplicativi) non indicati. Questi dati sono obbligatori per una erogazione di tipologia 'interna'");
					}
					if(erogazione.getServizioApplicativo().getDatiApplicativi().getEndpoint()==null){
						throw new ProtocolException("Servizio Applicativo (Endpoint) non indicato. Questo dato è obbligatorio per una erogazione di tipologia 'interna', quando non viene indicato un servizio applicativo");
					}
					createApplicativoErogatore = true;
				}
					
				if(createApplicativoErogatore){
					datiApplicativiErogatore.setEndpoint(erogazione.getServizioApplicativo().getDatiApplicativi().getEndpoint());
					if(erogazione.getServizioApplicativo().getDatiApplicativi().getCredenzialiBasic()!=null){
						datiApplicativiErogatore.setUsername(erogazione.getServizioApplicativo().getDatiApplicativi().getCredenzialiBasic().getUsername());
						datiApplicativiErogatore.setPassword(erogazione.getServizioApplicativo().getDatiApplicativi().getCredenzialiBasic().getPassword());
					}
				}
				
			}
			else{
				if(erogazione.getServizioApplicativo()!=null){
					throw new ProtocolException("Servizio Applicativo (Nomi o DatiApplicativi) indicati in una erogazione di tipologia 'esterna'. Tale tipologia non prevede la configurazione di una PA e di un ServizioApplicativo");
				}
			}
			
			// Dati Soggetto
			boolean createSoggetto = false;
			boolean createPdd = false;
			DatiSoggetto datiSoggetto = new DatiSoggetto();
			datiSoggetto.setId(soggettoErogatore);
			if(this.existsSoggetto(archive, registryReader, erogazione.getSoggettoErogatore().getIdSoggetto()) == false){
				if(erogazione.getSoggettoErogatore().getNotExistsBehaviour()==null || erogazione.getSoggettoErogatore().getNotExistsBehaviour().isCreate()==false){
					throw new ProtocolException("Soggetto Erogatore ["+soggettoErogatore.getTipo()+"/"+soggettoErogatore.getNome()+"] non esistente");
				}
				else{
					
					createSoggetto = true;
					
					datiSoggetto.setEndpoint(erogazione.getSoggettoErogatore().getNotExistsBehaviour().getEndpoint());
					datiSoggetto.setPortaDominio(erogazione.getSoggettoErogatore().getNotExistsBehaviour().getPortaDominio());
					
					if(datiSoggetto.getPortaDominio()!=null){
						if(this.existsPdd(archive, registryReader, datiSoggetto.getPortaDominio())){
							if(Tipologia.INTERNA.equals(erogazione.getTipologia())){
								if(this.isPddOperativa(archive, registryReader, datiSoggetto.getPortaDominio())==false){
									throw new ProtocolException("La pdd ["+datiSoggetto.getPortaDominio()+
											"] indicata per il Soggetto Erogatore ["+soggettoErogatore.getTipo()+"/"+soggettoErogatore.getNome()+"] "
													+ "possiede un tipo 'esterno' non compatibile con una erogazione di tipologia 'interna'. Deve essere associata una pdd di tipo 'operativo'");
								}
							}
							else{
								if(this.isPddOperativa(archive, registryReader, datiSoggetto.getPortaDominio())){
									throw new ProtocolException("La pdd ["+datiSoggetto.getPortaDominio()+
											"] indicata per il Soggetto Erogatore ["+soggettoErogatore.getTipo()+"/"+soggettoErogatore.getNome()+"] "
													+ "possiede un tipo 'operativo' non compatibile con una erogazione di tipologia 'esterna'. Deve essere associata una pdd di tipo 'esterno'");
								}
							}
						}
						else{
							if(Tipologia.INTERNA.equals(erogazione.getTipologia())){
								throw new ProtocolException("PdD ["+datiSoggetto.getPortaDominio()+"] non esistente (E' obbligatorio fornire una PdD esistente di tipo 'operativo' se la tipologia di erogazione è 'interna')");
							}
							createPdd = true;
						}
					}
					else{
						if(Tipologia.INTERNA.equals(erogazione.getTipologia())){
							datiSoggetto.setPortaDominio(this.getPddOperativa(registryReader));
						}else{
							createPdd = true; // creo pdd esterna.
						}
					}
					
				}
			}
					
			
			// Identificativo Unico di Correlazione
			ArchiveIdCorrelazione idCorrelazione = generateIdCorrelazione(soggettoErogatore.getTipo(), soggettoErogatore.getNome(), 
					datiServizio.getTipo(), datiServizio.getNome(),
					erogazione.getDescrizione());
			
			// Creazione mappa per FreeMarker
			Map<String, Object> data = new HashMap<>();
			data.put(CostantiAbstraction.EROGAZIONE_MAP_KEY_SERVIZIO, datiServizio);
			data.put(CostantiAbstraction.EROGAZIONE_MAP_KEY_SOGGETTO, datiSoggetto);
			if(datiApplicativiErogatore!=null){
				data.put(CostantiAbstraction.EROGAZIONE_MAP_KEY_APPLICATIVO_EROGATORE, datiApplicativiErogatore);
			}
			
			
			// Creazione Pdd e Soggetti
			if(createPdd){
				byte[]xml = TemplateUtils.toByteArray(templateErogazione.getTemplatePdd(), data);
				try{
					this.filler.readPortaDominio(archive, new ByteArrayInputStream(xml), xml, "pdd", validationDocuments, idCorrelazione);
				}catch(Exception e){
					throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
				}
			}
			if(createSoggetto){
				byte[]xml = TemplateUtils.toByteArray(templateErogazione.getTemplateSoggetto(), data);
				try{
					this.filler.readSoggetto(archive,new ByteArrayInputStream(xml), xml, "soggetto", 
							soggettoErogatore.getTipo(), soggettoErogatore.getNome(), validationDocuments, idCorrelazione);
				}catch(Exception e){
					throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
				}
			}
			
			// Creazione Accordi di Servizio Parte Specifica
			for (int i = 0; i < templateErogazione.getTemplateAccordiParteSpecifica().size(); i++) {
				byte[]xml = TemplateUtils.toByteArray(templateErogazione.getTemplateAccordiParteSpecifica().get(i), data);
				try{
					//String fileSystemName = this.filler.convertCharNonPermessiQualsiasiSistemaOperativo(datiServizio.getNome(),false);
					String tipo = datiServizio.getTipo();
					String nome = datiServizio.getNome();
					this.filler.readAccordoServizioParteSpecifica(archive, new ByteArrayInputStream(xml), xml,
						"asps_"+i, soggettoErogatore.getTipo(), soggettoErogatore.getNome(), "asps_"+i, 
						tipo, nome, ZIPReadUtils.USE_VERSION_XML_BEAN, validationDocuments, idCorrelazione,
						ArchiveVersion.V_1,null,null);
				}catch(Exception e){
					throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
				}
			}
			
			// Creazione Fruitori
			if(erogazione.getServizio()!=null && 
					erogazione.getServizio().getFruitori()!=null &&
					erogazione.getServizio().getFruitori().sizeFruitoreList()>0){
				for (int i = 0; i < erogazione.getServizio().getFruitori().sizeFruitoreList(); i++) {
					Soggetto fruitore = erogazione.getServizio().getFruitori().getFruitore(i);
					IdSoggetto s = new IdSoggetto();
					s.setTipo(fruitore.getTipo());
					s.setNome(fruitore.getNome());
					data.remove(CostantiAbstraction.EROGAZIONE_MAP_KEY_FRUITORE);
					data.put(CostantiAbstraction.EROGAZIONE_MAP_KEY_FRUITORE, s);
					
					byte[]xml = TemplateUtils.toByteArray(templateErogazione.getTemplateFruitore(), data);
					try{
						this.filler.readAccordoServizioParteSpecifica_Fruitore(archive, new ByteArrayInputStream(xml), xml, 
								"fruitore_"+i, "asps_"+i, soggettoErogatore.getTipo(), soggettoErogatore.getNome(), 
								tipoServizio,nomeServizio, ZIPReadUtils.USE_VERSION_XML_BEAN, validationDocuments, idCorrelazione,
								ArchiveVersion.V_1,null);
					}catch(Exception e){
						throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
					}
				}
			}
			data.remove(CostantiAbstraction.EROGAZIONE_MAP_KEY_FRUITORE);
			
			// Creazione SA
			if(createApplicativoErogatore){
				for (int i = 0; i < templateErogazione.getTemplateServiziApplicativi().size(); i++) {
					byte[]xml = TemplateUtils.toByteArray(templateErogazione.getTemplateServiziApplicativi().get(i), data);
					try{
						this.filler.readServizioApplicativo(archive, new ByteArrayInputStream(xml), xml, 
							"sa_"+i, soggettoErogatore.getTipo(), soggettoErogatore.getNome(), validationDocuments, idCorrelazione);
					}catch(Exception e){
						throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
					}
				}
			}
			
			// Creazione PortaApplicativa
			if(createPortaApplicativa){
				for (int i = 0; i < templateErogazione.getTemplatePorteApplicative().size(); i++) {
					byte[]xml = TemplateUtils.toByteArray(templateErogazione.getTemplatePorteApplicative().get(i), data);
					try{
						this.filler.readPortaApplicativa(archive, new ByteArrayInputStream(xml), xml, 
							"pa_"+i, soggettoErogatore.getTipo(), soggettoErogatore.getNome(), validationDocuments, idCorrelazione);
					}catch(Exception e){
						throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
					}
				}
			}
			
			// Creazione Mapping Erogazione
			for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
				ArchivePortaApplicativa aPA = archive.getPorteApplicative().get(i);
				if(idCorrelazione.equals(aPA.getIdCorrelazione())){
					// Devo aggiungerlo in tutte le erogazioni la porta applicativa
					for (int j = 0; j < archive.getAccordiServizioParteSpecifica().size(); j++) {
						ArchiveAccordoServizioParteSpecifica aASPS = archive.getAccordiServizioParteSpecifica().get(j);
						if(idCorrelazione.equals(aASPS.getIdCorrelazione())){
								
							MappingErogazionePortaApplicativa mapping = new MappingErogazionePortaApplicativa();
							mapping.setNome("regola_"+i+"_"+aPA.getIdPortaApplicativa().getNome());
							mapping.setIdServizio(aASPS.getIdAccordoServizioParteSpecifica());
							mapping.setIdPortaApplicativa(aPA.getIdPortaApplicativa());
							mapping.setDefault(aPA.getPortaApplicativa().getAzione()==null || 
									!PortaApplicativaAzioneIdentificazione.DELEGATED_BY.equals(aPA.getPortaApplicativa().getAzione().getIdentificazione()));
							
							if(aASPS.getMappingPorteApplicativeAssociate()==null) {
								aASPS.setMappingPorteApplicativeAssociate(new ArrayList<>());
							}
							aASPS.getMappingPorteApplicativeAssociate().add(mapping);
						}
					}
				}
			}
			
			return idCorrelazione;
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
		
	}
	

	
}
