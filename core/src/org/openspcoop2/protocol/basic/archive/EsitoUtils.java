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

package org.openspcoop2.protocol.basic.archive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveActivePolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveConfigurationPolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImport;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetail;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetailConfigurazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveGruppo;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveMappingErogazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveMappingFruizione;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveRuolo;
import org.openspcoop2.protocol.sdk.archive.ArchiveScope;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.ArchiveTokenPolicy;
import org.openspcoop2.protocol.sdk.constants.ArchiveStatoImport;

/**
 * EsitoUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoUtils {

	protected IProtocolFactory<?> protocolFactory = null;
	protected IDAccordoCooperazioneFactory idAccordoCooperazioneFactory;
	protected IDAccordoFactory idAccordoFactory;
	protected IDServizioFactory idServizioFactory;
	public EsitoUtils(IProtocolFactory<?> protocolFactory){
		this.protocolFactory = protocolFactory;
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idAccordoFactory = IDAccordoFactory.getInstance();
		this.idServizioFactory = IDServizioFactory.getInstance();
	}
	
	public String toString(ArchiveEsitoImport archive, boolean readIdCorrelazione, boolean importOperation) throws ProtocolException{
		
		if(!readIdCorrelazione){
			return this._toString(archive,importOperation);
		}
		
		Hashtable<String, ArchiveEsitoImport> map = new Hashtable<String, ArchiveEsitoImport>();
		Hashtable<String, ArchiveIdCorrelazione> mapIdCorrelazione = new Hashtable<String, ArchiveIdCorrelazione>();
		this.readIdCorrelazione(map, mapIdCorrelazione, archive);
		
		if(map.size()<1){
			return "";
		}
		
		if(map.size()==1){
			// in caso di una unica correlazione non riporto comunque il raggruppamento se è quella di default
			String idCorrelazione = map.keys().nextElement();
			if(ZIPUtils.ID_CORRELAZIONE_DEFAULT.equals(idCorrelazione)){
				return this._toString(archive,importOperation);
			}
		}
		
		StringBuilder bfEsito = new StringBuilder();
		
		ArchiveEsitoImport defaultArchiveEsitoImport = null;
		ArchiveIdCorrelazione defaultArchiveIdCorrelazione = null;
		
		Enumeration<String> correlazioni = map.keys();
		List<String> idCorrelazioneOrdinato = new ArrayList<String>();
		while (correlazioni.hasMoreElements()) {
			String idCorrelazione = (String) correlazioni.nextElement();
			idCorrelazioneOrdinato.add(idCorrelazione);
		}
		Collections.sort(idCorrelazioneOrdinato);
		
		for (String idCorrelazione : idCorrelazioneOrdinato) {
			ArchiveIdCorrelazione archiveIdCorrelazione = mapIdCorrelazione.get(idCorrelazione);
			ArchiveEsitoImport archiveCorrelazione = map.get(idCorrelazione);
		
			if(ZIPUtils.ID_CORRELAZIONE_DEFAULT.equals(idCorrelazione)){
				defaultArchiveEsitoImport = archiveCorrelazione;
				defaultArchiveIdCorrelazione = archiveIdCorrelazione;
				continue;
			}
			
			this.append(bfEsito, archiveIdCorrelazione, archiveCorrelazione, importOperation);
			
		}
		
		if(defaultArchiveEsitoImport!=null){
			ArchiveIdCorrelazione i = new ArchiveIdCorrelazione(defaultArchiveIdCorrelazione.getId());
			i.setDescrizione("       Altro      ");
			this.append(bfEsito, i, defaultArchiveEsitoImport, importOperation);
		}
		
		return bfEsito.toString();
		
	}
	private void append(StringBuilder bfEsito, ArchiveIdCorrelazione archiveIdCorrelazione, ArchiveEsitoImport archiveCorrelazione, boolean importOperation){
		
		String descrizione = archiveIdCorrelazione.getDescrizione();
		if(descrizione==null){
			descrizione = archiveIdCorrelazione.getId();
		}
		
		for (int i = 0; i < descrizione.length(); i++) {
			bfEsito.append("=");
		}
		bfEsito.append("\n");
		bfEsito.append(descrizione);
		bfEsito.append("\n");
		for (int i = 0; i < descrizione.length(); i++) {
			bfEsito.append("=");
		}
		bfEsito.append("\n");
		bfEsito.append("\n");
		bfEsito.append(this._toString(archiveCorrelazione,importOperation));
		bfEsito.append("\n\n");
		
	}
			
	private String _toString(ArchiveEsitoImport archive, boolean importOperation){
		
		StringBuilder bfEsito = new StringBuilder();
		
		String labelNonErrore = "non importato";
		if(!importOperation) {
			labelNonErrore = "non eliminato";
		}
		
		// Pdd
		if(archive.getPdd().size()>0){
			bfEsito.append("Gateway (").append(archive.getPdd().size()).append(")\n");
		}
		for (int i = 0; i < archive.getPdd().size(); i++) {
			try{
				ArchiveEsitoImportDetail archivePdd = archive.getPdd().get(i);
				String nomePdd = ((ArchivePdd)archivePdd.getArchiveObject()).getNomePdd();
				bfEsito.append("\t- [").append(nomePdd).append("] ");
				serializeStato(archivePdd, bfEsito, importOperation);
			}catch(Throwable e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getPdd().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Tags
		if(archive.getGruppi().size()>0){
			bfEsito.append("Tags (").append(archive.getGruppi().size()).append(")\n");
		}
		for (int i = 0; i < archive.getGruppi().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveGruppo = archive.getGruppi().get(i);
				String nomeGruppo = ((ArchiveGruppo)archiveGruppo.getArchiveObject()).getIdGruppo().getNome();
				bfEsito.append("\t- [").append(nomeGruppo).append("] ");
				serializeStato(archiveGruppo, bfEsito, importOperation);
			}catch(Throwable e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getGruppi().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Ruoli
		if(archive.getRuoli().size()>0){
			bfEsito.append("Ruoli (").append(archive.getRuoli().size()).append(")\n");
		}
		for (int i = 0; i < archive.getRuoli().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveRuolo = archive.getRuoli().get(i);
				String nomeRuolo = ((ArchiveRuolo)archiveRuolo.getArchiveObject()).getIdRuolo().getNome();
				bfEsito.append("\t- [").append(nomeRuolo).append("] ");
				serializeStato(archiveRuolo, bfEsito, importOperation);
			}catch(Throwable e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getRuoli().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Scope
		if(archive.getScope().size()>0){
			bfEsito.append("Scope (").append(archive.getScope().size()).append(")\n");
		}
		for (int i = 0; i < archive.getScope().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveScope = archive.getScope().get(i);
				String nomeScope = ((ArchiveScope)archiveScope.getArchiveObject()).getIdScope().getNome();
				bfEsito.append("\t- [").append(nomeScope).append("] ");
				serializeStato(archiveScope, bfEsito, importOperation);
			}catch(Throwable e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getScope().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Soggetti
		if(archive.getSoggetti().size()>0){
			bfEsito.append("Soggetti (").append(archive.getSoggetti().size()).append(")\n");
		}
		for (int i = 0; i < archive.getSoggetti().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveSoggetto = archive.getSoggetti().get(i);
				IDSoggetto idSoggetto =((ArchiveSoggetto)archiveSoggetto.getArchiveObject()).getIdSoggetto();
				bfEsito.append("\t- [").append(idSoggetto.toString()).append("] ");
				serializeStato(archiveSoggetto, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getSoggetti().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Servizi Applicativi
		if(archive.getServiziApplicativi().size()>0){
			bfEsito.append("Applicativi Fruitori/Erogatori (").append(archive.getServiziApplicativi().size()).append(")\n");
		}
		for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveServizioApplicativo = archive.getServiziApplicativi().get(i);
				IDServizioApplicativo idServizioApplicativo = ((ArchiveServizioApplicativo)archiveServizioApplicativo.getArchiveObject()).getIdServizioApplicativo();
				bfEsito.append("\t- [").append(idServizioApplicativo.getIdSoggettoProprietario().toString()).
						append("_").append(idServizioApplicativo.getNome()).append("] ");
				serializeStato(archiveServizioApplicativo, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getServiziApplicativi().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Cooperazione
		if(archive.getAccordiCooperazione().size()>0){
			bfEsito.append("Accordi di Cooperazione (").append(archive.getAccordiCooperazione().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoCooperazione = archive.getAccordiCooperazione().get(i);
				IDAccordoCooperazione idAccordoCooperazione = ((ArchiveAccordoCooperazione)archiveAccordoCooperazione.getArchiveObject()).getIdAccordoCooperazione();
				String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordoCooperazione);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoCooperazione, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiCooperazione().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Parte Comune
		if(archive.getAccordiServizioParteComune().size()>0){
			bfEsito.append("API (").append(archive.getAccordiServizioParteComune().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteComune = archive.getAccordiServizioParteComune().get(i);
				IDAccordo idAccordo = ((ArchiveAccordoServizioParteComune)archiveAccordoServizioParteComune.getArchiveObject()).getIdAccordoServizioParteComune();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioParteComune, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiServizioParteComune().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Parte Specifica (implementano accordi di servizio parte comune)
		if(archive.getAccordiServizioParteSpecifica().size()>0){
			bfEsito.append("Servizi (").append(archive.getAccordiServizioParteSpecifica().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecifica().get(i);
				IDServizio idServizio = ((ArchiveAccordoServizioParteSpecifica)archiveAccordoServizioParteSpecifica.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idServizioFactory.getUriFromIDServizio(idServizio);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioParteSpecifica, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiServizioParteSpecifica().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Composto
		if(archive.getAccordiServizioComposto().size()>0){
			bfEsito.append("Accordi di Servizio Composto (").append(archive.getAccordiServizioComposto().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioComposto = archive.getAccordiServizioComposto().get(i);
				IDAccordo idAccordo = ((ArchiveAccordoServizioComposto)archiveAccordoServizioComposto.getArchiveObject()).getIdAccordoServizioParteComune();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioComposto, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiServizioComposto().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Parte Specifica (implementano accordi di servizio composto)
		if(archive.getAccordiServizioParteSpecificaServiziComposti().size()>0){
			bfEsito.append("Servizi (").
				append(archive.getAccordiServizioParteSpecificaServiziComposti().size()).append(") [accordi di servizio composto]\n");
		}
		for (int i = 0; i < archive.getAccordiServizioParteSpecificaServiziComposti().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecificaServiziComposti().get(i);
				IDServizio idServizio = ((ArchiveAccordoServizioParteSpecifica)archiveAccordoServizioParteSpecifica.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idServizioFactory.getUriFromIDServizio(idServizio);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioParteSpecifica, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiServizioParteSpecificaServiziComposti().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Fruitori
		if(archive.getAccordiFruitori().size()>0){
			bfEsito.append("Fruitori (").append(archive.getAccordiFruitori().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveFruitore = archive.getAccordiFruitori().get(i);
				IDServizio idServizio = ((ArchiveFruitore)archiveFruitore.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idServizioFactory.getUriFromIDServizio(idServizio);
				IDSoggetto idFruitore = ((ArchiveFruitore)archiveFruitore.getArchiveObject()).getIdSoggettoFruitore();
				bfEsito.append("\t- ["+idFruitore+"] -> [").append(uriAccordo).append("] ");
				serializeStato(archiveFruitore, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiFruitori().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// PorteDelegate
		if(archive.getPorteDelegate().size()>0){
			bfEsito.append("Porte Outbound (").append(archive.getPorteDelegate().size()).append(")\n");
		}
		for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
			try{
				ArchiveEsitoImportDetail archivePortaDelegata = archive.getPorteDelegate().get(i);
				IDPortaDelegata idPortaDelegata = ((ArchivePortaDelegata)archivePortaDelegata.getArchiveObject()).getIdPortaDelegata();
				IDSoggetto idProprietario = ((ArchivePortaDelegata)archivePortaDelegata.getArchiveObject()).getIdSoggettoProprietario();
				bfEsito.append("\t- ["+idProprietario+"]["+idPortaDelegata.getNome()+"] ");
				serializeStato(archivePortaDelegata, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
			
			// Si registrano solo gli errori per il check mapping (ci saranno solo in createUpdate)
			if(importOperation) {
				try{
					ArchiveEsitoImportDetail archivePortaDelegata = archive.getPorteDelegate_initMapping().get(i);
					if(ArchiveStatoImport.ERROR.equals(archivePortaDelegata.getState())){
						IDPortaDelegata idPortaDelegata = ((ArchivePortaDelegata)archivePortaDelegata.getArchiveObject()).getIdPortaDelegata();
						IDSoggetto idProprietario = ((ArchivePortaDelegata)archivePortaDelegata.getArchiveObject()).getIdSoggettoProprietario();
						bfEsito.append("\t- Fruizione ["+idProprietario+"]["+idPortaDelegata.getNome()+"] ");
						serializeStato(archivePortaDelegata, bfEsito, importOperation);
						bfEsito.append("\n");
					}
				}catch(Exception e){
					bfEsito.append("\t- [").append((i+1)).append("] fruizione non creata: ").append(e.getMessage());
					bfEsito.append("\n");
				}
			}
		}
		if(archive.getPorteDelegate().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// PorteApplicative
		if(archive.getPorteApplicative().size()>0){
			bfEsito.append("Porte Inbound (").append(archive.getPorteApplicative().size()).append(")\n");
		}
		for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
			try{
				ArchiveEsitoImportDetail archivePortaApplicativa = archive.getPorteApplicative().get(i);
				IDPortaApplicativa idPortaApplicativa = ((ArchivePortaApplicativa)archivePortaApplicativa.getArchiveObject()).getIdPortaApplicativa();
				IDSoggetto idProprietario = ((ArchivePortaApplicativa)archivePortaApplicativa.getArchiveObject()).getIdSoggettoProprietario();
				bfEsito.append("\t- ["+idProprietario+"]["+idPortaApplicativa.getNome()+"] ");
				serializeStato(archivePortaApplicativa, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
			
			// Si registrano solo gli errori per il check mapping (ci saranno solo in createUpdate)
			if(importOperation) {
				try{
					ArchiveEsitoImportDetail archivePortaApplicativa = archive.getPorteApplicative_initMapping().get(i);
					if(ArchiveStatoImport.ERROR.equals(archivePortaApplicativa.getState())){
						IDPortaApplicativa idPortaApplicativa = ((ArchivePortaApplicativa)archivePortaApplicativa.getArchiveObject()).getIdPortaApplicativa();
						IDSoggetto idProprietario = ((ArchivePortaApplicativa)archivePortaApplicativa.getArchiveObject()).getIdSoggettoProprietario();
						bfEsito.append("\t- Erogazione ["+idProprietario+"]["+idPortaApplicativa.getNome()+"] ");
						serializeStato(archivePortaApplicativa, bfEsito, importOperation);
						bfEsito.append("\n");
					}
				}catch(Exception e){
					bfEsito.append("\t- [").append((i+1)).append("] erogazione non creata: ").append(e.getMessage());
					bfEsito.append("\n");
				}
			}
		}
		if(archive.getPorteApplicative().size()>0){
			bfEsito.append("\n");	
		}
		
		
		
		
		// Mapping Fruizioni
		if(archive.getMappingFruizioni().size()>0){
			bfEsito.append("Fruizioni (").append(archive.getMappingFruizioni().size()).append(")\n");
		}
		for (int i = 0; i < archive.getMappingFruizioni().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveFruizione = archive.getMappingFruizioni().get(i);
				MappingFruizionePortaDelegata mapping = ((ArchiveMappingFruizione)archiveFruizione.getArchiveObject()).getMappingFruizionePortaDelegata();
				String nome = (mapping.getDescrizione()!=null && !"".equals(mapping.getDescrizione())) ? mapping.getDescrizione() : mapping.getNome();
				IDServizio idServizio = mapping.getIdServizio();
				IDSoggetto idFruitore = mapping.getIdFruitore();
				bfEsito.append("\t- (gruppo:"+nome+") ["+idFruitore+"] -> ["+idServizio+"] ");
				serializeStato(archiveFruizione, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getMappingFruizioni().size()>0){
			bfEsito.append("\n");	
		}
		
		
		
		
		
		// Mapping Erogazioni
		if(archive.getMappingErogazioni().size()>0){
			bfEsito.append("Erogazioni (").append(archive.getMappingErogazioni().size()).append(")\n");
		}
		for (int i = 0; i < archive.getMappingErogazioni().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveErogazione = archive.getMappingErogazioni().get(i);
				MappingErogazionePortaApplicativa mapping = ((ArchiveMappingErogazione)archiveErogazione.getArchiveObject()).getMappingErogazionePortaApplicativa();
				String nome = (mapping.getDescrizione()!=null && !"".equals(mapping.getDescrizione())) ? mapping.getDescrizione() : mapping.getNome();
				IDServizio idServizio = mapping.getIdServizio();
				bfEsito.append("\t- (gruppo:"+nome+") ["+idServizio+"] ");
				serializeStato(archiveErogazione, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getMappingErogazioni().size()>0){
			bfEsito.append("\n");	
		}
		
		
		
		
				
		
		// Controllo Traffico (Configurazione)
		if(archive.getControlloTraffico_configurazione()!=null){
			bfEsito.append("Controllo del Traffico - Configurazione\n");
			try{
				ArchiveEsitoImportDetailConfigurazione<org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale> configurazione = 
						archive.getControlloTraffico_configurazione();
				bfEsito.append("\t- ");
				serializeStato(configurazione, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- non importata: ").append(e.getMessage());
			}
			bfEsito.append("\n");
			bfEsito.append("\n");
		}
		
		// Controllo Traffico (ConfigurazionePolicies)
		if(archive.getControlloTraffico_configurationPolicies().size()>0){
			bfEsito.append("Controllo del Traffico - Registro Policy (").append(archive.getControlloTraffico_configurationPolicies().size()).append(")\n");
		}
		for (int i = 0; i < archive.getControlloTraffico_configurationPolicies().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveCCPolicy = archive.getControlloTraffico_configurationPolicies().get(i);
				String nomePolicy = ((ArchiveConfigurationPolicy)archiveCCPolicy.getArchiveObject()).getNomePolicy();
				bfEsito.append("\t- [").append(nomePolicy).append("] ");
				serializeStato(archiveCCPolicy, bfEsito, importOperation);
			}catch(Throwable e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getControlloTraffico_configurationPolicies().size()>0){
			bfEsito.append("\n");	
		}
		
		// Controllo Traffico (AttivazionePolicies)
		if(archive.getControlloTraffico_activePolicies().size()>0){
			bfEsito.append("Controllo del Traffico - Policy (").append(archive.getControlloTraffico_activePolicies().size()).append(")\n");
		}
		for (int i = 0; i < archive.getControlloTraffico_activePolicies().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveCCPolicy = archive.getControlloTraffico_activePolicies().get(i);
				String nomePolicy = ((ArchiveActivePolicy)archiveCCPolicy.getArchiveObject()).getNomePolicy();
				bfEsito.append("\t- [").append(nomePolicy).append("] ");
				serializeStato(archiveCCPolicy, bfEsito, importOperation);
			}catch(Throwable e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getControlloTraffico_activePolicies().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Token Policy (Validation)
		if(archive.getToken_validation_policies().size()>0){
			bfEsito.append("Token Policy - Validation (").append(archive.getToken_validation_policies().size()).append(")\n");
		}
		for (int i = 0; i < archive.getToken_validation_policies().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveTokenPolicy = archive.getToken_validation_policies().get(i);
				String nomePolicy = ((ArchiveTokenPolicy)archiveTokenPolicy.getArchiveObject()).getNomePolicy();
				bfEsito.append("\t- [").append(nomePolicy).append("] ");
				serializeStato(archiveTokenPolicy, bfEsito, importOperation);
			}catch(Throwable e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getToken_validation_policies().size()>0){
			bfEsito.append("\n");	
		}
		
		// Token Policy (Retrieve)
		if(archive.getToken_retrieve_policies().size()>0){
			bfEsito.append("Token Policy - Retrieve (").append(archive.getToken_retrieve_policies().size()).append(")\n");
		}
		for (int i = 0; i < archive.getToken_retrieve_policies().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveTokenPolicy = archive.getToken_retrieve_policies().get(i);
				String nomePolicy = ((ArchiveTokenPolicy)archiveTokenPolicy.getArchiveObject()).getNomePolicy();
				bfEsito.append("\t- [").append(nomePolicy).append("] ");
				serializeStato(archiveTokenPolicy, bfEsito, importOperation);
			}catch(Throwable e){
				bfEsito.append("\t- [").append((i+1)).append("] "+labelNonErrore+": ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getToken_retrieve_policies().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Configurazione
		if(archive.getConfigurazionePdD()!=null){
			bfEsito.append("Configurazione\n");
			try{
				ArchiveEsitoImportDetailConfigurazione<Configurazione> configurazione = archive.getConfigurazionePdD();
				bfEsito.append("\t- ");
				serializeStato(configurazione, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- non importata: ").append(e.getMessage());
			}
			bfEsito.append("\n");
			bfEsito.append("\n");
		}
		
		
		return bfEsito.toString();
	}
	public final static String LABEL_IMPORT_POLICY = "Policy di Configurazione";
	public final static String LABEL_DELETE_POLICY =  LABEL_IMPORT_POLICY;
	public final static String LABEL_IMPORT_CONFIGURAZIONE = "Configurazione di GovWay";
	public void serializeStato(ArchiveEsitoImportDetail detail,StringBuilder bfEsito, boolean importOperation){
		String stateDetail = "";
		if(detail.getStateDetail()!=null){
			stateDetail = detail.getStateDetail();
		}
		switch (detail.getState()) {
		case UPDATE_NOT_ENABLED:
			bfEsito.append("non importato: già presente (aggiornamento non abilitato)").append(stateDetail);
			break;
		case NOT_UPDATABLE:
			bfEsito.append("già presente (aggiornamento non necessario)").append(stateDetail);
			break;
		case IMPORT_POLICY_CONFIG_NOT_ENABLED:
			bfEsito.append("non importato: opzione '"+LABEL_IMPORT_POLICY+"' non abilitata").append(stateDetail);
			break;
		case IMPORT_CONFIG_NOT_ENABLED:
			bfEsito.append("non importato: opzione '"+LABEL_IMPORT_CONFIGURAZIONE+"' non abilitata").append(stateDetail);
			break;
		case ERROR:
			if(detail.getStateDetail()!=null){
				stateDetail = " ["+detail.getStateDetail()+"]";
			}
			if(importOperation)
				bfEsito.append("non importato"+stateDetail+": ").append(detail.getException().getMessage());
			else
				bfEsito.append("non eliminato"+stateDetail+": ").append(detail.getException().getMessage());
			break;
		case CREATED:
			bfEsito.append("importato correttamente").append(stateDetail);
			break;
		case UPDATED:
			bfEsito.append("già presente, aggiornato correttamente").append(stateDetail);
			break;
		case DELETED_POLICY_CONFIG_NOT_ENABLED:
			bfEsito.append("non eliminto: opzione '"+LABEL_DELETE_POLICY+"' non abilitata").append(stateDetail);
			break;
		case DELETED_NOT_EXISTS:
			bfEsito.append("non esistente").append(stateDetail);
			break;
		case DELETED:
			bfEsito.append("eliminato correttamente").append(stateDetail);
			break;
		}
	}
	public void serializeStato(ArchiveEsitoImportDetailConfigurazione<?> detail,StringBuilder bfEsito){
		String stateDetail = "";
		if(detail.getStateDetail()!=null){
			stateDetail = detail.getStateDetail();
		}
		switch (detail.getState()) {
		case UPDATE_NOT_ENABLED:
			// Stato mai usato per questo oggetto
			break;
		case NOT_UPDATABLE:
			// Stato mai usato per questo oggetto
			break;
		case IMPORT_POLICY_CONFIG_NOT_ENABLED:
			bfEsito.append("non importato: opzione '"+LABEL_IMPORT_POLICY+"' non abilitata").append(stateDetail);
			break;
		case IMPORT_CONFIG_NOT_ENABLED:
			bfEsito.append("non importato: opzione '"+LABEL_IMPORT_CONFIGURAZIONE+"' non abilitata").append(stateDetail);
			break;
		case ERROR:
			if(detail.getStateDetail()!=null){
				stateDetail = " ["+detail.getStateDetail()+"]";
			}
			bfEsito.append("non importata"+stateDetail+": ").append(detail.getException().getMessage());
			break;
		case CREATED:
			// Stato mai usato per questo oggetto
			break;
		case UPDATED:
			bfEsito.append("aggiornata correttamente").append(stateDetail);
			break;
		case DELETED_POLICY_CONFIG_NOT_ENABLED:
			bfEsito.append("non eliminto: opzione '"+LABEL_DELETE_POLICY+"' non abilitata").append(stateDetail);
			break;
		case DELETED_NOT_EXISTS:
			// Stato mai usato per questo oggetto
			break;
		case DELETED:
			// Stato utilizzato solamente per le extended info
			bfEsito.append("eliminazione effettuata correttamente").append(stateDetail);
			break;
		}
	}

	
	
	private ArchiveEsitoImport getArchiveEsitoImport(ArchiveIdCorrelazione idCorrelazioneParam,
			Hashtable<String, ArchiveEsitoImport> map,
			Hashtable<String, ArchiveIdCorrelazione> mapIdCorrelazione){
		String idCorrelazione = idCorrelazioneParam.getId();
		if(idCorrelazione==null){
			idCorrelazione = ZIPUtils.ID_CORRELAZIONE_DEFAULT;	
		}
		ArchiveEsitoImport tmp = null;
		if(map.containsKey(idCorrelazione)){
			tmp = map.get(idCorrelazione);
		}
		else{
			tmp = new ArchiveEsitoImport();
			map.put(idCorrelazione, tmp);
			mapIdCorrelazione.put(idCorrelazione, idCorrelazioneParam);
		}
		return tmp;
	}
	
	public void readIdCorrelazione(Hashtable<String, ArchiveEsitoImport> map, 
			Hashtable<String, ArchiveIdCorrelazione> mapIdCorrelazione,
			ArchiveEsitoImport archive) throws ProtocolException{
		
		// Pdd
		for (int i = 0; i < archive.getPdd().size(); i++) {
			ArchiveEsitoImportDetail archivePdd = archive.getPdd().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchivePdd)archivePdd.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getPdd().add(archivePdd);
		}
		
		// Gruppi
		for (int i = 0; i < archive.getGruppi().size(); i++) {
			ArchiveEsitoImportDetail archiveGruppo = archive.getGruppi().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveGruppo)archiveGruppo.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getGruppi().add(archiveGruppo);
		}
		
		// Ruoli
		for (int i = 0; i < archive.getRuoli().size(); i++) {
			ArchiveEsitoImportDetail archiveRuolo = archive.getRuoli().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveRuolo)archiveRuolo.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getRuoli().add(archiveRuolo);
		}
		
		// Scope
		for (int i = 0; i < archive.getScope().size(); i++) {
			ArchiveEsitoImportDetail archiveScope = archive.getScope().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveScope)archiveScope.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getScope().add(archiveScope);
		}
		
		// Soggetti
		for (int i = 0; i < archive.getSoggetti().size(); i++) {
			ArchiveEsitoImportDetail archiveSoggetto = archive.getSoggetti().get(i);
			ArchiveIdCorrelazione idCorrelazione =((ArchiveSoggetto)archiveSoggetto.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getSoggetti().add(archiveSoggetto);
		}

		// Servizi Applicativi
		for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
			ArchiveEsitoImportDetail archiveServizioApplicativo = archive.getServiziApplicativi().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveServizioApplicativo)archiveServizioApplicativo.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getServiziApplicativi().add(archiveServizioApplicativo);
		}
			
		// Accordi di Cooperazione
		for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
			ArchiveEsitoImportDetail archiveAccordoCooperazione = archive.getAccordiCooperazione().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveAccordoCooperazione)archiveAccordoCooperazione.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getAccordiCooperazione().add(archiveAccordoCooperazione);
		}
			
		// Accordi di Servizio Parte Comune
		for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
			ArchiveEsitoImportDetail archiveAccordoServizioParteComune = archive.getAccordiServizioParteComune().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveAccordoServizioParteComune)archiveAccordoServizioParteComune.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getAccordiServizioParteComune().add(archiveAccordoServizioParteComune);
		}
			
		// Accordi di Servizio Parte Specifica (implementano accordi di servizio parte comune)
		for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
			ArchiveEsitoImportDetail archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecifica().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveAccordoServizioParteSpecifica)archiveAccordoServizioParteSpecifica.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getAccordiServizioParteSpecifica().add(archiveAccordoServizioParteSpecifica);
		}
			
		// Accordi di Servizio Composto
		for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
			ArchiveEsitoImportDetail archiveAccordoServizioComposto = archive.getAccordiServizioComposto().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveAccordoServizioComposto)archiveAccordoServizioComposto.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getAccordiServizioComposto().add(archiveAccordoServizioComposto);
		}
		
		// Accordi di Servizio Parte Specifica (implementano accordi di servizio composto)
		for (int i = 0; i < archive.getAccordiServizioParteSpecificaServiziComposti().size(); i++) {
			ArchiveEsitoImportDetail archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecificaServiziComposti().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveAccordoServizioParteSpecifica)archiveAccordoServizioParteSpecifica.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getAccordiServizioParteSpecificaServiziComposti().add(archiveAccordoServizioParteSpecifica);
		}
		
		// Fruitori
		for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
			ArchiveEsitoImportDetail archiveFruitore = archive.getAccordiFruitori().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveFruitore)archiveFruitore.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getAccordiFruitori().add(archiveFruitore);
		}
		
		// PorteDelegate
		for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
			ArchiveEsitoImportDetail archivePortaDelegata = archive.getPorteDelegate().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchivePortaDelegata)archivePortaDelegata.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getPorteDelegate().add(archivePortaDelegata);
		}
		
		// PorteApplicative
		for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
			ArchiveEsitoImportDetail archivePortaApplicativa = archive.getPorteApplicative().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchivePortaApplicativa)archivePortaApplicativa.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getPorteApplicative().add(archivePortaApplicativa);
		}
		
		// ControlloTraffico (Configurazione)
		if(archive.getControlloTraffico_configurazione()!=null){
			ArchiveEsitoImportDetailConfigurazione<org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale> configurazione = 
					archive.getControlloTraffico_configurazione();
			if(mapIdCorrelazione.size()>1){
				throw new ProtocolException("Configurazione permessa solo con una unica correlazione tra oggetti");
			}
			ArchiveIdCorrelazione idCorrelazione = null; 
			if(mapIdCorrelazione.size()==1){
				idCorrelazione = mapIdCorrelazione.values().iterator().next();
			}
			else if(mapIdCorrelazione.size()==0){
				// l'archivio non contiene altri oggetti se non la configurazione
				idCorrelazione = new ArchiveIdCorrelazione(ZIPUtils.ID_CORRELAZIONE_DEFAULT);
			}
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).setControlloTraffico_configurazione(configurazione);
		}
		
		// ControlloTraffico (ConfigurazionePolicy)
		for (int i = 0; i < archive.getControlloTraffico_configurationPolicies().size(); i++) {
			ArchiveEsitoImportDetail archiveCCPolicy = archive.getControlloTraffico_configurationPolicies().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveConfigurationPolicy)archiveCCPolicy.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getControlloTraffico_configurationPolicies().add(archiveCCPolicy);
		}
		
		// ControlloTraffico (AttivazionePolicy)
		for (int i = 0; i < archive.getControlloTraffico_activePolicies().size(); i++) {
			ArchiveEsitoImportDetail archiveCCPolicy = archive.getControlloTraffico_activePolicies().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveActivePolicy)archiveCCPolicy.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getControlloTraffico_activePolicies().add(archiveCCPolicy);
		}
		
		// Token Policy (Validation)
		for (int i = 0; i < archive.getToken_validation_policies().size(); i++) {
			ArchiveEsitoImportDetail archiveTokenPolicy = archive.getToken_validation_policies().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveTokenPolicy)archiveTokenPolicy.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getToken_validation_policies().add(archiveTokenPolicy);
		}
		
		// Token Policy (Retrieve)
		for (int i = 0; i < archive.getToken_retrieve_policies().size(); i++) {
			ArchiveEsitoImportDetail archiveTokenPolicy = archive.getToken_retrieve_policies().get(i);
			ArchiveIdCorrelazione idCorrelazione = ((ArchiveTokenPolicy)archiveTokenPolicy.getArchiveObject()).getIdCorrelazione();
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).getToken_retrieve_policies().add(archiveTokenPolicy);
		}
		
		// Configurazione
		if(archive.getConfigurazionePdD()!=null){
			ArchiveEsitoImportDetailConfigurazione<Configurazione> configurazione = archive.getConfigurazionePdD();
			if(mapIdCorrelazione.size()>1){
				throw new ProtocolException("Configurazione permessa solo con una unica correlazione tra oggetti");
			}
			ArchiveIdCorrelazione idCorrelazione = null; 
			if(mapIdCorrelazione.size()==1){
				idCorrelazione = mapIdCorrelazione.values().iterator().next();
			}
			else if(mapIdCorrelazione.size()==0){
				// l'archivio non contiene altri oggetti se non la configurazione
				idCorrelazione = new ArchiveIdCorrelazione(ZIPUtils.ID_CORRELAZIONE_DEFAULT);
			}
			this.getArchiveEsitoImport(idCorrelazione, map, mapIdCorrelazione).setConfigurazionePdD(configurazione);
		}
		
	}
}
