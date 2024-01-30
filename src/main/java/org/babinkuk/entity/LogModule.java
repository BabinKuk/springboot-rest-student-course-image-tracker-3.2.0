package org.babinkuk.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "log_module")
public class LogModule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lm_id")
	private int lmId;
	
	@Column(name = "lm_description", length = 256)
	private String lmDescription;
	
	@Column(name = "lm_entity_name", length = 256)
	private String lmEntityName;
	
	// bi-directional
	@OneToMany(mappedBy = "logModule", // refers to logModule property in ChangeLog class
				fetch = FetchType.LAZY,	
				cascade = {CascadeType.ALL}) // if LogModule is deleted, delete all associated changeLogs rows
	private Set<ChangeLog> changeLogs = new HashSet<ChangeLog>(0);
	
	public LogModule() {
		// TODO Auto-generated constructor stub
	}

	public LogModule(int lmId, String lmDescription, String lmEntityName, Set<ChangeLog> changeLogs) {
		this.lmId = lmId;
		this.lmDescription = lmDescription;
		this.lmEntityName = lmEntityName;
		this.changeLogs = changeLogs;
	}

	public int getLmId() {
		return lmId;
	}

	public void setLmId(int lmId) {
		this.lmId = lmId;
	}

	public String getLmDescription() {
		return lmDescription;
	}

	public void setLmDescription(String lmDescription) {
		this.lmDescription = lmDescription;
	}
	
	public String getLmEntityName() {
		return lmEntityName;
	}

	public void setLmEntityName(String lmEntityName) {
		this.lmEntityName = lmEntityName;
	}

	public Set<ChangeLog> getChangeLogs() {
		return changeLogs;
	}

	public void setChangeLogs(Set<ChangeLog> changeLogs) {
		this.changeLogs = changeLogs;
	}
	
	@Override
	public String toString() {
		return "LogModule [lmId=" + lmId + ", lmDescription=" + lmDescription + ", lmEntityName=" + lmEntityName + "]";
	}
}