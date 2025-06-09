package org.openspcoop2.monitor.engine.statistic;

public class StatisticsEngineException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public StatisticsEngineException() {
		super();
	}
	
	public StatisticsEngineException(Throwable t) {
		super(t);
	}
	
	public StatisticsEngineException(String message) {
		super(message);
	}
	
	public StatisticsEngineException(Throwable t, String message) {
		super(message, t);
	}
	
	public StatisticsEngineException(String message, Throwable t) {
		super(message, t);
	}

}
