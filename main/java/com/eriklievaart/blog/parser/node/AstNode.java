package com.eriklievaart.blog.parser.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.eriklievaart.blog.parser.api.ParserConstants;
import com.eriklievaart.blog.parser.api.Token;
import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class AstNode {
	private static final AstNodeType[] VALID_PARENTS = new AstNodeType[] { AstNodeType.MLA_OPEN, AstNodeType.SLA,
			AstNodeType.ROOT };

	private Token token;
	private AstNodeType type = null;
	private List<AstNode> children = new ArrayList<>();

	public AstNode(Token token) {
		this.token = token;
		type = getAstNodeType(token);
	}

	private static AstNodeType getAstNodeType(Token token) {
		if (token == null) {
			return AstNodeType.ROOT;
		}
		switch (token.kind) {

		case ParserConstants.ANNOTATION_OPEN:
			return token.image.endsWith("{") ? AstNodeType.MLA_OPEN : AstNodeType.SLA;

		case ParserConstants.ANNOTATION_CLOSE:
			return AstNodeType.MLA_CLOSE;

		case ParserConstants.NL:
			return AstNodeType.NL;

		case ParserConstants.TEXT:
			return AstNodeType.TEXT;
		}
		throw new FormattedException("% is unkown", ParserConstants.tokenImage[token.kind]);
	}

	public Token getToken() {
		return token;
	}

	public void addChild(AstNode child) {
		Check.isTrue(ArrayUtils.contains(VALID_PARENTS, type), "$ cannot have children", toString());
		children.add(child);
	}

	public List<AstNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}

	public String getRepresentation() {
		StringBuilder builder = new StringBuilder();

		if (token == null) {
			getRepresentationChildren(builder);
			return builder.toString();
		}
		switch (token.kind) {

		case ParserConstants.ANNOTATION_OPEN:
			builder.append("@");
			if (isSLA()) {
				getRepresentationChildren(builder);
			} else {
				builder.append("{");
				getRepresentationChildren(builder);
				builder.append("}");
			}
			break;

		case ParserConstants.TEXT:
			builder.append("T");
			break;

		case ParserConstants.NL:
			builder.append("N");
			break;

		default:
			throw new FormattedException("Unknown Token Type $", token);
		}
		return builder.toString();
	}

	private void getRepresentationChildren(StringBuilder builder) {
		for (AstNode child : children) {
			builder.append(child.getRepresentation());
		}
	}

	public boolean isSLA() {
		return type == AstNodeType.SLA;
	}

	public boolean isOpenMLA() {
		return type == AstNodeType.MLA_OPEN;
	}

	public boolean isCloseMLA() {
		return type == AstNodeType.MLA_CLOSE;
	}

	public AstNodeType getType() {
		return type;
	}

	@Override
	public String toString() {
		if (token == null) {
			return "[ROOT]";
		}
		return Str.sub("$[$:$]", ParserConstants.tokenImage[token.kind], token.beginLine, token.image);
	}
}
