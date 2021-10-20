
package org.strongswan.android.logic;

import android.util.Log;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TrustedCertificateManager extends Observable
{
	private static final String TAG = TrustedCertificateManager.class.getSimpleName();
	private final ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();
	private Hashtable<String, X509Certificate> mCACerts = new Hashtable<String, X509Certificate>();
	private volatile boolean mReload;
	private boolean mLoaded;
	private final ArrayList<KeyStore> mKeyStores = new ArrayList<KeyStore>();

	public enum TrustedCertificateSource
	{
		SYSTEM("system:"),
		USER("user:"),
		LOCAL("local:");

		private final String mPrefix;

		private TrustedCertificateSource(String prefix)
		{
			mPrefix = prefix;
		}

		private String getPrefix()
		{
			return mPrefix;
		}
	}


	private TrustedCertificateManager()
	{
		for (String name : new String[]{"LocalCertificateStore", "AndroidCAStore"})
		{
			KeyStore store;
			try
			{
				store = KeyStore.getInstance(name);
				store.load(null, null);
				mKeyStores.add(store);
			}
			catch (Exception e)
			{
				Log.e(TAG, "Unable to load KeyStore: " + name);
				e.printStackTrace();
			}
		}
	}


	private static class Singleton
	{
		public static final TrustedCertificateManager mInstance = new TrustedCertificateManager();
	}

	public static TrustedCertificateManager getInstance()
	{
		return Singleton.mInstance;
	}


	public TrustedCertificateManager reset()
	{
		Log.d(TAG, "Force reload of cached CA certificates on next load");
		this.mReload = true;
		this.setChanged();
		this.notifyObservers();
		return this;
	}


	public TrustedCertificateManager load()
	{
		Log.d(TAG, "Ensure cached CA certificates are loaded");
		this.mLock.writeLock().lock();
		if (!this.mLoaded || this.mReload)
		{
			this.mReload = false;
			loadCertificates();
		}
		this.mLock.writeLock().unlock();
		return this;
	}

	private void loadCertificates()
	{
		Log.d(TAG, "Load cached CA certificates");
		Hashtable<String, X509Certificate> certs = new Hashtable<String, X509Certificate>();
		for (KeyStore store : this.mKeyStores)
		{
			fetchCertificates(certs, store);
		}
		this.mCACerts = certs;
		if (!this.mLoaded)
		{
			this.setChanged();
			this.notifyObservers();
			this.mLoaded = true;
		}
		Log.d(TAG, "Cached CA certificates loaded");
	}


	private void fetchCertificates(Hashtable<String, X509Certificate> certs, KeyStore store)
	{
		try
		{
			Enumeration<String> aliases = store.aliases();
			while (aliases.hasMoreElements())
			{
				String alias = aliases.nextElement();
				Certificate cert;
				cert = store.getCertificate(alias);
				if (cert != null && cert instanceof X509Certificate)
				{
					certs.put(alias, (X509Certificate)cert);
				}
			}
		}
		catch (KeyStoreException ex)
		{
			ex.printStackTrace();
		}
	}


	public X509Certificate getCACertificateFromAlias(String alias)
	{
		X509Certificate certificate = null;

		if (this.mLock.readLock().tryLock())
		{
			certificate = this.mCACerts.get(alias);
			this.mLock.readLock().unlock();
		}
		else
		{
			for (KeyStore store : this.mKeyStores)
			{
				try
				{
					Certificate cert = store.getCertificate(alias);
					if (cert != null && cert instanceof X509Certificate)
					{
						certificate = (X509Certificate)cert;
						break;
					}
				}
				catch (KeyStoreException e)
				{
					e.printStackTrace();
				}
			}
		}
		return certificate;
	}

	@SuppressWarnings("unchecked")
	public Hashtable<String, X509Certificate> getAllCACertificates()
	{
		Hashtable<String, X509Certificate> certs;
		this.mLock.readLock().lock();
		certs = (Hashtable<String, X509Certificate>)this.mCACerts.clone();
		this.mLock.readLock().unlock();
		return certs;
	}


	public Hashtable<String, X509Certificate> getCACertificates(TrustedCertificateSource source)
	{
		Hashtable<String, X509Certificate> certs = new Hashtable<String, X509Certificate>();
		this.mLock.readLock().lock();
		for (String alias : this.mCACerts.keySet())
		{
			if (alias.startsWith(source.getPrefix()))
			{
				certs.put(alias, this.mCACerts.get(alias));
			}
		}
		this.mLock.readLock().unlock();
		return certs;
	}
}
