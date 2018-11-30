package com.noriental.praxissvr.question.bean.html;

import java.io.Serializable;

public class Type implements Serializable{

	private int id;
	private String name;

	public Type() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Type [id=" + id + ", name=" + name + "]";
	}

}
