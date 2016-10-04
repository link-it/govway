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


package org.openspcoop2.protocol.spcoop.testsuite.handler;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;

/**
 * Libreria per handler testsuite
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Properties forwardProperties;
	private boolean generaErroreVerificaInstallazioneArchivioTest;
	private EsitoTransazioneName esito;
	private Integer returnCodePDReq;
	private Integer returnCodePDRes;
	private Integer returnCodePAReq;
	private Integer returnCodePARes;
	private String locationPD;
	private String locationPA;
	private String tipoConnettorePD;
	private String tipoConnettorePA;
	private ProtocolContext egovContext;
	private String correlazioneApplicativaPDReq;
	private String correlazioneApplicativaPAReq;
	private String servizioApplicativoFruitore;
	private String servizioApplicativoErogatore;
	private Boolean statelessPD;
	private Boolean statelessPA;
	private Date dataInizioTest;
	private boolean rispostaVuotaSA_PD;
	private boolean rispostaVuotaPD_PA;
	private boolean rispostaVuotaPA_SA;
	private boolean rispostaVuotaPA_PD;
	
	public TestContext() throws HandlerException{
		// Utilizzato dalla Testsuite
	}
	public TestContext(TipoPdD tipoPdD,Properties p) throws HandlerException{
		
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			this.forwardProperties = new Properties();
		}
		
		// Generazione errore
		Object o = getProperty(p,Costanti.TEST_CONTEXT_GENERA_ERRORE);
		if( (o!=null) && (o instanceof String) ){
			if("true".equals((String)o)){
				this.setGeneraErroreVerificaInstallazioneArchivioTest(true);
			}
		}

		// Data inizio Test
		Object oDataTest = getProperty(p,Costanti.TEST_CONTEXT_DATA_INIZIO_TEST);
		if( (oDataTest!=null) && (oDataTest instanceof String) ){
			try{
				SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
				this.dataInizioTest = dateformat.parse((String)oDataTest);
			}catch(Exception e){
				throw new HandlerException("Data fornita nell'header ["+(String)oDataTest+"] non corretta rispetto allo standard [yyyy-MM-dd_HH:mm:ss.SSS]");
			}
		}
	
		// Dimensione risposta
		Object oRispostaVuotaSA_PD = getProperty(p,Costanti.TEST_CONTEXT_RISPOSTA_VUOTA_SA_PD);
		if( (oRispostaVuotaSA_PD!=null) && (oRispostaVuotaSA_PD instanceof String) ){
			if("true".equals((String)oRispostaVuotaSA_PD)){
				this.setRispostaVuotaSA_PD(true);
			}
		}
		Object oRispostaVuotaPD_PA = getProperty(p,Costanti.TEST_CONTEXT_RISPOSTA_VUOTA_PD_PA);
		if( (oRispostaVuotaPD_PA!=null) && (oRispostaVuotaPD_PA instanceof String) ){
			if("true".equals((String)oRispostaVuotaPD_PA)){
				this.setRispostaVuotaPD_PA(true);
			}
		}
		Object oRispostaVuotaPA_SA = getProperty(p,Costanti.TEST_CONTEXT_RISPOSTA_VUOTA_PA_SA);
		if( (oRispostaVuotaPA_SA!=null) && (oRispostaVuotaPA_SA instanceof String) ){
			if("true".equals((String)oRispostaVuotaPA_SA)){
				this.setRispostaVuotaPA_SA(true);
			}
		}
		Object oRispostaVuotaPA_PD = getProperty(p,Costanti.TEST_CONTEXT_RISPOSTA_VUOTA_PA_PD);
		if( (oRispostaVuotaPA_PD!=null) && (oRispostaVuotaPA_PD instanceof String) ){
			if("true".equals((String)oRispostaVuotaPA_PD)){
				this.setRispostaVuotaPA_PD(true);
			}
		}
		
		// Esito
		Object oEsito = getProperty(p,Costanti.TEST_CONTEXT_ESITO);
		if( (oEsito!=null) && (oEsito instanceof String) ){
			EsitoTransazioneName e = EsitoTransazioneName.convertoTo((String)oEsito);
			if(e==null){
				throw new HandlerException("Esito richiesto dalla testsuite ["+(String)oEsito+"] non gestito dalla Porta di Dominio");
			}
			this.setEsito(e);
		}
		
		// Return Code
		this.setReturnCodePDReq(getIntValue(p, Costanti.TEST_CONTEXT_DELEGATA_REQUEST_RETURN_CODE));
		this.setReturnCodePDRes(getIntValue(p, Costanti.TEST_CONTEXT_DELEGATA_RESPONSE_RETURN_CODE));
		this.setReturnCodePAReq(getIntValue(p, Costanti.TEST_CONTEXT_APPLICATIVA_REQUEST_RETURN_CODE));
		this.setReturnCodePARes(getIntValue(p, Costanti.TEST_CONTEXT_APPLICATIVA_RESPONSE_RETURN_CODE));
		
		// Location
		this.setLocationPD(getStringValue(p, Costanti.TEST_CONTEXT_DELEGATA_LOCATION));
		this.setLocationPA(getStringValue(p, Costanti.TEST_CONTEXT_APPLICATIVA_LOCATION));
		
		// Tipo Connettore
		this.setTipoConnettorePD(getStringValue(p, Costanti.TEST_CONTEXT_DELEGATA_TIPO_CONNETTORE));
		this.setTipoConnettorePA(getStringValue(p, Costanti.TEST_CONTEXT_APPLICATIVA_TIPO_CONNETTORE));
		
		// EGov Context
		Object oEGovContext = getProperty(p,Costanti.TEST_CONTEXT_EGOV);
		if(oEGovContext!=null && "true".equals(oEGovContext)){
			ProtocolContext egov = new ProtocolContext();
			egov.setFruitore(getIDSoggettoValue(p, Costanti.TEST_CONTEXT_EGOV_TIPO_MITTENTE, Costanti.TEST_CONTEXT_EGOV_MITTENTE));
			egov.setErogatore(getIDSoggettoValue(p, Costanti.TEST_CONTEXT_EGOV_TIPO_DESTINATARIO, Costanti.TEST_CONTEXT_EGOV_DESTINATARIO));
			egov.setIdRichiesta(getStringValue(p, Costanti.TEST_CONTEXT_EGOV_ID_RICHIESTA));
			egov.setIdRisposta(getStringValue(p, Costanti.TEST_CONTEXT_EGOV_ID_RISPOSTA));
			egov.setProfiloCollaborazione(ProfiloDiCollaborazione.toProfiloDiCollaborazione(getStringValue(p, Costanti.TEST_CONTEXT_EGOV_PROFILO_COLLABORAZIONE)),
					getStringValue(p, Costanti.TEST_CONTEXT_EGOV_PROFILO_COLLABORAZIONE));
			egov.setCollaborazione(getStringValue(p, Costanti.TEST_CONTEXT_EGOV_COLLABORAZIONE));
			egov.setScenarioCooperazione(getStringValue(p, Costanti.TEST_CONTEXT_EGOV_SCENARIO));
			egov.setTipoServizio(getStringValue(p, Costanti.TEST_CONTEXT_EGOV_TIPO_SERVIZIO));
			egov.setServizio(getStringValue(p, Costanti.TEST_CONTEXT_EGOV_SERVIZIO));
			egov.setAzione(getStringValue(p, Costanti.TEST_CONTEXT_EGOV_AZIONE));
			this.setEgovContext(egov);
		}
		
		// Integration Context
		this.correlazioneApplicativaPDReq = getStringValue(p, Costanti.TEST_CONTEXT_CORRELAZIONE_APPLICATIVA_PD_REQ);
		this.correlazioneApplicativaPAReq = getStringValue(p, Costanti.TEST_CONTEXT_CORRELAZIONE_APPLICATIVA_PA_REQ);
		this.servizioApplicativoFruitore = getStringValue(p, Costanti.TEST_CONTEXT_SERVIZIO_APPLICATIVO_FRUITORE);
		this.servizioApplicativoErogatore = getStringValue(p, Costanti.TEST_CONTEXT_SERVIZIO_APPLICATIVO_EROGATORE);
		this.statelessPD = getBooleanValue(p, Costanti.TEST_CONTEXT_STATELESS_PD);
		this.statelessPA = getBooleanValue(p, Costanti.TEST_CONTEXT_STATELESS_PA);
	}
	
	private String getStringValue(Properties p,String key){
		Object o = getProperty(p, key);
		if(o!=null && o instanceof String){
			return (String) o;
		}else{
			return null;
		}
	}
	private Integer getIntValue(Properties p,String key){
		Object o = getProperty(p, key);
		if( (o!=null) && (o instanceof Integer) ){
			return(Integer)o;
		}else if( (o!=null) && (o instanceof String) ){
			return Integer.parseInt((String)o);
		}
		else{
			return null;
		}
	}
	private Boolean getBooleanValue(Properties p,String key){
		Object o = getProperty(p, key);
		if( (o!=null) && (o instanceof Boolean) ){
			return(Boolean)o;
		}else if( (o!=null) && (o instanceof String) ){
			return Boolean.parseBoolean((String)o);
		}
		else{
			return null;
		}
	}
	private IDSoggetto getIDSoggettoValue(Properties p,String tipoKey,String nomeKey){
		Object tipo =  getStringValue(p, tipoKey);
		Object nome =  getStringValue(p, nomeKey);
		if(tipo!=null || nome!=null){
			IDSoggetto id = new IDSoggetto();
			if(tipo!=null){
				id.setTipo((String)tipo);
			}
			if(nome!=null){
				id.setNome((String)nome);
			}
			return id;
		}
		else{
			return null;
		}
	}
	
	private Object getProperty(Properties p,String key){
		
		Object o = p.get(key);
		if(o == null){
			o = p.get(key.toLowerCase());
		}
		if(o == null){
			o = p.get(key.toUpperCase());
		}
		
		if(this.forwardProperties!=null && (o instanceof String)){
			this.forwardProperties.put(key, (String)o);
		}
		
		return o;
	}
	
	public void writeTo(Properties p) throws HandlerException{
		if(this.dataInizioTest!=null){
			SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
			p.setProperty(Costanti.TEST_CONTEXT_DATA_INIZIO_TEST, dateformat.format(this.dataInizioTest));
		}
		// sempre serializzata
		p.setProperty(Costanti.TEST_CONTEXT_RISPOSTA_VUOTA_SA_PD, this.rispostaVuotaSA_PD+"");
		p.setProperty(Costanti.TEST_CONTEXT_RISPOSTA_VUOTA_PD_PA, this.rispostaVuotaPD_PA+"");
		p.setProperty(Costanti.TEST_CONTEXT_RISPOSTA_VUOTA_PA_SA, this.rispostaVuotaPA_SA+"");
		p.setProperty(Costanti.TEST_CONTEXT_RISPOSTA_VUOTA_PA_PD, this.rispostaVuotaPA_PD+"");
		
		if(this.esito!=null){
			p.setProperty(Costanti.TEST_CONTEXT_ESITO, EsitoTransazioneName.OK.toString());
		}
		if(this.returnCodePDReq!=null){
			p.setProperty(Costanti.TEST_CONTEXT_DELEGATA_REQUEST_RETURN_CODE, this.returnCodePDReq+"");
		}
		if(this.returnCodePDRes!=null){
			p.setProperty(Costanti.TEST_CONTEXT_DELEGATA_RESPONSE_RETURN_CODE, this.returnCodePDRes+"");
		}
		if(this.returnCodePAReq!=null){
			p.setProperty(Costanti.TEST_CONTEXT_APPLICATIVA_REQUEST_RETURN_CODE, this.returnCodePAReq+"");
		}
		if(this.returnCodePARes!=null){
			p.setProperty(Costanti.TEST_CONTEXT_APPLICATIVA_RESPONSE_RETURN_CODE, this.returnCodePARes+"");
		}
		if(this.locationPD!=null){
			p.setProperty(Costanti.TEST_CONTEXT_DELEGATA_LOCATION, this.locationPD);
		}
		if(this.locationPA!=null){
			p.setProperty(Costanti.TEST_CONTEXT_APPLICATIVA_LOCATION, this.locationPA);
		}
		if(this.tipoConnettorePD!=null){
			p.setProperty(Costanti.TEST_CONTEXT_DELEGATA_TIPO_CONNETTORE, this.tipoConnettorePD);
		}
		if(this.tipoConnettorePA!=null){
			p.setProperty(Costanti.TEST_CONTEXT_DELEGATA_TIPO_CONNETTORE, this.tipoConnettorePA);
		}
		if(this.egovContext!=null){
			p.setProperty(Costanti.TEST_CONTEXT_EGOV, "true");
			if(this.egovContext.getFruitore()!=null){
				if(this.egovContext.getFruitore().getTipo()!=null){
					p.setProperty(Costanti.TEST_CONTEXT_EGOV_TIPO_MITTENTE,this.egovContext.getFruitore().getTipo());
				}
				if(this.egovContext.getFruitore().getNome()!=null){
					p.setProperty(Costanti.TEST_CONTEXT_EGOV_MITTENTE,this.egovContext.getFruitore().getNome());
				}
			}
			if(this.egovContext.getErogatore()!=null){
				if(this.egovContext.getErogatore().getTipo()!=null){
					p.setProperty(Costanti.TEST_CONTEXT_EGOV_TIPO_DESTINATARIO,this.egovContext.getErogatore().getTipo());
				}
				if(this.egovContext.getErogatore().getNome()!=null){
					p.setProperty(Costanti.TEST_CONTEXT_EGOV_DESTINATARIO,this.egovContext.getErogatore().getNome());
				}
			}
			if(this.egovContext.getIdRichiesta()!=null){
				p.setProperty(Costanti.TEST_CONTEXT_EGOV_ID_RICHIESTA, this.egovContext.getIdRichiesta());
			}
			if(this.egovContext.getIdRisposta()!=null){
				p.setProperty(Costanti.TEST_CONTEXT_EGOV_ID_RISPOSTA, this.egovContext.getIdRisposta());
			}
			if(this.egovContext.getProfiloCollaborazione()!=null){
				p.setProperty(Costanti.TEST_CONTEXT_EGOV_PROFILO_COLLABORAZIONE, this.egovContext.getProfiloCollaborazione().getEngineValue());
			}
			if(this.egovContext.getCollaborazione()!=null){
				p.setProperty(Costanti.TEST_CONTEXT_EGOV_COLLABORAZIONE, this.egovContext.getCollaborazione());
			}
			if(this.egovContext.getScenarioCooperazione()!=null){
				p.setProperty(Costanti.TEST_CONTEXT_EGOV_SCENARIO, this.egovContext.getScenarioCooperazione());
			}
			if(this.egovContext.getTipoServizio()!=null){
				p.setProperty(Costanti.TEST_CONTEXT_EGOV_TIPO_SERVIZIO, this.egovContext.getTipoServizio());
			}
			if(this.egovContext.getServizio()!=null){
				p.setProperty(Costanti.TEST_CONTEXT_EGOV_SERVIZIO, this.egovContext.getServizio());
			}
			if(this.egovContext.getAzione()!=null){
				p.setProperty(Costanti.TEST_CONTEXT_EGOV_AZIONE, this.egovContext.getAzione());
			}
		}
		if(this.correlazioneApplicativaPDReq!=null){
			p.setProperty(Costanti.TEST_CONTEXT_CORRELAZIONE_APPLICATIVA_PD_REQ, this.correlazioneApplicativaPDReq);
		}
		if(this.correlazioneApplicativaPAReq!=null){
			p.setProperty(Costanti.TEST_CONTEXT_CORRELAZIONE_APPLICATIVA_PA_REQ, this.correlazioneApplicativaPAReq);
		}
		if(this.servizioApplicativoFruitore!=null){
			p.setProperty(Costanti.TEST_CONTEXT_SERVIZIO_APPLICATIVO_FRUITORE, this.servizioApplicativoFruitore);
		}
		if(this.servizioApplicativoErogatore!=null){
			p.setProperty(Costanti.TEST_CONTEXT_SERVIZIO_APPLICATIVO_EROGATORE, this.servizioApplicativoErogatore);
		}
		if(this.getStatelessPD()!=null){
			p.setProperty(Costanti.TEST_CONTEXT_STATELESS_PD, this.getStatelessPD().booleanValue()+"");
		}
		if(this.getStatelessPA()!=null){
			p.setProperty(Costanti.TEST_CONTEXT_STATELESS_PA, this.getStatelessPA().booleanValue()+"");
		}
	}
	
	public EsitoTransazioneName getEsito() {
		return this.esito;
	}

	public void setEsito(EsitoTransazioneName esito) {
		this.esito = esito;
	}

	public Properties getForwardProperties() {
		return this.forwardProperties;
	}

	public void setForwardProperties(Properties forwardProperties) {
		this.forwardProperties = forwardProperties;
	}

	public boolean isGeneraErroreVerificaInstallazioneArchivioTest() {
		return this.generaErroreVerificaInstallazioneArchivioTest;
	}

	public void setGeneraErroreVerificaInstallazioneArchivioTest(
			boolean generaErroreVerificaInstallazioneArchivioTest) {
		this.generaErroreVerificaInstallazioneArchivioTest = generaErroreVerificaInstallazioneArchivioTest;
	}

	public ProtocolContext getEgovContext() {
		return this.egovContext;
	}
	public void setEgovContext(ProtocolContext egovContext) {
		this.egovContext = egovContext;
	}
	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}
	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}
	public String getServizioApplicativoErogatore() {
		return this.servizioApplicativoErogatore;
	}
	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		this.servizioApplicativoErogatore = servizioApplicativoErogatore;
	}
	public Integer getReturnCodePDReq() {
		return this.returnCodePDReq;
	}
	public void setReturnCodePDReq(Integer returnCodePDReq) {
		this.returnCodePDReq = returnCodePDReq;
	}
	public Integer getReturnCodePDRes() {
		return this.returnCodePDRes;
	}
	public void setReturnCodePDRes(Integer returnCodePDRes) {
		this.returnCodePDRes = returnCodePDRes;
	}
	public Integer getReturnCodePAReq() {
		return this.returnCodePAReq;
	}
	public void setReturnCodePAReq(Integer returnCodePAReq) {
		this.returnCodePAReq = returnCodePAReq;
	}
	public Integer getReturnCodePARes() {
		return this.returnCodePARes;
	}
	public void setReturnCodePARes(Integer returnCodePARes) {
		this.returnCodePARes = returnCodePARes;
	}
	public String getLocationPD() {
		return this.locationPD;
	}
	public void setLocationPD(String locationPD) {
		this.locationPD = locationPD;
	}
	public String getLocationPA() {
		return this.locationPA;
	}
	public void setLocationPA(String locationPA) {
		this.locationPA = locationPA;
	}
	public String getTipoConnettorePD() {
		return this.tipoConnettorePD;
	}
	public void setTipoConnettorePD(String tipoConnettorePD) {
		this.tipoConnettorePD = tipoConnettorePD;
	}
	public String getTipoConnettorePA() {
		return this.tipoConnettorePA;
	}
	public void setTipoConnettorePA(String tipoConnettorePA) {
		this.tipoConnettorePA = tipoConnettorePA;
	}
	public Date getDataInizioTest() {
		return this.dataInizioTest;
	}
	public void setDataInizioTest(Date dataInizioTest) {
		this.dataInizioTest = dataInizioTest;
	}
	public boolean isRispostaVuotaSA_PD() {
		return this.rispostaVuotaSA_PD;
	}
	public void setRispostaVuotaSA_PD(boolean rispostaVuotaSA_PD) {
		this.rispostaVuotaSA_PD = rispostaVuotaSA_PD;
	}
	public boolean isRispostaVuotaPD_PA() {
		return this.rispostaVuotaPD_PA;
	}
	public void setRispostaVuotaPD_PA(boolean rispostaVuotaPD_PA) {
		this.rispostaVuotaPD_PA = rispostaVuotaPD_PA;
	}
	public boolean isRispostaVuotaPA_SA() {
		return this.rispostaVuotaPA_SA;
	}
	public void setRispostaVuotaPA_SA(boolean rispostaVuotaPA_SA) {
		this.rispostaVuotaPA_SA = rispostaVuotaPA_SA;
	}
	public boolean isRispostaVuotaPA_PD() {
		return this.rispostaVuotaPA_PD;
	}
	public void setRispostaVuotaPA_PD(boolean rispostaVuotaPA_PD) {
		this.rispostaVuotaPA_PD = rispostaVuotaPA_PD;
	}
	public String getCorrelazioneApplicativaPDReq() {
		return this.correlazioneApplicativaPDReq;
	}
	public void setCorrelazioneApplicativaPDReq(String correlazioneApplicativaPDReq) {
		this.correlazioneApplicativaPDReq = correlazioneApplicativaPDReq;
	}
	public String getCorrelazioneApplicativaPAReq() {
		return this.correlazioneApplicativaPAReq;
	}
	public void setCorrelazioneApplicativaPAReq(String correlazioneApplicativaPAReq) {
		this.correlazioneApplicativaPAReq = correlazioneApplicativaPAReq;
	}
	public Boolean getStatelessPD() {
		return this.statelessPD;
	}
	public void setStatelessPD(Boolean statelessPD) {
		this.statelessPD = statelessPD;
	}
	public Boolean getStatelessPA() {
		return this.statelessPA;
	}
	public void setStatelessPA(Boolean statelessPA) {
		this.statelessPA = statelessPA;
	}
}
