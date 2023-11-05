package com.excelTemplate.demo.Repository;

import com.excelTemplate.demo.Entity.OrganizationData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrganizationDataRepository extends MongoRepository<OrganizationData, String> {
}
