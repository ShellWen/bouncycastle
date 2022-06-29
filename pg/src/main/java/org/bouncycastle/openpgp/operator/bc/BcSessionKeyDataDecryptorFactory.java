package org.bouncycastle.openpgp.operator.bc;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPSessionKey;
import org.bouncycastle.openpgp.operator.PGPDataDecryptor;
import org.bouncycastle.openpgp.operator.SessionKeyDataDecryptorFactory;

/**
 * A decryptor factory for handling PGP session keys.
 */
public class BcSessionKeyDataDecryptorFactory
    implements SessionKeyDataDecryptorFactory
{
    private final PGPSessionKey sessionKey;

    public BcSessionKeyDataDecryptorFactory(PGPSessionKey sessionKey)
    {
        this.sessionKey = sessionKey;
    }

    public byte[] recoverSessionData(int keyAlgorithm, byte[] key, byte[] seckKeyData)
        throws PGPException
    {
        throw new IllegalStateException("trying to recover session data from session key!");
    }

    public byte[] recoverSessionData(int keyAlgorithm, byte[][] secKeyData)
        throws PGPException
    {
        throw new IllegalStateException("trying to recover session data from session key!");
    }

    public PGPSessionKey getSessionKey()
    {
        return sessionKey;
    }

    public PGPDataDecryptor createDataDecryptor(boolean withIntegrityPacket, int encAlgorithm, byte[] key)
        throws PGPException
    {
        BlockCipher engine = BcImplProvider.createBlockCipher(encAlgorithm);

        return BcUtil.createDataDecryptor(withIntegrityPacket, engine, key);
    }
}
