package com.sendtomoon.autosql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

@Controller
public class IndexController {

	@RequestMapping("/save")
	@ResponseBody
	public String save(String datas, String startNo, String editor, String tableName, String tableComment)
			throws UnsupportedEncodingException, IOException {
		List<RowDataDTO> list = JSON.parseArray(datas, RowDataDTO.class);
		int startNum = Integer.valueOf(startNo);
		String no = String.valueOf(startNo);
		while (no.length() < 3) {
			no = "0" + no;
		}
		File file = new File(no + "_ommdata_ddl_create_" + tableName + "_" + editor + ".sql");
		FileOutputStream out = new FileOutputStream(file, false);
		StringBuffer sb = new StringBuffer();
		sb.append("create table " + tableName + " \r\n");
		sb.append("( \r\n");
		for (RowDataDTO dto : list) {
			sb.append("  " + dto.getFiled() + "     " + dto.getType() + " default " + dto.getDefVal() + ""
					+ this.isNull(dto.isAllowNull()) + ", \r\n");
		}
		sb.append("); \r\n");
		sb.append("comment on table " + tableName + " is '" + tableComment + "'; \r\n");
		for (RowDataDTO dto : list) {
			sb.append("comment on column " + tableName + "." + dto.getFiled() + " is '" + dto.getComment() + "'; \r\n");
		}
		out.write(sb.toString().getBytes("utf-8"));
		out.flush();
		out.close();

		startNum = startNum + 1;
		no = String.valueOf(startNum);
		while (no.length() < 3) {
			no = "0" + no;
		}
		file = new File(no + "_ommdata_ddl_syn_" + tableName + "_" + editor + ".sql");
		out = new FileOutputStream(file, false);
		sb = new StringBuffer();
		sb.append("create public synonym " + tableName + " for OMMDATA." + tableName + "; \r\n");
		out.write(sb.toString().getBytes("utf-8"));
		out.flush();
		out.close();

		startNum = startNum + 1;
		no = String.valueOf(startNum);
		while (no.length() < 3) {
			no = "0" + no;
		}
		file = new File(no + "_ommdata_ddl_grt_" + tableName + "_" + editor + ".sql");
		out = new FileOutputStream(file, false);
		sb = new StringBuffer();
		sb.append("grant select, insert, update, delete on OMMDATA." + tableName + " to OMMOPR; \r\n");
		sb.append("grant select on OMMDATA." + tableName + " to devsup01;");
		out.write(sb.toString().getBytes("utf-8"));
		out.flush();
		out.close();

		return "successful";
	}

	private String isNull(boolean isnull) {
		return isnull ? " " : " not null ";
	}

}
