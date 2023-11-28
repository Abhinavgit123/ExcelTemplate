package com.excelTemplate.demo.Service;

import com.excelTemplate.demo.Entity.OrganizationData;
import com.excelTemplate.demo.Repository.OrganizationDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Column;
import java.util.NoSuchElementException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class OrganizationDataService {
    @Autowired
    private OrganizationDataRepository organizationDataRepository;

    public OrganizationData addData(String orgId, Map<String, List<String>> data) {
        OrganizationData organizationData = organizationDataRepository.findById(orgId)
                .orElse(new OrganizationData());

        organizationData.setId(orgId);

        Map<String, List<String>> existingData = organizationData.getData();
        if (existingData == null) {
            existingData = new LinkedHashMap<>();
        }

        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            List<String> fieldValues = entry.getValue();

            // If the field already exists, add the new values to the existing list
            if (existingData.containsKey(fieldName)) {
                existingData.get(fieldName).addAll(fieldValues);
            } else {
                existingData.put(fieldName, new ArrayList<>(fieldValues));
            }
        }
        organizationData.setData(existingData);

        organizationData.setExcelData(generateExcelData(existingData,orgId));

        return organizationDataRepository.save(organizationData);
    }



    public OrganizationData retrieveData(String orgId) {
        try {
            return organizationDataRepository.findById(orgId)
                    .orElseThrow(() -> new NoSuchElementException("No data found for orgId: " + orgId));
        } catch (NoSuchElementException ex) {
            log.error("No data found for orgId: {}", orgId);
            // Handle the exception, return a default value, or do something appropriate
            // For example, you might return a special OrganizationData instance or null
            return null;
        } catch (Exception ex) {
            log.error("An unexpected error occurred", ex);
            // Handle other exceptions, log them, and return a default value or null
            return null;
        }
    }


    private byte[] generateExcelData(Map<String, List<String>> data,String orgId) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(orgId);

            int rowIndex = 0;
            Row headerRow = sheet.createRow(rowIndex++);
            int columnIndex = 0;
            for (Map.Entry<String, List<String>> entry : data.entrySet()) {
                Cell cell = headerRow.createCell(columnIndex++);
                cell.setCellValue(entry.getKey());
            }

               // Starting from the second row
            int colIndex = 0;  // Starting from the first column

            for (String fieldName : data.keySet()) {
                rowIndex = 1;
                List<String> fieldValues = data.get(fieldName);

                for (String value : fieldValues) {
                    Row dataRow = sheet.getRow(rowIndex);
                    if (dataRow == null) {
                        dataRow = sheet.createRow(rowIndex);
                    }

                    Cell cell = dataRow.createCell(colIndex);
                    cell.setCellValue(value);

                    rowIndex++;
                }

                // Move to the next column
                colIndex++;
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}
