#!/bin/bash
# Démarrer la stack DevOps complète

echo "🚀 Démarrage de la stack DevOps..."
docker-compose -f docker-compose.yml up -d

echo "⏳ Attente du démarrage des services..."
sleep 10

echo "✅ Services lancés:"
echo "  - Jenkins: http://localhost:8080"
echo "  - SonarQube: http://localhost:9000 (admin/admin)"
echo "  - Grafana: http://localhost:3000 (admin/admin)"
echo "  - Prometheus: http://localhost:9090"
echo "  - MongoDB: mongodb://admin:admin123@localhost:27017"

echo ""
echo "📋 Logs Jenkins:"
docker-compose logs -f jenkins
