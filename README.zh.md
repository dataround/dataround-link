# Dataround link

Dataround link 是一个**开源数据集成工具**，专为多源异构数据同步而设计。它支持**结构化、半结构化和非结构化数据**的无缝集成和同步。

Dataround link 采用**零代码可视化**设计理念，无需配置文件或编程。用户可以通过直观的Web界面轻松配置和管理复杂的数据同步任务，真正实现数据同步的民主化。

## 核心特性

- **多源异构支持** - 统一支持结构化、半结构化和非结构化数据源
- **结构化数据支持** - 传统关系型数据库（MySQL、PostgreSQL、Oracle、SQL Server、TiDB等）
- **半结构化数据支持** - MongoDB、Kafka、JSON、XML等
- **非结构化数据支持** - 图片、视频、模型文件、文档和其他文件类型
- **零代码可视化界面** - 无需配置文件或编程，纯可视化操作
- **虚拟表支持** - 通过虚拟表将来自Kafka、Redis等的半结构化数据映射为结构化数据，实现与关系型数据库的同步
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

3. 初始化数据库：
- 安装PostgreSQL并创建数据库：
```sql
CREATE DATABASE dataround_link;
```
- 运行初始化脚本创建表：
```bash
psql -d dataround_link -f $DATAROUND_HOME/conf/init_pg_schema.sql
```

4. 启动 dataround link 服务器：

修改 `$DATAROUND_HOME/conf/application.yaml` 中的数据库IP、名称和密码，启动服务：
```bash
./bin/start.sh
```

应用程序将在 `http://localhost:5600/datalink` 上可用

## 文档

详细文档请参考以下内容：
- [用户指南](docs/user-guide.md)
- [开发者指南](docs/developer-guide.md)
- [API文档](docs/api-documentation.md)

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