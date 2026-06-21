#!/bin/bash

# AnnDaan Food Delivery App - Stop Script
set -e

echo "==========================================="
echo "   AnnDaan Backend - Stopping Services"
echo "==========================================="

if ! docker info > /dev/null 2>&1; then
    echo "WARNING: Docker daemon is not running. Using fallback stop..."
fi

# Stop and remove containers and volumes
docker compose down -v

echo ""
echo "==========================================="
echo "   All services stopped and volumes cleaned!"
echo "==========================================="
