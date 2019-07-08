package com.sendtomoon.autosql;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

@Service
public class TableService {
	public String getNumber(String startNo, int addValue) {
		String no = String.valueOf(startNo);
		int startNum = Integer.valueOf(startNo);
		startNum = startNum + addValue;
		no = String.valueOf(startNum);
		while (no.length() < 3) {
			no = "0" + no;
		}
		return no;
	}

	public String grt(String startNo, String editor, String tableName) throws Exception {
		File file = new File(this.getNumber(startNo, 2) + "_ommdata_ddl_grt_" + tableName + "_" + editor + ".sql");
		FileOutputStream out = new FileOutputStream(file, false);
		StringBuffer grtSB = new StringBuffer();
		grtSB.append("grant select, insert, update, delete on OMMDATA." + tableName + " to OMMOPR; \r\n");
		grtSB.append("grant select on OMMDATA." + tableName + " to devsup01;");
		out.write(grtSB.toString().getBytes("utf-8"));
		out.flush();
		out.close();
		return file.getPath();
	}

	public String create(String datas, String startNo, String editor, String tableName, String tableComment)
			throws Exception {
		List<RowDataDTO> list = JSON.parseArray(datas, RowDataDTO.class);

		String no = String.valueOf(startNo);
		while (no.length() < 3) {
			no = "0" + no;
		}
		File file = new File(this.getNumber(startNo, 0) + "_ommdata_ddl_create_" + tableName + "_" + editor + ".sql");
		FileOutputStream out = new FileOutputStream(file, false);
		StringBuffer createSB = new StringBuffer();
		createSB.append("-- Create table \r\n");
		createSB.append("create table " + tableName + " \r\n");
		createSB.append("( \r\n");
		for (int i = 0; i < list.size(); i++) {
			RowDataDTO dto = list.get(i);
			createSB.append(" " + dto.getFiled() + " " + dto.getType() + this.isDef(dto.getDefVal())
					+ this.isNull(dto.isAllowNull()) + (i == list.size() ? "" : ",") + "\r\n");
		}
		createSB.append("); \r\n");
		createSB.append("-- Add comments to the table  \r\n");
		createSB.append("comment on table " + tableName + " is '" + tableComment + "'; \r\n");
		createSB.append("-- Add comments to the columns  \r\n");
		for (RowDataDTO dto : list) {
			createSB.append(
					"comment on column " + tableName + "." + dto.getFiled() + " is '" + dto.getComment() + "'; \r\n");
		}
		out.write(createSB.toString().getBytes("utf-8"));
		out.flush();
		out.close();
		return file.getPath();
	}

	public String syn(String startNo, String editor, String tableName) throws Exception {
		File file = new File(this.getNumber(startNo, 1) + "_ommdata_ddl_syn_" + tableName + "_" + editor + ".sql");
		FileOutputStream out = new FileOutputStream(file, false);
		StringBuffer synSB = new StringBuffer();
		synSB.append("create public synonym " + tableName + " for OMMDATA." + tableName + "; \r\n");
		out.write(synSB.toString().getBytes("utf-8"));
		out.flush();
		out.close();
		return file.getPath();
	}

	private String isNull(boolean isnull) {
		return isnull ? " " : " not null ";
	}

	private String isDef(String def) {
		if (StringUtils.isNotBlank(def)) {
			return " default " + def + " ";
		}
		return "";
	}
}
