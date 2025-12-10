# Dataround link

Dataround link 是一个**开源数据集成工具**，专为多源异构数据同步而设计。它支持**结构化、半结构化和非结构化数据**的无缝集成和同步。

Dataround link 采用**零代码可视化**设计理念，无需配置文件或编程。用户可以通过直观的Web界面轻松配置和管理复杂的数据同步任务，真正实现数据同步的民主化。

## 核心特性

- **多源异构支持** - 统一支持结构化、半结构化和非结构化数据源
- **结构化数据支持** - 传统关系型数据库（MySQL、PostgreSQL、Oracle、SQL Server、TiDB等）
- **半结构化数据支持** - Kafka、JSON等
- **非结构化数据支持** - 图片、视频、模型文件、文档和其他文件类型
- **零代码可视化界面** - 无需配置文件或编程，纯可视化操作
- **虚拟表支持** - 通过虚拟表将来自Kafka、JSON等的半结构化数据映射为结构化数据，实现与关系型数据库的同步
- **基于SeaTunnel** - Dataround link表数据同步功能基于Apache SeaTunnel实现，提供稳定可靠的数据传输能力

## 产品截图

### 作业管理
![作业管理](docs/imgs/joblist.png)

### 表映射
![表映射](docs/imgs/tablemapping.png)


## 从源码构建 dataround link

构建 Dataround 的先决条件：

- Java 17 或更高版本
- Maven 3.8 或更高版本

1. 克隆仓库：
```bash
git clone https://github.com/dataround/dataround-link.git
```

2. 构建后端：
```bash
cd dataround-link
mvn clean package -DskipTests
```

最终程序包将生成在 `dataround-link-svc/target/dataround-link-xxx.tar.gz`


## 部署

### 数据库初始化

Dataround link 支持 PostgreSQL 和 H2 数据库：

**选项 1: 使用 PostgreSQL (默认)**
- 安装PostgreSQL并创建数据库：
```sql
CREATE DATABASE dataround_link;
```
- 表会在启动时自动创建

**选项 2: 使用 H2 (用于开发/测试)**
- 无需安装，H2 已配置为将数据持久化保存至本地文件
- 表会在启动时自动创建

### 部署选项

#### 1. Docker 部署

**使用默认 H2 数据库:**
```bash
docker run -p 5600:5600 -d dataround/link:0.9.0
```

**使用外部 PostgreSQL 数据库:**
```bash
docker run -e SPRING_PROFILES_ACTIVE=prod \
  -e DATASOURCE_URL=jdbc:postgresql://your-postgres-host:5432/dataround_link \
  -e DATASOURCE_USERNAME=your_username \
  -e DATASOURCE_PASSWORD=your_password \
  -p 5600:5600 -d \
  dataround/link:0.9.0
```

#### 2. Docker Compose 部署

创建并启动服务：
```bash
docker-compose up -d
```

这将启动 PostgreSQL 和 Dataround Link 服务并进行适当配置。

#### 3. Kubernetes 部署

部署到 Kubernetes 集群：
```bash
kubectl apply -f kubernetes.yml
```

这将创建：
- 带持久化存储的 PostgreSQL 部署
- 具有2个副本的 Dataround Link 部署
- 两个组件的服务
- 用于配置的 ConfigMaps 和 Secrets

#### 4. 手动部署

启动 dataroud link 服务器：

**对于 PostgreSQL (生产环境):**
通过环境变量或修改 `$DATAROUND_HOME/conf/application-prod.yaml` 中的数据库配置，然后启动服务：
```bash
./bin/start.sh
```

**对于 H2 数据库 (测试/开发环境):**
```bash
./bin/start.sh --spring.profiles.active=test
```

应用程序将在 `http://localhost:5600/datalink` 上可用


## 文档

详细文档请参考以下内容：
- [快速指南](https://dataround.io/quickstart/)
- [API文档](https://dataround.io/)

## 贡献

我们欢迎对 Dataround link 的贡献！请查看我们的[贡献指南](CONTRIBUTING.md)，了解如何提交拉取请求、报告问题和为项目做出贡献的详细信息。

## 许可证

本项目采用 GNU General Public License v3.0 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件。

## 支持

如果您有任何疑问或建议，我们建议您：

1. **GitHub Discussions** - 在我们的 [GitHub Discussions](https://github.com/dataround/dataround-link/discussions) 中分享您的想法并获得社区帮助
2. **直接联系** - 扫描下方二维码加入我们的微信群（加入时请注明"dataround"）

    ![微信二维码](docs/imgs/author_wechat.png)

## 致谢

感谢所有帮助使 Dataround link 变得更好的贡献者！