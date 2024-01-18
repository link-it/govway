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
package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Proprieta;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.InUsoType;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCostanti;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * SoggettiHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettiHelper extends ConnettoriHelper {

	public SoggettiHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public SoggettiHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}

	public List<DataElement> addSoggettiToDati(TipoOperazione tipoOp,List<DataElement> dati, String nomeprov, String tipoprov, String portadom, String descr, 
			boolean isRouter, List<String> tipiSoggetti, String profilo, boolean privato, String codiceIpa, List<String> versioni, 
			boolean isSupportatoCodiceIPA, boolean isSupportatoIdentificativoPorta,
			String [] pddList,String[] pddEsterneList,String nomePddGestioneLocale, String pdd, 
			List<String> listaTipiProtocollo, String protocollo,
			boolean isSupportatoAutenticazioneSoggetti, String utente,String password, String subject, String principal, String tipoauth,
			boolean isPddEsterna,String tipologia, String dominio,
			String tipoCredenzialiSSLSorgente, org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio, BinaryParameter tipoCredenzialiSSLFileCertificato, String tipoCredenzialiSSLFileCertificatoPassword,
			List<String> listaAliasEstrattiCertificato, String tipoCredenzialiSSLAliasCertificato,	String tipoCredenzialiSSLAliasCertificatoSubject, String tipoCredenzialiSSLAliasCertificatoIssuer,
			String tipoCredenzialiSSLAliasCertificatoType, String tipoCredenzialiSSLAliasCertificatoVersion, String tipoCredenzialiSSLAliasCertificatoSerialNumber, String tipoCredenzialiSSLAliasCertificatoSelfSigned,
			String tipoCredenzialiSSLAliasCertificatoNotBefore, String tipoCredenzialiSSLAliasCertificatoNotAfter, String tipoCredenzialiSSLVerificaTuttiICampi, String tipoCredenzialiSSLConfigurazioneManualeSelfSigned,
			String issuer,String tipoCredenzialiSSLStatoElaborazioneCertificato,
			String changepwd,
			String multipleApiKey, String appId, String apiKey, 
			boolean visualizzaModificaCertificato, boolean visualizzaAddCertificato, String servletCredenzialiList, List<Parameter> parametersServletCredenzialiList, Integer numeroCertificati, String servletCredenzialiAdd) throws Exception  {
		return addSoggettiToDati(tipoOp, dati, nomeprov, tipoprov, portadom, descr, 
				isRouter, tipiSoggetti, profilo, privato, codiceIpa, versioni, 
				isSupportatoCodiceIPA, isSupportatoIdentificativoPorta,
				pddList, pddEsterneList, nomePddGestioneLocale, pdd,
				null,null,null,null,
				-1,null,-1,null,listaTipiProtocollo,protocollo,
				isSupportatoAutenticazioneSoggetti, utente, password, subject, principal, tipoauth,
				isPddEsterna, tipologia, dominio,
				tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
				tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
				tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
				tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
				tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuer,tipoCredenzialiSSLStatoElaborazioneCertificato,
				changepwd,
				multipleApiKey, appId, apiKey,
				visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd, 0);
	}
	public List<DataElement> addSoggettiToDati(TipoOperazione tipoOp,List<DataElement> dati, String nomeprov, String tipoprov, String portadom, String descr, 
			boolean isRouter, List<String> tipiSoggetti, String profilo, boolean privato, String codiceIpa, List<String> versioni, 
			boolean isSupportatoCodiceIPA, boolean isSupportatoIdentificativoPorta,
			String [] pddList,String[] pddEsterneList,String nomePddGestioneLocale,String pdd, 
			String id, String oldnomeprov, String oldtipoprov, org.openspcoop2.core.registry.Connettore connettore,
			long numPD,String pdUrlPrefixRewriter,long numPA, String paUrlPrefixRewriter, List<String> listaTipiProtocollo, String protocollo,
			boolean isSupportatoAutenticazioneSoggetti, String utente,String password, String subject, String principal, String tipoauth,
			boolean isPddEsterna,String tipologia, String dominio,String tipoCredenzialiSSLSorgente, org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio, BinaryParameter tipoCredenzialiSSLFileCertificato, String tipoCredenzialiSSLFileCertificatoPassword,
			List<String> listaAliasEstrattiCertificato, String tipoCredenzialiSSLAliasCertificato,	String tipoCredenzialiSSLAliasCertificatoSubject, String tipoCredenzialiSSLAliasCertificatoIssuer,
			String tipoCredenzialiSSLAliasCertificatoType, String tipoCredenzialiSSLAliasCertificatoVersion, String tipoCredenzialiSSLAliasCertificatoSerialNumber, String tipoCredenzialiSSLAliasCertificatoSelfSigned,
			String tipoCredenzialiSSLAliasCertificatoNotBefore, String tipoCredenzialiSSLAliasCertificatoNotAfter, String tipoCredenzialiSSLVerificaTuttiICampi, String tipoCredenzialiSSLConfigurazioneManualeSelfSigned,
			String issuer,String tipoCredenzialiSSLStatoElaborazioneCertificato,
			String changepwd,
			String multipleApiKey, String appId, String apiKey, 
			boolean visualizzaModificaCertificato, boolean visualizzaAddCertificato, String servletCredenzialiList, List<Parameter> parametersServletCredenzialiList, Integer numeroCertificati, String servletCredenzialiAdd, int numeroProprieta) throws Exception {

		Soggetto soggetto = null;
		if(TipoOperazione.CHANGE.equals(tipoOp) &&
				oldtipoprov!=null && !"".equals(oldtipoprov) && 
				oldnomeprov!=null && !"".equals(oldnomeprov)) {
			IDSoggetto idSoggetto = new IDSoggetto(oldtipoprov, oldnomeprov);
			soggetto = this.soggettiCore.getSoggettoRegistro(idSoggetto);
		}

		if(TipoOperazione.CHANGE.equals(tipoOp)){
			String labelSoggetto = this.getLabelNomeSoggetto(protocollo, tipoprov, nomeprov);
			
			List<Parameter> listaParametriChange = new ArrayList<>();
			listaParametriChange.add(new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,id));
			listaParametriChange.add(new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,nomeprov));
			listaParametriChange.add(new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,tipoprov));
			
			// In Uso Button
			this.addComandoInUsoButton(labelSoggetto,
					id,
					InUsoType.SOGGETTO);
			
			// Verifica Certificati
			if(this.core.isSoggettiVerificaCertificati()) {
				// Verifica certificati visualizzato solo se il soggetto ha credenziali https
				boolean ssl = false;
				if(org.openspcoop2.core.registry.constants.CredenzialeTipo.SSL.equals(tipoauth)) { 
						//&& c.getCertificate()!=null) { non viene ritornato dalla lista
					ssl = true;
				}
				if(ssl) {
					this.pd.addComandoVerificaCertificatiElementoButton(SoggettiCostanti.SERVLET_NAME_SOGGETTI_VERIFICA_CERTIFICATI, listaParametriChange);
				}
			}
			
			// se e' abilitata l'opzione reset cache per elemento, visualizzo il comando nell'elenco dei comandi disponibili nella lista
			if(this.core.isElenchiVisualizzaComandoResetCacheSingoloElemento()){
				listaParametriChange.add(new Parameter(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE, "true"));
				this.pd.addComandoResetCacheElementoButton(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, listaParametriChange);
			}
			
			// Proprieta Button
			if(this.existsProprietaOggetto(soggetto.getProprietaOggetto(), soggetto.getDescrizione())) {
				this.addComandoProprietaOggettoButton(labelSoggetto,id, InUsoType.SOGGETTO);
			}
		}
		

		
		
		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
		
		if(TipoOperazione.CHANGE.equals(tipoOp)){
			DataElement de = new DataElement();
			de.setLabel(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			dati.add(de);
		}

		
		DataElement de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_SOGGETTO);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		boolean gestionePdd = true;
		if(this.core.isSinglePdD() &&
			!this.core.isGestionePddAbilitata(this)) {
			gestionePdd = false;
		}
		
		boolean multiTenant = this.core.isMultitenant();
		boolean hiddenDatiDominioInterno = false;
		if(!multiTenant &&
			!gestionePdd) {
			hiddenDatiDominioInterno = SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(dominio);
		}
		
		if(gestionePdd) {
			
			if(TipoOperazione.ADD.equals(tipoOp)){
				
				if(this.core.isRegistroServiziLocale()){
					de = new DataElement();
					de.setLabel(PddCostanti.LABEL_PORTA_DI_DOMINIO);
					de.setType(DataElementType.SELECT);
					de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
					if(this.soggettiCore.isMultitenant()) {
						de.setValues(pddList);
					}
					else {
						de.setValues(pddEsterneList);
					}
					de.setSelected(pdd);
					de.setPostBack(isSupportatoAutenticazioneSoggetti);
					if (this.core.isSinglePdD()) {
						if( 
								(pdd==null || "".equals(pdd))
								&&
								nomePddGestioneLocale!=null
							){
							de.setSelected(nomePddGestioneLocale);
						}
					}else{
						de.setRequired(true);
					}
					dati.add(de);
				}
			}
			else{		
				if(this.core.isSinglePdD()){
					if(this.core.isRegistroServiziLocale()){
						de = new DataElement();
						de.setLabel(PddCostanti.LABEL_PORTA_DI_DOMINIO);
						de.setType(DataElementType.SELECT);
						de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
						if(this.soggettiCore.isMultitenant()) {
							de.setValues(pddList);
						}
						else {
							de.setValues(pddEsterneList);
						}
						de.setSelected(pdd);
						de.setPostBack(isSupportatoAutenticazioneSoggetti);
						dati.add(de);
					}
				}else{
					de = new DataElement();
					de.setLabel(PddCostanti.LABEL_PORTA_DI_DOMINIO);
					de.setType(DataElementType.TEXT);
					de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
					de.setValue(pdd);
					dati.add(de);
				}
			}
		}
		else {
			boolean listOperativoSolamente = false;
			if(pddList!=null && pddList.length==1 && pddList[0].equals(nomePddGestioneLocale)) {
				listOperativoSolamente = true;
			}
			
			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DOMINIO);
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);
			if(!multiTenant) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(dominio);
			}
			else if(TipoOperazione.CHANGE.equals(tipoOp) && listOperativoSolamente){
				de.setType(DataElementType.HIDDEN);
				de.setValue(dominio);
				dati.add(de);
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DOMINIO);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO+CostantiControlStation.PARAMETRO_SUFFIX_LABEL);
				String valueDom = dominio;
				String [] sdValues = SoggettiCostanti.getSoggettiDominiValue();
				String [] sdLabels = SoggettiCostanti.getSoggettiDominiLabel();
				for (int i = 0; i < sdValues.length; i++) {
					if(sdValues[i].equals(dominio)) {
						valueDom = sdLabels[i];
						break;
					}
				}
				de.setValue(valueDom);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(SoggettiCostanti.getSoggettiDominiValue());
				de.setLabels(SoggettiCostanti.getSoggettiDominiLabel());
				de.setSelected(dominio);
				de.setPostBack(isSupportatoAutenticazioneSoggetti);
			}
			dati.add(de);
		}


		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO);
		if(TipoOperazione.ADD.equals(tipoOp)){
			if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1){
				de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO);
				de.setValues(listaTipiProtocollo);
				de.setLabels(this.getLabelsProtocolli(listaTipiProtocollo));
				de.setSelected(protocollo);
				de.setType(DataElementType.SELECT);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
				de.setPostBack(true);
			} else {
				de.setValue(protocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
			}
		} else {
			if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1){
				
				DataElement deLABEL = new DataElement();
				deLABEL.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO);
				deLABEL.setType(DataElementType.TEXT);
				deLABEL.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO+CostantiControlStation.PARAMETRO_SUFFIX_LABEL);
				deLABEL.setValue(this.getLabelProtocollo(protocollo));
				dati.add(deLABEL);
				
				de.setValue(protocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
			} else {
				de.setValue(protocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
			}
		}
		de.setSize(this.getSize());
		dati.add(de);

		String[] tipiLabel = new String[tipiSoggetti.size()];
		for (int i = 0; i < tipiSoggetti.size(); i++) {
			String nomeTipo = tipiSoggetti.get(i);
			tipiLabel[i] = nomeTipo;
		}

		String[] versioniLabel = new String[versioni.size()];
		for (int i = 0; i < versioni.size(); i++) {
			String versione = versioni.get(i);
			versioniLabel[i] = versione;
		}

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPO);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
		if(tipiLabel!=null && tipiLabel.length>1) {
			de.setType(DataElementType.SELECT);
			de.setValues(tipiLabel);
			de.setSelected(tipoprov);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			if( 
					((tipoprov==null || "".equals(tipoprov)) && tipiLabel!=null && tipiLabel.length>0)
					||
					(tipoprov!=null && tipiLabel!=null && tipiLabel.length>0 && !tipoprov.equals(tipiLabel[0])) // fix per cambio protocollo
			) {
				tipoprov = tipiLabel[0];
			}
			de.setValue(tipoprov);
		}
		de.setSize(this.getSize());
		de.setPostBack(true);
		dati.add(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME);
		de.setValue(nomeprov);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.add(de);
		
		if(TipoOperazione.ADD.equals(tipoOp)){
			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPOLOGIA);
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPOLOGIA);
			if(isPddEsterna && isSupportatoAutenticazioneSoggetti){
				de.setValue(tipologia);
				de.setSelected(tipologia);
				de.setType(DataElementType.SELECT);
				de.setValues(SoggettiCostanti.getSoggettiRuoli());
				de.setPostBack(true);
			}
			else{
				de.setValue("");
				de.setType(DataElementType.HIDDEN);
			}
			de.setSize(this.getSize());
			dati.add(de);
		}
		

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_CODICE_PORTA);
		de.setValue(portadom);
		if (!isSupportatoIdentificativoPorta) {
			de.setType(DataElementType.HIDDEN);
		}else{
			de.setType(DataElementType.TEXT_EDIT);
		}
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_PORTA);
		de.setSize(this.getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_CODICE_IPA);
		de.setValue(codiceIpa);
		if (!isSupportatoCodiceIPA) {
			de.setType(DataElementType.HIDDEN);
		}else{
			if(this.core.isRegistroServiziLocale()){
				de.setType(DataElementType.TEXT_EDIT);
			}else{
				de.setType(DataElementType.HIDDEN);
			}
		}
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);
		de.setSize(this.getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DESCRIZIONE);
		de.setValue(descr);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(2);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_DESCRIZIONE);
		de.setSize(this.getSize());
		dati.add(de);


		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);
		boolean showVersioneProtocollo = this.core.isRegistroServiziLocale() && (this.apsCore.getVersioniProtocollo(protocollo).size()>1);
		if(showVersioneProtocollo){
			de.setValues(versioniLabel);
			de.setSelected(profilo);
			de.setType(DataElementType.SELECT);
		}
		else{
			de.setValue(profilo);
			de.setType(DataElementType.HIDDEN);
		}
		de.setSize(this.getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_IS_PRIVATO);
		if (this.core.isShowFlagPrivato() && this.isModalitaAvanzata() && this.core.isRegistroServiziLocale() ) {
			de.setType(DataElementType.CHECKBOX);
		} else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO);
		de.setSelected(privato ? "yes" : "");
		de.setSize(this.getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_IS_ROUTER);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_ROUTER);
		// Un router lo si puo' voler creare anche in singlePdD.
		if (this.isModalitaAvanzata() && this.core.isShowGestioneSoggettiRouter()) {
			de.setType(DataElementType.CHECKBOX);
			if (isRouter) {
				de.setSelected(true);
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(Costanti.CHECK_BOX_DISABLED);
		}
		dati.add(de);


		if(TipoOperazione.CHANGE.equals(tipoOp)){

			boolean showConnettore = this.core.isRegistroServiziLocale() && this.isModalitaCompleta();
						
			if(showConnettore){
				
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,id),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,oldnomeprov),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,oldtipoprov));
				Utilities.setDataElementLabelTipoConnettore(de, connettore);
				dati.add(de);
				
			}
		
		}
		
		// link proprieta
		if(TipoOperazione.CHANGE.equals(tipoOp)){
			de = new DataElement();
			de.setType(DataElementType.LINK);
			
			List<Parameter> parametersServletSoggettoChange = new ArrayList<>();
			Parameter pIdSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, id);
			Parameter pNomeSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, nomeprov);
			Parameter pTipoSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, tipoprov);
			parametersServletSoggettoChange.add(pIdSoggetto);
			parametersServletSoggettoChange.add(pNomeSoggetto);
			parametersServletSoggettoChange.add(pTipoSoggetto);
			
			de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_PROPRIETA_LIST, parametersServletSoggettoChange.toArray(new Parameter[parametersServletSoggettoChange.size()]));
			if (contaListe!=null && contaListe.booleanValue()) {
				de.setValue(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROPRIETA+"(" + numeroProprieta + ")");
			} else {
				de.setValue(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROPRIETA);
			}
			
			dati.add(de);
			
		}
		
		// Credenziali di accesso
		if(isSupportatoAutenticazioneSoggetti){
			if (utente == null) {
				utente = "";
			}
			if (password == null) {
				password = "";
			}
			if (subject == null) {
				subject = "";
			}
			if (principal == null) {
				principal = "";
			}
			String servlet = null;
			if(TipoOperazione.ADD.equals(tipoOp)){
				servlet = SoggettiCostanti.SERVLET_NAME_SOGGETTI_ADD;
			}
			else{
				servlet = SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE;
			}
			
			boolean autenticazioneNessunaAbilitata = true;
			boolean showCredenziali = true;
			if(this.pddCore.isPddEsterna(pdd)){
				if(SoggettiCostanti.SOGGETTO_RUOLO_FRUITORE.equals(tipologia) || SoggettiCostanti.SOGGETTO_RUOLO_ENTRAMBI.equals(tipologia)){
					autenticazioneNessunaAbilitata = this.saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(protocollo);
				}
				if(SoggettiCostanti.SOGGETTO_RUOLO_EROGATORE.equals(tipologia)){
					showCredenziali = false;
				}
			}
			else{
				if(hiddenDatiDominioInterno) {
					showCredenziali = false;
				}
				else if(TipoOperazione.ADD.equals(tipoOp)){
					showCredenziali = this.isModalitaAvanzata();
				}
			}
			
			if(showCredenziali){
				
				String oldtipoauth = null;
				if(isSupportatoAutenticazioneSoggetti && TipoOperazione.CHANGE.equals(tipoOp) &&
						soggetto!=null) {
					
					// prendo il primo
					org.openspcoop2.core.registry.constants.CredenzialeTipo tipo = null;
					if(soggetto.sizeCredenzialiList()>0) {
						tipo = soggetto.getCredenziali(0).getTipo();
					}
					if(tipo!=null) {
						oldtipoauth = tipo.getValue();
					}
					else {
						oldtipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
					}
				}
				
				dati = this.addCredenzialiToDati(tipoOp, dati, tipoauth, oldtipoauth, utente, password, subject, principal, servlet, true, null, false, true, null, autenticazioneNessunaAbilitata,
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuer,tipoCredenzialiSSLStatoElaborazioneCertificato,
						changepwd,
						multipleApiKey, appId, apiKey, visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd,
						false, null, null, false,
						SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE.equals(dominio), protocollo);
			}
		}
		
		if(TipoOperazione.CHANGE.equals(tipoOp)){

			de = new DataElement();
			de.setLabel(RuoliCostanti.LABEL_RUOLI);
			de.setType(DataElementType.TITLE);
			dati.add(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			if(this.isModalitaCompleta()) {
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_RUOLI_LIST,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,id+""));
			}
			else {
				// Imposto Accesso da Change!
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_RUOLI_LIST,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,id+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_RUOLI_ACCESSO_DA_CHANGE,Costanti.CHECK_BOX_ENABLED));
			}
			if (contaListe) {
				// BugFix OP-674
				/**List<String> lista1 = this.soggettiCore.soggettiRuoliList(Long.parseLong(id),new Search(true));*/
				ConsoleSearch searchForCount = new ConsoleSearch(true,1);
				this.soggettiCore.soggettiRuoliList(Long.parseLong(id),searchForCount);
				int numRuoli = searchForCount.getNumEntries(Liste.SOGGETTI_RUOLI);
				ServletUtils.setDataElementCustomLabel(de,RuoliCostanti.LABEL_RUOLI,Long.valueOf(numRuoli));
			} else{
				ServletUtils.setDataElementCustomLabel(de,RuoliCostanti.LABEL_RUOLI);
			}
			dati.add(de);
			
		}

		
		if(TipoOperazione.CHANGE.equals(tipoOp) && !this.pddCore.isPddEsterna(pdd)){
			
			if (this.isModalitaCompleta()) {	
				de = new DataElement();
				de.setLabel(SoggettiCostanti.LABEL_CLIENT);
				de.setType(DataElementType.TITLE);
				dati.add(de);
			}

			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			if (this.isModalitaCompleta()) {	
				de.setType(DataElementType.TEXT_EDIT);
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			de.setValue(pdUrlPrefixRewriter);
			de.setSize(this.getSize());
			dati.add(de);
			
			if (this.isModalitaCompleta()) {	
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,id+""),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_SOGGETTO,oldnomeprov),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SOGGETTO,oldtipoprov));
				if (contaListe) {
					ServletUtils.setDataElementVisualizzaLabel(de,numPD);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				dati.add(de);
			}

			if (this.isModalitaCompleta()) {	
				de = new DataElement();
				de.setLabel(SoggettiCostanti.LABEL_SERVER);
				de.setType(DataElementType.TITLE);
				dati.add(de);
			}

			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);
			if (this.isModalitaCompleta()) {	
				de.setType(DataElementType.TEXT_EDIT);
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);
			de.setValue(paUrlPrefixRewriter);
			de.setSize(this.getSize());
			dati.add(de);
			
			if (this.isModalitaCompleta()) {	
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,id+""),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_SOGGETTO,oldnomeprov),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_SOGGETTO,oldtipoprov));
				if (contaListe) {
					ServletUtils.setDataElementVisualizzaLabel(de,numPA);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				dati.add(de);
			}
		}

		return dati;
	}

	public boolean soggettiCheckData(TipoOperazione tipoOp, String id, String tipoprov, String nomeprov, String codiceIpa, String pdUrlPrefixRewriter, String paUrlPrefixRewriter,
			Soggetto soggettoOld, boolean isSupportatoAutenticazioneSoggetti, String descrizione) throws Exception {
		try {

			int idInt = 0;
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				idInt = Integer.parseInt(id);
			}


			// Campi obbligatori
			if (nomeprov.equals("") || tipoprov.equals("") ) {
				String tmpElenco = "";
				if (nomeprov.equals("")) {
					tmpElenco = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME;
				}
				if (tipoprov.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPO;
					} else {
						tmpElenco = tmpElenco + ", "+SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPO;
					}
				}
				this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nomeprov.indexOf(" ") != -1) || (tipoprov.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Il tipo deve contenere solo lettere e numeri
			if(!this.checkSimpleName(tipoprov, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPO)){
				return false;
			}
			
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoprov);
			
			// Il nome deve contenere solo lettere e numeri e -
			if(this.soggettiCore.isSupportatoTrattinoNomeSoggetto(protocollo)) {
				if(!this.checkSimpleNamePath(nomeprov, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME)){
					return false;
				}
			}
			else {
				if(!this.checkSimpleName(nomeprov, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME)){
					return false;
				}
			}
			
			// check lunghezza NOTA: Si usa una lunghezza inferiore di 20 al limite massimo della colonna 
			// per poter salvaguardare poi il 255 delle colonne per codice ipa e identificativo porta calcolate in base al nome del soggetto
			int maxLength = 255 - 20;
			if(this.soggettiCore.getSoggettiNomeMaxLength()!=null && this.soggettiCore.getSoggettiNomeMaxLength()>0) {
				maxLength = this.soggettiCore.getSoggettiNomeMaxLength();
			}
			if(!this.checkLength(nomeprov, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME, -1, maxLength)) {
				return false;
			}
			if(descrizione!=null && !"".equals(descrizione)) {
				if(this.checkLength4000(descrizione, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DESCRIZIONE)==false) {
					return false;
				}
			}

			
			// check Codice IPA
			/**try{
				if(codiceIpa!=null && !"".equals(codiceIpa)){
					SICAtoOpenSPCoopUtilities.validateIDSoggettoSICA(codiceIpa);
				}
			}catch(Exception e){
				this.pd.setMessage("Codice IPA non corretto: " + e.getMessage());
				return false;
			}*/

			// Controllo che eventuali PdUrlPrefixRewriter o PaUrlPrefixRewriter rispettino l'espressione regolare: [A-Za-z]+:\/\/(.*)
			if(pdUrlPrefixRewriter!=null && !"".equals(pdUrlPrefixRewriter) &&
				!RegularExpressionEngine.isMatch(pdUrlPrefixRewriter, "[A-Za-z]+:\\/\\/(.*)")){
				this.pd.setMessage("Il campo UrlPrefix rewriter del profilo client contiene un valore errato. Il valore atteso deve seguire la sintassi: "+
						StringEscapeUtils.escapeHtml("protocol://hostname[:port][/*]"));
				return false;
			}
			if(paUrlPrefixRewriter!=null && !"".equals(paUrlPrefixRewriter) &&
				!RegularExpressionEngine.isMatch(paUrlPrefixRewriter, "[A-Za-z]+:\\/\\/(.*)")){
				this.pd.setMessage("Il campo UrlPrefix rewriter del profilo server contiene un valore errato. Il valore atteso deve seguire la sintassi: "+
						StringEscapeUtils.escapeHtml("protocol://hostname[:port][/*]"));
				return false;
			}

			IDSoggetto ids = new IDSoggetto(tipoprov, nomeprov);
			String labelSoggetto = this.getLabelNomeSoggetto(ids);
			
			// Se tipoOp = add o tipoOp = change, controllo che non esistano
			// altri soggetti con stessi nome e tipo
			// Se tipoOp = change, devo fare attenzione a non escludere nome e
			// tipo del soggetto selezionato
			if (tipoOp.equals(TipoOperazione.ADD) || tipoOp.equals(TipoOperazione.CHANGE)) {
				int idSogg = 0;
				boolean existsSogg = this.soggettiCore.existsSoggetto(ids);
				if (existsSogg) {
					if(this.core.isRegistroServiziLocale()){
						Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(ids);
						idSogg = mySogg.getId().intValue();
					}else{
						org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(ids);
						idSogg = mySogg.getId().intValue();
					}
				}
				if ((idSogg != 0) && (tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && (idInt != idSogg)))) {
					this.pd.setMessage("Esiste gi&agrave; un soggetto "+labelSoggetto);
					return false;
				}

				// Controllo che il codiceIPA non sia gia utilizzato. Il fatto che non esista in base al nome, e' gia garantito rispetto all'univocita' del nome.
				if(this.core.isRegistroServiziLocale() &&
					codiceIpa!=null && !"".equals(codiceIpa)){
					if(tipoOp.equals(TipoOperazione.ADD)){
						if(this.soggettiCore.existsSoggetto(codiceIpa)){
							this.pd.setMessage("Esiste gi&agrave; un soggetto con Codice IPA: " + codiceIpa);
							return false;
						}
					}
					else{
						Soggetto mySogg = null;
						try{
							mySogg = this.soggettiCore.getSoggettoByCodiceIPA(codiceIpa);
						}catch(DriverRegistroServiziNotFound dnot){
							// ignore
						}
						if(mySogg!=null &&
							mySogg.getId()!=idInt){
							this.pd.setMessage("Esiste gi&agrave; un soggetto con Codice IPA: " + codiceIpa);
							return false;
						}
					}
				}
			}

			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				
				String oldTipoAuth = null;
				if(isSupportatoAutenticazioneSoggetti &&
						soggettoOld!=null) {
					// prendo il primo
					org.openspcoop2.core.registry.constants.CredenzialeTipo tipo = null;
					if(soggettoOld.sizeCredenzialiList()>0) {
						tipo = soggettoOld.getCredenziali(0).getTipo();
					}
					if(tipo!=null) {
						oldTipoAuth = tipo.getValue();
					}
					else {
						oldTipoAuth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
					}
				}
				
				String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
				
				if(oldTipoAuth!=null && !oldTipoAuth.equals(tipoauth)) {
					// controllo che non sia usato in qualche PD
					
					FiltroRicercaPorteApplicative filtro = new FiltroRicercaPorteApplicative();
					filtro.setIdSoggettoAutorizzato(ids);
					List<IDPortaApplicativa> list = this.porteApplicativeCore.getAllIdPorteApplicative(filtro);
					
					if(org.openspcoop2.protocol.engine.constants.Costanti.SPCOOP_PROTOCOL_NAME.equals(protocollo)) {
						// verifico che non sia utilizzato in porte applicative dove è abilitata l'autenticazione
						int count = 0;
						if(list!=null && !list.isEmpty()) {
							for (IDPortaApplicativa idPortaApplicativa : list) {
								PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idPortaApplicativa);
								if(!CostantiConfigurazione.AUTENTICAZIONE_NONE.equalsIgnoreCase(pa.getAutenticazione()) &&
										!CostantiConfigurazione.DISABILITATO.toString().equalsIgnoreCase(pa.getAutenticazione())) {
									count++; // default ssl se non impostata
								}
							}
						}
						if(count>0) {
							this.pd.setMessage("Non &egrave; possibile modificare il tipo di credenziali poich&egrave; il soggetto viene utilizzato all'interno del controllo degli accessi di "+
									list.size()+" configurazioni di erogazione di servizio con autenticazione trasporto abilitata");
							return false;
						}
					}
					else {
						if(list!=null && !list.isEmpty()) {
							this.pd.setMessage("Non &egrave; possibile modificare il tipo di credenziali poich&egrave; il soggetto viene utilizzato all'interno del controllo degli accessi di "+
									list.size()+" configurazioni di erogazione di servizio");
							return false;
						}
					}
				}
				
			}
			

			boolean oldPasswordCifrata = false;
			if(soggettoOld!=null && soggettoOld.sizeCredenzialiList()>0 && soggettoOld.getCredenziali(0).isCertificateStrictVerification()) {
				oldPasswordCifrata = true;
			}
			boolean encryptEnabled = this.saCore.isSoggettiPasswordEncryptEnabled();
			if(this.credenzialiCheckData(tipoOp,oldPasswordCifrata, encryptEnabled, this.soggettiCore.getSoggettiPasswordVerifier())==false){
				return false;
			}

			String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			if (tipoauth == null) {
				tipoauth = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
			}
			String utente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			String password = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);

			String subject = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
			String issuer = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);
			if("".equals(issuer)) {
				issuer = null;
			}
			String principal = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
			
			// Se sono presenti credenziali, controllo che non siano gia'
			// utilizzate da altri soggetti
			if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC)) {
				// recupero soggetto con stesse credenziali
				boolean checkPassword = this.soggettiCore.isSoggettiCredenzialiBasicCheckUniqueUsePassword(); // la password non viene utilizzata per riconoscere se l'username e' già utilizzato.
				Soggetto soggettoAutenticato = this.soggettiCore.soggettoWithCredenzialiBasic(utente, password, checkPassword);
				
				if(soggettoAutenticato!=null && tipoOp.equals(TipoOperazione.CHANGE)){
					if(idInt == soggettoAutenticato.getId()){
						soggettoAutenticato = null;
					}
				}

				// Messaggio di errore
				if(soggettoAutenticato!=null){
					String labelSoggettoAutenticato = this.getLabelNomeSoggetto(new IDSoggetto(soggettoAutenticato.getTipo(), soggettoAutenticato.getNome()));
					this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già l'utente (http-basic) indicato");
					return false;
				}
				
				if(!this.soggettiCore.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials()) {
					// Verifico applicativi
					
					// recupera lista servizi applicativi con stesse credenziali
					boolean checkPasswordSA = this.saCore.isApplicativiCredenzialiBasicCheckUniqueUsePassword(); // la password non viene utilizzata per riconoscere se l'username e' già utilizzato.
					List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiBasicList(utente, password, checkPasswordSA);
					
					for (int i = 0; i < saList.size(); ) {
						ServizioApplicativo sa = saList.get(i);

						// Messaggio di errore
						String labelSoggettoGiaEsistente = this.getLabelNomeSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						if(sa.getTipo()!=null && StringUtils.isNotEmpty(sa.getTipo())) {
							this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggettoGiaEsistente+") possiede già l'utente (http-basic) indicato");
						}
						else {
							this.pd.setMessage("L'erogazione "+sa.getNome()+" possiede già l'utente (http-basic) indicato per il servizio '"+ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX+"'");
						}
						return false; // ne basta uno
					}
				}
				
			} 
			else if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY)) {
				// Univocita garantita dal meccanismo di generazione delle chiavi
				/*
				// Viene calcolato String appId = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
				String multipleApiKeys = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS);
							
				// recupero soggetto con stesse credenziali
				//Soggetto soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoBasic(utente, password);
				boolean multipleApiKeysEnabled = ServletUtils.isCheckBoxEnabled(multipleApiKeys);
				String appId = this.soggettiCore.toAppId(this.soggettiCore.getProtocolloAssociatoTipoSoggetto(ids.getTipo()), ids, multipleApiKeysEnabled);
				Soggetto soggettoAutenticato = this.soggettiCore.soggettoWithCredenzialiApiKey(appId, multipleApiKeysEnabled);
				
				if(soggettoAutenticato!=null && tipoOp.equals(TipoOperazione.CHANGE)){
					if(idInt == soggettoAutenticato.getId()){
						soggettoAutenticato = null;
					}
				}

				// Messaggio di errore
				if(soggettoAutenticato!=null){
					String labelSoggettoAutenticato = this.getLabelNomeSoggetto(new IDSoggetto(soggettoAutenticato.getTipo(), soggettoAutenticato.getNome()));
					String tipoCredenzialiApiKey = ServletUtils.isCheckBoxEnabled(multipleApiKeys) ? ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS_DESCR : ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY;
					this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già una credenziale '"+tipoCredenzialiApiKey+"' con identico '"+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID+"'");
					return false;
				}
				*/
			}
			else if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
				String tipoCredenzialiSSLSorgente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
				if(tipoCredenzialiSSLSorgente == null) {
					tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
				}
				String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
				if (tipoCredenzialiSSLConfigurazioneManualeSelfSigned == null) {
					tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_ENABLED;
				}
				
				String details = "";
				Soggetto soggettoAutenticato = null;
				String tipoSsl = null;
				Certificate cSelezionato = null;
				boolean strictVerifier = false;
				
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) { 
			
					// recupero soggetto con stesse credenziali
					soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoSsl(subject, issuer);
					tipoSsl = "subject/issuer";
			
				} else {
					
					BinaryParameter tipoCredenzialiSSLFileCertificato = this.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
					String tipoCredenzialiSSLVerificaTuttiICampi = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
					strictVerifier = ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi);
					String tipoCredenzialiSSLTipoArchivioS = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
					String tipoCredenzialiSSLFileCertificatoPassword = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
					String tipoCredenzialiSSLAliasCertificato = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
					if (tipoCredenzialiSSLAliasCertificato == null) {
						tipoCredenzialiSSLAliasCertificato = "";
					}
					org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio= null;
					if(tipoCredenzialiSSLTipoArchivioS == null) {
						tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
					} else {
						tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.valueOf(tipoCredenzialiSSLTipoArchivioS);
					}
					byte [] archivio = tipoCredenzialiSSLFileCertificato.getValue();
					if(TipoOperazione.CHANGE.equals(tipoOp) && archivio==null) {
						archivio = soggettoOld.getCredenziali(0).getCertificate();
					}
					if(tipoCredenzialiSSLTipoArchivio.equals(org.openspcoop2.utils.certificate.ArchiveType.CER)) {
						cSelezionato = ArchiveLoader.load(archivio);
					}else {
						cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, archivio, tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
					}
					//soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoSsl(cSelezionato.getCertificate(), strictVerifier);
					// Fix: usando il metodo sopra e' permesso caricare due soggetti con lo stesso certificato (anche serial number) uno in strict e uno no, e questo e' sbagliato.
					List<org.openspcoop2.core.registry.Soggetto> soggettiAutenticati = this.soggettiCore.soggettoWithCredenzialiSslList(cSelezionato.getCertificate(), strictVerifier);
					if(soggettiAutenticati!=null && soggettiAutenticati.size()>0) {
						soggettoAutenticato = soggettiAutenticati.get(0);
						if(!strictVerifier) {
							List<org.openspcoop2.core.registry.Soggetto> soggettiAutenticatiCheck = this.soggettiCore.soggettoWithCredenzialiSslList(cSelezionato.getCertificate(), true);
							if(soggettiAutenticatiCheck==null || soggettiAutenticatiCheck.isEmpty() ) {
								details=ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_DETAILS;
							}
						}
					}
					tipoSsl = "certificato";
				}
				
				if(soggettoAutenticato!=null && tipoOp.equals(TipoOperazione.CHANGE)){
					if(idInt == soggettoAutenticato.getId()){
						soggettoAutenticato = null;
					}
				}

				// Messaggio di errore
				if(soggettoAutenticato!=null){
					String labelSoggettoAutenticato = this.getLabelNomeSoggetto(new IDSoggetto(soggettoAutenticato.getTipo(), soggettoAutenticato.getNome()));
					this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già le credenziali ssl ("+tipoSsl+") indicate."+details);
					return false;
				}
				
				if(!this.soggettiCore.isSoggettiApplicativiCredenzialiSslPermitSameCredentials()) {
					// Verifico applicativi
					
					details = "";
					List<ServizioApplicativo> saList = null;
					tipoSsl = null;
					
					if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) { 
						saList = this.saCore.servizioApplicativoWithCredenzialiSslList(subject,issuer);
						tipoSsl = "subject/issuer";
					}
					else {
						
						saList = this.saCore.servizioApplicativoWithCredenzialiSslList(cSelezionato.getCertificate(), strictVerifier);
						if(!strictVerifier && saList!=null && saList.size()>0) {
							List<ServizioApplicativo> saListCheck = this.saCore.servizioApplicativoWithCredenzialiSslList(cSelezionato.getCertificate(), true);
							if(saListCheck==null || saListCheck.isEmpty() ) {
								details=ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_DETAILS;
							}
						}
						tipoSsl = "certificato";
						
					}
					
					if(saList!=null) {
						for (int i = 0; i < saList.size(); i++) {
							ServizioApplicativo sa = saList.get(i);
				
							boolean tokenWithHttpsEnabledByConfigSA = false;
							if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>0) {
								Credenziali c = sa.getInvocazionePorta().getCredenziali(0);
								if(c!=null && c.getTokenPolicy()!=null && StringUtils.isNotEmpty(c.getTokenPolicy())) {
									// se entro in questa servlet sono sicuramente con credenziale ssl, se esiste anche token policy abbiamo la combo
									tokenWithHttpsEnabledByConfigSA = true;
								}
							}
							
							if(!tokenWithHttpsEnabledByConfigSA) {  // se e' abilitato il token non deve essere controllata l'univocita' del certificato
							
								// Raccolgo informazioni soggetto
								// Messaggio di errore
								String labelSoggettoApplicativo = this.getLabelNomeSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
								this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggettoApplicativo+") possiede già le credenziali ssl ("+tipoSsl+") indicate."+details);
								return false; 
								
							}
						}
					}
				}
			}
			else if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL)) {
				// recupero soggetto con stesse credenziali
				Soggetto soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoPrincipal(principal);
				
				if(soggettoAutenticato!=null && tipoOp.equals(TipoOperazione.CHANGE)){
					if(idInt == soggettoAutenticato.getId()){
						soggettoAutenticato = null;
					}
				}

				// Messaggio di errore
				if(soggettoAutenticato!=null){
					String labelSoggettoAutenticato = this.getLabelNomeSoggetto(new IDSoggetto(soggettoAutenticato.getTipo(), soggettoAutenticato.getNome()));
					this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già il principal indicato");
					return false;
				}
				
				if(!this.soggettiCore.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials()) {
					// Verifico applicativi
					
					List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiPrincipalList(principal);

					for (int i = 0; i < saList.size(); ) {
						ServizioApplicativo sa = saList.get(i);

						// Messaggio di errore
						String labelSoggettoApplicativo = this.getLabelNomeSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggettoApplicativo+") possiede già il principal indicato");
						return false; // ne basta uno
					}
				}
			} 
			
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareSoggettiList(List<org.openspcoop2.core.registry.Soggetto> lista, ISearch ricerca) throws Exception {
		try {
			boolean modalitaCompleta = this.isModalitaCompleta(); 
			
			if(!modalitaCompleta) {
				this.pd.setCustomListViewName(SoggettiCostanti.SOGGETTI_NOME_VISTA_CUSTOM_LISTA);
			}
			
			ServletUtils.addListElementIntoSession(this.request, this.session, SoggettiCostanti.OBJECT_NAME_SOGGETTI);

			boolean multiTenant = this.core.isMultitenant();
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			int idLista = Liste.SOGGETTI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			String filterProtocollo = addFilterProtocol(ricerca, idLista, true);
			boolean profiloSelezionato = false;
			String protocolloSel = filterProtocollo;
			if(protocolloSel==null) {
				// significa che e' stato selezionato un protocollo nel menu in alto a destra
				List<String> protocolli = this.core.getProtocolli(this.request, this.session);
				if(protocolli!=null && protocolli.size()==1) {
					protocolloSel = protocolli.get(0);
				}
			}
			if( (filterProtocollo!=null && 
					//!"".equals(filterProtocollo) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo))
					||
				(filterProtocollo==null && protocolloSel!=null)
					) {
				profiloSelezionato = true;
			}
			
			if(this.core.isGestionePddAbilitata(this)==false && multiTenant) {
				String filterDominio = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_DOMINIO);
				addFilterDominio(filterDominio, false);
			}
			
			String filterTipoSoggetto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_SOGGETTO);
			this.addFilterTipoSoggetto(filterTipoSoggetto,false);
			
			String filterTipoCredenziali = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_CREDENZIALI);
			this.addFilterTipoCredenziali(filterTipoCredenziali,true,false);
			
			String filterCredenziale = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CREDENZIALE);
			this.addFilterCredenziale(filterTipoCredenziali, filterCredenziale);
			
			String filterCredenzialeIssuer = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CREDENZIALE_ISSUER);
			this.addFilterCredenzialeIssuer(filterTipoCredenziali, filterCredenzialeIssuer);
						
			String filterRuolo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO);
			addFilterRuolo(filterRuolo, false);
			
			String filterGruppo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
			addFilterGruppo(filterProtocollo, filterGruppo, true);
			
			String filterApiContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_CONTESTO);
			this.addFilterApiContesto(filterApiContesto, true);
			
			if(profiloSelezionato &&
					(filterApiContesto!=null && 
					//!"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto))
					&&
					!SoggettiCostanti.SOGGETTO_RUOLO_EROGATORE.equals(filterTipoSoggetto)
					&&
					!(SoggettiCostanti.SOGGETTO_RUOLO_FRUITORE.equals(filterTipoSoggetto) && TipoPdD.DELEGATA.getTipo().equals(filterApiContesto))) {
				String filterApiImplementazione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_IMPLEMENTAZIONE);
				this.addFilterApiImplementazione(filterProtocollo, null, filterGruppo, filterApiContesto, filterApiImplementazione, false);
			}
			else {
				SearchUtils.clearFilter(ricerca, idLista, Filtri.FILTRO_API_IMPLEMENTAZIONE);
			}
			
			// filtri proprieta
			String protocolloPerFiltroProprieta = protocolloSel;
			// valorizzato con il protocollo nel menu in alto a destra oppure null, controllo se e' stato selezionato nel filtro di ricerca
			if(protocolloPerFiltroProprieta == null) {
				if(
						//"".equals(filterProtocollo) || 
						CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo)) {
					protocolloPerFiltroProprieta = null;
				} else {
					protocolloPerFiltroProprieta = filterProtocollo;
				}
			}
			
			// **** filtro proprieta ****
			
			List<String> nomiProprieta = this.nomiProprietaSoggetti(protocolloPerFiltroProprieta); 
			if(nomiProprieta != null && !nomiProprieta.isEmpty()) {
				this.addFilterSubtitle(CostantiControlStation.NAME_SUBTITLE_PROPRIETA, CostantiControlStation.LABEL_SUBTITLE_PROPRIETA, false);
				
				// filtro nome
				this.addFilterProprietaNome(ricerca, idLista, nomiProprieta);
				
				// filtro valore
				this.addFilterProprietaValore(ricerca, idLista, nomiProprieta);
				
				// imposto apertura sezione
				this.impostaAperturaSubtitle(CostantiControlStation.NAME_SUBTITLE_PROPRIETA);
			}
			
			// **** fine filtro proprieta ****
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			this.pd.setSearchLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME);
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			boolean showProtocolli = this.core.countProtocolli(this.request, this.session)>1;

			setLabelColonne(modalitaCompleta, multiTenant, showProtocolli);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, SoggettiCostanti.LABEL_SOGGETTI, search);
			}

			// Prendo la lista di pdd dell'utente connesso
			/**List<String> nomiPdd = new ArrayList<>();
			List<PdDControlStation> listaPdd = this.core.pddList(superUser, new Search(true));
			Iterator<PdDControlStation> itP = listaPdd.iterator();
			while (itP.hasNext()) {
				PdDControlStation pds = (PdDControlStation) itP.next();
				nomiPdd.add(pds.getNome());
			}*/

			if(lista!=null) {
				Iterator<org.openspcoop2.core.registry.Soggetto> it = lista.listIterator();
				while (it.hasNext()) {
					List<DataElement> e = modalitaCompleta 
							? this.creaEntry(modalitaCompleta, multiTenant, contaListe, showProtocolli, it) 
									: this.creaEntryCustom(multiTenant, showProtocolli, it);
	
					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			if(lista!=null && !lista.isEmpty() &&
				this.core.isShowPulsantiImportExport()) {

				ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
				if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.SOGGETTO, this.request, this.session)){

					List<AreaBottoni> bottoni = new ArrayList<>();

					AreaBottoni ab = new AreaBottoni();
					List<DataElement> otherbott = new ArrayList<>();
					DataElement de = new DataElement();
					de.setValue(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI);
					de.setOnClick(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI_ONCLICK);
					de.setDisabilitaAjaxStatus();
					otherbott.add(de);
					ab.setBottoni(otherbott);
					bottoni.add(ab);

					this.pd.setAreaBottoni(bottoni);

				}

			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	private List<DataElement> creaEntry(boolean modalitaCompleta, boolean multiTenant, Boolean contaListe,
			boolean showProtocolli, Iterator<org.openspcoop2.core.registry.Soggetto> it)
			throws DriverControlStationException, DriverControlStationNotFound, DriverRegistroServiziNotFound,
			DriverRegistroServiziException, Exception, DriverConfigurazioneException, DriverConfigurazioneNotFound {
		org.openspcoop2.core.registry.Soggetto elem = it.next();

		PdDControlStation pdd = null;
		String nomePdD = elem.getPortaDominio();
		if (nomePdD!=null && (!nomePdD.equals("-")) )
			pdd = this.pddCore.getPdDControlStation(nomePdD);
		boolean pddEsterna = this.pddCore.isPddEsterna(nomePdD);

		List<DataElement> e = new ArrayList<>();

		String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(elem.getTipo());
							
		DataElement de = new DataElement();
		de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
				new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""),
				new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,elem.getNome()),
				new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,elem.getTipo()));
		de.setValue(this.getLabelNomeSoggetto(protocollo, elem.getTipo(), elem.getNome()));
		de.setIdToRemove(elem.getId().toString());
		de.setToolTip(de.getValue());
		de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		e.add(de);

		if(showProtocolli) {
			de = new DataElement();
			de.setValue(this.getLabelProtocollo(protocollo));
			e.add(de);
		}
		
		if(this.core.isGestionePddAbilitata(this)) {
			de = new DataElement();
			if (pdd != null && (!nomePdD.equals("-"))){
				if (!nomePdD.equals("-")){
					//if (nomiPdd.contains(nomePdD)) {
					if(this.core.isSinglePdD()){
						de.setUrl(PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_CHANGE,
								new Parameter(PddCostanti.PARAMETRO_PDD_ID,pdd.getId()+""),
								new Parameter(PddCostanti.PARAMETRO_PDD_NOME,pdd.getNome()));
					}else{
						de.setUrl(PddCostanti.SERVLET_NAME_PDD_CHANGE,
								new Parameter(PddCostanti.PARAMETRO_PDD_ID,pdd.getId()+""));
					}
					//}
				}
				de.setValue(nomePdD);
			}
			else{
				de.setValue("-");
			}
			e.add(de);
		}
		else if(multiTenant) {
			de = new DataElement();
			if(pddEsterna) {
				de.setValue(SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_LABEL);
			}else {
				de.setValue(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_LABEL);
			}
			e.add(de);
		}

		if(modalitaCompleta) {
			
			boolean showConnettore = this.core.isRegistroServiziLocale() &&
					(this.pddCore.isPddEsterna(nomePdD) || multiTenant );
			
			de = new DataElement();
			if(showConnettore){
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,elem.getNome()),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,elem.getTipo()));
				ServletUtils.setDataElementVisualizzaLabel(de);
			}
			else{
				de.setType(DataElementType.TEXT);
				de.setValue("-");
			}
			e.add(de);
		}

		
		// Ruoli
		de = new DataElement();
		de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_RUOLI_LIST,
				new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""));
		if (contaListe) {
			// BugFix OP-674
			//List<String> lista1 = this.soggettiCore.soggettiRuoliList(elem.getId(),new Search(true));
			ConsoleSearch searchForCount = new ConsoleSearch(true,1);
			this.soggettiCore.soggettiRuoliList(elem.getId(),searchForCount);
			//int numRuoli = lista1.size();
			int numRuoli = searchForCount.getNumEntries(Liste.SOGGETTI_RUOLI);
			ServletUtils.setDataElementVisualizzaLabel(de,(long)numRuoli);
		} else
			ServletUtils.setDataElementVisualizzaLabel(de);
		e.add(de);
		
		
		//Servizi Appicativi
		if(modalitaCompleta) {
			de = new DataElement();
			if (pddEsterna) {
				de.setType(DataElementType.TEXT);
				de.setValue("-");
			}
			else {
				de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,elem.getId()+""));
				if (contaListe) {
					// BugFix OP-674
					//List<ServizioApplicativo> lista1 = this.saCore.soggettiServizioApplicativoList(new Search(true), elem.getId());
					ConsoleSearch searchForCount = new ConsoleSearch(true,1);
					this.setFilterRuoloServizioApplicativo(searchForCount, Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO);
					this.saCore.soggettiServizioApplicativoList(searchForCount, elem.getId());
					//int numSA = lista1.size();
					int numSA = searchForCount.getNumEntries(Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO);
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numSA);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
			}
			e.add(de);
		}
		
		if(modalitaCompleta) {
			de = new DataElement();
			if (pddEsterna) {
				// se la pdd e' esterna non e' possibile
				// inseririre porte applicative
				de.setType(DataElementType.TEXT);
				de.setValue("-");
			} else {
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,elem.getId()+""),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_SOGGETTO,elem.getNome()),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_SOGGETTO,elem.getTipo()));
				if (contaListe) {
					// BugFix OP-674
					//List<PortaApplicativa> lista1 = this.porteApplicativeCore.porteAppList(elem.getId().intValue(), new Search(true));
					ConsoleSearch searchForCount = new ConsoleSearch(true,1);
					this.porteApplicativeCore.porteAppList(elem.getId().intValue(), searchForCount);
					//int numPA = lista1.size();
					int numPA = searchForCount.getNumEntries(Liste.PORTE_APPLICATIVE_BY_SOGGETTO);
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numPA);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);

			}
			e.add(de);

			de = new DataElement();
			if (pddEsterna) {
				// se la pdd e' esterna non e' possibile
				// inseririre porte delegate
				de.setType(DataElementType.TEXT);
				de.setValue("-");
			} else {
				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,elem.getId()+""),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_SOGGETTO,elem.getNome()),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SOGGETTO,elem.getTipo()));
				if (contaListe) {
					// BugFix OP-674
					//List<PortaDelegata> lista1 = this.porteDelegateCore.porteDelegateList(elem.getId().intValue(), new Search(true));
					ConsoleSearch searchForCount = new ConsoleSearch(true,1);
					this.porteDelegateCore.porteDelegateList(elem.getId().intValue(), searchForCount);
					//int numPD = lista1.size();
					int numPD = searchForCount.getNumEntries(Liste.PORTE_DELEGATE_BY_SOGGETTO);
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numPD);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
			}
			e.add(de);
		}
		return e;
	}
	private void setLabelColonne(boolean modalitaCompleta, boolean multiTenant, boolean showProtocolli) {
		
		if(!modalitaCompleta) {
			List<String> labels = new ArrayList<>();
			labels.add(SoggettiCostanti.LABEL_SOGGETTI);
			
			this.pd.setLabels(labels.toArray(new String[1]));
		} else {
			// setto le label delle colonne
			//int totEl = modalitaCompleta ? 4 : 2;
			int totEl = 4;
			if( showProtocolli ) {
				totEl++;
			}
			if(multiTenant || this.pddCore.isGestionePddAbilitata(this)) {
				// pdd o dominio
				totEl++;
			}
			if(modalitaCompleta) {
				totEl++; // connettore column
			}
			if(modalitaCompleta) {
				totEl++; // connettore servizi applicativi
			}
			String[] labels = new String[totEl];
			int i = 0;
			labels[i++] = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME;
			if( showProtocolli ) {
				labels[i++] = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO_COMPACT;
			}
			if(this.pddCore.isGestionePddAbilitata(this)) {
				labels[i++] = PddCostanti.LABEL_PORTA_DI_DOMINIO;
			}
			else if(multiTenant) {
				labels[i++] = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DOMINIO;
			}
			if(modalitaCompleta) {
				labels[i++] = ConnettoriCostanti.LABEL_CONNETTORE;
			}
			labels[i++] = RuoliCostanti.LABEL_RUOLI;
			if(modalitaCompleta) {
				labels[i++] = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			}
	//			else {
	//				labels[i++] = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
	//			}
			if(modalitaCompleta) {
				labels[i++] = PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE;
				labels[i++] = PorteDelegateCostanti.LABEL_PORTE_DELEGATE;
			}
			this.pd.setLabels(labels);
		}
	}
	
	private List<DataElement> creaEntryCustom(boolean multiTenant, boolean showProtocolli,
			Iterator<org.openspcoop2.core.registry.Soggetto> it)
			throws DriverRegistroServiziException, 
			DriverControlStationException, DriverControlStationNotFound, DriverConfigurazioneException {
		org.openspcoop2.core.registry.Soggetto elem = it.next();

		List<DataElement> e = new ArrayList<>();
		
		// Titolo (nome soggetto)
		String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(elem.getTipo());
		
		List<Parameter> listaParametriChange = new ArrayList<>();
		listaParametriChange.add(new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""));
		listaParametriChange.add(new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,elem.getNome()));
		listaParametriChange.add(new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,elem.getTipo()));
				
		DataElement de = new DataElement();
		de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, listaParametriChange.toArray(new Parameter[listaParametriChange.size()]));
		de.setValue(this.getLabelNomeSoggetto(protocollo, elem.getTipo(), elem.getNome()));
		de.setIdToRemove(elem.getId().toString());
		de.setToolTip(de.getValue());
		de.setType(DataElementType.TITLE);
		e.add(de);
		
		
		String nomePdD = elem.getPortaDominio();
		boolean pddEsterna = this.pddCore.isPddEsterna(nomePdD);

		// Metadati (profilo + dominio)
		if(showProtocolli || multiTenant) {
			boolean addMetadati = true;
			de = new DataElement();
			
			if(multiTenant) {
				String dominioLabel = "";
				if(pddEsterna) {
					dominioLabel = SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_LABEL;
				}else {
					dominioLabel = SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_LABEL;
				}
				
				
				if(showProtocolli) {
					String labelProtocollo =this.getLabelProtocollo(protocollo); 
					de.setValue(MessageFormat.format(SoggettiCostanti.MESSAGE_METADATI_SOGGETTO_CON_PROFILO, labelProtocollo, dominioLabel));
				} else {
					de.setValue(MessageFormat.format(SoggettiCostanti.MESSAGE_METADATI_SOGGETTO_SENZA_PROFILO, dominioLabel));
				}
			} else {
				String labelProtocollo =this.getLabelProtocollo(protocollo); 
				de.setValue(MessageFormat.format(SoggettiCostanti.MESSAGE_METADATI_SOGGETTO_SOLO_PROFILO, labelProtocollo));
			}
			
			de.setType(DataElementType.SUBTITLE);
			
			if(addMetadati)
				e.add(de);
		}

		// Ruoli
		List<String> listaRuoli = this.soggettiCore.soggettiRuoliList(elem.getId(),new ConsoleSearch(true));
		for (int j = 0; j < listaRuoli.size(); j++) {
			String ruolo = listaRuoli.get(j);
			
			de = new DataElement();
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO + "_" + j);
			de.setType(DataElementType.BUTTON);
			de.setLabel(ruolo);
			
