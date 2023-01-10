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

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.StatisticException;
import org.openspcoop2.monitor.sdk.statistic.IStatistic;
import org.openspcoop2.monitor.sdk.statistic.StatisticResourceFilter;
import org.openspcoop2.monitor.sdk.statistic.StatisticFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.utils.TipiDatabase;


/**
 * CustomStatisticsSdkGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CustomStatisticsSdkGenerator implements IStatistic {

	private StatisticBean statistic;
 	
	private StatisticType statisticType;
	
	private AbstractStatistiche abstractStatisticheCore;
	
	protected CustomStatisticsSdkGenerator(StatisticBean stat, StatisticType statisticType, AbstractStatistiche abstractStatisticheCore) {
		this.statistic = stat;
		this.statisticType = statisticType;
		this.abstractStatisticheCore = abstractStatisticheCore;
	}

	@Override
	public TipoPdD getTipoPorta(){
		return this.statistic.getTipoPorta();
	}
	
	@Override
	public String getIdentificativoPorta(){
		return this.statistic.getIdPorta();
	}

	@Override
	public String getTipoSoggettoMittente() {
		if(this.statistic.getMittente()!=null)
			return this.statistic.getMittente().getTipo();
		else
			return null;
	}
	@Override
	public String getSoggettoMittente() {
		if(this.statistic.getMittente()!=null)
			return this.statistic.getMittente().getNome();
		else
			return null;
	}
	
	@Override
	public String getTipoSoggettoDestinatario() {
		if(this.statistic.getDestinatario()!=null)
			return this.statistic.getDestinatario().getTipo();
		else
			return null;
	}
	@Override
	public String getSoggettoDestinatario() {
		if(this.statistic.getDestinatario()!=null)
			return this.statistic.getDestinatario().getNome();
		else
			return null;
	}
	
	@Override
	public String getTipoServizio() {
		return this.statistic.getTipoServizio();
	}
	@Override
	public String getServizio() {
		return this.statistic.getServizio();
	}
	@Override
	public Integer getVersioneServizio() {
		return this.statistic.getVersioneServizio();
	}
	
	@Override
	public String getAzione() {
		return this.statistic.getAzione();
	}
	
	@Override
	public String getServizioApplicativo() {
		return this.statistic.getServizioApplicativo();
	}
	
	@Override
	public String getIdentificativoAutenticato() {
		return this.statistic.getTrasportoMittente();
	}
	
	@Override
	public String getTokenInfoIssuer(){
		return this.statistic.getTokenIssuer();
	}
	@Override
	public String getTokenInfoClientId(){
		return this.statistic.getTokenClientId();
	}	
	@Override
	public String getTokenInfoSubject(){
		return this.statistic.getTokenSubject();
	}	
	@Override
	public String getTokenInfoUsername(){
		return this.statistic.getTokenUsername();
	}	
	@Override
	public String getTokenInfoEmail(){
		return this.statistic.getTokenMail();
	}
	
	@Override
	public String getClientAddress(){
		return this.statistic.getClientAddress();
	}
	
	@Override
	public String getGruppo(){
		return this.statistic.getGruppo();
	}
	
	@Override
	public String getApi(){
		return this.statistic.getApi();
	}
	
	@Override
	public String getClusterId() {
		return this.statistic.getClusterId();
	}
	
	@Override
	public TipiDatabase getDatabaseType(){
		return this.abstractStatisticheCore.getDatabaseType();
	}
	
	@Override
	public StatisticType getStatisticType(){
		return this.statisticType;
	}
	
	@Override
	public Logger getLogger(){
		return this.abstractStatisticheCore.logger;
	}
	
	@Override
	public DAOFactory getDAOFactory() throws StatisticException{
		try{
			return DAOFactory.getInstance(this.abstractStatisticheCore.logger);
		}catch(Exception e){
			throw new StatisticException(e.getMessage(),e);
		}
	}
	
	public StatisticBean getStatistic() {
		return this.statistic;
	}

	public AbstractStatistiche getAbstractStatisticheCore() {
		return this.abstractStatisticheCore;
	}
	
	
	private int numeroVolteCreazioneStatisticaSemplice = 0;
	
	@Override
	public void createStatistics(String idRisorsa) throws StatisticException {
		this.createStatistics(idRisorsa, new StatisticResourceFilter[0]);
	}
	
	@Override
	public void createStatistics(String idRisorsa, StatisticResourceFilter... idRisorseFiltri)
			throws StatisticException {

		if(idRisorsa==null){
			throw new StatisticException("IdResource undefined");
		}
		if(this.numeroVolteCreazioneStatisticaSemplice>0){
			throw new StatisticException("It is not allowed to generate more of a statistic without providing the identifier of the statistic (use method createStatistics(idStatistic,String idResource, ...))");
		}
		
		RisorsaSemplice risorsa = new RisorsaSemplice();
		risorsa.setIdRisorsa(idRisorsa);
		if(idRisorseFiltri!=null && idRisorseFiltri.length>0){
			for (int i = 0; i < idRisorseFiltri.length; i++) {
				risorsa.getFiltri().add(idRisorseFiltri[i]);
			}
		}
		
		this.abstractStatisticheCore.generaStatisticaPersonalizzata(this.statistic, risorsa);
		
		this.numeroVolteCreazioneStatisticaSemplice++;
 	
	}
	
	
	private List<String> idStatistiche = new ArrayList<String>();
	private List<String> idRisorse = new ArrayList<String>();
	private Map<String, String> mapIdRisorsaToIdStatistica = new HashMap<String, String>();
	
	@Override
	public void createStatistics(String idStatistica,String idRisorsa) throws StatisticException{
		this.createStatistics(idStatistica, idRisorsa, new StatisticResourceFilter[0]);
	}
		
	@Override
	public void createStatistics(String idStatistica,String idRisorsa, StatisticResourceFilter ... idRisorseFiltri) throws StatisticException{
		
		if(idStatistica==null || "".equals(idStatistica)){
			throw new StatisticException("IdStatistic undefined");
		}
		if(this.idStatistiche.contains(idStatistica)){
			throw new StatisticException("IdStatistic ["+idStatistica+"] already used");
		}
		if(idRisorsa==null || "".equals(idRisorsa)){
			throw new StatisticException("IdResource undefined");
		}
		if(this.idRisorse.contains(idRisorsa)){
			throw new StatisticException("IdResource ["+idRisorsa+"] already used for statistic with id: "+this.mapIdRisorsaToIdStatistica.get(idRisorsa));
		}

		
		RisorsaSemplice risorsa = new RisorsaSemplice();
		risorsa.setIdStatistica(idStatistica);
		risorsa.setIdRisorsa(idRisorsa);
		if(idRisorseFiltri!=null && idRisorseFiltri.length>0){
			for (int i = 0; i < idRisorseFiltri.length; i++) {
				risorsa.getFiltri().add(idRisorseFiltri[i]);
			}
		}
		
		this.abstractStatisticheCore.generaStatisticaPersonalizzata(this.statistic, risorsa);
		
		this.idStatistiche.add(idStatistica);
		this.idRisorse.add(idRisorsa);
		this.mapIdRisorsaToIdStatistica.put(idRisorsa, idStatistica);
	}
	
	
	
	
	//private List<String> valoreRisorseAggregate = new ArrayList<String>();
	
	@Override
	public void createStatistics(IFilter filtro, String valoreRisorsaAggregata)
			throws StatisticException {
		this.createStatistics(filtro, valoreRisorsaAggregata, new StatisticFilter[0]);
	}

	@Override
	public void createStatistics(IFilter filtro, String valoreRisorsaAggregata,
			StatisticFilter... filtriRicerca) throws StatisticException {
		
		if(filtro==null){
			throw new StatisticException("Filter undefined");
		}
		if(valoreRisorsaAggregata==null || "".equals(valoreRisorsaAggregata)){
			throw new StatisticException("AggregateResourceValue undefined");
		}
		// Posso implementare più statistiche con lo stesso valore di risorsa aggregata se cambio il filtro
		// TODO: Lavorare sul controllare che tutta la coppia valoreRisorsaAggregata - filtro non sia già usata
//		if(this.valoreRisorseAggregate.contains(valoreRisorsaAggregata)){
//			throw new StatisticException("AggregateResourceValue ["+valoreRisorsaAggregata+"] already used");
//		}
		
		RisorsaAggregata risorsa = new RisorsaAggregata();
		risorsa.setFiltro(filtro);
		try{
			risorsa.setValoreRisorsaAggregata(valoreRisorsaAggregata);
		}catch(Exception e){
			throw new StatisticException(e.getMessage(),e);
		}
		if(filtriRicerca!=null && filtriRicerca.length>0){
			for (int i = 0; i < filtriRicerca.length; i++) {
				risorsa.getFiltri().add(filtriRicerca[i]);
			}
		}
		
		this.abstractStatisticheCore.generaStatisticaPersonalizzata(this.statistic, risorsa);
		
		//this.valoreRisorseAggregate.add(valoreRisorsaAggregata);
	}
	
	
	
	
	public void createStatisticsByStato(){
		this.abstractStatisticheCore.generaStatisticaPersonalizzataByStato(this.statistic);
	}
}
