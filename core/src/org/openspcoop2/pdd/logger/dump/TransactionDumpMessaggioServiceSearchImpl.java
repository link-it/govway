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

package org.openspcoop2.pdd.logger.dump;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCDumpMessaggioServiceSearchImpl;
import org.openspcoop2.core.transazioni.utils.PropertiesSerializator;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.slf4j.Logger;

/**     
 * TransactionDumpMessaggioServiceSearchImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionDumpMessaggioServiceSearchImpl extends JDBCDumpMessaggioServiceSearchImpl {

	public TransactionDumpMessaggioServiceSearchImpl() {
		super();
	}

	@Override
	protected DumpMessaggio getEngine(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws Exception {
		
		DumpMessaggio dumpMessaggio = super.getEngine(jdbcProperties, log, connection, sqlQueryObject, tableId, idMappingResolutionBehaviour);
	
		readMultipartHeaderExt(dumpMessaggio);
		
		readHeaderExt(dumpMessaggio);
		
		readAllegatiExt(dumpMessaggio);
		
		return dumpMessaggio;
		
	}
	
	private void readMultipartHeaderExt(DumpMessaggio dumpMessaggio) throws CoreException {
		if(dumpMessaggio.getMultipartHeaderExt()!=null && !StringUtils.isEmpty(dumpMessaggio.getMultipartHeaderExt())) {
			Map<String, List<String>> headers = PropertiesSerializator.convertoFromDBColumnValue(dumpMessaggio.getMultipartHeaderExt());
			if(headers!=null && headers.size()>0){
				readMultipartHeaderExt(dumpMessaggio, headers);
			}
		}
	}
	private void readMultipartHeaderExt(DumpMessaggio dumpMessaggio, Map<String, List<String>> headers) {
		for (Map.Entry<String,List<String>> entry : headers.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			if(values!=null && !values.isEmpty()) {
				for (String value : values) {
					DumpMultipartHeader headerMultipart = new DumpMultipartHeader();
					headerMultipart.setNome(key);
					headerMultipart.setValore(value);
					headerMultipart.setDumpTimestamp(dumpMessaggio.getDumpTimestamp());
					dumpMessaggio.addMultipartHeader(headerMultipart);
				}
			}
		}
	}
	
	private void readHeaderExt(DumpMessaggio dumpMessaggio) throws CoreException {
		if(dumpMessaggio.getHeaderExt()!=null && !StringUtils.isEmpty(dumpMessaggio.getHeaderExt())) {
			Map<String, List<String>> headers = PropertiesSerializator.convertoFromDBColumnValue(dumpMessaggio.getHeaderExt());
			if(headers!=null && headers.size()>0){
				readHeaderExt(dumpMessaggio, headers);
			}
		}
	}
	private void readHeaderExt(DumpMessaggio dumpMessaggio, Map<String, List<String>> headers) {
		for (Map.Entry<String,List<String>> entry : headers.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			if(values!=null && !values.isEmpty()) {
				for (String value : values) {					
					DumpHeaderTrasporto headerTrasporto = new DumpHeaderTrasporto();
					headerTrasporto.setNome(key);
					headerTrasporto.setValore(value);
					headerTrasporto.setDumpTimestamp(dumpMessaggio.getDumpTimestamp());
					dumpMessaggio.addHeaderTrasporto(headerTrasporto);
				}
			}
		}
	}
	
	private void readAllegatiExt(DumpMessaggio dumpMessaggio) throws CoreException {
		if(dumpMessaggio.getAllegatoList()!=null && !dumpMessaggio.getAllegatoList().isEmpty()) {
			for (DumpAllegato dumpAllegato : dumpMessaggio.getAllegatoList()) {
				
				if(dumpAllegato.getHeaderExt()!=null && !StringUtils.isEmpty(dumpAllegato.getHeaderExt())) {
					Map<String, List<String>> headers = PropertiesSerializator.convertoFromDBColumnValue(dumpAllegato.getHeaderExt());
					if(headers!=null && headers.size()>0){
						readAllegatiExt(dumpMessaggio, dumpAllegato, headers);
					}
				}
				
			}
		}
	}
	private void readAllegatiExt(DumpMessaggio dumpMessaggio,DumpAllegato dumpAllegato, Map<String, List<String>> headers) {
		for (Map.Entry<String,List<String>> entry : headers.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			if(values!=null && !values.isEmpty()) {
				for (String value : values) {					
					DumpHeaderAllegato headerAllegato = new DumpHeaderAllegato();
					headerAllegato.setNome(key);
					headerAllegato.setValore(value);
					headerAllegato.setDumpTimestamp(dumpMessaggio.getDumpTimestamp());
					dumpAllegato.addHeader(headerAllegato);
				}
			}
		}
	}
}
