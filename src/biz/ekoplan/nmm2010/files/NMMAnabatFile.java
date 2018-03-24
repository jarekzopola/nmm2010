package biz.ekoplan.nmm2010.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Bertrik Sikken, Jaroslaw Kowalczyk
 *
 */
public class NMMAnabatFile {
    private static final int FILE_VERSION = 132;
    private static final int FILE_HEADER_SIZE = 6;
    private static final int DATA_INFO_SIZE = 54;
    private static final int TEXT_HEADER_SIZE = 276;
    
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    
    public int res1;
    public int vres;
    
    public byte[] idCode = new byte[6];
    public byte[] gpsData = new byte[32];
    
    private int divRatio;
    private List<Long> zeroCrossings;
    private String note = "";
    private String note1 = "";
    private String tape = "";
    private Date date = new Date();
    byte[] dateTime;
    
    /**
     * Constructor
     */
    public NMMAnabatFile() {
        // defaults
        res1 = 25000;
        vres = 0x52;
        note = "";
    }
    
    /**
     * @param fileName
     * @throws IOException
     */
    public boolean load(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        
        // file header
        ByteBuffer fileHeaderBuf = ByteBuffer.allocate(FILE_HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        stream.read(fileHeaderBuf.array());
        int dataInfoPtr = fileHeaderBuf.getShort() & 0xFFFF;
        fileHeaderBuf.get();
        int version = fileHeaderBuf.get() & 0xFF;
        fileHeaderBuf.get();
        fileHeaderBuf.get();
        if (version != FILE_VERSION) {
            System.out.println("Invalid file version " + version);
            return false;
        }
        
        // text header
        CharsetDecoder decoder = Charset.forName("US-ASCII").newDecoder();
        byte[] textHeader = new byte[TEXT_HEADER_SIZE];
        stream.read(textHeader);
        String tape     = decoder.decode(ByteBuffer.wrap(textHeader, 0, 8)).toString();
        String date     = decoder.decode(ByteBuffer.wrap(textHeader, 8, 8)).toString();
        System.out.println("Data czytana z pliku:"+date);
        String loc      = decoder.decode(ByteBuffer.wrap(textHeader, 16, 40)).toString();
        String species  = decoder.decode(ByteBuffer.wrap(textHeader, 56, 50)).toString();
        String spec     = decoder.decode(ByteBuffer.wrap(textHeader, 106, 16)).toString();
        note     = decoder.decode(ByteBuffer.wrap(textHeader, 122, 73)).toString();
        note1    = decoder.decode(ByteBuffer.wrap(textHeader, 195, 80)).toString();
        
        // data information table
        ByteBuffer dataInfoBuf = ByteBuffer.allocate(DATA_INFO_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        stream.read(dataInfoBuf.array());
        int dataStreamPtr = dataInfoBuf.getShort() & 0xFFFF;
        res1 = dataInfoBuf.getShort() & 0xFFFF;
        divRatio = dataInfoBuf.get() & 0xFF;
        vres = dataInfoBuf.get() & 0xFF;
        // date/time
        dateTime = new byte[10];
        dataInfoBuf.get(dateTime);
        this.getDateTime();
        
        // id code
        dataInfoBuf.get(idCode);
        // GPS data
        dataInfoBuf.get(gpsData);

        // data stream
        byte[] dataStream = new byte[(int) (file.length() - 0x150)];
        stream.read(dataStream);
        zeroCrossings = decode(dataStream);
        stream.close();
        
        return true;
    }
    
    /**
     * @param dataStream
     * @return
     */
    private List<Long> decode(byte[] dataStream) {
        List<Long> list = new ArrayList<Long>();
        ByteBuffer buf = ByteBuffer.wrap(dataStream);
        
        int interval = 0;
        long time = 0;
        while (buf.hasRemaining()) {
            byte data = buf.get();
            if ((data & (1 << 7)) == 0) {
                // interval delta 
                interval += data & 0x3F;
                if ((data & (1 << 6)) != 0) {
                    interval -= 64; // negative
                }
            } else {
                // absolute interval
                int extraBytes = ((data >> 5) & 0x3) + 1;
                if (extraBytes < 4) {
                    interval = data & 0x3F;
                    for (int i = 0; i < extraBytes; i++) {
                        interval = (interval << 8) | (buf.get() & 0xFF);
                    }
                } else {
                    // do nothing yet but consume byte
                    buf.get();
                }
            }
            
            time += interval;
            list.add(new Long(time));
        }
        return list;
    }
    
    /**
     * @param fileName
     * @throws IOException
     */
    public void save(File file) throws IOException {
        FileOutputStream stream = new FileOutputStream(file); 
        
        // file header
        ByteBuffer fileHeaderBuf = ByteBuffer.allocate(FILE_HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        fileHeaderBuf.putShort((short) 0x11A);
        fileHeaderBuf.put((byte) 0);
        fileHeaderBuf.put((byte) FILE_VERSION);
        fileHeaderBuf.put((byte) 0);
        fileHeaderBuf.put((byte) 0);
        stream.write(fileHeaderBuf.array());
        
        // text header
        CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();
        byte[] textHeaderArray = new byte[TEXT_HEADER_SIZE];
        for (int i = 0; i < (textHeaderArray.length - 1); i++) {
            textHeaderArray[i] = ' ';
        }
        ByteBuffer textHeaderBuf = ByteBuffer.wrap(textHeaderArray);
        textHeaderBuf.position(0);
        textHeaderBuf.put(encoder.encode(CharBuffer.wrap(tape)));
        textHeaderBuf.position(8);
        textHeaderBuf.put(encoder.encode(CharBuffer.wrap(DATE_FORMAT.format(date))));
        textHeaderBuf.position(122);
        textHeaderBuf.put(encoder.encode(CharBuffer.wrap(note)));
        textHeaderBuf.position(195);
        textHeaderBuf.put(encoder.encode(CharBuffer.wrap(note1)));
        stream.write(textHeaderBuf.array());
        
        // data information table
        ByteBuffer dataInfoBuf = ByteBuffer.allocate(DATA_INFO_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        dataInfoBuf.putShort((short) 0x150);
        dataInfoBuf.putShort((short) res1);
        dataInfoBuf.put((byte) divRatio);
        dataInfoBuf.put((byte) vres);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        dataInfoBuf.putShort((short) cal.get(Calendar.YEAR));
        dataInfoBuf.put((byte) (cal.get(Calendar.MONTH) + 1));
        dataInfoBuf.put((byte) cal.get(Calendar.DAY_OF_MONTH));
        dataInfoBuf.put((byte) cal.get(Calendar.HOUR_OF_DAY));
        dataInfoBuf.put((byte) cal.get(Calendar.MINUTE));
        dataInfoBuf.put((byte) cal.get(Calendar.SECOND));
        dataInfoBuf.put((byte) 0);
        dataInfoBuf.putShort((short) 0);
        dataInfoBuf.put(idCode);
        dataInfoBuf.put(gpsData);
        stream.write(dataInfoBuf.array());
        
        byte[] dataStream = encode(zeroCrossings);
        stream.write(dataStream);
        stream.close();
    }

    /**
     * @param list
     * @return
     */
    private byte[] encode(List<Long> list) {
        ByteBuffer buf = ByteBuffer.allocate(4 * list.size());

        long prevTime = 0;
        int prevInterval = 0;
        for (long time : list) {
            int interval = (int) (time - prevTime);
            int diff = interval - prevInterval;
            if ((diff >= -64) && (diff < 64)) {
                // encode as relative interval difference
                buf.put((byte) (diff & 0x7F));
            } else {
                // encode as absolute interval
                if (interval < (1 << 13)) {
                    buf.put((byte) (0x80 | (interval >> 8) & 0x1F));
                    buf.put((byte) ((interval >> 0) & 0xFF));
                } else if (interval < (1 << 21)) {
                    buf.put((byte) (0xA0 | (interval >> 16) & 0x1F));
                    buf.put((byte) ((interval >> 8) & 0xFF));
                    buf.put((byte) ((interval >> 0) & 0xFF));
                } else if (interval < (1 << 29)) {
                    buf.put((byte) (0xC0 | (interval >> 24) & 0x1F));
                    buf.put((byte) ((interval >> 16) & 0xFF));
                    buf.put((byte) ((interval >> 8) & 0xFF));
                    buf.put((byte) ((interval >> 0) & 0xFF));
                } else {
                    System.out.println("Cannot encode interval " + interval);
                }
            }
            // update previous values
            prevTime = time;
            prevInterval = interval;
        }
        
        // convert to byte[]
        byte[] dataStream = new byte[buf.position()];
        buf.rewind();
        buf.get(dataStream);
        return dataStream; 
    }

    public void setDivRatio(int divRatio) {
        this.divRatio = divRatio;
    }

    public void setZeroCrossings(List<Long> zeroCrossings) {
        this.zeroCrossings = zeroCrossings;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public void setNote1(String note) {
        this.note1 = note;
    }
    
    public void setTape(String tape) {
        this.tape  = tape;
    }
    
    public String getTape() {
        return this.tape;
    }
    
    public long getDateTime() {
        
        long t=0;
        System.out.println("--->"+this.dateTime.toString());
        ByteBuffer bb = ByteBuffer.wrap(this.dateTime).order(ByteOrder.LITTLE_ENDIAN);
        System.out.println("Rok: "+(bb.getShort() & 0xFFFF));
        System.out.println("Miesiąc: "+(bb.get() & 0xFF));
        System.out.println("Dzien: "+(bb.get() & 0xFF));
        System.out.println("Godzina: "+(bb.get() & 0xFF));
        System.out.println("Minuta: "+(bb.get() & 0xFF));
        System.out.println("Sekunda: "+(bb.get() & 0xFF));
        System.out.println("Setna: "+(bb.get() & 0xFF));
        System.out.println("Mikrosekund: "+(bb.getShort() & 0xFFFF));
        return t;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    private static char hexChar(int i) {
        if (i < 10) {
            return (char)('0' + i);
        }
        else {
            return (char)('A' + (i - 10));
        }
    }
    
    /**
     * Formats a file name according to date, using anabat conventions
     * 
     * @param date the date to convert
     * @return the file name string
     */
    public static String createFileName(Date date) {
        SimpleDateFormat encodeFormat = new SimpleDateFormat("ddHHmm.ss");

        StringBuilder sb = new StringBuilder();
        sb.append(hexChar(date.getYear() - 89));
        sb.append(hexChar(date.getMonth() + 1));
        sb.append(encodeFormat.format(date));
        sb.append('#');
        
        return sb.toString();
    }
    
    public List<Long> getZeroCrossings() {
                                        
        return this.zeroCrossings;                
    }

}
