package com.eriklievaart.blog.parser.javacc;

import java.io.StringReader;

import org.junit.Test;

import com.eriklievaart.blog.parser.node.AstNode;
import com.eriklievaart.toolkit.lang.api.check.CheckStr;

public class ParserU {

	@Test
	public void parseEOF() {
		validate("", "");
	}

	@Test
	public void parseAnnotation() {
		validate("@author", "@");
	}

	@Test
	public void parseText() {
		validate("text", "T");
	}

	@Test
	public void parseEscapedText() {
		validate("text@@more", "T");
	}

	@Test
	public void parseNewline() {
		validate("\n", "N");
	}

	@Test
	public void parseWrapped() {
		validate("@code{System.exit(0);@code}", "@{T}");
	}

	@Test(expected = RuntimeException.class)
	public void parseWrappedWrongTag() {
		validate("@b{What's my tag?@u}", null);
	}

	@Test
	public void parseAT() {
		validate("@h1 text", "@T");
	}

	@Test
	public void parseAAT() {
		validate("@b @u text", "@@T");
	}

	@Test
	public void parseANA() {
		validate("@author\n@date", "@N@");
	}

	@Test
	public void parseATAT() {
		validate("@b emp@u even more", "@T@T");
	}

	@Test
	public void parseATNT() {
		validate("@h1 text\ntest", "@TNT");
	}

	@Test
	public void parseAATNT() {
		validate("@b @u a\nb", "@@TNT");
	}

	@Test
	public void parseWTNT() {
		validate("@code{System.println(\"It Works!\");\nSystem.exit(0);@code}", "@{TNT}");
	}

	@Test
	public void parseWTNTN() {
		validate("@code{System.println(\"It Works!\");\nSystem.exit(0);\n@code}", "@{TNTN}");
	}

	@Test
	public void parseWTT() {
		validate("@code{System.exit(0);@code}Is very gut code", "@{T}T");
	}

	@Test
	public void parseAWT() {
		validate("@b@code{System.exit(0);@code}", "@@{T}");
	}

	@Test
	public void parseAP() {
		validate("@source(javacc.jj)", "@");
	}

	@Test
	public void parseAPEmpty() {
		validate("@source()", "@");
	}

	@Test(expected = RuntimeException.class)
	public void parseAPInvalid() {
		validate("@source(())", "@");
	}

	public void validate(String input, String expected) {
		try {
			Parser p = new Parser(new StringReader(input));
			AstNode root = p.parseFile();
			CheckStr.isEqual(root.getRepresentation(), expected);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
