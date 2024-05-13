import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class SegmentTree {
    private int[] segmentTree;
    private int size;
    private static int iterations = 0;

    private Workbook workbookAdd = new XSSFWorkbook();
    private Sheet sheetAdd = workbookAdd.createSheet("Add Data");
    private Workbook workbookSum = new XSSFWorkbook();
    private Sheet sheetSum = workbookSum.createSheet("Sum Data");
    private Workbook workbookDelete = new XSSFWorkbook();
    private Sheet sheetDelete = workbookDelete.createSheet("Delete Data");

    public SegmentTree(int n) {
        size = 1;
        while (size < n) {
            size *= 2;
        }
        segmentTree = new int[2 * size];
    }

    public int len(){
        return segmentTree.length;
    }

    public void add(int index, int value) {
        add(1, 0, size - 1, index, value);
    }

    private void add(int v, int l, int r, int index, int value) {
        iterations++;
        if (l == r) {
            segmentTree[v] += value;
        } else {
            int m = (l + r) / 2;
            if (index <= m) {
                add(2 * v, l, m, index, value);
            } else {
                add(2 * v + 1, m + 1, r, index, value);
            }
            segmentTree[v] = segmentTree[2 * v] + segmentTree[2 * v + 1];
        }
    }

    public void delete(int index) {
        add(index, -segmentTree[index + size]);
        iterations++;
    }

    public int summa(int left, int right) {
        return summa(1, 0, size - 1, left, right);
    }

    private int summa(int v, int l, int r, int left, int right) {
        iterations++;
        if (left > right) {
            return 0;
        }
        if (left == l && right == r) {
            return segmentTree[v];
        }
        int m = (l + r) / 2;
        return summa(2 * v, l, m, left, Math.min(right, m)) +
                summa(2 * v + 1, m + 1, r, Math.max(left, m + 1), right);
    }

    public void writeDataToExcel(Workbook workbook, Sheet sheet, int iterationCount, long duration, int len) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1 );
        row.createCell(0).setCellValue(iterationCount);
        row.createCell(1).setCellValue(duration);
        row.createCell(2).setCellValue(len);
    }

    public static void main(String[] args) throws IOException {
        Random rand = new Random();

        int n = 10000;
        int[] ar = new int[n];
        for (int j = 0; j < n; j++) {
            ar[j] = rand.nextInt(99999999);
        }

        SegmentTree segmentTree = new SegmentTree(n);

        for (int i = 0; i < n; i++) {
            long startTime = System.nanoTime();
            int value = ar[i];
            segmentTree.add(i, value);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            int lenTree = segmentTree.len();
            segmentTree.writeDataToExcel(segmentTree.workbookAdd, segmentTree.sheetAdd, iterations, duration,lenTree);
            iterations = 0;
        }

        for (int i = 0; i < 100; i++) {
            int left = rand.nextInt(n);
            int right = rand.nextInt(n);
            if (left > right) {
                int temp = left;
                left = right;
                right = temp;
            }
            long startTime = System.nanoTime();
            segmentTree.summa(left, right);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);

            int cntElements =right  - left + 1;
                    segmentTree.writeDataToExcel(segmentTree.workbookSum, segmentTree.sheetSum, iterations, duration,cntElements);
            iterations = 0;
        }

        for (int i = 0; i < 1000; i++) {
            int index = rand.nextInt(n);
            long startTimeDelete = System.nanoTime();
            segmentTree.delete(index);
            long endTimeDelete = System.nanoTime();
            long durationDelete = (endTimeDelete - startTimeDelete);
            int lenTree = segmentTree.len();

            segmentTree.writeDataToExcel(segmentTree.workbookDelete, segmentTree.sheetDelete, iterations, durationDelete,lenTree);
            iterations = 0;
        }

        FileOutputStream fileOutAdd = new FileOutputStream("add.xlsx");
        FileOutputStream fileOutSum = new FileOutputStream("sum.xlsx");
        FileOutputStream fileOutDelete = new FileOutputStream("delete.xlsx");
        segmentTree.workbookAdd.write(fileOutAdd);
        segmentTree.workbookSum.write(fileOutSum);
        segmentTree.workbookDelete.write(fileOutDelete);
        fileOutAdd.close();
        fileOutSum.close();
        fileOutDelete.close();
    }
}