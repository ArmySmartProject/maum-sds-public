package first.common.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

public class CellRef {

  /**
   * Cell에 해당하는 Column Name을 가젼온다(A,B,C..)
   * 만약 Cell이 Null이라면 int cellIndex의 값으로
   * Column Name을 가져온다.
   *
   * @param cell
   * @param cellIndex
   * @return
   */
  public static String getName(Cell cell, int cellIndex) {
    int cellNum = 0;
    if (cell != null) {
      cellNum = cell.getColumnIndex();
    } else {
      cellNum = cellIndex;
    }

    return CellReference.convertNumToColString(cellNum);
  }

  public static String getValue(Cell cell, Workbook wb) {
    String value = "";
    FormulaEvaluator formulaEval = wb.getCreationHelper().createFormulaEvaluator();

    if (cell == null) {
      value = "";
    } else {
      if (cell.getCellType() == CellType.FORMULA) {
        CellValue evaluate = formulaEval.evaluate(cell);
        value = evaluate.getStringValue();
      } else if (cell.getCellType() == CellType.NUMERIC) {
        value = (int)cell.getNumericCellValue() + "";
      } else if (cell.getCellType() == CellType.STRING) {
        value = cell.getStringCellValue();
      } else if (cell.getCellType() == CellType.BOOLEAN) {
        value = cell.getBooleanCellValue() + "";
      } else if (cell.getCellType() == CellType.ERROR) {
        value = cell.getErrorCellValue() + "";
      } else if (cell.getCellType() == CellType.BLANK) {
        value = "";
      } else {
        value = cell.getStringCellValue();
      }
    }

    return value;
  }

}
