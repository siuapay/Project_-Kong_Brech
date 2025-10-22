# Azure Deployment Guide

## Hướng dẫn deploy Spring Boot application lên Azure

### Yêu cầu trước khi bắt đầu

1. **Azure CLI**: Cài đặt Azure CLI
   ```bash
   # Windows
   winget install Microsoft.AzureCLI
   
   # macOS
   brew install azure-cli
   
   # Linux
   curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
   ```

2. **Java 17**: Đảm bảo có Java 17
3. **Maven**: Để build project
4. **Azure Account**: Tài khoản Azure với subscription

### Bước 1: Setup Azure Resources

1. **Chạy script setup**:
   ```bash
   chmod +x setup-azure.sh
   ./setup-azure.sh
   ```

2. **Cập nhật thông tin trong script**:
   - Mở `setup-azure.sh`
   - Thay đổi các biến:
     ```bash
     APP_NAME="your-unique-app-name"
     SQL_SERVER_NAME="your-unique-sql-server"
     SQL_ADMIN_PASSWORD="YourSecurePassword123!"
     ```

### Bước 2: Deploy Application

1. **Build và deploy**:
   ```bash
   chmod +x deploy-to-azure.sh
   ./deploy-to-azure.sh
   ```

2. **Hoặc deploy manual**:
   ```bash
   mvn clean package -DskipTests
   az webapp deploy --name your-app-name --resource-group rg-branch-demo --src-path target/*.jar
   ```

### Bước 3: Setup Database

1. **Connect to Azure SQL**:
   - Sử dụng SQL Server Management Studio
   - Connection string từ Azure Portal
   - Import database schema

2. **Run migration scripts**:
   ```sql
   -- Tạo tables từ JPA entities
   -- Import data nếu cần
   ```

### Bước 4: Configure Domain (Optional)

1. **Add custom domain**:
   ```bash
   az webapp config hostname add --webapp-name your-app-name --resource-group rg-branch-demo --hostname yourdomain.com
   ```

2. **Setup SSL certificate**:
   ```bash
   az webapp config ssl bind --certificate-thumbprint {thumbprint} --ssl-type SNI --name your-app-name --resource-group rg-branch-demo
   ```

### Monitoring và Troubleshooting

1. **View logs**:
   ```bash
   az webapp log tail --name your-app-name --resource-group rg-branch-demo
   ```

2. **SSH into container**:
   ```bash
   az webapp ssh --name your-app-name --resource-group rg-branch-demo
   ```

3. **Check application settings**:
   ```bash
   az webapp config appsettings list --name your-app-name --resource-group rg-branch-demo
   ```

### Cost Estimation

- **App Service Plan S2**: ~$73/month
- **Azure SQL Database S2**: ~$30/month
- **Storage Account**: ~$2/month
- **Total**: ~$105/month

### Security Best Practices

1. **Enable HTTPS only** (đã config trong script)
2. **Configure SQL firewall rules**
3. **Use managed identity** cho production
4. **Enable Application Insights** cho monitoring
5. **Setup backup strategy**

### Automatic Deployment với GitHub Actions

1. **Setup GitHub repository**
2. **Add secrets**:
   - `AZURE_WEBAPP_PUBLISH_PROFILE`
3. **Copy `azure-deploy.yml` to `.github/workflows/`**
4. **Push code để trigger deployment**

### Support

Nếu gặp vấn đề:
1. Check Azure Portal logs
2. Verify connection strings
3. Check firewall rules
4. Contact Azure support nếu cần

---

**Lưu ý**: Thay đổi tất cả placeholder values trước khi chạy scripts!