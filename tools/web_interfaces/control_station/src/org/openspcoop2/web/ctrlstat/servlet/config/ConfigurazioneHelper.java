/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneProtocolli;
import org.openspcoop2.core.config.ConfigurazioneProtocollo;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
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
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.pdd.config.ConfigurazionePdD;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.InformazioniProtocollo;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathNotValidException;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.CheckboxStatusType;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
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

	//	private ConnettoriHelper connettoriHelper = null;
	public ConfigurazioneHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
		//		this.connettoriHelper = new ConnettoriHelper(request, pd, session);
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

	public void setDataElementCache(Vector<DataElement> dati, String intestazioneSezione,
			String nomeParametroStatoCache, String statocache,
			String nomeParametroDimensioneCache, String dimensionecache,
			String nomeParametroAlgoritmoCache, String algoritmocache,
			String nomeParametroIdleCache, String idlecache,
			String nomeParametroLifeCache, String lifecache){
		
		boolean view = this.isModalitaAvanzata();
		
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
		if(view){
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
			de.setValue(lifecache);
			if(view){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
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
			if(view){
				de.setType(DataElementType.TEXT_EDIT);
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
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_REGISTRY,lifecache);

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
			
			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && (lifecache==null || lifecache.equals("")) ) {
				this.pd.setMessage("Deve essere indicato un valore per l'impostazione 'Life second' della Cache "+nomeCache);
				return false;
			}
			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO) && !lifecache.equals("") && 
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
			
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
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
			String inoltromin, String stato,
			String controllo, String severita, String severita_log4j,
			String integman, String nomeintegman, String profcoll,
			String connessione, String utilizzo, String validman,
			String gestman, String registrazioneTracce, String dumpPD, String dumpPA,
			String xsd,	String tipoValidazione, String confPers, Configurazione configurazione,
			Vector<DataElement> dati, String applicaMTOM, ConfigurazioneProtocolli configProtocolli) throws Exception {
		DataElement de = new DataElement();

		if (this.isModalitaStandard()) {
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

		if (!this.isModalitaStandard()) {
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
		if (this.isModalitaStandard()) {
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
		if (this.isModalitaStandard()) {
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

		if (this.isModalitaStandard()) {
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

		if (!this.isModalitaStandard()) {
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
		if (this.isModalitaStandard()) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.SELECT);
			de.setValues(tipoGM);
			de.setSelected(gestman);
		}
		de.setValue(gestman);
		dati.addElement(de);
		
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_URL_INVOCAZIONE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		ProtocolFactoryManager pManager = ProtocolFactoryManager.getInstance();
		MapReader<String, IProtocolFactory<?>> mapPFactory = pManager.getProtocolFactories();
		Enumeration<String> protocolName = mapPFactory.keys();
		while (protocolName.hasMoreElements()) {
			String protocollo = (String) protocolName.nextElement();
			IProtocolFactory<?> pFactory = mapPFactory.get(protocollo);
			String context = "";
			if(pFactory.getManifest().getWeb().sizeContextList()>0) {
				context = pFactory.getManifest().getWeb().getContext(0).getName();
			}
			
			InformazioniProtocollo infoProt = pFactory.getInformazioniProtocol();
			
			ConfigurazioneProtocollo configProtocollo = null;
			if(configProtocolli!=null) {
				for (ConfigurazioneProtocollo check : configProtocolli.getProtocolloList()) {
					if(check.getNome().equals(protocollo)) {
						configProtocollo = check;
						break;
					}
				}
			}
			
			User user = ServletUtils.getUserFromSession(this.session);
			boolean multiTenant = user.isPermitMultiTenant();
			String userLogin = user.getLogin();
			
			String nameP = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_NAME+protocollo);
			String urlInvocazionePD = null;
			String urlInvocazionePA = null;
			if(nameP!=null) {
				urlInvocazionePD = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD+protocollo);
				urlInvocazionePA = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA+protocollo);
			}
			if(urlInvocazionePD==null) {
				if(configProtocollo!=null) {
					urlInvocazionePD = configProtocollo.getUrlInvocazioneServizioPD();
				}
			}
			if(urlInvocazionePA==null) {
				if(configProtocollo!=null) {
					urlInvocazionePA = configProtocollo.getUrlInvocazioneServizioPA();
				}
			}
			if(urlInvocazionePD==null) {
				urlInvocazionePD = ConfigurazioneCostanti.getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePd(context);
			}
			if(urlInvocazionePA==null) {
				urlInvocazionePA = ConfigurazioneCostanti.getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePa(context);
			}
			
			if(mapPFactory.size()>1) {
				de = new DataElement();
				de.setLabel(infoProt.getLabel());
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_NAME);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_NAME+protocollo);
			de.setType(DataElementType.HIDDEN);
			de.setValue(protocollo);
			dati.addElement(de);
									
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA+protocollo);
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(urlInvocazionePA);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD+protocollo);
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(urlInvocazionePD);
			dati.addElement(de);
					
			if(!multiTenant) {
				IDSoggetto idSoggetto = this.soggettiCore.getSoggettoOperativo(userLogin, protocollo);
				long idSoggettoLong = this.soggettiCore.getIdSoggetto(idSoggetto.getNome(), idSoggetto.getTipo());
						
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_SOGGETTO);
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_SOGGETTO+protocollo);
				de.setValue(this.getLabelNomeSoggetto(protocollo, idSoggetto.getTipo(), idSoggetto.getNome()));
				de.setType(DataElementType.TEXT);
				dati.addElement(de);
				
				de = new DataElement();
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_SOGGETTO_VISUALIZZA_DATI);
				de.setType(DataElementType.LINK);
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,idSoggettoLong+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,idSoggetto.getNome()),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,idSoggetto.getTipo()));
				dati.addElement(de);
			}
		}
		
		if (!this.isModalitaStandard()) {
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
		if(this.core.isShowConfigurazioneTracciamentoDiagnostica()){
			de.setType(DataElementType.SELECT);
			de.setValues(tipoDump);
			de.setSelected(dumpApplicativo);
			de.setPostBack(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(dumpApplicativo);
		}
		dati.addElement(de);
		
		if(this.core.isShowConfigurazioneTracciamentoDiagnostica() && dumpApplicativo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)) {
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
				(this.core.isShowConfigurazioneTracciamentoDiagnostica() && this.isModalitaCompleta()) 
				||
				(this.isModalitaAvanzata() && (this.confCore.isTracce_showConfigurazioneCustomAppender() || this.confCore.isTracce_showSorgentiDatiDatabase()));
				
		if(showTitleSection) {
			if(this.core.isShowConfigurazioneTracciamentoDiagnostica() || this.isModalitaAvanzata()){
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
		if(this.core.isShowConfigurazioneTracciamentoDiagnostica() && this.isModalitaCompleta()){
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
		DataElement de;
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MESSAGGI_DIAGNOSTICI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		//					String[] tipoMsg = { "off", "fatalOpenspcoop", "errorSpcoop", "errorOpenspcoop", "infoSpcoop", "infoOpenspcoop",
		//							"debugLow", "debugMedium", "debugHigh", "all" };
		String[] tipoMsg = { LogLevels.LIVELLO_OFF, LogLevels.LIVELLO_FATAL, LogLevels.LIVELLO_ERROR_PROTOCOL, LogLevels.LIVELLO_ERROR_INTEGRATION, 
				LogLevels.LIVELLO_INFO_PROTOCOL, LogLevels.LIVELLO_INFO_INTEGRATION,
				LogLevels.LIVELLO_DEBUG_LOW, LogLevels.LIVELLO_DEBUG_MEDIUM, LogLevels.LIVELLO_DEBUG_HIGH,
				LogLevels.LIVELLO_ALL};
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
		//		de.setLabel("Livello Severita");
		de.setType(DataElementType.SELECT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
		de.setValues(tipoMsg);
		de.setSelected(severita);
		dati.addElement(de);

		de = new DataElement();
		//		de.setLabel("Livello Severita Log4J");
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
		if(this.core.isShowConfigurazioneTracciamentoDiagnostica()){
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(severita_log4j);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(severita_log4j);
		}
		dati.addElement(de);

		if (this.isModalitaAvanzata()) {
			if (this.confCore.isMsgDiagnostici_showConfigurazioneCustomAppender()) {
				de = new DataElement();
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
				de = new DataElement();
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
	
	public Vector<DataElement> addConfigurazioneSistemaSelectListNodiCluster(Vector<DataElement> dati) throws Exception {
		
		DataElement de = new DataElement();
		de.setType(DataElementType.SELECT);
		de.setValues(this.confCore.getJmxPdD_aliases());
		List<String> labels = new ArrayList<String>();
		for (String alias : this.confCore.getJmxPdD_aliases()) {
			labels.add(this.confCore.getJmxPdD_descrizione(alias));
		}
		de.setLabels(labels);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
		de.setSize(this.getSize());
		//de.setPostBack(true);
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
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD+"?"+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_CACHE+"="+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES+
					"&"+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO+"="+ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NOME_METODO_RESET_ALL_CACHE_ALL_NODES);
			de.setType(DataElementType.LINK);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES);
			de.setValue(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES);
			de.setSize(this.getSize());
			dati.addElement(de);
		}		
		
		return dati;
	}
	
	private void addInformazioneNonDisponibile(Vector<DataElement> dati, String label){
		DataElement de = new DataElement();
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
			de.setValue(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_RESET_ALL_CACHES);
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
		if(messageFactory!=null){
			messageFactory = StringEscapeUtils.escapeHtml(messageFactory);
		}
		de = newDataElementStyleRuntime();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_MESSAGE_FACTORY);
		de.setValue(messageFactory);
		de.setType(DataElementType.TEXT);
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
			if(this.core.isShowConfigurazioneTracciamentoDiagnostica()){
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
			if(this.core.isShowConfigurazioneTracciamentoDiagnostica()){
				de.setType(DataElementType.SELECT);
				de.setValues(tipoMsg);
				de.setSelected(v);
				de.setPostBack_viaPOST(true);
			}
			else{
				de.setType(DataElementType.TEXT);
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
					de.setValue(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_CACHE_RESET);
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
								String value = "";
								if(params[i].contains(":")){
									label = params[i].split(":")[0];
									value = params[i].split(":")[1];
								}
								
								if(ConfigurazioneCostanti.CONFIGURAZIONE_SISTEMA_CACHE_STATO_ELEMENTI_VISUALIZZATI.contains(label)){
								
									de = newDataElementStyleRuntime();
									de.setLabel(label);
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
		

		return dati;
	}

	private boolean isErroreHttp(String stato, String risorsa){
		if(stato!=null && stato.startsWith("[httpCode ")){
			this.log.error("Errore durante la lettura della risorsa ["+risorsa+"]: "+stato);
			return true;
		}
		return false;
	}
	
	public void addToDatiRegistrazioneEsiti(Vector<DataElement> dati, TipoOperazione tipoOperazione, String tracciamentoEsiti) throws Exception {
		
	
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
//		de = new DataElement();
//		de.setLabel(ConfigurazioneCostanti.LABEL_NOTE_CONFIGURAZIONE_REGISTRAZIONE_ESITI);
//		de.setType(DataElementType.SUBTITLE);
//		dati.addElement(de);
		
		List<String> attivi = new ArrayList<String>();
		if(tracciamentoEsiti!=null){
			String [] tmp = tracciamentoEsiti.split(",");
			if(tmp!=null){
				for (int i = 0; i < tmp.length; i++) {
					attivi.add(tmp[i].trim());
				}
			}
		}
		
		EsitiProperties esiti = EsitiProperties.getInstance(ControlStationCore.getLog());
		List<Integer> esitiCodes = esiti.getEsitiCode();
		if(esitiCodes!=null){
			for (Integer esito : esitiCodes) {
				de = new DataElement();
				de.setLabel(esiti.getEsitoLabel(esito));
				de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
//				de.setNote(esiti.getEsitoLabel(esito));
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(attivi.contains((esito+"")));
				dati.addElement(de);
			}
		}
	}
	
	public String readConfigurazioneRegistrazioneEsitiFromHttpParameters(String configurazioneEsiti, boolean first) throws Exception {
	
	
		StringBuffer bf = new StringBuffer();
		EsitiProperties esiti = EsitiProperties.getInstance(ControlStationCore.getLog());
		List<Integer> esitiCodes = esiti.getEsitiCode();
		if(esitiCodes!=null){
			for (Integer esito : esitiCodes) {
				String esitoParam = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
				boolean checked = ServletUtils.isCheckBoxEnabled(esitoParam);
				if(checked){
					if(bf.length()>0){
						bf.append(",");
					}
					bf.append(esito);
				}
			}
		}
		if(bf.length()>0){
			return bf.toString();
		}
		else{
			if(first==false){
				return null;
			}
			else{
				if(configurazioneEsiti == null || "".equals(configurazioneEsiti.trim())){
					// creo un default composto da tutti ad eccezione dell'esito 84 (MaxThreads)
					this.getRegistrazioneEsiti(configurazioneEsiti, bf);
					if(bf.length()>0){
						return bf.toString();
					}
					else{
						return null;
					}
				}
			}
		}
		return configurazioneEsiti;
	}
	
	public List<String> getRegistrazioneEsiti(String configurazioneEsiti, StringBuffer bf) throws Exception{
		if(configurazioneEsiti==null ||"".equals(configurazioneEsiti.trim())){
			
			// creo un default composto da tutti ad eccezione dell'esito 84 (MaxThreads)
			EsitiProperties esiti = EsitiProperties.getInstance(ControlStationCore.getLog());
			List<Integer> esitiCodes = esiti.getEsitiCode();
			
			if(esitiCodes!=null && esitiCodes.size()>0){
				List<String> esitiDaRegistrare = new ArrayList<String>();
				for (Integer esito : esitiCodes) {
					if(esito!=84){
						if(bf.length()>0){
							bf.append(",");
						}
						bf.append(esito);
						esitiDaRegistrare.add(esito+"");
					}
				}
				if(esitiDaRegistrare.size()>0){
					return esitiDaRegistrare;
				}
			}
			
			return null; // non dovrebbe succedere, degli esiti nell'EsitiProperties dovrebbero esistere
		}
		else{
			
			String [] tmp = configurazioneEsiti.split(",");
			if(tmp!=null && tmp.length>0){
				List<String> esitiDaRegistrare = new ArrayList<String>();
				for (int i = 0; i < tmp.length; i++) {
					String t = tmp[i];
					if(t!=null){
						t = t.trim();
						if(!"".equals(t)){
							if(bf.length()>0){
								bf.append(",");
							}
							bf.append(t);
							esitiDaRegistrare.add(t);
						}
					}
				}
				if(esitiDaRegistrare.size()>0){
					return esitiDaRegistrare;
				}
			}
			
			return null; // non dovrebbe succedere, si rientra nel ramo then dell'if principale
		}
	}
	
	public boolean checkConfigurazioneTracciamento(TipoOperazione tipoOperazione, String configurazioneEsiti)	throws Exception {
		if(configurazioneEsiti ==null || "".equals(configurazioneEsiti.trim())){
			this.pd.setMessage("Deve essere selezionato almeno un esito");
			return false;
		}
		
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
	
		
		// tipo errore
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE);
		if(controlloTraffico.isControlloMaxThreadsEnabled() && (controlloTraffico.isControlloMaxThreadsWarningOnly() == false)) {
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
		
		// messaggio di errore
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE_DESCRIZIONE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_NUM_MASSIMO_RICHIESTE_SIMULTANEE_TIPOLOGIA_ERRORE_DESCRIZIONE);
		if(controlloTraffico.isControlloMaxThreadsEnabled() && (controlloTraffico.isControlloMaxThreadsWarningOnly() == false)) {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(controlloTraffico.isControlloMaxThreadsTipoErroreIncludiDescrizione());
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(controlloTraffico.isControlloMaxThreadsTipoErroreIncludiDescrizione()+"");
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
		
		// tipo errore
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_TIPOLOGIA_ERRORE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_TIPOLOGIA_ERRORE);
		de.setType(DataElementType.SELECT);
		de.setValues(ConfigurazioneCostanti.TIPI_ERRORE);
		de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TIPI_ERRORE);
		if(controlloTraffico.getRateLimiting().getTipoErrore()!=null) {
			TipoErrore tipoErroEnum = TipoErrore.toEnumConstant(controlloTraffico.getRateLimiting().getTipoErrore());
			if(tipoErroEnum!=null) {
				de.setSelected(tipoErroEnum.getValue());
			}
		}
		dati.addElement(de);
		
		// messaggio di errore
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_TIPOLOGIA_ERRORE_DESCRIZIONE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_TIPOLOGIA_ERRORE_DESCRIZIONE);
		de.setType(DataElementType.CHECKBOX);
		de.setSelected(controlloTraffico.getRateLimiting().isTipoErroreIncludiDescrizione());
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
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_LINK);
		de.setType(DataElementType.LINK);
		de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST);
		de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_LINK+" (" + sizeGlobalPolicy+ ")");
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
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_LIFE_TIME);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_LIFE_TIME);
		if(enabled && cache.isCache()){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
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
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		if(cache.getIdleTime()!=null)
			de.setValue(cache.getIdleTime()+"");
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
					cache.setLifeTime(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_LIFE_TIME);
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
			
			if(configurazioneControlloTraffico.getCache().getLifeTime()==null){
				String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CACHE_DIMENSIONE+"'";
				this.pd.setMessage(messaggio);
				return false;
			}
			
		}
		return true;
	}
	
	public void prepareConfigurazionePolicyList(Search ricerca, List<ConfigurazionePolicy> lista, int idLista) throws Exception{
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY);

			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO_POLICY, null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);
			
			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NOME
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
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY_CHANGE, pPolicyId);
					de.setValue(policy.getIdPolicy());
					de.setIdToRemove(""+policy.getId());
					de.setToolTip(policy.getIdPolicy()+"\n"+nDesr); 
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

	public void prepareAttivazionePolicyList(Search ricerca, List<AttivazionePolicy> lista, int idLista) throws Exception{
		try {
			ServletUtils.addListElementIntoSession(this.session, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY);

			if (lista == null)
				this.pd.setNumEntries(0);
			else
				this.pd.setNumEntries(lista.size());

			this.pd.setSearchDescription("");
			this.pd.setSearch("off");

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_LINK, null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);
			
			// setto le label delle colonne
			String[] labels = { 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_IDENTIFICATIVO,
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO,
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME,
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_FILTRO,
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RAGGRUPPAMENTO_COLUMN
			};
			this.pd.setLabels(labels);
			
			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				for (int i = 0; i < lista.size(); i++) {
					AttivazionePolicy policy = lista.get(i);
					Vector<DataElement> e = new Vector<DataElement>();
					
					String nDesr = "";
					try{
						nDesr = this.confCore.getInfoPolicy(policy.getIdPolicy()).getDescrizione();
					}catch(Exception ex){
						ControlStationCore.getLog().error(ex.getMessage(),ex);
					}
					if(StringUtils.isNotEmpty(policy.getAlias())) {
						nDesr = policy.getAlias()+ "\n"+ policy.getIdPolicy()+"\n"+nDesr;
					} else {
						nDesr = policy.getIdPolicy()+"\n"+nDesr;	
					}
					
					
					Parameter pPolicyId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID, policy.getId() + ""); 

					DataElement de = new DataElement();
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId);
					
					if(StringUtils.isNotEmpty(policy.getAlias()))
						de.setValue(policy.getIdPolicy());
					else 
						de.setValue(policy.getIdActivePolicy());
					
					de.setIdToRemove(""+policy.getId());
					de.setToolTip(nDesr); 
					e.addElement(de);
					
					de = new DataElement();
					de.setType(DataElementType.CHECKBOX);
					if(policy.isEnabled()){
						if(policy.isWarningOnly()){
							de.setToolTip(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_WARNING_ONLY);
							de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_WARNING_ONLY);
							de.setSelected(CheckboxStatusType.WARNING_ONLY);
						}
						else{
							de.setToolTip(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
							de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
							de.setSelected(CheckboxStatusType.ABILITATO);
						}
					}
					else{
						de.setToolTip(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
						de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
						de.setSelected(CheckboxStatusType.DISABILITATO);
					}
					e.addElement(de);
					
					de = new DataElement();
					if(policy.isEnabled()){
						de.setValue("Visualizza");
					}
					else{
						de.setValue("-");
					}

					Parameter pJmx = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+"");
					if(policy.isEnabled()){
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId,pJmx);
					}
					e.addElement(de);
								
					de = new DataElement();
					String filtro = this.toStringCompactFilter(policy.getFiltro());
					if(filtro.length()>60){
						de.setValue(filtro.substring(0,57)+"...");
					}else{
						de.setValue(filtro);
					}
					de.setToolTip(filtro);
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId);
					e.addElement(de);
					
					de = new DataElement();
					String groupBy = this.toStringCompactGroupBy(policy.getGroupBy());
					if(groupBy.length()>60){
						de.setValue(groupBy.substring(0,57)+"...");
					}else{
						de.setValue(groupBy);
					}
					de.setToolTip(groupBy);
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, pPolicyId);
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
	
	public String toStringCompactFilter(AttivazionePolicyFiltro filtro) throws Exception {
		StringBuffer bf = new StringBuffer("");
		if(filtro.isEnabled()){

			if( (filtro.getRuoloPorta()!=null && !RuoloPolicy.ENTRAMBI.equals(filtro.getRuoloPorta())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				if(RuoloPolicy.DELEGATA.equals(filtro.getRuoloPorta())){
					bf.append("Tipologia: ");
					bf.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUOLO_PORTA_DELEGATA);
				}
				else if(RuoloPolicy.APPLICATIVA.equals(filtro.getRuoloPorta())){
					bf.append("Tipologia: ");
					bf.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUOLO_PORTA_APPLICATIVA);
				}
			}

			if( !(filtro.getProtocollo()==null || "".equals(filtro.getProtocollo())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append(CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT+": ");
				bf.append(this.getLabelProtocollo(filtro.getProtocollo()));
			}
						
			if(filtro.getRuoloErogatore()!=null) {
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("RuoloErogatore: ");
				bf.append(filtro.getRuoloErogatore());
			}
			else if( !( (filtro.getTipoErogatore()==null || "".equals(filtro.getTipoErogatore())) 
					||
					(filtro.getNomeErogatore()==null || "".equals(filtro.getNomeErogatore())) ) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Erogatore: ");
				IDSoggetto idSoggetto = new IDSoggetto(filtro.getTipoErogatore(), filtro.getNomeErogatore());
				bf.append(this.getLabelNomeSoggetto(idSoggetto));
			}

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
				bf.append("Servizio: ");
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(filtro.getTipoServizio(), filtro.getNomeServizio(), 
						filtro.getTipoErogatore(), filtro.getNomeErogatore(), 
						filtro.getVersioneServizio());
				bf.append(this.getLabelIdServizio(idServizio));
			}
			
			if( !(filtro.getAzione()==null || "".equals(filtro.getAzione())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Azione: ");
				bf.append(filtro.getAzione());
			}
			
			if( !(filtro.getServizioApplicativoErogatore()==null || "".equals(filtro.getServizioApplicativoErogatore())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("SAErogatore: ");
				bf.append(filtro.getServizioApplicativoErogatore());
			}
			
			if(filtro.getRuoloFruitore()!=null) {
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("RuoloFruitore: ");
				bf.append(filtro.getRuoloFruitore());
			}
			else if( !( (filtro.getTipoFruitore()==null || "".equals(filtro.getTipoFruitore())) 
					||
					(filtro.getNomeFruitore()==null || "".equals(filtro.getNomeFruitore())) ) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Fruitore: ");
				IDSoggetto idSoggetto = new IDSoggetto(filtro.getTipoFruitore(), filtro.getNomeFruitore());
				bf.append(this.getLabelNomeSoggetto(idSoggetto));
			}

			if( !(filtro.getServizioApplicativoFruitore()==null || "".equals(filtro.getServizioApplicativoFruitore())) ){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("SAFruitore: ");
				bf.append(filtro.getServizioApplicativoFruitore());
			}
			
			if(filtro.isInformazioneApplicativaEnabled()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Chiave: ");
				bf.append(filtro.getInformazioneApplicativaTipo());
			}

		}
		else{
			bf.append("Disabilitato");
		}

		return bf.toString();
	}
	
	public String toStringCompactGroupBy(AttivazionePolicyRaggruppamento groupBy) {
		StringBuffer bf = new StringBuffer("");
		if(groupBy.isEnabled()){

			if(groupBy.isRuoloPorta()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Tipologia");
			}
			
			if(groupBy.isProtocollo()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append(CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT);
			}
			
			if(groupBy.isErogatore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Erogatore");
			}
			
			if(groupBy.isServizio()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Servizio");
			}
			
			if(groupBy.isAzione()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Azione");
			}
			
			if(groupBy.isServizioApplicativoErogatore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("SAErogatore");
			}
			
			if(groupBy.isFruitore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("Fruitore");
			}
			
			if(groupBy.isServizioApplicativoFruitore()){
				if(bf.length()>0){
					bf.append(", ");
				}
				bf.append("SAFruitore");
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
			bf.append("Disabilitato");
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
		String risorsa = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA);
		if(risorsa!=null && !"".equals(risorsa)){
			try{
				policy.setRisorsa(TipoRisorsa.toEnumConstant(risorsa, true).getValue());
			}catch(Exception e){
				String messaggio = "Il valore ("+risorsa+") indicato in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA+"' non è tra i tipi di risorsa gestiti";
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
		
		// richiesteSimultanee
		String simultanee = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_RICHIESTE_SIMULTANEE);
		if(first==false){
			if(TipoRisorsa.NUMERO_RICHIESTE.getValue().equals(policy.getRisorsa())){
				policy.setSimultanee(ServletUtils.isCheckBoxEnabled(simultanee));
			}
			else{
				policy.setSimultanee(false);
			}
		}
		
		
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
		
		StringBuffer bfSuggerimentoNome = new StringBuffer();
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
			StringBuffer bfInterno = new StringBuffer();
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
		
		StringBuffer bfSuggerimentoDescrizione = new StringBuffer();
		
		TipoRisorsa risorsa = TipoRisorsa.toEnumConstant(policy.getRisorsa());
		
		StringBuffer bfIntervallo = new StringBuffer();
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
				bfSuggerimentoDescrizione.append("La policy limita il numero totale massimo di richieste, completate con successo, consentite");
				break;
			case NUMERO_RICHIESTE_FALLITE:
				bfSuggerimentoDescrizione.append("La policy limita il numero totale massimo di richieste fallite consentite");
				break;
			case NUMERO_FAULT_APPLICATIVI:
				bfSuggerimentoDescrizione.append("La policy limita il numero totale massimo di richieste, che veicolano un fault applicativo, consentite");
				break;
			}
		}
		
		bfSuggerimentoDescrizione.append(bfIntervallo.toString());
		
		
		if(policy.isApplicabilitaConCongestione() || policy.isApplicabilitaDegradoPrestazionale() || policy.isApplicabilitaStatoAllarme()){
			bfSuggerimentoDescrizione.append("\nLa policy viene applicata solamente se ");
			StringBuffer bfApplicabilita = new StringBuffer();
			if(policy.isApplicabilitaConCongestione()){
				bfApplicabilita.append("la PdD risulta Congestionata dalla richieste");
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
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA);
		de.setValues(TipoRisorsa.toStringArray());
		de.setSelected(policy.getRisorsa());
		de.setValue(policy.getRisorsa());
		if(editMode) {
			de.setType(DataElementType.SELECT);
			de.setPostBack(true);
		}
		else {
			de.setType(DataElementType.TEXT);
		}
		dati.addElement(de);
		
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
		
		
		// simultanee
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_RICHIESTE_SIMULTANEE);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_RICHIESTE_SIMULTANEE);
		if(TipoRisorsa.NUMERO_RICHIESTE.getValue().equals(policy.getRisorsa())){
			if(editMode) {
				de.setType(DataElementType.CHECKBOX);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
		}
		else{
			de.setType(DataElementType.HIDDEN);
			policy.setSimultanee(false);
		}
		de.setSelected(policy.isSimultanee());
		de.setValue(policy.isSimultanee()+"");
		de.setPostBack(true);
		dati.addElement(de);
		
		if(TipoRisorsa.NUMERO_RICHIESTE.getValue().equals(policy.getRisorsa()) && !editMode){
			// Il valore del parametor originale viene passato come hidden
			// L'elemento seguente serve solo come presentation, infatti il nome del parametro termina con un suffisso noEdit
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_RICHIESTE_SIMULTANEE+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_SOGLIA_RICHIESTE_SIMULTANEE);
			if(policy.isSimultanee()) {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
			}
			else {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
			}
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
		
		
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
			de.setPostBack(true);
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
			de.setPostBack(true);
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
			de.setPostBack(true);			
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
			de.setPostBack(true);
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
			de.setPostBack(true);
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
		de.setPostBack(true);
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
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_LABEL);
		de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE);
		if(condizionata){
			if(editMode) {
				de.setType(DataElementType.CHECKBOX);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setPostBack(true);
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
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE);
			if(policy.isApplicabilitaConCongestione()) {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
			}
			else {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
			}
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
		// con degrado prestazionale
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_LABEL);
		de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE);
		if(condizionata && 
				!TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(policy.getRisorsa()) && 
				!TipoRisorsa.TEMPO_COMPLESSIVO_RISPOSTA.equals(policy.getRisorsa()) 
			){
			if(editMode) {
				de.setType(DataElementType.CHECKBOX);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setPostBack(true);
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
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE);
			if(policy.isApplicabilitaDegradoPrestazionale()) {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
			}
			else {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
			}
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
		// con stato allarme
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_LABEL);
		de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOTE);
		if(condizionata && this.isAllarmiModuleEnabled()){
			if(editMode) {
				de.setType(DataElementType.CHECKBOX);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setPostBack(true);
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
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME+
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_NO_EDIT_SUFFIX);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_LABEL);
			de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOTE);
			if(policy.isApplicabilitaStatoAllarme()) {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ABILITATO);
			}
			else {
				de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_DISABILITATO);
			}
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
		// ConCongestione
		addToApplicabilitaConCongestione(dati, tipoOperazione, policy, controlloTraffico, editMode);
		
		// Degrado Prestazionale
		addToApplicabilitaDegradoPrestazionale(dati, tipoOperazione, policy , editMode);
		
		// StatoAllarme
		addToApplicabilitaStatoAllarme(dati, tipoOperazione, policy , editMode);
	}
	
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
			
			if(controlloTraffico.isControlloCongestioneEnabled()){
				
				Integer soglia = controlloTraffico.getControlloCongestioneThreshold();
				Long numeroThreadCongestionamento = null; 
				Long numeroThreadComplessivi = controlloTraffico.getControlloMaxThreadsSoglia();
				
				double numD = numeroThreadComplessivi.doubleValue();
				double totale = 100d;
				double sogliaD = soglia.doubleValue();
				Double numeroThreadCongestionamentoD = (numD / totale) *  sogliaD;
				numeroThreadCongestionamento = numeroThreadCongestionamentoD.longValue();
			
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE_CONGESTION_ACTIVE_AS_TEXT.
						replace(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE_NUMERO, numeroThreadCongestionamento+""));
			}
			else{
				
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_CONGESTIONE_NOTE_CONGESTION_NOT_ACTIVE_AS_TEXT);
				
			}
			
			dati.addElement(de);
		}
		
	}
	
	@SuppressWarnings("incomplete-switch")
	private void addToApplicabilitaDegradoPrestazionale(Vector<DataElement> dati, TipoOperazione tipoOperazione, ConfigurazionePolicy policy, boolean editMode) throws Exception {
		
		if(policy.isApplicabilitaDegradoPrestazionale()){
			DataElement de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DEGRADO_PRESTAZIONALE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			// Note
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE);
			de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_CON_DEGRADO_PRESTAZIONALE_NOTE_AS_TEXT);
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			de.setRows(6);
			de.setCols(55);
			dati.addElement(de);
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
			de.setPostBack(true);
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
			de.setPostBack(true);
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
			de.setPostBack(true);
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
			}else {
				de.setType(DataElementType.TEXT);
			}
			de.setRequired(true);
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
			de.setPostBack(true);
		}
		dati.addElement(de);
		
		if(editMode==false && policy.isApplicabilitaDegradoPrestazionale()) {
			dati.addElement(deNoEditMode);
		}
		
		
	}
	
	private void addToApplicabilitaStatoAllarme(Vector<DataElement> dati, TipoOperazione tipoOperazione, ConfigurazionePolicy policy, boolean editMode) throws Exception {
		
		if(policy.isApplicabilitaStatoAllarme()){
			DataElement de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_ALLARME);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			// Note
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOTE);
			de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_APPLICABILITA_STATO_ALLARME_NOTE_AS_TEXT);
			de.setType(DataElementType.TEXT);
