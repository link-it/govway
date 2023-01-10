/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.pdd.services.skeleton;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.engine.builder.DateBuilder;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;

/**
 * <p>Java class for IntegrationManagerException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationManagerException">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codiceEccezione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descrizioneEccezione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificativoFunzione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificativoPorta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="oraRegistrazione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoEccezione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

/**
 * Contiene la definizione di una eccezione lanciata dal servizio IntegrationManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@javax.xml.bind.annotation.XmlType(name = "IntegrationManagerException", namespace="http://services.pdd.openspcoop2.org", propOrder = {
    "codiceEccezione",
    "descrizioneEccezione",
    "identificativoFunzione",
    "identificativoPorta",
    "oraRegistrazione",
    "tipoEccezione"
})
@javax.xml.ws.WebFault(name = "IntegrationManagerException", targetNamespace = "http://services.pdd.openspcoop2.org")

public class IntegrationManagerException extends Exception implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4100781617691120752L;
	
	public final static String ECCEZIONE_PROTOCOLLO = "EccezioneProtocollo";
	public final static String ECCEZIONE_INTEGRAZIONE = "EccezioneIntegrazione";
	
	/** Variabili */

    private String codiceEccezione;
	private String descrizioneEccezione;
	private String identificativoFunzione;
	private String identificativoPorta;
	private String oraRegistrazione;
	private String tipoEccezione;
	
	private CodiceErroreIntegrazione codiceErroreIntegrazione;
	private CodiceErroreCooperazione codiceErroreCooperazione;
	private ProprietaErroreApplicativo proprietaErroreAppl;
    
	
	private void _setCode(String codErrore, IntegrationFunctionError functionError, ErroriProperties erroriProperties) throws ProtocolException {
		if(Costanti.isTRANSACTION_ERROR_STATUS_ABILITATO()){
			this.codiceEccezione = codErrore;
		}
		else {
			String govwayType = erroriProperties.getErrorType(functionError);
			this.codiceEccezione = govwayType;
		}
	}
	private static String _getDescription(String descrErrore, IntegrationFunctionError functionError, ErroriProperties erroriProperties) {
		try {
			boolean genericDetails = false;
			if(erroriProperties.isForceGenericDetails(functionError)) {
				genericDetails = true;
			}
			if (Costanti.isTRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS()) {
				genericDetails = false;
			}
			
			if(genericDetails){
				String govwayDetails = erroriProperties.getGenericDetails(functionError);
				return govwayDetails;
			}
			else {
				return descrErrore;
			}
		}catch(ProtocolException pExc) {
			return descrErrore; // errore non dovrebbe succedere
		}
	}
	private void _setDescription(String descrErrore, IntegrationFunctionError functionError, ErroriProperties erroriProperties) throws ProtocolException {
		this.descrizioneEccezione = _getDescription(descrErrore, functionError, erroriProperties);
	}
	
	public IntegrationManagerException(OpenSPCoop2SoapMessage message, String faultCode, String faultString, IDSoggetto identitaPdD, String identificativoFunzione) throws Exception {
		
		Object govwayPrefixCodeInContextProperty = message.getContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_PREFIX_CODE);
		Object govwayCodeInContextProperty = message.getContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_CODE);
		//Object govwayDetailsInContextProperty = message.getContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_DETAILS);
		
		String govwayPrefixInternalErrorCode = null;
		String govwayInternalErrorCode = null;
		if(govwayPrefixCodeInContextProperty!=null && govwayCodeInContextProperty!=null){
			govwayPrefixInternalErrorCode = (String) govwayPrefixCodeInContextProperty;
			govwayInternalErrorCode = (String) govwayCodeInContextProperty;
		
			if(Costanti.isTRANSACTION_ERROR_STATUS_ABILITATO()){
				this.codiceEccezione = govwayInternalErrorCode;
			}
			else {
				if(faultCode.startsWith(org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT+org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR)) {
					this.codiceEccezione = faultCode.substring((org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT+org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR).length());
				}
				else if(faultCode.startsWith(org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER+org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR)) {
					this.codiceEccezione = faultCode.substring((org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER+org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR).length());
				}
				else {
					this.codiceEccezione = faultCode;
				}
			}
			
			if(org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_PROTOCOL.equals(govwayPrefixInternalErrorCode)){
				this.setTipoEccezione(IntegrationManagerException.ECCEZIONE_PROTOCOLLO);
			}
			else {
				this.setTipoEccezione(IntegrationManagerException.ECCEZIONE_INTEGRAZIONE);
			}
			
			this.setDescrizioneEccezione(faultString);
			
			this.setOraRegistrazione(DateUtils.getSimpleDateFormatMs().format(DateManager.getDate()));
			
			this.setIdentificativoPorta(identitaPdD.getCodicePorta());
			
			this.setIdentificativoFunzione(identificativoFunzione);
		}
		else {
			throw new Exception("Not exists internal error code");
		}

	}
	

	public IntegrationManagerException(IProtocolFactory<?> protocolFactory,ErroreIntegrazione errore, 
			IntegrationFunctionError functionError, ErroriProperties erroriProperties) {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_INTEGRAZIONE,null, 
				functionError, erroriProperties);
	}
	public IntegrationManagerException(IProtocolFactory<?> protocolFactory,ErroreIntegrazione errore,IDServizioApplicativo servizioApplicativo, 
			IntegrationFunctionError functionError, ErroriProperties erroriProperties) {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_INTEGRAZIONE,servizioApplicativo, 
				functionError, erroriProperties);
	}
	private IntegrationManagerException(IProtocolFactory<?> protocolFactory,ErroreIntegrazione errore,String tipo,IDServizioApplicativo servizioApplicativo, 
			IntegrationFunctionError functionError, ErroriProperties erroriProperties) {
		super( _getDescription(errore.getDescrizioneRawValue(), functionError, erroriProperties) );
		try{
			this.initProprietaBase(protocolFactory, tipo, servizioApplicativo);
			this.codiceErroreIntegrazione = errore.getCodiceErrore();
			String cod = null;
			String desc = null;
			if(protocolFactory!=null){
				cod = protocolFactory.createTraduttore().toString(errore.getCodiceErrore(), 
						this.proprietaErroreAppl.getFaultPrefixCode(), this.proprietaErroreAppl.isFaultAsGenericCode());
				desc = errore.getDescrizione(protocolFactory);
			}
			else{
				cod = errore.getCodiceErrore().getCodice()+"";
				desc = errore.getDescrizioneRawValue();
			}
			this._setCode(cod, functionError, erroriProperties);
			this._setDescription(desc, functionError, erroriProperties);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	

	public IntegrationManagerException(IProtocolFactory<?> protocolFactory,ErroreCooperazione errore, 
			IntegrationFunctionError functionError, ErroriProperties erroriProperties) {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_PROTOCOLLO,null, 
				functionError, erroriProperties);
	}
	public IntegrationManagerException(IProtocolFactory<?> protocolFactory,ErroreCooperazione errore,IDServizioApplicativo servizioApplicativo, 
			IntegrationFunctionError functionError, ErroriProperties erroriProperties) {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_PROTOCOLLO,servizioApplicativo, 
				functionError, erroriProperties);
	}
	private IntegrationManagerException(IProtocolFactory<?> protocolFactory,ErroreCooperazione errore,String tipo,IDServizioApplicativo servizioApplicativo, 
			IntegrationFunctionError functionError, ErroriProperties erroriProperties)  {
		super( _getDescription(errore.getDescrizioneRawValue(), functionError, erroriProperties) );
		try{
			this.initProprietaBase(protocolFactory, tipo, servizioApplicativo);
			this.codiceErroreCooperazione = errore.getCodiceErrore();
			String cod = protocolFactory.createTraduttore().toString(errore.getCodiceErrore());
			String desc = errore.getDescrizione(protocolFactory);
			this._setCode(cod, functionError, erroriProperties);
			this._setDescription(desc, functionError, erroriProperties);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	
	public IntegrationManagerException(IProtocolFactory<?> protocolFactory,Eccezione errore, 
			IntegrationFunctionError functionError, ErroriProperties erroriProperties)  {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_PROTOCOLLO,null, 
				functionError, erroriProperties);
	}
	public IntegrationManagerException(IProtocolFactory<?> protocolFactory,Eccezione errore,IDServizioApplicativo servizioApplicativo, 
			IntegrationFunctionError functionError, ErroriProperties erroriProperties) {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_PROTOCOLLO,servizioApplicativo, 
				functionError, erroriProperties);
	}
	private IntegrationManagerException(IProtocolFactory<?> protocolFactory,Eccezione errore,String tipo,IDServizioApplicativo servizioApplicativo, 
			IntegrationFunctionError functionError, ErroriProperties erroriProperties) {
		super( _getDescription(getDescrizione(errore,protocolFactory), functionError, erroriProperties) );
		try{
			this.initProprietaBase(protocolFactory, tipo, servizioApplicativo);
			this.codiceErroreCooperazione = errore.getCodiceEccezione();
			String cod = protocolFactory.createTraduttore().toString(errore.getCodiceEccezione());
			String desc = errore.getDescrizione(protocolFactory);
			this._setCode(cod, functionError, erroriProperties);
			this._setDescription(desc, functionError, erroriProperties);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	private static String getDescrizione(Eccezione errore,IProtocolFactory<?> protocolFactory){
		try{
			return errore.getDescrizione(protocolFactory);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	
	public IntegrationManagerException() { }
	
	
	private void initProprietaBase(IProtocolFactory<?> protocolFactory,String tipoEccezione, IDServizioApplicativo servizioApplicativo) throws ProtocolException {
		this.oraRegistrazione = DateBuilder.getDate_Format(null);
		this.identificativoFunzione = IntegrationManager.ID_MODULO;
		this.tipoEccezione = tipoEccezione;
		OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();
		openspcoopProperties = OpenSPCoop2Properties.getInstance();
		try{
			IProtocolManager pm = null;
			if(protocolFactory!=null){
				pm = protocolFactory.createProtocolManager();
			}
			this.proprietaErroreAppl = openspcoopProperties.getProprietaGestioneErrorePD(pm);
		}catch(Exception e){
			// ignore
		}
		if(servizioApplicativo!=null){
			try{
				ConfigurazionePdDManager configPdDReader = ConfigurazionePdDManager.getInstance();
				ServizioApplicativo sa = configPdDReader.getServizioApplicativo(servizioApplicativo, null);
				configPdDReader.aggiornaProprietaGestioneErrorePD(this.proprietaErroreAppl,sa);
			}catch(Exception e){
				//OpenSPCoopLogger.getLoggerOpenSPCoopCore().error("Aggiornamento gestione errore PD servizio applicativo in IntegrationManagerException.init dell'IntegrationManager  SA["+servizioApplicativo+"] non riuscito");
			}
		}
		String protocol = null;
		if(protocolFactory!=null){
			protocol = protocolFactory.getProtocol();
		}
		this.identificativoPorta = openspcoopProperties.getIdentificativoPortaDefault(protocol, null);
	}

	@XmlTransient
	public CodiceErroreIntegrazione getCodiceErroreIntegrazione() {
		return this.codiceErroreIntegrazione;
	}	
	@XmlTransient
	public CodiceErroreCooperazione getCodiceErroreCooperazione() {
		return this.codiceErroreCooperazione;
	}
	

	@XmlElement(required = true, nillable = true)
	public void setDescrizioneEccezione(String descrizione){
		this.descrizioneEccezione = descrizione;
	}
	
	public String getDescrizioneEccezione(){
		return this.descrizioneEccezione;
	}
	
	@XmlElement(required = true, nillable = true)
	public void setTipoEccezione(String tipo){
		this.tipoEccezione = tipo;
	}
	
	public String getTipoEccezione(){
		return this.tipoEccezione;
	}
	
	@XmlElement(required = true, nillable = true)
	public void setOraRegistrazione(String ora){
		this.oraRegistrazione = ora;
	}

	public String getOraRegistrazione(){
		return this.oraRegistrazione;
	}
	
	@XmlElement(required = true, nillable = true)
	public void setIdentificativoPorta(String id){
		this.identificativoPorta = id;
	}
	
	public String getIdentificativoPorta(){
		return this.identificativoPorta;
	}
	
	@XmlElement(required = true, nillable = true)
	public void setIdentificativoFunzione(String id){
		this.identificativoFunzione = id;
	}
	
	public String getIdentificativoFunzione(){
		return this.identificativoFunzione;
	}

	@XmlElement(required = true, nillable = true)
	public void setCodiceEccezione(String codice){
		this.codiceEccezione = codice;
	}

	@XmlElement(required = true, nillable = true)
	public String getCodiceEccezione(){
		return this.codiceEccezione;
	}

	protected ProprietaErroreApplicativo getProprietaErroreApplicativo(){
		return this.proprietaErroreAppl;
	}
	

	// I seguenti transient method servono per evitare il warning:
	// WARN  JAXBSchemaInitializer:595 - propOrder in @XmlType doesn't define all schema elements :
	// [codiceEccezione, descrizioneEccezione, identificativoFunzione, identificativoPorta, oraRegistrazione, tipoEccezione]
	
	@Override
	@XmlTransient
	public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();
	}
	
	@Override
	@XmlTransient
	public String getMessage() {
		return super.getMessage();
	}
	
	@Override
	@XmlTransient
	public String getLocalizedMessage() {
		return super.getLocalizedMessage();
	}
	
	@Override
	@XmlTransient
	public Throwable getCause() {
		return super.getCause();
    }
	

}
