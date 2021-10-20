
package org.strongswan.android.utils;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class Utils
{
	static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	public static String bytesToHex(byte[] bytes)
	{
		char[] hex = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++)
		{
			int value = bytes[i];
			hex[i*2]   = HEXDIGITS[(value & 0xf0) >> 4];
			hex[i*2+1] = HEXDIGITS[ value & 0x0f];
		}
		return new String(hex);
	}


	public native static boolean isProposalValid(boolean ike, String proposal);


	private native static byte[] parseInetAddressBytes(String address);


	public static InetAddress parseInetAddress(String address) throws UnknownHostException
	{
		byte[] bytes = parseInetAddressBytes(address);
		if (bytes == null)
		{
			throw new UnknownHostException();
		}
		return InetAddress.getByAddress(bytes);
	}


	public static byte[] getUTF8Bytes(String str) {
		try { return str.getBytes("UTF-8"); } catch (Exception ex) { return null; }
	}



	public static String getMACAddress(String interfaceName) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				if (interfaceName != null) {
					if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
				}
				byte[] mac = intf.getHardwareAddress();
				if (mac==null) return "";
				StringBuilder buf = new StringBuilder();
				for (byte aMac : mac) buf.append(String.format("%02X:",aMac));
				if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
				return buf.toString();
			}
		} catch (Exception ignored) { }
		return "";

	}


	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress();
						boolean isIPv4 = sAddr.indexOf(':')<0;

						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%');
								return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
							}
						}
					}
				}
			}
		} catch (Exception ignored) { }
		return "";
	}
}
