package com.sendtomoon.autosql;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

	@Autowired
	private TableService service;

	@Autowired
	private SeqService seqService;

	@RequestMapping("/saveSeq")
	@ResponseBody
	public void save(String startNo, String editor, String tableName) throws Exception {
		String create = seqService.create(startNo, editor, tableName);
		String syn = seqService.syn(startNo, editor, tableName);
		String grt = seqService.grt(startNo, editor, tableName);

	}

	@RequestMapping("/save")
	@ResponseBody
	public ResponseEntity<byte[]> save(String datas, String startNo, String editor, String tableName,
			String tableComment, HttpServletResponse response) throws Exception {
		String create = service.create(datas, startNo, editor, tableName, tableComment);
		String syn = service.syn(startNo, editor, tableName);
		String grt = service.grt(startNo, editor, tableName);
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
