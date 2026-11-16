/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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



package it.gov.spcoop.sica.manifest.driver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.openspcoop2.utils.xml.JaxbUtils;
import org.openspcoop2.utils.xml.ValidatoreXSD;

import it.gov.spcoop.sica.manifest.AccordoCooperazione;
import it.gov.spcoop.sica.manifest.AccordoServizio;
import it.gov.spcoop.sica.manifest.AccordoServizioParteComune;
import it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica;
import it.gov.spcoop.sica.manifest.DocumentoCoordinamento;
import it.gov.spcoop.sica.manifest.DocumentoLivelloServizio;
import it.gov.spcoop.sica.manifest.DocumentoSemiformale;
import it.gov.spcoop.sica.manifest.DocumentoSicurezza;
import it.gov.spcoop.sica.manifest.ServizioComposto;
import it.gov.spcoop.sica.manifest.SpecificaCoordinamento;
import it.gov.spcoop.sica.manifest.SpecificaLivelliServizio;
import it.gov.spcoop.sica.manifest.SpecificaSicurezza;


/**
 * Classe utilizzata per lavorare sui manifest inseriti all'interno di un package di un accordo di servizio o di cooperazione 
 *
 *
 * @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
 */

public class XMLUtils  {
	
	private XMLUtils() {}

