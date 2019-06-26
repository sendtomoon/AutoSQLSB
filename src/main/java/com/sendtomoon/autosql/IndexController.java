package com.sendtomoon.autosql;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

@Controller
public class IndexController {

	@RequestMapping("/save")
	@ResponseBody
	public ResponseEntity<byte[]> save(String datas, String startNo, String editor, String tableName,
			String tableComment, HttpServletResponse response) throws Exception {
		String create = this.create(datas, startNo, editor, tableName, tableComment);
		String syn = this.syn(startNo, editor, tableName);
		String grt = this.grt(startNo, editor, tableName);
		this.zip(create, syn, grt);

		File file = new File("SQL.zip");
		HttpHeaders headers = new HttpHeaders();
		// 解决中文乱码
		String downloadfile = new String("SQL.zip".getBytes("UTF-8"), "iso-8859-1");
		// 以下载方式打开文件
		headers.setContentDispositionFormData("attachment", downloadfile);
		// 二进制流
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);

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

	private String grt(String startNo, String editor, String tableName) throws Exception {
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

	private String create(String datas, String startNo, String editor, String tableName, String tableComment)
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

	private String syn(String startNo, String editor, String tableName) throws Exception {
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

	public boolean zip(String create, String syn, String grt) {
		byte[] buffer = new byte[512];// 缓冲器
		ZipEntry zipEntry = null;
		int readLength = 0;// 每次读出来的长度

		try {
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream("SQL.zip"));
			File createFile = new File(create);
			File synFile = new File(syn);
			File grtFile = new File(grt);
			zipEntry = new ZipEntry(create);
			zipEntry.setSize(createFile.length());
			zipEntry.setTime(createFile.lastModified());
			zipOutputStream.putNextEntry(zipEntry);
			InputStream inputStream = new BufferedInputStream(new FileInputStream(createFile));
			while ((readLength = inputStream.read(buffer, 0, 512)) != -1) {
				zipOutputStream.write(buffer, 0, readLength);
			}
			inputStream.close();
			System.out.println("file compressed: " + createFile.getCanonicalPath());

			zipEntry = new ZipEntry(syn);
			zipEntry.setSize(synFile.length());
			zipEntry.setTime(synFile.lastModified());
			zipOutputStream.putNextEntry(zipEntry);
			inputStream = new BufferedInputStream(new FileInputStream(synFile));
			while ((readLength = inputStream.read(buffer, 0, 512)) != -1) {
				zipOutputStream.write(buffer, 0, readLength);
			}
			inputStream.close();
			System.out.println("file compressed: " + synFile.getCanonicalPath());

			zipEntry = new ZipEntry(grt);
			zipEntry.setSize(grtFile.length());
			zipEntry.setTime(grtFile.lastModified());
			zipOutputStream.putNextEntry(zipEntry);
			inputStream = new BufferedInputStream(new FileInputStream(grtFile));
			while ((readLength = inputStream.read(buffer, 0, 512)) != -1) {
				zipOutputStream.write(buffer, 0, readLength);
			}
			inputStream.close();
			System.out.println("file compressed: " + grtFile.getCanonicalPath());

			zipOutputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println("zip fail!");

			return false;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println("zip fail!");

			return false;
		}

		System.out.println("zip success!");

		return true;
	}
}
