
package org.strongswan.android.logic.imc;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Xml;

public class RemediationInstruction implements Parcelable
{
	private String mTitle;
	private String mDescription;
	private String mHeader;
	private final List<String> mItems = new LinkedList<String>();

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(mTitle);
		dest.writeString(mDescription);
		dest.writeString(mHeader);
		dest.writeStringList(mItems);
	}

	public static final Parcelable.Creator<RemediationInstruction> CREATOR = new Creator<RemediationInstruction>() {

		@Override
		public RemediationInstruction[] newArray(int size)
		{
			return new RemediationInstruction[size];
		}

		@Override
		public RemediationInstruction createFromParcel(Parcel source)
		{
			return new RemediationInstruction(source);
		}
	};

	private RemediationInstruction()
	{
	}

	private RemediationInstruction(Parcel source)
	{
		mTitle = source.readString();
		mDescription = source.readString();
		mHeader = source.readString();
		source.readStringList(mItems);
	}

	public String getTitle()
	{
		return mTitle;
	}

	private void setTitle(String title)
	{
		mTitle = title;
	}

	public String getDescription()
	{
		return mDescription;
	}

	private void setDescription(String description)
	{
		mDescription = description;
	}

	public String getHeader()
	{
		return mHeader;
	}

	private void setHeader(String header)
	{
		mHeader = header;
	}

	public List<String> getItems()
	{
		return Collections.unmodifiableList(mItems);
	}

	private void addItem(String item)
	{
		mItems.add(item);
	}


	public static List<RemediationInstruction> fromXml(String xml)
	{
		List<RemediationInstruction> instructions = new LinkedList<RemediationInstruction>();
		XmlPullParser parser = Xml.newPullParser();
		try
		{
			parser.setInput(new StringReader(xml));
			parser.nextTag();
			readInstructions(parser, instructions);
		}
		catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return instructions;
	}


	private static void readInstructions(XmlPullParser parser, List<RemediationInstruction> instructions) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, null, "remediationinstructions");
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			if (parser.getName().equals("instruction"))
			{
				RemediationInstruction instruction = new RemediationInstruction();
				readInstruction(parser, instruction);
				instructions.add(instruction);
			}
			else
			{
				skipTag(parser);
			}
		}
	}


	private static void readInstruction(XmlPullParser parser, RemediationInstruction instruction) throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.START_TAG, null, "instruction");
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String name = parser.getName();
			if (name.equals("title"))
			{
				instruction.setTitle(parser.nextText());
			}
			else if (name.equals("description"))
			{
				instruction.setDescription(parser.nextText());
			}
			else if (name.equals("itemsheader"))
			{
				instruction.setHeader(parser.nextText());
			}
			else if (name.equals("items"))
			{
				readItems(parser, instruction);
			}
			else
			{
				skipTag(parser);
			}
		}
	}


	private static void readItems(XmlPullParser parser, RemediationInstruction instruction) throws XmlPullParserException, IOException
	{
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			if (parser.getName().equals("item"))
			{
				instruction.addItem(parser.nextText());
			}
			else
			{
				skipTag(parser);
			}
		}
	}


	private static void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException
	{
		int depth = 1;

		parser.require(XmlPullParser.START_TAG, null, null);
		while (depth != 0)
		{
			switch (parser.next())
			{
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}
}
