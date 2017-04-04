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
import de.rub.nds.tlsattacker.tls.protocol.message.HelloVerifyRequestMessage;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Robert Merget - robert.merget@rub.de
 */
public class HelloVerifyRequestSerializer extends HandshakeMessageSerializer<HelloVerifyRequestMessage> {

    private static final Logger LOGGER = LogManager.getLogger("SERIALIZER");

    private HelloVerifyRequestMessage msg;

     /**
     * Constructor for the HelloVerifyRequestSerializer
     *
     * @param message
     *            Message that should be serialized
     * @param version
     *            Version of the Protocol
     */
    public HelloVerifyRequestSerializer(HelloVerifyRequestMessage message, ProtocolVersion version) {
        super(message, version);
        this.msg = message;
    }

    @Override
    public byte[] serializeHandshakeMessageContent() {
        writeProtocolVersion(msg);
        writeCookieLength(msg);
        writeCookie(msg);
        return getAlreadySerialized();
    }

    /**
     * Writes the ProtocolVersion of the HelloVerifyMessage into the final byte[]
     */
    private void writeProtocolVersion(HelloVerifyRequestMessage msg) {
        appendBytes(msg.getProtocolVersion().getValue());
        LOGGER.debug("ProtocolVersion: "+ Arrays.toString(msg.getProtocolVersion().getValue()));
    }

    /**
     * Writes the CookieLength of the HelloVerifyMessage into the final byte[]
     */
    private void writeCookieLength(HelloVerifyRequestMessage msg) {
        appendByte(msg.getCookieLength().getValue());
        LOGGER.debug("CookieLength: "+ msg.getCookieLength().getValue());
    }

    /**
     * Writes the Cookie of the HelloVerifyMessage into the final byte[]
     */
    private void writeCookie(HelloVerifyRequestMessage msg) {
        appendBytes(msg.getCookie().getValue());
        LOGGER.debug("Cookie: "+ Arrays.toString(msg.getCookie().getValue()));
    }

}
