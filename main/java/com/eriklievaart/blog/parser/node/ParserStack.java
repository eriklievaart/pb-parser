package com.eriklievaart.blog.parser.node;

import java.util.Stack;

import com.eriklievaart.blog.parser.javacc.ParserConstants;
import com.eriklievaart.blog.parser.javacc.Token;
import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class ParserStack {

	private Stack<AstNode> stack = new Stack<>();

	public ParserStack() {
		stack.push(new AstNode(null));
	}

	public AstNode popRoot() {
		while (stack.size() > 1) {
			AstNode node = stack.pop();
			if (node.isOpenMLA()) {
				throw new FormattedException("$ missing closing annotation", node);
			}
		}
		return stack.get(0);
	}

	public void openAnnotation(Token token) {
		Check.isTrue(token.kind == ParserConstants.ANNOTATION_OPEN);

		AstNode node = new AstNode(token);
		stack.peek().addChild(node);
		stack.push(node);
	}

	public void closeMLA(Token token) {
		while (stack.peek().isSLA()) {
			stack.pop();
		}
		AstNode openNode = stack.pop();
		String openAnnotationImage = openNode.getToken().image;
		boolean valid = openNode.isOpenMLA() && openAnnotationImage.startsWith(token.image.replace("}", ""));
		Check.isTrue(valid, "$ found, expecting closing tag for $", new AstNode(token), openNode);
	}

	public void popMLA(Token closeToken) {
		while (stack.peek().getToken() != null && stack.peek().isSLA()) {
			stack.pop();
		}
		AstNode openNode = stack.pop();
		Token openToken = openNode.getToken();
		boolean valid = openNode.isCloseMLA() && openToken.image.startsWith(closeToken.image);
		Check.isTrue(valid, "$ found, expecting closing tag for $", new AstNode(closeToken), openNode);
	}

	public void addText(Token token) {
		Check.isTrue(token.kind == ParserConstants.TEXT);
		stack.peek().addChild(new AstNode(token));
	}

	public void addNL(Token token) {
		Check.isTrue(token.kind == ParserConstants.NL);
		while (stack.peek().isSLA()) {
			stack.pop();
		}
		stack.peek().addChild(new AstNode(token));
	}
}
