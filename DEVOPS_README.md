# 🎮 Streetleague - CI/CD DevOps Pipeline

**Projet PI 2026** | Angular 21 + Spring Boot 4 + MongoDB + Jenkins + Kubernetes

---

## 🚀 Quick Start

### Local Development (without Docker)
```bash
# Backend
cd back && mvn clean install package && mvn spring-boot:run

# Frontend (new terminal)
cd front/streetleague && npm install && npm start
```

### Complete Stack (with DevOps)
```bash
# Start Jenkins + SonarQube + Grafana + MongoDB
docker-compose up -d

# Access:
# - Jenkins: http://localhost:8080
# - SonarQube: http://localhost:9000 (admin/admin)
# - Grafana: http://localhost:3000 (admin/admin)
# - MongoDB: mongodb://admin:admin123@localhost:27017
```

---

## 📁 Project Structure

```
├── back/                       # Spring Boot Backend
│   ├── pom.xml                # Maven build config
│   ├── Dockerfile             # Multi-stage Maven build
│   └── src/main/java/...
│
├── front/                      # Angular Frontend
│   ├── streetleague/
│   ├── Dockerfile             # Node + Nginx multi-stage
│   ├── nginx.conf             # SPA routing config
│   └── package.json
│
├── k8s/                        # Kubernetes Manifests
│   ├── deployment.yaml        # Backend + Frontend
│   ├── config.yaml            # ConfigMap, Secrets, PDB
│   └── ...
│
├── Jenkinsfile               # CI/CD Pipeline (Maven → SonarQube → Docker → K8s)
├── docker-compose.yml        # Full DevOps stack
├── prometheus.yml            # Metrics configuration
├── DEPLOYMENT.md             # Complete deployment guide
└── scripts/                  # Helper scripts
    ├── start-devops.sh
    └── start-devops.bat
```

---

## 📋 Prerequisites

### Development
- Java 17+ (Maven 3.9+)
- Node.js 18+ (npm)
- Git
- Docker & Docker Compose

### Production (K8s)
- Kubernetes cluster (AKS/GKE/EKS)
- kubectl configured
- GitHub Container Registry account

---

## 🔧 Development Workflow

### 1. Maven Commands (Backend)
```bash
cd back
mvn clean              # Clean build
mvn install            # Download deps
mvn package            # Build JAR
mvn test               # Run tests
mvn sonar:sonar        # Code analysis
```

### 2. Git Workflow
```bash
git checkout -b feature/my-feature
# ... edit code
git add . && git commit -m "feat: description"
git push origin feature/my-feature
# → Create PR on GitHub
```

### 3. Build & Test Locally
```bash
# Backend
cd back && mvn clean package

# Frontend
cd front/streetleague && npm ci && npm run build

# Docker
cd back && docker build -t streetleague-backend .
cd front && docker build -t streetleague-frontend .
```

---

## 🔄 CI/CD Pipeline (Jenkins)

### Stages Automatiques
1. **Checkout** → Clone git repository
2. **Backend: Maven Build** → Compile + Package
3. **Backend: Unit Tests** → mvn test
4. **Backend: SonarQube Scan** → Code quality check
5. **Frontend: Build** → npm run build
6. **Frontend: Tests** → npm run test
7. **Docker: Build & Push** → Create container images
8. **Kubernetes: Deploy** → Update production (main branch only)
9. **Verify Deployment** → Health checks

### Configure Jenkins
```bash
# 1. Create GitHub credentials in Jenkins
#    Manage Jenkins → Credentials
#    Add: DOCKER_HUB_USERNAME, DOCKER_HUB_PASSWORD, sonarqube-token

# 2. Create Pipeline Job
#    New Item → Pipeline
#    Pipeline script from SCM → Git → Jenkinsfile

# 3. Add GitHub Webhook
#    GitHub Repo Settings → Webhooks → http://jenkins:8080/github-webhook/
```

---

## 🐳 Docker Deployment

### Build & Push
```bash
# Backend
cd back
docker build -t ghcr.io/username/streetleague-backend:v1.0 .
docker push ghcr.io/username/streetleague-backend:v1.0

# Frontend
cd front
docker build -t ghcr.io/username/streetleague-frontend:v1.0 .
docker push ghcr.io/username/streetleague-frontend:v1.0
```

