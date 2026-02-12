Miglioramenti alla funzionalità di Sicurezza Messaggio
---------------------------------------------------------

Introdotta nuova modalità di sicurezza messaggio 'JWS Compact Payload Enrichment' che arricchisce il payload JSON con claims JWT standard (iss, aud, exp, iat, nbf, jti) e lo firma in formato JWS Compact.
Supporta policy configurabili per gestire conflitti con claims esistenti (preserve/override/error) e filtro su codici HTTP per le risposte.
