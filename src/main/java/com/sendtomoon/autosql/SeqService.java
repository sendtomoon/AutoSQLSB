package com.sendtomoon.autosql;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.stereotype.Service;

@Service
public class SeqService {

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
		grtSB.append("grant select on OMMDATA." + tableName + " to OMMOPR; \r\n");
		grtSB.append("grant select on OMMDATA." + tableName + " to devsup01; \r\n");
		out.write(grtSB.toString().getBytes("utf-8"));
		out.flush();
		out.close();
		return file.getPath();
	}

	public String create(String startNo, String editor, String seqName) throws Exception {

		String no = String.valueOf(startNo);
		while (no.length() < 3) {
			no = "0" + no;
		}
		File file = new File(this.getNumber(startNo, 0) + "_ommdata_ddl_create_" + seqName + "_" + editor + ".sql");
		FileOutputStream out = new FileOutputStream(file, false);
		StringBuffer createSB = new StringBuffer();
		createSB.append("create sequence " + seqName + " \r\n");
		createSB.append("minvalue 1\r\n");
		createSB.append("maxvalue 99999999 \r\n");
		createSB.append("start with 1\r\n");
		createSB.append("increment by 1\r\n");
		createSB.append("cache 20;\r\n");
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
}
