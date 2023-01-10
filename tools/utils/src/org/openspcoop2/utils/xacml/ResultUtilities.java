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

package org.openspcoop2.utils.xacml;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.herasaf.xacml.core.context.impl.DecisionType;
import org.herasaf.xacml.core.context.impl.ResultType;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.JaxbUtils;
import org.slf4j.Logger;


/**
 * ResultCombining
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResultUtilities {


	public static List<ResultType> evaluate(XacmlRequest xacmlRequest, Logger log, String policyKey, PolicyDecisionPoint pdp) throws UtilsException{
    	List<ResultType> results = null;
		try {	  
			log.debug("evaluete XACMLRequest (idPolicy:"+policyKey+") ... ");
			results = pdp.evaluate(xacmlRequest);
			StringBuilder bf = new StringBuilder();
			bf.append("----XACML Results (idPolicy:"+policyKey+") begin ---\n");
			for(ResultType result: results) {
				bf.append("Decision: "+result.getDecision().toString()+"\n");
			}
			bf.append("----XACML Results end ---");
			log.debug(bf.toString());
			return results;
		} catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static String toRawString(List<ResultType> results) throws UtilsException{
		try{
			StringBuilder bfPolicy = new StringBuilder();
			for (int i = 0; i < results.size(); i++) {
        		ResultType res = results.get(i);
				if(bfPolicy.length()>0){
					bfPolicy.append("\n");
				}
				bfPolicy.append("Result["+(i+1)+"]: ");
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				JaxbUtils.objToXml(bout, res.getClass(), res);
				bout.flush();
				bout.close();
				bfPolicy.append(bout.toString());
			}
			return bfPolicy.toString();
		}catch(Throwable e){
			throw new UtilsException("Serializzazione risposta non riuscita",e);
		}
	}
	
	public static String toString(List<ResultType> results, DecisionType decision) throws UtilsException{
		try{
			StringBuilder bf = new StringBuilder();
        	for (int i = 0; i < results.size(); i++) {
        		ResultType res = results.get(i);
        		
        		boolean check = false;
	        	if(DecisionType.DENY.equals(decision)) {
	        		check = DecisionType.DENY.equals(res.getDecision());
	        	}
	        	else{
	        		check = DecisionType.DENY.equals(res.getDecision()) || DecisionType.INDETERMINATE.equals(res.getDecision()) || DecisionType.NOT_APPLICABLE.equals(res.getDecision());
	        	}
        		
    	    	if(check) {
    	    		if(bf.length()>0){
    	    			bf.append(" - ");
    				}
    	    		if(results.size()>1){
    	    			bf.append("(");
    	    		}
    	    		bf.append("result-"+(i+1)+" "+res.getDecision().name());
    	    		if( res.getStatus() != null ){
	    	    		if(res.getStatus().getStatusCode() != null){
	    	    			bf.append(" code:").append(res.getStatus().getStatusCode().getValue());
	    	    		}
	    	    		if(res.getStatus().getStatusMessage() != null){
	    	    			bf.append(" ").append(res.getStatus().getStatusMessage());
	    	    		}
    	    		}
    	    		if(results.size()>1){
    	    			bf.append(")");
    	    		}
    	        	
    	    	}
    	    }
        	return bf.toString();
		}catch(Throwable e){
			throw new UtilsException("Serializzazione risposta non riuscita",e);
		}
	}

}
