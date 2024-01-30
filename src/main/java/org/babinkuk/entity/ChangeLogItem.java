package org.babinkuk.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "change_log_item")
public class ChangeLogItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chli_id")
	private int chliId;
	
	@ManyToOne(cascade = {
			CascadeType.PERSIST,
			CascadeType.DETACH,
			CascadeType.MERGE,
			CascadeType.REFRESH})
	// cascade.REMOVE not used, if item is deleted, do not delete associated ChangeLog!!!
	@JoinColumn(name = "chlo_id") 
	private ChangeLog changeLog;
	
	@Column(name = "chli_field_name")
	private String chliFieldName;
	
	@Column(name = "chli_old_value_id")
	private int chliOldValueId; 
	
	@Column(name = "chli_old_value")
	private String chliOldValue; 
	
	@Column(name = "chli_new_value_id")
	private int chliNewValueId; 
	
	@Column(name = "chli_new_value")
	private String chliNewValue; 
	
	public ChangeLogItem() {
		// TODO Auto-generated constructor stub
	}

	public ChangeLogItem(int chliId, ChangeLog changeLog, String chliFieldName, int chliOldValueId,
			String chliOldValue, int chliNewValueId, String chliNewValue) {
		this.chliId = chliId;
		this.changeLog = changeLog;
		this.chliFieldName = chliFieldName;
		this.chliOldValueId = chliOldValueId;
		this.chliOldValue = chliOldValue;
		this.chliNewValue = chliNewValue;
		this.chliNewValueId = chliNewValueId;
	}

	public int getChliId() {
		return chliId;
	}

	public void setChliId(int chliId) {
		this.chliId = chliId;
	}

	public ChangeLog getChangeLog() {
		return changeLog;
	}

	public void setChangeLog(ChangeLog changeLog) {
		this.changeLog = changeLog;
	}

	public String getChliFieldName() {
		return chliFieldName;
	}

	public void setChliFieldName(String chliFieldName) {
		this.chliFieldName = chliFieldName;
	}

	public int getChliOldValueId() {
		return chliOldValueId;
	}

	public void setChliOldValueId(int chliOldValueId) {
		this.chliOldValueId = chliOldValueId;
	}

	public String getChliOldValue() {
		return chliOldValue;
	}

	public void setChliOldValue(String chliOldValue) {
		this.chliOldValue = chliOldValue;
	}

	public String getChliNewValue() {
		return chliNewValue;
	}

	public void setChliNewValue(String chliNewValue) {
		this.chliNewValue = chliNewValue;
	}

	public int getChliNewValueId() {
		return chliNewValueId;
	}

	public void setChliNewValueId(int chliNewValueId) {
		this.chliNewValueId = chliNewValueId;
	}

	@Override
	public String toString() {
		return "ChangeLogItem [chliId=" + chliId
				//+ ", changeLog=" + changeLog
				+ ", chliFieldName=" + chliFieldName
				+ ", chliOldValueId=" + chliOldValueId + ", chliOldValue=" + chliOldValue
				+ ", chliNewValue="	+ chliNewValue + ", chliNewValueId=" + chliNewValueId + "]";
	}
	
}