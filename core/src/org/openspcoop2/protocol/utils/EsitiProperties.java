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

package org.openspcoop2.protocol.utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;

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
	private static EsitiProperties esitiProperties = null;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'esiti.properties' */
	private EsitiInstanceProperties reader;


	


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public EsitiProperties(String confDir,Logger log) throws ProtocolException{

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

	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static synchronized void initialize(String confDir,Logger log,Loader loader) throws ProtocolException{

		if(EsitiProperties.esitiProperties==null){
			EsitiProperties.esitiProperties = new EsitiProperties(confDir,log);
			EsitiProperties.esitiProperties.validaConfigurazione(loader);
		}

	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di OpenSPCoopProperties
	 * @throws Exception 
	 * 
	 */
	public static EsitiProperties getInstance(Logger log) throws ProtocolException{

		if(EsitiProperties.esitiProperties==null)
			throw new ProtocolException("EsitiProperties not initialized (use init method in factory)");

		return EsitiProperties.esitiProperties;
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
					if(codeEsitoOk == codeEsito){
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
	private EsitoTransazioneName getEsitoTransazioneName(Integer cod) throws ProtocolException{
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
		List<Integer> codes = getEsitiCode();
		for (Integer codeEsito : codes) {
			String nameCheck = this.getEsitoName(codeEsito);
			if(nameCheck.equals(name)){
				return codeEsito;
			}
		}
		throw new ProtocolException("Not exists esito code with name ["+name+"]");
	}
	
	public boolean existsEsitoCode(Integer code) throws ProtocolException{
		List<Integer> codes = getEsitiCode();
		for (Integer codeEsito : codes) {
			if(codeEsito == code){
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

	private static List<Integer> esitiCode = null;
	public List<Integer> getEsitiCode() throws ProtocolException {
		if(EsitiProperties.esitiCode == null){
			EsitiProperties.esitiCode = getListaInteger("esiti.codes"); 	   
		}

		return EsitiProperties.esitiCode;
	}
	
	private static List<Integer> esitiCodeOk = null;
	public List<Integer> getEsitiCodeOk() throws ProtocolException {
		if(EsitiProperties.esitiCodeOk == null){
			EsitiProperties.esitiCodeOk = getListaInteger("esiti.codes.ok"); 	   
		}

		return EsitiProperties.esitiCodeOk;
	}
	
	private static List<Integer> esitiCodeKo = null;
	public List<Integer> getEsitiCodeKo() throws ProtocolException {
		if(EsitiProperties.esitiCodeKo == null){
			EsitiProperties.esitiCodeKo = new ArrayList<Integer>();
			List<Integer> esiti = this.getEsitiCode();
			for (Integer esito : esiti) {
				boolean found = false;
				List<Integer> oks = this.getEsitiCodeOk();
				for (Integer ok : oks) {
					if(ok == esito){
						found = true;
						break;
					}
				}
				if(!found){
					EsitiProperties.esitiCodeKo.add(esito);
				}
			}
		}

		return EsitiProperties.esitiCodeKo;
	}
	
	private static List<Integer> esitiCodeForSoapFaultIdentificationMode = null;
	public List<Integer> getEsitiCodeForSoapFaultIdentificationMode() throws ProtocolException {
		if(EsitiProperties.esitiCodeForSoapFaultIdentificationMode == null){
			EsitiProperties.esitiCodeForSoapFaultIdentificationMode = new ArrayList<Integer>();
			List<Integer> codes = getEsitiCode();  
			for (Integer codeEsito : codes) {
				if(EsitoIdentificationMode.SOAP_FAULT.equals(this.getEsitoIdentificationMode(codeEsito))){
					EsitiProperties.esitiCodeForSoapFaultIdentificationMode.add(codeEsito);
				}
			}
		}

		return EsitiProperties.esitiCodeForSoapFaultIdentificationMode;
	}
	
	private static List<Integer> esitiCodeForContextPropertyIdentificationMode = null;
	public List<Integer> getEsitiCodeForContextPropertyIdentificationMode() throws ProtocolException {
		if(EsitiProperties.esitiCodeForContextPropertyIdentificationMode == null){
			EsitiProperties.esitiCodeForContextPropertyIdentificationMode = new ArrayList<Integer>();
			List<Integer> codes = getEsitiCode();  
			for (Integer codeEsito : codes) {
				if(EsitoIdentificationMode.CONTEXT_PROPERTY.equals(this.getEsitoIdentificationMode(codeEsito))){
					EsitiProperties.esitiCodeForContextPropertyIdentificationMode.add(codeEsito);
				}
			}
		}

		return EsitiProperties.esitiCodeForContextPropertyIdentificationMode;
	}

	private static List<Integer> esitiCodeOrderLabel = null;
	public List<Integer> getEsitiCodeOrderLabel() throws ProtocolException {
		if(EsitiProperties.esitiCodeOrderLabel == null){
			EsitiProperties.esitiCodeOrderLabel = getListaInteger("esiti.codes.labelOrder"); 	   
		}

		return EsitiProperties.esitiCodeOrderLabel;
	}
	
	private static List<String> esitiOrderLabel = null;
	public List<String> getEsitiOrderLabel() throws ProtocolException {
		if(EsitiProperties.esitiOrderLabel == null){
			List<Integer> codes = getEsitiCode();
			EsitiProperties.esitiOrderLabel = new ArrayList<String>();
			for (Integer codeEsito : codes) {
				EsitiProperties.esitiOrderLabel.add(this.getEsitoLabel(codeEsito));
			}
		}

		return EsitiProperties.esitiOrderLabel;
	}
	
	private static Hashtable<String,String> esitoName= null;
	public String getEsitoName(Integer codeEsito) throws ProtocolException {
		if(EsitiProperties.esitoName == null){
			esitoName = new Hashtable<String, String>();
			List<Integer> codes = getEsitiCode();
			for (Integer code : codes) {
				esitoName.put(code+"", getProperty("esito."+code+".name"));
			}
		}
		if(EsitiProperties.esitoName.containsKey(codeEsito+"")==false){
			throw new ProtocolException("EsitoName for code ["+codeEsito+"] not found");
		}
		return EsitiProperties.esitoName.get(codeEsito+"");
	}
	
	private static Hashtable<String,String> esitoDescription= null;
	public String getEsitoDescription(Integer codeEsito) throws ProtocolException {
		if(EsitiProperties.esitoDescription == null){
			esitoDescription = new Hashtable<String, String>();
			List<Integer> codes = getEsitiCode();
			for (Integer code : codes) {
				esitoDescription.put(code+"", getProperty("esito."+code+".description"));
			} 
		}
		if(EsitiProperties.esitoDescription.containsKey(codeEsito+"")==false){
			throw new ProtocolException("EsitoDescription for code ["+codeEsito+"] not found");
		}
		return EsitiProperties.esitoDescription.get(codeEsito+"");
	}
	
	private static Hashtable<String,String> esitoLabel= null;
	public String getEsitoLabel(Integer codeEsito) throws ProtocolException {
		if(EsitiProperties.esitoLabel == null){
			esitoLabel = new Hashtable<String, String>();
			List<Integer> codes = getEsitiCode();
			for (Integer code : codes) {
				esitoLabel.put(code+"", getProperty("esito."+code+".label"));
			}    
		}
		if(EsitiProperties.esitoLabel.containsKey(codeEsito+"")==false){
			throw new ProtocolException("EsitoLabel for code ["+codeEsito+"] not found");
		}
		return EsitiProperties.esitoLabel.get(codeEsito+"");
	}
	
	private static Hashtable<String,EsitoIdentificationMode> esitoIdentificationMode= null;
	public EsitoIdentificationMode getEsitoIdentificationMode(Integer codeEsito) throws ProtocolException {
		if(EsitiProperties.esitoIdentificationMode == null){
			esitoIdentificationMode = new Hashtable<String, EsitoIdentificationMode>();
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
		}
		if(EsitiProperties.esitoIdentificationMode.containsKey(codeEsito+"")==false){
			throw new ProtocolException("EsitoIdentificationMode for code ["+codeEsito+"] not found");
		}
		return EsitiProperties.esitoIdentificationMode.get(codeEsito+"");
	}
	
	private static Hashtable<String,List<EsitoIdentificationModeSoapFault>> esitoIdentificationModeSoapFaultList= null;
	public List<EsitoIdentificationModeSoapFault> getEsitoIdentificationModeSoapFaultList(Integer codeEsito) throws ProtocolException {
		if(esitoIdentificationModeSoapFaultList==null){
			esitoIdentificationModeSoapFaultList = new Hashtable<String, List<EsitoIdentificationModeSoapFault>>();
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
		}
		return EsitiProperties.esitoIdentificationModeSoapFaultList.get(codeEsito+"");
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
	
	
	
	
	private static Hashtable<String,List<EsitoIdentificationModeContextProperty>> esitoIdentificationModeContextPropertyList= null;
	public List<EsitoIdentificationModeContextProperty> getEsitoIdentificationModeContextPropertyList(Integer codeEsito) throws ProtocolException {
		if(esitoIdentificationModeContextPropertyList==null){
			esitoIdentificationModeContextPropertyList = new Hashtable<String, List<EsitoIdentificationModeContextProperty>>();
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
		}
		return EsitiProperties.esitoIdentificationModeContextPropertyList.get(codeEsito+"");
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
	
	
	
	private static List<String> esitiTransactionContextCode = null;
	public List<String> getEsitiTransactionContextCode() throws ProtocolException {
		if(EsitiProperties.esitiTransactionContextCode == null){
			EsitiProperties.esitiTransactionContextCode = getLista("esiti.transactionContext");
			for (String context : EsitiProperties.esitiTransactionContextCode) {
				if(context.length()>255){
					throw new ProtocolException("Context id ["+context+"] length must be less then 255 characters");
				}
			}
		}

		return EsitiProperties.esitiTransactionContextCode;
	}
	
	private static List<String> esitiTransactionContextCodeOrderLabel = null;
	public List<String> getEsitiTransactionContextCodeOrderLabel() throws ProtocolException {
		if(EsitiProperties.esitiTransactionContextCodeOrderLabel == null){
			EsitiProperties.esitiTransactionContextCodeOrderLabel = getLista("esiti.transactionContext.labelOrder"); 	   
		}

		return EsitiProperties.esitiTransactionContextCode;
	}
	
	private static List<String> esitiTransactionContextOrderLabel = null;
	public List<String> getEsitiTransactionContextOrderLabel() throws ProtocolException {
		if(EsitiProperties.esitiTransactionContextOrderLabel == null){
			List<String> codes = getEsitiTransactionContextCode();
			EsitiProperties.esitiTransactionContextOrderLabel = new ArrayList<String>();
			for (String codeTransactionContext : codes) {
				EsitiProperties.esitiTransactionContextOrderLabel.add(this.getEsitoTransactionContextLabel(codeTransactionContext));
			}
		}

		return EsitiProperties.esitiTransactionContextOrderLabel;
	}

	private static Hashtable<String,String> esitoTransactionContextLabel= null;
	public String getEsitoTransactionContextLabel(String tipo) throws ProtocolException {
		if(EsitiProperties.esitoTransactionContextLabel == null){
			esitoTransactionContextLabel = new Hashtable<String, String>();
			List<String> codes = getEsitiTransactionContextCode();
			for (String code : codes) {
				esitoTransactionContextLabel.put(code, getProperty("esiti.transactionContext."+code+".label"));
			}   
		}
		if(EsitiProperties.esitoTransactionContextLabel.containsKey(tipo)==false){
			throw new ProtocolException("EsitoTransactionContextLabel for code ["+tipo+"] not found");
		}
		return EsitiProperties.esitoTransactionContextLabel.get(tipo);
	}
	
	private static String esitoTransactionContextDefault= null;
	private static Boolean esitoTransactionContextDefault_read= null;
	public String getEsitoTransactionContextDefault() throws ProtocolException {
		if(EsitiProperties.esitoTransactionContextDefault_read == null){
			EsitiProperties.esitoTransactionContextDefault = getOptionalProperty("esiti.transactionContext.default");
			EsitiProperties.esitoTransactionContextDefault_read = true;
			if(EsitiProperties.esitoTransactionContextDefault!=null){
				if(this.getEsitiTransactionContextCode().contains(EsitiProperties.esitoTransactionContextDefault)==false){
					throw new ProtocolException("Indicato nella proprietà 'esiti.transactionContext.default' un codice di contesto non registrato");
				}
			}
		}
		return EsitiProperties.esitoTransactionContextDefault;
	}
	
	private static String esitoTransactionContextHeaderTrasportoName= null;
	public String getEsitoTransactionContextHeaderTrasportoName() throws ProtocolException {
		if(EsitiProperties.esitoTransactionContextHeaderTrasportoName == null){
			EsitiProperties.esitoTransactionContextHeaderTrasportoName = getProperty("esiti.transactionContext.trasporto.headerName");	   
		}
		return EsitiProperties.esitoTransactionContextHeaderTrasportoName;
	}
	
	private static String esitoTransactionContextFormBasedPropertyName= null;
	public String getEsitoTransactionContextFormBasedPropertyName() throws ProtocolException {
		if(EsitiProperties.esitoTransactionContextFormBasedPropertyName == null){
			EsitiProperties.esitoTransactionContextFormBasedPropertyName = getProperty("esiti.transactionContext.urlFormBased.propertyName");	   
		}
		return EsitiProperties.esitoTransactionContextFormBasedPropertyName;
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
		try{ 
			String name = null;
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
					throw new Exception("valore "+(i+1)+" della proprieta non definita");
				}
				p = p .trim();
				if(p.equals("")){
					throw new Exception("valore "+(i+1)+" della proprieta è vuoto");
				}
				if(lista.contains(p)){
					throw new Exception("valore "+(i+1)+" della proprieta è definito più di una volta");
				}
				lista.add(p);
			}
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la lettura della proprieta' '"+property+"': "+e.getMessage();
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
