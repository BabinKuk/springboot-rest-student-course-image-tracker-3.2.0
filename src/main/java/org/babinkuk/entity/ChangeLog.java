package org.babinkuk.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "change_log")
public class ChangeLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chlo_id")
	private int chloId;
	
	@Column(name = "chlo_timestamp")
	private Date chloTimestamp;
	
	@Column(name = "chlo_user_id")
	private String chloUserId; 
	
	@Column(name = "chlo_table_id")
	private int chloTableId;
	
	// bi-directional
	@OneToMany(mappedBy = "changeLog", // refers to changeLog property in ChangeLogItem class
			fetch = FetchType.LAZY,	
			cascade = {
					CascadeType.PERSIST,
					CascadeType.DETACH,
					CascadeType.MERGE,
					CascadeType.REFRESH})
	private Set<ChangeLogItem> changeLogItems = new HashSet<ChangeLogItem>(0);
	
	// bi-directional
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chlo_lm_id")
	private LogModule logModule;
	
	public ChangeLog() {
		// TODO Auto-generated constructor stub
	}

	public ChangeLog(int chloId, Date chloTimestamp, String chloUserId, int chloTableId,
			Set<ChangeLogItem> changeLogItems, LogModule logModule) {
		this.chloId = chloId;
		this.chloTimestamp = chloTimestamp;
		this.chloUserId = chloUserId;
		this.chloTableId = chloTableId;
		this.changeLogItems = changeLogItems;
		this.logModule = logModule;
	}

	public int getChloId() {
		return chloId;
	}

	public void setChloId(int chloId) {
		this.chloId = chloId;
	}

	public Date getChloTimestamp() {
		return chloTimestamp;
	}

	public void setChloTimestamp(Date chloTimestamp) {
		this.chloTimestamp = chloTimestamp;
	}

	public String getChloUserId() {
		return chloUserId;
	}

	public void setChloUserId(String chloUserId) {
		this.chloUserId = chloUserId;
	}

	public int getChloTableId() {
		return chloTableId;
	}

	public void setChloTableId(int chloTableId) {
		this.chloTableId = chloTableId;
	}

	public Set<ChangeLogItem> getChangeLogItems() {
		return changeLogItems;
	}
	
	public void setChangeLogItems(Set<ChangeLogItem> changeLogItems) {
		this.changeLogItems = changeLogItems;
	}
	
	public LogModule getLogModule() {
		return logModule;
	}

	public void setLogModule(LogModule logModule) {
		this.logModule = logModule;
	}
	
	@Override
	public String toString() {
		return "ChangeLog [chloId=" + chloId + ", chloTimestamp=" + chloTimestamp
				+ ", chloUserId=" + chloUserId + ", logModule=" + logModule
				+ ", chloTableId=" + chloTableId + ", changeLogItems=" + changeLogItems + "]";
	}
	
}