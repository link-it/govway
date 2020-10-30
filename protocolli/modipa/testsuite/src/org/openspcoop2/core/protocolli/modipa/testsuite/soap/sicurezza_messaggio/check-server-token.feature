Feature: Controllo del token server

Scenario:
    * match /Envelope/Header/Security/Signature == "#present"
    * match /Envelope/Header/Security/Timestamp/Created == "#string"
    * match /Envelope/Header/Security/Timestamp/Expires == "#string"
    * match /Envelope/Header/To == to
    * match /Envelope/Header/From/Address == from
    * match /Envelope/Header/MessageID == "#uuid"
    * match /Envelope/Header/ReplyTo/Address == "http://www.w3.org/2005/08/addressing/anonymous"
    * match /Envelope/Header/RelatesTo == bodyPath('/Envelope/Header/MessageID')

    * def body = response 
    * call check_signature [ {element: 'To'}, {element: 'From'}, {element: 'MessageID'}, {element: 'ReplyTo'}, {element: 'RelatesTo'} ]