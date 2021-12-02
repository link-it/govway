Feature: Controllo del token server

Scenario:
    * print "Response = ", response
    * match /Envelope/Header/Security/Signature == "#present"
    * match /Envelope/Header/Security/Timestamp/Created == "#string"
    * match /Envelope/Header/Security/Timestamp/Expires == "#string"
    * match /Envelope/Header/To == to
    * match /Envelope/Header/From/Address == from
    * match /Envelope/Header/MessageID == "#uuid"
    * match /Envelope/Header/ReplyTo/Address == "http://www.w3.org/2005/08/addressing/anonymous"
    * match /Envelope/Header/RelatesTo == bodyPath('/Envelope/Header/MessageID')

    * def body = response 
    * call check_signature ([ {element: 'To', body: body}, {element: 'From', body: body}, {element: 'MessageID', body: body}, {element: 'ReplyTo', body: body}, {element: 'RelatesTo', body: body} ])