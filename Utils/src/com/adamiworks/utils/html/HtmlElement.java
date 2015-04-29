package com.adamiworks.utils.html;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an util to generate HTML structures with full support to element
 * values and attributes.
 * 
 * @author Tiago
 *
 */
public class HtmlElement {
	private List<Attribute> attributes;
	private String name;
	private String value = "";

	public HtmlElement(String name) {
		this.name = name;
	}

	public HtmlElement addAttribute(String name, String value) {
		if (attributes == null) {
			attributes = new ArrayList<HtmlElement.Attribute>();
		}
		Attribute a = new Attribute();
		a.name = name;
		a.value = value;
		attributes.add(a);
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public HtmlElement setValue(String value) {
		//value = value.replaceAll("<", "&lt;");
		//value = value.replaceAll(">", "&gt;");
		this.value = value;
		return this;
	}

	public HtmlElement setValue(HtmlElement element) {
		this.value = element.toString();
		return this;
	}

	public HtmlElement appendValue(String value) {
		//value = value.replaceAll("<", "&lt;");
		//value = value.replaceAll(">", "&gt;");
		this.value += value;
		return this;
	}

	public HtmlElement appendValue(HtmlElement element) {
		this.value += element.toString();
		return this;
	}

	private class Attribute {
		String name;
		String value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<");
		sb.append(this.name);

		if (this.attributes != null) {
			for (Attribute a : attributes) {
				sb.append(" ").append(a.name).append("=\"");
				sb.append(a.value);
				sb.append("\"");
			}
		}

		sb.append(">");
		sb.append(value);
		sb.append("<").append(name).append(">");

		return sb.toString();
	}
}
