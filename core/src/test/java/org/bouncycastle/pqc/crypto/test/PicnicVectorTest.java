package org.bouncycastle.pqc.crypto.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

import junit.framework.TestCase;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.pqc.crypto.picnic.PicnicKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicKeyPairGenerator;
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicSigner;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class PicnicVectorTest
    extends TestCase

{

    public void testParameters()
            throws Exception
    {
        //todo
    }


    public void testVectors()
        throws Exception
    {
        boolean full = System.getProperty("test.full", "false").equals("true");
        String[] files;
        PicnicParameters[] params;
        if (full)
        {
            files = new String[]{
                    "picnicl1fs.rsp",
                    "picnicl1ur.rsp",
                    "picnicl3fs.rsp",
                    "picnicl3ur.rsp",
                    "picnicl5fs.rsp",
                    "picnicl5ur.rsp",
                    "picnic3l1.rsp",
                    "picnic3l3.rsp",
                    "picnic3l5.rsp",
                    "picnicl1full.rsp",
                    "picnicl3full.rsp",
                    "picnicl5full.rsp",

            };
            params = new PicnicParameters[]{
                    PicnicParameters.picnicl1fs,
                    PicnicParameters.picnicl1ur,
                    PicnicParameters.picnicl3fs,
                    PicnicParameters.picnicl3ur,
                    PicnicParameters.picnicl5fs,
                    PicnicParameters.picnicl5ur,
                    PicnicParameters.picnic3l1,
                    PicnicParameters.picnic3l3,
                    PicnicParameters.picnic3l5,
                    PicnicParameters.picnicl1full,
                    PicnicParameters.picnicl3full,
                    PicnicParameters.picnicl5full
            };
        }
        else
        {
            files = new String[]{
                    "picnicl1fs.rsp",
                    "picnic3l1.rsp",
                    "picnicl3ur.rsp",
                    "picnicl1full.rsp",
            };
            params = new PicnicParameters[]{
                    PicnicParameters.picnicl1fs,
                    PicnicParameters.picnic3l1,
                    PicnicParameters.picnicl3ur,
                    PicnicParameters.picnicl1full,
            };
        }


        for (int fileIndex = 0; fileIndex != files.length; fileIndex++)
        {
            String name = files[fileIndex];
            System.out.println("testing: " + name);
            InputStream src = PicnicVectorTest.class.getResourceAsStream("/org/bouncycastle/pqc/crypto/test/picnic/" + name);
            BufferedReader bin = new BufferedReader(new InputStreamReader(src));

            String line = null;
            HashMap<String, String> buf = new HashMap<String, String>();
            Random rnd = new Random(System.currentTimeMillis());
            while ((line = bin.readLine()) != null)
            {
                line = line.trim();

                if (line.startsWith("#"))
                {
                    continue;
                }
                if (line.length() == 0)
                {
                    if (buf.size() > 0)
                    {
                        String count = buf.get("count");
                        if (!"0".equals(count))
                        {
                            // randomly skip tests after zero.
                            if (rnd.nextBoolean())
                            {
                                continue;
                            }
                        }
                        System.out.println("test case: " + count);
                        byte[] seed = Hex.decode(buf.get("seed"));      // seed for picnic secure random
                        int mlen = Integer.parseInt(buf.get("mlen"));   // message length
                        byte[] msg = Hex.decode(buf.get("msg"));        // message
                        byte[] pk = Hex.decode(buf.get("pk"));          // public key
                        byte[] sk = Hex.decode(buf.get("sk"));          // private key
                        int smlen = Integer.parseInt(buf.get("smlen")); // signature length
                        byte[] sigExpected = Hex.decode(buf.get("sm"));          // signature

//                        System.out.println("message: " + Hex.toHexString(msg));
                        NISTSecureRandom random = new NISTSecureRandom(seed, null);
                        PicnicParameters parameters = params[fileIndex];


                        PicnicKeyPairGenerator kpGen = new PicnicKeyPairGenerator();
                        PicnicKeyGenerationParameters genParams = new PicnicKeyGenerationParameters(random, parameters);
                        //
                        // Generate keys and test.
                        //
                        kpGen.init(genParams);
                        AsymmetricCipherKeyPair kp = kpGen.generateKeyPair();


                        PicnicPublicKeyParameters pubParams = (PicnicPublicKeyParameters) PublicKeyFactory.createKey(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(kp.getPublic()));
                        PicnicPrivateKeyParameters privParams = (PicnicPrivateKeyParameters) PrivateKeyFactory.createKey(PrivateKeyInfoFactory.createPrivateKeyInfo(kp.getPrivate()));

//                        System.out.println("pk = " + Hex.toHexString(pubParams.getEncoded()).toUpperCase());
//                        System.out.println("sk = " + Hex.toHexString(privParams.getEncoded()).toUpperCase());

                        assertTrue(name + " " + count + ": public key", Arrays.areEqual(pk, pubParams.getEncoded()));
                        assertTrue(name + " " + count + ": secret key", Arrays.areEqual(sk, privParams.getEncoded()));


                        //
                        // Signature test
                        //
                        PicnicSigner signer = new PicnicSigner(random);

                        signer.init(true, privParams);

                        byte[] sigGenerated = signer.generateSignature(msg);

//                        System.out.println("expected:\t" + Hex.toHexString(sigExpected));
//                        System.out.println("generated:\t" + Hex.toHexString(sigGenerated));

                        assertEquals(name + " " + count + ": signature length", smlen, sigGenerated.length);

                        signer.init(false, pubParams);

                        assertTrue(name + " " + count + ": signature verify", signer.verifySignature(msg, sigGenerated));
                        assertTrue(name + " " + count + ": signature gen match", Arrays.areEqual(sigExpected, sigGenerated));

                    }
                    buf.clear();

                    continue;
                }

                int a = line.indexOf("=");
                if (a > -1)
                {
                    buf.put(line.substring(0, a).trim(), line.substring(a + 1).trim());
                }
            }
            System.out.println("testing successful!");
        }
    }

}
