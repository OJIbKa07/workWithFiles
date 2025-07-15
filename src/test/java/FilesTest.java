import com.codeborne.pdftest.PDF;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zeroturnaround.zip.ZipEntryCallback;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;


public class FilesTest {

    @Test
    void pdfFileParsingTest() throws Exception {
        File zipFile = new File("src/test/resources/Архив.zip");

        ByteArrayOutputStream pdfData = new ByteArrayOutputStream();
        AtomicBoolean pdfFound = new AtomicBoolean(false);

        ZipUtil.iterate(zipFile, new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                if (pdfFound.get()) return;

                if (zipEntry.getName().toLowerCase().endsWith(".pdf")) {
                    System.out.println("PDF найден: " + zipEntry.getName());
                    in.transferTo(pdfData);
                    pdfFound.set(true);
                }
            }
        });

        assertThat(pdfFound.get())
                .as("PDF-файл не найден в архиве")
                .isTrue();

        assertThat(pdfData.size())
                .as("Найденный PDF-файл пуст")
                .isGreaterThan(0);

        try (InputStream pdfInputStream = new ByteArrayInputStream(pdfData.toByteArray())) {
            PDF pdf = new PDF(pdfInputStream);

            assertThat(pdf.text).contains("Лори", "Содержание");
            assertThat(pdf.numberOfPages).isBetween(20, 25);

            if (pdf.author != null) {
                assertThat(pdf.author).containsIgnoringCase("Стивен Кинг");
            }

            assertThat(pdf.text.trim()).isNotEmpty();
        }
    }

    @Test
    void excelFileParsingTest() throws Exception {
        File xlsxFile = new File("src/test/resources/Архив.zip");

        ByteArrayOutputStream xlsxData = new ByteArrayOutputStream();
        AtomicBoolean xlsxFound = new AtomicBoolean(false);

        ZipUtil.iterate(xlsxFile, new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                if (xlsxFound.get()) return;

                if (zipEntry.getName().toLowerCase().endsWith(".xlsx")) {
                    System.out.println("XLSX найден: " + zipEntry.getName());
                    in.transferTo(xlsxData);
                    xlsxFound.set(true);
                }
            }
        });

        Assertions.assertTrue(xlsxFound.get(), "XLSX-файл не найден в архиве");

        Assertions.assertTrue(xlsxData.size() > 0, "XLSX-файл найден, но он пуст");

        try (InputStream xlsxInputStream = new ByteArrayInputStream(xlsxData.toByteArray())) {
            XLS xls = new XLS(xlsxInputStream);

            String actualCellValue0 = xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
            String actualCellValue1 = xls.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue();
            String actualCellValue2 = xls.excel.getSheetAt(0).getRow(0).getCell(2).getStringCellValue();

            Assertions.assertTrue(actualCellValue0.contains("Имя пользователя"));
            Assertions.assertTrue(actualCellValue1.contains("Возраст"));
            Assertions.assertTrue(actualCellValue2.contains("Профессия"));
        }
    }

    @Test
    void csvFileParsingTest() throws Exception {
        File zipFile = new File("src/test/resources/Архив.zip");

        ByteArrayOutputStream csvData = new ByteArrayOutputStream();
        AtomicBoolean csvFound = new AtomicBoolean(false);

        ZipUtil.iterate(zipFile, new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                if (csvFound.get()) return;

                if (zipEntry.getName().toLowerCase().endsWith(".csv")) {
                    in.transferTo(csvData);
                    csvFound.set(true);
                }
            }
        });

        Assertions.assertTrue(csvFound.get(), "CSV-файл не найден в архиве");

        try (
                InputStream csvInputStream = new ByteArrayInputStream(csvData.toByteArray());
                InputStreamReader isr = new InputStreamReader(csvInputStream);
                CSVReader csvReader = new CSVReader(isr)
        ) {
            List<String[]> data = csvReader.readAll();

            Assertions.assertFalse(data.isEmpty(), "CSV-файл пуст");

            Assertions.assertArrayEquals(new String[]{"Alice", "13", "New York"}, data.get(0));
            Assertions.assertArrayEquals(new String[]{"Bob", "30", "Los Angels"}, data.get(1));
            Assertions.assertArrayEquals(new String[]{"Tim", "20", "Chicago"}, data.get(2));
        }
    }
}