//			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
//			de.setRows(6);
//			de.setCols(80);
			dati.addElement(de);
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
			if(protocollo!=null && !"".equals(protocollo) && !ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI.equals(protocollo)){
				policy.getFiltro().setProtocollo(protocollo);
			}
			else{
				if(!first){
					policy.getFiltro().setProtocollo(null);
				}
			}
			
			// ruolo erogatore
			String ruoloErogatore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_EROGATORE);
			if(ruoloErogatore!=null && !"".equals(ruoloErogatore) && !ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI.equals(ruoloErogatore) ){
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
			if(erogatore!=null && !"".equals(erogatore) && !ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI.equals(erogatore) && erogatore.contains("/") ){
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
			if(servizioApplicativoErogatore!=null && !"".equals(servizioApplicativoErogatore) && !ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI.equals(servizioApplicativoErogatore) ){
				policy.getFiltro().setServizioApplicativoErogatore(servizioApplicativoErogatore);
			}
			else{
				if(!first){
					policy.getFiltro().setServizioApplicativoErogatore(null);
				}
			}
			
			// servizio
			String servizio = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO);
			if(servizio!=null && !"".equals(servizio) && !ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI.equals(servizio) && servizio.contains("/") ){
				String [] tmp = servizio.split("/");
				policy.getFiltro().setTipoServizio(tmp[0]);
				policy.getFiltro().setNomeServizio(tmp[1]);
				policy.getFiltro().setVersioneServizio(Integer.parseInt(tmp[2]));
				policy.getFiltro().setTipoErogatore(tmp[3]);
				policy.getFiltro().setNomeErogatore(tmp[4]);
			}
			else{
				if(!first){
					policy.getFiltro().setTipoServizio(null);
					policy.getFiltro().setNomeServizio(null);
					policy.getFiltro().setVersioneServizio(null);
					if(!erogatoreSelected) {
						policy.getFiltro().setTipoErogatore(null);
						policy.getFiltro().setNomeErogatore(null);
					}
				}
			}
			
			// azione
			String azione = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE);
			if(azione!=null && !"".equals(azione) && !ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI.equals(azione) ){
				policy.getFiltro().setAzione(azione);
			}
			else{
				if(!first){
					policy.getFiltro().setAzione(null);
				}
			}
			
			// ruolo fruitore
			String ruoloFruitore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE);
			if(ruoloFruitore!=null && !"".equals(ruoloFruitore) && !ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI.equals(ruoloFruitore) ){
				policy.getFiltro().setRuoloFruitore(ruoloFruitore);
			}
			else{
				if(!first){
					policy.getFiltro().setRuoloFruitore(null);
				}
			}
			
			// fruitore
			String fruitore = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE);
			if(fruitore!=null && !"".equals(fruitore) && !ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI.equals(fruitore) && fruitore.contains("/") ){
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
			if(servizioApplicativoFruitore!=null && !"".equals(servizioApplicativoFruitore) && !ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI.equals(servizioApplicativoFruitore) ){
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
			policy.getGroupBy().setEnabled(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_ABILITATO.equals(statoGroupBy));
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

	public void addAttivazionePolicyToDati(Vector<DataElement> dati, TipoOperazione tipoOperazione, AttivazionePolicy policy, String nomeSezione, List<InfoPolicy> policies) throws Exception {
		List<String> idPolicies = new ArrayList<String>();
		idPolicies.add("-");
		InfoPolicy infoPolicy = null;
		if(policies!=null && policies.size()>0){
			for (InfoPolicy info : policies) {
				idPolicies.add(info.getIdPolicy());
				if(policy.getIdPolicy()!=null && policy.getIdPolicy().equals(info.getIdPolicy())){
					infoPolicy = info;
				}
			}
		}
		
		DataElement de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_IS_ATTIVAZIONE_GLOBALE);
		de.setValue(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_IS_ATTIVAZIONE_GLOBALE_VALORE);
		de.setType(DataElementType.HIDDEN);
		dati.addElement(de);
						
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
		
		if(policy.getIdActivePolicy()!=null && !"".equals(policy.getIdActivePolicy())){
			// id-active-policy
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_ID_UNICO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_ID_UNICO);
			if(!jmx){
				de.setType(DataElementType.TEXT);
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setValue(policy.getIdActivePolicy());
			dati.addElement(de);
		}
		
		// policy
		de = new DataElement();
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID);
		if(TipoOperazione.ADD.equals(tipoOperazione)){
			de.setValues(idPolicies);
			if(policy.getIdPolicy()!=null)
				de.setSelected(policy.getIdPolicy());
			else
				de.setSelected("-");
			de.setType(DataElementType.SELECT);
			de.setRequired(true);
			de.setPostBack(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		if(policy.getIdPolicy()!=null)
			de.setValue(policy.getIdPolicy());
		else
			de.setValue("-");
		dati.addElement(de);
		
					
		if(!jmx){
		
			// alias
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS);
			if(infoPolicy!=null){
				de.setType(DataElementType.TEXT_EDIT);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setValue(policy.getAlias());
			dati.addElement(de);
			
			// descrizione
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_DESCRIZIONE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_DESCRIZIONE);
			if(infoPolicy!=null){
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setRows(6);
				//de.setCols(57);
				de.setCols(55);
				de.setLabelAffiancata(true);
				de.setValue(infoPolicy.getDescrizione());
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(null);
			}
			dati.addElement(de);
			
			if(infoPolicy!=null){
				// stato
				addToDatiDataElementStato(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ENABLED, 
						ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ENABLED, policy.isEnabled(), false,
						true, policy.isWarningOnly());
			}

			if(TipoOperazione.CHANGE.equals(tipoOperazione)){
				if(policy!=null && policy.isEnabled()){
					// Link visualizza stato 
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO);
					de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO);
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""));
					de.setType(DataElementType.LINK);
					dati.addElement(de);
				}
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
			de.setPostBack(true);
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
				// Filtro 
				addToDatiAttivazioneFiltro(dati, tipoOperazione, policy, nomeSezione, infoPolicy);
				
				// GroupBy 
				addToDatiAttivazioneGroupBy(dati, tipoOperazione, policy, nomeSezione, infoPolicy);
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
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
					new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
					new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""));
			de.setType(DataElementType.LINK);
			dati.addElement(de);

			if(showResetCounters && aliases.size()>1){
				// Link resetCounters
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_RESET);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO_RESET_ALL_NODES);
				de.setValue(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VISUALIZZA_STATO_RESET_ALL_NODES);
				de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""),
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET_ALL_VALUE));
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
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_CHANGE, 
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ID,policy.getId()+""),
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_STATE, true+""),
							new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_JMX_RESET, alias));
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
	
	private void addToDatiDataElementStato(Vector<DataElement> dati, String param, String label, boolean enabled, boolean postBack, boolean withWarningOnly, boolean warningOnly){
		DataElement de = new DataElement();
		de.setName(param);
		de.setLabel(label);
		de.setType(DataElementType.SELECT);
		if(withWarningOnly){
			de.setValues(ConfigurazioneCostanti.STATI_CON_WARNING);
		}
		else{
			de.setValues(ConfigurazioneCostanti.STATI);
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
		de.setPostBack(postBack);
		dati.addElement(de);
	}
	private void addToDatiDataElementStatoReadOnly(Vector<DataElement> dati, String param, String label, boolean enabled, boolean postBack, boolean withWarningOnly, boolean warningOnly){
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
	
	private void addToDatiAttivazioneFiltro(Vector<DataElement> dati, TipoOperazione tipoOperazione,AttivazionePolicy policy, String nomeSezione, InfoPolicy infoPolicy) throws Exception {
	
		
		// Elaboro valori con dipendenze
		
		List<String> protocolliLabel = null;
		List<String> protocolliValue = null;
		String protocolloSelezionatoLabel = null;
		String protocolloSelezionatoValue = null;
		
		List<String> ruoliErogatore = null;
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
		
		List<String> azioni = null;
		String azioneSelezionataLabel = null;
		String azioneSelezionataValue = null;
		
		List<String> serviziApplicativiErogatoreLabel = null;
		List<String> serviziApplicativiErogatoreValue = null;
		String servizioApplicativoErogatoreSelezionatoLabel = null;
		String servizioApplicativoErogatoreSelezionatoValue = null;
		
		List<String> ruoliFruitore = null;
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
			
			// ruolo erogatore
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				ruoloErogatoreSelezionatoValue = policy.getFiltro().getRuoloErogatore();
				ruoloErogatoreSelezionatoLabel = ruoloErogatoreSelezionatoValue!=null ? ruoloErogatoreSelezionatoValue :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI;
			}
			else {
				ruoliErogatore = this.core.getAllRuoli(filtroRuoli);
				
				if(policy.getFiltro().getRuoloErogatore()!=null) {
					ruoloErogatoreSelezionatoValue = policy.getFiltro().getRuoloErogatore();
				}
				if(!ruoliErogatore.contains(ruoloErogatoreSelezionatoValue)){
					policy.getFiltro().setRuoloErogatore(null);
					ruoloErogatoreSelezionatoValue = null;
				}
				ruoliErogatore = enrichListConQualsiasi(ruoliErogatore);
			}
			
			// erogatore
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				IDSoggetto idSoggetto = null;
				if(policy.getFiltro().getTipoErogatore()!=null && policy.getFiltro().getNomeErogatore()!=null){
					datiIdentificativiErogatoreSelezionatoValue = policy.getFiltro().getTipoErogatore() + "/" + policy.getFiltro().getNomeErogatore();
					idSoggetto = new IDSoggetto(policy.getFiltro().getTipoErogatore() , policy.getFiltro().getNomeErogatore());
				}
				datiIdentificativiErogatoreSelezionatoLabel = idSoggetto!=null ? this.getLabelNomeSoggetto(idSoggetto) :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI;
			}
			else {
				List<IDSoggetto> listErogatori = this.confCore.getSoggettiErogatori(protocolloSelezionatoValue, protocolliValue);
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
				erogatoriLabel = enrichListConQualsiasi(erogatoriLabel);
				erogatoriValue = enrichListConQualsiasi(erogatoriValue);
			}
					
			// servizio
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				IDServizio idServizio = null;
				if(policy.getFiltro().getTipoServizio()!=null && policy.getFiltro().getNomeServizio()!=null && policy.getFiltro().getVersioneServizio()!=null &&
						policy.getFiltro().getTipoErogatore()!=null && policy.getFiltro().getNomeErogatore()!=null
						){
					datiIdentificativiServizioSelezionatoValue = policy.getFiltro().getTipoServizio()+"/"+policy.getFiltro().getNomeServizio()+"/"+policy.getFiltro().getVersioneServizio().intValue()+"/"+
							policy.getFiltro().getTipoErogatore()+"/"+policy.getFiltro().getNomeErogatore();
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(policy.getFiltro().getTipoServizio(), 
							policy.getFiltro().getNomeServizio(), 
							policy.getFiltro().getTipoErogatore(), 
							policy.getFiltro().getNomeErogatore(), 
							policy.getFiltro().getVersioneServizio());
				}
				datiIdentificativiServizioSelezionatoLabel = idServizio!=null ? this.getLabelIdServizio(idServizio) :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI;
			}
			else {
				List<IDServizio> listServizi = this.confCore.getServizi(protocolloSelezionatoValue, protocolliValue,
						policy.getFiltro().getTipoErogatore(), policy.getFiltro().getNomeErogatore());
				serviziLabel = new ArrayList<>();
				serviziValue = new ArrayList<>();
				for (IDServizio idServizio : listServizi) {
					serviziLabel.add(this.getLabelIdServizio(idServizio));
					serviziValue.add(idServizio.getTipo()+"/"+idServizio.getNome()+"/"+idServizio.getVersione().intValue()+"/"+
							idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome());
				}
				if(policy.getFiltro().getTipoServizio()!=null && policy.getFiltro().getNomeServizio()!=null && policy.getFiltro().getVersioneServizio()!=null &&
						policy.getFiltro().getTipoErogatore()!=null && policy.getFiltro().getNomeErogatore()!=null
						){
					datiIdentificativiServizioSelezionatoValue = policy.getFiltro().getTipoServizio()+"/"+policy.getFiltro().getNomeServizio()+"/"+policy.getFiltro().getVersioneServizio().intValue()+"/"+
							policy.getFiltro().getTipoErogatore()+"/"+policy.getFiltro().getNomeErogatore();
				}
				if(!serviziValue.contains(datiIdentificativiServizioSelezionatoValue)){
					policy.getFiltro().setTipoServizio(null);
					policy.getFiltro().setNomeServizio(null);
					policy.getFiltro().setVersioneServizio(null);
					datiIdentificativiServizioSelezionatoValue = null;
				}
				serviziLabel = enrichListConQualsiasi(serviziLabel);
				serviziValue = enrichListConQualsiasi(serviziValue);
			}
			
			// azioni
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				if(policy.getFiltro().getAzione()!=null){
					azioneSelezionataValue = policy.getFiltro().getAzione();
				}
				azioneSelezionataLabel = azioneSelezionataValue!=null ? azioneSelezionataValue :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI;
			}
			else {
				if(datiIdentificativiServizioSelezionatoValue!=null) {
					azioni = this.confCore.getAzioni(protocolloSelezionatoValue, protocolliValue,
							policy.getFiltro().getTipoErogatore(), policy.getFiltro().getNomeErogatore(), 
							policy.getFiltro().getTipoServizio(), policy.getFiltro().getNomeServizio(), policy.getFiltro().getVersioneServizio());
				}
				else {
					azioni = new ArrayList<>();
				}
				if(policy.getFiltro().getAzione()!=null){
					azioneSelezionataValue = policy.getFiltro().getAzione();
				}
				if(!azioni.contains(azioneSelezionataValue)){
					policy.getFiltro().setAzione(null);
					azioneSelezionataValue = null;
				}
				azioni = enrichListConQualsiasi(azioni);
			}
				
			// servizi applicativi erogatore
			if(policy.getFiltro().getRuoloPorta()==null ||
					RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta()) || 
					RuoloPolicy.APPLICATIVA.equals(policy.getFiltro().getRuoloPorta())){
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					if(policy.getFiltro().getServizioApplicativoErogatore()!=null){
						servizioApplicativoErogatoreSelezionatoValue = policy.getFiltro().getServizioApplicativoErogatore();
					}
					servizioApplicativoErogatoreSelezionatoLabel = servizioApplicativoErogatoreSelezionatoValue!=null ? servizioApplicativoErogatoreSelezionatoValue :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI;
				}
				else {
					serviziApplicativiErogatoreLabel = new ArrayList<>();
					serviziApplicativiErogatoreValue = new ArrayList<>();
					if(datiIdentificativiErogatoreSelezionatoValue!=null) {
						List<IDServizioApplicativo> listSA = this.confCore.getServiziApplicativiErogatori(protocolloSelezionatoValue, protocolliValue,
								policy.getFiltro().getTipoErogatore(), policy.getFiltro().getNomeErogatore(),
								policy.getFiltro().getTipoServizio(), policy.getFiltro().getNomeServizio(), policy.getFiltro().getVersioneServizio(),
								azioneSelezionataValue);
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
					serviziApplicativiErogatoreLabel = enrichListConQualsiasi(serviziApplicativiErogatoreLabel);
					serviziApplicativiErogatoreValue = enrichListConQualsiasi(serviziApplicativiErogatoreValue);
				}
			}
			
			// ruolo fruitore
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				if(policy.getFiltro().getRuoloFruitore()!=null) {
					ruoloFruitoreSelezionatoValue = policy.getFiltro().getRuoloFruitore();
				}
				ruoloFruitoreSelezionatoLabel = ruoloFruitoreSelezionatoValue!=null ? ruoloFruitoreSelezionatoValue :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI;
			}
			else {
				ruoliFruitore = this.core.getAllRuoli(filtroRuoli);
				if(policy.getFiltro().getRuoloFruitore()!=null) {
					ruoloFruitoreSelezionatoValue = policy.getFiltro().getRuoloFruitore();
				}
				if(!ruoliFruitore.contains(ruoloFruitoreSelezionatoValue)){
					policy.getFiltro().setRuoloFruitore(null);
					ruoloFruitoreSelezionatoValue = null;
				}
				ruoliFruitore = enrichListConQualsiasi(ruoliFruitore);
			}
			
			// fruitore
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				IDSoggetto idSoggetto = null;
				if(policy.getFiltro().getTipoFruitore()!=null && policy.getFiltro().getNomeFruitore()!=null){
					datiIdentificativiFruitoreSelezionatoValue = policy.getFiltro().getTipoFruitore() + "/" + policy.getFiltro().getNomeFruitore();
					idSoggetto = new IDSoggetto(policy.getFiltro().getTipoFruitore() , policy.getFiltro().getNomeFruitore());
				}
				datiIdentificativiFruitoreSelezionatoLabel = idSoggetto!=null ? this.getLabelNomeSoggetto(idSoggetto) :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI;
			}
			else {
				List<IDSoggetto> listFruitori = this.confCore.getSoggettiFruitori(protocolloSelezionatoValue, protocolliValue,
						policy.getFiltro().getTipoErogatore(), policy.getFiltro().getNomeErogatore(), 
						policy.getFiltro().getTipoServizio(), policy.getFiltro().getNomeServizio(), policy.getFiltro().getVersioneServizio());
				fruitoriLabel = new ArrayList<>();
				fruitoriValue = new ArrayList<>();
				for (IDSoggetto idSoggetto : listFruitori) {
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
				fruitoriLabel = enrichListConQualsiasi(fruitoriLabel);
				fruitoriValue = enrichListConQualsiasi(fruitoriValue);
			}
			
			// servizi applicativi fruitore
			if(policy.getFiltro().getRuoloPorta()==null ||
					RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta()) || 
					RuoloPolicy.DELEGATA.equals(policy.getFiltro().getRuoloPorta())){
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					if(policy.getFiltro().getServizioApplicativoFruitore()!=null){
						servizioApplicativoFruitoreSelezionatoValue = policy.getFiltro().getServizioApplicativoFruitore();
					}
					servizioApplicativoFruitoreSelezionatoLabel = servizioApplicativoFruitoreSelezionatoValue!=null ? servizioApplicativoFruitoreSelezionatoValue :  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI;
				}
				else {
					serviziApplicativiFruitoreLabel = new ArrayList<>();
					serviziApplicativiFruitoreValue = new ArrayList<>();
					if(datiIdentificativiFruitoreSelezionatoValue!=null) {
						List<IDServizioApplicativo> listSA = this.confCore.getServiziApplicativiFruitore(protocolloSelezionatoValue, protocolliValue,
								policy.getFiltro().getTipoFruitore(), policy.getFiltro().getNomeFruitore(), 
								policy.getFiltro().getTipoErogatore(), policy.getFiltro().getNomeErogatore(),
								policy.getFiltro().getTipoServizio(), policy.getFiltro().getNomeServizio(), policy.getFiltro().getVersioneServizio(),
								azioneSelezionataValue);
						for (IDServizioApplicativo idServizioApplicativo : listSA) {
							serviziApplicativiFruitoreLabel.add(idServizioApplicativo.getNome());
							serviziApplicativiFruitoreValue.add(idServizioApplicativo.getNome());
						}
					}
					if(policy.getFiltro().getServizioApplicativoFruitore()!=null){
						servizioApplicativoFruitoreSelezionatoValue = policy.getFiltro().getServizioApplicativoFruitore();
					}
					if(!serviziApplicativiFruitoreValue.contains(servizioApplicativoFruitoreSelezionatoValue)){
						policy.getFiltro().setServizioApplicativoFruitore(null);
						servizioApplicativoFruitoreSelezionatoValue = null;
					}
					serviziApplicativiFruitoreLabel = enrichListConQualsiasi(serviziApplicativiFruitoreLabel);
					serviziApplicativiFruitoreValue = enrichListConQualsiasi(serviziApplicativiFruitoreValue);
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
			addToDatiDataElementStato(dati, ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED, 
					ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED, policy.getFiltro().isEnabled(), true,
					false, false);
		}
		
		if(policy.getFiltro().isEnabled()){
		
			// Ruolo PdD
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD);
			if(policy.getFiltro().getRuoloPorta()!=null){
				de.setValue(policy.getFiltro().getRuoloPorta().getValue());
			}
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD+"___LABEL");
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD);
				if(policy.getFiltro().getRuoloPorta()!=null){
					de.setValue(policy.getFiltro().getRuoloPorta().getValue());
				}
				else {
					de.setValue(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI);
				}
				de.setType(DataElementType.TEXT);
			}
			else {
				de.setValues(ConfigurazioneCostanti.TIPI_RUOLO_PDD);
				de.setLabels(ConfigurazioneCostanti.LABEL_TIPI_RUOLO_PDD);
				if(policy.getFiltro().getRuoloPorta()!=null){
					de.setSelected(policy.getFiltro().getRuoloPorta().getValue());
				}
				de.setType(DataElementType.SELECT);
				de.setPostBack(true);
			}
			dati.addElement(de);
			
	
			// Protocollo
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO);
			de.setValue(protocolloSelezionatoValue); // un protocollo e' sempre selezionato 
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO+"___LABEL");
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO);
				de.setValue(protocolloSelezionatoLabel); // un protocollo e' sempre selezionato 
				de.setType(DataElementType.TEXT);
			}
			else if(protocolliValue.size()>1){ 
				de.setValues(protocolliValue);
				de.setLabels(protocolliLabel);
				de.setSelected(protocolloSelezionatoValue);
				de.setType(DataElementType.SELECT);
				de.setPostBack(true);
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
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_EROGATORE+"___LABEL");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_EROGATORE);
					de.setValue(ruoloErogatoreSelezionatoLabel);
					de.setType(DataElementType.TEXT);
				}
				else {
					de.setValues(ruoliErogatore);
					de.setSelected(ruoloErogatoreSelezionatoValue);
					de.setType(DataElementType.SELECT);
					de.setPostBack(true);
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
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_EROGATORE+"___LABEL");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_EROGATORE);
					de.setValue(datiIdentificativiErogatoreSelezionatoLabel);
					de.setType(DataElementType.TEXT);
				}
				else {
					de.setLabels(erogatoriLabel);
					de.setValues(erogatoriValue);
					de.setSelected(datiIdentificativiErogatoreSelezionatoValue);
					de.setType(DataElementType.SELECT);
					de.setPostBack(true);
				}
			}
			dati.addElement(de);
						
			// Servizio
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO);
			de.setValue(datiIdentificativiServizioSelezionatoValue);
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO+"___LABEL");
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO);
				de.setValue(datiIdentificativiServizioSelezionatoLabel);
				de.setType(DataElementType.TEXT);
			}
			else {
				de.setLabels(serviziLabel);
				de.setValues(serviziValue);
				de.setSelected(datiIdentificativiServizioSelezionatoValue);
				de.setType(DataElementType.SELECT);
				de.setPostBack(true);
			}
			dati.addElement(de);
			
			// Azione
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE);
			de.setValue(azioneSelezionataValue);
			if(protocolloAssociatoFiltroNonSelezionatoUtente) {
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE+"___LABEL");
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE);
				de.setValue(azioneSelezionataLabel);
				de.setType(DataElementType.TEXT);
			}
			else {
				de.setValues(azioni);
				de.setSelected(azioneSelezionataValue);
				de.setType(DataElementType.SELECT);
				de.setPostBack(true);
			}
			dati.addElement(de);
			
			// Servizio Applicativo Erogatore
			if(serviziApplicativiErogatoreValue!=null){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE);
				de.setValue(servizioApplicativoErogatoreSelezionatoValue);
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE+"___LABEL");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE);
					de.setValue(servizioApplicativoErogatoreSelezionatoLabel);
					de.setType(DataElementType.TEXT);
				}
				else {
					de.setLabels(serviziApplicativiErogatoreLabel);
					de.setValues(serviziApplicativiErogatoreValue);
					de.setSelected(servizioApplicativoErogatoreSelezionatoValue);
					de.setType(DataElementType.SELECT);
					de.setPostBack(true);
				}
				dati.addElement(de);
			}
			
			// Ruolo Fruitore
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE);
			if(datiIdentificativiFruitoreSelezionatoValue!=null) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setValue(ruoloFruitoreSelezionatoValue);
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE+"___LABEL");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE);
					de.setValue(ruoloFruitoreSelezionatoLabel);
					de.setType(DataElementType.TEXT);
				}
				else {
					de.setValues(ruoliFruitore);
					de.setSelected(ruoloFruitoreSelezionatoValue);
					de.setType(DataElementType.SELECT);
					de.setPostBack(true);
				}
			}
			dati.addElement(de);
			
			// Fruitore
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE);
			if(ruoloFruitoreSelezionatoValue!=null) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setValue(datiIdentificativiFruitoreSelezionatoValue);
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE+"___LABEL");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE);
					de.setValue(datiIdentificativiFruitoreSelezionatoLabel);
					de.setType(DataElementType.TEXT);
				}
				else {
					de.setLabels(fruitoriLabel);
					de.setValues(fruitoriValue);
					de.setSelected(datiIdentificativiFruitoreSelezionatoValue);
					de.setType(DataElementType.SELECT);
					de.setPostBack(true);
				}
			}
			dati.addElement(de);
			
			// Servizio Applicativo Fruitore
			if(serviziApplicativiFruitoreValue!=null){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE);
				de.setValue(servizioApplicativoFruitoreSelezionatoValue);
				if(protocolloAssociatoFiltroNonSelezionatoUtente) {
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE+"___LABEL");
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE);
					de.setValue(servizioApplicativoFruitoreSelezionatoLabel);
					de.setType(DataElementType.TEXT);
				}
				else {
					de.setLabels(serviziApplicativiFruitoreLabel);
					de.setValues(serviziApplicativiFruitoreValue);
					de.setSelected(servizioApplicativoFruitoreSelezionatoValue);
					de.setType(DataElementType.SELECT);
					de.setPostBack(true);
				}
				dati.addElement(de);
			}
			
			if(filtroByKey){
			
				// per chiave
				
				if(policy.getFiltro().isInformazioneApplicativaEnabled()){
					de = new DataElement();
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
				}
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED);
				if(policy.getFiltro().isInformazioneApplicativaEnabled()){
					de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO);
				}
				else{
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED);
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
					de.setPostBack(true);
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
						de.setSelected(policy.getFiltro().getInformazioneApplicativaTipo());
						de.setType(DataElementType.SELECT);
						de.setPostBack(true);
					}
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME);
					de.setLabel(this.getLabelTipoInformazioneApplicativaFiltro(policy.getFiltro().getInformazioneApplicativaTipo()));
					de.setValue(policy.getFiltro().getInformazioneApplicativaNome());
					if(tipoFiltro==null || TipoFiltroApplicativo.SOAPACTION_BASED.equals(tipoFiltro)){
						de.setType(DataElementType.HIDDEN);
					}
					else{
						if(protocolloAssociatoFiltroNonSelezionatoUtente) {
							de.setType(DataElementType.TEXT);
						}
						else {
							de.setRequired(true);
							de.setType(DataElementType.TEXT_EDIT);
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
	
	private void addToDatiAttivazioneGroupBy(Vector<DataElement> dati, TipoOperazione tipoOperazione,AttivazionePolicy policy, String nomeSezione,	InfoPolicy infoPolicy) throws Exception {
	
				
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


		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RAGGRUPPAMENTO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// stato
//		addToDatiDataElementStato(dati, ConfigurazioneCostanti.PARAMETRO_CONTROLLO_CONGESTIONE_POLICY_ACTIVE_GROUPBY_ENABLED, 
//				ConfigurazioneCostanti.LABEL_PARAMETRO_CONTROLLO_CONGESTIONE_POLICY_ACTIVE_GROUPBY_ENABLED, policy.getGroupBy().isEnabled(), true);
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
		de.setPostBack(true);
		dati.addElement(de);
		
		if(policy.getGroupBy().isEnabled()){
		
			// Ruolo PdD
			if( policy.getFiltro()==null || 
					policy.getFiltro().isEnabled()==false || 
					policy.getFiltro().getRuoloPorta()==null ||
					RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta())
				){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RUOLO_PDD);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RUOLO_PDD_LABEL);
				de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RUOLO_PDD_NOTE);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(policy.getGroupBy().isRuoloPorta());
				de.setValue(policy.getGroupBy().isRuoloPorta()+"");
				dati.addElement(de);
			}
	
			// Protocollo
			de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PROTOCOLLO);
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PROTOCOLLO);
			if(protocolli.size()>1 && (policy.getFiltro()==null || 
					policy.getFiltro().isEnabled()==false || 
					policy.getFiltro().getProtocollo()==null)){
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
			if( policy.getFiltro()==null || 
					policy.getFiltro().isEnabled()==false || 
					policy.getFiltro().getTipoErogatore()==null ||
					policy.getFiltro().getNomeErogatore()==null
				){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_EROGATORE);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_EROGATORE);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(policy.getGroupBy().isErogatore());
				de.setValue(policy.getGroupBy().isErogatore()+"");
				dati.addElement(de);
			}
						
			// Servizio
			if( policy.getFiltro()==null || 
					policy.getFiltro().isEnabled()==false || 
					policy.getFiltro().getTipoServizio()==null ||
					policy.getFiltro().getNomeServizio()==null
				){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SERVIZIO);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SERVIZIO);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(policy.getGroupBy().isServizio());
				de.setValue(policy.getGroupBy().isServizio()+"");
				dati.addElement(de);
			}
			
			// Azione
			if( policy.getFiltro()==null || 
					policy.getFiltro().isEnabled()==false || 
					policy.getFiltro().getAzione()==null
				){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_AZIONE);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_AZIONE);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(policy.getGroupBy().isAzione());
				de.setValue(policy.getGroupBy().isAzione()+"");
				dati.addElement(de);
			}
			
			// Servizio Applicativo Erogatore
			if( policy.getFiltro()==null || 
					policy.getFiltro().isEnabled()==false || 
					policy.getFiltro().getRuoloPorta()==null ||
					RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta()) ||
					RuoloPolicy.APPLICATIVA.equals(policy.getFiltro().getRuoloPorta())
				){
				if( policy.getFiltro()==null || 
						policy.getFiltro().isEnabled()==false || 
						policy.getFiltro().getServizioApplicativoErogatore()==null
					){
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_EROGATORE);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_EROGATORE);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(policy.getGroupBy().isServizioApplicativoErogatore());
					de.setValue(policy.getGroupBy().isServizioApplicativoErogatore()+"");
					dati.addElement(de);
				}
			}
			
			// Fruitore
			if( policy.getFiltro()==null || 
					policy.getFiltro().isEnabled()==false || 
					policy.getFiltro().getTipoFruitore()==null ||
					policy.getFiltro().getNomeFruitore()==null
				){
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_FRUITORE);
				de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_FRUITORE);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(policy.getGroupBy().isFruitore());
				de.setValue(policy.getGroupBy().isFruitore()+"");
				dati.addElement(de);
			}
			
			// Servizio Applicativo Fruitore
			if( policy.getFiltro()==null || 
					policy.getFiltro().isEnabled()==false || 
					policy.getFiltro().getRuoloPorta()==null ||
					RuoloPolicy.ENTRAMBI.equals(policy.getFiltro().getRuoloPorta()) ||
					RuoloPolicy.DELEGATA.equals(policy.getFiltro().getRuoloPorta())
				){
				if( policy.getFiltro()==null || 
						policy.getFiltro().isEnabled()==false || 
						policy.getFiltro().getServizioApplicativoFruitore()==null
					){
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_FRUITORE);
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_FRUITORE);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(policy.getGroupBy().isServizioApplicativoFruitore());
					de.setValue(policy.getGroupBy().isServizioApplicativoFruitore()+"");
					dati.addElement(de);
				}
			}
			
			if(groupByKey){
			
				// per chiave
				
				if(policy.getGroupBy().isInformazioneApplicativaEnabled()){
					de = new DataElement();
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_NOTE);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
				}
				
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED);
				if(policy.getGroupBy().isInformazioneApplicativaEnabled()){
					de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO);
				}
				else{
					de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_LABEL);
					de.setNote(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_NOTE);
				}
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(policy.getGroupBy().isInformazioneApplicativaEnabled());
				de.setValue(policy.getGroupBy().isInformazioneApplicativaEnabled()+"");
				de.setPostBack(true);
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
					de.setSelected(policy.getGroupBy().getInformazioneApplicativaTipo());
					de.setValue(policy.getGroupBy().getInformazioneApplicativaTipo());
					de.setType(DataElementType.SELECT);
					de.setPostBack(true);
					dati.addElement(de);
					
					de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME);
					de.setLabel(this.getLabelTipoInformazioneApplicativaGroupBy(policy.getGroupBy().getInformazioneApplicativaTipo()));
					de.setValue(policy.getGroupBy().getInformazioneApplicativaNome());
					if(tipoChiaveGroupBy==null || TipoFiltroApplicativo.SOAPACTION_BASED.equals(tipoChiaveGroupBy)){
						de.setType(DataElementType.HIDDEN);
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
	
	private List<String> enrichListConQualsiasi(List<String> l){
		List<String> newList = new ArrayList<String>();
		newList.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI);
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
		case FORM_BASED:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME_NOME;
		case CONTENT_BASED:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME_ESPRESSIONE_XPATH;
		case URLBASED:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME_ESPRESSIONE_REGOLARE;
		case SOAPACTION_BASED:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME_SOAP_ACTION;
		case PLUGIN_BASED:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME_CUSTOM;
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
		case FORM_BASED:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME_NOME;
		case CONTENT_BASED:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME_ESPRESSIONE_XPATH;
		case URLBASED:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME_ESPRESSIONE_REGOLARE;
		case SOAPACTION_BASED:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME_SOAP_ACTION;
		case PLUGIN_BASED:
			return ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME_CUSTOM;
		}
		return null;
	}

	public boolean attivazionePolicyCheckData(TipoOperazione tipoOperazione, ConfigurazioneGenerale configurazioneControlloTraffico, AttivazionePolicy policy, InfoPolicy infoPolicy) throws Exception { 

		if(infoPolicy!=null){
			AttivazionePolicy p = null;
			try {
				p = this.confCore.getGlobalPolicy(policy.getIdPolicy(),policy.getFiltro(), policy.getGroupBy());
			}catch(DriverControlStationNotFound e) {
				//ignore
			}
			if(p!=null){
				if(TipoOperazione.ADD.equals(tipoOperazione) ||	(p.getId()!=null &&	policy.getId()!=null &&	p.getId().longValue()!=policy.getId().longValue())){
					String messaggio = "Esiste già una attivazione per la policy con nome '"+
							policy.getIdPolicy()+"' <br/>e<br/>Collezionamento dei Dati: "+ this.toStringCompactGroupBy(policy.getGroupBy())+"<br/>e<br/>"+	this.toStringFilter(policy.getFiltro());
					this.pd.setMessage(messaggio);
					return false; 
				}
			}
			
			AttivazionePolicy pAlias = null;
			if(policy.getAlias()!=null && !"".equals(policy.getAlias())) {
				try {
					pAlias = this.confCore.getGlobalPolicyByAlias(policy.getAlias());
				}catch(DriverControlStationNotFound e) {
					//ignore
				}
				if(pAlias!=null){
					if(TipoOperazione.ADD.equals(tipoOperazione) || (pAlias.getId()!=null && policy.getId()!=null && pAlias.getId().longValue()!=policy.getId().longValue())){
						String messaggio = "Esiste già una attivazione per la policy con "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS+" '"+	policy.getAlias()+"'";
						this.pd.setMessage(messaggio);
						return false;
					}
				}
			}
		}
		
		return this.checkAttivazionePolicy(configurazioneControlloTraffico,policy,infoPolicy);
	}
	
	public boolean checkAttivazionePolicy(ConfigurazioneGenerale c,AttivazionePolicy policy,InfoPolicy infoPolicy) throws Exception{
		
		// IdPolicy
		if(policy.getIdPolicy()==null || "".equals(policy.getIdPolicy()) || "-".equals(policy.getIdPolicy())){
			String messaggio = "Deve essere selezionata una policy in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID+"'";
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
				
				if(!TipoFiltroApplicativo.SOAPACTION_BASED.equals(tipo)){
					if(policy.getFiltro().getInformazioneApplicativaNome()==null){
						String messaggio = "Deve essere indicato un valore in '"+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED
								+" - "+getLabelTipoInformazioneApplicativaFiltro(policy.getFiltro().getInformazioneApplicativaTipo())+"'";
						this.pd.setMessage(messaggio);
						return false;
					}
				}
								
				if(TipoFiltroApplicativo.CONTENT_BASED.equals(tipo)){
					XPathExpressionEngine xPathEngine = new XPathExpressionEngine();
					try{
						xPathEngine.validate(policy.getFiltro().getInformazioneApplicativaNome());
					}catch(XPathNotValidException notValidException){
						String messaggio = "L'espressione fornita in '"+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED
								+" - "+getLabelTipoInformazioneApplicativaFiltro(policy.getFiltro().getInformazioneApplicativaTipo())+"' non è valida: "+
								notValidException.getMessage();
						this.pd.setMessage(messaggio);
						return false;
					}
				}
				
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
			
			if( !policy.getGroupBy().isRuoloPorta() && 
					!policy.getGroupBy().getProtocollo() &&
					!policy.getGroupBy().getFruitore() &&
					!policy.getGroupBy().getServizioApplicativoFruitore() &&
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
				
				if(!TipoFiltroApplicativo.SOAPACTION_BASED.equals(tipo)){
				
					if(policy.getGroupBy().getInformazioneApplicativaNome()==null){
						String messaggio = "Deve essere indicato un valore in '"+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_NOTE
								+" - "+getLabelTipoInformazioneApplicativaGroupBy(policy.getGroupBy().getInformazioneApplicativaTipo())+"'";
						this.pd.setMessage(messaggio);
						return false;
					}
					
				}
				
				if(TipoFiltroApplicativo.CONTENT_BASED.equals(tipo)){
					XPathExpressionEngine xPathEngine = new XPathExpressionEngine();
					try{
						xPathEngine.validate(policy.getGroupBy().getInformazioneApplicativaNome());
					}catch(XPathNotValidException notValidException){
						String messaggio = "L'espressione fornita in '"+
								ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED_NOTE
								+" - "+getLabelTipoInformazioneApplicativaGroupBy(policy.getGroupBy().getInformazioneApplicativaTipo())+"' non è valida: "+
								notValidException.getMessage();
						this.pd.setMessage(messaggio);
						return false;
					}
				}
				
			}
			
		}
		return true;
	}
	
	public String toStringFilter(AttivazionePolicyFiltro filtro) throws NotFoundException { 
		StringBuffer bf = new StringBuffer("Filtro");
		if(filtro.isEnabled()){

			bf.append(" abilitato con le seguenti impostazioni:");
			
			bf.append("<br/>");
			if( (filtro.getRuoloPorta()==null || RuoloPolicy.ENTRAMBI.equals(filtro.getRuoloPorta())) ){
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD+": Qualsiasi");
			}
			else{
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD+":"+filtro.getRuoloPorta().getValue());
			}
			
			bf.append("<br/>");
			if( (filtro.getProtocollo()==null || "".equals(filtro.getProtocollo())) ){
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO+": Qualsiasi");
			}
			else{
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO+": "+filtro.getProtocollo());
			}
			
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
			
			bf.append("<br/>");
			if( (filtro.getTipoServizio()==null || "".equals(filtro.getTipoServizio())) 
					||
					(filtro.getNomeServizio()==null || "".equals(filtro.getNomeServizio()))){
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO+": Qualsiasi");
			}
			else{
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SERVIZIO+": "+filtro.getTipoServizio()+"/"+filtro.getNomeServizio());
			}
			
			bf.append("<br/>");
			if( (filtro.getAzione()==null || "".equals(filtro.getAzione())) ){
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE+": Qualsiasi");
			}
			else{
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE+": "+filtro.getAzione());
			}
			
			bf.append("<br/>");
			if( (filtro.getServizioApplicativoErogatore()==null || "".equals(filtro.getServizioApplicativoErogatore())) ){
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE+": Qualsiasi");
			}
			else{
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_EROGATORE+": "+filtro.getServizioApplicativoErogatore());
			}
			
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
			
			bf.append("<br/>");
			if( (filtro.getServizioApplicativoFruitore()==null || "".equals(filtro.getServizioApplicativoFruitore())) ){
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE+": Qualsiasi");
			}
			else{
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE+": "+filtro.getServizioApplicativoFruitore());
			}
			
			if(filtro.isInformazioneApplicativaEnabled()){
				
				bf.append("<br/>");
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED+": Abilitato");
				
				bf.append("<br/>");
				bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO+": "+filtro.getInformazioneApplicativaTipo());
				
				bf.append("<br/>");
				TipoFiltroApplicativo tipoFiltroApplicativo = TipoFiltroApplicativo.toEnumConstant(filtro.getInformazioneApplicativaTipo(), true);
				switch (tipoFiltroApplicativo) {
				case HEADER_BASED:
				case FORM_BASED:
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME_NOME+": "+filtro.getInformazioneApplicativaNome());
					break;
				case URLBASED:
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME_ESPRESSIONE_REGOLARE+": "+filtro.getInformazioneApplicativaNome());
					break;
				case CONTENT_BASED:
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME_ESPRESSIONE_XPATH+": "+filtro.getInformazioneApplicativaNome());
					break;
				case SOAPACTION_BASED:
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME_SOAP_ACTION+": "+filtro.getInformazioneApplicativaNome());
					break;
				case PLUGIN_BASED:
					bf.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME_CUSTOM+": "+filtro.getInformazioneApplicativaNome());
					break;
				}
				
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
	
	public String eseguiResetJmx(TipoOperazione tipoOperazione) throws Exception{
		try{
			List<String> aliases = this.core.getJmxPdD_aliases();
			if(aliases==null || aliases.size()<=0){
				throw new Exception("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}
			for (String alias : aliases) {
				
				String resultReset = null;
				String idAllPolicy = null;
				try{
					idAllPolicy = ConfigurazionePdD._getKey_ElencoIdPolicyAttive();
					resultReset = this.core.invokeJMXMethod(this.core.getGestoreRisorseJMX(alias),alias,JMXConstants.JMX_TYPE, CostantiPdD.JMX_CONFIGURAZIONE_PDD, JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT, idAllPolicy);
					this.log.debug("reset["+idAllPolicy+"] "+resultReset);
				}catch(Exception e){
					String errorMessage = "Errore durante l'invocazione dell'operazione ["+JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT+"] sulla risorsa ["+
							CostantiPdD.JMX_CONFIGURAZIONE_PDD+"] (param:"+idAllPolicy+"): "+e.getMessage();
					this.log.error(errorMessage,e);
					resultReset = errorMessage;
				}
				
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
			if(!this.core.isTokenPolicyForceIdEnabled())
				lstLabels.add(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO);
			
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
						de.setValue(policy.getTipo());
						e.addElement(de);
					}
					
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
				de.setType(DataElementType.TEXT);
				de.setValue(tipo);
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(tipo);
		}
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
			
			// Tipo
			if(StringUtils.isEmpty(tipo)  || CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(tipo)){
				String messaggio = "Deve essere indicato un valore in '"+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO+"'";
				this.pd.setMessage(messaggio);
				return false;
			}
		
			try {
				// check duplicati per tipologia
				this.confCore.getGenericProperties(nome, tipologia);
				String messaggio = "&Egrave; gi&agrave; presente un Policy Gestione Token con nome " + nome ;
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
}
