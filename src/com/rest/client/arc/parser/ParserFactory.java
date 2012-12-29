package com.rest.client.arc.parser;

public class ParserFactory {
	public static Parser getParser(String contentType) {
		if (contentType.contains("json")) {
			return new JsonParser();
		} else if (contentType.contains("xml")) {
			return new XmlParser();
		}
		
		return null;
	}
}
