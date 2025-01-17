package org.bouncycastle.openpgp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.bouncycastle.bcpg.BCPGInputStream;
import org.bouncycastle.bcpg.LiteralDataPacket;
import org.bouncycastle.bcpg.Packet;
import org.bouncycastle.bcpg.PacketTags;

/**
 * A single literal data packet in a PGP object stream.
 */
public class PGPLiteralData
{
    /** Format tag for binary literal data */
    public static final char    BINARY = 'b';
    /** Format tag for textual literal data */
    public static final char    TEXT = 't';
    /** Format tag for UTF-8 encoded textual literal data */
    public static final char    UTF8 = 'u';
    /** Format tag for MIME message bodies */
    public static final char    MIME = 'm';

    /**
     * The special name indicating a "for your eyes only" packet.
     */
    public static final String  CONSOLE = "_CONSOLE";

    /**
     * The special time for a modification time of "now" or
     * the present time.
     */
    public static final Date    NOW = new Date(0L);

    LiteralDataPacket    data;

    /**
     * Construct a PGP LiteralData carrier from the passed in byte array.
     *
     * @param encData an encoding of PGP literal data.
     * @throws IOException if an error occurs reading from the PGP input.
     */
    public PGPLiteralData(
        byte[]    encData)
        throws IOException
    {
        this(Util.createBCPGInputStream(new ByteArrayInputStream(encData), PacketTags.LITERAL_DATA));
    }

    /**
     * Construct a PGP LiteralData carrier from the passed in input stream.
     *
     * @param inStream an input stream containing an encoding of PGP literal data.
     * @throws IOException if an error occurs reading from the PGP input.
     */
    public PGPLiteralData(
        InputStream    inStream)
        throws IOException
    {
        this(Util.createBCPGInputStream(inStream, PacketTags.LITERAL_DATA));
    }

    /**
     * Construct a PGP LiteralData carrier from the passed in BCPG input stream.
     *
     * @param pIn a BCPG input stream containing an encoded PGP literal data structure.
     * @throws IOException if an error occurs reading from the PGP input.
     */
    public PGPLiteralData(
        BCPGInputStream    pIn)
        throws IOException
    {
        Packet packet = pIn.readPacket();
        if (!(packet instanceof LiteralDataPacket))
        {
            throw new IOException("unexpected packet in stream: " + packet);
        }
        data  = (LiteralDataPacket)packet;
    }

    /**
     * Return the format of the data packet. One of {@link #BINARY}, {@link #TEXT}, {@link #MIME} or {@link #UTF8}
     */
    public int getFormat()
    {
        return data.getFormat();
    }

    /**
     * Return the file name associated with the data packet.
     */
    public String getFileName()
    {
        return data.getFileName();
    }

    /**
     * Return the file name as an uninterpreted (UTF-8 encoded) byte array.
     */
    public byte[] getRawFileName()
    {
        return data.getRawFileName();
    }

    /**
     * Return the modification time for the file (at second level precision).
     */
    public Date getModificationTime()
    {
        return new Date(data.getModificationTime());
    }

    /**
     * Return the length of the contained literal data, if available. Returns null if the
     * length is not available, which is the case for not partial data packets if not yet
     * fully read.
     */
    public Long getDataLengthIfAvailable()
    {
        return data.getInputStream().getBodyLengthIfAvailable();
    }

    /**
     * Return the length of the contained literal data, if available. This method skips over
     * the entire content of the packet to find the length if necessary, and should be used
     * only if the content is not required. Returns null if the length is not available.
     */
    public Long findDataLength()
    {
        return data.getInputStream().findDataLength();
    }

    /**
     * Return the raw input stream for the data packet.
     */
    public InputStream getInputStream()
    {
        return data.getInputStream();
    }

    /**
     * Return the input stream representing the data stream.
     * Equivalent to {@link #getInputStream()}.
     */
    public InputStream getDataStream()
    {
        return this.getInputStream();
    }
}
