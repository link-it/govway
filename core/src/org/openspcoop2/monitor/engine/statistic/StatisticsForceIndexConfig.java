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
package org.openspcoop2.monitor.engine.statistic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.generic_project.expression.Index;
import org.openspcoop2.utils.properties.InstanceProperties;
import org.openspcoop2.core.transazioni.Transazione;

/**
 * StatisticsForceIndexConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsForceIndexConfig {

	/** Lista di indici forzati */
	private List<Index> transazioniForceIndexGroupBy_numero_dimesione = null;
	private List<Index> transazioniForceIndexGroupBy_latenze = null;
	private List<Index> transazioniForceIndexGroupBy_custom_numero_dimesione = null;
	private List<Index> transazioniForceIndexGroupBy_custom_latenze = null;
	
	public StatisticsForceIndexConfig(){}
	
	private final static String pRepo = "statistiche.generazione.forceIndex.repository";
	private final static String pGroupBy_numero_dimensione = "statistiche.generazione.forceIndex.groupBy.numero_dimensione";
	private final static String pGroupBy_latenza = "statistiche.generazione.forceIndex.groupBy.latenza";
	private final static String pGroupBy_custom_numero_dimensione = "statistiche.generazione.forceIndex.groupBy.custom.numero_dimensione";
	private final static String pGroupBy_custom_latenza = "statistiche.generazione.forceIndex.groupBy.custom.latenza";
	
	public StatisticsForceIndexConfig(Properties p) throws Exception{
		
		String tmpRepo = p.getProperty(StatisticsForceIndexConfig.pRepo);
		Properties pRepoExternal = this.getExternalRepository(tmpRepo);

		
		String groupByNumeroDimensione = p.getProperty(StatisticsForceIndexConfig.pGroupBy_numero_dimensione);
		if(groupByNumeroDimensione!=null){
			groupByNumeroDimensione = groupByNumeroDimensione.trim();
		}
		this.transazioniForceIndexGroupBy_numero_dimesione = this.getIndexList(StatisticsForceIndexConfig.pGroupBy_numero_dimensione, groupByNumeroDimensione, pRepoExternal);
		
		String groupByLatenza = p.getProperty(StatisticsForceIndexConfig.pGroupBy_latenza);
		if(groupByLatenza!=null){
			groupByLatenza = groupByLatenza.trim();
		}
		this.transazioniForceIndexGroupBy_latenze = this.getIndexList(StatisticsForceIndexConfig.pGroupBy_latenza, groupByLatenza, pRepoExternal);
		
		String groupCustomByNumeroDimensione = p.getProperty(StatisticsForceIndexConfig.pGroupBy_custom_numero_dimensione);
		if(groupCustomByNumeroDimensione!=null){
			groupCustomByNumeroDimensione = groupCustomByNumeroDimensione.trim();
		}
		this.transazioniForceIndexGroupBy_custom_numero_dimesione = this.getIndexList(StatisticsForceIndexConfig.pGroupBy_custom_numero_dimensione, groupCustomByNumeroDimensione, pRepoExternal);
		
		String groupCustomByLatenza = p.getProperty(StatisticsForceIndexConfig.pGroupBy_custom_latenza);
		if(groupCustomByLatenza!=null){
			groupCustomByLatenza = groupCustomByLatenza.trim();
		}
		this.transazioniForceIndexGroupBy_custom_latenze = this.getIndexList(StatisticsForceIndexConfig.pGroupBy_custom_latenza, groupCustomByLatenza, pRepoExternal);
		
	}
	
	public StatisticsForceIndexConfig(InstanceProperties p) throws Exception{
		
		String tmpRepo = p.getValue_convertEnvProperties(StatisticsForceIndexConfig.pRepo);
		Properties pRepoExternal = this.getExternalRepository(tmpRepo);
		
		
		
		String groupByNumeroDimensione = p.getValue_convertEnvProperties(StatisticsForceIndexConfig.pGroupBy_numero_dimensione);
		if(groupByNumeroDimensione!=null){
			groupByNumeroDimensione = groupByNumeroDimensione.trim();
		}
		this.transazioniForceIndexGroupBy_numero_dimesione = this.getIndexList(StatisticsForceIndexConfig.pGroupBy_numero_dimensione, groupByNumeroDimensione, pRepoExternal);
		
		String groupByLatenza = p.getValue_convertEnvProperties(StatisticsForceIndexConfig.pGroupBy_latenza);
		if(groupByLatenza!=null){
			groupByLatenza = groupByLatenza.trim();
		}
		this.transazioniForceIndexGroupBy_latenze = this.getIndexList(StatisticsForceIndexConfig.pGroupBy_latenza, groupByLatenza, pRepoExternal);
		
		String groupCustomByNumeroDimensione = p.getValue_convertEnvProperties(StatisticsForceIndexConfig.pGroupBy_custom_numero_dimensione);
		if(groupCustomByNumeroDimensione!=null){
			groupCustomByNumeroDimensione = groupCustomByNumeroDimensione.trim();
		}
		this.transazioniForceIndexGroupBy_custom_numero_dimesione = this.getIndexList(StatisticsForceIndexConfig.pGroupBy_custom_numero_dimensione, groupCustomByNumeroDimensione, pRepoExternal);
		
		String groupCustomByLatenza = p.getValue_convertEnvProperties(StatisticsForceIndexConfig.pGroupBy_custom_latenza);
		if(groupCustomByLatenza!=null){
			groupCustomByLatenza = groupCustomByLatenza.trim();
		}
		this.transazioniForceIndexGroupBy_custom_latenze = this.getIndexList(StatisticsForceIndexConfig.pGroupBy_custom_latenza, groupCustomByLatenza, pRepoExternal);
		
	}
	
	private Properties getExternalRepository(String tmpRepo)throws Exception{
		Properties pRepoExternal = null;
		if(tmpRepo!=null){
			InputStream is = null;
			File f = new File(tmpRepo);
			if(f.exists()){
				is = new FileInputStream(f);
			}
			else{
				// provo a cercarlo nel classpath
				is = StatisticsForceIndexConfig.class.getResourceAsStream(tmpRepo);
			}
			if(is!=null){
				pRepoExternal = new Properties();
				pRepoExternal.load(is);
				is.close();
	 		}
		}
		return  pRepoExternal;
	}
	
	private List<Index> getIndexList(String propertyName,String propertyValue, Properties externalRepository) throws Exception{
		
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
		
		if(s!=null){
			List<Index> l = new ArrayList<Index>();
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
		return null;
	}
	

	public List<Index> getTransazioniForceIndexGroupBy_numero_dimesione() {
		return this.transazioniForceIndexGroupBy_numero_dimesione;
	}

	public void setTransazioniForceIndexGroupBy_numero_dimesione(
			List<Index> transazioniForceIndexGroupBy_numero_dimesione) {
		this.transazioniForceIndexGroupBy_numero_dimesione = transazioniForceIndexGroupBy_numero_dimesione;
	}

	public List<Index> getTransazioniForceIndexGroupBy_latenze() {
		return this.transazioniForceIndexGroupBy_latenze;
	}

	public void setTransazioniForceIndexGroupBy_latenze(List<Index> transazioniForceIndexGroupBy_latenze) {
		this.transazioniForceIndexGroupBy_latenze = transazioniForceIndexGroupBy_latenze;
	}
	
	public List<Index> getTransazioniForceIndexGroupBy_custom_numero_dimesione() {
		return this.transazioniForceIndexGroupBy_custom_numero_dimesione;
	}

	public void setTransazioniForceIndexGroupBy_custom_numero_dimesione(
			List<Index> transazioniForceIndexGroupBy_custom_numero_dimesione) {
		this.transazioniForceIndexGroupBy_custom_numero_dimesione = transazioniForceIndexGroupBy_custom_numero_dimesione;
	}

	public List<Index> getTransazioniForceIndexGroupBy_custom_latenze() {
		return this.transazioniForceIndexGroupBy_custom_latenze;
	}

	public void setTransazioniForceIndexGroupBy_custom_latenze(List<Index> transazioniForceIndexGroupBy_custom_latenze) {
		this.transazioniForceIndexGroupBy_custom_latenze = transazioniForceIndexGroupBy_custom_latenze;
	}


}
