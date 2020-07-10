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
package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
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

	public Vector<DataElement> addSoggettiToDati(TipoOperazione tipoOp,Vector<DataElement> dati, String nomeprov, String tipoprov, String portadom, String descr, 
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
			String multipleApiKey, String appId, String apiKey) throws Exception  {
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
				multipleApiKey, appId, apiKey);
	}
	public Vector<DataElement> addSoggettiToDati(TipoOperazione tipoOp,Vector<DataElement> dati, String nomeprov, String tipoprov, String portadom, String descr, 
			boolean isRouter, List<String> tipiSoggetti, String profilo, boolean privato, String codiceIpa, List<String> versioni, 
			boolean isSupportatoCodiceIPA, boolean isSupportatoIdentificativoPorta,
			String [] pddList,String[] pddEsterneList,String nomePddGestioneLocale,String pdd, 
			String id, String oldnomeprov, String oldtipoprov, org.openspcoop2.core.registry.Connettore connettore,
			long numPD,String pd_url_prefix_rewriter,long numPA, String pa_url_prefix_rewriter, List<String> listaTipiProtocollo, String protocollo,
			boolean isSupportatoAutenticazioneSoggetti, String utente,String password, String subject, String principal, String tipoauth,
			boolean isPddEsterna,String tipologia, String dominio,String tipoCredenzialiSSLSorgente, org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio, BinaryParameter tipoCredenzialiSSLFileCertificato, String tipoCredenzialiSSLFileCertificatoPassword,
			List<String> listaAliasEstrattiCertificato, String tipoCredenzialiSSLAliasCertificato,	String tipoCredenzialiSSLAliasCertificatoSubject, String tipoCredenzialiSSLAliasCertificatoIssuer,
			String tipoCredenzialiSSLAliasCertificatoType, String tipoCredenzialiSSLAliasCertificatoVersion, String tipoCredenzialiSSLAliasCertificatoSerialNumber, String tipoCredenzialiSSLAliasCertificatoSelfSigned,
			String tipoCredenzialiSSLAliasCertificatoNotBefore, String tipoCredenzialiSSLAliasCertificatoNotAfter, String tipoCredenzialiSSLVerificaTuttiICampi, String tipoCredenzialiSSLConfigurazioneManualeSelfSigned,
			String issuer,String tipoCredenzialiSSLStatoElaborazioneCertificato,
			String changepwd,
			String multipleApiKey, String appId, String apiKey) throws Exception {

		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
		
		if(TipoOperazione.CHANGE.equals(tipoOp)){
			DataElement de = new DataElement();
			de.setLabel(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			dati.addElement(de);
		}

		
		DataElement de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_SOGGETTO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		boolean gestionePdd = true;
		if(this.core.isSinglePdD()){
			if(this.core.isGestionePddAbilitata(this)==false) {
				gestionePdd = false;
			}
		}
		
		boolean multiTenant = this.core.isMultitenant();
		boolean hiddenDatiDominioInterno = false;
		if(!multiTenant) {
			if(!gestionePdd) {
				hiddenDatiDominioInterno = SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(dominio);
			}
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
						if(pdd==null || "".equals(pdd)){
							if(nomePddGestioneLocale!=null){
								de.setSelected(nomePddGestioneLocale);
							}
						}
					}else{
						de.setRequired(true);
					}
					dati.addElement(de);
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
						dati.addElement(de);
					}
				}else{
					de = new DataElement();
					de.setLabel(PddCostanti.LABEL_PORTA_DI_DOMINIO);
					de.setType(DataElementType.TEXT);
					de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
					de.setValue(pdd);
					dati.addElement(de);
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
				dati.addElement(de);
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DOMINIO);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO+"__LABEL");
				String valueDom = dominio;
				for (int i = 0; i < SoggettiCostanti.SOGGETTI_DOMINI_VALUE.length; i++) {
					if(SoggettiCostanti.SOGGETTI_DOMINI_VALUE[i].equals(dominio)) {
						valueDom = SoggettiCostanti.SOGGETTI_DOMINI_LABEL[i];
						break;
					}
				}
				de.setValue(valueDom);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(SoggettiCostanti.SOGGETTI_DOMINI_VALUE);
				de.setLabels(SoggettiCostanti.SOGGETTI_DOMINI_LABEL);
				de.setSelected(dominio);
				de.setPostBack(isSupportatoAutenticazioneSoggetti);
			}
			dati.addElement(de);
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
				deLABEL.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO+"__label");
				deLABEL.setValue(this.getLabelProtocollo(protocollo));
				dati.addElement(deLABEL);
				
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
		dati.addElement(de);

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
			//de.setType(DataElementType.TEXT);
			de.setType(DataElementType.HIDDEN);
			if( (tipoprov==null || "".equals(tipoprov)) && tipiLabel!=null && tipiLabel.length>0) {
				tipoprov = tipiLabel[0];
			}
			else if(tipoprov!=null && tipiLabel!=null && tipiLabel.length>0 && !tipoprov.equals(tipiLabel[0])) {
				tipoprov = tipiLabel[0]; // fix per cambio protocollo
			}
			de.setValue(tipoprov);
		}
		de.setSize(this.getSize());
		//		de.setOnChange("CambiaDatiSoggetto('" + tipoOp + "')");
		de.setPostBack(true);
		dati.addElement(de);
		//}

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME);
		de.setValue(nomeprov);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);
		
		if(TipoOperazione.ADD.equals(tipoOp)){
			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPOLOGIA);
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPOLOGIA);
			if(isPddEsterna && isSupportatoAutenticazioneSoggetti){
				de.setValue(tipologia);
				de.setSelected(tipologia);
				de.setType(DataElementType.SELECT);
				de.setValues(SoggettiCostanti.SOGGETTI_RUOLI);
				de.setPostBack(true);
			}
			else{
				de.setValue("");
				de.setType(DataElementType.HIDDEN);
			}
			de.setSize(this.getSize());
			dati.addElement(de);
		}
		

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_CODICE_PORTA);
		de.setValue(portadom);
		if (!isSupportatoIdentificativoPorta) {
			de.setType(DataElementType.HIDDEN);
		}else{
//			if (this.isModalitaStandard()) {
//				de.setType(DataElementType.HIDDEN);
//			}else{
			de.setType(DataElementType.TEXT_EDIT);
			//}
		}
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_PORTA);
		de.setSize(this.getSize());
		dati.addElement(de);

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
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DESCRIZIONE);
		de.setValue(descr);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_DESCRIZIONE);
		de.setSize(this.getSize());
		dati.addElement(de);


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
		dati.addElement(de);
		//}

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
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_IS_ROUTER);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_ROUTER);
		//if (!this.core.isSinglePdD() && !InterfaceType.STANDARD.equals(user.getInterfaceType())) {
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
		dati.addElement(de);


		if(TipoOperazione.CHANGE.equals(tipoOp)){

//			boolean showConnettore = !this.isModalitaStandard() && 
//					this.core.isRegistroServiziLocale() &&
//					(this.isModalitaCompleta() || this.pddCore.isPddEsterna(pdd) || multiTenant );
			boolean showConnettore = this.core.isRegistroServiziLocale() && this.isModalitaCompleta();
				
//			if(!showConnettore) {
//				// guardo se fosse previsto un connettore static
//				boolean connettoreStatic = this.apsCore.isConnettoreStatic(protocollo);
//				if(connettoreStatic) {
//					showConnettore = true; // e' l'unico modo di indicare un connettore da utilizzare come info di registro (es. nel pmode per eDelivery)
//				}
//			}
			
			if(showConnettore){
				
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,id),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,oldnomeprov),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,oldtipoprov));
				Utilities.setDataElementLabelTipoConnettore(de, connettore);
				dati.addElement(de);
				
			}
		
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
					autenticazioneNessunaAbilitata = false;
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
						oldtipoprov!=null && !"".equals(oldtipoprov) && 
						oldnomeprov!=null && !"".equals(oldnomeprov)) {
					IDSoggetto idSoggetto = new IDSoggetto(oldtipoprov, oldnomeprov);
					Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(idSoggetto);

					// prendo il primo
					org.openspcoop2.core.registry.constants.CredenzialeTipo tipo = null;
					if(soggetto.getCredenziali()!=null) {
						tipo = soggetto.getCredenziali().getTipo();
					}
					if(tipo!=null) {
						oldtipoauth = tipo.getValue();
					}
					else {
						oldtipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
					}
				}
				
				dati = this.addCredenzialiToDati(dati, tipoauth, oldtipoauth, utente, password, subject, principal, servlet, true, null, false, true, null, autenticazioneNessunaAbilitata,
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuer,tipoCredenzialiSSLStatoElaborazioneCertificato,
						changepwd,
						multipleApiKey, appId, apiKey);
			}
		}
		
		if(TipoOperazione.CHANGE.equals(tipoOp)){

			de = new DataElement();
			de.setLabel(RuoliCostanti.LABEL_RUOLI);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
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
				//List<String> lista1 = this.soggettiCore.soggettiRuoliList(Long.parseLong(id),new Search(true));
				Search searchForCount = new Search(true,1);
				this.soggettiCore.soggettiRuoliList(Long.parseLong(id),searchForCount);
				//int numRuoli = lista1.size();
				int numRuoli = searchForCount.getNumEntries(Liste.SOGGETTI_RUOLI);
				ServletUtils.setDataElementCustomLabel(de,RuoliCostanti.LABEL_RUOLI,Long.valueOf(numRuoli));
			} else{
				ServletUtils.setDataElementCustomLabel(de,RuoliCostanti.LABEL_RUOLI);
			}
			dati.addElement(de);
			
		}

		
		if(TipoOperazione.CHANGE.equals(tipoOp) && !this.pddCore.isPddEsterna(pdd)){
			
			if (this.isModalitaCompleta()) {	
				de = new DataElement();
				de.setLabel(SoggettiCostanti.LABEL_CLIENT);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			if (this.isModalitaCompleta()) {	
				de.setType(DataElementType.TEXT_EDIT);
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			de.setValue(pd_url_prefix_rewriter);
			de.setSize(this.getSize());
			dati.addElement(de);
			
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
				dati.addElement(de);
			}

			if (this.isModalitaCompleta()) {	
				de = new DataElement();
				de.setLabel(SoggettiCostanti.LABEL_SERVER);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);
			if (this.isModalitaCompleta()) {	
				de.setType(DataElementType.TEXT_EDIT);
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);
			de.setValue(pa_url_prefix_rewriter);
			de.setSize(this.getSize());
			dati.addElement(de);
			
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
				dati.addElement(de);
			}
		}

		return dati;
	}

	public boolean soggettiCheckData(TipoOperazione tipoOp, String id, String tipoprov, String nomeprov, String codiceIpa, String pd_url_prefix_rewriter, String pa_url_prefix_rewriter,
			Soggetto soggettoOld, boolean isSupportatoAutenticazioneSoggetti, String descrizione) throws Exception {
		try {
//			String id = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			int idInt = 0;
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				idInt = Integer.parseInt(id);
			}
//			String nomeprov = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
//			String tipoprov = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
//			String codiceIpa = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);
//			String pd_url_prefix_rewriter = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
//			String pa_url_prefix_rewriter = this.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);

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
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nomeprov.indexOf(" ") != -1) || (tipoprov.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Il nome deve contenere solo lettere e numeri
			if(this.checkSimpleName(nomeprov, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME)==false){
				return false;
			}
			
			// Il tipo deve contenere solo lettere e numeri
			if(this.checkSimpleName(tipoprov, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPO)==false){
				return false;
			}

			// check lunghezza NOTA: Si usa una lunghezza inferiore di 20 al limite massimo della colonna 
			// per poter salvaguardare poi il 255 delle colonne per codice ipa e identificativo porta calcolate in base al nome del soggetto
			int maxLength = 255 - 20;
			if(this.checkLength(nomeprov, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME, -1, maxLength)==false) {
				return false;
			}
			if(descrizione!=null && !"".equals(descrizione)) {
				if(this.checkLength255(descrizione, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DESCRIZIONE)==false) {
					return false;
				}
			}

			
			// check Codice IPA
			// TODO CODICE IPA
			/*try{
				if(codiceIpa!=null && !"".equals(codiceIpa)){
					SICAtoOpenSPCoopUtilities.validateIDSoggettoSICA(codiceIpa);
				}
			}catch(Exception e){
				this.pd.setMessage("Codice IPA non corretto: " + e.getMessage());
				return false;
			}*/
			// TODO CODICE IPA

			// Controllo che eventuali PdUrlPrefixRewriter o PaUrlPrefixRewriter rispettino l'espressione regolare: [A-Za-z]+:\/\/(.*)
			if(pd_url_prefix_rewriter!=null && !"".equals(pd_url_prefix_rewriter)){
				if(RegularExpressionEngine.isMatch(pd_url_prefix_rewriter, "[A-Za-z]+:\\/\\/(.*)")==false){
					this.pd.setMessage("Il campo UrlPrefix rewriter del profilo client contiene un valore errato. Il valore atteso deve seguire la sintassi: "+
							StringEscapeUtils.escapeHtml("protocol://hostname[:port][/*]"));
					return false;
				}
			}
			if(pa_url_prefix_rewriter!=null && !"".equals(pa_url_prefix_rewriter)){
				if(RegularExpressionEngine.isMatch(pa_url_prefix_rewriter, "[A-Za-z]+:\\/\\/(.*)")==false){
					this.pd.setMessage("Il campo UrlPrefix rewriter del profilo server contiene un valore errato. Il valore atteso deve seguire la sintassi: "+
							StringEscapeUtils.escapeHtml("protocol://hostname[:port][/*]"));
					return false;
				}
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
					//this.pd.setMessage("Esiste gi&agrave; un soggetto con nome " + nomeprov + " e tipo " + tipoprov);
					this.pd.setMessage("Esiste gi&agrave; un soggetto "+labelSoggetto);
					return false;
				}

				// Controllo che il codiceIPA non sia gia utilizzato. Il fatto che non esista in base al nome, e' gia garantito rispetto all'univocita' del nome.
				if(this.core.isRegistroServiziLocale()){
					if(codiceIpa!=null && !"".equals(codiceIpa)){
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
							}catch(DriverRegistroServiziNotFound dnot){}
							if(mySogg!=null){
								if(mySogg.getId()!=idInt){
									this.pd.setMessage("Esiste gi&agrave; un soggetto con Codice IPA: " + codiceIpa);
									return false;
								}
							}
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
					if(soggettoOld.getCredenziali()!=null) {
						tipo = soggettoOld.getCredenziali().getTipo();
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
					if(list!=null && list.size()>0) {
						this.pd.setMessage("Non &egrave; possibile modificare il tipo di credenziali poich&egrave; il soggetto viene utilizzato all'interno del controllo degli accessi di "+
								list.size()+" configurazioni di erogazione di servizio");
						return false;
					}
				}
				
			}
			

			boolean oldPasswordCifrata = false;
			if(soggettoOld!=null && soggettoOld.getCredenziali()!=null && soggettoOld.getCredenziali().isCertificateStrictVerification()) {
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
			// String confpw = this.getParameter("confpw");
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
				//Soggetto soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoBasic(utente, password);
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
					this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già le credenziali basic indicate.");
					return false;
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
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) { 
			
					// recupero soggetto con stesse credenziali
					soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoSsl(subject, issuer);
					tipoSsl = "subject/issuer";
			
				} else {
					
					BinaryParameter tipoCredenzialiSSLFileCertificato = this.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
					String tipoCredenzialiSSLVerificaTuttiICampi = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
					boolean strictVerifier = ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi);
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
					Certificate cSelezionato = null;
					byte [] archivio = tipoCredenzialiSSLFileCertificato.getValue();
					if(TipoOperazione.CHANGE.equals(tipoOp) && archivio==null) {
						archivio = soggettoOld.getCredenziali().getCertificate();
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
					this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già le credenziali principal indicate.");
					return false;
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
			ServletUtils.addListElementIntoSession(this.session, SoggettiCostanti.OBJECT_NAME_SOGGETTI);

			boolean multiTenant = this.core.isMultitenant();
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			int idLista = Liste.SOGGETTI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			addFilterProtocol(ricerca, idLista);
			
			if(this.core.isGestionePddAbilitata(this)==false && multiTenant) {
				String filterDominio = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_DOMINIO);
				addFilterDominio(filterDominio, false);
			}
			
			String filterTipoCredenziali = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_CREDENZIALI);
			this.addFilterTipoCredenziali(filterTipoCredenziali,false);
						
			String filterRuolo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO);
			addFilterRuolo(filterRuolo, false);
			
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

			boolean showProtocolli = this.core.countProtocolli(this.session)>1;

			// setto le label delle colonne
			int totEl = this.isModalitaCompleta() ? 4 : 2;
			if( showProtocolli ) {
				totEl++;
			}
			if(multiTenant || this.pddCore.isGestionePddAbilitata(this)) {
				// pdd o dominio
				totEl++;
			}
			if(this.isModalitaCompleta()) {
				totEl++; // connettore column
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
			if(this.pddCore.isGestionePddAbilitata(this)) {
				labels[i++] = PddCostanti.LABEL_PORTA_DI_DOMINIO;
			}
			else if(multiTenant) {
				labels[i++] = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DOMINIO;
			}
			if(this.isModalitaCompleta()) {
				labels[i++] = ConnettoriCostanti.LABEL_CONNETTORE;
			}
			labels[i++] = RuoliCostanti.LABEL_RUOLI;
			if(this.isModalitaCompleta()) {
				labels[i++] = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			}
//			else {
//				labels[i++] = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
//			}
			if(this.isModalitaCompleta()) {
				labels[i++] = PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE;
				labels[i++] = PorteDelegateCostanti.LABEL_PORTE_DELEGATE;
			}
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, SoggettiCostanti.LABEL_SOGGETTI, search);
			}

			// Prendo la lista di pdd dell'utente connesso
			/*List<String> nomiPdd = new ArrayList<String>();
			List<PdDControlStation> listaPdd = this.core.pddList(superUser, new Search());
			Iterator<PdDControlStation> itP = listaPdd.iterator();
			while (itP.hasNext()) {
				PdDControlStation pds = (PdDControlStation) itP.next();
				nomiPdd.add(pds.getNome());
			}*/

			Iterator<org.openspcoop2.core.registry.Soggetto> it = lista.listIterator();
			while (it.hasNext()) {
				org.openspcoop2.core.registry.Soggetto elem = it.next();

				PdDControlStation pdd = null;
				String nomePdD = elem.getPortaDominio();
				if (nomePdD!=null && (!nomePdD.equals("-")) )
					pdd = this.pddCore.getPdDControlStation(nomePdD);
				boolean pddEsterna = this.pddCore.isPddEsterna(nomePdD);

				Vector<DataElement> e = new Vector<DataElement>();

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
				e.addElement(de);

				if(showProtocolli) {
					de = new DataElement();
					de.setValue(this.getLabelProtocollo(protocollo));
					e.addElement(de);
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
					e.addElement(de);
				}
				else if(multiTenant) {
					de = new DataElement();
					if(pddEsterna) {
						de.setValue(SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_LABEL);
					}else {
						de.setValue(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_LABEL);
					}
					e.addElement(de);
				}

				if(this.isModalitaCompleta()) {
					
					boolean showConnettore = this.core.isRegistroServiziLocale() &&
							(this.isModalitaCompleta() || this.pddCore.isPddEsterna(nomePdD) || multiTenant );
					
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
					e.addElement(de);
				}

				
				// Ruoli
				de = new DataElement();
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_RUOLI_LIST,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""));
				if (contaListe) {
					// BugFix OP-674
					//List<String> lista1 = this.soggettiCore.soggettiRuoliList(elem.getId(),new Search(true));
					Search searchForCount = new Search(true,1);
					this.soggettiCore.soggettiRuoliList(elem.getId(),searchForCount);
					//int numRuoli = lista1.size();
					int numRuoli = searchForCount.getNumEntries(Liste.SOGGETTI_RUOLI);
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numRuoli);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);
				
				
				//Servizi Appicativi
				if(this.isModalitaCompleta()) {
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
							Search searchForCount = new Search(true,1);
							this.setFilterRuoloServizioApplicativo(searchForCount, Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO);
							this.saCore.soggettiServizioApplicativoList(searchForCount, elem.getId());
							//int numSA = lista1.size();
							int numSA = searchForCount.getNumEntries(Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO);
							ServletUtils.setDataElementVisualizzaLabel(de,(long)numSA);
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
					}
					e.addElement(de);
				}
				
				if(this.isModalitaCompleta()) {
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
							Search searchForCount = new Search(true,1);
							this.porteApplicativeCore.porteAppList(elem.getId().intValue(), searchForCount);
							//int numPA = lista1.size();
							int numPA = searchForCount.getNumEntries(Liste.PORTE_APPLICATIVE_BY_SOGGETTO);
							ServletUtils.setDataElementVisualizzaLabel(de,(long)numPA);
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
	
					}
					e.addElement(de);
	
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
							Search searchForCount = new Search(true,1);
							this.porteDelegateCore.porteDelegateList(elem.getId().intValue(), searchForCount);
							//int numPD = lista1.size();
							int numPD = searchForCount.getNumEntries(Liste.PORTE_DELEGATE_BY_SOGGETTO);
							ServletUtils.setDataElementVisualizzaLabel(de,(long)numPD);
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
					}
					e.addElement(de);
				}

				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.SOGGETTO, this.session)){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI);
						de.setOnClick(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI_ONCLICK);
						de.setDisabilitaAjaxStatus();
						otherbott.addElement(de);
						ab.setBottoni(otherbott);
						bottoni.addElement(ab);

						this.pd.setAreaBottoni(bottoni);

					}

				}
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareSoggettiConfigList(List<org.openspcoop2.core.config.Soggetto> lista, ISearch ricerca) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, SoggettiCostanti.OBJECT_NAME_SOGGETTI);

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			int idLista = Liste.SOGGETTI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			addFilterProtocol(ricerca, idLista);
						
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

			boolean showProtocolli = this.core.countProtocolli(this.session)>1;
			
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
//			else {
//				labels[i++] = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
//			}
			if(this.isModalitaCompleta()) {
				labels[i++] = PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE;
				labels[i++] = PorteDelegateCostanti.LABEL_PORTE_DELEGATE;
			}
			
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, SoggettiCostanti.LABEL_SOGGETTI, search);
			}

			Iterator<org.openspcoop2.core.config.Soggetto> it = lista.listIterator();
			while (it.hasNext()) {
				org.openspcoop2.core.config.Soggetto elem = (org.openspcoop2.core.config.Soggetto) it.next();

				Vector<DataElement> e = new Vector<DataElement>();
				
				//Soggetto
				DataElement de = new DataElement();
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,elem.getNome()),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,elem.getTipo()));
				de.setValue(elem.getTipo() + "/" + elem.getNome());
				de.setIdToRemove(elem.getId().toString());
				de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
				e.addElement(de);

				if(showProtocolli) {
					de = new DataElement();
					de.setValue(this.getLabelProtocollo(this.soggettiCore.getProtocolloAssociatoTipoSoggetto(elem.getTipo())));
					e.addElement(de);
				}
				
				//Servizi Appicativi
				if(this.isModalitaCompleta()) {
					de = new DataElement();
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,elem.getId()+""));
					if (contaListe) {
						// BugFix OP-674
						//List<ServizioApplicativo> lista1 = this.saCore.soggettiServizioApplicativoList(new Search(true), elem.getId());
						Search searchForCount = new Search(true,1);
						this.setFilterRuoloServizioApplicativo(searchForCount, Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO);
						this.saCore.soggettiServizioApplicativoList(searchForCount, elem.getId());
						//int numSA = lista1.size();
						int numSA = searchForCount.getNumEntries(Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO);
						ServletUtils.setDataElementVisualizzaLabel(de,(long)numSA);
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
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
						// List<PortaApplicativa> lista1 = this.porteApplicativeCore.porteAppList(elem.getId().intValue(), new Search(true));
						Search searchForCount = new Search(true,1);
						this.porteApplicativeCore.porteAppList(elem.getId().intValue(), searchForCount);
						//int numPA = lista1.size();
						int numPA = searchForCount.getNumEntries(Liste.PORTE_APPLICATIVE_BY_SOGGETTO);
						ServletUtils.setDataElementVisualizzaLabel(de,(long)numPA);
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
	
					//Porte Delegate
					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,elem.getId()+""),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_SOGGETTO,elem.getNome()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SOGGETTO,elem.getTipo()));
					if (contaListe) {
						// BugFix OP-674
						//List<PortaDelegata> lista1 = this.porteDelegateCore.porteDelegateList(elem.getId().intValue(), new Search(true));
						Search searchForCount = new Search(true,1);
						this.porteDelegateCore.porteDelegateList(elem.getId().intValue(), searchForCount);
						//int numPD = lista1.size();
						int numPD = searchForCount.getNumEntries(Liste.PORTE_DELEGATE_BY_SOGGETTO);
						ServletUtils.setDataElementVisualizzaLabel(de,(long)numPD);
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}


				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.SOGGETTO, this.session)){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI);
						de.setOnClick(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI_ONCLICK);
						de.setDisabilitaAjaxStatus();
						otherbott.addElement(de);
						ab.setBottoni(otherbott);
						bottoni.addElement(ab);

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

			ServletUtils.addListElementIntoSession(this.session, SoggettiCostanti.OBJECT_NAME_SOGGETTI_RUOLI, 
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
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<String> it = lista.iterator();
				while (it.hasNext()) {
					String ruolo = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

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
					
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
}
