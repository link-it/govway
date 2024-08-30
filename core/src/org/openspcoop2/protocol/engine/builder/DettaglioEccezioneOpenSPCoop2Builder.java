/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.engine.builder;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.Dettagli;
import org.openspcoop2.core.eccezione.details.Dettaglio;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.Eccezione;
import org.openspcoop2.core.eccezione.details.Eccezioni;
import org.openspcoop2.core.eccezione.details.constants.TipoEccezione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.config.IntegrationErrorReturnConfiguration;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**
 * DettaglioEccezioneOpenSPCoop2Builder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DettaglioEccezioneOpenSPCoop2Builder {

	/** Logger utilizzato per debug. */
	private Logger log = null;
	private org.openspcoop2.protocol.sdk.IProtocolFactory<?> protocolFactory;
	private ITraduttore traduttore;
	private IProtocolManager protocolManager;
	private ErroriProperties erroriProperties;

	public DettaglioEccezioneOpenSPCoop2Builder(Logger aLog, org.openspcoop2.protocol.sdk.IProtocolFactory<?> protocolFactory) throws ProtocolException{
		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger(DettaglioEccezioneOpenSPCoop2Builder.class);
		this.protocolFactory = protocolFactory;
		this.traduttore = this.protocolFactory.createTraduttore();

		this.protocolManager = this.protocolFactory.createProtocolManager();
		
		this.erroriProperties = ErroriProperties.getInstance(this.log);
	}

	public org.openspcoop2.protocol.sdk.IProtocolFactory<?> getProtocolFactory(){
		return this.protocolFactory;
	}





//	public DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione codErrore,String msgErrore,
//			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError)throws ProtocolException{
//		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, 
//				this.traduttore.toString(codErrore, null, this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche()),
//				false,msgErrore, null, false, false,
//				returnConfig, functionError);
//	}
//	public DettaglioEccezione buildDettaglioEccezioneProcessamentoBusta(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione codErrore,String msgErrore,
//			Exception eProcessamento,
//			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError)throws ProtocolException{
//		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, 
//				this.traduttore.toString(codErrore, null, this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche()),
//				false,msgErrore, eProcessamento, false, false,
//				returnConfig, functionError);
//	}
//	public DettaglioEccezione buildDettaglioEccezioneIntegrazione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione codErrore,String msgErrore,
//			Exception eProcessamento,boolean generaInformazioniGeneriche,
//			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError)throws ProtocolException{
//		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, 
//				this.traduttore.toString(codErrore, null, this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche()),
//				false,msgErrore, eProcessamento, true, generaInformazioniGeneriche,
//				returnConfig, functionError);
//	}
	
	public DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,ErroreIntegrazione errore,String msgErrore,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, 
				this.traduttore.toCodiceErroreIntegrazioneAsString(errore, null, this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche()),
				false,msgErrore, null, false, false,
				returnConfig, functionError);
	}
	public DettaglioEccezione buildDettaglioEccezioneProcessamentoBusta(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,ErroreIntegrazione errore,String msgErrore,
			Exception eProcessamento,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, 
				this.traduttore.toCodiceErroreIntegrazioneAsString(errore, null, this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche()),
				false,msgErrore, eProcessamento, false, false,
				returnConfig, functionError);
	}
	public DettaglioEccezione buildDettaglioEccezioneIntegrazione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,ErroreIntegrazione errore,String msgErrore,
			Exception eProcessamento,boolean generaInformazioniGeneriche,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, 
				this.traduttore.toCodiceErroreIntegrazioneAsString(errore, null, this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche()),
				false,msgErrore, eProcessamento, true, generaInformazioniGeneriche,
				returnConfig, functionError);
	}

	public DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,CodiceErroreCooperazione codErrore,String msgErrore,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, this.protocolFactory.createTraduttore().toString(codErrore), true, msgErrore, null, false, false,
				returnConfig, functionError);
	}
	public DettaglioEccezione buildDettaglioEccezioneProcessamentoBusta(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,CodiceErroreCooperazione codErrore,String msgErrore,
			Exception eProcessamento,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, this.protocolFactory.createTraduttore().toString(codErrore), true, msgErrore, 
				eProcessamento, false, false,
				returnConfig, functionError);
	}
	public DettaglioEccezione buildDettaglioEccezioneIntegrazione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,CodiceErroreCooperazione codErrore,String msgErrore,
			Exception eProcessamento,boolean generaInformazioniGeneriche,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, this.protocolFactory.createTraduttore().toString(codErrore), true, msgErrore, 
				eProcessamento, true, generaInformazioniGeneriche,
				returnConfig, functionError);
	}

	public DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,String codErrore,String msgErrore,boolean isErroreProtocollo,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, codErrore, isErroreProtocollo, msgErrore, null, false, false,
				returnConfig, functionError);
	}
	public DettaglioEccezione buildDettaglioEccezioneProcessamentoBusta(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,String codErrore,String msgErrore,boolean isErroreProtocollo,
			Exception eProcessamento,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, codErrore, isErroreProtocollo,msgErrore, eProcessamento, false, false,
				returnConfig, functionError);
	}
	public DettaglioEccezione buildDettaglioEccezioneIntegrazione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,String codErrore,String msgErrore,boolean isErroreProtocollo,
			Exception eProcessamento,boolean generaInformazioniGeneriche,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, codErrore, isErroreProtocollo,msgErrore, eProcessamento, true, generaInformazioniGeneriche,
				returnConfig, functionError);
	}

	private DettaglioEccezione buildDettaglioEccezione_engineBuildEccezione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,String codErrore,boolean isErroreProtocollo,String msgErrore,
			Exception eProcessamento,boolean isIntegrazione,boolean generaInformazioniGeneriche,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		org.openspcoop2.core.eccezione.details.Eccezione eccezione = new org.openspcoop2.core.eccezione.details.Eccezione();
		if(isErroreProtocollo){
			eccezione.setType(TipoEccezione.PROTOCOL);
		}else{
			eccezione.setType(TipoEccezione.INTEGRATION);
		}
		eccezione.setCode(codErrore);
		eccezione.setDescription(msgErrore);		
		return buildDettaglioEccezione_engine(DateManager.getDate(), identitaPdD, tipoPdD , modulo, eccezione, null, 
				eProcessamento, isIntegrazione, generaInformazioniGeneriche,
				returnConfig, functionError);
	}

	public DettaglioEccezione buildDettaglioEccezione(Date oraRegistrazione, IDSoggetto identitaPdD, TipoPdD tipoPdD,  String modulo, Eccezione eccezione, Dettaglio dettaglio,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engine(oraRegistrazione, identitaPdD, tipoPdD, modulo, eccezione, dettaglio, null, false, false,
				returnConfig, functionError);
	}
	public DettaglioEccezione buildDettaglioEccezioneProcessamentoBusta(Date oraRegistrazione, IDSoggetto identitaPdD,TipoPdD tipoPdD,  String modulo, Eccezione eccezione, Dettaglio dettaglio,
			Exception eProcessamento,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engine(oraRegistrazione, identitaPdD, tipoPdD, modulo, eccezione, dettaglio, eProcessamento, false, false,
				returnConfig, functionError);
	}
	public DettaglioEccezione buildDettaglioEccezioneIntegrazione(Date oraRegistrazione, IDSoggetto identitaPdD,TipoPdD tipoPdD,  String modulo, Eccezione eccezione, Dettaglio dettaglio,
			Exception eProcessamento,boolean generaInformazioniGeneriche,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{
		return buildDettaglioEccezione_engine(oraRegistrazione, identitaPdD, tipoPdD, modulo, eccezione, dettaglio, eProcessamento, true, generaInformazioniGeneriche,
				returnConfig, functionError);
	}

	private DettaglioEccezione buildDettaglioEccezione_engine(Date oraRegistrazione, IDSoggetto identitaPdD,TipoPdD tipoPdD, String modulo, Eccezione eccezione, Dettaglio dettaglio,
			Exception eProcessamento,boolean isIntegrazione,boolean generaInformazioniGeneriche,
			IntegrationErrorReturnConfiguration returnConfig, IntegrationFunctionError functionError) throws ProtocolException{

		DettaglioEccezione dettaglioEccezione = DaoBuilder.buildDettaglioEccezione(oraRegistrazione, identitaPdD, tipoPdD, modulo, eccezione, dettaglio,
				returnConfig, functionError, this.erroriProperties);

		if(eProcessamento!=null){
			if(isIntegrazione){
				gestioneDettaglioEccezioneIntegrazione(eProcessamento, dettaglioEccezione, generaInformazioniGeneriche);
			}else{
				gestioneDettaglioEccezioneProcessamento(eProcessamento, dettaglioEccezione);
			}
		}

		return dettaglioEccezione;
	}


	// METODO CHIAMATO SOLO DALLA PDD EROGATORE PER GENERARE IL DETTAGLIO DI UNA BUSTA ERRORE
	public DettaglioEccezione buildDettaglioEccezioneFromBusta(IDSoggetto identitaPdD,TipoPdD tipoPdD,
			String modulo,
			String servizioApplicativoErogatore,
			Busta busta,
			Throwable eProcessamento) throws ProtocolException{

		DettaglioEccezione dettaglioEccezione = new DettaglioEccezione();

		// info generali
		dettaglioEccezione.setTimestamp(DateManager.getDate());
		dettaglioEccezione.setDomain(DaoBuilder.buildDominio(identitaPdD, tipoPdD, modulo));

		// eccezioni buste
		for (int i = 0; i < busta.sizeListaEccezioni(); i++) {

			org.openspcoop2.protocol.sdk.Eccezione e = busta.getEccezione(i);

			org.openspcoop2.core.eccezione.details.Eccezione eccezione = new org.openspcoop2.core.eccezione.details.Eccezione();
			eccezione.setType(TipoEccezione.PROTOCOL);
			eccezione.setCode(e.getCodiceEccezioneValue(this.protocolFactory));
			eccezione.setSeverity(e.getRilevanzaValue(this.protocolFactory));
			if(this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche()){
				eccezione.setDescription(this.transformFaultMsg(e.getCodiceEccezione(), e.getDescrizione(this.protocolFactory)));
				eccezione.setContext(this.protocolFactory.createTraduttore().toString(ContestoCodificaEccezione.PROCESSAMENTO));
			}
			else{
				eccezione.setDescription(e.getDescrizione(this.protocolFactory));
				eccezione.setContext(this.protocolFactory.createTraduttore().toString(e.getContestoCodifica()));
			}

			if(dettaglioEccezione.getExceptions()==null){
				dettaglioEccezione.setExceptions(new Eccezioni());
			}
			dettaglioEccezione.getExceptions().addException(eccezione);
		}

		// dettagli
		if(this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche()==false){
			if(servizioApplicativoErogatore!=null){
				Dettaglio detail = new Dettaglio();
				detail.setType("servizioApplicativo");
				detail.setBase(servizioApplicativoErogatore);
				if(dettaglioEccezione.getDetails()==null){
					dettaglioEccezione.setDetails(new Dettagli());
				}
				dettaglioEccezione.getDetails().addDetail(detail);
			}	
		}
		gestioneDettaglioEccezioneProcessamento(eProcessamento, dettaglioEccezione);

		return dettaglioEccezione;
	}


	public void gestioneDettaglioEccezioneIntegrazione(Throwable eProcessamento,DettaglioEccezione dettaglioEccezione,boolean generaInformazioniGeneriche){
		gestioneDettaglioEccezioneProcessamento_engine(eProcessamento, dettaglioEccezione,
				this.protocolManager.isGenerazioneDetailsFaultIntegrationeConStackTrace(),
				generaInformazioniGeneriche);
	}
	public void gestioneDettaglioEccezioneProcessamento(Throwable eProcessamento,DettaglioEccezione dettaglioEccezione){
		gestioneDettaglioEccezioneProcessamento_engine(eProcessamento, dettaglioEccezione, 
				this.protocolManager.isGenerazioneDetailsFaultProtocolloConStackTrace(),
				this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche());
	}
	private void gestioneDettaglioEccezioneProcessamento_engine(Throwable eProcessamento,DettaglioEccezione dettaglioEccezione,
			boolean generaStackTrace,boolean generaInformazioniGeneriche){
		if(eProcessamento!=null){

			if(generaInformazioniGeneriche){

				Dettaglio detail = new Dettaglio();
				detail.setType("causa");

				String msg = null;
				if(eProcessamento.getMessage()!=null){
					msg = eProcessamento.getMessage();
				}else{
					msg = eProcessamento.toString();
				}
				//System.out.println("MESSAGE ["+msg+"]");
				// faccio viaggiare solo le informazioni che ritengo pubbliche
				if("Connection refused".equals(msg)){
					msg = "Connection refused";
					detail.setBase(msg);
					if(dettaglioEccezione.getDetails()==null){
						dettaglioEccezione.setDetails(new Dettagli());
					}
					dettaglioEccezione.getDetails().addDetail(detail);			
				}else if("Read timed out".equals(msg)){
					msg = "Read timed out";
					detail.setBase(msg);
					if(dettaglioEccezione.getDetails()==null){
						dettaglioEccezione.setDetails(new Dettagli());
					}
					dettaglioEccezione.getDetails().addDetail(detail);	
				}else if("connect timed out".equals(msg)){
					msg = "Connect timed out";
					detail.setBase(msg);
					if(dettaglioEccezione.getDetails()==null){
						dettaglioEccezione.setDetails(new Dettagli());
					}
					dettaglioEccezione.getDetails().addDetail(detail);	
				}

			}else{

				Dettaglio detail = new Dettaglio();
				detail.setType("causa");

				if(eProcessamento.getMessage()!=null)
					detail.setBase(eProcessamento.getMessage());
				else
					detail.setBase(eProcessamento.toString());
				if(dettaglioEccezione.getDetails()==null){
					dettaglioEccezione.setDetails(new Dettagli());
				}
				dettaglioEccezione.getDetails().addDetail(detail);

				if(eProcessamento.getCause()!=null){
					gestioneDettaglioEccezioneProcessamento_engine_InnerException(eProcessamento.getCause(), dettaglioEccezione);
				}

				if(generaStackTrace){
					Dettaglio detailStackTrace = new Dettaglio();
					detailStackTrace.setType("stackTrace");
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					PrintWriter pWriter = new PrintWriter(bout);
					eProcessamento.printStackTrace(pWriter);
					try{
						bout.flush();
						pWriter.flush();
						pWriter.close();
						bout.close();
					}catch(Exception eClose){
						System.err.println("ERRORE buildEccezioneProcessamentoFromBusta: "+eClose.getMessage());
					}
					detailStackTrace.setBase(bout.toString());
					if(dettaglioEccezione.getDetails()==null){
						dettaglioEccezione.setDetails(new Dettagli());
					}
					dettaglioEccezione.getDetails().addDetail(detailStackTrace);
				}
			}
		}
	}
	private void gestioneDettaglioEccezioneProcessamento_engine_InnerException(Throwable e,DettaglioEccezione dettaglioEccezione){
		if(e!=null){
			Dettaglio detail = new Dettaglio();
			detail.setType("causato da");
			if(e.getMessage()!=null)
				detail.setBase(e.getMessage());
			else
				detail.setBase(e.toString());
			if(dettaglioEccezione.getDetails()==null){
				dettaglioEccezione.setDetails(new Dettagli());
			}
			dettaglioEccezione.getDetails().addDetail(detail);

			if(e.getCause()!=null){
				gestioneDettaglioEccezioneProcessamento_engine_InnerException(e.getCause(), dettaglioEccezione);
			}
		}
	}




	public Eccezione buildEccezione(String codice, String descrizione, String rilevanza, String contesto, boolean isErroreProtocollo){
		return DaoBuilder.buildEccezione(codice, descrizione, rilevanza, contesto, isErroreProtocollo);
	}

	public Dettaglio buildDettagio(String tipo, String base){
		Dettaglio dettaglio = new Dettaglio();
		dettaglio.setType(tipo);
		dettaglio.setBase(base);
		return dettaglio;
	}




	public String transformFaultMsg(CodiceErroreCooperazione code,String msg) throws ProtocolException{
		if(this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche()){
			if(code.equals(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO)){
				//errore di processamento.
				// Lascio intatto solo il msg di ServizioApplicativo non disponibile
				if(!CostantiProtocollo.SERVIZIO_APPLICATIVO_NON_DISPONIBILE.equals(msg)){
					return MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO_SENZA_CODICE.toString(this.protocolFactory);
				}
			}
		}
		return msg;	
	}

	public String transformFaultMsg(ErroreIntegrazione errore) throws ProtocolException{
		if(this.protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche()){
			// 	Mi appoggio a questa utility
			ProprietaErroreApplicativo pErroreApplicativo = new ProprietaErroreApplicativo();
			pErroreApplicativo.setFaultAsGenericCode(true);
			return pErroreApplicativo.transformFaultMsg(errore,this.protocolFactory);
		}
		return errore.getDescrizione(this.protocolFactory);
	}
}
