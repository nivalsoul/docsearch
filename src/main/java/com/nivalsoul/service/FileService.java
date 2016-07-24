package com.nivalsoul.service;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nivalsoul.config.FileSaveConfig;
import com.nivalsoul.dao.DocumentDao;
import com.nivalsoul.domain.Document;
import com.nivalsoul.domain.Page;
import com.nivalsoul.utils.ImageUtil;
import com.nivalsoul.utils.MD5;

@Service
public class FileService {
	
	@Autowired
	private FileSaveConfig fileSaveConfig;
	
	@Autowired 
	private DocumentDao documentDao;

	public Map<String, Object> saveFile(MultipartFile mf) {
		Map<String, Object> info = new HashMap<String, Object>();
		//通过文件的md5判断文件是否存在
		//...
		String fileName=mf.getOriginalFilename();
		String documentId = UUID.randomUUID().toString();
    	if(fileSaveConfig.getSaveTo().equals("local")){
    		String path = fileSaveConfig.getConfigInfo().get("local-path");
			try {
				//对每个文件创建一个文件夹
				File folder = new File(path, documentId);
				folder.mkdirs();
				//保存文件到文件夹
				File outputFile = new File(folder, fileName);
				mf.transferTo(outputFile);
				//gbk编码的txt文件解析会乱码，改为utf8编码
				if(fileName.endsWith(".txt") && codeString(outputFile).equals("GBK")){
					String data = FileUtils.readFileToString(outputFile, "GBK");
					FileUtils.writeStringToFile(outputFile, data, "UTF-8");
				}
				
				info.put("code", 200);
		    	info.put("message", "file ["+fileName+"] upload successful");
		    	info.put("document_id", documentId);
			} catch (Exception e) {
				e.printStackTrace();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
				info.put("code", 500);
				info.put("message", "file ["+fileName+"] upload failed");
			}
    	}
    	return info;
	}
	
	/**
	 * 获取文件编码
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String codeString(File file) throws Exception {
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
		int p = (bin.read() << 8) + bin.read();
		bin.close();
		String code = null;
		switch (p) {
			case 0xefbb:
				code = "UTF-8";
				break;
			case 0xfffe:
				code = "Unicode";
				break;
			case 0xfeff:
				code = "UTF-16BE";
				break;
			default:
				code = "GBK";
		}

		return code;
    }

	/**
	 * 发送文档id到消息体统，以便进行转换处理
	 * @param documentId
	 */
	public void sendMessage(String documentId) {
		//异步进行文件转换
		new Thread(new Runnable() {
			public void run() {
				startJob(documentId);
			}
		}).start();
		
	}


