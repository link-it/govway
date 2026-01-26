.. _itwallet:

IT Wallet - Fonte Autentica
---------------------------

L'ecosistema IT-Wallet prevede che i Credential Issuer possano richiedere attributi certificati alle Fonti Autentiche (Attribute Providers) tramite la Piattaforma Digitale Nazionale Dati (PDND).

Una Fonte Autentica è un ente pubblico o privato che detiene dati certificati relativi ai cittadini (es. anagrafe, titoli di studio, patente) e li espone tramite API conformi alle specifiche PDND.

GovWay semplifica l'implementazione di una Fonte Autentica gestendo automaticamente:

- La validazione del token PDND di autorizzazione
- La firma JWT della risposta secondo le specifiche IT-Wallet
- La gestione dei claims standard (iss, aud, exp, iat, nbf, jti)
- La trasformazione degli errori nel formato atteso

.. toctree::
    :maxdepth: 2

    panoramica
    configurazione
