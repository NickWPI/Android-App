package com.lexicon.androidtest.androidtest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//unpacks the web socket data from CryptoCompare
public class CCUnpacker {
    public enum Field {
        TYPE(0x0),
        MARKET(0x0),
        FROMSYMBOL(0x0),
        TOSYMBOL(0x0),
        FLAGS(0x0),
        PRICE(0x1),
        BID(0x2),
        OFFER(0x4),
        LASTUPDATE(0x8),
        AVG(0x10),
        LASTVOLUME(0x20),
        LASTVOLUMETO(0x40),
        LASTTRADEID(0x80),
        VOLUMEHOUR(0x100),
        VOLUMEHOURTO(0x200),
        VOLUME24HOUR(0x400),
        VOLUME24HOURTO(0x800),
        OPENHOUR(0x1000),
        HIGHHOUR(0x2000),
        LOWHOUR(0x4000),
        OPEN24HOUR(0x8000),
        HIGH24HOUR(0x10000),
        LOW24HOUR(0x20000),
        LASTMARKET(0x40000);

        private int m_Value;

        Field(int value) {
            m_Value = value;
        }

        public int getValue() {
            return m_Value;
        }
    }

    public static Map<String, String> unpack(String tradeString) {
        Map<String, String> contents = new HashMap<String, String>();
        String[] tradeValues = tradeString.split("~");
        int length = tradeValues.length;
        String maskString = tradeValues[length - 1];
        int mask = Integer.parseInt(maskString, 16);
        int currentField = 0;
        for(Field field : Field.values()) {
            int intField = field.getValue();
            if(intField == 0) {
                contents.put(field.name(), tradeValues[currentField]);
                currentField++;
            }
            else if((mask & intField) != 0) {
                contents.put(field.name(), tradeValues[currentField]);
                currentField++;
            }
        }
        return contents;
    }

    public static String convertSmallNumberToDisplayValue(String value) {
        return value;
    }

    public static String convertLargeNumberToDisplayValue(String value) {
        Long longValue = Long.parseLong(value);
		if(longValue > 1000000000) {
			longValue /= 1000000000;
			//truncate to 3 significant digits
            String s = String.format(Locale.US, "%.3G", BigDecimal.valueOf(longValue));
			return s + "b";
		}
		else if(longValue > 1000000) {
			longValue /= 1000000;
            String s = String.format(Locale.US, "%.3G", BigDecimal.valueOf(longValue));
			return s + "m";
		} 
		else if(longValue > 1000) {
			longValue /= 1000;
            String s = String.format(Locale.US, "%.3G", BigDecimal.valueOf(longValue));
			return s + "k";
		}
		else if(longValue >= 1) {
			return String.valueOf(longValue);
		}
        return String.valueOf(longValue);
    }
}
