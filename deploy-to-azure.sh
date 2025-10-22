#!/bin/bash

# Manual Deployment Script to Azure
# Run this after setup-azure.sh to deploy your application

# Variables - Update these to match your setup-azure.sh values
RESOURCE_GROUP="rg-branch-demo"
APP_NAME="branch-demo-app"

echo "🚀 Deploying Branch Demo Application to Azure..."

# Build the application
echo "🔨 Building application..."
mvn clean package -DskipTests

# Check if JAR file exists
JAR_FILE=$(find target -name "*.jar" -not -name "*sources.jar" | head -1)
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR file not found in target directory"
    exit 1
fi

echo "📦 Found JAR file: $JAR_FILE"

# Deploy to Azure
echo "🌐 Deploying to Azure Web App..."
az webapp deploy \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --src-path "$JAR_FILE" \
  --type jar

# Restart the web app to ensure new deployment is loaded
echo "🔄 Restarting Web App..."
az webapp restart \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP

# Show deployment status
echo "📊 Checking deployment status..."
az webapp show \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --query "defaultHostName" \
  --output tsv

echo "✅ Deployment completed!"
echo "🌐 Your application is available at: https://$APP_NAME.azurewebsites.net"
echo ""
echo "📋 Useful commands:"
echo "View logs: az webapp log tail --name $APP_NAME --resource-group $RESOURCE_GROUP"
echo "SSH into container: az webapp ssh --name $APP_NAME --resource-group $RESOURCE_GROUP"