package osiris;

/**
 *
 * @author ZEGEEK
 */
public class Utils {
    public static short getDataLength(byte[] buffer, short startIndex, byte delimiter) {
        short length = 0;
       
        for(short i = startIndex; i < buffer.length; i++) {
            if(buffer[i] == delimiter) {
                break;
            }
            length++;
        }
        
        return length;
    }
    
    public static byte[] getDataFromBuffer(byte[] buffer, short startIndex, short length) {
        byte[] bytes = new byte[length];
        
        for(short i = 0; i < length; i++) {
            bytes[i] = buffer[(short)(startIndex + i)];
        }
        return bytes;
    }
    
    public static short byteArrayDataToNumber(byte[] buffer, short startIndex, short length) {
        short result = 0;
        
        if (length == 4) {
            result += (short)(buffer[startIndex] & 0xFF) * 1000;
            result += (short)(buffer[(short)(startIndex + 1)] & 0xFF) * 100;
            result += (short)(buffer[(short)(startIndex + 2)] & 0xFF) * 10;
            result += (short)(buffer[(short)(startIndex + 3)] & 0xFF);
        } else if (length == 3) {
            result += (short)(buffer[startIndex] & 0xFF) * 100;
            result += (short)(buffer[(short)(startIndex + 1)] & 0xFF) * 10;
            result += (short)(buffer[(short)(startIndex + 2)] & 0xFF);
        }
        
        return result;
    }
    
    public static byte[] numberToByteArray(short n) {
        return new byte[] { (byte) ((n & (short)0xFF00) >> (short)8), (byte) (n & (short)0x00FF) };
    }
}
