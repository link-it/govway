package org.openspcoop2.web.ctrlstat.servlet.protocol_properties;

import java.util.Vector;

import org.openspcoop2.web.lib.mvc.ForwardParams;

public class ProtocolPropertiesCostanti {

	public final static String OBJECT_NAME_PP = "protocolProperty";
	
	public final static ForwardParams TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE = ForwardParams.OTHER("BinaryPropertyChange");
	public static final String SERVLET_NAME_BINARY_PROPERTY_CHANGE = OBJECT_NAME_PP+"BinaryPropertyChange.do";
	
	public final static Vector<String> SERVLET_PP = new Vector<String>();
	static{
		SERVLET_PP.add(SERVLET_NAME_BINARY_PROPERTY_CHANGE);
	}
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_PP_ID = "id";
	public static final String PARAMETRO_PP_ID_PROPRIETARIO = "idProprietario";
	public static final String PARAMETRO_PP_TIPO_PROPRIETARIO = "tipoProprietario";
	public static final String PARAMETRO_PP_URL_ORIGINALE_CHANGE = "urlOrigChange";
	public static final String PARAMETRO_PP_SET = "ppSet";
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_PP_ID = "Id Property";
	public final static String LABEL_PARAMETRO_PP_ID_PROPRIETARIO = "Id Proprietario";
	public final static String LABEL_PARAMETRO_PP_TIPO_PROPRIETARIO = "Tipo Proprietario";



}
