/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2020 Ruhr University Bochum, Paderborn University,
 * and Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import de.rub.nds.tlsattacker.core.constants.HandshakeByteLength;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.exceptions.AdjustmentException;
import de.rub.nds.tlsattacker.core.protocol.message.extension.SupportedVersionsExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.SupportedVersionsExtensionParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.SupportedVersionsExtensionPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.SupportedVersionsExtensionSerializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This handler processes the SupportedVersions extensions, as defined in
 * https://tools.ietf.org/html/draft-ietf-tls-tls13-21#section-4.2.1
 */
public class SupportedVersionsExtensionHandler extends ExtensionHandler<SupportedVersionsExtensionMessage> {

    private static final Logger LOGGER = LogManager.getLogger();

    public SupportedVersionsExtensionHandler(TlsContext context) {
        super(context);
    }

    @Override
    public SupportedVersionsExtensionParser getParser(byte[] message, int pointer) {
        return new SupportedVersionsExtensionParser(pointer, message);
    }

    @Override
    public SupportedVersionsExtensionPreparator getPreparator(SupportedVersionsExtensionMessage message) {
        return new SupportedVersionsExtensionPreparator(context.getChooser(), message, getSerializer(message));
    }

    @Override
    public SupportedVersionsExtensionSerializer getSerializer(SupportedVersionsExtensionMessage message) {
        return new SupportedVersionsExtensionSerializer(message);
    }

    @Override
    public void adjustTLSExtensionContext(SupportedVersionsExtensionMessage message) {
        byte[] versionBytes = message.getSupportedVersions().getValue();
        if (versionBytes.length % HandshakeByteLength.VERSION != 0) {
            throw new AdjustmentException("Could not create resonable ProtocolVersions from VersionBytes");
        }
        List<ProtocolVersion> versionList = ProtocolVersion.getProtocolVersions(versionBytes);
        if (context.getTalkingConnectionEndType() == ConnectionEndType.CLIENT) {
            context.setClientSupportedProtocolVersions(versionList);
            context.setHighestClientProtocolVersion(ProtocolVersion.getHighestProtocolVersion(versionList));
            List<ProtocolVersion> clientVersions = new ArrayList<>(context.getClientSupportedProtocolVersions());
            ProtocolVersion.sort(clientVersions, false);
            boolean found = false;
            for (ProtocolVersion i : clientVersions) {
                if (context.getConfig().getHighestProtocolVersion().compare(i) >= 0) {
                    context.setSelectedProtocolVersion(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                LOGGER.warn("No common protocol version could be found.");
                context.setSelectedProtocolVersion(context.getConfig().getDefaultSelectedProtocolVersion());
            }
        } else {
            if (versionList.size() == 1) {
                context.setSelectedProtocolVersion(versionList.get(0));
            } else {
                LOGGER.warn("Received a SupportedProtocolVersionExtension with unknown contents");
            }
        }
    }
}
