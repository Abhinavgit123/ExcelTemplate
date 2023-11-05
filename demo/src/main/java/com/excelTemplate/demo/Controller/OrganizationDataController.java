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

@RestController
@RequestMapping("/organizations")
public class OrganizationDataController {
    @Autowired
    private OrganizationDataService organizationDataService;


    @PostMapping("/{orgId}/add-data")
    public OrganizationData addData(@PathVariable String orgId, @RequestBody Map<String, List<String>> data) {
        return organizationDataService.addData(orgId, data);
    }

    @GetMapping("/{orgId}/retrieve-data")
    public OrganizationData retrieveData(@PathVariable String orgId) {
        return organizationDataService.retrieveData(orgId);
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
