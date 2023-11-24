package first.common.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class MSExcelFileParser {

  /**
   * 첫 행을 무시하고 처리할지에 대한 판단. (default는 false)
   */
  private boolean useOfFirstRow = false;
  /**
   * MSExcel File InputStream data
   */
  private InputStream inp = null;
  /**
   * 처리 대상 sheet 번호 (default는 0)
   */
  private int targetSheetNum = 0;
  /**
   * row별로 각각의 cell data에 대한 Naming key - array key 순서대로 컬럼 처리.
   */
  private String[] orderedKeyName = null;

  public MSExcelFileParser() {
  }

  /**
   * @param inp InputStream Excel file inputstream
   * @param targetSheetNum int 처리 대상 시트 번호
   * @param orderedKeyName String[]  row별로 각 cell data에 대한 Naming key
   * @param useOfFirstRow boolean 첫 행을 무시하고 처리할지에 대한 판단
   */
  public MSExcelFileParser(InputStream inp, int targetSheetNum, String[] orderedKeyName,
      boolean useOfFirstRow) {
    this.inp = inp;
    this.targetSheetNum = targetSheetNum;
    this.orderedKeyName = orderedKeyName;
    this.useOfFirstRow = useOfFirstRow;
  }

  /**
   * 엑셀 파일의 특정 Sheet에 대하여 row별로 각 cell에 대하여 주어진 Key에 해당하는 값으로 Map에 담아 처리 후, Map Data를 List에 담아 반환.
   *
   * @return List<Map < String, String>>
   */
  public List<Map<String, String>> getData() throws Exception {

    Workbook wb = WorkbookFactory.create(inp);

    //int sheetNum = wb.getNumberOfSheets();//시트 개수

    Sheet sheet = wb.getSheetAt(targetSheetNum);
    int rows = sheet.getPhysicalNumberOfRows(); //행수
    Row row;

    ArrayList<Map<String, String>> dataList = new ArrayList();

    // 1부터 시작 -> 첫번째 줄 제외
    int startRow = 1;
    for (int i = 0; i < rows; i++) {
      row = sheet.getRow(i);
      Cell cell = row.getCell(0);
      if (cell != null ) {
        startRow = this.useOfFirstRow ? i : i + 1;
        break;
      }
    }

    for (int i = startRow; i < rows; i++) {
      row = sheet.getRow(i);
      if (row == null || isRowEmpty(row)) {
        break;
      }
      dataList.add(getColumnData(row));
    }
    return dataList;
  }

  /**
   * 엑셀의 Row Data를  Cell 별로 Map에 담아 반환. - Map의 Key는 생성자 참조.(String[] orderedKeyName)
   *
   * @param row Row
   * @return Map<String, String>
   */
  private Map<String, String> getColumnData(Row row) throws Exception {

    int processCellCount = this.orderedKeyName.length;  //총 orderedKeyName array 수 만큼 Column data가 생성됨.

    String cellData = "";
    Map<String, String> dataMap = new HashMap<String, String>();

    for (int i = 0; i < processCellCount; i++) {

      cellData = getCellData(row.getCell(i));
      if (cellData == null || "".equals(cellData)) {
        //throw new Exception("Data insufficient !!");
      }
      dataMap.put(this.orderedKeyName[i], cellData);
    }

    return dataMap;
  }

  /**
   * Cell의 Data value를 String으로 변환하여 반환. - 유형에 따라 처리(numeric, formula, string, blank 지원)
   *
   * @param cell Cell
   * @return String
   */
  private String getCellData(Cell cell) throws Exception {
    String data = "";

    if (cell != null) {
      FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper()
          .createFormulaEvaluator();

      try {
        switch (cell.getCellType()) {
          case STRING:
            data = cell.getRichStringCellValue().getString().trim();
            break;
          case FORMULA:
            if (!(cell.toString() == "")) {
              if (evaluator.evaluateFormulaCell(cell) == CellType.NUMERIC) {
                data = numericToString(cell.getNumericCellValue());
                data = cell.getNumericCellValue() + "";
              } else if (evaluator.evaluateFormulaCell(cell) == CellType.STRING) {
                data = cell.getRichStringCellValue().getString().trim();
              }
            }
            break;
          case NUMERIC:
            data = numericToString(cell.getNumericCellValue());
            data = cell.getNumericCellValue() + "";
            break;
          case BLANK:
            data = "";
            break;

        }
      } catch (Exception ex) {
        System.out.println("===== getCellData Exception : {}");
      }
    }
    // 2013-06-20 by es - Excel 처리 방식 변경 끝 - 1

    return data;
  }

  private boolean isRowEmpty(Row row) {
    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
      Cell cell = row.getCell(c);
      if (cell != null && cell.getCellType() != CellType.BLANK) {
        return false;
      }
    }
    return true;
  }

  /**
   * 엑셀에서 numeric value는 double 형이다. 즉, 1은 1.0 으로 표시된다. 이것을 String으로 전환시 "1.0"으로 표시되므로, 이를 "1"로 변환하기
   * 위한 처리를 한다. - double의 int값으로 double을 나눈 몫이 0보다 크면 double을 String, 그렇지 않으면 int값을 String 으로 반환. 예)
   * 1.23 : "1.23" ,  1.0 : "1"
   *
   * @param d_tmp double
   * @return String
   */
  private String numericToString(double d_tmp) {
    int i_tmp = (int) d_tmp;
    return ((d_tmp % i_tmp) > 0) ? String.valueOf(d_tmp) : String.valueOf(i_tmp);
  }
}
