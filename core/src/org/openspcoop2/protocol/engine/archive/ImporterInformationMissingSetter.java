/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.protocol.engine.archive;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.information_missing.ReplaceMatchFieldType;
import org.openspcoop2.protocol.information_missing.ReplaceMatchType;
import org.openspcoop2.protocol.information_missing.Soggetto;
import org.openspcoop2.protocol.information_missing.constants.Costanti;
import org.openspcoop2.protocol.information_missing.constants.ReplaceKeywordType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;

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
	
	public static void setInformationMissingFruitore(Archive archive, org.openspcoop2.protocol.information_missing.Fruitore fruitore) throws ProtocolException{
		
		switch (fruitore.getTipo()) {
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
			AccordoServizioParteSpecifica as = archive.getAccordiServizioParteSpecifica().get(i).getAccordoServizioParteSpecifica();
			Servizio servizio = as.getServizio();
			String tipoSoggettoReferente = null;
			String nomeSoggettoReferente = null;
			if(servizio!=null){
				tipoSoggettoReferente = servizio.getTipoSoggettoErogatore();
				nomeSoggettoReferente = servizio.getNomeSoggettoErogatore();
			}
			if(matchSoggetto(soggetto.getReplaceMatch(), 
					tipoSoggettoReferente, nomeSoggettoReferente)){
				if(servizio==null){
					as.setServizio(new Servizio());
				}
				as.getServizio().setTipoSoggettoErogatore(idSoggetto.getTipo());
				as.getServizio().setNomeSoggettoErogatore(idSoggetto.getNome());
				archive.getAccordiServizioParteSpecifica().get(i).update(as, false);
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
			Fruitore fruitore = archive.getAccordiFruitori().get(i).getFruitore();
			IDAccordo idAps = archive.getAccordiFruitori().get(i).getIdAccordoServizioParteSpecifica();
			String tipoSoggettoReferente = null;
			String nomeSoggettoReferente = null;
			if(idAps.getSoggettoReferente()!=null){
				tipoSoggettoReferente = idAps.getSoggettoReferente().getTipo();
				nomeSoggettoReferente = idAps.getSoggettoReferente().getNome();
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
					idAps = IDAccordoFactory.getInstance().getIDAccordoFromValuesWithoutCheck(idAps.getNome(),
							idSoggetto.getTipo(),idSoggetto.getNome(), idAps.getVersione());
				}catch(Exception e){
					throw new ProtocolException(e.getMessage(),e);
				}
				archive.getAccordiFruitori().get(i).update(idAps,fruitore, false);
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
						sa.getInvocazioneServizio().getCredenziali().setTipo(invocazioneServizio.getCredenziali().getTipo());
						sa.getInvocazioneServizio().getCredenziali().setUser(invocazioneServizio.getCredenziali().getUser());
						sa.getInvocazioneServizio().getCredenziali().setPassword(invocazioneServizio.getCredenziali().getPassword());
						sa.getInvocazioneServizio().getCredenziali().setSubject(invocazioneServizio.getCredenziali().getSubject());
					}
					
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
			if(asps.getServizio()!=null){
				
				boolean matchTipoNome = matchServizio(aspsMissingInfo.getReplaceMatch(), 
						asps.getServizio().getTipo(), asps.getServizio().getNome());
					
				String uriAccordo = null;
				try{
					uriAccordo = IDAccordoFactory.getInstance().getUriFromAccordo(asps);
				}catch(Exception e){
					throw new ProtocolException(e.getMessage(),e);
				}
				boolean matchAccordo = matchAccordo(aspsMissingInfo.getReplaceMatch(), 
						uriAccordo);
				
				if(matchTipoNome || matchAccordo){
					if(asps.getServizio().getConnettore()==null)
						asps.getServizio().setConnettore(connettore);
					else{
						asps.getServizio().getConnettore().setCustom(connettore.getCustom());
						asps.getServizio().getConnettore().setTipo(connettore.getTipo());
						while(asps.getServizio().getConnettore().sizePropertyList()>0)
							asps.getServizio().getConnettore().removeProperty(0);
						if(connettore.sizePropertyList()>0)
							asps.getServizio().getConnettore().getPropertyList().addAll(connettore.getPropertyList());
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
				if(asps.getServizio()!=null){
					
					boolean matchTipoNome = matchServizio(aspsMissingInfo.getReplaceMatch(), 
							asps.getServizio().getTipo(), asps.getServizio().getNome());
						
					String uriAccordo = null;
					try{
						uriAccordo = IDAccordoFactory.getInstance().getUriFromAccordo(asps);
					}catch(Exception e){
						throw new ProtocolException(e.getMessage(),e);
					}
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
	
	private static String replaceSoggettoProprietario(String original, String tipoSoggetto, String nomeSoggetto){
		
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
	
	private static String replaceSoggettoErogatore(String original, String tipoSoggetto, String nomeSoggetto){
		
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
					Credenziali c  = sa.getInvocazioneServizio().getCredenziali();
					c.setUser(replaceSoggettoProprietario(c.getUser(), 
							sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					c.setPassword(replaceSoggettoProprietario(c.getPassword(), 
							sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					c.setSubject(replaceSoggettoProprietario(c.getSubject(), 
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
					Credenziali c  = sa.getRispostaAsincrona().getCredenziali();
					c.setUser(replaceSoggettoProprietario(c.getUser(), 
							sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					c.setPassword(replaceSoggettoProprietario(c.getPassword(), 
							sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					c.setSubject(replaceSoggettoProprietario(c.getSubject(), 
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
			
			if(pd.getLocation()!=null && !"".equals(pd.getLocation())){
				pd.setLocation(replaceSoggettoProprietario(pd.getLocation(), 
						pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
				if(pd.getSoggettoErogatore()!=null && 
						pd.getSoggettoErogatore().getNome()!=null && 
						!"".equals(pd.getSoggettoErogatore().getNome()) ){
					pd.setLocation(replaceSoggettoErogatore(pd.getLocation(), 
							pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
				}
				if(pd.getServizio()!=null && 
						pd.getServizio().getNome()!=null && 
						!"".equals(pd.getServizio().getNome()) ){
					pd.setLocation(replaceServizio(pd.getLocation(), 
							pd.getServizio().getTipo(), pd.getServizio().getNome()));
				}
				if(pd.getAzione()!=null && 
						pd.getAzione().getNome()!=null &&
						!"".equals(pd.getAzione().getNome()) ){
					pd.setLocation(replaceAzione(pd.getLocation(), 
							pd.getAzione().getNome()));
				}
			}
			
			if(pd.getSoggettoErogatore()!=null){
				if(pd.getSoggettoErogatore().getPattern()!=null &&
						!"".equals(pd.getSoggettoErogatore().getPattern())){
					pd.getSoggettoErogatore().setPattern(replaceSoggettoProprietario(pd.getSoggettoErogatore().getPattern(), 
							pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
					// se ho il pattern configurato, non c'è il nome del soggetto erogatore
//					pd.getSoggettoErogatore().setPattern(replaceSoggettoErogatore(pd.getSoggettoErogatore().getPattern(), 
//							pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
					// se ho il pattern configurato, non c'è il nome del soggetto erogatore
					if(pd.getServizio()!=null && 
							pd.getServizio().getNome()!=null && 
							!"".equals(pd.getServizio().getNome()) ){
						pd.getSoggettoErogatore().setPattern(replaceServizio(pd.getSoggettoErogatore().getPattern(), 
								pd.getServizio().getTipo(), pd.getServizio().getNome()));
					}
					if(pd.getAzione()!=null && 
							pd.getAzione().getNome()!=null &&
							!"".equals(pd.getAzione().getNome()) ){
						pd.getSoggettoErogatore().setPattern(replaceAzione(pd.getSoggettoErogatore().getPattern(), 
								pd.getAzione().getNome()));
					}			
				}
			}
			
			if(pd.getServizio()!=null){
				if(pd.getServizio().getPattern()!=null &&
						!"".equals(pd.getServizio().getPattern())){
					pd.getServizio().setPattern(replaceSoggettoProprietario(pd.getServizio().getPattern(), 
							pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
					if(pd.getSoggettoErogatore()!=null && 
							pd.getSoggettoErogatore().getNome()!=null && 
							!"".equals(pd.getSoggettoErogatore().getNome()) ){
						pd.getServizio().setPattern(replaceSoggettoErogatore(pd.getServizio().getPattern(), 
								pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
					}
					// se ho il pattern configurato, non c'è il nome del servizio
//					pd.getServizio().setPattern(replaceServizio(pd.getServizio().getPattern(), 
//							pd.getServizio().getTipo(), pd.getServizio().getNome()));
					// se ho il pattern configurato, non c'è il nome del servizio
					if(pd.getAzione()!=null && 
							pd.getAzione().getNome()!=null &&
							!"".equals(pd.getAzione().getNome()) ){
						pd.getServizio().setPattern(replaceAzione(pd.getServizio().getPattern(), 
								pd.getAzione().getNome()));
					}			
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
		}
		
		// PorteApplicative
		for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
			PortaApplicativa pa = archive.getPorteApplicative().get(i).getPortaApplicativa();
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
		}
		
		// AccordoServizioParteSpecifica
		for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
			AccordoServizioParteSpecifica as = archive.getAccordiServizioParteSpecifica().get(i).getAccordoServizioParteSpecifica();
			
			if(as.getServizio()!=null && as.getServizio().getConnettore()!=null){
				for (int j = 0; j < as.getServizio().getConnettore().sizePropertyList(); j++) {
					Property p = as.getServizio().getConnettore().getProperty(j);
					p.setNome(replaceSoggettoProprietario(p.getNome(), as.getServizio().getTipoSoggettoErogatore(),as.getServizio().getNomeSoggettoErogatore()));
					p.setValore(replaceSoggettoProprietario(p.getValore(), as.getServizio().getTipoSoggettoErogatore(),as.getServizio().getNomeSoggettoErogatore()));
				}
			}
			if(as.getServizio()!=null && as.getServizio().getConnettore()!=null){
				for (int j = 0; j < as.getServizio().getConnettore().sizePropertyList(); j++) {
					Property p = as.getServizio().getConnettore().getProperty(j);
					p.setNome(replaceSoggettoErogatore(p.getNome(), as.getServizio().getTipoSoggettoErogatore(),as.getServizio().getNomeSoggettoErogatore()));
					p.setValore(replaceSoggettoErogatore(p.getValore(), as.getServizio().getTipoSoggettoErogatore(),as.getServizio().getNomeSoggettoErogatore()));
				}
			}
			
			if(as.sizeFruitoreList()==1){
				if(as.getServizio()!=null && as.getServizio().getConnettore()!=null){
					for (int j = 0; j < as.getServizio().getConnettore().sizePropertyList(); j++) {
						Property p = as.getServizio().getConnettore().getProperty(j);
						p.setNome(replaceFruitore(p.getNome(), as.getFruitore(0).getTipo(), as.getFruitore(0).getNome()));
						p.setValore(replaceFruitore(p.getValore(), as.getFruitore(0).getTipo(), as.getFruitore(0).getNome()));
					}
				}
			}
			else{
				if(as.sizeFruitoreList()==0){
					
					IDAccordo idAccordoAttuale = null;
					try{
						idAccordoAttuale = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as);
					}catch(Exception e){
						throw new ProtocolException(e.getMessage(),e);
					}
					
					// cerco fruitori di questo accordo
					List<Fruitore> listFruitori = new ArrayList<Fruitore>();
					for (int j = 0; j < archive.getAccordiFruitori().size(); j++) {
						Fruitore fr = archive.getAccordiFruitori().get(j).getFruitore();
						IDAccordo idAccordo = archive.getAccordiFruitori().get(j).getIdAccordoServizioParteSpecifica();
						if(idAccordo.equals(idAccordoAttuale)){
							listFruitori.add(fr);
						}
					}
					if(listFruitori.size()==1){
						if(as.getServizio()!=null && as.getServizio().getConnettore()!=null){
							for (int j = 0; j < as.getServizio().getConnettore().sizePropertyList(); j++) {
								Property p = as.getServizio().getConnettore().getProperty(j);
								p.setNome(replaceFruitore(p.getNome(), listFruitori.get(0).getTipo(), listFruitori.get(0).getNome()));
								p.setValore(replaceFruitore(p.getValore(), listFruitori.get(0).getTipo(), listFruitori.get(0).getNome()));
							}
						}
					}
				}
			}
		}
		
		// Fruitori
		for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
			Fruitore fr = archive.getAccordiFruitori().get(i).getFruitore();
			IDAccordo idAccordo = archive.getAccordiFruitori().get(i).getIdAccordoServizioParteSpecifica();
			String tipoSoggettoErogatore = null;
			String nomeSoggettoErogatore = null;
			if(idAccordo!=null && idAccordo.getSoggettoReferente()!=null){
				tipoSoggettoErogatore = idAccordo.getSoggettoReferente().getTipo();
				nomeSoggettoErogatore = idAccordo.getSoggettoReferente().getNome();
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
		}
	}
}
