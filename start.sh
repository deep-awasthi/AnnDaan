#!/bin/bash

# AnnDaan Food Delivery App - Start Script
# Exit immediately if a command exits with a non-zero status
set -e

echo "==========================================="
echo "   AnnDaan Backend - Container Bootstrapping"
echo "==========================================="

# Check if Docker daemon is running
if ! docker info > /dev/null 2>&1; then
    echo "ERROR: Docker daemon is not running."
    echo "Please start Docker Desktop on your macOS system and try again."
    exit 1
fi

echo "Docker daemon detected. Building and starting services..."

# Build and start services in background
docker compose up --build -d

echo ""
echo "==========================================="
echo "   AnnDaan Backend is starting up!"
echo "==========================================="
echo "Backend endpoint URL: http://localhost:8088"
echo "Database URL: jdbc:postgresql://localhost:5432/anndaan"
echo ""
echo "To view live logs, run:"
echo "  docker compose logs -f backend"
echo ""
echo "To view database logs, run:"
echo "  docker compose logs -f db"
echo ""
echo "To stop all services and wipe database data, run:"
echo "  ./stop.sh"
echo "==========================================="
