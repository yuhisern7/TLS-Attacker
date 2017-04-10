/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.tls.workflow.action;

import de.rub.nds.tlsattacker.tls.exceptions.WorkflowExecutionException;
import de.rub.nds.tlsattacker.tls.record.cipher.RecordNullCipher;
import de.rub.nds.tlsattacker.tls.workflow.TlsContext;
import de.rub.nds.tlsattacker.tls.workflow.action.executor.ActionExecutor;

/**
 * 
 * @author Robert Merget - robert.merget@rub.de
 */
public class DeactivateEncryptionAction extends TLSAction {

    public DeactivateEncryptionAction() {
    }

    @Override
    public void execute(TlsContext tlsContext, ActionExecutor executor) throws WorkflowExecutionException {
        tlsContext.getRecordLayer().setRecordCipher(new RecordNullCipher());
        tlsContext.getRecordLayer().updateDecryptionCipher();
        tlsContext.getRecordLayer().updateEncryptionCipher();
        LOGGER.info("Deactivated Encryption/Decryption");
        executed = true;
    }

    @Override
    public void reset() {
        executed = false;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DeactivateEncryptionAction;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

}
