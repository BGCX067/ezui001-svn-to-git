package cn.vstore.appserver.util;

public class NetUtil {

    public static String getSubnet(String subnet) {
        String sn = subnet.trim();
        String[] ss = subnet.trim().split("/");
        if (ss.length == 2) {
            int mask = Integer.parseInt(ss[1].trim());
            String masks = "";
            if (mask < 32) {
                sn = ss[0];
                for (int i = 0; i < 3; i++) {
                    if (mask >= 8) {
                        masks += "255.";
                        mask = mask - 8;
                    }
                }
                if (mask > 0) {
                    String lastMask = "";
                    for (int i = 0; i < mask; i++) {
                        lastMask += "1";
                    }
                    for (int i = lastMask.length(); i < 8; i++) {
                        lastMask += "0";
                    }
                    masks += Integer.parseInt(lastMask, 2);
                } else {
                    masks += "0";
                }
                String[] ms = masks.split("\\.");
                for (int i = ms.length; i < 4; i++) {
                    masks += ".0";
                }
                sn = subnet(sn, masks);
            }
        } else {
            sn += "/32";
        }
        return sn;
    }

    public static String subnet(String ip, String mask) {
        String[] subnetIps = ip.split("\\.");
        String[] netmasks = mask.split("\\.");
        int[] nms = new int[netmasks.length];
        mask = "";
        for (int i = 0; i < netmasks.length; i++) {
            nms[i] = new Integer(netmasks[i]);
            mask += Integer.toString(nms[i], 2);
        }
        mask = mask.replaceAll("0", "");
        String ret = "";
        for (int i = 0; i < subnetIps.length; i++) {
            ret += "" + (nms[i] & new Integer(subnetIps[i])) + ".";
        }
        ret = ret.substring(0, ret.length() - 1);

        return ret + "/" + mask.length();
    }

    public static boolean isInSubnet(String ip, String subnet, String mask) {
        String[] ips = ip.split("\\.");
        String[] subnetIps = subnet.split("\\.");
        String[] netmasks = mask.trim().split("\\.");
        int[] nms = new int[netmasks.length];
        for (int i = 0; i < netmasks.length; i++) {
            nms[i] = new Integer(netmasks[i].trim());
        }
        for (int i = 0; i < subnetIps.length; i++) {
            subnetIps[i] = "" + (nms[i] & new Integer(subnetIps[i]));
        }
        for (int i = 0; i < ips.length; i++) {
            ips[i] = "" + (nms[i] & new Integer(ips[i]));
            if (!ips[i].equals(subnetIps[i])) {
                return false;
            }
        }
        return true;
    }

    public static String getMask(int mask) {
        String masks = "";
        if (mask <= 32) {
            for (int i = 0; i < 3; i++) {
                if (mask >= 8) {
                    masks += "255.";
                    mask = mask - 8;
                }
            }
            if (mask > 0) {
                String lastMask = "";
                for (int i = 0; i < mask; i++) {
                    lastMask += "1";
                }
                for (int i = lastMask.length(); i < 8; i++) {
                    lastMask += "0";
                }
                masks += Integer.parseInt(lastMask, 2);
            } else {
                masks += "0";
            }
            String[] ms = masks.split("\\.");
            for (int i = ms.length; i < 4; i++) {
                masks += ".0";
            }
        }
        return masks;
    }
}
