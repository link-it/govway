function(params) {

	/*
	 * Produce un token JWT firmato, equivalente a quello generato dalla trasformazione 'generaToken'
	 * utilizzata dalle fruizioni che identificano l'applicativo tramite una token policy interna.
	 *
	 * Il token viene firmato con la chiave 'ExampleClient1', il cui certificato e' presente nel truststore
	 * associato alle policy di validazione 'AutenticazioneInternaRiconoscimentoApplicativoModI*', che
	 * risolvono la chiave di verifica tramite l'alias indicato nel 'kid'.
	 * Il claim 'client_id' individua l'applicativo tramite le credenziali di tipo token ad esso associate.
	 *
	 * La firma viene prodotta con le sole API del JDK per non richiedere nel classpath della testsuite
	 * le librerie utilizzate dal gateway.
	 */

	var StringType = Java.type('java.lang.String')
	var Base64 = Java.type('java.util.Base64')
	var KeyStore = Java.type('java.security.KeyStore')
	var Signature = Java.type('java.security.Signature')
	var FileInputStream = Java.type('java.io.FileInputStream')

	var keystoreFile = params.keystoreFile ? params.keystoreFile : '/etc/govway/keys/xca/ExampleClient1.p12'
	var keystoreType = params.keystoreType ? params.keystoreType : 'pkcs12'
	var keystorePassword = params.keystorePassword ? params.keystorePassword : '123456'
	var keyPassword = params.keyPassword ? params.keyPassword : '123456'
	var keyAlias = params.keyAlias ? params.keyAlias : 'ExampleClient1'

	var keystore = KeyStore.getInstance(keystoreType)
	var is = new FileInputStream(keystoreFile)
	try {
		keystore.load(is, new StringType(keystorePassword).toCharArray())
	} finally {
		is.close()
	}

	// l'alias presente nel keystore puo' differire nelle maiuscole da quello configurato
	if (!keystore.containsAlias(keyAlias)) {
		var aliases = keystore.aliases()
		while (aliases.hasMoreElements()) {
			var alias = aliases.nextElement()
			if (new StringType(alias).equalsIgnoreCase(keyAlias)) {
				keyAlias = alias
				break
			}
		}
	}

	var privateKey = keystore.getKey(keyAlias, new StringType(keyPassword).toCharArray())
	var certificate = keystore.getCertificate(keyAlias)

	var b64url = function(bytes) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
	}
	var b64urlText = function(text) {
		return b64url(new StringType(text).getBytes('UTF-8'))
	}

	var header = {
		alg: 'RS256',
		typ: 'JWT',
		kid: keyAlias,
		x5c: [ Base64.getEncoder().encodeToString(certificate.getEncoded()) ]
	}

	var now = Math.floor(new Date().getTime() / 1000)
	var ttl = params.ttl ? params.ttl : 300

	var payload = {
		iss: params.iss ? params.iss : 'govwayTest',
		sub: params.clientId,
		client_id: params.clientId,
		aud: params.aud ? params.aud : 'test',
		iat: now,
		nbf: now,
		exp: now + ttl,
		jti: 'testsuite-' + now + '-' + Math.floor(Math.random() * 1000000)
	}

	var signingInput = b64urlText(JSON.stringify(header)) + '.' + b64urlText(JSON.stringify(payload))

	var signature = Signature.getInstance('SHA256withRSA')
	signature.initSign(privateKey)
	signature.update(new StringType(signingInput).getBytes('UTF-8'))

	var token = signingInput + '.' + b64url(signature.sign())

	// con una policy che preleva il token da un header custom il valore non deve essere prefissato
	var prefix = (params.prefix != null) ? params.prefix : 'Bearer '

	return prefix + token
}
