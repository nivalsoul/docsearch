package docsearch;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

public class FileConvert {
	
	//缓存文件头信息-文件头信息
	public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();
	static {
		// images
		mFileTypes.put("FFD8FF", "jpg");
		mFileTypes.put("89504E47", "png");
		mFileTypes.put("47494638", "gif");
		mFileTypes.put("49492A00", "tif");
		mFileTypes.put("424D", "bmp");
		//
		mFileTypes.put("41433130", "dwg"); // CAD
		mFileTypes.put("38425053", "psd");
		mFileTypes.put("7B5C727466", "rtf"); // 日记本
		mFileTypes.put("3C3F786D6C", "xml");
		mFileTypes.put("68746D6C3E", "html");
		mFileTypes.put("44656C69766572792D646174653A", "eml"); // 邮件
		mFileTypes.put("D0CF11E0", "doc");
		mFileTypes.put("5374616E64617264204A", "mdb");
		mFileTypes.put("252150532D41646F6265", "ps");
		mFileTypes.put("255044462D312E", "pdf");
		mFileTypes.put("504B0304", "docx");
		mFileTypes.put("52617221", "rar");
		mFileTypes.put("57415645", "wav");
		mFileTypes.put("41564920", "avi");
		mFileTypes.put("2E524D46", "rm");
		mFileTypes.put("000001BA", "mpg");
		mFileTypes.put("000001B3", "mpg");
		mFileTypes.put("6D6F6F76", "mov");
		mFileTypes.put("3026B2758E66CF11", "asf");
		mFileTypes.put("4D546864", "mid");
		mFileTypes.put("1F8B08", "gz");
	}

	public static void main(String[] args) throws IOException {
		//convert();
		/*String inputFile="E:/test/韦雨晴的简历2.docx";
		String outputFile="E:/test/韦雨晴的简历000.pdf";
		convert(inputFile, outputFile);*/
		//convertAll("E:/test");
		//System.out.println(new File("c:/abc/test.txt").getName().endsWith(".txt"));
		toUTF8(new File("E:\\文学艺术\\new.html"));
	}
	
	public static void toUTF8(File input) {
		try {
			/*BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input),"UTF-8"));
			File outputFile = new File(input.getParent(), "cp_"+input.getName());
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8"));
			for(String line = reader.readLine();line != null;line=reader.readLine()){
			    writer.println(line);
			}
			writer.close();
			reader.close();*/
			
			/*File outputFile = new File(input.getParent(), "cp_"+input.getName());
			String data = FileUtils.readFileToString(input, "GBK");
			FileUtils.writeStringToFile(outputFile, data, "UTF-8");
			*/

			System.out.println("Mime Type is " + new MimetypesFileTypeMap().getContentType(input));  
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("finish");
	}
	

/**
  * byte数组转换成16进制字符串
  * @param src
  * @return
  */
 public static String bytesToHexString(byte[] src){      
        StringBuilder stringBuilder = new StringBuilder();      
        if (src == null || src.length <= 0) {      
            return null;      
        }      
        for (int i = 0; i < src.length; i++) {      
            int v = src[i] & 0xFF;      
            String hv = Integer.toHexString(v);      
            if (hv.length() < 2) {      
                stringBuilder.append(0);      
            }      
            stringBuilder.append(hv);      
        }      
        return stringBuilder.toString();      
    } 
 
 /**
  * 根据文件流读取图片文件真实类型
  * @param is
  * @return
  */
	public static String getTypeByStream(FileInputStream is) {
		byte[] b = new byte[4];
		try {
			is.read(b, 0, b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String type = bytesToHexString(b).toUpperCase();
		return type;
	}
	
 
	
	/**
     * 判断文件的编码格式
     * 
     * @param fileName
     *            :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String codeString(String fileName) throws Exception {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
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


	private static void convert(String input, String output) {
		OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
        OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();
        
        officeManager.start();
        try {
        	File inputFile = new File(input);
			String inputExtension = FilenameUtils.getExtension(inputFile.getName());
			System.out.println(inputExtension);
            DocumentFormat inputFormat = formatRegistry.getFormatByExtension(inputExtension);
            File outputFile = new File(output);
            outputFile.createNewFile();
            DocumentFormat outputFormat = formatRegistry.getFormatByExtension(
            		FilenameUtils.getExtension(outputFile.getName()));
			System.out.printf("-- converting %s to %s... ", inputFormat.getExtension(), outputFormat .getExtension());
            long start = System.currentTimeMillis();
			converter.convert(inputFile, outputFile, outputFormat);
			long end = System.currentTimeMillis();
            System.out.printf("done.\n");
            float size = inputFile.length()/(1024*1024f);
            System.out.println("fileSize:"+size+"M,use:"+(end-start)/1000+"s");
            if(outputFile.isFile() && outputFile.length() > 0){
            	
            }
        }catch(Exception e){
        	e.printStackTrace();
        }finally {
            officeManager.stop();
        }
	}
	
	private static void convertAll(String inputPath) {
		OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
        OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();
        
        officeManager.start();
        try {
        	File dir = new File(inputPath);
            File[] files = dir.listFiles();
            for (File inputFile : files) {
            	if(inputFile.isDirectory())
            		continue;
				String inputExtension = FilenameUtils.getExtension(inputFile.getName());
	            DocumentFormat inputFormat = formatRegistry.getFormatByExtension(inputExtension);
	            File outputFile = new File(inputPath+"/pdf/"+inputFile.getName()+".pdf");
	            outputFile.createNewFile();
	            DocumentFormat outputFormat = formatRegistry.getFormatByExtension(
	            		FilenameUtils.getExtension(outputFile.getName()));
				System.out.printf("-- converting %s to %s... ", inputFormat.getExtension(), outputFormat .getExtension());
	            long start = System.currentTimeMillis();
				converter.convert(inputFile, outputFile, outputFormat);
				long end = System.currentTimeMillis();
	            System.out.printf("done.\n");
	            long size = inputFile.length()/(1024*1024);
	            System.out.println("fileName:"+inputFile.getName()+",fileSize:"+size+"M,use:"+(end-start)/1000+"s");
	            if(outputFile.isFile() && outputFile.length() > 0){
	            	
	            }
            }
        }catch(Exception e){
        	e.printStackTrace();
        }finally {
            officeManager.stop();
        }
	}

}
