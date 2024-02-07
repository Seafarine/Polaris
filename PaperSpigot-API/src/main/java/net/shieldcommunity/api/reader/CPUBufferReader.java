package net.shieldcommunity.api.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A simple set of commands to get CPU's name (e.g. Intel (R) Core(TM) i7-6700K ...) and manufacturer (e.g. GenuineIntel)
 *
 * Created by minjaesong on 2018-11-26.
 */
public class CPUBufferReader {

    private static final int LINUX = 0;
    private static final int WIN = 1;
    private static final int OSX = 2;

    private static int getOS() {
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN")) {
            return WIN;
        }
        else if (OS.contains("OS X")) {
            return OSX;
        }
        else {
            return LINUX;
        }
    }

    /**
     * Gets CPU's model name as saved in your operation system.
     *
     * This method will first identify the OS and then run a suitable command.
     *
     * Commands executed are:
     *
     * <ul>
     *     <li>Windows: <code>wmic cpu get name</code>; Vista or higher is required</li>
     *     <li>Linux: <code>cat /proc/cpuinfo</code> and try to fetch the <code>model name</code> field (grep is not used as in some environments it just doesn't work, tested on repl.it online IDE)</li>
     *     <li>Mac: <code>sysctl -a</code> and try to fetch the <code>machdep.cpu.brand_string</code> field</li>
     * </ul>
     *
     * If the OS is neither Windows nor Mac, it will fall back to Linux, which means it won't work on Solaris.
     *
     * @return CPU brand name; <code>null</code> on failure
     * @throws IOException
     */
    public static String getModelName() throws IOException {
        String lineRead;
        String returnLine = "";
        BufferedReader br;
        boolean lineFound = false;

        switch (getOS()) {
            case WIN:
                br = runCmdAndGetReader("wmic cpu get name");

                // actually try to read
                while ( (lineRead = br.readLine()) != null) {
                    lineRead = lineRead.trim(); // strings are somehow shit dirty
                    // only keep the longest readLine, this is a rule of thumb
                    if (!lineRead.startsWith("Name") && lineRead.length() > returnLine.length()) {
                        returnLine = lineRead;
                    }
                }

                br.close();
                return returnLine;
            case LINUX:
                br = runCmdAndGetReader("cat /proc/cpuinfo");

                // actually try to read
                while ( (lineRead = br.readLine()) != null) {
                    // filter line "model name    : ........"
                    if (lineRead.startsWith("model name")) {
                        int dropCnt = 0;
                        while (lineRead.charAt(dropCnt) != ':') {
                            dropCnt += 1;
                            //lineRead = lineRead.substring(1);
                        } // slowly drop a char from the beginning until we see ':'

                        lineRead = lineRead.substring(dropCnt + 2); // drop ': '
                        lineFound = true;
                        break;
                    }
                }

                br.close();

                if (!lineFound) return null;

                return lineRead;
            case OSX:
                br = runCmdAndGetReader("sysctl -a");

                // actually try to read
                while ( (lineRead = br.readLine()) != null) {
                    // filter line "model name    : ........"
                    if (lineRead.startsWith("machdep.cpu.brand_string")) {
                        int dropCnt = 0;
                        while (lineRead.charAt(dropCnt) != ':') {
                            dropCnt += 1;
                        } // slowly drop a char from the beginning until we see ':'

                        lineRead = lineRead.substring(dropCnt + 2); // drop ': '
                        lineFound = true;
                        break;
                    }
                }

                br.close();

                if (!lineFound) return null;

                return lineRead;
        }

        return null;
    }

    /**
     * Gets CPU's vendor ID (CPUID) as saved in your operation system.
     *
     * This method will first identify the OS and then run a suitable command.
     *
     * This command will only work on x86/AMD64 processors, as <code>CPUID</code> is x86 instruction. For other processors, <code>null</code> will be returned.
     *
     * Commands executed are:
     *
     * <ul>
     *     <li>Windows: <code>wmic cpu get manufacturer</code>; Vista or higher is required</li>
     *     <li>Linux: <code>cat /proc/cpuinfo</code> and try to fetch the <code>vendor_id</code> field (grep is not used as in some environments it just doesn't work, tested on repl.it online IDE)</li>
     *     <li>Mac: <code>sysctl -a</code> and try to fetch the <code>machdep.cpu.vendor</code> field</li>
     * </ul>
     *
     * If the OS is neither Windows nor Mac, it will fall back to Linux, which means it won't work on Solaris.
     *
     * @return
     * @throws IOException
     */
    public static String getCPUID() throws IOException {
        String lineRead;
        String returnLine = "";
        BufferedReader br;
        boolean lineFound = false;

        switch (getOS()) {
            case WIN:
                br = runCmdAndGetReader("wmic cpu get manufacturer");

                // actually try to read
                while ( (lineRead = br.readLine()) != null) {
                    lineRead = lineRead.trim(); // strings are somehow shit dirty
                    // only keep the longest readLine, this is a rule of thumb
                    if (!lineRead.startsWith("Manufacturer") && lineRead.length() > returnLine.length()) {
                        returnLine = lineRead;
                    }
                }

                br.close();
                return returnLine;
            case LINUX:
                br = runCmdAndGetReader("cat /proc/cpuinfo");

                // actually try to read
                while ( (lineRead = br.readLine()) != null) {
                    // filter line "vendor_id    : ........"
                    if (lineRead.startsWith("vendor_id")) {
                        int dropCnt = 0;
                        while (lineRead.charAt(dropCnt) != ':') {
                            lineRead = lineRead.substring(1);
                        } // slowly drop a char from the beginning until we see ':'

                        lineRead = lineRead.substring(dropCnt + 2); // drop ': '
                        lineFound = true;
                        break;
                    }
                }

                br.close();

                if (!lineFound) return null;

                return lineRead;
            case OSX:
                br = runCmdAndGetReader("sysctl -a");

                // actually try to read
                while ( (lineRead = br.readLine()) != null) {
                    // filter line "model name    : ........"
                    if (lineRead.startsWith("machdep.cpu.vendor")) {
                        int dropCnt = 0;
                        while (lineRead.charAt(dropCnt) != ':') {
                            lineRead = lineRead.substring(1);
                        } // slowly drop a char from the beginning until we see ':'

                        lineRead = lineRead.substring(dropCnt + 2); // drop ': '
                        lineFound = true;
                        break;
                    }
                }

                br.close();

                if (!lineFound) return null;

                return lineRead;
        }

        return null;
    }

    private static BufferedReader runCmdAndGetReader(String cmd) throws IOException {
        Process p = Runtime.getRuntime().exec(cmd);
        InputStreamReader ir = new InputStreamReader(p.getInputStream());
        BufferedReader br = new BufferedReader(ir);

        return br;
    }

}