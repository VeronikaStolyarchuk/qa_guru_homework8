package tests;

import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.codeborne.pdftest.PDF;
import utils.Product;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilesParsingTests {

    private final ClassLoader cl = FilesParsingTests.class.getClassLoader();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void pdfZipFilesParsingTest () throws Exception {
        try(ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream("new.zip"))){
            Assertions.assertNotNull(zis);

            ZipEntry entry;
            boolean hasZipEntry = false;
            boolean hasPdfFile = false;

            while((entry = zis.getNextEntry()) != null){
                hasZipEntry = true;
                String name = entry.getName();

                if(name.contains(".pdf")){
                    hasPdfFile = true;
                    PDF pdf = new PDF(zis);
                    Assertions.assertTrue(pdf.text.contains("How do black holes form?"));

                }
            }
            Assertions.assertTrue(hasZipEntry, "Архив не содежит файлов");
            Assertions.assertTrue(hasPdfFile, "Архив не содежит Pdf файлов");
        }
    }

    @Test
    void xlsZipFilesParsingTest () throws Exception {
        try(ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream("new.zip"))){
            Assertions.assertNotNull(zis);

            ZipEntry entry;
            boolean hasZipEntry = false;
            boolean hasExcelFile = false;

            while((entry = zis.getNextEntry()) != null){
                hasZipEntry = true;
                String name = entry.getName();

                if(name.endsWith(".xls") || name.endsWith(".xlsx")){
                    hasExcelFile = true;
                    XLS xls = new XLS(zis);
                    String textXls = xls.excel.getSheetAt(0).getRow(5).getCell(3).getStringCellValue();
                    Assertions.assertTrue(textXls.contains("КОМИТЕТ ФИНАНСОВ АДМИНИСТРАЦИИ МУНИЦИПАЛЬНОГО ОКРУГА ЛИПЕЦКОЙ ОБЛАСТИ"),
                            "Значение не найдено");

                }
            }
            Assertions.assertTrue(hasZipEntry, "Архив не содежит файлов");
            Assertions.assertTrue(hasExcelFile, "Архив не содежит Excel файлов");
        }
    }

    @Test
    void csvZipFilesParsingTest () throws Exception {
        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream("new.zip"))) {
            Assertions.assertNotNull(zis);

            ZipEntry entry;
            boolean hasZipEntry = false;
            boolean hasCsvFile = false;

            while ((entry = zis.getNextEntry()) != null) {
                hasZipEntry = true;
                String name = entry.getName();

                if (name.endsWith(".csv")) {
                    hasCsvFile = true;

                    String csvContent = new String(zis.readAllBytes(), StandardCharsets.UTF_8);
                    zis.closeEntry();

                    try (CSVReader csv = new CSVReader(new StringReader(csvContent))) {

                        List<String[]> data = csv.readAll();

                        Assertions.assertArrayEquals(new String[]{"name", "phoneNumber", "email",
                                "address", "userAgent", "hexcolor"}, data.get(0));
                        Assertions.assertArrayEquals(new String[]{"Antonetta Buckridge", "+1-982-452-1335", "mathew.thiel@hotmail.com",
                                "428 Bayer Rapids Apt. 644", "Linux i686", "#d82653"}, data.get(1));
                    }
                } else {
                    zis.closeEntry();
                }
            }
            Assertions.assertTrue(hasZipEntry, "Архив не содежит файлов");
            Assertions.assertTrue(hasCsvFile, "Архив не содежит Csv файлов");

        }
    }

    @Test
    void jsonFileParsingTest () throws Exception {
        try(InputStream is = cl.getResourceAsStream("mobile.json")){
            Product product = objectMapper.readValue(is, Product.class);

            Assertions.assertEquals(98765, product.id);
            Assertions.assertEquals("Беспроводной зарядный модуль ProCharge X2", product.name);
            Assertions.assertEquals("VoltaTech", product.brand);

            Assertions.assertEquals(185.5, product.mass.value);
            Assertions.assertEquals("g", product.mass.unit);

            Assertions.assertEquals(12.4, product.dimensions.length);
            Assertions.assertEquals(7.2, product.dimensions.width);
            Assertions.assertEquals(2.1, product.dimensions.height);
            Assertions.assertEquals("cm", product.dimensions.unit);

            Assertions.assertEquals(2, product.prices.size());

            Assertions.assertEquals("RUB", product.prices.get(0).currency);
            Assertions.assertEquals(2990, product.prices.get(0).amount);
            Assertions.assertEquals("retail", product.prices.get(0).type);

            Assertions.assertEquals("RUB", product.prices.get(1).currency);
            Assertions.assertEquals(2450, product.prices.get(1).amount);
            Assertions.assertEquals("wholesale", product.prices.get(1).type);

            Assertions.assertEquals(true, product.is_active);

        }
    }
}
