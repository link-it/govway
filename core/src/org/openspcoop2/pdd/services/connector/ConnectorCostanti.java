package org.openspcoop2.pdd.services.connector;


public class ConnectorCostanti {

	public final static String SEPARATOR_CODE = "-";
	
	public final static String KEYWORD_METHOD_HTTP = "@METHOD@";
	public final static String MESSAGE_METHOD_HTTP_NOT_SUPPORTED = "Method HTTP @METHOD@ non supportato";

	// ID: 7 cifre (parlante)
	public final static String ID_ERRORE_GENERICO = "0000000";
	// Altri codici definiti in org.openspcoop2.protocol.engine.constants.IDService
	
	
	// CODE: 4 cifre
	
	public final static String CODE_PROTOCOL_NOT_SUPPORTED = "0001";
	
	public final static String CODE_HTTP_METHOD_GET_UNSUPPORTED = "0011";
	public final static String CODE_HTTP_METHOD_POST_UNSUPPORTED = "0012";
	public final static String CODE_HTTP_METHOD_PUT_UNSUPPORTED = "0013";
	public final static String CODE_HTTP_METHOD_HEAD_UNSUPPORTED = "0014";
	public final static String CODE_HTTP_METHOD_DELETE_UNSUPPORTED = "0015";
	public final static String CODE_HTTP_METHOD_OPTIONS_UNSUPPORTED = "0016";
	public final static String CODE_HTTP_METHOD_TRACE_UNSUPPORTED = "0017";
	
	public final static String CODE_WSDL = "0021";
	
	public final static String CODE_ENGINE_FILTER = "0031";

	public final static String CODE_FUNCTION_UNSUPPORTED = "0041";
	
}
