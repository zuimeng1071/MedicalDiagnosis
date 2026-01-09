# MedicalDiagnosis
# Medical Diagnosis Application

## 项目描述

这是一个基于Spring Boot的医疗诊断应用，利用AI技术提供智能医疗诊断服务。系统集成了多种AI代理，包括聊天代理、分类代理、诊断总结代理、图像分析代理和搜索代理，支持用户通过文本和图像进行医疗咨询和诊断。

## 主要功能

- **用户管理**: 用户注册、登录、身份验证
- **管理员管理**: 管理员登录、统计数据查看
- **AI诊断服务**:
  - 文本聊天诊断
  - 图像分析诊断
  - 疾病分类
  - 诊断总结
  - 智能搜索
- **文件上传**: 支持图像文件上传到阿里云OSS
- **数据持久化**: 使用MySQL数据库和Redis缓存
- **API文档**: 集成Swagger UI提供API文档

## 技术栈

- **后端框架**: Spring Boot 2.x
- **数据库**: MySQL
- **缓存**: Redis
- **ORM**: MyBatis
- **对象存储**: 阿里云OSS
- **AI集成**: OpenAI API / 自定义AI代理
- **Python集成**: HTTP客户端调用Python服务
- **前端交互**: RESTful API
- **文档**: Swagger
- **构建工具**: Maven

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/ai/medical_diagnosis/
│   │       ├── MedicalDiagnosisApplication.java          # 主启动类
│   │       ├── Config/                                    # 配置类
│   │       │   ├── AgentConfig.java
│   │       │   ├── ChatModelConfig.java
│   │       │   ├── InitVectorDatabaseConfig.java
│   │       │   ├── MvcConfig.java
│   │       │   ├── OssConfiguration.java
│   │       │   ├── RedisConfig.java
│   │       │   ├── RedisSaverConfig.java
│   │       │   ├── SwaggerConfig.java
│   │       │   └── VectorStoreConfig.java
│   │       ├── constants/                                 # 常量类
│   │       │   ├── AiConstants.java
│   │       │   ├── PythonConstants.java
│   │       │   └── RedisConstants.java
│   │       ├── controller/                                # 控制器
│   │       │   ├── admin/                                 # 管理员接口
│   │       │   ├── common/                                # 公共接口
│   │       │   └── user/                                  # 用户接口
│   │       ├── domain/                                    # 领域对象
│   │       │   ├── dto/                                   # 数据传输对象
│   │       │   ├── po/                                    # 持久化对象
│   │       │   └── vo/                                    # 视图对象
│   │       ├── interceptor/                               # 拦截器
│   │       ├── json/                                      # JSON配置
│   │       ├── mapper/                                    # MyBatis映射器
│   │       ├── properties/                                # 配置属性
│   │       ├── result/                                    # 响应结果
│   │       ├── service/                                   # 服务层
│   │       └── utils/                                     # 工具类
│   └── resources/
│       ├── application.yml                                # 应用配置
│       ├── aiResourcesText/                               # AI资源文本
│       │   ├── instruction/                               # 指令文件
│       │   ├── ragText/                                   # RAG文本
│       │   └── system/                                    # 系统提示
│       └── mapper/                                        # MyBatis XML映射
└── test/
    └── java/
        └── com/ai/medical_diagnosis/
            └── MedicalDiagnosisApplicationTests.java     # 测试类
```

## 环境要求

- Java 8 或更高版本
- Maven 3.x
- MySQL 5.7+
- Redis 5.x+
- Python 3.x (用于AI服务)

## 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd Medical_diagnosis
   ```

2. **配置数据库**
   - 创建MySQL数据库
   - 执行SQL脚本创建表结构（如果有）

3. **配置Redis**
   - 安装并启动Redis服务

4. **配置阿里云OSS**
   - 在阿里云控制台创建OSS存储桶
   - 获取AccessKey ID和AccessKey Secret

5. **配置AI服务**
   - 设置OpenAI API密钥或其他AI服务配置
   - 确保Python服务可用

## 配置说明

编辑 `src/main/resources/application.yml` 文件，配置以下内容：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medical_diagnosis
    username: your_username
    password: your_password

redis:
  host: localhost
  port: 6379

aliyun:
  oss:
    endpoint: your_oss_endpoint
    access-key-id: your_access_key_id
    access-key-secret: your_access_key_secret
    bucket-name: your_bucket_name

ai:
  openai:
    api-key: your_openai_api_key
  python:
    service-url: http://localhost:5000
```
