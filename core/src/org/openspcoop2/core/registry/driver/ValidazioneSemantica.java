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


package org.openspcoop2.core.registry.driver;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipiDocumentoCoordinamento;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.ServizioAzione;
import org.openspcoop2.core.registry.ServizioAzioneFruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.utils.regexp.RegExpUtilities;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;


/**
 * Validazione Semantica
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneSemantica {

	/** Registro servizi */
	private org.openspcoop2.core.registry.RegistroServizi registro = null;
	/** Lista in cui scrivo le anomalie riscontrate */
	private List<String> errori = new ArrayList<String>();
	/** Logger */
	private Logger log = null;
	/** Controllo url */
	private boolean checkEsistenzaFileDefinitoTramiteURI = false;
	
	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();


	/** Lista di tipi di connettori validi */
	private List<String> tipoConnettori = new ArrayList<String>();
	/** Lista dei tipi di connettori ammessi */
	private String getTipoConnettori(){
		StringBuffer bf = new StringBuffer();
		for(int i=0; i<this.tipoConnettori.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoConnettori.get(i));
		}
		return bf.toString();
	}

	/** Lista di tipi di soggetti validi */
	private List<String> tipoSoggetti = new ArrayList<String>();
	/** Lista dei tipi di soggetti ammessi */
	private String getTipoSoggetti(){
		StringBuffer bf = new StringBuffer();
		for(int i=0; i<this.tipoSoggetti.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoSoggetti.get(i));
		}
		return bf.toString();
	}

	/** Lista di tipi di servizi validi */
	private List<String> tipoServizi = new ArrayList<String>();
	/** Lista dei tipi di servizi ammessi */
	private String getTipoServizi(){
		StringBuffer bf = new StringBuffer();
		for(int i=0; i<this.tipoServizi.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.tipoServizi.get(i));
		}
		return bf.toString();
	}


	public ValidazioneSemantica(org.openspcoop2.core.registry.RegistroServizi registro,boolean checkEsistenzaFileDefinitoTramiteURI,
			String[]tipoConnettori,String[]tipoSoggetti,String[]tipoServizi,Logger log) throws DriverRegistroServiziException{
		this.registro = registro;
		this.checkEsistenzaFileDefinitoTramiteURI = checkEsistenzaFileDefinitoTramiteURI;
		this.log = log;
		if(tipoConnettori!=null && tipoConnettori.length>0 ){
			for(int i=0; i<tipoConnettori.length; i++){
				this.tipoConnettori.add(tipoConnettori[i]);
			}
		}else{
			throw new DriverRegistroServiziException("Tipo di connettori ammissibili non definiti");
		}
		if(tipoSoggetti!=null && tipoSoggetti.length>0 ){
			for(int i=0; i<tipoSoggetti.length; i++){
				this.tipoSoggetti.add(tipoSoggetti[i]);
			}
		}else{
			throw new DriverRegistroServiziException("Tipo di soggetti ammissibili non definiti");
		}
		if(tipoServizi!=null && tipoServizi.length>0 ){
			for(int i=0; i<tipoServizi.length; i++){
				this.tipoServizi.add(tipoServizi[i]);
			}
		}else{
			throw new DriverRegistroServiziException("Tipo di servizi ammissibili non definiti");
		}
	}
	
	
	
	public ValidazioneSemantica(org.openspcoop2.core.registry.RegistroServizi registro,boolean checkEsistenzaFileDefinitoTramiteURI,
			String[]tipoConnettori,String[]tipoSoggetti,String[]tipoServizi) throws DriverRegistroServiziException{
		this(registro,checkEsistenzaFileDefinitoTramiteURI,tipoConnettori,tipoSoggetti,tipoServizi,null);
	}

	private void printMsg(String msg){
		if(this.log == null){
			System.out.println(msg);
		}else{
			this.log.debug(msg);
		}
	}


	public void validazioneSemantica(boolean showIDOggettiAnalizzati) throws DriverRegistroServiziException {

		if(showIDOggettiAnalizzati)
			printMsg("---------------------------------------AccordiCooperazione("+this.registro.sizeAccordoCooperazioneList()+")--------------------------------------------------");

		// accordi di Cooperazione
		for(int i=0; i<this.registro.sizeAccordoCooperazioneList();i++){
			AccordoCooperazione ac = this.registro.getAccordoCooperazione(i);
			if(showIDOggettiAnalizzati)
				printMsg("Accordo di cooperazione: "+this.idAccordoCooperazioneFactory.getUriFromAccordo(ac));
			validaAccordoCooperazione(ac);
		}

		if(showIDOggettiAnalizzati)
			printMsg("\n\n------------------------------------AccordiServizioParteComune("+this.registro.sizeAccordoServizioParteComuneList()+")-----------------------------------------------------");

		// accordi di servizio
		for(int i=0; i<this.registro.sizeAccordoServizioParteComuneList();i++){
			AccordoServizioParteComune as = this.registro.getAccordoServizioParteComune(i);
			if(showIDOggettiAnalizzati)
				printMsg("Accordo di servizio parte comune: "+this.idAccordoFactory.getUriFromAccordo(as));
			validaAccordoServizioParteComune(as);
		}

		if(showIDOggettiAnalizzati)
			printMsg("\n\n------------------------------------Soggetti("+this.registro.sizeSoggettoList()+")-----------------------------------------------------");

		// soggetto
		for(int i=0; i<this.registro.sizeSoggettoList();i++){
			Soggetto sogg = this.registro.getSoggetto(i);
			if(showIDOggettiAnalizzati)
				printMsg("Soggetto: "+sogg.getTipo()+"/"+sogg.getNome());
			validaSoggetto(sogg,showIDOggettiAnalizzati);
		}

		if(showIDOggettiAnalizzati)
			printMsg("\n\n------------------------------------Porte di Dominio("+this.registro.sizePortaDominioList()+")-----------------------------------------------------");

		// porta di dominio
		for(int i=0; i<this.registro.sizePortaDominioList();i++){
			PortaDominio pd = this.registro.getPortaDominio(i);
			if(showIDOggettiAnalizzati)
				printMsg("Porta di dominio: "+pd.getNome());
			validaPortaDominio(pd);
		}

		if(showIDOggettiAnalizzati)
			printMsg("\n\n------------------------------------Connettori("+this.registro.sizeConnettoreList()+")-----------------------------------------------------");

		// connettori
		for(int i=0; i<this.registro.sizeConnettoreList();i++){
			Connettore con = this.registro.getConnettore(i);
			if(showIDOggettiAnalizzati)
				printMsg("Connettore: "+con.getNome());
			validaConnettore(con,null,null);
		}

		if(showIDOggettiAnalizzati)
			printMsg("\n\n-----------------------------------------------------------------------------------------");

		// Se è stata trovata almeno un'anomalia, lancio un'eccezione
		if (!this.errori.isEmpty()) {
			StringBuffer stringB = new StringBuffer().append("\n");
			Iterator<String> itE = this.errori.iterator();
			while (itE.hasNext())
				stringB.append(itE.next()).append("\n");
			throw new DriverRegistroServiziException(stringB.toString());
		}
	}



	private void validaAccordoCooperazione(AccordoCooperazione ac) throws DriverRegistroServiziException {

		// required
		if(ac.getNome()==null){
			this.errori.add("Esiste un accordo di cooperazione senza nome");
			return;
		}
		
		IDAccordoCooperazione idAccordoCooperazione = this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(ac);
		String uriAC = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordoCooperazione);

		// Devi controllare che 'soggetto-referente' deve puntare (attraverso tipo/nome) ad un soggetto realmente esistente
		IdSoggetto acsr = ac.getSoggettoReferente();
		if (acsr != null) {
			// required
			if(acsr.getTipo()==null){
				this.errori.add("Tipo di un soggetto referente dell'accordo cooperazione "+uriAC+" non definito");
			}
			else if(acsr.getNome()==null){
				this.errori.add("Nome di un soggetto referente dell'accordo cooperazione "+uriAC+" non definito");
			}else{
				// check esistenza
				if (!this.existsSoggetto(acsr.getTipo(), acsr.getNome()))
					this.errori.add("Il soggetto referente ["+acsr.getTipo()+"/"+acsr.getNome()+"] dell'accordo cooperazione "+uriAC+" non corrisponde a nessuno dei soggetti registrati");
			}
		}

		// Controllo elenco soggetti partecipanti
		if(ac.getElencoPartecipanti()!=null){
			AccordoCooperazionePartecipanti partecipanti = ac.getElencoPartecipanti();
			for(int i=0; i<partecipanti.sizeSoggettoPartecipanteList(); i++){
				IdSoggetto partecipante = partecipanti.getSoggettoPartecipante(i);
				// required
				if(partecipante.getTipo()==null){
					this.errori.add("Tipo di un soggetto partecipante dell'accordo cooperazione "+uriAC+" non definito");
				}
				else if(partecipante.getNome()==null){
					this.errori.add("Nome di un soggetto partecipante dell'accordo cooperazione "+uriAC+" non definito");
				}
				else{
					// check esistenza
					if (!this.existsSoggetto(partecipante.getTipo(),partecipante.getNome()))
						this.errori.add("Il soggetto partecipante ["+partecipante.getTipo()+"/"+partecipante.getNome()+"] dell'accordo cooperazione "+uriAC+" non corrisponde a nessuno dei soggetti registrati");
				}
			}
			if(partecipanti.sizeSoggettoPartecipanteList()<2){
				this.errori.add("L'accordo di cooperazione ["+uriAC+"] non possiede il numero minimo di soggetti partecipanti (2)");
			}
		}
		else{
			this.errori.add("L'accordo di cooperazione ["+uriAC+"] non possiede soggetti partecipanti (attesi almeno 2)");
		}

		// Allegati
		for(int i=0; i<ac.sizeAllegatoList(); i++){
			Documento allegato = ac.getAllegato(i);
			this.validateDocumento(allegato, RuoliDocumento.allegato,"accordoCooperazione["+uriAC+"]");
		}
		// SpecificheSemiformali
		for(int i=0; i<ac.sizeSpecificaSemiformaleList(); i++){
			Documento specificaSemiformale = ac.getSpecificaSemiformale(i);
			this.validateDocumento(specificaSemiformale, RuoliDocumento.specificaSemiformale,"accordoCooperazione["+uriAC+"]");
		}

		// Il nome dell'accordo di cooperazione ha gli stessi vincoli del nome di un accordo di servizio.
		// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
		try{
			if (!RegularExpressionEngine.isMatch(ac.getNome(),"^[0-9A-Za-z_\\-\\.]+$")) {
				this.errori.add("Il nome dell'accordo di cooperazione ["+uriAC+"] dev'essere formato solo caratteri, cifre, '_' , '-' e '.'");
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare del nome dell'accordo di cooperazione ["+uriAC+"]: "+e.getMessage(),e);
		}

		// La versione dell'accordo deve essere un numero intero
		if(ac.getVersione()!=null){
			try{
				if (!RegularExpressionEngine.isMatch(ac.getVersione(),"^[1-9]+[0-9]*$")) {
					this.errori.add("La versione dell'accordo di cooperazione ["+uriAC+"] dev'essere rappresentata da un numero intero");
				}
			}catch(Exception e){
				throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare della versione dell'accordo di cooperazione ["+uriAC+"]: "+e.getMessage(),e);
			}
		}

		// Ogni accordo di cooperazione deve possedere un nome diverso
		int numAc = 0;
		for(int j=0; j<this.registro.sizeAccordoCooperazioneList();j++){
			AccordoCooperazione tmpAc = this.registro.getAccordoCooperazione(j);
			if (this.idAccordoCooperazioneFactory.getUriFromAccordo(ac).equals(this.idAccordoCooperazioneFactory.getUriFromAccordo(tmpAc)))
				numAc++;
		}
		if (numAc > 1)
			this.errori.add("Non può esistere più di un accordo di cooperazione con uri "+uriAC);
	}

	private void validaAccordoServizioParteComune(AccordoServizioParteComune as) throws DriverRegistroServiziException {

		// required
		if(as.getNome()==null){
			this.errori.add("Esiste un accordo di servizio senza nome");
			return;
		}
		
		IDAccordo idAccordoServizio = this.idAccordoFactory.getIDAccordoFromAccordo(as);
		String uriAS = this.idAccordoFactory.getUriFromIDAccordo(idAccordoServizio);
		AccordoServizioParteComuneServizioComposto assc = as.getServizioComposto();
		if (assc != null) {
			uriAS = "servizio composto ["+uriAS+"]";
		}else{
			uriAS = "accordo di servizio ["+uriAS+"]";
		}

		// Devi controllare che 'soggetto-referente' deve puntare (attraverso tipo/nome) ad un soggetto realmente esistente
		IdSoggetto assr = as.getSoggettoReferente();
		if (assr != null) {
			// required
			if(assr.getTipo()==null){
				this.errori.add("Tipo di un soggetto referente nel "+uriAS+" non definito");
			}
			else if(assr.getNome()==null){
				this.errori.add("Nome di un soggetto referente nel "+uriAS+" non definito");
			}
			else{
				// esistenza
				if (!this.existsSoggetto(assr.getTipo(), assr.getNome()))
					this.errori.add("Il soggetto referente ["+assr.getTipo()+"/"+assr.getNome()+"] nel "+uriAS+" non corrisponde a nessuno dei soggetti registrati");
			}
		}

		// Se presente 'servizio-composto' devono essere visionati i servizi componenti interni. Per ogni servizio componente deve essere controllato che l'accordo di servizio esista realmente e in caso viene definito anche il port type, che questo esista realmente. Inoltre deve essere controllato che il ws-bpel, se presente, sia una URI valida (usa la classe java java.net.URI, il costruttore se non riesce a costruire ritorna errore) o un path ad un file locale falido (utilizza la classe java.io.File e il metodo exists() ).
		if (assc != null) {
			for (int j = 0; j < assc.sizeServizioComponenteList(); j++) {
				AccordoServizioParteComuneServizioCompostoServizioComponente asscsc = assc.getServizioComponente(j);

				// required
				if(asscsc.getTipo()==null){
					this.errori.add("Tipo di un servizio componente nel "+uriAS+" non definito");
				}
				else if(asscsc.getNome()==null){
					this.errori.add("Nome di un servizio componente nel "+uriAS+" non definito");
				}
				else if(asscsc.getTipoSoggetto()==null){
					this.errori.add("Tipo dell'erogatore di un servizio componente nel "+uriAS+" non definito");
				}
				else if(asscsc.getNomeSoggetto()==null){
					this.errori.add("Nome dell'erogatore di un servizio componente nel "+uriAS+" non definito");
				}
				else {
					// Check servizio
					if (!this.existsAccordoServizioParteSpecifica(asscsc.getTipo(), asscsc.getNome(), asscsc.getTipoSoggetto(), asscsc.getNomeSoggetto()))
						this.errori.add("Il servizio componente ["+asscsc.getTipo()+"/"+asscsc.getNome()+"] erogato dal soggetto ["+asscsc.getTipoSoggetto()+"/"+asscsc.getNomeSoggetto()+
								"], referenziato nel "+uriAS+", non corrisponde a nessun servizio registrato");
				}

				// Check azione associata al servizio
				if(asscsc.getAzione()!=null){
					AccordoServizioParteSpecifica serv = this.getAccordoServizioParteSpecifica(asscsc.getTipo(), asscsc.getNome(), asscsc.getTipoSoggetto(), asscsc.getNomeSoggetto());
					if(this.existsAccordoServizioParteComune(serv.getAccordoServizioParteComune())){
						AccordoServizioParteComune asServizioComposto = this.getAccordoServizioParteComune(serv.getAccordoServizioParteComune());
						if(serv.getPortType()!=null){
							if(this.existsPortType_AccordoServizioParteComune(asServizioComposto, serv.getPortType()) ){
								PortType pt = this.getPortType_AccordoServizioParteComune(asServizioComposto, serv.getPortType());
								if(this.existsAzione_PortType_AccordoServizio(pt, asscsc.getAzione())==false){
									this.errori.add("Il servizio componente ["+asscsc.getTipo()+"/"+asscsc.getNome()+"] erogato dal soggetto ["+asscsc.getTipoSoggetto()+"/"+asscsc.getNomeSoggetto()+
											"], referenziato nel "+uriAS+", utilizza un'azione ["+asscsc.getAzione()+"] che non risulta definita nell'accordo di servizio ["+serv.getAccordoServizioParteComune()+"] implementato dal servizio componente (istanziato per il servizio ["+serv.getPortType()+"])");
								}
							}else{
								this.errori.add("Il servizio componente ["+asscsc.getTipo()+"/"+asscsc.getNome()+"] erogato dal soggetto ["+asscsc.getTipoSoggetto()+"/"+asscsc.getNomeSoggetto()+
										"], referenziato nel "+uriAS+", implementa un accordo di servizio ["+serv.getAccordoServizioParteComune()+"] che e' istanziato per uno specifico servizio ["+serv.getPortType()+"] che non risulta registrato");
							}
						}else{
							if(this.existsAzione_AccordoServizioParteComune(asServizioComposto, asscsc.getAzione())==false){
								this.errori.add("Il servizio componente ["+asscsc.getTipo()+"/"+asscsc.getNome()+"] erogato dal soggetto ["+asscsc.getTipoSoggetto()+"/"+asscsc.getNomeSoggetto()+
										"], referenziato nel "+uriAS+", utilizza un'azione ["+asscsc.getAzione()+"] che non risulta definita nell'accordo di servizio ["+serv.getAccordoServizioParteComune()+"] implementato dal servizio componente");
							}
						}
					}else{
						this.errori.add("Il servizio componente ["+asscsc.getTipo()+"/"+asscsc.getNome()+"] erogato dal soggetto ["+asscsc.getTipoSoggetto()+"/"+asscsc.getNomeSoggetto()+
								"], referenziato nel "+uriAS+", implementa un accordo di servizio ["+serv.getAccordoServizioParteComune()+"] che non risulta registrato");
					}
				}
			}

			// SpecificheCoordinamento
			for(int i=0; i<assc.sizeSpecificaCoordinamentoList(); i++){
				Documento specificaCoordinamento = assc.getSpecificaCoordinamento(i);
				this.validateDocumento(specificaCoordinamento, RuoliDocumento.specificaCoordinamento,uriAS);
			}

			// required
			if(assc.getAccordoCooperazione()==null){
				this.errori.add("Nome dell'accordo di cooperazione implementato nel "+uriAS+" non definito");
			}
			else{
				// Nome accordo di cooperazione
				if(this.existsAccordoCooperazione(assc.getAccordoCooperazione())==false){
					this.errori.add("L'accordo di cooperazione ["+assc.getAccordoCooperazione()+"] referenziato nel "+uriAS+" non corrisponde a nessuno degli accordi di cooperazione registrati");
				}
			}
		}


		// Azioni
		for (int j = 0; j < as.sizeAzioneList(); j++) {
			Azione az = as.getAzione(j);

			// required
			if(az.getNome()==null){
				this.errori.add("Non è stato definito il nome di un'azione nel "+uriAS);
				continue;
			}
			
			// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
			try{
				if (!RegularExpressionEngine.isMatch(az.getNome(),"^[_A-Za-z][\\-\\._A-Za-z0-9]*$")) {
					this.errori.add("Il nome dell'azione ["+az.getNome()+"] nel "+uriAS+" non e' valido; puo' iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-' e '.'");
				}
			}catch(Exception e){
				throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare del nome dell'azione ["+az.getNome()+"] nel "+uriAS+ " :" +e.getMessage(),e);
			}
				
			// ogni azione presente nell'accordo di servizio deve possedere un nome diverso
			int numAz = 0;
			for(int k=0; k<as.sizeAzioneList();k++){
				Azione tmpAz = as.getAzione(k);
				if (az.getNome().equals(tmpAz.getNome()))
					numAz++;
			}
			if (numAz > 1)
				this.errori.add("Non può esistere più di un'azione con nome "+az.getNome()+" nel "+uriAS);

			// Se presente attributo 'correlata', deve controllare che esista nell'accordo un altra azione che possieda come nome il valore presente nell'attributo.
			String correlata = az.getCorrelata();
			if (correlata != null) {
				if(this.existsAzione_AccordoServizioParteComune(as, correlata)==false){
					this.errori.add("L'azione ["+correlata+"] correlata all'azione "+az.getNome()+" nel "+uriAS+" non corrisponde a nessuna delle azioni registrate");
				}
			}

			// XSD: profilo-collaborazione: oneway, sincrono, asincronoSimmetrico, asincronoAsimmetrico
			ProfiloCollaborazione profColl = az.getProfiloCollaborazione();
			if (profColl != null && !profColl.equals(CostantiRegistroServizi.ONEWAY) 
					&& !profColl.equals(CostantiRegistroServizi.SINCRONO) 
					&& !profColl.equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO) && 
					!profColl.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO))
				this.errori.add("Il profilo di collaborazione dell'azione "+az.getNome()+" nel "+uriAS+" deve possedere un profilo "+CostantiRegistroServizi.ONEWAY
						+", "+CostantiRegistroServizi.SINCRONO+", "+CostantiRegistroServizi.ASINCRONO_SIMMETRICO+" o "+CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
			// XSD: filtro-duplicati: abilitato, disabilitato
			StatoFunzionalita filtroDup = az.getFiltroDuplicati();
			if (filtroDup != null && !filtroDup.equals(CostantiRegistroServizi.ABILITATO) && !filtroDup.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("Il filtro duplicati dell'azione "+az.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// XSD: conferma-ricezione: abilitato, disabilitato
			StatoFunzionalita confRic = az.getConfermaRicezione();
			if (confRic != null && !confRic.equals(CostantiRegistroServizi.ABILITATO) && !confRic.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("La conferma ricezione dell'azione "+az.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// XSD: id-collaborazione: abilitato, disabilitato
			StatoFunzionalita idColl = az.getIdCollaborazione();
			if (idColl != null && !idColl.equals(CostantiRegistroServizi.ABILITATO) && !idColl.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("L'id collaborazione dell'azione "+az.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// XSD: consegna-in-ordine: abilitato, disabilitato
			StatoFunzionalita consOrd = az.getConsegnaInOrdine();
			if (consOrd != null && !consOrd.equals(CostantiRegistroServizi.ABILITATO) && !consOrd.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("La consegna in ordine dell'azione "+az.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// Scadenza
			if(az.getScadenza()!=null){
				try{
					Integer.parseInt(az.getScadenza());
				}catch(Exception e){
					this.errori.add("Il valore associato alla scadenza dell'azione "+az.getNome()+" nel "+uriAS+" dev'essere un numero intero");
				}
			}
		}


		// Port Types
		for (int j = 0; j < as.sizePortTypeList(); j++) {
			PortType pt = as.getPortType(j);
			
			// required
			if(pt.getNome()==null){
				this.errori.add("Non è stato definito il nome di un servizio nel "+uriAS);
				continue;
			}
			
			// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
			try{
				if (!RegularExpressionEngine.isMatch(pt.getNome(),"^[_A-Za-z][\\-\\._A-Za-z0-9]*$")) {
					this.errori.add("Il nome del servizio ["+pt.getNome()+"] nel "+uriAS+" non e' valido; puo' iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-' e '.'");
				}
			}catch(Exception e){
				throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare del nome del servizio ["+pt.getNome()+"] nel "+uriAS+" :" +e.getMessage(),e);
			}

			// ogni port type presente nell'accordo di servizio deve possedere un nome diverso
			int numPt = 0;
			for(int k=0; k<as.sizePortTypeList();k++){
				PortType tmpPt = as.getPortType(k);
				if (pt.getNome().equals(tmpPt.getNome()))
					numPt++;
			}
			if (numPt > 1)
				this.errori.add("Non può esistere più di un port-type con nome "+pt.getNome()+" nel "+uriAS);

			// XSD: style: document, rpc
			BindingStyle style = pt.getStyle();
			if ((style != null) && !style.equals(CostantiRegistroServizi.WSDL_STYLE_DOCUMENT) && !style.equals(CostantiRegistroServizi.WSDL_STYLE_RPC))
				this.errori.add("Lo style del servizio "+pt.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.WSDL_STYLE_DOCUMENT+" o "+CostantiRegistroServizi.WSDL_STYLE_RPC+"");
			// XSD: profilo-collaborazione: oneway, sincrono, asincronoSimmetrico, asincronoAsimmetrico
			ProfiloCollaborazione profColl = pt.getProfiloCollaborazione();
			if (profColl != null && !profColl.equals(CostantiRegistroServizi.ONEWAY) 
					&& !profColl.equals(CostantiRegistroServizi.SINCRONO) 
					&& !profColl.equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO) && 
					!profColl.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO))
				this.errori.add("Il profilo di collaborazione del servizio "+pt.getNome()+" nel "+uriAS+" deve possedere un profilo "+CostantiRegistroServizi.ONEWAY
						+", "+CostantiRegistroServizi.SINCRONO+", "+CostantiRegistroServizi.ASINCRONO_SIMMETRICO+" o "+CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
			// XSD: filtro-duplicati: abilitato, disabilitato
			StatoFunzionalita filtroDup = pt.getFiltroDuplicati();
			if (filtroDup != null && !filtroDup.equals(CostantiRegistroServizi.ABILITATO) && !filtroDup.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("Il filtro duplicati del servizio "+pt.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// XSD: conferma-ricezione: abilitato, disabilitato
			StatoFunzionalita confRic = pt.getConfermaRicezione();
			if (confRic != null && !confRic.equals(CostantiRegistroServizi.ABILITATO) && !confRic.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("La conferma ricezione del servizio "+pt.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// XSD: id-collaborazione: abilitato, disabilitato
			StatoFunzionalita idColl = pt.getIdCollaborazione();
			if (idColl != null && !idColl.equals(CostantiRegistroServizi.ABILITATO) && !idColl.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("L'id collaborazione del servizio "+pt.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// XSD: consegna-in-ordine: abilitato, disabilitato
			StatoFunzionalita consOrd = pt.getConsegnaInOrdine();
			if (consOrd != null && !consOrd.equals(CostantiRegistroServizi.ABILITATO) && !consOrd.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("La consegna in ordine del servizio "+pt.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// Scadenza
			if(pt.getScadenza()!=null){
				try{
					Integer.parseInt(pt.getScadenza());
				}catch(Exception e){
					this.errori.add("Il valore associato alla scadenza del servizio "+pt.getNome()+" nel "+uriAS+" dev'essere un numero intero");
				}
			}

			// Operations
			for(int k=0; k<pt.sizeAzioneList();k++){
				Operation op = pt.getAzione(k);

				//  required
				if(op.getNome()==null){
					this.errori.add("Non è stato definito il nome di un'azione del servizio "+pt.getNome()+" nel "+uriAS);
					continue;
				}
				
				// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
				try{
					if (!RegularExpressionEngine.isMatch(op.getNome(),"^[_A-Za-z][\\-\\._A-Za-z0-9]*$")) {
						this.errori.add("Il nome dell'azione "+op.getNome()+" nel "+uriAS+" puo' iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-' e '.'");
					}
				}catch(Exception e){
					throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare del nome dell'azione "+op.getNome()+" nel "+uriAS+" :" +e.getMessage(),e);
				}

				// ogni azione di un port type presente nell'accordo di servizio deve possedere un nome diverso	
				int numOp = 0;
				for(int h=0; h<pt.sizeAzioneList();h++){
					Operation tmpOp = pt.getAzione(h);
					if (op.getNome().equals(tmpOp.getNome()))
						numOp++;
				}
				if (numOp > 1)
					this.errori.add("Non può esistere più di un'azione con nome "+op.getNome()+" nel "+uriAS);

				// Se presente attributo 'correlata', deve controllare che esista nell'accordo un altra azione che possieda come nome il valore presente nell'attributo.
				String correlata = op.getCorrelata();
				String correlataServizio = op.getCorrelataServizio();
				if (correlata != null && correlataServizio!=null) {
					if(this.existsPortType_AccordoServizioParteComune(as, correlataServizio)==false){
						this.errori.add("Il servizio ["+correlataServizio+"] correlata all'azione "+op.getNome()+" nel servizio "+pt.getNome()+" non corrisponde a nessuno dei servizi registrati nel "+uriAS);
					}
					PortType ptCorrelata = this.getPortType_AccordoServizioParteComune(as, correlataServizio);
					if(this.existsAzione_PortType_AccordoServizio(ptCorrelata, correlata)==false){
						this.errori.add("L'azione ["+correlata+"] correlata all'azione "+op.getNome()+" nel servizio "+pt.getNome()+" non corrisponde a nessuna delle azioni registrate nel servizio correlato ["+correlataServizio+"] nel "+uriAS);
					}
				}
				else if (correlata != null){
					if(this.existsAzione_PortType_AccordoServizio(pt, correlata)==false){
						this.errori.add("L'azione ["+correlata+"] correlata all'azione "+op.getNome()+" nel servizio "+pt.getNome()+" non corrisponde a nessuna delle azioni registrate nel "+uriAS);
					}
				}else if(correlataServizio!=null){
					this.errori.add("L'azione "+op.getNome()+" nel servizio "+pt.getNome()+" del "+uriAS+" contiene una correlazione verso un altro servizio ["+correlataServizio+"] senza pero' aver definito l'azione correlata");
				}

				// XSD: style: document, rpc
				style = op.getStyle();
				if ((style != null) && !style.equals(CostantiRegistroServizi.WSDL_STYLE_DOCUMENT) && !style.equals(CostantiRegistroServizi.WSDL_STYLE_RPC))
					this.errori.add("Lo style dell'azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.WSDL_STYLE_DOCUMENT+" o "+CostantiRegistroServizi.WSDL_STYLE_RPC+"");
				// XSD: profilo-collaborazione: oneway, sincrono, asincronoSimmetrico, asincronoAsimmetrico
				profColl = op.getProfiloCollaborazione();
				if (profColl != null && !profColl.equals(CostantiRegistroServizi.ONEWAY) 
						&& !profColl.equals(CostantiRegistroServizi.SINCRONO) 
						&& !profColl.equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO) && 
						!profColl.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO))
					this.errori.add("Il profilo di collaborazione dell'azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+" deve possedere un profilo "+CostantiRegistroServizi.ONEWAY
							+", "+CostantiRegistroServizi.SINCRONO+", "+CostantiRegistroServizi.ASINCRONO_SIMMETRICO+" o "+CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
				// XSD: filtro-duplicati: abilitato, disabilitato
				filtroDup = op.getFiltroDuplicati();
				if (filtroDup != null && !filtroDup.equals(CostantiRegistroServizi.ABILITATO) && !filtroDup.equals(CostantiRegistroServizi.DISABILITATO))
					this.errori.add("Il filtro duplicati dell'azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
				// XSD: conferma-ricezione: abilitato, disabilitato
				confRic = op.getConfermaRicezione();
				if (confRic != null && !confRic.equals(CostantiRegistroServizi.ABILITATO) && !confRic.equals(CostantiRegistroServizi.DISABILITATO))
					this.errori.add("La conferma ricezione del dell'azione "+op.getNome()+ " servizio "+pt.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
				// XSD: id-collaborazione: abilitato, disabilitato
				idColl = op.getIdCollaborazione();
				if (idColl != null && !idColl.equals(CostantiRegistroServizi.ABILITATO) && !idColl.equals(CostantiRegistroServizi.DISABILITATO))
					this.errori.add("L'id collaborazione dell'azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
				// XSD: consegna-in-ordine: abilitato, disabilitato
				consOrd = op.getConsegnaInOrdine();
				if (consOrd != null && !consOrd.equals(CostantiRegistroServizi.ABILITATO) && !consOrd.equals(CostantiRegistroServizi.DISABILITATO))
					this.errori.add("La consegna in ordine ddell'azione "+op.getNome()+ " el servizio "+pt.getNome()+" nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
				// Scadenza
				if(op.getScadenza()!=null){
					try{
						Integer.parseInt(op.getScadenza());
					}catch(Exception e){
						this.errori.add("Il valore associato alla scadenza dell'azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+" dev'essere un numero intero");
					}
				}

				// XSD: message-use: literal, encoded
				Message mi = op.getMessageInput();
				if (mi != null) {
					BindingUse useI = mi.getUse();
					if ((useI != null) && !useI.equals(CostantiRegistroServizi.WSDL_USE_LITERAL) && !useI.equals(CostantiRegistroServizi.WSDL_USE_ENCODED))
						this.errori.add("L'uso del message-input dell'azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+" dev'essere "+CostantiRegistroServizi.WSDL_USE_ENCODED+" o "+CostantiRegistroServizi.WSDL_USE_LITERAL);
					for(int l=0;l<mi.sizePartList();l++){
						MessagePart part = mi.getPart(l);
						if(part.getName()==null){
							this.errori.add("Non è stato definito il name del message-input dell'azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS);
						}
						if(part.getElementName()!=null && part.getTypeName()!=null){
							this.errori.add("Nella definizione del message-input sono stati definiti sia parametri di tipologia 'element' che parametri di tipologia 'type', solo una delle due tipologie è ammessa (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
						}else if(part.getElementName()==null && part.getTypeName()==null){ 
							this.errori.add("Nella definizione del message-input non sono stati definiti ne parametri di tipologia 'element' che parametri di tipologia 'type' (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
						}else{
							if(part.getElementName()!=null && part.getElementNamespace()==null){
								this.errori.add("Nella definizione del message-input è stata definita una tipologia 'element' che non presenta la definizione del namespace (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
							}
							if(part.getElementName()==null && part.getElementNamespace()!=null){
								this.errori.add("Nella definizione del message-input è stata definita una tipologia 'element' che non presenta la definizione dell'element name (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
							}
							if(part.getTypeName()!=null && part.getTypeNamespace()==null){
								this.errori.add("Nella definizione del message-input è stata definita una tipologia 'type' che non presenta la definizione del namespace (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
							}
							if(part.getTypeName()==null && part.getTypeNamespace()!=null){
								this.errori.add("Nella definizione del message-input è stata definita una tipologia 'type' che non presenta la definizione del type name (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
							}
						}
					}
				}
				Message mo = op.getMessageOutput();
				if (mo != null) {
					BindingUse useO = mo.getUse();
					if ((useO != null) && !useO.equals(CostantiRegistroServizi.WSDL_USE_LITERAL) && !useO.equals(CostantiRegistroServizi.WSDL_USE_ENCODED))
						this.errori.add("L'uso del message-output dell'azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+" dev'essere "+CostantiRegistroServizi.WSDL_USE_ENCODED+" o "+CostantiRegistroServizi.WSDL_USE_LITERAL);
					for(int l=0;l<mo.sizePartList();l++){
						MessagePart part = mo.getPart(l);
						if(part.getName()==null){
							this.errori.add("Non è stato definito il name del message-output dell'azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS);
						}
						if(part.getElementName()!=null && part.getTypeName()!=null){
							this.errori.add("Nella definizione del message-output sono stati definiti sia parametri di tipologia 'element' che parametri di tipologia 'type', solo una delle due tipologie è ammessa (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
						}else if(part.getElementName()==null && part.getTypeName()==null){ 
							this.errori.add("Nella definizione del message-output non sono stati definiti ne parametri di tipologia 'element' che parametri di tipologia 'type' (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
						}else{
							if(part.getElementName()!=null && part.getElementNamespace()==null){
								this.errori.add("Nella definizione del message-output è stata definita una tipologia 'element' che non presenta la definizione del namespace (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
							}
							if(part.getElementName()==null && part.getElementNamespace()!=null){
								this.errori.add("Nella definizione del message-output è stata definita una tipologia 'element' che non presenta la definizione dell'element name (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
							}
							if(part.getTypeName()!=null && part.getTypeNamespace()==null){
								this.errori.add("Nella definizione del message-output è stata definita una tipologia 'type' che non presenta la definizione del namespace (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
							}
							if(part.getTypeName()==null && part.getTypeNamespace()!=null){
								this.errori.add("Nella definizione del message-output è stata definita una tipologia 'type' che non presenta la definizione del type name (azione "+op.getNome()+ " del servizio "+pt.getNome()+" nel "+uriAS+")");
							}
						}
					}
				}
			}

		}


		// Allegati
		for(int i=0; i<as.sizeAllegatoList(); i++){
			Documento allegato = as.getAllegato(i);
			this.validateDocumento(allegato, RuoliDocumento.allegato,uriAS);
		}
		// SpecificheSemiformali
		for(int i=0; i<as.sizeSpecificaSemiformaleList(); i++){
			Documento specificaSemiformale = as.getSpecificaSemiformale(i);
			this.validateDocumento(specificaSemiformale, RuoliDocumento.specificaSemiformale,uriAS);
		}

		// Il nome dell'accordo deve possedere i stessi vincoli presenti per il nome di un accordo inserito nell'interfaccia grafica regserv.
		// Il nome deve contenere solo lettere e numeri e '_' '-' '.'1
		try{
			if (!RegularExpressionEngine.isMatch(as.getNome(),"^[0-9A-Za-z_\\-\\.]+$")) {
				this.errori.add("Il nome nel "+uriAS+" dev'essere formato solo caratteri, cifre, '_' , '-' e '.'");
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare del nome nel "+uriAS+" :" +e.getMessage(),e);
		}

		// XSD: profilo-collaborazione: oneway, sincrono, asincronoSimmetrico, asincronoAsimmetrico
		ProfiloCollaborazione profColl = as.getProfiloCollaborazione();
		if(profColl==null){
			this.errori.add("Il profilo di collaborazione nel "+uriAS+" non è definito; deve possedere un profilo "+CostantiRegistroServizi.ONEWAY
					+", "+CostantiRegistroServizi.SINCRONO+", "+CostantiRegistroServizi.ASINCRONO_SIMMETRICO+" o "+CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
		}else{
			if ( !profColl.equals(CostantiRegistroServizi.ONEWAY) 
					&& !profColl.equals(CostantiRegistroServizi.SINCRONO) 
					&& !profColl.equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO) && 
					!profColl.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO))
				this.errori.add("Il profilo di collaborazione nel "+uriAS+" deve possedere un profilo "+CostantiRegistroServizi.ONEWAY
						+", "+CostantiRegistroServizi.SINCRONO+", "+CostantiRegistroServizi.ASINCRONO_SIMMETRICO+" o "+CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
		}
		// XSD: filtro-duplicati: abilitato, disabilitato
		StatoFunzionalita filtroDup = as.getFiltroDuplicati();
		if ( filtroDup!=null && !filtroDup.equals(CostantiRegistroServizi.ABILITATO) && !filtroDup.equals(CostantiRegistroServizi.DISABILITATO))
			this.errori.add("Il filtro duplicati nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
		// XSD: conferma-ricezione: abilitato, disabilitato
		StatoFunzionalita confRic = as.getConfermaRicezione();
		if (confRic!=null && !confRic.equals(CostantiRegistroServizi.ABILITATO) && !confRic.equals(CostantiRegistroServizi.DISABILITATO))
			this.errori.add("La conferma ricezione nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
		// XSD: id-collaborazione: abilitato, disabilitato
		StatoFunzionalita idColl = as.getIdCollaborazione();
		if (idColl!=null && idColl != null && !idColl.equals(CostantiRegistroServizi.ABILITATO) && !idColl.equals(CostantiRegistroServizi.DISABILITATO))
			this.errori.add("L'id collaborazione nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
		// XSD: consegna-in-ordine: abilitato, disabilitato
		StatoFunzionalita consOrd = as.getConsegnaInOrdine();
		if (consOrd!=null && consOrd != null && !consOrd.equals(CostantiRegistroServizi.ABILITATO) && !consOrd.equals(CostantiRegistroServizi.DISABILITATO))
			this.errori.add("La consegna in ordine nel "+uriAS+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
		// Scadenza
		if(as.getScadenza()!=null){
			try{
				Integer.parseInt(as.getScadenza());
			}catch(Exception e){
				this.errori.add("Il valore associato alla scadenza nel "+uriAS+" dev'essere un numero intero");
			}
		}


		// Se presenti i wsdl e le specifiche di conversazione (in tutto sono 7) devi controllare che siano URI valide o un path ad un file locale.
		if(as.getWsdlConcettuale()!=null){
			try{
				RegExpUtilities.validateUri(as.getWsdlConcettuale(),this.checkEsistenzaFileDefinitoTramiteURI);
			}catch(Exception e){
				this.errori.add("Il wsdl concettuale nel "+uriAS+" non è valido: "+e.getMessage());
			}
		}
		if(as.getWsdlDefinitorio()!=null){
			try{
				RegExpUtilities.validateUri(as.getWsdlDefinitorio(),this.checkEsistenzaFileDefinitoTramiteURI);
			}catch(Exception e){
				this.errori.add("Il wsdl definitorio nel "+uriAS+" non è valido: "+e.getMessage());
			}
		}
		if(as.getWsdlLogicoErogatore()!=null){
			try{
				RegExpUtilities.validateUri(as.getWsdlLogicoErogatore(),this.checkEsistenzaFileDefinitoTramiteURI);
			}catch(Exception e){
				this.errori.add("Il wsdl logico erogatore nel "+uriAS+" non è valido: "+e.getMessage());
			}
		}
		if(as.getWsdlLogicoFruitore()!=null){
			try{
				RegExpUtilities.validateUri(as.getWsdlLogicoFruitore(),this.checkEsistenzaFileDefinitoTramiteURI);
			}catch(Exception e){
				this.errori.add("Il wsdl logico fruitore nel "+uriAS+" non è valido: "+e.getMessage());
			}
		}
		if(as.getSpecificaConversazioneConcettuale()!=null){
			try{
				RegExpUtilities.validateUri(as.getSpecificaConversazioneConcettuale(),this.checkEsistenzaFileDefinitoTramiteURI);
			}catch(Exception e){
				this.errori.add("Il wsdl concettuale nel "+uriAS+" non è valido: "+e.getMessage());
			}
		}
		if(as.getSpecificaConversazioneErogatore()!=null){
			try{
				RegExpUtilities.validateUri(as.getSpecificaConversazioneErogatore(),this.checkEsistenzaFileDefinitoTramiteURI);
			}catch(Exception e){
				this.errori.add("Il wsdl definitorio nel "+uriAS+" non è valido: "+e.getMessage());
			}
		}
		if(as.getSpecificaConversazioneFruitore()!=null){
			try{
				RegExpUtilities.validateUri(as.getSpecificaConversazioneFruitore(),this.checkEsistenzaFileDefinitoTramiteURI);
			}catch(Exception e){
				this.errori.add("Il wsdl logico erogatore nel "+uriAS+" non è valido: "+e.getMessage());
			}
		}

		// Se utilizzo senza azioni e' false, devi controllare che esista almeno un'azione presente nell'accordo di servizio.
		if (!as.getUtilizzoSenzaAzione()) {
			if ( (as.sizeAzioneList() == 0) && (as.sizePortTypeList()==0)) {
				this.errori.add("Il "+uriAS+" non prevede l'utilizzo senza azioni, quindi dev'essere definita almeno un'azione o almeno un servizio (con relative azioni)");
			}
		}

		// La versione dell'accordo deve essere un numero intero
		if(as.getVersione()!=null){
			try{
				if (!RegularExpressionEngine.isMatch(as.getVersione(),"^[1-9]+[0-9]*$")) {
					this.errori.add("La versione nel "+uriAS+" dev'essere rappresentata da un numero intero");
				}
			}catch(Exception e){
				throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare della versione nel "+uriAS+" :" +e.getMessage(),e);
			}
		}


		// Ogni accordo di servizio deve possedere un nome diverso
		int numAs = 0;
		for(int j=0; j<this.registro.sizeAccordoServizioParteComuneList();j++){
			AccordoServizioParteComune tmpAs = this.registro.getAccordoServizioParteComune(j);
			if (this.idAccordoFactory.getUriFromAccordo(tmpAs).equals(this.idAccordoFactory.getUriFromAccordo(as)))
				numAs++;
		}
		if (numAs > 1)
			this.errori.add("Non può esistere più di un accordo di servizio (o servizio composto) con nome "+this.idAccordoFactory.getUriFromAccordo(as));
	}

	private  void validaSoggetto(Soggetto sogg,boolean showIDOggettiAnalizzati) throws DriverRegistroServiziException {

		// required
		if(sogg.getNome()==null){
			this.errori.add("Esiste un soggetto senza nome");
			return;
		}
		if(sogg.getTipo()==null){
			this.errori.add("Esiste un soggetto senza tipo");
			return;
		}
		
		// Valida il connettore
		Connettore conn = sogg.getConnettore();
		if (conn != null){
			validaConnettore(conn, "soggetto", sogg.getTipo()+"/"+sogg.getNome());
		}

		// Valida i servizi
		for (int j=0; j<sogg.sizeAccordoServizioParteSpecificaList(); j++) {
			AccordoServizioParteSpecifica serv = sogg.getAccordoServizioParteSpecifica(j);
			if(showIDOggettiAnalizzati)
				printMsg("\tServizio: "+sogg.getTipo()+"/"+sogg.getNome()+"_"+serv.getServizio().getTipo()+"/"+serv.getServizio().getNome());
			validaAccordoServizioParteSpecifica(serv, sogg);
		}

		// Il tipo del soggetto deve essere uno tra quelli definiti in openspcoop2.properties. Ci puoi accedere attraverso il comando: org.openspcoop.pdd.config.OpenSPCoopProperties.getInstance().getTipiSoggetti()
		if(this.tipoSoggetti.contains(sogg.getTipo())==false){
			this.errori.add("Il tipo del soggetto "+sogg.getTipo()+"/"+sogg.getNome()+" non è valido (Tipi utilizzabili: "+this.getTipoSoggetti()+")");
		}

		// Nome del soggetto
		try{
			if (!RegularExpressionEngine.isMatch(sogg.getNome(),"^[0-9A-Za-z]+$")) {
				this.errori.add("Il nome del soggetto "+sogg.getTipo()+"/"+sogg.getNome()+" dev'essere formato solo caratteri e cifre");
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare del nome del soggetto "+sogg.getTipo()+"/"+sogg.getNome()+" :" +e.getMessage(),e);
		}

		// ogni soggetto identificato come coppia tipo/nome, deve essere univoco
		int numS = 0;
		for(int j=0; j<this.registro.sizeSoggettoList();j++){
			Soggetto tmpSogg = this.registro.getSoggetto(j);
			if (sogg.getNome().equals(tmpSogg.getNome()) && sogg.getTipo().equals(tmpSogg.getTipo()))
				numS++;
		}
		if (numS > 1)
			this.errori.add("Non può esistere più di un soggetto con nome "+sogg.getNome()+" e tipo "+sogg.getTipo());

		// l'elemento portaDominio di un soggetto, se presente, deve puntare ad una porta di dominio presente nel registro
		if(sogg.getPortaDominio()!=null){
			if(this.existsPortaDominio(sogg.getPortaDominio())==false){
				this.errori.add("La porta di dominio ["+sogg.getPortaDominio()+"] del soggetto "+sogg.getTipo()+"/"+sogg.getNome()+" non corrisponde a nessuna delle porte di dominio registrate");
			}
		}

	}

	private  void validaAccordoServizioParteSpecifica(AccordoServizioParteSpecifica asps, Soggetto sogg) throws DriverRegistroServiziException {
		
		// required		
		if(asps.getNome()==null){
			this.errori.add("Non e' stato fornito un nome per l'accordo di servizio parte specifica");
			return;
		}
		
		// La versione dell'accordo parte specifica, se non specificato varra "1", altrimenti deve essere un numero intero
		if(asps.getVersione()!=null){
			try{
				if (!RegularExpressionEngine.isMatch(asps.getVersione(),"^[1-9]+[0-9]*$")) {
					this.errori.add("La versione nell'accordo di servizio parte specifica ["+asps.getNome()+"] dev'essere rappresentata da un numero intero");
				}
			}catch(Exception e){
				throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare della versione nell'accordo di servizio parte specifica ["+asps.getNome()+"] :" +e.getMessage(),e);
			}
		}
		
		if(asps.getServizio()==null){
			this.errori.add("Esiste un accordo di servizio parte specifica senza servizio");
			return;
		}
		if(asps.getServizio().getNome()==null){
			this.errori.add("Esiste un servizio senza nome");
			return;
		}
		if(asps.getServizio().getTipo()==null){
			this.errori.add("Esiste un servizio senza tipo");
			return;
		}
		Servizio serv = asps.getServizio();
		
		String idServizio = sogg.getTipo()+"/"+sogg.getNome()+"_"+serv.getTipo()+"/"+serv.getNome();
		
		// Il connettore, se presente, deve essere validato.
		Connettore conn = serv.getConnettore();
		if (conn != null){
			validaConnettore(conn, "servizio", serv.getTipo()+"/"+serv.getNome());
		}else{
			if(sogg.getConnettore()==null || CostantiRegistroServizi.DISABILITATO.equals(sogg.getConnettore().getTipo())){
				this.errori.add("Il servizio ["+idServizio+"] deve obbligatoriamente definire un connettore, poiche' il soggetto erogatore non ne possiede uno");
			}
		}

		// Azione di un servizio
		for(int k=0; k<serv.sizeParametriAzioneList();k++){
			ServizioAzione az = serv.getParametriAzione(k);
			
			if(az.getNome()==null){
				this.errori.add("Esiste un'azione del servizio "+idServizio+" per cui non è definito il nome");
				continue;
			}
			
			// 1. ogni azione di un servizio deve essere univoca
			int numA = 0;
			for (int h=0; h<serv.sizeParametriAzioneList(); h++) {
				ServizioAzione tmpSsa = serv.getParametriAzione(h);
				if (az.getNome().equals(tmpSsa.getNome()))
					numA++;
			}
			if (numA > 1)
				this.errori.add("Non può esistere più di un'azione con nome "+az.getNome()+" nel servizio "+idServizio);
			
			// L'azione deve esistere nell'accordo implementato dal servizio
			if(this.existsAccordoServizioParteComune(asps.getAccordoServizioParteComune())){
				AccordoServizioParteComune as = this.getAccordoServizioParteComune(asps.getAccordoServizioParteComune());
				if(asps.getPortType()!=null){
					if(this.existsPortType_AccordoServizioParteComune(as, asps.getPortType()) ){
						PortType pt = this.getPortType_AccordoServizioParteComune(as, asps.getPortType());
						if(this.existsAzione_PortType_AccordoServizio(pt, az.getNome())==false){
							this.errori.add("Il servizio ["+idServizio+
									"], utilizza un'azione ["+az.getNome()+"] che non risulta definita nell'accordo di servizio ["+
									asps.getAccordoServizioParteComune()+"] implementato dal servizio (istanziato per il servizio ["+asps.getPortType()+"])");
						}
					}else{
						// Caso gestito nel seguito di questo metodo
					}
				}else{
					if(this.existsAzione_AccordoServizioParteComune(as, az.getNome())==false){
						this.errori.add("Il servizio ["+idServizio+
								"], utilizza un'azione ["+az.getNome()+"] che non risulta definita nell'accordo di servizio ["+asps.getAccordoServizioParteComune()+"] implementato dal servizio");
					}
				}
			}else{
				// Caso gestito nel seguito di questo metodo
			}
			
			// Deve almeno o essere definito un connettore o essere definito i fruitori
			if( (az.getConnettore()==null || CostantiRegistroServizi.DISABILITATO.equals(az.getConnettore().getTipo())) && 
					az.sizeParametriFruitoreList()==0){
				this.errori.add("Non può esistere un'azione con nome "+az.getNome()+" nel servizio "+idServizio+" che non definisce ne al suo interno un connettore, ne una lista di fruitori");	
			}
			
			// Validazione connettore azione
			conn = az.getConnettore();
			if(conn!=null){
				this.validaConnettore(conn, "Azione ["+az.getNome()+"] ridefinita nel Servizio", idServizio);
			}
			
			// 3. Se presenti fruitori deve essere controllato che il tipo/nome indicano un soggetto realmente esistente nel registro dei servizi. 
			// Inoltre il connettore deve essere validato con le solite convenzioni.
			for(int w=0; w<az.sizeParametriFruitoreList();w++){
				ServizioAzioneFruitore ssaf = az.getParametriFruitore(w);
				String tipoSsaf = ssaf.getTipo();
				String nomeSsaf = ssaf.getNome();
				
				if(tipoSsaf==null){
					this.errori.add("Esiste un fruitore dell'azione "+az.getNome()+" del servizio "+serv.getTipo()+"/"+serv.getNome()+" per cui non è definito il tipo");
					continue;
				}
				if(nomeSsaf==null){
					this.errori.add("Esiste un fruitore dell'azione "+az.getNome()+" del servizio "+serv.getTipo()+"/"+serv.getNome()+" per cui non è definito il nome");
					continue;
				}
				
				if(this.existsSoggetto(tipoSsaf, nomeSsaf)==false){
					this.errori.add("Il fruitore "+tipoSsaf+"/"+nomeSsaf+" dell'azione "+az.getNome()+" del servizio "+serv.getTipo()+"/"+serv.getNome()+" non corrisponde a nessuno dei soggetti registrati");
				}
				conn = ssaf.getConnettore();
				if (conn != null){
					validaConnettore(conn, "", idServizio);
				}else{
					this.errori.add("Non e' stato definito il connettore per il Fruitore ["+tipoSsaf+"/"+nomeSsaf+"] dell'azione ["+az.getNome()+"] ridefinita nel Servizio "+idServizio);	
				}
			}
		}

		// Fruitore di un servizio
		for(int k=0; k<asps.sizeFruitoreList();k++){
			Fruitore fru = asps.getFruitore(k);
		
			if(fru.getTipo()==null){
				this.errori.add("Esiste un fruitore del servizio ["+idServizio+"] per cui non è definito il tipo");
				continue;
			}
			if(fru.getNome()==null){
				this.errori.add("Esiste un fruitore del servizio ["+idServizio+"] per cui non è definito il nome");
				continue;
			}
						
			// 3. Il tipo e il nome deve puntare ad un soggetto realmente esistente nel registro dei servizi.
			if(this.existsSoggetto(fru.getTipo(), fru.getNome())==false){
				this.errori.add("Il fruitore "+fru.getTipo()+"/"+fru.getNome()+" del servizio "+idServizio+" non corrisponde a nessuno dei soggetti registrati");
			}
				
			// 1. ogni fruitore (identificato dalla coppia tipo/nome) di un servizio deve essere univoco
			int numF = 0;
			for (int h=0; h<asps.sizeFruitoreList(); h++) {
				Fruitore tmpF = asps.getFruitore(h);
				if (fru.getNome().equals(tmpF.getNome()) && fru.getTipo().equals(tmpF.getTipo()))
					numF++;
			}
			if (numF > 1)
				this.errori.add("Non può esistere più di un fruitore con nome "+fru.getNome()+" e tipo "+fru.getTipo()+" per il servizio "+idServizio);
			
			// 2.  Il connettore, se presente, deve essere validato. NOTA: che il connettore puo' essere definito all'interno del servizio o direttamente nella root del registro dei servizi. In tal caso all'interno del servizio vi e' solo il nome del connettore. La validazione deve tenere conto di cio, e quindi se vi e' presente solo il nome, deve prima cercare il connettore nella root e poi validarlo come descritto nel paragrafo apposta.
			conn = fru.getConnettore();
			if (conn != null)
				validaConnettore(conn, "fruitore ["+fru.getTipo()+"/"+fru.getNome()+"] del servizio", idServizio);
			
			// Wsdl
			if(fru.getWsdlImplementativoErogatore()!=null){
				try{
					RegExpUtilities.validateUri(fru.getWsdlImplementativoErogatore(),this.checkEsistenzaFileDefinitoTramiteURI);
				}catch(Exception e){
					this.errori.add("Il wsdl implementativo erogatore del fruitore ["+fru.getTipo()+"/"+fru.getNome()+"] del servizio "+idServizio+" non è valido: "+e.getMessage());
				}
			}
			if(fru.getWsdlImplementativoFruitore()!=null){
				try{
					RegExpUtilities.validateUri(fru.getWsdlImplementativoFruitore(),this.checkEsistenzaFileDefinitoTramiteURI);
				}catch(Exception e){
					this.errori.add("Il wsdl implementativo fruitore del fruitore ["+fru.getTipo()+"/"+fru.getNome()+"] del servizio "+idServizio+" non è valido: "+e.getMessage());
				}
			}
			
			// XSD: filtro-duplicati: abilitato, disabilitato
			StatoFunzionalita filtroDup = fru.getFiltroDuplicati();
			if ( filtroDup != null && !filtroDup.equals(CostantiRegistroServizi.ABILITATO) && !filtroDup.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("Il filtro duplicati del fruitore ["+fru.getTipo()+"/"+fru.getNome()+"] nel servizio "+idServizio+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// XSD: conferma-ricezione: abilitato, disabilitato
			StatoFunzionalita confRic = fru.getConfermaRicezione();
			if ( confRic != null && !confRic.equals(CostantiRegistroServizi.ABILITATO) && !confRic.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("La conferma ricezione del fruitore ["+fru.getTipo()+"/"+fru.getNome()+"] nel servizio "+idServizio+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// XSD: id-collaborazione: abilitato, disabilitato
			StatoFunzionalita idColl = fru.getIdCollaborazione();
			if (idColl != null && !idColl.equals(CostantiRegistroServizi.ABILITATO) && !idColl.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("L'id collaborazione del fruitore ["+fru.getTipo()+"/"+fru.getNome()+"] nel servizio "+idServizio+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// XSD: consegna-in-ordine: abilitato, disabilitato
			StatoFunzionalita consOrd = fru.getConsegnaInOrdine();
			if (consOrd != null && !consOrd.equals(CostantiRegistroServizi.ABILITATO) && !consOrd.equals(CostantiRegistroServizi.DISABILITATO))
				this.errori.add("La consegna in ordine del fruitore ["+fru.getTipo()+"/"+fru.getNome()+"] nel servizio "+idServizio+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
			// Scadenza
			if(fru.getScadenza()!=null){
				try{
					Integer.parseInt(fru.getScadenza());
				}catch(Exception e){
					this.errori.add("Il valore associato alla scadenza del fruitore ["+fru.getTipo()+"/"+fru.getNome()+"] nel servizio "+idServizio+" dev'essere un numero intero");
				}
			}

			// XSD: client-auth: abilitato, disabilitato, default
			StatoFunzionalita clA = fru.getClientAuth();
			if (clA != null && !clA.equals(CostantiRegistroServizi.ABILITATO) && !clA.equals(CostantiRegistroServizi.DISABILITATO) && !clA.equals("default"))
				this.errori.add("Il client-auth del fruitore "+fru.getTipo()+"/"+fru.getNome()+" nel servizio "+idServizio+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+","+CostantiRegistroServizi.DISABILITATO+" o default");
		}

		
		// Allegati
		for(int i=0; i<asps.sizeAllegatoList(); i++){
			Documento allegato = asps.getAllegato(i);
			this.validateDocumento(allegato, RuoliDocumento.allegato,"Servizio["+idServizio+"]");
		}
		// SpecificheSemiformali
		for(int i=0; i<asps.sizeSpecificaSemiformaleList(); i++){
			Documento specificaSemiformale = asps.getSpecificaSemiformale(i);
			this.validateDocumento(specificaSemiformale, RuoliDocumento.specificaSemiformale,"Servizio["+idServizio+"]");
		}
		// SpecificheLivelliServizio
		for(int i=0; i<asps.sizeSpecificaLivelloServizioList(); i++){
			Documento specificaLivelloServizio = asps.getSpecificaLivelloServizio(i);
			this.validateDocumento(specificaLivelloServizio, RuoliDocumento.specificaLivelloServizio,"Servizio["+idServizio+"]");
		}
		// SpecificheSicurezza
		for(int i=0; i<asps.sizeSpecificaSicurezzaList(); i++){
			Documento specificaSicurezza = asps.getSpecificaSicurezza(i);
			this.validateDocumento(specificaSicurezza, RuoliDocumento.specificaSicurezza,"Servizio["+idServizio+"]");
		}
		
		
		// Il tipo deve essere uno tra quelli definiti in openspcoop2.properties. Ci puoi accedere attraverso il comando: org.openspcoop.pdd.config.OpenSPCoopProperties.getInstance().getTipiServizi()
		if(this.tipoServizi.contains(serv.getTipo())==false){
			this.errori.add("Il tipo del servizio "+serv.getTipo()+"/"+serv.getNome()+" non è valido (tipi utilizzabili: "+this.getTipoServizi()+")");
		}

		// Il nome del servizio deve possedere i stessi vincoli presenti per il nome di un servizio inserito nell'interfaccia grafica regserv.
		// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
		try{
			if (!RegularExpressionEngine.isMatch(serv.getNome(),"^[0-9A-Za-z_\\-\\.]+$")) {
				this.errori.add("Il nome del servizio "+serv.getTipo()+"/"+serv.getNome()+" dev'essere formato solo caratteri, cifre, '_' , '-' e '.'");
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare del servizio "+serv.getTipo()+"/"+serv.getNome()+" :" +e.getMessage(),e);
		}
	
		if(asps.getAccordoServizioParteComune()==null){
			this.errori.add("Il Servizio "+idServizio+" non implementa un accordo di servizio");
		}else{
			// Il nome di un accordo di servizio deve puntare ad un accordo realmente esistente.
			if(this.existsAccordoServizioParteComune(asps.getAccordoServizioParteComune())==false){
				this.errori.add("L'accordo servizio "+asps.getAccordoServizioParteComune()+" del servizio "+idServizio+" non corrisponde a nessuno degli accordi servizio registrati");
			}
			else{
				// Il nome di un port type, se presente, deve puntare ad un porttype definito dentro l'accordo di servizio del punto 7.
				String nomePT = asps.getPortType();
				if (nomePT != null) {
					AccordoServizioParteComune as = this.getAccordoServizioParteComune(asps.getAccordoServizioParteComune());
					if(this.existsPortType_AccordoServizioParteComune(as, nomePT)==false){
						this.errori.add("Il servizio "+nomePT+" del servizio "+idServizio+" non corrisponde a nessuno dei servizi dell'accordo servizio implementato ["+asps.getAccordoServizioParteComune()+"]");
					}
				}
			}
		}
			
		
		// ogni servizio identificato dalla quadrupla tipo/nome del servizio e tipo/nome del soggetto erogatore, deve essere univoco
		int numS = 0;
		for (int j=0; j<sogg.sizeAccordoServizioParteSpecificaList(); j++) {
			AccordoServizioParteSpecifica tmpS = sogg.getAccordoServizioParteSpecifica(j);
			if (serv.getNome().equals(tmpS.getServizio().getNome()) && serv.getTipo().equals(tmpS.getServizio().getTipo()) && ((serv.getNomeSoggettoErogatore() == null && tmpS.getServizio().getNomeSoggettoErogatore() == null) || serv.getNomeSoggettoErogatore().equals(tmpS.getServizio().getNomeSoggettoErogatore())) && ((serv.getTipoSoggettoErogatore() == null && tmpS.getServizio().getTipoSoggettoErogatore() == null) || serv.getTipoSoggettoErogatore().equals(tmpS.getServizio().getTipoSoggettoErogatore())))
				numS++;
		}
		if (numS > 1)
			this.errori.add("Non può esistere più di un servizio con nome "+serv.getNome()+" e tipo "+serv.getTipo()+" e nome soggetto erogatore "+serv.getNomeSoggettoErogatore()+" e tipo soggetto erogatore "+serv.getTipoSoggettoErogatore());

			
		// Wsdl
		if(asps.getWsdlImplementativoErogatore()!=null){
			try{
				RegExpUtilities.validateUri(asps.getWsdlImplementativoErogatore(),this.checkEsistenzaFileDefinitoTramiteURI);
			}catch(Exception e){
				this.errori.add("Il wsdl implementativo erogatore del servizio "+idServizio+" non è valido: "+e.getMessage());
			}
		}
		if(asps.getWsdlImplementativoFruitore()!=null){
			try{
				RegExpUtilities.validateUri(asps.getWsdlImplementativoFruitore(),this.checkEsistenzaFileDefinitoTramiteURI);
			}catch(Exception e){
				this.errori.add("Il wsdl implementativo fruitore del servizio "+idServizio+" non è valido: "+e.getMessage());
			}
		}
		

		// XSD: filtro-duplicati: abilitato, disabilitato
		StatoFunzionalita filtroDup = asps.getFiltroDuplicati();
		if ( filtroDup != null && !filtroDup.equals(CostantiRegistroServizi.ABILITATO) && !filtroDup.equals(CostantiRegistroServizi.DISABILITATO))
			this.errori.add("Il filtro duplicati del servizio "+idServizio+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
		// XSD: conferma-ricezione: abilitato, disabilitato
		StatoFunzionalita confRic = asps.getConfermaRicezione();
		if (confRic != null && !confRic.equals(CostantiRegistroServizi.ABILITATO) && !confRic.equals(CostantiRegistroServizi.DISABILITATO))
			this.errori.add("La conferma ricezione del servizio "+idServizio+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
		// XSD: id-collaborazione: abilitato, disabilitato
		StatoFunzionalita idColl = asps.getIdCollaborazione();
		if (idColl != null && !idColl.equals(CostantiRegistroServizi.ABILITATO) && !idColl.equals(CostantiRegistroServizi.DISABILITATO))
			this.errori.add("L'id collaborazione del servizio "+idServizio+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
		// XSD: consegna-in-ordine: abilitato, disabilitato
		StatoFunzionalita consOrd = asps.getConsegnaInOrdine();
		if (consOrd != null && !consOrd.equals(CostantiRegistroServizi.ABILITATO) && !consOrd.equals(CostantiRegistroServizi.DISABILITATO))
			this.errori.add("La consegna in ordine del servizio "+idServizio+" deve possedere il valore "+CostantiRegistroServizi.ABILITATO+" o "+CostantiRegistroServizi.DISABILITATO);
		// Scadenza
		if(asps.getScadenza()!=null){
			try{
				Integer.parseInt(asps.getScadenza());
			}catch(Exception e){
				this.errori.add("Il valore associato alla scadenza del servizio "+idServizio+" dev'essere un numero intero");
			}
		}

	}

	private  void validaPortaDominio(PortaDominio pdd) throws DriverRegistroServiziException {
		
		// required
		if(pdd.getNome()==null){
			this.errori.add("Esiste una porta di dominio senza nome");
			return;
		}
		
		// Controllo sul nome
		try{
			if (!RegularExpressionEngine.isMatch(pdd.getNome(),"^[0-9A-Za-z_\\-\\.]+$")) {
				this.errori.add("Il nome della porta di dominio dev'essere formato solo da caratteri, cifre, '_' , '-' e '.'");
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante l'analisi tramite espressione regolare del nome della porta di dominio :" +e.getMessage(),e);
		}	
		
		// XSD: client-auth: abilitato, disabilitato
		StatoFunzionalita clA = pdd.getClientAuth();
		if ((clA != null) && !clA.equals("abilitato") && !clA.equals("disabilitato"))
			this.errori.add("Il client-auth della porta di dominio "+pdd.getNome()+" dev'essere abilitato o disabilitato");
		
		// se presente 'client-auth=abilitato' il subject deve essere fornito
		if (pdd.getClientAuth() != null && pdd.getClientAuth().equals("abilitato") && (pdd.getSubject() == null || pdd.getSubject().equals("")))
			this.errori.add("Il subject della porta di dominio "+pdd.getNome()+" dev'essere fornito quando client-auth è abilitato");

		// Ogni porta di dominio deve avere un nome diverso!
		int numPd = 0;
		for(int j=0; j<this.registro.sizePortaDominioList();j++){
			PortaDominio tmpPd = this.registro.getPortaDominio(j);
			if (pdd.getNome().equals(tmpPd.getNome()))
				numPd++;
		}
		if (numPd > 1)
			this.errori.add("Non può esistere più di una porta di dominio con nome "+pdd.getNome());
	
	}

	private  void validaConnettore(Connettore conn, String oggetto, String tipoEnomeOggetto) throws DriverRegistroServiziException {

		// Valida il connettore
		// NOTA: che il connettore puo' essere definito all'interno dell'oggetto o direttamente nella root del registro dei servizi. In tal caso all'interno dell'oggetto vi e' solo il nome del connettore. La validazione deve tenere conto di cio, e quindi se vi e' presente solo il nome, deve prima cercare il connettore nella root e poi validarlo.
		// 1. i connettori definiti nella radice del registro devono possedere un nome univoco
		// 2. Il connettore deve possedere un tipo definito. Il tipo di connettore definito deve essere validato utilizzando il seguente metodo: org.openspcoop.pdd.config.ClassNameProperties.getInstance().getConnettore(tipo). Se il metodo ritorna null, il tipo di connettore non e' valido.
		// 3. Se il connettore e' di tipo http deve possedere i stessi vincoli presenti per un connettore http inserito nell'interfaccia grafica regserv, e cioe deve possedere le property che servono ad un connettore http.
		// 4. Se il connettore e' di tipo jms deve possedere i stessi vincoli presenti per un connettore jms inserito nell'interfaccia grafica regserv, e cioe deve possedere le property che servono ad un connettore jms.
		// 5. Se il connettore e' di tipo 'disabilitato' non deve possedere property

		String nomeConn = conn.getNome();
		String tipoConn = conn.getTipo();

		// required
		if(nomeConn==null){
			if(oggetto==null)
				this.errori.add("Esiste un connettore nella radice del registro per cui non è definito il nome");
			else
				this.errori.add("Esiste un connettore del "+oggetto+" "+tipoEnomeOggetto+" per cui non è definito il nome");
			return;
		}
		
		if(oggetto==null){

			// 1. i connettori definiti nella radice del registro devono possedere un nome univoco
			int numC = 0;
			for(int j=0; j<this.registro.sizeConnettoreList();j++){
				Connettore tmpC = this.registro.getConnettore(j);
				if (nomeConn.equals(tmpC.getNome()))
					numC++;
			}
			if (numC > 1)
				this.errori.add("Non può esistere più di un connettore con nome "+nomeConn+" definito come elemento radice del registro servizi");
		}

		else{
			if (tipoConn == null) {
				// Cerco il connettore nella root del registro
				conn = null;
				for (int j=0; j<this.registro.sizeConnettoreList();j++) {
					Connettore connettore = this.registro.getConnettore(j);
					if (nomeConn.equals(connettore.getNome())) {
						conn = connettore;
						break;
					}
				}
			}
			if (conn == null)
				this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" non corrisponde a nessuno dei connettori registrati");
			else {
				tipoConn = conn.getTipo();
			}
		}

		// 2. Il connettore deve possedere un tipo definito. Il tipo di connettore definito deve essere validato utilizzando il seguente metodo: org.openspcoop.pdd.config.ClassNameProperties.getInstance().getConnettore(tipo). Se il metodo ritorna null, il tipo di connettore non e' valido.
		if(this.tipoConnettori.contains(tipoConn)==false){
			if(oggetto==null){
				this.errori.add("Il tipo ["+tipoConn+"] del connettore "+nomeConn+", definito nella radice del registro, non è valido (Tipi conosciuti: "+this.getTipoConnettori()+")");
			}else{
				this.errori.add("Il tipo ["+tipoConn+"] del connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" non è valido (Tipi conosciuti: "+this.getTipoConnettori()+")");
			}
		}
		else{

			// Check generale sulle proprietà
			for (int j=0; j<conn.sizePropertyList();j++) {
				Property cp = conn.getProperty(j);
				if(cp.getNome()==null){
					if(oggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, possiede una proprietà per cui non è definito il nome");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" possiede una proprietà per cui non è definito il nome");
					}
					return;
				}
				if(cp.getValore()==null){
					if(oggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, possiede una proprietà per cui non è definito il valore");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" possiede una proprietà per cui non è definito il valore");
					}
					return;
				}
			}
			
			// Connettore HTTP
			if (tipoConn.equals(TipiConnettore.HTTP.getNome())) {
				String urlConn = null;
				for (int j=0; j<conn.sizePropertyList();j++) {
					Property cp = conn.getProperty(j);
					if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTP_LOCATION)) {
						urlConn = cp.getValore();
						break;
					}
				}
				if (urlConn == null){
					if(oggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo http, ma non ha una url definita");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo http, ma non ha una url definita");
					}
				}
				else{
					try{
						this.validaUrl(urlConn);
					}catch(Exception e){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo http, ma non ha una url valida: "+e.getMessage());
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo http, ma non ha una url valida: "+e.getMessage());
						}
					}
				}
			}

			// Connettore JMS
			else if (tipoConn.equals(TipiConnettore.JMS.getNome())) {
				String jmsNome = null, jmsTipo = null, jmsConnFact = null, jmsSendAs = null;
				for (int j=0; j<conn.sizePropertyList();j++) {
					Property cp = conn.getProperty(j);
					if (cp.getNome().equals(CostantiDB.CONNETTORE_JMS_NOME))
						jmsNome = cp.getValore();
					if (cp.getNome().equals(CostantiDB.CONNETTORE_JMS_TIPO))
						jmsTipo = cp.getValore();
					if (cp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY))
						jmsConnFact = cp.getValore();
					if (cp.getNome().equals(CostantiDB.CONNETTORE_JMS_SEND_AS))
						jmsSendAs = cp.getValore();
				}
				if (jmsNome == null){
					if(oggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo jms, ma non ha un nome coda/topic definito");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo jms, ma non ha un nome coda/topic definito");
					}
				}
				if (jmsTipo == null){
					if(oggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo jms, ma non ha un tipo coda/topic definito");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo jms, ma non ha un tipo coda/topic definito");
					}
				}else{
					if (!jmsTipo.equals(CostantiConnettori.CONNETTORE_JMS_TIPO_QUEUE) && !jmsTipo.equals(CostantiConnettori.CONNETTORE_JMS_TIPO_TOPIC)){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo jms, ma non ha un tipo coda/topic valido (valori assumibili sono topic/queue)");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo jms, ma non ha un tipo coda/topic valido (valori assumibili sono topic/queue)");
						}
					}
				}
				if (jmsConnFact == null){
					if(oggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo jms, ma non ha una connection factory definita");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo jms, ma non ha una connection factory definita");
					}
				}
				if (jmsSendAs == null){
					if(oggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo jms, ma non ha un tipo di messaggio (sendAs) definito");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo jms, ma non ha un tipo di messaggio (sendAs) definito");
					}
				}else{
					if (!jmsSendAs.equals(CostantiConnettori.CONNETTORE_JMS_SEND_AS_TEXT_MESSAGE) && !jmsSendAs.equals(CostantiConnettori.CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE)){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo jms, ma non ha un tipo di messaggio (sendAs) (valori assumibili sono TextMessage/BytesMessage)");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo jms, ma non ha un tipo di messaggio (sendAs) (valori assumibili sono TextMessage/BytesMessage)");
						}
					}
				}
			}

			// Connettore HTTPS
			else if (tipoConn.equals(TipiConnettore.HTTPS.getNome())) {
				String urlConn = null;
				String trustStoreLocation = null;
				String trustStorePassword = null;
				String keyStoreLocation = null;
				String keyStorePassword = null;
				String keyPassword = null;
				String hostNameVerifier = null;
				for (int j=0; j<conn.sizePropertyList();j++) {
					Property cp = conn.getProperty(j);
					if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_LOCATION)) {
						urlConn = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION)) {
						trustStoreLocation = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD)) {
						trustStorePassword = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION)) {
						keyStoreLocation = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD)) {
						keyStorePassword = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD)) {
						keyPassword = cp.getValore();
					}
					else if (cp.getNome().equals(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER)) {
						hostNameVerifier = cp.getValore();
					}
				}
				if (urlConn == null){
					if(oggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo http, ma non ha una url definita");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo http, ma non ha una url definita");
					}
				}
				else{
					try{
						this.validaUrl(urlConn);
					}catch(Exception e){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo http, ma non ha una url valida: "+e.getMessage());
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo http, ma non ha una url valida: "+e.getMessage());
						}
					}
				}
				if (trustStoreLocation != null){
					File f = new File(trustStoreLocation);
					if(f.exists()==false){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, definisce un truststore "+trustStoreLocation+" che non esiste");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" definisce un truststore "+trustStoreLocation+" che non esiste");
						}
					}
					else if(f.isFile()==false){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, definisce un truststore "+trustStoreLocation+" che non e' un file");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" definisce un truststore "+trustStoreLocation+" che non e' un file");
						}
					}
					else if(f.canRead()==false){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, definisce un truststore "+trustStoreLocation+" che non e' accessibile (permessi in lettura non forniti)");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" definisce un truststore "+trustStoreLocation+" che non e' accessibile (permessi in lettura non forniti)");
						}
					}
					else if(trustStorePassword==null){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, definisce un truststore "+trustStoreLocation+" per cui non e' stata specificata una password");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" definisce un truststore "+trustStoreLocation+" per cui non e' stata specificata una password");
						}
					}
				}
				if (keyStoreLocation != null){
					File f = new File(keyStoreLocation);
					if(f.exists()==false){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, definisce un keystore "+keyStoreLocation+" che non esiste");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" definisce un keystore "+keyStoreLocation+" che non esiste");
						}
					}
					else if(f.isFile()==false){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, definisce un keystore "+keyStoreLocation+" che non e' un file");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" definisce un keystore "+keyStoreLocation+" che non e' un file");
						}
					}
					else if(f.canRead()==false){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, definisce un keystore "+keyStoreLocation+" che non e' accessibile (permessi in lettura non forniti)");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" definisce un keystore "+keyStoreLocation+" che non e' accessibile (permessi in lettura non forniti)");
						}
					}
					else if(keyStorePassword==null){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, definisce un keystore "+keyStoreLocation+" per cui non e' stata specificata una password");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" definisce un keystore "+keyStoreLocation+" per cui non e' stata specificata una password");
						}
					}
					else if(keyPassword==null){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, definisce un keystore "+keyStoreLocation+" per cui non e' stata specificata una password per la chiave privata");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" definisce un keystore "+keyStoreLocation+" per cui non e' stata specificata una password per la chiave privata");
						}
					}
				}
				if(hostNameVerifier!=null){
					try{
						Boolean.parseBoolean(hostNameVerifier);
					}catch(Exception e){
						if(oggetto==null){
							this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, associa un valore non valido  alla proprieta' 'hostNameVerifier'; valori utilizzabili: true e false");
						}else{
							this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" associa un valore non valido  alla proprieta' 'hostNameVerifier'; valori utilizzabili: true e false");
						}
					}
				}
			}
			
			else if (tipoConn.equals(TipiConnettore.DISABILITATO.getNome())) {
				if (conn.sizePropertyList()>0) {
					if(oggetto==null){
						this.errori.add("Il connettore "+nomeConn+", definito nella radice del registro, è di tipo disabilitato, ma ha delle properties definite");
					}else{
						this.errori.add("Il connettore "+nomeConn+" del "+oggetto+" "+tipoEnomeOggetto+" è di tipo disabilitato, ma ha delle properties definite");
					}
				}
			}
		}
	}

	private void validateDocumento(Documento doc,RuoliDocumento ruolo,String oggetto) {

		if(doc.getTipo()==null){
			this.errori.add("Esiste un documento con ruolo ("+ruolo.toString()+") per l'oggetto "+oggetto+" per cui non è definito il tipo");
			return;
		}
		if(doc.getFile()==null){
			this.errori.add("Esiste un documento con ruolo ("+ruolo.toString()+") per l'oggetto "+oggetto+" per cui non è definito il file");
			return;
		}
		
		String idDocumento = "("+ruolo.toString()+")("+doc.getTipo()+")("+doc.getFile()+") ";

		// analizzo tipo
		if(RuoliDocumento.allegato.toString().equals(ruolo.toString())){
			// Allegato vale qualsiasi tipo 
		}else if(RuoliDocumento.specificaSemiformale.toString().equals(ruolo.toString())){
			if(TipiDocumentoSemiformale.toEnumConstant(doc.getTipo())==null){
				String [] tipi = TipiDocumentoSemiformale.toStringArray();
				StringBuffer bf = new StringBuffer();
				for(int i=0; i<tipi.length; i++){
					if(i>0)
						bf.append(",");
					bf.append(tipi[i]);
				}
				this.errori.add(idDocumento+" tipo non utilizzabile in una specifica semiformale (oggetto: "+oggetto+"), valori ammessi: ");
			}
		}else if(RuoliDocumento.specificaCoordinamento.toString().equals(ruolo.toString())){
			if(TipiDocumentoCoordinamento.toEnumConstant(doc.getTipo())==null){
				String [] tipi = TipiDocumentoCoordinamento.toStringArray();
				StringBuffer bf = new StringBuffer();
				for(int i=0; i<tipi.length; i++){
					if(i>0)
						bf.append(",");
					bf.append(tipi[i]);
				}
				this.errori.add(idDocumento+" tipo non utilizzabile in una specifica di coordinamento (oggetto: "+oggetto+"), valori ammessi: ");
			}
		}else if(RuoliDocumento.specificaLivelloServizio.toString().equals(ruolo.toString())){
			if(TipiDocumentoLivelloServizio.toEnumConstant(doc.getTipo())==null){
				String [] tipi = TipiDocumentoLivelloServizio.toStringArray();
				StringBuffer bf = new StringBuffer();
				for(int i=0; i<tipi.length; i++){
					if(i>0)
						bf.append(",");
					bf.append(tipi[i]);
				}
				this.errori.add(idDocumento+" tipo non utilizzabile in una specifica dei livelli di servizio (oggetto: "+oggetto+"), valori ammessi: ");
			}
		}else if(RuoliDocumento.specificaSicurezza.toString().equals(ruolo.toString())){
			if(TipiDocumentoSicurezza.toEnumConstant(doc.getTipo())==null){
				String [] tipi = TipiDocumentoSicurezza.toStringArray();
				StringBuffer bf = new StringBuffer();
				for(int i=0; i<tipi.length; i++){
					if(i>0)
						bf.append(",");
					bf.append(tipi[i]);
				}
				this.errori.add(idDocumento+" tipo non utilizzabile in una specifica di sicurezza (oggetto: "+oggetto+"), valori ammessi: ");
			}
		}else{
			this.errori.add((idDocumento+" ruolo non conosciuto (oggetto: "+oggetto+"): "+ruolo.toString()));
		}

		// Validazione url
		try{
			RegExpUtilities.validateUri(doc.getFile(),this.checkEsistenzaFileDefinitoTramiteURI);
		}catch(Exception e){
			this.errori.add(idDocumento+" file non valido (oggetto: "+oggetto+"): "+e.getMessage());
		}
	}







	/* ------------------ UTILITY ------------------------------*/

	private void validaUrl(String url) throws java.net.MalformedURLException {
		java.net.URL testUrl = new java.net.URL(url);
		testUrl.toString();
	}

	private boolean existsAccordoCooperazione(String uriAccordo) throws DriverRegistroServiziException{
		return this.getAccordoCooperazione(uriAccordo)!=null;
	}
	private AccordoCooperazione getAccordoCooperazione(String uriAccordo) throws DriverRegistroServiziException{
		for(int j=0; j<this.registro.sizeAccordoCooperazioneList();j++){
			AccordoCooperazione ac = this.registro.getAccordoCooperazione(j);
			if (this.idAccordoCooperazioneFactory.getUriFromAccordo(ac).equals(uriAccordo)) {
				return ac;
			}
		}
		return null;
	}

	private boolean existsAccordoServizioParteComune(String uriAccordo) throws DriverRegistroServiziException{
		return this.getAccordoServizioParteComune(uriAccordo)!=null;
	}
	private AccordoServizioParteComune getAccordoServizioParteComune(String uriAccordo) throws DriverRegistroServiziException{
		for(int j=0; j<this.registro.sizeAccordoServizioParteComuneList();j++){
			AccordoServizioParteComune as = this.registro.getAccordoServizioParteComune(j);
			if (this.idAccordoFactory.getUriFromAccordo(as).equals(uriAccordo)) {
				return as;
			}
		}
		return null;
	}

	private boolean existsAzione_AccordoServizioParteComune(AccordoServizioParteComune as, String azione) throws DriverRegistroServiziException{
		return this.getAzione_AccordoServizioParteComune(as, azione)!=null;
	}
	private Azione getAzione_AccordoServizioParteComune(AccordoServizioParteComune as, String azione) throws DriverRegistroServiziException{
		for(int j=0; j<as.sizeAzioneList();j++){
			Azione az = as.getAzione(j);
			if(az.getNome().equals(azione))
				return az;
		}
		return null;
	}

	private boolean existsPortType_AccordoServizioParteComune(AccordoServizioParteComune as, String porttype) throws DriverRegistroServiziException{
		return this.getPortType_AccordoServizioParteComune(as, porttype)!=null;
	}
	private PortType getPortType_AccordoServizioParteComune(AccordoServizioParteComune as, String porttype) throws DriverRegistroServiziException{
		for(int j=0; j<as.sizePortTypeList();j++){
			PortType pt = as.getPortType(j);
			if(pt.getNome().equals(porttype))
				return pt;
		}
		return null;
	}

	private boolean existsAzione_PortType_AccordoServizio(PortType pt, String azione) throws DriverRegistroServiziException{
		return this.getAzione_PortType_AccordoServizio(pt, azione)!=null;
	}
	private Operation getAzione_PortType_AccordoServizio(PortType pt, String azione) throws DriverRegistroServiziException{
		for(int j=0; j<pt.sizeAzioneList();j++){
			Operation az = pt.getAzione(j);
			if(az.getNome().equals(azione))
				return az;
		}
		return null;
	}

	private boolean existsPortaDominio(String nome){
		return this.getPortaDominio(nome)!=null;
	}
	private PortaDominio getPortaDominio(String nome){
		for(int j=0; j<this.registro.sizePortaDominioList();j++){
			PortaDominio pdd = this.registro.getPortaDominio(j);
			if (nome.equals(pdd.getNome())) {
				return pdd;
			}
		}
		return null;
	}

	private boolean existsSoggetto(String tipo,String nome){
		return this.getSoggetto(tipo, nome)!=null;
	}
	private Soggetto getSoggetto(String tipo,String nome){
		for(int j=0; j<this.registro.sizeSoggettoList();j++){
			Soggetto ss = this.registro.getSoggetto(j);
			if (tipo.equals(ss.getTipo()) && nome.equals(ss.getNome())) {
				return ss;
			}
		}
		return null;
	}

	private boolean existsAccordoServizioParteSpecifica(String tipo,String nome,String tipoSoggetto,String nomeSoggetto){
		return this.getAccordoServizioParteSpecifica(tipo, nome, tipoSoggetto, nomeSoggetto)!=null;
	}
	private AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(String tipo,String nome,String tipoSoggetto,String nomeSoggetto){
		for(int j=0; j<this.registro.sizeSoggettoList();j++){
			Soggetto ss = this.registro.getSoggetto(j);
			if (tipoSoggetto.equals(ss.getTipo()) && nomeSoggetto.equals(ss.getNome())) {
				for(int h=0; h<ss.sizeAccordoServizioParteSpecificaList();h++){
					AccordoServizioParteSpecifica serv = ss.getAccordoServizioParteSpecifica(h);
					if (tipo.equals(serv.getServizio().getTipo()) && nome.equals(serv.getServizio().getNome())) {	
						return serv;
					}
				}
			}
		}
		return null;
	}
}
