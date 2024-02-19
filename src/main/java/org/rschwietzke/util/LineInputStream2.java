package org.rschwietzke.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class LineInputStream2 implements AutoCloseable
{
	private final InputStream src;
	private byte[] buffer;
	private int startPos = 0;
	private int length = 0;
	private int separatorPos = -1;
	private int hash = 0;

	public String toString()
	{
		return Arrays.toString(new String(buffer).toCharArray());
	}

	public LineInputStream2(final InputStream src) throws IOException
	{
		this.src = src;
		this.buffer = new byte[1000];
	}

	public LineInputStream2(final InputStream src, int size) throws IOException
	{
		this.src = src;
		this.buffer = new byte[size];
	}

	public LineInfo readLine() throws IOException
	{
		this.hash = 0;
		this.separatorPos = -1;
		int currentPos = this.startPos;

		// read text and hash it
		currentPos = readText(currentPos);

		// read number
		currentPos = readNumber(currentPos);

		// if we got here, we might still have data

		// done
		if (this.separatorPos >= 0)
		{
			var l = new LineInfo(buffer, startPos, separatorPos, currentPos - 1, hash);
			this.startPos = currentPos + 1;
			return l;
		}
		else
		{
			// maybe we ended exactly at the end with the last line, so we
			// had a round with nothing
			return null;
		}
	}

	private int readText(int currentPos) throws IOException
	{
		int localHash = 0;
		byte[] localBuffer = buffer;
		int localLength = length;

		while (true)
		{
			// read the text first and calculate a hash
			for (; currentPos < localLength; currentPos++)
			{
				final byte c = localBuffer[currentPos];

				if (c == (byte)';')
				{
					// number continues later
					this.separatorPos = currentPos;
					this.hash = localHash;
					currentPos++;

					return currentPos;
				}
				else
				{
					// calculate hash
					localHash = (localHash << 5) - (localHash + c);
				}
			}

			// if we got here, we have not seen a ;, so we have to reload the buffer
			// if we are not into the buffer at all, we cannot move anything forward
			if (this.startPos == 0)
			{
				// we have not moved a bit, so increase buffer size
				localBuffer = buffer = Arrays.copyOf(buffer, (localBuffer.length << 1));

				// load from current pos up
				final int read = src.read(localBuffer, currentPos, localBuffer.length - currentPos);
				if (read == -1)
				{
					// all read, we end here
					this.hash = localHash;
					return currentPos;
				}

				this.length = localLength = currentPos + read;
			}
			else
			{
				// ok, we are not sitting at the front, so we can move
				int moveBy = startPos;
				System.arraycopy(localBuffer, startPos, buffer, 0, localBuffer.length - moveBy);

				// we can fill the rest now
				final int read = src.read(localBuffer, localBuffer.length - moveBy, moveBy);
				if (read == -1)
				{
					// ok, nothing more
					this.hash = localHash;
					return currentPos;
				}

				// adjust start pos and pos, we are into our current data, so don't start from 0
				// just continue reading, but we increase currentPos over length, so take it back
				// one
				currentPos = currentPos - moveBy;
				// current data record start is now here
				this.startPos = 0;
				// how much can we read
				this.length = localLength = currentPos + read;
				// seperator moved too
				this.separatorPos = this.separatorPos - moveBy;
			}
		}
	}

	private int readNumber(int currentPos) throws IOException
	{
		byte[] localBuffer = this.buffer;
		int localLength = this.length;

		while (true)
		{
			// read the text first and calculate a hash
			for (; currentPos < localLength; currentPos++)
			{
				final byte c = localBuffer[currentPos];

				if (c == (byte)'\n')
				{
					// end of line
					return currentPos;
				}
			}

			// if we got here, we have not seen a ;, so we have to reload the buffer
			// if we are not into the buffer at all, we cannot move anything forward
			if (this.startPos == 0)
			{
				// we have not moved a bit, so increase buffer size
				this.buffer = localBuffer = Arrays.copyOf(localBuffer, (localBuffer.length << 1));

				// load from current pos up
				final int read = src.read(localBuffer, currentPos, localBuffer.length - currentPos);
				if (read == -1)
				{
					// all read, we end here
					return currentPos;
				}

				this.length = localLength = currentPos + read;
			}
			else
			{
				// ok, we are not sitting at the front, so we can move
				int moveBy = startPos;
				System.arraycopy(localBuffer, startPos, localBuffer, 0, localBuffer.length - moveBy);

				// we can fill the rest now
				final int read = src.read(localBuffer, localBuffer.length - moveBy, moveBy);
				if (read == -1)
				{
					// ok, nothing more
					return currentPos;
				}

				// adjust start pos and pos, we are into our current data, so don't start from 0
				// just continue reading, but we increase currentPos over length, so take it back
				// one
				currentPos = currentPos - moveBy - 1;
				// current data record start is now here
				this.startPos = 0;
				// how much can we read
				this.length = localLength = currentPos + read + 1;
				// seperator moved too
				this.separatorPos = this.separatorPos - moveBy;
			}
		}
	}

	public static record LineInfo(byte[] buffer, int from, int separator, int end, int hash)
	{
		@Override
		public String toString()
		{
			return "LineInfo [text=" + text() + ", number=" + number() + ", hash=" + hash + "]";
		}

		public String text()
		{
			return new String(buffer, from, separator - from);
		}

		public String number()
		{
			return new String(buffer, separator + 1, end - separator);
		}
	}

	@Override
	public void close() throws IOException
	{
		src.close();
	}
}


