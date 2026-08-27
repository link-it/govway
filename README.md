<p align="right">
  <b>English</b> | <a href="README.it.md">Italiano</a>
</p>

<p align="center">
  <img src="https://www.link.it/wp-content/uploads/2025/01/logo-govway.svg" alt="GovWay Logo" width="200"/>
</p>

# GovWay - API Gateway for Public Administration

[![Quality Gate Status](https://jenkins.link.it/govway4-sonarqube-badge)](https://jenkins.link.it/govway4-sonarqube/dashboard?id=govway)
[![Build Status](https://jenkins.link.it/govway4/buildStatus/icon?job=GovWay&style=plastic)](https://jenkins.link.it/govway4/job/GovWay/)
[![Docker](./resources/images/docker.svg)](https://hub.docker.com/r/linkitaly/govway)
[![Documentation Status](https://readthedocs.org/projects/govway/badge/?version=latest&style=plastic)](https://govway.readthedocs.io/it/latest/?badge=latest)
[![License](./resources/images/license.svg)](https://raw.githubusercontent.com/link-it/govway/master/LICENSE)

Based on the experience of the Italian "Porta di Dominio", GovWay is the API Gateway compliant with Public Administration regulations, able to guarantee:

- **Compliance with market standards**: support for standard market protocols, such as SOAP 1.1 and 1.2, RESTful APIs serialized in JSON or XML, or simple binary data over HTTP.

- **Compliance with Italian interoperability specifications**: support for AGID's new interoperability guidelines (ModI). Backward compatibility with the SPCoop protocol, still widely adopted for Public Administration services, is also ensured.

- **Compliance with European interoperability specifications**: support for the AS4 protocol, through integration with the eDelivery Building Block of the European CEF (Connecting European Facilities) project.

- Compliance with the specifications for electronic invoicing on the SdiCoop channel.

## Contributions

You can participate by creating [issues](https://github.com/link-it/govway/issues) and [pull requests](https://github.com/link-it/govway/pulls).

The project maintains a technical support backlog made up of [Issues labeled 'Support'](https://github.com/link-it/govway/issues?utf8=%E2%9C%93&q=is%3Aissue+label%3A%22support%22+).

## Documentation

Project website:

* https://govway.org

Online documentation:

* [Read the docs](https://govway.readthedocs.io/it/latest/) ([download](https://readthedocs.org/projects/govway/downloads/htmlzip/latest/))
* Technical support backlog made up of [Issues labeled 'Support'](https://github.com/link-it/govway/issues?utf8=%E2%9C%93&q=is%3Aissue+label%3A%22support%22+)

PDF documentation:

* [Installation Manual](./resources/doc/pdf/GovWay-ManualeInstallazione.pdf)
* [User Manual](./resources/doc/pdf/GovWay-ManualeUtente.pdf)
* [Monitoring Manual](./resources/doc/pdf/GovWay-ManualeMonitoraggio.pdf)

## Docker

Test environments for stable GovWay versions, containerized in Docker format, are available on [Docker Hub](https://hub.docker.com/r/linkitaly/govway).

The [govway-docker](https://github.com/link-it/govway-docker) project provides everything needed to build a new, ready-to-use GovWay test environment, containerized in Docker format, starting even from unstable versions or from source releases.

## Govlet

* [Active Invoicing](./resources/doc/govlet/Fatturazione/Govlet_FatturaPA_FatturazioneAttiva.pdf)
* [Passive Invoicing](./resources/doc/govlet/Fatturazione/Govlet_FatturaPA_FatturazionePassiva.pdf)
* [PagoPA](./resources/doc/govlet/pagoPA/Govlet_pagoPA.pdf)
* [SIOPE+](./resources/doc/govlet/SiopePlus/Govlet_SiopePlus.pdf)
* [ANPR](./resources/doc/govlet/ANPR/Govlet_ANPR.pdf)

## Contacts

- Mailing list: [GovWay Users](https://govway.org/mailing)
- Reports: [GitHub Issues](https://github.com/link-it/govway/issues)

# Key Features

## Market Standards

Support for APIs compliant with standard market protocols, such as SOAP 1.1 and 1.2, RESTful APIs serialized in JSON or XML, or simple binary data over HTTP. Integration always takes place through native application APIs, regardless of the interoperability profiles adopted, which are handled transparently by the gateway.

## Compliance with Italian and European interoperability specifications

Compliance of APIs (both consumption and provisioning) with AGID's new Interoperability Guidelines, both for the technical profiles required by the new interoperability model (ModI) and for the management of tokens issued by PDND (signed JWT, purposeId, sessionInfo). All of this is handled transparently to the applications internal to the domain managed by the gateway.
Support for the "eDelivery" building block of the European CEF (Connecting European Facilities) project.
Backward compatibility with the SPCoop protocol, still widely adopted for Public Administration services.

## API Registry

APIs can be registered manually or by uploading interface descriptors (OpenAPI 3.0 or Swagger 2 for REST services, WSDL for SOAP services, Service Agreements for SPCoop services).

## Govlet Management

GovWay introduces the concept of Govlet, an archive format that can be uploaded directly from the product's Consoles, for rapid configuration of Public Administration services. To date, the library of Govlets available for GovWay includes Electronic Invoicing services (active and passive), PagoPA and SIOPE+, and is rapidly expanding.

## Token Management

Management of authentication tokens compliant with the JWT, OAuth2 and OIDC standards, both during service consumption and provisioning.
During provisioning: support for token validation and claim verification for subsequent authentication and authorization phases, including interaction with external Authorization Servers via Introspection and UserInfo features.
During consumption: support for token negotiation with Client Credentials, Resource Owner Password and Signed JWT (RFC 7523) grant types.

## Rate Limiting

Regulation of inbound traffic on GovWay, limiting the number of requests or the amount of bandwidth used for specific provisioning or consumption services, also based on parameters such as average processing time, error rate, or characteristics of the specific application requests.

## Authentication

Handles authentication of inbound and outbound application requests to and from the internal domain, through native support for HTTP-Basic, TLS and ApiKey protocols, or through integration with external Identity Management systems.

## Authorization

Handles authorization of application requests, through registration of the consumers of the managed APIs and their roles, or through integration with external Identity Management systems. Support for OAuth2 and XACML Authorization protocols, with the ability to manage the evaluation of XACML policies locally or by using an external Policy Decision Point.

## Message Transformation

Messages in transit can be modified, both in terms of Protocol, from SOAP to REST or vice versa, and in terms of transforming message contents (headers, properties or payload).

## Validation

Validation of application request contents, with verification of XML messages for SOAP services and JSON or XML for REST services. Validation is performed against the API descriptions (OpenAPI, Swagger, WSDL, JSON Schema, XSD) registered during service configuration.

## Response Caching

For each API, it is possible to enable response caching so that subsequent requests with the same characteristics (URI, HTTP headers, payload) are served directly by the gateway until a configurable timeout expires.

## CORS Support

Ability to configure cross-origin HTTP requests (CORS) either globally, so that it applies to all APIs, or specifically, refining it for a single provisioning or consumption service.

## Tracing

Generation of traces compliant with regulations for every managed application request. In addition to metadata regarding the service request (transaction id, sender, recipient, ...), it is possible to include identifying elements extracted from messages in transit in the traces; the extraction methods supported by the product are: XPath, Regular Expressions and JSONPath.

## Content Security

The gateway can add or verify security measures for application requests. For SOAP APIs, the WS-Security standard is supported. For REST APIs, the XMLEncryption and XMLSignature protocols are supported for XML messages, and JOSE (JWS/JWE) for JSON messages.

## MTOM Format Handling

The gateway is able to wrap or unwrap the message in transit according to the MTOM protocol. When validating an MTOM message, the gateway can normalize the message before performing validation and then restore the original format once the validation process is complete.

## Request Routing

Delivery of the request to backend services, with native support for the following connection protocols: HTTP, HTTPS with mutual authentication, JMS and file writing. Additional connectors can be implemented as simple plugins.

## Management Console

Web console for registering APIs (interfaces), Provisioning (implementations), Consumption (subscriptions) and the various policies that govern them. Support for different user profiles allows administrative functions to be selected according to the roles of the various administrators. All operations are subject to auditing, so that the authors of any changes made to the configurations can always be identified.

## Monitoring Console

Web console for diagnostics and monitoring of traffic managed by the API gateway; it provides infrastructure administrators with full control over messages in transit, helping to diagnose and prevent any type of anomaly; for project managers, it offers the ability to analyze usage flows, outcomes and the overall efficiency of the APIs used in their project.

# License

GovWay - A customizable API Gateway
https://govway.org

Copyright (c) 2005-2026 Link.it srl (https://link.it).

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, as published by
the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
