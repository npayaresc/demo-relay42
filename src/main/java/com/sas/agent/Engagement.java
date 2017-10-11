package com.sas.agent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Engagement implements Cloneable{

	public int getSiteNumber() {
		return siteNumber;
	}

	public void setSiteNumber(int siteNumber) {
		this.siteNumber = siteNumber;
	}

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Variables getVariables() {


		return variables;
	}

	public void setVariables(Variables variables) {
		this.variables = variables;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	private int siteNumber;
    private String trackId;
    private long timestamp;
    private String eventId;
	private String type;
	private String content;
	@Getter
	@Setter
	private String interactionType;
	private Variables variables;




}