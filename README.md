# MedicalDiagnosis

这是一个基于Spring Boot的医疗诊断AI应用项目。

## 主要功能

- **用户管理**：用户注册、登录、认证和会话管理。
- **管理员功能**：管理员登录、用户管理、系统统计和监控。
- **AI诊断服务**：
  - 聊天代理：与用户进行智能对话。
  - 分类代理：对医疗问题进行分类。
  - 诊断总结：生成诊断报告总结。
  - 图像分析：分析医疗图像以辅助诊断。
- **检测记录管理**：存储、查询和更新医疗检测记录。
- **统计服务**：提供系统使用统计和数据分析。
- **通用服务**：公共API和工具函数。

## 项目结构

```
pom.xml
README.md
src/
    main/
        java/
            com/
                ai/
                    medical_diagnosis/
                        MedicalDiagnosisApplication.java
                        Config/
                            AgentConfig.java
                            ChatModelConfig.java
                            InitVectorDatabaseConfig.java
                            MvcConfig.java
                            OssConfiguration.java
                            RedisConfig.java
                            RedisSaverConfig.java
                            SwaggerConfig.java
                            VectorStoreConfig.java
                        constants/
                            AiConstants.java
                            PythonConstants.java
                            RedisConstants.java
                        controller/
                            admin/
                                ...
                            common/
                            user/
                        domain/
                            dto/
                            po/
                            vo/
                        interceptor/
                            AdminLoginInterceptor.java
                            AdminLoginTtlInterceptor.java
                            UserLoginInterceptor.java
                            UserLoginTtlInterceptor.java
                        json/
                            JacksonObjectMapper.java
                        mapper/
                            AdminMapper.java
                            AiChatMapper.java
                            DetectionRecordMapper.java
                            UserMapper.java
                        properties/
                            AliOssProperties.java
                            ChatModelProperties.java
                            RedisProperties.java
                        result/
                            PageResult.java
                            Result.java
                        service/
                            AdminService.java
                            AgentService.java
                            AIService.java
                            CommonService.java
                            EnhanceService.java
                            StatisticsService.java
                            UserService.java
                            impl/
                        utils/
                            AdminHolder.java
                            AliOssUtil.java
                            ImageUtils.java
                            PythonHttpClient.java
                            ResourceUtils.java
                            TextUtils.java
                            ...
        resources/
            application.yml
            aiResourcesText/
                instruction/
                    ChatAgent.txt
                    ClassificationAgent.txt
                    DiagnosisSummaryAgent.txt
                    ImageAnalysisAgent.txt
                    SearchAgent.txt
                ragText/
                    RAGOrgText.txt
                system/
                    chatAgentSystem.txt
                    ClassificationAgentSystem.txt
                    diagnosisSystem.txt
            mapper/
                AdminMapper.xml
                AiChatMapper.xml
                DetectionRecordMapper.xml
                UserMapper.xml
    test/
        java/
            com/
                ai/
                    medical_diagnosis/
                        MedicalDiagnosisApplicationTests.java
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
