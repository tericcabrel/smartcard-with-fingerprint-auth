package osirisclient;

/**
 *
 * @author ZEGEEK
 */
public class Utils {
    public static String byteArrayToString(byte[] byteArray) {
        short n = (short) (((byteArray[byteArray.length - 2] << 8)) | ((byteArray[byteArray.length - 1] & 0xff)));
        
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length - 2; i++) {
            hexStringBuffer.append((char) byteArray[i]);
        }
        
        return hexStringBuffer.toString() + String.valueOf(n);
    }
    
    public static byte[] numberStringToByteArray(String str) {
        if (str == null) {
            return new byte[]{ };
        }
        
        int strLength = str.length();
        byte[] bytes = new byte[strLength];
        
        for(int i = 0; i < strLength; i++) {
            bytes[i] = Integer.valueOf(str.charAt(i) + "", 10).byteValue();
        }
        
        return bytes;
    }
    
    public static byte[] prepareNumberForApdu(int number) {
        int length = number > 1000 ? 4 : 3;
        byte[] result = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        int temp = 0;
        
        if(length == 4) {
            int milli = (number - number%1000) / 1000;
            temp = number - (milli * 1000);
            int centaine = (temp - temp%100) / 100;
            temp = temp - (centaine * 100);
            int dizaine = (temp - temp%10) / 10;
            temp = temp - (dizaine * 10);
            
            result = new byte[]{ (byte)milli, (byte)centaine, (byte)dizaine, (byte)temp};
        } else if (length == 3) {
            int centaine = (number - number % 100) / 100;
            temp = number - (centaine * 100);
            int dizaine = (temp - temp % 10) / 10;
            temp = temp - (dizaine * 10);
            
            result = new byte[]{ (byte)centaine, (byte)dizaine, (byte)temp };
        }
              
        return result;
    }
}
