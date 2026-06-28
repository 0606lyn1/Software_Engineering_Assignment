# 体育场馆预约系统（全栈）

本项目包含：
- 后端：`stadium`（Java 17 + Spring Boot 3 + MyBatis-Plus + Spring Security + JWT）
- 前端：`first`（Vue 3 + Vite + Pinia + Axios + Ant Design Vue）
- 数据库：MySQL 8.4（utf8mb4）

## 目录结构

- `stadium`：后端工程
- `stadium/db.sql`：建表与样例数据
- `stadium/openapi.yaml`：OpenAPI 示例
- `first`：前端工程

## 1. 克隆项目

```bash
git clone <your-repo-url>
cd runajian
```

## 2. 导入数据库

1. 启动 MySQL 8.4。
2. 执行 SQL：

```bash
mysql -uroot -p < stadium/db.sql
```

默认数据库配置（后端 `application.yml`）：
- 数据库：`tiyu`
- 用户名：`root`
- 密码：通过环境变量 `DB_PASSWORD` 配置

如不一致，请修改 `stadium/src/main/resources/application.yml`。

## 3. 启动后端

```bash
cd stadium
set DB_PASSWORD=你的数据库密码
set JWT_SECRET=至少32位的JWT签名密钥
mvn clean install
mvn spring-boot:run
```

PowerShell 中请使用：

```powershell
$env:DB_PASSWORD="你的数据库密码"
$env:JWT_SECRET="至少32位的JWT签名密钥"
mvn spring-boot:run
```

预约临近邮件提醒使用环境变量配置 SMTP，不要把邮箱密码写入仓库：

```powershell
$env:MAIL_HOST="smtp.example.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="notice@example.com"
$env:MAIL_PASSWORD="邮箱授权码或SMTP密码"
$env:MAIL_FROM="notice@example.com"
$env:APP_PUBLIC_URL="http://localhost:5173"
```

系统会每 5 分钟扫描未来预约，并在开场前 24 小时和 2 小时生成站内消息；配置 SMTP 后同步发送邮件。

后端地址：`http://localhost:8080`

Swagger UI：`http://localhost:8080/swagger-ui/index.html`

## 4. 启动前端

```bash
cd first
npm install
npm run dev
```

前端地址：`http://localhost:5173`

## 5. 测试账号

测试账号请由管理员在用户管理中创建或向项目维护者获取，不在公开文档中写入明文密码。

## 6. 认证说明

登录后会返回 JWT Token，前端自动存储并在后续请求中通过 `Authorization: Bearer <token>` 发送。

## 7. 接口返回格式

统一 JSON：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## 8. 关键接口

- `POST /api/auth/register`：注册
- `POST /api/auth/login`：登录
- `GET /api/venues`：场馆列表
- `POST /api/reservations`：创建预约（防同用户同场地重叠）
- `GET /api/reservations`：我的预约
- `GET/POST /api/comments`：评论查询与发布

## 9. 部署到 GitHub Pages

本仓库已包含 GitHub Actions 工作流：`.github/workflows/deploy-frontend.yml`。

1. 在 GitHub 创建仓库，并把本地仓库推送到 GitHub：

```bash
git remote add github https://github.com/<your-user>/<your-repo>.git
git push github master
```

如默认分支是 `main`，请改为：

```bash
git push github main
```

2. 打开 GitHub 仓库的 `Settings -> Pages`，`Source` 选择 `GitHub Actions`。
3. 如后端已经部署到公网，在 `Settings -> Secrets and variables -> Actions -> Variables` 中新增变量：

```text
VITE_API_BASE_URL=https://<your-backend-domain>/api
```

未设置时默认使用 `/api`，适合本地开发或同域反向代理部署。

4. 推送到 `master` 或 `main` 后，Actions 会自动构建 `first` 前端并发布到 GitHub Pages。
