# MedicalDiagnosis

这是一个基于Spring Boot的医疗诊断AI应用项目。

## 主要功能

- **用户管理**：用户注册、登录、认证和会话管理。
- **管理员功能**：管理员登录、用户管理、系统统计和监控。
- **AI诊断服务**：
  - 聊天代理：与用户进行智能对话。
  - 图像分析：分析医疗图像以辅助诊断。
- **检测记录管理**：存储、查询和更新医疗检测记录。
- **通用服务**：公共API和工具函数。

## 项目结构

```
MedicalDiagnosisProject
│
├── pom.xml                      # Maven项目的配置文件，定义了项目依赖和构建配置。
├── README.md                    # 项目说明文档，包含项目概述、使用方法等信息。
│
└── src/
    ├── main/
    │   ├── java/                # 存放Java源代码的目录。
    │   │   └── com/ai/medical_diagnosis/
    │   │       ├── MedicalDiagnosisApplication.java          # Spring Boot应用启动类。
    │   │       ├── Config/                                   # 配置类目录，包含各种系统配置。
    │   │       ├── constants/                                # 常量定义目录。
    │   │       ├── controller/                               # 控制器目录，处理HTTP请求。
    │   │       ├── domain/dto, po, vo                        # 数据传输对象、持久化对象、视图对象目录。
    │   │       ├── interceptor/                              # 拦截器目录，用于拦截请求做预处理。
    │   │       ├── json/                                     # JSON处理相关类目录。
    │   │       ├── mapper/                                   # MyBatis Mapper接口目录。
    │   │       ├── properties/                               # 属性配置类目录。
    │   │       ├── result/                                   # 结果封装类目录。
    │   │       ├── service/, impl/                           # 服务层接口及其实现目录。
    │   │       └── utils/                                    # 工具类目录。
    │   └── resources/                                        # 资源文件目录。
    │       ├── application.yml                               # Spring Boot配置文件。
    │       ├── aiResourcesText/                              # AI资源文本目录，包括指令、RAG文本、系统文本等。
    │       └── mapper/                                       # MyBatis XML映射文件目录。
    └── test/                                                 # 测试代码目录。
        └── java/                                             # 存放测试用Java源代码。
            └── com/ai/medical_diagnosis/
                └── MedicalDiagnosisApplicationTests.java     # 测试类，用于单元测试或集成测试。
```

## 技术栈

- Spring Boot 3.5.9
- Java 17
- MySQL
- Redis
- AI集成（qwen3）
## 其他代码
- python：https://github.com/zuimeng1071/MedicalDiagnosisPython.git
- 前端：https://github.com/wysaly/MedicalModel.git
