package com.sendtomoon.autosql;

import java.io.Serializable;

public class RowDataDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6784039958807913951L;
	private String filed;
	private String type;
	private String comment;
	private String defVal;
	private boolean allowNull;

	public String getFiled() {
		return filed;
	}

	public void setFiled(String filed) {
		this.filed = filed;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDefVal() {
		return defVal;
	}

	public void setDefVal(String defVal) {
		this.defVal = defVal;
	}

	public boolean isAllowNull() {
		return allowNull;
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
	}

}
