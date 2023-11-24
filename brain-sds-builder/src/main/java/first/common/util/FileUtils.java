package first.common.util;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import first.common.system.SystemCode;
import first.common.system.SystemErrMsg;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUtils {

  static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

  @Value("${env.var.fast}")
  private String envPath;

  @Value("${temp.file.path}")
  private String tempPath;

  private FileOutputStream fos;

  public HashMap<String, Object> uploadFile(MultipartFile file, String path) {
    HashMap<String, Object> resultMap = new HashMap<>();
    String orginFileName = file.getOriginalFilename();
    String fileFullPath = getSystemConfPath(path);
    Date date = new Date();
    Format formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    String fileName = formatter.format(date);

    try {
      if (file == null || "".equals(path)) {
        throw new Exception("Internal Server Error");
      } else {
        mkdirs(fileFullPath);
        file.transferTo(
            new File(fileFullPath + "/" + fileName + "." + orginFileName.split("\\.")[1]));
        resultMap.put("isSuccess", true);
        resultMap.put("name", orginFileName);
        resultMap.put("msg", "Complete");
        resultMap.put("id", fileName);
        logger.info("File Generate Complete [{}]", fileFullPath);
      }
    } catch (Exception e) {
      logger.info("File Generate Fail [{}]", fileFullPath);
      logger.error("uploadFile e : " , e);
      resultMap.put("isSuccess", false);
      resultMap.put("name", orginFileName);
      resultMap.put("msg", e.getMessage());
      resultMap.put("id", "");
    }
    return resultMap;
  }

  public void deleteFile(List<String> ids, String path, String extension) {
    for (String id : ids) {
      String fileFullPath = getSystemConfPath(path) + "/" + id + extension;

      try {
        File file = new File(fileFullPath);
        if (file.exists()) {
          if (file.delete()) {
            logger.debug("File Delete Success [{}]", fileFullPath);
          } else {
            throw new Exception();
          }
        }
      } catch (Exception e) {
        logger.debug("File Delete Fail [{}]", fileFullPath);
      }
    }
  }

  public String getSystemConfPath(String paramPath) {
    String path = System.getenv(envPath);
    path += paramPath;
    return path;
  }

  public String getFileChecksum(MultipartFile mfile) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    logger.debug("isExists : " + mfile.isEmpty());
    InputStream fis = new BufferedInputStream(mfile.getInputStream());
    byte[] byteArray = new byte[1024];
    int bytesCount = 0;

    while ((bytesCount = fis.read(byteArray)) != -1) {
      digest.update(byteArray, 0, bytesCount);
    }

    fis.close();

    byte[] bytes = digest.digest();

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
    }

    return sb.toString();
  }

  public Integer getFileDuration(MultipartFile mfile) throws Exception {
    float durationInSeconds = 0;

    if (!SystemCode.FILE_COTENT_TYPE_AUDIO.equals(mfile.getContentType())) {
      return Math.round(durationInSeconds);
    }

    try {
      File file = multipartToFile(mfile);
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
      AudioFormat format = audioInputStream.getFormat();
      long audioFileLength = file.length();
      int frameSize = format.getFrameSize();
      float frameRate = format.getFrameRate();
      durationInSeconds = (audioFileLength / (frameSize * frameRate));
    } catch (Exception e) {
      logger.error("getFileDuration e : " , e);
    } finally {
      return Math.round(durationInSeconds);
    }
  }

  public String getWavFileRate(MultipartFile mfile) throws Exception {

    if (!SystemCode.FILE_COTENT_TYPE_AUDIO.equals(mfile.getContentType())) {
      throw new Exception(SystemErrMsg.NOT_SUPPORTED_FILE_EXTENSION);
    }

    try {
      File file = multipartToFile(mfile);
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
      AudioFormat format = audioInputStream.getFormat();
      if (format.getSampleRate() == 16000.0) {
        return "16000";
      } else if (format.getSampleRate() == 8000.0) {
        return "8000";
      } else {
        throw new Exception(SystemErrMsg.NOT_SUPPORTED_WAV_RATE);
      }
    } catch (Exception e) {
      logger.error("getWavFileRate e : " , e);
      throw e;
    }
  }

  public void mkdirs(String path) {
    try {
      if (!"".equals(path)) {
        File f = new File(path);
        if (!f.exists()) {
          f.mkdirs();
        }
      }
    } catch (Exception e) {
      logger.error("mkdirs e : " , e);
    }
  }

  public File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
    File convFile = new File(getSystemConfPath(tempPath) + "/" + multipart.getOriginalFilename());
    convFile.createNewFile();
    FileOutputStream fos = new FileOutputStream(convFile);
    fos.write(multipart.getBytes());
    fos.close();
    return convFile;
  }

  public File getPcmFile(String id, String path) throws Exception {
    String fileFullPath = getSystemConfPath(path) + "/" + id + SystemCode.FILE_EXTENSION_WAV;
    File sourceFile = new File(fileFullPath);
    File targetFile = new File(getSystemConfPath(tempPath) + "/temp.wav");

    AudioInputStream sourceAudioInputStream = AudioSystem.getAudioInputStream(sourceFile);
    AudioInputStream targetAudioInputStream = AudioSystem
        .getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, sourceAudioInputStream);

    AudioFormat targetFormat = new AudioFormat(new AudioFormat.Encoding("PCM_SIGNED"), 8000, 16, 1,
        2, 8000, false);
    AudioInputStream targetAudioInputStream1 = AudioSystem
        .getAudioInputStream(targetFormat, targetAudioInputStream);
    AudioSystem.write(targetAudioInputStream1, AudioFileFormat.Type.WAVE, targetFile);

    return targetFile;
  }

  public byte[] getFileToByteArr(File file) throws Exception {
    FileInputStream fileInputStream = null;
    byte[] bytesArray = null;

    bytesArray = new byte[(int) file.length()];
    fileInputStream = new FileInputStream(file);

    if (fileInputStream != null) {
      try {
        while (fileInputStream.read(bytesArray) > 0) {
          fileInputStream.read(bytesArray);
        }
        fileInputStream.close();
      } catch (IOException e) {
        logger.error("getFileToByteArr e : " , e);
      }
    }
    return bytesArray;
  }

  /*public <T>File objectToCsvFile(List<T> objectList, List<String> headers) throws Exception {
    //BufferedWriter out = new BufferedWriter(new FileWriter(getSystemConfPath(tempPath)+"/blrablra.csv"));
    //mkdirs(tempPath);
    BufferedWriter out = new BufferedWriter(new FileWriter("/home/minds/maum/upload/mlt/blrablra.csv"));


    try{

      for(int i=0 ; i < headers.size() ; i++){
        if(i == 0) out.write(headers.get(i));
        else out.write(SystemCode.FILE_SEPARATE_COMMA + headers.get(i));
        if(i == headers.size()-1) out.newLine();
      }

      for(T object : objectList){
        for (int i=0 ; i < headers.size() ; i++) {
          Method m = object.getClass().getDeclaredMethod("get" + StringUtils.capitalize(headers.get(i)));
          if(i == 0) out.write((String)m.invoke(object));
          else out.write(SystemCode.FILE_SEPARATE_COMMA + m.invoke(object));
          if(i == headers.size()-1) out.newLine();
        }
      }

      //return new File(getSystemConfPath(tempPath)+"/blrablra.csv");
      File file = new File("/home/minds/maum/upload/mlt/blrablra.csv");
      return new File("/home/minds/maum/upload/mlt/blrablra.csv");
    } catch (Exception e){
      return null;
    }finally {
      out.close();
    }
  }*/

  public List<Map> csvFileToObject(MultipartFile mfile) throws IOException {
    List<Map> resultList = new ArrayList<>();
    File file = multipartToFile(mfile);
    Reader reader = new FileReader(file);
    Iterator<Map<String, String>> iterator = new CsvMapper()
        .readerFor(Map.class)
        .with(CsvSchema.emptySchema().withHeader())
        .readValues(reader);
    while (iterator.hasNext()) {
      Map<String, String> keyVals = iterator.next();
      resultList.add(keyVals);
    }
    return resultList;
  }

  public List<Map> formatFileToObject(MultipartFile mfile, String separate) throws IOException {
    mkdirs(getSystemConfPath(tempPath));
    List<Map> resultList = new ArrayList<>();
    File file = multipartToFile(mfile);
    BufferedReader buf = new BufferedReader(new FileReader(file));

    String lineJustFetched = null;
    String[] wordsArray;

    while (true) {
      lineJustFetched = buf.readLine();
      if (lineJustFetched == null) {
        break;
      } else {
        wordsArray = lineJustFetched.split(separate);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < wordsArray.length; i++) {
          map.put("column" + (i + 1), wordsArray[i]);
        }
        resultList.add(map);
      }
    }
    return resultList;
  }


  public List<List> excelFileToObject(MultipartFile mfile, List<String> columns) throws Exception {
    List<List> allList = new ArrayList<>();
    String extension = mfile.getOriginalFilename().split("\\.")[1];
    File file = multipartToFile(mfile);
    FileInputStream fis = new FileInputStream(file);
    if ("xls".equals(extension.toLowerCase())) {
      HSSFWorkbook workbook = new HSSFWorkbook(fis);
      allList = xlsToObject(workbook, columns);
    } else if ("xlsx".equals(extension.toLowerCase())) {
      XSSFWorkbook workbook = new XSSFWorkbook(fis);
      allList = xlsxToObject(workbook, columns);
    } else {
      throw new Exception("not support extension :: " + extension);
    }
    return allList;
  }

  public List<List> xlsToObject(HSSFWorkbook workbook, List<String> columns) {
    List<List> allList = new ArrayList<>();
    FormulaEvaluator objFormulaEvaluator = new HSSFFormulaEvaluator(workbook);
    DataFormatter objDefaultFormat = new DataFormatter();

    for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
      HSSFSheet sheet = workbook.getSheetAt(s);
      List<HashMap> sheetList = new ArrayList<>();
      for (int r = 1; r < sheet.getPhysicalNumberOfRows(); r++) {
        HSSFRow row = sheet.getRow(r);
        HashMap<String, Object> rowMap = new HashMap<>();
        if (row != null) {
          for (int c = 0; c < row.getPhysicalNumberOfCells(); c++) {
            Cell cellValue = row.getCell(c);
            if (cellValue != null) {
              objFormulaEvaluator.evaluate(
                  cellValue);
              // DataFormatter 를 쓰면 Cell안에 값 그대로 넘어옴
              String cellValueStr = objDefaultFormat
                  .formatCellValue(cellValue, objFormulaEvaluator);
              rowMap.put(columns.get(c), cellValueStr);
            }
//            if (cell == null) {
//              continue;
//            } else {
//              switch (cell.getCellType()) {
//                case HSSFCell.CELL_TYPE_FORMULA:
//                  value = cell.getCellFormula();
//                  break;
//                case HSSFCell.CELL_TYPE_NUMERIC:
//                  value = cell.getNumericCellValue() + "";
//                  break;
//                case HSSFCell.CELL_TYPE_STRING:
//                  value = cell.getStringCellValue() + "";
//                  break;
//                case HSSFCell.CELL_TYPE_BLANK:
//                  value = "";
//                  break;
//                case HSSFCell.CELL_TYPE_ERROR:
//                  value = cell.getErrorCellValue() + "";
//                  break;
//              }
//              rowMap.put(columns.get(c), value);
//            }
          }
          sheetList.add(rowMap);
        }
      }
      allList.add(sheetList);
    }
    return allList;
  }

  public List<List> xlsxToObject(XSSFWorkbook workbook, List<String> columns) {
    List<List> allList = new ArrayList<>();
    FormulaEvaluator objFormulaEvaluator = new XSSFFormulaEvaluator(workbook);
    DataFormatter objDefaultFormat = new DataFormatter();
    for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
      XSSFSheet sheet = workbook.getSheetAt(s);
      List<HashMap> sheetList = new ArrayList<>();
      for (int r = 1; r < sheet.getPhysicalNumberOfRows(); r++) {
        XSSFRow row = sheet.getRow(r);
        HashMap<String, Object> rowMap = new HashMap<>();
        if (row != null) {
          for (int c = 0; c < row.getPhysicalNumberOfCells(); c++) {
            Cell cellValue = row.getCell(c);
            if (cellValue != null) {
              objFormulaEvaluator.evaluate(
                  cellValue);
              // DataFormatter 를 쓰면 Cell안에 값 그대로 넘어옴
              String cellValueStr = objDefaultFormat
                  .formatCellValue(cellValue, objFormulaEvaluator);
              rowMap.put(columns.get(c), cellValueStr);
            }
//            if (cell == null) {
//              continue;
//            } else {
//              switch (cell.getCellType()) {
//                case XSSFCell.CELL_TYPE_FORMULA:
//                  value = cell.getCellFormula();
//                  break;
//                case XSSFCell.CELL_TYPE_NUMERIC:
//                  value = cell.getDateCellValue() + "";
//                  break;
//                case XSSFCell.CELL_TYPE_STRING:
//                  value = cell.getStringCellValue() + "";
//                  break;
//                case XSSFCell.CELL_TYPE_BLANK:
//                  value = "";
//                  break;
//                case XSSFCell.CELL_TYPE_ERROR:
//                  value = cell.getErrorCellValue() + "";
//                  break;
//              }
//              rowMap.put(columns.get(c), value);
//            }
          }
          sheetList.add(rowMap);
        }
      }
      allList.add(sheetList);
    }
    return allList;
  }

  public <T> InputStreamResource ObjectToExcel(List<T> objectList, List<String> headers,
      List<String> columns, String fileName) throws Exception {
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet();
    XSSFRow row = sheet.createRow(0);
    XSSFCell cell;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int hIdx = 0;
    for (String header : headers) {
      cell = row.createCell(hIdx);
      cell.setCellValue(header);
      hIdx++;
    }

    int i = 0;
    for (T t : objectList) {
      row = sheet.createRow(++i);

      for (int j = 0; j < columns.size(); j++) {
        Method m = t.getClass().getDeclaredMethod("get" + StringUtils.capitalize(columns.get(j)));
        cell = row.createCell(j);

        switch (m.getReturnType().toGenericString()) {
          case "public final class java.lang.String":
            cell.setCellType(CellType.STRING);
            cell.setCellValue((String) m.invoke(t));
            break;
          case "public class java.math.BigDecimal":
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(((BigDecimal) m.invoke(t)).floatValue());
            break;
          case "public class java.util.Date":
            cell.setCellValue(df.format(m.invoke(t)));
            break;
        }
      }

    }
    mkdirs(tempPath);
    File file = new File(tempPath + "/" + fileName + ".xlsx");

    try {
      fos = new FileOutputStream(file);
      workbook.write(fos);
    } catch (FileNotFoundException e) {
      logger.error("ObjectToExcel e : " , e);
    } catch (IOException e) {
      logger.error("ObjectToExcel e : " , e);
    } finally {
      try {
        if (workbook != null) {
          workbook.close();
        }
        if (fos != null) {
          fos.close();
        }

      } catch (IOException e) {
        // TODO Auto-generated catch block
        logger.error("ObjectToExcel e : " , e);
      }
    }

    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

    return resource;
  }


  public List<List> oneToManyXlsxToObject(XSSFWorkbook workbook, List<String> columns) {
    List<List> allList = new ArrayList<>();
    for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
      XSSFSheet sheet = workbook.getSheetAt(s);
      List<HashMap> sheetList = new ArrayList<>();
      for (int r = 1; r < sheet.getPhysicalNumberOfRows(); r++) {
        XSSFRow row = sheet.getRow(r);

        if (row != null) {
          String value = "";
          List<String> array = new ArrayList<>();
          for (int c = 0; c < row.getPhysicalNumberOfCells(); c++) {
            XSSFCell cell = row.getCell(c);
            if (cell == null) {
              continue;
            } else {
              if (c == 0) {
                value = cell.getStringCellValue() + "";
              } else {
                array.add(cell.getStringCellValue() + "");
              }
            }
          }
          for (String str : array) {
            if (str.equals("")) {

            } else {
              HashMap<String, Object> rowMap = new HashMap<>();
              rowMap.put("one", value);
              rowMap.put("many", str);
              sheetList.add(rowMap);
            }

          }
        }
      }
      allList.add(sheetList);
    }
    return allList;
  }


  // tar.gz 압축
  public static void compressTarGZ(String dirPath, String tarGzPath)
      throws FileNotFoundException, IOException {
    TarArchiveOutputStream tOut = null;

    try {
      logger.debug(new File(".").getAbsolutePath());
      tOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(
          new BufferedOutputStream(
              new FileOutputStream(
                  new File(tarGzPath)
              )
          )
      )
      );
      addFileToTarGz(tOut, dirPath, "");
    } finally {
      tOut.finish();
      ;
      tOut.close();
      tOut = null;
    }
  }

  private static void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base)
      throws IOException {
    File f = new File(path);
    String entryName = base + f.getName();
    TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
    tOut.putArchiveEntry(tarEntry);
    if (f.isFile()) {
      IOUtils.copy(new FileInputStream(f), tOut);
      tOut.closeArchiveEntry();
    } else {
      tOut.closeArchiveEntry();
      File[] children = f.listFiles();
      if (children != null) {
        for (File child : children) {
          addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/");
        }
      }
    }
  }

  // tar.gz 압축 해제
  public static void uncompressTarGZ(File tarFile, File dest) throws IOException {
    logger.debug("====== uncompressTarGZ start :: tarFile {}, dest {}",
        tarFile.getCanonicalPath(), dest.getCanonicalPath());
    dest.mkdirs();
    TarArchiveInputStream tarIn = null;

    tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(
        new BufferedInputStream(
            new FileInputStream(
                tarFile
            )
        )
    )
    );
    TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
    while (tarEntry != null) {
      File destPath = new File(dest, tarEntry.getName());
      if (tarEntry.isDirectory()) {
        destPath.mkdirs();
      } else {
        destPath.createNewFile();
        byte[] btoRead = new byte[1024];
        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
        int len = 0;

        while ((len = tarIn.read(btoRead)) != -1) {
          bout.write(btoRead, 0, len);
        }
        bout.close();
        btoRead = null;
      }
      tarEntry = tarIn.getNextTarEntry();
    }
    tarIn.close();
    logger.debug("====== uncompressTarGZ end");
  }

  public static void moveFileToRootDir(String rootDirPath) throws IOException {
    logger.debug("====== moveFileToRootDir start :: rootDirPath {}", rootDirPath);
    // 1depth 폴더 안에 파일까지만 옮길수 있음
    File rootDir = new File(rootDirPath);
    File[] fileList = rootDir.listFiles();
    for (int i = 0; i < fileList.length; i++) {
      File file = fileList[i];
      if (file.isDirectory()) {
        File subDir = new File(file.getCanonicalPath().toString());
        File[] subDirFileList = subDir.listFiles();
        for (int j = 0; j < subDirFileList.length; j++) {
          File f = subDirFileList[j];
          if (f.isFile()) {
            // file
            Files.move(Paths.get(f.getCanonicalFile().toString()),
                Paths.get(rootDirPath).resolve(f.getName()));
          }
        }
        subDir.delete();
      }
    }
    logger.debug("====== moveFileToRootDir end");
  }

  public static void copyFile(String source, String dest) throws IOException {
    logger.debug("===== copyFile start :: source {}, dest {}", source, dest);
    Files.copy(new File(source).toPath(), new File(dest).toPath());
    logger.debug("===== copyFile end");
  }
}
