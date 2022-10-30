package me.xmrvizzy.skyblocker.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtils {
    public static List<String> addQuotes(Collection<String> list){
        List<String> list2 = new ArrayList<String>();
        for(String line:list){
            list2.add(String.format("\'%s\'", line));
        }
        return list2;
    }
    public static List<String> addQuotesIfNeeded(Collection<String> list){
        List<String> list2 = new ArrayList<String>();
        String pattern = ".*\\W.*";
        for(String line:list){
            if(Pattern.matches(pattern, line)){
                list2.add(String.format("\'%s\'", line.replace("\\", "\\\\").replace("\'", "\\\'")));
            }
            else
            list2.add(line);
        }
        return list2;
    }
    public static String addQuotesIfNeeded(String line){
        String pattern = ".*\\W.*";
        if(Pattern.matches(pattern, line)){
            return(String.format("\'%s\'", line.replace("\\", "\\\\").replace("\'", "\\\'")));
        }
        else
        return(line);
    }
}