/**					int indexOf = ruoliDisponibili.indexOf(ruolo);
//					if(indexOf == -1)
//						indexOf = 0;
//					
//					indexOf = indexOf % CostantiControlStation.NUMERO_GRUPPI_CSS;*/
			
			de.setStyleClass("ruolo-label-info-0"); /**+indexOf);*/
			
			de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_RUOLI_LIST,
					new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""));
			
			de.setToolTip(RuoliCostanti.LABEL_RUOLI); 
			
			e.add(de);
		}
		
		listaParametriChange.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA, "true"));
		listaParametriChange.add(new Parameter(CostantiControlStation.PARAMETRO_RESET_CACHE_FROM_LISTA, "true"));
		
		String labelSoggetto = this.getLabelNomeSoggetto(protocollo, elem.getTipo(), elem.getNome());
		
		// In Uso Button
		this.addInUsoButton(e, labelSoggetto, elem.getId()+"", InUsoType.SOGGETTO);
		
		if(this.core.isSoggettiVerificaCertificati()) {
			// Verifica certificati visualizzato solo se il soggetto ha credenziali https
			boolean ssl = false;
			for (int i = 0; i < elem.sizeCredenzialiList(); i++) {
				CredenzialiSoggetto c = elem.getCredenziali(i);
				if(org.openspcoop2.core.registry.constants.CredenzialeTipo.SSL.equals(c.getTipo())) { 
						//&& c.getCertificate()!=null) { non viene ritornato dalla lista
					ssl = true;
				}
			}
			if(ssl) {
				this.addVerificaCertificatiButton(e, SoggettiCostanti.SERVLET_NAME_SOGGETTI_VERIFICA_CERTIFICATI, listaParametriChange);
			}
		}
				
		// se e' abilitata l'opzione reset cache per elemento, visualizzo il comando nell'elenco dei comandi disponibili nella lista
		if(this.core.isElenchiVisualizzaComandoResetCacheSingoloElemento()){
			this.addComandoResetCacheButton(e,this.getLabelNomeSoggetto(protocollo, elem.getTipo(), elem.getNome()), SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, listaParametriChange);
		}
		
		// Proprieta Button
		/**if(this.existsProprietaOggetto(elem.getProprietaOggetto(), elem.getDescrizione())) {
		 * la lista non riporta le proprietà. Ma esistono e poi sarà la servlet a gestirlo
		 */
		this.addProprietaOggettoButton(e, labelSoggetto, elem.getId()+"", InUsoType.SOGGETTO);
		
		return e;
	}

	public void prepareSoggettiConfigList(List<org.openspcoop2.core.config.Soggetto> lista, ISearch ricerca) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.request, this.session, SoggettiCostanti.OBJECT_NAME_SOGGETTI);

			Boolean contaListeObject = ServletUtils.getContaListeFromSession(this.session);
			boolean contaListe = false;
			if(contaListeObject!=null) {
				contaListe = contaListeObject.booleanValue();
			}

			int idLista = Liste.SOGGETTI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			String filterProtocollo = addFilterProtocol(ricerca, idLista, true);
			String protocolloSel = filterProtocollo;
			if(protocolloSel==null) {
				// significa che e' stato selezionato un protocollo nel menu in alto a destra
				List<String> protocolli = this.core.getProtocolli(this.request, this.session);
				if(protocolli!=null && protocolli.size()==1) {
					protocolloSel = protocolli.get(0);
				}
			}
			
			// filtri proprieta
			String protocolloPerFiltroProprieta = protocolloSel;
			// valorizzato con il protocollo nel menu in alto a destra oppure null, controllo se e' stato selezionato nel filtro di ricerca
			if(protocolloPerFiltroProprieta == null) {
				if(
						//"".equals(filterProtocollo) || 
						CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo)) {
					protocolloPerFiltroProprieta = null;
				} else {
					protocolloPerFiltroProprieta = filterProtocollo;
				}
			}
			
			// **** filtro proprieta ****
			
			List<String> nomiProprieta = this.nomiProprietaSoggetti(protocolloPerFiltroProprieta); 
			if(nomiProprieta != null && !nomiProprieta.isEmpty()) {
				this.addFilterSubtitle(CostantiControlStation.NAME_SUBTITLE_PROPRIETA, CostantiControlStation.LABEL_SUBTITLE_PROPRIETA, false);
				
				// filtro nome
				this.addFilterProprietaNome(ricerca, idLista, nomiProprieta);
				
				// filtro valore
				this.addFilterProprietaValore(ricerca, idLista, nomiProprieta);
				
				// imposto apertura sezione
				this.impostaAperturaSubtitle(CostantiControlStation.NAME_SUBTITLE_PROPRIETA);
			}
			
			// **** fine filtro proprieta ****
						
						
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			boolean showProtocolli = this.core.countProtocolli(this.request, this.session)>1;
			
			// setto le label delle colonne
			int totEl = this.isModalitaCompleta() ? 3 : 1;
			if( showProtocolli ) {
				totEl++;
			}
			if(this.isModalitaCompleta()) {
				totEl++; // connettore servizi applicativi
			}
			String[] labels = new String[totEl];
			int i = 0;
			labels[i++] = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME;
			if( showProtocolli ) {
				labels[i++] = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO_COMPACT;
			}
			if(this.isModalitaCompleta()) {
				labels[i++] = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			}
