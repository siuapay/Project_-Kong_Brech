#!/bin/bash

# Azure Setup Script for Spring Boot Application
# Run this script to create all necessary Azure resources

# Variables - Update these with your values
RESOURCE_GROUP="rg-branch-demo"
LOCATION="Southeast Asia"
APP_NAME="branch-demo-app-$(date +%s)"  # Add timestamp for uniqueness
SQL_SERVER_NAME="branch-demo-sql-$(date +%s)"  # Add timestamp for uniqueness
SQL_DATABASE_NAME="branchdb"
SQL_ADMIN_USER="sqladmin"
SQL_ADMIN_PASSWORD="BranchDemo2024!"  # Strong password
STORAGE_ACCOUNT="branchstorage$(date +%s | tail -c 6)"  # Must be lowercase, no special chars
APP_SERVICE_PLAN="branch-demo-plan"

echo "🚀 Starting Azure setup for Branch Demo Application..."

# Login to Azure (if not already logged in)
echo "📝 Please login to Azure..."
az login

# Create Resource Group
echo "📦 Creating Resource Group..."
az group create --name $RESOURCE_GROUP --location "$LOCATION"

# Create App Service Plan
echo "🏗️ Creating App Service Plan..."
az appservice plan create \
  --name $APP_SERVICE_PLAN \
  --resource-group $RESOURCE_GROUP \
  --location "$LOCATION" \
  --sku S2 \
  --is-linux

# Create Web App
echo "🌐 Creating Web App..."
az webapp create \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --plan $APP_SERVICE_PLAN \
  --runtime "JAVA:17-java17"

# Create SQL Server
echo "🗄️ Creating SQL Server..."
az sql server create \
  --name $SQL_SERVER_NAME \
  --resource-group $RESOURCE_GROUP \
  --location "$LOCATION" \
  --admin-user $SQL_ADMIN_USER \
  --admin-password $SQL_ADMIN_PASSWORD

# Create SQL Database
echo "💾 Creating SQL Database..."
az sql db create \
  --name $SQL_DATABASE_NAME \
  --server $SQL_SERVER_NAME \
  --resource-group $RESOURCE_GROUP \
  --service-objective S2

# Configure SQL Server Firewall (Allow Azure services)
echo "🔥 Configuring SQL Server Firewall..."
az sql server firewall-rule create \
  --name "AllowAzureServices" \
  --server $SQL_SERVER_NAME \
  --resource-group $RESOURCE_GROUP \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0

# Create Storage Account
echo "📁 Creating Storage Account..."
az storage account create \
  --name $STORAGE_ACCOUNT \
  --resource-group $RESOURCE_GROUP \
  --location "$LOCATION" \
  --sku Standard_LRS \
  --kind StorageV2

# Create Storage Container
echo "📂 Creating Storage Container..."
az storage container create \
  --name "uploads" \
  --account-name $STORAGE_ACCOUNT \
  --public-access blob

# Get connection strings
echo "🔗 Getting connection strings..."
SQL_CONNECTION_STRING=$(az sql db show-connection-string \
  --name $SQL_DATABASE_NAME \
  --server $SQL_SERVER_NAME \
  --client jdbc \
  --output tsv)

STORAGE_CONNECTION_STRING=$(az storage account show-connection-string \
  --name $STORAGE_ACCOUNT \
  --resource-group $RESOURCE_GROUP \
  --output tsv)

# Replace placeholders in connection string
SQL_CONNECTION_STRING=${SQL_CONNECTION_STRING//<username>/$SQL_ADMIN_USER}
SQL_CONNECTION_STRING=${SQL_CONNECTION_STRING//<password>/$SQL_ADMIN_PASSWORD}

# Configure Web App Settings
echo "⚙️ Configuring Web App Settings..."
az webapp config appsettings set \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --settings \
    SPRING_PROFILES_ACTIVE="azure" \
    AZURE_SQL_CONNECTION_STRING="$SQL_CONNECTION_STRING" \
    AZURE_SQL_USERNAME="$SQL_ADMIN_USER" \
    AZURE_SQL_PASSWORD="$SQL_ADMIN_PASSWORD" \
    AZURE_STORAGE_CONNECTION_STRING="$STORAGE_CONNECTION_STRING"

# Enable HTTPS Only
echo "🔒 Enabling HTTPS Only..."
az webapp update \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --https-only true

echo "✅ Azure setup completed successfully!"
echo ""
echo "📋 Summary:"
echo "Resource Group: $RESOURCE_GROUP"
echo "Web App: https://$APP_NAME.azurewebsites.net"
echo "SQL Server: $SQL_SERVER_NAME.database.windows.net"
echo "Database: $SQL_DATABASE_NAME"
echo "Storage Account: $STORAGE_ACCOUNT"
echo ""
echo "🔑 Next steps:"
echo "1. Build your application: mvn clean package"
echo "2. Deploy using: az webapp deploy --name $APP_NAME --resource-group $RESOURCE_GROUP --src-path target/*.jar"
echo "3. Or setup GitHub Actions for automatic deployment"