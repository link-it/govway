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
package org.openspcoop2.protocol.sdi.validator;

import it.gov.fatturapa.sdi.messaggi.v1_0.constants.TipiMessaggi;

import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * SDIValidatoreNomeFile
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIValidatoreNomeFile {

	/**
	 * NOTE differenze tra progressivo nel nome del File, progressivo all'interno dei messaggi e messagiId
	 * 
	 * 
	 *  ---Fattura (Progressivo) ---
	 *  
	 *  Sembrano informazioni differenti.
	 *  
	 *  Progressivo interno alla fattura:
	 *  - descrizione del formatoTabellare: progressivo univoco, attribuito dal soggetto che trasmette, relativo ad ogni singolo documento fattura
	 *  - xsd -> <xs:element name="ProgressivoInvio" type="String10Type" /> dove String10Type = (\p{IsBasicLatin}{1,10})
	 *  
	 *  Progressivo fileName: è relativo al trasmittente
	 *  - il progressivo univoco del file è rappresentato da una stringa alfanumerica di lunghezza massima di 5 caratteri e con valori ammessi [a-z], [A-Z], [0-9].
	 *  
	 *  MessageId metainfomrazioni:
	 *  - descrizione: Identificativo del messaggio 
	 *  - xsd -> <xsd:element name="MessageId" type="types:MessageId_Type"/> dove 
	 *  		<xsd:simpleType name="MessageId_Type">
	 *	                <xsd:restriction base="xsd:string">
	 *                        <xsd:minLength value="1" />
	 *	                        <xsd:maxLength value="14" />
	 *	                </xsd:restriction>
	 *	        </xsd:simpleType>
	 *	
	 * 
	 *          
	 *  ---Fattura (IdPaese e IdCodice) ---
	 *  
	 *  Sembra la stessa informazione.
	 *  
	 *  IdTrasmittente interno alla fattura:
	 *  - descrizione del formatoTabellare: è l’identificativo univoco del soggetto trasmittente; 
	 *    per i soggetti residenti in Italia, siano essi persone fisiche o giuridiche, 
	 *    corrisponde al codice fiscale preceduto da IT; per i soggetti non residenti corrisponde al numero identificativo IVA 
	 *    (dove i primi due caratteri rappresentano il paese secondo lo standard ISO 3166-1 alpha-2 code, ed i restanti, 
	 *    fino ad un massimo di 28, il codice vero e proprio) 
	 *    . idPaese: codice della nazione espresso secondo lo standard ISO 3166-1 alpha-2 code 
	 *    . idCodice: codice identificativo fiscale 
	 *  - xsd -> <xs:element name="IdTrasmittente" type="IdFiscaleType" /> dove
	 *		     <xs:complexType name="IdFiscaleType">
	 *			    <xs:sequence>
	 *			      <xs:element name="IdPaese" type="NazioneType" />
	 *			      <xs:element name="IdCodice" type="CodiceType" />
	 *			    </xs:sequence>
	 *			  </xs:complexType>
	 *		      <xs:simpleType name="NazioneType">
	 *			    <xs:restriction base="xs:string">
	 *			      <xs:pattern value="[A-Z]{2}" />
	 *			    </xs:restriction>
	 *			  </xs:simpleType>
	 *		      <xs:simpleType name="CodiceType">
	 *			    <xs:restriction base="xs:string">
	 *			      <xs:minLength value="1" />
	 *			      <xs:maxLength value="28" />
	 *			    </xs:restriction>
	 *			  </xs:simpleType>
	 *  
	 *  IdTrasmittente fileName:
	 *  - il codice paese va espresso secondo lo standard ISO 3166-1 alpha-2 code;
	 *  - l’identificativo univoco del soggetto trasmittente, sia esso persona fisica o persona giuridica, 
	 *    è rappresentato dal suo identificativo fiscale (codice fiscale nel caso di soggetto trasmittente residente in Italia, 
	 *    identificativo proprio del paese di appartenenza nel caso di soggetto trasmittente residente all’estero); 
	 *    la lunghezza di questo identificativo è di:
	 *    . 11 caratteri (minimo) e 16 caratteri (massimo) nel caso di codice paese IT;
	 *    . 2 caratteri (minimo) e 28 caratteri (massimo) altrimenti;
	 *    
	 *    
	 *    
	 *    
	 *  --- Messaggi (Progressivo) ---
	 *  
	 *  Sembrano informazioni differenti.
	 *  
	 *  Progressivo interno ai messaggi:
	 *  - non esiste
	 *  
	 *  Progressivo fileName: è relativo al file inviato
	 *  - Il Progressivo univoco deve essere una stringa alfanumerica di lunghezza massima 3 caratteri e 
	 *  con valori ammessi [a-z], [A-Z], [0-9] che identifica univocamente ogni notifica / ricevuta relativa al file inviato.
	 *  
	 *  MessageId :
	 *  - descrizione: Identificativo del messaggio 
	 *  - xsd -> <xsd:element name="MessageId" type="types:MessageId_Type"/> dove 
	 *  		<xsd:simpleType name="MessageId_Type">
	 *	                <xsd:restriction base="xsd:string">
	 *                        <xsd:minLength value="1" />
	 *	                        <xsd:maxLength value="14" />
	 *	                </xsd:restriction>
	 *	        </xsd:simpleType>
	 */
	
	
	
	
	
	private static List<String> CODICI_PAESE = null;
	private static synchronized void initCodiciPaese(){
		if(CODICI_PAESE==null){
			CODICI_PAESE = new ArrayList<String>(); 
			String [] l = Locale.getISOCountries();
			for (int i = 0; i < l.length; i++) {
				CODICI_PAESE.add(l[i]);
			}
		}
	}
	private static void validaCodicePaese(String codicePaese) throws ProtocolException{
		if(CODICI_PAESE==null){
			initCodiciPaese();
		}
		if(CODICI_PAESE.contains(codicePaese)==false){
			throw new ProtocolException("CodicePaese ["+codicePaese+"] sconosciuto");
		}
	}
	
	public static void validaNomeFileFattura(String nomeFile, boolean isRicezione) throws ProtocolException{
		
		/**
		 * <codice Paese>< identificativo univoco del soggetto trasmittente >_<progressivoUnicoFile>
		 * dove: 
		 * - il codice paese va espresso secondo lo standard ISO 3166-1 alpha-2 code; 
		 * - l’identificativo univoco del soggetto trasmittente, sia esso persona fisica o persona giuridica, 
		 * 		è rappresentato dal suo identificativo fiscale (codice fiscale  nel caso di soggetto trasmittente residente in Italia, 
		 * 		identificativo proprio del paese di appartenenza nel caso di soggetto trasmittente residente all’estero); 
		 * 		la  lunghezza di questo identificativo è di: 11 caratteri (minimo) e 16 caratteri (massimo) nel caso di codice paese IT; 
		 *		2 caratteri (minimo) e 28 caratteri (massimo) altrimenti; 
		 * -il progressivo univoco del file è rappresentato da una stringa alfanumerica di lunghezza massima di 5 caratteri 
		 * 	e con valori ammessi [a-z], [A-Z], [0-9].
		 * 
		 *  Il file deve essere firmato elettronicamente (come indicato al precedente paragrafo 2.1); 
		 *  in base al formato di firma elettronica adottato, l’estensione del file assume il valore “.xml” (XAdES-BES) oppure “.xml.p7m” (CadES-BES) .
		 *  Il separatore tra il secondo ed il terzo elemento del nome file è il carattere underscore (“_”), codice ASCII 95. 
		 *  Nel caso c) il nome del file deve rispettare la stessa nomenclatura e l’estensione del file può essere solo .zip. 
		 *  
		 *  Es.: 
		 *  ITAAABBB99T99X999W_00001.zip
		 *  ITAAABBB99T99X999W_00002.xml
		 *  ITAAABBB99T99X999W_00003.xml
		 *  ITAAABBB99T99X999W_00004.xml.p7m 
		 */
		
		if(nomeFile==null){
			throw new ProtocolException("NomeFile Fattura non fornito");
		}
		if(nomeFile.length()<10){ // 2(codicePaese)+2(minimoPaeseDiversoIT)+1(_)+1(progressivoUnivoco)+1(.)+3(xml o pdf)
			throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (length="+nomeFile.length()+"<10)");
		}
		if(nomeFile.contains("_")==false){
			throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto ('_' non presente)");
		}
		if(nomeFile.contains(".")==false){
			throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto ('.' non presente)");
		}
		String [] splitUnderscore = nomeFile.split("_");
		if(splitUnderscore.length!=2){
			throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (split('_')="+splitUnderscore.length+"<>2)");
		}
		String leftCode = splitUnderscore[0];
		String rightCode = splitUnderscore[1];
		if(rightCode.contains(".")==false){
			throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (rightUnderscore=["+rightCode+"] '.' non presente)");
		}
		String [] splitPoint = rightCode.split("\\.");
		if(splitPoint.length!=2 && splitPoint.length!=3){
			throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (split('.')="+splitPoint.length+"<>2/3)");
		}
		
		// codicePaese
		String codicePaese = nomeFile.substring(0, 2);
		validaCodicePaese(codicePaese);
		
		// identificativoTrasmittente
		@SuppressWarnings("unused")
		String identificativoTrasmittente = null;
		if("IT".equals(codicePaese)){
			if(leftCode.length()<13){
				throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (leftUnderscore=["+leftCode+"] length="+leftCode.length()+"<13)");
			}
			if(leftCode.length()>18){
				throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (leftUnderscore=["+leftCode+"] length="+leftCode.length()+">18)");
			}
		}
		else{
			if(leftCode.length()<4){
				throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (leftUnderscore=["+leftCode+"] length="+leftCode.length()+"<4)");
			}
			if(leftCode.length()>30){
				throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (leftUnderscore=["+leftCode+"] length="+leftCode.length()+">30)");
			}
		}
		identificativoTrasmittente = leftCode.substring(2);

		// progressivoUnivoco
		String progressivoUnivoco = splitPoint[0];
		if(progressivoUnivoco.length()<1){
			throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (progressivoUnivoco=["+progressivoUnivoco+"] length="+progressivoUnivoco.length()+"<1)");
		}
		if(progressivoUnivoco.length()>5){
			throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (progressivoUnivoco=["+progressivoUnivoco+"] length="+progressivoUnivoco.length()+">5)");
		}
		try{
			if(!RegularExpressionEngine.isMatch(progressivoUnivoco,"^[A-Za-z0-9]*$")){
				throw new ProtocolException("Sono presenti caratteri non alfanumerici");
			}
		}catch(Exception e){
			throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (progressivoUnivoco=["+progressivoUnivoco+"] "+e.getMessage()+")");
		}
		
		// extension
		String ext = rightCode.substring((progressivoUnivoco+".").length(), rightCode.length());
		if(ext==null){
			throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (extension non presente)");
		}
		if(isRicezione){
			if(!SDICostanti.SDI_FATTURA_ESTENSIONE_XML.equalsIgnoreCase(ext) && 
					!SDICostanti.SDI_FATTURA_ESTENSIONE_P7M.equalsIgnoreCase(ext) ){
				throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (extension=["+ext+"] non valida, attesa "+
						SDICostanti.SDI_FATTURA_ESTENSIONE_XML+" o "+
						SDICostanti.SDI_FATTURA_ESTENSIONE_P7M+")");
			}
		}
		else{
			if(!SDICostanti.SDI_FATTURA_ESTENSIONE_XML.equalsIgnoreCase(ext) && 
					!SDICostanti.SDI_FATTURA_ESTENSIONE_P7M.equalsIgnoreCase(ext) &&
					!SDICostanti.SDI_FATTURA_ESTENSIONE_ZIP.equalsIgnoreCase(ext)){
				throw new ProtocolException("NomeFile Fattura ["+nomeFile+"] con struttura non corretto (extension=["+ext+"] non valida, attesa "+
						SDICostanti.SDI_FATTURA_ESTENSIONE_XML+" o "+
						SDICostanti.SDI_FATTURA_ESTENSIONE_P7M+" o "+
						SDICostanti.SDI_FATTURA_ESTENSIONE_ZIP+")");
			}
		}
			
	}
	
	public static String getNomeFileFatturaSenzaEstensione(String nomeFileFattura){
		String nomeFileFatturaSenzaEstensione = new String(nomeFileFattura);
		if(nomeFileFatturaSenzaEstensione.toLowerCase().endsWith(SDICostanti.SDI_FATTURA_ESTENSIONE_XML) 
				&& nomeFileFatturaSenzaEstensione.length()>(SDICostanti.SDI_FATTURA_ESTENSIONE_XML.length()+1)){
			nomeFileFatturaSenzaEstensione = nomeFileFatturaSenzaEstensione.substring(0,(nomeFileFatturaSenzaEstensione.length()-(SDICostanti.SDI_FATTURA_ESTENSIONE_XML.length()+1)));
		}
		else if(nomeFileFatturaSenzaEstensione.toLowerCase().endsWith(SDICostanti.SDI_FATTURA_ESTENSIONE_P7M)
			&& nomeFileFatturaSenzaEstensione.length()>(SDICostanti.SDI_FATTURA_ESTENSIONE_P7M.length()+1)){
			nomeFileFatturaSenzaEstensione = nomeFileFatturaSenzaEstensione.substring(0,(nomeFileFatturaSenzaEstensione.length()-(SDICostanti.SDI_FATTURA_ESTENSIONE_P7M.length()+1)));
		}
		else if(nomeFileFatturaSenzaEstensione.toLowerCase().endsWith(SDICostanti.SDI_FATTURA_ESTENSIONE_ZIP)
			&& nomeFileFatturaSenzaEstensione.length()>(SDICostanti.SDI_FATTURA_ESTENSIONE_ZIP.length()+1)){
			nomeFileFatturaSenzaEstensione = nomeFileFatturaSenzaEstensione.substring(0,(nomeFileFatturaSenzaEstensione.length()-(SDICostanti.SDI_FATTURA_ESTENSIONE_ZIP.length()+1)));
		}
		return nomeFileFatturaSenzaEstensione;
	}
	
	public static String getNomeFileFatturaPerMessaggi(String nomeFileFattura){
		
		String nomeFileFatturaSenzaEstensione = getNomeFileFatturaSenzaEstensione(nomeFileFattura);
		
		if(nomeFileFatturaSenzaEstensione.length()>36){
			return nomeFileFatturaSenzaEstensione.substring(0, 36);
		}else{
			return nomeFileFatturaSenzaEstensione;
		}
	}
	
	public static void validaNomeFileMessaggi(String nomeFile, TipiMessaggi tipoMessaggio) throws ProtocolException{
		validaNomeFileMessaggi(null, nomeFile, tipoMessaggio);
	}
	public static void validaNomeFileMessaggi(String nomeFileFattura, String nomeFile, TipiMessaggi tipoMessaggio) throws ProtocolException{
		
		/**
		 * <NomeFilaFatturaRicevutoSenzaEstensione>_<TipoMessaggio>_<ProgressivoUnivoco>
		 * Il Nome del file fattura ricevuto senza estensione deve essere conforme alle regole definite nel precedente paragrafo. 
		 * Nel caso in cui il nome file non sia conforme e la sua lunghezza sia superiore ai 36 caratteri il nome sarà troncato 
		 * ed i caratteri oltre il 36-esimo non saranno presenti nella notifica di scarto. 
		 * Il Tipo di messaggio può assumere i seguenti valori: 
		 * - RC Ricevuta di consegna 
		 * - NS Notifica di scarto 
		 * - MC Notifica di mancata consegna 
		 * - NE Notifica esito cedente / prestatore 
		 * - MT File dei metadati 
		 * - EC Notifica di esito cessionario / committente 
		 * - SE Notifica di scarto esito cessionario / committente 
		 * - DT Notifica decorrenza termini 
		 * - AT Attestazione di avvenuta trasmissione della fattura con impossibilità di recapito 
		 * 
		 * Il Progressivo univoco deve essere una stringa alfanumerica di lunghezza massima 3 caratteri e con valori ammessi [a-z], [A-Z], [0-9] 
		 * che identifica univocamente ogni notifica / ricevuta relativa al file inviato. 
		 * Il carattere di separazione degli elementi componenti il nome file corrisponde all’underscore (“_”), codice ASCII 95, l’estensione è sempre “.xml”. 
		 * 
		 * Se il SdI ha ricevuto un file di tipo compresso (es.: ITAAABBB99T99X999W_00001.zip ) e non è possibile accedere al suo contenuto perché “corrotto”, 
		 * il nome del file con il quale il SdI inoltra al  soggetto trasmittente la notifica di scarto è il seguente: ITAAABBB99T99X999W_00001_NS_001.xml 
		 * 
		 * NOTA: A queste regole di nomenclatura fa eccezione l’Attestazione di avvenuta trasmissione della fattura con impossibilità di recapito (rif. paragrafo 1.10); 
		 * in questo caso, se il SdI ha ricevuto un file con nome  ITAAABBB99T99X999W_00001.xml, inoltra al soggetto trasmittente il seguente file .zip 
		 * ITAAABBB99T99X999W_00001_AT_001.zip 
		 * che al suo interno contiene il file ricevuto (ITAAABBB99T99X999W_00001.xml) e l’attestazione (ITAAABBB99T99X999W_00001_AT_001.xml). 
		 */
		
		if(nomeFile==null){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+") non fornito");
		}
		if(nomeFile.length()<19){ // 10(fattura)+1(_)+2(tipo)+1(_)+1(progressivo)+4(.xml)
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (length="+nomeFile.length()+"<19)");
		}
		if(nomeFile.contains("_")==false){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto ('_' non presente)");
		}
		if(nomeFile.contains(".")==false){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto ('.' non presente)");
		}
		String [] splitUnderscore = nomeFile.split("_");
		if(splitUnderscore.length!=4){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (split('_')="+splitUnderscore.length+"<>4)");
		}
		String nomeFileFatturaInterno = splitUnderscore[0]+"_"+splitUnderscore[1];
		if(nomeFileFattura!=null){
			
			String nomeFileFatturaSenzaEstensione = getNomeFileFatturaSenzaEstensione(nomeFileFattura);
			
			if(nomeFileFatturaSenzaEstensione.length()>36){
				if(nomeFileFatturaSenzaEstensione.substring(0, 36).equals(nomeFileFatturaInterno)==false){
					throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (nomeFattura.substring(36)=["+nomeFileFatturaSenzaEstensione+"] non riportato correttamente nel nome del file "+tipoMessaggio.name()+")");
				}
			}else{
				if(nomeFileFatturaSenzaEstensione.equals(nomeFileFatturaInterno)==false){
					throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (nomeFattura=["+nomeFileFatturaSenzaEstensione+"] non riportato correttamente nel nome del file "+tipoMessaggio.name()+")");
				}
			}
		}
		else{
			// non posso effettuare validazione poiche' lo stesso nome file di fattura e' errato in origine
		}
		
		if(nomeFile.length()<=nomeFileFatturaInterno.length()){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura non presenti)");
		}
		String nomeFileMessaggio = nomeFile.substring(nomeFileFatturaInterno.length());
		if(nomeFileMessaggio==null){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura non presenti [null])");
		}
		if(nomeFileMessaggio.length()<9){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura ["+nomeFileMessaggio+"], lenght="+nomeFileMessaggio.length()+"<9)");
		}
		if(nomeFileMessaggio.startsWith("_")==false){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura ["+nomeFileMessaggio+"], '_' atteso dopo il nome della fattura)");
		}
		nomeFileMessaggio = nomeFileMessaggio.substring(1);
				
		if(nomeFileMessaggio.contains("_")==false){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura ["+nomeFileMessaggio+"], '_' non presente)");
		}
		if(nomeFileMessaggio.contains(".")==false){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura ["+nomeFileMessaggio+"], '.' non presente)");
		}
		String [] splitUnderscoreMessaggio = nomeFileMessaggio.split("_");
		if(splitUnderscoreMessaggio.length!=2){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura ["+nomeFileMessaggio+"], split('_')="+splitUnderscoreMessaggio.length+"<>2)");
		}
		if(splitUnderscoreMessaggio[1].contains(".")==false){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura ["+nomeFileMessaggio+"], '.' non presente dopo il tipoMessaggio)");
		}
		String [] splitPointMessaggio = nomeFileMessaggio.split("\\.");
		if(splitPointMessaggio.length!=2){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura ["+nomeFileMessaggio+"], split('.')="+splitPointMessaggio.length+"<>2)");
		}
		String [] splitPointMessaggioUnderscore = splitPointMessaggio[0].split("_");
		if(splitPointMessaggioUnderscore.length!=2){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura ["+splitPointMessaggio[0]+"] senza ext, split('_')="+splitPointMessaggioUnderscore.length+"<>2)");
		}
		
		// tipoMessaggio
		String tipoMessaggioPresente = splitUnderscoreMessaggio[0];
		if(tipoMessaggio.name().equals(tipoMessaggioPresente)==false){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (informazioni dopo nomeFattura ["+nomeFileMessaggio+"], tipoMessaggio '"+
					tipoMessaggioPresente+"' differente da quello atteso '"+tipoMessaggio.name()+"')");
		}

		// progressivoUnivoco
		String progressivoUnivoco = splitPointMessaggioUnderscore[1];
		if(progressivoUnivoco.length()<1){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (progressivoUnivoco=["+progressivoUnivoco+"] length="+progressivoUnivoco.length()+"<1)");
		}
		if(progressivoUnivoco.length()>3){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (progressivoUnivoco=["+progressivoUnivoco+"] length="+progressivoUnivoco.length()+">3)");
		}
		try{
			if(!RegularExpressionEngine.isMatch(progressivoUnivoco,"^[A-Za-z0-9]*$")){
				throw new ProtocolException("Sono presenti caratteri non alfanumerici");
			}
		}catch(Exception e){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (progressivoUnivoco=["+progressivoUnivoco+"] "+e.getMessage()+")");
		}
		
		// extension
		String ext = splitPointMessaggio[1];
		if(ext==null){
			throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (extension non presente)");
		}
		if(TipiMessaggi.AT.equals(tipoMessaggio)){
			if(!SDICostanti.SDI_FATTURA_ESTENSIONE_ZIP.equalsIgnoreCase(ext) ){
				throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (extension["+ext+"] non valida, attesa "+
						SDICostanti.SDI_FATTURA_ESTENSIONE_ZIP+")");
			}
		}
		else{
			if(!SDICostanti.SDI_FATTURA_ESTENSIONE_XML.equalsIgnoreCase(ext) ){
				throw new ProtocolException("NomeFile ("+tipoMessaggio.name()+")["+nomeFile+"] con struttura non corretto (extension["+ext+"] non valida, attesa "+
						SDICostanti.SDI_FATTURA_ESTENSIONE_XML+")");
			}
		}
	}
}
