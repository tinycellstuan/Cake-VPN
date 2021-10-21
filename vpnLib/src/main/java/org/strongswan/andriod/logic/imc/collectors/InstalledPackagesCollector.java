
package org.strongswan.android.logic.imc.collectors;

import java.util.List;

import org.strongswan.android.logic.imc.attributes.Attribute;
import org.strongswan.android.logic.imc.attributes.InstalledPackagesAttribute;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class InstalledPackagesCollector implements Collector
{
	private final PackageManager mPackageManager;

	public InstalledPackagesCollector(Context context)
	{
		mPackageManager = context.getPackageManager();
	}

	@Override
	public Attribute getMeasurement()
	{
		InstalledPackagesAttribute attribute = new InstalledPackagesAttribute();
		List<PackageInfo> packages = mPackageManager.getInstalledPackages(0);
		for (PackageInfo info : packages)
		{
			if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ||
				info.packageName == null || info.versionName == null)
			{
				continue;
			}
			attribute.addPackage(info.packageName, info.versionName);
		}
		return attribute;
	}
}
