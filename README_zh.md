# AngusStorage

[English](README.md) | [中文](README_zh.md)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-4.2.0-brightgreen)](https://spring.io/projects/spring-cloud)
[![Eureka Client](https://img.shields.io/badge/Eureka%20Client-2.0.4-lightgrey)](https://spring.io/projects/spring-cloud-netflix)
[![Angus Infra](https://img.shields.io/badge/Angus%20Infra-1.0.0-red)](https://github.com/xcancloud/AngusInfra)
[![Open API](https://img.shields.io/badge/Open%20API-3.0.1-blue)](https://swagger.io/specification/)

**AngusStorage** 是面向混合云环境设计的企业级统一文件存储服务。通过标准化 RESTFul API 和精细化治理工具，提供安全、可扩展且贴合业务需求的存储管理能力，尤其适合多租户 SaaS 应用。

## 🚀 核心特点

- **统一文件存储入口**
  - ***集中化管理***: 通过单一入口管理多后端存储（兼容 S3 协议的对象存储/本地磁盘），消除工具碎片化，降低运维复杂度。
  - ***精细化安全管控***: 支持多维度的安全配置、访问策略和审计追踪。
  - ***合规性简化***: 通过访问控制和系统日志满足安全与合规审计要求。
- **多租户支持**：租户级数据隔离与配额管理。
- **灵活存储后端**：根据业务需求和系统复杂度选择存储方案。
- **高性能传输**：支持断点续传的下载功能。

## 🚀 核心功能

| 功能模块         | 描述                                                                 |  
|------------------|--------------------------------------------------------------------|  
| **存储配置**       | 管理存储后端设置（兼容 S3 协议的对象存储/本地磁盘），支持多节点文件自动路由定位。        |  
| **桶管理**        | 创建、配置和管理存储桶，实现命名空间隔离与权限控制。                              |  
| **业务桶映射**     | 将存储桶映射至业务单元，实现数据分类和维护流程管理。                              |  
| **空间管理**       | 细粒度存储空间配额分配与访问策略实施。                                      |  
| **空间授权**       | 基于角色的空间级权限控制（用户/部门/用户组）。                                |  
| **对象管理**       | 空间内文件/目录的增删改查（CRUD）与元数据管理。                              |  
| **分享管理**       | 支持跨租户/用户的安全文件共享（含链接时效和密码保护）。                           |  
| **文件访问**       | 统一多协议（HTTP/S3）文件上传下载接口。                                   |  

## Angus 系列应用初始化数据

- **默认初始化桶**
  - ***xcan-angustester***: 存储 AngusTester 测试生成或用户上传的数据文件。
  - ***xcan-baseapp***: 基础应用数据（如用户头像、任务附件、图片等）。

- ***常见问题***
  - 桶名格式错误（例如：不支持大写字母！）。
  - 在阿里云 OSS 创建桶时，名称被其他账号占用会返回 403 错误！
  - 本地配置修改未生效？请检查 `storage_setting` 中的配置是否更新——其优先级高于通用配置文件！

> 💡 **注意**: 私有化部署时需配置存储设置，选择本地存储或 AWS 兼容的对象存储。若选择对象存储：
> 1. 在对象存储服务中创建桶。
> 2. 更新 `bucket` 和 `bucket_biz_config` 中的对应桶名。
> 3. 为简化流程，初次部署时可合并使用单一桶。

## 开源协议

📜 本项目采用 [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html) 开源协议。
