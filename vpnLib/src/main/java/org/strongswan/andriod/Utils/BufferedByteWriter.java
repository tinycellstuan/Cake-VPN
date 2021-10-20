
package org.strongswan.android.utils;

import java.nio.ByteBuffer;


public class BufferedByteWriter
{

	private byte[] mBuffer;


	private ByteBuffer mWriter;

	public BufferedByteWriter()
	{
		this(0);
	}


	public BufferedByteWriter(int capacity)
	{
		capacity = capacity > 4 ? capacity : 32;
		mBuffer = new byte[capacity];
		mWriter = ByteBuffer.wrap(mBuffer);
	}


	private void ensureCapacity(int required)
	{
		if (mWriter.remaining() >= required)
		{
			return;
		}
		byte[] buffer = new byte[(mBuffer.length + required) * 2];
		System.arraycopy(mBuffer, 0, buffer, 0, mWriter.position());
		mBuffer = buffer;
		ByteBuffer writer = ByteBuffer.wrap(buffer);
		writer.position(mWriter.position());
		mWriter = writer;
	}


	public BufferedByteWriter put(byte[] value)
	{
		ensureCapacity(value.length);
		mWriter.put(value);
		return this;
	}

	public BufferedByteWriter put(byte value)
	{
		ensureCapacity(1);
		mWriter.put(value);
		return this;
	}


	public BufferedByteWriter putLen8(byte[] value)
	{
		ensureCapacity(1 + value.length);
		mWriter.put((byte)value.length);
		mWriter.put(value);
		return this;
	}


	public BufferedByteWriter putLen16(byte[] value)
	{
		ensureCapacity(2 + value.length);
		mWriter.putShort((short)value.length);
		mWriter.put(value);
		return this;
	}


	public BufferedByteWriter put16(byte value)
	{
		return this.put16((short)(value & 0xFF));
	}


	public BufferedByteWriter put16(short value)
	{
		ensureCapacity(2);
		mWriter.putShort(value);
		return this;
	}


	public BufferedByteWriter put24(byte value)
	{
		ensureCapacity(3);
		mWriter.putShort((short)0);
		mWriter.put(value);
		return this;
	}


	public BufferedByteWriter put24(short value)
	{
		ensureCapacity(3);
		mWriter.put((byte)0);
		mWriter.putShort(value);
		return this;
	}


	public BufferedByteWriter put24(int value)
	{
		ensureCapacity(3);
		mWriter.put((byte)(value >> 16));
		mWriter.putShort((short)value);
		return this;
	}


	public BufferedByteWriter put32(byte value)
	{
		return put32(value & 0xFF);
	}


	public BufferedByteWriter put32(short value)
	{
		return put32(value & 0xFFFF);
	}


	public BufferedByteWriter put32(int value)
	{
		ensureCapacity(4);
		mWriter.putInt(value);
		return this;
	}


	public BufferedByteWriter put64(byte value)
	{
		return put64(value & 0xFFL);
	}


	public BufferedByteWriter put64(short value)
	{
		return put64(value & 0xFFFFL);
	}


	public BufferedByteWriter put64(int value)
	{
		return put64(value & 0xFFFFFFFFL);
	}


	public BufferedByteWriter put64(long value)
	{
		ensureCapacity(8);
		mWriter.putLong(value);
		return this;
	}


	public byte[] toByteArray()
	{
		int length = mWriter.position();
		byte[] bytes = new byte[length];
		System.arraycopy(mBuffer, 0, bytes, 0, length);
		return bytes;
	}
}
