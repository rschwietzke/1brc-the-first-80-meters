package org.rschwietzke.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import org.junit.jupiter.api.Test;
import org.rschwietzke.util.LineInputStream.LineInfo;

public class LineInputStreamTest
{
	private record SplitLine(String text, String number, int hash)
	{
		public SplitLine(String s)
		{
			this(s.split(";")[0], s.split(";")[1], s.split(";")[0].hashCode());
		}
	}

	private void helper(byte[] buffer, String expected)
	{
		this.helper(buffer, 512, expected);
	}

	private void helper(byte[] buffer, int size, String expected)
	{
		List<SplitLine> lines = new ArrayList<>();
		List<SplitLine> expectedLines = Arrays.stream(expected.split("\n")).map(SplitLine::new).toList();

		try (var r = new LineInputStream(new ByteArrayInputStream(buffer), size))
		{
			LineInfo l;
			while ((l = r.readLine()) != null)
			{
				lines.add(new SplitLine(l.text(), l.number(), l.hash()));
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		assertEquals(expectedLines.size(), lines.size());

		for (int i = 0; i < lines.size(); i++)
		{
			var e = expectedLines.get(i);
			var c = lines.get(i);
			assertEquals(e.text, c.text);
			assertEquals(e.number, c.number);
			assertEquals(e.hash, c.hash);
		}
	}

	@Test
	public void oneLinerInBuffer() throws IOException
	{
		var lines = """
				1:Test;12
				""";
		helper(lines.getBytes(), lines);
	}

	@Test
	public void oneLinerInBufferNoLineEnding() throws IOException
	{
		var lines = """
				1:Test;12""";
		helper(lines.getBytes(), lines);
	}

	@Test
	public void twoLinesInBuffer()
	{
		var lines = """
				1:Test;12.1
				2:Foo;1.1
				""";
		helper(lines.getBytes(), lines);
	}

	@Test
	public void threeLinesInBuffer()
	{
		var lines = """
				1:ABCDEFGH;12.1
				2:ABCDEFGH;-11.2
				3:ABCDEFGH;99.9
				""";
		helper(lines.getBytes(), lines);
	}

	@Test
	public void bufferEndInText()
	{
		var lines = """
				1:ABCDEFGH;12.1
				2:ABCDEFGH;-11.2
				3:ABCDEFGH;99.9
				""";
		helper(lines.getBytes(), 8, lines);
	}

	@Test
	public void bufferEndInSemicolon()
	{
		var lines = """
				1:ABCDEFGH;12.1
				2:ABCDEFGH;-11.2
				3:ABCDEFGH;99.9
				""";
		helper(lines.getBytes(), 9, lines);
		helper(lines.getBytes(), 10, lines);
		helper(lines.getBytes(), 11, lines);
	}

	@Test
	public void bufferEndInNumber()
	{
		var lines = """
				1:ABCDEFGH;12.1
				2:ABCDEFGH;-11.2
				3:ABCDEFGH;99.9
				""";
		helper(lines.getBytes(), 11, lines);
		helper(lines.getBytes(), 12, lines);
		helper(lines.getBytes(), 13, lines);
	}

	@Test
	public void bufferEndInNewline()
	{
		var lines = """
				1:ABCDEFGH;12.1
				2:ABCDEFGH;-11.2
				3:ABCDEFGH;99.9
				""";
		helper(lines.getBytes(), 14, lines);
		helper(lines.getBytes(), 15, lines);
		helper(lines.getBytes(), 16, lines);
	}

	@Test
	public void generatedExample()
	{
		final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final StringBuilder sb = new StringBuilder(19181);
		final RandomGenerator r = RandomGeneratorFactory.getDefault().create();

		for (int i = 0; i < 10000; i++)
		{
			sb.append(i);

			// text
			int length = r.nextInt(1, 100);
			for (int l = 0; l < length; l++)
			{
				sb.append(CHARS.charAt(r.nextInt(CHARS.length())));
			}
			sb.append(";");

			// number
			int number = r.nextInt(1,  2000) - 1000; // -999 - 999
			sb.append(BigDecimal.valueOf(number).divide(BigDecimal.valueOf(10)).toString());

			sb.append("\n");
		}

		var lines = sb.toString();
		var b = lines.getBytes();
		helper(b, 12, lines);
		helper(b, 128, lines);
		helper(b, 728, lines);
		helper(b, 100, lines);
		helper(b, 500, lines);
		helper(b, 1001, lines);
		helper(b, 1000, lines);
	}

}
