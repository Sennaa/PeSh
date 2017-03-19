/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.util;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Locale;

public class OSInfo {
    private static HashMap<String, String> archMapping = new HashMap();
    public static final String X86 = "x86";
    public static final String X86_64 = "x86_64";
    public static final String IA64_32 = "ia64_32";
    public static final String IA64 = "ia64";
    public static final String PPC = "ppc";
    public static final String PPC64 = "ppc64";

    public static void main(String[] args) {
        if (args.length >= 1) {
            if ("--os".equals(args[0])) {
                System.out.print(OSInfo.getOSName());
                return;
            }
            if ("--arch".equals(args[0])) {
                System.out.print(OSInfo.getArchName());
                return;
            }
        }
        System.out.print(OSInfo.getNativeLibFolderPathForCurrentOS());
    }

    public static String getNativeLibFolderPathForCurrentOS() {
        return OSInfo.getOSName() + "/" + OSInfo.getArchName();
    }

    public static String getOSName() {
        return OSInfo.translateOSNameToFolderName(System.getProperty("os.name"));
    }

    public static String getArchName() {
        String osArch;
        block10 : {
            osArch = System.getProperty("os.arch");
            if (osArch.startsWith("arm")) {
                if (System.getProperty("sun.arch.abi") != null && System.getProperty("sun.arch.abi").startsWith("gnueabihf")) {
                    return OSInfo.translateArchNameToFolderName("armhf");
                }
                if (System.getProperty("os.name").contains("Linux")) {
                    String javaHome = System.getProperty("java.home");
                    try {
                        int exitCode2 = Runtime.getRuntime().exec("which readelf").waitFor();
                        if (exitCode2 == 0) {
                            String[] cmdarray = new String[]{"/bin/sh", "-c", "find '" + javaHome + "' -name 'libjvm.so' | head -1 | xargs readelf -A | " + "grep 'Tag_ABI_VFP_args: VFP registers'"};
                            exitCode2 = Runtime.getRuntime().exec(cmdarray).waitFor();
                            if (exitCode2 == 0) {
                                return OSInfo.translateArchNameToFolderName("armhf");
                            }
                            break block10;
                        }
                        System.err.println("WARNING! readelf not found. Cannot check if running on an armhf system, armel architecture will be presumed.");
                    }
                    catch (IOException exitCode2) {
                    }
                    catch (InterruptedException exitCode2) {}
                }
            } else {
                String lc = osArch.toLowerCase(Locale.US);
                if (archMapping.containsKey(lc)) {
                    return archMapping.get(lc);
                }
            }
        }
        return OSInfo.translateArchNameToFolderName(osArch);
    }

    static String translateOSNameToFolderName(String osName) {
        if (osName.contains("Windows")) {
            return "Windows";
        }
        if (osName.contains("Mac") || osName.contains("Darwin")) {
            return "Mac";
        }
        if (osName.contains("Linux")) {
            return "Linux";
        }
        if (osName.contains("AIX")) {
            return "AIX";
        }
        return osName.replaceAll("\\W", "");
    }

    static String translateArchNameToFolderName(String archName) {
        return archName.replaceAll("\\W", "");
    }

    static {
        archMapping.put("x86", "x86");
        archMapping.put("i386", "x86");
        archMapping.put("i486", "x86");
        archMapping.put("i586", "x86");
        archMapping.put("i686", "x86");
        archMapping.put("pentium", "x86");
        archMapping.put("x86_64", "x86_64");
        archMapping.put("amd64", "x86_64");
        archMapping.put("em64t", "x86_64");
        archMapping.put("universal", "x86_64");
        archMapping.put("ia64", "ia64");
        archMapping.put("ia64w", "ia64");
        archMapping.put("ia64_32", "ia64_32");
        archMapping.put("ia64n", "ia64_32");
        archMapping.put("ppc", "ppc");
        archMapping.put("power", "ppc");
        archMapping.put("powerpc", "ppc");
        archMapping.put("power_pc", "ppc");
        archMapping.put("power_rs", "ppc");
        archMapping.put("ppc64", "ppc64");
        archMapping.put("power64", "ppc64");
        archMapping.put("powerpc64", "ppc64");
        archMapping.put("power_pc64", "ppc64");
        archMapping.put("power_rs64", "ppc64");
    }
}

