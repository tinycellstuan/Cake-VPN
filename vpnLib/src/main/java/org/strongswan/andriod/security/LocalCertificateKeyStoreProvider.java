
package org.strongswan.android.security;

import java.security.Provider;

public class LocalCertificateKeyStoreProvider extends Provider
{
	private static final long serialVersionUID = 3515038332469843219L;

	public LocalCertificateKeyStoreProvider()
	{
		super("LocalCertificateKeyStoreProvider", 1.0, "KeyStore provider for local certificates");
		put("KeyStore.LocalCertificateStore", LocalCertificateKeyStoreSpi.class.getName());
	}
}
