package me.xmrvizzy.skyblocker.utils;
import java.util.HashMap;

public class RomanNumeralsParser {
    static final HashMap<Character, Integer> RomanMap = new HashMap<Character, Integer>();
    static{
        RomanMap.put('I', 1);
        RomanMap.put('V', 5);
        RomanMap.put('X', 10);
        RomanMap.put('L', 50);
        RomanMap.put('C', 100);
        RomanMap.put('D', 500);
        RomanMap.put('M', 1000);
    }
    public static int romanToInt(String s) {
        if (s == null || s.length() == 0)    return -1;
        try {
            int len = s.length(), result = RomanMap.get(s.charAt(len - 1));
            for (int i = len - 2; i >= 0; i--) {
                if (RomanMap.get(s.charAt(i)) >= RomanMap.get(s.charAt(i + 1)))
                    result += RomanMap.get(s.charAt(i));
                else
                    result -= RomanMap.get(s.charAt(i));
            }
            return result;
        } catch (Exception e) {
            return -1;
        }
    }
}
