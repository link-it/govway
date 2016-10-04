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

package org.openspcoop2.protocol.engine.builder;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.Dettagli;
import org.openspcoop2.core.eccezione.details.Dettaglio;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.Eccezione;
import org.openspcoop2.core.eccezione.details.Eccezioni;
import org.openspcoop2.core.eccezione.details.constants.Costanti;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;

/**
 * DettaglioEccezioneOpenSPCoop2Builder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DettaglioEccezioneOpenSPCoop2Builder {

	/** Logger utilizzato per debug. */
	@SuppressWarnings("unused")
	private Logger log = null;
	private org.openspcoop2.protocol.sdk.IProtocolFactory protocolFactory;
	private ITraduttore traduttore;
	private IProtocolManager protocolManager;

	public DettaglioEccezioneOpenSPCoop2Builder(Logger aLog, org.openspcoop2.protocol.sdk.IProtocolFactory protocolFactory) throws ProtocolException{
		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger(DettaglioEccezioneOpenSPCoop2Builder.class);
		this.protocolFactory = protocolFactory;
		this.traduttore = this.protocolFactory.createTraduttore();

		this.protocolManager = this.protocolFactory.createProtocolManager();
	}

	public org.openspcoop2.protocol.sdk.IProtocolFactory getProtocolFactory(){
		return this.protocolFactory;
	}





	public DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,CodiceErroreIntegrazione codErrore,String msgErrore){
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, 
				this.traduttore.toString(codErrore, null, this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()),
				false,msgErrore, null, false, false);
	}
	public DettaglioEccezione buildDettaglioEccezioneProcessamentoBusta(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,CodiceErroreIntegrazione codErrore,String msgErrore,
			Exception eProcessamento){
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, 
				this.traduttore.toString(codErrore, null, this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()),
				false,msgErrore, eProcessamento, false, false);
	}
	public DettaglioEccezione buildDettaglioEccezioneIntegrazione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,CodiceErroreIntegrazione codErrore,String msgErrore,
			Exception eProcessamento,boolean generaInformazioniGeneriche){
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, 
				this.traduttore.toString(codErrore, null, this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()),
				false,msgErrore, eProcessamento, true, generaInformazioniGeneriche);
	}

	public DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,CodiceErroreCooperazione codErrore,String msgErrore) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, this.protocolFactory.createTraduttore().toString(codErrore), true, msgErrore, null, false, false);
	}
	public DettaglioEccezione buildDettaglioEccezioneProcessamentoBusta(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,CodiceErroreCooperazione codErrore,String msgErrore,
			Exception eProcessamento) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, this.protocolFactory.createTraduttore().toString(codErrore), true, msgErrore, eProcessamento, false, false);
	}
	public DettaglioEccezione buildDettaglioEccezioneIntegrazione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,CodiceErroreCooperazione codErrore,String msgErrore,
			Exception eProcessamento,boolean generaInformazioniGeneriche) throws ProtocolException{
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, this.protocolFactory.createTraduttore().toString(codErrore), true, msgErrore, eProcessamento, true, generaInformazioniGeneriche);
	}

	public DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,String codErrore,String msgErrore,boolean isErroreProtocollo){
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, codErrore, isErroreProtocollo, msgErrore, null, false, false);
	}
	public DettaglioEccezione buildDettaglioEccezioneProcessamentoBusta(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,String codErrore,String msgErrore,boolean isErroreProtocollo,
			Exception eProcessamento){
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, codErrore, isErroreProtocollo,msgErrore, eProcessamento, false, false);
	}
	public DettaglioEccezione buildDettaglioEccezioneIntegrazione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,String codErrore,String msgErrore,boolean isErroreProtocollo,
			Exception eProcessamento,boolean generaInformazioniGeneriche){
		return buildDettaglioEccezione_engineBuildEccezione(identitaPdD, tipoPdD, modulo, codErrore, isErroreProtocollo,msgErrore, eProcessamento, true, generaInformazioniGeneriche);
	}

	private DettaglioEccezione buildDettaglioEccezione_engineBuildEccezione(IDSoggetto identitaPdD, TipoPdD tipoPdD,String modulo,String codErrore,boolean isErroreProtocollo,String msgErrore,
			Exception eProcessamento,boolean isIntegrazione,boolean generaInformazioniGeneriche){
		org.openspcoop2.core.eccezione.details.Eccezione eccezione = new org.openspcoop2.core.eccezione.details.Eccezione();
		if(isErroreProtocollo){
			eccezione.setTipo(Costanti.TIPO_ECCEZIONE_PROTOCOLLO);
		}else{
			eccezione.setTipo(Costanti.TIPO_ECCEZIONE_INTEGRAZIONE);
		}
		eccezione.setCodice(codErrore);
		eccezione.setDescrizione(msgErrore);		
		return buildDettaglioEccezione_engine(DateManager.getDate(), identitaPdD, tipoPdD , modulo, eccezione, null, 
				eProcessamento, isIntegrazione, generaInformazioniGeneriche);
	}

	public DettaglioEccezione buildDettaglioEccezione(Date oraRegistrazione, IDSoggetto identitaPdD, TipoPdD tipoPdD,  String modulo, Eccezione eccezione, Dettaglio dettaglio){
		return buildDettaglioEccezione_engine(oraRegistrazione, identitaPdD, tipoPdD, modulo, eccezione, dettaglio, null, false, false);
	}
	public DettaglioEccezione buildDettaglioEccezioneProcessamentoBusta(Date oraRegistrazione, IDSoggetto identitaPdD,TipoPdD tipoPdD,  String modulo, Eccezione eccezione, Dettaglio dettaglio,
			Exception eProcessamento){
		return buildDettaglioEccezione_engine(oraRegistrazione, identitaPdD, tipoPdD, modulo, eccezione, dettaglio, eProcessamento, false, false);
	}
	public DettaglioEccezione buildDettaglioEccezioneIntegrazione(Date oraRegistrazione, IDSoggetto identitaPdD,TipoPdD tipoPdD,  String modulo, Eccezione eccezione, Dettaglio dettaglio,
			Exception eProcessamento,boolean generaInformazioniGeneriche){
		return buildDettaglioEccezione_engine(oraRegistrazione, identitaPdD, tipoPdD, modulo, eccezione, dettaglio, eProcessamento, true, generaInformazioniGeneriche);
	}

	private DettaglioEccezione buildDettaglioEccezione_engine(Date oraRegistrazione, IDSoggetto identitaPdD,TipoPdD tipoPdD, String modulo, Eccezione eccezione, Dettaglio dettaglio,
			Exception eProcessamento,boolean isIntegrazione,boolean generaInformazioniGeneriche){

		DettaglioEccezione dettaglioEccezione = DaoBuilder.buildDettaglioEccezione(oraRegistrazione, identitaPdD, tipoPdD, modulo, eccezione, dettaglio);

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
		dettaglioEccezione.setOraRegistrazione(DateManager.getDate());
		dettaglioEccezione.setDominio(DaoBuilder.buildDominio(identitaPdD, tipoPdD, modulo));

		// eccezioni buste
		for (int i = 0; i < busta.sizeListaEccezioni(); i++) {

			org.openspcoop2.protocol.sdk.Eccezione e = busta.getEccezione(i);

			org.openspcoop2.core.eccezione.details.Eccezione eccezione = new org.openspcoop2.core.eccezione.details.Eccezione();
			if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()){
				eccezione.setTipo(Costanti.TIPO_ECCEZIONE_PROTOCOLLO);
				eccezione.setCodice(e.getCodiceEccezioneValue(this.protocolFactory));
				eccezione.setDescrizione(this.transformFaultMsg(e.getCodiceEccezione(), e.getDescrizione(this.protocolFactory)));
				eccezione.setRilevanza(e.getRilevanzaValue(this.protocolFactory));
				eccezione.setContestoCodifica(this.protocolFactory.createTraduttore().toString(ContestoCodificaEccezione.PROCESSAMENTO));
			}
			else{
				eccezione.setTipo(Costanti.TIPO_ECCEZIONE_PROTOCOLLO);
				eccezione.setCodice(e.getCodiceEccezioneValue(this.protocolFactory));
				eccezione.setDescrizione(e.getDescrizione(this.protocolFactory));
				eccezione.setRilevanza(e.getRilevanzaValue(this.protocolFactory));
				eccezione.setContestoCodifica(this.protocolFactory.createTraduttore().toString(e.getContestoCodifica()));
			}

			if(dettaglioEccezione.getEccezioni()==null){
				dettaglioEccezione.setEccezioni(new Eccezioni());
			}
			dettaglioEccezione.getEccezioni().addEccezione(eccezione);
		}

		// dettagli
		if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()==false){
			if(servizioApplicativoErogatore!=null){
				Dettaglio detail = new Dettaglio();
				detail.setTipo("servizioApplicativo");
				detail.setBase(servizioApplicativoErogatore);
				if(dettaglioEccezione.getDettagli()==null){
					dettaglioEccezione.setDettagli(new Dettagli());
				}
				dettaglioEccezione.getDettagli().addDettaglio(detail);
			}	
		}
		gestioneDettaglioEccezioneProcessamento(eProcessamento, dettaglioEccezione);

		return dettaglioEccezione;
	}


	public void gestioneDettaglioEccezioneIntegrazione(Throwable eProcessamento,DettaglioEccezione dettaglioEccezione,boolean generaInformazioniGeneriche){
		gestioneDettaglioEccezioneProcessamento_engine(eProcessamento, dettaglioEccezione,
				this.protocolManager.isGenerazioneDetailsSOAPFaultIntegrationeConStackTrace(),
				generaInformazioniGeneriche);
	}
	public void gestioneDettaglioEccezioneProcessamento(Throwable eProcessamento,DettaglioEccezione dettaglioEccezione){
		gestioneDettaglioEccezioneProcessamento_engine(eProcessamento, dettaglioEccezione, 
				this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConStackTrace(),
				this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche());
	}
	private void gestioneDettaglioEccezioneProcessamento_engine(Throwable eProcessamento,DettaglioEccezione dettaglioEccezione,
			boolean generaStackTrace,boolean generaInformazioniGeneriche){
		if(eProcessamento!=null){

			if(generaInformazioniGeneriche){

				Dettaglio detail = new Dettaglio();
				detail.setTipo("causa");

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
					if(dettaglioEccezione.getDettagli()==null){
						dettaglioEccezione.setDettagli(new Dettagli());
					}
					dettaglioEccezione.getDettagli().addDettaglio(detail);			
				}else if("Read timed out".equals(msg)){
					msg = "Read timed out";
					detail.setBase(msg);
					if(dettaglioEccezione.getDettagli()==null){
						dettaglioEccezione.setDettagli(new Dettagli());
					}
					dettaglioEccezione.getDettagli().addDettaglio(detail);	
				}else if("connect timed out".equals(msg)){
					msg = "Connect timed out";
					detail.setBase(msg);
					if(dettaglioEccezione.getDettagli()==null){
						dettaglioEccezione.setDettagli(new Dettagli());
					}
					dettaglioEccezione.getDettagli().addDettaglio(detail);	
				}

			}else{

				Dettaglio detail = new Dettaglio();
				detail.setTipo("causa");

				if(eProcessamento.getMessage()!=null)
					detail.setBase(eProcessamento.getMessage());
				else
					detail.setBase(eProcessamento.toString());
				if(dettaglioEccezione.getDettagli()==null){
					dettaglioEccezione.setDettagli(new Dettagli());
				}
				dettaglioEccezione.getDettagli().addDettaglio(detail);

				if(eProcessamento.getCause()!=null){
					gestioneDettaglioEccezioneProcessamento_engine_InnerException(eProcessamento.getCause(), dettaglioEccezione);
				}

				if(generaStackTrace){
					Dettaglio detailStackTrace = new Dettaglio();
					detailStackTrace.setTipo("stackTrace");
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
					if(dettaglioEccezione.getDettagli()==null){
						dettaglioEccezione.setDettagli(new Dettagli());
					}
					dettaglioEccezione.getDettagli().addDettaglio(detailStackTrace);
				}
			}
		}
	}
	private void gestioneDettaglioEccezioneProcessamento_engine_InnerException(Throwable e,DettaglioEccezione dettaglioEccezione){
		if(e!=null){
			Dettaglio detail = new Dettaglio();
			detail.setTipo("causato da");
			if(e.getMessage()!=null)
				detail.setBase(e.getMessage());
			else
				detail.setBase(e.toString());
			if(dettaglioEccezione.getDettagli()==null){
				dettaglioEccezione.setDettagli(new Dettagli());
			}
			dettaglioEccezione.getDettagli().addDettaglio(detail);

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
		dettaglio.setTipo(tipo);
		dettaglio.setBase(base);
		return dettaglio;
	}




	public String transformFaultMsg(CodiceErroreCooperazione code,String msg) throws ProtocolException{
		if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()){
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
		if(this.protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche()){
			// 	Mi appoggio a questa utility
			ProprietaErroreApplicativo pErroreApplicativo = new ProprietaErroreApplicativo();
			pErroreApplicativo.setFaultAsGenericCode(true);
			return pErroreApplicativo.transformFaultMsg(errore,this.protocolFactory);
		}
		return errore.getDescrizione(this.protocolFactory);
	}
}
