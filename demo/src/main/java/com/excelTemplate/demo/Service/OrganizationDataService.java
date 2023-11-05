package com.excelTemplate.demo.Service;

import com.excelTemplate.demo.Entity.OrganizationData;
import com.excelTemplate.demo.Repository.OrganizationDataRepository;
import org.apache.poi.ss.formula.functions.Column;
import java.util.NoSuchElementException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class OrganizationDataService {
    @Autowired
    private OrganizationDataRepository organizationDataRepository;

    public OrganizationData addData(String orgId, Map<String, List<String>> data) {
        OrganizationData organizationData = organizationDataRepository.findById(orgId)
                .orElse(new OrganizationData());

        organizationData.setId(orgId);

        Map<String, List<String>> existingData = organizationData.getData();
        if (existingData == null) {
            existingData = new HashMap<>();
        }

        // Merge the new data with the existing data
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
        return organizationDataRepository.findById(orgId)
                .orElseThrow(() -> new NoSuchElementException("No data found for orgId: " + orgId));
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

            int colIndex = 0;
            for (String fieldName : data.keySet()) {
                List<String> fieldValues = data.get(fieldName);
                rowIndex = 1;  // Starting from the second row
                for (String value : fieldValues) {
                    Row dataRow = sheet.getRow(rowIndex);
                    if (dataRow == null) {
                        dataRow = sheet.createRow(rowIndex++);
                    }

                    Cell cell = dataRow.createCell(colIndex);
                    cell.setCellValue(value);
                }
                colIndex++;
//                rowIndex++;
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}