### Docker Compose (Local Testing)
```bash
docker-compose up -d
# Services: Jenkins, SonarQube, MongoDB, Prometheus, Grafana
```

---

## ☸️ Kubernetes Deployment

### Apply Manifests
```bash
# Set kubeconfig
export KUBECONFIG=~/.kube/config

# Deploy
kubectl apply -f k8s/config.yaml      # Secrets + ConfigMaps
kubectl apply -f k8s/deployment.yaml  # Deployments + Services

# Check status
kubectl get pods -n streetleague
kubectl get svc -n streetleague
```

### Port Forward (Local Testing)
```bash
kubectl port-forward -n streetleague svc/streetleague-frontend 8080:80
# Open http://localhost:8080
```

### View Logs
```bash
kubectl logs -n streetleague deployment/streetleague-backend
kubectl logs -n streetleague deployment/streetleague-frontend
```

---

## 📊 Monitoring (Grafana)

### Access Dashboards
- **URL**: http://localhost:3000
- **Credentials**: admin / admin

### Available Metrics
- CPU & Memory usage
- HTTP request rates
- MongoDB operations
- Jenkins build status
- Error rates & latencies

### Configure Alerts
```
Alerting → Alert Rules → Create Rule
Condition: CPU > 80%
For: 5 minutes
Notification: Slack/Email
```

---

## 🧪 Testing

### Backend Tests
```bash
cd back && mvn test
# Results: target/surefire-reports/
```

### Frontend Tests
```bash
cd front/streetleague && npm run test
```

### SonarQube Quality Gate
```
Dashboard: http://localhost:9000
Metrics: Code coverage, bugs, vulnerabilities
```

---

## 📚 Documentation

- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Complete deployment guide
- **[BACKEND_DOCUMENTATION.md](BACKEND_DOCUMENTATION.md)** - API documentation
- **[FRONTEND_IMPLEMENTATION_SUMMARY.md](FRONTEND_IMPLEMENTATION_SUMMARY.md)** - Frontend details
- **[PROJECT_STATUS.md](PROJECT_STATUS.md)** - Current status

---

## 🤝 Contributing

1. **Create feature branch**
   ```bash
   git checkout -b feature/your-feature
   ```

2. **Make changes** → **Commit** → **Push**
   ```bash
   git add .
   git commit -m "feat: description"
   git push origin feature/your-feature
   ```

3. **Create Pull Request** on GitHub

4. **Wait for CI/CD** (Jenkins tests + SonarQube analysis)

5. **Merge to main** (triggers automatic deployment)

---

## 🛠️ Troubleshooting

### Backend
```bash
# Port already in use
lsof -i :8081

# Database connection error
mongosh "mongodb://admin:admin123@localhost:27017"

# Maven build fails
mvn clean install -U  # Update snapshots
```

### Frontend
```bash
# Port 4200 in use
npm start -- --port 4201

# Build errors
npm ci              # Clean install
npm run build       # Full build
```

### Docker/K8s
```bash
# Check pod logs
kubectl logs pod-name -n streetleague

# Describe pod for events
kubectl describe pod pod-name -n streetleague

# Check service connectivity
kubectl get endpoints -n streetleague
```

---

## 📞 Support

- **Backend Issues**: Check logs with `mvn spring-boot:run`
- **Frontend Issues**: Check browser console
- **Deployment Issues**: Review `DEPLOYMENT.md`
- **Monitoring**: Access Grafana at http://localhost:3000

---

## 📝 License

Project PI 2026 - Streetleague Team

---

## 🎯 Next Steps

1. ✅ Implement CI/CD pipeline with Jenkins
2. ✅ Setup Docker containerization
3. ✅ Configure Kubernetes manifests
4. ✅ Enable SonarQube code analysis
5. ✅ Deploy Grafana monitoring
6. 📋 **TODO**: Add authentication (OAuth/JWT)
7. 📋 **TODO**: Setup automated scaling
8. 📋 **TODO**: Configure SSL/TLS certificates

---

**Ready to deploy?** See [DEPLOYMENT.md](DEPLOYMENT.md) for full instructions! 🚀
