package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * webrawdata 实体类
 * @author 贰拾壹
 *
 */
@Entity
@Table(name="tb_webrawdata")
public class Webrawdata implements Serializable{

	@Id
	//编号
	private String id;


	
	//webinfo编号
	private String webinfoid;
	//响应头
	private String header;
	//响应
	private String response;
	public Webrawdata() {
	}

	public Webrawdata(String id, String webinfoid, String header, String response) {
		this.id = id;
		this.webinfoid = webinfoid;
		this.header = header;
		this.response = response;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getWebinfoid() {
		return webinfoid;
	}
	public void setWebinfoid(String webinfoid) {
		this.webinfoid = webinfoid;
	}

	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}

	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
}
