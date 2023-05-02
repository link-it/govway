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

import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.protocol.abstraction.Fruizione;
import org.openspcoop2.protocol.abstraction.constants.Autenticazione;
import org.openspcoop2.protocol.abstraction.constants.CostantiAbstraction;
import org.openspcoop2.protocol.abstraction.constants.Tipologia;
import org.openspcoop2.protocol.abstraction.template.DatiFruizione;
import org.openspcoop2.protocol.abstraction.template.DatiServizio;
import org.openspcoop2.protocol.abstraction.template.DatiServizioApplicativoFruitore;
import org.openspcoop2.protocol.abstraction.template.DatiSoggetto;
import org.openspcoop2.protocol.abstraction.template.IdSoggetto;
import org.openspcoop2.protocol.abstraction.template.TemplateFruizione;
import org.openspcoop2.protocol.basic.archive.ZIPReadUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.constants.ArchiveVersion;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.utils.ManagerUtils;
import org.openspcoop2.utils.RandomString;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.TemplateUtils;
import org.slf4j.Logger;

/**     
 * FruizioneConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FruizioneConverter extends AbstractConverter {

	// ----- Static method -----
	
	public static synchronized ArchiveIdCorrelazione generateIdCorrelazione(
			String tipoSoggettoFruitore,String nomeSoggettoFruitore,
			String tipoSoggettoErogatore,String nomeSoggettoErogatore,
			String tipoServizio, String nomeServizio,
			String descrizione) throws ProtocolException{
		
		// Identificativo Unico di Correlazione
		String uuid = "Fruizione_"+
				tipoSoggettoFruitore+"/"+nomeSoggettoFruitore+"_"+
				tipoSoggettoErogatore+"/"+nomeSoggettoErogatore+"_"+
				tipoServizio+"/"+nomeServizio+"_"+System.currentTimeMillis();
		
		Utilities.sleep(1); // per rendere univco il prossimo uuid
	
		ArchiveIdCorrelazione idCorrelazione = new ArchiveIdCorrelazione(uuid);
		
		String d = null;
		if(descrizione!=null && !"".equals(descrizione)){
			d = descrizione;
		}
		else{
			d = "Fruizione "+tipoSoggettoFruitore+"/"+nomeSoggettoFruitore+" -> (servizio:"+tipoServizio+"/"+nomeServizio+" erogatore:"+tipoSoggettoErogatore+"/"+nomeSoggettoErogatore+")";
		}
		idCorrelazione.setDescrizione(d);

		return idCorrelazione;
	}
	
	
	
	
	
	
	// ----- Instance method -----

	public FruizioneConverter(Logger log,ZIPReadUtils zipReader) throws ProtocolException{
		super(log, zipReader);
	}
	
	// ritorna l'identificativo di correlazione.
	public ArchiveIdCorrelazione fillArchive(Archive archive, Fruizione fruizione, TemplateFruizione templateFruizione,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, boolean validationDocuments) throws ProtocolException{
		
		try{
			
			// Cerco identificare accordo di servizio parte speicfica
			IDServizio idAccordoServizioParteSpecifica = null;
			if(fruizione.getAccordoServizioParteSpecifica().getUri()!=null){
				idAccordoServizioParteSpecifica = this.idServizioFactory.getIDServizioFromUri(fruizione.getAccordoServizioParteSpecifica().getUri());
				if(idAccordoServizioParteSpecifica.getVersione()==null){
					// forzo il default a 1
					idAccordoServizioParteSpecifica = this.idServizioFactory.getIDServizioFromValues(idAccordoServizioParteSpecifica.getTipo(), idAccordoServizioParteSpecifica.getNome(), 
							idAccordoServizioParteSpecifica.getSoggettoErogatore(), 1);
				}
				if(this.existsAccordoServizioParteSpecifica(archive, registryReader, idAccordoServizioParteSpecifica)==false){
					throw new ProtocolException("Accordo di Servizio Parte Specifica ["+idAccordoServizioParteSpecifica.toString()+"] non esistente");
				}
			}
			else {
				if(fruizione.getAccordoServizioParteSpecifica().getIdServizio().getSoggetto()!=null && 
						fruizione.getAccordoServizioParteSpecifica().getIdServizio().getTipo()!=null){
					idAccordoServizioParteSpecifica = this.idServizioFactory.getIDServizioFromValues(fruizione.getAccordoServizioParteSpecifica().getIdServizio().getSoggetto().getTipo(), 
							fruizione.getAccordoServizioParteSpecifica().getIdServizio().getSoggetto().getNome(),
							fruizione.getAccordoServizioParteSpecifica().getIdServizio().getTipo(),
							fruizione.getAccordoServizioParteSpecifica().getIdServizio().getNome(), 
							1);
					if(this.existsAccordoServizioParteSpecifica(archive, registryReader, idAccordoServizioParteSpecifica)==false){
						throw new ProtocolException("Accordo di Servizio Parte Specifica ["+idAccordoServizioParteSpecifica.toString()+"] non esistente");
					}
				}
				else{
					idAccordoServizioParteSpecifica = this.findIdAccordoServizioParteSpecifica(archive, registryReader, 
							fruizione.getAccordoServizioParteSpecifica().getIdServizio().getNome(), 
							fruizione.getAccordoServizioParteSpecifica().getIdServizio().getTipo(), 
							1,
							fruizione.getAccordoServizioParteSpecifica().getIdServizio().getSoggetto());
				}
			}
			AccordoServizioParteSpecifica asps = this.getAccordoServizioParteSpecifica(archive, registryReader, idAccordoServizioParteSpecifica);
			if(asps==null){
				throw new ProtocolException("Accordo di Servizio Parte Specifica ["+idAccordoServizioParteSpecifica.toString()+"] non esistente ?");
			}
			
			// IdSoggetto
			IdSoggetto soggettoErogatore = new IdSoggetto();
			soggettoErogatore.setTipo(idAccordoServizioParteSpecifica.getSoggettoErogatore().getTipo());
			soggettoErogatore.setNome(idAccordoServizioParteSpecifica.getSoggettoErogatore().getNome());
			
			// Protocollo
			@SuppressWarnings("unused")
			String protocollo = ManagerUtils.getProtocolByOrganizationType(soggettoErogatore.getTipo());
						
			// Dati Servizio
			DatiServizio datiServizio = new DatiServizio();
			datiServizio.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
			datiServizio.setPortType(asps.getPortType());
			datiServizio.setTipo(idAccordoServizioParteSpecifica.getTipo());
			datiServizio.setNome(idAccordoServizioParteSpecifica.getNome());
			datiServizio.setErogatore(soggettoErogatore);
			
			// Dati Fruizione
			IdSoggetto soggettoFruitore = new IdSoggetto();
			soggettoFruitore.setTipo(fruizione.getSoggettoFruitore().getIdSoggetto().getTipo());
			soggettoFruitore.setNome(fruizione.getSoggettoFruitore().getIdSoggetto().getNome());
			DatiFruizione datiFruizione = new DatiFruizione();
			datiFruizione.setSoggetto(soggettoFruitore);
			if(fruizione.getFruizione()!=null){
				if(fruizione.getFruizione().getClientAuth()!=null){
					datiFruizione.setClientAuth(fruizione.getFruizione().getClientAuth().getValue());
				}
				datiFruizione.setEndpoint(fruizione.getFruizione().getEndpoint());
			}

			// Dati Applicativo Fruitore
			boolean createApplicativoFruitore = false;
			boolean createPortaDelegata = false;
			DatiServizioApplicativoFruitore datiApplicativoFruitore = null;
			if(Tipologia.INTERNA.equals(fruizione.getTipologia())){
				
				createPortaDelegata = true;
				
				if(fruizione.getServizioApplicativo()==null){
					throw new ProtocolException("Servizio Applicativo (Nome o DatiApplicativi) non indicati. Questi dati sono obbligatori per una fruizione di tipologia 'interna'");
				}
				
				datiApplicativoFruitore = new DatiServizioApplicativoFruitore();
				datiApplicativoFruitore.setNome(fruizione.getServizioApplicativo().getNome());
				datiApplicativoFruitore.setNomePortaDelegata(fruizione.getServizioApplicativo().getNomePortaDelegata());
				
				if(datiApplicativoFruitore.getNome()!=null){
					boolean existsSA = this.existsServizioApplicativo(archive, configIntegrationReader, fruizione.getSoggettoFruitore().getIdSoggetto(), datiApplicativoFruitore.getNome());
					if(!existsSA){
						if(fruizione.getServizioApplicativo().getDatiApplicativi()==null){
							throw new ProtocolException("Servizio Applicativo (DatiApplicativi) non indicati. Questi dati sono obbligatori per una fruizione di tipologia 'interna', quando viene indicato un servizio applicativo non esistente");
						}
						if(fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione()==null){
							throw new ProtocolException("Servizio Applicativo (Autenticazione) non indicato. Questo dato è obbligatorio per una fruizione di tipologia 'interna', quando viene indicato un servizio applicativo non esistente");
						}
						datiApplicativoFruitore.setAutenticazione(fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione().getValue());
						if(Autenticazione.NONE.equals(fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione())==false){
							createApplicativoFruitore = true;
						}
					}
					else{
						// Se cmq ho indicato dei dati applicativi, allora magari desidero fare un update
						if(fruizione.getServizioApplicativo().getDatiApplicativi()!=null &&
								fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione() != null	){
							datiApplicativoFruitore.setAutenticazione(fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione().getValue());
							if(Autenticazione.NONE.equals(fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione())==false){
								createApplicativoFruitore = true;
							}
						}
						else{
							datiApplicativoFruitore.setAutenticazione(this.getTipoCredenzialeServizioApplicativo(archive, configIntegrationReader, 
									fruizione.getSoggettoFruitore().getIdSoggetto(), datiApplicativoFruitore.getNome()));
						}
					}
				}
				else{
					if(fruizione.getServizioApplicativo().getDatiApplicativi()==null){
						throw new ProtocolException("Servizio Applicativo (Nome o DatiApplicativi) non indicati. Questi dati sono obbligatori per una fruizione di tipologia 'interna'");
					}
					if(fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione()==null){
						throw new ProtocolException("Servizio Applicativo (Autenticazione) non indicato. Questo dato è obbligatorio per una fruizione di tipologia 'interna', quando non viene indicato un servizio applicativo");
					}
					datiApplicativoFruitore.setAutenticazione(fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione().getValue());
					if(Autenticazione.NONE.equals(fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione())==false){
						createApplicativoFruitore = true;
					}
				}
				
				if(createApplicativoFruitore){
					
					if(Autenticazione.SSL.equals(fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione())){
						if(fruizione.getServizioApplicativo().getDatiApplicativi().getSslSubject()==null){
							throw new ProtocolException("Ssl Subject non indicato per la fruizione, nonostante sia indicata una autenticazione di tipo 'ssl'");
						}
						if(fruizione.getServizioApplicativo().getDatiApplicativi().getBasicUsername()!=null){
							throw new ProtocolException("Basic Username non deve essere indicato per la fruizione. E' stata impostata una autenticazione di tipo 'ssl'");
						}
						if(fruizione.getServizioApplicativo().getDatiApplicativi().getBasicPassword()!=null){
							throw new ProtocolException("Basic Password non deve essere indicato per la fruizione. E' stata impostata una autenticazione di tipo 'ssl'");
						}
						datiApplicativoFruitore.setSslSubject(fruizione.getServizioApplicativo().getDatiApplicativi().getSslSubject());
					}
					else if(Autenticazione.BASIC.equals(fruizione.getServizioApplicativo().getDatiApplicativi().getAutenticazione())){
						if(fruizione.getServizioApplicativo().getDatiApplicativi().getSslSubject()!=null){
							throw new ProtocolException("Ssl Subject non deve essere indicato per la fruizione. E' stata impostata una autenticazione di tipo 'basic'");
						}
						datiApplicativoFruitore.setBasicUsername(fruizione.getServizioApplicativo().getDatiApplicativi().getBasicUsername());
						datiApplicativoFruitore.setBasicPassword(fruizione.getServizioApplicativo().getDatiApplicativi().getBasicPassword());
						if(datiApplicativoFruitore.getBasicPassword()==null){
							RandomString randomString = new RandomString(10);
							datiApplicativoFruitore.setBasicPassword(randomString.nextString());
						}
					}
				}
	
			}
			else{
				if(fruizione.getServizioApplicativo()!=null){
					throw new ProtocolException("Servizio Applicativo (Nomi o DatiApplicativi) indicati in una fruizione di tipologia 'esterna'. Tale tipologia non prevede la configurazione di una PD e di un ServizioApplicativo");
				}
			}
			
			// Dati Soggetto
			boolean createSoggetto = false;
			boolean createPdd = false;
			DatiSoggetto datiSoggetto = new DatiSoggetto();
			datiSoggetto.setId(soggettoFruitore);
			if(this.existsSoggetto(archive, registryReader, fruizione.getSoggettoFruitore().getIdSoggetto()) == false){
				if(fruizione.getSoggettoFruitore().getNotExistsBehaviour()==null || fruizione.getSoggettoFruitore().getNotExistsBehaviour().isCreate()==false){
					throw new ProtocolException("Soggetto Fruitore ["+soggettoFruitore.getTipo()+"/"+soggettoFruitore.getNome()+"] non esistente");
				}
				else{
					
					createSoggetto = true;
					
					datiSoggetto.setEndpoint(fruizione.getSoggettoFruitore().getNotExistsBehaviour().getEndpoint());
					datiSoggetto.setPortaDominio(fruizione.getSoggettoFruitore().getNotExistsBehaviour().getPortaDominio());
					
					if(datiSoggetto.getPortaDominio()!=null){
						if(this.existsPdd(archive, registryReader, datiSoggetto.getPortaDominio())){
							if(Tipologia.INTERNA.equals(fruizione.getTipologia())){
								if(this.isPddOperativa(archive, registryReader, datiSoggetto.getPortaDominio())==false){
									throw new ProtocolException("La pdd ["+datiSoggetto.getPortaDominio()+
											"] indicata per il Soggetto Fruitore ["+soggettoFruitore.getTipo()+"/"+soggettoFruitore.getNome()+"] "
													+ "possiede un tipo 'esterno' non compatibile con una fruizione di tipologia 'interna'. Deve essere associata una pdd di tipo 'operativo'");
								}
							}
							else{
								if(this.isPddOperativa(archive, registryReader, datiSoggetto.getPortaDominio())){
									throw new ProtocolException("La pdd ["+datiSoggetto.getPortaDominio()+
											"] indicata per il Soggetto Fruitore ["+soggettoFruitore.getTipo()+"/"+soggettoFruitore.getNome()+"] "
													+ "possiede un tipo 'operativo' non compatibile con una fruizione di tipologia 'esterna'. Deve essere associata una pdd di tipo 'esterno'");
								}
							}
						}
						else{
							if(Tipologia.INTERNA.equals(fruizione.getTipologia())){
								throw new ProtocolException("PdD ["+datiSoggetto.getPortaDominio()+"] non esistente (E' obbligatorio fornire una PdD esistente di tipo 'operativo' se la tipologia di fruizione è 'interna')");
							}
							createPdd = true;
						}
					}
					else{
						if(Tipologia.INTERNA.equals(fruizione.getTipologia())){
							datiSoggetto.setPortaDominio(this.getPddOperativa(registryReader));
						}else{
							createPdd = true; // creo pdd esterna.
						}
					}
					
				}
			}
					
			
			// Identificativo Unico di Correlazione
			ArchiveIdCorrelazione idCorrelazione = generateIdCorrelazione(soggettoFruitore.getTipo(),soggettoFruitore.getNome(),
					soggettoErogatore.getTipo(), soggettoErogatore.getNome(), datiServizio.getTipo(), datiServizio.getNome(),
					fruizione.getDescrizione());
			
			// Creazione mappa per FreeMarker
			Map<String, Object> data = new HashMap<>();
			data.put(CostantiAbstraction.EROGAZIONE_MAP_KEY_SERVIZIO, datiServizio);
			data.put(CostantiAbstraction.EROGAZIONE_MAP_KEY_SOGGETTO, datiSoggetto);
			data.put(CostantiAbstraction.EROGAZIONE_MAP_KEY_FRUIZIONE, datiFruizione);
			data.put(CostantiAbstraction.EROGAZIONE_MAP_KEY_FRUITORE, soggettoFruitore); // metto entrambi in modo da averli a disposizione nel template
			if(datiApplicativoFruitore!=null){
				data.put(CostantiAbstraction.EROGAZIONE_MAP_KEY_APPLICATIVO_FRUITORE, datiApplicativoFruitore);
			}
			
			
			// Creazione Pdd e Soggetti
			if(createPdd){
				byte[]xml = TemplateUtils.toByteArray(templateFruizione.getTemplatePdd(), data);
				try{
					this.filler.readPortaDominio(archive, new ByteArrayInputStream(xml), xml, "pdd", validationDocuments, idCorrelazione);
				}catch(Exception e){
					throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
				}
			}
			if(createSoggetto){
				byte[]xml = TemplateUtils.toByteArray(templateFruizione.getTemplateSoggetto(), data);
				try{
					this.filler.readSoggetto(archive,new ByteArrayInputStream(xml), xml, "soggetto", 
							soggettoFruitore.getTipo(), soggettoFruitore.getNome(), validationDocuments, idCorrelazione);
				}catch(Exception e){
					throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
				}
			}
			
			// Creazione Fruizione
			byte[]xml = TemplateUtils.toByteArray(templateFruizione.getTemplateFruitore(), data);
			try{
				this.filler.readAccordoServizioParteSpecifica_Fruitore(archive, new ByteArrayInputStream(xml), xml, 
						"fruizione", "servizio", soggettoErogatore.getTipo(), soggettoErogatore.getNome(), 
						idAccordoServizioParteSpecifica.getTipo(), idAccordoServizioParteSpecifica.getNome(), idAccordoServizioParteSpecifica.getVersione()+"", 
						validationDocuments, idCorrelazione,
						ArchiveVersion.V_1,null);
			}catch(Exception e){
				throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
			}
			
			// Creazione SA
			if(createApplicativoFruitore){
				for (int i = 0; i < templateFruizione.getTemplateServiziApplicativi().size(); i++) {
					xml = TemplateUtils.toByteArray(templateFruizione.getTemplateServiziApplicativi().get(i), data);
					try{
						this.filler.readServizioApplicativo(archive, new ByteArrayInputStream(xml), xml, 
							"sa_"+i, soggettoFruitore.getTipo(), soggettoFruitore.getNome(), validationDocuments, idCorrelazione);
					}catch(Exception e){
						throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
					}
				}
			}
						
			// Creazione PortaDelegata
			if(createPortaDelegata){
				for (int i = 0; i < templateFruizione.getTemplatePorteDelegate().size(); i++) {
					xml = TemplateUtils.toByteArray(templateFruizione.getTemplatePorteDelegate().get(i), data);
					try{
						this.filler.readPortaDelegata(archive, new ByteArrayInputStream(xml), xml, 
							"pd_"+i, soggettoFruitore.getTipo(), soggettoFruitore.getNome(), validationDocuments, idCorrelazione);
					}catch(Exception e){
						throw new Exception("XmlTemplate["+new String(xml)+"]\n"+e.getMessage(),e);
					}
				}
			}
			
			// Creazione Mapping Fruizione
			for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
				ArchivePortaDelegata aPD = archive.getPorteDelegate().get(i);
				if(idCorrelazione.equals(aPD.getIdCorrelazione())){
					// Devo aggiungere in tutte le fruizioni la porta delegata
					for (int j = 0; j < archive.getAccordiFruitori().size(); j++) {
						ArchiveFruitore aFruitore = archive.getAccordiFruitori().get(j);
						if(idCorrelazione.equals(aFruitore.getIdCorrelazione())){
							
							MappingFruizionePortaDelegata mapping = new MappingFruizionePortaDelegata();
							mapping.setNome("regola_"+i+"_"+aPD.getIdPortaDelegata().getNome());
							mapping.setIdServizio(aFruitore.getIdAccordoServizioParteSpecifica());
							mapping.setIdFruitore(aFruitore.getIdSoggettoFruitore());
							mapping.setIdPortaDelegata(aPD.getIdPortaDelegata());
							mapping.setDefault(aPD.getPortaDelegata().getAzione()==null || 
									!PortaDelegataAzioneIdentificazione.DELEGATED_BY.equals(aPD.getPortaDelegata().getAzione().getIdentificazione()));
							
							if(aFruitore.getMappingPorteDelegateAssociate()==null) {
								aFruitore.setMappingPorteDelegateAssociate(new ArrayList<>());
							}
							aFruitore.getMappingPorteDelegateAssociate().add(mapping);
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
