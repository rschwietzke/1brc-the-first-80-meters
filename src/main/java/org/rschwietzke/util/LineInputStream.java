package org.rschwietzke.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class LineInputStream implements AutoCloseable
{
	private final InputStream src;
	private byte[] buffer;
	private int currentPos = 0;
	private int startPos = 0;
	private int length = 0;
	private int separatorPos = -1;
	private int hash = 0;

	public String toString()
	{
		return Arrays.toString(new String(buffer).toCharArray());
	}

	public LineInputStream(final InputStream src) throws IOException
	{
		this.src = src;
		this.buffer = new byte[512];
	}

	public LineInputStream(final InputStream src, int size) throws IOException
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
		readText();

		// read number
		readNumber();

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

	private void readText() throws IOException
	{
		while (true)
		{
			// read the text first and calculate a hash
			for (; currentPos < length; currentPos++)
			{
				final byte c = buffer[currentPos];

				if (c == (byte)';')
				{
					// number continues later
					this.separatorPos = currentPos;
					currentPos++;
					return;
				}
				else
				{
					// calculate hash
					hash = (hash << 5) - hash + c;
				}
			}

			// if we got here, we have not seen a ;, so we have to reload the buffer
			// if we are not into the buffer at all, we cannot move anything forward
			if (this.startPos == 0)
			{
				// we have not moved a bit, so increase buffer size
				buffer = Arrays.copyOf(buffer, (this.buffer.length << 1));

				// load from current pos up
				final int read = src.read(buffer, currentPos, this.buffer.length - currentPos);
				if (read == -1)
				{
					// all read, we end here
					return;
				}

				this.length = currentPos + read;
			}
			else
			{
				// ok, we are not sitting at the front, so we can move
				int moveBy = startPos;
				System.arraycopy(buffer, startPos, buffer, 0, this.buffer.length - moveBy);

				// we can fill the rest now
				final int read = src.read(buffer, this.buffer.length - moveBy, moveBy);
				if (read == -1)
				{
					// ok, nothing more
					return;
				}

				// adjust start pos and pos, we are into our current data, so don't start from 0
				// just continue reading, but we increase currentPos over length, so take it back
				// one
				this.currentPos = this.currentPos - moveBy;
				// current data record start is now here
				this.startPos = 0;
				// how much can we read
				this.length = this.currentPos + read;;
				// seperator moved too
				this.separatorPos = this.separatorPos - moveBy;
			}
		}
	}

	private void readNumber() throws IOException
	{
		while (true)
		{
			// read the text first and calculate a hash
			for (; currentPos < length; currentPos++)
			{
				final byte c = buffer[currentPos];

				if (c == (byte)'\n')
				{
					// end of line
					return;
				}
			}

			// if we got here, we have not seen a ;, so we have to reload the buffer
			// if we are not into the buffer at all, we cannot move anything forward
			if (this.startPos == 0)
			{
				// we have not moved a bit, so increase buffer size
				buffer = Arrays.copyOf(buffer, (this.buffer.length << 1));

				// load from current pos up
				final int read = src.read(buffer, currentPos, this.buffer.length - currentPos);
				if (read == -1)
				{
					// all read, we end here
					return;
				}

				this.length = currentPos + read;
			}
			else
			{
				// ok, we are not sitting at the front, so we can move
				int moveBy = startPos;
				System.arraycopy(buffer, startPos, buffer, 0, this.buffer.length - moveBy);

				// we can fill the rest now
				final int read = src.read(buffer, this.buffer.length - moveBy, moveBy);
				if (read == -1)
				{
					// ok, nothing more
					return;
				}

				// adjust start pos and pos, we are into our current data, so don't start from 0
				// just continue reading, but we increase currentPos over length, so take it back
				// one
				this.currentPos = this.currentPos - moveBy - 1;
				// current data record start is now here
				this.startPos = 0;
				// how much can we read
				this.length = this.currentPos + read + 1;
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


