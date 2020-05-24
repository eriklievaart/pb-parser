package com.eriklievaart.blog.parser.node;

import java.util.Arrays;
import java.util.List;

import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.pattern.PatternTool;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class AnnotationNodeWrapper {

	private static final String REGEX = "@([a-z0-9]++)(?:\\(([^()]*+)\\))?(?:[{]?)";

	private AstNode node;
	private final String name;
	private final List<String> arguments;

	public AnnotationNodeWrapper(AstNode node) {
		this.node = node;
		Check.isTrue(node.getType() == AstNodeType.SLA || node.getType() == AstNodeType.MLA_OPEN);

		List<String> groups = splitTokenImage(node.getToken().image);
		name = groups.get(1);
		arguments = splitArguments(groups.get(2));
	}

	public String getName() {
		return name;
	}

	static List<String> splitArguments(String arguments) {
		if (Str.isBlank(arguments)) {
			return Arrays.asList();
		}
		return Arrays.asList(PatternTool.split("\\s*,\\s*", arguments));
	}

	static List<String> splitTokenImage(String raw) {
		List<String> groups = PatternTool.getGroups(REGEX, raw);
		CheckCollection.notEmpty(groups, "% invalid", raw);
		return groups;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public int getLineNumber() {
		return node.getToken().beginLine;
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[@$]", name);
	}
}
