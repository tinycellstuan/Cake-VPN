
package org.strongswan.android.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class IPRangeSet implements Iterable<IPRange>
{
	private TreeSet<IPRange> mRanges = new TreeSet<>();


	public static IPRangeSet fromString(String ranges)
	{
		IPRangeSet set = new IPRangeSet();
		if (ranges != null)
		{
			for (String range : ranges.split("\\s+"))
			{
				try
				{
					set.add(new IPRange(range));
				}
				catch (Exception unused)
				{
					return null;
				}
			}
		}
		return set;
	}


	public void add(IPRange range)
	{
		if (mRanges.contains(range))
		{
			return;
		}
		reinsert:
		while (true)
		{
			Iterator<IPRange> iterator = mRanges.iterator();
			while (iterator.hasNext())
			{
				IPRange existing = iterator.next();
				IPRange replacement = existing.merge(range);
				if (replacement != null)
				{
					iterator.remove();
					range = replacement;
					continue reinsert;
				}
			}
			mRanges.add(range);
			break;
		}
	}


	public void add(IPRangeSet ranges)
	{
		if (ranges == this)
		{
			return;
		}
		for (IPRange range : ranges.mRanges)
		{
			add(range);
		}
	}


	public void addAll(Collection<? extends IPRange> coll)
	{
		for (IPRange range : coll)
		{
			add(range);
		}
	}


	public void remove(IPRange range)
	{
		ArrayList <IPRange> additions = new ArrayList<>();
		Iterator<IPRange> iterator = mRanges.iterator();
		while (iterator.hasNext())
		{
			IPRange existing = iterator.next();
			List<IPRange> result = existing.remove(range);
			if (result.size() == 0)
			{
				iterator.remove();
			}
			else if (!result.get(0).equals(existing))
			{
				iterator.remove();
				additions.addAll(result);
			}
		}
		mRanges.addAll(additions);
	}

	public void remove(IPRangeSet ranges)
	{
		if (ranges == this)
		{
			mRanges.clear();
			return;
		}
		for (IPRange range : ranges.mRanges)
		{
			remove(range);
		}
	}


	public Iterable<IPRange> subnets()
	{
		return new Iterable<IPRange>()
		{
			@Override
			public Iterator<IPRange> iterator()
			{
				return new Iterator<IPRange>()
				{
					private Iterator<IPRange> mIterator = mRanges.iterator();
					private List<IPRange> mSubnets;

					@Override
					public boolean hasNext()
					{
						return (mSubnets != null && mSubnets.size() > 0) || mIterator.hasNext();
					}

					@Override
					public IPRange next()
					{
						if (mSubnets == null || mSubnets.size() == 0)
						{
							IPRange range = mIterator.next();
							mSubnets = range.toSubnets();
						}
						return mSubnets.remove(0);
					}

					@Override
					public void remove()
					{
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	@Override
	public Iterator<IPRange> iterator()
	{
		return mRanges.iterator();
	}


	public int size()
	{
		return mRanges.size();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (IPRange range : mRanges)
		{
			if (sb.length() > 0)
			{
				sb.append(" ");
			}
			sb.append(range.toString());
		}
		return sb.toString();
	}
}
