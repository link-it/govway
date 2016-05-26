package org.openspcoop2.utils.logger.beans.proxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.logger.constants.proxy.Result;

public class ResultSearch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Result> results = new ArrayList<Result>();
	private boolean and = true;
	
	public List<Result> getResults() {
		return this.results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	public void addResult(Result result){
		this.results.add(result);
	}
	public boolean isAnd() {
		return this.and;
	}
	public void setAnd(boolean and) {
		this.and = and;
	}
	
}
