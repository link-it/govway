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
package org.openspcoop2.web.ctrlstat.servlet.config;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.ModalitaIdentificazione;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaleConfigurazioneNodo;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.Cache;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting;
import org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione;
import org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.beans.JMXConstants;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.CacheAlgorithm;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoApplicabilita;
import org.openspcoop2.core.controllo_traffico.constants.TipoBanda;
import org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo;
import org.openspcoop2.core.controllo_traffico.constants.TipoErrore;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoLatenza;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.db.IDSoggettoDB;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdD;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.filetrace.FileTraceGovWayState;
import org.openspcoop2.pdd.timers.TimerState;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.InformazioniProtocollo;
import org.openspcoop2.protocol.utils.ProtocolUtils;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.InUsoType;
import org.openspcoop2.web.ctrlstat.costanti.MultitenantSoggettiErogazioni;
import org.openspcoop2.web.ctrlstat.costanti.MultitenantSoggettiFruizioni;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.CheckboxStatusType;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementImage;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * ConfigurazioneHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneHelper extends ConsoleHelper{

	public ConfigurazioneHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public ConfigurazioneHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}

	public Vector<DataElement>   addIdProprietaToDati(TipoOperazione tipoOp, String idprop, Vector<DataElement> dati) {
		DataElement de = new DataElement();

		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_ID_PROPRIETA);
		de.setValue(idprop);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID_PROPRIETA);
		dati.addElement(de);

		return dati;
	}

	public Vector<DataElement> addTipoTracciamentoAppenderToDati(TipoOperazione tipoOp, String tipo,
			Vector<DataElement> dati,String idAppenderDati, int dimensioneAppenderDati) {
		
		DataElement de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO);
		de.setValue(tipo);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
		de.setSize(getSize());
		de.setRequired(true);
		dati.addElement(de);

		if(tipoOp.equals(TipoOperazione.CHANGE)){
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES_LIST ,
					new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, idAppenderDati));
			if (contaListe)
				ServletUtils.setDataElementVisualizzaLabel(de, (long) dimensioneAppenderDati);
			else
				ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);
		}

		return dati;
	}

	public String convertLifeCacheValue(String v) {
		if(v==null) {
			return "-1";
		}
		try {
			int vInt = Integer.valueOf(v);
			if(vInt>0) {
				return vInt+"";
			}
			else {
				return "-1";
			}
		}catch(Exception e) {
			return "-1";
		}
	}
	
	public void setDataElementCRLCacheInfo(Vector<DataElement> dati,
			String nomeParametroCrlLifeCache, String crllifecache,
			boolean allHidden){
	
		boolean view = this.isModalitaAvanzata() && !allHidden;
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CRL_LIFE_CACHE);
		int value = -1;
		try {
			value = Integer.valueOf(crllifecache);
		}catch(Exception e) {}
		if(value>0){
			de.setValue(value+"");
		}
		if(view){
			de.setType(DataElementType.TEXT_EDIT);
			de.setNote(ConfigurazioneCostanti.LABEL_CACHE_SECONDS_NOTE);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(nomeParametroCrlLifeCache);
		de.setSize( getSize());
		dati.addElement(de);
		
	}
	
	public void setDataElementCache(Vector<DataElement> dati, String intestazioneSezione,
			String nomeParametroStatoCache, String statocache,
			String nomeParametroDimensioneCache, String dimensionecache,
			String nomeParametroAlgoritmoCache, String algoritmocache,
			String nomeParametroIdleCache, String idlecache,
			String nomeParametroLifeCache, String lifecache,
			boolean allHidden){
		
		boolean view = this.isModalitaAvanzata() && !allHidden;
		
		if(view){
			DataElement de = new DataElement();
			de.setLabel(intestazioneSezione);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}

		String[] tipoStatoCache = {
				ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
				ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO
		};
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_CACHE);
		de.setName(nomeParametroStatoCache);
		if(view && 
				!ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_RISPOSTE.equals(intestazioneSezione) &&
				!ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONSEGNA_APPLICATIVI.equals(intestazioneSezione)){
			de.setType(DataElementType.SELECT);
			de.setValues(tipoStatoCache);
			de.setSelected(statocache);
			de.setPostBack(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(statocache);
		}
		dati.addElement(de);

		if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE);
			de.setValue(dimensionecache);
			if(view){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(nomeParametroDimensioneCache);
			de.setSize( getSize());
			dati.addElement(de);

			String[] tipoAlg = {
					ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_LRU,
					ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_MRU
			};
			String[] labelsAlg = {
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_LRU,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_MRU
			};
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE);
			de.setName(nomeParametroAlgoritmoCache);
			if(view){
				de.setType(DataElementType.SELECT);
				de.setLabels(labelsAlg);
				de.setValues(tipoAlg);
				de.setSelected(algoritmocache);
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(algoritmocache);
			}
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIFE_CACHE);
			int value = -1;
			try {
				value = Integer.valueOf(lifecache);
			}catch(Exception e) {}
			if(value>0){
				de.setValue(value+"");
			}
			if(view && 
					!ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_RISPOSTE.equals(intestazioneSezione) &&
					!ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONSEGNA_APPLICATIVI.equals(intestazioneSezione)){
				de.setType(DataElementType.TEXT_EDIT);
				//de.setRequired(true);
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(nomeParametroLifeCache);
			de.setSize( getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_IDLE_CACHE);
			de.setValue(idlecache);
			if(view &&
					!ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONSEGNA_APPLICATIVI.equals(intestazioneSezione)){
				de.setType(DataElementType.TEXT_EDIT);
				if(!ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_KEYSTORE.equals(intestazioneSezione)){
					de.setNote(ConfigurazioneCostanti.LABEL_CACHE_SECONDS_NOTE);
				}
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(nomeParametroIdleCache);
			de.setSize( getSize());
			dati.addElement(de);
		}
		
	}
	
	public Vector<DataElement> addConfigurazioneRegistroToDati(String statocache,
			String dimensionecache, String algoritmocache, String idlecache,
			String lifecache, Vector<DataElement> dati) {
		DataElement de = new DataElement();


		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setType(DataElementType.LINK);
		de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_REGISTRI_LIST);
		de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_REGISTRI);
		dati.addElement(de);

		this.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_REGISTRY,
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_REGISTRY,statocache,
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_REGISTRY,dimensionecache,
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_REGISTRY,algoritmocache,
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_REGISTRY,idlecache,
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_REGISTRY,lifecache,
				false);

		if(this.isModalitaStandard()){
			this.pd.disableEditMode();
		}
		
		dati = this.addParameterApplicaModifica(dati);
		
		return dati;
	}


	public Vector<DataElement> addTipoDiagnosticaAppenderToDati(TipoOperazione tipoOp, String tipo,
			Vector<DataElement> dati,String idAppenderDati, int dimensioneAppenderDati) {
		
		DataElement de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO);
		de.setValue(tipo);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
		de.setSize(getSize());
		de.setRequired(true);
		dati.addElement(de);

		if(tipoOp.equals(TipoOperazione.CHANGE)){
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER_PROPERTIES_LIST,
					new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, idAppenderDati));
			if (contaListe)
				ServletUtils.setDataElementVisualizzaLabel(de, (long) dimensioneAppenderDati);
			else
				ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);
		}

		return dati;
	}

	public Vector<DataElement>   addDiagnosticaDatasourceToDati(TipoOperazione tipoOp, String nome, String nomeJndi,
			String tipoDatabase, String[] tipoDbList, Vector<DataElement> dati, String idSorgenteDati, int dimensioneSorgenteDati) {
		
		DataElement de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SORGENTI_DATI);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME);
		de.setValue(nome);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
		de.setSize(getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME_JNDI);
		de.setValue(nomeJndi);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_JNDI);
		de.setSize(getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_DATABASE);
		de.setType(DataElementType.SELECT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_DATABASE);
		de.setValues(tipoDbList);
		if(tipoDatabase != null){
			de.setSelected(tipoDatabase);
		}
		de.setRequired(true);
		dati.addElement(de);

		if(tipoOp .equals(TipoOperazione.CHANGE)){
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES_LIST,
					new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, idSorgenteDati));
			if (contaListe)
				ServletUtils.setDataElementVisualizzaLabel(de, (long) dimensioneSorgenteDati);
			else
				ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);
		}

		return dati;
	}

	public Vector<DataElement>   addTracciamentoDatasourceToDati(TipoOperazione tipoOp, String nome, String nomeJndi,
			String tipoDatabase, String[] tipoDbList, Vector<DataElement> dati, String idSorgenteDati, int dimensioneSorgenteDati) {

		DataElement de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SORGENTI_DATI);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME);
		de.setValue(nome);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
		de.setSize(getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME_JNDI);
		de.setValue(nomeJndi);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_JNDI);
		de.setSize(getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_DATABASE);
		de.setType(DataElementType.SELECT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_DATABASE);
		de.setValues(tipoDbList);
		if(tipoDatabase != null){
			de.setSelected(tipoDatabase);
		}
		de.setRequired(true);
		dati.addElement(de);

		if(tipoOp .equals(TipoOperazione.CHANGE)){
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_DATASOURCE_PROPERTIES_LIST,
					new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, idSorgenteDati));
			if (contaListe)
				ServletUtils.setDataElementVisualizzaLabel(de, (long) dimensioneSorgenteDati);
			else
				ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);
		}

		return dati;
	}

	// Prepara la lista di datasource del tracciamento
	public void prepareTracciamentoDatasourceList(List<OpenspcoopSorgenteDati> lista)
			throws Exception {
		try {

			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_DATASOURCE);

			Boolean contaListe = ServletUtils.getConfigurazioniPersonalizzateFromSession(this.session);

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_SORGENTI_DATI_TRACCIAMENTO , null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = {
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME_JNDI,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_DATABASE,
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					OpenspcoopSorgenteDati od = lista.get(i);

					Vector<DataElement> e = new Vector<DataElement>();

					Parameter pid = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, od.getId() + "");

					DataElement de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_DATASOURCE_CHANGE, pid);
					de.setValue(od.getNome());
					de.setIdToRemove(""+od.getId());
					e.addElement(de);

					de = new DataElement();
					de.setValue(od.getNomeJndi());
					e.addElement(de);

					de = new DataElement();
					de.setValue(od.getTipoDatabase());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_DATASOURCE_PROPERTIES_LIST, pid);
					if (contaListe)
						ServletUtils.setDataElementVisualizzaLabel(de, (long) od.sizePropertyList());
					else
						ServletUtils.setDataElementVisualizzaLabel(de);

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

	// Controlla i dati del sorgente dati del tracciamento
	public boolean tracciamentoDatasourceCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String nomeJndi = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_JNDI);
			String tipoDatabase = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_DATABASE);

			if (nome == null || "".equals(nome)) {
				this.pd.setMessage("Il campo Nome deve essere specificato.");
				return false;
			}
			if (nomeJndi == null || "".equals(nomeJndi)) {
				this.pd.setMessage("Il campo Nome Jndi deve essere specificato.");
				return false;
			}
			if (tipoDatabase == null || "".equals(tipoDatabase)) {
				this.pd.setMessage("Il campo Tipo Database deve essere specificato.");
				return false;
			}

			// Se tipoOp = add, controllo che il sorgente dati non sia gia' stato registrato
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean trovatoDatasource = false;
				Configurazione newConfigurazione = this.core.getConfigurazioneGenerale();
				Tracciamento t = newConfigurazione.getTracciamento();
				if (t != null) {
					List<OpenspcoopSorgenteDati> lista = t.getOpenspcoopSorgenteDatiList();
					OpenspcoopSorgenteDati od = null;
					for (int j = 0; j < t.sizeOpenspcoopSorgenteDatiList(); j++) {
						od = lista.get(j);
						if (nome.equals(od.getNome())) {
							trovatoDatasource = true;
							break;
						}
					}
				}
				if (trovatoDatasource) {
					this.pd.setMessage("Esiste gi&agrave; un sorgente dati con nome " + nome);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di property di appender del tracciamento
	public void prepareTracciamentoAppenderPropList(OpenspcoopAppender oa,
			List<Property> lista)
					throws Exception {
		try {
			Parameter pOaId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, ""+oa.getId());
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES, 
					pOaId);			

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_TRACCIAMENTO , 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_LIST));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA+ " di " + oa.getTipo(), null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_VALORE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					Property oap = lista.get(i);

					Vector<DataElement> e = new Vector<DataElement>();
					Parameter pOapId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID_PROPRIETA, oap.getId()  + "");
					DataElement de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES_CHANGE, pOaId, pOapId
							);
					de.setValue(oap.getNome());
					de.setIdToRemove(""+oap.getId());
					e.addElement(de);

					de = new DataElement();
					de.setValue(oap.getValore());
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

	// Controlla i dati delle property dell'appender del tracciamento
	public boolean tracciamentoAppenderPropCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String id = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String valore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALORE);

			if (nome == null || "".equals(nome)) {
				this.pd.setMessage("Il campo Nome deve essere specificato.");
				return false;
			}
			if (valore == null || "".equals(valore)) {
				this.pd.setMessage("Il campo Valore deve essere specificato.");
				return false;
			}

			// Se tipoOp = add, controllo che la property non sia gia' stata registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean trovataProp = false;
				Configurazione newConfigurazione = this.core.getConfigurazioneGenerale();
				Tracciamento t = newConfigurazione.getTracciamento();
				OpenspcoopAppender oa = null;
				for (int j = 0; j < t.sizeOpenspcoopAppenderList(); j++) {
					oa = t.getOpenspcoopAppender(j);
					if (idInt == oa.getId().intValue()) {
						break;
					}
				}
				Property oap = null;
				for (int i = 0; i < oa.sizePropertyList(); i++) {
					oap = oa.getProperty(i);
					if (nome.equals(oap.getNome())) {
						trovataProp = true;
						break;
					}
				}
				if (trovataProp) {
					this.pd.setMessage("Esiste gi&agrave; una Proprietà con nome " + nome);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di property di datasource del tracciamento
	public void prepareTracciamentoDatasourcePropList(OpenspcoopSorgenteDati od,
			List<Property> lista)
					throws Exception {
		try {

			Parameter pOdId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, ""+od.getId());
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_DATASOURCE_PROPERTIES, 
					pOdId);	

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_SORGENTI_DATI_TRACCIAMENTO , 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_DATASOURCE_LIST));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA+ " di " + od.getNome(), null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_VALORE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					Property odp = lista.get(i);

					Parameter pOapId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID_PROPRIETA, odp.getId()  + "");

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_DATASOURCE_PROPERTIES_CHANGE, pOdId, pOapId
							);
					de.setValue(odp.getNome());
					de.setIdToRemove(""+odp.getId());
					e.addElement(de);

					de = new DataElement();
					de.setValue(odp.getValore());
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

	// Controlla i dati delle property del datasource del tracciamento
	public boolean tracciamentoDatasourcePropCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String id = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String valore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALORE);

			if (nome == null || "".equals(nome)) {
				this.pd.setMessage("Il campo Nome deve essere specificato.");
				return false;
			}
			if (valore == null || "".equals(valore)) {
				this.pd.setMessage("Il campo Valore deve essere specificato.");
				return false;
			}

			// Se tipoOp = add, controllo che la property non sia gia' stata registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean trovataProp = false;
				Configurazione newConfigurazione = this.core.getConfigurazioneGenerale();
				Tracciamento t = newConfigurazione.getTracciamento();
				List<OpenspcoopSorgenteDati> lista = t.getOpenspcoopSorgenteDatiList();
				OpenspcoopSorgenteDati od = null;
				for (int j = 0; j < t.sizeOpenspcoopSorgenteDatiList(); j++) {
					od = lista.get(j);
					if (idInt == od.getId().intValue()) {
						break;
					}
				}
				List<Property> lista1 = od.getPropertyList();
				Property odp = null;
				for (int i = 0; i < od.sizePropertyList(); i++) {
					odp = lista1.get(i);
					if (nome.equals(odp.getNome())) {
						trovataProp = true;
						break;
					}
				}
				if (trovataProp) {
					this.pd.setMessage("Esiste gi&agrave; una Proprietà con nome " + nome);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di appender del tracciamento
	public void prepareTracciamentoAppenderList(List<OpenspcoopAppender> lista)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER);
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_TRACCIAMENTO, null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO,
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					OpenspcoopAppender oa = lista.get(i);

					Vector<DataElement> e = new Vector<DataElement>();

					Parameter pId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, oa.getId()  + "");
					DataElement de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_CHANGE, pId);
					de.setValue(oa.getTipo());
					de.setIdToRemove(""+oa.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES_LIST ,pId);

					if (contaListe)
						ServletUtils.setDataElementVisualizzaLabel(de, (long)oa.sizePropertyList());
					else
						ServletUtils.setDataElementVisualizzaLabel(de);
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

	// Controlla i dati dell'appender del tracciamento
	public boolean tracciamentoAppenderCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String tipo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);

			if (tipo == null || "".equals(tipo)) {
				this.pd.setMessage("Il campo Tipo deve essere specificato.");
				return false;
			}

			// Se tipoOp = add, controllo che l'appender non sia gia' stato registrato
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean trovatoAppender = false;
				Configurazione newConfigurazione = this.core.getConfigurazioneGenerale();
				Tracciamento t = newConfigurazione.getTracciamento();
				if (t != null) {
					OpenspcoopAppender oa = null;
					for (int j = 0; j < t.sizeOpenspcoopAppenderList(); j++) {
						oa = t.getOpenspcoopAppender(j);
						if (tipo.equals(oa.getTipo())) {
							trovatoAppender = true;
							break;
						}
					}
				}
				if (trovatoAppender) {
					this.pd.setMessage("Esiste gi&agrave; un Appender con tipo " + tipo);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Prepara la lista di appender del dump
	public void prepareDumpAppenderList(List<OpenspcoopAppender> lista)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_APPENDER);
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_DUMP, null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO,
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					OpenspcoopAppender oa = lista.get(i);

					Vector<DataElement> e = new Vector<DataElement>();

					Parameter pId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, oa.getId()  + "");
					DataElement de = new DataElement();
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_APPENDER_CHANGE, pId);
					de.setValue(oa.getTipo());
					de.setIdToRemove(""+oa.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES_LIST ,pId);

					if (contaListe)
						ServletUtils.setDataElementVisualizzaLabel(de, (long)oa.sizePropertyList());
					else
						ServletUtils.setDataElementVisualizzaLabel(de);
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

	// Controlla i dati dell'appender del dump
	public boolean dumpAppenderCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String tipo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);

			if (tipo == null || "".equals(tipo)) {
				this.pd.setMessage("Il campo Tipo deve essere specificato.");
				return false;
			}

			// Se tipoOp = add, controllo che l'appender non sia gia' stato registrato
			if (tipoOp.equals(TipoOperazione.ADD)) {
					boolean trovatoAppender = false;
					Configurazione newConfigurazione = this.core.getConfigurazioneGenerale();
					Dump dump = newConfigurazione.getDump();
					if (dump != null) {
						OpenspcoopAppender oa = null;
						for (int j = 0; j < dump.sizeOpenspcoopAppenderList(); j++) {
							oa = dump.getOpenspcoopAppender(j);
							if (tipo.equals(oa.getTipo())) {
								trovatoAppender = true;
								break;
							}
						}
					}
					if (trovatoAppender) {
						this.pd.setMessage("Esiste gi&agrave; un Appender con tipo " + tipo);
						return false;
					}
				}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addTipoDumpAppenderToDati(TipoOperazione tipoOp, String tipo, Vector<DataElement> dati,String idAppenderDati, int dimensioneAppenderDati) {
		
		DataElement de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO);
		de.setValue(tipo);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
		de.setSize(getSize());
		de.setRequired(true);
		dati.addElement(de);

		if(tipoOp.equals(TipoOperazione.CHANGE)){
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES_LIST, new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, idAppenderDati));
			if (contaListe)
				ServletUtils.setDataElementVisualizzaLabel(de, (long) dimensioneAppenderDati);
			else
				ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);
		}

		return dati;
	}
	
	// Prepara la lista di property di appender del dump
	public void prepareDumpAppenderPropList(OpenspcoopAppender oa,	List<Property> lista)
					throws Exception {
		try {
			Parameter pOaId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, ""+oa.getId());
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES, 
					pOaId);			

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_DUMP , 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_APPENDER_LIST));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA+ " di " + oa.getTipo(), null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_VALORE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					Property oap = lista.get(i);

					Vector<DataElement> e = new Vector<DataElement>();
					Parameter pOapId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID_PROPRIETA, oap.getId()  + "");
					DataElement de = new DataElement();
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES_CHANGE, pOaId, pOapId	);
					de.setValue(oap.getNome());
					de.setIdToRemove(""+oap.getId());
					e.addElement(de);

					de = new DataElement();
					de.setValue(oap.getValore());
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

	// Controlla i dati delle property dell'appender del dump
	public boolean dumpAppenderPropCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String id = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String valore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALORE);

			if (nome == null || "".equals(nome)) {
				this.pd.setMessage("Il campo Nome deve essere specificato.");
				return false;
			}
			if (valore == null || "".equals(valore)) {
				this.pd.setMessage("Il campo Valore deve essere specificato.");
				return false;
			}

			// Se tipoOp = add, controllo che la property non sia gia' stata registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean trovataProp = false;
				Configurazione newConfigurazione = this.core.getConfigurazioneGenerale();
				Dump dump = newConfigurazione.getDump();
				OpenspcoopAppender oa = null;
				for (int j = 0; j < dump.sizeOpenspcoopAppenderList(); j++) {
					oa = dump.getOpenspcoopAppender(j);
					if (idInt == oa.getId().intValue()) {
						break;
					}
				}
				Property oap = null;
				for (int i = 0; i < oa.sizePropertyList(); i++) {
					oap = oa.getProperty(i);
					if (nome.equals(oap.getNome())) {
						trovataProp = true;
						break;
					}
				}
				if (trovataProp) {
					this.pd.setMessage("Esiste gi&agrave; una Propriet&agrave; con nome " + nome);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati del system properties
	public boolean systemPropertiesCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			//String id = this.getParameter("id");
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String valore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALORE);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = "Nome";
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Valore";
					} else {
						tmpElenco = tmpElenco + ", Valore";
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			//if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nei nomi");
				return false;
			}
			if(valore.startsWith(" ") || valore.endsWith(" ")){
				this.pd.setMessage("Non inserire spazi all'inizio o alla fine dei valori");
				return false;
			}
			
			if(this.checkLength255(nome, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME)==false) {
				return false;
			}
			if(this.checkLength255(valore, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_VALORE)==false) {
				return false;
			}

			// Se tipoOp = add, controllo che non sia gia' stato
			// registrato per la porta delegata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				SystemProperties sps = this.confCore.getSystemPropertiesPdD();
				if(sps!=null){
					for (int i = 0; i < sps.sizeSystemPropertyList(); i++) {
						Property tmpSP = sps.getSystemProperty(i);
						if (nome.equals(tmpSP.getNome())) {
							giaRegistrato = true;
							break;
						}
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("La proprietà di sistema " + nome + " &egrave; gi&agrave; stata registrata");
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareSystemPropertiesList(ISearch ricerca, List<Property> lista)
			throws Exception {
		try {

			String id = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			Parameter pId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, id);
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_SYSTEM_PROPERTIES, pId);

			int idLista = Liste.SYSTEM_PROPERTIES;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA,	null));

			} else{
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SYSTEM_PROPERTIES_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}
			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA, search);
			}

			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_VALORE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Property> it = lista.iterator();
				while (it.hasNext()) {
					Property sp = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					Parameter pIdProp = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, ""+sp.getId());

					DataElement de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SYSTEM_PROPERTIES_CHANGE, pIdProp);
					de.setValue(sp.getNome());
					de.setIdToRemove(sp.getNome());
					e.addElement(de);

					de = new DataElement();
					de.setValue(sp.getValore());
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

	public void refreshSystemProperties() {
		// reimposto proprietà
		List<String> aliases = this.confCore.getJmxPdD_aliases();
		boolean resetOk = false;
		if(aliases!=null && aliases.size()>0){
			resetOk = true;
			for (String alias : aliases) {
				String stato = null;
				try{
					stato = this.confCore.invokeJMXMethod(this.confCore.getGestoreRisorseJMX(alias), alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
							this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaSystemPropertiesPdD(alias),
							this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_refreshPersistentConfiguration(alias));
					if(this.isErroreHttp(stato, "refresh System Properties")){
						// e' un errore
						throw new Exception(stato);
					}
				}catch(Exception e){
					ControlStationCore.getLog().error("Errore durante il refresh via jmx delle system properties (alias: "+alias+"): "+e.getMessage(),e);
					resetOk = false;
				}
			}
		}
		
		if(!resetOk) {
			this.pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
		}
	}
	
	// Controlla i dati del pannello Configurazione -> Tabella di routing
	public boolean routingCheckData(String[] registriList) throws Exception {

		try{

			String rottaenabled = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ROTTA_ENABLED);
			String tiporotta = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA);
			if (tiporotta == null) {
				tiporotta = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY;
			}
			String tiposoggrotta = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
			if (tiposoggrotta == null) {
				tiposoggrotta = "";
			}
			String nomesoggrotta = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
			if (nomesoggrotta == null) {
				nomesoggrotta = "";
			}
			String registrorotta = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA);
			if (registrorotta == null) {
				registrorotta = "";
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY) && 
					!tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO)) {
				this.pd.setMessage("Tipo Rotta dev'essere gateway o registro");
				return false;
			}

			// Campi obbligatori
			if (rottaenabled.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) &&
					tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY) && ( (tiposoggrotta.equals("") || tiposoggrotta.equals("-")) || nomesoggrotta.equals(""))) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare Tipo e Nome Soggetto");
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY) && ((tiposoggrotta.indexOf(" ") != -1) || (nomesoggrotta.indexOf(" ") != -1))) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che il registrorotta appartenga alla lista di registri
			// disponibili
			if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO)) {
				boolean trovatoReg = false;
				for (int i = 0; i < registriList.length; i++) {
					String tmpReg = registriList[i];
					if (tmpReg.equals(registrorotta)) {
						trovatoReg = true;
					}
				}
				if (!trovatoReg) {
					this.pd.setMessage("Il registro dev'essere 'all' oppure dev'essere scelto tra quelli definiti nel pannello Registri");
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati delle rotte statiche
	public boolean routingListCheckData(TipoOperazione tipoOp, String[] registriList)
			throws Exception {

		try{

			// String id = this.getParameter("id");
			// int idInt = 0;
			// if (tipoOp.equals("change"))
			// idInt = Integer.parseInt(id);
			String tipo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String tiporotta = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA);
			String tiposoggrotta = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
			String nomesoggrotta = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
			String registrorotta = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA);

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY) && !tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO)) {
				this.pd.setMessage("Tipo Rotta dev'essere gateway o registro");
				return false;
			}

			// Campi obbligatori
			if (tipo.equals("") || nome.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare Tipo e Nome");
				return false;
			}
			if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY) && (tiposoggrotta.equals("") || nomesoggrotta.equals(""))) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare Tipo e Nome Soggetto");
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((tipo.indexOf(" ") != -1) || (nome.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}
			if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY) && ((tiposoggrotta.indexOf(" ") != -1) || (nomesoggrotta.indexOf(" ") != -1))) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che il registrorotta appartenga alla lista di registri
			// disponibili
			if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO)) {
				boolean trovatoReg = false;
				for (int i = 0; i < registriList.length; i++) {
					String tmpReg = registriList[i];
					if (tmpReg.equals(registrorotta)) {
						trovatoReg = true;
					}
				}
				if (!trovatoReg) {
					this.pd.setMessage("Il registro dev'essere 'all' oppure dev'essere scelto tra quelli definiti nel pannello Registri");
					return false;
				}
			}

			// Se tipoOp = add, controllo che il routing non sia gia' stato
			// registrato
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;

				RoutingTable rt = this.confCore.getRoutingTable();
				for (int i = 0; i < rt.sizeDestinazioneList(); i++) {
					RoutingTableDestinazione rtd = rt.getDestinazione(i);
					if (nome.equals(rtd.getNome()) && tipo.equals(rtd.getTipo())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("La destinazione " + tipo + "/" + nome + " &egrave; gi&agrave; stata registrata");
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di rotte statiche
	public void prepareRoutingList(ISearch ricerca, List<RoutingTableDestinazione> lista)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ROTTE_ROUTING);

			int idLista = Liste.ROUTING;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_DI_ROUTING, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROUTING));
			if(search.equals("")){ 
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DESTINAZIONI, 
						null));
			}else{
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DESTINAZIONI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROTTE_ROUTING_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DESTINAZIONI, search);
			}

			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DESTINATARIO,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA 
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<RoutingTableDestinazione> it = lista.iterator();
				while (it.hasNext()) {
					RoutingTableDestinazione rtd = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					Parameter pId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, rtd.getId() + "");
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROTTE_ROUTING_CHANGE, pId);
					de.setValue(rtd.getTipo()+"/"+rtd.getNome());
					e.addElement(de);

					Route r = rtd.getRoute(0);
					de = new DataElement();
					if (r.getGateway() != null) {
						de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY);
					} else {
						de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO);
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

	
	// Controlla i dati del pannello Configurazione -> Registro
	public boolean registroCheckData() throws Exception {

		try{

			String statocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_REGISTRY);
			String dimensionecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_REGISTRY);
			String algoritmocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_REGISTRY);
			String idlecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_REGISTRY);
			String lifecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_REGISTRY);

			return checkDatiCache(CostantiPdD.JMX_REGISTRO_SERVIZI, statocache, dimensionecache, algoritmocache, idlecache, lifecache);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean checkDatiCache(String nomeCache, String statocache, String dimensionecache, String algoritmocache, String idlecache, String lifecache) throws Exception {

		try{

			// Campi obbligatori
			if (statocache.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare lo Stato in "+nomeCache);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && ((dimensionecache.indexOf(" ") != -1) || (idlecache.indexOf(" ") != -1) || (lifecache.indexOf(" ") != -1))) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && !statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
				this.pd.setMessage("Stato Cache "+nomeCache+" dev'essere abilitato o disabilitato");
				return false;
			}
			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && 
					!algoritmocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_LRU) && 
					!algoritmocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_MRU)) {
				this.pd.setMessage("Algoritmo della Cache "+nomeCache+" dev'essere LRU o MRU");
				return false;
			}

			// dimensionecache, idlecache e lifecache devono essere numerici
			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && (dimensionecache==null || dimensionecache.equals("")) ) {
				this.pd.setMessage("Deve essere indicato un valore per la Dimensione della Cache "+nomeCache);
				return false;
			}
			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && !dimensionecache.equals("") && 
					!this.checkNumber(dimensionecache, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE+ "("+nomeCache+")", false)) {
				return false;
			}
			
			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && !idlecache.equals("") && 
					!this.checkNumber(idlecache, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_IDLE_CACHE+ "("+nomeCache+")", false)) {
				return false;
			}
			
//			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && (lifecache==null || lifecache.equals("")) ) {
//				this.pd.setMessage("Deve essere indicato un valore per l'impostazione 'Life second' della Cache "+nomeCache);
//				return false;
//			}
			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && lifecache!=null && !lifecache.equals("") && 
					!this.checkNumber(lifecache, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIFE_CACHE+ "("+nomeCache+")", false)) {
				return false;
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati del registro
	public boolean registriCheckData(TipoOperazione tipoOp) throws Exception {

		try{

			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String location = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LOCATION);
			String tipo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
			String utente = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_UTENTE);
			String password = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PASSWORD);
			String confpw = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONFERMA_PASSWORD);

			// Campi obbligatori
			if (nome.equals("") || location.equals("") || tipo.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME;
				}
				if (location.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco =  ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOCATION;
					} else {
						tmpElenco = tmpElenco + ", "+ ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOCATION;
					}
				}
				if (tipo.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO;
					} else {
						tmpElenco = tmpElenco + ", "+ ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO;
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) || (location.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}
			if (tipo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_UDDI) &&
					((utente.indexOf(" ") != -1) || (password.indexOf(" ") != -1) || (confpw.indexOf(" ") != -1))) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// length
			if(this.checkLength255(nome, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME)==false) {
				return false;
			}
			if(this.checkLength255(location, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOCATION)==false) {
				return false;
			}
			if(utente!=null && !"".equals(utente)) {
				if(this.checkLength255(utente, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_UTENTE)==false) {
					return false;
				}
			}
			if(password!=null && !"".equals(password)) {
				if(this.checkLength255(password, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PASSWORD)==false) {
					return false;
				}
			}
			
			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!tipo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_XML) &&
					!tipo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_DB) &&
					!tipo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_UDDI) &&
					!tipo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_WEB) &&
					!tipo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_WS)) {
				this.pd.setMessage("Tipo dev'essere xml, db, uddi, web o ws");
				return false;
			}

			// Controllo che le password corrispondano
			if (tipo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_UDDI) && !password.equals(confpw)) {
				this.pd.setMessage("Le password non corrispondono");
				return false;
			}

			// Se tipoOp = add, controllo che il registro non sia gia' stato
			// registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;

				AccessoRegistro ar = this.confCore.getAccessoRegistro();
				for (int i = 0; i < ar.sizeRegistroList(); i++) {
					AccessoRegistroRegistro arr = ar.getRegistro(i);
					if (nome.equals(arr.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage("Il registro " + nome + " &egrave; gi&agrave; stato registrato");
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	

	// Prepara la lista di registri
	public void prepareRegistriList(ISearch ricerca, List<AccessoRegistroRegistro> lista)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_REGISTRI);

			int idLista = Liste.REGISTRI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_REGISTRI, 
						null));
			}else{
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_REGISTRI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_REGISTRI_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRI, search);
			}

			// setto le label delle colonne	
			String[] labels = {
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<AccessoRegistroRegistro> it = lista.iterator();
				while (it.hasNext()) {
					AccessoRegistroRegistro arr = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					Parameter pNome = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME, arr.getNome());
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_REGISTRI_CHANGE , pNome);
					de.setValue(arr.getNome());
					de.setIdToRemove(arr.getNome());
					e.addElement(de);

					de = new DataElement();
					de.setValue(arr.getTipo().toString());
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
	
	// Prepara la lista delle regole di configurazione del caching risposta
	public void prepareResponseCachingConfigurazioneRegolaList(ISearch ricerca, List<ResponseCachingConfigurazioneRegola> lista, Integer defaultCacheSeconds) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA);

			int idLista = Liste.CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			this.pd.setSearchDescription("");
			
			ServletUtils.disabledPageDataSearch(this.pd);

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLE, null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne	
			String[] labels = {
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_FAULT,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_CACHE_TIMEOUT_SECONDS
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<ResponseCachingConfigurazioneRegola> it = lista.iterator();
				while (it.hasNext()) {
					ResponseCachingConfigurazioneRegola regola = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setIdToRemove(regola.getId() + "");
					
					Integer statusMin = regola.getReturnCodeMin();
					Integer statusMax = regola.getReturnCodeMax();
					
					// se e' stato salvato il valore 0 lo tratto come null
					if(statusMin != null && statusMin.intValue() <= 0) {
						statusMin = null;
					}
					
					if(statusMax != null && statusMax.intValue() <= 0) {
						statusMax = null;
					}
					
					String statusValue = null;
					// Intervallo
					if(statusMin != null && statusMax != null) {
						if(statusMax.longValue() == statusMin.longValue()) // esatto
							statusValue = statusMin + "";
						else 
							statusValue = "[" + statusMin + " - " + statusMax + "]";
					} else if(statusMin != null && statusMax == null) { // definito solo l'estremo inferiore
						statusValue = "&gt;" + statusMin;
					} else if(statusMin == null && statusMax != null) { // definito solo l'estremo superiore
						statusValue = "&lt;" + statusMax;
					} else { //entrambi null 
						statusValue = CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI;
					}
					
					de.setValue(statusValue);
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(regola.getFault() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO);
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(regola.getCacheTimeoutSeconds() != null ? regola.getCacheTimeoutSeconds() + "" : "default ("+defaultCacheSeconds+")");
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
	
	// Controlla i dati del registro
	public boolean responseCachingConfigurazioneRegolaCheckData(TipoOperazione tipoOp) throws Exception {

		try{
			
			if(this.checkRegolaResponseCaching() == false) {
				return false;
			}
			
			String returnCode = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE);
			String statusMinS = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MIN);
			String statusMaxS = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MAX);
			String faultS = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_FAULT);
			
			Integer statusMin = null;
			Integer statusMax = null;
			boolean fault = ServletUtils.isCheckBoxEnabled(faultS);
			
			if(!returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI)) {

				if(StringUtils.isNotEmpty(statusMinS)) {
					statusMin = Integer.parseInt(statusMinS);
				}
				
				if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO)) {
					if(StringUtils.isNotEmpty(statusMaxS)) {
						statusMax = Integer.parseInt(statusMaxS);
					}
				}
				
				// return code esatto, ho salvato lo stesso valore nel campo return code;
				if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO))
					statusMax = statusMin;
			}
			
			// Se tipoOp = add, controllo che il registro non sia gia' stato
			// registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = this.confCore.existsResponseCachingConfigurazioneRegola(statusMin, statusMax, fault);

				if (giaRegistrato) {
					this.pd.setMessage("&Egrave; gi&agrave; presente una Regola di Response Caching con in parametri indicati.");
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati del pannello Configurazione -> Generale
	public boolean configurazioneCheckData() throws Exception {

		try{

			String inoltromin = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_INOLTRO_MIN);
			String stato = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO);
			String controllo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO);
			//String severita = this.getParameter("severita");
			//String severita_log4j = this.getParameter("severita_log4j");
			String integman = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_INTEGMAN);
			String nomeintegman = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_INTEGMAN);
			String profcoll = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROFILO_COLLABORAZIONE);
			String connessione = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONNESSIONE);
			String utilizzo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_UTILIZZO);
			String validman = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALIDMAN);
			String gestman = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTMAN);
			String registrazioneTracce = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
//			String dumpApplicativo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_APPLICATIVO);
//			String dumpPD = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
//			String dumpPA = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);

			// Campi obbligatori
			if (inoltromin.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare una cadenza");
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if (inoltromin.indexOf(" ") != -1) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (stato != null && !stato.equals("") && !stato.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && 
					!stato.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO) && !stato.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_STATO_WARNING_ONLY)) {
				this.pd.setMessage("Stato dev'essere abilitato, disabilitato o warningOnly");
				return false;
			}
			if (controllo != null && !controllo.equals("") && 
					!controllo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_RIGIDO) &&
					!controllo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_NORMALE)) {
				this.pd.setMessage("Controllo dev'essere rigido o normale");
				return false;
			}
			/*if (!spcoop.equals("off") && !spcoop.equals("fatalOpenspcoop") && !spcoop.equals("errorSpcoop") && !spcoop.equals("errorOpenspcoop") && !spcoop.equals("infoSpcoop") && !spcoop.equals("infoOpenspcoop") && !spcoop.equals("debugLow") && !spcoop.equals("debugMedium") && !spcoop.equals("debugHigh") && !spcoop.equals("all")) {
					this.pd.setMessage("Livello SPCoop dev'essere off, fatalOpenspcoop, errorSpcoop, errorOpenspcoop, infoSpcoop, infoOpenspcoop, debugLow, debugMedium, debugHigh o all");
					return false;
				}
				if (!openspcoop.equals("off") && !openspcoop.equals("fatalOpenspcoop") && !openspcoop.equals("errorSpcoop") && !openspcoop.equals("errorOpenspcoop") && !openspcoop.equals("infoSpcoop") && !openspcoop.equals("infoOpenspcoop") && !openspcoop.equals("debugLow") && !openspcoop.equals("debugMedium") && !openspcoop.equals("debugHigh") && !openspcoop.equals("all")) {
					this.pd.setMessage("Livello OpenSPCoop dev'essere off, fatalOpenspcoop, errorSpcoop, errorOpenspcoop, infoSpcoop, infoOpenspcoop, debugLow, debugMedium, debugHigh o all");
					return false;
				}*/
			boolean foundIM = false;
			for (int i = 0; i < ConfigurazioneCostanti.PARAMETRI_CONFIGURAZIONE_IM.length; i++) {
				if(ConfigurazioneCostanti.PARAMETRI_CONFIGURAZIONE_IM[i].equals(integman)){
					foundIM = true;
					break;
				}
			}
			if (!foundIM &&
					!integman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_IM_CUSTOM)) {
				this.pd.setMessage("Tipo autenticazione per integrationManager sconosciuto");
				return false;
			}
			if (profcoll != null && !profcoll.equals("") &&
					!profcoll.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && 
					!profcoll.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
				this.pd.setMessage("Profilo di collaborazione dev'essere abilitato o disabilitato");
				return false;
			}
			if (connessione != null && !connessione.equals("") &&
					!connessione.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONNESSIONE_NEW) &&
					!connessione.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONNESSIONE_REPLY)) {
				this.pd.setMessage("Connessione dev'essere abilitato o disabilitato");
				return false;
			}
			if (utilizzo != null && !utilizzo.equals("") && !utilizzo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && 
					!utilizzo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
				this.pd.setMessage("Utilizzo dev'essere abilitato o disabilitato");
				return false;
			}
			if (validman != null && !validman.equals("") && !validman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) &&
					!validman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
				this.pd.setMessage("Manifest attachments dev'essere abilitato o disabilitato");
				return false;
			}
			if (!gestman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && 
					!gestman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
				this.pd.setMessage("Gestione dev'essere abilitato o disabilitato");
				return false;
			}
			if (!registrazioneTracce.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) &&
					!registrazioneTracce.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
				this.pd.setMessage("Buste dev'essere abilitato o disabilitato");
				return false;
			}
//			if (!dumpApplicativo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) &&
//					!dumpApplicativo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
//				this.pd.setMessage("Dump Applicativo dev'essere abilitato o disabilitato");
//				return false;
//			}
//			if (!dumpPD.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) &&
//					!dumpPD.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
//				this.pd.setMessage("Dump Binario Porta Delegata dev'essere abilitato o disabilitato");
//				return false;
//			}
//			if (!dumpPA.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) &&
//					!dumpPA.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
//				this.pd.setMessage("Dump Binario Porta Applicativa dev'essere abilitato o disabilitato");
//				return false;
//			}

			// inoltromin dev'essere numerico
			if (!this.checkNumber(inoltromin, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_INOLTRO_BUSTE_NON_RISCONTRATE, false) ) {
				return false;
			}

			// Se integman = custom, nomeintegman dev'essere specificato
			if (integman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_IM_CUSTOM) && 
					(nomeintegman == null || nomeintegman.equals(""))) {
				this.pd.setMessage("Indicare un nome per il tipo autenticazione");
				return false;
			}

			if(this.configurazioneCheckDataCache()==false){
				return false;
			}
			
			if(this.datiAutorizzazioneCheckDataCache()==false){
				return false;
			}
			
			if(this.datiAutenticazioneCheckDataCache()==false){
				return false;
			}
			
			if(this.datiGestioneTokenCheckDataCache()==false){
				return false;
			}
			
			if(this.datiKeystoreCheckDataCache()==false){
				return false;
			}
			
			if(this.datiResponseCachingCheckDataCache()==false){
				return false;
			}
			
			if(this.datiGestoreConsegnaApplicativiCheckDataCache()==false){
				return false;
			}
			
			// validazione URL Invocazione
			if(!this.checkDataURLInvocazione()) {
				return false;
			}
			
			// validazione Cors
			if(!this.checkDataCors()) {
				return false;
			}
			
			// validazione caching risposta
			if(!this.checkDataResponseCaching()) {
				return false;
			}
			
			// validazione canali
			if(!this.canaliCheckData()) {
				return false;
			}
			
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	private boolean canaliCheckData() throws Exception {
		try{
			String canaliEnabledTmp = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_STATO);
			boolean canaliEnabled = ServletUtils.isCheckBoxEnabled(canaliEnabledTmp);
			
			Configurazione configurazione = this.confCore.getConfigurazioneGenerale();
			CanaliConfigurazione gestioneCanali = configurazione.getGestioneCanali();
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : null;
			
			if(canaliEnabled) {
				String canaliNome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NOME);
				String canaliDescrizione = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_DESCRIZIONE);
				String canaliDefault = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_DEFAULT);
			
				if(canaleList == null || canaleList.size() == 0) { // in questa situazione mi dovrei trovare solo quando attivo la funzionalita'
					if(canaleDatiCheckData(canaliNome, canaliDescrizione) == false) {
						return false;
					}
				} else {
					if(StringUtils.isEmpty(canaliDefault)){
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_DEFAULT));
						return false;
					}
					
					boolean found = false;
					for (CanaleConfigurazione canaleConfigurazione : canaleList) {
						if(canaleConfigurazione.getNome().equals(canaliDefault)) {
							found = true;
							break;
						}
					}
					
					if(found ==false) {
						this.pd.setMessage(ConfigurazioneCostanti.MESSAGGIO_CANALE_DEFAULT_SELEZIONATO_NON_ESISTE);
						return false;
					}
				}
			} else {
				boolean oldEnabled = (gestioneCanali != null && StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato()) ) ? true: false;
				
				StringBuilder inUsoMessage = new StringBuilder();
				
				if(oldEnabled) { 
					// se era abilitato e ora sto disabilitando devo controllare che non ci sia nessun canale in uso in API, Erogazioni o Fruizioni
					
					if(canaleList != null) {
						for (CanaleConfigurazione canale : canaleList) {
							if(ConfigurazioneCanaliUtilities.isCanaleInUsoRegistro(canale, this.confCore, this, inUsoMessage, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE)) {
								this.pd.setMessage(MessageFormat.format(ConfigurazioneCostanti.MESSAGGIO_FUNZIONALITA_CANALI_NON_DISATTIVABILE, inUsoMessage.toString()));
								return false;
							}
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
	private boolean canaleDatiCheckData(String canaliNome, String canaliDescrizione) throws Exception {
		// nome obbligatorio
		if(StringUtils.isEmpty(canaliNome)){
			this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NOME));
			return false;
		}
		
		if(this.checkSpazi(canaliNome, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NOME)==false) {
			return false;
		}
		
		if(this.checkLength255(canaliNome, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NOME)==false) {
			return false;
		}
		
		if(this.checkLength255(canaliDescrizione, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_DESCRIZIONE)==false) {
			return false;
		}
		
		return true;
	}
	
	public boolean configurazioneCheckDataCache() throws Exception {

		try{

			String statocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONFIG);
			String dimensionecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONFIG);
			String algoritmocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONFIG);
			String idlecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONFIG);
			String lifecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONFIG);

			return checkDatiCache(CostantiPdD.JMX_CONFIGURAZIONE_PDD, statocache, dimensionecache, algoritmocache, idlecache, lifecache);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean datiAutorizzazioneCheckDataCache() throws Exception {

		try{

			String statocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHZ);
			String dimensionecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHZ);
			String algoritmocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHZ);
			String idlecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHZ);
			String lifecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHZ);

			return checkDatiCache(CostantiPdD.JMX_AUTORIZZAZIONE, statocache, dimensionecache, algoritmocache, idlecache, lifecache);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean datiAutenticazioneCheckDataCache() throws Exception {

		try{

			String statocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHN);
			String dimensionecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHN);
			String algoritmocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHN);
			String idlecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHN);
			String lifecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHN);

			return checkDatiCache(CostantiPdD.JMX_AUTENTICAZIONE, statocache, dimensionecache, algoritmocache, idlecache, lifecache);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean datiGestioneTokenCheckDataCache() throws Exception {

		try{

			String statocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_TOKEN);
			String dimensionecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_TOKEN);
			String algoritmocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_TOKEN);
			String idlecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_TOKEN);
			String lifecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_TOKEN);

			return checkDatiCache(CostantiPdD.JMX_TOKEN, statocache, dimensionecache, algoritmocache, idlecache, lifecache);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean datiKeystoreCheckDataCache() throws Exception {

		try{

			String statocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_KEYSTORE);
			String dimensionecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_KEYSTORE);
			String algoritmocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_KEYSTORE);
			String idlecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_KEYSTORE);
			String lifecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_KEYSTORE);

			boolean esito = checkDatiCache(CostantiPdD.JMX_KEYSTORE_CACHING, statocache, dimensionecache, algoritmocache, idlecache, lifecache);
			if(esito==false) {
				return false;
			}
			
			String crllifecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CRL_LIFE_CACHE_KEYSTORE);
			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && crllifecache!=null && !crllifecache.equals("") && 
					!this.checkNumber(crllifecache, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CRL_LIFE_CACHE+ "("+crllifecache+")", false)) {
				return false;
			}
			
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean datiGestoreConsegnaApplicativiCheckDataCache() throws Exception {

		try{

			String statocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_KEYSTORE);
			String dimensionecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_KEYSTORE);
			String algoritmocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_KEYSTORE);
			String idlecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_KEYSTORE);
			String lifecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_KEYSTORE);

			return checkDatiCache(CostantiPdD.JMX_LOAD_BALANCER, statocache, dimensionecache, algoritmocache, idlecache, lifecache);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean datiResponseCachingCheckDataCache() throws Exception {

		try{

			String statocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_RISPOSTE);
			String dimensionecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_RISPOSTE);
			String algoritmocache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_RISPOSTE);
			String idlecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_RISPOSTE);
			String lifecache = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_RISPOSTE);

			return checkDatiCache(CostantiPdD.JMX_RESPONSE_CACHING, statocache, dimensionecache, algoritmocache, idlecache, lifecache);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}





	// Prepara la lista di appender dei messaggi diagnostici
	public void prepareDiagnosticaAppenderList(List<OpenspcoopAppender> lista)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER);
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_MESSAGGI_DIAGNOSTICI ,   null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = {
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO,
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					OpenspcoopAppender oa = lista.get(i);

					Vector<DataElement> e = new Vector<DataElement>();

					Parameter pOaId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, oa.getId() + ""); 

					DataElement de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER_CHANGE, pOaId);
					de.setValue(oa.getTipo());
					de.setIdToRemove(""+oa.getId());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER_PROPERTIES_LIST, pOaId);
					if (contaListe)
						ServletUtils.setDataElementVisualizzaLabel(de, (long)oa.sizePropertyList());
					else
						ServletUtils.setDataElementVisualizzaLabel(de);
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

	// Controlla i dati dell'appender dei messaggi diagnostici
	public boolean diagnosticaAppenderCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String tipo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);

			if (tipo == null || "".equals(tipo)) {
				this.pd.setMessage("Il campo Tipo deve essere specificato.");
				return false;
			}

			// Se tipoOp = add, controllo che l'appender non sia gia' stato registrato
			if (tipoOp.equals(TipoOperazione.ADD)) {
					boolean trovatoAppender = false;
					Configurazione newConfigurazione = this.core.getConfigurazioneGenerale();
					MessaggiDiagnostici md = newConfigurazione.getMessaggiDiagnostici();
					if (md != null) {
						OpenspcoopAppender oa = null;
						for (int j = 0; j < md.sizeOpenspcoopAppenderList(); j++) {
							oa =  md.getOpenspcoopAppender(j);
							if (tipo.equals(oa.getTipo())) {
								trovatoAppender = true;
								break;
							}
						}
					}
					if (trovatoAppender) {
						this.pd.setMessage("Esiste gi&agrave; un Appender con tipo " + tipo);
						return false;
					}
				}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di property di appender dei messaggi diagnostici
	public void prepareDiagnosticaAppenderPropList(OpenspcoopAppender oa,
			List<Property> lista)
					throws Exception {
		try {
			Parameter pOaId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, ""+oa.getId());
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER_PROPERTIES, pOaId);


			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_MESSAGGI_DIAGNOSTICI, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER_LIST));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA + " di " + oa.getTipo(),
					null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_VALORE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					Property oap = lista.get(i);

					Vector<DataElement> e = new Vector<DataElement>();

					Parameter pIdProp = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID_PROPRIETA, oap.getId()  + "");
					DataElement de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER_PROPERTIES_CHANGE, pOaId, pIdProp);
					de.setValue(oap.getNome());
					de.setIdToRemove(""+oap.getId());
					e.addElement(de);

					de = new DataElement();
					de.setValue(oap.getValore());
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

	// Controlla i dati delle property dell'appender dei messaggi diagnostici
	public boolean diagnosticaAppenderPropCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String id = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String valore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALORE);

			if (nome == null || "".equals(nome)) {
				this.pd.setMessage("Il campo Nome deve essere specificato.");
				return false;
			}
			if (valore == null || "".equals(valore)) {
				this.pd.setMessage("Il campo Valore deve essere specificato.");
				return false;
			}

			// Se tipoOp = add, controllo che la property non sia gia' stata registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean trovataProp = false;
				Configurazione newConfigurazione = this.core.getConfigurazioneGenerale();
				MessaggiDiagnostici md = newConfigurazione.getMessaggiDiagnostici();
				OpenspcoopAppender oa = null;
				for (int j = 0; j < md.sizeOpenspcoopAppenderList(); j++) {
					oa = md.getOpenspcoopAppender(j);
					if (idInt == oa.getId().intValue()) {
						break;
					}
				}
				Property oap = null;
				for (int i = 0; i < oa.sizePropertyList(); i++) {
					oap = oa.getProperty(i);
					if (nome.equals(oap.getNome())) {
						trovataProp = true;
						break;
					}
				}
				if (trovataProp) {
					this.pd.setMessage("Esiste gi&agrave; una Proprietà con nome " + nome);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di datasource dei messaggi diagnostici
	public void prepareDiagnosticaDatasourceList(List<OpenspcoopSorgenteDati> lista)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE);
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_SORGENTI_DATI_MESSAGGI_DIAGNOSTICI , null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME_JNDI,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_DATABASE,
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					OpenspcoopSorgenteDati od = lista.get(i);

					Vector<DataElement> e = new Vector<DataElement>();

					Parameter pId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, od.getId() + "");
					DataElement de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_CHANGE, pId);
					de.setValue(od.getNome());
					de.setIdToRemove(""+od.getId());
					e.addElement(de);

					de = new DataElement();
					de.setValue(od.getNomeJndi());
					e.addElement(de);

					de = new DataElement();
					de.setValue(od.getTipoDatabase());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES_LIST, pId);
					if (contaListe)
						ServletUtils.setDataElementVisualizzaLabel(de, (long)  od.sizePropertyList());
					else
						ServletUtils.setDataElementVisualizzaLabel(de);
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

	// Controlla i dati del sorgente dati dei messaggi diagnostici
	public boolean diagnosticaDatasourceCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String nomeJndi = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_JNDI);
			String tipoDatabase = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_DATABASE);

			if (nome == null || "".equals(nome)) {
				this.pd.setMessage("Il campo Nome deve essere specificato.");
				return false;
			}
			if (nomeJndi == null || "".equals(nomeJndi)) {
				this.pd.setMessage("Il campo Nome Jndi deve essere specificato.");
				return false;
			}
			if (tipoDatabase == null || "".equals(tipoDatabase)) {
				this.pd.setMessage("Il campo Tipo Database deve essere specificato.");
				return false;
			}

			// Se tipoOp = add, controllo che il sorgente dati non sia gia' stato registrato
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean trovatoDatasource = false;
				Configurazione newConfigurazione = this.core.getConfigurazioneGenerale();
				MessaggiDiagnostici md = newConfigurazione.getMessaggiDiagnostici();
				if (md != null) {
					List<OpenspcoopSorgenteDati> lista = md.getOpenspcoopSorgenteDatiList();
					OpenspcoopSorgenteDati od = null;
					for (int j = 0; j < md.sizeOpenspcoopSorgenteDatiList(); j++) {
						od = lista.get(j);
						if (nome.equals(od.getNome())) {
							trovatoDatasource = true;
							break;
						}
					}
				}
				if (trovatoDatasource) {
					this.pd.setMessage("Esiste gi&agrave; un sorgente dati con nome " + nome);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di property di datasource dei messaggi diagnostici
	public void prepareDiagnosticaDatasourcePropList(OpenspcoopSorgenteDati od,
			List<Property> lista)
					throws Exception {
		try {
			Parameter pId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, od.getId() + "");
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES, pId);

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_SORGENTI_DATI_MESSAGGI_DIAGNOSTICI, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_LIST));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA + " di " + od.getNome(),  null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_VALORE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					Property odp = lista.get(i);

					Vector<DataElement> e = new Vector<DataElement>();
					Parameter pOdpId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID_PROPRIETA, odp.getId() + "");
					DataElement de = new DataElement();
					de.setUrl(
							ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES_CHANGE, pId, pOdpId);
					de.setValue(odp.getNome());
					de.setIdToRemove(""+odp.getId());
					e.addElement(de);

					de = new DataElement();
					de.setValue(odp.getValore());
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

	// Controlla i dati delle property del datasource dei messaggi diagnostici
	public boolean diagnosticaDatasourcePropCheckData(TipoOperazione tipoOp)
			throws Exception {
		try {
			String id = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String valore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALORE);

			if (nome == null || "".equals(nome)) {
				this.pd.setMessage("Il campo Nome deve essere specificato.");
				return false;
			}
			if (valore == null || "".equals(valore)) {
				this.pd.setMessage("Il campo Valore deve essere specificato.");
				return false;
			}

			// Se tipoOp = add, controllo che la property non sia gia' stata registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean trovataProp = false;
				Configurazione newConfigurazione = this.core.getConfigurazioneGenerale();
				MessaggiDiagnostici md = newConfigurazione.getMessaggiDiagnostici();
				List<OpenspcoopSorgenteDati> lista = md.getOpenspcoopSorgenteDatiList();
				OpenspcoopSorgenteDati od = null;
				for (int j = 0; j < md.sizeOpenspcoopSorgenteDatiList(); j++) {
					od = lista.get(j);
					if (idInt == od.getId().intValue()) {
						break;
					}
				}
				List<Property> lista1 = od.getPropertyList();
				Property odp = null;
				for (int i = 0; i < od.sizePropertyList(); i++) {
					odp = lista1.get(i);
					if (nome.equals(odp.getNome())) {
						trovataProp = true;
						break;
					}
				}
				if (trovataProp) {
					this.pd.setMessage("Esiste gi&agrave; una Proprietà con nome " + nome);
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}



	public Vector<DataElement>  addValoriRottaToDati(TipoOperazione tipoOp, String nome,
			String tipo, String tiporotta, String registrorotta,
			String[] registriList,
			String[] registriListLabel, Vector<DataElement> dati, String tiposoggrotta, String nomesoggrotta, 
			String[] tipiSoggettiLabel, String[] tipiSoggettiLabelPerProtocollo )
					throws DriverRegistroServiziException {
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DESTINATARIO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

//		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
//			de = new DataElement();
//			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO);
//			de.setValue(this.soggettiCore.getTipoSoggettoDefault());
//			de.setType(DataElementType.HIDDEN);
//			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
//			de.setSize(getSize());
//			dati.addElement(de);
//		} else {
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO);
//		if (tipo == null) {
//			de.setValue("");
//		} else {
//			de.setValue(tipo);
//		}
		de.setSelected(tipo);
		de.setValues(tipiSoggettiLabel);
		//de.setType(DataElementType.TEXT_EDIT);
		de.setType(DataElementType.SELECT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
		de.setPostBack(true);
		de.setSize(getSize());
		de.setRequired(true);
		dati.addElement(de);
//		}

		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME);
		de.setValue(nome);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
		de.setSize(getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ROTTA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		String[] tipoR = { ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY,
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO 
		};
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO);
		de.setType(DataElementType.SELECT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA);
		de.setValues(tipoR);
		de.setSelected(tiporotta);
		//				de.setOnChange("CambiaRotta('add')");
		de.setPostBack(true);
		dati.addElement(de);

//		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
//			if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY)) {
//				de = new DataElement();
//				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
//				de.setValue(this.soggettiCore.getTipoSoggettoDefault());
//				de.setType(DataElementType.HIDDEN);
//				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
//				de.setSize(getSize());
//				dati.addElement(de);
//
//				de = new DataElement();
//				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
//				de.setValue(nomesoggrotta);
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
//				de.setSize(getSize());
//				de.setRequired(true);
//				dati.addElement(de);
//			}
//		} else {
		if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY)) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
			//de.setValue(tiposoggrotta);
			de.setSelected(tiposoggrotta);
			de.setValues(tipiSoggettiLabelPerProtocollo);
			//de.setType(DataElementType.TEXT_EDIT);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
			de.setSize(getSize());
			de.setRequired(true);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
			de.setValue(nomesoggrotta);
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
			de.setSize(getSize());
			de.setRequired(true);
			dati.addElement(de);
		}
//		}

		if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO)) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA);
			de.setValues(registriList);
			de.setLabels(registriListLabel);
			de.setSelected(registrorotta);
			dati.addElement(de);
		}

		return dati;
	}




	public Vector<DataElement> addRoutingToDati(TipoOperazione tipoOp, String tiporotta,
			String tiposoggrotta, String nomesoggrotta, String registrorotta,
			String rottaenabled,  
			String[] registriList, String[] registriListLabel, String[] tipiSoggettiLabel,
			Vector<DataElement> dati) throws DriverRegistroServiziException {
		
		DataElement dataElement = new DataElement();
		dataElement.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ROUTING_DELLE_BUSTE);
		dataElement.setType(DataElementType.TITLE);
		dati.add(dataElement);
		
		DataElement de = new DataElement();
		String[] tipoRouting = {
				ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
				ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO
		};
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ROUTING_DELLE_BUSTE_STATO);
		de.setType(DataElementType.SELECT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ROTTA_ENABLED);
		de.setValues(tipoRouting);
		de.setSelected(rottaenabled);
		//					de.setOnChange("CambiaRouting()");
		de.setPostBack(true);
		dati.addElement(de);

		if (rottaenabled.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ROTTA_DI_DEFAULT);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			String[] tipoR = {
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO
			};
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA);
			de.setValues(tipoR);
			de.setSelected(tiporotta);
			//						de.setOnChange("CambiaRotta('routing')");
			de.setPostBack(true);
			dati.addElement(de);

//			if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
//				if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY)) {
//					de = new DataElement();
//					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
//					de.setValue(this.soggettiCore.getTipoSoggettoDefault());
//					de.setType(DataElementType.HIDDEN);
//					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
//					de.setSize(getSize());
//					dati.addElement(de);
//
//					de = new DataElement();
//					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
//					de.setValue(nomesoggrotta);
//					de.setType(DataElementType.TEXT_EDIT);
//					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
//					de.setRequired(true);
//					de.setSize(getSize());
//					dati.addElement(de);
//				}
//			} else {
			if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_GATEWAY)) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
				//de.setValue(tiposoggrotta);
				//de.setType(DataElementType.TEXT_EDIT);
				de.setType(DataElementType.SELECT);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_SOGGETTO_ROTTA);
				de.setValues(tipiSoggettiLabel);
				de.setSelected(tiposoggrotta);
				de.setSize(getSize());
				de.setRequired(true);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
				de.setValue(nomesoggrotta);
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_SOGGETTO_ROTTA);
				de.setSize(getSize());
				de.setRequired(true);
				dati.addElement(de);
			}
//			}

			if (tiporotta.equals(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_ROTTA_REGISTRO)) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA);
				de.setType(DataElementType.SELECT);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRO_ROTTA);
				de.setValues(registriList);
				de.setLabels(registriListLabel);
				de.setSelected(registrorotta);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ROTTE_STATICHE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROTTE_ROUTING_LIST);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DESTINAZIONI);
			dati.addElement(de);
		}
		
		dati = this.addParameterApplicaModifica(dati);

		return dati;
	}

	public Vector<DataElement> addRegistroToDati(TipoOperazione tipoOP, String nome, String location, String tipo,
			String utente, String password, String confpw,
			Vector<DataElement> dati) {
		
		DataElement dataElement = new DataElement();
		dataElement.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO);
		dataElement.setType(DataElementType.TITLE);
		dati.add(dataElement);
		
		DataElement de = new DataElement();
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME);
		de.setValue(nome);
		if(TipoOperazione.ADD.equals(tipoOP)){
			de.setType(DataElementType.TEXT_EDIT);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
		de.setSize( getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOCATION);
		de.setValue(location);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LOCATION);
		de.setSize( getSize());
		de.setRequired(true);
		dati.addElement(de);

		//String[] tipoReg = { "xml", "db", "uddi", "web", "ws" };
		String[] tipoReg = { 
				ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_XML,
				ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_DB,
				ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_WEB,
				ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_WS
		};
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO);
		de.setType(DataElementType.SELECT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
		de.setValues(tipoReg);
		de.setSelected(tipo);
		//				de.setOnChange("CambiaTipo('add')");
		de.setPostBack(true);
		dati.addElement(de);

		if (tipo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_UDDI)) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_UTENTE);
			de.setValue(utente);
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_UTENTE);
			de.setSize( getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PASSWORD);
			de.setValue(password);
			de.setType(DataElementType.CRYPT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PASSWORD);
			de.setSize( getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONFERMA_PASSWORD);
			de.setValue(confpw);
			de.setType(DataElementType.CRYPT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONFERMA_PASSWORD);
			de.setSize( getSize());
			dati.addElement(de);
		}


		return dati;
	}
	
	


	public Vector<DataElement> addConfigurazioneToDati(  
			boolean allHidden,
			String inoltromin, String stato,
			String controllo, String severita, String severita_log4j,
			String integman, String nomeintegman, String profcoll,
			String connessione, String utilizzo, String validman,
			String gestman, String registrazioneTracce, String dumpPD, String dumpPA,
			String xsd,	String tipoValidazione, String confPers, Configurazione configurazione,
			Vector<DataElement> dati, String applicaMTOM, 
			String urlInvocazionePA, String urlInvocazionePD,
			boolean multitenantEnabled, String multitenantSoggettiFruizioni, String multitenantSoggettiErogazioni,
			boolean editModeEnabled,
			boolean corsStato, TipoGestioneCORS corsTipo, boolean corsAllAllowOrigins, boolean corsAllAllowHeaders, boolean corsAllAllowMethods,	
			String corsAllowHeaders, String corsAllowOrigins, String corsAllowMethods, 
			boolean corsAllowCredential, String corsExposeHeaders, boolean corsMaxAge, int corsMaxAgeSeconds,
			boolean responseCachingEnabled,	int responseCachingSeconds, boolean responseCachingMaxResponseSize,	long responseCachingMaxResponseSizeBytes, 
			boolean responseCachingDigestUrlInvocazione, boolean responseCachingDigestHeaders, boolean responseCachingDigestPayload, String responseCachingDigestHeadersNomiHeaders, StatoFunzionalitaCacheDigestQueryParameter responseCachingDigestQueryParameter, String responseCachingDigestNomiParametriQuery, 
			boolean responseCachingCacheControlNoCache, boolean responseCachingCacheControlMaxAge, boolean responseCachingCacheControlNoStore, boolean visualizzaLinkConfigurazioneRegola, 
			String servletResponseCachingConfigurazioneRegolaList, List<Parameter> paramsResponseCachingConfigurazioneRegolaList, int numeroResponseCachingConfigurazioneRegola, int numeroRegoleProxyPass,
			boolean canaliEnabled, int numeroCanali, int numeroNodi, String canaliNome, String canaliDescrizione, List<CanaleConfigurazione> canaleList, String canaliDefault			
			) throws Exception {
		DataElement de = new DataElement();

		if (this.isModalitaStandard() || allHidden) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_INOLTRO_MIN);
			de.setValue(inoltromin);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_INOLTRO_MIN);
			de.setSize( getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO);
			de.setValue(stato);

			dati.addElement(de);
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO);
			de.setValue(controllo);

			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROFILO_COLLABORAZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROFILO_COLLABORAZIONE);
			de.setValue(profcoll);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_VALIDMAN);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALIDMAN);
			de.setValue(validman);
			dati.addElement(de);
		} else {

			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_INOLTRO_BUSTE_NON_RISCONTRATE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
 
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_INOLTRO_MIN);
			de.setValue(inoltromin);
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_INOLTRO_MIN);
			de.setRequired(true);
			de.setSize(getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_VALIDAZIONE_BUSTE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			String[] tipoStato = { 
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
					ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO,
					ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_STATO_WARNING_ONLY
			};
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO);
			de.setValues(tipoStato);
			de.setSelected(stato);
			dati.addElement(de);

			String[] tipoControllo = { 
					ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_RIGIDO,
					ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_NORMALE
			};
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO);
			de.setValues(tipoControllo);
			de.setSelected(controllo);
			dati.addElement(de);

			String[] tipoPF = { 
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
					ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO
			};
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROFILO_COLLABORAZIONE);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROFILO_COLLABORAZIONE);
			de.setValues(tipoPF);
			de.setSelected(profcoll);
			dati.addElement(de);

			String[] tipoVM = { 
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
					ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO
			};
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_VALIDMAN);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALIDMAN);
			de.setValues(tipoVM);
			de.setSelected(validman);
			dati.addElement(de);
		}

		addMessaggiDiagnosticiToDatiAsHidden(severita, severita_log4j, dati);

		addTracciamentoToDatiAsHidden(registrazioneTracce, configurazione, dati);
		
		addRegistrazioneMessaggiToDatiAsHidden(dumpPD, dumpPA, configurazione, dati);
		
		
		// I.M.

		if (!this.isModalitaStandard() && !allHidden) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_INTEGRATION_MANAGER);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}

		int totEl = ConfigurazioneCostanti.PARAMETRI_CONFIGURAZIONE_IM.length;
		if (confPers.equals(Costanti.CHECK_BOX_ENABLED_TRUE))
			totEl++;
		String[] tipoIM = new String[totEl];
		for (int i = 0; i < ConfigurazioneCostanti.PARAMETRI_CONFIGURAZIONE_IM.length; i++) {
			tipoIM[i] = ConfigurazioneCostanti.PARAMETRI_CONFIGURAZIONE_IM[i];
		}
		if (confPers.equals(Costanti.CHECK_BOX_ENABLED_TRUE))
			tipoIM[(totEl-1)] = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_IM_CUSTOM;
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_INTEGMAN);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_INTEGMAN);
		if (this.isModalitaStandard() || allHidden) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.SELECT);
			de.setValues(tipoIM);
			de.setSelected(integman);
			de.setPostBack(true);
		}
		de.setValue(integman);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_NOME_INTEGMAN);
		if (this.isModalitaStandard() || allHidden) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			if (integman == null || !integman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_IM_CUSTOM))
				de.setType(DataElementType.HIDDEN);
			else
				de.setType(DataElementType.TEXT_EDIT);
		}
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_INTEGMAN);
		de.setValue(nomeintegman);
		dati.addElement(de);

		if (this.isModalitaStandard() || allHidden) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONNESSIONE);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONNESSIONE);
			de.setValue(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONNESSIONE_REPLY);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_UTILIZZO);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_UTILIZZO);
			de.setValue(CostantiConfigurazione.DISABILITATO.toString());
			de.setSelected(utilizzo);
			dati.addElement(de);
		} else {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_VALIDAZIONE_CONTENUTI_APPLICATIVI);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			String[] tipoXsd = { 
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
					ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO,
					ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_STATO_WARNING_ONLY 
			};
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_XSD);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_XSD);
			de.setValues(tipoXsd);
			//						de.setOnChange("CambiaValidazione('" +
			//							InterfaceType.STANDARD.equals(user.getInterfaceType()) +
			//							"')");
			de.setPostBack(true);
			if (xsd == null) {
				de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO);
			} else {
				de.setSelected(xsd);
			}
			dati.addElement(de);

			if (ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(xsd) || 
					ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_STATO_WARNING_ONLY .equals(xsd)) {
				//String[] tipi_validazione = { "xsd", "wsdl", "openspcoop" };
				String[] tipi_validazione = { 
						ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_VALIDAZIONE_XSD,
						ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_VALIDAZIONE_WSDL
				};
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TIPO_VALIDAZIONE);
				de.setType(DataElementType.SELECT);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_VALIDAZIONE);
				de.setValues(tipi_validazione);
				if (tipoValidazione == null) {
					de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_VALIDAZIONE_XSD);
				} else {
					de.setSelected(tipoValidazione);
				}
				dati.addElement(de);
				
				
				// Applica MTOM 
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ACCETTA_MTOM);

				
				if(this.isModalitaAvanzata()){
					de.setType(DataElementType.CHECKBOX);
					if( ServletUtils.isCheckBoxEnabled(applicaMTOM) || CostantiRegistroServizi.ABILITATO.equals(applicaMTOM) ){
						de.setSelected(true);
					}
				}
				else{
					de.setType(DataElementType.HIDDEN);
					de.setValue(applicaMTOM);
				}
			 
				de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RISPOSTE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			String[] tipoConn = { 
					ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONNESSIONE_NEW,
					ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONNESSIONE_REPLY
			};
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONNESSIONE);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONNESSIONE);
			de.setValues(tipoConn);
			de.setSelected(connessione);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_INDIRIZZO_TELEMATICO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			String[] tipoU = { 
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
					ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO
			};
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_UTILIZZO);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_UTILIZZO);
			de.setValues(tipoU);
			de.setSelected(utilizzo);
			dati.addElement(de);
		}

		if (!this.isModalitaStandard() && !allHidden) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MANIFEST_ATTACHMENTS);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}

		String[] tipoGM = { 
				ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
				ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO
		};
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTMAN);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTMAN);
		if (this.isModalitaStandard() || allHidden) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.SELECT);
			de.setValues(tipoGM);
			de.setSelected(gestman);
		}
		de.setValue(gestman);
		dati.addElement(de);
		
		
		if(!allHidden) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MULTITENANT);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		boolean existsMoreThanOneSoggettoOperativoPerProtocollo = false;
		List<org.openspcoop2.core.registry.Soggetto> l = this.soggettiCore.getSoggettiOperativi();
		Map<String, Integer> countSoggettoOperativiByProtocol = new HashMap<>();
		if(l!=null && !l.isEmpty()) {
			for (org.openspcoop2.core.registry.Soggetto soggetto : l) {
				String protocol = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
				int count = 0;
				if(countSoggettoOperativiByProtocol.containsKey(protocol)) {
					count = countSoggettoOperativiByProtocol.remove(protocol);
				}
				count ++;
				if(count>1) {
					existsMoreThanOneSoggettoOperativoPerProtocollo = true;
					break;
				}
				countSoggettoOperativiByProtocol.put(protocol, count);
			}
		}
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_MULTITENANT_STATO);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_STATO);
		if(allHidden) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			if(!existsMoreThanOneSoggettoOperativoPerProtocollo) {
				de.setType(DataElementType.SELECT);
				de.setPostBack(true);
				de.setValues(ConfigurazioneCostanti.STATI);
				de.setSelected(multitenantEnabled ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue());
			}
			else {
				de.setType(DataElementType.TEXT);
			}
		}
		de.setValue(multitenantEnabled ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue());
		dati.addElement(de);
		
		if(multitenantEnabled) {
			
			boolean linkSoggettiFiltroDominioImpostato = false; // confonde
			if(!allHidden && linkSoggettiFiltroDominioImpostato) {
				de = new DataElement();
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_MULTITENANT_SOGGETTI);
				de.setType(DataElementType.LINK);
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_FILTER_DOMINIO_INTERNO,"true"));
				dati.addElement(de);
			}
			
			if(!allHidden) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MULTITENANT_FRUIZIONI);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_MULTITENANT_FRUIZIONI_SOGGETTO_EROGATORE);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_FRUIZIONI_SOGGETTO_EROGATORE);
			if(!allHidden && editModeEnabled) {
				de.setType(DataElementType.SELECT);
				String [] values = MultitenantSoggettiFruizioni.toEnumNameArray();
				String [] labels = MultitenantSoggettiFruizioni.toArray();
				de.setValues(values);
				de.setLabels(labels);
				de.setSelected(multitenantSoggettiFruizioni);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setValue(multitenantSoggettiFruizioni);
			dati.addElement(de);
			
			if(!editModeEnabled) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_MULTITENANT_FRUIZIONI_SOGGETTO_EROGATORE);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_FRUIZIONI_SOGGETTO_EROGATORE+"__LABEL");
				String multi = MultitenantSoggettiFruizioni.SOLO_SOGGETTI_ESTERNI.getValue();
				try {
					multi = MultitenantSoggettiFruizioni.valueOf(multitenantSoggettiFruizioni).getValue();
				}catch(Exception e) {}
				de.setValue(multi);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}
				dati.addElement(de);
			}
			
			if(!allHidden) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MULTITENANT_EROGAZIONI);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_MULTITENANT_EROGAZIONI_SOGGETTI_FRUITORI);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_EROGAZIONI_SOGGETTI_FRUITORI);
			if(!allHidden && editModeEnabled) {
				de.setType(DataElementType.SELECT);
				String [] values = MultitenantSoggettiErogazioni.toEnumNameArray();
				String [] labels = MultitenantSoggettiErogazioni.toArray();
				de.setValues(values);
				de.setLabels(labels);
				de.setSelected(multitenantSoggettiErogazioni);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setValue(multitenantSoggettiErogazioni);
			dati.addElement(de);
			
			if(!editModeEnabled) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_MULTITENANT_EROGAZIONI_SOGGETTI_FRUITORI);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_EROGAZIONI_SOGGETTI_FRUITORI+"__LABEL");
				String multi = MultitenantSoggettiErogazioni.SOLO_SOGGETTI_ESTERNI.getValue();
				try {
					multi = MultitenantSoggettiErogazioni.valueOf(multitenantSoggettiErogazioni).getValue();
				}catch(Exception e) {}
				de.setValue(multi);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}
				dati.addElement(de);
			}
		}
		
		if(!allHidden) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PREFIX_URL_INVOCAZIONE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA);
		if(allHidden) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		de.setValue(urlInvocazionePA);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD);
		if(allHidden) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(false);
		}
		de.setValue(urlInvocazionePD);
		dati.addElement(de);
		
		if(!allHidden) {
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA_LIST);
			boolean contaListeFromSession = ServletUtils.getContaListeFromSession(this.session) != null ? ServletUtils.getContaListeFromSession(this.session) : false;
			if (contaListeFromSession)
				de.setValue(ConfigurazioneCostanti.LABEL_REGOLE_PROXY_PASS+" (" + numeroRegoleProxyPass + ")");
			else
				de.setValue(ConfigurazioneCostanti.LABEL_REGOLE_PROXY_PASS);
			dati.addElement(de);
		}
		
		
		// Configuriazione CORS
		this.addConfigurazioneCorsToDati(dati, corsStato, corsTipo, 
				corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
				corsAllowHeaders, corsAllowOrigins, corsAllowMethods, 
				corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds, 
				true,
				allHidden);
		
		// Configurazione Response Caching
		this.addResponseCachingToDati(dati, responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize,		responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, 
				responseCachingDigestPayload, true, responseCachingDigestHeadersNomiHeaders, responseCachingDigestQueryParameter, responseCachingDigestNomiParametriQuery,  
				responseCachingCacheControlNoCache, responseCachingCacheControlMaxAge, responseCachingCacheControlNoStore,
				visualizzaLinkConfigurazioneRegola, servletResponseCachingConfigurazioneRegolaList, paramsResponseCachingConfigurazioneRegolaList, numeroResponseCachingConfigurazioneRegola,
				allHidden);
		
		// Configurazione Canali
		this.addConfigurazioneCanaliToDati(dati, canaliEnabled, numeroCanali, numeroNodi, canaliNome, canaliDescrizione, true, allHidden, canaleList, canaliDefault);
		
		
		if(!allHidden && !multitenantEnabled) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROFILO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		ProtocolFactoryManager pManager = ProtocolFactoryManager.getInstance();
		MapReader<String, IProtocolFactory<?>> mapPFactory = pManager.getProtocolFactories();
		Enumeration<String> protocolName = mapPFactory.keys();
		List<String> protocolliDispondibili = new ArrayList<>();
		while (protocolName.hasMoreElements()) {
			String protocollo = (String) protocolName.nextElement();
			protocolliDispondibili.add(protocollo);
		}
		for (String protocollo : ProtocolUtils.orderProtocolli(protocolliDispondibili)) {
			IProtocolFactory<?> pFactory = mapPFactory.get(protocollo);
			
			InformazioniProtocollo infoProt = pFactory.getInformazioniProtocol();
					
			User user = ServletUtils.getUserFromSession(this.session);
			String userLogin = user.getLogin();
			
			if(mapPFactory.size()>1) {
				if(!allHidden && !multitenantEnabled) {
					de = new DataElement();
					de.setLabel(infoProt.getLabel());
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
				}
			}
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_NAME);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_NAME+protocollo);
			de.setType(DataElementType.HIDDEN);
			de.setValue(protocollo);
			dati.addElement(de);
														
			if(!multitenantEnabled) {
				IDSoggetto idSoggetto = this.soggettiCore.getSoggettoOperativoDefault(userLogin, protocollo);
				long idSoggettoLong = this.soggettiCore.getIdSoggetto(idSoggetto.getNome(), idSoggetto.getTipo());
						
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_SOGGETTO);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_SOGGETTO+protocollo);
				de.setValue(this.getLabelNomeSoggetto(protocollo, idSoggetto.getTipo(), idSoggetto.getNome()));
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setType(DataElementType.TEXT);
				}
				dati.addElement(de);
				
				if(!allHidden) {
					de = new DataElement();
					de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_SOGGETTO_VISUALIZZA_DATI);
					de.setType(DataElementType.LINK);
					de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,idSoggettoLong+""),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,idSoggetto.getNome()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,idSoggetto.getTipo()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_MODIFICA_OPERATIVO,"true"));
					dati.addElement(de);
				}
			}
		}
		
		if (!allHidden && !this.isModalitaStandard()) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO_SERVIZI);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
	
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI);
			ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);
	
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TABELLA_DI_ROUTING);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
	
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ROUTING);
			ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);
		
		}
		
		if (!allHidden) {
	
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SYSTEM_PROPERTIES_LIST);
			ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);
			
		}
		
		// Lascio solo in menu
//		if(this.confCore.getJmxPdD_aliases()!=null && this.confCore.getJmxPdD_aliases().size()>0){
//		
//			de = new DataElement();
//			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA);
//			de.setType(DataElementType.TITLE);
//			dati.addElement(de);
//	
//			de = new DataElement();
//			de.setType(DataElementType.LINK);
//			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD);
//			ServletUtils.setDataElementVisualizzaLabel(de);
//			dati.addElement(de);
//			
//		}

		return dati;
	}
	
	private void addConfigurazioneCanaliToDati(Vector<DataElement> dati, boolean canaliEnabled, int numeroCanali,
			int numeroNodi, String canaliNome, String canaliDescrizione, boolean addTitle, boolean allHidden, List<CanaleConfigurazione> canaleList, String canaliDefault) throws Exception {
		
		boolean contaListeFromSession = ServletUtils.getContaListeFromSession(this.session) != null ? ServletUtils.getContaListeFromSession(this.session) : false;
		DataElement de;
		
		if(!allHidden && addTitle) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALI);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		boolean funzionalitaDisabilitabile = true;
		if(canaliEnabled) {
			if(canaleList != null) {
				for (CanaleConfigurazione canale : canaleList) {
					if(ConfigurazioneCanaliUtilities.isCanaleInUsoRegistro(canale, this.confCore, this, new StringBuilder(), org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE)) {
						funzionalitaDisabilitabile = false;
						break;
					}
				}
			}
		}
		
		de = new DataElement();
		de.setLabel(addTitle ?  ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_STATO : "");
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_STATO);
		if(allHidden) {
			de.setType(DataElementType.HIDDEN);
		}
		else if(!funzionalitaDisabilitabile) {
			de.setType(DataElementType.TEXT);
		}
		else {
			de.setType(DataElementType.SELECT);
			de.setPostBack(true);
			de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
			de.setSelected(canaliEnabled ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
		}
		de.setValue(canaliEnabled ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
		dati.addElement(de);
		
		if(!allHidden) {
			// se si passa da disabilitato ad abilitato, il numero dei canali e' 0, devo visualizzare la form di inserimento del canale di default
			if(canaliEnabled) {
				if(numeroCanali == 0) {
					// subtitle
					de = new DataElement();
					de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALE_DEFAULT);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
					
					// nome canale 
					de = new DataElement();
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NOME);
					de.setType(DataElementType.TEXT_EDIT);
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NOME);
					de.setValue(canaliNome);
					de.setRequired(true);
					dati.addElement(de);
					
					
					// descrizione canale
					de = new DataElement();
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_DESCRIZIONE);
					de.setType(DataElementType.TEXT_EDIT);
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_DESCRIZIONE);
					de.setValue(canaliDescrizione);
					dati.addElement(de);
				} else {
					// scelta canale default
					de = new DataElement();
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_DEFAULT);
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_DEFAULT);
					de.setType(DataElementType.SELECT);
					
					List<String> canaliListValues = canaleList.stream().map(CanaleConfigurazione::getNome).collect(Collectors.toList());
					de.setValues(canaliListValues);
					de.setLabels(canaliListValues);
					de.setSelected(canaliDefault);
					dati.addElement(de);
					
					// link canali
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CANALI_LIST);
					
					if (contaListeFromSession)
						de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_CANALI +" (" + numeroCanali + ")");
					else
						de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_CANALI);
					dati.addElement(de);
					
					// link nodi
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CANALI_NODI_LIST);
					if (contaListeFromSession)
						de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_CANALI_NODI +" (" + numeroNodi + ")");
					else
						de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_CANALI_NODI);
					dati.addElement(de);
				}
			}
		}
	}
	public void addRegistrazioneMessaggiToDatiAsHidden(String dumpPD, String dumpPA, Configurazione configurazione,	Vector<DataElement> dati) {
		DataElement de = new DataElement();
		
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
		de.setType(DataElementType.HIDDEN);
		de.setValue(dumpPD);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
		de.setType(DataElementType.HIDDEN);
		de.setValue(dumpPA);
		dati.addElement(de);
		
	}
	
	public void addRegistrazioneMessaggiToDati(String dumpApplicativo, String dumpPD, String dumpPA, Configurazione configurazione,	Vector<DataElement> dati, Boolean contaListe) {
		DataElement de;
		// DUMP
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_MESSAGGI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		String[] tipoDump = {
				ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
				ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO
		};
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_APPLICATIVO);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO);
		de.setType(DataElementType.SELECT);
		de.setValues(tipoDump);
		de.setSelected(dumpApplicativo);
		de.setPostBack_viaPOST(true);
		dati.addElement(de);
		
		if(dumpApplicativo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)) {
			String oldDumpApplicativo =null;
			if(configurazione.getDump().getStato()!=null)
				oldDumpApplicativo = configurazione.getDump().getStato().toString();
			
			if(dumpApplicativo.equals(oldDumpApplicativo)) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_CONFIGURAZIONE);
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DUMP_CONFIGURAZIONE);
				dati.addElement(de);
			}
		}
		
		if (this.isModalitaAvanzata()) {
			if (this.confCore.isDump_showConfigurazioneCustomAppender()) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_APPENDER_LIST);
				if (contaListe) {
					int totAppender = 0;
					if (configurazione.getDump() != null)
						totAppender =
						configurazione.getDump().sizeOpenspcoopAppenderList();
					ServletUtils.setDataElementCustomLabel(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER, (long)totAppender);
				} else
					ServletUtils.setDataElementCustomLabel(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER);
				dati.addElement(de);
			}
		}
		
		if(this.isModalitaAvanzata()){
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}
		
		String[] tipoDumpConnettorePD = {
				ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
				ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO
		};
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
		if(this.isModalitaAvanzata()){
			de.setType(DataElementType.SELECT);
			de.setValues(tipoDumpConnettorePD);
			de.setSelected(dumpPD);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(dumpPD);
		}
		dati.addElement(de);
		
		String[] tipoDumpConnettorePA = {
				ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
				ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO
		};
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
		if(this.isModalitaAvanzata()){
			de.setType(DataElementType.SELECT);
			de.setValues(tipoDumpConnettorePA);
			de.setSelected(dumpPA);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(dumpPA);
		}
		dati.addElement(de);
		
		
	}
	
	public void addTracciamentoToDatiAsHidden(String registrazioneTracce, Configurazione configurazione, Vector<DataElement> dati) {
		DataElement de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO);
		de.setType(DataElementType.HIDDEN);
		de.setValue(registrazioneTracce);
		dati.addElement(de);
	}

	public void addTracciamentoToDati(String registrazioneTracce, Configurazione configurazione, Vector<DataElement> dati,	Boolean contaListe) {
		DataElement de;
		
		boolean showTitleSection = 
				(this.isModalitaCompleta()) 
				||
				(this.isModalitaAvanzata() && (this.confCore.isTracce_showConfigurazioneCustomAppender() || this.confCore.isTracce_showSorgentiDatiDatabase()));
				
		if(showTitleSection) {
			if(this.isModalitaAvanzata()){
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCE);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}
		}
	
		String[] tipoBuste = { 
				ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO,
				ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO
		};
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO);
		if(this.isModalitaCompleta()){
			de.setType(DataElementType.SELECT);
			de.setValues(tipoBuste);
			de.setSelected(registrazioneTracce);
			de.setPostBack(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(registrazioneTracce);
		}
		dati.addElement(de);
	
		if (this.isModalitaAvanzata()) {
			if (this.confCore.isTracce_showConfigurazioneCustomAppender()) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_LIST);
				if (contaListe) {
					int totAppender = 0;
					if (configurazione.getTracciamento() != null)
						totAppender =
						configurazione.getTracciamento().sizeOpenspcoopAppenderList();
					ServletUtils.setDataElementCustomLabel(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER, (long)totAppender);
				} else
					ServletUtils.setDataElementCustomLabel(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER);
				dati.addElement(de);
			}
			if (this.confCore.isTracce_showSorgentiDatiDatabase()) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_DATASOURCE_LIST);
				if (contaListe) {
					int totDs = 0;
					if (configurazione.getTracciamento() != null)
						totDs =
						configurazione.getTracciamento().sizeOpenspcoopSorgenteDatiList();
					ServletUtils.setDataElementCustomLabel(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SORGENTI_DATI, (long)totDs);
				} else
					ServletUtils.setDataElementCustomLabel(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SORGENTI_DATI);
				dati.addElement(de);
			}
		}
		
	}

	public void addMessaggiDiagnosticiToDatiAsHidden(String severita, String severita_log4j, Vector<DataElement> dati) {
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
		de.setType(DataElementType.HIDDEN);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
		de.setValue(severita);
		dati.addElement(de);

		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
		de.setType(DataElementType.HIDDEN);
		de.setValue(severita_log4j);
		dati.addElement(de);
	}
		
	public void addMessaggiDiagnosticiToDati(String severita, String severita_log4j, Configurazione configurazione,
			Vector<DataElement> dati, Boolean contaListe) {
		
		this.addSeveritaMessaggiDiagnosticiToDati(severita, severita_log4j, dati);

		if (this.isModalitaAvanzata()) {
			if (this.confCore.isMsgDiagnostici_showConfigurazioneCustomAppender()) {
				DataElement de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_APPENDER_LIST);
				if (contaListe) {
					int totAppender = 0;
					if (configurazione.getMessaggiDiagnostici() != null)
						totAppender =
						configurazione.getMessaggiDiagnostici().sizeOpenspcoopAppenderList();
					ServletUtils.setDataElementCustomLabel(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER, (long)totAppender);
				} else
					ServletUtils.setDataElementCustomLabel(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER);
				dati.addElement(de);
			}
			if (this.confCore.isMsgDiagnostici_showSorgentiDatiDatabase()) {
				DataElement de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_LIST);
				if (contaListe) {
					int totDs = 0;
					if (configurazione.getMessaggiDiagnostici() != null)
						totDs =
						configurazione.getMessaggiDiagnostici().sizeOpenspcoopSorgenteDatiList();
					ServletUtils.setDataElementCustomLabel(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SORGENTI_DATI, (long)totDs);
				} else
					ServletUtils.setDataElementCustomLabel(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SORGENTI_DATI);
				dati.addElement(de);
			}
		}
	}
	
	public Vector<DataElement> addConfigurazioneSistemaSelectListNodiCluster(Vector<DataElement> dati, String [] nodiSelezionati) throws Exception {
		
		DataElement de = new DataElement();
		de.setType(DataElementType.MULTI_SELECT);
		de.setValues(this.confCore.getJmxPdD_aliases());
		List<String> labels = new ArrayList<String>();
		for (String alias : this.confCore.getJmxPdD_aliases()) {
			labels.add(this.confCore.getJmxPdD_descrizione(alias));
		}
		de.setLabels(labels);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODI_CLUSTER);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
		de.setSize(this.getSize());
		de.setPostBack(true);
		if(labels.size()>10) {
			de.setRows(10);
		}
		else {
			de.setRows(labels.size());
		}
		de.setSelezionati(nodiSelezionati);
		dati.addElement(de);
		
				
		boolean resetAllCaches = false;
		for (String alias : this.confCore.getJmxPdD_aliases()) {
			
			Object gestoreRisorseJMX = this.confCore.getGestoreRisorseJMX(alias);
			
			List<String> caches = this.confCore.getJmxPdD_caches(alias);
			if(caches!=null && caches.size()>0){
				for (String cache : caches) {
				
					String stato = null;
					try{
						stato = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
								cache,
								this.confCore.getJmxPdD_cache_nomeAttributo_cacheAbilitata(alias));
						if(stato.equalsIgnoreCase("true")){
							stato = "abilitata";
						}
						else if(stato.equalsIgnoreCase("false")){
							stato = "disabilitata";
						}
						else{
							throw new Exception("Stato ["+stato+"] sconosciuto");
						}
					}catch(Exception e){
						this.log.error("Errore durante la lettura dello stato della cache ["+cache+"](jmxResourcePdD): "+e.getMessage(),e);
						stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
					}
					
					if("abilitata".equals(stato)){
						resetAllCaches = true;
						break;
					}
				}
			}
			if(resetAllCaches) {
				break;
			}
		}
		if(resetAllCaches){
			de = new DataElement();
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD+"?"+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE+"="+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES+
					"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO+"="+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO_RESET_ALL_CACHE_ALL_NODES);
			de.setType(DataElementType.LINK);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES);
			de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET_ALL_NODES);
			de.setSize(this.getSize());
			dati.addElement(de);
			
			StringBuilder sb = new StringBuilder("");
			if(nodiSelezionati!=null && nodiSelezionati.length>0) {
				for (int i = 0; i < nodiSelezionati.length; i++) {
					sb.append("&");
					sb.append(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODI_CLUSTER);
					sb.append("=");
					sb.append(nodiSelezionati[i]);
				}
			}
			de = new DataElement();
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD+"?"+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE+"="+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_SELECTED_CACHES+
					"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO+"="+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO_RESET_ALL_CACHE_SELECTED_NODES+
					sb.toString());
			de.setType(DataElementType.LINK);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_SELECTED_CACHES);
			de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET_SELECTED_NODES);
			de.setSize(this.getSize());
			dati.addElement(de);
			
			 Map<String, List<String>> map = this.confCore.getJmxPdD_gruppi_aliases();
			 if(map!=null && !map.isEmpty()) {
				 List<String> gruppi = new ArrayList<String>();
				 for (String gruppo : map.keySet()) {
					 gruppi.add(gruppo);
				 }
				 Collections.sort(gruppi);
				 int indexGr = 0;
				 for (String gruppo : gruppi) {
					 
					 indexGr++;
					 
					 List<String> aliases = map.get(gruppo);
					 StringBuilder sbGruppi = new StringBuilder("");
					 if(aliases!=null && aliases.size()>0) {
						 for (int i = 0; i < aliases.size(); i++) {
							 sbGruppi.append("&");
							 sbGruppi.append(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODI_CLUSTER);
							 sbGruppi.append("=");
							 sbGruppi.append(aliases.get(i));
						 }
						 
						 de = new DataElement();
						 de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD+"?"+
								 ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE+"="+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_SELECTED_CACHES+
								 "&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO+"="+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO_RESET_ALL_CACHE_SELECTED_NODES+
								 sbGruppi.toString());
						 de.setType(DataElementType.LINK);
						 de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_SELECTED_CACHES+"__gr"+indexGr);
						 de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET_GROUPES_NODES.replace(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET_GROUPES_NODES_KEYWORD, gruppo));
						 de.setSize(this.getSize());
						 dati.addElement(de);
					 }
					 
				 }
			 }
		}		
		
		return dati;
	}
	
	private void addInformazioneNonDisponibile(Vector<DataElement> dati, String label){
		DataElement de = newDataElementStyleRuntime();
		de.setLabel(label);
		de.setValue(ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE);
		de.setType(DataElementType.TEXT);
		de.setSize(this.getSize());
		dati.addElement(de);
	}
	
	private DataElement newDataElementStyleRuntime() {
		DataElement de = new DataElement();
		de.setLabelStyleClass(Costanti.LABEL_MEDIUM_CSS_CLASS);
		return de;
	}
	
	public Vector<DataElement> addConfigurazioneSistema(Vector<DataElement> dati, String alias) throws Exception {
	
		
		Object gestoreRisorseJMX = this.confCore.getGestoreRisorseJMX(alias);
		
		
		
		DataElement de = newDataElementStyleRuntime();
		de.setType(DataElementType.TITLE);
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_RUNTIME);
		dati.addElement(de);
				
		de = newDataElementStyleRuntime();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
		de.setLabel(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
		de.setType(DataElementType.HIDDEN);
		de.setValue(alias);
		dati.addElement(de);
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_EXPORT);
		de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_EXPORTER,
				new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER,alias));
		de.setType(DataElementType.LINK);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_EXPORT);
		de.setValue(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_EXPORT);
		de.setSize(this.getSize());
		dati.addElement(de);
		

		boolean resetAllCaches = false;
		List<String> caches = this.confCore.getJmxPdD_caches(alias);
		if(caches!=null && caches.size()>0){
			for (String cache : caches) {
			
				String stato = null;
				try{
					stato = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
							cache,
							this.confCore.getJmxPdD_cache_nomeAttributo_cacheAbilitata(alias));
					if(stato.equalsIgnoreCase("true")){
						stato = "abilitata";
					}
					else if(stato.equalsIgnoreCase("false")){
						stato = "disabilitata";
					}
					else{
						throw new Exception("Stato ["+stato+"] sconosciuto");
					}
				}catch(Exception e){
					this.log.error("Errore durante la lettura dello stato della cache ["+cache+"](jmxResourcePdD): "+e.getMessage(),e);
					stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
				}
				
				if("abilitata".equals(stato)){
					resetAllCaches = true;
					break;
				}
			}
		}
		if(resetAllCaches){
			de = newDataElementStyleRuntime();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD+"?"+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER+"="+alias+
					"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE+"="+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES+
					"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO+"="+this.confCore.getJmxPdD_cache_nomeMetodo_resetCache(alias));
			de.setType(DataElementType.LINK);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES);
			de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET);
			de.setSize(this.getSize());
			dati.addElement(de);
		}		
		

				
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_GENERALI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		String versionePdD = null;
		try{
			versionePdD = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_versionePdD(alias));
			if(this.isErroreHttp(versionePdD, "versione della PdD")){
				// e' un errore
				versionePdD = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura della versione della PdD (jmxResourcePdD): "+e.getMessage(),e);
			versionePdD = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		if(versionePdD!=null){
			versionePdD = StringEscapeUtils.escapeHtml(versionePdD);
		}
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_VERSIONE_PDD);
		de.setValue(versionePdD);
		de.setType(DataElementType.TEXT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_VERSIONE_PDD);
		de.setSize(this.getSize());
		dati.addElement(de);

		
		String versioneBaseDati = null;
		try{
			versioneBaseDati = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_versioneBaseDati(alias));
			if(this.isErroreHttp(versioneBaseDati, "versione della base dati")){
				// e' un errore
				versioneBaseDati = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura della versione della base dati (jmxResourcePdD): "+e.getMessage(),e);
			versioneBaseDati = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		if(versioneBaseDati!=null){
			versioneBaseDati = StringEscapeUtils.escapeHtml(versioneBaseDati);
		}
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_VERSIONE_BASE_DATI);
		de.setValue(versioneBaseDati);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_VERSIONE_BASE_DATI);
		de.setRows(4);
		de.setCols(60);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		
		String confDir = null;
		try{
			confDir = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione(alias));
			if(this.isErroreHttp(confDir, "directory di configurazione")){
				// e' un errore
				confDir = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura della directory di configurazione (jmxResourcePdD): "+e.getMessage(),e);
			confDir = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		if(confDir!=null){
			confDir = StringEscapeUtils.escapeHtml(confDir);
		}
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_DIRECTORY_CONFIGURAZIONE);
		de.setValue(confDir);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_DIRECTORY_CONFIGURAZIONE);
		de.setRows(4);
		de.setCols(60);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		
		String vendorJava = null;
		try{
			vendorJava = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_vendorJava(alias));
			if(this.isErroreHttp(vendorJava, "vendor di java")){
				// e' un errore
				vendorJava = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sul vendor di java (jmxResourcePdD): "+e.getMessage(),e);
			vendorJava = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		if(vendorJava!=null){
			vendorJava = StringEscapeUtils.escapeHtml(vendorJava);
		}
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_VENDOR_JAVA);
		de.setValue(vendorJava);
		de.setType(DataElementType.TEXT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_VENDOR_JAVA);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		
		String versioneJava = null;
		try{
			versioneJava = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_versioneJava(alias));
			if(this.isErroreHttp(versioneJava, "versione di java")){
				// e' un errore
				versioneJava = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura della versione di java (jmxResourcePdD): "+e.getMessage(),e);
			versioneJava = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		if(versioneJava!=null){
			versioneJava = StringEscapeUtils.escapeHtml(versioneJava);
		}
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_VERSIONE_JAVA);
		de.setValue(versioneJava);
		de.setType(DataElementType.TEXT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_VERSIONE_JAVA);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		
		
		
		String messageFactory = null;
		try{
			messageFactory = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_messageFactory(alias));
			if(this.isErroreHttp(messageFactory, "message factory")){
				// e' un errore
				messageFactory = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura della message factory (jmxResourcePdD): "+e.getMessage(),e);
			messageFactory = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
//		if(messageFactory!=null){
//			messageFactory = StringEscapeUtils.escapeHtml(messageFactory);
//		}
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MESSAGE_FACTORY);
		de.setValue(messageFactory.trim().contains(" ") ? messageFactory.trim().replaceAll(" ", "\n") : messageFactory);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setRows(2);
		de.setCols(60);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_MESSAGE_FACTORY);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setType(DataElementType.TITLE);
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_STATO_SERVIZI);
		dati.addElement(de);
		
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegata(alias));
			boolean enable = CostantiConfigurazione.ABILITATO.getValue().equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sullo stato del servizio Porta Delegata (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PD);
		}
		
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativa(alias));
			boolean enable = CostantiConfigurazione.ABILITATO.getValue().equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sullo stato del servizio Porta Applicativa (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_PA);
		}
			
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioIntegrationManager(alias));
			boolean enable = CostantiConfigurazione.ABILITATO.getValue().equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sullo stato del servizio Integration Manager (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_STATO_SERVIZIO_IM);
		}
		
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_DIAGNOSTICA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		try{
			String livelloSeverita = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici(alias));
			
			String[] tipoMsg = { LogLevels.LIVELLO_OFF, LogLevels.LIVELLO_FATAL, LogLevels.LIVELLO_ERROR_PROTOCOL, LogLevels.LIVELLO_ERROR_INTEGRATION, 
					LogLevels.LIVELLO_INFO_PROTOCOL, LogLevels.LIVELLO_INFO_INTEGRATION,
					LogLevels.LIVELLO_DEBUG_LOW, LogLevels.LIVELLO_DEBUG_MEDIUM, LogLevels.LIVELLO_DEBUG_HIGH,
					LogLevels.LIVELLO_ALL};
			de = newDataElementStyleRuntime();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
			de.setValues(tipoMsg);
			de.setSelected(livelloSeverita);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sul livello dei diagnostici (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
		}
		
		try{
			String livelloSeverita = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j(alias));
			
			String[] tipoMsg = { LogLevels.LIVELLO_OFF, LogLevels.LIVELLO_FATAL, LogLevels.LIVELLO_ERROR_PROTOCOL, LogLevels.LIVELLO_ERROR_INTEGRATION, 
					LogLevels.LIVELLO_INFO_PROTOCOL, LogLevels.LIVELLO_INFO_INTEGRATION,
					LogLevels.LIVELLO_DEBUG_LOW, LogLevels.LIVELLO_DEBUG_MEDIUM, LogLevels.LIVELLO_DEBUG_HIGH,
					LogLevels.LIVELLO_ALL};
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
			if(this.core.isVisualizzazioneConfigurazioneDiagnosticaLog4J()){
				de.setType(DataElementType.SELECT);
				de.setValues(tipoMsg);
				de.setSelected(livelloSeverita);
				de.setPostBack_viaPOST(true);
			}
			else{
				de.setType(DataElementType.TEXT);
				de.setValue(livelloSeverita);
			}
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sul livello dei diagnostici log4j (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
		}
		
		try{
			String log4j_diagnostica = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_log4j_diagnostica(alias));
			boolean enable = "true".equals(log4j_diagnostica);
			
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LOG4J_DIAGNOSTICA);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DIAGNOSTICA_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DIAGNOSTICA_NOTE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.TEXT);
			de.setValue(v);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sullo stato di log del file govway_diagnostici.log (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DIAGNOSTICA_LABEL);
		}
		
		try{
			String log4j_openspcoop = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_log4j_openspcoop(alias));
			boolean enable = "true".equals(log4j_openspcoop);
			
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LOG4J_OPENSPCOOP);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_OPENSPCOOP_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_OPENSPCOOP_NOTE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.TEXT);
			de.setValue(v);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sullo stato di log del file govway.log (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_OPENSPCOOP_LABEL);
		}
		
		try{
			String log4j_integrationManager = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_log4j_integrationManager(alias));
			boolean enable = "true".equals(log4j_integrationManager);
			
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LOG4J_INTEGRATION_MANAGER);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_INTEGRATION_MANAGER_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_INTEGRATION_MANAGER_NOTE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.TEXT);
			de.setValue(v);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sullo stato di log del file govway_integrationManager.log (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_INTEGRATION_MANAGER_LABEL);
		}

		
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_TRACCIAMENTO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_tracciamento(alias));
			boolean enable = "true".equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			if(this.isModalitaAvanzata()) {
				if(this.isModalitaCompleta()){
					de.setType(DataElementType.SELECT);
					de.setValues(tipoMsg);
					de.setSelected(v);
					de.setPostBack_viaPOST(true);
				}
				else{
					de.setType(DataElementType.TEXT);
					de.setValue(v);
				}
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(v);
			}
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sul tracciamento (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
		}
			
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_dumpPD(alias));
			boolean enable = "true".equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_CONNETTORE_PD_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_CONNETTORE_PD_NOTE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sul dump binario sulla Porta Delegata (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_CONNETTORE_PD_LABEL);
		}
			
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_dumpPA(alias));
			boolean enable = "true".equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_CONNETTORE_PA_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_CONNETTORE_PA_NOTE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sul dump binario sulla Porta Applicativa (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_CONNETTORE_PA_LABEL);
		}
		
		try{
			String log4j_tracciamento = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_log4j_tracciamento(alias));
			boolean enable = "true".equals(log4j_tracciamento);
			
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LOG4J_TRACCIAMENTO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_TRACCIAMENTO_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_TRACCIAMENTO_NOTE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.TEXT);
			de.setValue(v);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sullo stato di log del file govway_tracciamento.log (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_TRACCIAMENTO_LABEL);
		}
		
		try{
			String log4j_dump = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_log4j_dump(alias));
			boolean enable = "true".equals(log4j_dump);
			
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_NOTE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.TEXT);
			de.setValue(v);
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sullo stato di log del file govway_dump.log (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LOG4J_DUMP_LABEL);
		}
		
		try{
			String fileTraceGovWayState = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias),
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_getFileTrace(alias));
			FileTraceGovWayState state = FileTraceGovWayState.toConfig(fileTraceGovWayState);

			if(state.isEnabled()) {
				de = newDataElementStyleRuntime();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_FILE_TRACE_LABEL);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
			
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FILE_TRACE);
			if(state.isEnabled()) {
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_FILE_TRACE_STATO_LABEL);
			}
			else {
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_FILE_TRACE_LABEL);
			}
			String v = state.isEnabled() ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.TEXT);
			de.setValue(v);
			dati.addElement(de);
			
			if(state.isEnabled()) {
				
				de = newDataElementStyleRuntime();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FILE_TRACE_CONFIG);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_FILE_TRACE_CONFIGURAZIONE_LABEL);
				de.setType(DataElementType.TEXT);
				de.setValue(state.getPath());
				dati.addElement(de);
				
				String[] valori = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
				String[] label = { ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_FILE_TRACE_CONFIGURAZIONE_NOTE, state.getLastModified() };
				
				de = newDataElementStyleRuntime();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FILE_TRACE_UPDATE);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_FILE_TRACE_LAST_UPDATE_LABEL);
				de.setType(DataElementType.SELECT);
				de.setValues(valori);
				de.setLabels(label);
				de.setSelected(CostantiConfigurazione.DISABILITATO.getValue());
				de.setPostBack_viaPOST(true);
				dati.addElement(de);
				
			}
		
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sul FileTrace (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_FILE_TRACE_LABEL);
		}
		
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalRequestError(alias));
			boolean enable = "true".equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			String[] labeles = { ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST_ABILITATO, 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST_DISABILITATO };
			de.setLabels(labeles);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST+")";
			this.log.error("Errore durante la lettura delle informazioni (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_REQUEST);
		}
		
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeBadResponse(alias));
			String value2 = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalResponseError(alias));
			boolean enable = "true".equals(value) && "true".equals(value2);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			String[] labeles = { ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE_ABILITATO, 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE_DISABILITATO };
			de.setLabels(labeles);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE+")";
			this.log.error("Errore durante la lettura delle informazioni (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_PROCESS_RESPONSE);
		}
		
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalError(alias));
			boolean enable = "true".equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			String[] labeles = { ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR_ABILITATO, 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR_DISABILITATO };
			de.setLabels(labeles);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_TYPE+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR+")";
			this.log.error("Errore durante la lettura delle informazioni (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_TYPE_INTERNAL_ERROR);
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorStatusCode(alias));
			boolean enable = "true".equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE+")";
			this.log.error("Errore durante la lettura delle informazioni (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS_CODE);
		}
		
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorUseStatusCodeAsFaultCode(alias));
			boolean enable = "true".equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_STATUS+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT+")";
			this.log.error("Errore durante la lettura delle informazioni (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_USE_STATUS_CODE_AS_SOAP_FAULT);
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_DETAILS);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorDetails(alias));
			boolean enable = "true".equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_DETAILS+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS+")";
			this.log.error("Errore durante la lettura delle informazioni (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SPECIFIC_ERROR_DETAILS);
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorInstanceId(alias));
			boolean enable = "true".equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID+")";
			this.log.error("Errore durante la lettura delle informazioni (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_INSTANCE_ID);
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		try{
			String value = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorGenerateHttpHeaderGovWayCode(alias));
			boolean enable = "true".equals(value);
			
			String[] tipoMsg = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
			de = newDataElementStyleRuntime();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE);
			String v = enable ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue();
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(v);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
		}catch(Exception e){
			String tipo = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP+" ("+
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE+")";
			this.log.error("Errore durante la lettura delle informazioni (jmxResourcePdD) ["+tipo+"]: "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_TRANSACTION_ERROR_SOAP_GENERATE_HTTP_CODE);
		}
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_DATABASE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		String [] infoDatabase = null;
		try{
			String tmp = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase(alias));
			if(this.isErroreHttp(tmp, "informazioni sul database")){
				// e' un errore
				tmp = null;
			}
			infoDatabase = tmp.split("\n");
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sul database (jmxResourcePdD): "+e.getMessage(),e);
		}
		if(infoDatabase==null || infoDatabase.length<=0){
			addInformazioneNonDisponibile(dati, "");
		}
		else{
			for (int i = 0; i < infoDatabase.length; i++) {
				
				try{
					String label = infoDatabase[i];
					String value = "";
					if(infoDatabase[i].contains(":")){
						label = infoDatabase[i].split(":")[0];
						value = infoDatabase[i].split(":")[1];
					}
					
					de = newDataElementStyleRuntime();
					de.setLabel(label);
					if(value!=null){
						value = StringEscapeUtils.escapeHtml(value);
					}
					de.setValue(value);
					de.setType(DataElementType.TEXT);
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_DATABASE+i);
					de.setSize(this.getSize());
					dati.addElement(de);
				}catch(Exception e){
					this.log.error("Errore durante la lettura delle informazioni sul database (jmxResourcePdD): "+e.getMessage(),e);
				}
			}
		}
		
		HashMap<String, String> infoConnessioneAltriDB = null;
		HashMap<String, String> statoConnessioniAltriDB = null;
		try{
			int numeroDatasource = 0;
			try{
				String stato = this.confCore.readJMXAttribute(gestoreRisorseJMX,alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
						this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW(alias),
						this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_numeroDatasourceGW(alias));
				if(stato!=null && !"".equals(stato)) {
					if(this.isErroreHttp(stato, "stato delle connessioni verso altri database")){
						// e' un errore
						throw new Exception(stato);
					}
					numeroDatasource = Integer.valueOf(stato);
				}
			}catch(Exception e){
				ControlStationCore.logDebug("Numero di datasource attivi non ottenibili: "+e.getMessage());
			}
			if(numeroDatasource>0) {
				String nomiDatasource = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
						this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW(alias),
						this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_getDatasourcesGW(alias));
				if(nomiDatasource!=null && !"".equals(nomiDatasource)) {
					if(this.isErroreHttp(nomiDatasource, "stato delle connessioni verso altri database")){
						// e' un errore
						throw new Exception(nomiDatasource);
					}
					/* Esempio:
					 * 3 datasource allocati: 
	(2020-01-23_15:40:22.391) idDatasource:88c4db87-07a5-4fa6-95a5-e6caf4c21a7f jndiName:org.govway.datasource.tracciamento ConnessioniAttive:0
	(2020-01-23_15:40:22.396) idDatasource:bae6582a-659b-4b70-bc9c-aca3570b45af jndiName:org.govway.datasource.statistiche ConnessioniAttive:0
	(2020-01-23_15:40:22.627) idDatasource:4ff843af-94d6-4506-8ecf-aac52bcb3525 jndiName:org.govway.datasource.console ConnessioniAttive:0
					 **/
					String [] lines = nomiDatasource.split("\n");
					if(lines!=null && lines.length>0) {
						for (String line : lines) {
							if(line.startsWith("(")) {
								String [] tmp = line.split(" ");
								if(tmp!=null && tmp.length>3) {
									String nomeDS = tmp[2]+" "+tmp[1];
									try{
										String idDS = tmp[1].split(":")[1];
										
										String statoInfo = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
												this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW(alias),
												this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_getInformazioniDatabaseDatasourcesGW(alias),
												idDS);
										if(infoConnessioneAltriDB==null) {
											infoConnessioneAltriDB = new HashMap<String, String>();
										}
										infoConnessioneAltriDB.put(nomeDS,statoInfo);
										
										String statoDB = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
												this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW(alias),
												this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_getUsedConnectionsDatasourcesGW(alias),
												idDS);
										if(this.isErroreHttp(statoDB, "stato delle connessioni verso database "+nomeDS)){
											// e' un errore
											throw new Exception(statoDB);
										}
										if(statoConnessioniAltriDB==null) {
											statoConnessioniAltriDB = new HashMap<String, String>();
										}
										statoConnessioniAltriDB.put(nomeDS,statoDB);
										
									}catch(Exception e){
										ControlStationCore.logError("Errore durante la lettura delle informazioni verso il database "+nomeDS+" (jmxResourcePdD): "+e.getMessage(),e);
										
										if(infoConnessioneAltriDB==null) {
											infoConnessioneAltriDB = new HashMap<String, String>();
										}
										infoConnessioneAltriDB.put(nomeDS,ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE);
										
										if(statoConnessioniAltriDB==null) {
											statoConnessioniAltriDB = new HashMap<String, String>();
										}
										statoConnessioniAltriDB.put(nomeDS,ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE);
									}		
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la lettura delle informazioni verso gli altri database (jmxResourcePdD): "+e.getMessage(),e);
			
			if(infoConnessioneAltriDB==null) {
				infoConnessioneAltriDB = new HashMap<String, String>();
			}
			infoConnessioneAltriDB.put("GovWayDatasources",ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE);
			
			if(statoConnessioniAltriDB==null) {
				statoConnessioniAltriDB = new HashMap<String, String>();
			}
			statoConnessioniAltriDB.put("GovWayDatasources",ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE);
		}
		
		if(infoConnessioneAltriDB!=null && infoConnessioneAltriDB.size()>0) {
			Iterator<String> it = infoConnessioneAltriDB.keySet().iterator();
			int index = 0;
			while (it.hasNext()) {
				String idAltroDB = (String) it.next();
				String infoConnessioneAltroDB = infoConnessioneAltriDB.get(idAltroDB);
				
				de = newDataElementStyleRuntime();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_DATABASE+" "+idAltroDB.split(" ")[0]);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
				
				String [] infoConnessioneDatabase = infoConnessioneAltroDB.split("\n");
				
				if(infoConnessioneDatabase==null || infoConnessioneDatabase.length<=0){
					addInformazioneNonDisponibile(dati, "");
				}
				else{
					for (int i = 0; i < infoConnessioneDatabase.length; i++) {
						
						try{
							String label = infoConnessioneDatabase[i];
							String value = "";
							if(infoConnessioneDatabase[i].contains(":")){
								label = infoConnessioneDatabase[i].split(":")[0];
								value = infoConnessioneDatabase[i].split(":")[1];
							}
							
							de = newDataElementStyleRuntime();
							de.setLabel(label);
							if(value!=null){
								value = StringEscapeUtils.escapeHtml(value);
							}
							de.setValue(value);
							de.setType(DataElementType.TEXT);
							de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_DATABASE+"_db"+index+"_"+i);
							de.setSize(this.getSize());
							dati.addElement(de);
						}catch(Exception e){
							this.log.error("Errore durante la lettura delle informazioni sul database (jmxResourcePdD): "+e.getMessage(),e);
						}
					}
				}
				
				index++;
			}
		}
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_SSL);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		String [] infoSSL = null;
		try{
			String tmp = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniSSL(alias));
			if(this.isErroreHttp(tmp, "informazioni SSL")){
				// e' un errore
				tmp = null;
			}
			infoSSL = tmp.split("\n");
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni SSL (jmxResourcePdD): "+e.getMessage(),e);
		}
		if(infoSSL==null || infoSSL.length<=0){
			addInformazioneNonDisponibile(dati, "");
		}
		else{
			for (int i = 0; i < infoSSL.length; i++) {
				
				try{
					String label = infoSSL[i];
					String value = "";
					if(infoSSL[i].contains(":")){
						label = infoSSL[i].split(":")[0];
						value = infoSSL[i].split(":")[1];
					}
					
					de = newDataElementStyleRuntime();
					de.setLabel(label);
					if(value!=null){
						value = StringEscapeUtils.escapeHtml(value);
					}
					de.setValue(value);
					de.setType(DataElementType.TEXT);
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_SSL+i);
					de.setSize(this.getSize());
					dati.addElement(de);
				}catch(Exception e){
					this.log.error("Errore durante la lettura delle informazioni SSL (jmxResourcePdD): "+e.getMessage(),e);
				}
			}
		}
		
		
		
		if(this.core.isJmxPdD_configurazioneSistema_showInformazioniCryptographyKeyLength()){
		
			de = newDataElementStyleRuntime();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_CRYPTOGRAPHY_KEY_LENGTH);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			String [] infoCryptoKeyLength = null;
			try{
				String tmp = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
						this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
						this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCryptographyKeyLength(alias));
				if(this.isErroreHttp(tmp, "informazioni CryptographyKeyLength")){
					// e' un errore
					tmp = null;
				}
				infoCryptoKeyLength = tmp.split("\n");
			}catch(Exception e){
				this.log.error("Errore durante la lettura delle informazioni sulla lunghezza delle chiavi di cifratura (jmxResourcePdD): "+e.getMessage(),e);
			}
			if(infoCryptoKeyLength==null || infoCryptoKeyLength.length<=0){
				addInformazioneNonDisponibile(dati, "");
			}
			else{
				for (int i = 0; i < infoCryptoKeyLength.length; i++) {
					
					try{
						String label = infoCryptoKeyLength[i];
						String value = "";
						if(infoCryptoKeyLength[i].contains(":")){
							label = infoCryptoKeyLength[i].split(":")[0];
							value = infoCryptoKeyLength[i].split(":")[1];
						}
						
						de = newDataElementStyleRuntime();
						de.setLabel(label);
						if(value!=null){
							value = StringEscapeUtils.escapeHtml(value);
						}
						de.setValue(value);
						de.setType(DataElementType.TEXT);
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_CRYPTOGRAPHY_KEY_LENGTH+i);
						de.setSize(this.getSize());
						dati.addElement(de);
					}catch(Exception e){
						this.log.error("Errore durante la lettura delle informazioni sulla lunghezza delle chiavi di cifratura (jmxResourcePdD): "+e.getMessage(),e);
					}
				}
			}
		}
		
		
		
		
		
		
		
		
		
		

		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_CHARSET);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		String [] infoCharset = null;
		try{
			String tmp = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCharset(alias));
			if(this.isErroreHttp(tmp, "informazioni Charset")){
				// e' un errore
				tmp = null;
			}
			infoCharset = tmp.split("\n");
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sull'internazionalizzazione (jmxResourcePdD): "+e.getMessage(),e);
		}
		if(infoCharset==null || infoCharset.length<=0){
			addInformazioneNonDisponibile(dati, "");
		}
		else{
			for (int i = 0; i < infoCharset.length; i++) {
				
				try{
					String label = infoCharset[i];
					String value = "";
					if(infoCharset[i].contains(":")){
						label = infoCharset[i].split(":")[0];
						value = infoCharset[i].substring(infoCharset[i].indexOf(":")+1);
					}
					
					de = newDataElementStyleRuntime();
					if(value==null || "".equals(value)){
						value = label;
						label = "Name";
					}
					de.setLabel(label);
					if(value!=null){
						value = StringEscapeUtils.escapeHtml(value);
					}
					de.setValue(value);
					de.setType(DataElementType.TEXT);
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_CHARSET+i);
					de.setSize(this.getSize());
					dati.addElement(de);
				}catch(Exception e){
					this.log.error("Errore durante la lettura delle informazioni sul charset (jmxResourcePdD): "+e.getMessage(),e);
				}
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_INTERNAZIONALIZZAZIONE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		String [] infoInternazionalizzazione = null;
		try{
			String tmp = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniInternazionalizzazione(alias));
			if(this.isErroreHttp(tmp, "informazioni Internazionalizzazione")){
				// e' un errore
				tmp = null;
			}
			infoInternazionalizzazione = tmp.split("\n");
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sull'internazionalizzazione (jmxResourcePdD): "+e.getMessage(),e);
		}
		if(infoInternazionalizzazione==null || infoInternazionalizzazione.length<=0){
			addInformazioneNonDisponibile(dati, "");
		}
		else{
			for (int i = 0; i < infoInternazionalizzazione.length; i++) {
				
				try{
					String label = infoInternazionalizzazione[i];
					String value = "";
					if(infoInternazionalizzazione[i].contains(":")){
						label = infoInternazionalizzazione[i].split(":")[0];
						value = infoInternazionalizzazione[i].substring(infoInternazionalizzazione[i].indexOf(":")+1);
					}
					
					de = newDataElementStyleRuntime();
					if(value==null || "".equals(value)){
						value = label;
						label = "Name";
					}
					de.setLabel(label);
					if(value!=null){
						value = StringEscapeUtils.escapeHtml(value);
					}
					de.setValue(value);
					de.setType(DataElementType.TEXT);
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_INTERNAZIONALIZZAZIONE+i);
					de.setSize(this.getSize());
					dati.addElement(de);
				}catch(Exception e){
					this.log.error("Errore durante la lettura delle informazioni sull'internazionalizzazione (jmxResourcePdD): "+e.getMessage(),e);
				}
			}
		}
		
		
		
		
		
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_TIMEZONE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		String [] infoTimezone = null;
		try{
			String tmp = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniTimeZone(alias));
			if(this.isErroreHttp(tmp, "informazioni Internazionalizzazione")){
				// e' un errore
				tmp = null;
			}
			infoTimezone = tmp.split("\n");
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sul TimeZone (jmxResourcePdD): "+e.getMessage(),e);
		}
		if(infoTimezone==null || infoTimezone.length<=0){
			addInformazioneNonDisponibile(dati, "");
		}
		else{
			for (int i = 0; i < infoTimezone.length; i++) {
				
				try{
					String label = infoTimezone[i];
					String value = "";
					if(infoTimezone[i].contains(":")){
						label = infoTimezone[i].split(":")[0];
						value = infoTimezone[i].substring(infoTimezone[i].indexOf(":")+1);
					}
					
					de = newDataElementStyleRuntime();
					if(value==null || "".equals(value)){
						value = label;
						label = "Name";
					}
					de.setLabel(label);
					if(value!=null){
						value = StringEscapeUtils.escapeHtml(value);
					}
					de.setValue(value);
					de.setType(DataElementType.TEXT);
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_TIMEZONE+i);
					de.setSize(this.getSize());
					dati.addElement(de);
				}catch(Exception e){
					this.log.error("Errore durante la lettura delle informazioni sul TimeZone (jmxResourcePdD): "+e.getMessage(),e);
				}
			}
		}
		
		
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_JAVA_NET);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		String [] infoJavaNet = null;
		try{
			String tmp = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaNetworking(alias));
			if(this.isErroreHttp(tmp, "informazioni Java Networking")){
				// e' un errore
				tmp = null;
			}
			infoJavaNet = tmp.split("\n");
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni di Java Networking (jmxResourcePdD): "+e.getMessage(),e);
		}
		if(infoJavaNet==null || infoJavaNet.length<=0){
			addInformazioneNonDisponibile(dati, "");
		}
		else{
			for (int i = 0; i < infoJavaNet.length; i++) {
				
				try{
					if(infoJavaNet[i]==null || "".equals(infoJavaNet[i].trim())) {
						continue;
					}
					
					String label = infoJavaNet[i];
					String value = "";
					if(infoJavaNet[i].contains("=")){
						label = infoJavaNet[i].split("=")[0];
						value = infoJavaNet[i].substring(infoJavaNet[i].indexOf("=")+1);
					}
					
					de = newDataElementStyleRuntime();
					if(value==null || "".equals(value)){
						if(label.startsWith("SecurityManager ")) {
							String tmp = label;
							label = "SecurityManager";
							value = tmp.substring("SecurityManager ".length());
						}
						else {
							value = label;
							label = "Name";
						}
					}
					de.setLabel(label);
					if(value!=null){
						value = StringEscapeUtils.escapeHtml(value);
					}
					de.setValue(value);
					de.setType(DataElementType.TEXT);
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_JAVA_NET+i);
					de.setSize(this.getSize());
					dati.addElement(de);
				}catch(Exception e){
					this.log.error("Errore durante la lettura delle informazioni di Java Networking (jmxResourcePdD): "+e.getMessage(),e);
				}
			}
		}
		
		
		
		
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_INFO_PROTOCOLLI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		String [] infoProtocolli = null;
		try{
			String tmp = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsa(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols(alias));
			infoProtocolli = tmp.split("\n");
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sui protocolli (jmxResourcePdD): "+e.getMessage(),e);
		}
		if(infoProtocolli==null || infoProtocolli.length<=0){
			addInformazioneNonDisponibile(dati, "");
		}
		else{
			boolean addProtocollo = false;
			Hashtable<String, String> map = new Hashtable<String, String>();
			for (int i = 0; i < infoProtocolli.length; i++) {
				
				try{
					String context = infoProtocolli[i].split(" ")[0];
					String protocol = infoProtocolli[i].split(" ")[1];
					protocol = protocol.split(":")[1];
					protocol = protocol.substring(0, protocol.length()-1);
					if(map.containsKey(protocol)){
						String c = map.remove(protocol);
						map.put(protocol, (c+", "+context));
					}
					else{
						map.put(protocol, context);
					}
				}catch(Exception e){
					this.log.error("Errore durante la lettura delle informazioni sui protocolli (jmxResourcePdD): "+e.getMessage(),e);
				}
				
			}
			Enumeration<String> protocolli = map.keys();
			int index = 0;
			while (protocolli.hasMoreElements()) {
				String protocollo = (String) protocolli.nextElement();
				
				addProtocollo = true;
				
				de = newDataElementStyleRuntime();
				de.setLabel(protocollo);
				String value = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_PROTOCOLLO_CONTESTO+map.get(protocollo);
				if(value!=null){
					value = StringEscapeUtils.escapeHtml(value);
				}
				de.setValue(value);
				de.setType(DataElementType.TEXT);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_PROTOCOLLO+index);
				de.setSize(this.getSize());
				dati.addElement(de);
				
//				de = newDataElementStyleLong();
//				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_PROTOCOLLO);
//				de.setValue(protocollo);
//				de.setType(DataElementType.TEXT);
//				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_PROTOCOLLO+index);
//				de.setSize(this.getSize());
//				dati.addElement(de);
//				
//				de = newDataElementStyleLong();
//				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_PROTOCOLLO_CONTESTO);
//				de.setValue(map.get(protocollo));
//				de.setType(DataElementType.TEXT);
//				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_INFO_PROTOCOLLO_CONTESTO+index);
//				de.setSize(this.getSize());
//				dati.addElement(de);
				
				index++;
			}
			
			if(!addProtocollo){
				addInformazioneNonDisponibile(dati, "");
			}
		}
		
		caches = this.confCore.getJmxPdD_caches(alias);
		if(caches!=null && caches.size()>0){
			
			de = newDataElementStyleRuntime();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_CACHE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			for (String cache : caches) {
			
				de = newDataElementStyleRuntime();
				de.setLabel(cache);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
				
				String stato = null;
				try{
					stato = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
							cache,
							this.confCore.getJmxPdD_cache_nomeAttributo_cacheAbilitata(alias));
					if(stato.equalsIgnoreCase("true")){
						stato = "abilitata";
					}
					else if(stato.equalsIgnoreCase("false")){
						stato = "disabilitata";
					}
					else{
						throw new Exception("Stato ["+stato+"] sconosciuto");
					}
				}catch(Exception e){
					this.log.error("Errore durante la lettura dello stato della cache ["+cache+"](jmxResourcePdD): "+e.getMessage(),e);
					stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
				}
								
				if("abilitata".equals(stato)){
					
					de = newDataElementStyleRuntime();
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET);
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD+"?"+
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER+"="+alias+
							"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE+"="+cache+
							"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO+"="+this.confCore.getJmxPdD_cache_nomeMetodo_resetCache(alias));
					de.setType(DataElementType.LINK);
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET);
					de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET_SINGOLA);
					de.setSize(this.getSize());
					dati.addElement(de);
					
					if(this.confCore.getJmxPdD_caches_prefill(alias).contains(cache)){
						de = newDataElementStyleRuntime();
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_PREFILL);
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD+"?"+
								ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER+"="+alias+
								"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE+"="+cache+
								"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO+"="+this.confCore.getJmxPdD_cache_nomeMetodo_prefillCache(alias));
						de.setType(DataElementType.LINK);
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_PREFILL);
						de.setValue(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_PREFILL);
						de.setSize(this.getSize());
						dati.addElement(de);
					}
					
				}
				
				de = newDataElementStyleRuntime();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_STATO);
				de.setValue(stato);
				de.setType(DataElementType.TEXT);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_STATO);
				de.setSize(this.getSize());
				dati.addElement(de);
				
				if("abilitata".equals(stato)){
					
					String [] params = null;
					try{
						String tmp = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_cache_type(alias), 
								cache,
								this.confCore.getJmxPdD_cache_nomeMetodo_statoCache(alias));
						params = tmp.split("\n");
					}catch(Exception e){
						this.log.error("Errore durante la lettura dello stato della cache ["+cache+"](jmxResourcePdD): "+e.getMessage(),e);
						stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
					}
					
					if(params!=null && params.length>0){
						for (int i = 0; i < params.length; i++) {
							
							try{
								String label = params[i];
								String labelCorretta = label;
								String value = "";
								if(params[i].contains(":")){
									label = params[i].split(":")[0];
									labelCorretta = label;
									value = params[i].split(":")[1];
									
									if("ElementiInCache".equals(label)) {
										labelCorretta = "Elementi in Cache";
									}
									else if("MemoriaOccupata".equals(label)) {
										labelCorretta = "Memoria Occupata";
									}
									else if("IdleTime".equals(label)) {
										labelCorretta = "Idle Time";
									}
									else if("LifeTime".equals(label)) {
										labelCorretta = "Life Time";
									}
									
								}
								
								if(ConfigurazioneCostanti.CONFIGURAZIONE_SISTEMA_CACHE_STATO_ELEMENTI_VISUALIZZATI.contains(label)){
								
									de = newDataElementStyleRuntime();
									de.setLabel(labelCorretta);
									if(value!=null){
										value = StringEscapeUtils.escapeHtml(value);
									}
									de.setValue(value);
									de.setType(DataElementType.TEXT);
									de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_STATO+"_"+i);
									de.setSize(this.getSize());
									dati.addElement(de);
									
								}
									
							}catch(Exception e){
								this.log.error("Errore durante la lettura dello stato della cache ["+cache+"]: "+e.getMessage(),e);
							}
						}
					}
				}
			}
						
		}
			
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_CONNESSIONI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_CONNESSIONE_DATABASE);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		String stato = null;
		try{
			stato = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio(alias),
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_connessioniDB(alias));
			if(this.isErroreHttp(stato, "stato delle connessioni al database")){
				// e' un errore
				stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura dello stato delle connessioni al database (jmxResourcePdD): "+e.getMessage(),e);
			stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CONNESSIONI_STATO);
		if(stato!=null){
			stato = StringEscapeUtils.escapeHtml(stato);
		}
		de.setValue(stato);
		de.setLabelAffiancata(false);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CONNESSIONI_DB);
		de.setSize(this.getSize());
		de.setRows(6);
		de.setCols(80);
		dati.addElement(de);
		
		
		// statoConnessioniAltriDB, letto prima durante l'acquisizione delle informazion
		if(statoConnessioniAltriDB!=null && statoConnessioniAltriDB.size()>0) {
			Iterator<String> it = statoConnessioniAltriDB.keySet().iterator();
			int index = 0;
			while (it.hasNext()) {
				String idAltroDB = (String) it.next();
				String statoConnessioniAltroDB = statoConnessioniAltriDB.get(idAltroDB);
				
				de = newDataElementStyleRuntime();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_CONNESSIONE_DATABASE+" "+idAltroDB.split(" ")[0]);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
				
				de = newDataElementStyleRuntime();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CONNESSIONI_STATO);
				if(statoConnessioniAltroDB!=null){
					statoConnessioniAltroDB = StringEscapeUtils.escapeHtml(statoConnessioniAltroDB);
				}
				de.setValue(statoConnessioniAltroDB);
				de.setLabelAffiancata(false);
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CONNESSIONI_DB+"_ds"+index++);
				de.setSize(this.getSize());
				de.setRows(6);
				de.setCols(80);
				dati.addElement(de);
			}
		}
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_CONNESSIONE_JMS);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		stato = null;
		try{
			stato = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio(alias),
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_connessioniJMS(alias));
			if(this.isErroreHttp(stato, "stato delle connessioni JMS")){
				// e' un errore
				stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura dello stato delle connessioni JMS (jmxResourcePdD): "+e.getMessage(),e);
			stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CONNESSIONI_STATO);
		if(stato!=null){
			stato = StringEscapeUtils.escapeHtml(stato);
		}
		de.setValue(stato);
		de.setLabelAffiancata(false);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CONNESSIONI_JMS);
		de.setSize(this.getSize());
		de.setRows(6);
		de.setCols(80);
		dati.addElement(de);
		
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_TRANSAZIONI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_TRANSAZIONI_ID);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		stato = null;
		try{
			stato = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio(alias),
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_idTransazioniAttive(alias));
			if(this.isErroreHttp(stato, "identificativi delle transazioni attive")){
				// e' un errore
				stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura degli identificativi delle transazioni attive (jmxResourcePdD): "+e.getMessage(),e);
			stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TRANSAZIONI_STATO);
		if(stato!=null){
			stato = StringEscapeUtils.escapeHtml(stato);
		}
		de.setValue(stato);
		de.setLabelAffiancata(false);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_TRANSAZIONI_ID);
		de.setSize(this.getSize());
		de.setRows(6);
		de.setCols(80);
		dati.addElement(de);
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_TRANSAZIONI_ID_PROTOCOLLO);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		stato = null;
		try{
			stato = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio(alias),
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_idProtocolloTransazioniAttive(alias));
			if(this.isErroreHttp(stato, "identificativi di protocollo delle transazioni attive")){
				// e' un errore
				stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura degli identificativi di protocollo delle transazioni attive (jmxResourcePdD): "+e.getMessage(),e);
			stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_TRANSAZIONI_STATO);
		if(stato!=null){
			stato = StringEscapeUtils.escapeHtml(stato);
		}
		de.setValue(stato);
		de.setLabelAffiancata(false);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_TRANSAZIONI_ID_PROTOCOLLO);
		de.setSize(this.getSize());
		de.setRows(6);
		de.setCols(80);
		dati.addElement(de);
		
		
		
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_CONNESSIONI_HTTP);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
				
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiVerificaConnessioniAttive(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_VERIFICA_CONNESSIONI_ATTIVE);
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_CONNESSIONE_PD);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		stato = null;
		try{
			stato = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio(alias),
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_connessioniPD(alias));
			if(this.isErroreHttp(stato, "stato delle connessioni http verso le PD")){
				// e' un errore
				stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura dello stato delle connessioni http verso le PD (jmxResourcePdD): "+e.getMessage(),e);
			stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CONNESSIONI_STATO);
		if(stato!=null){
			stato = StringEscapeUtils.escapeHtml(stato);
		}
		de.setValue(stato);
		de.setLabelAffiancata(false);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CONNESSIONI_PD);
		de.setSize(this.getSize());
		de.setRows(6);
		de.setCols(80);
		dati.addElement(de);
		
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_CONNESSIONE_PA);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		stato = null;
		try{
			stato = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio(alias),
					this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_connessioniPA(alias));
			if(this.isErroreHttp(stato, "stato delle connessioni http verso le PA")){
				// e' un errore
				stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
		}catch(Exception e){
			this.log.error("Errore durante la lettura dello stato delle connessioni http verso le PA (jmxResourcePdD): "+e.getMessage(),e);
			stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CONNESSIONI_STATO);
		if(stato!=null){
			stato = StringEscapeUtils.escapeHtml(stato);
		}
		de.setValue(stato);
		de.setLabelAffiancata(false);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CONNESSIONI_PA);
		de.setSize(this.getSize());
		de.setRows(6);
		de.setCols(80);
		dati.addElement(de);
		
		
		
		
		
		
		
		
		List<String> code = this.confCore.getConsegnaNotificaCode();
		
		if(code.size()<=1) {
			de = newDataElementStyleRuntime();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_THREADS);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_NOTIFICHE);
		if(code.size()<=1) {
			de.setType(DataElementType.SUBTITLE);
		}
		else {
			de.setType(DataElementType.TITLE);
		}
		dati.addElement(de);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerConsegnaContenutiApplicativi(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CONSEGNA_CONTENUTI_APPLICATIVI, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CONSEGNA_CONTENUTI_APPLICATIVI);
				
		
		for (String coda : code) {

			String labelCoda = this.confCore.getConsegnaNotificaCodaLabel(coda);
			
			if(code.size()>1) {
				de = newDataElementStyleRuntime();
				de.setLabel(labelCoda);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
			
			stato = null;
			try{
				stato = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
						this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi(alias),
						this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_getThreadPoolStatus(alias),
						coda);
				if(this.isErroreHttp(stato, "stato del thread pool per la consegna agli applicativi")){
					// e' un errore
					stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
				}
			}catch(Exception e){
				this.log.error("Errore durante la lettura dello stato del thread pool per la consegna agli applicativi (jmxResourcePdD): "+e.getMessage(),e);
				stato = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
			
			de = newDataElementStyleRuntime();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_THREAD_POOL_STATO);
			if(stato!=null){
				stato = StringEscapeUtils.escapeHtml(stato);
			}
			de.setValue(stato);
			de.setLabelAffiancata(false);
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_THREADS_CONSEGNA_APPLICATIVI);
			de.setSize(this.getSize());
			de.setRows(2);
			de.setCols(80);
			dati.addElement(de);
			
			String configurazioneCoda = null;
			try{
				configurazioneCoda = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
						this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi(alias),
						this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_getQueueConfig(alias),
						coda);
				if(this.isErroreHttp(configurazioneCoda, "Configurazione del thread pool '"+labelCoda+"' per la consegna agli applicativi")){
					// e' un errore
					configurazioneCoda = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
				}
			}catch(Exception e){
				this.log.error("Errore durante la lettura della configurazione del thread pool '"+labelCoda+"' per la consegna agli applicativi (jmxResourcePdD): "+e.getMessage(),e);
				configurazioneCoda = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
			
			de = newDataElementStyleRuntime();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_POOL_CONFIG);
			if(configurazioneCoda!=null){
				configurazioneCoda = StringEscapeUtils.escapeHtml(configurazioneCoda);
			}
			de.setValue(configurazioneCoda);
			de.setLabelAffiancata(false);
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_THREADS_CONSEGNA_APPLICATIVI_CONFIG);
			de.setSize(this.getSize());
			de.setRows(2);
			de.setCols(80);
			dati.addElement(de);
			
			String connettoriPrioritari = null;
			try{
				connettoriPrioritari = this.confCore.invokeJMXMethod(gestoreRisorseJMX, alias,this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
						this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi(alias),
						this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_getConnettoriPrioritari(alias),
						coda);
				if(this.isErroreHttp(connettoriPrioritari, "Connettori prioritari del thread pool '"+labelCoda+"' per la consegna agli applicativi")){
					// e' un errore
					connettoriPrioritari = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
				}
			}catch(Exception e){
				this.log.error("Errore durante la lettura dei connettori prioritari del thread pool '"+labelCoda+"' per la consegna agli applicativi (jmxResourcePdD): "+e.getMessage(),e);
				connettoriPrioritari = ConfigurazioneCostanti.LABEL_INFORMAZIONE_NON_DISPONIBILE;
			}
			
			de = newDataElementStyleRuntime();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_POOL_CONNNETTORI_PRIORITARI);
			if(connettoriPrioritari!=null){
				connettoriPrioritari = StringEscapeUtils.escapeHtml(connettoriPrioritari);
			}
			de.setValue(connettoriPrioritari);
			de.setLabelAffiancata(false);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_THREADS_CONSEGNA_APPLICATIVI_CONNNETTORI_PRIORITARI);
			if("".equals(connettoriPrioritari)) {
				de.setType(DataElementType.TEXT);
				de.setValue("Nessun Connettore");
			}
			else {
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setSize(this.getSize());
				de.setRows(2);
				de.setCols(80);
			}
			dati.addElement(de);
			
			if(!"".equals(connettoriPrioritari)) {
				de = newDataElementStyleRuntime();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_THREAD_POOL_ELIMINA_CONNETTORI_PRIORITARI);
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD+"?"+
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER+"="+alias+
						"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE+"="+coda+
						"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO+"="+this.confCore.getJmxPdD_configurazioneSistema_nomeMetodo_resetConnettoriPrioritari(alias));
				de.setType(DataElementType.LINK);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_THREAD_POOL_ELIMINA_CONNETTORI_PRIORITARI);
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_THREAD_POOL_ELIMINA_CONNETTORI_PRIORITARI);
				de.setSize(this.getSize());
				dati.addElement(de);
			}
		}
		
		
		if(code.size()>1) {
			de = newDataElementStyleRuntime();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_THREADS);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
			
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_TIMERS_STATISTICHE);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);

		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheOrarie(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_ORARIE);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheGiornaliere(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_GIORNALIERE);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheSettimanali(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_SETTIMANALI);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheMensili(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_STATISTICHE_MENSILI);
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_TIMERS_RUNTIME);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);

		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaMessaggiEliminati(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_ELIMINATI);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaMessaggiScaduti(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_SCADUTI);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreRepositoryBuste(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_REPOSITORY_BUSTE);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaCorrelazioneApplicativa(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_CORRELAZIONE_APPLICATIVA);
		
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaMessaggiNonGestiti(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_MESSAGGI_PULIZIA_MESSAGGI_NON_GESTITI);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestorePuliziaMessaggiAnomali(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_PULIZIA_MESSAGGI_ANOMALI);
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_TIMERS_MONITORAGGIO);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);

		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerMonitoraggioRisorseThread(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MONITORAGGIO_RISORSE_THREAD);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerThresholdThread(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_THRESHOLD_THREAD);
		
		
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA_TIMERS_SISTEMA);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);

		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerEventi(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_EVENTI);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerFileSystemRecovery(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_FILE_SYSTEM_RECOVERY);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreBusteOnewayNonRiscontrate(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ONEWAY_NON_RISCONTRATE);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreBusteAsincroneNonRiscontrate(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_GESTORE_BUSTE_ASINCRONE_NON_RISCONTRATE);
		
		addTimerState(dati, gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_nomeAttributo_timerRepositoryStatefulThread(alias), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_REPOSITORY_STATEFUL_THREAD);

		
		return dati;
	}

	private void addTimerState(Vector<DataElement> dati, Object gestoreRisorseJMX, String alias, String nomeAttributo, String nomeParametro, String labelParametro) {
		try{
			String stato = this.confCore.readJMXAttribute(gestoreRisorseJMX, alias, this.confCore.getJmxPdD_configurazioneSistema_type(alias), 
					this.confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias), 
					nomeAttributo);
			
			TimerState timerState = TimerState.valueOf(stato);
			
			DataElement de = newDataElementStyleRuntime();
			de.setName(nomeParametro);
			de.setLabel(labelParametro);
			if(TimerState.OFF.equals(timerState)) {
				de.setType(DataElementType.TEXT);
				de.setValue(TimerState.OFF.name());
			}
			else {
				String[] labeles = { CostantiConfigurazione.ABILITATO.getValue(), CostantiConfigurazione.DISABILITATO.getValue() };
				String[] values = { TimerState.ENABLED.name(), TimerState.DISABLED.name() };
				de.setType(DataElementType.SELECT);
				de.setValues(values);
				de.setLabels(labeles);
				de.setSelected(stato);
				de.setPostBack_viaPOST(true);
			}
			dati.addElement(de);
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni sul '"+nomeAttributo+"' (jmxResourcePdD): "+e.getMessage(),e);
			addInformazioneNonDisponibile(dati, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
		}
	}
	
	public boolean isErroreHttp(String stato, String risorsa){
		if(stato!=null && stato.startsWith("[httpCode ")){
			this.log.error("Errore durante la lettura della risorsa ["+risorsa+"]: "+stato);
			return true;
		}
		return false;
	}
	

	
	public boolean checkConfigurazioneTracciamento(TipoOperazione tipoOperazione, String configurazioneEsiti)	throws Exception {
		// E' possibile disabilitare anche tutti gli esiti
//		if(configurazioneEsiti ==null || "".equals(configurazioneEsiti.trim())){
//			this.pd.setMessage("Deve essere selezionato almeno un esito");
//			return false;
//		}
		
		//String severita = this.getParameter("severita");
		//String severita_log4j = this.getParameter("severita_log4j");
		String registrazioneTracce = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
		String dumpApplicativo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_APPLICATIVO);
		String dumpPD = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
		String dumpPA = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
		
		if (!registrazioneTracce.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) &&
				!registrazioneTracce.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
			this.pd.setMessage("Buste dev'essere abilitato o disabilitato");
			return false;
		}
		if (!dumpApplicativo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) &&
				!dumpApplicativo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
			this.pd.setMessage("Dump Applicativo dev'essere abilitato o disabilitato");
			return false;
		}
		if (!dumpPD.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) &&
				!dumpPD.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
			this.pd.setMessage("Dump Binario Porta Delegata dev'essere abilitato o disabilitato");
			return false;
		}
		if (!dumpPA.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) &&
				!dumpPA.equals(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO)) {
			this.pd.setMessage("Dump Binario Porta Applicativa dev'essere abilitato o disabilitato");
			return false;
		}
		
		return true;
	}
	
	public void addConfigurazionControlloTrafficoToDati(Vector<DataElement> dati, TipoOperazione tipoOperazione, org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico, long sizePolicy, long sizeGlobalPolicy, boolean configurazioneTerminata) throws Exception {
		
		
		boolean first = this.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME); 
		
		// Limitazione
		
		this.addToDatiConfigurazioneControlloTraffico(dati, tipoOperazione, configurazioneControlloTraffico.getControlloTraffico());
		
							
		// Rate Limiting
		
		this.addToDatiConfigurazioneRateLimiting(dati, tipoOperazione, configurazioneControlloTraffico, first, configurazioneTerminata,sizePolicy,sizeGlobalPolicy);
		
		
		// Tempi di Risposta
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		this.addToDatiTempiRispostaFruizione(dati, tipoOperazione, true, configurazioneControlloTraffico.getTempiRispostaFruizione());
		
		this.addToDatiTempiRispostaErogazione(dati, tipoOperazione, true, configurazioneControlloTraffico.getTempiRispostaErogazione());
		
		// Cache
		this.addToDatiConfigurazioneCache(dati, tipoOperazione, configurazioneControlloTraffico.getCache(), this.isModalitaAvanzata());
		
		// Set First Time == false
		this.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
			
	}
	
	public void addToDatiConfigurazioneControlloTraffico(Vector<DataElement> dati, TipoOperazione tipoOperazione, ConfigurazioneControlloTraffico controlloTraffico) throws Exception {
	
		// **** Limitazione Numero di Richieste Complessive Gestite dalla PdD ****
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_LIMITAZIONE_NUMERO_RICHIESTE_COMPLESSIVE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// stato
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_STATO);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_STATO);
		de.setType(DataElementType.SELECT);
		de.setValues(ConfigurazioneCostanti.STATI_CON_WARNING);
		if(controlloTraffico.isControlloMaxThreadsEnabled()) {
			if(controlloTraffico.isControlloMaxThreadsWarningOnly()) {
				de.setSelected(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.getValue());
			}
			else {
				de.setSelected(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.getValue());
			}
		}
		else {
			de.setSelected(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.getValue());
		}
		de.setPostBack(true);
		dati.addElement(de);
		
		
		// soglia
		Long numeroThreadComplessivi = null;
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_SOGLIA);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_SOGLIA);
		if(controlloTraffico.isControlloMaxThreadsEnabled()) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		if(controlloTraffico!=null && 
				controlloTraffico.getControlloMaxThreadsSoglia()!=null){
			numeroThreadComplessivi = controlloTraffico.getControlloMaxThreadsSoglia();
			de.setValue(numeroThreadComplessivi+"");
		}
		dati.addElement(de);
	
				
		// messaggio di errore
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE_DESCRIZIONE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE_DESCRIZIONE);
		if(!this.isModalitaStandard() && controlloTraffico.isControlloMaxThreadsEnabled() && (controlloTraffico.isControlloMaxThreadsWarningOnly() == false)) {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(controlloTraffico.isControlloMaxThreadsTipoErroreIncludiDescrizione());
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(controlloTraffico.isControlloMaxThreadsTipoErroreIncludiDescrizione()+"");
		}
		dati.addElement(de);
			
		// tipo errore
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE);
		if(controlloTraffico.isControlloMaxThreadsEnabled() && (controlloTraffico.isControlloMaxThreadsWarningOnly() == false)) {
			if(this.isModalitaStandard()) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(ConfigurazioneCostanti.TIPI_ERRORE);
				de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_ERRORE);
				if(controlloTraffico.getControlloMaxThreadsTipoErrore()!=null) {
					TipoErrore tipoErroEnum = TipoErrore.toEnumConstant(controlloTraffico.getControlloMaxThreadsTipoErrore());
					if(tipoErroEnum!=null) {
						de.setSelected(tipoErroEnum.getValue());
					}
				}
			}
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		if(controlloTraffico.getControlloMaxThreadsTipoErrore()!=null) {
			TipoErrore tipoErroEnum = TipoErrore.toEnumConstant(controlloTraffico.getControlloMaxThreadsTipoErrore());
			if(tipoErroEnum!=null) {
				de.setValue(tipoErroEnum.getValue());
			}
		}
		dati.addElement(de);
		
		if(controlloTraffico.isControlloMaxThreadsEnabled()) {
			
			// Link visualizza stato 
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_VISUALIZZA_STATO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_VISUALIZZA_STATO);
			de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_VISUALIZZA_STATO);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RUNTIME);
			de.setType(DataElementType.LINK);
			dati.addElement(de);
			
		}
		
		
		
		// *** Controllo del Traffico ***
		
		if(controlloTraffico.isControlloMaxThreadsEnabled()) {
		
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_STATO_CONTROLLO_CONGESTIONE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
		
			// stato
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_STATO_CONTROLLO_CONGESTIONE);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_STATO_CONTROLLO_CONGESTIONE);
			de.setType(DataElementType.SELECT);
			de.setValues(ConfigurazioneCostanti.STATI);
			de.setSelected(controlloTraffico.isControlloCongestioneEnabled() ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue());
			de.setPostBack(true);
			dati.addElement(de);
					
			
			// threshold
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_THRESHOLD_CONTROLLO_CONGESTIONE);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_THRESHOLD_CONTROLLO_CONGESTIONE);
			if(controlloTraffico.isControlloCongestioneEnabled()){
				de.setType(DataElementType.SELECT);
				String [] v = new String[100];
				for (int i = 0; i < 100; i++) {
					v[i] = (i+1)+"";
				}
				de.setValues(v);
				if(controlloTraffico.getControlloCongestioneThreshold()!=null){
					de.setSelected(controlloTraffico.getControlloCongestioneThreshold()+"");
				}
				de.setPostBack(true);
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			if(controlloTraffico.getControlloCongestioneThreshold()!=null){
				de.setValue(controlloTraffico.getControlloCongestioneThreshold()+"");
			}
			dati.addElement(de);
			
			Integer soglia = controlloTraffico.getControlloCongestioneThreshold();
			Long numeroThreadCongestionamento = null; 
			if(numeroThreadComplessivi!=null && soglia!=null){
				double numD = numeroThreadComplessivi.doubleValue();
				double totale = 100d;
				double sogliaD = soglia.doubleValue();
				Double numeroThreadCongestionamentoD = (numD / totale) *  sogliaD;
				numeroThreadCongestionamento = numeroThreadCongestionamentoD.longValue();
			}
			if(numeroThreadCongestionamento!=null && controlloTraffico.isControlloCongestioneEnabled()){
				de = new DataElement();
				de.setType(DataElementType.NOTE);
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_CONGESTIONE_THRESHOLD_DESCRIZIONE.
						replace(ConfigurazioneCostanti.CONFIGURAZIONE_CONTROLLO_CONGESTIONE_THRESHOLD_DESCRIZIONE_TEMPLATE, numeroThreadCongestionamento+""));
				dati.addElement(de);
			}
					
		}
	}
	
	public void addToDatiConfigurazioneRateLimiting(Vector<DataElement> dati, TipoOperazione tipoOperazione, org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale controlloTraffico, boolean first, boolean finished, long sizePolicy, long sizeGlobalPolicy) throws Exception {
			
		// Policy
//		if( first || finished ){

		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
				
		// messaggio di errore
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_TIPOLOGIA_ERRORE_DESCRIZIONE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_TIPOLOGIA_ERRORE_DESCRIZIONE);
		if(this.isModalitaStandard()) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(controlloTraffico.getRateLimiting().isTipoErroreIncludiDescrizione()+"");
		}
		else {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(controlloTraffico.getRateLimiting().isTipoErroreIncludiDescrizione());
		}
		dati.addElement(de);
		
		// tipo errore
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_TIPOLOGIA_ERRORE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_TIPOLOGIA_ERRORE);
		TipoErrore tipoErroEnum = null;
		if(controlloTraffico.getRateLimiting().getTipoErrore()!=null) {
			tipoErroEnum = TipoErrore.toEnumConstant(controlloTraffico.getRateLimiting().getTipoErrore());
		}	
		if(this.isModalitaStandard()) {
			de.setType(DataElementType.HIDDEN);
			if(tipoErroEnum!=null) {
				de.setValue(tipoErroEnum.getValue());
			}
		}
		else {
			de.setType(DataElementType.SELECT);
			de.setValues(ConfigurazioneCostanti.TIPI_ERRORE);
			de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_ERRORE);
			if(tipoErroEnum!=null) {
				de.setSelected(tipoErroEnum.getValue());
			}
		}
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_LINK);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_LINK);
		de.setType(DataElementType.LINK);
		de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY_LIST);
		de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO_POLICY+" (" +sizePolicy+ ")");
		dati.addElement(de);
				
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK);
		de.setType(DataElementType.LINK);
		de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST);
		de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK+" (" + sizeGlobalPolicy+ ")");
		dati.addElement(de);
//		}
		
	}
	
	public void addConfigurazioneControlloTrafficoJmxStateToDati(Vector<DataElement> dati, TipoOperazione tipoOperazione) throws Exception {
		// jmx
		
		List<String> aliases = this.getCore().getJmxPdD_aliases();
		if(aliases==null || aliases.size()<=0){
			throw new Exception("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
		}
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_STATO_RUNTIME);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Link visualizza stato 
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_VISUALIZZA_STATO);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_VISUALIZZA_STATO_REFRESH);
		de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_VISUALIZZA_STATO_REFRESH);
		de.setUrl(org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RUNTIME);
		de.setType(DataElementType.LINK);
		dati.addElement(de);
		
		
		for (String alias : aliases) {
			
			String descrizioneAlias = this.getCore().getJmxPdD_descrizione(alias);
			
			de = new DataElement();
			de.setLabel(descrizioneAlias);
			de.setValue(descrizioneAlias);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			String threadsAttivi = null;
			try{
				threadsAttivi = this.getCore().readJMXAttribute(this.getCore().getGestoreRisorseJMX(alias), alias, JMXConstants.JMX_TYPE, 
						JMXConstants.JMX_NAME, JMXConstants.CC_ATTRIBUTE_ACTIVE_THREADS);
			}catch(Exception e){
				String errorMessage = "Errore durante il recupero dell'attributo ["+JMXConstants.CC_ATTRIBUTE_ACTIVE_THREADS+"] sulla risorsa ["+JMXConstants.JMX_NAME+"]: "+e.getMessage();
				ControlStationCore.getLog().error(errorMessage,e);
				threadsAttivi = errorMessage;
			}
			
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_VISUALIZZA_STATO_THREADS_ATTIVI);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_VISUALIZZA_STATO_THREADS_ATTIVI);
			de.setType(DataElementType.TEXT);
			de.setValue(threadsAttivi);
			dati.addElement(de);
			
			String pddCongestionata = null;
			try{
				pddCongestionata = this.getCore().readJMXAttribute(this.getCore().getGestoreRisorseJMX(alias), alias, JMXConstants.JMX_TYPE, 
						JMXConstants.JMX_NAME, JMXConstants.CC_ATTRIBUTE_PDD_CONGESTIONATA);
			}catch(Exception e){
				String errorMessage = "Errore durante il recupero dell'attributo ["+JMXConstants.CC_ATTRIBUTE_PDD_CONGESTIONATA+"] sulla risorsa ["+JMXConstants.JMX_NAME+"]: "+e.getMessage();
				ControlStationCore.getLog().error(errorMessage,e);
				pddCongestionata = errorMessage;
			}
			
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_VISUALIZZA_STATO_CONGESTIONE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_VISUALIZZA_STATO_CONGESTIONE);
			de.setType(DataElementType.TEXT);
			de.setValue(pddCongestionata);
			dati.addElement(de);
			
			this.getPd().disableEditMode();
			
		}
	}
	
	public void addToDatiTempiRispostaFruizione(Vector<DataElement> dati, TipoOperazione tipoOperazione, boolean editEnabled, TempiRispostaFruizione tempiRispostaFruizione) throws Exception {
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_FRUIZIONI);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT_LABEL);
		de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_MILLISECONDI_NOTE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT_FRUIZIONE);
		if(editEnabled){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		if(tempiRispostaFruizione.getConnectionTimeout()!=null)
			de.setValue(tempiRispostaFruizione.getConnectionTimeout()+"");
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_READ_TIMEOUT_LABEL);
		de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_MILLISECONDI_NOTE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_READ_TIMEOUT_FRUIZIONE);
		if(editEnabled){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		if(tempiRispostaFruizione.getReadTimeout()!=null)
			de.setValue(tempiRispostaFruizione.getReadTimeout()+"");
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA_LABEL);
		de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_MILLISECONDI_NOTE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA_FRUIZIONE);
		if(editEnabled){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		if(tempiRispostaFruizione.getTempoMedioRisposta()!=null)
			de.setValue(tempiRispostaFruizione.getTempoMedioRisposta()+"");
		dati.addElement(de);
		
	}
	
	
	public void addToDatiTempiRispostaErogazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, boolean editEnabled, TempiRispostaErogazione tempiRispostaErogazione) throws Exception {
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_EROGAZIONI);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT_LABEL);
		de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_MILLISECONDI_NOTE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT_EROGAZIONE);
		if(editEnabled){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		if(tempiRispostaErogazione.getConnectionTimeout()!=null)
			de.setValue(tempiRispostaErogazione.getConnectionTimeout()+"");
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_READ_TIMEOUT_LABEL);
		de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_MILLISECONDI_NOTE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_READ_TIMEOUT_EROGAZIONE);
		if(editEnabled){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		if(tempiRispostaErogazione.getReadTimeout()!=null)
			de.setValue(tempiRispostaErogazione.getReadTimeout()+"");
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA_LABEL);
		de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_MILLISECONDI_NOTE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA_EROGAZIONE);
		if(editEnabled){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		if(tempiRispostaErogazione.getTempoMedioRisposta()!=null)
			de.setValue(tempiRispostaErogazione.getTempoMedioRisposta()+"");
		dati.addElement(de);
		
	}
	
	
	public void addToDatiConfigurazioneCache(Vector<DataElement> dati, TipoOperazione tipoOperazione, 
			Cache cache, boolean enabled) throws Exception {
		
	
		if(enabled){
			DataElement de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONFIGURAZIONE_CACHE_DATI_STATISTICI);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_STATO);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_STATO);
		if(enabled){
			//de.setType(DataElementType.CHECKBOX);
			de.setType(DataElementType.SELECT);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setValues(ConfigurazioneCostanti.STATI);
		de.setSelected(cache.isCache() ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue());
		de.setValue(cache.isCache() ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue());
		de.setPostBack(true);
		dati.addElement(de);
				
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_DIMENSIONE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_DIMENSIONE);
		if(enabled && cache.isCache()){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		if(cache.getSize()!=null)
			de.setValue(cache.getSize()+"");
		dati.addElement(de);
		
		
		String[] tipoAlgoritmo = { 
				CacheAlgorithm.LRU.name(),
				CacheAlgorithm.MRU.name()
		};
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_ALGORITMO);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_ALGORITMO);
		if(enabled && cache.isCache()){
			de.setType(DataElementType.SELECT);
			de.setValues(tipoAlgoritmo);
			if(cache.getAlgorithm()!=null){
				de.setSelected(cache.getAlgorithm().name());
			}
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		if(cache.getAlgorithm()!=null){
			de.setValue(cache.getAlgorithm().name());
		}
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_LIFE_TIME);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_LIFE_TIME);
		if(enabled && cache.isCache()){
			de.setType(DataElementType.TEXT_EDIT);
			//de.setRequired(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		if(cache.getLifeTime()!=null)
			de.setValue(cache.getLifeTime()+"");
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_IDLE_TIME);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_IDLE_TIME);
		if(enabled && cache.isCache()){
			de.setType(DataElementType.TEXT_EDIT);
			de.setNote(ConfigurazioneCostanti.LABEL_CACHE_SECONDS_NOTE);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		if(cache.getIdleTime()!=null)
			de.setValue(cache.getIdleTime()+"");
		dati.addElement(de);
		
		
	}
	
	public String readConfigurazioneControlloTrafficoFromHttpParameters(ConfigurazioneControlloTraffico controlloTraffico, boolean first) throws Exception {
		
		StringBuilder sbParsingError = new StringBuilder();
		// **** Limitazione Numero di Richieste Complessive Gestite dalla PdD ****
		
		// enabled
		String statoMaxThreads = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_STATO);
		if(statoMaxThreads!=null && !"".equals(statoMaxThreads)){
			controlloTraffico.setControlloMaxThreadsEnabled(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.getValue().equals(statoMaxThreads) || 
					CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.getValue().equals(statoMaxThreads));
			controlloTraffico.setControlloMaxThreadsWarningOnly(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.getValue().equals(statoMaxThreads));
		}
		
		// soglia		
		String numRichieste = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_SOGLIA);
		if(numRichieste!=null && !"".equals(numRichieste)){
			try{
				long l = Long.parseLong(numRichieste);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				controlloTraffico.setControlloMaxThreadsSoglia(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+numRichieste+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_SOGLIA+"' deve essere un numero intero maggiore di 0";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first==false)
				controlloTraffico.setControlloMaxThreadsSoglia(null); // il check segnalera' l'errore  
		}
			
		// tipo errore
		String tipoErroreMaxThreads = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE);
		if(tipoErroreMaxThreads!=null && !"".equals(tipoErroreMaxThreads)){
			try{
				TipoErrore tipo = TipoErrore.toEnumConstant(tipoErroreMaxThreads, true);
				controlloTraffico.setControlloMaxThreadsTipoErrore(tipo.getValue());
			}catch(Exception e){
				String messaggio = "Il valore ("+tipoErroreMaxThreads+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE+"' deve assumere uno dei seguenti valori: "+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_ERRORE_FAULT +","+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_ERRORE_HTTP_429 +","+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_ERRORE_HTTP_503+","+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_ERRORE_HTTP_500;
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first){
				if(controlloTraffico.getControlloMaxThreadsTipoErrore()==null){
					// default
					controlloTraffico.setControlloMaxThreadsTipoErrore(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE);
				}
			}
		}
		
		// includi descrizione
		String tipoErroreIncludiDescrizioneMaxThreads = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE_DESCRIZIONE);
		if(tipoErroreMaxThreads!=null && !"".equals(tipoErroreMaxThreads)){
			controlloTraffico.setControlloMaxThreadsTipoErroreIncludiDescrizione(ServletUtils.isCheckBoxEnabled(tipoErroreIncludiDescrizioneMaxThreads));
		}
		
		
		// *** Controllo della Congestione ***
		
		// enabled
		String statoControlloCongestione = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_STATO_CONTROLLO_CONGESTIONE);
		if(statoControlloCongestione!=null && !"".equals(statoControlloCongestione)){
			controlloTraffico.setControlloCongestioneEnabled(CostantiConfigurazione.ABILITATO.getValue().equals(statoControlloCongestione));
		}
		
		// threshold
		String threshold = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_THRESHOLD_CONTROLLO_CONGESTIONE);
		if(threshold!=null && !"".equals(threshold)){
			try{
				int t = Integer.parseInt(threshold);
				if(t<=0 || t>100){
					throw new Exception("Valore non nell'intervallo");
				}
				controlloTraffico.setControlloCongestioneThreshold(t);
			}catch(Exception e){
				String messaggio = "Il valore ("+threshold+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_THRESHOLD_CONTROLLO_CONGESTIONE+"' deve essere un numero compreso nell'intervallo [1,100]";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(controlloTraffico.isControlloCongestioneEnabled()){
				if(controlloTraffico.getControlloCongestioneThreshold()==null){
					// default
					controlloTraffico.setControlloCongestioneThreshold(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_PARAMETRO_CONTROLLO_CONGESTIONE_THRESHOLD);
				}
			}
		}
				
		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}
	
	
	public String readConfigurazioneRateLimitingFromHttpParameters(ConfigurazioneRateLimiting rateLimiting, boolean first) throws Exception {
		
		StringBuilder sbParsingError = new StringBuilder();
		// tipo errore
		String tipoErroreMaxThreads = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_TIPOLOGIA_ERRORE);
		if(tipoErroreMaxThreads!=null && !"".equals(tipoErroreMaxThreads)){
			try{
				TipoErrore tipo = TipoErrore.toEnumConstant(tipoErroreMaxThreads, true);
				rateLimiting.setTipoErrore(tipo.getValue());
			}catch(Exception e){
				String messaggio = "Il valore ("+tipoErroreMaxThreads+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE+"' deve assumere uno dei seguenti valori: "+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_ERRORE_FAULT +","+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_ERRORE_HTTP_429 +","+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_ERRORE_HTTP_503+","+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_ERRORE_HTTP_500;
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first){
				if(rateLimiting.getTipoErrore()==null){
					// default
					rateLimiting.setTipoErrore(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE);
				}
			}
		}
		
		// includi descrizione
		String tipoErroreIncludiDescrizioneMaxThreads = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_TIPOLOGIA_ERRORE_DESCRIZIONE);
		if(tipoErroreMaxThreads!=null && !"".equals(tipoErroreMaxThreads)){
			rateLimiting.setTipoErroreIncludiDescrizione(ServletUtils.isCheckBoxEnabled(tipoErroreIncludiDescrizioneMaxThreads));
		}
		
				
		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}
	

	public String readTempiRispostaFruizioneFromHttpParameters(TempiRispostaFruizione tempiRispostaFruizione, boolean first) throws Exception {
			
		StringBuilder sbParsingError = new StringBuilder();
		// connection-timeout
		String connectionTimeout = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT_FRUIZIONE);
		if(connectionTimeout!=null && !"".equals(connectionTimeout)){
			try{
				int l = Integer.parseInt(connectionTimeout);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				tempiRispostaFruizione.setConnectionTimeout(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+connectionTimeout+") indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_FRUIZIONE
						+"' in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT_LABEL+"' deve essere un numero intero maggiore di 0";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first==false){
				tempiRispostaFruizione.setConnectionTimeout(null); // il check segnalera' l'errore  
			}
		}
		
		// read-timeout
		String readTimeout = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_READ_TIMEOUT_FRUIZIONE);
		if(readTimeout!=null && !"".equals(readTimeout)){
			try{
				int l = Integer.parseInt(readTimeout);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				tempiRispostaFruizione.setReadTimeout(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+readTimeout+") indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_FRUIZIONE
						+"' in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_READ_TIMEOUT_LABEL+"' deve essere un numero intero maggiore di 0";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first==false){
				tempiRispostaFruizione.setReadTimeout(null); // il check segnalera' l'errore  
			}
		}
		
		// tempo medio risposta
		String tempoMedioRisposta = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA_FRUIZIONE);
		if(tempoMedioRisposta!=null && !"".equals(tempoMedioRisposta)){
			try{
				int l = Integer.parseInt(tempoMedioRisposta);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				tempiRispostaFruizione.setTempoMedioRisposta(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+tempoMedioRisposta+") indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_FRUIZIONE
						+"' in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA_LABEL+"' deve essere un numero intero maggiore di 0";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first==false){
				tempiRispostaFruizione.setTempoMedioRisposta(null); // il check segnalera' l'errore  
			}
		}
				
		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}
	
	public String readTempiRispostaErogazioneFromHttpParameters(TempiRispostaErogazione tempiRispostaErogazione, boolean first) throws Exception {
		
		StringBuilder sbParsingError = new StringBuilder();
		// connection-timeout
		String connectionTimeout = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT_EROGAZIONE);
		if(connectionTimeout!=null && !"".equals(connectionTimeout)){
			try{
				int l = Integer.parseInt(connectionTimeout);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				tempiRispostaErogazione.setConnectionTimeout(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+connectionTimeout+") indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_EROGAZIONE
						+"' in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT_LABEL+"' deve essere un numero intero maggiore di 0";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first==false){
				tempiRispostaErogazione.setConnectionTimeout(null); // il check segnalera' l'errore  
			}
		}
		
		// read-timeout
		String readTimeout = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_READ_TIMEOUT_EROGAZIONE);
		if(readTimeout!=null && !"".equals(readTimeout)){
			try{
				int l = Integer.parseInt(readTimeout);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				tempiRispostaErogazione.setReadTimeout(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+readTimeout+") indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_EROGAZIONE
						+"' in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_READ_TIMEOUT_LABEL+"' deve essere un numero intero maggiore di 0";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first==false){
				tempiRispostaErogazione.setReadTimeout(null); // il check segnalera' l'errore  
			}
		}
		
		// tempo medio risposta
		String tempoMedioRisposta = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA_EROGAZIONE);
		if(tempoMedioRisposta!=null && !"".equals(tempoMedioRisposta)){
			try{
				int l = Integer.parseInt(tempoMedioRisposta);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				tempiRispostaErogazione.setTempoMedioRisposta(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+tempoMedioRisposta+") indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_EROGAZIONE
						+"' in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA_LABEL+"' deve essere un numero intero maggiore di 0";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first==false){
				tempiRispostaErogazione.setTempoMedioRisposta(null); // il check segnalera' l'errore  
			}
		}
				
		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}
	
	
	public String readConfigurazioneCacheFromHttpParameters(Cache cache, boolean first) throws Exception {
		
		StringBuilder sbParsingError = new StringBuilder();
		// cache enable
		String cacheStato = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_STATO);
		//cache.setCache(ServletUtils.isCheckBoxEnabled(cacheStato));
		if(cacheStato!=null && !"".equals(cacheStato)){
			cache.setCache(CostantiConfigurazione.ABILITATO.getValue().equals(cacheStato));
		}
		
		// cache dimensione
		String cacheDimensione = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_DIMENSIONE);
		if(cacheDimensione!=null && !"".equals(cacheDimensione)){
			try{
				long l = Long.parseLong(cacheDimensione);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				cache.setSize(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+cacheDimensione+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_DIMENSIONE+"' deve essere un numero intero maggiore di 0";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first){
				if(cache.getSize()==null){
					// default
					cache.setSize(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_DIMENSIONE);
				}
			}
			else{
				cache.setSize(null); // il check segnalera' l'errore  
			}	
		}
		
		// cache item life
		String cacheItemLife = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_LIFE_TIME);
		if(cacheItemLife!=null && !"".equals(cacheItemLife)){
			try{
				long l = Long.parseLong(cacheItemLife);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				cache.setLifeTime(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+cacheItemLife+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_LIFE_TIME+"' deve essere un numero intero maggiore di 0";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first){
				if(cache.getLifeTime()==null){
					// default
					//cache.setLifeTime(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_LIFE_TIME);
				}
			}else{
				cache.setLifeTime(null); // il check segnalera' l'errore  
			}	
		}
		
		// cache item idle
		String cacheItemIdle = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_IDLE_TIME);
		if(cacheItemIdle!=null && !"".equals(cacheItemIdle)){
			try{
				long l = Long.parseLong(cacheItemIdle);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				cache.setIdleTime(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+cacheItemIdle+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_IDLE_TIME+"' deve essere un numero intero maggiore di 0";
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first){
				if(cache.getIdleTime()==null){
					// default is null
					//cache.setCacheIdleTime(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONTROLLO_CONGESTIONE_CACHE_IDLE_TIME);
				}
			}
			else{
				cache.setIdleTime(null);
			}
		}
		
		// cache algorithm
		String cacheAlgorithm = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_ALGORITMO);
		if(cacheAlgorithm!=null && !"".equals(cacheAlgorithm)){
			try{
				if(!CacheAlgorithm.LRU.name().equals(cacheAlgorithm) && 
						!CacheAlgorithm.MRU.name().equals(cacheAlgorithm)){
					throw new Exception("Valore non nell'intervallo");
				}
				cache.setAlgorithm(CacheAlgorithm.toEnumConstant(cacheAlgorithm));
			}catch(Exception e){
				String messaggio = "Il valore ("+cacheAlgorithm+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_ALGORITMO+"' deve assumere uno dei seguenti valori: "+
						CacheAlgorithm.LRU.name() +","+CacheAlgorithm.MRU.name();
				ControlStationCore.getLog().error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(first){
				if(cache.getAlgorithm()==null){
					// default
					cache.setAlgorithm(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_ALGORITMO);
				}
			}
		}
		
		
		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}
	
	public void addParsingError(StringBuilder sbParsingError, String parsingError) {
		if(sbParsingError.length() == 0)
			sbParsingError.append(parsingError);
		else
			sbParsingError.append("<br/>").append(parsingError);
	}
	
	
	public boolean checkDatiConfigurazioneControlloTraffico(TipoOperazione tipoOperazione, StringBuilder sbParsingError, org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico) throws Exception {

		// errori di parsing letti durante la read della richiesta
		if(sbParsingError.length() >0){
			this.pd.setMessage(sbParsingError.toString());
			return false;
		}
		
		if(configurazioneControlloTraffico.getControlloTraffico().getControlloMaxThreadsSoglia()==null){
			String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_SOGLIA+"'";
			this.pd.setMessage(messaggio);
			return false;
		}
		
		// tempi risposta fruizione
		
		if(configurazioneControlloTraffico.getTempiRispostaFruizione().getConnectionTimeout()==null){
			String messaggio = "Deve essere indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_FRUIZIONE
						+"' un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT_LABEL+"'";
			this.pd.setMessage(messaggio);
			return false;
		}
		
		if(configurazioneControlloTraffico.getTempiRispostaFruizione().getReadTimeout()==null){
			String messaggio = "Deve essere indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_FRUIZIONE
						+"' un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_READ_TIMEOUT_LABEL+"'";
			this.pd.setMessage(messaggio);
			return false;
		}
		
		if(configurazioneControlloTraffico.getTempiRispostaFruizione().getTempoMedioRisposta()==null){
			String messaggio = "Deve essere indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_FRUIZIONE
						+"' un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA_LABEL+"'";
			this.pd.setMessage(messaggio);
			return false;
		}
		
		// tempi risposta erogazione
		
		if(configurazioneControlloTraffico.getTempiRispostaErogazione().getConnectionTimeout()==null){
			String messaggio = "Deve essere indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_EROGAZIONE
						+"' un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT_LABEL+"'";
			this.pd.setMessage(messaggio);
			return false;
		}
		
		if(configurazioneControlloTraffico.getTempiRispostaErogazione().getReadTimeout()==null){
			String messaggio = "Deve essere indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_EROGAZIONE
						+"' un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_READ_TIMEOUT_LABEL+"'";
			this.pd.setMessage(messaggio);
			return false;
		}
		
		if(configurazioneControlloTraffico.getTempiRispostaErogazione().getTempoMedioRisposta()==null){
			String messaggio = "Deve essere indicato nella sezione '"+
						ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TEMPI_RISPOSTA_EROGAZIONE
						+"' un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_TEMPO_MEDIO_RISPOSTA_LABEL+"'";
			this.pd.setMessage(messaggio);
			return false;
		}
				
		// cache
		
		if(configurazioneControlloTraffico.getCache().isCache()){
			
			if(configurazioneControlloTraffico.getCache().getSize()==null){
				String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_DIMENSIONE+"'";
				this.pd.setMessage(messaggio);
				return false;
			}
			
//			if(configurazioneControlloTraffico.getCache().getLifeTime()==null){
//				String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_DIMENSIONE+"'";
//				this.pd.setMessage(messaggio);
//				return false;
//			}
			
		}
		return true;
	}
	
	public void prepareConfigurazionePolicyList(Search ricerca, List<ConfigurazionePolicy> lista, int idLista) throws Exception{
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY);

			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);	

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
//
//			this.pd.setSearchDescription("");
//			this.pd.setSearch("off");
			
			String filterTipoPolicy = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_POLICY);
			this.addFilterTipoPolicy(filterTipoPolicy,false);
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO_POLICY, null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, "Policy", search);
			}
			
			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NOME,
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO
			};
			this.pd.setLabels(labels);
		
			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					Vector<DataElement> e = new Vector<DataElement>();
					ConfigurazionePolicy policy = lista.get(i);
					
					String nDesr = policy.getDescrizione();
					
					Parameter pPolicyId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID, policy.getId() + ""); 

					DataElement de = new DataElement();
					de.setSize(100);
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY_CHANGE, pPolicyId);
					de.setValue(policy.getIdPolicy());
					de.setIdToRemove(""+policy.getId());
					de.setToolTip(policy.getIdPolicy()+"\n"+nDesr); 
					e.addElement(de);
					
					de = new DataElement();
					if(policy.isBuiltIn()) {
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_BUILT_IN);
					}
					else {
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_UTENTE);
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

	public List<Parameter> getTitleListAttivazionePolicy(RuoloPolicy ruoloPorta, String nomePorta, ServiceBinding serviceBinding, String nomeOggetto) throws Exception{
		List<Parameter> lstParamPorta = null;
		if(ruoloPorta!=null) {
		
			String labelPerPorta = null;
			if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
				// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
				Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, this.session);
				if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
				
				IDPortaDelegata idPortaDelegata = new IDPortaDelegata();
				idPortaDelegata.setNome(nomePorta);
				PortaDelegata myPD = this.porteDelegateCore.getPortaDelegata(idPortaDelegata);
				String idporta = myPD.getNome();
				
				MappingFruizionePortaDelegata mappingPD = this.porteDelegateCore.getMappingFruizionePortaDelegata(myPD);
				long idSoggetto = myPD.getIdSoggetto().longValue();
				long idAsps = this.apsCore.getIdAccordoServizioParteSpecifica(mappingPD.getIdServizio());
				long idFruizione = this.apsCore.getIdFruizioneAccordoServizioParteSpecifica(mappingPD.getIdFruitore(),mappingPD.getIdServizio());
				
				PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(this.request, this.pd, this.session);
				lstParamPorta = porteDelegateHelper.getTitoloPD(parentPD,idSoggetto +"", idAsps+"", idFruizione+"");
				
				if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
					labelPerPorta = this.porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
							ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_DI,
							ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING,
							myPD);
				}
				else {
					labelPerPorta = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_DI+idporta;
				}
				
			}
			else {
				Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, this.session);
				
				IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
				idPortaApplicativa.setNome(nomePorta);
				PortaApplicativa myPA = this.porteApplicativeCore.getPortaApplicativa(idPortaApplicativa);
				String idporta = myPA.getNome();
				
				MappingErogazionePortaApplicativa mappingPA = this.porteApplicativeCore.getMappingErogazionePortaApplicativa(myPA);
				long idSoggetto = myPA.getIdSoggetto().longValue();
				long idAsps = this.apsCore.getIdAccordoServizioParteSpecifica(mappingPA.getIdServizio());
				
				PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(this.request, this.pd, this.session);
				lstParamPorta = porteApplicativeHelper.getTitoloPA(parentPA, idSoggetto+"", idAsps+"");
				
				if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
					labelPerPorta = this.porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
							ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_DI,
							ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING,
							myPA);
				}
				else {
					labelPerPorta = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_DI+idporta;
				}
				
			}
			
			if(nomeOggetto==null) {
				lstParamPorta.add(new Parameter(labelPerPorta,null));
			}
			else {
				List<Parameter> list = new ArrayList<>();
				list.add(new Parameter( ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_RUOLO_PORTA, ruoloPorta.getValue()));
				list.add(new Parameter( ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_NOME_PORTA, nomePorta));
				if(serviceBinding!=null) {
					list.add(new Parameter( ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_SERVICE_BINDING,serviceBinding.name()));
				}
				lstParamPorta.add(new Parameter(labelPerPorta,
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST,
						list
						));
				lstParamPorta.add(new Parameter(nomeOggetto,null));
			}
		}
		
		return lstParamPorta;
	}
	
	public List<TipoRisorsaPolicyAttiva> gestisciCriteriFiltroRisorsaPolicy(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta) throws Exception {
		
		int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;
		
		// Gestione risorsa
		List<TipoRisorsaPolicyAttiva> listaTipoRisorsa = this.confCore.attivazionePolicyTipoRisorsaList(ricerca, ruoloPorta, nomePorta);
		String filterTipoRisorsaPolicy = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_RISORSA_POLICY);
		if(filterTipoRisorsaPolicy!=null && !"".equals(filterTipoRisorsaPolicy)) {
			TipoRisorsaPolicyAttiva filterTipoRisorsaPolicyAsObject = TipoRisorsaPolicyAttiva.toEnumConstant(filterTipoRisorsaPolicy, false);
			if(filterTipoRisorsaPolicyAsObject==null) {
				filterTipoRisorsaPolicy = null;
			}
			else {
				if(!listaTipoRisorsa.contains(filterTipoRisorsaPolicyAsObject)) {
					filterTipoRisorsaPolicy = null; // in seguito ad una eliminazione
				}
			}
		}
		// gestione default
		if(filterTipoRisorsaPolicy==null || "".equals(filterTipoRisorsaPolicy)) {
			
			TipoRisorsaPolicyAttiva defaultValue = CostantiControlStation.DEFAULT_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA_TIPO_VALUE;
			if(listaTipoRisorsa!=null && !listaTipoRisorsa.isEmpty() && !listaTipoRisorsa.contains(defaultValue)) {
				// cerco le metriche più gettonate, altrimeni uso la prima che esiste
				if(listaTipoRisorsa.contains(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE)) {
					defaultValue = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE;
				}
				else if(listaTipoRisorsa.contains(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE)) {
					defaultValue = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE;
				}
				else if(listaTipoRisorsa.contains(TipoRisorsaPolicyAttiva.OCCUPAZIONE_BANDA)) {
					defaultValue = TipoRisorsaPolicyAttiva.OCCUPAZIONE_BANDA;
				}
				else {
					defaultValue = listaTipoRisorsa.get(0);
				}
			}
			
			ricerca.addFilter(idLista, Filtri.FILTRO_TIPO_RISORSA_POLICY, defaultValue.getValue());
		}
		
		return listaTipoRisorsa;
	}
	
	public void prepareAttivazionePolicyList(Search ricerca, List<AttivazionePolicy> lista, List<TipoRisorsaPolicyAttiva> listaTipoRisorsa, 
			int idLista,RuoloPolicy ruoloPorta, String nomePorta, ServiceBinding serviceBinding) throws Exception{
		try {
			List<Parameter> lstParamSession = new ArrayList<Parameter>();

			Parameter parRuoloPorta = null;
			if(ruoloPorta!=null) {
				parRuoloPorta = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_RUOLO_PORTA, ruoloPorta.getValue());
				lstParamSession.add(parRuoloPorta);
			}
			Parameter parNomePorta = null;
			if(nomePorta!=null) {
				parNomePorta = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_NOME_PORTA, nomePorta);
				lstParamSession.add(parNomePorta);
			}
			Parameter parServiceBinding = null;
			if(serviceBinding!=null) {
				parServiceBinding = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_SERVICE_BINDING, serviceBinding.name());
				lstParamSession.add(parServiceBinding);
			}
			
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY, lstParamSession);

			List<Parameter> lstParamPorta = null;
			if(ruoloPorta!=null) {
				lstParamPorta = getTitleListAttivazionePolicy(ruoloPorta, nomePorta, serviceBinding, null);
			}

			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);	

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String filterTipoRisorsaPolicy = null;
			if(listaTipoRisorsa!=null && !listaTipoRisorsa.isEmpty()) {
				filterTipoRisorsaPolicy = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_RISORSA_POLICY);
				this.addFilterTipoRisorsaPolicy(listaTipoRisorsa,filterTipoRisorsaPolicy, false);
			}
			else {
				this.removeFilterTipoRisorsaPolicy();
			}
			
//			this.pd.setSearchDescription("");
//			this.pd.setSearch("off");

			// controllo eventuali risultati ricerca
			if (!search.equals("") || (filterTipoRisorsaPolicy!=null && !"".equals(filterTipoRisorsaPolicy))) {
				ServletUtils.enabledPageDataSearch(this.pd, "Policy", search);
			}
			
			boolean showMetricaColumn = false;
			
			// setto la barra del titolo
			List<Parameter> lstParam = null;
			if(lstParamPorta!=null) {
				lstParam = lstParamPorta;
			}
			else {
				lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);
				
			String labelSogliaColonna = null;
			if(filterTipoRisorsaPolicy!=null && !"".equals(filterTipoRisorsaPolicy)){
				TipoRisorsaPolicyAttiva tipoRisorsa = TipoRisorsaPolicyAttiva.toEnumConstant(filterTipoRisorsaPolicy, true);
				switch (tipoRisorsa) {
				case NUMERO_RICHIESTE:
				case NUMERO_RICHIESTE_SIMULTANEE:
				case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
				case NUMERO_RICHIESTE_FALLITE:
				case NUMERO_FAULT_APPLICATIVI:
				case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
					//labelSogliaColonna =ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE_ESTESA;
					labelSogliaColonna =ConfigurazioneCostanti.LABEL_POLICY_INFORMAZIONI_SOGLIA_NUMERO;
					break;
				case OCCUPAZIONE_BANDA:
					//labelSogliaColonna =ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_OCCUPAZIONE_DI_BANDA_KB_LABEL;
					labelSogliaColonna =ConfigurazioneCostanti.LABEL_POLICY_INFORMAZIONI_SOGLIA_BANDA;
					break;
				case TEMPO_MEDIO_RISPOSTA:
					//labelSogliaColonna =ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_MS_LABEL;
					labelSogliaColonna =ConfigurazioneCostanti.LABEL_POLICY_INFORMAZIONI_SOGLIA_TEMPI_MS;
					break;
				case TEMPO_COMPLESSIVO_RISPOSTA:
					//labelSogliaColonna =ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_MS_LABEL;
					labelSogliaColonna =ConfigurazioneCostanti.LABEL_POLICY_INFORMAZIONI_SOGLIA_TEMPI_SECONDI;
					break;
				}
			}
			
			
			// setto le label delle colonne
			List<String> lstLabels = new ArrayList<>();
			if(lista != null && lista.size() > 1) {
				lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POSIZIONE);
			}
			lstLabels.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO);
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NOME);
			if(labelSogliaColonna!=null) {
				lstLabels.add(labelSogliaColonna);
			}
			if(showMetricaColumn) {
				if((filterTipoRisorsaPolicy==null || "".equals(filterTipoRisorsaPolicy))) {
					lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_RISORSA);
				}
			}
			lstLabels.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME);
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_CONTINUE);
			//lstLabels.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_FILTRO);
			//lstLabels.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RAGGRUPPAMENTO_COLUMN);
			this.pd.setLabels(lstLabels.toArray(new String [lstLabels.size()]));
			
			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				
				int numeroElementi = lista.size();
				
				Integer sizeColumn = null;
				
				for (int i = 0; i < lista.size(); i++) {
					AttivazionePolicy policy = lista.get(i);
					Vector<DataElement> e = new Vector<DataElement>();
					
					// Fix retrocompatibilita dove il nome non era obbligatorio.
					policy.setAlias(PolicyUtilities.getNomeActivePolicy(policy.getAlias(), policy.getIdActivePolicy()));
					
					String descrizionePolicy = "";
					try{
						descrizionePolicy = this.confCore.getInfoPolicy(policy.getIdPolicy()).getDescrizione();
					}catch(Exception ex){
						ControlStationCore.getLog().error(ex.getMessage(),ex);
					}
					String nDescr = "";
					if(StringUtils.isNotEmpty(policy.getAlias())) {
						nDescr = policy.getAlias();
					}
					nDescr = nDescr + "\n"+"Identificativo Runtime: "+ policy.getIdActivePolicy();	
					nDescr = nDescr + "\n"+"Policy: "+ policy.getIdPolicy();
					nDescr = nDescr+"\n"+descrizionePolicy;	
										
					
					Parameter pPolicyId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID, policy.getId() + ""); 
					Parameter pPolicyRisorsa = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA, filterTipoRisorsaPolicy); 



					// Posizione
					if(lista.size() > 1) {
						DataElement de = new DataElement();
						de.setWidthPx(48);
						de.setType(DataElementType.IMAGE);
						DataElementImage imageUp = new DataElementImage();
						Parameter pDirezioneSu = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POSIZIONE, 
								CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU);
						Parameter pDirezioneGiu = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POSIZIONE, 
								CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_GIU);
								
						if(i > 0) {
							imageUp.setImage(CostantiControlStation.ICONA_FRECCIA_SU);
							imageUp.setToolTip(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SPOSTA_SU);
							List<Parameter> listP = new ArrayList<>();
							listP.add(pPolicyId);
							listP.add(pPolicyRisorsa);
							if(ruoloPorta!=null) {
								listP.add(parRuoloPorta);
								listP.add(parNomePorta);
								listP.add(parServiceBinding);
							}
							listP.add(pDirezioneSu);
							imageUp.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST, listP.toArray(new Parameter[1])); 
						}
						else {
							imageUp.setImage(CostantiControlStation.ICONA_PLACEHOLDER);
						}
						de.addImage(imageUp);
						
						if(i < numeroElementi -1) {
							DataElementImage imageDown = new DataElementImage();
							imageDown.setImage(CostantiControlStation.ICONA_FRECCIA_GIU);
							imageDown.setToolTip(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SPOSTA_GIU);
							List<Parameter> listP = new ArrayList<>();
							listP.add(pPolicyId);
							listP.add(pPolicyRisorsa);
							if(ruoloPorta!=null) {
								listP.add(parRuoloPorta);
								listP.add(parNomePorta);
								listP.add(parServiceBinding);
							}
							listP.add(pDirezioneGiu);
							imageDown.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST, listP.toArray(new Parameter[1])); 
							de.addImage(imageDown);
						}
						de.setValue(policy.getPosizione()+"");
						e.addElement(de);
					}
										
					// Stato
					DataElement de = new DataElement();
					de.setWidthPx(10);
					de.setType(DataElementType.CHECKBOX);
					if(policy.isEnabled()){
						if(policy.isWarningOnly()){
							de.setToolTip(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_WARNING_ONLY);
							de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_WARNING_ONLY);
							de.setSelected(CheckboxStatusType.CONFIG_WARNING);
						}
						else{
							de.setToolTip(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
							de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
							de.setSelected(CheckboxStatusType.CONFIG_ENABLE);
						}
					}
					else{
						de.setToolTip(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
						de.setSelected(CheckboxStatusType.CONFIG_DISABLE);
					}
					if(ruoloPorta!=null) {
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId, parRuoloPorta, parNomePorta, parServiceBinding);
					}
					else {
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId);
					}
					e.addElement(de);
					
					
					// nome
					de = new DataElement();
					if(ruoloPorta!=null) {
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId, parRuoloPorta, parNomePorta, parServiceBinding);
					}
					else {
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId);
					}
					
					if(StringUtils.isNotEmpty(policy.getAlias()))
						de.setValue(policy.getAlias());
					else 
						de.setValue(policy.getIdActivePolicy());
					
					de.setIdToRemove(""+policy.getId());
					de.setToolTip(nDescr); 
					e.addElement(de);

					
					ConfigurazionePolicy configPolicy = null;
					
					
					// Soglia
					if(labelSogliaColonna!=null) {
						de = new DataElement();
						if(policy.isRidefinisci()) {
							de.setValue(policy.getValore()+"");
						}
						else {
							if(configPolicy==null) {
								configPolicy = this.confCore.getConfigurazionePolicy(policy.getIdPolicy());
							}
							de.setValue(configPolicy.getValore()+"");
						}
						
						if(sizeColumn==null) {
							de.setWidthPx(70);
							sizeColumn = 70;
						}
						
						if(de.getValue().length()>12) {
							if(sizeColumn<110) {
								de.setWidthPx(110);
								sizeColumn = 110;
							}
						}
						else if(de.getValue().length()>9) {
							if(sizeColumn<90) {
								de.setWidthPx(90);
								sizeColumn = 90;
							}
						}
						
						e.addElement(de);
					}
					
					
					// Tipo Risorsa
									
					if(showMetricaColumn) {
						if((filterTipoRisorsaPolicy==null || "".equals(filterTipoRisorsaPolicy))) {
						
							if(configPolicy==null) {
								configPolicy = this.confCore.getConfigurazionePolicy(policy.getIdPolicy());
							}
							TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.getTipo(configPolicy.getRisorsa(),configPolicy.isSimultanee());
							String labelRisorsaPolicyAttiva = this.getLabelTipoRisorsaPolicyAttiva(tipoRisorsaPolicyAttiva);							
							de = new DataElement();
							de.setValue(labelRisorsaPolicyAttiva);
							e.addElement(de);
							
						}
					}
					
										
					
					de = new DataElement();
					if(policy.isEnabled()){
						de.setValue("Visualizza");
					}
					else{
						de.setValue("-");
					}
					de.allineaTdAlCentro();
					de.setWidthPx(60);

					Parameter pJmx = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+"");
					if(policy.isEnabled()){
						if(ruoloPorta!=null) {
							de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId,pJmx, parRuoloPorta, parNomePorta, parServiceBinding);
						}
						else {
							de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId,pJmx);
						}
					}
					e.addElement(de);
								
					
					
					// Continue
					
					de = new DataElement();
					de.setWidthPx(24);
					de.setType(DataElementType.IMAGE);
					
					DataElementImage imageUp = new DataElementImage();
					if(policy.isContinuaValutazione()) {
						imageUp.setImage(CostantiControlStation.ICONA_CONTINUE);
						imageUp.setToolTip(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_CONTINUE_PROSEGUI_TOOLTIP);
					}
					else {
						imageUp.setImage(CostantiControlStation.ICONA_BREAK);
						imageUp.setToolTip(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_CONTINUE_INTERROMPI_TOOLTIP);
					}
					if(ruoloPorta!=null) {
						imageUp.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId, parRuoloPorta, parNomePorta, parServiceBinding);
					}
					else {
						imageUp.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId);
					}
					de.addImage(imageUp);
					
					de.allineaTdAlCentro();
					
					de.setValue(policy.getPosizione()+"");
					e.addElement(de);
					
					
//					de = new DataElement();
//					String filtro = this.toStringCompactFilter(policy.getFiltro(),ruoloPorta,nomePorta);
//					if(filtro.length()>60){
//						de.setValue(filtro.substring(0,57)+"...");
//					}else{
//						de.setValue(filtro);
//					}
//					de.setToolTip(filtro);
//					if(ruoloPorta!=null) {
//						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId, parRuoloPorta, parNomePorta, parServiceBinding);
//					}
//					else {
//						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId);
//					}
//					e.addElement(de);
//					
//					de = new DataElement();
//					String groupBy = this.toStringCompactGroupBy(policy.getGroupBy(),ruoloPorta,nomePorta);
//					if(groupBy.length()>60){
//						de.setValue(groupBy.substring(0,57)+"...");
//					}else{
//						de.setValue(groupBy);
//					}
//					de.setToolTip(groupBy);
//					if(ruoloPorta!=null) {
//						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId, parRuoloPorta, parNomePorta, parServiceBinding);
//					}
//					else {
//						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId);
//					}
//					e.addElement(de);
					
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
	
	public String toStringCompactFilter(AttivazionePolicyFiltro filtro, RuoloPolicy ruoloPorta, String nomePorta, ServiceBinding serviceBinding) throws Exception {
		
		boolean delegata = false;
		boolean applicativa = false;
		boolean configurazione = false;
		if(ruoloPorta!=null) {
			if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
				delegata = (nomePorta!=null);
			}
			else if(RuoloPolicy.APPLICATIVA.equals(ruoloPorta)) {
				applicativa = (nomePorta!=null);
			}
		}
		configurazione = !delegata && !applicativa;
		
		StringBuilder bf = new StringBuilder("");
		if(filtro.isEnabled()){

			if(configurazione) {
				if( (filtro.getRuoloPorta()!=null && !RuoloPolicy.ENTRAMBI.equals(filtro.getRuoloPorta())) ){
					if(bf.length()>0){
						bf.append(", ");
					}
					if(RuoloPolicy.DELEGATA.equals(filtro.getRuoloPorta())){
						bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD).append(": ");
						bf.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUOLO_PORTA_DELEGATA);
					}
					else if(RuoloPolicy.APPLICATIVA.equals(filtro.getRuoloPorta())){
						bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD).append(": ");
						bf.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUOLO_PORTA_APPLICATIVA);
					}
				}
			}

			if(configurazione) {
				if( !(filtro.getProtocollo()==null || "".equals(filtro.getProtocollo())) ){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT+": ");
					bf.append(this.getLabelProtocollo(filtro.getProtocollo()));
				}
			}
				
			if(configurazione) {
				if( !(filtro.getNomePorta()==null || "".equals(filtro.getNomePorta())) ){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PORTA).append(": ");
					bf.append(filtro.getNomePorta());
				}
			}
						
			if(configurazione) {
				if(filtro.getRuoloErogatore()!=null) {
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_EROGATORE).append(": ");
					bf.append(filtro.getRuoloErogatore());
				}
				else if( !( (filtro.getTipoErogatore()==null || "".equals(filtro.getTipoErogatore())) 
						||
						(filtro.getNomeErogatore()==null || "".equals(filtro.getNomeErogatore())) ) ){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_EROGATORE_COMPACT).append(": ");
					IDSoggetto idSoggetto = new IDSoggetto(filtro.getTipoErogatore(), filtro.getNomeErogatore());
					bf.append(this.getLabelNomeSoggetto(idSoggetto));
				}
			}

			if(configurazione) {
				if( !( (filtro.getTipoServizio()==null || "".equals(filtro.getTipoServizio())) 
						||
						(filtro.getNomeServizio()==null || "".equals(filtro.getNomeServizio()))
						||
						(filtro.getVersioneServizio()==null)
						||
						(filtro.getTipoErogatore()==null || "".equals(filtro.getTipoErogatore())) 
						||
						(filtro.getNomeErogatore()==null || "".equals(filtro.getNomeErogatore()))
						) ){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO).append(": ");
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(filtro.getTipoServizio(), filtro.getNomeServizio(), 
							filtro.getTipoErogatore(), filtro.getNomeErogatore(), 
							filtro.getVersioneServizio());
					bf.append(this.getLabelIdServizio(idServizio));
				}
			}
			
			if( !(filtro.getAzione()==null || "".equals(filtro.getAzione())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append(this.getLabelAzione(serviceBinding)).append(": ");
				bf.append(filtro.getAzione());
			}
			
			if(configurazione) {
				if( !(filtro.getServizioApplicativoErogatore()==null || "".equals(filtro.getServizioApplicativoErogatore())) ){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE).append(": ");
					bf.append(filtro.getServizioApplicativoErogatore());
				}
			}
			
			if(configurazione || applicativa) {
				if(filtro.getRuoloFruitore()!=null) {
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE).append(": ");
					bf.append(filtro.getRuoloFruitore());
				}
				else if( !( (filtro.getTipoFruitore()==null || "".equals(filtro.getTipoFruitore())) 
						||
						(filtro.getNomeFruitore()==null || "".equals(filtro.getNomeFruitore())) ) ){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE_COMPACT).append(": ");
					IDSoggetto idSoggetto = new IDSoggetto(filtro.getTipoFruitore(), filtro.getNomeFruitore());
					bf.append(this.getLabelNomeSoggetto(idSoggetto));
				}
				if(configurazione) {
					if( !(filtro.getServizioApplicativoFruitore()==null || "".equals(filtro.getServizioApplicativoFruitore())) ){
						if(bf.length()>0){
							bf.append(", ");
						}
						bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE_COMPACT).append(": ");
						bf.append(filtro.getServizioApplicativoFruitore());
					}
				}
			}
			else if(delegata) {
				if(filtro.getRuoloFruitore()!=null) {
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE).append(": ");
					bf.append(filtro.getRuoloFruitore());
				}
				else if( !(filtro.getServizioApplicativoFruitore()==null || "".equals(filtro.getServizioApplicativoFruitore())) ){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE_COMPACT).append(": ");
					bf.append(filtro.getServizioApplicativoFruitore());
				}
			}
			
			if(filtro.isInformazioneApplicativaEnabled()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED_COMPACT).append(": ");
				bf.append(filtro.getInformazioneApplicativaTipo());
			}

		}
		else{
			bf.append(CostantiControlStation.LABEL_STATO_DISABILITATO);
		}

		if(bf.length()<=0 && (delegata || applicativa)) {
			bf.append(CostantiControlStation.LABEL_STATO_DISABILITATO);
		}
		
		return bf.toString();
	}
	
	public String toStringCompactGroupBy(AttivazionePolicyRaggruppamento groupBy, RuoloPolicy ruoloPorta, String nomePorta, ServiceBinding serviceBinding) {
		
		boolean delegata = false;
		boolean applicativa = false;
		boolean configurazione = false;
		if(ruoloPorta!=null) {
			if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
				delegata = (nomePorta!=null);
			}
			else if(RuoloPolicy.APPLICATIVA.equals(ruoloPorta)) {
				applicativa = (nomePorta!=null);
			}
		}
		configurazione = !delegata && !applicativa;
		
		StringBuilder bf = new StringBuilder("");
		if(groupBy.isEnabled()){

			if(configurazione) {
				if(groupBy.isRuoloPorta()){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RUOLO_PDD_LABEL);
				}
			}
			
			if(configurazione) {
				if(groupBy.isProtocollo()){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT);
				}
			}
			
			if(configurazione) {
				if(groupBy.isErogatore()){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_EROGATORE_COMPACT);
				}
			}
			
			if(configurazione) {
				if(groupBy.isServizio()){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SERVIZIO);
				}
			}
			
			if(groupBy.isAzione()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append(this.getLabelAzione(serviceBinding));
			}
			
			if(configurazione) {
				if(groupBy.isServizioApplicativoErogatore()){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_EROGATORE);
				}
			}
			
			if(configurazione) {
				if(groupBy.isFruitore()){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_FRUITORE_COMPACT);
				}
			}
			
			if(configurazione) {
				if(groupBy.isServizioApplicativoFruitore()){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_FRUITORE_COMPACT);
				}
			}
			
			if(!configurazione) {
				if(groupBy.isServizioApplicativoFruitore() || groupBy.isFruitore() || groupBy.isIdentificativoAutenticato()){
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RICHIEDENTE);
				}
			}
			
			if(groupBy.getToken()!=null){
				String [] tmp = groupBy.getToken().split(",");
				if(tmp!=null && tmp.length>0) {
					for (int i = 0; i < tmp.length; i++) {
						if(bf.length()>0){
							bf.append(", ");
						}
						bf.append(tmp[i]);	
					}
				}
			}
			
			if(groupBy.isInformazioneApplicativaEnabled()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Chiave:");
				bf.append(groupBy.getInformazioneApplicativaTipo());
			}

		}
		else{
			bf.append(CostantiControlStation.LABEL_STATO_DISABILITATO);
		}

		return bf.toString();
	}
	
	public String readDatiGeneraliPolicyFromHttpParameters(ConfigurazionePolicy policy, boolean first) throws Exception {
			
		StringBuilder sbParsingError = new StringBuilder();
		// id
		String id = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID);
		if(id!=null && !"".equals(id)){
			try{
				long l = Long.parseLong(id);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				policy.setId(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+id+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID+"' deve essere un numero intero maggiore di 0";
				this.log.error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		
		
		// risorsa
		String valoreDataElementRisorsa = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA);
		if(valoreDataElementRisorsa!=null && !"".equals(valoreDataElementRisorsa)){
			try{
				TipoRisorsa tipoRisorsa = this.getTipoRisorsa(valoreDataElementRisorsa, this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA_ESITI)); 
				policy.setRisorsa(tipoRisorsa.getValue());
				policy.setSimultanee(this.isTipoRisorsaNumeroRichiesteSimultanee(valoreDataElementRisorsa));
			}catch(Exception e){
				String messaggio = "Il valore ("+valoreDataElementRisorsa+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA+"' non è tra i tipi di risorsa gestiti";
				this.log.error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		
		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}
	
	public String readValoriSogliaPolicyFromHttpParameters(ConfigurazionePolicy policy, boolean first) throws Exception {
			
		StringBuilder sbParsingError = new StringBuilder();
		
//		// richiesteSimultanee
//		String simultanee = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_RICHIESTE_SIMULTANEE);
//		if(first==false){
//			if(TipoRisorsa.NUMERO_RICHIESTE.getValue().equals(policy.getRisorsa())){
//				policy.setSimultanee(ServletUtils.isCheckBoxEnabled(simultanee));
//			}
//			else{
//				policy.setSimultanee(false);
//			}
//		}
		
		
		// valore della policy
		TipoRisorsa tipoRisorsa = null;
		try{
			tipoRisorsa = TipoRisorsa.toEnumConstant(policy.getRisorsa(), true);
		}catch(Exception e){
			String messaggio = "Il valore ("+policy.getRisorsa()+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA+"' non è tra le risorse gestite";
			this.log.error(messaggio,e);
			this.addParsingError(sbParsingError,messaggio);
		}
		
		if(TipoRisorsa.OCCUPAZIONE_BANDA.equals(tipoRisorsa)){
			String tipoBanda = null;
			try{
				tipoBanda = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TIPO_BANDA);
				if(tipoBanda!=null && !"".equals(tipoBanda)){
					policy.setValoreTipoBanda(TipoBanda.toEnumConstant(tipoBanda, true));
				}
				else{
					if(policy.getValoreTipoBanda()==null){
						policy.setValoreTipoBanda(ConfigurazioneCostanti.TIPO_BANDA_DEFAULT);
					}
				}
			}catch(Exception e){
				String label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TIPO_BANDA;
				String messaggio = "Il valore ("+tipoBanda+") indicato in '"+label+"' non rientra tra i tipi conosciuti";
				this.log.error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			policy.setValoreTipoBanda(null);
		}
		
		if(TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(tipoRisorsa) || TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(tipoRisorsa)){
			String tipoLatenza = null;
			try{
				tipoLatenza = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TIPO_LATENZA);
				if(tipoLatenza!=null && !"".equals(tipoLatenza)){
					policy.setValoreTipoLatenza(TipoLatenza.toEnumConstant(tipoLatenza, true));
				}
				else{
					if(policy.getValoreTipoLatenza()==null){
						policy.setValoreTipoLatenza(ConfigurazioneCostanti.TIPO_LATENZA_DEFAULT);
					}
				}
			}catch(Exception e){
				String label = null;
				if(TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(tipoRisorsa)){
					label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_TIPO_LATENZA;
				}
				else{
					label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_TIPO_LATENZA;
				}
				String messaggio = "Il valore ("+tipoLatenza+") indicato in '"+label+"' non rientra tra i tipi conosciuti";
				this.log.error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			policy.setValoreTipoLatenza(null);
		}
		
		
		String valore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE);
		if(valore!=null && !"".equals(valore)){
			try{
				long l = Long.parseLong(valore);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				policy.setValore(l);
			}catch(Exception e){
				String label = null;
				if(TipoRisorsa.NUMERO_RICHIESTE.equals(tipoRisorsa)){
					label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE;
				}
				else if(TipoRisorsa.OCCUPAZIONE_BANDA.equals(tipoRisorsa)){
					label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_OCCUPAZIONE_DI_BANDA_LABEL;
				}
				else if(TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(tipoRisorsa)){
					label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_LABEL;
				}
				else{
					label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_LABEL;
				}
				String messaggio = "Il valore ("+valore+") indicato in '"+label+"' deve essere un numero intero maggiore di 0";
				this.log.error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		else{
			if(!first){
				policy.setValore(null);
			}
		}
		
		
		if(policy.isSimultanee()==false){
			
			// Modalità di Controllo
			if(TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(policy.getRisorsa())){
				// è permesso solo il realtime
				policy.setModalitaControllo(TipoControlloPeriodo.REALTIME);
			}
			else{
				String modalitaControllo = null;
				try{
					modalitaControllo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_CONTROLLO);
					if(modalitaControllo!=null && !"".equals(modalitaControllo)){
						policy.setModalitaControllo(TipoControlloPeriodo.toEnumConstant(modalitaControllo, true));
					}
					
				}catch(Exception e){
					String messaggio = "Il valore ("+modalitaControllo+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_CONTROLLO+"' non rientra tra le modalità conosciute";
					this.log.error(messaggio,e);
					this.addParsingError(sbParsingError,messaggio);
				}
			}
			
			// Intervallo Osservazione
			if(policy.getModalitaControllo()!=null){
				
				String tipoControlloPeriodo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_PERIODO);
				if(tipoControlloPeriodo!=null && !"".equals(tipoControlloPeriodo)){
					if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
						try{
							policy.setTipoIntervalloOsservazioneRealtime(TipoPeriodoRealtime.toEnumConstant(tipoControlloPeriodo, true));
						}catch(Exception e){
							try{
								// Controllo se l'errore fosse dovuto al fatto che è stato cambiato la modalitù di controllo ed il valore presenta era per l'altra modalita
								if(TipoPeriodoStatistico.toEnumConstant(tipoControlloPeriodo, true)!=null){
									policy.setTipoIntervalloOsservazioneRealtime(ConfigurazioneCostanti.TIPO_PERIODO_REALTIME_DEFAULT);
								}
							}catch(Exception eInterno){
								// NOTA: Viene registrato volutamente l'errore più esterno
								String messaggio = "Il valore ("+tipoControlloPeriodo+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_PERIODO+"' non rientra tra gli intervalli conosciuti";
								this.log.error(messaggio,e);
								this.addParsingError(sbParsingError,messaggio);
							}
						}
					}
					else{
						try{
							policy.setTipoIntervalloOsservazioneStatistico(TipoPeriodoStatistico.toEnumConstant(tipoControlloPeriodo, true));
						}catch(Exception e){
							try{
								// Controllo se l'errore fosse dovuto al fatto che è stato cambiato la modalitù di controllo ed il valore presenta era per l'altra modalita
								if(TipoPeriodoRealtime.toEnumConstant(tipoControlloPeriodo, true)!=null){
									policy.setTipoIntervalloOsservazioneStatistico(ConfigurazioneCostanti.TIPO_PERIODO_STATISTICO_DEFAULT);
								}
							}catch(Exception eInterno){
								// NOTA: Viene registrato volutamente l'errore più esterno
								String messaggio = "Il valore ("+tipoControlloPeriodo+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_PERIODO+"' non rientra tra gli intervalli conosciuti";
								this.log.error(messaggio,e);
								this.addParsingError(sbParsingError,messaggio);
							}
						}
					}
				}
				
				String periodo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_PERIODO);
				if(periodo!=null && !"".equals(periodo)){
					try{
						int i = Integer.parseInt(periodo);
						if(i<=0){
							throw new Exception("Valore non nell'intervallo");
						}
						policy.setIntervalloOsservazione(i);
					}catch(Exception e){
						String labelPeriodo = null;
						if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
							labelPeriodo = this.getLabel(policy.getTipoIntervalloOsservazioneRealtime());
						}else{
							labelPeriodo = this.getLabel(policy.getTipoIntervalloOsservazioneStatistico());
						}
						String messaggio = "Il valore ("+periodo+") indicato in '"+labelPeriodo+"' deve essere un numero intero maggiore di 0";
						this.log.error(messaggio,e);
						this.addParsingError(sbParsingError,messaggio);
					}
				}
				else{
					if(!first){
						policy.setIntervalloOsservazione(null);
					}
				}
				
				String finestraPeriodo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_FINESTRA_PERIODO);
				if(finestraPeriodo!=null && !"".equals(finestraPeriodo)){
					boolean found = false;
					if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
						for (int i = 0; i < ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_REALTIME.length; i++) {
							if(ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_REALTIME[i].equals(finestraPeriodo)){
								found = true;
								break;
							}
						}
					}
					else{
						for (int i = 0; i < ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_STATISTICO.length; i++) {
							if(ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_STATISTICO[i].equals(finestraPeriodo)){
								found = true;
								break;
							}
						}
					}
					if(found){
						// Controllo se l'errore fosse dovuto al fatto che è stato cambiato la modalitù di controllo ed il valore presenta era per l'altra modalita
						try{
							policy.setFinestraOsservazione(TipoFinestra.toEnumConstant(finestraPeriodo, true));
						}catch(Exception e){
							String messaggio = "Il valore ("+finestraPeriodo+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_FINESTRA_PERIODO+"' non rientra tra le finestre conosciute";
							this.log.error(messaggio,e);
							this.addParsingError(sbParsingError,messaggio);
						}
					}
					else{
						policy.setFinestraOsservazione(null);
					}
				}
				
				
				// Controllo se è stato cambiato il tipo di risorsa. In tal caso forzo il change del tipo di finestra
				String postBackElementName = this.getPostBackElementName();
				if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA.equals(postBackElementName) || 
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_CONTROLLO.equals(postBackElementName)){
					if(policy.getFinestraOsservazione()!=null){
						policy.setFinestraOsservazione(null);
					}
				}
			}
			else{
				// reset
				policy.setModalitaControllo(ConfigurazioneCostanti.TIPO_CONTROLLO_PERIODO_DEFAULT);
				policy.setTipoIntervalloOsservazioneRealtime(ConfigurazioneCostanti.TIPO_PERIODO_REALTIME_DEFAULT);
				policy.setTipoIntervalloOsservazioneStatistico(ConfigurazioneCostanti.TIPO_PERIODO_STATISTICO_DEFAULT);
				policy.setIntervalloOsservazione(null);
				policy.setFinestraOsservazione(null);
			}
			
		}
		else{
			// reset
			policy.setModalitaControllo(ConfigurazioneCostanti.TIPO_CONTROLLO_PERIODO_DEFAULT);
			policy.setTipoIntervalloOsservazioneRealtime(ConfigurazioneCostanti.TIPO_PERIODO_REALTIME_DEFAULT);
			policy.setTipoIntervalloOsservazioneStatistico(ConfigurazioneCostanti.TIPO_PERIODO_STATISTICO_DEFAULT);
			policy.setIntervalloOsservazione(null);
			policy.setFinestraOsservazione(null);
		}
		

		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}
	
	public String readApplicabilitaPolicyFromHttpParameters(ConfigurazionePolicy policy, boolean first) throws Exception {
			
		StringBuilder sbParsingError = new StringBuilder();
		
		// condizionale
		String condizionale = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_TIPO);
		if(first==false){
			if (ServletUtils.isCheckBoxEnabled(condizionale) ){
				policy.setTipoApplicabilita(TipoApplicabilita.CONDIZIONALE);
			}
			else{
				policy.setTipoApplicabilita(TipoApplicabilita.SEMPRE);
			}
		}
		else{
			if(policy.getTipoApplicabilita()==null)
				policy.setTipoApplicabilita(TipoApplicabilita.SEMPRE);
		}
		
		
		// con congestione in corso
		String congestioneInCorso = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE);
		if(first==false){
			policy.setApplicabilitaConCongestione(ServletUtils.isCheckBoxEnabled(congestioneInCorso));
		}
		
		
		// con degrado prestazionale
		String degradoPrestazionale = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE);
		if(first==false){
			policy.setApplicabilitaDegradoPrestazionale(ServletUtils.isCheckBoxEnabled(degradoPrestazionale));
		}
		
		
		// con stato allarme
		String statoAllarme = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME);
		if(first==false){
			policy.setApplicabilitaStatoAllarme(ServletUtils.isCheckBoxEnabled(statoAllarme));
		}
		
		
		// Degrado Prestazionale
		String messaggioErroreDegradoPrestazione = readApplicabilitaDegradoPrestazionalePolicyFromHttpParameters(policy, first);
		if(messaggioErroreDegradoPrestazione != null)
			this.addParsingError(sbParsingError, messaggioErroreDegradoPrestazione);
		
		// StatoAllarmi
		String messaggioErroreReadApplicabilita = readApplicabilitaStatoAllarmePolicyFromHttpParameters(policy, first);
		if(messaggioErroreReadApplicabilita != null)
			this.addParsingError(sbParsingError, messaggioErroreReadApplicabilita);
		

		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}
	
	private String readApplicabilitaDegradoPrestazionalePolicyFromHttpParameters(ConfigurazionePolicy policy, boolean first) throws Exception {

		StringBuilder sbParsingError = new StringBuilder();
		// ** Degrado Prestazionale **
		if(policy.isApplicabilitaDegradoPrestazionale()){
			
			// Modalità di Controllo
			String modalitaControllo = null;
			try{
				modalitaControllo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_CONTROLLO);
				if(modalitaControllo!=null && !"".equals(modalitaControllo)){
					policy.setDegradoAvgTimeModalitaControllo(TipoControlloPeriodo.toEnumConstant(modalitaControllo, true));
				}
				else{
					if(policy.getDegradoAvgTimeModalitaControllo()==null){
						policy.setDegradoAvgTimeModalitaControllo(ConfigurazioneCostanti.TIPO_CONTROLLO_PERIODO_DEFAULT);
					}
				}
			}catch(Exception e){
				String messaggio = "Il valore ("+modalitaControllo+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_CONTROLLO+"' non rientra tra le modalità conosciute";
				this.log.error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
					
			// Tipo Latenza
			String tipoLatenza = null;
			try{
				tipoLatenza = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_LATENZA);
				if(tipoLatenza!=null && !"".equals(tipoLatenza)){
					policy.setDegradoAvgTimeTipoLatenza(TipoLatenza.toEnumConstant(tipoLatenza, true));
				}
				else{
					if(policy.getDegradoAvgTimeTipoLatenza()==null){
						policy.setDegradoAvgTimeTipoLatenza(ConfigurazioneCostanti.TIPO_LATENZA_DEFAULT);
					}
				}
			}catch(Exception e){
				String messaggio = "Il valore ("+tipoLatenza+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_LATENZA+"' non rientra tra i tipi conosciuti";
				this.log.error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}

			// Intervallo Osservazione
			String tipoControlloPeriodo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_PERIODO);
			if(tipoControlloPeriodo!=null && !"".equals(tipoControlloPeriodo)){
				if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
					try{
						policy.setDegradoAvgTimeTipoIntervalloOsservazioneRealtime(TipoPeriodoRealtime.toEnumConstant(tipoControlloPeriodo, true));
					}catch(Exception e){
						try{
							// Controllo se l'errore fosse dovuto al fatto che è stato cambiato la modalitù di controllo ed il valore presenta era per l'altra modalita
							if(TipoPeriodoStatistico.toEnumConstant(tipoControlloPeriodo, true)!=null){
								policy.setDegradoAvgTimeTipoIntervalloOsservazioneRealtime(ConfigurazioneCostanti.TIPO_PERIODO_REALTIME_DEFAULT);
							}
						}catch(Exception eInterno){
							// NOTA: Viene registrato volutamente l'errore più esterno
							String messaggio = "Il valore ("+tipoControlloPeriodo+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_PERIODO+"' non rientra tra gli intervalli conosciuti";
							this.log.error(messaggio,e);
							this.addParsingError(sbParsingError,messaggio);
						}
					}
				}
				else{
					try{
						policy.setDegradoAvgTimeTipoIntervalloOsservazioneStatistico(TipoPeriodoStatistico.toEnumConstant(tipoControlloPeriodo, true));
					}catch(Exception e){
						try{
							// Controllo se l'errore fosse dovuto al fatto che è stato cambiato la modalitù di controllo ed il valore presenta era per l'altra modalita
							if(TipoPeriodoRealtime.toEnumConstant(tipoControlloPeriodo, true)!=null){
								policy.setDegradoAvgTimeTipoIntervalloOsservazioneStatistico(ConfigurazioneCostanti.TIPO_PERIODO_STATISTICO_DEFAULT);
							}
						}catch(Exception eInterno){
							// NOTA: Viene registrato volutamente l'errore più esterno
							String messaggio = "Il valore ("+tipoControlloPeriodo+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_PERIODO+"' non rientra tra gli intervalli conosciuti";
							this.log.error(messaggio,e);
							this.addParsingError(sbParsingError,messaggio);
						}
					}
				}
			}
			else{
				if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
					if(policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime()==null){
						policy.setDegradoAvgTimeTipoIntervalloOsservazioneRealtime(ConfigurazioneCostanti.TIPO_PERIODO_REALTIME_DEFAULT);
					}
				}
				else{
					if(policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico()==null){
						policy.setDegradoAvgTimeTipoIntervalloOsservazioneStatistico(ConfigurazioneCostanti.TIPO_PERIODO_STATISTICO_DEFAULT);
					}
				}
			}
			
			String periodo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_PERIODO);
			if(periodo!=null && !"".equals(periodo)){
				try{
					int i = Integer.parseInt(periodo);
					if(i<=0){
						throw new Exception("Valore non nell'intervallo");
					}
					policy.setDegradoAvgTimeIntervalloOsservazione(i);
				}catch(Exception e){
					String labelIntervallo = null;
					if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
						labelIntervallo = this.getLabel(policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime());
					}
					else{
						labelIntervallo = this.getLabel(policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico());
					}
					String messaggio = "Il valore ("+periodo+") indicato in '"+labelIntervallo+"' deve essere un numero intero maggiore di 0";
					this.log.error(messaggio,e);
					this.addParsingError(sbParsingError,messaggio);
				}
			}
			else{
				if(!first){
					policy.setDegradoAvgTimeIntervalloOsservazione(null);
				}
			}
			
			String finestraPeriodo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_FINESTRA_PERIODO);
			if(finestraPeriodo!=null && !"".equals(finestraPeriodo)){
				boolean found = false;
				if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
					for (int i = 0; i < ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_REALTIME.length; i++) {
						if(ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_REALTIME[i].equals(finestraPeriodo)){
							found = true;
							break;
						}
					}
				}
				else{
					for (int i = 0; i < ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_STATISTICO.length; i++) {
						if(ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_STATISTICO[i].equals(finestraPeriodo)){
							found = true;
							break;
						}
					}
				}
				if(found){
					// Controllo se l'errore fosse dovuto al fatto che è stato cambiato la modalitù di controllo ed il valore presenta era per l'altra modalita
					try{
						policy.setDegradoAvgTimeFinestraOsservazione(TipoFinestra.toEnumConstant(finestraPeriodo, true));
					}catch(Exception e){
						String messaggio = "Il valore ("+finestraPeriodo+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_FINESTRA_PERIODO+"' non rientra tra le finestre conosciute";
						this.log.error(messaggio,e);
						this.addParsingError(sbParsingError,messaggio);
					}
				}
				else{
					policy.setDegradoAvgTimeFinestraOsservazione(null);
				}
			}
			
			// Controllo se è stato cambiato il tipo di risorsa. In tal caso forzo il change del tipo di finestra
			String postBackElementName = this.getPostBackElementName();
			//if(ConfigurazioneCostanti.PARAMETRO_CONTROLLO_CONGESTIONE_POLICY_RISORSA.equals(postBackElementName) ||
			// La risorsa non impatta sul tipo di finestra di osservazione. Al massimo se viene scelta una risorsa dove non è previsto il controllo sul degrado sparisce tutto il degrado
			if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_CONTROLLO.equals(postBackElementName)){
				if(policy.getDegradoAvgTimeFinestraOsservazione()!=null){
					policy.setDegradoAvgTimeFinestraOsservazione(null);
				}
			}
			
		}
		else{
			
			policy.setDegradoAvgTimeModalitaControllo(null);
			policy.setDegradoAvgTimeTipoLatenza(null);
			policy.setDegradoAvgTimeTipoIntervalloOsservazioneRealtime(null);
			policy.setDegradoAvgTimeTipoIntervalloOsservazioneStatistico(null);
			policy.setDegradoAvgTimeIntervalloOsservazione(null);
			policy.setDegradoAvgTimeFinestraOsservazione(null);
			
		}
	
		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}
	
	private String readApplicabilitaStatoAllarmePolicyFromHttpParameters(ConfigurazionePolicy policy, boolean first) throws Exception {
		
		StringBuilder sbParsingError = new StringBuilder();
		// ** Degrado Prestazionale **
		if(policy.isApplicabilitaStatoAllarme()){
	
			// Nome
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOME);
			if(nome!=null && !"".equals(nome) && !"-".equals(nome)){
				policy.setAllarmeNome(nome);
			}
			
			// Not Stato
			String statoNotAllarme = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOT_STATO);
			if(first==false){
				policy.setAllarmeNotStato(ServletUtils.isCheckBoxEnabled(statoNotAllarme));
			}
			
			// StatoAllarme
			String stato = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_STATO);
			if(stato!=null && !"".equals(stato)){
				try{
					int i = Integer.parseInt(stato);
					if(i<0){
						throw new Exception("Valore non nell'intervallo");
					}
					policy.setAllarmeStato(i);
				}catch(Exception e){
					String messaggio = "Lo stato ("+stato+") selezionato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_STATO+
							"' non risulta tra quelli gestiti";
					this.log.error(messaggio,e);
					this.addParsingError(sbParsingError,messaggio);
				}
			}
			else{
				if(policy.getAllarmeStato()==null){
					policy.setAllarmeStato(ConfigurazioneCostanti.CONFIGURAZIONE_ALLARME_STATO_DEFAULT);
				}
			}
			
		}
		else{
			
			policy.setAllarmeNome(null);
			policy.setAllarmeNotStato(false);
			policy.setAllarmeStato(null);
			
		}
		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}
	
	public String getNomeSuggerito(ConfigurazionePolicy policy){
		
		StringBuilder bfSuggerimentoNome = new StringBuilder();
		bfSuggerimentoNome.append(policy.getRisorsa());
		if(policy.isSimultanee()){
			bfSuggerimentoNome.append("-").append("RichiesteSimultanee");
		}
		else{
			if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
				bfSuggerimentoNome.append("-ControlloRealtime").
					append((policy.getTipoIntervalloOsservazioneRealtime().getValue().charAt(0)+"").toUpperCase()).
					append(policy.getTipoIntervalloOsservazioneRealtime().getValue().substring(1));
			}
			else{
				bfSuggerimentoNome.append("-ControlloStatistico").
					append((policy.getTipoIntervalloOsservazioneStatistico().getValue().charAt(0)+"").toUpperCase()).
					append(policy.getTipoIntervalloOsservazioneStatistico().getValue().substring(1));
			}	
		}
		if(TipoApplicabilita.CONDIZIONALE.equals(policy.getTipoApplicabilita())){
			StringBuilder bfInterno = new StringBuilder();
			bfInterno.append("-Condizionale");
			if(policy.isApplicabilitaConCongestione()){
				bfInterno.append("-CongestioneTraffico");
			}
			if(policy.isApplicabilitaDegradoPrestazionale()){
				bfInterno.append("-DegradoPrestazionale");
				if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
					bfInterno.append("Realtime").
					append((policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime().getValue().charAt(0)+"").toUpperCase()).
					append(policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime().getValue().substring(1));
				}
				else{
					bfInterno.append("Statistico").
					append((policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico().getValue().charAt(0)+"").toUpperCase()).
					append(policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico().getValue().substring(1));
				}
			}
			if(policy.isApplicabilitaStatoAllarme()){
				bfInterno.append("-StatoAllarme");
			}
			bfSuggerimentoNome.append(bfInterno.toString());
		}
		return bfSuggerimentoNome.toString();
		
	}
	
	public String getDescrizioneSuggerita(ConfigurazionePolicy policy) {
		
		StringBuilder bfSuggerimentoDescrizione = new StringBuilder();
		
		TipoRisorsa risorsa = TipoRisorsa.toEnumConstant(policy.getRisorsa());
		
		StringBuilder bfIntervallo = new StringBuilder();
		if(!policy.isSimultanee()){
			bfIntervallo.append(" durante l'intervallo di tempo specificato in ");
			if(policy.getIntervalloOsservazione()!=null)
				bfIntervallo.append(policy.getIntervalloOsservazione()).append(" ");
			
			TipoFinestra finestra = policy.getFinestraOsservazione();
			if(finestra==null){
				finestra = this.getTipoFinestraDefault(policy.getModalitaControllo(), policy.getRisorsa(), false);
			}
			String finestraDescrizione = "";
			if(finestra!=null){
				finestraDescrizione = ", finestra "+finestra.getValue();
			}
			
			String tipoRisorsa = "";
			if(risorsa!=null && TipoRisorsa.OCCUPAZIONE_BANDA.equals(risorsa)){
				TipoBanda banda = policy.getValoreTipoBanda();
				if(banda==null){
					banda = ConfigurazioneCostanti.TIPO_BANDA_DEFAULT;
				}
				if(banda!=null){
					tipoRisorsa = ", tipo banda "+banda.getValue();
				}
			}
			else if(risorsa!=null && (TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(risorsa) || TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(risorsa)) ){
				TipoLatenza latenza = policy.getValoreTipoLatenza();
				if(latenza==null){
					latenza = ConfigurazioneCostanti.TIPO_LATENZA_DEFAULT;
				}
				if(latenza!=null){
					tipoRisorsa = ", tipo latenza "+latenza.getValue();
				}
			}
			
			if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
				switch (policy.getTipoIntervalloOsservazioneRealtime()) {
				case SECONDI:
					bfIntervallo.append("secondi");
					break;
				case MINUTI:
					bfIntervallo.append("minuti");
					break;
				case ORARIO:
					bfIntervallo.append("ore");
					break;
				case GIORNALIERO:
					bfIntervallo.append("giorni");
					break;
				}
				bfIntervallo.append(" (campionamento real-time").
					append(finestraDescrizione).
					append(tipoRisorsa).
					append(").");
			}
			else{
				switch (policy.getTipoIntervalloOsservazioneStatistico()) {
				case ORARIO:
					bfIntervallo.append("ore");
					break;
				case GIORNALIERO:
					bfIntervallo.append("giorni");
					break;
				case SETTIMANALE:
					bfIntervallo.append("settimane");
					break;
				case MENSILE:
					bfIntervallo.append("mesi");
					break;
				}
				bfIntervallo.append(" (campionamento statistico").
					append(finestraDescrizione).
					append(tipoRisorsa).
					append(").");
			}	
		}
		
		if(risorsa!=null){
			switch (risorsa) {
			case NUMERO_RICHIESTE:
				bfSuggerimentoDescrizione.append("La policy limita il numero totale massimo di richieste ");
				if(policy.isSimultanee()){
					bfSuggerimentoDescrizione.append("simultanee ");
				}
				bfSuggerimentoDescrizione.append("consentite");
				if(policy.isSimultanee()){
					bfSuggerimentoDescrizione.append(".");
				}
				break;
			case OCCUPAZIONE_BANDA:
				bfSuggerimentoDescrizione.append("La policy limita il numero totale massimo di KB consentiti");
				break;
			case TEMPO_MEDIO_RISPOSTA:
				bfSuggerimentoDescrizione.append("La policy blocca ogni successiva richiesta se viene rilevato un tempo medio di risposta elevato");
				break;
			case TEMPO_COMPLESSIVO_RISPOSTA:
				bfSuggerimentoDescrizione.append("La policy limita il numero totale massimo di secondi consentiti");
				break;
			case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
				bfSuggerimentoDescrizione.append("La policy conteggia il numero di richieste completate con successo; raggiunto il limite, ogni successiva richiesta viene bloccata");
				break;
			case NUMERO_RICHIESTE_FALLITE:
				bfSuggerimentoDescrizione.append("La policy conteggia il numero di richieste fallite; raggiunto il limite, ogni successiva richiesta viene bloccata");
				break;
			case NUMERO_FAULT_APPLICATIVI:
				bfSuggerimentoDescrizione.append("La policy conteggia il numero di richieste che veicolano un fault applicativo; raggiunto il limite, ogni successiva richiesta viene bloccata");
				break;
			case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
				bfSuggerimentoDescrizione.append("La policy conteggia il numero di richieste fallite o che veicolano un fault applicativo; raggiunto il limite, ogni successiva richiesta viene bloccata");
				break;
			}
		}
		
		bfSuggerimentoDescrizione.append(bfIntervallo.toString());
		
		
		if(policy.isApplicabilitaConCongestione() || policy.isApplicabilitaDegradoPrestazionale() || policy.isApplicabilitaStatoAllarme()){
			bfSuggerimentoDescrizione.append("\nLa policy viene applicata solamente se ");
			StringBuilder bfApplicabilita = new StringBuilder();
			if(policy.isApplicabilitaConCongestione()){
				bfApplicabilita.append("il Gateway risulta Congestionato dalle richieste");
			}
			if(policy.isApplicabilitaDegradoPrestazionale()){
				if(bfApplicabilita.length()>0){
					bfApplicabilita.append(" ed ");
				}
				
				TipoFinestra finestra = policy.getDegradoAvgTimeFinestraOsservazione();
				if(finestra==null){
					finestra = this.getTipoFinestraDefault(policy.getDegradoAvgTimeModalitaControllo(), policy.getRisorsa(), true);
				}
				String finestraDescrizione = "";
				if(finestra!=null){
					finestraDescrizione = ", finestra "+finestra.getValue();
				}
				
				String tipoRisorsa = "";
				TipoLatenza latenza = policy.getDegradoAvgTimeTipoLatenza();
				if(latenza==null){
					latenza = ConfigurazioneCostanti.TIPO_LATENZA_DEFAULT;
				}
				if(latenza!=null){
					tipoRisorsa = ", tipo latenza "+latenza.getValue();
				}
				
				String intervalloDescrizione = "";
				if(policy.getDegradoAvgTimeModalitaControllo()!=null){
					intervalloDescrizione =  ", intervallo di tempo specificato in ";
					if(policy.getDegradoAvgTimeIntervalloOsservazione()!=null){
						intervalloDescrizione+=policy.getDegradoAvgTimeIntervalloOsservazione();
						intervalloDescrizione+=" ";
					}
					
					if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
						switch (policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime()) {
						case SECONDI:
							intervalloDescrizione+="secondi";
							break;
						case MINUTI:
							intervalloDescrizione+="minuti";
							break;
						case ORARIO:
							intervalloDescrizione+="ore";
							break;
						case GIORNALIERO:
							intervalloDescrizione+="giorni";
							break;
						}
					}
					else{
						switch (policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico()) {
						case ORARIO:
							intervalloDescrizione+="ore";
							break;
						case GIORNALIERO:
							intervalloDescrizione+="giorni";
							break;
						case SETTIMANALE:
							intervalloDescrizione+="settimane";
							break;
						case MENSILE:
							intervalloDescrizione+="mesi";
							break;
						}
					}	
				}
				
				String tipoCampionamento = null;
				if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
					tipoCampionamento = "(campionamento realtime"+intervalloDescrizione+finestraDescrizione+tipoRisorsa+")";
				}
				else{
					tipoCampionamento = "(campionamento statistico"+intervalloDescrizione+finestraDescrizione+tipoRisorsa+")";
				}
				bfApplicabilita.append("il tempo medio di risposta del servizio "+tipoCampionamento+" risulta superiore ai livelli di soglia impostati");
			}
			if(policy.isApplicabilitaStatoAllarme()){
				if(bfApplicabilita.length()>0){
					bfApplicabilita.append(" e ");
				}
				bfApplicabilita.append("l'allarme selezionato soddisfa lo stato indicato");
			}
			bfApplicabilita.append(".");
			bfSuggerimentoDescrizione.append(bfApplicabilita.toString());
		}
		return bfSuggerimentoDescrizione.toString();
		
	}
	
	public TipoFinestra getTipoFinestraDefault(TipoControlloPeriodo modalitaControllo,String risorsa, boolean degrado){
		if(modalitaControllo!=null){
			if(TipoControlloPeriodo.REALTIME.equals(modalitaControllo)){
				if(degrado){
					return TipoFinestra.PRECEDENTE;
				}
				else if(risorsa!=null){
					TipoRisorsa tipo = TipoRisorsa.toEnumConstant(risorsa);
					if(tipo!=null){
						switch (tipo) {
						case NUMERO_RICHIESTE:
						case OCCUPAZIONE_BANDA:
						case TEMPO_COMPLESSIVO_RISPOSTA:
						case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
						case NUMERO_RICHIESTE_FALLITE:
						case NUMERO_FAULT_APPLICATIVI:
						case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
							return TipoFinestra.CORRENTE;
						case TEMPO_MEDIO_RISPOSTA:
							return TipoFinestra.PRECEDENTE;
						}
					}
				}
			}
			else{
				if(degrado){
					return TipoFinestra.SCORREVOLE;
				}
				else if(risorsa!=null){
					TipoRisorsa tipo = TipoRisorsa.toEnumConstant(risorsa);
					if(tipo!=null){
						switch (tipo) {
						case NUMERO_RICHIESTE:
						case OCCUPAZIONE_BANDA:
						case TEMPO_COMPLESSIVO_RISPOSTA:
						case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
						case NUMERO_RICHIESTE_FALLITE:
						case NUMERO_FAULT_APPLICATIVI:
						case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
							return TipoFinestra.CORRENTE;
						case TEMPO_MEDIO_RISPOSTA:
							return TipoFinestra.SCORREVOLE;
						}
					}
				}
			}
		}
		return null;
	}
	
	public void addConfigurazionePolicyToDati(Vector<DataElement> dati, TipoOperazione tipoOperazione, ConfigurazionePolicy policy, boolean editMode, long numeroPolicyIstanziate,
			String oldNomeSuggeritoPolicy,  String oldDDescrizioneSuggeritaPolicy, String oldPolicyId) throws Exception {
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		if(policy.getId()!=null && policy.getId()>0){
			// id
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID);
			de.setValue(policy.getId()+"");
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		
		if(oldNomeSuggeritoPolicy !=null){
			// oldNomeSuggerito
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_OLD_NOME_SUGGERITO);
			de.setValue(oldNomeSuggeritoPolicy);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		
		if(oldDDescrizioneSuggeritaPolicy !=null){
			// oldNomeSuggerito
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_OLD_DESCRIZIONE_SUGGERITA);
			de.setValue(oldDDescrizioneSuggeritaPolicy);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		
		if(oldPolicyId !=null){
			// oldId
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_OLD_ID);
			de.setValue(oldPolicyId);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		
		/* Servono per le bradcump */
		
		// name
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NOME);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NOME);
		de.setValue(policy.getIdPolicy());
		if(editMode) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else {
			de.setType(DataElementType.TEXT);
		}
		//de.setSize(consoleHelper.getSize());
		de.setSize(70);
		dati.addElement(de);
		
		// descrizione
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_DESCRIZIONE);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_DESCRIZIONE);
		de.setValue(StringEscapeUtils.escapeHtml(policy.getDescrizione()));
		if(editMode) {
			de.setType(DataElementType.TEXT_AREA);
			de.setLabelAffiancata(true);
			de.setRequired(true);
		}
		else {
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		}
		de.setRows(6);
		de.setCols(55);
		//de.setCols(80);
		dati.addElement(de);
		
		// risorsa	
		addDataElementRisorsa(dati, 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA, getDataElementValueRisorsa(policy.getRisorsa(), policy.isSimultanee()), 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA_ESITI, getDataElementValueRisorsaEsiti(policy.getRisorsa()),
				editMode);
		
		// Descrizione sul numero di policy attive
		if(!editMode && numeroPolicyIstanziate>0) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ISTANZIATA);
			String name = ( numeroPolicyIstanziate+" istanza");
			if(numeroPolicyIstanziate>1) {
				name = ( numeroPolicyIstanziate+" istanze");
			}
			de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ISTANZIATA_VALUE.
					replace(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ISTANZIATA_TEMPLATE,name));
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
	}
	
	public void addConfigurazionePolicyValoriSoglia(Vector<DataElement> dati, TipoOperazione tipoOperazione, ConfigurazionePolicy policy, boolean editMode, boolean editOnlyValueMode) throws Exception {
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_VALORI_SOGLIA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		
//		// simultanee
//		de = new DataElement();
//		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_RICHIESTE_SIMULTANEE);
//		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_RICHIESTE_SIMULTANEE);
//		if(TipoRisorsa.NUMERO_RICHIESTE.getValue().equals(policy.getRisorsa())){
//			if(editMode) {
//				de.setType(DataElementType.CHECKBOX);
//			}
//			else {
//				de.setType(DataElementType.HIDDEN);
//			}
//		}
//		else{
//			de.setType(DataElementType.HIDDEN);
//			policy.setSimultanee(false);
//		}
//		de.setSelected(policy.isSimultanee());
//		de.setValue(policy.isSimultanee()+"");
//		de.setPostBack_viaPOST(true);
//		dati.addElement(de);
//		
//		if(TipoRisorsa.NUMERO_RICHIESTE.getValue().equals(policy.getRisorsa()) && !editMode){
//			// Il valore del parametor originale viene passato come hidden
//			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
//			de = new DataElement();
//			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_RICHIESTE_SIMULTANEE+
//					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
//			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_RICHIESTE_SIMULTANEE);
//			if(policy.isSimultanee()) {
//				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
//			}
//			else {
//				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
//			}
//			de.setType(DataElementType.TEXT);
//			dati.addElement(de);
//		}
		
		
		
		if(policy.isSimultanee()==false){
			
			// Modalità di Controllo
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_CONTROLLO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_CONTROLLO);
			if(TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(policy.getRisorsa())){
				de.setType(DataElementType.TEXT);
				policy.setModalitaControllo(TipoControlloPeriodo.REALTIME); // obbligatorio il realtime
				de.setValue(policy.getModalitaControllo().getValue());
			}else{
				de.setType(DataElementType.SELECT);
				de.setValues(ConfigurazioneCostanti.TIPI_CONTROLLO);
				de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_CONTROLLO);
				if(policy.getModalitaControllo()!=null){
					de.setSelected(policy.getModalitaControllo().getValue());
					de.setValue(policy.getModalitaControllo().getValue());
				}
			}
			de.setPostBack_viaPOST(true);
			if(!editMode) {
				de.setType(DataElementType.HIDDEN);
			}
			dati.addElement(de);
			
			if(!editMode) {
				// Il valore del parametor originale viene passato come hidden
				// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_CONTROLLO+
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_CONTROLLO);
				if(TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(policy.getRisorsa())){
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MODALITA_CONTROLLO_REALTIME);
				}
				else {
					if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MODALITA_CONTROLLO_REALTIME);
					}
					else {
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MODALITA_CONTROLLO_STATISTICA);
					}
				}
				de.setType(DataElementType.TEXT);
				dati.addElement(de);
			}
			
		}
		
		// valore della policy
		addToDatiPolicyValue(dati, policy, editMode, editOnlyValueMode);
		
		
		if(policy.isSimultanee()==false){
		
			
			// Intervallo Osservazione
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_INTERVALLO_OSSERVAZIONE);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_PERIODO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_PERIODO);
			if(editMode) {
				de.setType(DataElementType.SELECT);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
				de.setValues(ConfigurazioneCostanti.TIPI_INTERVALLO_OSSERVAZIONE_REALTIME);
				de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_INTERVALLO_OSSERVAZIONE_REALTIME);
				if(policy.getTipoIntervalloOsservazioneRealtime()!=null){
					de.setSelected(policy.getTipoIntervalloOsservazioneRealtime().getValue());
					de.setValue(policy.getTipoIntervalloOsservazioneRealtime().getValue());
				}
			}
			else{
				de.setValues(ConfigurazioneCostanti.TIPI_INTERVALLO_OSSERVAZIONE_STATISTICO);
				de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_INTERVALLO_OSSERVAZIONE_STATISTICO);
				if(policy.getTipoIntervalloOsservazioneStatistico()!=null){
					de.setSelected(policy.getTipoIntervalloOsservazioneStatistico().getValue());
					de.setValue(policy.getTipoIntervalloOsservazioneStatistico().getValue());
				}
			}
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
			if(!editMode) {
				// Il valore del parametor originale viene passato come hidden
				// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_PERIODO+
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_TIPO_PERIODO);
				if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
					switch (policy.getTipoIntervalloOsservazioneRealtime()) {
					case GIORNALIERO:
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_GIORNALIERO);
						break;
					case ORARIO:
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_ORARIO);
						break;
					case MINUTI:
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_MINUTI);
						break;
					case SECONDI:
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_SECONDI);
						break;
					}	
				}
				else {
					switch (policy.getTipoIntervalloOsservazioneStatistico()) {
					case MENSILE:
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_MENSILE);
						break;
					case SETTIMANALE:
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_SETTIMANALE);
						break;
					case GIORNALIERO:
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_GIORNALIERO);
						break;
					case ORARIO:
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_ORARIO);
						break;
					}
				}
				de.setType(DataElementType.TEXT);
				dati.addElement(de);
			}
	
			
			
			// Intervallo Osservazione (Soglia)
	
			String labelPeriodo = null;
			if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
				labelPeriodo = this.getLabel(policy.getTipoIntervalloOsservazioneRealtime());
			}else{
				labelPeriodo = this.getLabel(policy.getTipoIntervalloOsservazioneStatistico());
			}
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_PERIODO);
			de.setLabel(labelPeriodo);
			if(editMode) {
				de.setType(DataElementType.TEXT_EDIT);
			}else {
				de.setType(DataElementType.TEXT);
			}
			if(policy.getIntervalloOsservazione()!=null)
				de.setValue(policy.getIntervalloOsservazione()+"");
			if(editMode) {
				de.setRequired(true);
			}
			//de.setSize(consoleHelper.getSize());
			dati.addElement(de);
			
			
			
			// Intervallo Osservazione (Finestra)
			
			DataElement deNoEditMode = null;
			if(editMode==false) {
				deNoEditMode = new DataElement();
				deNoEditMode.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_FINESTRA_PERIODO+
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
				deNoEditMode.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_FINESTRA_PERIODO);
				deNoEditMode.setType(DataElementType.TEXT);
			}
			
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_FINESTRA_PERIODO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_FINESTRA_PERIODO);
			if(editMode) {
				de.setType(DataElementType.SELECT);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			setValueFinestra(de, deNoEditMode, policy.getModalitaControllo(), policy.getFinestraOsservazione(), policy.getRisorsa(), false);
			if(!editMode) {
				de.setRequired(false);
			}
			de.setPostBack_viaPOST(true);			
			dati.addElement(de);
			
			if(editMode==false) {
				dati.addElement(deNoEditMode);
			}
		}
	}
	
	private void setValueFinestra(DataElement de,DataElement deNoEditMode, TipoControlloPeriodo modalitaControllo, TipoFinestra finestra, String risorsa, boolean degrado){
		boolean found = false;
		if(TipoControlloPeriodo.REALTIME.equals(modalitaControllo)){
			de.setValues(ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_REALTIME);
			de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_FINESTRA_OSSERVAZIONE_REALTIME);
			if(finestra!=null){
				for (int i = 0; i < ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_REALTIME.length; i++) {
					if(ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_REALTIME[i].equals(finestra.getValue())){
						found = true;
						break;
					}
				}
			}
		}
		else{
			de.setValues(ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_STATISTICO);
			de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_FINESTRA_OSSERVAZIONE_STATISTICO);
			if(finestra!=null){
				for (int i = 0; i < ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_STATISTICO.length; i++) {
					if(ConfigurazioneCostanti.TIPI_FINESTRA_OSSERVAZIONE_STATISTICO[i].equals(finestra.getValue())){
						found = true;
						break;
					}
				}
			}
		}
		if(found){
			de.setSelected(finestra.getValue());
			de.setValue(finestra.getValue());
			if(deNoEditMode!=null) {
				switch (finestra) {
				case CORRENTE:
					deNoEditMode.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_FINESTRA_OSSERVAZIONE_CORRENTE);
					break;
				case PRECEDENTE:
					deNoEditMode.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_FINESTRA_OSSERVAZIONE_PRECEDENTE);
					break;
				case SCORREVOLE:
					deNoEditMode.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_FINESTRA_OSSERVAZIONE_SCORREVOLE);
					break;
				}
			}
		}
		else{
			TipoFinestra tipoFinestraDefault = this.getTipoFinestraDefault(modalitaControllo, risorsa, degrado);
			if(tipoFinestraDefault!=null){
				de.setSelected(tipoFinestraDefault.getValue());
				de.setValue(tipoFinestraDefault.getValue());
				if(deNoEditMode!=null) {
					switch (tipoFinestraDefault) {
					case CORRENTE:
						deNoEditMode.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_FINESTRA_OSSERVAZIONE_CORRENTE);
						break;
					case PRECEDENTE:
						deNoEditMode.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_FINESTRA_OSSERVAZIONE_PRECEDENTE);
						break;
					case SCORREVOLE:
						deNoEditMode.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_FINESTRA_OSSERVAZIONE_SCORREVOLE);
						break;
					}
				}
			}
			else{
				de.setSelectedAsNull();
				de.setValue(null);
			}
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private void addToDatiPolicyValue(Vector<DataElement> dati, ConfigurazionePolicy policy, boolean editMode, boolean editOnlyValueMode) throws Exception{
		
		TipoRisorsa tipoRisorsa = TipoRisorsa.toEnumConstant(policy.getRisorsa(), true);
	
		if(TipoRisorsa.OCCUPAZIONE_BANDA.equals(tipoRisorsa) ){
			
			DataElement de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TIPO_BANDA);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TIPO_BANDA);
			if(editMode) {
				de.setType(DataElementType.SELECT);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setValues(ConfigurazioneCostanti.TIPI_BANDA);
			de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_BANDA);
			if(policy.getValoreTipoBanda()!=null){
				de.setSelected(policy.getValoreTipoBanda().getValue());
				de.setValue(policy.getValoreTipoBanda().getValue());
			}
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
			if(!editMode) {
				// Il valore del parametor originale viene passato come hidden
				// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TIPO_BANDA+
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TIPO_BANDA);
				switch (policy.getValoreTipoBanda()) {
				case COMPLESSIVA:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_BANDA_COMPLESSIVA);
					break;
				case INTERNA:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_BANDA_INTERNA);
					break;
				case ESTERNA:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_BANDA_ESTERNA);
					break;
				}
				de.setType(DataElementType.TEXT);
				dati.addElement(de);
			}
			
		}
		
		if(TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(tipoRisorsa) || TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(tipoRisorsa)){
			
			DataElement de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TIPO_LATENZA);
			if(TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(tipoRisorsa)){
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_TIPO_LATENZA);
			}
			else{
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_TIPO_LATENZA);
			}
			if(editMode) {
				de.setType(DataElementType.SELECT);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setValues(ConfigurazioneCostanti.TIPI_LATENZA);
			de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_LATENZA);
			if(policy.getValoreTipoLatenza()!=null){
				de.setSelected(policy.getValoreTipoLatenza().getValue());
				de.setValue(policy.getValoreTipoLatenza().getValue());
			}
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
			if(!editMode) {
				// Il valore del parametor originale viene passato come hidden
				// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TIPO_LATENZA+
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
				if(TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(tipoRisorsa)){
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_TIPO_LATENZA);
				}
				else{
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_TIPO_LATENZA);
				}
				switch (policy.getValoreTipoLatenza()) {
				case TOTALE:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_LATENZA_TOTALE);
					break;
					// Porta non usato
//					case PORTA:
//						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_LATENZA_PORTA);
//						break;
				case SERVIZIO:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_LATENZA_SERVIZIO);
					break;
				}
				de.setType(DataElementType.TEXT);
				dati.addElement(de);
			}
			
		}
		
		DataElement de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE);
		switch (tipoRisorsa) {
		case NUMERO_RICHIESTE:
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE);
			break;
		case OCCUPAZIONE_BANDA:
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_OCCUPAZIONE_DI_BANDA_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_OCCUPAZIONE_DI_BANDA_NOTE);
			break;
		case TEMPO_MEDIO_RISPOSTA:
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_NOTE);
			break;
		case TEMPO_COMPLESSIVO_RISPOSTA:
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_NOTE);
			break;
		case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO_NOTE);
			break;
		case NUMERO_RICHIESTE_FALLITE:
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE_FALLITE_NOTE);
			break;
		case NUMERO_FAULT_APPLICATIVI:
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_NUMERO_FAULT_APPLICATIVI_NOTE);
			break;
		}
		if(editMode || editOnlyValueMode) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else {
			de.setType(DataElementType.TEXT);
		}
		if(policy.getValore()!=null){
			de.setValue(policy.getValore()+"");
		}
		//de.setSize(consoleHelper.getSize());
		dati.addElement(de);
		
		if(!editMode) {
			
			if(editOnlyValueMode) { 
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_MODIFICATO_CON_ISTANZE_ATTIVE_MODIFICA_EFFETTIVA);
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_ENABLED);
				dati.addElement(de);
				
			}
			else {
							
				// Link modifica
				de = new DataElement(); 
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_MODIFICATO_CON_ISTANZE_ATTIVE_RICHIESTA_MODIFICA);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_MODIFICATO_CON_ISTANZE_ATTIVE_RICHIESTA_MODIFICA);
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_MODIFICATO_CON_ISTANZE_ATTIVE_RICHIESTA_MODIFICA);
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY_CHANGE, 
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_MODIFICATO_CON_ISTANZE_ATTIVE_RICHIESTA_MODIFICA,Costanti.CHECK_BOX_ENABLED));
				de.setType(DataElementType.LINK);
				dati.addElement(de);
				
			}
		}
	}
	
	public void addConfigurazionePolicyApplicabilitaToDati(Vector<DataElement> dati, TipoOperazione tipoOperazione,  ConfigurazionePolicy policy, ConfigurazioneControlloTraffico controlloTraffico, boolean editMode) throws Exception {
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPLICABILITA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		
		// condizionale
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_TIPO);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_TIPO);
		boolean condizionata = TipoApplicabilita.CONDIZIONALE.equals(policy.getTipoApplicabilita());
		if(editMode) {
			de.setType(DataElementType.CHECKBOX);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setSelected(condizionata);
		de.setValue(condizionata+"");
		de.setPostBack_viaPOST(true);
		dati.addElement(de);
		
		if(!editMode){
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_TIPO+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_TIPO);
			if(condizionata) {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
			}
			else {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
			}
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
		// con congestione in corso
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE);
		//de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_LABEL);
		//de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE);
		de.setLabelRight(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE);
		if(condizionata){
			if(editMode) {
				de.setType(DataElementType.CHECKBOX);
				
				DataElementInfo dInfoDescrizioneCongestione = new DataElementInfo(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_LABEL);
				dInfoDescrizioneCongestione.setHeaderBody(replaceToHtmlSeparator(this.getApplicabilitaConCongestione(controlloTraffico)));
				de.setInfo(dInfoDescrizioneCongestione);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setPostBack_viaPOST(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setApplicabilitaConCongestione(false);
		}
		de.setSelected(policy.isApplicabilitaConCongestione());
		de.setValue(policy.isApplicabilitaConCongestione()+"");
		dati.addElement(de);
		
		if(!editMode && condizionata){
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			if(policy.isApplicabilitaConCongestione()) {
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE+
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE);
				de.setType(DataElementType.TEXT);
				dati.addElement(de);
			}
		}
		
		// con degrado prestazionale
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE);
		//de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_LABEL);
		//de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE);
		de.setLabelRight(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE);
		if(condizionata && 
				!TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(policy.getRisorsa()) && 
				!TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(policy.getRisorsa()) 
			){
			if(editMode) {
				de.setType(DataElementType.CHECKBOX);
				DataElementInfo dInfoDescrizioneDegrado = new DataElementInfo(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_LABEL);
				dInfoDescrizioneDegrado.setHeaderBody(replaceToHtmlSeparator(this.getApplicabilitaDegradoPrestazionale()));
				de.setInfo(dInfoDescrizioneDegrado);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setPostBack_viaPOST(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setApplicabilitaDegradoPrestazionale(false);
		}
		de.setSelected(policy.isApplicabilitaDegradoPrestazionale());
		de.setValue(policy.isApplicabilitaDegradoPrestazionale()+"");
		dati.addElement(de);
		
		if(!editMode && condizionata && 
				!TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(policy.getRisorsa()) && 
				!TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(policy.getRisorsa()) ) {
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			if(policy.isApplicabilitaDegradoPrestazionale()) {
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE+
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE);
				de.setType(DataElementType.TEXT);
				dati.addElement(de);
			}
		}
		
		// con stato allarme
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_LABEL);
		de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOTE);
		if(condizionata && this.isAllarmiModuleEnabled()){
			if(editMode) {
				de.setType(DataElementType.CHECKBOX);
				
				DataElementInfo dInfoDescrizioneAllarmi = new DataElementInfo(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_LABEL);
				dInfoDescrizioneAllarmi.setHeaderBody(replaceToHtmlSeparator(this.getApplicabilitaAllarmi()));
				de.setInfo(dInfoDescrizioneAllarmi);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setPostBack_viaPOST(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setApplicabilitaStatoAllarme(false);
		}
		de.setSelected(policy.isApplicabilitaStatoAllarme());
		de.setValue(policy.isApplicabilitaStatoAllarme()+"");
		dati.addElement(de);
		
		if(!editMode && condizionata && this.isAllarmiModuleEnabled()){
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			if(policy.isApplicabilitaStatoAllarme()) {
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME+
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOTE);
				de.setType(DataElementType.TEXT);
				dati.addElement(de);
			}
		}
		
		// ConCongestione
		//addToApplicabilitaConCongestione(dati, tipoOperazione, policy, controlloTraffico, editMode);
		
		// Degrado Prestazionale
		addToApplicabilitaDegradoPrestazionale(dati, tipoOperazione, policy , editMode);
		
		// StatoAllarme
		addToApplicabilitaStatoAllarme(dati, tipoOperazione, policy , editMode);
	}
	
	// Metodo soppiantato dall'info
	@SuppressWarnings("unused")
	private void addToApplicabilitaConCongestione(Vector<DataElement> dati, TipoOperazione tipoOperazione, ConfigurazionePolicy policy, ConfigurazioneControlloTraffico controlloTraffico, boolean editMode) throws Exception {
		
		if(policy.isApplicabilitaConCongestione()){
			DataElement de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONGESTIONE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			// Note
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE);
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			de.setRows(4);
			de.setCols(55);
			de.setValue(this.getApplicabilitaConCongestione(controlloTraffico));
			dati.addElement(de);
		}
		
	}
	
	private String getApplicabilitaConCongestione(ConfigurazioneControlloTraffico controlloTraffico) {
		
		String result = null;
		
		if(controlloTraffico.isControlloCongestioneEnabled()){
			
			Integer soglia = controlloTraffico.getControlloCongestioneThreshold();
			Long numeroThreadCongestionamento = null; 
			Long numeroThreadComplessivi = controlloTraffico.getControlloMaxThreadsSoglia();
			
			double numD = numeroThreadComplessivi.doubleValue();
			double totale = 100d;
			double sogliaD = soglia.doubleValue();
			Double numeroThreadCongestionamentoD = (numD / totale) *  sogliaD;
			numeroThreadCongestionamento = numeroThreadCongestionamentoD.longValue();
		
			result = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE_CONGESTION_ACTIVE_AS_TEXT.
					replace(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE_NUMERO, numeroThreadCongestionamento+"");
		}
		else{
			
			result = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE_CONGESTION_NOT_ACTIVE_AS_TEXT;
			
		}
		
		return result;
	}
	
	@SuppressWarnings("incomplete-switch")
	private void addToApplicabilitaDegradoPrestazionale(Vector<DataElement> dati, TipoOperazione tipoOperazione, ConfigurazionePolicy policy, boolean editMode) throws Exception {
		
		if(policy.isApplicabilitaDegradoPrestazionale()){
			DataElement de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DEGRADO_PRESTAZIONALE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			// Spostato nell'info
//			// Note
//			de = new DataElement();
//			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE);
//			de.setValue(this.getApplicabilitaDegradoPrestazionale());
//			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
//			de.setRows(6);
//			de.setCols(55);
//			dati.addElement(de);
		}
		
		// Modalità di Controllo
		DataElement de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_CONTROLLO);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_CONTROLLO);
		if(policy.isApplicabilitaDegradoPrestazionale()){
			de.setType(DataElementType.SELECT);
			de.setValues(ConfigurazioneCostanti.TIPI_CONTROLLO);
			de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_CONTROLLO);
			if(policy.getDegradoAvgTimeModalitaControllo()==null){
				policy.setDegradoAvgTimeModalitaControllo(ConfigurazioneCostanti.TIPO_CONTROLLO_PERIODO_DEFAULT);
			}
			de.setSelected(policy.getDegradoAvgTimeModalitaControllo().getValue());
			de.setPostBack_viaPOST(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setDegradoAvgTimeModalitaControllo(null);
		}
		if(policy.getDegradoAvgTimeModalitaControllo()!=null){
			de.setValue(policy.getDegradoAvgTimeModalitaControllo().getValue());
		}
		else{
			de.setValue(null);
		}
		if(!editMode) {
			de.setType(DataElementType.HIDDEN);
		}
		dati.addElement(de);
		
		if(!editMode && policy.isApplicabilitaDegradoPrestazionale()){
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_CONTROLLO+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_CONTROLLO);
			switch (policy.getDegradoAvgTimeModalitaControllo()) {
			case REALTIME:
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MODALITA_CONTROLLO_REALTIME);
				break;
			case STATISTIC:
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MODALITA_CONTROLLO_STATISTICA);
				break;
			}
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
		
		// Tipo Latenza
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_LATENZA);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_LATENZA);
		if(policy.isApplicabilitaDegradoPrestazionale()){
			de.setType(DataElementType.SELECT);
			de.setValues(ConfigurazioneCostanti.TIPI_LATENZA);
			de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_LATENZA);
			if(policy.getDegradoAvgTimeTipoLatenza()==null){
				policy.setDegradoAvgTimeTipoLatenza(ConfigurazioneCostanti.TIPO_LATENZA_DEFAULT);
			}
			de.setSelected(policy.getDegradoAvgTimeTipoLatenza().getValue());
			de.setPostBack_viaPOST(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setDegradoAvgTimeTipoLatenza(null);
		}
		if(policy.getDegradoAvgTimeTipoLatenza()!=null){
			de.setValue(policy.getDegradoAvgTimeTipoLatenza().getValue());
		}
		else{
			de.setValue(null);
		}
		if(!editMode) {
			de.setType(DataElementType.HIDDEN);
		}
		dati.addElement(de);
		
		if(!editMode && policy.isApplicabilitaDegradoPrestazionale()){
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_LATENZA+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_LATENZA);
			switch (policy.getDegradoAvgTimeTipoLatenza()) {
			case TOTALE:
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_LATENZA_TOTALE);
				break;
				// Non supportato
//			case PORTA:
//				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_LATENZA_PORTA);
//				break;
			case SERVIZIO:
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_LATENZA_SERVIZIO);
				break;
			}
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
				
		// Intervallo Osservazione
		
		if(policy.isApplicabilitaDegradoPrestazionale()){
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_INTERVALLO_OSSERVAZIONE);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_PERIODO);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_PERIODO);
		if(policy.isApplicabilitaDegradoPrestazionale()){
			de.setType(DataElementType.SELECT);
			if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
				de.setValues(ConfigurazioneCostanti.TIPI_INTERVALLO_OSSERVAZIONE_REALTIME);
				de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_INTERVALLO_OSSERVAZIONE_REALTIME);
				if(policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime()==null){
					policy.setDegradoAvgTimeTipoIntervalloOsservazioneRealtime(ConfigurazioneCostanti.TIPO_PERIODO_REALTIME_DEFAULT);
				}
				de.setSelected(policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime().getValue());
			}
			else{
				de.setValues(ConfigurazioneCostanti.TIPI_INTERVALLO_OSSERVAZIONE_STATISTICO);
				de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_INTERVALLO_OSSERVAZIONE_STATISTICO);
				if(policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico()==null){
					policy.setDegradoAvgTimeTipoIntervalloOsservazioneStatistico(ConfigurazioneCostanti.TIPO_PERIODO_STATISTICO_DEFAULT);
				}
				de.setSelected(policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico().getValue());
			}
			de.setPostBack_viaPOST(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setDegradoAvgTimeTipoIntervalloOsservazioneRealtime(null);
			policy.setDegradoAvgTimeTipoIntervalloOsservazioneStatistico(null);
		}
		if(policy.getDegradoAvgTimeModalitaControllo()!=null){
			if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
				if(policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime()!=null){
					de.setValue(policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime().getValue());
				}
				else{
					de.setValue(null);
				}
			}
			else{
				if(policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico()!=null){
					de.setValue(policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico().getValue());
				}
				else{
					de.setValue(null);
				}
			}
		}
		else{
			de.setValue(null);
		}
		if(!editMode) {
			de.setType(DataElementType.HIDDEN);
		}
		dati.addElement(de);
		
		if(!editMode && policy.isApplicabilitaDegradoPrestazionale()){
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_PERIODO+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_TIPO_PERIODO);
			if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
				switch (policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime()) {
				case GIORNALIERO:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_GIORNALIERO);
					break;
				case ORARIO:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_ORARIO);
					break;
				case MINUTI:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_MINUTI);
					break;
				case SECONDI:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_SECONDI);
					break;
				}	
			}
			else {
				switch (policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico()) {
				case MENSILE:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_MENSILE);
					break;
				case SETTIMANALE:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_SETTIMANALE);
					break;
				case GIORNALIERO:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_GIORNALIERO);
					break;
				case ORARIO:
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPO_PERIODO_ORARIO);
					break;
				}
			}
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
		
		// Intervallo Osservazione (Soglia)

		String labelIntervallo = null;
		if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
			labelIntervallo = this.getLabel(policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime());
		}
		else{
			labelIntervallo = this.getLabel(policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico());
		}
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_PERIODO);
		de.setLabel(labelIntervallo);
		if(policy.isApplicabilitaDegradoPrestazionale()){
			if(editMode) {
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			}else {
				de.setType(DataElementType.TEXT);
			}
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setDegradoAvgTimeIntervalloOsservazione(null);
		}
		if(policy.getDegradoAvgTimeIntervalloOsservazione()!=null){
			de.setValue(policy.getDegradoAvgTimeIntervalloOsservazione()+"");
		}
		//de.setSize(consoleHelper.getSize());
		dati.addElement(de);
		
		
		
		// Intervallo Osservazione (Finestra)
		
		DataElement deNoEditMode = null;
		if(editMode==false) {
			deNoEditMode = new DataElement();
			deNoEditMode.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_FINESTRA_PERIODO+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			deNoEditMode.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_FINESTRA_PERIODO);
			deNoEditMode.setType(DataElementType.TEXT);
		}
		
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_FINESTRA_PERIODO);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_FINESTRA_PERIODO);
		if(editMode) {
			de.setType(DataElementType.SELECT);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		setValueFinestra(de, deNoEditMode, policy.getDegradoAvgTimeModalitaControllo(), policy.getDegradoAvgTimeFinestraOsservazione(), policy.getRisorsa(), true);
		if(!editMode) {
			de.setRequired(false);
		}
		if(policy.isApplicabilitaDegradoPrestazionale()==false){
			de.setType(DataElementType.HIDDEN);
		}
		else{
			de.setPostBack_viaPOST(true);
		}
		dati.addElement(de);
		
		if(editMode==false && policy.isApplicabilitaDegradoPrestazionale()) {
			dati.addElement(deNoEditMode);
		}
		
		
	}
	
	private String getApplicabilitaDegradoPrestazionale() {
		
		String result = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE_AS_TEXT;
		return result;
		
	}
	
	private void addToApplicabilitaStatoAllarme(Vector<DataElement> dati, TipoOperazione tipoOperazione, ConfigurazionePolicy policy, boolean editMode) throws Exception {
		
		if(policy.isApplicabilitaStatoAllarme()){
			DataElement de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ALLARME);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
//			// Note
//			de = new DataElement();
//			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOTE);
//			de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOTE_AS_TEXT);
//			de.setType(DataElementType.TEXT);
////			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
////			de.setRows(6);
////			de.setCols(80);
//			dati.addElement(de);
		}
		
		List<String> allarmi = null;
		if(policy.isApplicabilitaStatoAllarme()){
			throw new NotImplementedException("Da implementare quando verranno aggiunti gli allarmi."); 
			
//			try{
//				allarmi = AllarmiCore.getAllIdAllarmi();
//			}catch(Exception eError){
//				this.log.error(eError.getMessage(),eError);
//			}
		}
		if(allarmi==null){
			allarmi = new ArrayList<String>();
		}
		
		if(policy.isApplicabilitaStatoAllarme() && allarmi.size()<=0){
			DataElement de = new DataElement();
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ALLARMI_NON_ESISTENTI);
			de.setType(DataElementType.NOTE);
			dati.addElement(de);
		}
		
		// Nome
		DataElement de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOME);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOME);
		if(policy.isApplicabilitaStatoAllarme() && allarmi.size()>0){
			String [] values = null;
			int index = 0;
			if(TipoOperazione.CHANGE.equals(tipoOperazione)){
				values = new String[allarmi.size()];
			}
			else{
				values = new String[allarmi.size() + 1];
				values[0] = "-";
				index = 1;
			}

			for (String allarme : allarmi) {
				values[index++] = allarme;
			}
			de.setType(DataElementType.SELECT);
			de.setValues(values);
			if(policy.getAllarmeNome()!=null){
				de.setSelected(policy.getAllarmeNome());
			}
			else{
				de.setSelected("-");
			}
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setAllarmeNome(null);
		}
		de.setValue(policy.getAllarmeNome());
		if(!editMode) {
			de.setType(DataElementType.HIDDEN);
		}
		dati.addElement(de);
		
		if(!editMode && policy.isApplicabilitaStatoAllarme() && allarmi.size()>0){
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOME+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOME);
			de.setValue(policy.getAllarmeNome());
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
		
		// NotStato
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOT_STATO);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOT_STATO);
		if(policy.isApplicabilitaStatoAllarme() && allarmi.size()>0){
			if(editMode) {
				de.setType(DataElementType.CHECKBOX);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setAllarmeNotStato(false);
		}
		de.setSelected(policy.isAllarmeNotStato());
		de.setValue(policy.isAllarmeNotStato()+"");
		dati.addElement(de);
		
		if(!editMode && policy.isApplicabilitaStatoAllarme() && allarmi.size()>0){
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOT_STATO+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOT_STATO);
			if(policy.isAllarmeNotStato()) {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
			}
			else {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
			}
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
		
		// Stato
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_STATO);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_STATO);
		if(policy.isApplicabilitaStatoAllarme() && allarmi.size()>0){
			de.setType(DataElementType.SELECT);
			de.setValues(ConfigurazioneCostanti.CONFIGURAZIONE_STATI_ALLARMI);
			de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATI_ALLARMI);
			if(policy.getAllarmeStato()==null){
				policy.setAllarmeStato(ConfigurazioneCostanti.CONFIGURAZIONE_ALLARME_STATO_DEFAULT);
			}
			de.setSelected(policy.getAllarmeStato()+"");
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setAllarmeStato(null);
		}
		if(policy.getAllarmeStato()!=null){
			de.setSelected(policy.getAllarmeStato()+"");
		}
		else{
			de.setValue(null);
		}
		if(!editMode) {
			de.setType(DataElementType.HIDDEN);
		}
		dati.addElement(de);
		
		if(!editMode && policy.isApplicabilitaStatoAllarme() && allarmi.size()>0){
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_STATO+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_STATO);
			if(policy.getAllarmeStato()!=null) {
				if(policy.getAllarmeStato().intValue() == ConfigurazioneCostanti.CONFIGURAZIONE_ALLARME_STATO_OK) {
					de.setValue(ConfigurazioneCostanti.CONFIGURAZIONE_ALLARME_LABEL_STATO_OK);		
				}
				else if(policy.getAllarmeStato().intValue() == ConfigurazioneCostanti.CONFIGURAZIONE_ALLARME_STATO_WARNING) {
					de.setValue(ConfigurazioneCostanti.CONFIGURAZIONE_ALLARME_LABEL_STATO_WARNING);		
				}
				else {
					de.setValue(ConfigurazioneCostanti.CONFIGURAZIONE_ALLARME_LABEL_STATO_ERROR);		
				}
			}
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}

	}
	
	private String getApplicabilitaAllarmi() {
		
		String result = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOTE_AS_TEXT;
		return result;
		
	}
	
	public String getLabel(TipoPeriodoRealtime tipo){
		if(tipo==null){
			return "Valore";
		}
		switch (tipo) {
		case SECONDI:
			return "Secondi";
		case MINUTI:
			return "Minuti";
		case ORARIO:
			return "Ore";
		case GIORNALIERO:
			return "Giorni";
		default:
			return "Valore";
		}
	}
	public String getLabel(TipoPeriodoStatistico tipo){
		if(tipo==null){
			return "Valore";
		}
		switch (tipo) {
		case ORARIO:
			return "Ore";
		case GIORNALIERO:
			return "Giorni";
		case SETTIMANALE:
			return "Settimane";
		case MENSILE:
			return "Mesi";
		default:
			return "Valore";
		}
	}
	
	public boolean configurazionePolicyCheckData(TipoOperazione tipoOperazione, org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico, ConfigurazionePolicy policyToCheck,
			String oldNomeSuggeritoPolicy,  String oldDescrizioneSuggeritaPolicy, String oldPolicyId,	List<AttivazionePolicy> listPolicyAttiveConStatoDisabilitato,
	boolean updateValueInSeguitoModificaSogliaPolicy) throws Exception{
		if(TipoOperazione.CHANGE.equals(tipoOperazione)){
			
			long count = 0;
			String id = null;
			if(oldPolicyId!=null){
				id = oldPolicyId;
			}
			else{
				id = policyToCheck.getIdPolicy();
			}
			count = this.confCore.countInUseAttivazioni(id);
			if(count>0){
				String messaggio = "Non è possibile modificare la policy '"+id+"' essendo utilizzata in "+count+" istanze di "+ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING;
				boolean modificaVolutaUtente = listPolicyAttiveConStatoDisabilitato != null && !listPolicyAttiveConStatoDisabilitato.isEmpty();
				long countPolicyNonDisabilitate = this.confCore.countInUseAttivazioni(id, true);
				if(countPolicyNonDisabilitate>0 && !modificaVolutaUtente ) {
					this.pd.setMessage(messaggio);
					return false;
				}
			}
				
		}
		
		ConfigurazionePolicy p = null;
		try{
			p = this.confCore.getConfigurazionePolicy(policyToCheck.getIdPolicy());
		}catch(DriverControlStationNotFound e) {
			// ignore
		}catch(Exception e){
			throw e;
		}
		
		if(p!=null){
			
			if(TipoOperazione.ADD.equals(tipoOperazione) || (p.getId()!=null && policyToCheck.getId()!=null && p.getId().longValue()!=policyToCheck.getId().longValue())){
				String messaggio = "Esiste già una policy con nome '"+policyToCheck.getIdPolicy()+"'";
				this.pd.setMessage(messaggio);
				return false;
			}
		}
		
		return this.checkConfigurazionePolicy(configurazioneControlloTraffico,policyToCheck);
	}
	
	
	public boolean checkConfigurazionePolicy(org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale c,ConfigurazionePolicy policy) throws Exception{
		
		// Dati Generali
		if(policy.getIdPolicy()==null || "".equals(policy.getIdPolicy())){
			String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NOME+"'";
			this.pd.setMessage(messaggio);
				return false;
		}
		
		// Nome
		if (!RegularExpressionEngine.isMatch(policy.getIdPolicy(),"^[\\-\\._A-Za-z0-9]*$")) {
			String messaggio = "Il nome indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NOME+
					"' puo' contenere solamente caratteri [A-Za-z], numeri [0-9] e i simboli '-','.' e '_'";
			this.pd.setMessage(messaggio);
			return false;
		}
		if(this.checkLength255(policy.getIdPolicy(), ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NOME)==false) {
			return false;
		}
		
		// Descrizione
		if(policy.getDescrizione()==null || "".equals(policy.getDescrizione())){
			String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_DESCRIZIONE+"'";
			this.pd.setMessage(messaggio);
				return false;
		}
		
		// Valori di Soglia
		TipoRisorsa tipoRisorsa = null;
		try{
			tipoRisorsa = TipoRisorsa.toEnumConstant(policy.getRisorsa(), true);
		}catch(Exception e){
			this.pd.setMessage(e.getMessage());
			return false;
		}
		String name = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE;
		if(TipoRisorsa.OCCUPAZIONE_BANDA.equals(tipoRisorsa)){
			name = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_OCCUPAZIONE_DI_BANDA_LABEL;
		}
		else if(TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(tipoRisorsa)){
			name = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_LABEL;
		}
		else if(TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(tipoRisorsa)){
			name = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_LABEL;
		}
		if(policy.getValore()==null) { //|| "".equals(policy.getValore())){
			String messaggio = "Deve essere indicato un valore in '"+name+"'";
			this.pd.setMessage(messaggio);
				return false;
		}
		long l = policy.getValore();
		try{
			if(l<0){
				throw new Exception("Valore non nell'intervallo");
			}
		}catch(Exception e){
			String messaggio = "Il valore ("+policy.getValore()+") indicato in '"+name+"' deve essere un numero intero maggiore o uguale a 0";
			this.pd.setMessage(messaggio);
			return false;
		}
		if(TipoRisorsa.NUMERO_RICHIESTE.equals(tipoRisorsa) && policy.isSimultanee()){
			if(c.getControlloTraffico().isControlloMaxThreadsEnabled()) {
				if(l > c.getControlloTraffico().getControlloMaxThreadsSoglia()){
					String messaggio = "Deve essere indicato un valore in '"+name+
							"' minore di quanto indicato nella configurazione generale alla voce '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_SOGLIA+"'";
					this.pd.setMessage(messaggio);
				return false;
				}
			}
		}
		
		if(policy.isSimultanee()==false){
			if(policy.getIntervalloOsservazione()==null){
				String labelPeriodo = null;
				if(TipoControlloPeriodo.REALTIME.equals(policy.getModalitaControllo())){
					labelPeriodo = this.getLabel(policy.getTipoIntervalloOsservazioneRealtime());
				}else{
					labelPeriodo = this.getLabel(policy.getTipoIntervalloOsservazioneStatistico());
				}
				String messaggio = "Deve essere indicato un valore in '"+labelPeriodo+"'";
				this.pd.setMessage(messaggio);
				return false;
			}
		}
		
		
		// Applicabilita
		
		if(TipoApplicabilita.CONDIZIONALE.equals(policy.getTipoApplicabilita())){
			
			if(!policy.isApplicabilitaConCongestione() && 
					!policy.isApplicabilitaDegradoPrestazionale() &&
					!policy.isApplicabilitaStatoAllarme()){
				String messaggio = "Deve essere selezionato almeno un criterio di applicabilità della Policy";
				this.pd.setMessage(messaggio);
				return false;
			}
			
		}
		
		if(policy.isApplicabilitaDegradoPrestazionale()){
						
			if(policy.getDegradoAvgTimeIntervalloOsservazione()==null){
				String labelIntervallo = null;
				if(TipoControlloPeriodo.REALTIME.equals(policy.getDegradoAvgTimeModalitaControllo())){
					labelIntervallo = this.getLabel(policy.getDegradoAvgTimeTipoIntervalloOsservazioneRealtime());
				}
				else{
					labelIntervallo = this.getLabel(policy.getDegradoAvgTimeTipoIntervalloOsservazioneStatistico());
				}
				String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DEGRADO_PRESTAZIONALE
						+" - "+labelIntervallo+"'";
				this.pd.setMessage(messaggio);
				return false;
			}
			
		}
		
		if(policy.isApplicabilitaStatoAllarme()){
			throw new NotImplementedException("Da implementare quando verranno aggiunti gli allarmi."); 
//			List<String> allarmi = null;
//			try{
//				allarmi = AllarmiCore.getAllIdAllarmi();
//			}catch(Exception eError){
//				this.log.error(eError.getMessage(),eError);
//			}

//			if(allarmi==null || allarmi.size()<=0){
//				String messaggio = "Non risultano attivi allarmi; disabilitare l'opzione '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_LABEL+"'";
//				this.pd.setMessage(messaggio);
//				return false;
//			}			
//			else{
//				if(policy.getAllarmeNome()==null || "".equals(policy.getAllarmeNome())  || "-".equals(policy.getAllarmeNome())) {
//					String messaggio = "Selezionare uno degli allarmi indicati in '"+ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ALLARME
//							+" - "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOME+"'";
//					this.pd.setMessage(messaggio);
//				return false;
//				}
//			}
		}
		return true;
	}
	
	
	private static Boolean allarmiEnabled = null;
	public boolean isAllarmiModuleEnabled(){
		if(allarmiEnabled==null){
			// sono abilitati gli allarmi se esiste la classe degli allarmi nel classpath
			try{
				Class<?> c = Class.forName("it.link.pdd.core.plugins.allarmi.ConfigurazioneAllarme");
				allarmiEnabled = (c!=null);
			}catch(ClassNotFoundException notFound){
				allarmiEnabled = false;
			}
		}
		return allarmiEnabled;
	}

	public String readDatiAttivazionePolicyFromHttpParameters(AttivazionePolicy policy, boolean first, TipoOperazione tipoOperazione, InfoPolicy infoPolicy) throws Exception{
		
		StringBuilder sbParsingError = new StringBuilder();
		// id
		String id = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID);
		if(id!=null && !"".equals(id)){
			try{
				long l = Long.parseLong(id);
				if(l<=0){
					throw new Exception("Valore non nell'intervallo");
				}
				policy.setId(l);
			}catch(Exception e){
				String messaggio = "Il valore ("+id+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID+"' deve essere un numero intero maggiore di 0";
				this.log.error(messaggio,e);
				this.addParsingError(sbParsingError,messaggio);
			}
		}
		
		// alias
		String alias = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS);
		if(alias!=null && !"".equals(alias)){
			policy.setAlias(alias);
		}
		else {
			if(!first) {
				policy.setAlias(null);
			}
		}
		
		// stato
		String stato = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ENABLED);
		if(stato!=null && !"".equals(stato)){
			policy.setEnabled(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO.equalsIgnoreCase(stato) || ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_WARNING_ONLY.equalsIgnoreCase(stato));
			policy.setWarningOnly(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_WARNING_ONLY.equalsIgnoreCase(stato));
		}
		else{
			if(TipoOperazione.ADD.equals(tipoOperazione)){
				policy.setEnabled(true); // default
				policy.setWarningOnly(false); // default
			}
		}
		
		// continue
		String continueS = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_CONTINUE);
		if(continueS!=null && !"".equals(continueS)){
			policy.setContinuaValutazione(Boolean.valueOf(continueS));
		}
		else {
			if(TipoOperazione.ADD.equals(tipoOperazione)){
				policy.setContinuaValutazione(false); // default
			}
		}
		
		
		// ridefinisci
		String ridefinisci = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_RIDEFINISCI);
		if(first==false){
			policy.setRidefinisci(ServletUtils.isCheckBoxEnabled(ridefinisci));
		}
		
		
		if(infoPolicy!=null){
			
			TipoRisorsa tipoRisorsa = infoPolicy.getTipoRisorsa();
			
			// valore
			String valore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VALORE);
			if(valore!=null && !"".equals(valore)){
				try{
					long l = Long.parseLong(valore);
					if(l<=0){
						throw new Exception("Valore non nell'intervallo");
					}
					policy.setValore(l);
				}catch(Exception e){
					String label = null;
					if(TipoRisorsa.NUMERO_RICHIESTE.equals(tipoRisorsa)){
						label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE;
					}
					else if(TipoRisorsa.OCCUPAZIONE_BANDA.equals(tipoRisorsa)){
						label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_OCCUPAZIONE_DI_BANDA_LABEL;
					}
					else if(TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(tipoRisorsa)){
						label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_LABEL;
					}
					else{
						label = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_LABEL;
					}
					String messaggio = "Il valore ("+valore+") indicato in '"+label+"' deve essere un numero intero maggiore di 0";
					this.log.error(messaggio,e);
					this.addParsingError(sbParsingError,messaggio);
				}
			}
			else{
				if(!first){
					policy.setValore(null);
				}
			}
					
		}
		else{
			policy.setValore(null);
		}
			
		
		// filtro
		String errorMsgDatiAttivazione = readDatiAttivazioneFiltroFromHttpParameters(policy, first, infoPolicy);
		if (errorMsgDatiAttivazione != null) {
			this.addParsingError(sbParsingError, errorMsgDatiAttivazione); 
		}
		
		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}

	public String readDatiAttivazioneFiltroFromHttpParameters(AttivazionePolicy policy,boolean first, InfoPolicy infoPolicy) throws Exception {
			
		StringBuilder sbParsingError = new StringBuilder();
		
		// Filtro - stato
		String stato = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED);
		if(stato!=null && !"".equals(stato)){
			policy.getFiltro().setEnabled(ServletUtils.isCheckBoxEnabled(stato));
		}
		
		// Filtro
		if(policy.getFiltro().isEnabled()){
			
			// ruolo della PdD
			String ruoloPdD = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD);
			if(ruoloPdD!=null && !"".equals(ruoloPdD)){
				try{
					policy.getFiltro().setRuoloPorta(RuoloPolicy.toEnumConstant(ruoloPdD, true));
				}catch(Exception e){
					String messaggio = "Il valore ("+ruoloPdD+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD+"' non è tra i ruoli gestiti";
					this.log.error(messaggio,e);
					this.addParsingError(sbParsingError,messaggio);
				}
			}
			
			// protocollo
			String protocollo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO);
			if(protocollo!=null && !"".equals(protocollo) && !ConfigurazioneCostanti.VALUE_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI.equals(protocollo)){
				policy.getFiltro().setProtocollo(protocollo);
			}
			else{
				if(!first){
					policy.getFiltro().setProtocollo(null);
				}
			}
			
			// ruolo erogatore
			String ruoloErogatore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_EROGATORE);
			if(ruoloErogatore!=null && !"".equals(ruoloErogatore) && !ConfigurazioneCostanti.VALUE_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI.equals(ruoloErogatore) ){
				policy.getFiltro().setRuoloErogatore(ruoloErogatore);
			}
			else{
				if(!first){
					policy.getFiltro().setRuoloErogatore(null);
				}
			}
			
			// erogatore
			String erogatore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_EROGATORE);
			boolean erogatoreSelected = false;
			if(erogatore!=null && !"".equals(erogatore) && !ConfigurazioneCostanti.VALUE_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI.equals(erogatore) && erogatore.contains("/") ){
				String [] tmp = erogatore.split("/");
				policy.getFiltro().setTipoErogatore(tmp[0]);
				policy.getFiltro().setNomeErogatore(tmp[1]);
				erogatoreSelected = true;
			}
			else{
				if(!first){
					policy.getFiltro().setTipoErogatore(null);
					policy.getFiltro().setNomeErogatore(null);
				}
			}
			
			// servizio applicativo erogatore
			String servizioApplicativoErogatore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE);
			if(servizioApplicativoErogatore!=null && !"".equals(servizioApplicativoErogatore) && !ConfigurazioneCostanti.VALUE_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI.equals(servizioApplicativoErogatore) ){
				policy.getFiltro().setServizioApplicativoErogatore(servizioApplicativoErogatore);
			}
			else{
				if(!first){
					policy.getFiltro().setServizioApplicativoErogatore(null);
				}
			}
			
			// servizio
			String servizio = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO);
			if(servizio!=null && !"".equals(servizio) && !ConfigurazioneCostanti.VALUE_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI.equals(servizio) && servizio.contains("/") ){
				String [] tmp = servizio.split("/");
				policy.getFiltro().setTipoServizio(tmp[0]);
				policy.getFiltro().setNomeServizio(tmp[1]);
				policy.getFiltro().setVersioneServizio(Integer.parseInt(tmp[2]));
				if(this.core.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore()) {
					policy.getFiltro().setTipoErogatore(tmp[3]);
					policy.getFiltro().setNomeErogatore(tmp[4]);
				}
			}
			else{
				if(!first){
					policy.getFiltro().setTipoServizio(null);
					policy.getFiltro().setNomeServizio(null);
					policy.getFiltro().setVersioneServizio(null);
					if(this.core.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore()&& !erogatoreSelected) {
						policy.getFiltro().setTipoErogatore(null);
						policy.getFiltro().setNomeErogatore(null);
					}
				}
			}
			
			// azione
			String [] azione = this.getParameterValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE);
			if(azione!=null && azione.length>0) {
				StringBuilder bf = new StringBuilder();
				for (String az : azione) {
					if(bf.length()>0) {
						bf.append(",");
					}
					bf.append(az);
				}
				policy.getFiltro().setAzione(bf.toString());
			}
			else{
				if(!first){
					policy.getFiltro().setAzione(null);
				}
			}
			
			// ruolo fruitore
			String ruoloFruitore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE);
			if(ruoloFruitore!=null && !"".equals(ruoloFruitore) && !ConfigurazioneCostanti.VALUE_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI.equals(ruoloFruitore) ){
				policy.getFiltro().setRuoloFruitore(ruoloFruitore);
			}
			else{
				if(!first){
					policy.getFiltro().setRuoloFruitore(null);
				}
			}
			
			// fruitore
			String fruitore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE);
			if(fruitore!=null && !"".equals(fruitore) && !ConfigurazioneCostanti.VALUE_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI.equals(fruitore) && fruitore.contains("/") ){
				String [] tmp = fruitore.split("/");
				policy.getFiltro().setTipoFruitore(tmp[0]);
				policy.getFiltro().setNomeFruitore(tmp[1]);
			}
			else{
				if(!first){
					policy.getFiltro().setTipoFruitore(null);
					policy.getFiltro().setNomeFruitore(null);
				}
			}
			
			// servizio applicativo fruitore
			String servizioApplicativoFruitore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE);
			if(servizioApplicativoFruitore!=null && !"".equals(servizioApplicativoFruitore) && !ConfigurazioneCostanti.VALUE_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI.equals(servizioApplicativoFruitore) ){
				policy.getFiltro().setServizioApplicativoFruitore(servizioApplicativoFruitore);
			}
			else{
				if(!first){
					policy.getFiltro().setServizioApplicativoFruitore(null);
				}
			}
			
			// per Chiave
			String perChiave = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED);
			if(first==false){
				policy.getFiltro().setInformazioneApplicativaEnabled(ServletUtils.isCheckBoxEnabled(perChiave));
			}
			
			if(policy.getFiltro().isInformazioneApplicativaEnabled()){
				
				// Per Chiave - Tipo
				String perChiaveTipo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO);
				if(perChiaveTipo!=null && !"".equals(perChiaveTipo) ){
					policy.getFiltro().setInformazioneApplicativaTipo(perChiaveTipo);
				}
				else{
					if(!first){
						policy.getFiltro().setInformazioneApplicativaTipo(ConfigurazioneCostanti.CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO_DEFAULT); // default
					}
				}
				
				// Per Chiave - Nome
				String perChiaveNome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME);
				if(perChiaveNome!=null && !"".equals(perChiaveNome) ){
					policy.getFiltro().setInformazioneApplicativaNome(perChiaveNome);
				}
				else{
					if(!first){
						policy.getFiltro().setInformazioneApplicativaNome(null);
					}
				}
				
				// Per Chiave - Valore
				String perChiaveValore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_VALORE);
				if(perChiaveValore!=null && !"".equals(perChiaveValore) ){
					policy.getFiltro().setInformazioneApplicativaValore(StringEscapeUtils.unescapeHtml(perChiaveValore)); // il valore viene "escaped" perchè può conteenere ""
				}
				else{
					if(!first){
						policy.getFiltro().setInformazioneApplicativaValore(null);
					}
				}
				
			}
			else{
				policy.getFiltro().setInformazioneApplicativaTipo(null);
				policy.getFiltro().setInformazioneApplicativaNome(null);
				policy.getFiltro().setInformazioneApplicativaValore(null);
			}
		}
		else{
			policy.getFiltro().setRuoloPorta(RuoloPolicy.ENTRAMBI);
			policy.getFiltro().setTipoFruitore(null);
			policy.getFiltro().setNomeFruitore(null);
			policy.getFiltro().setServizioApplicativoFruitore(null);
			policy.getFiltro().setTipoErogatore(null);
			policy.getFiltro().setNomeErogatore(null);
			policy.getFiltro().setServizioApplicativoErogatore(null);
			policy.getFiltro().setTipoServizio(null);
			policy.getFiltro().setNomeServizio(null);
			policy.getFiltro().setAzione(null);
			policy.getFiltro().setInformazioneApplicativaEnabled(false);
			policy.getFiltro().setInformazioneApplicativaTipo(null);
			policy.getFiltro().setInformazioneApplicativaNome(null);
			policy.getFiltro().setInformazioneApplicativaValore(null);
		}
		
		// GroupBy - stato
		String statoGroupBy = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_ENABLED);
		if(statoGroupBy!=null && !"".equals(statoGroupBy)){
			policy.getGroupBy().setEnabled(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_ABILITATO.equals(statoGroupBy) ||
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(statoGroupBy));
		}
		
		// GroupBy
		if(policy.getGroupBy().isEnabled()){
			
			// ruolo della PdD
			String ruoloPdD = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RUOLO_PDD);
			if(first==false){
				policy.getGroupBy().setRuoloPorta(ServletUtils.isCheckBoxEnabled(ruoloPdD));
			}
			
			// protocollo
			String protocollo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PROTOCOLLO);
			if(first==false){
				policy.getGroupBy().setProtocollo(ServletUtils.isCheckBoxEnabled(protocollo));
			}
			
			// erogatore
			String erogatore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_EROGATORE);
			if(first==false){
				policy.getGroupBy().setErogatore(ServletUtils.isCheckBoxEnabled(erogatore));
			}
			
			// servizio applicativo erogatore
			String servizioApplicativoErogatore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_EROGATORE);
			if(first==false){
				policy.getGroupBy().setServizioApplicativoErogatore(ServletUtils.isCheckBoxEnabled(servizioApplicativoErogatore));
			}
			
			// servizio
			String servizio = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SERVIZIO);
			if(first==false){
				policy.getGroupBy().setServizio(ServletUtils.isCheckBoxEnabled(servizio));
				policy.getGroupBy().setErogatore(ServletUtils.isCheckBoxEnabled(servizio)); // imposto anche l'erogatore poiche' identifica API differenti
			}
			
			// azione
			String azione = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_AZIONE);
			if(first==false){
				policy.getGroupBy().setAzione(ServletUtils.isCheckBoxEnabled(azione));
			}
			
			// fruitore
			String fruitore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_FRUITORE);
			if(first==false){
				policy.getGroupBy().setFruitore(ServletUtils.isCheckBoxEnabled(fruitore));
			}
			
			// servizio applicativo fruitore
			String servizioApplicativoFruitore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_FRUITORE);
			if(first==false){
				policy.getGroupBy().setServizioApplicativoFruitore(ServletUtils.isCheckBoxEnabled(servizioApplicativoFruitore));
			}
			
			// richiedente
			String richiedente = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RICHIEDENTE);
			if(first==false){
				policy.getGroupBy().setServizioApplicativoFruitore(ServletUtils.isCheckBoxEnabled(richiedente));
				policy.getGroupBy().setFruitore(ServletUtils.isCheckBoxEnabled(richiedente));
				policy.getGroupBy().setIdentificativoAutenticato(ServletUtils.isCheckBoxEnabled(richiedente));
			}
			
			// token
			String token = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN);
			if(first==false){
				if(ServletUtils.isCheckBoxEnabled(token)) {
					String [] tokenSelezionati = this.getParameterValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN_CLAIMS);
					if(tokenSelezionati!=null && tokenSelezionati.length>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < tokenSelezionati.length; i++) {
							TipoCredenzialeMittente tipo = TipoCredenzialeMittente.valueOf(tokenSelezionati[i]);
							if(TipoCredenzialeMittente.token_issuer.equals(tipo)) {
								continue;
							}
							else if(TipoCredenzialeMittente.token_subject.equals(tipo)) {
								if(!bf.toString().endsWith(",") && bf.length()>0) {
									bf.append(",");
								}
								bf.append(TipoCredenzialeMittente.token_issuer.name());
								if(i==0) {
									bf.append(",");
								}
							}
							if(i>0) {
								bf.append(",");
							}
							bf.append(tokenSelezionati[i]);
						}
						if(bf.length()>0) {
							policy.getGroupBy().setToken(bf.toString());
						}
						else {
							policy.getGroupBy().setToken(null);
						}
					}
					else {
						policy.getGroupBy().setToken(null);
					}
				}
				else {
					policy.getGroupBy().setToken(null);
				}
			}

			// per Chiave
			String perChiave = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED);
			if(first==false){
				policy.getGroupBy().setInformazioneApplicativaEnabled(ServletUtils.isCheckBoxEnabled(perChiave));
			}
			
			if(policy.getGroupBy().isInformazioneApplicativaEnabled()){
				
				// Per Chiave - Tipo
				String perChiaveTipo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_TIPO);
				if(perChiaveTipo!=null && !"".equals(perChiaveTipo) ){
					policy.getGroupBy().setInformazioneApplicativaTipo(perChiaveTipo);
				}
				else{
					if(!first){
						policy.getGroupBy().setInformazioneApplicativaTipo(ConfigurazioneCostanti.CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_TIPO_DEFAULT); // default
					}
				}
				
				// Per Chiave - Nome
				String perChiaveNome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME);
				if(perChiaveNome!=null && !"".equals(perChiaveNome) ){
					policy.getGroupBy().setInformazioneApplicativaNome(perChiaveNome);
				}
				else{
					if(!first){
						policy.getGroupBy().setInformazioneApplicativaNome(null);
					}
				}
				
				
			}
			else{
				policy.getGroupBy().setInformazioneApplicativaTipo(null);
				policy.getGroupBy().setInformazioneApplicativaNome(null);
			}
		}
		else{
			policy.getGroupBy().setRuoloPorta(false);
			policy.getGroupBy().setFruitore(false);
			policy.getGroupBy().setServizioApplicativoFruitore(false);
			policy.getGroupBy().setIdentificativoAutenticato(false);
			policy.getGroupBy().setToken(null);
			policy.getGroupBy().setErogatore(false);
			policy.getGroupBy().setServizioApplicativoErogatore(false);
			policy.getGroupBy().setServizio(false);
			policy.getGroupBy().setAzione(false);
			policy.getGroupBy().setInformazioneApplicativaEnabled(false);
			policy.getGroupBy().setInformazioneApplicativaTipo(null);
			policy.getGroupBy().setInformazioneApplicativaNome(null);
		}
		
		
		if(sbParsingError.length() > 0){
			return sbParsingError.toString();
		}
		return null;
	}

	private void addDataElementRisorsa( 
			Vector<DataElement> dati, 
			String parametroRisorsaNome, String valoreRisorsa,
			String parametroEsitiNome, String valoreEsiti,
			boolean editMode) {
		
		if(CostantiControlStation.USE_SELECT_LIST_SEPARATE_METRICHE) {
			
			DataElement de = new DataElement();
			de.setName(parametroRisorsaNome);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA);
			if(editMode) {
				de.setValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA);
				de.setSelected(valoreRisorsa);
				de.setType(DataElementType.SELECT);
				de.setPostBack_viaPOST(true);
			}
			else {
				de.setType(DataElementType.TEXT);
			}
			de.setValue(valoreRisorsa);
			dati.addElement(de);
			
			if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_NUMERO_RICHIESTE.equals(valoreRisorsa)) {
				
				de = new DataElement();
				de.setName(parametroEsitiNome);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ESITI);
				if(editMode) {
					de.setValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_ESITI);
					de.setSelected(valoreEsiti);
					de.setType(DataElementType.SELECT);
					de.setPostBack_viaPOST(true);
				}
				else {
					if(valoreEsiti==null || "".equals(valoreEsiti)) {
						valoreEsiti = CostantiControlStation.LABEL_QUALSIASI;
					}
					de.setType(DataElementType.TEXT);
				}
				de.setValue(valoreEsiti);
				dati.addElement(de);
				
			}
		}
		else {
			DataElement de = new DataElement();
			de.setName(parametroRisorsaNome);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA_TIPO);
			if(editMode) {
				de.setValues(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_RISORSE_VALORI);
				de.setLabels(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_RISORSE_LABELS);
				de.setSelected(valoreRisorsa);
				de.setType(DataElementType.SELECT);
				de.setPostBack_viaPOST(true);
				de.setValue(valoreRisorsa);
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(valoreRisorsa);
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(parametroRisorsaNome+"__LABEL");
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA_TIPO);
				de.setType(DataElementType.TEXT);
				String labelRisorsaPolicyAttiva = this.getLabelTipoRisorsaPolicyAttiva(valoreRisorsa);
				de.setValue(labelRisorsaPolicyAttiva);
			}
			dati.addElement(de);
		}
		
	}
	
	private TipoRisorsa getTipoRisorsa(String valoreRisorsa, String valoreEsiti) throws Exception {
		
		if(CostantiControlStation.USE_SELECT_LIST_SEPARATE_METRICHE) {
		
			TipoRisorsa tipoRisorsaSelezionata = null;
			if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_NUMERO_RICHIESTE.equals(valoreRisorsa)) {
				if(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK.equals(valoreEsiti)) {
					tipoRisorsaSelezionata = TipoRisorsa.NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO;
				}
				else if(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE.equals(valoreEsiti)) {
					tipoRisorsaSelezionata = TipoRisorsa.NUMERO_RICHIESTE_FALLITE;
				}
				else if(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT.equals(valoreEsiti)) {
					tipoRisorsaSelezionata = TipoRisorsa.NUMERO_FAULT_APPLICATIVI;
				}
				else if(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE_FAULT.equals(valoreEsiti)) {
					tipoRisorsaSelezionata = TipoRisorsa.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI;
				}
				else {
					tipoRisorsaSelezionata = TipoRisorsa.NUMERO_RICHIESTE;
				}
			}
			else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_NUMERO_RICHIESTE_SIMULTANEE.equals(valoreRisorsa)) {
				tipoRisorsaSelezionata = TipoRisorsa.NUMERO_RICHIESTE;
			}
			else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_OCCUPAZIONE_BANDA.equals(valoreRisorsa)) {
				tipoRisorsaSelezionata = TipoRisorsa.OCCUPAZIONE_BANDA;
			}
			else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_TEMPO_MEDIO_RISPOSTA.equals(valoreRisorsa)) {
				tipoRisorsaSelezionata = TipoRisorsa.TEMPO_MEDIO_RISPOSTA;
			}
			else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_TEMPO_COMPLESSIVO_RISPOSTA.equals(valoreRisorsa)) {
				tipoRisorsaSelezionata = TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA;
			}
			else {
				throw new Exception("Risorsa '"+valoreRisorsa+"' sconosciuta");
			}
			return tipoRisorsaSelezionata;
			
		}
		else {
			
			TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.toEnumConstant(valoreRisorsa, false);
			if(tipoRisorsaPolicyAttiva==null) {
				throw new Exception("Risorsa '"+valoreRisorsa+"' sconosciuta");
			}
			return tipoRisorsaPolicyAttiva.getTipoRisorsa(true);
			
		}
		
	}
	public boolean isTipoRisorsaNumeroRichiesteSimultanee(String valoreRisorsa) throws Exception {
		if(CostantiControlStation.USE_SELECT_LIST_SEPARATE_METRICHE) {
			return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_NUMERO_RICHIESTE_SIMULTANEE.equals(valoreRisorsa);
		}
		else {
			TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.toEnumConstant(valoreRisorsa, false);
			if(tipoRisorsaPolicyAttiva==null) {
				return false;
			}
			return tipoRisorsaPolicyAttiva.isRichiesteSimultanee();
		}
	}
	
	public String getDataElementValueRisorsa(String tipoRisorsa, boolean simultanee) throws Exception {
		TipoRisorsa tipo = TipoRisorsa.toEnumConstant(tipoRisorsa);
		if(tipo==null) {
			return null;
		}
		return this.getDataElementValueRisorsa(tipo, simultanee);
	}
	public String getDataElementValueRisorsa(TipoRisorsa tipoRisorsa, boolean simultanee) throws Exception {
		if(CostantiControlStation.USE_SELECT_LIST_SEPARATE_METRICHE) {
			switch (tipoRisorsa) {
			case NUMERO_RICHIESTE:
				if(simultanee) {
					return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_NUMERO_RICHIESTE_SIMULTANEE;
				}
				else {
					return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_NUMERO_RICHIESTE;
				}
			case NUMERO_RICHIESTE_FALLITE:
			case NUMERO_FAULT_APPLICATIVI:
			case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
			case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
				return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_NUMERO_RICHIESTE;
			case OCCUPAZIONE_BANDA:
				return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_OCCUPAZIONE_BANDA;
			case TEMPO_MEDIO_RISPOSTA:
				return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_TEMPO_MEDIO_RISPOSTA;
			case TEMPO_COMPLESSIVO_RISPOSTA:
				return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_TEMPO_COMPLESSIVO_RISPOSTA;
			}
			
			throw new Exception("Tipo risorsa '"+tipoRisorsa+"' non gestita");
		}
		else {
			TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.getTipo(tipoRisorsa, simultanee);
			return tipoRisorsaPolicyAttiva.getValue();
		}
	}
	
	public String getDataElementValueRisorsaEsiti(String tipoRisorsa) throws Exception {
		TipoRisorsa tipo = TipoRisorsa.toEnumConstant(tipoRisorsa);
		if(tipo==null) {
			return null;
		}
		return this.getDataElementValueRisorsaEsiti(tipo);
	}
	public String getDataElementValueRisorsaEsiti(TipoRisorsa tipoRisorsa) throws Exception {
		if(CostantiControlStation.USE_SELECT_LIST_SEPARATE_METRICHE) {
			switch (tipoRisorsa) {
			case NUMERO_RICHIESTE_FALLITE:
				return ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE;
			case NUMERO_FAULT_APPLICATIVI:
				return ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT;
			case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
				return ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE_FAULT;
			case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
				return ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK;
			case NUMERO_RICHIESTE:
			case OCCUPAZIONE_BANDA:
			case TEMPO_MEDIO_RISPOSTA:
			case TEMPO_COMPLESSIVO_RISPOSTA:
				return null;
			}
			
			throw new Exception("Tipo risorsa '"+tipoRisorsa+"' non gestita");
		}
		else {
			return null;
		}
	}
	
	public void findPolicyBuiltIn(List<InfoPolicy> policies, 
			List<InfoPolicy> idPoliciesSoddisfanoCriteri,
			String modalitaRisorsa, String modalitaEsiti,
			boolean modalitaSimultaneeEnabled, String modalitaIntervallo, 
			boolean modalitaCongestioneEnabled,
			boolean modalitaDegradoEnabled,
			boolean modalitaErrorRateEnabled) throws Exception {
		if(policies!=null && policies.size()>0 && modalitaRisorsa!=null){
		
			TipoRisorsa tipoRisorsaSelezionata = getTipoRisorsa(modalitaRisorsa, modalitaEsiti);
			
			for (InfoPolicy info : policies) {
				
				if(!tipoRisorsaSelezionata.equals(info.getTipoRisorsa())) {
					continue;
				}
				
				if(modalitaSimultaneeEnabled) {
					if(!info.isCheckRichiesteSimultanee()) {
						continue;
					}
				}
				
				if(!modalitaSimultaneeEnabled) {
					if(!info.isIntervalloUtilizzaRisorseRealtime()) {
						continue;
					}
					if(modalitaIntervallo==null) {
						throw new Exception("Intervallo Temporale non definito");
					}
					TipoPeriodoRealtime tipo = TipoPeriodoRealtime.toEnumConstant(modalitaIntervallo, true);
					if(!tipo.equals(info.getIntervalloUtilizzaRisorseRealtimeTipoPeriodo())) {
						continue;
					}
				}
				
				if(modalitaCongestioneEnabled) {
					if(!info.isControlloCongestione()) {
						continue;
					}
				}
				else {
					if(info.isControlloCongestione()) {
						continue;
					}
				}
				
				if(modalitaDegradoEnabled) {
					if(!info.isDegradoPrestazionaleUtilizzaRisorseStatistiche()) {
						continue;
					}
				}
				else {
					if(info.isDegradoPrestazionaleUtilizzaRisorseStatistiche()) {
						continue;
					}
				}
				
				if(modalitaErrorRateEnabled) {
					if(!info.isErrorRate()) {
						continue;
					}
				}
				else {
					if(info.isErrorRate()) {
						continue;
					}
				}
				
				idPoliciesSoddisfanoCriteri.add(info);
			}
		}
	}
	
	public void addAttivazionePolicyToDati(Vector<DataElement> dati, TipoOperazione tipoOperazione, AttivazionePolicy policy, String nomeSezione, List<InfoPolicy> policies, 
			RuoloPolicy ruoloPorta, String nomePorta, ServiceBinding serviceBinding, String modalita) throws Exception {
				
		
		
		Parameter parRuoloPorta = null;
		if(ruoloPorta!=null) {
			parRuoloPorta = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_RUOLO_PORTA, ruoloPorta.getValue());
		}
		Parameter parNomePorta = null;
		if(nomePorta!=null) {
			parNomePorta = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_NOME_PORTA, nomePorta);
		}
		Parameter parServiceBinding = null;
		if(serviceBinding!=null) {
			parServiceBinding = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_SERVICE_BINDING, serviceBinding.name());
		}
		
		DataElement de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_IS_ATTIVAZIONE_GLOBALE);
		de.setValue(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_IS_ATTIVAZIONE_GLOBALE_VALORE);
		de.setType(DataElementType.HIDDEN);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_RUOLO_PORTA);
		de.setValue(ruoloPorta!=null ? ruoloPorta.getValue() : null);
		de.setType(DataElementType.HIDDEN);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_NOME_PORTA);
		de.setValue(nomePorta);
		de.setType(DataElementType.HIDDEN);
		dati.addElement(de);
		
		if(serviceBinding!=null) {
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_SERVICE_BINDING);
			de.setValue(serviceBinding.name());
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
						
		/* Servono per le bradcump */
		
		String jmxParam = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE);
		boolean jmx = jmxParam!=null && "true".equals(jmxParam);
		
		if(!jmx){
			de = new DataElement();
			de.setLabel(nomeSezione);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		if(policy.getId()!=null && policy.getId()>0){
			// id
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID);
			de.setValue(policy.getId()+"");
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
			
		InfoPolicy infoPolicy = null;
		List<String> idPolicies = new ArrayList<String>();
		String modalitaRisorsa = null;
		String modalitaEsiti = null;
		String modalitaIntervallo = null;
		String modalitaCongestione = null;
		String modalitaDegrado = null;
		String modalitaErrorRate = null;
		boolean modalitaSimultaneeEnabled = false;
		boolean modalitaCongestioneEnabled = false;
		boolean modalitaDegradoEnabled = false;
		boolean modalitaErrorRateEnabled = false;
		
		boolean addInfoDescrizionePolicy = !jmx;
		
		if(TipoOperazione.ADD.equals(tipoOperazione)){
		
			if(modalita==null) {
				modalita = ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_DEFAULT;
			}
			
		}
		
		
		if(TipoOperazione.ADD.equals(tipoOperazione) && modalita!=null && 
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_BUILT_IN.equals(modalita)){
		
			modalitaRisorsa = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_RISORSA);
			modalitaEsiti = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_ESITI);
			modalitaIntervallo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_INTERVALLO);
			modalitaCongestione = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_CONGESTIONE);
			modalitaDegrado = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_DEGRADO);
			modalitaErrorRate = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_ERROR_RATE);
		
			if(modalitaRisorsa==null) {
				if(CostantiControlStation.USE_SELECT_LIST_SEPARATE_METRICHE) {
					modalitaRisorsa = ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_DEFAULT;
				}
				else {
					modalitaRisorsa = CostantiControlStation.DEFAULT_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA_TIPO_VALUE.getValue();
				}
			}
			if(modalitaEsiti==null) {
				if(CostantiControlStation.USE_SELECT_LIST_SEPARATE_METRICHE) {
					modalitaEsiti = ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_ESITI_DEFAULT;
				}
			}
			if(modalitaIntervallo==null) {
				modalitaIntervallo = ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_RISORSA_INTERVALLO_OSSERVAZIONE_DEFAULT;
			}
				
			modalitaSimultaneeEnabled = isTipoRisorsaNumeroRichiesteSimultanee(modalitaRisorsa);

			modalitaCongestioneEnabled = ServletUtils.isCheckBoxEnabled(modalitaCongestione);
			
			modalitaDegradoEnabled = ServletUtils.isCheckBoxEnabled(modalitaDegrado);
			
			modalitaErrorRateEnabled = ServletUtils.isCheckBoxEnabled(modalitaErrorRate);
			
			List<String> idPoliciesTmp = new ArrayList<String>();
			List<InfoPolicy> idPoliciesSoddisfanoCriteri = new ArrayList<>();
			findPolicyBuiltIn(policies, 
					idPoliciesSoddisfanoCriteri,
					modalitaRisorsa, modalitaEsiti,
					modalitaSimultaneeEnabled, modalitaIntervallo, 
					modalitaCongestioneEnabled,
					modalitaDegradoEnabled,
					modalitaErrorRateEnabled);
			if(!idPoliciesSoddisfanoCriteri.isEmpty()) {
				for (InfoPolicy infoPolicyCheck : idPoliciesSoddisfanoCriteri) {
					idPoliciesTmp.add(infoPolicyCheck.getIdPolicy());
					if(policy.getIdPolicy()!=null && policy.getIdPolicy().equals(infoPolicyCheck.getIdPolicy())){
						infoPolicy = infoPolicyCheck;
					}	
				}
			}
			if(idPoliciesTmp.size()<=0) {
				
				if(policies==null || policies.size()<=0 ) {
					this.pd.setMessage(ConfigurazioneCostanti.MESSAGGIO_POLICY_BUILT_IN_NON_ESISTENTI, MessageType.ERROR);
				}
				else {
					this.pd.setMessage(ConfigurazioneCostanti.MESSAGGIO_POLICY_BUILT_IN_CRITERI_NON_ESISTENTI, MessageType.ERROR);
				}
				
			}
			else {
				
				if(idPoliciesTmp.size()>1) {
					idPoliciesTmp.add("-");
					idPolicies.addAll(idPoliciesTmp);
				}
				else {
					String idPolicy = idPoliciesTmp.get(0);
					idPolicies.add(idPolicy);
					for (InfoPolicy info : policies) {
						if(idPolicy!=null && idPolicy.equals(info.getIdPolicy())){
							infoPolicy = info;
						}
					}
				}
				
			}
			
		}
		else {
			
			if(policies==null || policies.size()<=0) {
				
				this.pd.setMessage(ConfigurazioneCostanti.MESSAGGIO_POLICY_UTENTE_NON_ESISTENTI, MessageType.ERROR);
				
			}
			else {
				idPolicies.add("-");
				for (InfoPolicy info : policies) {
					idPolicies.add(info.getIdPolicy());
					if(policy.getIdPolicy()!=null && policy.getIdPolicy().equals(info.getIdPolicy())){
						infoPolicy = info;
					}
				}
			}
		}
						
		if(!jmx){
		
			// alias
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS);
			//if(infoPolicy!=null){
			de.setType(DataElementType.TEXT_EDIT);
//			}
//			else {
//				de.setType(DataElementType.HIDDEN);
//			}
			de.setValue(policy.getAlias());
			de.setRequired(true);
			dati.addElement(de);
			
			// descrizione
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_DESCRIZIONE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_DESCRIZIONE);
			if(infoPolicy!=null){
				if(!addInfoDescrizionePolicy) {
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					de.setRows(6);
					//de.setCols(57);
					de.setCols(55);
					de.setLabelAffiancata(true);
				}
				else {
					de.setType(DataElementType.HIDDEN);					
				}
				de.setValue(infoPolicy.getDescrizione());
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(null);
			}
			dati.addElement(de);
			
			//if(infoPolicy!=null){
			// stato
			//boolean hidden = (ruoloPorta!=null);
			boolean hidden = false; // anche una policy di rate limiting sulla singola porta puo' essere disabiltiata temporaneamente
			addToDatiDataElementStato_postBackViaPOST(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ENABLED, 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ENABLED, policy.isEnabled(), false,
					true, policy.isWarningOnly(), hidden);
			
			if(TipoOperazione.CHANGE.equals(tipoOperazione) && addInfoDescrizionePolicy && infoPolicy!=null) {
				DataElementInfo dInfoDescrizionePolicy = new DataElementInfo(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID);
				dInfoDescrizionePolicy.setHeaderBody(replaceToHtmlSeparator(infoPolicy.getDescrizione()));
				dInfoDescrizionePolicy.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_POLICY_STATO_VALORI);
				dati.get(dati.size()-1).setInfo(dInfoDescrizionePolicy);
			}
			else {
				DataElementInfo dInfoDescrizionePolicy = new DataElementInfo(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ENABLED);
				dInfoDescrizionePolicy.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_POLICY_STATO_VALORI);
				dati.get(dati.size()-1).setInfo(dInfoDescrizionePolicy);
			}

			
			// continue
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_CONTINUE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_CONTINUE);
			if(hidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(ConfigurazioneCostanti.PARAMETRI_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_CONTINUE);
				de.setLabels(ConfigurazioneCostanti.LABELS_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_CONTINUE);
				de.setPostBack_viaPOST(true);
				de.setSelected(policy.isContinuaValutazione()+"");
			}
			de.setValue(policy.isContinuaValutazione()+"");
			
			DataElementInfo dInfoContinuePolicy = new DataElementInfo(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_CONTINUE);
			dInfoContinuePolicy.setListBody(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_CONTINUE_ELEM);
			de.setInfo(dInfoContinuePolicy);
			
			dati.addElement(de);
		}
		
		if(!jmx){
			if(TipoOperazione.CHANGE.equals(tipoOperazione) && infoPolicy!=null) {
				
				if(infoPolicy.isBuiltIn()) {
					
					de = new DataElement();
					de.setLabel(ConfigurazioneCostanti.LABEL_POLICY_CRITERI);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
					
					String modalitaRisorsaConvertValue = this.getDataElementValueRisorsa(infoPolicy.getTipoRisorsa(), infoPolicy.isCheckRichiesteSimultanee());
					String modalitaEsitiConvertValue = this.getDataElementValueRisorsaEsiti(infoPolicy.getTipoRisorsa());
					addDataElementRisorsa(dati, 
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_RISORSA+"__label", modalitaRisorsaConvertValue, 
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_ESITI+"__label", modalitaEsitiConvertValue,
							false);
					
					if(!infoPolicy.isCheckRichiesteSimultanee()) {
						de = new DataElement();
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_INTERVALLO+"__label");
						de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_INTERVALLO_OSSERVAZIONE);
						de.setType(DataElementType.TEXT);
						if(infoPolicy.getIntervalloUtilizzaRisorseRealtimeTipoPeriodo()!=null) {
							String labelValue = infoPolicy.getIntervalloUtilizzaRisorseRealtimeTipoPeriodo().getValue();
							for (int i = 0; i < ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_INTERVALLO_OSSERVAZIONE.length; i++) {
								if(labelValue.equals(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_INTERVALLO_OSSERVAZIONE[i])) {
									labelValue = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_INTERVALLO_OSSERVAZIONE[i];
									break;
								}
							}
							de.setValue(labelValue);
						}
						dati.addElement(de);
					}
					
					if(infoPolicy.isControlloCongestione()) {
						de = new DataElement();
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_CONGESTIONE+"__label");
						de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE);
						de.setType(DataElementType.TEXT);
						dati.addElement(de);
					}
					
					if(infoPolicy.isDegradoPrestazione()) {
						de = new DataElement();
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_DEGRADO+"__label");
						de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE);
						de.setType(DataElementType.TEXT);
						dati.addElement(de);
					}
	
				}
				else {
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID+"__label");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID);
					de.setType(DataElementType.TEXT);
					de.setValue(infoPolicy.getIdPolicy());
					dati.addElement(de);
					
				}
				
			}
		}
		

		if(!jmx){
			
			if(TipoOperazione.CHANGE.equals(tipoOperazione)){
				if(policy!=null && policy.isEnabled()){
					de = new DataElement();
					de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);	
				}
			}
			
		}
		
		if(TipoOperazione.CHANGE.equals(tipoOperazione) && 
				policy.getIdActivePolicy()!=null && !"".equals(policy.getIdActivePolicy())){
			// id-active-policy
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_ID_UNICO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_ID_UNICO);
			if(!jmx){
				//de.setType(DataElementType.TEXT);
				de.setType(DataElementType.HIDDEN);
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setValue(policy.getIdActivePolicy());
			dati.addElement(de);
		}
		
		if(!jmx){
			
			if(TipoOperazione.CHANGE.equals(tipoOperazione)){
				if(policy!=null && policy.isEnabled()){
					// Link visualizza stato 
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO);
					de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO);
					if(ruoloPorta!=null) {
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""),
								parRuoloPorta,parNomePorta,parServiceBinding);			
					}
					else {
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""));
					}
					de.setType(DataElementType.LINK);
					dati.addElement(de);
				}
			}
			
			
			if(TipoOperazione.ADD.equals(tipoOperazione)) {
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA);
				de.setValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_VALORI);
				de.setSelected(modalita);
				de.setValue(modalita);
				de.setType(DataElementType.SELECT);
				de.setPostBack_viaPOST(true);
				dati.addElement(de);
				
			}
			
		}
		
		// policy
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID);
		boolean configurazionePerCriteri = false;
		if(TipoOperazione.ADD.equals(tipoOperazione)){
			if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_BUILT_IN.equals(modalita) &&
					(idPolicies==null || idPolicies.size()<=1)) {
				de.setType(DataElementType.HIDDEN);
				configurazionePerCriteri = true;
			}
			else {
				de.setValues(idPolicies);
				if(policy.getIdPolicy()!=null)
					de.setSelected(policy.getIdPolicy());
				else
					de.setSelected("-");
				de.setType(DataElementType.SELECT);
				de.setRequired(true);
				de.setPostBack_viaPOST(true);
				
				if(addInfoDescrizionePolicy && infoPolicy!=null) {
					DataElementInfo dInfoDescrizionePolicy = new DataElementInfo(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID);
					dInfoDescrizionePolicy.setHeaderBody(replaceToHtmlSeparator(infoPolicy.getDescrizione()));
					de.setInfo(dInfoDescrizionePolicy);
				}
			}
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		if(configurazionePerCriteri && idPolicies!=null && idPolicies.size()==1) {
			de.setValue(idPolicies.get(0));
		}
		else {
			if(policy.getIdPolicy()!=null) {
				de.setValue(policy.getIdPolicy());
			}
			else {
				de.setValue("-");
			}
		}
		dati.addElement(de);
		
		if(!jmx){
			
			if(TipoOperazione.ADD.equals(tipoOperazione) && modalita!=null && 
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_BUILT_IN.equals(modalita)){
			
				org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = this.confCore.getConfigurazioneControlloTraffico();
				
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_POLICY_CRITERI);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
				
				addDataElementRisorsa(dati, 
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_RISORSA, modalitaRisorsa, 
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_ESITI, modalitaEsiti,
						true);
								
				if(!modalitaSimultaneeEnabled) {
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_INTERVALLO);
					de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_INTERVALLO_OSSERVAZIONE);
					de.setType(DataElementType.SELECT);
					de.setValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_INTERVALLO_OSSERVAZIONE);
					de.setLabels(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_INTERVALLO_OSSERVAZIONE);
					de.setSelected(modalitaIntervallo);
					de.setValue(modalitaIntervallo);
					de.setPostBack_viaPOST(true);
					dati.addElement(de);
				}
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_CONGESTIONE);
				//de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_LABEL);
				de.setLabelRight(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(modalitaCongestioneEnabled);
				de.setValue(modalitaCongestioneEnabled+"");
				de.setPostBack_viaPOST(true);
				DataElementInfo dInfoDescrizioneCongestione = new DataElementInfo(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_LABEL);
				dInfoDescrizioneCongestione.setHeaderBody(replaceToHtmlSeparator(this.getApplicabilitaConCongestione(configurazioneControlloTraffico.getControlloTraffico())));
				de.setInfo(dInfoDescrizioneCongestione);
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_DEGRADO);
				//de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_LABEL);
				de.setLabelRight(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE);
				if(!TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(modalitaRisorsa) && 
					!TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(modalitaRisorsa) 
					){
					de.setType(DataElementType.CHECKBOX);
				}
				else{
					de.setType(DataElementType.HIDDEN);
				}
				de.setSelected(modalitaDegradoEnabled);
				de.setValue(modalitaDegradoEnabled+"");
				de.setPostBack_viaPOST(true);
				DataElementInfo dInfoDescrizioneDegrado = new DataElementInfo(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_LABEL);
				dInfoDescrizioneDegrado.setHeaderBody(replaceToHtmlSeparator(this.getApplicabilitaDegradoPrestazionale()));
				de.setInfo(dInfoDescrizioneDegrado);
				dati.addElement(de);
				
//				de = new DataElement();
//				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CRITERIO_ERROR_RATE);
//				//de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_ERROR_RATE_LABEL);
//				de.setLabelRight(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_ERROR_RATE_NOTE);
//				de.setType(DataElementType.CHECKBOX);
//				de.setSelected(modalitaErrorRateEnabled);
//				de.setValue(modalitaErrorRateEnabled+"");
//				de.setPostBack_viaPOST(true);
//				dati.addElement(de);
				
			}
			
			
			if(idPolicies!=null && idPolicies.size()>0 && infoPolicy!=null) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_POLICY_INFORMAZIONI_SOGLIA);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}
			
			// ridefinisci
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_RIDEFINISCI);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_RIDEFINISCI);
			if(infoPolicy!=null){
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(policy.isRidefinisci());
				de.setValue(policy.isRidefinisci()+"");
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue("false");
			}
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
			// Valore Soglia
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VALORE);
			if(infoPolicy!=null){
				switch (infoPolicy.getTipoRisorsa()) {
				case NUMERO_RICHIESTE:
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE);
					break;
				case OCCUPAZIONE_BANDA:
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_OCCUPAZIONE_DI_BANDA_LABEL);
					de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_OCCUPAZIONE_DI_BANDA_NOTE);
					break;
				case TEMPO_MEDIO_RISPOSTA:
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_LABEL);
					de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_NOTE);
					break;
				case TEMPO_COMPLESSIVO_RISPOSTA:
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_LABEL);
					de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_NOTE);
					break;
				case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE);
					de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO_NOTE);
					break;
				case NUMERO_RICHIESTE_FALLITE:
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE);
					de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE_FALLITE_NOTE);
					break;
				case NUMERO_FAULT_APPLICATIVI:
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE);
					de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_FAULT_APPLICATIVI_NOTE);
					break;
				case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE);
					de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE_FALLITE_O_FAULT_NOTE);
					break;
				}
			}
			if(infoPolicy!=null){
				if(policy.isRidefinisci()){
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
					if(policy.getValore()!=null){
						de.setValue(policy.getValore()+"");
					}
					else{
						de.setValue("");
					}
				}
				else{
					de.setType(DataElementType.TEXT);
					if(infoPolicy.getValore()!=null){
						de.setValue(infoPolicy.getValore()+"");
					}
					else{
						de.setValue("");
					}
				}
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue("");
			}
			//de.setSize(consoleHelper.getSize());
			dati.addElement(de);
			
			if(infoPolicy!=null){
								
				boolean delegata = false;
				boolean applicativa = false;
				@SuppressWarnings("unused")
				boolean configurazione = false;
				if(ruoloPorta!=null) {
					if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
						delegata = (nomePorta!=null);
					}
					else if(RuoloPolicy.APPLICATIVA.equals(ruoloPorta)) {
						applicativa = (nomePorta!=null);
					}
				}
				configurazione = !delegata && !applicativa;
				
				boolean multitenant = this.confCore.isMultitenant();
				
				boolean tokenAbilitato = true;
				
				PddTipologia pddTipologiaSoggettoAutenticati = null;
				boolean gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore = false;
				PortaDelegata portaDelegata = null;
				PortaApplicativa portaApplicativa = null;
				CredenzialeTipo tipoAutenticazione = null;
				Boolean appId = null;
				IDSoggetto idSoggettoProprietario = null;
				if(ruoloPorta!=null) {
					if(applicativa) {
						
						if(multitenant && this.confCore.getMultitenantSoggettiErogazioni()!=null) {
							switch (this.confCore.getMultitenantSoggettiErogazioni()) {
							case SOLO_SOGGETTI_ESTERNI:
								pddTipologiaSoggettoAutenticati = PddTipologia.ESTERNO;
								break;
							case ESCLUDI_SOGGETTO_EROGATORE:
								gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore = true;
								break;
							case TUTTI:
								break;
							}
						}
						
						IDPortaApplicativa idPA = new IDPortaApplicativa();
						idPA.setNome(nomePorta);
						portaApplicativa = this.porteApplicativeCore.getPortaApplicativa(idPA);
						tipoAutenticazione = CredenzialeTipo.toEnumConstant(portaApplicativa.getAutenticazione());
						if(CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
							ApiKeyState apiKeyState =  new ApiKeyState(this.porteApplicativeCore.getParametroAutenticazione(portaApplicativa.getAutenticazione(), portaApplicativa.getProprietaAutenticazioneList()));
							appId = apiKeyState.appIdSelected;
						}
						idSoggettoProprietario = new IDSoggetto(portaApplicativa.getTipoSoggettoProprietario(), portaApplicativa.getNomeSoggettoProprietario());
						
						if(portaApplicativa.getGestioneToken()!=null) {
							String gestioneTokenPolicy = portaApplicativa.getGestioneToken().getPolicy();
							if(	gestioneTokenPolicy == null ||
									gestioneTokenPolicy.equals("") ||
									gestioneTokenPolicy.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
								tokenAbilitato = false;
							}						
						}
						else {
							tokenAbilitato = false;
						}
	
					}
					if(delegata) {
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(nomePorta);
						portaDelegata = this.porteDelegateCore.getPortaDelegata(idPD);
						tipoAutenticazione = CredenzialeTipo.toEnumConstant(portaDelegata.getAutenticazione());
						if(CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
							ApiKeyState apiKeyState =  new ApiKeyState(this.porteDelegateCore.getParametroAutenticazione(portaDelegata.getAutenticazione(), portaDelegata.getProprietaAutenticazioneList()));
							appId = apiKeyState.appIdSelected;
						}
						idSoggettoProprietario = new IDSoggetto(portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario());
						
						if(portaDelegata.getGestioneToken()!=null) {
							String gestioneTokenPolicy = portaDelegata.getGestioneToken().getPolicy();
							if(	gestioneTokenPolicy == null ||
									gestioneTokenPolicy.equals("") ||
									gestioneTokenPolicy.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
								tokenAbilitato = false;
							}						
						}
						else {
							tokenAbilitato = false;
						}
					}
				}
				
				// GroupBy 
				addToDatiAttivazioneGroupBy(dati, tipoOperazione, policy, nomeSezione, infoPolicy, 
						ruoloPorta, nomePorta, serviceBinding,
						tokenAbilitato
						);
				
				// Filtro 
				addToDatiAttivazioneFiltro(dati, tipoOperazione, policy, nomeSezione, infoPolicy, ruoloPorta, nomePorta, serviceBinding,
						idSoggettoProprietario, tokenAbilitato, 
						tipoAutenticazione, appId, 
						pddTipologiaSoggettoAutenticati, gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore
						);
				
			}
			
		}
		
		else{
			
			// jmx
			
			List<String> aliases = this.core.getJmxPdD_aliases();
			if(aliases==null || aliases.size()<=0){
				throw new Exception("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}
			
			String jmxResetParam = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET);
			boolean jmxReset = jmxResetParam!=null && !"".equals(jmxResetParam);
			String aliasJmxReset = null;
			if(jmxReset){
				if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET_ALL_VALUE.equals(jmxResetParam)==false){
					aliasJmxReset = jmxResetParam;
				}
			}

			boolean showResetCounters =  (infoPolicy.isCheckRichiesteSimultanee()==false) && 
					(infoPolicy.isIntervalloUtilizzaRisorseRealtime() || infoPolicy.isDegradoPrestazionaleUtilizzaRisorseRealtime());

			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_INFORMAZIONI_RUNTIME);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			// Link refresh
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO_REFRESH);
			de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO_REFRESH);
			if(ruoloPorta!=null) {
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""),
						parRuoloPorta, parNomePorta,parServiceBinding);
			}
			else {
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""));
			}
			de.setType(DataElementType.LINK);
			dati.addElement(de);

			if(showResetCounters && aliases.size()>1){
				// Link resetCounters
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_RESET);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO_RESET_ALL_NODES);
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO_RESET_ALL_NODES);
				if(ruoloPorta!=null) {
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""),
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET, 
									ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET_ALL_VALUE),
							parRuoloPorta, parNomePorta,parServiceBinding);
				}
				else {
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""),
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET, 
									ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET_ALL_VALUE));
				}
				de.setType(DataElementType.LINK);
				dati.addElement(de);
			}
			
			int i=0;
			for (String alias : aliases) {
				
				String descrizioneAlias = this.core.getJmxPdD_descrizione(alias);
				
				de = new DataElement();
				de.setLabel(descrizioneAlias);
				de.setValue(descrizioneAlias);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
				
				if(jmxReset && (aliasJmxReset==null || aliasJmxReset.equals(alias))){
					
					String resultReset = null;
					String uniqueIdMap = null;
					try{
						uniqueIdMap = UniqueIdentifierUtilities.getUniqueId(policy);
						resultReset = this.core.invokeJMXMethod(this.core.getGestoreRisorseJMX(alias),alias,JMXConstants.JMX_TYPE, 
								JMXConstants.JMX_NAME,
								JMXConstants.CC_METHOD_NAME_RESET_POLICY_COUNTERS,
								uniqueIdMap);
					}catch(Exception e){
						String errorMessage = "Errore durante l'invocazione dell'operazione ["+JMXConstants.CC_METHOD_NAME_RESET_POLICY_COUNTERS+"] sulla risorsa ["+JMXConstants.JMX_NAME+"] (param:"+uniqueIdMap+"): "+e.getMessage();
						ControlStationCore.getLog().error(errorMessage,e);
						resultReset = errorMessage;
					}
					
					de = new DataElement();
					de.setType(DataElementType.NOTE);
					de.setValue(resultReset);
					dati.addElement(de);
					
				}
				
				// Link resetCounters
				if(showResetCounters){
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_RESET+"_"+i);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO_RESET);
					de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO_RESET);
					if(ruoloPorta!=null) {
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""),
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET, alias),
								parRuoloPorta, parNomePorta,parServiceBinding);
					}
					else {
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""),
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET, alias));
					}
					de.setType(DataElementType.LINK);
					dati.addElement(de);
				}
					
				String result = null;
				String uniqueIdMap = null;
				try{
					uniqueIdMap = UniqueIdentifierUtilities.getUniqueId(policy);
					result = this.core.invokeJMXMethod(this.core.getGestoreRisorseJMX(alias),alias,JMXConstants.JMX_TYPE, 
							JMXConstants.JMX_NAME,
							JMXConstants.CC_METHOD_NAME_GET_STATO_POLICY,
							uniqueIdMap);
				}catch(Exception e){
					String errorMessage = "Errore durante l'invocazione dell'operazione ["+JMXConstants.CC_METHOD_NAME_GET_STATO_POLICY+"] sulla risorsa ["+JMXConstants.JMX_NAME+"] (param:"+uniqueIdMap+"): "+e.getMessage();
					ControlStationCore.getLog().error(errorMessage,e);
					result = errorMessage;
				}
				
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE);
				de.setLabelAffiancata(false);
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setRows(20);
				de.setCols(100);
				de.setValue(result);
				dati.addElement(de);
				
				this.pd.disableEditMode();
				
				i++;
			}
			
		}
	}
	
	protected void addToDatiDataElementStato_postBackViaGET(Vector<DataElement> dati, String param, String label, boolean enabled, boolean postBack, boolean withWarningOnly, 
			boolean warningOnly, boolean hidden){
		this._addToDatiDataElementStato(dati, param, label, enabled, postBack, false, withWarningOnly, warningOnly, hidden);
	}
	protected void addToDatiDataElementStato_postBackViaPOST(Vector<DataElement> dati, String param, String label, boolean enabled, boolean postBackPOST, boolean withWarningOnly, 
			boolean warningOnly, boolean hidden){
		this._addToDatiDataElementStato(dati, param, label, enabled, false, postBackPOST, withWarningOnly, warningOnly, hidden);
	}
	
	private void _addToDatiDataElementStato(Vector<DataElement> dati, String param, String label, boolean enabled, boolean postBack, boolean postBackPOST, boolean withWarningOnly, 
			boolean warningOnly, boolean hidden){
		DataElement de = new DataElement();
		de.setName(param);
		de.setLabel(label);
		if(hidden) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.SELECT);
			if(withWarningOnly){
				de.setValues(ConfigurazioneCostanti.STATI_CON_WARNING);
			}
			else{
				de.setValues(ConfigurazioneCostanti.STATI);
			}
			if(postBack) {
				de.setPostBack(postBack);
			}
			if(postBackPOST) {
				de.setPostBack_viaPOST(postBackPOST);
			}
		}
		if(enabled && (!withWarningOnly || !warningOnly) ){
			de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
		}
		else if(warningOnly && withWarningOnly){
			de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_STATO_WARNING_ONLY);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_WARNING_ONLY);
		}
		else{
			de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
		}
		dati.addElement(de);
	}
	private void addToDatiDataElementStatoReadOnly(Vector<DataElement> dati, String param, String label, boolean enabled, boolean postBack, 
			boolean withWarningOnly, boolean warningOnly){
		DataElement de = new DataElement();
		de.setName(param);
		de.setLabel(label);
		de.setType(DataElementType.HIDDEN);
		if(enabled && (!withWarningOnly || !warningOnly) ){
			de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
		}
		else if(warningOnly && withWarningOnly){
			de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_STATO_WARNING_ONLY);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_WARNING_ONLY);
		}
		else{
			de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
		}
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(param+"___LABEL");
		de.setLabel(label);
		de.setType(DataElementType.TEXT);
		if(enabled && (!withWarningOnly || !warningOnly) ){
			de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
		}
		else if(warningOnly && withWarningOnly){
			de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_STATO_WARNING_ONLY);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_WARNING_ONLY);
		}
		else{
			de.setSelected(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
		}
		dati.addElement(de);
		
	}
	
	private void addToDatiAttivazioneFiltro(Vector<DataElement> dati, TipoOperazione tipoOperazione,AttivazionePolicy policy, String nomeSezione, InfoPolicy infoPolicy, 
			RuoloPolicy ruoloPorta, String nomePorta, ServiceBinding serviceBinding,
			IDSoggetto idSoggettoProprietario, boolean tokenAbilitato, 
			CredenzialeTipo tipoAutenticazione, Boolean appId, 
			PddTipologia pddTipologiaSoggettoAutenticati, boolean gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore) throws Exception {
	
		boolean delegata = false;
		boolean applicativa = false;
		boolean configurazione = false;
		if(ruoloPorta!=null) {
			if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
				delegata = (nomePorta!=null);
			}
			else if(RuoloPolicy.APPLICATIVA.equals(ruoloPorta)) {
				applicativa = (nomePorta!=null);
			}
		}
		configurazione = !delegata && !applicativa;
		
		org.openspcoop2.core.config.constants.CredenzialeTipo tipoAutenticazioneConfig = null;
		if(tipoAutenticazione!=null) {
			tipoAutenticazioneConfig = org.openspcoop2.core.config.constants.CredenzialeTipo.toEnumConstant(tipoAutenticazione.getValue(), true);
		}
		
		boolean multitenant = this.confCore.isMultitenant();
		
		
		// Elaboro valori con dipendenze
		
		List<String> protocolliLabel = null;
		List<String> protocolliValue = null;
		String protocolloSelezionatoLabel = null;
		String protocolloSelezionatoValue = null;
		
		List<String> ruoliErogatoreLabel = null;
		List<String> ruoliErogatoreValue = null;
		String ruoloErogatoreSelezionatoLabel = null;
		String ruoloErogatoreSelezionatoValue = null;
		
		List<String> erogatoriLabel = null;
		List<String> erogatoriValue = null;
		String datiIdentificativiErogatoreSelezionatoLabel = null;
		String datiIdentificativiErogatoreSelezionatoValue = null;
		
		List<String> serviziLabel = null;
		List<String> serviziValue = null;
		String datiIdentificativiServizioSelezionatoLabel = null;
		String datiIdentificativiServizioSelezionatoValue = null;
		
		List<String> azioniLabel = null;
		List<String> azioniValue = null;
		List<String> azioniSelezionataLabel = null;
		List<String> azioniSelezionataValue = null;
		
		List<String> serviziApplicativiErogatoreLabel = null;
		List<String> serviziApplicativiErogatoreValue = null;
		String servizioApplicativoErogatoreSelezionatoLabel = null;
		String servizioApplicativoErogatoreSelezionatoValue = null;
		
		List<String> ruoliFruitoreLabel = null;
		List<String> ruoliFruitoreValue = null;
		String ruoloFruitoreSelezionatoLabel = null;
		String ruoloFruitoreSelezionatoValue = null;
		
		List<String> fruitoriLabel = null;
		List<String> fruitoriValue = null;
		String datiIdentificativiFruitoreSelezionatoLabel = null;
		String datiIdentificativiFruitoreSelezionatoValue = null;
		
		List<String> serviziApplicativiFruitoreLabel = null;
		List<String> serviziApplicativiFruitoreValue = null;
		String servizioApplicativoFruitoreSelezionatoLabel = null;
		String servizioApplicativoFruitoreSelezionatoValue = null;
		
		boolean filtroByKey = false;
		
		// Cerco Ruoli con queste caratteristiche
		FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
		filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
		
		boolean protocolloAssociatoFiltroNonSelezionatoUtente = false;
		if(policy.getFiltro().isEnabled()){
			protocolliValue = this.confCore.getProtocolli(this.session);
			if(policy.getFiltro().getProtocollo()!=null) {
				// sara' sempre impostato, a meno della prima volta (create policy)
				if(protocolliValue.contains(policy.getFiltro().getProtocollo())==false) {
					protocolloAssociatoFiltroNonSelezionatoUtente = true;
				}
			}
		}
		
		if(policy.getFiltro().isEnabled()){
			
			// protocollo
			if(configurazione) {
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					protocolloSelezionatoValue = policy.getFiltro().getProtocollo();
				}
				else {
					if(!protocolliValue.contains(policy.getFiltro().getProtocollo())){
						policy.getFiltro().setProtocollo(null);
					}
					protocolloSelezionatoValue = policy.getFiltro().getProtocollo();
					if(protocolloSelezionatoValue==null || protocolloSelezionatoValue.equals("")) {
						if(protocolliValue.size()==1) {
							protocolloSelezionatoValue = protocolliValue.get(0);
						}
						else {
							protocolloSelezionatoValue = this.confCore.getProtocolloDefault(this.session, protocolliValue);
						}
					}
					//protocolli = enrichListConQualsiasi(protocolli); NOTA: In questa versione un protocollo deve essere per forza selezionato.
					protocolliLabel = new ArrayList<>();
					for (String protocollo : protocolliValue) {
						protocolliLabel.add(this.getLabelProtocollo(protocollo));
					}
				}
				protocolloSelezionatoLabel = this.getLabelProtocollo(protocolloSelezionatoValue); 
			}
			else {
				protocolloSelezionatoValue = policy.getFiltro().getProtocollo();
				if(protocolloSelezionatoValue==null) {
					protocolloSelezionatoValue = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(idSoggettoProprietario.getTipo());
				}
			}
			
			// ruolo erogatore
			if(configurazione) {
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					ruoloErogatoreSelezionatoValue = policy.getFiltro().getRuoloErogatore();
					ruoloErogatoreSelezionatoLabel = ruoloErogatoreSelezionatoValue!=null ? ruoloErogatoreSelezionatoValue :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI;
				}
				else {
					List<String> ruoliErogatore = this.core.getAllRuoli(filtroRuoli);
					
					if(policy.getFiltro().getRuoloErogatore()!=null) {
						ruoloErogatoreSelezionatoValue = policy.getFiltro().getRuoloErogatore();
					}
					if(!ruoliErogatore.contains(ruoloErogatoreSelezionatoValue)){
						policy.getFiltro().setRuoloErogatore(null);
						ruoloErogatoreSelezionatoValue = null;
					}
					ruoliErogatoreLabel = enrichListConLabelQualsiasi(ruoliErogatore);
					ruoliErogatoreValue = enrichListConValueQualsiasi(ruoliErogatore);
				}
			}
			
			// erogatore
			if(configurazione) {
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					IDSoggetto idSoggetto = null;
					if(policy.getFiltro().getTipoErogatore()!=null && policy.getFiltro().getNomeErogatore()!=null){
						datiIdentificativiErogatoreSelezionatoValue = policy.getFiltro().getTipoErogatore() + "/" + policy.getFiltro().getNomeErogatore();
						idSoggetto = new IDSoggetto(policy.getFiltro().getTipoErogatore() , policy.getFiltro().getNomeErogatore());
					}
					datiIdentificativiErogatoreSelezionatoLabel = idSoggetto!=null ? this.getLabelNomeSoggetto(idSoggetto) :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI;
				}
				else {
					List<IDSoggetto> listErogatori = new ArrayList<>();

					List<IDSoggetto> listSoggettiPreFilterMultitenant = this.confCore.getSoggettiErogatori(protocolloSelezionatoValue, protocolliValue);
					if(policy.getFiltro().getRuoloPorta()!=null && !RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta())) {
						for (IDSoggetto idSoggetto : listSoggettiPreFilterMultitenant) {
							Soggetto s = this.soggettiCore.getSoggettoRegistro(idSoggetto);
							boolean isPddEsterna = this.pddCore.isPddEsterna(s.getPortaDominio());
							if(RuoloPolicy.DELEGATA.equals(policy.getFiltro().getRuoloPorta())) {
								if(isPddEsterna) {
									listErogatori.add(idSoggetto);
								}	
								else {
									if(!PddTipologia.ESTERNO.equals(pddTipologiaSoggettoAutenticati)) {
										// multitenant abilitato
										listErogatori.add(idSoggetto);
									}
								}
							}
							else {
								if(!isPddEsterna) {
									listErogatori.add(idSoggetto);
								}
							}
						}
					}
					else {
						listErogatori.addAll(listSoggettiPreFilterMultitenant);
					}
					
					erogatoriLabel = new ArrayList<>();
					erogatoriValue = new ArrayList<>();
					for (IDSoggetto idSoggetto : listErogatori) {
						erogatoriLabel.add(this.getLabelNomeSoggetto(idSoggetto));
						erogatoriValue.add(idSoggetto.getTipo()+"/"+idSoggetto.getNome());
					}
					if(policy.getFiltro().getTipoErogatore()!=null && policy.getFiltro().getNomeErogatore()!=null){
						datiIdentificativiErogatoreSelezionatoValue = policy.getFiltro().getTipoErogatore() + "/" + policy.getFiltro().getNomeErogatore();
					}
					if(!erogatoriValue.contains(datiIdentificativiErogatoreSelezionatoValue)){
						policy.getFiltro().setTipoErogatore(null);
						policy.getFiltro().setNomeErogatore(null);
						datiIdentificativiErogatoreSelezionatoValue = null;
					}
					erogatoriLabel = enrichListConLabelQualsiasi(erogatoriLabel);
					erogatoriValue = enrichListConValueQualsiasi(erogatoriValue);
				}
			}
					
			// servizio
			if(configurazione) {
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					IDServizio idServizio = null;
					if(policy.getFiltro().getTipoServizio()!=null && policy.getFiltro().getNomeServizio()!=null && policy.getFiltro().getVersioneServizio()!=null &&
							policy.getFiltro().getTipoErogatore()!=null && policy.getFiltro().getNomeErogatore()!=null
							){
						datiIdentificativiServizioSelezionatoValue = policy.getFiltro().getTipoServizio()+"/"+policy.getFiltro().getNomeServizio()+"/"+policy.getFiltro().getVersioneServizio().intValue();
						if(this.core.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore()) {
							datiIdentificativiServizioSelezionatoValue = datiIdentificativiServizioSelezionatoValue+"/"+policy.getFiltro().getTipoErogatore()+"/"+policy.getFiltro().getNomeErogatore();
							idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(policy.getFiltro().getTipoServizio(), 
									policy.getFiltro().getNomeServizio(), 
									policy.getFiltro().getTipoErogatore(), 
									policy.getFiltro().getNomeErogatore(), 
									policy.getFiltro().getVersioneServizio());
						}
						else {
							idServizio = IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(policy.getFiltro().getTipoServizio(), 
									policy.getFiltro().getNomeServizio(), 
									null, 
									null, 
									policy.getFiltro().getVersioneServizio());
						}
					}
					if(this.core.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore()) {
						datiIdentificativiServizioSelezionatoLabel = idServizio!=null ? this.getLabelIdServizio(idServizio) :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI;
					}
					else {
						datiIdentificativiServizioSelezionatoLabel = idServizio!=null ? this.getLabelIdServizioSenzaErogatore(idServizio) :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI;
					}
				}
				else {
					List<IDServizio> listServizi = this.confCore.getServizi(protocolloSelezionatoValue, protocolliValue,
							policy.getFiltro().getTipoErogatore(), policy.getFiltro().getNomeErogatore());
					serviziLabel = new ArrayList<>();
					serviziValue = new ArrayList<>();
					for (IDServizio idServizio : listServizi) {
						
						String valueAPI = idServizio.getTipo()+"/"+idServizio.getNome()+"/"+idServizio.getVersione().intValue();
						if(this.core.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore()) {
							valueAPI = valueAPI +"/"+ idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome();
						}
						if(serviziValue.contains(valueAPI)) {
							continue;
						}
						serviziValue.add(valueAPI);
						
						String labelAPI = null;
						if(this.core.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore()) {
							labelAPI = this.getLabelIdServizio(idServizio);
						}
						else {
							labelAPI = this.getLabelIdServizioSenzaErogatore(idServizio);
						}
						serviziLabel.add(labelAPI);
					}
					boolean definedApi = policy.getFiltro().getTipoServizio()!=null && policy.getFiltro().getNomeServizio()!=null && policy.getFiltro().getVersioneServizio()!=null;
					if(this.core.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore()) {
						definedApi = definedApi && policy.getFiltro().getTipoErogatore()!=null && policy.getFiltro().getNomeErogatore()!=null;
					}
					if( definedApi ){
						datiIdentificativiServizioSelezionatoValue = policy.getFiltro().getTipoServizio()+"/"+policy.getFiltro().getNomeServizio()+"/"+policy.getFiltro().getVersioneServizio().intValue();
						if(this.core.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore()) {
							datiIdentificativiServizioSelezionatoValue = datiIdentificativiServizioSelezionatoValue +"/"+policy.getFiltro().getTipoErogatore()+"/"+policy.getFiltro().getNomeErogatore();
						}
					}
					if(!serviziValue.contains(datiIdentificativiServizioSelezionatoValue)){
						policy.getFiltro().setTipoServizio(null);
						policy.getFiltro().setNomeServizio(null);
						policy.getFiltro().setVersioneServizio(null);
						datiIdentificativiServizioSelezionatoValue = null;
					}
					serviziLabel = enrichListConLabelQualsiasi(serviziLabel);
					serviziValue = enrichListConValueQualsiasi(serviziValue);
				}
			}
			
			// azioni
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				if(policy.getFiltro().getAzione()!=null && !"".equals(policy.getFiltro().getAzione())){
					azioniSelezionataValue = new ArrayList<>();
					if(policy.getFiltro().getAzione().contains(",")) {
						String [] tmp = policy.getFiltro().getAzione().split(",");
						for (String az : tmp) {
							azioniSelezionataValue.add(az);
						}
					}
					else {
						azioniSelezionataValue.add(policy.getFiltro().getAzione());
					}
					if(!azioniSelezionataValue.isEmpty()) {
						azioniSelezionataLabel = new ArrayList<>();
						for (String az : azioniSelezionataValue) {
							azioniSelezionataLabel.add(az);
						}
					}
				}
				if(azioniSelezionataLabel==null) {
					azioniSelezionataLabel = new ArrayList<>();
					azioniSelezionataLabel.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI);
				}
			}
			else {
				List<String> azioni = null;
				Map<String,String> azioniConLabel = null;
				if(configurazione && datiIdentificativiServizioSelezionatoValue!=null) {
					azioni = this.confCore.getAzioni(protocolloSelezionatoValue, protocolliValue,
							policy.getFiltro().getTipoErogatore(), policy.getFiltro().getNomeErogatore(), 
							policy.getFiltro().getTipoServizio(), policy.getFiltro().getNomeServizio(), policy.getFiltro().getVersioneServizio());
				}
				else if(delegata) {
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(policy.getFiltro().getNomePorta());
					PortaDelegata pd = this.porteDelegateCore.getPortaDelegata(idPD);
					
					MappingFruizionePortaDelegata mappingPD = this.porteDelegateCore.getMappingFruizionePortaDelegata(pd);
					IDServizio idServizio = mappingPD.getIdServizio();
					AccordoServizioParteSpecifica asps = this.apsCore.getServizio(idServizio,false);
					AccordoServizioParteComuneSintetico aspc = this.apcCore.getAccordoServizioSintetico(this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
					
					if(pd.getAzione()!=null && pd.getAzione().sizeAzioneDelegataList()>0) {
						azioni = pd.getAzione().getAzioneDelegataList();
					}
					else {
						List<String> azioniAll = this.confCore.getAzioni(protocolloSelezionatoValue, protocolliValue,
								pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome(), 
								pd.getServizio().getTipo(), pd.getServizio().getNome(), pd.getServizio().getVersione());
						
						IDSoggetto idSoggettoFruitore = mappingPD.getIdFruitore();
						List<MappingFruizionePortaDelegata> listaMappingFruizione = this.apsCore.serviziFruitoriMappingList(idSoggettoFruitore, idServizio, null);
						List<String> azioniOccupate = new ArrayList<>();
						int listaMappingFruizioneSize = listaMappingFruizione != null ? listaMappingFruizione.size() : 0;
						if(listaMappingFruizioneSize > 0) {
							for (int i = 0; i < listaMappingFruizione.size(); i++) {
								MappingFruizionePortaDelegata mappingFruizionePortaDelegata = listaMappingFruizione.get(i);
								// colleziono le azioni gia' configurate
								PortaDelegata portaDelegataTmp = this.porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
								if(portaDelegataTmp.getAzione() != null && portaDelegataTmp.getAzione().getAzioneDelegataList() != null)
									azioniOccupate.addAll(portaDelegataTmp.getAzione().getAzioneDelegataList());
							}
						}
						
						azioni = new ArrayList<>();
						for (int i = 0; i < azioniAll.size(); i++) {
							String az = azioniAll.get(i);
							if(azioniOccupate.contains(az)==false) {
								azioni.add(az);
							}
						}
					}
					
					azioniConLabel = this.porteDelegateCore.getAzioniConLabel(asps, aspc, false, true, null);
				}
				else if(applicativa) {
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(policy.getFiltro().getNomePorta());
					PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idPA);
					MappingErogazionePortaApplicativa mappingPA = this.porteApplicativeCore.getMappingErogazionePortaApplicativa(pa);
					IDServizio idServizio = mappingPA.getIdServizio();
					AccordoServizioParteSpecifica asps = this.apsCore.getServizio(idServizio,false);
					AccordoServizioParteComuneSintetico aspc = this.apcCore.getAccordoServizioSintetico(this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
					
					if(pa.getAzione()!=null && pa.getAzione().sizeAzioneDelegataList()>0) {
						azioni = pa.getAzione().getAzioneDelegataList();
					}
					else {
						List<String> azioniAll = this.confCore.getAzioni(protocolloSelezionatoValue, protocolliValue,
								pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), 
								pa.getServizio().getTipo(), pa.getServizio().getNome(), pa.getServizio().getVersione());
						
						List<MappingErogazionePortaApplicativa> listaMappingErogazione = this.apsCore.mappingServiziPorteAppList(idServizio, null);
						List<String> azioniOccupate = new ArrayList<>();
						int listaMappingErogazioneSize = listaMappingErogazione != null ? listaMappingErogazione.size() : 0;
						if(listaMappingErogazioneSize > 0) {
							for (int i = 0; i < listaMappingErogazione.size(); i++) {
								MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = listaMappingErogazione.get(i);
								// colleziono le azioni gia' configurate
								PortaApplicativa portaApplicativaTmp = this.porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
								if(portaApplicativaTmp.getAzione() != null && portaApplicativaTmp.getAzione().getAzioneDelegataList() != null)
									azioniOccupate.addAll(portaApplicativaTmp.getAzione().getAzioneDelegataList());
							}
						}
						
						azioni = new ArrayList<>();
						for (int i = 0; i < azioniAll.size(); i++) {
							String az = azioniAll.get(i);
							if(azioniOccupate.contains(az)==false) {
								azioni.add(az);
							}
						}
					}
					
					azioniConLabel = this.porteApplicativeCore.getAzioniConLabel(asps, aspc, false, true, null);
				}
				else {
					azioni = new ArrayList<>();
				}
				if(policy.getFiltro().getAzione()!=null && !"".equals(policy.getFiltro().getAzione())){
					azioniSelezionataValue = new ArrayList<>();
					if(policy.getFiltro().getAzione().contains(",")) {
						String [] tmp = policy.getFiltro().getAzione().split(",");
						for (String az : tmp) {
							if(azioni.contains(az)){
								azioniSelezionataValue.add(az);
							}
						}
					}
					else {
						if(azioni.contains(policy.getFiltro().getAzione())){
							azioniSelezionataValue.add(policy.getFiltro().getAzione());
						}
					}
				}
				if(azioniSelezionataValue==null || azioniSelezionataValue.isEmpty()) {
					azioniSelezionataValue = null;
				}
				if(azioniConLabel!=null && azioniConLabel.size()>0) {
					azioniLabel = new ArrayList<>();
					azioniValue = new ArrayList<>();

					for (String idAzione : azioniConLabel.keySet()) {
						if(azioni.contains(idAzione)) {
							azioniValue.add(idAzione);
							azioniLabel.add(azioniConLabel.get(idAzione));
						}
					}
					
//					azioniLabel = enrichListConLabelQualsiasi(azioniLabel);
//					azioniValue = enrichListConValueQualsiasi(azioniValue);
				}
				else {
//					azioniLabel = enrichListConLabelQualsiasi(azioni);
//					azioniValue = enrichListConValueQualsiasi(azioni);
					azioniLabel = azioni;
					azioniValue = azioni;
				}
			}
				
			// servizi applicativi erogatore
			if(configurazione) {
				if(policy.getFiltro().getRuoloPorta()==null ||
						RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta()) || 
						RuoloPolicy.APPLICATIVA.equals(policy.getFiltro().getRuoloPorta())){
					if(protocolloAssociatoFiltroNonSelezionatoUtente) {
						if(policy.getFiltro().getServizioApplicativoErogatore()!=null){
							servizioApplicativoErogatoreSelezionatoValue = policy.getFiltro().getServizioApplicativoErogatore();
						}
						servizioApplicativoErogatoreSelezionatoLabel = servizioApplicativoErogatoreSelezionatoValue!=null ? servizioApplicativoErogatoreSelezionatoValue :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI;
					}
					else {
						serviziApplicativiErogatoreLabel = new ArrayList<>();
						serviziApplicativiErogatoreValue = new ArrayList<>();
						if(datiIdentificativiErogatoreSelezionatoValue!=null) {
							List<IDServizioApplicativo> listSA = this.confCore.getServiziApplicativiErogatori(protocolloSelezionatoValue, protocolliValue,
									policy.getFiltro().getTipoErogatore(), policy.getFiltro().getNomeErogatore(),
									policy.getFiltro().getTipoServizio(), policy.getFiltro().getNomeServizio(), policy.getFiltro().getVersioneServizio(),
									null);
							for (IDServizioApplicativo idServizioApplicativo : listSA) {
								serviziApplicativiErogatoreLabel.add(idServizioApplicativo.getNome());
								serviziApplicativiErogatoreValue.add(idServizioApplicativo.getNome());
							}
						}
						
						if(policy.getFiltro().getServizioApplicativoErogatore()!=null){
							servizioApplicativoErogatoreSelezionatoValue = policy.getFiltro().getServizioApplicativoErogatore();
						}
						if(!serviziApplicativiErogatoreValue.contains(servizioApplicativoErogatoreSelezionatoValue)){
							policy.getFiltro().setServizioApplicativoErogatore(null);
							servizioApplicativoErogatoreSelezionatoValue = null;
						}
						serviziApplicativiErogatoreLabel = enrichListConLabelQualsiasi(serviziApplicativiErogatoreLabel);
						serviziApplicativiErogatoreValue = enrichListConValueQualsiasi(serviziApplicativiErogatoreValue);
					}
				}
			}
			
			// ruolo fruitore (diventa ruolo richiedente nel caso di porta)
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				if(policy.getFiltro().getRuoloFruitore()!=null) {
					ruoloFruitoreSelezionatoValue = policy.getFiltro().getRuoloFruitore();
				}
				ruoloFruitoreSelezionatoLabel = ruoloFruitoreSelezionatoValue!=null ? ruoloFruitoreSelezionatoValue :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI;
			}
			else {
				List<String> ruoliFruitore = this.core.getAllRuoli(filtroRuoli);
				if(policy.getFiltro().getRuoloFruitore()!=null) {
					ruoloFruitoreSelezionatoValue = policy.getFiltro().getRuoloFruitore();
				}
				if(!ruoliFruitore.contains(ruoloFruitoreSelezionatoValue)){
					policy.getFiltro().setRuoloFruitore(null);
					ruoloFruitoreSelezionatoValue = null;
				}
				ruoliFruitoreLabel = enrichListConLabelQualsiasi(ruoliFruitore);
				ruoliFruitoreValue = enrichListConValueQualsiasi(ruoliFruitore);
			}
			
			// fruitore
			if(configurazione || applicativa) {
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					IDSoggetto idSoggetto = null;
					if(policy.getFiltro().getTipoFruitore()!=null && policy.getFiltro().getNomeFruitore()!=null){
						datiIdentificativiFruitoreSelezionatoValue = policy.getFiltro().getTipoFruitore() + "/" + policy.getFiltro().getNomeFruitore();
						idSoggetto = new IDSoggetto(policy.getFiltro().getTipoFruitore() , policy.getFiltro().getNomeFruitore());
					}
					datiIdentificativiFruitoreSelezionatoLabel = idSoggetto!=null ? this.getLabelNomeSoggetto(idSoggetto) :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI;
				}
				else {
//					List<IDSoggetto> listFruitori = this.confCore.getSoggettiFruitori(protocolloSelezionatoValue, protocolliValue,
//							policy.getFiltro().getTipoErogatore(), policy.getFiltro().getNomeErogatore(), 
//							policy.getFiltro().getTipoServizio(), policy.getFiltro().getNomeServizio(), policy.getFiltro().getVersioneServizio());
					
					List<IDSoggetto> listSoggetti = new ArrayList<>();
					if(configurazione) {
						List<IDSoggetto> listSoggettiPreFilterMultitenant = this.confCore.getSoggetti(protocolloSelezionatoValue, protocolliValue);
						if(policy.getFiltro().getRuoloPorta()!=null && !RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta())) {
							for (IDSoggetto idSoggetto : listSoggettiPreFilterMultitenant) {
								Soggetto s = this.soggettiCore.getSoggettoRegistro(idSoggetto);
								boolean isPddEsterna = this.pddCore.isPddEsterna(s.getPortaDominio());
								if(RuoloPolicy.APPLICATIVA.equals(policy.getFiltro().getRuoloPorta())) {
									if(isPddEsterna) {
										listSoggetti.add(idSoggetto);
									}	
									else {
										if(!PddTipologia.ESTERNO.equals(pddTipologiaSoggettoAutenticati)) {
											// multitenant abilitato
											listSoggetti.add(idSoggetto);
										}
									}
								}
								else {
									if(!isPddEsterna) {
										listSoggetti.add(idSoggetto);
									}
								}
							}
						}
						else {
							listSoggetti.addAll(listSoggettiPreFilterMultitenant);
						}
					}
					else {
					
						User user = ServletUtils.getUserFromSession(this.session);
						String userLogin = user.getLogin();
						
						List<String> tipiSoggettiGestitiProtocollo = this.soggettiCore.getTipiSoggettiGestitiProtocollo(protocolloSelezionatoValue);
						
						List<IDSoggettoDB> list = null;
						if(this.core.isVisioneOggettiGlobale(userLogin)){
							list = this.soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiGestitiProtocollo, null, tipoAutenticazione, appId, pddTipologiaSoggettoAutenticati);
						}else{
							list = this.soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiGestitiProtocollo, userLogin, tipoAutenticazione, appId, pddTipologiaSoggettoAutenticati);
						}
						if(list!=null && !list.isEmpty() && gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore) {
							for (int i = 0; i < list.size(); i++) {
								IDSoggettoDB soggettoCheck = list.get(i);
								if(soggettoCheck.getTipo().equals(idSoggettoProprietario.getTipo()) && soggettoCheck.getNome().equals(idSoggettoProprietario.getNome())) {
									list.remove(i);
									break;
								}
							}
						}
						
						if(list==null) {
							list = new ArrayList<>();
						}
						
						// aggiungo soggetti operativi per poi poter selezionare un applicativo
						if(multitenant) {
							List<IDSoggetto> listSoggettiPreFilterMultitenant = this.confCore.getSoggetti(protocolloSelezionatoValue, protocolliValue);
							for (IDSoggetto idSoggetto : listSoggettiPreFilterMultitenant) {
								Soggetto s = this.soggettiCore.getSoggettoRegistro(idSoggetto);
								boolean isPddEsterna = this.pddCore.isPddEsterna(s.getPortaDominio());
								if(!isPddEsterna) {
									boolean found = false;
									for (IDSoggettoDB sogg : list) {
										if(sogg.getTipo().equals(s.getTipo()) && sogg.getNome().equals(s.getNome())) {
											found = true;
											break;
										}
									}
									if(!found) {
										List<IDServizioApplicativoDB> listServiziApplicativiTmp = this.saCore.soggettiServizioApplicativoList(idSoggetto,userLogin,tipoAutenticazioneConfig, appId);
										if(listServiziApplicativiTmp!=null && !listServiziApplicativiTmp.isEmpty()) {
											IDSoggettoDB idSoggettoDB = new IDSoggettoDB();
											idSoggettoDB.setTipo(s.getTipo());
											idSoggettoDB.setNome(s.getNome());
											idSoggettoDB.setCodicePorta(s.getIdentificativoPorta());
											idSoggettoDB.setId(s.getId());
											list.add(idSoggettoDB);
										}
									}
								}
							}
						}
						
						if(!list.isEmpty()) {
							for (IDSoggettoDB soggetto : list) {
								listSoggetti.add(new IDSoggetto(soggetto.getTipo(), soggetto.getNome()));
							}
						}
						
					}
					
					fruitoriLabel = new ArrayList<>();
					fruitoriValue = new ArrayList<>();
					for (IDSoggetto idSoggetto : listSoggetti) {
						fruitoriLabel.add(this.getLabelNomeSoggetto(idSoggetto));
						fruitoriValue.add(idSoggetto.getTipo()+"/"+idSoggetto.getNome());
					}
					if(policy.getFiltro().getTipoFruitore()!=null && policy.getFiltro().getNomeFruitore()!=null){
						datiIdentificativiFruitoreSelezionatoValue = policy.getFiltro().getTipoFruitore() + "/" + policy.getFiltro().getNomeFruitore();
					}
					if(!fruitoriValue.contains(datiIdentificativiFruitoreSelezionatoValue)){
						policy.getFiltro().setTipoFruitore(null);
						policy.getFiltro().setNomeFruitore(null);
						datiIdentificativiFruitoreSelezionatoValue = null;
					}
					fruitoriLabel = enrichListConLabelQualsiasi(fruitoriLabel);
					fruitoriValue = enrichListConValueQualsiasi(fruitoriValue);
				}
			}
			else {
				if(delegata) {
					if(policy.getFiltro().getTipoFruitore()!=null && policy.getFiltro().getNomeFruitore()!=null){
						datiIdentificativiFruitoreSelezionatoValue = policy.getFiltro().getTipoFruitore() + "/" + policy.getFiltro().getNomeFruitore();
					}
				}
			}
			
			// servizi applicativi fruitore
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				if(policy.getFiltro().getServizioApplicativoFruitore()!=null){
					servizioApplicativoFruitoreSelezionatoValue = policy.getFiltro().getServizioApplicativoFruitore();
				}
				servizioApplicativoFruitoreSelezionatoLabel = servizioApplicativoFruitoreSelezionatoValue!=null ? servizioApplicativoFruitoreSelezionatoValue :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI;
			}
			else {
				
				IDSoggetto soggettoProprietarioServiziApplicativi = null;
				if(datiIdentificativiFruitoreSelezionatoValue!=null || !configurazione) {
					String tipoFruitore = null;
					String nomeFruitore = null;
					if(datiIdentificativiFruitoreSelezionatoValue!=null) {
						tipoFruitore = policy.getFiltro().getTipoFruitore();
						nomeFruitore = policy.getFiltro().getNomeFruitore();
					}
					else {
						tipoFruitore = idSoggettoProprietario.getTipo();
						nomeFruitore = idSoggettoProprietario.getNome();
					}
					soggettoProprietarioServiziApplicativi = new IDSoggetto(tipoFruitore, nomeFruitore);
				}
				
				if(soggettoProprietarioServiziApplicativi!=null) {
					serviziApplicativiFruitoreLabel = new ArrayList<>();
					serviziApplicativiFruitoreValue = new ArrayList<>();

					List<IDServizioApplicativo> listSA =null;
					if(configurazione) {
						listSA = this.confCore.getServiziApplicativiFruitore(protocolloSelezionatoValue, protocolliValue,
								soggettoProprietarioServiziApplicativi.getTipo(), soggettoProprietarioServiziApplicativi.getNome());
					}
					else {
						
						listSA = new ArrayList<>();
						
						User user = ServletUtils.getUserFromSession(this.session);
						String userLogin = user.getLogin();
						
						List<IDServizioApplicativoDB> listServiziApplicativiTmp = null;
						if(delegata || !multitenant) {
							listServiziApplicativiTmp = this.saCore.soggettiServizioApplicativoList(idSoggettoProprietario,userLogin,tipoAutenticazioneConfig,appId);
						}
						else {
							// sull'applicativa con multitenant deve essere stata selezionato un soggetto operativo.
							if(policy.getFiltro().getTipoFruitore()!=null && policy.getFiltro().getNomeFruitore()!=null) {
								IDSoggetto idSoggettoSelezionato = new IDSoggetto(policy.getFiltro().getTipoFruitore(), policy.getFiltro().getNomeFruitore());
								Soggetto s = this.soggettiCore.getSoggettoRegistro(idSoggettoSelezionato);
								boolean isPddEsterna = this.pddCore.isPddEsterna(s.getPortaDominio());
								if(!isPddEsterna) {
									listServiziApplicativiTmp = this.saCore.soggettiServizioApplicativoList(idSoggettoSelezionato,userLogin,tipoAutenticazioneConfig,appId);
								}									
							}
						}
						
						if(listServiziApplicativiTmp!=null && !listServiziApplicativiTmp.isEmpty()) {
							for (IDServizioApplicativoDB servizioApplicativo : listServiziApplicativiTmp) {
								IDServizioApplicativo idSA = new IDServizioApplicativo();
								idSA.setIdSoggettoProprietario(idSoggettoProprietario);
								idSA.setNome(servizioApplicativo.getNome());
								listSA.add(idSA);
							}
						}
						
					}
					for (IDServizioApplicativo idServizioApplicativo : listSA) {
						serviziApplicativiFruitoreLabel.add(idServizioApplicativo.getNome());
						serviziApplicativiFruitoreValue.add(idServizioApplicativo.getNome());
					}
					
					if(policy.getFiltro().getServizioApplicativoFruitore()!=null){
						servizioApplicativoFruitoreSelezionatoValue = policy.getFiltro().getServizioApplicativoFruitore();
					}
					if(!serviziApplicativiFruitoreValue.contains(servizioApplicativoFruitoreSelezionatoValue)){
						policy.getFiltro().setServizioApplicativoFruitore(null);
						servizioApplicativoFruitoreSelezionatoValue = null;
					}
					serviziApplicativiFruitoreLabel = enrichListConLabelQualsiasi(serviziApplicativiFruitoreLabel);
					serviziApplicativiFruitoreValue = enrichListConValueQualsiasi(serviziApplicativiFruitoreValue);
				}
			}
			
			// filtro by key se non sono richiesti campionamenti statistici
			// NON è vero. Il filtro ci può sempre essere
			//if(infoPolicy!=null && infoPolicy.isUtilizzoRisorseStatistiche()==false){
			if(infoPolicy!=null){
				filtroByKey = true;
			}
		}

		
		
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_FILTRO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		boolean filtroAbilitatoAPI = false;
		if(ruoloPorta!=null) {
			boolean first = this.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
			if(first) {
				String filtro = this.toStringCompactFilter(policy.getFiltro(),ruoloPorta,nomePorta,serviceBinding);
				filtroAbilitatoAPI = filtro!=null && !"".equals(filtro) && !CostantiControlStation.LABEL_STATO_DISABILITATO.equals(filtro);
			}
			else {
				String filtro = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED_CONSOLE_ONLY);
				filtroAbilitatoAPI = ServletUtils.isCheckBoxEnabled(filtro);
			}
		}
		
		// stato
		if(protocolloAssociatoFiltroNonSelezionatoUtente) {
			
			addToDatiDataElementStatoReadOnly(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED, 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED, policy.getFiltro().isEnabled(), true,
					false, false);
			
			if(policy.getFiltro().isEnabled()){
				de = new DataElement();
				de.setType(DataElementType.NOTE);
				de.setValue("Filtro non modificabile poichè definito per un "+CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_DI.toLowerCase()+" non attivo nella console");
				dati.addElement(de);
			}
		}
		else {
			boolean hidden = ruoloPorta!=null;
			addToDatiDataElementStato_postBackViaPOST(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED, 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED, policy.getFiltro().isEnabled(), true,
					false, false, hidden);
			
			if(ruoloPorta!=null) {
				addToDatiDataElementStato_postBackViaPOST(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED_CONSOLE_ONLY, 
						ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED, filtroAbilitatoAPI, true,
						false, false, false);
			}
			
		}
		
		boolean filtroEnabled = policy.getFiltro().isEnabled();
		if(ruoloPorta!=null) {
			filtroEnabled = filtroAbilitatoAPI;
		}
		
		if(!filtroEnabled && ruoloPorta!=null) {
			// Protocollo
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO);
			de.setValue(protocolloSelezionatoValue); // un protocollo e' sempre selezionato 
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		
		if(filtroEnabled){
		
			// Ruolo PdD
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD);
			if(policy.getFiltro().getRuoloPorta()!=null){
				de.setValue(policy.getFiltro().getRuoloPorta().getValue());
			}
			if(protocolloAssociatoFiltroNonSelezionatoUtente || !configurazione) {
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				if(configurazione) {
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD+"___LABEL");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD);
					if(policy.getFiltro().getRuoloPorta()!=null){
						de.setValue(policy.getFiltro().getRuoloPorta().getValue());
					}
					else {
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI);
					}
					de.setType(DataElementType.TEXT);
				}
			}
			else {
				de.setValues(ConfigurazioneCostanti.TIPI_RUOLO_PDD);
				de.setLabels(ConfigurazioneCostanti.LABEL_TIPI_RUOLO_PDD);
				if(policy.getFiltro().getRuoloPorta()!=null){
					de.setSelected(policy.getFiltro().getRuoloPorta().getValue());
				}
				de.setType(DataElementType.SELECT);
				de.setPostBack_viaPOST(true);
			}
			dati.addElement(de);
			
	
			// Protocollo
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO);
			de.setValue(protocolloSelezionatoValue); // un protocollo e' sempre selezionato 
			if(protocolloAssociatoFiltroNonSelezionatoUtente || !configurazione) {
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				if(configurazione) {
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO+"___LABEL");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO);
					de.setValue(protocolloSelezionatoLabel); // un protocollo e' sempre selezionato 
					de.setType(DataElementType.TEXT);
				}
			}
			else if(protocolliValue.size()>1){ 
				de.setValues(protocolliValue);
				de.setLabels(protocolliLabel);
				de.setSelected(protocolloSelezionatoValue);
				de.setType(DataElementType.SELECT);
				de.setPostBack_viaPOST(true);
			}
			else{
				de.setType(DataElementType.HIDDEN);
				if(protocolliValue!=null && protocolliValue.size()>0) {
					dati.addElement(de);
					
					// Si è deciso cmq di farlo vedere
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO+"___LABEL");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO);
					de.setValue(this.getLabelProtocollo(protocolliValue.get(0))); // un protocollo e' sempre selezionato 
					de.setType(DataElementType.TEXT);
				}
			}
			dati.addElement(de);
			
			// Ruolo Erogatore
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_EROGATORE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_EROGATORE);
			if(datiIdentificativiErogatoreSelezionatoValue!=null) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setValue(ruoloErogatoreSelezionatoValue);
				if(protocolloAssociatoFiltroNonSelezionatoUtente || !configurazione) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					if(configurazione) {
						de = new DataElement();
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_EROGATORE+"___LABEL");
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_EROGATORE);
						de.setValue(ruoloErogatoreSelezionatoLabel);
						de.setType(DataElementType.TEXT);
					}
				}
				else {
					de.setLabels(ruoliErogatoreLabel);
					de.setValues(ruoliErogatoreValue);
					de.setSelected(ruoloErogatoreSelezionatoValue);
					de.setType(DataElementType.SELECT);
					de.setPostBack_viaPOST(true);
				}
			}
			dati.addElement(de);
			
			// Erogatore
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_EROGATORE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_EROGATORE);
			if(ruoloErogatoreSelezionatoValue!=null) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setValue(datiIdentificativiErogatoreSelezionatoValue);
				if(protocolloAssociatoFiltroNonSelezionatoUtente || !configurazione) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					if(configurazione) {
						de = new DataElement();
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_EROGATORE+"___LABEL");
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_EROGATORE);
						de.setValue(datiIdentificativiErogatoreSelezionatoLabel);
						de.setType(DataElementType.TEXT);
					}
				}
				else {
					de.setLabels(erogatoriLabel);
					de.setValues(erogatoriValue);
					de.setSelected(datiIdentificativiErogatoreSelezionatoValue);
					de.setType(DataElementType.SELECT);
					de.setPostBack_viaPOST(true);
				}
			}
			dati.addElement(de);
						
			// Servizio
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO);
			de.setValue(datiIdentificativiServizioSelezionatoValue);
			if(protocolloAssociatoFiltroNonSelezionatoUtente || !configurazione) {
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				if(configurazione) {
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO+"___LABEL");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO);
					de.setValue(datiIdentificativiServizioSelezionatoLabel);
					if(this.core.isControlloTrafficoPolicyGlobaleFiltroApi()) {
						de.setType(DataElementType.TEXT);
					}
					else {
						de.setType(DataElementType.HIDDEN);
					}
				}
			}
			else {
				de.setValue(datiIdentificativiServizioSelezionatoValue);
				if(this.core.isControlloTrafficoPolicyGlobaleFiltroApi()) {
					de.setLabels(serviziLabel);
					de.setValues(serviziValue);
					de.setSelected(datiIdentificativiServizioSelezionatoValue);
					de.setType(DataElementType.SELECT);
					de.setPostBack_viaPOST(true);
				}
				else {
					de.setType(DataElementType.HIDDEN);
				}
			}
			dati.addElement(de);
			
			// Azione
			boolean showAzione = true;
			if(configurazione) {
				if(datiIdentificativiServizioSelezionatoValue==null) {
					showAzione = false;
				}
			}
			if(showAzione) {
				
				boolean azioniAll = false;
				boolean first = this.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
				if(first) {
					azioniAll = azioniSelezionataValue==null || azioniSelezionataValue.isEmpty();
				}
				else if(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO.equals(this.getPostBackElementName()) ||
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED_CONSOLE_ONLY.equals(this.getPostBackElementName())) {
					azioniAll = true;
				}
				else {
					String azioniAllPart = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE_PUNTUALE);
					azioniAll = ServletUtils.isCheckBoxEnabled(azioniAllPart);
				}
				
				if(!protocolloAssociatoFiltroNonSelezionatoUtente) {
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE_PUNTUALE);
					de.setPostBack_viaPOST(true);
					de.setValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE_PUNTUALE_ALL_VALUES);
					if(ServiceBinding.REST.equals(serviceBinding)) {
						de.setLabels(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE_PUNTUALE_RISORSE_ALL_VALUES);
					}
					else {
						de.setLabels(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE_PUNTUALE_ALL_VALUES);
					}
					if(azioniAll) {
						de.setSelected(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE_PUNTUALE_ALL_VALUE_TRUE);
					}
					else {
						de.setSelected(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE_PUNTUALE_ALL_VALUE_FALSE);
					}
					if(serviceBinding!=null) {
						de.setLabel(this.getLabelAzioni(serviceBinding));
					}
					else {
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE);
					}
					de.setType(DataElementType.SELECT);
					dati.addElement(de);
				}
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE);
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					if(serviceBinding!=null) {
						de.setLabel(this.getLabelAzioni(serviceBinding));
					}
					else {
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE);
					}
				}
				else {
					de.setLabel("");
				}
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					de.setValue(policy.getFiltro().getAzione());
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE+"___LABEL");
					if(serviceBinding!=null) {
						de.setLabel(this.getLabelAzioni(serviceBinding));
					}
					else {
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE);
					}
					if(azioniSelezionataLabel!=null && !azioniSelezionataLabel.isEmpty()) {
						if(azioniSelezionataLabel.size()==1) {
							de.setValue(azioniSelezionataLabel.get(0));
						}
						else {
							de.setValue(azioniSelezionataLabel.toString());
						}
					}
					de.setType(DataElementType.TEXT);
				}
				else {
					if(!azioniAll) {
						de.setLabels(azioniLabel);
						de.setValues(azioniValue);
						de.setSelezionati(azioniSelezionataValue);
						de.setType(DataElementType.MULTI_SELECT);
						if(azioniValue!=null && azioniValue.size()<=10) {
							if(azioniValue.size()<=3) {
								de.setRows(3);
							}
							else {
								de.setRows(azioniValue.size());
							}
						}
						else {
							de.setRows(10);
						}
						de.setPostBack_viaPOST(true);
					}
					else {
						de.setType(DataElementType.HIDDEN);
					}
				}
				dati.addElement(de);
			}
			
			// Servizio Applicativo Erogatore
			if(serviziApplicativiErogatoreValue!=null){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE);
				de.setValue(servizioApplicativoErogatoreSelezionatoValue);
				if(protocolloAssociatoFiltroNonSelezionatoUtente || !configurazione) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					if(configurazione) {
						de = new DataElement();
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE+"___LABEL");
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE);
						de.setValue(servizioApplicativoErogatoreSelezionatoLabel);
						//de.setType(DataElementType.TEXT);
						de.setType(DataElementType.HIDDEN);
					}
				}
				else {
					de.setLabels(serviziApplicativiErogatoreLabel);
					de.setValues(serviziApplicativiErogatoreValue);
					de.setSelected(servizioApplicativoErogatoreSelezionatoValue);
					de.setValue(servizioApplicativoErogatoreSelezionatoValue);
					//de.setType(DataElementType.SELECT);
					de.setType(DataElementType.HIDDEN);
					de.setPostBack_viaPOST(true);
				}
				dati.addElement(de);
			}
			
			// Ruolo Fruitore
			boolean showRuoloRichiedente = false;
			if(configurazione) {
				showRuoloRichiedente = true;
			}
			else {
				if(serviziApplicativiFruitoreValue!=null && serviziApplicativiFruitoreValue.size()>1){
					showRuoloRichiedente = true;
				}
				else if(fruitoriValue!=null && fruitoriValue.size()>1){
					showRuoloRichiedente = true;
				}
			}
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE);
//			if(configurazione) {
//				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE);
//			}
//			else {
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_RICHIEDENTE);
			//}
			if((datiIdentificativiFruitoreSelezionatoValue!=null && !delegata) || servizioApplicativoFruitoreSelezionatoValue!=null || !showRuoloRichiedente) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setValue(ruoloFruitoreSelezionatoValue);
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					if(configurazione) {
						de = new DataElement();
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE+"___LABEL");
//						if(configurazione) {
//							de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE);
//						}
//						else {
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_RICHIEDENTE);
						//}
						de.setValue(ruoloFruitoreSelezionatoLabel);
						de.setType(DataElementType.TEXT);
					}
				}
				else {
					de.setLabels(ruoliFruitoreLabel);
					de.setValues(ruoliFruitoreValue);
					de.setSelected(ruoloFruitoreSelezionatoValue);
					de.setType(DataElementType.SELECT);
					de.setPostBack_viaPOST(true);
				}
			}
			dati.addElement(de);
			
			// Fruitore
			if(fruitoriValue!=null && fruitoriValue.size()>1){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE);
				if(ruoloFruitoreSelezionatoValue!=null) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setValue(datiIdentificativiFruitoreSelezionatoValue);
					if(protocolloAssociatoFiltroNonSelezionatoUtente || delegata) {
						de.setType(DataElementType.HIDDEN);
						dati.addElement(de);
						
						if(configurazione) {
							de = new DataElement();
							de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE+"___LABEL");
							de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE);
							de.setValue(datiIdentificativiFruitoreSelezionatoLabel);
							de.setType(DataElementType.TEXT);
						}
					}
					else {
						de.setLabels(fruitoriLabel);
						de.setValues(fruitoriValue);
						de.setSelected(datiIdentificativiFruitoreSelezionatoValue);
						de.setType(DataElementType.SELECT);
						de.setPostBack_viaPOST(true);
					}
				}
				dati.addElement(de);
			}
			
			// Servizio Applicativo Fruitore
			if(serviziApplicativiFruitoreValue!=null && serviziApplicativiFruitoreValue.size()>1){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE);
				if(ruoloFruitoreSelezionatoValue!=null) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setValue(servizioApplicativoFruitoreSelezionatoValue);
					if(protocolloAssociatoFiltroNonSelezionatoUtente) {
						de.setType(DataElementType.HIDDEN);
						dati.addElement(de);
						
						if(configurazione) {
							de = new DataElement();
							de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE+"___LABEL");
							de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE);
							de.setValue(servizioApplicativoFruitoreSelezionatoLabel);
							de.setType(DataElementType.TEXT);
						}
					}
					else {
						de.setLabels(serviziApplicativiFruitoreLabel);
						de.setValues(serviziApplicativiFruitoreValue);
						de.setSelected(servizioApplicativoFruitoreSelezionatoValue);
						de.setType(DataElementType.SELECT);
						de.setPostBack_viaPOST(true);
					}
				}
				dati.addElement(de);
			}
			
			if(filtroByKey){
				
				// per chiave
				
				if(policy.getFiltro().isInformazioneApplicativaEnabled()){
					de = new DataElement();
					de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED);
					de.setType(DataElementType.NOTE);
					dati.addElement(de);
				}
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED);
				if(policy.getFiltro().isInformazioneApplicativaEnabled()){
					de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO);
				}
				else{
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED_COMPACT);
				}
				de.setValue(policy.getFiltro().isInformazioneApplicativaEnabled()+"");
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED+"___LABEL");
					if(policy.getFiltro().isInformazioneApplicativaEnabled()){
						de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO);
					}
					else{
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED);
					}
					if(policy.getFiltro().isInformazioneApplicativaEnabled()){
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
					}
					else {
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
					}
					de.setType(DataElementType.TEXT);
				}
				else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(policy.getFiltro().isInformazioneApplicativaEnabled());
					de.setPostBack_viaPOST(true);
				}
				dati.addElement(de);
				
				if(policy.getFiltro().isInformazioneApplicativaEnabled()){
					
					TipoFiltroApplicativo tipoFiltro = null;
					if(policy.getFiltro().getInformazioneApplicativaTipo()!=null && !"".equals(policy.getFiltro().getInformazioneApplicativaTipo())){
						tipoFiltro = TipoFiltroApplicativo.toEnumConstant(policy.getFiltro().getInformazioneApplicativaTipo());
					}
					if(tipoFiltro==null){
						tipoFiltro = TipoFiltroApplicativo.toEnumConstant(ConfigurazioneCostanti.CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO_DEFAULT);
					}
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO);
					de.setValue(policy.getFiltro().getInformazioneApplicativaTipo());
					if(protocolloAssociatoFiltroNonSelezionatoUtente) {
						de.setType(DataElementType.HIDDEN);
						dati.addElement(de);
						
						de = new DataElement();
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO+"___LABEL");
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO);
						de.setValue(policy.getFiltro().getInformazioneApplicativaTipo());
						de.setType(DataElementType.TEXT);
					}
					else {
						de.setValues(TipoFiltroApplicativo.toStringArray());
						de.setLabels(ConfigurazioneCostanti.LABEL_RATE_LIMITING_FILTRO_APPLICATIVO);
						de.setSelected(policy.getFiltro().getInformazioneApplicativaTipo());
						de.setType(DataElementType.SELECT);
						de.setPostBack_viaPOST(true);
					}
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME);
					de.setLabel(this.getLabelTipoInformazioneApplicativaFiltro(policy.getFiltro().getInformazioneApplicativaTipo()));
					de.setValue(policy.getFiltro().getInformazioneApplicativaNome());
					if(tipoFiltro==null || 
							TipoFiltroApplicativo.SOAPACTION_BASED.equals(tipoFiltro) || 
							TipoFiltroApplicativo.INDIRIZZO_IP.equals(tipoFiltro) || 
							TipoFiltroApplicativo.INDIRIZZO_IP_FORWARDED.equals(tipoFiltro)){
						de.setType(DataElementType.HIDDEN);
					}
					else{
						if(protocolloAssociatoFiltroNonSelezionatoUtente) {
							de.setType(DataElementType.TEXT);
						}
						else {
							de.setRequired(true);
							if(TipoFiltroApplicativo.URLBASED.equals(tipoFiltro) ||
									TipoFiltroApplicativo.CONTENT_BASED.equals(tipoFiltro)) {
								de.setType(DataElementType.TEXT_AREA);
							}
							else {
								de.setType(DataElementType.TEXT_EDIT);
							}
						}
					}
					dati.addElement(de);
				
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_VALORE);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_VALORE);
					de.setValue(StringEscapeUtils.escapeHtml(policy.getFiltro().getInformazioneApplicativaValore())); // il valore può contenere ""
					if(protocolloAssociatoFiltroNonSelezionatoUtente) {
						de.setType(DataElementType.HIDDEN);
						dati.addElement(de);
						
						de = new DataElement();
						de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_VALORE+"___LABEL");
						de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_VALORE);
						de.setValue(StringEscapeUtils.escapeHtml(policy.getFiltro().getInformazioneApplicativaValore())); // il valore può contenere ""
						de.setType(DataElementType.TEXT);
					}
					else {
						de.setRequired(true);
						de.setType(DataElementType.TEXT_EDIT);
					}
					dati.addElement(de);
				}
				
			}
			
		}
	}
	
	private void addToDatiAttivazioneGroupBy(Vector<DataElement> dati, TipoOperazione tipoOperazione,AttivazionePolicy policy, String nomeSezione,	
			InfoPolicy infoPolicy, RuoloPolicy ruoloPorta, String nomePorta, ServiceBinding serviceBinding,
			boolean tokenAbilitato) throws Exception {
	
		boolean delegata = false;
		boolean applicativa = false;
		boolean configurazione = false;
		if(ruoloPorta!=null) {
			if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
				delegata = (nomePorta!=null);
			}
			else if(RuoloPolicy.APPLICATIVA.equals(ruoloPorta)) {
				applicativa = (nomePorta!=null);
			}
		}
		configurazione = !delegata && !applicativa;
		
		
				
		List<String> protocolli = null;
		boolean groupByKey = false;
		
		if(policy.getGroupBy().isEnabled()){
			
			// protocollo
			protocolli = this.confCore.getProtocolli();
			
			// group by by key se non sono richiesti campionamenti statistici
			if(infoPolicy!=null && infoPolicy.isIntervalloUtilizzaRisorseStatistiche()==false && 
					infoPolicy.isDegradoPrestazionaleUtilizzaRisorseStatistiche()==false){
				groupByKey = true;
			}
		}

//
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RAGGRUPPAMENTO);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RAGGRUPPAMENTO_NOTE);
		de.setType(DataElementType.NOTE);
		dati.addElement(de);
		
		// stato
		addToDatiDataElementStato_postBackViaPOST(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_ENABLED, 
				ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_ENABLED_STATO, policy.getGroupBy().isEnabled(), true, false, false, false);
		
		/*
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_ENABLED);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_ENABLED);
		de.setType(DataElementType.SELECT);
		de.setValues(ConfigurazioneCostanti.CONFIGURAZIONE_STATI_COLLEZIONAMENTO);
		if(policy.getGroupBy().isEnabled()){
			de.setSelected(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_ABILITATO);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_ABILITATO);
		}
		else{
			de.setSelected(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_DISABILITATO);
			de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_DISABILITATO);
		}
		de.setPostBack_viaPOST(true);
		dati.addElement(de);
		*/
		
		if(policy.getGroupBy().isEnabled()){
		
			
			// --- GENERALI ---
			
			if(configurazione) {
				
				boolean showRuoloPdD = policy.getFiltro()==null || 
						policy.getFiltro().isEnabled()==false || 
						policy.getFiltro().getRuoloPorta()==null ||
						RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta());
				
				boolean showProtocollo = protocolli.size()>1 && (policy.getFiltro()==null || 
						policy.getFiltro().isEnabled()==false || 
						policy.getFiltro().getProtocollo()==null);
				
				boolean showErogatore = policy.getFiltro()==null || 
						policy.getFiltro().isEnabled()==false || 
						policy.getFiltro().getTipoErogatore()==null ||
						policy.getFiltro().getNomeErogatore()==null;
				
				if(showRuoloPdD || showProtocollo || showErogatore) {
//					de = new DataElement();
//					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_DATI_GENERALI);
//					de.setType(DataElementType.NOTE);
//					dati.addElement(de);
				}
				
				// Ruolo PdD
				if( showRuoloPdD ){
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RUOLO_PDD);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RUOLO_PDD_LABEL);
					//de.setLabelRight(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RUOLO_PDD_NOTE);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(policy.getGroupBy().isRuoloPorta());
					de.setValue(policy.getGroupBy().isRuoloPorta()+"");
					dati.addElement(de);
				}
			
				// Protocollo
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PROTOCOLLO);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PROTOCOLLO);
				if(showProtocollo){
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(policy.getGroupBy().isProtocollo());
					de.setValue(policy.getGroupBy().isProtocollo()+"");
				}
				else{
					de.setType(DataElementType.HIDDEN);
					if(protocolli.size()==1){
						de.setValue("false");
					}
				}
				dati.addElement(de);
				
				// Erogatore
				if( showErogatore ){
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_EROGATORE);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_EROGATORE);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(policy.getGroupBy().isErogatore());
					de.setValue(policy.getGroupBy().isErogatore()+"");
					dati.addElement(de);
				}
								
			}

			
			// --- API ---
			
			boolean showServizio = false;
			
			boolean showAzione = policy.getFiltro()==null || 
					policy.getFiltro().isEnabled()==false || 
					policy.getFiltro().getAzione()==null;
			
			boolean showSAErogatore = false;
			
			if(configurazione) {
			
				if(this.core.isControlloTrafficoPolicyGlobaleGroupByApi()) {
					showServizio = policy.getFiltro()==null || 
							policy.getFiltro().isEnabled()==false || 
							policy.getFiltro().getTipoServizio()==null ||
							policy.getFiltro().getNomeServizio()==null;
				}

				if(showAzione) {
					showAzione = showServizio && policy.getGroupBy().isServizio(); // l'azione la scelgo se ho prima selezionato una API
				}
				
				showSAErogatore = policy.getFiltro()==null || 
						policy.getFiltro().isEnabled()==false || 
						policy.getFiltro().getRuoloPorta()==null ||
						RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta()) ||
						RuoloPolicy.APPLICATIVA.equals(policy.getFiltro().getRuoloPorta());
				if(showSAErogatore) {
					showSAErogatore = policy.getFiltro()==null || 
							policy.getFiltro().isEnabled()==false || 
							policy.getFiltro().getServizioApplicativoErogatore()==null;
				}
				
				if(showServizio || showAzione || showSAErogatore) {
					if(configurazione) {
//						de = new DataElement();
//						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_API);
//						de.setType(DataElementType.NOTE);
//						dati.addElement(de);
					}
				}
						
				// Servizio
				if( showServizio ){
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SERVIZIO);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SERVIZIO);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(policy.getGroupBy().isServizio());
					de.setValue(policy.getGroupBy().isServizio()+"");
					de.setPostBack_viaPOST(true);
					dati.addElement(de);
				}
			}
				
			// Azione
			if( showAzione ){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_AZIONE);
				if(serviceBinding!=null) {
					de.setLabel(getLabelAzione(serviceBinding));
				}
				else {
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_AZIONE);
				}
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(policy.getGroupBy().isAzione());
				de.setValue(policy.getGroupBy().isAzione()+"");
				dati.addElement(de);
			}
				
			// Servizio Applicativo Erogatore
			if(configurazione) {
				if( showSAErogatore	){
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_EROGATORE);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_EROGATORE);
					//de.setType(DataElementType.CHECKBOX);
					de.setType(DataElementType.HIDDEN);
					de.setSelected(policy.getGroupBy().isServizioApplicativoErogatore());
					de.setValue(policy.getGroupBy().isServizioApplicativoErogatore()+"");
					dati.addElement(de);
				}
			}
				

			
			// --- RICHIEDENTI ---
			
			if(configurazione) {
//				de = new DataElement();
//				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_MITTENTE);
//				de.setType(DataElementType.NOTE);
//				dati.addElement(de);
			}
			
			// Fruitore
			
			if(configurazione) {
				
				boolean showFruitore = policy.getFiltro()==null || 
						policy.getFiltro().isEnabled()==false || 
						policy.getFiltro().getTipoFruitore()==null ||
						policy.getFiltro().getNomeFruitore()==null;
				
				// Fruitore
				if( showFruitore ){
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_FRUITORE);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_FRUITORE);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(policy.getGroupBy().isFruitore());
					de.setValue(policy.getGroupBy().isFruitore()+"");
					dati.addElement(de);
				}
				
				
				boolean showRichiedenteApplicativo = policy.getFiltro()==null || 
						policy.getFiltro().isEnabled()==false || 
						policy.getFiltro().getRuoloPorta()==null ||
						policy.getFiltro().getServizioApplicativoFruitore()==null;
				
				// Applicativo Fruitore
				if( showRichiedenteApplicativo ){
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_FRUITORE);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_FRUITORE);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(policy.getGroupBy().isServizioApplicativoFruitore());
					de.setValue(policy.getGroupBy().isServizioApplicativoFruitore()+"");
					dati.addElement(de);
				}
				
			}
			else {
			
				// Richiedente API (Significa SoggettoMittente per le erogazioni, Applicativo e Identificativo Autenticato sia per le erogazioni che per le fruizioni)
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RICHIEDENTE);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RICHIEDENTE);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(policy.getGroupBy().isIdentificativoAutenticato()); // uso isIdentificativoAutenticato come informazione equivalente a isServizioApplicativoFruitore e isSoggettoFruitore
				de.setValue(policy.getGroupBy().isIdentificativoAutenticato()+"");
				dati.addElement(de);
			
			}
			

			// Token
			
			if(tokenAbilitato) {
			
				boolean first = this.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);
				String token = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN);
				
				String [] tokenSelezionatiDB = null;
				if(policy.getGroupBy().getToken()!=null && !"".equals(policy.getGroupBy().getToken())) {
					tokenSelezionatiDB = policy.getGroupBy().getToken().split(",");
				}
				String [] tokenSelezionatiSenzaIssuer = null;
				if(tokenSelezionatiDB!=null && tokenSelezionatiDB.length>0) {
					List<String> l = new ArrayList<>();
					for (int i = 0; i < tokenSelezionatiDB.length; i++) {
						TipoCredenzialeMittente tipo = TipoCredenzialeMittente.valueOf(tokenSelezionatiDB[i]);
						if(!TipoCredenzialeMittente.token_issuer.equals(tipo)) {
							l.add(tokenSelezionatiDB[i]);
						}
					}
					if(!l.isEmpty()) {
						tokenSelezionatiSenzaIssuer = l.toArray(new String[1]);
					}
				}
				boolean groupByToken = false;
				if(first) {
					groupByToken = (tokenSelezionatiDB!=null && tokenSelezionatiDB.length>0);
				}
				else {
					groupByToken = ServletUtils.isCheckBoxEnabled(token);
				}
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(groupByToken); // uso isIdentificativoAutenticato come informazione equivalente a isServizioApplicativoFruitore e isSoggettoFruitore
				de.setValue(groupByToken+"");
				de.setPostBack_viaPOST(true);
				dati.addElement(de);
				
				if(groupByToken) {
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN_CLAIMS);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN_CLAIMS);
					de.setValues(CostantiControlStation.TOKEN_VALUES_WITHOUT_ISSUER);
					de.setLabels(CostantiControlStation.LABEL_TOKEN_VALUES_WITHOUT_ISSUER);
					de.setSelezionati(tokenSelezionatiSenzaIssuer);
					de.setType(DataElementType.MULTI_SELECT);
					de.setRows(4); 
					de.setRequired(true);
					dati.addElement(de);
				}
				
			}
			
			if(groupByKey){
			
				// per chiave
				
				if(policy.getGroupBy().isInformazioneApplicativaEnabled()){
					de = new DataElement();
					de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_NOTE);
					de.setType(DataElementType.NOTE);
					dati.addElement(de);
				}
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED);
				if(policy.getGroupBy().isInformazioneApplicativaEnabled()){
					de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO);
				}
				else{
					//de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_LABEL);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_LABEL);
					//de.setLabelRight(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_NOTE);
				}
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(policy.getGroupBy().isInformazioneApplicativaEnabled());
				de.setValue(policy.getGroupBy().isInformazioneApplicativaEnabled()+"");
				de.setPostBack_viaPOST(true);
				dati.addElement(de);
				
				if(policy.getGroupBy().isInformazioneApplicativaEnabled()){
					
					TipoFiltroApplicativo tipoChiaveGroupBy = null;
					if(policy.getGroupBy().getInformazioneApplicativaTipo()!=null && !"".equals(policy.getGroupBy().getInformazioneApplicativaTipo())){
						tipoChiaveGroupBy = TipoFiltroApplicativo.toEnumConstant(policy.getGroupBy().getInformazioneApplicativaTipo());
					}
					if(tipoChiaveGroupBy==null){
						tipoChiaveGroupBy = TipoFiltroApplicativo.toEnumConstant(ConfigurazioneCostanti.CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_TIPO_DEFAULT);
					}
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_TIPO);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_TIPO);
					de.setValues(TipoFiltroApplicativo.toStringArray());
					de.setLabels(ConfigurazioneCostanti.LABEL_RATE_LIMITING_FILTRO_APPLICATIVO);
					de.setSelected(policy.getGroupBy().getInformazioneApplicativaTipo());
					de.setValue(policy.getGroupBy().getInformazioneApplicativaTipo());
					de.setType(DataElementType.SELECT);
					de.setPostBack_viaPOST(true);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME);
					de.setLabel(this.getLabelTipoInformazioneApplicativaGroupBy(policy.getGroupBy().getInformazioneApplicativaTipo()));
					de.setValue(policy.getGroupBy().getInformazioneApplicativaNome());
					if(tipoChiaveGroupBy==null || 
							TipoFiltroApplicativo.SOAPACTION_BASED.equals(tipoChiaveGroupBy)  || 
							TipoFiltroApplicativo.INDIRIZZO_IP.equals(tipoChiaveGroupBy) || 
							TipoFiltroApplicativo.INDIRIZZO_IP_FORWARDED.equals(tipoChiaveGroupBy)){
						de.setType(DataElementType.HIDDEN);
					}
					else if(TipoFiltroApplicativo.URLBASED.equals(tipoChiaveGroupBy) ||
							TipoFiltroApplicativo.CONTENT_BASED.equals(tipoChiaveGroupBy)) {
						de.setRequired(true);
						de.setType(DataElementType.TEXT_AREA);
					}
					else{
						de.setRequired(true);
						de.setType(DataElementType.TEXT_EDIT);
					}
					dati.addElement(de);
				}
				
			}
		}
	}
	
	private List<String> enrichListConLabelQualsiasi(List<String> l){
		List<String> newList = new ArrayList<String>();
		newList.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI);
		if(l!=null && l.size()>0){
			newList.addAll(l);
		}
		return newList;
	}
	private List<String> enrichListConValueQualsiasi(List<String> l){
		List<String> newList = new ArrayList<String>();
		newList.add(ConfigurazioneCostanti.VALUE_CONFIGURAZIONE_RATE_LIMITING_QUALSIASI);
		if(l!=null && l.size()>0){
			newList.addAll(l);
		}
		return newList;
	}
	
	public String getLabelTipoInformazioneApplicativaFiltro(String tipoInformazioneApplicativa){
		TipoFiltroApplicativo tipo = TipoFiltroApplicativo.toEnumConstant(ConfigurazioneCostanti.CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO_DEFAULT);
		if(tipoInformazioneApplicativa!=null){
			tipo = TipoFiltroApplicativo.toEnumConstant(tipoInformazioneApplicativa);
		}
		switch (tipo) {
		case HEADER_BASED:
			return ModalitaIdentificazione.HEADER_BASED.getLabelParametro();
		case FORM_BASED:
			return ModalitaIdentificazione.FORM_BASED.getLabelParametro();
		case CONTENT_BASED:
			return ModalitaIdentificazione.CONTENT_BASED.getLabelParametro();
		case URLBASED:
			return ModalitaIdentificazione.URL_BASED.getLabelParametro();
		case SOAPACTION_BASED:
			return ModalitaIdentificazione.SOAP_ACTION_BASED.getLabelParametro();
		case INDIRIZZO_IP:
			return ModalitaIdentificazione.INDIRIZZO_IP_BASED.getLabelParametro();
		case INDIRIZZO_IP_FORWARDED:
			return ModalitaIdentificazione.X_FORWARD_FOR_BASED.getLabelParametro();
		case PLUGIN_BASED:
			return ModalitaIdentificazione.PLUGIN_BASED.getLabelParametro();
		}
		return null;
	}
	
	public String getLabelTipoInformazioneApplicativaGroupBy(String tipoInformazioneApplicativa){
		TipoFiltroApplicativo tipo = TipoFiltroApplicativo.toEnumConstant(ConfigurazioneCostanti.CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_TIPO_DEFAULT);
		if(tipoInformazioneApplicativa!=null){
			tipo = TipoFiltroApplicativo.toEnumConstant(tipoInformazioneApplicativa);
		}
		switch (tipo) {
		case HEADER_BASED:
			return ModalitaIdentificazione.HEADER_BASED.getLabelParametro();
		case FORM_BASED:
			return ModalitaIdentificazione.FORM_BASED.getLabelParametro();
		case CONTENT_BASED:
			return ModalitaIdentificazione.CONTENT_BASED.getLabelParametro();
		case URLBASED:
			return ModalitaIdentificazione.URL_BASED.getLabelParametro();
		case SOAPACTION_BASED:
			return ModalitaIdentificazione.SOAP_ACTION_BASED.getLabelParametro();
		case INDIRIZZO_IP:
			return ModalitaIdentificazione.INDIRIZZO_IP_BASED.getLabelParametro();
		case INDIRIZZO_IP_FORWARDED:
			return ModalitaIdentificazione.X_FORWARD_FOR_BASED.getLabelParametro();
		case PLUGIN_BASED:
			return ModalitaIdentificazione.PLUGIN_BASED.getLabelParametro();
		}
		return null;
	}

	public boolean attivazionePolicyCheckData(TipoOperazione tipoOperazione, ConfigurazioneGenerale configurazioneControlloTraffico, 
			AttivazionePolicy policy, InfoPolicy infoPolicy, RuoloPolicy ruoloPorta, String nomePorta, ServiceBinding serviceBinding, String modalita) throws Exception { 
	
		boolean check = this.checkAttivazionePolicy(configurazioneControlloTraffico,policy,infoPolicy);
		if(!check) {
			return false;
		}
		
		StringBuilder existsMessage = new StringBuilder();
		
		boolean alreadyExists = ConfigurazioneUtilities.alreadyExists(tipoOperazione, this.confCore, this, 
				policy, infoPolicy, ruoloPorta, nomePorta, serviceBinding, 
				existsMessage, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE, modalita);
		
		if(alreadyExists) {
			this.pd.setMessage(existsMessage.toString());
			return false; 
		}
		
		return true;
	}
	
	public boolean checkAttivazionePolicy(ConfigurazioneGenerale c,AttivazionePolicy policy,InfoPolicy infoPolicy) throws Exception{
		
		// IdPolicy
		if(policy.getIdPolicy()==null || "".equals(policy.getIdPolicy()) || "-".equals(policy.getIdPolicy())){
			String messaggio = "Deve essere selezionata una policy in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID+"'";
			this.pd.setMessage(messaggio);
			return false;
		}
		
		if(policy.getAlias()!=null && !"".equals(policy.getAlias())) {
			if(this.checkLength255(policy.getAlias(), ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS)==false) {
				return false;
			}
			if(this.checkNCName(policy.getAlias(), ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS)==false) {
				return false;
			}
		}
		else {
			String messaggio = "Deve essere indicato un nome";
			this.pd.setMessage(messaggio);
			return false;
		}
		
		if(policy.isRidefinisci()){
		
			// Valori di Soglia
			if(infoPolicy!=null){
				TipoRisorsa tipoRisorsa = infoPolicy.getTipoRisorsa();
				String name = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_NUMERO_RICHIESTE;
				if(TipoRisorsa.OCCUPAZIONE_BANDA.equals(tipoRisorsa)){
					name = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_OCCUPAZIONE_DI_BANDA_LABEL;
				}
				else if(TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(tipoRisorsa)){
					name = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_TEMPO_MEDIO_LABEL;
				}
				else if(TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(tipoRisorsa)){
					name = ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_SOGLIA_VALORE_TEMPO_COMPLESSIVO_LABEL;
				}
				if(policy.getValore()==null){
					String messaggio = "Deve essere indicato un valore in '"+name+"'";
					this.pd.setMessage(messaggio);
					return false;
				}
				long l = policy.getValore();
				try{
					if(l<=0){
						throw new Exception("Valore non nell'intervallo");
					}
				}catch(Exception e){
					String messaggio = "Il valore ("+policy.getValore()+") indicato in '"+name+"' deve essere un numero intero maggiore di 0";
					this.pd.setMessage(messaggio);
					return false;
				}
				if(TipoRisorsa.NUMERO_RICHIESTE.equals(tipoRisorsa) && infoPolicy.isCheckRichiesteSimultanee()){
					if(c.getControlloTraffico().isControlloMaxThreadsEnabled()) {
						if(l > c.getControlloTraffico().getControlloMaxThreadsSoglia()){
							String messaggio = "Deve essere indicato un valore in '"+name+
									"' minore di quanto indicato nella configurazione generale alla voce '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_SOGLIA+"'";
							this.pd.setMessage(messaggio);
							return false;
						}
					}
				}
				
			}
			

		}
		
		if(policy.getFiltro().isEnabled()){
			
			if( (policy.getFiltro().getRuoloPorta()==null || RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta()))
					&& 
					policy.getFiltro().getProtocollo()==null &&
					policy.getFiltro().getRuoloFruitore()==null &&
					policy.getFiltro().getTipoFruitore()==null &&
					policy.getFiltro().getNomeFruitore()==null &&
					policy.getFiltro().getServizioApplicativoFruitore()==null &&
					policy.getFiltro().getRuoloErogatore()==null &&
					policy.getFiltro().getTipoErogatore()==null &&
					policy.getFiltro().getNomeErogatore()==null &&
					policy.getFiltro().getServizioApplicativoErogatore()==null &&
					policy.getFiltro().getTipoServizio()==null &&
					policy.getFiltro().getNomeServizio()==null &&
					policy.getFiltro().getAzione()==null &&
					policy.getFiltro().isInformazioneApplicativaEnabled()==false){
				String messaggio = "Se si abilita il filtro deve essere selezionato almeno un criterio";
				this.pd.setMessage(messaggio);
				return false;
			}
			
			if(policy.getFiltro().isInformazioneApplicativaEnabled()){
				
				TipoFiltroApplicativo tipo = TipoFiltroApplicativo.toEnumConstant(ConfigurazioneCostanti.CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO_DEFAULT);
				if(policy.getFiltro().getInformazioneApplicativaTipo()!=null){
					tipo = TipoFiltroApplicativo.toEnumConstant(policy.getFiltro().getInformazioneApplicativaTipo());
				}
				
				if(!TipoFiltroApplicativo.SOAPACTION_BASED.equals(tipo) &&
						!TipoFiltroApplicativo.INDIRIZZO_IP.equals(tipo) &&
						!TipoFiltroApplicativo.INDIRIZZO_IP_FORWARDED.equals(tipo)){
					if(policy.getFiltro().getInformazioneApplicativaNome()==null){
						String messaggio = "Deve essere indicato un valore in '"+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED
								+" - "+getLabelTipoInformazioneApplicativaFiltro(policy.getFiltro().getInformazioneApplicativaTipo())+"'";
						this.pd.setMessage(messaggio);
						return false;
					}
				}
							
				// Puo' essere anche una jsonPath
//				if(TipoFiltroApplicativo.CONTENT_BASED.equals(tipo)){
//					XPathExpressionEngine xPathEngine = new XPathExpressionEngine();
//					try{
//						xPathEngine.validate(policy.getFiltro().getInformazioneApplicativaNome());
//					}catch(XPathNotValidException notValidException){
//						String messaggio = "L'espressione fornita in '"+
//								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED
//								+" - "+getLabelTipoInformazioneApplicativaFiltro(policy.getFiltro().getInformazioneApplicativaTipo())+"' non è valida: "+
//								notValidException.getMessage();
//						this.pd.setMessage(messaggio);
//						return false;
//					}
//				}
				
				if(policy.getFiltro().getInformazioneApplicativaValore()==null){
					String messaggio = "Deve essere indicato un valore in '"+
							ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED+
							" - "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_VALORE+"'";
					this.pd.setMessage(messaggio);
					return false;
				}
				
			}
			
		}
		
		if(policy.getGroupBy().isEnabled()){
			
			// token
			String token = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN);
			if(ServletUtils.isCheckBoxEnabled(token)) {
				String [] tokenSelezionati = this.getParameterValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN_CLAIMS);
				if(tokenSelezionati==null || tokenSelezionati.length<=0) {
					String messaggio = "Se si abilita il raggruppamento per token deve essere selezionato almeno un claim";
					this.pd.setMessage(messaggio);
					return false;
				}
			}
			
			if( !policy.getGroupBy().isRuoloPorta() && 
					!policy.getGroupBy().getProtocollo() &&
					!policy.getGroupBy().getFruitore() &&
					!policy.getGroupBy().getServizioApplicativoFruitore() &&
					!policy.getGroupBy().getIdentificativoAutenticato() &&
					(policy.getGroupBy().getToken()==null || "".equals(policy.getGroupBy().getToken())) &&
					!policy.getGroupBy().getErogatore() &&
					!policy.getGroupBy().getServizioApplicativoErogatore() &&
					!policy.getGroupBy().getServizio() &&
					!policy.getGroupBy().getAzione() &&
					!policy.getGroupBy().isInformazioneApplicativaEnabled()){
				String messaggio = "Se si abilita il collezionamento dei dati deve essere selezionato almeno un criterio di raggruppamento";
				this.pd.setMessage(messaggio);
				return false;
			}
						
			if(policy.getGroupBy().isInformazioneApplicativaEnabled()){
				
				TipoFiltroApplicativo tipo = TipoFiltroApplicativo.toEnumConstant(ConfigurazioneCostanti.CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_TIPO_DEFAULT);
				if(policy.getGroupBy().getInformazioneApplicativaTipo()!=null){
					tipo = TipoFiltroApplicativo.toEnumConstant(policy.getGroupBy().getInformazioneApplicativaTipo());
				}
				
				if(!TipoFiltroApplicativo.SOAPACTION_BASED.equals(tipo) &&
						!TipoFiltroApplicativo.INDIRIZZO_IP.equals(tipo) &&
						!TipoFiltroApplicativo.INDIRIZZO_IP_FORWARDED.equals(tipo)){
				
					if(policy.getGroupBy().getInformazioneApplicativaNome()==null){
						String messaggio = "Deve essere indicato un valore in '"+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_NOTE
								+" - "+getLabelTipoInformazioneApplicativaGroupBy(policy.getGroupBy().getInformazioneApplicativaTipo())+"'";
						this.pd.setMessage(messaggio);
						return false;
					}
					
				}
				
				// Puo' essere anche una jsonPath
//				if(TipoFiltroApplicativo.CONTENT_BASED.equals(tipo)){
//					XPathExpressionEngine xPathEngine = new XPathExpressionEngine();
//					try{
//						xPathEngine.validate(policy.getGroupBy().getInformazioneApplicativaNome());
//					}catch(XPathNotValidException notValidException){
//						String messaggio = "L'espressione fornita in '"+
//								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_NOTE
//								+" - "+getLabelTipoInformazioneApplicativaGroupBy(policy.getGroupBy().getInformazioneApplicativaTipo())+"' non è valida: "+
//								notValidException.getMessage();
//						this.pd.setMessage(messaggio);
//						return false;
//					}
//				}
				
			}
			
		}
		return true;
	}
	
	public String toStringFilter(AttivazionePolicyFiltro filtro, RuoloPolicy ruoloPorta, String nomePorta) throws NotFoundException { 

		boolean delegata = false;
		boolean applicativa = false;
		boolean configurazione = false;
		if(ruoloPorta!=null) {
			if(RuoloPolicy.DELEGATA.equals(ruoloPorta)) {
				delegata = (nomePorta!=null);
			}
			else if(RuoloPolicy.APPLICATIVA.equals(ruoloPorta)) {
				applicativa = (nomePorta!=null);
			}
		}
		configurazione = !delegata && !applicativa;
		
		StringBuilder bf = new StringBuilder("Filtro");
		if(filtro.isEnabled()){

			bf.append(" abilitato con le seguenti impostazioni:");
			
			if(configurazione) {
				bf.append("<br/>");
				if( (filtro.getRuoloPorta()==null || RuoloPolicy.ENTRAMBI.equals(filtro.getRuoloPorta())) ){
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD+": Qualsiasi");
				}
				else{
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD+":"+filtro.getRuoloPorta().getValue());
				}
			}
			
			if(configurazione) {
				bf.append("<br/>");
				if( (filtro.getProtocollo()==null || "".equals(filtro.getProtocollo())) ){
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO+": Qualsiasi");
				}
				else{
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO+": "+filtro.getProtocollo());
				}
			}
			
			if(configurazione) {
				bf.append("<br/>");
				if(filtro.getRuoloErogatore()!=null) {
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_EROGATORE+": "+filtro.getRuoloErogatore());
				}
				else if( (filtro.getTipoErogatore()==null || "".equals(filtro.getTipoErogatore())) 
						||
						(filtro.getNomeErogatore()==null || "".equals(filtro.getNomeErogatore()))){
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_EROGATORE+": Qualsiasi");
				}
				else{
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_EROGATORE+": "+filtro.getTipoErogatore()+"/"+filtro.getNomeErogatore());
				}
			}
			
			if(configurazione) {
				bf.append("<br/>");
				if( (filtro.getTipoServizio()==null || "".equals(filtro.getTipoServizio())) 
						||
						(filtro.getNomeServizio()==null || "".equals(filtro.getNomeServizio()))){
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO+": Qualsiasi");
				}
				else{
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO+": "+filtro.getTipoServizio()+"/"+filtro.getNomeServizio());
				}
			}
			
			bf.append("<br/>");
			if( (filtro.getAzione()==null || "".equals(filtro.getAzione())) ){
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE+": Qualsiasi");
			}
			else{
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE+": "+filtro.getAzione());
			}
			
			if(configurazione) {
				bf.append("<br/>");
				if( (filtro.getServizioApplicativoErogatore()==null || "".equals(filtro.getServizioApplicativoErogatore())) ){
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE+": Qualsiasi");
				}
				else{
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE+": "+filtro.getServizioApplicativoErogatore());
				}
			}
			
			if(configurazione || applicativa) {
				bf.append("<br/>");
				if(filtro.getRuoloFruitore()!=null) {
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE+": "+filtro.getRuoloFruitore());
				}
				else if( (filtro.getTipoFruitore()==null || "".equals(filtro.getTipoFruitore())) 
						||
						(filtro.getNomeFruitore()==null || "".equals(filtro.getNomeFruitore()))){
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE+": Qualsiasi");
				}
				else{
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE+": "+filtro.getTipoFruitore()+"/"+filtro.getNomeFruitore());
				}
				
				if(configurazione) {
					bf.append("<br/>");
					if( (filtro.getServizioApplicativoFruitore()==null || "".equals(filtro.getServizioApplicativoFruitore())) ){
						bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE+": Qualsiasi");
					}
					else{
						bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE+": "+filtro.getServizioApplicativoFruitore());
					}
				}
			}
			else if(delegata) {
				bf.append("<br/>");
				if(filtro.getRuoloFruitore()!=null) {
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE+": "+filtro.getRuoloFruitore());
				}
				else if( (filtro.getServizioApplicativoFruitore()==null || "".equals(filtro.getServizioApplicativoFruitore())) ){
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE+": Qualsiasi");
				}
				else{
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE+": "+filtro.getServizioApplicativoFruitore());
				}
			}
			
			if(filtro.isInformazioneApplicativaEnabled()){
				
				bf.append("<br/>");
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED+": Abilitato");
				
				bf.append("<br/>");
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO+": "+filtro.getInformazioneApplicativaTipo());
				
				bf.append("<br/>");
				bf.append(getLabelTipoInformazioneApplicativaFiltro(filtro.getInformazioneApplicativaTipo())).append(": ").append(filtro.getInformazioneApplicativaNome());
				
				bf.append("<br/>");
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_VALORE+": "+filtro.getInformazioneApplicativaValore());
			}
			else{
				
				bf.append("<br/>");
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED+": Disabilitato");
				
			}
			
		}
		else{
			bf.append(": Disabilitato");
		}
		return bf.toString();
	}
	
	public String eseguiResetJmx(TipoOperazione tipoOperazione, RuoloPolicy ruoloPorta, String nomePorta) throws Exception{
		try{
			List<String> aliases = this.core.getJmxPdD_aliases();
			if(aliases==null || aliases.size()<=0){
				throw new Exception("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}
			for (String alias : aliases) {
				
				String resultReset = null;
				String idAllPolicy = null;
				try{
					if(nomePorta!=null && !"".equals(nomePorta)) {
						idAllPolicy = ConfigurazionePdD._getKey_ElencoIdPolicyAttiveAPI(RuoloPolicy.DELEGATA.equals(ruoloPorta)?TipoPdD.DELEGATA : TipoPdD.APPLICATIVA, nomePorta);
					}
					else {
						idAllPolicy = ConfigurazionePdD._getKey_ElencoIdPolicyAttiveGlobali();
					}
					resultReset = this.core.invokeJMXMethod(this.core.getGestoreRisorseJMX(alias),alias,JMXConstants.JMX_TYPE, CostantiPdD.JMX_CONFIGURAZIONE_PDD, JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT, idAllPolicy);
					this.log.debug("reset["+idAllPolicy+"] "+resultReset);
				}catch(Exception e){
					String errorMessage = "Errore durante l'invocazione dell'operazione ["+JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT+"] sulla risorsa ["+
							CostantiPdD.JMX_CONFIGURAZIONE_PDD+"] (param:"+idAllPolicy+"): "+e.getMessage();
					this.log.error(errorMessage,e);
					resultReset = errorMessage;
				}
				
				boolean resetSinglePolicy = false; // le policy attive contengono anche la data quindi non e' possibile resettarle singolarmente.
				if(resetSinglePolicy) {
					resultReset = null;
					String idPolicy = null;
					String tmpIdPolicy = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID);
					try{
						idPolicy = ConfigurazionePdD._getKey_AttivazionePolicy(tmpIdPolicy);
						resultReset = this.core.invokeJMXMethod(this.core.getGestoreRisorseJMX(alias),alias,JMXConstants.JMX_TYPE, CostantiPdD.JMX_CONFIGURAZIONE_PDD, JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT, idPolicy);
						this.log.debug("reset["+idPolicy+"] "+resultReset);
					}catch(Exception e){
						String errorMessage = "Errore durante l'invocazione dell'operazione ["+JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT+"] sulla risorsa ["+
								CostantiPdD.JMX_CONFIGURAZIONE_PDD+"] (param:"+idAllPolicy+"): "+e.getMessage();
						this.log.error(errorMessage,e);
						resultReset = errorMessage;
					}
				}
				
			}
		}catch(Exception e){
			this.log.error("ResetCache:"+e.getMessage(), e);
		}
		
		return ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_MODIFICATA_CON_SUCCESSO_SENZA_RIAVVIO_RICHIESTO;
	}

	public void prepareGestorePolicyTokenList(Search ricerca, List<GenericProperties> lista, int idLista) throws Exception{
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN);

			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			
			PropertiesSourceConfiguration propertiesSourceConfiguration = this.confCore.getPolicyGestioneTokenPropertiesSourceConfiguration();
			ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
			configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);
			List<String> nomiConfigurazioniPolicyGestioneToken = configManager.getNomiConfigurazioni(propertiesSourceConfiguration);
			List<String> labelConfigurazioniPolicyGestioneToken = configManager.convertToLabel(propertiesSourceConfiguration, nomiConfigurazioniPolicyGestioneToken);
			
			String filterTipoTokenPolicy = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_TOKEN_POLICY);
			addFilterTipoTokenPolicy(filterTipoTokenPolicy, false, nomiConfigurazioniPolicyGestioneToken, labelConfigurazioniPolicyGestioneToken);
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, null));
			
			this.pd.setSearchLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
			}else{
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);
			
			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME, search);
			}
			
			List<String> lstLabels = new ArrayList<>();
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME);
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_DESCRIZIONE);
			if(!this.core.isTokenPolicyForceIdEnabled()) {
				lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO);
			}
			lstLabels.add(CostantiControlStation.LABEL_IN_USO_COLONNA_HEADER); // inuso
			
			// setto le label delle colonne
			String[] labels = lstLabels.toArray(new String[lstLabels.size()]);
			
			this.pd.setLabels(labels);
		
			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					Vector<DataElement> e = new Vector<DataElement>();
					GenericProperties policy = lista.get(i);
					
					Parameter pPolicyId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_ID, policy.getId() + ""); 

					DataElement de = new DataElement();
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_CHANGE, pPolicyId);
					de.setValue(policy.getNome());
					de.setIdToRemove(""+policy.getId());
					e.addElement(de);
					
					de = new DataElement();
					de.setValue(policy.getDescrizione());
					e.addElement(de);
					
					if(!this.core.isTokenPolicyForceIdEnabled()) {
						de = new DataElement();
						if(nomiConfigurazioniPolicyGestioneToken!=null && nomiConfigurazioniPolicyGestioneToken.contains(policy.getTipo())) {
							boolean found = false;
							for (int j = 0; j < nomiConfigurazioniPolicyGestioneToken.size(); j++) {
								String nome = nomiConfigurazioniPolicyGestioneToken.get(j);
								if(nome.equals(policy.getTipo())) {
									de.setValue(labelConfigurazioniPolicyGestioneToken.get(j));
									found = true;
									break;
								}
							}
							if(!found) {
								de.setValue(policy.getTipo());
							}
						}
						else {
							de.setValue(policy.getTipo());
						}
						e.addElement(de);
					}
					
					this.addInUsoButtonVisualizzazioneClassica(e, policy.getNome(), policy.getId()+"", InUsoType.TOKEN_POLICY);
					
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

	public Vector<DataElement> addPolicyGestioneTokenToDati(TipoOperazione tipoOperazione, Vector<DataElement> dati, String id, String nome, String descrizione, String tipo, String[] propConfigPolicyGestioneTokenLabelList, String[] propConfigPolicyGestioneTokenList) throws Exception {
		
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// id
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_ID);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_ID);
		de.setType(DataElementType.HIDDEN);
		de.setValue(id);
		dati.addElement(de);

		// tipo
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO);
		if(!this.core.isTokenPolicyForceIdEnabled()) {
			if(tipoOperazione.equals(TipoOperazione.ADD)) {
				de.setType(DataElementType.SELECT);
				de.setPostBack(true);
				de.setValues(propConfigPolicyGestioneTokenList);
				de.setLabels(propConfigPolicyGestioneTokenLabelList);
				de.setSelected(tipo); 
				de.setRequired(true);
			}else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(tipo);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO+"__LABEL");
				de.setType(DataElementType.TEXT);
				if(propConfigPolicyGestioneTokenList!=null && propConfigPolicyGestioneTokenList.length>0) {
					boolean found = false;
					for (int j = 0; j < propConfigPolicyGestioneTokenList.length; j++) {
						String nomeP = propConfigPolicyGestioneTokenList[j];
						if(nomeP.equals(tipo)) {
							de.setValue(propConfigPolicyGestioneTokenLabelList[j]);
							found = true;
							break;
						}
					}
					if(!found) {
						de.setValue(tipo);
					}
				}
				else {
					de.setValue(tipo);
				}
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(tipo);
		}
		dati.addElement(de);
		
		// nome
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME);
		if(tipoOperazione.equals(TipoOperazione.ADD)) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		} else {
			de.setType(DataElementType.TEXT);
		}
		de.setValue(nome);
		dati.addElement(de);
		
		// descrizione
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_DESCRIZIONE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_DESCRIZIONE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(descrizione);
		dati.addElement(de);

		
		return dati;
	}

	public boolean policyGestioneTokenCheckData(TipoOperazione tipoOperazione, String nome, String descrizione,	String tipo,String tipologia) throws Exception {
		
		if(tipoOperazione.equals(TipoOperazione.ADD)) {
			// Nome
			if(StringUtils.isEmpty(nome)){
				String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME+"'";
				this.pd.setMessage(messaggio);
				return false;
			}
			if(nome.contains(" ")){
				String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME+"' senza spazi";
				this.pd.setMessage(messaggio);
				return false;
			}
			
			// Tipo
			if(StringUtils.isEmpty(tipo)  || CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(tipo)){
				String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO+"'";
				this.pd.setMessage(messaggio);
				return false;
			}
		
			if(this.checkLength255(nome, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_NOME)==false) {
				return false;
			}
			if(descrizione!=null && !"".equals(descrizione)) {
				if(this.checkLength255(descrizione, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_DESCRIZIONE)==false) {
					return false;
				}
			}
			
			try {
				// check duplicati per tipologia
				this.confCore.getGenericProperties(nome, tipologia);
				String messaggio = "&Egrave; gi&agrave; presente un Policy, del tipo indicato, con nome " + nome ;
				this.pd.setMessage(messaggio);
				return false;
			} catch(DriverConfigurazioneNotFound e) {
				// ok
			} catch(Exception e) {
				throw e;
			}
		}
		return true;
	}
	
	public String replaceToHtmlSeparator(String value) {
		return this.replaceSeparator(value, "\n", "<BR/>");
	}
	public String replaceSeparator(String value, String originale,String destinazione) {
		if(value!=null) {
			while(value.contains(originale)) {
				value = value.replace(originale, destinazione);
			}
		}
		return value;
	}
	
	// Prepara la lista delle regole di configurazione proxy pass
	public void prepareProxyPassConfigurazioneRegolaList(ISearch ricerca, List<ConfigurazioneUrlInvocazioneRegola> lista) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA);

			int idLista = Liste.CONFIGURAZIONE_PROXY_PASS_REGOLA;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			
			
			this.pd.setSearchLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROXY_PASS_REGOLE, null));
			}else{
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROXY_PASS_REGOLE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);
			
			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME, search);
			}

			// setto le label delle colonne	
			List<String> lstLabels = new ArrayList<>();
			if(lista != null && lista.size() > 1)
				lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_POSIZIONE);
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_STATO);
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME);
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_DESCRIZIONE);
			this.pd.setLabels(lstLabels.toArray(new String [lstLabels.size()]));

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<ConfigurazioneUrlInvocazioneRegola> it = lista.iterator();
				int numeroElementi = lista.size();
				int i = 0;
				while (it.hasNext()) {
					ConfigurazioneUrlInvocazioneRegola regola = it.next();

					Vector<DataElement> e = new Vector<DataElement>();
					Parameter pIdRegola = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_ID_REGOLA, regola.getId() + "");
					
					// Posizione
					if(lista.size() > 1) {
						DataElement de = new DataElement();
						de.setWidthPx(48);
						de.setType(DataElementType.IMAGE);
						DataElementImage imageUp = new DataElementImage();
						Parameter pDirezioneSu = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_POSIZIONE, 
								CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU);
						Parameter pDirezioneGiu = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_POSIZIONE, 
								CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_GIU);
								
						if(i > 0) {
							imageUp.setImage(CostantiControlStation.ICONA_FRECCIA_SU);
							imageUp.setToolTip(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SPOSTA_SU);
							imageUp.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA_LIST, pIdRegola, pDirezioneSu); 
						}
						else {
							imageUp.setImage(CostantiControlStation.ICONA_PLACEHOLDER);
						}
						de.addImage(imageUp);
						
						if(i < numeroElementi -1) {
							DataElementImage imageDown = new DataElementImage();
							imageDown.setImage(CostantiControlStation.ICONA_FRECCIA_GIU);
							imageDown.setToolTip(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SPOSTA_GIU);
							imageDown.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA_LIST, pIdRegola, pDirezioneGiu);
							de.addImage(imageDown);
						}
						de.setValue(regola.getPosizione()+"");
						e.addElement(de);
					}
					
					// Stato
					DataElement de = new DataElement();
					de.setWidthPx(10);
					de.setType(DataElementType.CHECKBOX);
					if(regola.getStato()==null // backward compatibility 
							||
							StatoFunzionalita.ABILITATO.equals(regola.getStato())){
						de.setToolTip(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
						de.setSelected(CheckboxStatusType.CONFIG_ENABLE);
					}
					else{
						de.setToolTip(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
						de.setSelected(CheckboxStatusType.CONFIG_DISABLE);
					}
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA_CHANGE, pIdRegola);
					e.addElement(de);
					
					
					// Nome
					de = new DataElement();
					de.setIdToRemove(regola.getId() + "");
					de.setValue(regola.getNome());
					de.setToolTip(regola.getNome());
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA_CHANGE, pIdRegola);
					e.addElement(de);
					
					de = new DataElement();
					if(regola.getDescrizione() != null && regola.getDescrizione().length() > 100) {
						de.setValue(regola.getDescrizione().substring(0, 97)+"...");
						de.setToolTip(regola.getDescrizione());
					} else {
						de.setValue(regola.getDescrizione());
					}
					
					e.addElement(de);

					dati.addElement(e);
					i++;
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	// Controlla i dati del registro
	public boolean proxyPassConfigurazioneRegolaCheckData(TipoOperazione tipoOp, String oldNome) throws Exception {

		try{
			
			String nome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME);
			
			if(StringUtils.isEmpty(nome)){
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME));
				return false;
			}
			
			if(!this.checkLength(nome, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME, 1, 255)) {
				return false;
			}
			
			String stato = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_STATO);
			
			if(StringUtils.isEmpty(stato)){
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_STATO));
				return false;
			} else {
				if(!stato.equals(StatoFunzionalita.ABILITATO.getValue()) && !stato.equals(StatoFunzionalita.DISABILITATO.getValue())) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_NON_VALIDO,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_STATO));
					return false;
				}
			}
			
			String regolaText = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT);
			
			if(StringUtils.isEmpty(regolaText)){
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT));
				return false;
			}
			
			if(!this.checkLength(regolaText, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT, 1, 255)) {
				return false;
			}
			
			String contestoEsterno = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO);
			
			/*
			if(StringUtils.isEmpty(contestoEsterno)){
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO));
				return false;
			}
			*/
			
			if(!StringUtils.isEmpty(contestoEsterno)){
				if(contestoEsterno.contains(" ")) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO));   
					return false;
				}
				
				if(!this.checkLength(contestoEsterno, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO, 1, 255)) {
					return false;
				}
			}
			
			String baseUrl = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL);
			
			if(!StringUtils.isEmpty(baseUrl)){
				if(baseUrl.contains(" ")) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL));   
					return false;
				}
				
				if(!this.checkLength(baseUrl, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL, 1, 255)) {
					return false;
				}
				
				try{
					org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(baseUrl);
				}catch(Exception e){
					this.pd.setMessage(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL + " non correttamente formata: "+e.getMessage());
					return false;
				}
			}
			
//			String protocollo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_PROFILO);
//			
//			if(!StringUtils.isEmpty(protocollo)){
//				
//				// valida protocollo
//				
//				String soggetto = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SOGGETTO);
//				
//				if(!StringUtils.isEmpty(soggetto)){
//					// valida id soggetto
//				}
//			}
			
			String ruolo = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_RUOLO);
			if(!StringUtils.isEmpty(ruolo)){
				if(!ruolo.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_EROGAZIONE) && !ruolo.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_FRUIZIONE)){
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_NON_VALIDO,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_RUOLO));
					return false;
				}
			}
			String serviceBinding = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SERVICE_BINDING);
			
			if(!StringUtils.isEmpty(serviceBinding)){
				if(!serviceBinding.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP) && !serviceBinding.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST)) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_NON_VALIDO,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SERVICE_BINDING));
					return false;
				}
			}
			

			// Se tipoOp = add, controllo che la regola non sia gia' stata registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = this.confCore.existsProxyPassConfigurazioneRegola(nome);

				if (giaRegistrato) {
					this.pd.setMessage("&Egrave; gi&agrave; presente una Regola di Proxy Pass con questo nome.");
					return false;
				}
			} else { // change controllo che se ho modificato il nome, il nuovo nome non sia comunque utilizzato
				if(!oldNome.equals(nome)) {
					boolean giaRegistrato = this.confCore.existsProxyPassConfigurazioneRegola(nome);

					if (giaRegistrato) {
						this.pd.setMessage("&Egrave; gi&agrave; presente una Regola di Proxy Pass con questo nome.");
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
	
	public CanaliConfigurazione getGestioneCanali(boolean canaliEnabled, String canaliDefault, List<CanaleConfigurazione> canaleList, String canaliNome, 
			String canaliDescrizione, List<CanaleConfigurazioneNodo> nodoList) {
		CanaliConfigurazione configurazione = new CanaliConfigurazione();
		
		if(canaliEnabled) {
			configurazione.setStato(StatoFunzionalita.ABILITATO);
			
			if(canaleList == null)
				canaleList = new ArrayList<>();
			
			if(canaliDefault != null) {
				// viene modificato solo il canale default
				for (CanaleConfigurazione canaleConfigurazione : canaleList) {
					canaleConfigurazione.setCanaleDefault(false);
				}
				
				for (CanaleConfigurazione canaleConfigurazione : canaleList) {
					if(canaleConfigurazione.getNome().equals(canaliDefault)) {
						canaleConfigurazione.setCanaleDefault(true);
						break;
					}
				}
			} else {
				// creo il canale default
				
				CanaleConfigurazione canaleConfigurazione = new CanaleConfigurazione();
				canaleConfigurazione.setNome(canaliNome);
				canaleConfigurazione.setDescrizione(canaliDescrizione);
				canaleConfigurazione.setCanaleDefault(true);
				
				canaleList.add(canaleConfigurazione);
			}
			
			configurazione.setCanaleList(canaleList);
			
			if(nodoList == null)
				nodoList = new ArrayList<>();
			
			configurazione.setNodoList(nodoList);
		} else {
			configurazione.setStato(StatoFunzionalita.DISABILITATO);
		}
		
		return configurazione;
	}
	public void prepareCanaleConfigurazioneList(Search ricerca, List<CanaleConfigurazione> lista) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CANALI);

			int idLista = Liste.CONFIGURAZIONE_CANALI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			
			
			this.pd.setSearchLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALI, null));
			}else{
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALI, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CANALI_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);
			
			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NOME, search);
			}

			// setto le label delle colonne	
			List<String> lstLabels = new ArrayList<>();
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NOME);
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_DESCRIZIONE);
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_DEFAULT);
			lstLabels.add(CostantiControlStation.LABEL_IN_USO_COLONNA_HEADER); // inuso
			this.pd.setLabels(lstLabels.toArray(new String [lstLabels.size()]));

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<CanaleConfigurazione> it = lista.iterator();
				while (it.hasNext()) {
					CanaleConfigurazione regola = it.next();

					Vector<DataElement> e = new Vector<DataElement>();
					Parameter pIdCanale = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_ID_CANALE, regola.getId() + "");
					
					// Nome
					DataElement de = new DataElement();
					de.setIdToRemove(regola.getId() + "");
					de.setValue(regola.getNome());
					de.setToolTip(regola.getNome());
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CANALI_CHANGE, pIdCanale);
					e.addElement(de);
					
					de = new DataElement();
					if(regola.getDescrizione() != null && regola.getDescrizione().length() > 100) {
						de.setValue(regola.getDescrizione().substring(0, 97)+"...");
						de.setToolTip(regola.getDescrizione());
					} else {
						de.setValue(regola.getDescrizione());
					}
					
					e.addElement(de);
					
					// Default
					de = new DataElement();
					de.setWidthPx(10);
					if(regola.isCanaleDefault()){
						de.setValue("Si");
					}
					else{
						de.setValue("No");
					}
					e.addElement(de);
					
					
					this.addInUsoButtonVisualizzazioneClassica(e, regola.getNome(), regola.getNome(), InUsoType.CANALE);

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
	public Vector<DataElement> addCanaleToDati(TipoOperazione tipoOp, Vector<DataElement> dati, String idCanaleS,
			String nome, String descrizione) {
		
		DataElement dataElement = new DataElement();
		dataElement.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALE);
		dataElement.setType(DataElementType.TITLE);
		dati.add(dataElement);
		
		if(tipoOp.equals(TipoOperazione.CHANGE)) {
			// old id canale
			DataElement de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_ID_CANALE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(idCanaleS);
			dati.add(de);
		}
		
		// nome
		DataElement de = new DataElement();
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NOME);
		de.setValue(nome);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NOME);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		// descrizione
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_AREA);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_DESCRIZIONE);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		return dati;
	}
	
	public boolean canaleCheckData(TipoOperazione tipoOp, String oldNome) throws Exception {
		try{
			String canaliNome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NOME);
			String canaliDescrizione = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_DESCRIZIONE);
			
			if(canaleDatiCheckData(canaliNome, canaliDescrizione) == false) {
				return false;
			}
			
			boolean existsCanale = this.confCore.existsCanale(canaliNome);
			
			if(tipoOp.equals(TipoOperazione.ADD)) {
				if(existsCanale) { // nome gia' occupato
					this.pd.setMessage(ConfigurazioneCostanti.MESSAGGIO_CANALE_GIA_PRESENTE);
					return false;
				}
			} else {
				// se ho cambiato il nome ed e' gia' scelto allora devo segnalare l'errore.
				if(!oldNome.equals(canaliNome) && existsCanale) {
					this.pd.setMessage(ConfigurazioneCostanti.MESSAGGIO_CANALE_NUOVO_NOME_GIA_PRESENTE);
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	public void prepareCanaleNodoConfigurazioneList(Search ricerca, List<CanaleConfigurazioneNodo> lista) throws Exception { 
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CANALI_NODI);

			int idLista = Liste.CONFIGURAZIONE_CANALI_NODI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			
			
			this.pd.setSearchLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALI_NODI, null));
			}else{
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALI_NODI, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CANALI_NODI_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);
			 
			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME, search);
			}

			// setto le label delle colonne	
			List<String> lstLabels = new ArrayList<>();
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME);
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_DESCRIZIONE);
			lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_CANALI);
			this.pd.setLabels(lstLabels.toArray(new String [lstLabels.size()]));

			// preparo i dati  
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<CanaleConfigurazioneNodo> it = lista.iterator();
				while (it.hasNext()) {
					CanaleConfigurazioneNodo regola = it.next();

					Vector<DataElement> e = new Vector<DataElement>();
					Parameter pIdCanaleNodo = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_ID_NODO, regola.getId() + "");
					
					// Nome
					DataElement de = new DataElement();
					de.setIdToRemove(regola.getId() + "");
					de.setValue(regola.getNome());
					de.setToolTip(regola.getNome());
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CANALI_NODI_CHANGE, pIdCanaleNodo);
					e.addElement(de);
					
					// descrizione
					de = new DataElement();
					if(regola.getDescrizione() != null && regola.getDescrizione().length() > 100) {
						de.setValue(regola.getDescrizione().substring(0, 97)+"...");
						de.setToolTip(regola.getDescrizione());
					} else {
						de.setValue(regola.getDescrizione());
					}
					
					e.addElement(de);
					
					// Canali
					de = new DataElement();
					List<String> canaleList = regola.getCanaleList();
					if(canaleList == null)
						canaleList = new ArrayList<String>();
					
					String labelTooltip = StringUtils.join(canaleList.toArray(new String[canaleList.size()]), ", ");
					
					if(labelTooltip.length() > 100) {
						de.setValue(labelTooltip.substring(0, 97)+"...");
						de.setToolTip(labelTooltip);
					} else {
						de.setValue(labelTooltip);
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
	public Vector<DataElement> addCanaleNodoToDati(TipoOperazione tipoOp, Vector<DataElement> dati, String idNodoS, String oldNome,
			String nome, String descrizione, String[] canali, List<CanaleConfigurazione> canaleList,
			boolean selectListNode, List<String> aliasesNodi) {
		
		DataElement dataElement = new DataElement();
		dataElement.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALI_NODO);
		dataElement.setType(DataElementType.TITLE);
		dati.add(dataElement);
					
		if(tipoOp.equals(TipoOperazione.CHANGE)) {
			// old id canale
			DataElement de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_ID_NODO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(idNodoS);
			dati.add(de);
			
			if(selectListNode && aliasesNodi.contains(oldNome) && aliasesNodi.size()>1 && !oldNome.equals(nome)) {
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_OLD_NOME);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_OLD_NOME);
				de.setType(DataElementType.TEXT);
				de.setValue(oldNome);
				dati.add(de);
			}
		}
		
		// nome
		if(selectListNode) {
			DataElement de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME);
			de.setValues(aliasesNodi);
			de.setLabels(aliasesNodi);
			de.setSelected(nome);
			de.setRequired(true);
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME);
			if(tipoOp.equals(TipoOperazione.CHANGE)) {
				de.setPostBack(true);
			}
			dati.addElement(de);
		}
		else {
			DataElement de = new DataElement();
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME);
			de.setValue(nome);
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME);
			de.setSize(this.getSize());
			dati.addElement(de);
		}
		
		// descrizione
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_AREA);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_DESCRIZIONE);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		// canali
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_CANALI);
		List<String> canaliListValues = canaleList.stream().map(CanaleConfigurazione::getNome).collect(Collectors.toList());
		de.setValues(canaliListValues);
		de.setLabels(canaliListValues);
		de.setSelezionati(canali);
		de.setRequired(true);
		de.setType(DataElementType.MULTI_SELECT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_CANALI);
		de.setRows(10);
		dati.addElement(de);
		
		return dati;
	}
	public boolean canaleNodoCheckData(TipoOperazione tipoOp, String oldNome) throws Exception { 
		try{
			String canaliNome = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME);
			String canaliDescrizione = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_DESCRIZIONE);
			String [] canali = this.getParameterValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_CANALI);
			
			// nome obbligatorio
			if(StringUtils.isEmpty(canaliNome)){
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,
						ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME));
				return false;
			}
			
			if(this.checkSpazi(canaliNome, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME)==false) {
				return false;
			}
			
			if(this.checkLength255(canaliNome, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME)==false) {
				return false;
			}
						
			if(this.checkLength255(canaliDescrizione, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_DESCRIZIONE)==false) {
				return false;
			}
			
			if(canali == null || canali.length == 0) {
				this.pd.setMessage(MessageFormat.format(ConfigurazioneCostanti.MESSAGGIO_CANALE_NODO_CAMPO_CANALE_OBBLIGATORIO,
						ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_NODI_CANALI));
				return false;
			}
			
			boolean existsCanale = this.confCore.existsCanaleNodo(canaliNome);
			
			if(tipoOp.equals(TipoOperazione.ADD)) {
				if(existsCanale) { // nome gia' occupato
					this.pd.setMessage(ConfigurazioneCostanti.MESSAGGIO_CANALE_NODO_GIA_PRESENTE);
					return false;
				}
			} else {
				// se ho cambiato il nome ed e' gia' scelto allora devo segnalare l'errore.
				if(!oldNome.equals(canaliNome) && existsCanale) {
					this.pd.setMessage(ConfigurazioneCostanti.MESSAGGIO_CANALE_NODO_NUOVO_NOME_GIA_PRESENTE);
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
