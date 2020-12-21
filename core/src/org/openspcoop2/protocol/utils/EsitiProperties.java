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

package org.openspcoop2.protocol.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.MapReader;
import org.slf4j.Logger;

/**
 * Classe che gestisce il file di properties 'esiti.properties'
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitiProperties {

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static ConcurrentHashMap<String, EsitiProperties> esitiPropertiesMap = null;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'esiti.properties' */
	private EsitiInstanceProperties reader;


	


	/* ********  C O S T R U T T O R E  ******** */

	private boolean erroreProtocollo = false;
	public boolean isErroreProtocollo() {
		return this.erroreProtocollo;
	}
	private boolean envelopeErroreProtocollo = false;
	public boolean isEnvelopeErroreProtocollo() {
		return this.envelopeErroreProtocollo;
	}
	private String labelErroreProtocollo = null;
	private boolean faultEsterno = false;
	public boolean isFaultEsterno() {
		return this.faultEsterno;
	}
	private String labelFaultEsterno = null;
	@SuppressWarnings("unused")
	private String protocollo = null;
	
	protected final static String NO_PROTOCOL_CONFIG = "NO_PROTOCOL_CONFIG"; 
	
	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	private EsitiProperties(String confDir,Logger log, IProtocolFactory<?> pf) throws ProtocolException{

		if(log != null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(EsitiProperties.class);

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = EsitiProperties.class.getResourceAsStream("/org/openspcoop2/protocol/utils/esiti.properties");
			if(properties==null){
				throw new Exception("File '/org/openspcoop2/protocol/utils/esiti.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'org/openspcoop2/protocol/utils/esiti.properties': "+e.getMessage());
			throw new ProtocolException("EsitiProperties initialize error: "+e.getMessage(),e);
		}finally{
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
		}
		try{
			this.reader = new EsitiInstanceProperties(confDir, propertiesReader, this.log);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
		if(pf==null) {
			// Serve per la configurazione
			this.erroreProtocollo = true;
			this.envelopeErroreProtocollo = true;
			this.faultEsterno = true;
		}
		else if(pf.getInformazioniProtocol()!=null) {
			this.erroreProtocollo = pf.getInformazioniProtocol().isErrorProtocol();
			this.envelopeErroreProtocollo = pf.getInformazioniProtocol().isEnvelopeErrorProtocol();
			this.labelErroreProtocollo = pf.getInformazioniProtocol().getLabelErrorProtocol();
			this.faultEsterno = pf.getInformazioniProtocol().isExternalFault();
			this.labelFaultEsterno = pf.getInformazioniProtocol().getLabelExternalFault();
			this.protocollo = pf.getProtocol();
		}

	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static synchronized void initialize(String confDir,Logger log,Loader loader, MapReader<String, IProtocolFactory<?>> protocols) throws ProtocolException{

		if(EsitiProperties.esitiPropertiesMap==null){
			
			EsitiProperties.esitiPropertiesMap = new ConcurrentHashMap<>();
			
			// Aggiungo configurazione speciale usato dal metodo org.openspcoop2.protocol.utils.EsitiConfigUtils
			// e usata anche per validare il file di properties esiti.properties
			EsitiProperties esitiProperties = new EsitiProperties(confDir,log,null);
			esitiProperties.validaConfigurazione(loader);
			EsitiProperties.esitiPropertiesMap.put(NO_PROTOCOL_CONFIG, esitiProperties);
			
			Enumeration<String> enP = protocols.keys();
			while (enP.hasMoreElements()) {
				String protocol = (String) enP.nextElement();
				IProtocolFactory<?> pf = protocols.get(protocol);
				
				EsitiProperties esitiPropertiesByProtocol = new EsitiProperties(confDir,log,pf);
				EsitiProperties.esitiPropertiesMap.put(protocol, esitiPropertiesByProtocol);
			}
						
		}

	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di OpenSPCoopProperties
	 * @throws Exception 
	 * 
	 */
	public static EsitiProperties getInstance(Logger log,String protocol) throws ProtocolException{

		if(EsitiProperties.esitiPropertiesMap==null)
			throw new ProtocolException("EsitiProperties not initialized (use init method in factory)");

		return EsitiProperties.esitiPropertiesMap.get(protocol);
	}




	public void validaConfigurazione(Loader loader) throws ProtocolException  {	
		try{  

			List<Integer> codes = getEsitiCode();
						
			getEsitiCodeOrderLabel();
			
			List<String> labelCheck = new ArrayList<String>();
			List<String> nameCheck = new ArrayList<String>();
			
			for (Integer codeEsito : codes) {
				
				String esitoName = getEsitoName(codeEsito);
				if(nameCheck.contains(esitoName)){
					throw new ProtocolException("Esito name ["+esitoName+"] already defined");
				}else{
					nameCheck.add(esitoName);
				}
				
				getEsitoDescription(codeEsito);
				
				String esitoLabel = getEsitoLabel(codeEsito);
				if(labelCheck.contains(esitoLabel)){
					throw new ProtocolException("Esito label ["+esitoLabel+"] already defined");
				}else{
					labelCheck.add(esitoLabel);
				}
				
				EsitoIdentificationMode mode = getEsitoIdentificationMode(codeEsito);
				if(EsitoIdentificationMode.SOAP_FAULT.equals(mode)){
					getEsitoIdentificationModeSoapFaultList(codeEsito);
				}
				else if(EsitoIdentificationMode.CONTEXT_PROPERTY.equals(mode)){
					getEsitoIdentificationModeContextPropertyList(codeEsito);
				}
			}
				
			List<Integer> codesOk = getEsitiCodeOk();
			getEsitiCodeKo(); // ottenuto come diff tra esiti e esiti ok
			for (Integer codeEsitoOk : codesOk) {
				boolean found = false;
				for (Integer codeEsito : codes) {
					if(codeEsitoOk.intValue() == codeEsito.intValue()){
						found = true;
						break;
					}
				}
				if(!found){
					throw new ProtocolException("Code 'ok' ["+codeEsitoOk+"] not defined in codes");
				}
			}
			
			List<String> tipi = getEsitiTransactionContextCode();
			
			getEsitiTransactionContextCodeOrderLabel();
			
			labelCheck = new ArrayList<String>();
			
			for (String tipo : tipi) {
				String tipoLabel = getEsitoTransactionContextLabel(tipo);
				if(labelCheck.contains(tipoLabel)){
					throw new ProtocolException("Transaction Context Label ["+tipoLabel+"] already defined");
				}else{
					labelCheck.add(tipoLabel);
				}
			}
			
			// Validazione tipi e context built-in che esistano nel file esiti.properties
			EsitoTransazioneName [] codeNames = EsitoTransazioneName.values();
			for (int i = 0; i < codeNames.length; i++) {
				if(!EsitoTransazioneName.CUSTOM.equals(codeNames[i])){
					this.convertoToCode(codeNames[i]);
				}
			}
			if(this.getEsitiTransactionContextCode().contains(CostantiProtocollo.ESITO_TRANSACTION_CONTEXT_STANDARD)==false){
				throw new ProtocolException("Required TipoContext ["+CostantiProtocollo.ESITO_TRANSACTION_CONTEXT_STANDARD+"] undefined");
			}
			
			// Altri init
			
			getEsitiOrderLabel();
			
			getEsitiTransactionContextOrderLabel();
			
			getEsitoTransactionContextDefault();
			
			
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la validazione della proprieta' degli esiti, "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		}
	}

	
	
	/* **** CONVERTER **** */
	public EsitoTransazioneName getEsitoTransazioneName(Integer cod) throws ProtocolException{
		return EsitoTransazioneName.convertoTo(this.getEsitoName(cod));
	}
	public List<EsitoTransazione> getListEsitoTransazioneFromFilter(Integer codeParam, String tipoContextParam) throws ProtocolException{
		List<EsitoTransazione> list = new ArrayList<EsitoTransazione>();
		
		if(codeParam==null){
			// *
			List<Integer> codes = this.getEsitiCode();
			for (Integer codeEsito : codes) {
				
				if(tipoContextParam==null){
					// **
					List<String> tipiContext = this.getEsitiTransactionContextCode();
					for (String tipo : tipiContext) {
						list.add(new EsitoTransazione(this.getEsitoTransazioneName(codeEsito),codeEsito,tipo));
					}
				}
				else{
					list.add(new EsitoTransazione(this.getEsitoTransazioneName(codeEsito),codeEsito,tipoContextParam));
				}
				
			}
		}
		else{
			if(tipoContextParam==null){
				// **
				List<String> tipiContext = this.getEsitiTransactionContextCode();
				for (String tipo : tipiContext) {
					list.add(new EsitoTransazione(this.getEsitoTransazioneName(codeParam),codeParam,tipo));
				}
			}
			else{
				list.add(new EsitoTransazione(this.getEsitoTransazioneName(codeParam),codeParam,tipoContextParam));
			}
		}
		
		return list;
	}
	
	public  EsitoTransazione convertToEsitoTransazione(Integer code, String tipoContext) throws ProtocolException{
		if(code==null){
			throw new ProtocolException("Code ["+code+"] undefined");
		}
		if(existsEsitoCode(code)==false){
			throw new ProtocolException("Code ["+tipoContext+"] unsupported");
		}
		String name = this.getEsitoName(code);
		if(name==null){
			throw new ProtocolException("Name for esito code ["+code+"] undefined");
		}
		EsitoTransazioneName nameEnum = EsitoTransazioneName.convertoTo(name);
		return convertToEsitoTransazione(nameEnum, code, tipoContext);
	}
	public  EsitoTransazione convertToEsitoTransazione(EsitoTransazioneName esito, String tipoContext) throws ProtocolException{
		return convertToEsitoTransazione(esito, this.convertoToCode(esito), tipoContext);
	}
	public  EsitoTransazione convertToEsitoTransazione(EsitoTransazioneName esito,Integer code, String tipoContext) throws ProtocolException{
		
		if(esito==null){
			throw new ProtocolException("EsitoTransazioneName ["+code+"] undefined");
		}
		
		if(code==null){
			throw new ProtocolException("Code ["+code+"] undefined");
		}
		if(existsEsitoCode(code)==false){
			throw new ProtocolException("Code ["+tipoContext+"] unsupported");
		}
		
		if(tipoContext==null){
			throw new ProtocolException("TipoContext ["+code+"] undefined");
		}
		if(this.getEsitiTransactionContextCode().contains(tipoContext)==false){
			throw new ProtocolException("TipoContext ["+tipoContext+"] unsupported");
		}
		
		return new EsitoTransazione(esito, code, tipoContext);
	}
	
	public Integer convertoToCode(EsitoTransazioneName esito) throws ProtocolException{
		Integer code = null;
		List<Integer> codes = getEsitiCode();
		for (Integer codeEsito : codes) {
			String name = this.getEsitoName(codeEsito);
			if(name.equals(esito.name())){
				code = codeEsito;
				break;
			}
		}
		if(code==null){
			throw new ProtocolException("Code ["+code+"] undefined");
		}
		return code;
	}
	
	public Integer convertLabelToCode(String label) throws ProtocolException{
		List<Integer> codes = getEsitiCode();
		for (Integer codeEsito : codes) {
			String labelCheck = this.getEsitoLabel(codeEsito);
			if(labelCheck.equals(label)){
				return codeEsito;
			}
		}
		throw new ProtocolException("Not exists esito code with label ["+label+"]");
	}
	
	public Integer convertNameToCode(String name) throws ProtocolException{
		List<Integer> codes = getEsitiCodeSenzaFiltri();
		for (Integer codeEsito : codes) {
			String nameCheck = this.getEsitoName(codeEsito);
			if(nameCheck.equals(name)){
				return codeEsito;
			}
		}
		throw new ProtocolException("Not exists esito code with name ["+name+"]");
	}
	
	public boolean existsEsitoCode(Integer code) throws ProtocolException{
		if(code==null) {
			return false;
		}
		List<Integer> codes = getEsitiCode();
		for (Integer codeEsito : codes) {
			if(codeEsito.intValue() == code.intValue()){
				return true;
			}
		}
		return false;
	}
	
	public String convertLabelToContextTypeCode(String label) throws ProtocolException{
		List<String> codes = getEsitiTransactionContextCode();
		for (String codeTransactionContext : codes) {
			String labelCheck = this.getEsitoTransactionContextLabel(codeTransactionContext);
			if(labelCheck.equals(label)){
				return codeTransactionContext;
			}
		}
		throw new ProtocolException("Not exists context type with label ["+label+"]");
	}
	

	
	
	/* **** LIBRERIA **** */

	private List<Integer> filterByProtocol(List<Integer> esitiCode) throws ProtocolException {
		List<Integer> esitiCodeNew = new ArrayList<>();
		int codeErroreProtocollo = this.convertNameToCode(EsitoTransazioneName.ERRORE_PROTOCOLLO.name());
		int codeFaultPdd = this.convertNameToCode(EsitoTransazioneName.ERRORE_SERVER.name());
		
		//System.out.println("["+this.protocollo+"] ["+this.erroreProtocollo+"]("+this.labelErroreProtocollo+") ["+this.faultEsterno+"]("+this.labelFaultEsterno+") listaOriginale["+esitiCode+"]");
		
		for (Integer esito : esitiCode) {
			if(esito.intValue() == codeErroreProtocollo) {
				if(this.erroreProtocollo==false) {
					continue;
				}
			}
			if(esito.intValue() == codeFaultPdd) {
				if(this.faultEsterno==false) {
					continue;
				}
			}
			esitiCodeNew.add(esito);
		}
		
		//System.out.println("["+this.protocollo+"] ["+this.erroreProtocollo+"]("+this.labelErroreProtocollo+") ["+this.faultEsterno+"]("+this.labelFaultEsterno+") listaFiltrata["+esitiCodeNew+"]");
		
		return esitiCodeNew;
	}
	
	private String filterByProtocol(String label, int code) throws ProtocolException {
		int codeErroreProtocollo = this.convertNameToCode(EsitoTransazioneName.ERRORE_PROTOCOLLO.name());
		int codeFaultPdd = this.convertNameToCode(EsitoTransazioneName.ERRORE_SERVER.name());
		
		if(code == codeErroreProtocollo) {
			if(this.erroreProtocollo && this.labelErroreProtocollo!=null) {
				return this.labelErroreProtocollo;
			}
		}
		if(code == codeFaultPdd) {
			if(this.faultEsterno && this.labelFaultEsterno!=null) {
				return this.labelFaultEsterno;
			}
		}

		return label;
	}
	
	private List<Integer> esitiCodeSenzaFiltri = null;
	public List<Integer> getEsitiCodeSenzaFiltri() throws ProtocolException {
		if(this.esitiCodeSenzaFiltri == null){
			this.initEsitiCodeSenzaFiltri(); 
		}

		return this.esitiCodeSenzaFiltri;
	}
	private synchronized void initEsitiCodeSenzaFiltri() throws ProtocolException {
		if(this.esitiCode == null){
			this.esitiCodeSenzaFiltri = getListaInteger("esiti.codes");
		}
	}
	
	private List<Integer> esitiCode = null;
	public List<Integer> getEsitiCode() throws ProtocolException {
		if(this.esitiCode == null){
			this.initEsitiCode();
		}

		return this.esitiCode;
	}
	private synchronized void initEsitiCode() throws ProtocolException {
		if(this.esitiCode == null){
			this.esitiCode = filterByProtocol(getListaInteger("esiti.codes"));
		}
	}
	
	private List<Integer> esitiCodeOk = null;
	public List<Integer> getEsitiCodeOk() throws ProtocolException {
		if(this.esitiCodeOk == null){
			this.initEsitiCodeOk();
		}

		return this.esitiCodeOk;
	}
	private synchronized void initEsitiCodeOk() throws ProtocolException {
		if(this.esitiCodeOk == null){
			this.esitiCodeOk = filterByProtocol(getListaInteger("esiti.codes.ok")); 	   
		}
	}
	
	private List<Integer> esitiCodeOk_senzaFaultApplicativo = null;
	public List<Integer> getEsitiCodeOk_senzaFaultApplicativo() throws ProtocolException {
		if(this.esitiCodeOk_senzaFaultApplicativo == null){
				this.initEsitiCodeOk_senzaFaultApplicativo();   
		}

		return this.esitiCodeOk_senzaFaultApplicativo;
	}
	private synchronized void initEsitiCodeOk_senzaFaultApplicativo() throws ProtocolException {
		if(this.esitiCodeOk_senzaFaultApplicativo == null){
			List<Integer> tmp = this.getEsitiCodeOk();
			int codeFaultApplicativo = this.convertNameToCode(EsitoTransazioneName.ERRORE_APPLICATIVO.name());
			List<Integer> esitiOk = new ArrayList<Integer>();
			for (Integer e : tmp) {
				if(e!=codeFaultApplicativo){
					esitiOk.add(e);
				}
			}
			this.esitiCodeOk_senzaFaultApplicativo = filterByProtocol(esitiOk); 	   
		}
	}
	
	private List<Integer> esitiCodeKo = null;
	public List<Integer> getEsitiCodeKo() throws ProtocolException {
		if(this.esitiCodeKo == null){
			this.initEsitiCodeKo();
		}

		return this.esitiCodeKo;
	}
	private synchronized void initEsitiCodeKo() throws ProtocolException {
		if(this.esitiCodeKo == null){
			List<Integer> esitiCodeKo = new ArrayList<Integer>();
			List<Integer> esiti = this.getEsitiCodeOrderLabel();
			for (Integer esito : esiti) {
				boolean found = false;
				List<Integer> oks = this.getEsitiCodeOk();
				for (Integer ok : oks) {
					if(ok.intValue() == esito.intValue()){
						found = true;
						break;
					}
				}
				if(!found){
					esitiCodeKo.add(esito);
				}
			}
			this.esitiCodeKo = filterByProtocol(esitiCodeKo);
		}
	}
	
	private List<Integer> esitiCodeKo_senzaFaultApplicativo = null;
	public List<Integer> getEsitiCodeKo_senzaFaultApplicativo() throws ProtocolException { // serve ad essere sicuri che anche se si è registrato un faultApplicativo tra gli errori, cmq non viene ritornato
		if(this.esitiCodeKo_senzaFaultApplicativo == null){
			this.initEsitiCodeKo_senzaFaultApplicativo();   
		}

		return this.esitiCodeKo_senzaFaultApplicativo;
	}
	private synchronized void initEsitiCodeKo_senzaFaultApplicativo() throws ProtocolException {
		if(this.esitiCodeKo_senzaFaultApplicativo == null){
			List<Integer> tmp = this.getEsitiCodeKo();
			int codeFaultApplicativo = this.convertNameToCode(EsitoTransazioneName.ERRORE_APPLICATIVO.name());
			List<Integer> esitiKo = new ArrayList<Integer>();
			for (Integer e : tmp) {
				if(e!=codeFaultApplicativo){
					esitiKo.add(e);
				}
			}
			this.esitiCodeKo_senzaFaultApplicativo = filterByProtocol(esitiKo); 	   
		}
	}
	
	private List<Integer> esitiCodeFaultApplicativo = null;
	public List<Integer> getEsitiCodeFaultApplicativo() throws ProtocolException {
		if(this.esitiCodeFaultApplicativo == null){
			this.initEsitiCodeFaultApplicativo();
		}

		return this.esitiCodeFaultApplicativo;
	}
	private synchronized void initEsitiCodeFaultApplicativo() throws ProtocolException {
		if(this.esitiCodeFaultApplicativo == null){
			int codeFaultApplicativo = this.convertNameToCode(EsitoTransazioneName.ERRORE_APPLICATIVO.name());
			List<Integer> esitiCodeFaultApplicativo = new ArrayList<Integer>();
			esitiCodeFaultApplicativo.add(codeFaultApplicativo);
			this.esitiCodeFaultApplicativo = esitiCodeFaultApplicativo;
		}
	}
	
	private List<Integer> esitiCodeRichiestaScartate = null;
	public List<Integer> getEsitiCodeRichiestaScartate() throws ProtocolException {
		if(this.esitiCodeRichiestaScartate == null){
			this.initEsitiCodeRichiestaScartate();
		}

		return this.esitiCodeRichiestaScartate;
	}
	private synchronized void initEsitiCodeRichiestaScartate() throws ProtocolException {
		if(this.esitiCodeRichiestaScartate == null){
			this.esitiCodeRichiestaScartate = filterByProtocol(getListaInteger("esiti.codes.richiestaScartate")); 	   
		}
	}
	
	private List<Integer> esitiCodeErroriConsegna = null;
	public List<Integer> getEsitiCodeErroriConsegna() throws ProtocolException {
		if(this.esitiCodeErroriConsegna == null){
			this.initEsitiCodeErroriConsegna();
		}

		return this.esitiCodeErroriConsegna;
	}
	private synchronized void initEsitiCodeErroriConsegna() throws ProtocolException {
		if(this.esitiCodeErroriConsegna == null){
			this.esitiCodeErroriConsegna = filterByProtocol(getListaInteger("esiti.codes.erroriConsegna")); 	   
		}
	}
	
	private List<Integer> esitiCodeForSoapFaultIdentificationMode = null;
	public List<Integer> getEsitiCodeForSoapFaultIdentificationMode() throws ProtocolException {
		if(this.esitiCodeForSoapFaultIdentificationMode == null){
			this.initEsitiCodeForSoapFaultIdentificationMode();
		}

		return this.esitiCodeForSoapFaultIdentificationMode;
	}
	private synchronized void initEsitiCodeForSoapFaultIdentificationMode() throws ProtocolException {
		if(this.esitiCodeForSoapFaultIdentificationMode == null){
			List<Integer> esitiCodeForSoapFaultIdentificationMode = new ArrayList<Integer>();
			List<Integer> codes = getEsitiCode();  
			for (Integer codeEsito : codes) {
				if(EsitoIdentificationMode.SOAP_FAULT.equals(this.getEsitoIdentificationMode(codeEsito))){
					esitiCodeForSoapFaultIdentificationMode.add(codeEsito);
				}
			}
			this.esitiCodeForSoapFaultIdentificationMode = filterByProtocol(esitiCodeForSoapFaultIdentificationMode);
		}
	}
	
	private List<Integer> esitiCodeForContextPropertyIdentificationMode = null;
	public List<Integer> getEsitiCodeForContextPropertyIdentificationMode() throws ProtocolException {
		if(this.esitiCodeForContextPropertyIdentificationMode == null){
			this.initEsitiCodeForContextPropertyIdentificationMode();
		}

		return this.esitiCodeForContextPropertyIdentificationMode;
	}
	private synchronized void initEsitiCodeForContextPropertyIdentificationMode() throws ProtocolException {
		if(this.esitiCodeForContextPropertyIdentificationMode == null){
			List<Integer> esitiCodeForContextPropertyIdentificationMode = new ArrayList<Integer>();
			List<Integer> codes = getEsitiCode();  
			for (Integer codeEsito : codes) {
				if(EsitoIdentificationMode.CONTEXT_PROPERTY.equals(this.getEsitoIdentificationMode(codeEsito))){
					esitiCodeForContextPropertyIdentificationMode.add(codeEsito);
				}
			}
			this.esitiCodeForContextPropertyIdentificationMode = filterByProtocol(esitiCodeForContextPropertyIdentificationMode);
		}
	}

	private List<Integer> esitiCodeOrderLabel = null;
	public List<Integer> getEsitiCodeOrderLabel() throws ProtocolException {
		if(this.esitiCodeOrderLabel == null){
			this.initEsitiCodeOrderLabel();
		}

		return this.esitiCodeOrderLabel;
	}
	private synchronized void initEsitiCodeOrderLabel() throws ProtocolException {
		if(this.esitiCodeOrderLabel == null){
			this.esitiCodeOrderLabel = filterByProtocol(getListaInteger("esiti.codes.labelOrder")); 	   
		}
	}
	
	private List<String> esitiOrderLabel = null;
	public List<String> getEsitiOrderLabel() throws ProtocolException {
		if(this.esitiOrderLabel == null){
			this.initEsitiOrderLabel();
		}

		return this.esitiOrderLabel;
	}
	private synchronized void initEsitiOrderLabel() throws ProtocolException {
		if(this.esitiOrderLabel == null){
			List<Integer> codes = getEsitiCode();
			List<String> esitiOrderLabel = new ArrayList<String>();
			for (Integer codeEsito : codes) {
				esitiOrderLabel.add(this.getEsitoLabel(codeEsito));
			}
			this.esitiOrderLabel = esitiOrderLabel;
		}
	}
	
	private ConcurrentHashMap<String,String> esitoName= null;
	public String getEsitoName(Integer codeEsito) throws ProtocolException {
		if(this.esitoName == null){
			this.initEsitoName();
		}
		if(this.esitoName.containsKey(codeEsito+"")==false){
			throw new ProtocolException("EsitoName for code ["+codeEsito+"] not found");
		}
		return this.esitoName.get(codeEsito+"");
	}
	private synchronized void initEsitoName() throws ProtocolException {
		if(this.esitoName == null){
			ConcurrentHashMap<String,String> esitoName = new ConcurrentHashMap<String, String>();
			List<Integer> codes = getEsitiCodeSenzaFiltri();
			for (Integer code : codes) {
				esitoName.put(code+"", getProperty("esito."+code+".name"));
			}
			this.esitoName = esitoName;
		}
	}
	
	private ConcurrentHashMap<String,String> esitoDescription= null;
	public String getEsitoDescription(Integer codeEsito) throws ProtocolException {
		if(this.esitoDescription == null){
			this.initEsitoDescription();
		}
		if(this.esitoDescription.containsKey(codeEsito+"")==false){
			throw new ProtocolException("EsitoDescription for code ["+codeEsito+"] not found");
		}
		return this.esitoDescription.get(codeEsito+"");
	}
	private synchronized void initEsitoDescription() throws ProtocolException {
		if(this.esitoDescription == null){
			ConcurrentHashMap<String, String> esitoDescription = new ConcurrentHashMap<String, String>();
			List<Integer> codes = getEsitiCode();
			for (Integer code : codes) {
				esitoDescription.put(code+"", getProperty("esito."+code+".description"));
			} 
			this.esitoDescription = esitoDescription;
		}
	}
	
	private ConcurrentHashMap<String,String> esitoLabel= null;
	public String getEsitoLabel(Integer codeEsito) throws ProtocolException {
		if(this.esitoLabel == null){
			this.initEsitoLabel(); 
		}
		if(this.esitoLabel.containsKey(codeEsito+"")==false){
			throw new ProtocolException("EsitoLabel for code ["+codeEsito+"] not found");
		}
		return this.esitoLabel.get(codeEsito+"");
	}
	private synchronized void initEsitoLabel() throws ProtocolException {
		if(this.esitoLabel == null){
			ConcurrentHashMap<String, String> esitoLabel = new ConcurrentHashMap<String, String>();
			List<Integer> codes = getEsitiCode();
			for (Integer code : codes) {
				String label = getProperty("esito."+code+".label");
				label = filterByProtocol(label, code);
				esitoLabel.put(code+"", label);
			}  
			this.esitoLabel = esitoLabel;
		}
	}
	
	private ConcurrentHashMap<String,String> esitoLabelSyntetic= null;
	public String getEsitoLabelSyntetic(Integer codeEsito) throws ProtocolException {
		if(this.esitoLabelSyntetic == null){
			this.initEsitoLabelSyntetic(); 
		}
		if(this.esitoLabelSyntetic.containsKey(codeEsito+"")==false){
			throw new ProtocolException("EsitoLabelSyntetic for code ["+codeEsito+"] not found");
		}
		return this.esitoLabelSyntetic.get(codeEsito+"");
	}
	private synchronized void initEsitoLabelSyntetic() throws ProtocolException {
		if(this.esitoLabelSyntetic == null){
			ConcurrentHashMap<String, String> esitoLabelSyntetic = new ConcurrentHashMap<String, String>();
			List<Integer> codes = getEsitiCode();
			for (Integer code : codes) {
				String label = getProperty("esito."+code+".label.syntetic");
				label = filterByProtocol(label, code);
				esitoLabelSyntetic.put(code+"", label);
			}
			this.esitoLabelSyntetic = esitoLabelSyntetic;
		}
	}
	
	private ConcurrentHashMap<String,EsitoIdentificationMode> esitoIdentificationMode= null;
	public EsitoIdentificationMode getEsitoIdentificationMode(Integer codeEsito) throws ProtocolException {
		if(this.esitoIdentificationMode == null){
			this.initEsitoIdentificationMode();  
		}
		if(this.esitoIdentificationMode.containsKey(codeEsito+"")==false){
			throw new ProtocolException("EsitoIdentificationMode for code ["+codeEsito+"] not found");
		}
		return this.esitoIdentificationMode.get(codeEsito+"");
	}
	private synchronized void initEsitoIdentificationMode() throws ProtocolException {
		if(this.esitoIdentificationMode == null){
			ConcurrentHashMap<String, EsitoIdentificationMode> esitoIdentificationMode = new ConcurrentHashMap<String, EsitoIdentificationMode>();
			List<Integer> codes = getEsitiCode();
			for (Integer code : codes) {
				String prop = "esito."+code+".mode";
				String tmp = getProperty(prop);
				try{ 
					EsitoIdentificationMode e = EsitoIdentificationMode.toEnumConstant(tmp);
					if(e==null){
						throw new Exception("proprieta con valore non supportato");
					}
					esitoIdentificationMode.put(code+"", e);
				}catch(java.lang.Exception e) {
					String msg = "Riscontrato errore durante la lettura della proprieta' '"+prop+"': "+e.getMessage();
					this.log.error(msg,e);
					throw new ProtocolException(msg,e);
				} 	 
			} 
			this.esitoIdentificationMode = esitoIdentificationMode;
		}
	}
	
	private ConcurrentHashMap<String,List<EsitoIdentificationModeSoapFault>> esitoIdentificationModeSoapFaultList= null;
	public List<EsitoIdentificationModeSoapFault> getEsitoIdentificationModeSoapFaultList(Integer codeEsito) throws ProtocolException {
		if(this.esitoIdentificationModeSoapFaultList==null){
			this.initEsitoIdentificationModeSoapFaultList();
		}
		return this.esitoIdentificationModeSoapFaultList.get(codeEsito+"");
	}
	private synchronized void initEsitoIdentificationModeSoapFaultList() throws ProtocolException {
		if(this.esitoIdentificationModeSoapFaultList==null){
			ConcurrentHashMap<String, List<EsitoIdentificationModeSoapFault>> esitoIdentificationModeSoapFaultList = new ConcurrentHashMap<String, List<EsitoIdentificationModeSoapFault>>();
			List<Integer> codes = getEsitiCode();
			for (Integer code : codes) {
				try{
					EsitoIdentificationMode mode = this.getEsitoIdentificationMode(code);
					if(EsitoIdentificationMode.SOAP_FAULT.equals(mode)){
						esitoIdentificationModeSoapFaultList.put(code+"", this._getEsitoIdentificationModeSoapFaultList(code));
					}
				}catch(Exception e){
					throw new ProtocolException("Errore durante la gestione del codice ["+code+"]: "+e.getMessage(),e);
				}
			}
			this.esitoIdentificationModeSoapFaultList= esitoIdentificationModeSoapFaultList;
		}
	}
	private List<EsitoIdentificationModeSoapFault> _getEsitoIdentificationModeSoapFaultList(Integer codeEsito) throws ProtocolException {
			
		List<EsitoIdentificationModeSoapFault>  l = null;
		
		String prefix = "esito."+codeEsito+".mode.soapFault.";
		
		int index = 0;
		while(index<1000){
			
			EsitoIdentificationModeSoapFault esito = new EsitoIdentificationModeSoapFault();
			
			esito.setFaultCode(this.getOptionalProperty(prefix+index+".code"));
			esito.setFaultNamespaceCode(this.getOptionalProperty(prefix+index+".namespaceCode"));
			
			esito.setFaultReason(this.getOptionalProperty(prefix+index+".reason"));
			esito.setFaultReasonContains(this.getOptionalBooleanProperty(prefix+index+".reason.contains"));
			
			esito.setFaultActor(this.getOptionalProperty(prefix+index+".actor"));
			esito.setFaultActorNotDefined(this.getOptionalBooleanProperty(prefix+index+".actorNotDefined"));
			
			// check consistenza
			if(esito.getFaultReasonContains()!=null && esito.getFaultReason()==null){
				throw new ProtocolException("Per il codice ["+codeEsito+"] esiste un mapping rispetto alla modalità soapFault in cui è stato definita la proprietà '*.reason.contains' senza la proprietà '*.reason'");
			}
			if(esito.getFaultActor()!=null && esito.getFaultActorNotDefined()!=null){
				throw new ProtocolException("Per il codice ["+codeEsito+"] esiste un mapping rispetto alla modalità soapFault in cui sono state definite entrambe le modalità di indenficazione dell'actor");
			}
			
			if(esito.getFaultActor()==null && esito.getFaultActorNotDefined()==null && 
					esito.getFaultCode()==null && esito.getFaultNamespaceCode()==null && 
					esito.getFaultReason()==null){
				if(index==0){
					// almeno una opzione è obbligatoria in modalità soapFault
					throw new ProtocolException("Per il codice ["+codeEsito+"] non esiste alcun mapping rispetto alla modalità soapFault");
				}
				break;
			}
		
			if(l==null){
				l = new ArrayList<EsitoIdentificationModeSoapFault>();
			}
			l.add(esito);
			
			index++;
		}
			
		return l;
	}
	
	
	
	
	private ConcurrentHashMap<String,List<EsitoIdentificationModeContextProperty>> esitoIdentificationModeContextPropertyList= null;
	public List<EsitoIdentificationModeContextProperty> getEsitoIdentificationModeContextPropertyList(Integer codeEsito) throws ProtocolException {
		if(this.esitoIdentificationModeContextPropertyList==null){
			this.initEsitoIdentificationModeContextPropertyList();
		}
		return this.esitoIdentificationModeContextPropertyList.get(codeEsito+"");
	}
	private synchronized void initEsitoIdentificationModeContextPropertyList() throws ProtocolException {
		if(this.esitoIdentificationModeContextPropertyList==null){
			ConcurrentHashMap<String, List<EsitoIdentificationModeContextProperty>> esitoIdentificationModeContextPropertyList = new ConcurrentHashMap<String, List<EsitoIdentificationModeContextProperty>>();
			List<Integer> codes = getEsitiCode();
			for (Integer code : codes) {
				try{
					EsitoIdentificationMode mode = this.getEsitoIdentificationMode(code);
					if(EsitoIdentificationMode.CONTEXT_PROPERTY.equals(mode)){
						esitoIdentificationModeContextPropertyList.put(code+"", this._getEsitoIdentificationModeContextPropertyList(code));
					}
				}catch(Exception e){
					throw new ProtocolException("Errore durante la gestione del codice ["+code+"]: "+e.getMessage(),e);
				}
			}
			this.esitoIdentificationModeContextPropertyList=esitoIdentificationModeContextPropertyList;
		}
	}
	private List<EsitoIdentificationModeContextProperty> _getEsitoIdentificationModeContextPropertyList(Integer codeEsito) throws ProtocolException {
			
		List<EsitoIdentificationModeContextProperty>  l = null;
		
		String prefix = "esito."+codeEsito+".mode.contextProperty.";
		
		int index = 0;
		while(index<1000){
			
			EsitoIdentificationModeContextProperty esito = new EsitoIdentificationModeContextProperty();
			
			esito.setName(this.getOptionalProperty(prefix+index+".name"));
			
			esito.setValue(this.getOptionalProperty(prefix+index+".value"));

			if(esito.getName()==null && esito.getValue()==null){
				if(index==0){
					// almeno una opzione è obbligatoria in modalità soapFault
					throw new ProtocolException("Per il codice ["+codeEsito+"] non esiste alcun mapping rispetto alla modalità contextProperty");
				}
				break;
			}
			
			if(esito.getName()==null){
				throw new ProtocolException("Per il codice ["+codeEsito+"] non esiste il mapping rispetto al nome, obbligtorio per la modalità contextProperty");
			}
			
			if(l==null){
				l = new ArrayList<EsitoIdentificationModeContextProperty>();
			}
			l.add(esito);
			
			index++;
		}
			
		return l;
	}
	
	
	
	private List<String> esitiTransactionContextCode = null;
	public List<String> getEsitiTransactionContextCode() throws ProtocolException {
		if(this.esitiTransactionContextCode == null){
			this.initEsitiTransactionContextCode();
		}

		return this.esitiTransactionContextCode;
	}
	private synchronized void initEsitiTransactionContextCode() throws ProtocolException {
		if(this.esitiTransactionContextCode == null){
			this.esitiTransactionContextCode = getLista("esiti.transactionContext");
			for (String context : this.esitiTransactionContextCode) {
				if(context.length()>255){
					throw new ProtocolException("Context id ["+context+"] length must be less then 255 characters");
				}
			}
		}
	}
	
	private List<String> esitiTransactionContextCodeOrderLabel = null;
	public List<String> getEsitiTransactionContextCodeOrderLabel() throws ProtocolException {
		if(this.esitiTransactionContextCodeOrderLabel == null){
			this.initEsitiTransactionContextCodeOrderLabel();
		}

		return this.esitiTransactionContextCodeOrderLabel;
	}
	private synchronized void initEsitiTransactionContextCodeOrderLabel() throws ProtocolException {
		if(this.esitiTransactionContextCodeOrderLabel == null){
			this.esitiTransactionContextCodeOrderLabel = getLista("esiti.transactionContext.labelOrder"); 	   
		}
	}
	
	private List<String> esitiTransactionContextOrderLabel = null;
	public List<String> getEsitiTransactionContextOrderLabel() throws ProtocolException {
		if(this.esitiTransactionContextOrderLabel == null){
			this.initEsitiTransactionContextOrderLabel();
		}

		return this.esitiTransactionContextOrderLabel;
	}
	private synchronized void initEsitiTransactionContextOrderLabel() throws ProtocolException {
		if(this.esitiTransactionContextOrderLabel == null){
			List<String> codes = getEsitiTransactionContextCode();
			List<String> esitiTransactionContextOrderLabel = new ArrayList<String>();
			for (String codeTransactionContext : codes) {
				esitiTransactionContextOrderLabel.add(this.getEsitoTransactionContextLabel(codeTransactionContext));
			}
			this.esitiTransactionContextOrderLabel = esitiTransactionContextOrderLabel;
		}
	}

	private ConcurrentHashMap<String,String> esitoTransactionContextLabel= null;
	public String getEsitoTransactionContextLabel(String tipo) throws ProtocolException {
		if(this.esitoTransactionContextLabel == null){
			this.initEsitoTransactionContextLabel();
		}
		if(this.esitoTransactionContextLabel.containsKey(tipo)==false){
			throw new ProtocolException("EsitoTransactionContextLabel for code ["+tipo+"] not found");
		}
		return this.esitoTransactionContextLabel.get(tipo);
	}
	private synchronized void initEsitoTransactionContextLabel() throws ProtocolException {
		if(this.esitoTransactionContextLabel == null){
			ConcurrentHashMap<String, String> esitoTransactionContextLabel = new ConcurrentHashMap<String, String>();
			List<String> codes = getEsitiTransactionContextCode();
			for (String code : codes) {
				esitoTransactionContextLabel.put(code, getProperty("esiti.transactionContext."+code+".label"));
			} 
			this.esitoTransactionContextLabel = esitoTransactionContextLabel;
		}
	}
	
	private String esitoTransactionContextDefault= null;
	private Boolean esitoTransactionContextDefault_read= null;
	public String getEsitoTransactionContextDefault() throws ProtocolException {
		if(this.esitoTransactionContextDefault_read == null){
			this.initEsitoTransactionContextDefault();
		}
		return this.esitoTransactionContextDefault;
	}
	private synchronized void initEsitoTransactionContextDefault() throws ProtocolException {
		if(this.esitoTransactionContextDefault_read == null){
			this.esitoTransactionContextDefault = getOptionalProperty("esiti.transactionContext.default");
			this.esitoTransactionContextDefault_read = true;
			if(this.esitoTransactionContextDefault!=null){
				if(this.getEsitiTransactionContextCode().contains(this.esitoTransactionContextDefault)==false){
					throw new ProtocolException("Indicato nella proprietà 'esiti.transactionContext.default' un codice di contesto non registrato");
				}
			}
		}
	}
	
	private String esitoTransactionContextHeaderTrasportoName= null;
	public String getEsitoTransactionContextHeaderTrasportoName() throws ProtocolException {
		if(this.esitoTransactionContextHeaderTrasportoName == null){
			this.initEsitoTransactionContextHeaderTrasportoName();
		}
		return this.esitoTransactionContextHeaderTrasportoName;
	}
	private synchronized void initEsitoTransactionContextHeaderTrasportoName() throws ProtocolException {
		if(this.esitoTransactionContextHeaderTrasportoName == null){
			this.esitoTransactionContextHeaderTrasportoName = getProperty("esiti.transactionContext.trasporto.headerName");	   
		}
	}
	
	private String esitoTransactionContextFormBasedPropertyName= null;
	public String getEsitoTransactionContextFormBasedPropertyName() throws ProtocolException {
		if(this.esitoTransactionContextFormBasedPropertyName == null){
			this.initEsitoTransactionContextFormBasedPropertyName();
		}
		return this.esitoTransactionContextFormBasedPropertyName;
	}
	private synchronized void initEsitoTransactionContextFormBasedPropertyName() throws ProtocolException {
		if(this.esitoTransactionContextFormBasedPropertyName == null){
			this.esitoTransactionContextFormBasedPropertyName = getProperty("esiti.transactionContext.urlFormBased.propertyName");	   
		}
	}
	
	private List<EsitoTransportContextIdentification> esitoTransactionContextHeaderTrasportoDynamicIdentification = null;
	public  List<EsitoTransportContextIdentification> getEsitoTransactionContextHeaderTrasportoDynamicIdentification() throws ProtocolException {
		if(this.esitoTransactionContextHeaderTrasportoDynamicIdentification == null){
			this.initEsitoTransactionContextHeaderTrasportoDynamicIdentification();
		}
		return this.esitoTransactionContextHeaderTrasportoDynamicIdentification;
	}
	private synchronized void initEsitoTransactionContextHeaderTrasportoDynamicIdentification() throws ProtocolException {
		if(this.esitoTransactionContextHeaderTrasportoDynamicIdentification == null){
			this.esitoTransactionContextHeaderTrasportoDynamicIdentification = this.readEsitoTransportContextIdentification("esiti.transactionContext.trasporto.dynamic.");			
		}
	}
	
	private List<EsitoTransportContextIdentification> esitoTransactionContextHeaderFormBasedDynamicIdentification = null;
	public  List<EsitoTransportContextIdentification> getEsitoTransactionContextHeaderFormBasedDynamicIdentification() throws ProtocolException {
		if(this.esitoTransactionContextHeaderFormBasedDynamicIdentification == null){
			this.initEsitoTransactionContextHeaderFormBasedDynamicIdentification();
		}
		return this.esitoTransactionContextHeaderFormBasedDynamicIdentification;
	}
	private synchronized void initEsitoTransactionContextHeaderFormBasedDynamicIdentification() throws ProtocolException {
		if(this.esitoTransactionContextHeaderFormBasedDynamicIdentification == null){
			this.esitoTransactionContextHeaderFormBasedDynamicIdentification = this.readEsitoTransportContextIdentification("esiti.transactionContext.urlFormBased.dynamic.");			
		}
	}
	
	private List<EsitoTransportContextIdentification> readEsitoTransportContextIdentification(String pName) throws ProtocolException{
		try{
			List<EsitoTransportContextIdentification> l = new ArrayList<EsitoTransportContextIdentification>();
			Properties p = this.reader.readProperties_convertEnvProperties(pName);
			if(p.size()>0){
				List<String> keys = new ArrayList<>();
				Enumeration<?> enKeys = p.keys();
				while (enKeys.hasMoreElements()) {
					String key = (String) enKeys.nextElement();
					if(key.endsWith(".headerName")){
						keys.add(key.substring(0, (key.length()-".headerName".length())));
					}
				}
				for (String key : keys) {
					EsitoTransportContextIdentification etci = new EsitoTransportContextIdentification();
					etci.setName(p.getProperty(key+".headerName"));
					etci.setValue(p.getProperty(key+".value"));
					String mode = p.getProperty(key+".mode");
					etci.setMode(EsitoTransportContextIdentificationMode.toEnumConstant(mode));
					if(etci.getMode()==null){
						throw new ProtocolException("Modalità indicata ["+mode+"] per chiave ["+key+"] sconosciuta");
					}
					etci.setRegularExpr(p.getProperty(key+".regularExpr"));
					if(EsitoTransportContextIdentificationMode.MATCH.equals(etci.getMode())){
						if(etci.getRegularExpr()==null){
							throw new ProtocolException("Modalità indicata ["+mode+"] per la chiave ["+key+"] richiede obbligatoriamente la definizione di una espressione regolare");
						}
					}
					else if(EsitoTransportContextIdentificationMode.EQUALS.equals(etci.getMode()) || EsitoTransportContextIdentificationMode.CONTAINS.equals(etci.getMode())){
						if(etci.getValue()==null){
							throw new ProtocolException("Modalità indicata ["+mode+"] per la chiave ["+key+"] richiede obbligatoriamente la definizione di un valore su cui basare il confronto");
						}
					}
					etci.setType(p.getProperty(key+".type"));
					if(getEsitiTransactionContextCode().contains(etci.getType())==false){
						throw new ProtocolException("Tipo di contesto indicato ["+etci.getType()+"] per la chiave ["+key+"] non risulta tra le tipologie di contesto supportate: "+getEsitiTransactionContextCode());
					}
					l.add(etci);
				}
			}
			return l;
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	
	
	/* ******* UTILITIES ********* */
	
	public String getProperty(String property) throws ProtocolException {
		try{ 
			String name = null;
			name = this.reader.getValue_convertEnvProperties(property);
			if(name==null)
				throw new Exception("proprieta non definita");
			return name.trim();
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la lettura della proprieta' '"+property+"': "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		} 	   
	}
	
	public String getOptionalProperty(String property) throws ProtocolException {
		try{ 
			String name = null;
			name = this.reader.getValue_convertEnvProperties(property);
			if(name==null)
				return null;
			return name.trim();
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la lettura della proprieta' '"+property+"': "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		} 	   
	}
	
	public Boolean getOptionalBooleanProperty(String property) throws ProtocolException {
		String p = this.getOptionalProperty(property);
		if(p!=null){
			try{
				return Boolean.parseBoolean(p);
			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+property+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			} 	
		}
		return null;
	}
	
	private List<String> getLista(String property) throws ProtocolException {
		List<String> lista = null;
		String name = null;
		try{ 
			name = this.reader.getValue_convertEnvProperties(property);
			if(name==null)
				throw new Exception("proprieta non definita");
			else
				name = name.trim();
			lista  = new ArrayList<String>();
			String [] split = name.split(",");
			if(split==null || split.length<=0){
				throw new Exception("proprieta non definita (dopo split?)");
			}
			for (int i = 0; i < split.length; i++) {
				String p = split[i];
				if(p==null){
					throw new Exception("valore alla posizione "+(i+1)+" della proprieta non definita");
				}
				p = p .trim();
				if(p.equals("")){
					throw new Exception("valore alla posizione "+(i+1)+" della proprieta è vuoto");
				}
				if(lista.contains(p)){
					throw new Exception("valore '"+p+"' alla posizione "+(i+1)+" della proprieta è definito più di una volta");
				}
				lista.add(p);
			}
		}catch(java.lang.Exception e) {
			String listaDebug = "";
			if(StringUtils.isNotEmpty(name)) {
				listaDebug = " (lista: "+name+")";
			}
			String msg = "Riscontrato errore durante la lettura della proprieta' '"+property+"'"+listaDebug+": "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		} 	   
		
		return lista;
	}
	
	private List<Integer> getListaInteger(String property) throws ProtocolException {
		List<String> lista = this.getLista(property);
		List<Integer> listaInteger = null;
		if(lista!=null && lista.size()>0){
			listaInteger = new ArrayList<Integer>();
			for (String s : lista) {
				try{
					listaInteger.add(Integer.parseInt(s));
				}catch(Exception e){
					throw new ProtocolException("Property ["+property+"] with wrong value (not integer) ["+s+"]");
				}	
			}
		}
		return listaInteger;
	}
}
