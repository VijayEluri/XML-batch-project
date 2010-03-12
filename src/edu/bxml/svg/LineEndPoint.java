package edu.bxml.svg;

public enum LineEndPoint {
	LEFT("1"), RIGHT("2");
	
	String location = "1";
	
	LineEndPoint(String x) {
		this.location = x;
	}
	
	public String getLocation() {
		return location;
	}
}
