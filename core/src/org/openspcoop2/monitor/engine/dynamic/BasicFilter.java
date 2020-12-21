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
package org.openspcoop2.monitor.engine.dynamic;

import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.condition.StatisticsContext;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.plugins.ISearchArguments;
import org.openspcoop2.monitor.sdk.plugins.ISearchProcessing;
import org.openspcoop2.monitor.sdk.plugins.IStatisticProcessing;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * BasicFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicFilter implements IDynamicFilter{
	
	private static Logger log = LoggerWrapperFactory.getLogger(BasicFilter.class);
	
	private String tipoPlugin;
	private String tipo;
	private String className;
	
	protected BasicFilter(String tipoPlugin, String tipo, String className) {
		this.tipoPlugin = tipoPlugin;
		this.tipo = tipo;
		this.className = className;
	}
	
	@Override
	public org.openspcoop2.monitor.sdk.condition.IFilter createConditionFilter(Context context) throws SearchException {
		try{
			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(this.tipoPlugin, this.tipo, this.className, BasicFilter.log);
			
			Object obj = bl.newInstance();
			
			if(obj instanceof ISearchProcessing){
				return ((ISearchProcessing) obj).createSearchFilter(context);
			}else if(obj instanceof IStatisticProcessing && context instanceof StatisticsContext	){
					return ((IStatisticProcessing) obj).createSearchFilter((StatisticsContext)context); 
			}else{
				String iface = ISearchArguments.class.getName();
				throw new Exception("La classe ["+this.className+"] non implementa l'interfaccia ["+iface+"]");
			}
		}catch(ClassNotFoundException cnfe){
			throw new SearchException("Impossibile caricare il plugin. La classe indicata ["+this.className+"] non esiste.");
		}catch (Exception e) {
			BasicFilter.log.error(e.getMessage(),e);
			throw new SearchException("Si e' verificato un errore: "+e.getMessage());
		}
	}

}
