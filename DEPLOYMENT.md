# 🚀 DEPLOYMENT GUIDE - Streetleague CI/CD Pipeline

## Overview
Pipeline complet: **Git → Maven → SonarQube → Docker → Kubernetes + Monitoring Grafana**

---

## 📋 Prerequisites

### Local Development
- Git
- Java 17+ & Maven 3.9+
- Node.js 18+
- Docker & Docker Compose
- kubectl & kubeconfig (pour K8s)

### Cloud/Production
- Docker Hub / GHCR account
- Kubernetes cluster (AKS, GKE, EKS, ou local)
- MongoDB Atlas ou instance MongoDB
- Jenkins server (ou utiliser Docker)

---

## 1️⃣ MAVEN COMMANDS (Backend Development)

```bash
# Navigate to backend
cd back

# Clean old builds
mvn clean

# Download dependencies & compile
mvn install

# Run tests
mvn test

# Package as JAR
mvn package

# Full build with all checks
mvn clean install package
```

**Expected output:**
- `target/projectPi-1.0.0.jar` (executable JAR)
- Test reports: `target/surefire-reports/`

---

## 2️⃣ GIT WORKFLOW

### Create feature branch & commit
```bash
git checkout -b feature/my-feature
# ... make changes
git add .
git commit -m "feat: add new feature"
git push origin feature/my-feature
```

### Create Pull Request
```
1. Go to GitHub → New Pull Request
2. Base: main, Compare: feature/my-feature
3. Wait for Jenkins CI checks to pass
```

### Merge to main (triggers CD)
```bash
# PR approved → Merge on GitHub
# OR locally:
git checkout main
git pull origin main
git merge feature/my-feature
git push origin main
```

---

## 3️⃣ SONARQUBE ANALYSIS

### Option A: Local Analysis
```bash
cd back

mvn sonar:sonar \
  -Dsonar.projectKey=streetleague-backend \
  -Dsonar.sources=src/main/java \
  -Dsonar.tests=src/test/java \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=squ_YOUR_TOKEN_HERE
```

### Option B: Jenkins handles it (automatic)
- SonarQube runs in stage "Backend: SonarQube Scan"
- Results: http://localhost:9000/dashboard

### View Results
```
SonarQube Dashboard: http://localhost:9000
  - Code Quality: bugs, vulnerabilities, coverage
  - Code Smells: maintainability issues
  - Duplications: code duplication %
```

---

## 4️⃣ JENKINS AUTOMATION

### Setup Jenkins
```bash
# Start Jenkins with Docker Compose (see below)
docker-compose up -d jenkins

# Access Jenkins
# URL: http://localhost:8080
# Username: admin
# Password: (check docker logs)
```

### Create Jenkins Pipeline Job
1. **New Item** → Enter name: `Streetleague-Pipeline`
2. **Pipeline** → OK
3. **Pipeline Definition** → "Pipeline script from SCM"
4. **SCM** → Git
5. **Repository URL**: `https://github.com/Daly-belguith/Projet-pi-2026.git`
6. **Branch**: `*/main`
7. **Script Path**: `Jenkinsfile`
8. **Poll SCM** (optional): `H H * * *` (daily check) or use GitHub webhook
9. **Save**

### Add GitHub Webhook (auto-trigger)
```
GitHub Repo → Settings → Webhooks → Add webhook
  - Payload URL: http://jenkins:8080/github-webhook/
  - Content type: application/json
  - Events: Push events
```

### Configure Jenkins Credentials
**Manage Jenkins → Credentials → System → Global credentials:**
1. **DOCKER_HUB_USERNAME** (Secret text): `your-github-username`
2. **DOCKER_HUB_PASSWORD** (Secret text): `ghcr_token_here`
3. **sonarqube-token** (Secret text): `squ_xxxxx`
4. **kubeconfig** (Secret file): Upload your `~/.kube/config`

---

## 5️⃣ DOCKER - CONTINUOUS DELIVERY

### Build Images Locally
```bash
# Backend
cd back
docker build -t streetleague-backend:v1.0 .
docker run -p 8081:8081 streetleague-backend:v1.0

# Frontend
cd front
docker build -t streetleague-frontend:v1.0 .
docker run -p 80:80 streetleague-frontend:v1.0
```

### Push to GHCR
```bash
docker login ghcr.io -u YOUR_GITHUB_USERNAME -p YOUR_GITHUB_TOKEN

docker tag streetleague-backend:v1.0 ghcr.io/daly-belguith/streetleague-backend:v1.0
docker tag streetleague-backend:v1.0 ghcr.io/daly-belguith/streetleague-backend:latest
docker push ghcr.io/daly-belguith/streetleague-backend:v1.0
docker push ghcr.io/daly-belguith/streetleague-backend:latest
```

### Jenkins handles push automatically
- Jenkinsfile stages: "Docker: Build & Push Backend/Frontend"
- Images tagged with: `{COMMIT_SHORT_SHA}-{BUILD_NUMBER}`

---

## 6️⃣ KUBERNETES DEPLOYMENT

### Prerequisites
```bash
# Set kubeconfig
export KUBECONFIG=~/.kube/config

# Verify cluster connection
kubectl cluster-info
kubectl get nodes
```

### Manual Deployment
```bash
# Apply namespace & secrets
kubectl apply -f k8s/config.yaml

# Update image tags in deployment.yaml
sed -i 's|latest|v1.0|g' k8s/deployment.yaml

# Deploy
kubectl apply -f k8s/deployment.yaml

# Check pods
kubectl get pods -n streetleague
kubectl get svc -n streetleague

# View logs
kubectl logs -n streetleague deployment/streetleague-backend
```

