package es.xism4.software.spigot.utils;

import java.util.regex.Pattern;

public class PatternCheck {

    public static Pattern safePatternCompile(String pattern) {
        try {
            return Pattern.compile(pattern);
        } catch (Exception var2) {
            System.out.println("Failed to compile pattern '" + pattern + "' - defaulting to allowing everything");
            return Pattern.compile(".*?");
        }
    }

}
