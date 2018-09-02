package com.eriklievaart.blog.parser.node;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.eriklievaart.blog.parser.node.AnnotationNodeWrapper;
import com.eriklievaart.toolkit.lang.api.check.CheckStr;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class AnnotationNodeWrapperU {

	@Test
	public void simple() {
		testSplitTokenImage("@author", "author");
	}

	@Test
	public void alphanumeric() {
		testSplitTokenImage("@h1", "h1");
	}

	@Test
	public void emptyParentheses() {
		testSplitTokenImage("@date()", "date");
	}

	@Test
	public void argument() {
		testSplitTokenImage("@source(javacc.jj)", "source,javacc.jj");
	}

	@Test
	public void brace() {
		testSplitTokenImage("@code{", "code");
	}

	@Test
	public void emptyParenthesesBrace() {
		testSplitTokenImage("@code(){", "code");
	}

	@Test
	public void argumentBrace() {
		testSplitTokenImage("@source(java){", "source,java");
	}

	@Test
	public void splitArguments() {
		List<String> split = AnnotationNodeWrapper.splitArguments("one,two , three");
		CheckStr.isEqual(StringUtils.join(split, "|"), "one|two|three");
	}

	private void testSplitTokenImage(String input, String expected) {
		List<String> split = AnnotationNodeWrapper.splitTokenImage(input);
		String found = split.get(1);
		if (Str.notBlank(split.get(2))) {
			found += "," + split.get(2);
		}
		CheckStr.isEqual(found, expected);
	}

}
