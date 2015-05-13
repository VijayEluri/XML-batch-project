package edu.bxml.regexp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BxmlPattern {
	final java.util.regex.Pattern pattern;

	public Pattern getPattern() {
		return pattern;
	}

	public BxmlPattern(String pattern) {
		this.pattern = java.util.regex.Pattern.compile(pattern);
	}

	public abstract String replace(List<String> match, Map env);

	public String execute(String line, Map env) {
		if (line == null)
			return line;
		StringBuffer out = new StringBuffer();

		Matcher matcher = pattern.matcher(line);
		String rest = line;
		int lastEnd = 0;

		while (matcher.find()) {
			out.append(line.substring(lastEnd, matcher.start()));
			List<String> groups = new ArrayList<String>();
			for (int i = 0; i <= matcher.groupCount(); i++) {
				groups.add(matcher.group(i));
			}
			out.append(replace(groups, env));
			rest = line.substring(matcher.end());
			lastEnd = matcher.end();
		}
		out.append(rest);

		return out.toString();
	}
}
