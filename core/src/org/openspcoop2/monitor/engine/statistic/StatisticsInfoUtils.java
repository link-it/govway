package org.openspcoop2.monitor.engine.statistic;

import org.openspcoop2.core.statistiche.StatisticaInfo;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import org.openspcoop2.core.statistiche.dao.IStatisticaInfoService;
import org.openspcoop2.core.statistiche.dao.IStatisticaInfoServiceSearch;

import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;

/**
 * StatisticsInfoUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsInfoUtils {

	
	public static Date readDataUltimaGenerazioneStatistiche(IStatisticaInfoServiceSearch statisticaInfoSearchDAO,TipoIntervalloStatistico tipoStatistica,Logger logger) throws NotFoundException  {



		try{
			IExpression expr = statisticaInfoSearchDAO.newExpression();

			expr.equals(StatisticaInfo.model().TIPO_STATISTICA, tipoStatistica);

			StatisticaInfo info = statisticaInfoSearchDAO.find(expr);

			return info.getDataUltimaGenerazione();

		} catch (ServiceException e){
			logger.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			logger.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			logger.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			logger.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			logger.error(e.getMessage(), e);
		}

		return new Date(0);

	}
	
	public static  void updateDataUltimaGenerazioneStatistiche(IStatisticaInfoServiceSearch statisticaInfoSearchDAO,IStatisticaInfoService statisticaInfoDAO,
			TipoIntervalloStatistico tipoStatistica,Logger logger,Date date) throws Exception{

		try{
			IExpression expr = statisticaInfoSearchDAO.newExpression();

			expr.equals(StatisticaInfo.model().TIPO_STATISTICA, tipoStatistica);

			StatisticaInfo info = statisticaInfoSearchDAO.find(expr);

			info.setDataUltimaGenerazione(date);

			statisticaInfoDAO.update(info);


		} catch (ServiceException e){
			logger.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			logger.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			logger.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			logger.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			logger.error(e.getMessage(), e);

			// non ho trovato nessuna data valida, inserisco l'ultima generata

			StatisticaInfo info = new StatisticaInfo();
			info.setDataUltimaGenerazione(date);
			info.setTipoStatistica(tipoStatistica);

			statisticaInfoDAO.create(info);

		} catch (MultipleResultException e) {
			logger.error(e.getMessage(), e);
		}

	}
	
}
