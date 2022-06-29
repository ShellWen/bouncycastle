package org.bouncycastle.openpgp.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.test.SimpleTest;

public class ArmoredOutputStreamUTF8Test
    extends SimpleTest
{
    public static void main(String[] args)
    {
        runTest(new ArmoredOutputStreamUTF8Test());
    }

    public String getName()
    {
        return "ArmoredOutputStreamUTF8Test";
    }

    public void performTest()
        throws Exception
    {
        String utf8WithUmlauts = "ÄÖÜß&Umlauts";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArmoredOutputStream armorOut = new ArmoredOutputStream(out);
        armorOut.setHeader("Comment", utf8WithUmlauts);

        armorOut.write(Strings.toUTF8ByteArray("Foo\nBar"));

        armorOut.close();

        String armoredOutput = out.toString();
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(Strings.toUTF8ByteArray(armoredOutput));
        ArmoredInputStream armorIn = new ArmoredInputStream(bytesIn);
        String[] header = armorIn.getArmorHeaders();

        String[] lines = armoredOutput.split("\n");
        String comment = null;
        for (int i = 0; i != lines.length; i++)
        {
            String line = lines[i];
            if (line.startsWith("Comment: "))
            {
                comment = line.substring("Comment: ".length());
                break;
            }
        }

        isTrue("We did not find the comment line. This MUST never happen.", comment != null);
        isEquals("Comment was not properly encoded. Expected: " + utf8WithUmlauts + ", Actual: " + comment, comment, utf8WithUmlauts);

        // round-tripped comment from ascii armor input stream
        isEquals(header[1].substring("Comment: ".length()), utf8WithUmlauts);
    }
}
