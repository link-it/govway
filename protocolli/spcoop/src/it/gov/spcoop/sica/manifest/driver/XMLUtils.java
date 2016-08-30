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



package it.gov.spcoop.sica.manifest.driver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.openspcoop2.utils.xml.JiBXUtils;
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

	
	/** Validatore XSD */
	static ValidatoreXSD validatoreXSD = null;
	public static synchronized ValidatoreXSD getValidatoreXSD(Logger log) throws Exception{
		if(XMLUtils.validatoreXSD==null){
			XMLUtils.validatoreXSD = new ValidatoreXSD(log,XMLUtils.class.getResourceAsStream("/ManifestPackageSICA_originale.xsd"));
		}
		return XMLUtils.validatoreXSD;
	}
	public static boolean validateManifestoAS(AccordoServizio manifestoAS,StringBuffer motivoErroreValidazione,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata){
		
		int size = motivoErroreValidazione.length();
		
		// Vincoli ManifestoAccordo
		if(manifestoAS.getDescrizione()==null){
			motivoErroreValidazione.append("Descrizione non fornita\n");
		}
		if(manifestoAS.getNome()==null){
			motivoErroreValidazione.append("Nome non fornito\n");
		}else if(lunghezzaNomeAccordoLimitata &&  manifestoAS.getNome().length()>Costanti.LUNGHEZZA_MAX_NOME_ACCORDO){
			motivoErroreValidazione.append("Nome fornito ["+manifestoAS.getNome()+"] supera il numero di caratteri consentiti: "+Costanti.LUNGHEZZA_MAX_NOME_ACCORDO+"\n");
		}
		
		if(manifestoAS.getDataCreazione()==null){
			motivoErroreValidazione.append("DataCreazione non fornita\n");
		}
		if(manifestoAS.getAllegati()!=null){
			for(int i=0; i<manifestoAS.getAllegati().sizeGenericoDocumentoList(); i++){
				if(manifestoAS.getAllegati().getGenericoDocumento(i)==null){
					motivoErroreValidazione.append("Allegato["+i+"] senza la definizione del documento\n");
				}
			}
		}
		if(manifestoAS.getSpecificaSemiformale()!=null){
			for(int i=0; i<manifestoAS.getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
				DocumentoSemiformale ss = manifestoAS.getSpecificaSemiformale().getDocumentoSemiformale(i);
				if(ss.getBase()==null){
					motivoErroreValidazione.append("SpecificaSemiformale["+i+"] senza la location del documento semiformale\n");
				}
				if(ss.getTipo()==null){
					motivoErroreValidazione.append("SpecificaSemiformale["+i+"] senza il tipo del documento semiformale\n");
				}else{
					if(TipiDocumentoSemiformale.HTML.toString().equals(ss.getTipo())==false &&
							TipiDocumentoSemiformale.UML.toString().equals(ss.getTipo())==false &&
							TipiDocumentoSemiformale.XML.toString().equals(ss.getTipo())==false &&
							TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString().equals(ss.getTipo())==false ){
						motivoErroreValidazione.append("SpecificaSemiformale["+i+"] con tipo del documento semiformale["+ss.getTipo()+"] diverso dai tipi accettati:"+
								TipiDocumentoSemiformale.HTML.toString()+","+
								TipiDocumentoSemiformale.UML.toString()+","+
								TipiDocumentoSemiformale.XML.toString()+","+
								TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString()+","+"\n");
					}
				}
			}
		}
		
		if(manifestoAS.getParteComune()!=null && manifestoAS.getParteSpecifica()!=null){
			motivoErroreValidazione.append("Definita sia una parte comune che una parte specifica\n");
		}else if(manifestoAS.getParteComune()!=null){
			AccordoServizioParteComune parteComune = manifestoAS.getParteComune();
			if(includiInfoRegistroGenerale){
				if(parteComune.getPubblicatore()==null){
					motivoErroreValidazione.append("Pubblicatore non definito\n");
				}
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
							TipiDocumentoInterfaccia.WSDL.toString().equals(parteComune.getSpecificaInterfaccia().getInterfacciaConcettuale().getTipo())==false){
						motivoErroreValidazione.append("tipo SpecificaInterfacciaConcettuale diverso da "+TipiDocumentoInterfaccia.WSDL.toString()+"\n");
					}
				}
				if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore()==null){
					motivoErroreValidazione.append("SpecificaInterfacciaLogicaLatoErogatore non definita\n");
				}else{
					if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getBase()==null){
						motivoErroreValidazione.append("contenuto SpecificaInterfacciaLogicaLatoErogatore non definito\n");
					}
					if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getTipo()!=null && 
							TipiDocumentoInterfaccia.WSDL.toString().equals(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getTipo())==false){
						motivoErroreValidazione.append("tipo SpecificaInterfacciaLogicaLatoErogatore diverso da "+TipiDocumentoInterfaccia.WSDL.toString()+"\n");
					}
				}
				if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore()!=null){
					if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getBase()==null){
						motivoErroreValidazione.append("contenuto SpecificaInterfacciaLogicaLatoFruitore non definito\n");
					}
					if(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getTipo()!=null && 
							TipiDocumentoInterfaccia.WSDL.toString().equals(parteComune.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getTipo())==false){
						motivoErroreValidazione.append("tipo SpecificaInterfacciaLogicaLatoFruitore diverso da "+TipiDocumentoInterfaccia.WSDL.toString()+"\n");
					}
				}
			}
			if(parteComune.getSpecificaConversazione()!=null){
				if(parteComune.getSpecificaConversazione().getConversazioneConcettuale()!=null){
					if(parteComune.getSpecificaConversazione().getConversazioneConcettuale().getBase()==null){
						motivoErroreValidazione.append("contenuto ConversazioneConcettuale non definito\n");
					}
					if(parteComune.getSpecificaConversazione().getConversazioneConcettuale().getTipo()!=null && 
							TipiDocumentoConversazione.BPEL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneConcettuale().getTipo())==false &&
							TipiDocumentoConversazione.WSBL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneConcettuale().getTipo())==false){
						motivoErroreValidazione.append("tipo ConversazioneConcettuale diverso da "+TipiDocumentoConversazione.BPEL.toString()+"/"+TipiDocumentoConversazione.WSBL.toString()+"\n");
					}
				}
				if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoErogatore()!=null){
					if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getBase()==null){
						motivoErroreValidazione.append("contenuto ConversazioneLogicaLatoErogatore non definito\n");
					}
					if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo()!=null && 
							TipiDocumentoConversazione.BPEL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo())==false &&
							TipiDocumentoConversazione.WSBL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo())==false){
						motivoErroreValidazione.append("tipo ConversazioneLogicaLatoErogatore diverso da "+TipiDocumentoConversazione.BPEL.toString()+"/"+TipiDocumentoConversazione.WSBL.toString()+"\n");
					}
				}
				if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoFruitore()!=null){
					if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getBase()==null){
						motivoErroreValidazione.append("contenuto ConversazioneLogicaLatoFruitore non definito\n");
					}
					if(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo()!=null && 
							TipiDocumentoConversazione.BPEL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo())==false &&
							TipiDocumentoConversazione.WSBL.toString().equals(parteComune.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo())==false){
						motivoErroreValidazione.append("tipo ConversazioneLogicaLatoFruitore diverso da "+TipiDocumentoConversazione.BPEL.toString()+"/"+TipiDocumentoConversazione.WSBL.toString()+"\n");
					}
				}
			}
				
		}else if(manifestoAS.getParteSpecifica()!=null){
			AccordoServizioParteSpecifica parteSpecifica = manifestoAS.getParteSpecifica();
			if(parteSpecifica.getAdesione()==null){
				motivoErroreValidazione.append("Adesione non definita\n");
			}
			if(includiInfoRegistroGenerale){
				if(parteSpecifica.getErogatore()==null){
					motivoErroreValidazione.append("Erogatore non definito\n");
				}
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
							TipiDocumentoInterfaccia.WSDL.toString().equals(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoErogatore().getTipo())==false){
						motivoErroreValidazione.append("tipo PortiAccessoErogatore diverso da "+TipiDocumentoInterfaccia.WSDL.toString()+"\n");
					}
				}
				if(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoFruitore()!=null){
					if(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoFruitore().getBase()==null){
						motivoErroreValidazione.append("contenuto PortiAccessoFruitore non definito\n");
					}
					if(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoFruitore().getTipo()!=null && 
							TipiDocumentoInterfaccia.WSDL.toString().equals(parteSpecifica.getSpecificaPortiAccesso().getPortiAccessoFruitore().getTipo())==false){
						motivoErroreValidazione.append("tipo PortiAccessoFruitore diverso da "+TipiDocumentoInterfaccia.WSDL.toString()+"\n");
					}
				}
			}
			SpecificaSicurezza specificaSicurezza = parteSpecifica.getSpecificaSicurezza();
			if(specificaSicurezza!=null){
				for(int i=0; i<specificaSicurezza.sizeDocumentoSicurezzaList(); i++){
					DocumentoSicurezza docS = specificaSicurezza.getDocumentoSicurezza(i);
					if(docS.getBase()==null){
						motivoErroreValidazione.append("SpecificaSicurezza["+i+"] senza la location del documento \n");
					}
					if(docS.getTipo()==null){
						motivoErroreValidazione.append("SpecificaSicurezza["+i+"] senza il tipo del documento \n");
					}else{
						if(TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString().equals(docS.getTipo())==false &&
								TipiDocumentoSicurezza.WSPOLICY.toString().equals(docS.getTipo())==false  ){
							motivoErroreValidazione.append("SpecificaSicurezza["+i+"] con tipo del documento specificaSicurezza["+docS.getTipo()+"] diverso dai tipi accettati:"+
									TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString()+","+TipiDocumentoSicurezza.WSPOLICY.toString()+"\n");
						}
					}
				}
			}
			SpecificaLivelliServizio specificaLivelliServizio = parteSpecifica.getSpecificaLivelliServizio();
			if(specificaSicurezza!=null){
				for(int i=0; i<specificaLivelliServizio.sizeDocumentoLivelloServizioList(); i++){
					DocumentoLivelloServizio docL = specificaLivelliServizio.getDocumentoLivelloServizio(i);
					if(docL.getBase()==null){
						motivoErroreValidazione.append("SpecificaLivelliServizio["+i+"] senza la location del documento \n");
					}
					if(docL.getTipo()==null){
						motivoErroreValidazione.append("SpecificaLivelliServizio["+i+"] senza il tipo del documento \n");
					}else{
						if(TipiDocumentoLivelloServizio.WSAGREEMENT.toString().equals(docL.getTipo())==false &&
								TipiDocumentoLivelloServizio.WSLA.toString().equals(docL.getTipo())==false  ){
							motivoErroreValidazione.append("SpecificaLivelliServizio["+i+"] con tipo del documento specificaLivelliServizio["+docL.getTipo()+"] diverso dai tipi accettati:"+
									TipiDocumentoLivelloServizio.WSAGREEMENT.toString()+","+TipiDocumentoLivelloServizio.WSLA.toString()+"\n");
						}
					}
				}
			}
			
		}else{
			motivoErroreValidazione.append("Una parte comune o una parte specifica non definita\n");
		}
		
		if(motivoErroreValidazione.length()!=size)
			return false;
		else
			return true;
		
	}
	public static boolean validateManifestoAC(AccordoCooperazione manifestoAC,StringBuffer motivoErroreValidazione, boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata){

		int size = motivoErroreValidazione.length();
		
		// Vincoli ManifestoAccordo
		if(manifestoAC.getDescrizione()==null){
			motivoErroreValidazione.append("Descrizione non fornita\n");
		}
		if(manifestoAC.getNome()==null){
			motivoErroreValidazione.append("Nome non fornito\n");
		}else if(lunghezzaNomeAccordoLimitata && manifestoAC.getNome().length()>Costanti.LUNGHEZZA_MAX_NOME_ACCORDO){
			motivoErroreValidazione.append("Nome fornito ["+manifestoAC.getNome()+"] supera il numero di caratteri consentiti: "+Costanti.LUNGHEZZA_MAX_NOME_ACCORDO+"\n");
		}
		if(manifestoAC.getDataCreazione()==null){
			motivoErroreValidazione.append("DataCreazione non fornita\n");
		}
		if(manifestoAC.getAllegati()!=null){
			for(int i=0; i<manifestoAC.getAllegati().sizeGenericoDocumentoList(); i++){
				if(manifestoAC.getAllegati().getGenericoDocumento(i)==null){
					motivoErroreValidazione.append("Allegato["+i+"] senza la definizione del documento\n");
				}
			}
		}
		if(manifestoAC.getSpecificaSemiformale()!=null){
			for(int i=0; i<manifestoAC.getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
				DocumentoSemiformale ss = manifestoAC.getSpecificaSemiformale().getDocumentoSemiformale(i);
				if(ss.getBase()==null){
					motivoErroreValidazione.append("SpecificaSemiformale["+i+"] senza la location del documento semiformale\n");
				}
				if(ss.getTipo()==null){
					motivoErroreValidazione.append("SpecificaSemiformale["+i+"] senza il tipo del documento semiformale\n");
				}else{
					if(TipiDocumentoSemiformale.HTML.toString().equals(ss.getTipo())==false &&
							TipiDocumentoSemiformale.UML.toString().equals(ss.getTipo())==false &&
							TipiDocumentoSemiformale.XML.toString().equals(ss.getTipo())==false &&
							TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString().equals(ss.getTipo())==false ){
						motivoErroreValidazione.append("SpecificaSemiformale["+i+"] con tipo del documento semiformale["+ss.getTipo()+"] diverso dai tipi accettati:"+
								TipiDocumentoSemiformale.HTML.toString()+","+
								TipiDocumentoSemiformale.UML.toString()+","+
								TipiDocumentoSemiformale.XML.toString()+","+
								TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString()+","+"\n");
					}
				}
			}
		}
		
		if(includiInfoRegistroGenerale){
			if(manifestoAC.getCoordinatore()==null){
				motivoErroreValidazione.append("Coordinatore non definito\n");
			}
		}
		if(manifestoAC.getElencoPartecipanti()==null){
			motivoErroreValidazione.append("ElencoPartecipanti non definito\n");
		}else{
			if(manifestoAC.getElencoPartecipanti().sizePartecipanteList()<2){
				motivoErroreValidazione.append("ElencoPartecipanti <2\n");
			}else{
				for(int i=0; i<manifestoAC.getElencoPartecipanti().sizePartecipanteList(); i++){
					if(manifestoAC.getElencoPartecipanti().getPartecipante(i)==null){
						motivoErroreValidazione.append("ElencoPartecipanti["+i+"] senza il partecipante \n");
					}
				}
			}
		}
		if(includiInfoRegistroGenerale){
			if(manifestoAC.getServiziComposti()!=null){
				for(int i=0; i<manifestoAC.getServiziComposti().sizeServizioCompostoList(); i++){
					String servComposti = manifestoAC.getServiziComposti().getServizioComposto(i);
					if(servComposti==null){
						motivoErroreValidazione.append("ElencoServiziComposti["+i+"] senza il servizio composto \n");
					}
				}
			}
		}
			
		if(motivoErroreValidazione.length()!=size)
			return false;
		else
			return true;
		
	}
	public static boolean validateManifestoSC(ServizioComposto manifestoSC,StringBuffer motivoErroreValidazione, boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata){

		int size = motivoErroreValidazione.length();
		
		// Vincoli ManifestoAccordo
		if(manifestoSC.getDescrizione()==null){
			motivoErroreValidazione.append("Descrizione non fornita\n");
		}
		if(manifestoSC.getNome()==null){
			motivoErroreValidazione.append("Nome non fornito\n");
		}else if(lunghezzaNomeAccordoLimitata && manifestoSC.getNome().length()>Costanti.LUNGHEZZA_MAX_NOME_ACCORDO){
			motivoErroreValidazione.append("Nome fornito ["+manifestoSC.getNome()+"] supera il numero di caratteri consentiti: "+Costanti.LUNGHEZZA_MAX_NOME_ACCORDO+"\n");
		}
		if(manifestoSC.getDataCreazione()==null){
			motivoErroreValidazione.append("DataCreazione non fornita\n");
		}
		if(manifestoSC.getAllegati()!=null){
			for(int i=0; i<manifestoSC.getAllegati().sizeGenericoDocumentoList(); i++){
				if(manifestoSC.getAllegati().getGenericoDocumento(i)==null){
					motivoErroreValidazione.append("Allegato["+i+"] senza la definizione del documento\n");
				}
			}
		}
		if(includiInfoRegistroGenerale){
			if(manifestoSC.getPubblicatore()==null){
				motivoErroreValidazione.append("Pubblicatore non definito\n");
			}
		}
		if(manifestoSC.getSpecificaSemiformale()!=null){
			for(int i=0; i<manifestoSC.getSpecificaSemiformale().sizeDocumentoSemiformaleList(); i++){
				DocumentoSemiformale ss = manifestoSC.getSpecificaSemiformale().getDocumentoSemiformale(i);
				if(ss.getBase()==null){
					motivoErroreValidazione.append("SpecificaSemiformale["+i+"] senza la location del documento semiformale\n");
				}
				if(ss.getTipo()==null){
					motivoErroreValidazione.append("SpecificaSemiformale["+i+"] senza il tipo del documento semiformale\n");
				}else{
					if(TipiDocumentoSemiformale.HTML.toString().equals(ss.getTipo())==false &&
							TipiDocumentoSemiformale.UML.toString().equals(ss.getTipo())==false &&
							TipiDocumentoSemiformale.XML.toString().equals(ss.getTipo())==false &&
							TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString().equals(ss.getTipo())==false ){
						motivoErroreValidazione.append("SpecificaSemiformale["+i+"] con tipo del documento semiformale["+ss.getTipo()+"] diverso dai tipi accettati:"+
								TipiDocumentoSemiformale.HTML.toString()+","+
								TipiDocumentoSemiformale.UML.toString()+","+
								TipiDocumentoSemiformale.XML.toString()+","+
								TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString()+","+"\n");
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
						TipiDocumentoInterfaccia.WSDL.toString().equals(manifestoSC.getSpecificaInterfaccia().getInterfacciaConcettuale().getTipo())==false){
					motivoErroreValidazione.append("tipo SpecificaInterfacciaConcettuale diverso da "+TipiDocumentoInterfaccia.WSDL.toString()+"\n");
				}
			}
			if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore()==null){
				motivoErroreValidazione.append("SpecificaInterfacciaLogicaLatoErogatore non definita\n");
			}else{
				if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getBase()==null){
					motivoErroreValidazione.append("contenuto SpecificaInterfacciaLogicaLatoErogatore non definito\n");
				}
				if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getTipo()!=null && 
						TipiDocumentoInterfaccia.WSDL.toString().equals(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoErogatore().getTipo())==false){
					motivoErroreValidazione.append("tipo SpecificaInterfacciaLogicaLatoErogatore diverso da "+TipiDocumentoInterfaccia.WSDL.toString()+"\n");
				}
			}
			if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore()!=null){
				if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getBase()==null){
					motivoErroreValidazione.append("contenuto SpecificaInterfacciaLogicaLatoFruitore non definito\n");
				}
				if(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getTipo()!=null && 
						TipiDocumentoInterfaccia.WSDL.toString().equals(manifestoSC.getSpecificaInterfaccia().getInterfacciaLogicaLatoFruitore().getTipo())==false){
					motivoErroreValidazione.append("tipo SpecificaInterfacciaLogicaLatoFruitore diverso da "+TipiDocumentoInterfaccia.WSDL.toString()+"\n");
				}
			}
		}
		if(manifestoSC.getSpecificaConversazione()!=null){
			if(manifestoSC.getSpecificaConversazione().getConversazioneConcettuale()!=null){
				if(manifestoSC.getSpecificaConversazione().getConversazioneConcettuale().getBase()==null){
					motivoErroreValidazione.append("contenuto ConversazioneConcettuale non definito\n");
				}
				if(manifestoSC.getSpecificaConversazione().getConversazioneConcettuale().getTipo()!=null && 
						TipiDocumentoConversazione.BPEL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneConcettuale().getTipo())==false &&
						TipiDocumentoConversazione.WSBL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneConcettuale().getTipo())==false){
					motivoErroreValidazione.append("tipo ConversazioneConcettuale diverso da "+TipiDocumentoConversazione.BPEL.toString()+"/"+TipiDocumentoConversazione.WSBL.toString()+"\n");
				}
			}
			if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoErogatore()!=null){
				if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getBase()==null){
					motivoErroreValidazione.append("contenuto ConversazioneLogicaLatoErogatore non definito\n");
				}
				if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo()!=null && 
						TipiDocumentoConversazione.BPEL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo())==false &&
						TipiDocumentoConversazione.WSBL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoErogatore().getTipo())==false){
					motivoErroreValidazione.append("tipo ConversazioneLogicaLatoErogatore diverso da "+TipiDocumentoConversazione.BPEL.toString()+"/"+TipiDocumentoConversazione.WSBL.toString()+"\n");
				}
			}
			if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoFruitore()!=null){
				if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getBase()==null){
					motivoErroreValidazione.append("contenuto ConversazioneLogicaLatoFruitore non definito\n");
				}
				if(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo()!=null && 
						TipiDocumentoConversazione.BPEL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo())==false &&
						TipiDocumentoConversazione.WSBL.toString().equals(manifestoSC.getSpecificaConversazione().getConversazioneLogicaLatoFruitore().getTipo())==false){
					motivoErroreValidazione.append("tipo ConversazioneLogicaLatoFruitore diverso da "+TipiDocumentoConversazione.BPEL.toString()+"/"+TipiDocumentoConversazione.WSBL.toString()+"\n");
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
				String servComponente = manifestoSC.getServiziComponenti().getServizioComponente(i);
				if(servComponente==null){
					motivoErroreValidazione.append("ElencoServiziComponenti["+i+"] senza il servizio componente \n");
				}
			}
		}
		
		SpecificaCoordinamento specificaCoordinamento = manifestoSC.getSpecificaCoordinamento();
		if(specificaCoordinamento!=null){
			for(int i=0; i<specificaCoordinamento.sizeDocumentoCoordinamentoList(); i++){
				DocumentoCoordinamento docC = specificaCoordinamento.getDocumentoCoordinamento(i);
				if(docC.getBase()==null){
					motivoErroreValidazione.append("SpecificaCoordinamento["+i+"] senza la location del documento \n");
				}
				if(docC.getTipo()==null){
					motivoErroreValidazione.append("SpecificaCoordinamento["+i+"] senza il tipo del documento \n");
				}else{
					if(TipiDocumentoCoordinamento.BPEL.toString().equals(docC.getTipo())==false &&
							TipiDocumentoCoordinamento.WSCDL.toString().equals(docC.getTipo())==false){
						motivoErroreValidazione.append("tipo SpecificaCoordinamento["+docC.getTipo()+"] diverso da "+TipiDocumentoCoordinamento.BPEL.toString()+"/"+TipiDocumentoCoordinamento.WSCDL.toString()+"\n");
					}
				}
			}
		}
		
		if(motivoErroreValidazione.length()!=size)
			return false;
		else
			return true;
		
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
			}catch(Exception eClose){}
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
			}catch(Exception eClose){}
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
			
			return (AccordoServizio) JiBXUtils.xmlToObj(binTrasformazione, AccordoServizio.class);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	/* ----- Marshall Manifest dell'accordo di servizio ----- */
	public static void generateManifestoAS(AccordoServizio manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,File out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validateManifestoAS(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			
			JiBXUtils.objToXml(out.getName(), AccordoServizio.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateManifestoAS(AccordoServizio manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,String fileName) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validateManifestoAS(manifest, risultatoValidazione, includiInfoRegistroGenerale,lunghezzaNomeAccordoLimitata)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			
			JiBXUtils.objToXml(fileName, AccordoServizio.class, manifest);
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
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validateManifestoAS(manifest, risultatoValidazione, includiInfoRegistroGenerale,lunghezzaNomeAccordoLimitata)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			
			JiBXUtils.objToXml(out, AccordoServizio.class, manifest);
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
			}catch(Exception eClose){}
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
			}catch(Exception eClose){}
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
			
			return (AccordoCooperazione) JiBXUtils.xmlToObj(binTrasformazione, AccordoCooperazione.class);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}

	
	
	
	
	
	
	
	/* ----- Marshall Manifest dell'accordo di cooperazione ----- */
	public static void generateManifestoAC(AccordoCooperazione manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,File out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validateManifestoAC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			
			JiBXUtils.objToXml(out.getName(), AccordoCooperazione.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateManifestoAC(AccordoCooperazione manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,String fileName) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validateManifestoAC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			
			JiBXUtils.objToXml(fileName, AccordoCooperazione.class, manifest);
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
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validateManifestoAC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			
			JiBXUtils.objToXml(out, AccordoCooperazione.class, manifest);
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
			}catch(Exception eClose){}
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
			}catch(Exception eClose){}
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
			
			return (ServizioComposto) JiBXUtils.xmlToObj(binTrasformazione, ServizioComposto.class);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	/* ----- Marshall Manifest dell'accordo di servizio ----- */
	public static void generateManifestoSC(ServizioComposto manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata, File out) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validateManifestoSC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			
			JiBXUtils.objToXml(out.getName(), ServizioComposto.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
	public static void generateManifestoSC(ServizioComposto manifest,boolean includiInfoRegistroGenerale,boolean lunghezzaNomeAccordoLimitata,String fileName) throws XMLUtilsException{
		try{
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validateManifestoSC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			
			JiBXUtils.objToXml(fileName, ServizioComposto.class, manifest);
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
			StringBuffer risultatoValidazione = new StringBuffer();
			if(XMLUtils.validateManifestoSC(manifest, risultatoValidazione, includiInfoRegistroGenerale, lunghezzaNomeAccordoLimitata)==false){
				throw new Exception(risultatoValidazione.toString());
			}
			
			JiBXUtils.objToXml(out, ServizioComposto.class, manifest);
		}catch(Exception e){
			throw new XMLUtilsException(e.getMessage(),e);
		}
	}
	
}