/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizio;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType;
import org.openspcoop2.protocol.information_missing.ReplaceMatchType;
import org.openspcoop2.protocol.information_missing.Soggetto;
import org.openspcoop2.protocol.information_missing.constants.Costanti;
import org.openspcoop2.protocol.information_missing.constants.ReplaceKeywordType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;

/**
 * ImporterInformationMissingSetter
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ImporterInformationMissingSetter {

	public static void setInformationMissingSoggetto(Archive archive, Soggetto soggetto, IDSoggetto idSoggetto, Connettore connettore) throws ProtocolException{
		
		switch (soggetto.getTipo()) {
		case RIFERIMENTO:
			setInformationMissing_RiferimentoSoggetto(archive, soggetto, idSoggetto);
			break;
		case CONNETTORE:
			setInformationMissing_ConnettoreSoggetto(archive, soggetto, connettore);
			break;
		}
		
	}
	
	public static void setInformationMissingServizioApplicativo(Archive archive, org.openspcoop2.protocol.information_missing.ServizioApplicativo sa, 
			InvocazioneServizio invocazioneServizio, Credenziali credenziali) throws ProtocolException{
		
		switch (sa.getTipo()) {
		case RIFERIMENTO:
			// non ancora gestito
			break;
		case CONNETTORE:
			setInformationMissing_ConnettoreSA(archive, sa, invocazioneServizio, credenziali);
			break;
		case CREDENZIALI_ACCESSO_PDD:
			setInformationMissing_CredenzialiSA(archive, sa, credenziali);
			break;
		case ALLINEA_CREDENZIALI_PD:
			setInformationMissing_AllineaCredenzialiPD(archive, sa);
			break;
		}
		
	}
	
	public static void setInformationMissingAccordoCooperazione(Archive archive, org.openspcoop2.protocol.information_missing.AccordoCooperazione ac, IDAccordoCooperazione idAccordo) throws ProtocolException{
		
		switch (ac.getTipo()) {
		case RIFERIMENTO:
			setInformationMissing_RiferimentoAccordoCooperazione(archive, ac, idAccordo);
			break;
		case STATO_ARCHIVIO:
			if(ac.getStato()==null){
				throw new ProtocolException("Stato non fornito, è richiesto con tipo di operazione ["+ac.getTipo().name()+"]");
			}
			setInformationMissing_StatoAccordoCooperazione(archive, ac);
			break;
		}
		
	}
	
	public static void setInformationMissingAccordoServizioParteComune(Archive archive, org.openspcoop2.protocol.information_missing.AccordoServizioParteComune as, IDAccordo idAccordo) throws ProtocolException{
		
		switch (as.getTipo()) {
		case RIFERIMENTO:
			setInformationMissing_RiferimentoAccordoServizioParteComune(archive, as, idAccordo);
			break;
		case STATO_ARCHIVIO:
			if(as.getStato()==null){
				throw new ProtocolException("Stato non fornito, è richiesto con tipo di operazione ["+as.getTipo().name()+"]");
			}
			setInformationMissing_StatoAccordoServizioParteComune(archive, as);
			break;
		}
		
	}
	
	public static void setInformationMissingAccordoServizioParteSpecifica(Archive archive, org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica asps, 
			Connettore connettore) throws ProtocolException{
		
		switch (asps.getTipo()) {
		case CONNETTORE:
			setInformationMissing_ConnettoreASPS(archive, asps, connettore);
			break;
		case STATO_ARCHIVIO:
			if(asps.getStato()==null){
				throw new ProtocolException("Stato non fornito, è richiesto con tipo di operazione ["+asps.getTipo().name()+"]");
			}
			setInformationMissing_StatoAccordoServizioParteSpecifica(archive, asps);
			break;
		}
		
	}
	
	public static void setInformationMissingAccordoServizioComposto(Archive archive, org.openspcoop2.protocol.information_missing.AccordoServizioParteComune as, IDAccordo idAccordo) throws ProtocolException{
		
		switch (as.getTipo()) {
		case RIFERIMENTO:
			setInformationMissing_RiferimentoAccordoServizioComposto(archive, as, idAccordo);
			break;
		case STATO_ARCHIVIO:
			if(as.getStato()==null){
				throw new ProtocolException("Stato non fornito, è richiesto con tipo di operazione ["+as.getTipo().name()+"]");
			}
			setInformationMissing_StatoAccordoServizioComposto(archive, as);
			break;
		}
		
	}
	
	public static void setInformationMissingFruitore(Archive archive, org.openspcoop2.protocol.information_missing.Fruitore fruitore,
			Connettore connettore) throws ProtocolException{
		
		switch (fruitore.getTipo()) {
		case CONNETTORE:
			setInformationMissing_ConnettoreFruitore(archive, fruitore, connettore);
			break;
		case STATO_ARCHIVIO:
			setInformationMissing_StatoFruitore(archive, fruitore);
			break;
		}
		
	}
	
	
	
	
	// ******* MATCH **********
	
	private static boolean match(ReplaceMatchFieldType fieldType,String value){
		if(ReplaceKeywordType.EMPTY.equals(fieldType.getTipo())){
			if( value==null || "".equals(value) ){
				return true;
			}
		}
		else if(ReplaceKeywordType.EQUALS.equals(fieldType.getTipo())){
			if(value!=null && value.equals(fieldType.getValore())){
				return true;
			}
		}
		else if(ReplaceKeywordType.CONTAINS.equals(fieldType.getTipo())){
			if(value!=null && value.contains(fieldType.getValore())){
				return true;
			}
		}
		else if(ReplaceKeywordType.STARTS_WITH.equals(fieldType.getTipo())){
			if(value!=null && value.startsWith(fieldType.getValore())){
				return true;
			}
		}
		else if(ReplaceKeywordType.ENDS_WITH.equals(fieldType.getTipo())){
			if(value!=null && value.endsWith(fieldType.getValore())){
				return true;
			}
		}
		else if(ReplaceKeywordType.ALL.equals(fieldType.getTipo())){
			return true;
		}
		return false;
	}
	
	private static boolean matchSoggetto(ReplaceMatchType replaceMatch,
			String tipoSoggetto,String nomeSoggetto){
		
		ReplaceMatchFieldType nome = replaceMatch.getNome();
		ReplaceMatchFieldType tipo = replaceMatch.getTipo();
		if(nome==null && tipo==null){
			return false; // per trovare un soggetto almeno un criterio di ricerca deve essere fornito
		}
		
		if(nome!=null){
			if(match(nome, nomeSoggetto)==false){
				return false;
			}
		}
		
		if(tipo!=null){
			if(match(tipo, tipoSoggetto)==false){
				return false;
			}
		}
		
		return true;
	}
	
	
	private static boolean matchServizio(ReplaceMatchType replaceMatch,
			String tipoServizio,String nomeServizio){
		ReplaceMatchFieldType nome = replaceMatch.getNome();
		ReplaceMatchFieldType tipo = replaceMatch.getTipo();
		if(nome==null && tipo==null){
			return false; // per trovare un servizio almeno un criterio di ricerca deve essere fornito
		}
		
		if(nome!=null){
			if(match(nome, nomeServizio)==false){
				return false;
			}
		}
		
		if(tipo!=null){
			if(match(tipo, tipoServizio)==false){
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean matchServizioApplicativo(ReplaceMatchType replaceMatch,
			String nomeServizio){
		ReplaceMatchFieldType nome = replaceMatch.getNome();
		if(nome==null){
			return false; // per trovare un servizio applicativo un criterio di ricerca deve essere fornito
		}
		
		if(nome!=null){
			if(match(nome, nomeServizio)==false){
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean matchAccordo(ReplaceMatchType replaceMatch,
			String nomeAccordo){
		ReplaceMatchFieldType nome = replaceMatch.getNome();
		if(nome==null){
			return false; // per trovare un accordo un criterio di ricerca deve essere fornito
		}
		
		if(nome!=null){
			if(match(nome, nomeAccordo)==false){
				return false;
			}
		}
		
		return true;
	}
	

	
	
	// ******* SOGGETTI **********
	
	private static void setInformationMissing_RiferimentoSoggetto(Archive archive, Soggetto soggetto, IDSoggetto idSoggetto) throws ProtocolException{
		
		// ServiziApplicativi
		for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
			ServizioApplicativo sa = archive.getServiziApplicativi().get(i).getServizioApplicativo();
			if(matchSoggetto(soggetto.getReplaceMatch(), 
					sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario())){
				sa.setTipoSoggettoProprietario(idSoggetto.getTipo());
				sa.setNomeSoggettoProprietario(idSoggetto.getNome());
				archive.getServiziApplicativi().get(i).update(sa, false);
			}
		}
		
		// PorteDelegate
		for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
			PortaDelegata pd = archive.getPorteDelegate().get(i).getPortaDelegata();
			if(matchSoggetto(soggetto.getReplaceMatch(), 
					pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario())){
				pd.setTipoSoggettoProprietario(idSoggetto.getTipo());
				pd.setNomeSoggettoProprietario(idSoggetto.getNome());
				archive.getPorteDelegate().get(i).update(pd, false);
			}
			if(pd.getSoggettoErogatore()!=null){
				if(matchSoggetto(soggetto.getReplaceMatch(), 
						pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome())){
					pd.getSoggettoErogatore().setTipo(idSoggetto.getTipo());
					pd.getSoggettoErogatore().setNome(idSoggetto.getNome());
					archive.getPorteDelegate().get(i).update(pd, false);
				}
			}
		}
		
		// PorteApplicative
		for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
			PortaApplicativa pa = archive.getPorteApplicative().get(i).getPortaApplicativa();
			if(matchSoggetto(soggetto.getReplaceMatch(), 
					pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario())){
				pa.setTipoSoggettoProprietario(idSoggetto.getTipo());
				pa.setNomeSoggettoProprietario(idSoggetto.getNome());
				archive.getPorteApplicative().get(i).update(pa, false);
			}
			if(pa.getSoggettoVirtuale()!=null){
				if(matchSoggetto(soggetto.getReplaceMatch(), 
						pa.getSoggettoVirtuale().getTipo(), pa.getSoggettoVirtuale().getNome())){
					pa.getSoggettoVirtuale().setTipo(idSoggetto.getTipo());
					pa.getSoggettoVirtuale().setNome(idSoggetto.getNome());
					archive.getPorteApplicative().get(i).update(pa, false);
				}
			}
		}
		
		// Accordi di Cooperazione
		for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
			AccordoCooperazione ac = archive.getAccordiCooperazione().get(i).getAccordoCooperazione();
			IdSoggetto soggettoReferente = ac.getSoggettoReferente();
			String tipoSoggettoReferente = null;
			String nomeSoggettoReferente = null;
			if(soggettoReferente!=null){
				tipoSoggettoReferente = soggettoReferente.getTipo();
				nomeSoggettoReferente = soggettoReferente.getNome();
			}
			if(matchSoggetto(soggetto.getReplaceMatch(), 
					tipoSoggettoReferente, nomeSoggettoReferente)){
				if(soggettoReferente==null){
					ac.setSoggettoReferente(new IdSoggetto());
				}
				ac.getSoggettoReferente().setTipo(idSoggetto.getTipo());
				ac.getSoggettoReferente().setNome(idSoggetto.getNome());
				archive.getAccordiCooperazione().get(i).update(ac, false);
			}
		}
		
		// Accordi di Servizio Parte Comune 
		for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
			AccordoServizioParteComune as = archive.getAccordiServizioParteComune().get(i).getAccordoServizioParteComune();
			IdSoggetto soggettoReferente = as.getSoggettoReferente();
			String tipoSoggettoReferente = null;
			String nomeSoggettoReferente = null;
			if(soggettoReferente!=null){
				tipoSoggettoReferente = soggettoReferente.getTipo();
				nomeSoggettoReferente = soggettoReferente.getNome();
			}
			if(matchSoggetto(soggetto.getReplaceMatch(), 
					tipoSoggettoReferente, nomeSoggettoReferente)){
				if(soggettoReferente==null){
					as.setSoggettoReferente(new IdSoggetto());
				}
				as.getSoggettoReferente().setTipo(idSoggetto.getTipo());
				as.getSoggettoReferente().setNome(idSoggetto.getNome());
				archive.getAccordiServizioParteComune().get(i).update(as, false);
			}
		}
		
		// Accordi di Servizio Parte Specifica 
		for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
			ArchiveAccordoServizioParteSpecifica archiveAS = archive.getAccordiServizioParteSpecifica().get(i); 
			AccordoServizioParteSpecifica as = archiveAS.getAccordoServizioParteSpecifica();
			String tipoSoggettoReferente = as.getTipoSoggettoErogatore();
			String nomeSoggettoReferente = as.getNomeSoggettoErogatore();
			if(matchSoggetto(soggetto.getReplaceMatch(), 
					tipoSoggettoReferente, nomeSoggettoReferente)){
				as.setTipoSoggettoErogatore(idSoggetto.getTipo());
				as.setNomeSoggettoErogatore(idSoggetto.getNome());
				archive.getAccordiServizioParteSpecifica().get(i).update(as, false);
			}
			
			if(archiveAS.getMappingPorteApplicativeAssociate()!=null && !archiveAS.getMappingPorteApplicativeAssociate().isEmpty()) {
				for (MappingErogazionePortaApplicativa mappingPA : archiveAS.getMappingPorteApplicativeAssociate()) {
					if(mappingPA.getIdServizio()!=null && mappingPA.getIdServizio().getSoggettoErogatore()!=null) {
						String tipoSoggettoErogatore = mappingPA.getIdServizio().getSoggettoErogatore().getTipo();
						String nomeSoggettoErogatore = mappingPA.getIdServizio().getSoggettoErogatore().getNome();
						if(matchSoggetto(soggetto.getReplaceMatch(), 
								tipoSoggettoErogatore, nomeSoggettoErogatore)){
							mappingPA.getIdServizio().getSoggettoErogatore().setTipo(idSoggetto.getTipo());
							mappingPA.getIdServizio().getSoggettoErogatore().setNome(idSoggetto.getNome());
						}
					}
				}
			}
		}

		// Accordi di Servizio Composti
		for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
			AccordoServizioParteComune as = archive.getAccordiServizioComposto().get(i).getAccordoServizioParteComune();
			IdSoggetto soggettoReferente = as.getSoggettoReferente();
			String tipoSoggettoReferente = null;
			String nomeSoggettoReferente = null;
			if(soggettoReferente!=null){
				tipoSoggettoReferente = soggettoReferente.getTipo();
				nomeSoggettoReferente = soggettoReferente.getNome();
			}
			if(matchSoggetto(soggetto.getReplaceMatch(), 
					tipoSoggettoReferente, nomeSoggettoReferente)){
				if(soggettoReferente==null){
					as.setSoggettoReferente(new IdSoggetto());
				}
				as.getSoggettoReferente().setTipo(idSoggetto.getTipo());
				as.getSoggettoReferente().setNome(idSoggetto.getNome());
				archive.getAccordiServizioComposto().get(i).update(as, false);
			}
		}
		
		// Fruizioni
		for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
			ArchiveFruitore archiveFruitore = archive.getAccordiFruitori().get(i);
			Fruitore fruitore = archiveFruitore.getFruitore();
			IDServizio idAps = archiveFruitore.getIdAccordoServizioParteSpecifica();
			String tipoSoggettoReferente = null;
			String nomeSoggettoReferente = null;
			if(idAps.getSoggettoErogatore()!=null){
				tipoSoggettoReferente = idAps.getSoggettoErogatore().getTipo();
				nomeSoggettoReferente = idAps.getSoggettoErogatore().getNome();
			}
			if(matchSoggetto(soggetto.getReplaceMatch(), 
					fruitore.getTipo(), fruitore.getNome())){
				fruitore.setTipo(idSoggetto.getTipo());
				fruitore.setNome(idSoggetto.getNome());
				archive.getAccordiFruitori().get(i).update(idAps,fruitore, false);
			}
			else if(matchSoggetto(soggetto.getReplaceMatch(), 
					tipoSoggettoReferente, nomeSoggettoReferente)){
				try{
					idAps = IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(idAps.getTipo(),idAps.getNome(),
							idSoggetto.getTipo(),idSoggetto.getNome(), idAps.getVersione());
				}catch(Exception e){
					throw new ProtocolException(e.getMessage(),e);
				}
				archive.getAccordiFruitori().get(i).update(idAps,fruitore, false);
			} 
			
			if(archiveFruitore.getMappingPorteDelegateAssociate()!=null && !archiveFruitore.getMappingPorteDelegateAssociate().isEmpty()) {
				for (MappingFruizionePortaDelegata mappingPD : archiveFruitore.getMappingPorteDelegateAssociate()) {
					if(mappingPD.getIdServizio()!=null && mappingPD.getIdServizio().getSoggettoErogatore()!=null) {
						String tipoSoggettoErogatore = mappingPD.getIdServizio().getSoggettoErogatore().getTipo();
						String nomeSoggettoErogatore = mappingPD.getIdServizio().getSoggettoErogatore().getNome();
						if(matchSoggetto(soggetto.getReplaceMatch(), 
								tipoSoggettoErogatore, nomeSoggettoErogatore)){
							mappingPD.getIdServizio().getSoggettoErogatore().setTipo(idSoggetto.getTipo());
							mappingPD.getIdServizio().getSoggettoErogatore().setNome(idSoggetto.getNome());
						}
					}
					if(mappingPD.getIdFruitore()!=null) {
						String tipoSoggettoFruitore = mappingPD.getIdFruitore().getTipo();
						String nomeSoggettoFruitore = mappingPD.getIdFruitore().getNome();
						if(matchSoggetto(soggetto.getReplaceMatch(), 
								tipoSoggettoFruitore, nomeSoggettoFruitore)){
							mappingPD.getIdFruitore().setTipo(idSoggetto.getTipo());
							mappingPD.getIdFruitore().setNome(idSoggetto.getNome());
						}
					}
				}
			}
		}
	}

	private static void setInformationMissing_ConnettoreSoggetto(Archive archive, Soggetto soggetto, Connettore connettore) throws ProtocolException{
		
		// Soggetti
		for (int i = 0; i < archive.getSoggetti().size(); i++) {
			org.openspcoop2.core.registry.Soggetto soggettoRegistro = archive.getSoggetti().get(i).getSoggettoRegistro();
			if(soggettoRegistro!=null){
				if(matchSoggetto(soggetto.getReplaceMatch(), 
						soggettoRegistro.getTipo(), soggettoRegistro.getNome())){
					if(soggettoRegistro.getConnettore()==null)
						soggettoRegistro.setConnettore(connettore);
					else{
						soggettoRegistro.getConnettore().setCustom(connettore.getCustom());
						soggettoRegistro.getConnettore().setTipo(connettore.getTipo());
						while(soggettoRegistro.getConnettore().sizePropertyList()>0)
							soggettoRegistro.getConnettore().removeProperty(0);
						if(connettore.sizePropertyList()>0)
							soggettoRegistro.getConnettore().getPropertyList().addAll(connettore.getPropertyList());
					}
				}
			}
		}
		
	}
	
	
	
	
	// ***** SERVIZI APPLICATIVI ******
	
	private static void setInformationMissing_ConnettoreSA(Archive archive,  org.openspcoop2.protocol.information_missing.ServizioApplicativo saMissingInfo, 
			InvocazioneServizio invocazioneServizio, Credenziali credenziali) throws ProtocolException{
		
		// ServiziApplicativi
		for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
			ServizioApplicativo sa = archive.getServiziApplicativi().get(i).getServizioApplicativo();
			if(matchServizioApplicativo(saMissingInfo.getReplaceMatch(), 
					sa.getNome())){
				
				if(credenziali!=null){
					if(sa.getInvocazionePorta()==null){
						sa.setInvocazionePorta(new InvocazionePorta());
					}
					while(sa.getInvocazionePorta().sizeCredenzialiList()>0){
						sa.getInvocazionePorta().removeCredenziali(0);
					}
					sa.getInvocazionePorta().addCredenziali(credenziali);
				}
				
				if(sa.getInvocazioneServizio()==null){
					sa.setInvocazioneServizio(invocazioneServizio);
				}
				else{
					sa.getInvocazioneServizio().setSbustamentoInformazioniProtocollo(invocazioneServizio.getSbustamentoInformazioniProtocollo());
					sa.getInvocazioneServizio().setSbustamentoSoap(invocazioneServizio.getSbustamentoSoap());
					sa.getInvocazioneServizio().setGetMessage(invocazioneServizio.getGetMessage());
					sa.getInvocazioneServizio().setInvioPerRiferimento(invocazioneServizio.getInvioPerRiferimento());
					sa.getInvocazioneServizio().setRispostaPerRiferimento(invocazioneServizio.getRispostaPerRiferimento());
					
					if(sa.getInvocazioneServizio().getCredenziali()==null){
						sa.getInvocazioneServizio().setCredenziali(invocazioneServizio.getCredenziali());
					}
					else{
						sa.getInvocazioneServizio().getCredenziali().setUser(invocazioneServizio.getCredenziali().getUser());
						sa.getInvocazioneServizio().getCredenziali().setPassword(invocazioneServizio.getCredenziali().getPassword());
					}
					
					sa.getInvocazioneServizio().setAutenticazione(invocazioneServizio.getAutenticazione());
					
					if(sa.getInvocazioneServizio().getConnettore()==null){
						sa.getInvocazioneServizio().setConnettore(invocazioneServizio.getConnettore());
					}
					else{
						sa.getInvocazioneServizio().getConnettore().setCustom(invocazioneServizio.getConnettore().getCustom());
						sa.getInvocazioneServizio().getConnettore().setTipo(invocazioneServizio.getConnettore().getTipo());
						while(sa.getInvocazioneServizio().getConnettore().sizePropertyList()>0)
							sa.getInvocazioneServizio().getConnettore().removeProperty(0);
						if(invocazioneServizio.getConnettore().sizePropertyList()>0)
							sa.getInvocazioneServizio().getConnettore().getPropertyList().addAll(invocazioneServizio.getConnettore().getPropertyList());
					}
				}
			}
		}
	}
	
	private static void setInformationMissing_CredenzialiSA(Archive archive,  org.openspcoop2.protocol.information_missing.ServizioApplicativo saMissingInfo, 
			 Credenziali credenziali) throws ProtocolException{
		
		// ServiziApplicativi
		for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
			ServizioApplicativo sa = archive.getServiziApplicativi().get(i).getServizioApplicativo();
			if(matchServizioApplicativo(saMissingInfo.getReplaceMatch(), 
					sa.getNome())){
				
				if(sa.getInvocazionePorta()==null){
					sa.setInvocazionePorta(new InvocazionePorta());
				}
				while(sa.getInvocazionePorta().sizeCredenzialiList()>0){
					sa.getInvocazionePorta().removeCredenziali(0);
				}
				sa.getInvocazionePorta().addCredenziali(credenziali);
				
			}
		}
	}
	private static void setInformationMissing_AllineaCredenzialiPD(Archive archive,  org.openspcoop2.protocol.information_missing.ServizioApplicativo saMissingInfo) throws ProtocolException{
		
		// ServiziApplicativi
		for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
			ServizioApplicativo sa = archive.getServiziApplicativi().get(i).getServizioApplicativo();
			if(matchServizioApplicativo(saMissingInfo.getReplaceMatch(), 
					sa.getNome())){
				
				if(sa.getInvocazionePorta()==null || sa.getInvocazionePorta().sizeCredenzialiList()<=0){
					throw new ProtocolException("Credenziali di accesso non presenti nel servizio applicativo ["+sa.getNome()+
							"]. Le credenziali sono richieste dalla modalità di gestione delle informazioni mancanti impostata");
				}
				Credenziali credenziali = sa.getInvocazionePorta().getCredenziali(0);
				if(credenziali.getTipo()==null){
					throw new ProtocolException("Credenziali di accesso non presenti nel servizio applicativo ["+sa.getNome()+
							"] (tipo non definito). Le credenziali sono richieste dalla modalità di gestione delle informazioni mancanti impostata");
				}
				
				for (int j = 0; j < archive.getPorteDelegate().size(); j++) {
					PortaDelegata pd = archive.getPorteDelegate().get(j).getPortaDelegata();
					if(pd!=null && pd.sizeServizioApplicativoList()>0){
						for (int k = 0; k < pd.sizeServizioApplicativoList(); k++) {
							if(sa.getNome().equals(pd.getServizioApplicativo(k).getNome())){
								pd.setAutenticazione(credenziali.getTipo().getValue());
								break;
							}
						}
					}
				}
			}
		}
	}
	

	
	
	
	
	
	
	// ******* ACCORDI COOPERAZIONE **********
	
	private static void setInformationMissing_RiferimentoAccordoCooperazione(Archive archive, org.openspcoop2.protocol.information_missing.AccordoCooperazione ac, IDAccordoCooperazione idAccordo) throws ProtocolException{
		
		// Accordi di Servizio Composti
		for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
			AccordoServizioParteComune as = archive.getAccordiServizioComposto().get(i).getAccordoServizioParteComune();
			
			String uriAccordoCooperazione = null;
			if(as.getServizioComposto()!=null){
				uriAccordoCooperazione = as.getServizioComposto().getAccordoCooperazione(); 
			}
			if(matchAccordo(ac.getReplaceMatch(), 
					uriAccordoCooperazione)){
				if(as.getServizioComposto()==null){
					as.setServizioComposto(new AccordoServizioParteComuneServizioComposto());
				}
				try{
					as.getServizioComposto().setAccordoCooperazione(IDAccordoCooperazioneFactory.getInstance().getUriFromIDAccordo(idAccordo));
				}catch(Exception e){
					throw new ProtocolException(e.getMessage(),e);
				}
				archive.getAccordiServizioComposto().get(i).update(as, false);
			}
		}
			
	}
	
	private static void setInformationMissing_StatoAccordoCooperazione(Archive archive, org.openspcoop2.protocol.information_missing.AccordoCooperazione ac) throws ProtocolException{
		
		// Accordi di Cooperazione
		if(ac.getStato()!=null){
			for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
				AccordoCooperazione acObject = archive.getAccordiCooperazione().get(i).getAccordoCooperazione();
				String uriAccordoCooperazione = null;
				try{
					uriAccordoCooperazione = IDAccordoCooperazioneFactory.getInstance().getUriFromAccordo(acObject);
				}catch(Exception e){
					throw new ProtocolException(e.getMessage(),e);
				}
				if(matchAccordo(ac.getReplaceMatch(), 
						uriAccordoCooperazione)){
					acObject.setStatoPackage(ac.getStato().getValue());
				}
			}
		}
			
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// ******* ACCORDI SERVIZIO PARTE COMUNE **********
	
	private static void setInformationMissing_RiferimentoAccordoServizioParteComune(Archive archive, org.openspcoop2.protocol.information_missing.AccordoServizioParteComune as, IDAccordo idAccordo) throws ProtocolException{
		
		// Accordi di Servizio Parte Specifica 
		for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
			AccordoServizioParteSpecifica asObject = archive.getAccordiServizioParteSpecifica().get(i).getAccordoServizioParteSpecifica();
			String uriAccordo = null;
			if(asObject.getAccordoServizioParteComune()!=null){
				uriAccordo = asObject.getAccordoServizioParteComune();
			}
			if(matchAccordo(as.getReplaceMatch(), 
					uriAccordo)){
				try{
					asObject.setAccordoServizioParteComune(IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo));
				}catch(Exception e){
					throw new ProtocolException(e.getMessage(),e);
				}
				archive.getAccordiServizioParteSpecifica().get(i).update(asObject, false);
			}
		}
			
	}
	
	private static void setInformationMissing_StatoAccordoServizioParteComune(Archive archive, org.openspcoop2.protocol.information_missing.AccordoServizioParteComune as) throws ProtocolException{
		
		// Accordi di Servizio Parte Comune 
		if(as.getStato()!=null){
			for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
				AccordoServizioParteComune asObject = archive.getAccordiServizioParteComune().get(i).getAccordoServizioParteComune();
				String uriAccordo = null;
				try{
					uriAccordo = IDAccordoFactory.getInstance().getUriFromAccordo(asObject);
				}catch(Exception e){
					throw new ProtocolException(e.getMessage(),e);
				}
				if(matchAccordo(as.getReplaceMatch(), 
						uriAccordo)){
					asObject.setStatoPackage(as.getStato().getValue());
				}
			}
		}
			
	}



	
	
	
	
	
	
	
	// ***** ACCORDI SERVIZIO PARTE SPECIFICA ******
	
	private static void setInformationMissing_ConnettoreASPS(Archive archive, org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica aspsMissingInfo, 
			Connettore connettore) throws ProtocolException{
		
		// Accordi
		for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
			org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps = archive.getAccordiServizioParteSpecifica().get(i).getAccordoServizioParteSpecifica();
			if(asps!=null){
				
				boolean matchTipoNome = matchServizio(aspsMissingInfo.getReplaceMatch(), 
						asps.getTipo(), asps.getNome());
					
				String uriAccordo = null;
				try{
					uriAccordo = IDServizioFactory.getInstance().getUriFromAccordo(asps);
				}catch(Exception e){
					throw new ProtocolException(e.getMessage(),e);
				}
				boolean matchAccordo = matchAccordo(aspsMissingInfo.getReplaceMatch(), 
						uriAccordo);
				
				if(matchTipoNome || matchAccordo){
					if(asps.getConfigurazioneServizio()==null){
						asps.setConfigurazioneServizio(new ConfigurazioneServizio());
					}
					if(asps.getConfigurazioneServizio().getConnettore()==null)
						asps.getConfigurazioneServizio().setConnettore(connettore);
					else{
						asps.getConfigurazioneServizio().getConnettore().setCustom(connettore.getCustom());
						asps.getConfigurazioneServizio().getConnettore().setTipo(connettore.getTipo());
						while(asps.getConfigurazioneServizio().getConnettore().sizePropertyList()>0)
							asps.getConfigurazioneServizio().getConnettore().removeProperty(0);
						if(connettore.sizePropertyList()>0)
							asps.getConfigurazioneServizio().getConnettore().getPropertyList().addAll(connettore.getPropertyList());
					}
				}
			}
		}
		
	}

	private static void setInformationMissing_StatoAccordoServizioParteSpecifica(Archive archive, org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica aspsMissingInfo) throws ProtocolException{
		
		// Accordi di Servizio Parte Specifica
		if(aspsMissingInfo.getStato()!=null){
			for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
				org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps = archive.getAccordiServizioParteSpecifica().get(i).getAccordoServizioParteSpecifica();
				if(asps!=null){
					
					boolean matchTipoNome = matchServizio(aspsMissingInfo.getReplaceMatch(), 
							asps.getTipo(), asps.getNome());
						
					String uriAccordo = asps.getAccordoServizioParteComune();
					boolean matchAccordo = matchAccordo(aspsMissingInfo.getReplaceMatch(), 
							uriAccordo);
								
					if(matchTipoNome || matchAccordo){
						asps.setStatoPackage(aspsMissingInfo.getStato().getValue());
					}
				}
			}
		}
			
	}
	
	
	
	
	
	// ******* ACCORDI SERVIZIO COMPOSTO **********
	
	private static void setInformationMissing_RiferimentoAccordoServizioComposto(Archive archive, org.openspcoop2.protocol.information_missing.AccordoServizioParteComune as, IDAccordo idAccordo) throws ProtocolException{
		
		setInformationMissing_RiferimentoAccordoServizioParteComune(archive, as, idAccordo);
			
	}
	
	private static void setInformationMissing_StatoAccordoServizioComposto(Archive archive, org.openspcoop2.protocol.information_missing.AccordoServizioParteComune as) throws ProtocolException{
		
		// Accordi di Servizio Composto
		if(as.getStato()!=null){
			for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
				AccordoServizioParteComune asObject = archive.getAccordiServizioComposto().get(i).getAccordoServizioParteComune();
				String uriAccordo = null;
				try{
					uriAccordo = IDAccordoFactory.getInstance().getUriFromAccordo(asObject);
				}catch(Exception e){
					throw new ProtocolException(e.getMessage(),e);
				}
				if(matchAccordo(as.getReplaceMatch(), 
						uriAccordo)){
					asObject.setStatoPackage(as.getStato().getValue());
				}
			}
		}
			
	}
	
	
	
	
	
	
	
	
	
	
	// ******* FRUITORE **********
	
	private static void setInformationMissing_ConnettoreFruitore(Archive archive, org.openspcoop2.protocol.information_missing.Fruitore fruitoreMissingInfo, 
			Connettore connettore) throws ProtocolException{
		
		// Accordi
		for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
			Fruitore fruitore = archive.getAccordiFruitori().get(i).getFruitore();
			if(fruitore!=null){
				
				if(matchSoggetto(fruitoreMissingInfo.getReplaceMatch(), 
						fruitore.getTipo(), fruitore.getNome())){
					
					if(fruitore.getConnettore()==null) {
						fruitore.setConnettore(connettore);
					}
					else {
						fruitore.getConnettore().setCustom(connettore.getCustom());
						fruitore.getConnettore().setTipo(connettore.getTipo());
						while(fruitore.getConnettore().sizePropertyList()>0)
							fruitore.getConnettore().removeProperty(0);
						if(connettore.sizePropertyList()>0)
							fruitore.getConnettore().getPropertyList().addAll(connettore.getPropertyList());
					}
					
				}
				
			}
		}
		
	}
	
	private static void setInformationMissing_StatoFruitore(Archive archive, org.openspcoop2.protocol.information_missing.Fruitore fruitoreMissingInfo) throws ProtocolException{
		
		// Fruitori
		if(fruitoreMissingInfo.getStato()!=null){
			for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
				Fruitore fruitore = archive.getAccordiFruitori().get(i).getFruitore();
				if(matchSoggetto(fruitoreMissingInfo.getReplaceMatch(), 
						fruitore.getTipo(), fruitore.getNome())){
					fruitore.setStatoPackage(fruitoreMissingInfo.getStato().getValue());
				}
			}
		}
			
	}
	
	
	
	
	
	
	
	
	// ******* REPLACE NAME **********
	
	protected static String replaceSoggettoProprietario(String original, String tipoSoggetto, String nomeSoggetto){
		
		if(original==null){
			return null;
		}
		
		String returnValue = new String(original);
		
		if(tipoSoggetto!=null && !"".equals(tipoSoggetto)){
			returnValue = returnValue.replace(Costanti.TIPO_SOGGETTO, tipoSoggetto);
			returnValue = returnValue.replace(Costanti.TIPO_SOGGETTO_PROPRIETARIO, tipoSoggetto);
		}
		
		if(nomeSoggetto!=null && !"".equals(nomeSoggetto)){
			returnValue = returnValue.replace(Costanti.NOME_SOGGETTO, nomeSoggetto);
			returnValue = returnValue.replace(Costanti.NOME_SOGGETTO_PROPRIETARIO, nomeSoggetto);
		}
		
		return returnValue;
	}
	
	protected static String replaceSoggettoErogatore(String original, String tipoSoggetto, String nomeSoggetto){
		
		if(original==null){
			return null;
		}
		
		String returnValue = new String(original);
		
		if(tipoSoggetto!=null && !"".equals(tipoSoggetto)){
			returnValue = returnValue.replace(Costanti.TIPO_SOGGETTO_EROGATORE, tipoSoggetto);
		}
		
		if(nomeSoggetto!=null && !"".equals(nomeSoggetto)){
			returnValue = returnValue.replace(Costanti.NOME_SOGGETTO_EROGATORE, nomeSoggetto);
		}
		
		return returnValue;
	}
	
	private static String replaceServizio(String original, String tipoServizio, String nomeServizio){
		
		if(original==null){
			return null;
		}
		
		String returnValue = new String(original);
		
		if(tipoServizio!=null && !"".equals(tipoServizio)){
			returnValue = returnValue.replace(Costanti.TIPO_SERVIZIO, tipoServizio);
		}
		
		if(nomeServizio!=null && !"".equals(nomeServizio)){
			returnValue = returnValue.replace(Costanti.NOME_SERVIZIO, nomeServizio);
		}
		
		return returnValue;
	}
	
	private static String replaceAzione(String original, String nome){
		
		if(original==null){
			return null;
		}
		
		String returnValue = new String(original);
		
		if(nome!=null && !"".equals(nome)){
			returnValue = returnValue.replace(Costanti.AZIONE, nome);
		}
		
		return returnValue;
	}
	
	private static String replaceFruitore(String original, String tipoSoggetto, String nomeSoggetto){
		
		if(original==null){
			return null;
		}
		
		String returnValue = new String(original);
		
		if(tipoSoggetto!=null && !"".equals(tipoSoggetto)){
			returnValue = returnValue.replace(Costanti.TIPO_FRUITORE, tipoSoggetto);
		}
		
		if(nomeSoggetto!=null && !"".equals(nomeSoggetto)){
			returnValue = returnValue.replace(Costanti.NOME_FRUITORE, nomeSoggetto);
		}
		
		return returnValue;
	}
	
	public static void replaceTemplatesNames(Archive archive) throws ProtocolException{
			
		// Soggetti
		for (int i = 0; i < archive.getSoggetti().size(); i++) {
			org.openspcoop2.core.registry.Soggetto soggetto = archive.getSoggetti().get(i).getSoggettoRegistro();
			if(soggetto!=null) {
				if(soggetto.getConnettore()!=null){
					if(soggetto.getConnettore().sizePropertyList()>0){
						for (int j = 0; j < soggetto.getConnettore().sizePropertyList(); j++) {
							org.openspcoop2.core.registry.Property p = soggetto.getConnettore().getProperty(j);
							p.setNome(replaceSoggettoProprietario(p.getNome(), 
									soggetto.getTipo(),soggetto.getNome()));
							p.setValore(replaceSoggettoProprietario(p.getValore(), 
									soggetto.getTipo(),soggetto.getNome()));
						}
					}
				}
				if(soggetto.getIdentificativoPorta()!=null) {
					soggetto.setIdentificativoPorta(replaceSoggettoProprietario(soggetto.getIdentificativoPorta(), 
									soggetto.getTipo(),soggetto.getNome()));
				}
				if(soggetto.getPortaDominio()!=null) {
					soggetto.setPortaDominio(replaceSoggettoProprietario(soggetto.getPortaDominio(), 
									soggetto.getTipo(),soggetto.getNome()));
				}
			}
		}
		
		// ServiziApplicativi
		for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
			ServizioApplicativo sa = archive.getServiziApplicativi().get(i).getServizioApplicativo();
			sa.setNome(replaceSoggettoProprietario(sa.getNome(), 
					sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
			if(sa.getInvocazionePorta()!=null){
				if(sa.getInvocazionePorta().sizeCredenzialiList()>0){
					for (int j = 0; j < sa.getInvocazionePorta().sizeCredenzialiList(); j++) {
						Credenziali c  = sa.getInvocazionePorta().getCredenziali(j);
						c.setUser(replaceSoggettoProprietario(c.getUser(), 
								sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						c.setPassword(replaceSoggettoProprietario(c.getPassword(), 
								sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						c.setSubject(replaceSoggettoProprietario(c.getSubject(), 
								sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					}
				}
			}
			if(sa.getInvocazioneServizio()!=null){
				if(sa.getInvocazioneServizio().getCredenziali()!=null){
					InvocazioneCredenziali c  = sa.getInvocazioneServizio().getCredenziali();
					c.setUser(replaceSoggettoProprietario(c.getUser(), 
							sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					c.setPassword(replaceSoggettoProprietario(c.getPassword(), 
							sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
				}
				if(sa.getInvocazioneServizio().getConnettore()!=null){
					if(sa.getInvocazioneServizio().getConnettore().sizePropertyList()>0){
						for (int j = 0; j < sa.getInvocazioneServizio().getConnettore().sizePropertyList(); j++) {
							org.openspcoop2.core.config.Property p = sa.getInvocazioneServizio().getConnettore().getProperty(j);
							p.setNome(replaceSoggettoProprietario(p.getNome(), 
									sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
							p.setValore(replaceSoggettoProprietario(p.getValore(), 
									sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						}
					}
				}
			}
			if(sa.getRispostaAsincrona()!=null){
				if(sa.getRispostaAsincrona().getCredenziali()!=null){
					InvocazioneCredenziali c  = sa.getRispostaAsincrona().getCredenziali();
					c.setUser(replaceSoggettoProprietario(c.getUser(), 
							sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					c.setPassword(replaceSoggettoProprietario(c.getPassword(), 
							sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
				}
				if(sa.getRispostaAsincrona().getConnettore()!=null){
					if(sa.getRispostaAsincrona().getConnettore().sizePropertyList()>0){
						for (int j = 0; j < sa.getRispostaAsincrona().getConnettore().sizePropertyList(); j++) {
							org.openspcoop2.core.config.Property p = sa.getRispostaAsincrona().getConnettore().getProperty(j);
							p.setNome(replaceSoggettoProprietario(p.getNome(), 
									sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
							p.setValore(replaceSoggettoProprietario(p.getValore(), 
									sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						}
					}
				}
			}
		}
		
		// PorteDelegate
		for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
			PortaDelegata pd = archive.getPorteDelegate().get(i).getPortaDelegata();
						
			pd.setNome(replaceSoggettoProprietario(pd.getNome(), 
					pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
			if(pd.getSoggettoErogatore()!=null && 
					pd.getSoggettoErogatore().getNome()!=null && 
					!"".equals(pd.getSoggettoErogatore().getNome()) ){
				pd.setNome(replaceSoggettoErogatore(pd.getNome(), 
						pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
			}
			if(pd.getServizio()!=null && 
					pd.getServizio().getNome()!=null && 
					!"".equals(pd.getServizio().getNome()) ){
				pd.setNome(replaceServizio(pd.getNome(), 
						pd.getServizio().getTipo(), pd.getServizio().getNome()));
			}
			if(pd.getAzione()!=null && 
					pd.getAzione().getNome()!=null &&
					!"".equals(pd.getAzione().getNome()) ){
				pd.setNome(replaceAzione(pd.getNome(), 
						pd.getAzione().getNome()));
			}
			
			if(pd.getDescrizione()!=null && !"".equals(pd.getDescrizione())){
				pd.setDescrizione(replaceSoggettoProprietario(pd.getDescrizione(), 
						pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
				if(pd.getSoggettoErogatore()!=null && 
						pd.getSoggettoErogatore().getNome()!=null && 
						!"".equals(pd.getSoggettoErogatore().getNome()) ){
					pd.setDescrizione(replaceSoggettoErogatore(pd.getDescrizione(), 
							pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
				}
				if(pd.getServizio()!=null && 
						pd.getServizio().getNome()!=null && 
						!"".equals(pd.getServizio().getNome()) ){
					pd.setDescrizione(replaceServizio(pd.getDescrizione(), 
							pd.getServizio().getTipo(), pd.getServizio().getNome()));
				}
				if(pd.getAzione()!=null && 
						pd.getAzione().getNome()!=null &&
						!"".equals(pd.getAzione().getNome()) ){
					pd.setDescrizione(replaceAzione(pd.getDescrizione(), 
							pd.getAzione().getNome()));
				}
			}
			
			if(pd.getAzione()!=null){
				if(pd.getAzione().getPattern()!=null &&
						!"".equals(pd.getAzione().getPattern())){
					pd.getAzione().setPattern(replaceSoggettoProprietario(pd.getAzione().getPattern(), 
							pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
					if(pd.getSoggettoErogatore()!=null && 
							pd.getSoggettoErogatore().getNome()!=null && 
							!"".equals(pd.getSoggettoErogatore().getNome()) ){
						pd.getAzione().setPattern(replaceSoggettoErogatore(pd.getAzione().getPattern(), 
								pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
					}
					if(pd.getServizio()!=null && 
							pd.getServizio().getNome()!=null && 
							!"".equals(pd.getServizio().getNome()) ){
						pd.getAzione().setPattern(replaceServizio(pd.getAzione().getPattern(), 
								pd.getServizio().getTipo(), pd.getServizio().getNome()));
					}
					// se ho il pattern configurato, non c'è il nome dell'azione
//					pd.getAzione().setPattern(replaceAzione(pd.getAzione().getPattern(), 
//							pd.getAzione().getNome()));
					// se ho il pattern configurato, non c'è il nome dell'azione
				}
			}
			
			if(pd.getAzione()!=null) {
				if(pd.getAzione().getNomePortaDelegante()!=null &&
						!"".equals(pd.getAzione().getNomePortaDelegante())){
					pd.getAzione().setNomePortaDelegante(replaceSoggettoProprietario(pd.getAzione().getNomePortaDelegante(), 
							pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
				}
			}
			
			for (int j = 0; j < pd.sizeServizioApplicativoList(); j++) {
				pd.getServizioApplicativo(j).setNome(replaceSoggettoProprietario(pd.getServizioApplicativo(j).getNome(), 
					pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
				if(pd.getSoggettoErogatore()!=null && 
						pd.getSoggettoErogatore().getNome()!=null && 
						!"".equals(pd.getSoggettoErogatore().getNome()) ){
					pd.getServizioApplicativo(j).setNome(replaceSoggettoErogatore(pd.getServizioApplicativo(j).getNome(), 
							pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
				}
				if(pd.getServizio()!=null && 
						pd.getServizio().getNome()!=null && 
						!"".equals(pd.getServizio().getNome()) ){
					pd.getServizioApplicativo(j).setNome(replaceServizio(pd.getServizioApplicativo(j).getNome(), 
							pd.getServizio().getTipo(), pd.getServizio().getNome()));
				}
				if(pd.getAzione()!=null && 
						pd.getAzione().getNome()!=null && 
						!"".equals(pd.getAzione().getNome()) ){
					pd.getServizioApplicativo(j).setNome(replaceAzione(pd.getServizioApplicativo(j).getNome(), 
							pd.getAzione().getNome()));
				}
			}
			
			if(pd.getCorrelazioneApplicativa()!=null && pd.getCorrelazioneApplicativa().sizeElementoList()>0) {
				for (CorrelazioneApplicativaElemento elemento : pd.getCorrelazioneApplicativa().getElementoList()) {
					if(elemento.getNome()!=null && !"".equals(elemento.getNome())){
						elemento.setNome(replaceSoggettoProprietario(elemento.getNome(), 
								pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
						if(pd.getSoggettoErogatore()!=null && 
								pd.getSoggettoErogatore().getNome()!=null && 
								!"".equals(pd.getSoggettoErogatore().getNome()) ){
							elemento.setNome(replaceSoggettoErogatore(elemento.getNome(), 
									pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
						}
						if(pd.getServizio()!=null && 
								pd.getServizio().getNome()!=null && 
								!"".equals(pd.getServizio().getNome()) ){
							elemento.setNome(replaceServizio(elemento.getNome(), 
									pd.getServizio().getTipo(), pd.getServizio().getNome()));
						}
						if(pd.getAzione()!=null && 
								pd.getAzione().getNome()!=null &&
								!"".equals(pd.getAzione().getNome()) ){
							elemento.setNome(replaceAzione(elemento.getNome(), 
									pd.getAzione().getNome()));
						}
					}
					if(elemento.getPattern()!=null && !"".equals(elemento.getPattern())){
						elemento.setPattern(replaceSoggettoProprietario(elemento.getPattern(), 
								pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
						if(pd.getSoggettoErogatore()!=null && 
								pd.getSoggettoErogatore().getNome()!=null && 
								!"".equals(pd.getSoggettoErogatore().getNome()) ){
							elemento.setPattern(replaceSoggettoErogatore(elemento.getPattern(), 
									pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
						}
						if(pd.getServizio()!=null && 
								pd.getServizio().getNome()!=null && 
								!"".equals(pd.getServizio().getNome()) ){
							elemento.setPattern(replaceServizio(elemento.getPattern(), 
									pd.getServizio().getTipo(), pd.getServizio().getNome()));
						}
						if(pd.getAzione()!=null && 
								pd.getAzione().getNome()!=null &&
								!"".equals(pd.getAzione().getNome()) ){
							elemento.setPattern(replaceAzione(elemento.getPattern(), 
									pd.getAzione().getNome()));
						}
					}
				}
			}
			if(pd.getCorrelazioneApplicativaRisposta()!=null && pd.getCorrelazioneApplicativaRisposta().sizeElementoList()>0) {
				for (CorrelazioneApplicativaRispostaElemento elemento : pd.getCorrelazioneApplicativaRisposta().getElementoList()) {
					if(elemento.getNome()!=null && !"".equals(elemento.getNome())){
						elemento.setNome(replaceSoggettoProprietario(elemento.getNome(), 
								pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
						if(pd.getSoggettoErogatore()!=null && 
								pd.getSoggettoErogatore().getNome()!=null && 
								!"".equals(pd.getSoggettoErogatore().getNome()) ){
							elemento.setNome(replaceSoggettoErogatore(elemento.getNome(), 
									pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
						}
						if(pd.getServizio()!=null && 
								pd.getServizio().getNome()!=null && 
								!"".equals(pd.getServizio().getNome()) ){
							elemento.setNome(replaceServizio(elemento.getNome(), 
									pd.getServizio().getTipo(), pd.getServizio().getNome()));
						}
						if(pd.getAzione()!=null && 
								pd.getAzione().getNome()!=null &&
								!"".equals(pd.getAzione().getNome()) ){
							elemento.setNome(replaceAzione(elemento.getNome(), 
									pd.getAzione().getNome()));
						}
					}
					if(elemento.getPattern()!=null && !"".equals(elemento.getPattern())){
						elemento.setPattern(replaceSoggettoProprietario(elemento.getPattern(), 
								pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
						if(pd.getSoggettoErogatore()!=null && 
								pd.getSoggettoErogatore().getNome()!=null && 
								!"".equals(pd.getSoggettoErogatore().getNome()) ){
							elemento.setPattern(replaceSoggettoErogatore(elemento.getPattern(), 
									pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
						}
						if(pd.getServizio()!=null && 
								pd.getServizio().getNome()!=null && 
								!"".equals(pd.getServizio().getNome()) ){
							elemento.setPattern(replaceServizio(elemento.getPattern(), 
									pd.getServizio().getTipo(), pd.getServizio().getNome()));
						}
						if(pd.getAzione()!=null && 
								pd.getAzione().getNome()!=null &&
								!"".equals(pd.getAzione().getNome()) ){
							elemento.setPattern(replaceAzione(elemento.getPattern(), 
									pd.getAzione().getNome()));
						}
					}
				}
			}
			
		}
		
		// PorteApplicative
		for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
			PortaApplicativa pa = archive.getPorteApplicative().get(i).getPortaApplicativa();
			
			if(pa.getDescrizione()!=null && !"".equals(pa.getDescrizione())){
				pa.setDescrizione(replaceSoggettoProprietario(pa.getDescrizione(), 
						pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
				if(pa.getServizio()!=null && 
						pa.getServizio().getNome()!=null && 
						!"".equals(pa.getServizio().getNome()) ){
					pa.setDescrizione(replaceServizio(pa.getDescrizione(), 
							pa.getServizio().getTipo(), pa.getServizio().getNome()));
				}
				if(pa.getAzione()!=null && 
						pa.getAzione().getNome()!=null && 
						!"".equals(pa.getAzione().getNome()) ){
					pa.setDescrizione(replaceAzione(pa.getDescrizione(), 
							pa.getAzione().getNome()));
				}
			}
			
			pa.setNome(replaceSoggettoProprietario(pa.getNome(), 
					pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
			if(pa.getServizio()!=null && 
					pa.getServizio().getNome()!=null && 
					!"".equals(pa.getServizio().getNome()) ){
				pa.setNome(replaceServizio(pa.getNome(), 
						pa.getServizio().getTipo(), pa.getServizio().getNome()));
			}
			if(pa.getAzione()!=null && 
					pa.getAzione().getNome()!=null && 
					!"".equals(pa.getAzione().getNome()) ){
				pa.setNome(replaceAzione(pa.getNome(), 
						pa.getAzione().getNome()));
			}
			
			if(pa.getAzione()!=null) {
				if(pa.getAzione().getNomePortaDelegante()!=null &&
						!"".equals(pa.getAzione().getNomePortaDelegante())){
					pa.getAzione().setNomePortaDelegante(replaceSoggettoProprietario(pa.getAzione().getNomePortaDelegante(), 
							pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
				}
			}
			
			for (int j = 0; j < pa.sizeServizioApplicativoList(); j++) {
				pa.getServizioApplicativo(j).setNome(replaceSoggettoProprietario(pa.getServizioApplicativo(j).getNome(), 
					pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
				if(pa.getServizio()!=null && 
						pa.getServizio().getNome()!=null && 
						!"".equals(pa.getServizio().getNome()) ){
					pa.getServizioApplicativo(j).setNome(replaceServizio(pa.getServizioApplicativo(j).getNome(), 
							pa.getServizio().getTipo(), pa.getServizio().getNome()));
				}
				if(pa.getAzione()!=null && 
						pa.getAzione().getNome()!=null && 
						!"".equals(pa.getAzione().getNome()) ){
					pa.getServizioApplicativo(j).setNome(replaceAzione(pa.getServizioApplicativo(j).getNome(), 
							pa.getAzione().getNome()));
				}
			}
			
			if(pa.getCorrelazioneApplicativa()!=null && pa.getCorrelazioneApplicativa().sizeElementoList()>0) {
				for (CorrelazioneApplicativaElemento elemento : pa.getCorrelazioneApplicativa().getElementoList()) {
					if(elemento.getNome()!=null && !"".equals(elemento.getNome())){
						elemento.setNome(replaceSoggettoProprietario(elemento.getNome(), 
								pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
						if(pa.getServizio()!=null && 
								pa.getServizio().getNome()!=null && 
								!"".equals(pa.getServizio().getNome()) ){
							elemento.setNome(replaceServizio(elemento.getNome(), 
									pa.getServizio().getTipo(), pa.getServizio().getNome()));
						}
						if(pa.getAzione()!=null && 
								pa.getAzione().getNome()!=null &&
								!"".equals(pa.getAzione().getNome()) ){
							elemento.setNome(replaceAzione(elemento.getNome(), 
									pa.getAzione().getNome()));
						}
					}
					if(elemento.getPattern()!=null && !"".equals(elemento.getPattern())){
						elemento.setPattern(replaceSoggettoProprietario(elemento.getPattern(), 
								pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
						if(pa.getServizio()!=null && 
								pa.getServizio().getNome()!=null && 
								!"".equals(pa.getServizio().getNome()) ){
							elemento.setPattern(replaceServizio(elemento.getPattern(), 
									pa.getServizio().getTipo(), pa.getServizio().getNome()));
						}
						if(pa.getAzione()!=null && 
								pa.getAzione().getNome()!=null &&
								!"".equals(pa.getAzione().getNome()) ){
							elemento.setPattern(replaceAzione(elemento.getPattern(), 
									pa.getAzione().getNome()));
						}
					}
				}
			}
			if(pa.getCorrelazioneApplicativaRisposta()!=null && pa.getCorrelazioneApplicativaRisposta().sizeElementoList()>0) {
				for (CorrelazioneApplicativaRispostaElemento elemento : pa.getCorrelazioneApplicativaRisposta().getElementoList()) {
					if(elemento.getNome()!=null && !"".equals(elemento.getNome())){
						elemento.setNome(replaceSoggettoProprietario(elemento.getNome(), 
								pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
						if(pa.getServizio()!=null && 
								pa.getServizio().getNome()!=null && 
								!"".equals(pa.getServizio().getNome()) ){
							elemento.setNome(replaceServizio(elemento.getNome(), 
									pa.getServizio().getTipo(), pa.getServizio().getNome()));
						}
						if(pa.getAzione()!=null && 
								pa.getAzione().getNome()!=null &&
								!"".equals(pa.getAzione().getNome()) ){
							elemento.setNome(replaceAzione(elemento.getNome(), 
									pa.getAzione().getNome()));
						}
					}
					if(elemento.getPattern()!=null && !"".equals(elemento.getPattern())){
						elemento.setPattern(replaceSoggettoProprietario(elemento.getPattern(), 
								pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
						if(pa.getServizio()!=null && 
								pa.getServizio().getNome()!=null && 
								!"".equals(pa.getServizio().getNome()) ){
							elemento.setPattern(replaceServizio(elemento.getPattern(), 
									pa.getServizio().getTipo(), pa.getServizio().getNome()));
						}
						if(pa.getAzione()!=null && 
								pa.getAzione().getNome()!=null &&
								!"".equals(pa.getAzione().getNome()) ){
							elemento.setPattern(replaceAzione(elemento.getPattern(), 
									pa.getAzione().getNome()));
						}
					}
				}
			}
		}
		
		// AccordoServizioParteSpecifica
		for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
			
			ArchiveAccordoServizioParteSpecifica archiveAS = archive.getAccordiServizioParteSpecifica().get(i); 
			AccordoServizioParteSpecifica as = archiveAS.getAccordoServizioParteSpecifica();
			
			if(as.getAccordoServizioParteComune()!=null) {
				as.setAccordoServizioParteComune(replaceSoggettoProprietario(as.getAccordoServizioParteComune(), as.getTipoSoggettoErogatore(),as.getNomeSoggettoErogatore()));
				as.setAccordoServizioParteComune(replaceSoggettoErogatore(as.getAccordoServizioParteComune(), as.getTipoSoggettoErogatore(),as.getNomeSoggettoErogatore()));
			}
			
			if(as.getConfigurazioneServizio()!=null && as.getConfigurazioneServizio().getConnettore()!=null){
				for (int j = 0; j < as.getConfigurazioneServizio().getConnettore().sizePropertyList(); j++) {
					Property p = as.getConfigurazioneServizio().getConnettore().getProperty(j);
					p.setNome(replaceSoggettoProprietario(p.getNome(), as.getTipoSoggettoErogatore(),as.getNomeSoggettoErogatore()));
					p.setValore(replaceSoggettoProprietario(p.getValore(), as.getTipoSoggettoErogatore(),as.getNomeSoggettoErogatore()));
				}
			}
			if(as.getConfigurazioneServizio()!=null && as.getConfigurazioneServizio().getConnettore()!=null){
				for (int j = 0; j < as.getConfigurazioneServizio().getConnettore().sizePropertyList(); j++) {
					Property p = as.getConfigurazioneServizio().getConnettore().getProperty(j);
					p.setNome(replaceSoggettoErogatore(p.getNome(), as.getTipoSoggettoErogatore(),as.getNomeSoggettoErogatore()));
					p.setValore(replaceSoggettoErogatore(p.getValore(), as.getTipoSoggettoErogatore(),as.getNomeSoggettoErogatore()));
				}
			}
			
			if(as.sizeFruitoreList()==1){
				if(as.getConfigurazioneServizio()!=null && as.getConfigurazioneServizio().getConnettore()!=null){
					for (int j = 0; j < as.getConfigurazioneServizio().getConnettore().sizePropertyList(); j++) {
						Property p = as.getConfigurazioneServizio().getConnettore().getProperty(j);
						p.setNome(replaceFruitore(p.getNome(), as.getFruitore(0).getTipo(), as.getFruitore(0).getNome()));
						p.setValore(replaceFruitore(p.getValore(), as.getFruitore(0).getTipo(), as.getFruitore(0).getNome()));
					}
				}
			}
			else{
				if(as.sizeFruitoreList()==0){
					
					IDServizio idAccordoAttuale = null;
					try{
						idAccordoAttuale = IDServizioFactory.getInstance().getIDServizioFromAccordo(as);
					}catch(Exception e){
						throw new ProtocolException(e.getMessage(),e);
					}
					
					// cerco fruitori di questo accordo
					List<Fruitore> listFruitori = new ArrayList<Fruitore>();
					for (int j = 0; j < archive.getAccordiFruitori().size(); j++) {
						Fruitore fr = archive.getAccordiFruitori().get(j).getFruitore();
						IDServizio idAccordo = archive.getAccordiFruitori().get(j).getIdAccordoServizioParteSpecifica();
						if(idAccordo.equals(idAccordoAttuale)){
							listFruitori.add(fr);
						}
					}
					if(listFruitori.size()==1){
						if(as.getConfigurazioneServizio()!=null && as.getConfigurazioneServizio().getConnettore()!=null){
							for (int j = 0; j < as.getConfigurazioneServizio().getConnettore().sizePropertyList(); j++) {
								Property p = as.getConfigurazioneServizio().getConnettore().getProperty(j);
								p.setNome(replaceFruitore(p.getNome(), listFruitori.get(0).getTipo(), listFruitori.get(0).getNome()));
								p.setValore(replaceFruitore(p.getValore(), listFruitori.get(0).getTipo(), listFruitori.get(0).getNome()));
							}
						}
					}
				}
			}
			
			if(archiveAS.getMappingPorteApplicativeAssociate()!=null && !archiveAS.getMappingPorteApplicativeAssociate().isEmpty()) {
				for (MappingErogazionePortaApplicativa mappingPA : archiveAS.getMappingPorteApplicativeAssociate()) {
					if(mappingPA.getIdPortaApplicativa()!=null && mappingPA.getIdPortaApplicativa().getNome()!=null) {
						mappingPA.getIdPortaApplicativa().setNome(replaceSoggettoProprietario(mappingPA.getIdPortaApplicativa().getNome(), 
								as.getTipoSoggettoErogatore(),as.getNomeSoggettoErogatore()));
						mappingPA.getIdPortaApplicativa().setNome(replaceSoggettoErogatore(mappingPA.getIdPortaApplicativa().getNome(), 
								as.getTipoSoggettoErogatore(),as.getNomeSoggettoErogatore()));
						mappingPA.getIdPortaApplicativa().setNome(replaceServizio(mappingPA.getIdPortaApplicativa().getNome(), 
								as.getTipo(),as.getNome()));
					}
					if(mappingPA.getIdServizio()!=null) {
						replaceTemplatesNames(mappingPA.getIdServizio(),
								as.getTipoSoggettoErogatore(),as.getNomeSoggettoErogatore(),
								as.getTipo(),as.getNome(),
								true);
					}
				}
			}
		}
		
		// Fruitori
		for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
			ArchiveFruitore archiveFr = archive.getAccordiFruitori().get(i);
			Fruitore fr = archiveFr.getFruitore();
			IDServizio idAccordo = archive.getAccordiFruitori().get(i).getIdAccordoServizioParteSpecifica();
			String tipoSoggettoErogatore = null;
			String nomeSoggettoErogatore = null;
			String tipoServizio = null;
			String nomeServizio = null;
			if(idAccordo!=null && idAccordo.getSoggettoErogatore()!=null){
				tipoSoggettoErogatore = idAccordo.getSoggettoErogatore().getTipo();
				nomeSoggettoErogatore = idAccordo.getSoggettoErogatore().getNome();
			}
			if(idAccordo!=null) {
				tipoServizio = idAccordo.getTipo();
				nomeServizio = idAccordo.getNome();
			}
			if(fr.getConnettore()!=null){
				for (int j = 0; j < fr.getConnettore().sizePropertyList(); j++) {
					Property p = fr.getConnettore().getProperty(j);
					p.setNome(replaceFruitore(p.getNome(), fr.getTipo(), fr.getNome()));
					p.setValore(replaceFruitore(p.getValore(), fr.getTipo(), fr.getNome()));
				}
				for (int j = 0; j < fr.getConnettore().sizePropertyList(); j++) {
					Property p = fr.getConnettore().getProperty(j);
					p.setNome(replaceSoggettoProprietario(p.getNome(), fr.getTipo(), fr.getNome()));
					p.setValore(replaceSoggettoProprietario(p.getValore(), fr.getTipo(), fr.getNome()));
				}
				if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
					for (int j = 0; j < fr.getConnettore().sizePropertyList(); j++) {
						Property p = fr.getConnettore().getProperty(j);
						p.setNome(replaceSoggettoErogatore(p.getNome(), tipoSoggettoErogatore, nomeSoggettoErogatore));
						p.setValore(replaceSoggettoErogatore(p.getValore(), tipoSoggettoErogatore, nomeSoggettoErogatore));
					}	
				}
			}
			
			if(archiveFr.getMappingPorteDelegateAssociate()!=null && !archiveFr.getMappingPorteDelegateAssociate().isEmpty()) {
				for (MappingFruizionePortaDelegata mappingPD : archiveFr.getMappingPorteDelegateAssociate()) {
					if(mappingPD.getIdPortaDelegata()!=null && mappingPD.getIdPortaDelegata().getNome()!=null) {
						if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
							mappingPD.getIdPortaDelegata().setNome(replaceSoggettoErogatore(mappingPD.getIdPortaDelegata().getNome(), 
									tipoSoggettoErogatore,nomeSoggettoErogatore));
						}
						if(tipoServizio!=null && nomeServizio!=null){
							mappingPD.getIdPortaDelegata().setNome(replaceServizio(mappingPD.getIdPortaDelegata().getNome(), 
									tipoServizio,nomeServizio));
						}
						mappingPD.getIdPortaDelegata().setNome(replaceSoggettoProprietario(mappingPD.getIdPortaDelegata().getNome(), 
								fr.getTipo(), fr.getNome()));
						mappingPD.getIdPortaDelegata().setNome(replaceFruitore(mappingPD.getIdPortaDelegata().getNome(), 
								fr.getTipo(), fr.getNome()));
					}
					if(mappingPD.getIdServizio()!=null) {
						if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null && tipoServizio!=null && nomeServizio!=null){
							replaceTemplatesNames(mappingPD.getIdServizio(),
									tipoSoggettoErogatore,nomeSoggettoErogatore,
									tipoServizio,nomeServizio,
									false);
						}
					}
					if(mappingPD.getIdFruitore()!=null) {
						if(mappingPD.getIdFruitore().getTipo()!=null) {
							mappingPD.getIdFruitore().setTipo(replaceSoggettoProprietario(mappingPD.getIdFruitore().getTipo(), 
									fr.getTipo(), fr.getNome()));
							mappingPD.getIdFruitore().setTipo(replaceFruitore(mappingPD.getIdFruitore().getTipo(), 
									fr.getTipo(), fr.getNome()));
						}
						if(mappingPD.getIdFruitore().getNome()!=null) {
							mappingPD.getIdFruitore().setNome(replaceSoggettoProprietario(mappingPD.getIdFruitore().getNome(), 
									fr.getTipo(), fr.getNome()));
							mappingPD.getIdFruitore().setNome(replaceFruitore(mappingPD.getIdFruitore().getNome(), 
									fr.getTipo(), fr.getNome()));
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private static void replaceTemplatesNames(IDServizio idServizio, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, 
			String tipoServizio, String nomeServizio,
			boolean replaceProprietario) {
		if(idServizio.getSoggettoErogatore()!=null) {
			if(idServizio.getSoggettoErogatore().getTipo()!=null) {
				if(replaceProprietario) {
					idServizio.getSoggettoErogatore().setTipo(replaceSoggettoProprietario(idServizio.getSoggettoErogatore().getTipo(), 
							tipoSoggettoErogatore,nomeSoggettoErogatore));
				}
				idServizio.getSoggettoErogatore().setTipo(replaceSoggettoErogatore(idServizio.getSoggettoErogatore().getTipo(), 
						tipoSoggettoErogatore,nomeSoggettoErogatore));
			}
			if(idServizio.getSoggettoErogatore().getNome()!=null) {
				if(replaceProprietario) {
					idServizio.getSoggettoErogatore().setNome(replaceSoggettoProprietario(idServizio.getSoggettoErogatore().getNome(), 
							tipoSoggettoErogatore,nomeSoggettoErogatore));
				}
				idServizio.getSoggettoErogatore().setNome(replaceSoggettoErogatore(idServizio.getSoggettoErogatore().getNome(), 
						tipoSoggettoErogatore,nomeSoggettoErogatore));
			}
		}
		if(idServizio.getTipo()!=null) {
			idServizio.setTipo(replaceServizio(idServizio.getTipo(), 
					tipoServizio,nomeServizio));
		}
		if(idServizio.getNome()!=null) {
			idServizio.setNome(replaceServizio(idServizio.getNome(), 
					tipoServizio,nomeServizio));
		}
	}
}