	protected void startJob(String documentId) {
		if(fileSaveConfig.getSaveTo().equals("local")){
    		String path = fileSaveConfig.getConfigInfo().get("local-path");
			try {
				File folder = new File(path, documentId);
				//获取原始文件
				File[] list = folder.listFiles();
				File file = null;
				for(int i = 0; i< list.length; i++){
					file = list[i];
					//约定文件夹下的文件即原文件
					if(file.isFile() && !file.getName().endsWith(".pdf"))
						break;
				}
				if(file == null) return;
				//设置libreOffice路径
				if(fileSaveConfig.getConfigInfo().get("setLibreOfficeHOme").equals("true"))
				    System.setProperty("office.home", fileSaveConfig.getConfigInfo().get("libreOfficeHome"));
				//将文件转成pdf
				boolean ok = convertFileToPDF(file);
				if(!ok){ //再试一次
					System.out.println("初次转换失败，再试一次...");
					Thread.sleep(10000);
					ok = convertFileToPDF(file);
				}
				//转换成功则进行解析操作
				if(ok){
					parsePDF(documentId, file);
				}else{
					Document doc = documentDao.getDocumentById(documentId);
					doc.setFile_name(file.getName());
					try {
						doc.setFile_hash(MD5.getHashString(file));
					} catch (IOException e) {
						e.printStackTrace();
					}
					doc.setStatus("fail");
					documentDao.update(doc);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Document doc = documentDao.getDocumentById(documentId);
				doc.setStatus("fail");
				documentDao.update(doc);
			}
    	}
	}

	/**
	 * 防止Libreoffice被多个线程同时调用，采用静态同步方法
	 * @param file
	 * @return 
	 */
	public synchronized static boolean convertFileToPDF(File inputFile) {
		String inputExtension = FilenameUtils.getExtension(inputFile.getName());
        if("pdf".equals(inputExtension)){//如果是pdf则无需转换
        	return true;
        }
        boolean result = false;
		OfficeManager officeManager = null;
        try {
        	officeManager = new DefaultOfficeManagerConfiguration()
        			.setTaskExecutionTimeout(1800*1000L) //超时时间30分钟
        			.buildOfficeManager();
        	OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        	DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();
        	officeManager.start();
			String fileName = FilenameUtils.getBaseName(inputFile.getName());
			File outputFile = new File(inputFile.getParent(), fileName+".pdf");
			if(outputFile.exists()){
				outputFile.delete();
			}
            outputFile.createNewFile();
            DocumentFormat outputFormat = formatRegistry.getFormatByExtension(
            		FilenameUtils.getExtension(outputFile.getName()));
            long start = System.currentTimeMillis();
			converter.convert(inputFile, outputFile, outputFormat);
			long end = System.currentTimeMillis();
            float size = inputFile.length()/(1024f);
            size = (int)(size*100)/100f;
            System.out.println("convert file: "+inputFile.getName()+"["+size+"K],use:"+(end-start)/1000+"s");
            if(outputFile.isFile() && outputFile.length() > 0){
            	result = true;
            }
        }catch(Exception e){
        	e.printStackTrace();
        }finally {
        	if(officeManager != null)
                officeManager.stop();
        }
        return result;
	}
	
	public void parsePDF(String documentId, File file) {
		System.out.println("===begin to parse file["+file.getName()+"]...");
		Document doc = documentDao.getDocumentById(documentId);
		doc.setFile_name(file.getName());
		String format = FilenameUtils.getExtension(file.getName());
		doc.setFormat(format.equals("") ? "unknown" : format);
		try {
			doc.setFile_hash(MD5.getHashString(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//解析文档内容
		String name = FilenameUtils.getBaseName(file.getName());
		File pdf = new File(file.getParent(), name+".pdf");
		try {
			PDDocument document = PDDocument.load(pdf);
			PDFTextStripper pdfstripper = new PDFTextStripper();
			int numPages = document.getNumberOfPages();
			doc.setPage_count(numPages);
			
			//按页生成图片
			System.out.println("===按页生成图片...");
			String imagesFolder = file.getParent()+"/images";
			if (!new File(imagesFolder).exists())
				new File(imagesFolder).mkdirs();
			PDFRenderer renderer = new PDFRenderer(document);
			for (int i = 0; i < numPages; i++) {
				String fileName = imagesFolder + "/p" + (i + 1) + ".png";
				new FileOutputStream(new File(fileName + ".rendererror")).close();
				BufferedImage image = renderer.renderImageWithDPI(i, 120); //每英寸120个像素点
				new File(fileName + ".rendererror").delete();
				new FileOutputStream(new File(fileName + ".writeerror")).close();
				ImageIO.write(image, "PNG", new File(fileName));
				new File(fileName + ".writeerror").delete();
				//生成缩略图
				ImageUtil.thumbnailImage(fileName, 96, 136, null);
			}
			
			//按页读取文本内容
			System.out.println("===按页解析文本内容...");
			List<Page> pageList = new ArrayList<Page>();
			for (int i = 1; i <= numPages; i++) {
				pdfstripper.setStartPage(i);
				pdfstripper.setEndPage(i);
				String text = pdfstripper.getText(document);
				FileUtils.writeStringToFile(new File(file.getParent()+"/text", "p"+i+".txt"), text);
				Page page = new Page();
				page.set_id(UUID.randomUUID().toString());
				page.setDocument_id(documentId);
				page.setPage(i);
				page.setText(text.replaceAll("\r\n", ""));
				pageList.add(page);
			}
			doc.setPages(pageList);
			//更新文档信息
			doc.setStatus("success");
			documentDao.update(doc);

			document.close();
		} catch (IOException e) {
			e.printStackTrace();
			doc.setStatus("fail");
			documentDao.update(doc);
		}
		System.out.println("===parse file["+file.getName()+"] finish!!!");

	}

	public boolean deleteFile(String documentId) {
		if(fileSaveConfig.getSaveTo().equals("local")){
    		String path = fileSaveConfig.getConfigInfo().get("local-path");
    		File folder = new File(path, documentId);
    		return deleteDir(folder);
		}
		return true;
	}
	
	/**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {//有一次失败则返回
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

	public File getFile(String documentId, String filename) {
		if(fileSaveConfig.getSaveTo().equals("local")){
    		String path = fileSaveConfig.getConfigInfo().get("local-path");
    		File folder = new File(path, documentId);
    		File file = new File(folder, filename);
    		if(file.exists())
    			return file;
		}
		return null;
	}
	
}
