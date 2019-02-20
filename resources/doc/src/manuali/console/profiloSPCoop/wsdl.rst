.. _profiloSPCoop_wsdl:

Interfacce WSDL (concettuale, logico ed implementativo)
-------------------------------------------------------

La specifica SPCoop prevede che nell'accordo di servizio siano
specificati i documenti WSDL del servizio applicativo erogatore e, nel
caso di profili di collaborazione asincroni asimmetrici, anche quelli
del servizio applicativo correlato erogato dal soggetto fruitore.

La :numref:`accordoServizioTab` riepiloga i documenti necessari alla descrizione formale di un
accordo di servizio che possono essere associati agli accordi parte
comune e specifica se viene utilizzata la modalità avanzata della
console

.. table:: Descrizione di un accordo di servizio
   :widths: auto
   :name: accordoServizioTab

   ===================================  ================
   Nome Documento                       Accordo
   ===================================  ================
     *Specifica delle Interfacce*
     WSDL Definitorio                    Parte Comune
     WSDL Concettuale                    Parte Comune
     WSDL Logico Erogatore               Parte Comune
     WSDL Logico Fruitore                Parte Comune
     *Specifica delle Implementazioni*
     WSDL Implementativo Erogatore       Parte Specifica
     WSDL Implementativo Fruitore        Parte Specifica
   ===================================  ================
