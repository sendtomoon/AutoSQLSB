package com.sendtomoon.autosql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

@Controller
public class IndexController {

	@RequestMapping("/save")
	@ResponseBody
	public String save(String datas, String startNo, String editor, String tableName, String tableComment)
			throws Exception {
		this.create(datas, startNo, editor, tableName, tableComment);
		this.syn(startNo, editor, tableName);
		this.grt(startNo, editor, tableName);
		return "successful";
	}

	private String getNumber(String startNo, int addValue) {
		String no = String.valueOf(startNo);
		int startNum = Integer.valueOf(startNo);
		startNum = startNum + addValue;
		no = String.valueOf(startNum);
		while (no.length() < 3) {
			no = "0" + no;
		}
		return no;
	}

	private void grt(String startNo, String editor, String tableName) throws Exception {
		File file = new File(this.getNumber(startNo, 2) + "_ommdata_ddl_grt_" + tableName + "_" + editor + ".sql");
		FileOutputStream out = new FileOutputStream(file, false);
		StringBuffer grtSB = new StringBuffer();
		grtSB.append("grant select, insert, update, delete on OMMDATA." + tableName + " to OMMOPR; \r\n");
		grtSB.append("grant select on OMMDATA." + tableName + " to devsup01;");
		out.write(grtSB.toString().getBytes("utf-8"));
		out.flush();
		out.close();
	}

	private void create(String datas, String startNo, String editor, String tableName, String tableComment)
			throws Exception {
		List<RowDataDTO> list = JSON.parseArray(datas, RowDataDTO.class);

		String no = String.valueOf(startNo);
		while (no.length() < 3) {
			no = "0" + no;
		}
		File file = new File(this.getNumber(startNo, 0) + "_ommdata_ddl_create_" + tableName + "_" + editor + ".sql");
		FileOutputStream out = new FileOutputStream(file, false);
		StringBuffer createSB = new StringBuffer();
		createSB.append("-- Create table");
		createSB.append("create table " + tableName + " \r\n");
		createSB.append("( \r\n");
		for (int i = 0; i < list.size(); i++) {
			RowDataDTO dto = list.get(i);
			createSB.append(" " + dto.getFiled() + " " + dto.getType() + this.isDef(dto.getDefVal())
					+ this.isNull(dto.isAllowNull()) + (i == list.size() ? "" : ",") + "\r\n");
		}
		createSB.append("); \r\n");
		createSB.append("-- Add comments to the table ");
		createSB.append("comment on table " + tableName + " is '" + tableComment + "'; \r\n");
		createSB.append("-- Add comments to the columns ");
		for (RowDataDTO dto : list) {
			createSB.append(
					"comment on column " + tableName + "." + dto.getFiled() + " is '" + dto.getComment() + "'; \r\n");
		}
		out.write(createSB.toString().getBytes("utf-8"));
		out.flush();
		out.close();
	}

	private void syn(String startNo, String editor, String tableName) throws Exception {
		File file = new File(this.getNumber(startNo, 1) + "_ommdata_ddl_syn_" + tableName + "_" + editor + ".sql");
		FileOutputStream out = new FileOutputStream(file, false);
		StringBuffer synSB = new StringBuffer();
		synSB.append("create public synonym " + tableName + " for OMMDATA." + tableName + "; \r\n");
		out.write(synSB.toString().getBytes("utf-8"));
		out.flush();
		out.close();
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
