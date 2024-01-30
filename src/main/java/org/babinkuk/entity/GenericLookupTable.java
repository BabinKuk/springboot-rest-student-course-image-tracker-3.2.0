//package org.babinkuk.entity;
//
//import java.io.Serializable;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.OneToOne;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "Generic_Lookup_Table")
//public class GenericLookupTable implements Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	@Id
//	//@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "glt_entity_name", length = 256)
//	private String gltEntityName;
//	
//	@Column(name = "glt_custom_name", length = 256)
//	private String gltCustomName;
//	
//	// mapping with rest_module table 
//	// foreign key (rest_module.rm_id column)
//	@OneToOne(fetch = FetchType.LAZY
//			/*cascade = CascadeType.ALL*/)
//	@JoinColumn(name = "glt_rm_id")
//	private RestModule restModule;
//	
//	public GenericLookupTable() {
//		// TODO Auto-generated constructor stub
//	}
//
//	public GenericLookupTable(String gltEntityName, String gltCustomName, RestModule restModule) {
//		this.gltEntityName = gltEntityName;
//		this.gltCustomName = gltCustomName;
//		this.restModule = restModule;
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
//	public RestModule getRestModule() {
//		return restModule;
//	}
//
//	public void setRestModule(RestModule restModule) {
//		this.restModule = restModule;
//	}
//
//	@Override
//	public String toString() {
//		return "GenericLookupTable [gltEntityName=" + gltEntityName
//				+ ", gltCustomName=" + gltCustomName
//				//+ ", restModule=" + restModule
//				+ "]";
//	}
//}