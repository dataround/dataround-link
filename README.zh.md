# DataRound Link

DataRound Link 是一个强大的数据集成和同步平台，能够实现不同数据库和系统之间的无缝数据传输。其最大的特点是采用**零代码、全可视化**的数据同步方式 - 无需编写配置文件或编程。用户可以通过直观的Web界面轻松配置和管理复杂的数据同步任务。

## 核心特点

- **零代码数据同步**
  - 无需编程或配置文件
  - 通过直观界面进行可视化任务配置
  - 拖拽式字段映射
  - 一键部署和执行

- **跨平台数据集成**
  - 支持多种数据库类型（MySQL、PostgreSQL、Oracle、SQL Server、TiDB等）
  - 异构数据源同步
  - 实时和批量数据传输
  - 灵活的数据转换选项

- **数据库连接管理**
  - 可视化连接测试和验证
  - 安全的凭证管理
  - 连接池优化
  - 连接健康监控

- **任务管理**
  - 可视化任务配置
  - 灵活的调度选项
  - 实时任务监控
  - 任务历史和指标

- **虚拟表管理**
  - 创建和管理虚拟表
  - 字段映射和转换
  - 数据预览和验证

- **用户界面**
  - 现代化响应式Web界面
  - 多语言支持（英文/中文）
  - 直观的导航和操作
  - 实时监控仪表板

## 项目结构

```
dataround-link/
├── dataround-link-common/     # 通用工具和共享代码
├── dataround-link-connector/  # 数据库连接器
├── dataround-link-svc/        # 后端服务
└── dataround-link-web/        # 前端应用
```

## 系统要求

- Java 17 或更高版本
- Node.js 16 或更高版本
- Maven 3.8 或更高版本
- Yarn 包管理器
- 现代网页浏览器

## 快速开始

1. 克隆仓库：
```bash
git clone https://github.com/dataround/dataround-link.git
cd dataround-link
```

2. 构建项目：
```bash
mvn clean install
```

3. 启动后端服务：
```bash
cd dataround-link-svc
mvn spring-boot:run
```

4. 启动前端开发服务器：
```bash
cd dataround-link-web
yarn install
yarn start
```

应用将在 `http://localhost:5600/datalink` 可用

## 生产环境构建

1. 构建后端：
```bash
mvn clean package -Pprod
```

2. 构建前端：
```bash
cd dataround-link-web
yarn run build
```

## 文档

详细文档请参考：
- [用户指南](docs/user-guide.md)
- [开发者指南](docs/developer-guide.md)
- [API文档](docs/api-documentation.md)

## 贡献指南

我们欢迎对DataRound Link的贡献！请查看我们的[贡献指南](CONTRIBUTING.md)了解如何提交拉取请求、报告问题和参与项目开发。

## 许可证

本项目采用GNU通用公共许可证v3.0 - 详见[LICENSE](LICENSE)文件。

## 支持

如需支持，请联系yuehan124@gmail.com或在GitHub仓库中提交issue。

## 致谢

感谢所有帮助改进DataRound Link的贡献者！ 