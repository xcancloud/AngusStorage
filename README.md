# AngusStorage

[English](README.md) | [中文](README_zh.md)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-4.2.0-brightgreen)](https://spring.io/projects/spring-cloud)
[![Eureka Client](https://img.shields.io/badge/Eureka%20Client-2.0.4-lightgrey)](https://spring.io/projects/spring-cloud-netflix)
[![Angus Infra](https://img.shields.io/badge/Angus%20Infra-1.0.0-red)](https://github.com/xcancloud/AngusInfra)
[![Open API](https://img.shields.io/badge/Open%20API-3.0.1-blue)](https://swagger.io/specification/)

**AngusStorage** is an enterprise-grade unified file storage service designed for hybrid cloud
environments. Through standardized RESTFul APIs and granular governance tools, it delivers secure,
scalable, and business-aligned storage management capabilities, particularly suited for multi-tenant
SaaS applications.

## 🚀 Key Features

- **Unified File Storage Gateway**
    - ***Centralized Management***: Manage multi-backend storage (S3-compatible OSS/local disks)
      through a single entrypoint, eliminating tool fragmentation and reducing operational
      complexity.
    - ***Granular Security Controls***: Support multi-dimensional security configurations, access
      policies, and audit trails.
    - ***Compliance Simplified***: Meet security and compliance audit requirements through access
      controls and system logs.
- **Multi-Tenancy Support**: Tenant-level data isolation and quota management.
- **Flexible Storage Backends**: Choose backend storage solutions based on business needs and system
  complexity.
- **High-Performance Transfers**: Resumeable downloads with breakpoint continuation.

## 🚀 Core Functionalities

| Functionality               | Description                                                                                                   |  
|-----------------------------|---------------------------------------------------------------------------------------------------------------|  
| **Storage Configuration**   | Manage storage backend settings (S3-compatible object storage/local disks) with multi-node file auto-routing. |  
| **Bucket Management**       | Create, configure, and manage buckets with namespace isolation and permission controls.                       |  
| **Business Bucket Mapping** | Map buckets to business units for data categorization and maintenance workflows.                              |  
| **Space Management**        | Granular space quota allocation and access policy enforcement.                                                |  
| **Space Authorization**     | Role-based space-level access control (users/departments/groups).                                             |  
| **Object Management**       | CRUD operations and metadata management for files/directories within spaces.                                  |  
| **Share Management**        | Secure cross-tenant/user file sharing with link expiration and password protection.                           |  
| **File Access**             | Unified multi-protocol (HTTP/S3) upload/download interfaces.                                                  |  

## Angus Series Application Initialization Data

- **Default Initial Buckets**
    - ***xcan-angustester***: Stores test-generated or user-uploaded data files for AngusTester.
    - ***xcan-baseapp***: Base application data (e.g., user avatars, task attachments, images).

- ***Common Issues***
    - Invalid bucket name format (e.g., uppercase letters are not allowed!).
    - 403 errors when creating buckets on Alibaba Cloud OSS due to name conflicts with other
      accounts!
    - Configuration changes not taking effect? Verify updates in `storage_setting` – its priority
      overrides general config files!

> 💡 **Note**: For private deployments, configure storage settings to choose between local storage or
> AWS-compatible OSS. If using object storage:
> 1. Create buckets in the object storage service.
> 2. Update corresponding bucket names in `bucket` and `bucket_biz_config`.
> 3. For simplicity, use a single bucket during initial private deployment.

## License

📜 Licensed under [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html).