### Automatic Deployment (Jenkins)
- **Stage: "Deploy: Update K8s Manifests"** updates image tags
- **Stage: "Deploy: Apply K8s Manifests"** applies manifests
- **Stage: "Verify Deployment"** checks rollout status

### Access Application
```bash
# Port forward to access locally
kubectl port-forward -n streetleague svc/streetleague-frontend 8080:80
# Open: http://localhost:8080

# Get LoadBalancer IP (if using cloud K8s)
kubectl get svc -n streetleague streetleague-frontend
```

### Rollback Deployment
```bash
kubectl rollout history -n streetleague deployment/streetleague-backend
kubectl rollout undo -n streetleague deployment/streetleague-backend --to-revision=2
```

---

## 7️⃣ GRAFANA CONTINUOUS MONITORING

### Start Monitoring Stack
```bash
# Option 1: Docker Compose
docker-compose up -d prometheus grafana

# Option 2: Kubernetes
kubectl apply -f k8s/monitoring/ # (create this)
```

### Access Grafana
```
URL: http://localhost:3000
Username: admin
Password: admin
```

### Add Prometheus Data Source
1. **Configuration** → **Data Sources** → **Add data source**
2. **Prometheus**
3. **URL**: `http://prometheus:9090`
4. **Save & Test**

### Create Dashboards
**Dashboards → New Dashboard → Add panels:**

#### Panel 1: CPU Usage
```
Prometheus Query:
rate(container_cpu_usage_seconds_total[5m]) * 100

Visualization: Graph
```

#### Panel 2: Memory Usage
```
Prometheus Query:
container_memory_usage_bytes / 1024 / 1024

Visualization: Gauge
```

#### Panel 3: Request Rate
```
Prometheus Query:
rate(http_requests_total[1m])

Visualization: Graph
```

#### Panel 4: Jenkins Build Status
```
Prometheus Query:
jenkins_builds_last_build_duration_seconds

Visualization: Stat
```

### Set Alerts
**Alerting → Alert rules → Create rule:**
```
Alert: High CPU Usage
Condition: rate(container_cpu_usage_seconds_total[5m]) > 0.8
For: 5m
```

---

## 📊 COMPLETE DEVOPS STACK (All-in-One)

### Start Everything
```bash
# Windows
scripts/start-devops.bat

# Linux/Mac
scripts/start-devops.sh

# Or manual:
docker-compose up -d
```

### Verify Services
```bash
docker-compose ps

# Should show:
# postgres      | UP
# sonarqube     | UP (port 9000)
# jenkins       | UP (port 8080)
# mongodb       | UP (port 27017)
# prometheus    | UP (port 9090)
# grafana       | UP (port 3000)
```

### Access Dashboards
| Service | URL | Credentials |
|---------|-----|-------------|
| Jenkins | http://localhost:8080 | admin / (generated) |
| SonarQube | http://localhost:9000 | admin / admin |
| Grafana | http://localhost:3000 | admin / admin |
| Prometheus | http://localhost:9090 | - |
| MongoDB | mongodb://admin:admin123@localhost:27017 | - |

---

## 🔄 TYPICAL CI/CD FLOW

```
1. Developer commits to feature branch
   ↓
2. GitHub webhook triggers Jenkins
   ↓
3. Jenkins Pipeline:
   a. Checkout code
   b. Maven build & test
   c. SonarQube analysis (quality gate)
   d. Docker image build & push
   ↓
4. Pull Request → Review → Approve
   ↓
5. Merge to main
   ↓
6. Jenkins CD Stage:
   a. Update K8s manifests with new image
   b. Apply manifests (rolling update)
   c. Verify deployment status
   ↓
7. Grafana shows:
   - Pod health
   - Memory/CPU usage
   - Request latency
   - Error rates
   ↓
8. Monitoring alerts on anomalies
```

---

## 🐛 TROUBLESHOOTING

### Jenkins Pipeline Fails
```bash
# Check logs
docker-compose logs jenkins

# Rebuild
Jenkins → Job → Build Now

# Debug pipeline
Add echo/sh commands in Jenkinsfile
```

### Docker Push Fails
```bash
# Verify credentials
docker login ghcr.io

# Check token permissions (repo, write:packages)
```

### K8s Deployment Fails
```bash
# Check pod events
kubectl describe pod -n streetleague <POD_NAME>

# Check logs
kubectl logs -n streetleague deployment/streetleague-backend

# Check manifest syntax
kubectl apply -f k8s/deployment.yaml --dry-run=client
```

### MongoDB Connection Error
```bash
# Test connection
mongosh "mongodb://admin:admin123@localhost:27017"

# Check environment variables
kubectl get deployment streetleague-backend -n streetleague -o yaml | grep MONGO
```

---

## 📚 Quick Reference

```bash
# Maven
mvn clean install package test

# Git
git checkout -b feature/xxx
git add . && git commit -m "msg"
git push origin feature/xxx

# Docker
docker build -t name:tag .
docker push registry/name:tag

# Kubernetes
kubectl apply -f manifest.yaml
kubectl get pods -n namespace
kubectl logs deployment/name -n namespace
kubectl port-forward svc/name 8080:80

# Jenkins
curl -u admin:token http://localhost:8080/job/Pipeline/build

# SonarQube
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000

# Grafana
Access dashboards at http://localhost:3000
```

---

## 📞 Support & Next Steps

1. **Document your changes** in `CHANGELOG.md`
2. **Update deployment versions** in `k8s/deployment.yaml`
3. **Monitor metrics** in Grafana
4. **Review code quality** in SonarQube
5. **Automate tests** in Jenkins pipeline

Good luck! 🚀