	private static final String SPECIFICA_SEMIFORMALE_PREFIX = "SpecificaSemiformale[";
	private static final String DIVERSO_DAI_TIPI_ACCETTATI = "] diverso dai tipi accettati:";
	private static final String DESCRIZIONE_NON_FORNITA = "Descrizione non fornita\n";
	private static final String NOME_NON_FORNITO = "Nome non fornito\n";
	private static final String NOME_FORNITO_PREFIX = "Nome fornito [";
	private static final String SUPERA_NUMERO_CARATTERI_CONSENTITI = "] supera il numero di caratteri consentiti: ";
	private static final String DATA_CREAZIONE_NON_FORNITA = "DataCreazione non fornita\n";
	private static final String ALLEGATO_PREFIX = "Allegato[";
	private static final String SENZA_DEFINIZIONE_DOCUMENTO = "] senza la definizione del documento\n";
	private static final String SENZA_LOCATION_DOCUMENTO_SEMIFORMALE = "] senza la location del documento semiformale\n";
	private static final String SENZA_TIPO_DOCUMENTO_SEMIFORMALE = "] senza il tipo del documento semiformale\n";
	private static final String CON_TIPO_DOCUMENTO_SEMIFORMALE = "] con tipo del documento semiformale[";
	private static final String SPECIFICA_SICUREZZA_PREFIX = "SpecificaSicurezza[";
	private static final String SENZA_LOCATION_DOCUMENTO = "] senza la location del documento \n";
	private static final String SENZA_TIPO_DOCUMENTO = "] senza il tipo del documento \n";
	private static final String SPECIFICA_LIVELLI_SERVIZIO_PREFIX = "SpecificaLivelliServizio[";

	
	/** Lock privato per l'inizializzazione (evita di esporre il monitor della classe: SEI CERT LCK00-J) */
	private static final Object INIT_LOCK = new Object();
	/** Validatore XSD */
	static ValidatoreXSD validatoreXSD = null;
	private static void initValidatoreXSD(Logger log) throws Exception{
		synchronized (INIT_LOCK) {
			if(XMLUtils.validatoreXSD==null){
				XMLUtils.validatoreXSD = new ValidatoreXSD(log,XMLUtils.class.getResourceAsStream("/ManifestPackageSICA_originale.xsd"));
			}
		}
	}
	public static ValidatoreXSD getValidatoreXSD(Logger log) throws Exception{
		if(XMLUtils.validatoreXSD==null){
			initValidatoreXSD(log);
		}
		return XMLUtils.validatoreXSD;
	}
	public static boolean validateManifestoAS(AccordoServizio manifestoAS,StringBuilder motivoErroreValidazione,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata){
		
		int size = motivoErroreValidazione.length();
		
		// Vincoli ManifestoAccordo
		if(manifestoAS.getDescrizione()==null){
			motivoErroreValidazione.append(DESCRIZIONE_NON_FORNITA);
		}
		if(manifestoAS.getNome()==null){
			motivoErroreValidazione.append(NOME_NON_FORNITO);
		}else if(lunghezzaNomeAccordoLimitata &&  manifestoAS.getNome().length()>Costanti.LUNGHEZZA_MAX_NOME_ACCORDO){
			motivoErroreValidazione.append(NOME_FORNITO_PREFIX).append(manifestoAS.getNome()).append(SUPERA_NUMERO_CARATTERI_CONSENTITI).append(Costanti.LUNGHEZZA_MAX_NOME_ACCORDO).append("\n");
		}
		
		if(manifestoAS.getDataCreazione()==null){
			motivoErroreValidazione.append(DATA_CREAZIONE_NON_FORNITA);
		}
		if(manifestoAS.getAllegati()!=null){
			for(int i=0; i<manifestoAS.getAllegati().sizeGenericoDocumentoList(); i++){
				if(manifestoAS.getAllegati().getGenericoDocumento(i)==null){
					motivoErroreValidazione.append(ALLEGATO_PREFIX).append(i).append(SENZA_DEFINIZIONE_DOCUMENTO);
				}
			}
		}
		if(manifestoAS.getSpecificaSemiformale()!=null){
			for(int i=0; i<manifestoAS.getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
				DocumentoSemiformale ss = manifestoAS.getSpecificaSemiformale().getDocumentoSemiformale(i);
				if(ss.getBase()==null){
					motivoErroreValidazione.append(SPECIFICA_SEMIFORMALE_PREFIX).append(i).append(SENZA_LOCATION_DOCUMENTO_SEMIFORMALE);
				}
				if(ss.getTipo()==null){
					motivoErroreValidazione.append(SPECIFICA_SEMIFORMALE_PREFIX).append(i).append(SENZA_TIPO_DOCUMENTO_SEMIFORMALE);
				}else{
					if(!TipiDocumentoSemiformale.HTML.toString().equals(ss.getTipo()) &&
							!TipiDocumentoSemiformale.UML.toString().equals(ss.getTipo()) &&
							!TipiDocumentoSemiformale.XML.toString().equals(ss.getTipo()) &&
							!TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString().equals(ss.getTipo()) ){
						motivoErroreValidazione.append(SPECIFICA_SEMIFORMALE_PREFIX).append(i).append(CON_TIPO_DOCUMENTO_SEMIFORMALE).append(ss.getTipo()).append(DIVERSO_DAI_TIPI_ACCETTATI)
								.append(TipiDocumentoSemiformale.HTML.toString()).append(",")
								.append(TipiDocumentoSemiformale.UML.toString()).append(",")
								.append(TipiDocumentoSemiformale.XML.toString()).append(",")
								.append(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString()).append(",").append("\n");
					}
				}
			}
		}
		
		if(manifestoAS.getParteComune()!=null && manifestoAS.getParteSpecifica()!=null){
			motivoErroreValidazione.append("Definita sia una parte comune che una parte specifica\n");
		}else if(manifestoAS.getParteComune()!=null){
			AccordoServizioParteComune parteComune = manifestoAS.getParteComune();
			if(includiInfoRegistroGenerale &&
				parteComune.getPubblicatore()==null){
				motivoErroreValidazione.append("Pubblicatore non definito\n");
			}
			if(parteComune.getSpecificaInterfaccia()==null){
				motivoErroreValidazione.append("SpecificaInterfaccia non definita\n");
			}else{
				if(parteComune.getSpecificaInterfaccia().getInterfacciaConcettuale()==null){
					motivoErroreValidazione.append("SpecificaInterfacciaConcettuale non definita\n");
				}else{
					if(parteComune.getSpecificaInterfaccia().getInterfacciaConcettuale().getBase()==null){
						motivoErroreValidazione.append("contenuto SpecificaInterfacciaConcettuale non definito\n");
					}
					if(parteComune.getSpecificaInterfaccia().getInterfacciaConcettuale().getTipo()!=null && 
							!TipiDocumentoInterfaccia.WSDL.toString().equals(parteComune.getSpecificaInterfaccia().getInterfacciaConcettuale().getTipo().name())){
						motivoErroreValidazione.append("tipo SpecificaInterfacciaConcettuale diverso da ").append(TipiDocumentoInterfaccia.WSDL.toString()).append("\n");
					}
				}
				if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore()==null){
					motivoErroreValidazione.append("SpecificaInterfacciaLogicaLatoErogatore non definita\n");
				}else{
					if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getBase()==null){
						motivoErroreValidazione.append("contenuto SpecificaInterfacciaLogicaLatoErogatore non definito\n");
					}
					if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getTipo()!=null && 
							!TipiDocumentoInterfaccia.WSDL.toString().equals(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getTipo().name())){
						motivoErroreValidazione.append("tipo SpecificaInterfacciaLogicaLatoErogatore diverso da ").append(TipiDocumentoInterfaccia.WSDL.toString()).append("\n");
					}
				}
				if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore()!=null){
					if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getBase()==null){
						motivoErroreValidazione.append("contenuto SpecificaInterfacciaLogicaLatoFruitore non definito\n");
					}
					if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getTipo()!=null && 
							!TipiDocumentoInterfaccia.WSDL.toString().equals(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getTipo().name())){
						motivoErroreValidazione.append("tipo SpecificaInterfacciaLogicaLatoFruitore diverso da ").append(TipiDocumentoInterfaccia.WSDL.toString()).append("\n");
					}
				}
			}
			if(parteComune.getSpecificaConversazione()!=null){
				if(parteComune.getSpecificaConversazione().getConversazioneConcettuale()!=null){
					if(parteComune.getSpecificaConversazione().getConversazioneConcettuale().getBase()==null){
						motivoErroreValidazione.append("contenuto ConversazioneConcettuale non definito\n");
					}
					if(parteComune.getSpecificaConversazione().getConversazioneConcettuale().getTipo()!=null && 
							!TipiDocumentoConversazione.BPEL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneConcettuale().getTipo().name()) &&
							!TipiDocumentoConversazione.WSBL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneConcettuale().getTipo().name())){
						motivoErroreValidazione.append("tipo ConversazioneConcettuale diverso da ").append(TipiDocumentoConversazione.BPEL.toString()).append("/").append(TipiDocumentoConversazione.WSBL.toString()).append("\n");
					}
				}
				if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoErogatore()!=null){
					if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getBase()==null){
						motivoErroreValidazione.append("contenuto ConversazioneLogicaLatoErogatore non definito\n");
					}
					if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo()!=null && 
							!TipiDocumentoConversazione.BPEL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo().name()) &&
							!TipiDocumentoConversazione.WSBL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo().name())){
						motivoErroreValidazione.append("tipo ConversazioneLogicaLatoErogatore diverso da ").append(TipiDocumentoConversazione.BPEL.toString()).append("/").append(TipiDocumentoConversazione.WSBL.toString()).append("\n");
					}
				}
				if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoFruitore()!=null){
					if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getBase()==null){
						motivoErroreValidazione.append("contenuto ConversazioneLogicaLatoFruitore non definito\n");
					}
					if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo()!=null && 
							!TipiDocumentoConversazione.BPEL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo().name()) &&
							!TipiDocumentoConversazione.WSBL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo().name())){
						motivoErroreValidazione.append("tipo ConversazioneLogicaLatoFruitore diverso da ").append(TipiDocumentoConversazione.BPEL.toString()).append("/").append(TipiDocumentoConversazione.WSBL.toString()).append("\n");
					}
				}
			}
				
		}else if(manifestoAS.getParteSpecifica()!=null){
			AccordoServizioParteSpecifica parteSpecifica = manifestoAS.getParteSpecifica();
			if(parteSpecifica.getAdesione()==null){
				motivoErroreValidazione.append("Adesione non definita\n");
			}
			if(includiInfoRegistroGenerale &&
				parteSpecifica.getErogatore()==null){
				motivoErroreValidazione.append("Erogatore non definito\n");
			}
			if(parteSpecifica.getRiferimentoParteComune()==null){
				motivoErroreValidazione.append("RiferimentoParteComune non definito\n");
			}
			if(parteSpecifica.getSpecificaPortiAccesso()!=null){
				if(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoErogatore()!=null){
					if(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoErogatore().getBase()==null){
						motivoErroreValidazione.append("contenuto PortiAccessoErogatore non definito\n");
					}
					if(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoErogatore().getTipo()!=null && 
							!TipiDocumentoInterfaccia.WSDL.toString().equals(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoErogatore().getTipo().name())){
						motivoErroreValidazione.append("tipo PortiAccessoErogatore diverso da ").append(TipiDocumentoInterfaccia.WSDL.toString()).append("\n");
					}
				}
				if(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoFruitore()!=null){
					if(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoFruitore().getBase()==null){
						motivoErroreValidazione.append("contenuto PortiAccessoFruitore non definito\n");
					}
					if(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoFruitore().getTipo()!=null && 
							!TipiDocumentoInterfaccia.WSDL.toString().equals(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoFruitore().getTipo().name())){
						motivoErroreValidazione.append("tipo PortiAccessoFruitore diverso da ").append(TipiDocumentoInterfaccia.WSDL.toString()).append("\n");
					}
				}
			}
			SpecificaSicurezza specificaSicurezza = parteSpecifica.getSpecificaSicurezza();
			if(specificaSicurezza!=null){
				for(int i=0; i<specificaSicurezza.sizeDocumentoSicurezzaList(); i++){
					DocumentoSicurezza docS = specificaSicurezza.getDocumentoSicurezza(i);
					if(docS.getBase()==null){
						motivoErroreValidazione.append(SPECIFICA_SICUREZZA_PREFIX).append(i).append(SENZA_LOCATION_DOCUMENTO);
					}
					if(docS.getTipo()==null){
						motivoErroreValidazione.append(SPECIFICA_SICUREZZA_PREFIX).append(i).append(SENZA_TIPO_DOCUMENTO);
					}else{
						if(!TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString().equals(docS.getTipo()) &&
								!TipiDocumentoSicurezza.WSPOLICY.toString().equals(docS.getTipo())  ){
							motivoErroreValidazione.append(SPECIFICA_SICUREZZA_PREFIX).append(i).append("] con tipo del documento specificaSicurezza[").append(docS.getTipo()).append(DIVERSO_DAI_TIPI_ACCETTATI)
									.append(TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString()).append(",").append(TipiDocumentoSicurezza.WSPOLICY.toString()).append("\n");
						}
					}
				}
			}
			SpecificaLivelliServizio specificaLivelliServizio = parteSpecifica.getSpecificaLivelliServizio();
			if(specificaSicurezza!=null){
				for(int i=0; i<specificaLivelliServizio.sizeDocumentoLivelloServizioList(); i++){
					DocumentoLivelloServizio docL = specificaLivelliServizio.getDocumentoLivelloServizio(i);
					if(docL.getBase()==null){
						motivoErroreValidazione.append(SPECIFICA_LIVELLI_SERVIZIO_PREFIX).append(i).append(SENZA_LOCATION_DOCUMENTO);
					}
					if(docL.getTipo()==null){
						motivoErroreValidazione.append(SPECIFICA_LIVELLI_SERVIZIO_PREFIX).append(i).append(SENZA_TIPO_DOCUMENTO);
					}else{
						if(!TipiDocumentoLivelloServizio.WSAGREEMENT.toString().equals(docL.getTipo()) &&
								!TipiDocumentoLivelloServizio.WSLA.toString().equals(docL.getTipo())  ){
							motivoErroreValidazione.append(SPECIFICA_LIVELLI_SERVIZIO_PREFIX).append(i).append("] con tipo del documento specificaLivelliServizio[").append(docL.getTipo()).append(DIVERSO_DAI_TIPI_ACCETTATI)
									.append(TipiDocumentoLivelloServizio.WSAGREEMENT.toString()).append(",").append(TipiDocumentoLivelloServizio.WSLA.toString()).append("\n");
						}
					}
				}
			}
			
		}else{
			motivoErroreValidazione.append("Una parte comune o una parte specifica non definita\n");
		}
		
		return motivoErroreValidazione.length()==size;
		
	}
	public static boolean validateManifestoAC(AccordoCooperazione manifestoAC,StringBuilder motivoErroreValidazione, boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata){

		int size = motivoErroreValidazione.length();
		
		// Vincoli ManifestoAccordo
		if(manifestoAC.getDescrizione()==null){
			motivoErroreValidazione.append(DESCRIZIONE_NON_FORNITA);
		}
		if(manifestoAC.getNome()==null){
			motivoErroreValidazione.append(NOME_NON_FORNITO);
		}else if(lunghezzaNomeAccordoLimitata && manifestoAC.getNome().length()>Costanti.LUNGHEZZA_MAX_NOME_ACCORDO){
			motivoErroreValidazione.append(NOME_FORNITO_PREFIX).append(manifestoAC.getNome()).append(SUPERA_NUMERO_CARATTERI_CONSENTITI).append(Costanti.LUNGHEZZA_MAX_NOME_ACCORDO).append("\n");
		}
		if(manifestoAC.getDataCreazione()==null){
			motivoErroreValidazione.append(DATA_CREAZIONE_NON_FORNITA);
		}
		if(manifestoAC.getAllegati()!=null){
			for(int i=0; i<manifestoAC.getAllegati().sizeGenericoDocumentoList(); i++){
				if(manifestoAC.getAllegati().getGenericoDocumento(i)==null){
					motivoErroreValidazione.append(ALLEGATO_PREFIX).append(i).append(SENZA_DEFINIZIONE_DOCUMENTO);
				}
			}
		}
		if(manifestoAC.getSpecificaSemiformale()!=null){
			for(int i=0; i<manifestoAC.getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
				DocumentoSemiformale ss = manifestoAC.getSpecificaSemiformale().getDocumentoSemiformale(i);
				if(ss.getBase()==null){
					motivoErroreValidazione.append(SPECIFICA_SEMIFORMALE_PREFIX).append(i).append(SENZA_LOCATION_DOCUMENTO_SEMIFORMALE);
				}
				if(ss.getTipo()==null){
					motivoErroreValidazione.append(SPECIFICA_SEMIFORMALE_PREFIX).append(i).append(SENZA_TIPO_DOCUMENTO_SEMIFORMALE);
				}else{
					if(!TipiDocumentoSemiformale.HTML.toString().equals(ss.getTipo()) &&
							!TipiDocumentoSemiformale.UML.toString().equals(ss.getTipo()) &&
							!TipiDocumentoSemiformale.XML.toString().equals(ss.getTipo()) &&
							!TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString().equals(ss.getTipo()) ){
						motivoErroreValidazione.append(SPECIFICA_SEMIFORMALE_PREFIX).append(i).append(CON_TIPO_DOCUMENTO_SEMIFORMALE).append(ss.getTipo()).append(DIVERSO_DAI_TIPI_ACCETTATI)
								.append(TipiDocumentoSemiformale.HTML.toString()).append(",")
								.append(TipiDocumentoSemiformale.UML.toString()).append(",")
								.append(TipiDocumentoSemiformale.XML.toString()).append(",")
								.append(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString()).append(",").append("\n");
					}
				}
			}
		}
		
		if(includiInfoRegistroGenerale &&
			manifestoAC.getCoordinatore()==null){
			motivoErroreValidazione.append("Coordinatore non definito\n");
		}
		if(manifestoAC.getElencoPartecipanti()==null){
			motivoErroreValidazione.append("ElencoPartecipanti non definito\n");
		}else{
			if(manifestoAC.getElencoPartecipanti().sizePartecipanteList()<2){
				motivoErroreValidazione.append("ElencoPartecipanti <2\n");
			}else{
				for(int i=0; i<manifestoAC.getElencoPartecipanti().sizePartecipanteList(); i++){
					if(manifestoAC.getElencoPartecipanti().getPartecipante(i)==null){
						motivoErroreValidazione.append("ElencoPartecipanti[").append(i).append("] senza il partecipante \n");
					}
				}
			}
		}
		if(includiInfoRegistroGenerale &&
			manifestoAC.getServiziComposti()!=null){
			for(int i=0; i<manifestoAC.getServiziComposti().sizeServizioCompostoList(); i++){
				java.net.URI servComposti = manifestoAC.getServiziComposti().getServizioComposto(i);
				if(servComposti==null){
					motivoErroreValidazione.append("ElencoServiziComposti[").append(i).append("] senza il servizio composto \n");
				}
			}
		}
			
		return motivoErroreValidazione.length()==size;
		
	}
	public static boolean validateManifestoSC(ServizioComposto manifestoSC,StringBuilder motivoErroreValidazione, boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata){

		int size = motivoErroreValidazione.length();
		
		// Vincoli ManifestoAccordo
		if(manifestoSC.getDescrizione()==null){
			motivoErroreValidazione.append(DESCRIZIONE_NON_FORNITA);
		}
		if(manifestoSC.getNome()==null){
			motivoErroreValidazione.append(NOME_NON_FORNITO);
		}else if(lunghezzaNomeAccordoLimitata && manifestoSC.getNome().length()>Costanti.LUNGHEZZA_MAX_NOME_ACCORDO){
			motivoErroreValidazione.append(NOME_FORNITO_PREFIX).append(manifestoSC.getNome()).append(SUPERA_NUMERO_CARATTERI_CONSENTITI).append(Costanti.LUNGHEZZA_MAX_NOME_ACCORDO).append("\n");
		}
		if(manifestoSC.getDataCreazione()==null){
			motivoErroreValidazione.append(DATA_CREAZIONE_NON_FORNITA);
		}
		if(manifestoSC.getAllegati()!=null){
			for(int i=0; i<manifestoSC.getAllegati().sizeGenericoDocumentoList(); i++){
				if(manifestoSC.getAllegati().getGenericoDocumento(i)==null){
					motivoErroreValidazione.append(ALLEGATO_PREFIX).append(i).append(SENZA_DEFINIZIONE_DOCUMENTO);
				}
			}
		}
		if(includiInfoRegistroGenerale &&
			manifestoSC.getPubblicatore()==null){
			motivoErroreValidazione.append("Pubblicatore non definito\n");
		}
		if(manifestoSC.getSpecificaSemiformale()!=null){
			for(int i=0; i<manifestoSC.getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
				DocumentoSemiformale ss = manifestoSC.getSpecificaSemiformale().getDocumentoSemiformale(i);
				if(ss.getBase()==null){
					motivoErroreValidazione.append(SPECIFICA_SEMIFORMALE_PREFIX).append(i).append(SENZA_LOCATION_DOCUMENTO_SEMIFORMALE);
				}
				if(ss.getTipo()==null){
					motivoErroreValidazione.append(SPECIFICA_SEMIFORMALE_PREFIX).append(i).append(SENZA_TIPO_DOCUMENTO_SEMIFORMALE);
				}else{
					if(!TipiDocumentoSemiformale.HTML.toString().equals(ss.getTipo()) &&
							!TipiDocumentoSemiformale.UML.toString().equals(ss.getTipo()) &&
							!TipiDocumentoSemiformale.XML.toString().equals(ss.getTipo()) &&
							!TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString().equals(ss.getTipo()) ){
						motivoErroreValidazione.append(SPECIFICA_SEMIFORMALE_PREFIX).append(i).append(CON_TIPO_DOCUMENTO_SEMIFORMALE).append(ss.getTipo()).append(DIVERSO_DAI_TIPI_ACCETTATI)
								.append(TipiDocumentoSemiformale.HTML.toString()).append(",")
								.append(TipiDocumentoSemiformale.UML.toString()).append(",")
								.append(TipiDocumentoSemiformale.XML.toString()).append(",")
								.append(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString()).append(",").append("\n");
					}
				}
			}
		}
		
		if(manifestoSC.getSpecificaInterfaccia()==null){
			motivoErroreValidazione.append("SpecificaInterfaccia non definita\n");
		}else{
			if(manifestoSC.getSpecificaInterfaccia().getInterfacciaConcettuale()==null){
				motivoErroreValidazione.append("SpecificaInterfacciaConcettuale non definita\n");
			}else{
				if(manifestoSC.getSpecificaInterfaccia().getInterfacciaConcettuale().getBase()==null){
					motivoErroreValidazione.append("contenuto SpecificaInterfacciaConcettuale non definito\n");
				}
				if(manifestoSC.getSpecificaInterfaccia().getInterfacciaConcettuale().getTipo()!=null && 
						!TipiDocumentoInterfaccia.WSDL.toString().equals(manifestoSC.getSpecificaInterfaccia().getInterfacciaConcettuale().getTipo().name())){
					motivoErroreValidazione.append("tipo SpecificaInterfacciaConcettuale diverso da ").append(TipiDocumentoInterfaccia.WSDL.toString()).append("\n");
				}
			}
			if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore()==null){
				motivoErroreValidazione.append("SpecificaInterfacciaLogicaLatoErogatore non definita\n");
			}else{
				if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getBase()==null){
					motivoErroreValidazione.append("contenuto SpecificaInterfacciaLogicaLatoErogatore non definito\n");
				}
				if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getTipo()!=null && 
						!TipiDocumentoInterfaccia.WSDL.toString().equals(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getTipo().name())){
					motivoErroreValidazione.append("tipo SpecificaInterfacciaLogicaLatoErogatore diverso da ").append(TipiDocumentoInterfaccia.WSDL.toString()).append("\n");
				}
			}
			if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore()!=null){
				if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getBase()==null){
					motivoErroreValidazione.append("contenuto SpecificaInterfacciaLogicaLatoFruitore non definito\n");
				}
				if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getTipo()!=null && 
						!TipiDocumentoInterfaccia.WSDL.toString().equals(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getTipo().name())){
					motivoErroreValidazione.append("tipo SpecificaInterfacciaLogicaLatoFruitore diverso da ").append(TipiDocumentoInterfaccia.WSDL.toString()).append("\n");
				}
			}
		}
		if(manifestoSC.getSpecificaConversazione()!=null){
			if(manifestoSC.getSpecificaConversazione().getConversazioneConcettuale()!=null){
				if(manifestoSC.getSpecificaConversazione().getConversazioneConcettuale().getBase()==null){
					motivoErroreValidazione.append("contenuto ConversazioneConcettuale non definito\n");
				}
				if(manifestoSC.getSpecificaConversazione().getConversazioneConcettuale().getTipo()!=null && 
						!TipiDocumentoConversazione.BPEL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneConcettuale().getTipo().name()) &&
						!TipiDocumentoConversazione.WSBL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneConcettuale().getTipo().name())){
					motivoErroreValidazione.append("tipo ConversazioneConcettuale diverso da ").append(TipiDocumentoConversazione.BPEL.toString()).append("/").append(TipiDocumentoConversazione.WSBL.toString()).append("\n");
				}
			}
			if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoErogatore()!=null){
				if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getBase()==null){
					motivoErroreValidazione.append("contenuto ConversazioneLogicaLatoErogatore non definito\n");
				}
				if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo()!=null && 
						!TipiDocumentoConversazione.BPEL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo().name()) &&
						!TipiDocumentoConversazione.WSBL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo().name())){
					motivoErroreValidazione.append("tipo ConversazioneLogicaLatoErogatore diverso da ").append(TipiDocumentoConversazione.BPEL.toString()).append("/").append(TipiDocumentoConversazione.WSBL.toString()).append("\n");
				}
			}
			if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoFruitore()!=null){
				if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getBase()==null){
					motivoErroreValidazione.append("contenuto ConversazioneLogicaLatoFruitore non definito\n");
				}
				if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo()!=null && 
						!TipiDocumentoConversazione.BPEL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo().name()) &&
						!TipiDocumentoConversazione.WSBL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo().name())){
					motivoErroreValidazione.append("tipo ConversazioneLogicaLatoFruitore diverso da ").append(TipiDocumentoConversazione.BPEL.toString()).append("/").append(TipiDocumentoConversazione.WSBL.toString()).append("\n");
				}
			}
		}
		
		if(manifestoSC.getRiferimentoAccordoCooperazione()==null){
			motivoErroreValidazione.append("RiferimentoAccordo non definito\n");
		}
		
		if(manifestoSC.getServiziComponenti()==null){
			motivoErroreValidazione.append("ElencoServiziComponenti non presente \n");
		}else{
			for(int i=0; i<manifestoSC.getServiziComponenti().sizeServizioComponenteList(); i++){
				java.net.URI servComponente = manifestoSC.getServiziComponenti().getServizioComponente(i);
				if(servComponente==null){
					motivoErroreValidazione.append("ElencoServiziComponenti[").append(i).append("] senza il servizio componente \n");
				}
			}
		}
		
		SpecificaCoordinamento specificaCoordinamento = manifestoSC.getSpecificaCoordinamento();
		if(specificaCoordinamento!=null){
			for(int i=0; i<specificaCoordinamento.sizeDocumentoCoordinamentoList(); i++){
				DocumentoCoordinamento docC = specificaCoordinamento.getDocumentoCoordinamento(i);
				if(docC.getBase()==null){
					motivoErroreValidazione.append("SpecificaCoordinamento[").append(i).append(SENZA_LOCATION_DOCUMENTO);
				}
				if(docC.getTipo()==null){
					motivoErroreValidazione.append("SpecificaCoordinamento[").append(i).append(SENZA_TIPO_DOCUMENTO);
				}else{
					if(!TipiDocumentoCoordinamento.BPEL.toString().equals(docC.getTipo()) &&
							!TipiDocumentoCoordinamento.WSCDL.toString().equals(docC.getTipo())){
						motivoErroreValidazione.append("tipo SpecificaCoordinamento[").append(docC.getTipo()).append("] diverso da ").append(TipiDocumentoCoordinamento.BPEL.toString()).append("/").append(TipiDocumentoCoordinamento.WSCDL.toString()).append("\n");
					}
				}
			}
		}
		
		return motivoErroreValidazione.length()==size;
		
	}
	
	
	
	
	
	
	
	/* ----- Unmarshall Manifest dell'accordo di servizio ----- */
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m byte[]
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public static AccordoServizio getManifestoAS(Logger log,byte[] m) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getManifestoAS(log,bin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(bin!=null)
					bin.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m File
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public static AccordoServizio getManifestoAS(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getManifestoAS(log,fin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fin!=null)
					fin.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m String
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public static AccordoServizio getManifestoAS(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getManifestoAS(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m InputStream
	 * @return ManifestoAS
	 * @throws XMLUtilsException
	 */
	public static AccordoServizio getManifestoAS(Logger log,InputStream m) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]reads = new byte[1024];
			int letti = 0;
			while( (letti=m.read(reads)) != -1){
				bout.write(reads, 0, letti);
			}
			bout.flush();
			bout.close();
			byte [] xml = bout.toByteArray();
			ByteArrayInputStream binValidazione = new ByteArrayInputStream(xml);

			ValidatoreXSD validatoreXSD = XMLUtils.getValidatoreXSD(log);
			validatoreXSD.valida(binValidazione);
			
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			
			return (AccordoServizio) JaxbUtils.xmlToObj(binTrasformazione, AccordoServizio.class);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	/* ----- Marshall Manifest dell'accordo di servizio ----- */
	public static void generateManifestoAS(AccordoServizio manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,File out) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validateManifestoAS(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			
			JaxbUtils.objToXml(out.getName(), AccordoServizio.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateManifestoAS(AccordoServizio manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,String fileName) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validateManifestoAS(manifest, risultatoValidazione, includiInfoRegistroGenerale,lunghezzaNomeAccordoLimitata)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			
			JaxbUtils.objToXml(fileName, AccordoServizio.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateManifestoAS(AccordoServizio manifest, boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			XMLUtils.generateManifestoAS(manifest,includiInfoRegistroGenerale,lunghezzaNomeAccordoLimitata,bout);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateManifestoAS(AccordoServizio manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,OutputStream out) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validateManifestoAS(manifest, risultatoValidazione, includiInfoRegistroGenerale,lunghezzaNomeAccordoLimitata)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			
			JaxbUtils.objToXml(out, AccordoServizio.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/* ----- Unmarshall Manifest dell'accordo di cooperazione ----- */
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m byte[]
	 * @return ManifestAC
	 * @throws XMLUtilsException
	 */
	public static AccordoCooperazione getManifestoAC(Logger log,byte[] m) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getManifestoAC(log,bin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(bin!=null)
					bin.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m File
	 * @return ManifestAC
	 * @throws XMLUtilsException
	 */
	public static AccordoCooperazione getManifestoAC(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getManifestoAC(log,fin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fin!=null)
					fin.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m String
	 * @return ManifestAC
	 * @throws XMLUtilsException
	 */
	public static AccordoCooperazione getManifestoAC(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getManifestoAC(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m InputStream
	 * @return ManifestAC
	 * @throws XMLUtilsException
	 */
	public static AccordoCooperazione getManifestoAC(Logger log,InputStream m) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]reads = new byte[1024];
			int letti = 0;
			while( (letti=m.read(reads)) != -1){
				bout.write(reads, 0, letti);
			}
			bout.flush();
			bout.close();
			byte [] xml = bout.toByteArray();
			ByteArrayInputStream binValidazione = new ByteArrayInputStream(xml);
			
			ValidatoreXSD validatoreXSD = XMLUtils.getValidatoreXSD(log);
			validatoreXSD.valida(binValidazione);
			
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			
			return (AccordoCooperazione) JaxbUtils.xmlToObj(binTrasformazione, AccordoCooperazione.class);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	
	
	
	
	
	
	
	/* ----- Marshall Manifest dell'accordo di cooperazione ----- */
	public static void generateManifestoAC(AccordoCooperazione manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,File out) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validateManifestoAC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			
			JaxbUtils.objToXml(out.getName(), AccordoCooperazione.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateManifestoAC(AccordoCooperazione manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,String fileName) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validateManifestoAC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			
			JaxbUtils.objToXml(fileName, AccordoCooperazione.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateManifestoAC(AccordoCooperazione manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			XMLUtils.generateManifestoAC(manifest,includiInfoRegistroGenerale,lunghezzaNomeAccordoLimitata,bout);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateManifestoAC(AccordoCooperazione manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,OutputStream out) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validateManifestoAC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			
			JaxbUtils.objToXml(out, AccordoCooperazione.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	/* ----- Unmarshall Manifest del servizio composto ----- */
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m byte[]
	 * @return ManifestAC
	 * @throws XMLUtilsException
	 */
	public static ServizioComposto getManifestoSC(Logger log,byte[] m) throws XMLUtilsException{
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(m);
			return XMLUtils.getManifestoSC(log,bin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(bin!=null)
					bin.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m File
	 * @return ManifestAC
	 * @throws XMLUtilsException
	 */
	public static ServizioComposto getManifestoSC(Logger log,File m) throws XMLUtilsException{
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(m);
			return XMLUtils.getManifestoSC(log,fin);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}finally{
			try{
				if(fin!=null)
					fin.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m String
	 * @return ManifestAC
	 * @throws XMLUtilsException
	 */
	public static ServizioComposto getManifestoSC(Logger log,String m) throws XMLUtilsException{
		return XMLUtils.getManifestoSC(log,m.getBytes());
	}
	
	/**
	 * Ritorna la rappresentazione java di un Manifest di un accordo di servizio
	 * 
	 * @param m InputStream
	 * @return ManifestAC
	 * @throws XMLUtilsException
	 */
	public static ServizioComposto getManifestoSC(Logger log,InputStream m) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]reads = new byte[1024];
			int letti = 0;
			while( (letti=m.read(reads)) != -1){
				bout.write(reads, 0, letti);
			}
			bout.flush();
			bout.close();
			byte [] xml = bout.toByteArray();
			ByteArrayInputStream binValidazione = new ByteArrayInputStream(xml);
			
			ValidatoreXSD validatoreXSD = XMLUtils.getValidatoreXSD(log);
			validatoreXSD.valida(binValidazione);
			
			ByteArrayInputStream binTrasformazione = new ByteArrayInputStream(xml);
			
			return (ServizioComposto) JaxbUtils.xmlToObj(binTrasformazione, ServizioComposto.class);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	/* ----- Marshall Manifest dell'accordo di servizio ----- */
	public static void generateManifestoSC(ServizioComposto manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata, File out) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validateManifestoSC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			
			JaxbUtils.objToXml(out.getName(), ServizioComposto.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateManifestoSC(ServizioComposto manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,String fileName) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validateManifestoSC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)){
				throw new Exception(risultatoValidazione.toString());
			}
			
			JaxbUtils.objToXml(fileName, ServizioComposto.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] generateManifestoSC(ServizioComposto manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata) throws XMLUtilsException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			XMLUtils.generateManifestoSC(manifest,includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata,bout);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	public static void generateManifestoSC(ServizioComposto manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,OutputStream out) throws XMLUtilsException{
		try{
			StringBuilder risultatoValidazione = new StringBuilder();
			if(!XMLUtils.validateManifestoSC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)){
				throw new XMLUtilsException(risultatoValidazione.toString());
			}
			
			JaxbUtils.objToXml(out, ServizioComposto.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
}