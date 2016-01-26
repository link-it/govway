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


package org.openspcoop2.pdd.services.skeleton;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
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
    

	public IntegrationManagerException(IProtocolFactory protocolFactory,ErroreIntegrazione errore) {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_INTEGRAZIONE,null);
	}
	public IntegrationManagerException(IProtocolFactory protocolFactory,ErroreIntegrazione errore,String servizioApplicativo) {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_INTEGRAZIONE,servizioApplicativo);
	}
	private IntegrationManagerException(IProtocolFactory protocolFactory,ErroreIntegrazione errore,String tipo,String servizioApplicativo) {
		super(errore.getDescrizioneRawValue());
		try{
			this.initProprietaBase(protocolFactory, tipo, servizioApplicativo);
			this.codiceErroreIntegrazione = errore.getCodiceErrore();
			if(protocolFactory!=null){
				this.codiceEccezione = protocolFactory.createTraduttore().toString(errore.getCodiceErrore(), 
						this.proprietaErroreAppl.getFaultPrefixCode(), this.proprietaErroreAppl.isFaultAsGenericCode());
				this.descrizioneEccezione = errore.getDescrizione(protocolFactory);
			}
			else{
				this.codiceEccezione = errore.getCodiceErrore().getCodice()+"";
				this.descrizioneEccezione = errore.getDescrizioneRawValue();
			}
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	

	public IntegrationManagerException(IProtocolFactory protocolFactory,ErroreCooperazione errore) {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_PROTOCOLLO,null);
	}
	public IntegrationManagerException(IProtocolFactory protocolFactory,ErroreCooperazione errore,String servizioApplicativo) {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_PROTOCOLLO,servizioApplicativo);
	}
	private IntegrationManagerException(IProtocolFactory protocolFactory,ErroreCooperazione errore,String tipo,String servizioApplicativo)  {
		super(errore.getDescrizioneRawValue());
		try{
			this.initProprietaBase(protocolFactory, tipo, servizioApplicativo);
			this.codiceErroreCooperazione = errore.getCodiceErrore();
			this.codiceEccezione = protocolFactory.createTraduttore().toString(errore.getCodiceErrore());
			this.descrizioneEccezione = errore.getDescrizione(protocolFactory);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	
	public IntegrationManagerException(IProtocolFactory protocolFactory,Eccezione errore)  {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_PROTOCOLLO,null);
	}
	public IntegrationManagerException(IProtocolFactory protocolFactory,Eccezione errore,String servizioApplicativo) {
		this(protocolFactory,errore,IntegrationManagerException.ECCEZIONE_PROTOCOLLO,servizioApplicativo);
	}
	private IntegrationManagerException(IProtocolFactory protocolFactory,Eccezione errore,String tipo,String servizioApplicativo) {
		super(getDescrizione(errore,protocolFactory));
		try{
			this.initProprietaBase(protocolFactory, tipo, servizioApplicativo);
			this.codiceErroreCooperazione = errore.getCodiceEccezione();
			this.codiceEccezione = protocolFactory.createTraduttore().toString(errore.getCodiceEccezione());
			this.descrizioneEccezione = errore.getDescrizione(protocolFactory);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	private static String getDescrizione(Eccezione errore,IProtocolFactory protocolFactory){
		try{
			return errore.getDescrizione(protocolFactory);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	
	public IntegrationManagerException() { }
	
	
	private void initProprietaBase(IProtocolFactory protocolFactory,String tipoEccezione, String servizioApplicativo) throws ProtocolException {
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
		}catch(Exception e){}
		if(servizioApplicativo!=null){
			try{
				ConfigurazionePdDManager configPdDReader = ConfigurazionePdDManager.getInstance();
				ServizioApplicativo sa = configPdDReader.getServizioApplicativo(new IDPortaDelegata(), servizioApplicativo);
				configPdDReader.aggiornaProprietaGestioneErrorePD(this.proprietaErroreAppl,sa);
			}catch(Exception e){
				//OpenSPCoopLogger.getLoggerOpenSPCoopCore().error("Aggiornamento gestione errore PD servizio applicativo in IntegrationManagerException.init dell'IntegrationManager  SA["+servizioApplicativo+"] non riuscito");
			}
		}
		String protocol = null;
		if(protocolFactory!=null){
			protocol = protocolFactory.getProtocol();
		}
		this.identificativoPorta = openspcoopProperties.getIdentificativoPortaDefault(protocol);
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
}
