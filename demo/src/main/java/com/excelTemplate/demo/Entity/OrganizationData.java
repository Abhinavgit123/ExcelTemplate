package com.excelTemplate.demo.Entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "organization_data")
@Data
public class OrganizationData {
    @Id
    private String id; // Use organization or unique identifier
    private Map<String, List<String>> data; // Key-value pairs for dynamic fields
    // Other organization-specific properties, getters, setters, etc.
    private byte[] excelData;
}
