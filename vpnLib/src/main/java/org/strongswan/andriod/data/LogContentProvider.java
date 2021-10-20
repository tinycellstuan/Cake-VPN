
package org.strongswan.android.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

import org.strongswan.android.logic.CharonVpnService;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.OpenableColumns;

public class LogContentProvider extends ContentProvider
{
	private static final String AUTHORITY = "org.strongswan.android.content.log";
	private static final long URI_VALIDITY = 30 * 60 * 1000;
	private static ConcurrentHashMap<Uri, Long> mUris = new ConcurrentHashMap<Uri, Long>();
	private File mLogFile;

	public LogContentProvider()
	{
	}

	@Override
	public boolean onCreate()
	{
		mLogFile = new File(getContext().getFilesDir(), CharonVpnService.LOG_FILE);
		return true;
	}

	public static Uri createContentUri()
	{
		SecureRandom random;
		try
		{
			random = SecureRandom.getInstance("SHA1PRNG");
		}
		catch (NoSuchAlgorithmException e)
		{
			return null;
		}
		Uri uri = Uri.parse("content://" + AUTHORITY + "/" + random.nextLong());
		mUris.put(uri, SystemClock.uptimeMillis());
		return uri;
	}

	@Override
	public String getType(Uri uri)
	{

		return "text/plain";
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder)
	{
		if (projection == null || projection.length < 1)
		{
			return null;
		}
		Long timestamp = mUris.get(uri);
		if (timestamp == null)
		{
			return null;
		}
		MatrixCursor cursor = new MatrixCursor(projection, 1);
		if (OpenableColumns.DISPLAY_NAME.equals(cursor.getColumnName(0)))
		{
			cursor.newRow().add(CharonVpnService.LOG_FILE);
		}
		else if (OpenableColumns.SIZE.equals(cursor.getColumnName(0)))
		{
			cursor.newRow().add(mLogFile.length());
		}
		else
		{
			return null;
		}
		return cursor;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException
	{
		Long timestamp = mUris.get(uri);
		if (timestamp != null)
		{
			long elapsed = SystemClock.uptimeMillis() - timestamp;
			if (elapsed > 0 && elapsed < URI_VALIDITY)
			{
				return ParcelFileDescriptor.open(mLogFile, ParcelFileDescriptor.MODE_CREATE | ParcelFileDescriptor.MODE_READ_ONLY);
			}
			mUris.remove(uri);
		}
		return super.openFile(uri, mode);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{

		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{

		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
					  String[] selectionArgs)
	{

		return 0;
	}
}
