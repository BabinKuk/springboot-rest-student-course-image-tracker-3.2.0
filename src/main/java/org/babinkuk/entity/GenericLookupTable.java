//package org.babinkuk.entity;
//
//import java.io.Serializable;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.OneToOne;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "change_log")
//public class GenericLookupTable implements Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	@Id
//	//@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "glt_entity_name", length = 64)
//	private String gltEntityName;
//	
//	@Column(name = "glt_custom_name", length = 64)
//	private String gltCustomName;
//	
//	// mapping with rest_module table 
//	// foreign key (rest_module.rm_id column)
//	@OneToOne(/*cascade = CascadeType.ALL*/)
//	@JoinColumn(name = "glt_mod_id")
//	private RestModule gltModule;
//	
//	public GenericLookupTable() {
//		// TODO Auto-generated constructor stub
//	}
//
//	public GenericLookupTable(String gltEntityName, String gltCustomName, RestModule gltModule) {
//		this.gltEntityName = gltEntityName;
//		this.gltCustomName = gltCustomName;
//		this.gltModule = gltModule;
//	}
//
//	public String getGltEntityName() {
//		return gltEntityName;
//	}
//
//	public void setGltEntityName(String gltEntityName) {
//		this.gltEntityName = gltEntityName;
//	}
//
//	public String getGltCustomName() {
//		return gltCustomName;
//	}
//
//	public void setGltCustomName(String gltCustomName) {
//		this.gltCustomName = gltCustomName;
//	}
//
//	public RestModule getGltModule() {
//		return gltModule;
//	}
//
//	public void setGltModule(RestModule gltModule) {
//		this.gltModule = gltModule;
//	}
//
//	@Override
//	public String toString() {
//		return "GenericLookupTable [gltEntityName=" + gltEntityName
//				+ ", gltCustomName=" + gltCustomName
//				+ ", gltModule=" + gltModule + "]";
//	}
//}