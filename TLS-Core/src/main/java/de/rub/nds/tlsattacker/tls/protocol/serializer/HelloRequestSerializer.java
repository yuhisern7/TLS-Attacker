/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.tls.protocol.serializer;

import de.rub.nds.tlsattacker.tls.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.tls.protocol.message.HelloRequestMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Robert Merget - robert.merget@rub.de
 */
public class HelloRequestSerializer extends HandshakeMessageSerializer<HelloRequestMessage> {

    private final HelloRequestMessage message;

    /**
     * Constructor for the HelloRequestSerializer
     *
     * @param message
     *            Message that should be serialized
     * @param version
     *            Version of the Protocol
     */
    public HelloRequestSerializer(HelloRequestMessage message, ProtocolVersion version) {
        super(message, version);
        this.message = message;
    }

    @Override
    public byte[] serializeHandshakeMessageContent() {
        return getAlreadySerialized();
    }

}
