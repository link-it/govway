/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package it.gov.spcoop.sica.wsbl;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.gov.spcoop.sica.wsbl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/

@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.gov.spcoop.sica.wsbl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TransitionType }
     */
    public TransitionType createTransitionType() {
        return new TransitionType();
    }

    /**
     * Create an instance of {@link EventTypeMessage }
     */
    public EventTypeMessage createEventTypeMessage() {
        return new EventTypeMessage();
    }

    /**
     * Create an instance of {@link TransitionsType }
     */
    public TransitionsType createTransitionsType() {
        return new TransitionsType();
    }

    /**
     * Create an instance of {@link Message }
     */
    public Message createMessage() {
        return new Message();
    }

    /**
     * Create an instance of {@link ConceptualBehavior }
     */
    public ConceptualBehavior createConceptualBehavior() {
        return new ConceptualBehavior();
    }

    /**
     * Create an instance of {@link StatesType }
     */
    public StatesType createStatesType() {
        return new StatesType();
    }

    /**
     * Create an instance of {@link EventType }
     */
    public EventType createEventType() {
        return new EventType();
    }

    /**
     * Create an instance of {@link StateTypeInitial }
     */
    public StateTypeInitial createStateTypeInitial() {
        return new StateTypeInitial();
    }

    /**
     * Create an instance of {@link MessagesTypes }
     */
    public MessagesTypes createMessagesTypes() {
        return new MessagesTypes();
    }

    /**
     * Create an instance of {@link CompletionModeTypeCompensateMessage }
     */
    public CompletionModeTypeCompensateMessage createCompletionModeTypeCompensateMessage() {
        return new CompletionModeTypeCompensateMessage();
    }

    /**
     * Create an instance of {@link CompletionModeType }
     */
    public CompletionModeType createCompletionModeType() {
        return new CompletionModeType();
    }

    /**
     * Create an instance of {@link EventListType }
     */
    public EventListType createEventListType() {
        return new EventListType();
    }

    /**
     * Create an instance of {@link StateTypeFinal }
     */
    public StateTypeFinal createStateTypeFinal() {
        return new StateTypeFinal();
    }

    /**
     * Create an instance of {@link TemporalConditionType }
     */
    public TemporalConditionType createTemporalConditionType() {
        return new TemporalConditionType();
    }

    /**
     * Create an instance of {@link StateTypeNormal }
     */
    public StateTypeNormal createStateTypeNormal() {
        return new StateTypeNormal();
    }

    /**
     * Create an instance of {@link MessageBehavior }
     */
    public MessageBehavior createMessageBehavior() {
        return new MessageBehavior();
    }

    /**
     * Create an instance of {@link GuardType }
     */
    public GuardType createGuardType() {
        return new GuardType();
    }


 }
