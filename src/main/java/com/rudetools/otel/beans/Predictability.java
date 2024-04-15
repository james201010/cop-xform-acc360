/**
 * 
 */
package com.rudetools.otel.beans;

/**
 * @author james101
 *
 */
public class Predictability {

	private String incNumber;
	private String openedAtDate;
	private String closedAtDate;
	private String shortDescr;
	private String callerId;
	private String priority;
	private String urgency;
	private String impact;
	private String state;
	private String category;
	private String assignmentGroup;
	private String assignedTo;
	private String sysUpdatedOn;
	private String sysUpdatedBy;
	private String slaDue;
	private String slaMade;
	private String sysCreatedOn;
	private String dueDate;
	private long timeWorked; // seems to be all 0s
	private long calendarStc;  // is this in days or hours ? Why was it defined as double in entity ? "calendar_stc": "102,197"
	private String resolvedAtDate;
	
	private long timeToResolveInHours; 
	private long timeToReactInHours;
	
	// fiscal or calendar YEAR/QTR is configurable, and in future, trigger zodiac function with an event to update summary?
	private String predQuarter;  // The QTR the incident was created in, could be fiscal or calendar	
	private String predYear;
	private String predMonth;
	
	
	/**
	 * 
	 */
	public Predictability() {
		
	}


	public String getIncNumber() {
		return incNumber;
	}


	public void setIncNumber(String incNumber) {
		this.incNumber = incNumber;
	}


	public String getOpenedAtDate() {
		return openedAtDate;
	}


	public void setOpenedAtDate(String openedAtDate) {
		this.openedAtDate = openedAtDate;
	}


	public String getClosedAtDate() {
		return closedAtDate;
	}


	public void setClosedAtDate(String closedAtDate) {
		this.closedAtDate = closedAtDate;
	}


	public String getShortDescr() {
		return shortDescr;
	}


	public void setShortDescr(String shortDescr) {
		this.shortDescr = shortDescr;
	}


	public String getCallerId() {
		return callerId;
	}


	public void setCallerId(String callerId) {
		this.callerId = callerId;
	}


	public String getPriority() {
		return priority;
	}


	public void setPriority(String priority) {
		this.priority = priority;
	}


	public String getUrgency() {
		return urgency;
	}


	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}


	public String getImpact() {
		return impact;
	}


	public void setImpact(String impact) {
		this.impact = impact;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public String getAssignmentGroup() {
		return assignmentGroup;
	}


	public void setAssignmentGroup(String assignmentGroup) {
		this.assignmentGroup = assignmentGroup;
	}


	public String getAssignedTo() {
		return assignedTo;
	}


	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}


	public String getSysUpdatedOn() {
		return sysUpdatedOn;
	}


	public void setSysUpdatedOn(String sysUpdatedOn) {
		this.sysUpdatedOn = sysUpdatedOn;
	}


	public String getSysUpdatedBy() {
		return sysUpdatedBy;
	}


	public void setSysUpdatedBy(String sysUpdatedBy) {
		this.sysUpdatedBy = sysUpdatedBy;
	}


	public String getSlaDue() {
		return slaDue;
	}


	public void setSlaDue(String slaDue) {
		this.slaDue = slaDue;
	}


	public String getSlaMade() {
		return slaMade;
	}


	public void setSlaMade(String madeSla) {
		this.slaMade = madeSla;
	}


	public String getSysCreatedOn() {
		return sysCreatedOn;
	}


	public void setSysCreatedOn(String sysCreatedOn) {
		this.sysCreatedOn = sysCreatedOn;
	}


	public String getDueDate() {
		return dueDate;
	}


	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}


	public long getTimeWorked() {
		return timeWorked;
	}


	public void setTimeWorked(long timeWorked) {
		this.timeWorked = timeWorked;
	}


	public long getCalendarStc() {
		return calendarStc;
	}


	public void setCalendarStc(long calendarStc) {
		this.calendarStc = calendarStc;
	}


	public String getResolvedAtDate() {
		return resolvedAtDate;
	}


	public void setResolvedAtDate(String resolvedAtDate) {
		this.resolvedAtDate = resolvedAtDate;
	}


	public long getTimeToResolveInHours() {
		return timeToResolveInHours;
	}


	public void setTimeToResolveInHours(long timeToResolveInHours) {
		this.timeToResolveInHours = timeToResolveInHours;
	}


	public long getTimeToReactInHours() {
		return timeToReactInHours;
	}


	public void setTimeToReactInHours(long timeToReactInHours) {
		this.timeToReactInHours = timeToReactInHours;
	}


	public String getPredYear() {
		return predYear;
	}


	public void setPredYear(String predYear) {
		this.predYear = predYear;
	}


	public String getPredMonth() {
		return predMonth;
	}


	public void setPredMonth(String predMonth) {
		this.predMonth = predMonth;
	}


	public String getPredQuarter() {
		return predQuarter;
	}


	public void setPredQuarter(String predQuarter) {
		this.predQuarter = predQuarter;
	}


	
}
