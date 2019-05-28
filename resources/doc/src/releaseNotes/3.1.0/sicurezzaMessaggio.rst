Miglioramenti alla funzionalità di Sicurezza Messaggio
---------------------------------------------------------

Sono state introdotte le seguenti nuove funzionalità riguardanti la sicurezza dei messaggi JSON:

-  *JSON Web Signature - Unencoded Payload Option*: aggiunto il supporto per generare un JWS con il payload non codificato come descritto nel RFC 7797 (https://tools.ietf.org/html/rfc7797).

-  *JSON Web Signature - Compact Detach*: aggiunto il supporto per generare un JWS con serializzazione 'Compact' in modalità 'Detach' come descritto nell'Appendice F del RFC 7515 (https://tools.ietf.org/html/rfc7515#appendix-F).

- *JWT Header per informazione sul certificato*: aggiunto supporto per la gestione degli header 'x5c','x5u','jwk', 'jku' sia per la Signature che per l'Encrypt.

- *JWT Header per custom e critical claim*: aggiunta possibilità di generare header custom e critical sia per la Signature che per l'Encrypt.

- *JWKSet*: aggiunta gestione dei keystore di tipo 'jwk'.

