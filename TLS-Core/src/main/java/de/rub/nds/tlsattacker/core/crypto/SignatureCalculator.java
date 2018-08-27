/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.crypto;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.modifiablevariable.util.BadRandom;
import de.rub.nds.tlsattacker.core.constants.AlgorithmResolver;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.constants.SignatureAndHashAlgorithm;
import de.rub.nds.tlsattacker.core.exceptions.CryptoException;
import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SignatureCalculator {

    private static final Logger LOGGER = LogManager.getLogger();

    public static byte[] generateSignature(SignatureAndHashAlgorithm algorithm, Chooser chooser, byte[] toBeSigned)
            throws CryptoException {
        switch (algorithm.getSignatureAlgorithm()) {
            case ANONYMOUS:
                return generateAnonymousSignature(chooser, toBeSigned, algorithm);
            case DSA:
                return generateDSASignature(chooser, toBeSigned, algorithm);
            case ECDSA:
                return generateECDSASignature(chooser, toBeSigned, algorithm);
            case RSA:
                return generateRSASignature(chooser, toBeSigned, algorithm);
            default:
                throw new UnsupportedOperationException("Unknown SignatureAlgorithm:"
                        + algorithm.getSignatureAlgorithm().name());
        }
    }

    public static byte[] generateSignature(PrivateKey key, byte[] toBeSigned, SignatureAndHashAlgorithm algorithm,
            BadRandom random, Chooser chooser) throws CryptoException {

        String algoName;
        if (chooser.getSelectedProtocolVersion() == ProtocolVersion.SSL3
                || chooser.getSelectedProtocolVersion() == ProtocolVersion.TLS10
                || chooser.getSelectedProtocolVersion() == ProtocolVersion.TLS11) {
            if (AlgorithmResolver.getKeyExchangeAlgorithm(chooser.getSelectedCipherSuite()).name().contains("RSA")) {
                algoName = "NONEwithRSA";
                toBeSigned = ArrayConverter.concatenate(MD5Utils.MD5(toBeSigned), SHA1Utils.sha1(toBeSigned));
            } else if (AlgorithmResolver.getKeyExchangeAlgorithm(chooser.getSelectedCipherSuite()).name()
                    .contains("ECDSA")) {
                algoName = "SHA1withECDSA";
            } else {
                throw new UnsupportedOperationException("Ciphersuite not supported - Check Debug Log");
            }
        } else {
            algoName = algorithm.getJavaName();
        }
        try {
            LOGGER.trace("Creating Signature with " + algoName + " over " + ArrayConverter.bytesToHexString(toBeSigned)
                    + " with the PrivateKey:" + key.toString());
            Signature instance = Signature.getInstance(algoName);
            instance.initSign(key, random);
            instance.update(toBeSigned);
            return instance.sign();
        } catch (SignatureException | InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new CryptoException("Could not sign Data", ex);
        }
    }

    public static byte[] generateRSASignature(Chooser chooser, byte[] toBeSigned, SignatureAndHashAlgorithm algorithm)
            throws CryptoException {
        RSAPrivateKey key = KeyGenerator.getRSAPrivateKey(chooser);
        return generateSignature(key, toBeSigned, algorithm, chooser.getContext().getBadSecureRandom(), chooser);
    }

    public static byte[] generateDSASignature(Chooser chooser, byte[] toBeSigned, SignatureAndHashAlgorithm algorithm)
            throws CryptoException {
        DSAPrivateKey key = KeyGenerator.getDSAPrivateKey(chooser);
        return generateSignature(key, toBeSigned, algorithm, chooser.getContext().getBadSecureRandom(), chooser);
    }

    public static byte[] generateECDSASignature(Chooser chooser, byte[] toBeSigned, SignatureAndHashAlgorithm algorithm)
            throws CryptoException {
        ECPrivateKey key = KeyGenerator.getECPrivateKey(chooser);
        return generateSignature(key, toBeSigned, algorithm, chooser.getContext().getBadSecureRandom(), chooser);
    }

    public static byte[] generateAnonymousSignature(Chooser chooser, byte[] toBeSigned,
            SignatureAndHashAlgorithm algorithm) {
        return new byte[0];
    }

    private SignatureCalculator() {
    }
}
