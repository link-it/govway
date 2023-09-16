#Per generare classi:
OP2_ROOT=$1
if [ -z "${OP2_ROOT}" ]
then
	echo "Indicare la root del progetto"
	exit 1;
fi

java -classpath "${OP2_ROOT}/lib/cxf/*:${OP2_ROOT}/lib/jakarta/*:${OP2_ROOT}/lib/jaxb/*:${OP2_ROOT}/lib/shared/*:${OP2_ROOT}/lib/log/*:${OP2_ROOT}/lib/commons/*:${OP2_ROOT}/lib/security/*" org.apache.cxf.tools.wsdlto.WSDLToJava -d src -p org.openspcoop2.example.server.rpc.literal.skeleton -impl -server config/rpcLiteralExample.wsdl 
java -classpath "${OP2_ROOT}/lib/cxf/*:${OP2_ROOT}/lib/jakarta/*:${OP2_ROOT}/lib/jaxb/*:${OP2_ROOT}/lib/shared/*:${OP2_ROOT}/lib/log/*:${OP2_ROOT}/lib/commons/*:${OP2_ROOT}/lib/security/*" org.apache.cxf.tools.wsdlto.WSDLToJava -d src -p org.openspcoop2.example.server.rpc.literal.stub -client config/rpcLiteralExample.wsdl 

java -classpath "${OP2_ROOT}/lib/cxf/*:${OP2_ROOT}/lib/jakarta/*:${OP2_ROOT}/lib/jaxb/*:${OP2_ROOT}/lib/shared/*:${OP2_ROOT}/lib/log/*:${OP2_ROOT}/lib/commons/*:${OP2_ROOT}/lib/security/*" org.apache.cxf.tools.wsdlto.WSDLToJava -d src -p org.openspcoop2.example.server.rpc.literal.skeleton_namespace_ridefinito -impl -server config/rpcLiteralExample_vNamespaceRidefinito.wsdl
java -classpath "${OP2_ROOT}/lib/cxf/*:${OP2_ROOT}/lib/jakarta/*:${OP2_ROOT}/lib/jaxb/*:${OP2_ROOT}/lib/shared/*:${OP2_ROOT}/lib/log/*:${OP2_ROOT}/lib/commons/*:${OP2_ROOT}/lib/security/*" org.apache.cxf.tools.wsdlto.WSDLToJava -d src -p org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito -client config/rpcLiteralExample_vNamespaceRidefinito.wsdl

for file in $(find src -name \*\.java -and -not -type l -and -not -type d)
do
        #echo "SET in ${file}"
        find ${file} -type f -exec perl -pi -e "s#file:config#/config#g" {} \;
done

java -classpath ${OP2_ROOT}/distrib/check/ GPLWriter src/

#!Si genera solo il literal, perchè encoded non è più supportato da cxf.!