/**			else {
//				labels[i++] = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
//			}*/
			if(this.isModalitaCompleta()) {
				labels[i++] = PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE;
				labels[i] = PorteDelegateCostanti.LABEL_PORTE_DELEGATE;
			}
			
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, SoggettiCostanti.LABEL_SOGGETTI, search);
			}

			if(lista!=null) {
				Iterator<org.openspcoop2.core.config.Soggetto> it = lista.listIterator();
				while (it.hasNext()) {
					org.openspcoop2.core.config.Soggetto elem = it.next();
	
					List<DataElement> e = new ArrayList<>();
					
					//Soggetto
					DataElement de = new DataElement();
					de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,elem.getNome()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,elem.getTipo()));
					de.setValue(elem.getTipo() + "/" + elem.getNome());
					de.setIdToRemove(elem.getId().toString());
					de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
					e.add(de);
	
					if(showProtocolli) {
						de = new DataElement();
						de.setValue(this.getLabelProtocollo(this.soggettiCore.getProtocolloAssociatoTipoSoggetto(elem.getTipo())));
						e.add(de);
					}
					
					//Servizi Appicativi
					if(this.isModalitaCompleta()) {
						de = new DataElement();
						de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,elem.getId()+""));
						if (contaListe) {
							// BugFix OP-674
							/**List<ServizioApplicativo> lista1 = this.saCore.soggettiServizioApplicativoList(new Search(true), elem.getId());*/
							ConsoleSearch searchForCount = new ConsoleSearch(true,1);
							this.setFilterRuoloServizioApplicativo(searchForCount, Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO);
							this.saCore.soggettiServizioApplicativoList(searchForCount, elem.getId());
							/**int numSA = lista1.size();*/
							int numSA = searchForCount.getNumEntries(Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO);
							ServletUtils.setDataElementVisualizzaLabel(de,(long)numSA);
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.add(de);
					}
					
					if(this.isModalitaCompleta()) {
						//Porte Applicative
						de = new DataElement();
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST,
								new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,elem.getId()+""),
								new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_SOGGETTO,elem.getNome()),
								new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_SOGGETTO,elem.getTipo()));
						if (contaListe) {
							// BugFix OP-674
							/** List<PortaApplicativa> lista1 = this.porteApplicativeCore.porteAppList(elem.getId().intValue(), new Search(true));*/
							ConsoleSearch searchForCount = new ConsoleSearch(true,1);
							this.porteApplicativeCore.porteAppList(elem.getId().intValue(), searchForCount);
							/**int numPA = lista1.size();*/
							int numPA = searchForCount.getNumEntries(Liste.PORTE_APPLICATIVE_BY_SOGGETTO);
							ServletUtils.setDataElementVisualizzaLabel(de,(long)numPA);
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.add(de);
		
						//Porte Delegate
						de = new DataElement();
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,elem.getId()+""),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_SOGGETTO,elem.getNome()),
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SOGGETTO,elem.getTipo()));
						if (contaListe) {
							// BugFix OP-674
							/**List<PortaDelegata> lista1 = this.porteDelegateCore.porteDelegateList(elem.getId().intValue(), new Search(true));*/
							ConsoleSearch searchForCount = new ConsoleSearch(true,1);
							this.porteDelegateCore.porteDelegateList(elem.getId().intValue(), searchForCount);
							/**int numPD = lista1.size();*/
							int numPD = searchForCount.getNumEntries(Liste.PORTE_DELEGATE_BY_SOGGETTO);
							ServletUtils.setDataElementVisualizzaLabel(de,(long)numPD);
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.add(de);
					}
	
	
					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			if(lista!=null && !lista.isEmpty()){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.SOGGETTO, this.request, this.session)){

						List<AreaBottoni> bottoni = new ArrayList<>();

						AreaBottoni ab = new AreaBottoni();
						List<DataElement> otherbott = new ArrayList<>();
						DataElement de = new DataElement();
						de.setValue(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI);
						de.setOnClick(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI_ONCLICK);
						de.setDisabilitaAjaxStatus();
						otherbott.add(de);
						ab.setBottoni(otherbott);
						bottoni.add(ab);

						this.pd.setAreaBottoni(bottoni);

					}

				}
			}

		} catch (Exception e) {
			this.log.error("Exception prepareSoggetti(Config)List: " + e.getMessage(), e);
			throw new Exception(e);
		}

	}



	public boolean soggettiEndPointCheckData(TipoOperazione tipoOp,List<ExtendedConnettore> listExtendedConnettore, String tipoSoggetto, String nomeSoggetto) throws Exception {
		try {
			String id = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID);
			int idInt = 0;
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				idInt = Integer.parseInt(id);
			}
			//String endpointtype = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			String endpointtype = this.readEndPointType();

			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggetto);
			if (!this.endPointCheckData(protocollo, false, listExtendedConnettore)) {
				return false;
			}

			// Se il connettore e' disabilitato devo controllare che i
			// connettore dei suoi servizi non siano disabilitati
			if (endpointtype.equals(TipiConnettore.DISABILITATO.toString())) {
				
				org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(tipoSoggetto, nomeSoggetto));
				if(this.pddCore.isPddEsterna(soggetto.getPortaDominio())){
				
					boolean trovatoServ = this.soggettiCore.existsSoggettoServiziWithoutConnettore(idInt);
					if (trovatoServ) {
						this.pd.setMessage("Il connettore deve essere specificato poichè alcuni servizi del soggetto non hanno un connettore definito");
						return false;
					}
					
				}
				else{
					
					boolean escludiSoggettiEsterni = true;
					boolean trovatoServ = this.soggettiCore.existFruizioniServiziSoggettoWithoutConnettore(idInt, escludiSoggettiEsterni);
					if (trovatoServ) {
						this.pd.setMessage("Il connettore deve essere specificato poichè alcune fruizioni dei servizi erogati dal soggetto non hanno un connettore definito");
						return false;
					}
					
				}
				
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	public void prepareRuoliList(ISearch ricerca, List<String> lista)
					throws Exception {
		try {
			String id = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			String accessDaChangeTmp = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_RUOLI_ACCESSO_DA_CHANGE);
			boolean accessDaChange = ServletUtils.isCheckBoxEnabled(accessDaChangeTmp);

			ServletUtils.addListElementIntoSession(this.request, this.session, SoggettiCostanti.OBJECT_NAME_SOGGETTI_RUOLI, 
					new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, id),
					new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_RUOLI_ACCESSO_DA_CHANGE, accessDaChangeTmp));

			int idLista = Liste.SOGGETTI_RUOLI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			Soggetto soggettoRegistry = this.soggettiCore.getSoggettoRegistro(Long.parseLong(id));
			String tmpTitle = this.getLabelNomeSoggetto(new IDSoggetto(soggettoRegistry.getTipo(),soggettoRegistry.getNome()));



			// setto la barra del titolo
			if(accessDaChange) {
				ServletUtils.setPageDataTitle_ServletFirst(this.pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST);
				ServletUtils.appendPageDataTitle(this.pd, 
						new Parameter(tmpTitle, 
								SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, 
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,soggettoRegistry.getId()+""),
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,soggettoRegistry.getNome()),
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,soggettoRegistry.getTipo())));
				ServletUtils.appendPageDataTitle(this.pd, 
						new Parameter(RuoliCostanti.LABEL_RUOLI, null));
			}
			else {
				ServletUtils.setPageDataTitle_ServletChange(this.pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, "Ruoli di " + tmpTitle);
			}


			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_RUOLO);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, RuoliCostanti.LABEL_RUOLI, search);
			}

			// setto le label delle colonne
			String[] labels = { 
					CostantiControlStation.LABEL_PARAMETRO_RUOLO
			};
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<String> it = lista.iterator();
				while (it.hasNext()) {
					String ruolo = it.next();

					List<DataElement> e = new ArrayList<>();

					DataElement de = new DataElement();
					de.setValue(ruolo);
					de.setIdToRemove(ruolo);
					
					if(!this.isModalitaCompleta()) {
						Ruolo ruoloObj = this.ruoliCore.getRuolo(ruolo);
						Parameter pIdRuolo = new Parameter(RuoliCostanti.PARAMETRO_RUOLO_ID, ruoloObj.getId()+"");
						
						String url = new Parameter("", RuoliCostanti.SERVLET_NAME_RUOLI_CHANGE , pIdRuolo).getValue();
						String tooltip = ruolo;
						
						this.newDataElementVisualizzaInNuovoTab(de, url, tooltip);
					}
					
					e.add(de);

					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	
	private void addFilterTipoSoggetto(String tipoSoggetto, boolean postBack) throws Exception{
		try {
			String [] tmpLabels = SoggettiCostanti.getLabelsSoggettoRuoloTipo();
			String [] tmpValues = SoggettiCostanti.getValuesSoggettoRuoloTipo();
			
			String [] values = new String[tmpValues.length + 1];
			String [] labels = new String[tmpLabels.length + 1];
			labels[0] = SoggettiCostanti.LABEL_PARAMETRO_FILTRO_SOGGETTO_TIPO_QUALSIASI;
			values[0] = SoggettiCostanti.DEFAULT_VALUE_PARAMETRO_FILTRO_SOGGETTO_TIPO_QUALSIASI;
			for (int i =0; i < tmpLabels.length ; i ++) {
				labels[i+1] = tmpLabels[i];
				values[i+1] = tmpValues[i];
			}
			
			String selectedValue = tipoSoggetto != null ? tipoSoggetto : SoggettiCostanti.DEFAULT_VALUE_PARAMETRO_FILTRO_SOGGETTO_TIPO_QUALSIASI;
			
			String label = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPO;

			this.pd.addFilter(Filtri.FILTRO_TIPO_SOGGETTO, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void prepareSoggettiCredenzialiList(Soggetto soggettoRegistry, String id) throws Exception{
		try {
			List<Parameter> parametersServletSoggettoChange = new ArrayList<>();
			Parameter pIdSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, id);
			Parameter pNomeSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, soggettoRegistry.getNome());
			Parameter pTipoSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, soggettoRegistry.getTipo());
			parametersServletSoggettoChange.add(pIdSoggetto);
			parametersServletSoggettoChange.add(pNomeSoggetto);
			parametersServletSoggettoChange.add(pTipoSoggetto);
			
			ServletUtils.addListElementIntoSession(this.request, this.session, SoggettiCostanti.OBJECT_NAME_SOGGETTI_CREDENZIALI, parametersServletSoggettoChange.toArray(new Parameter[parametersServletSoggettoChange.size()]));

			this.pd.setIndex(0);
			this.pd.setPageSize(soggettoRegistry.sizeCredenzialiList());
			this.pd.setNumEntries(soggettoRegistry.sizeCredenzialiList());
			
			// ricerca disattivata
			ServletUtils.disabledPageDataSearch(this.pd); 
			
			// setto la barra del titolo
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggettoRegistry.getTipo());
			
			ServletUtils.setPageDataTitle(this.pd, 
					new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
					new Parameter(this.getLabelNomeSoggetto(protocollo, soggettoRegistry.getTipo() , soggettoRegistry.getNome()),
					SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, parametersServletSoggettoChange.toArray(new Parameter[parametersServletSoggettoChange.size()])),
					new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, null)
					);
			
			// setto le label delle colonne
			List<String> labels = new ArrayList<>();
			
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_PRINCIPALE);
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT);
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER);
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE);
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER);
			
			this.pd.setLabels(labels.toArray(new String[1]));

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();
			
			List<CredenzialiSoggetto> lista = soggettoRegistry.getCredenzialiList();

			Iterator<CredenzialiSoggetto> it = lista.listIterator(); 
			int i = 0;
			while (it.hasNext()) {
				
				List<DataElement> e = new ArrayList<>();
				CredenzialiSoggetto credenziali = it.next();
				Certificate cSelezionato = ArchiveLoader.load(credenziali.getCertificate());
				String tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
				String tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
				boolean verificaTuttiCampi = credenziali.getCertificateStrictVerification();
				Date notBefore = cSelezionato.getCertificate().getNotBefore();
				String tipoCredenzialiSSLAliasCertificatoNotBefore = this.getSdfCredenziali().format(notBefore);
				Date notAfter = cSelezionato.getCertificate().getNotAfter();
				String tipoCredenzialiSSLAliasCertificatoNotAfter = this.getSdfCredenziali().format(notAfter);
				
				Parameter pIdCredenziale = new Parameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CREDENZIALI_ID, i+"");
				List<Parameter> parametersServletCredenzialeChange = new ArrayList<>();
				parametersServletCredenzialeChange.add(pIdCredenziale);
				parametersServletCredenzialeChange.addAll(parametersServletSoggettoChange);
				parametersServletCredenzialeChange.add(new Parameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_MULTI_AGGIORNA,Costanti.CHECK_BOX_ENABLED));
				
				//  Principale: si/no
				DataElement de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setValue(i == 0 ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO);
				de.allineaTdAlCentro();
				de.setWidthPx(60);
				e.add(de);
				
				// Subject con link edit
				de = new DataElement();
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CREDENZIALI_CHANGE, 
						parametersServletCredenzialeChange.toArray(new Parameter[parametersServletCredenzialeChange.size()]));
				de.setSize(ConnettoriCostanti.NUMERO_CARATTERI_SUBJECT_DA_VISUALIZZARE_IN_LISTA_CERTIFICATI);
				de.setValue(StringEscapeUtils.escapeHtml(tipoCredenzialiSSLAliasCertificatoSubject));
				de.setToolTip(StringEscapeUtils.escapeHtml(tipoCredenzialiSSLAliasCertificatoSubject)); 
				de.setIdToRemove(i+"");
				e.add(de);
				
				// Issuer 
				de = new DataElement();
				String issuerValue = StringEscapeUtils.escapeHtml(tipoCredenzialiSSLAliasCertificatoIssuer);
				if(issuerValue.length() > ConnettoriCostanti.NUMERO_CARATTERI_SUBJECT_DA_VISUALIZZARE_IN_LISTA_CERTIFICATI) {
					issuerValue = issuerValue.substring(0,(ConnettoriCostanti.NUMERO_CARATTERI_SUBJECT_DA_VISUALIZZARE_IN_LISTA_CERTIFICATI-3)) + "...";
				}
				de.setValue(issuerValue);
				de.setToolTip(StringEscapeUtils.escapeHtml(tipoCredenzialiSSLAliasCertificatoIssuer)); 
				e.add(de);
				
				// verifica tutti i campi
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setValue(verificaTuttiCampi ? ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI_ENABLE : 
					ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI_DISABLE);
				de.allineaTdAlCentro();
				de.setWidthPx(70);
				e.add(de);
				
				// not before
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				if(verificaTuttiCampi) {
					de.setValue(tipoCredenzialiSSLAliasCertificatoNotBefore);
				}
				else {
					de.setValue("-");
				}
				de.allineaTdAlCentro();
				de.setWidthPx(140);
				if(notBefore.after(new Date())) {
					// bold
					de.setLabelStyleClass(Costanti.INPUT_TEXT_BOLD_CSS_CLASS);
					de.setWidthPx(150);
				}
				e.add(de);	
				
				// not after
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				if(verificaTuttiCampi) {
					de.setValue(tipoCredenzialiSSLAliasCertificatoNotAfter);
				}else {
					de.setValue("-");
				}
				de.allineaTdAlCentro();
				de.setWidthPx(140);
				if(notAfter.before(new Date())) {
					// bold e rosso
					de.setLabelStyleClass(Costanti.INPUT_TEXT_BOLD_RED_CSS_CLASS);
					de.setWidthPx(150);
				}
				e.add(de);

				dati.add(e);
				i++;
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public List<DataElement> addSoggettoHiddenToDati(List<DataElement> dati, String id, String nome, String tipo) throws Exception {
		
		DataElement de = new DataElement();
		de.setLabel(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
		de.setValue(id);
		de.setType(DataElementType.HIDDEN);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
		dati.add(de);
		
		if(nome != null) {
			de = new DataElement();
			de.setLabel(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			de.setValue(nome);
			de.setType(DataElementType.HIDDEN);
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			dati.add(de);
		}
		
		if(tipo != null) {
			de = new DataElement();
			de.setLabel(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			de.setValue(tipo);
			de.setType(DataElementType.HIDDEN);
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			dati.add(de);
		}
		
		return dati;
	}
	
	public boolean soggettiCredenzialiCertificatiCheckData(TipoOperazione tipoOp, String id, Soggetto soggettoOld, int idxCertificato) throws Exception {
		try {
			int idInt = 0;
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				idInt = Integer.parseInt(id);
			}

//			boolean oldPasswordCifrata = false;
//			if(soggettoOld!=null && soggettoOld.sizeCredenzialiList()>0 && soggettoOld.getCredenziali(0).isCertificateStrictVerification()) {
//				oldPasswordCifrata = true;
//			}
//			boolean encryptEnabled = this.saCore.isSoggettiPasswordEncryptEnabled();
//			if(this.credenzialiCheckData(tipoOp,oldPasswordCifrata, encryptEnabled, this.soggettiCore.getSoggettiPasswordVerifier())==false){
//				return false;
//			}

			String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			if (tipoauth == null) {
				tipoauth = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
			}
			
			String subject = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
			String issuer = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);
			if("".equals(issuer)) {
				issuer = null;
			}
			
			String tipoCredenzialiSSLSorgente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
			if(tipoCredenzialiSSLSorgente == null) {
				tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
			}
			String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
			if (tipoCredenzialiSSLConfigurazioneManualeSelfSigned == null) {
				tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_ENABLED;
			}
			
			String details = "";
			Soggetto soggettoAutenticato = null;
			String tipoSsl = null;
			Certificate cSelezionato = null;
			boolean strictVerifier = false;
			List<org.openspcoop2.core.registry.Soggetto> soggettiAutenticati = null;
			if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) { 
		
				// recupero soggetto con stesse credenziali
				soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoSsl(subject, issuer);
				tipoSsl = "subject/issuer";
		
			} else {
				
				BinaryParameter tipoCredenzialiSSLFileCertificato = this.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
				String tipoCredenzialiSSLVerificaTuttiICampi = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
				strictVerifier = ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi);
				String tipoCredenzialiSSLTipoArchivioS = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
				String tipoCredenzialiSSLFileCertificatoPassword = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
				String tipoCredenzialiSSLAliasCertificato = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
				if (tipoCredenzialiSSLAliasCertificato == null) {
					tipoCredenzialiSSLAliasCertificato = "";
				}
				org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio= null;
				if(tipoCredenzialiSSLTipoArchivioS == null) {
					tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
				} else {
					tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.valueOf(tipoCredenzialiSSLTipoArchivioS);
				}
				byte [] archivio = tipoCredenzialiSSLFileCertificato.getValue();
				if(TipoOperazione.CHANGE.equals(tipoOp) && archivio==null) {
					archivio = soggettoOld.getCredenziali(idxCertificato).getCertificate();
				}
				if(tipoCredenzialiSSLTipoArchivio.equals(org.openspcoop2.utils.certificate.ArchiveType.CER)) {
					cSelezionato = ArchiveLoader.load(archivio);
				}else {
					cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, archivio, tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
				}
				//soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoSsl(cSelezionato.getCertificate(), strictVerifier);
				// Fix: usando il metodo sopra e' permesso caricare due soggetti con lo stesso certificato (anche serial number) uno in strict e uno no, e questo e' sbagliato.
				soggettiAutenticati = this.soggettiCore.soggettoWithCredenzialiSslList(cSelezionato.getCertificate(), strictVerifier);
				if(soggettiAutenticati!=null && soggettiAutenticati.size()>0) {
					soggettoAutenticato = soggettiAutenticati.get(0);
					if(!strictVerifier) {
						List<org.openspcoop2.core.registry.Soggetto> soggettiAutenticatiCheck = this.soggettiCore.soggettoWithCredenzialiSslList(cSelezionato.getCertificate(), true);
						if(soggettiAutenticatiCheck==null || soggettiAutenticatiCheck.isEmpty() ) {
							details=ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_DETAILS;
						}
					}
				}
				tipoSsl = "certificato";
			}
			
			if(soggettoAutenticato!=null && tipoOp.equals(TipoOperazione.CHANGE)){
				if(idInt == soggettoAutenticato.getId()){
					soggettoAutenticato = null;
				}
			}

			// Messaggio di errore
			if(soggettoAutenticato!=null){
				String labelSoggettoAutenticato = this.getLabelNomeSoggetto(new IDSoggetto(soggettoAutenticato.getTipo(), soggettoAutenticato.getNome()));
				this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già le credenziali ssl ("+tipoSsl+") indicate."+details);
				return false;
			}
			
			
			if(!this.soggettiCore.isSoggettiApplicativiCredenzialiSslPermitSameCredentials()) {
				// Verifico applicativi
			
				details = "";
				List<ServizioApplicativo> saList = null;
				tipoSsl = null;
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) {
					saList = this.saCore.servizioApplicativoWithCredenzialiSslList(subject,issuer);
					tipoSsl = "subject/issuer";
				}
				else {
					
					saList = this.saCore.servizioApplicativoWithCredenzialiSslList(cSelezionato.getCertificate(), strictVerifier);
					if(!strictVerifier && saList!=null && saList.size()>0) {
						List<ServizioApplicativo> saListCheck = this.saCore.servizioApplicativoWithCredenzialiSslList(cSelezionato.getCertificate(), true);
						if(saListCheck==null || saListCheck.isEmpty() ) {
							details=ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_DETAILS;
						}
					}
					tipoSsl = "certificato";
					
				}
				
				if(saList!=null) {
					for (int i = 0; i < saList.size(); i++) {
						ServizioApplicativo sa = saList.get(i);
		
						boolean tokenWithHttpsEnabledByConfigSA = false;
						if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>0) {
							Credenziali c = sa.getInvocazionePorta().getCredenziali(0);
							if(c!=null && c.getTokenPolicy()!=null && StringUtils.isNotEmpty(c.getTokenPolicy())) {
								// se entro in questa servlet sono sicuramente con credenziale ssl, se esiste anche token policy abbiamo la combo
								tokenWithHttpsEnabledByConfigSA = true;
							}
						}
						
						if(!tokenWithHttpsEnabledByConfigSA) {  // se e' abilitato il token non deve essere controllata l'univocita' del certificato
							// Raccolgo informazioni soggetto
							// Messaggio di errore
							String labelSoggetto = this.getLabelNomeSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
							this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già le credenziali ssl ("+tipoSsl+") indicate."+details);
							return false;	
						}
						
					}
				}
			}
			
			
			
			// Verifico che non sia già associato al soggetto
			
			String actionConfirm = this.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
			boolean promuoviInCorso = false;
			if(actionConfirm != null && actionConfirm.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK)){
				promuoviInCorso = true;
			}
			
			String aggiornatoCertificatoPrecaricatoTmp = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_MULTI_AGGIORNA);
			boolean aggiornatoCertificatoPrecaricato = ServletUtils.isCheckBoxEnabled(aggiornatoCertificatoPrecaricatoTmp);
			
			if(!promuoviInCorso && !aggiornatoCertificatoPrecaricato && cSelezionato!=null && soggettiAutenticati!=null && soggettiAutenticati.size()>0) {
				// dovrebbe essere 1 grazie al precedente controllo
				org.openspcoop2.core.registry.Soggetto soggettoCheck = soggettiAutenticati.get(0);
				if(soggettoCheck.sizeCredenzialiList()>0) {
					for (CredenzialiSoggetto c : soggettoCheck.getCredenzialiList()) {
						Certificate check = ArchiveLoader.load(c.getCertificate());
						if(check.getCertificate().equals(cSelezionato.getCertificate())) {
							this.pd.setMessage("Il certificato selezionato risulta già associato al soggetto");
							return false;
						}
					}
				}
			}
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	public void prepareSoggettiProprietaList(Soggetto soggettoRegistry, String id, ConsoleSearch ricerca,	List<Proprieta> lista) throws Exception {
		try {
			List<Parameter> parametersServletSoggettoChange = new ArrayList<>();
			Parameter pIdSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, id);
			Parameter pNomeSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, soggettoRegistry.getNome());
			Parameter pTipoSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, soggettoRegistry.getTipo());
			parametersServletSoggettoChange.add(pIdSoggetto);
			parametersServletSoggettoChange.add(pNomeSoggetto);
			parametersServletSoggettoChange.add(pTipoSoggetto);
			
			ServletUtils.addListElementIntoSession(this.request, this.session, SoggettiCostanti.OBJECT_NAME_SOGGETTI_PROPRIETA, parametersServletSoggettoChange.toArray(new Parameter[parametersServletSoggettoChange.size()]));

			// setto la barra del titolo
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggettoRegistry.getTipo());
			
			int idLista = Liste.SOGGETTI_PROP;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();
			lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			lstParam.add(new Parameter(this.getLabelNomeSoggetto(protocollo, soggettoRegistry.getTipo() , soggettoRegistry.getNome()),
					SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, parametersServletSoggettoChange.toArray(new Parameter[parametersServletSoggettoChange.size()])));

			this.pd.setSearchLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROP_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROPRIETA, null));
			}else{
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROPRIETA,
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_PROPRIETA_LIST, parametersServletSoggettoChange.toArray(new Parameter[parametersServletSoggettoChange.size()])));
				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROPRIETA, search);
			}

			// setto le label delle colonne
			String valueLabel = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROP_VALORE;
			String[] labels = { SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROP_NOME, valueLabel };
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<Proprieta> it = lista.iterator();
				while (it.hasNext()) {
					Proprieta ssp = it.next();

					List<DataElement> e = new ArrayList<>();

					Parameter pNomeProprieta = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTI_PROP_NOME, ssp.getNome());
					List<Parameter> parametersServletProprietaChange = new ArrayList<>();
					parametersServletProprietaChange.add(pNomeProprieta);
					parametersServletProprietaChange.addAll(parametersServletSoggettoChange);
				
					DataElement de = new DataElement();
					de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_PROPRIETA_CHANGE, 
							parametersServletProprietaChange.toArray(new Parameter[parametersServletProprietaChange.size()]));
					de.setValue(ssp.getNome());
					de.setIdToRemove(ssp.getNome());
					e.add(de);

					de = new DataElement();
					if(ssp.getValore()!=null)
						de.setValue(ssp.getValore().toString());
					e.add(de);

					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
		
	}
	
	public  List<DataElement> addProprietaToDati(TipoOperazione tipoOp, int size, String nome, String valore, List<DataElement> dati) {

		DataElement de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROPRIETA);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROP_NOME);
		de.setValue(nome);
		if(TipoOperazione.ADD.equals(tipoOp)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTI_PROP_NOME);
		de.setSize(size);
		dati.add(de);

		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_VALORE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTI_PROP_VALORE);
		de.setValue(valore);
		de.setSize(size);
		dati.add(de);

		return dati;
	}
	
	public boolean soggettiProprietaCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			String id = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			int idSogg = Integer.parseInt(id);
			String nome = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTI_PROP_NOME);
			String valore = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTI_PROP_VALORE);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROP_NOME;
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROP_VALORE;
					} else {
						tmpElenco = tmpElenco + ", " + SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROP_VALORE;
					}
				}
				this.pd.setMessage(MessageFormat.format(SoggettiCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
				return false;
			}
			
			// Check Lunghezza
			if(this.checkLength255(nome, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROP_NOME)==false) {
				return false;
			}
			if(this.checkLength4000(valore, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTI_PROP_VALORE)==false) {
				return false;
			}

			// Se tipoOp = add, controllo che la property non sia gia'
			// stata
			// registrata per l'applicativo
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				
				Soggetto soggettoRegistry = this.soggettiCore.getSoggettoRegistro(idSogg);
				String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggettoRegistry.getTipo());
				String nomeporta = this.getLabelNomeSoggetto(protocollo, soggettoRegistry.getTipo() , soggettoRegistry.getNome());

				for (int i = 0; i < soggettoRegistry.sizeProprietaList(); i++) {
					Proprieta tmpProp = soggettoRegistry.getProprieta(i);
					if (nome.equals(tmpProp.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage(MessageFormat.format(
							SoggettiCostanti.MESSAGGIO_ERRORE_LA_PROPRIETA_XX_E_GIA_STATO_ASSOCIATA_AL_SA_YY, nome,
							nomeporta));
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

}
