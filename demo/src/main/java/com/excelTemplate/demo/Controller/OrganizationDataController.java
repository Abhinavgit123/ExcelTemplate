package com.excelTemplate.demo.Controller;

import com.excelTemplate.demo.Entity.OrganizationData;
import com.excelTemplate.demo.Service.OrganizationDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/organizations")
public class OrganizationDataController {
    @Autowired
    private OrganizationDataService organizationDataService;


    @PostMapping("/{orgId}/add-data")
    public ResponseEntity<String> addData(@PathVariable String orgId, @RequestBody Map<String, List<String>> data) {
        OrganizationData res= organizationDataService.addData(orgId, data);
        if(res!=null){
            String message="\"Data has been added to orgId: \" + orgId";
            return new ResponseEntity<>(message,HttpStatus.OK);
        }else{
            String errorMessage = "Failed to add data to orgId: " + orgId;
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{orgId}/retrieve-data")
    public ResponseEntity<Object> retrieveDataResponse(@PathVariable String orgId) {
        OrganizationData data = organizationDataService.retrieveData(orgId);

        if (data != null) {
            // Data found, return a success response
            return new ResponseEntity<>(data, HttpStatus.OK);
        } else {
            // No data found, return a not found response with a custom message
            String errorMessage = "No data found for orgId: " + orgId;
            return new ResponseEntity<>(errorMessage, HttpStatus.OK);
        }
    }


    @GetMapping("/{orgId}/download-excel")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable String orgId) {
        OrganizationData organizationData = organizationDataService.retrieveData(orgId);
        byte[] excelData = organizationData.getExcelData();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", orgId + "_data.xlsx");

        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }
}
