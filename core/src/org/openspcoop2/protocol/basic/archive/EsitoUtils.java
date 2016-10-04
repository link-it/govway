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

package org.openspcoop2.protocol.basic.archive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImport;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetail;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetailConfigurazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;

/**
 * EsitoUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoUtils {

	protected IProtocolFactory protocolFactory = null;
	protected IDAccordoCooperazioneFactory idAccordoCooperazioneFactory;
	protected IDAccordoFactory idAccordoFactory;
	public EsitoUtils(IProtocolFactory protocolFactory){
		this.protocolFactory = protocolFactory;
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idAccordoFactory = IDAccordoFactory.getInstance();
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
		
		StringBuffer bfEsito = new StringBuffer();
		
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
	private void append(StringBuffer bfEsito, ArchiveIdCorrelazione archiveIdCorrelazione, ArchiveEsitoImport archiveCorrelazione, boolean importOperation){
		
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
		
		StringBuffer bfEsito = new StringBuffer();
		
		// Pdd
		if(archive.getPdd().size()>0){
			bfEsito.append("PorteDominio (").append(archive.getPdd().size()).append(")\n");
		}
		for (int i = 0; i < archive.getPdd().size(); i++) {
			try{
				ArchiveEsitoImportDetail archivePdd = archive.getPdd().get(i);
				String nomePdd = ((ArchivePdd)archivePdd.getArchiveObject()).getNomePdd();
				bfEsito.append("\t- [").append(nomePdd).append("] ");
				serializeStato(archivePdd, bfEsito, importOperation);
			}catch(Throwable e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getPdd().size()>0){
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
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getSoggetti().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Servizi Applicativi
		if(archive.getServiziApplicativi().size()>0){
			bfEsito.append("ServiziApplicativi (").append(archive.getServiziApplicativi().size()).append(")\n");
		}
		for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveServizioApplicativo = archive.getServiziApplicativi().get(i);
				IDServizioApplicativo idServizioApplicativo = ((ArchiveServizioApplicativo)archiveServizioApplicativo.getArchiveObject()).getIdServizioApplicativo();
				bfEsito.append("\t- [").append(idServizioApplicativo.getIdSoggettoProprietario().toString()).
						append("_").append(idServizioApplicativo.getNome()).append("] ");
				serializeStato(archiveServizioApplicativo, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
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
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiCooperazione().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Parte Comune
		if(archive.getAccordiServizioParteComune().size()>0){
			bfEsito.append("Accordi di Servizio Parte Comune (").append(archive.getAccordiServizioParteComune().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteComune = archive.getAccordiServizioParteComune().get(i);
				IDAccordo idAccordo = ((ArchiveAccordoServizioParteComune)archiveAccordoServizioParteComune.getArchiveObject()).getIdAccordoServizioParteComune();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioParteComune, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiServizioParteComune().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Parte Specifica (implementano accordi di servizio parte comune)
		if(archive.getAccordiServizioParteSpecifica().size()>0){
			bfEsito.append("Accordi di Servizio Parte Specifica (").append(archive.getAccordiServizioParteSpecifica().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecifica().get(i);
				IDAccordo idAccordo = ((ArchiveAccordoServizioParteSpecifica)archiveAccordoServizioParteSpecifica.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioParteSpecifica, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
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
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiServizioComposto().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Parte Specifica (implementano accordi di servizio composto)
		if(archive.getAccordiServizioParteSpecificaServiziComposti().size()>0){
			bfEsito.append("Accordi di Servizio Parte Specifica (").
				append(archive.getAccordiServizioParteSpecificaServiziComposti().size()).append(") [accordi di servizio composto]\n");
		}
		for (int i = 0; i < archive.getAccordiServizioParteSpecificaServiziComposti().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecificaServiziComposti().get(i);
				IDAccordo idAccordo = ((ArchiveAccordoServizioParteSpecifica)archiveAccordoServizioParteSpecifica.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioParteSpecifica, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
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
				IDAccordo idAccordo = ((ArchiveFruitore)archiveFruitore.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				IDSoggetto idFruitore = ((ArchiveFruitore)archiveFruitore.getArchiveObject()).getIdSoggettoFruitore();
				bfEsito.append("\t- ["+idFruitore+"] -> [").append(uriAccordo).append("] ");
				serializeStato(archiveFruitore, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiFruitori().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// PorteDelegate
		if(archive.getPorteDelegate().size()>0){
			bfEsito.append("PorteDelegate (").append(archive.getPorteDelegate().size()).append(")\n");
		}
		for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
			try{
				ArchiveEsitoImportDetail archivePortaDelegata = archive.getPorteDelegate().get(i);
				IDPortaDelegata idPortaDelegata = ((ArchivePortaDelegata)archivePortaDelegata.getArchiveObject()).getIdPortaDelegata();
				IDSoggetto idProprietario = ((ArchivePortaDelegata)archivePortaDelegata.getArchiveObject()).getIdSoggettoProprietario();
				bfEsito.append("\t- ["+idProprietario+"]["+idPortaDelegata.getLocationPD()+"] ");
				serializeStato(archivePortaDelegata, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getPorteDelegate().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// PorteApplicative
		if(archive.getPorteApplicative().size()>0){
			bfEsito.append("PorteApplicative (").append(archive.getPorteApplicative().size()).append(")\n");
		}
		for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
			try{
				ArchiveEsitoImportDetail archivePortaApplicativa = archive.getPorteApplicative().get(i);
				IDPortaApplicativaByNome idPortaApplicativa = ((ArchivePortaApplicativa)archivePortaApplicativa.getArchiveObject()).getIdPortaApplicativaByNome();
				bfEsito.append("\t- ["+idPortaApplicativa.getSoggetto()+"]["+idPortaApplicativa.getNome()+"] ");
				serializeStato(archivePortaApplicativa, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getPorteApplicative().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Configurazione
		if(archive.getConfigurazionePdD()!=null){
			bfEsito.append("Configurazione\n");
			try{
				ArchiveEsitoImportDetailConfigurazione configurazione = archive.getConfigurazionePdD();
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
	public void serializeStato(ArchiveEsitoImportDetail detail,StringBuffer bfEsito, boolean importOperation){
		String stateDetail = "";
		if(detail.getStateDetail()!=null){
			stateDetail = detail.getStateDetail();
		}
		switch (detail.getState()) {
		case UPDATE_NOT_PERMISSED:
			bfEsito.append("non importato: già presente (aggiornamento non abilitato)").append(stateDetail);
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
		case DELETED_NOT_EXISTS:
			bfEsito.append("non esistente").append(stateDetail);
			break;
		case DELETED:
			bfEsito.append("eliminato correttamente").append(stateDetail);
			break;
		}
	}
	public void serializeStato(ArchiveEsitoImportDetailConfigurazione detail,StringBuffer bfEsito){
		String stateDetail = "";
		if(detail.getStateDetail()!=null){
			stateDetail = detail.getStateDetail();
		}
		switch (detail.getState()) {
		case UPDATE_NOT_PERMISSED:
			// Stato mai usato per questo oggetto
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
		case DELETED_NOT_EXISTS:
			// Stato mai usato per questo oggetto
			break;
		case DELETED:
			// Stato mai usato per questo oggetto
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
		
		// Configurazione
		if(archive.getConfigurazionePdD()!=null){
			ArchiveEsitoImportDetailConfigurazione configurazione = archive.getConfigurazionePdD();
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
