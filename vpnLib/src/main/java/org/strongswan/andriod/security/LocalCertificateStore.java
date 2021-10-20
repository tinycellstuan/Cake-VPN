
package org.strongswan.android.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import org.strongswan.android.logic.MainApplication;
import org.strongswan.android.utils.Utils;

import android.content.Context;

public class LocalCertificateStore
{
	private static final String FILE_PREFIX = "certificate-";
	private static final String ALIAS_PREFIX = "local:";
	private static final Pattern ALIAS_PATTERN = Pattern.compile("^" + ALIAS_PREFIX + "[0-9a-f]{40}$");

		public boolean addCertificate(Certificate cert)
	{
		if (!(cert instanceof X509Certificate))
		{
			return false;
		}
		String keyid = getKeyId(cert);
		if (keyid == null)
		{
			return false;
		}
		FileOutputStream out;
		try
		{

			out = MainApplication.getContext().openFileOutput(FILE_PREFIX + keyid, Context.MODE_PRIVATE);
			try
			{
				out.write(cert.getEncoded());
				return true;
			}
			catch (CertificateEncodingException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}


	public void deleteCertificate(String alias)
	{
		if (ALIAS_PATTERN.matcher(alias).matches())
		{
			alias = alias.substring(ALIAS_PREFIX.length());
			MainApplication.getContext().deleteFile(FILE_PREFIX + alias);
		}
	}


	public X509Certificate getCertificate(String alias)
	{
		if (!ALIAS_PATTERN.matcher(alias).matches())
		{
			return null;
		}
		alias = alias.substring(ALIAS_PREFIX.length());
		try
		{
			FileInputStream in = MainApplication.getContext().openFileInput(FILE_PREFIX + alias);
			try
			{
				CertificateFactory factory = CertificateFactory.getInstance("X.509");
				X509Certificate certificate = (X509Certificate)factory.generateCertificate(in);
				return certificate;
			}
			catch (CertificateException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}


	public Date getCreationDate(String alias)
	{
		if (!ALIAS_PATTERN.matcher(alias).matches())
		{
			return null;
		}
		alias = alias.substring(ALIAS_PREFIX.length());
		File file = MainApplication.getContext().getFileStreamPath(FILE_PREFIX + alias);
		return file.exists() ? new Date(file.lastModified()) : null;
	}

	public ArrayList<String> aliases()
	{
		ArrayList<String> list = new ArrayList<String>();
		for (String file : MainApplication.getContext().fileList())
		{
			if (file.startsWith(FILE_PREFIX))
			{
				list.add(ALIAS_PREFIX + file.substring(FILE_PREFIX.length()));
			}
		}
		return list;
	}


	public boolean containsAlias(String alias)
	{
		return getCreationDate(alias) != null;
	}


	public String getCertificateAlias(Certificate cert)
	{
		String keyid = getKeyId(cert);
		return keyid != null ? ALIAS_PREFIX + keyid : null;
	}


	private String getKeyId(Certificate cert)
	{
		MessageDigest md;
		try
		{
			md = java.security.MessageDigest.getInstance("SHA1");
			byte[] hash = md.digest(cert.getPublicKey().getEncoded());
			return Utils.bytesToHex(hash);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
