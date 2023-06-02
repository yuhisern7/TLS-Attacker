/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.protocol;

import de.rub.nds.tlsattacker.core.layer.data.Parser;
import java.io.InputStream;

public abstract class ProtocolMessageParser<Message extends ProtocolMessage>
        extends Parser<Message> {

    public ProtocolMessageParser(InputStream stream) {
        super(stream);
    }
}
