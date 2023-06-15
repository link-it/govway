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
package org.openspcoop2.monitor.engine.statistic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.generic_project.expression.Index;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.InstanceProperties;

/**
 * StatisticsForceIndexConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsForceIndexConfig {

	/** Lista di indici forzati */
	private List<Index> transazioniForceIndexGroupByNumeroDimensione = null;
	private List<Index> transazioniForceIndexGroupByLatenze = null;
	private List<Index> transazioniForceIndexGroupByCustomNumeroDimensione = null;
	private List<Index> transazioniForceIndexGroupByCustomLatenze = null;
	
	public StatisticsForceIndexConfig(){}
	
	private static final String P_REPO = "statistiche.generazione.forceIndex.repository";
	private static final String P_GROUPBY_NUMERO_DIMENSIONE = "statistiche.generazione.forceIndex.groupBy.numero_dimensione";
	private static final String P_GROUPBY_LATENZA = "statistiche.generazione.forceIndex.groupBy.latenza";
	private static final String P_GROUPBY_CUSTOM_NUMERO_DIMENSIONE = "statistiche.generazione.forceIndex.groupBy.custom.numero_dimensione";
	private static final String P_GROUPBY_CUSTOM_LATENZA = "statistiche.generazione.forceIndex.groupBy.custom.latenza";
	
	public StatisticsForceIndexConfig(Properties p) throws IOException {
		
		String tmpRepo = p.getProperty(StatisticsForceIndexConfig.P_REPO);
		Properties pRepoExternal = this.getExternalRepository(tmpRepo);

		String groupByNumeroDimensione = p.getProperty(StatisticsForceIndexConfig.P_GROUPBY_NUMERO_DIMENSIONE);
		if(groupByNumeroDimensione!=null){
			groupByNumeroDimensione = groupByNumeroDimensione.trim();
		}
		this.transazioniForceIndexGroupByNumeroDimensione = this.getIndexList(StatisticsForceIndexConfig.P_GROUPBY_NUMERO_DIMENSIONE, groupByNumeroDimensione, pRepoExternal);
		
		String groupByLatenza = p.getProperty(StatisticsForceIndexConfig.P_GROUPBY_LATENZA);
		if(groupByLatenza!=null){
			groupByLatenza = groupByLatenza.trim();
		}
		this.transazioniForceIndexGroupByLatenze = this.getIndexList(StatisticsForceIndexConfig.P_GROUPBY_LATENZA, groupByLatenza, pRepoExternal);
		
		String groupCustomByNumeroDimensione = p.getProperty(StatisticsForceIndexConfig.P_GROUPBY_CUSTOM_NUMERO_DIMENSIONE);
		if(groupCustomByNumeroDimensione!=null){
			groupCustomByNumeroDimensione = groupCustomByNumeroDimensione.trim();
		}
		this.transazioniForceIndexGroupByCustomNumeroDimensione = this.getIndexList(StatisticsForceIndexConfig.P_GROUPBY_CUSTOM_NUMERO_DIMENSIONE, groupCustomByNumeroDimensione, pRepoExternal);
		
		String groupCustomByLatenza = p.getProperty(StatisticsForceIndexConfig.P_GROUPBY_CUSTOM_LATENZA);
		if(groupCustomByLatenza!=null){
			groupCustomByLatenza = groupCustomByLatenza.trim();
		}
		this.transazioniForceIndexGroupByCustomLatenze = this.getIndexList(StatisticsForceIndexConfig.P_GROUPBY_CUSTOM_LATENZA, groupCustomByLatenza, pRepoExternal);
		
	}
	
	public StatisticsForceIndexConfig(InstanceProperties p) throws IOException, UtilsException {
		
		String tmpRepo = p.getValueConvertEnvProperties(StatisticsForceIndexConfig.P_REPO);
		Properties pRepoExternal = this.getExternalRepository(tmpRepo);
		
		String groupByNumeroDimensione = p.getValueConvertEnvProperties(StatisticsForceIndexConfig.P_GROUPBY_NUMERO_DIMENSIONE);
		if(groupByNumeroDimensione!=null){
			groupByNumeroDimensione = groupByNumeroDimensione.trim();
		}
		this.transazioniForceIndexGroupByNumeroDimensione = this.getIndexList(StatisticsForceIndexConfig.P_GROUPBY_NUMERO_DIMENSIONE, groupByNumeroDimensione, pRepoExternal);
		
		String groupByLatenza = p.getValueConvertEnvProperties(StatisticsForceIndexConfig.P_GROUPBY_LATENZA);
		if(groupByLatenza!=null){
			groupByLatenza = groupByLatenza.trim();
		}
		this.transazioniForceIndexGroupByLatenze = this.getIndexList(StatisticsForceIndexConfig.P_GROUPBY_LATENZA, groupByLatenza, pRepoExternal);
		
		String groupCustomByNumeroDimensione = p.getValueConvertEnvProperties(StatisticsForceIndexConfig.P_GROUPBY_CUSTOM_NUMERO_DIMENSIONE);
		if(groupCustomByNumeroDimensione!=null){
			groupCustomByNumeroDimensione = groupCustomByNumeroDimensione.trim();
		}
		this.transazioniForceIndexGroupByCustomNumeroDimensione = this.getIndexList(StatisticsForceIndexConfig.P_GROUPBY_CUSTOM_NUMERO_DIMENSIONE, groupCustomByNumeroDimensione, pRepoExternal);
		
		String groupCustomByLatenza = p.getValueConvertEnvProperties(StatisticsForceIndexConfig.P_GROUPBY_CUSTOM_LATENZA);
		if(groupCustomByLatenza!=null){
			groupCustomByLatenza = groupCustomByLatenza.trim();
		}
		this.transazioniForceIndexGroupByCustomLatenze = this.getIndexList(StatisticsForceIndexConfig.P_GROUPBY_CUSTOM_LATENZA, groupCustomByLatenza, pRepoExternal);
		
	}
	
	private Properties getExternalRepository(String tmpRepo) throws IOException{
		Properties pRepoExternal = null;
		if(tmpRepo!=null){
			File f = new File(tmpRepo);
			try (InputStream is = f.exists() ? new FileInputStream(f) : StatisticsForceIndexConfig.class.getResourceAsStream(tmpRepo)){ // provo a cercarlo nel classpath se non e' un file esistente
				if(is!=null){
					pRepoExternal = new Properties();
					pRepoExternal.load(is);
				}
			}
		}
		return  pRepoExternal;
	}
	
	private List<Index> getIndexList(String propertyName,String propertyValue, Properties externalRepository) {
		
		String s = null;
		if(externalRepository!=null){
			String tmp = externalRepository.getProperty(propertyName);
			if(tmp!=null){
				s = tmp.trim();
			}
		}
		if(s==null){
			// provo a cercarlo nel file monitor
			s = propertyValue;
		}
		
		List<Index> l = null;
		if(s!=null){
			l = new ArrayList<>();
			if(s.contains(",")){
				String [] split = s.split(",");
				for (int i = 0; i < split.length; i++) {
					l.add(new Index(Transazione.model(),split[i]));
				}
			}
			else{
				l.add(new Index(Transazione.model(),s));
			}
			return l;
		}
		return l;
	}
	

	public List<Index> getTransazioniForceIndexGroupByNumeroDimensione() {
		return this.transazioniForceIndexGroupByNumeroDimensione;
	}

	public void setTransazioniForceIndexGroupByNumeroDimensione(
			List<Index> transazioniForceIndexGroupByNumeroDimensione) {
		this.transazioniForceIndexGroupByNumeroDimensione = transazioniForceIndexGroupByNumeroDimensione;
	}

	public List<Index> getTransazioniForceIndexGroupByLatenze() {
		return this.transazioniForceIndexGroupByLatenze;
	}

	public void setTransazioniForceIndexGroupByLatenze(List<Index> transazioniForceIndexGroupByLatenze) {
		this.transazioniForceIndexGroupByLatenze = transazioniForceIndexGroupByLatenze;
	}
	
	public List<Index> getTransazioniForceIndexGroupByCustomNumeroDimensione() {
		return this.transazioniForceIndexGroupByCustomNumeroDimensione;
	}

	public void setTransazioniForceIndexGroupByCustomNumeroDimensione(
			List<Index> transazioniForceIndexGroupByCustomNumeroDimensione) {
		this.transazioniForceIndexGroupByCustomNumeroDimensione = transazioniForceIndexGroupByCustomNumeroDimensione;
	}

	public List<Index> getTransazioniForceIndexGroupByCustomLatenze() {
		return this.transazioniForceIndexGroupByCustomLatenze;
	}

	public void setTransazioniForceIndexGroupByCustomLatenze(List<Index> transazioniForceIndexGroupByCustomLatenze) {
		this.transazioniForceIndexGroupByCustomLatenze = transazioniForceIndexGroupByCustomLatenze;
	}


}
