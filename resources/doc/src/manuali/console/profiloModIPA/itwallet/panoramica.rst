.. _itwalletPanoramica:

Panoramica
==========

Specifiche PDND per Attribute Services
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le specifiche IT-Wallet definiscono un template OpenAPI per le Fonti Autentiche pubblicato dalla PDND (https://italia.github.io/eid-wallet-it-docs/versione-corrente/it/appendix-oas-pdnd-as.html).

L'endpoint principale previsto dalle specifiche è:

::

    POST /v1.3.1/AttributeClaims/{dataset_id}

**Richiesta**

La richiesta prevede un body JSON con i seguenti parametri:

.. table:: Parametri della richiesta
   :widths: 20 15 65

   ==================== ========== ===============================================
   Parametro            Tipo       Descrizione
   ==================== ========== ===============================================
   unique_id            string     ID ANPR o Codice Fiscale (obbligatorio)
   object_id            string     Identificativo del dataset (opzionale)
   ==================== ========== ===============================================

**Headers di sicurezza richiesti**

La specifica PDND richiede diversi header JWT per la sicurezza:

- **Authorization**: Token JWT rilasciato dalla PDND (Bearer o DPoP)
- **Agid-JWT-Signature**: JWT per l'integrità del messaggio
- **Digest**: Hash SHA-256 del body della richiesta
- **Agid-JWT-TrackingEvidence**: JWT per audit e tracciamento (opzionale)

**Risposta**

La risposta deve essere un **JWT firmato** contenente:

*Header JWT:*

- ``alg``, ``kid``, ``typ`` - Metadati standard JWT

*Payload JWT:*

- ``iss``, ``aud``, ``exp``, ``iat``, ``nbf``, ``jti`` - Claims standard
- ``userClaims`` - Dati anagrafici dell'utente
- ``attributeClaims`` - Array di attributi certificati
- ``metadataClaims`` - Metadati sui dataset

Semplificazione con GovWay
~~~~~~~~~~~~~~~~~~~~~~~~~~

Implementare una Fonte Autentica conforme alle specifiche PDND richiede:

1. Validare il token PDND e verificare l'autorizzazione
2. Validare l'header Agid-JWT-Signature
3. Firmare la risposta come JWT con i claims richiesti
4. Gestire i codici di errore secondo le specifiche

GovWay permette di **semplificare drasticamente l'implementazione** del backend, che deve solo esporre un'API REST che restituisce un JSON semplice.

**OpenAPI semplificata per il backend**

Con GovWay, il backend deve implementare solo:

::

    POST /AttributeClaims/{dataset_id}

Con una risposta JSON (non JWT):

.. code-block:: json

    {
      "userClaims": {
        "given_name": "Mario",
        "family_name": "Rossi",
        "birth_date": "1980-01-10",
        "tax_id_code": "RSSMRA80A10H501X"
      },
      "attributeClaims": [
        {
          "object_id": "6F9619FF-8B86-D011-B42D-00C04FC964FF",
          "status": "VALID",
          "last_updated": "2025-12-31T23:59:59Z"
        }
      ],
      "metadataClaims": [
        {
          "object_id": "6F9619FF-8B86-D011-B42D-00C04FC964FF",
          "issuance_date": "2025-01-01",
          "expiry_date": "2025-12-31"
        }
      ]
    }

**GovWay si occupa automaticamente di:**

- Validare il token PDND in ingresso
- Aggiungere i claims JWT standard (iss, aud, exp, iat, nbf, jti)
- Firmare la risposta JSON come JWT compatto
- Impostare il Content-Type a ``application/jwt``
- Trasformare gli errori nel formato atteso dalle specifiche
