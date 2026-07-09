# 📑 DevOps Implementation - File Index

**Quick Reference Map** | Generated: May 16, 2026

---

## 📁 Directory Structure

```
Projet-pi-2026/
├── back/
│   └── Dockerfile                    ← Backend container image
├── front/
│   ├── Dockerfile                    ← Frontend container image
│   └── nginx.conf                    ← SPA routing config
├── k8s/
│   ├── deployment.yaml               ← K8s deployments + services
│   └── config.yaml                   ← K8s secrets + configmaps
├── scripts/
│   ├── start-devops.bat              ← Windows launcher
│   ├── start-devops.sh               ← Linux/Mac launcher
│   ├── validate-infrastructure.bat   ← Windows validator
│   └── validate-infrastructure.sh    ← Unix validator
├── grafana/
│   └── provisioning/
│       ├── datasources/
│       │   └── prometheus.yaml       ← Auto-configure Prometheus
│       └── dashboards/
│           ├── dashboard.yaml        ← Dashboard provider
│           └── streetleague-dashboard.json ← Sample dashboard
├── Jenkinsfile                       ← CI/CD pipeline definition
├── docker-compose.yml                ← Full DevOps stack
├── prometheus.yml                    ← Metrics scraping config
├── .gitignore                        ← Git exclusions
├── DEPLOYMENT.md                     ← 300+ line deployment guide
├── DOCKER_INSTALLATION_WINDOWS.md    ← Docker setup for Windows
├── DEVOPS_README.md                  ← Project overview + quick start
└── DEVOPS_IMPLEMENTATION_SUMMARY.md  ← This file's companion
```

---

## 🚀 Getting Started (Quick Links)

### First Time Setup
1. **Read First:** [DEVOPS_IMPLEMENTATION_SUMMARY.md](DEVOPS_IMPLEMENTATION_SUMMARY.md)
2. **Validate:** Run `scripts/validate-infrastructure.bat` (Windows) or `scripts/validate-infrastructure.sh` (Unix)
3. **Install Docker:** [DOCKER_INSTALLATION_WINDOWS.md](DOCKER_INSTALLATION_WINDOWS.md)
4. **Start Stack:** `scripts/start-devops.bat` or `scripts/start-devops.sh`
5. **Configure Jenkins:** [DEPLOYMENT.md](DEPLOYMENT.md#jenkins-automation) (Section: Jenkins Automation)
6. **Create Pipeline:** [DEPLOYMENT.md](DEPLOYMENT.md#jenkins-automation) (Section: Create Jenkins Pipeline Job)

### Development Workflow
1. **Make Code Changes**
2. **Git Commit & Push** to feature branch
3. **Create Pull Request** on GitHub
4. **Jenkins Pipeline** runs automatically
5. **Review Results:** SonarQube + Test Reports
6. **Merge to Main** (triggers CD)
7. **Production Deployment** automatic

### Deploy to Production
1. **Configure K8s Cluster:** `kubectl config use-context <cluster-name>`
2. **Update Image Registry:** Edit `k8s/deployment.yaml`
3. **Apply Secrets:** `kubectl apply -f k8s/config.yaml`
4. **Deploy Services:** `kubectl apply -f k8s/deployment.yaml`
5. **Verify Status:** `kubectl get pods -n streetleague`
6. **Access Application:** `kubectl port-forward svc/streetleague-frontend 8080:80`

---

## 📂 File Details

### Infrastructure Files (5 files)

#### 1. **back/Dockerfile**
- **Purpose:** Build backend Docker image
- **Type:** Multi-stage build
- **Build Stage:** maven:3.9.4-eclipse-temurin-17
- **Runtime Stage:** eclipse-temurin:17-jre-alpine
- **Size:** ~200 MB
- **Exposes:** Port 8081
- **See:** [Dockerfile](back/Dockerfile)

#### 2. **front/Dockerfile**
- **Purpose:** Build frontend Docker image
- **Type:** Multi-stage build
- **Build Stage:** node:18-alpine (npm ci + npm run build)
- **Runtime Stage:** nginx:alpine
- **Size:** ~50-100 MB
- **Exposes:** Port 80
- **See:** [Dockerfile](front/Dockerfile)

#### 3. **front/nginx.conf**
- **Purpose:** Nginx configuration for SPA
- **Features:** 
  - SPA routing (all requests → index.html)
  - Backend proxy (/api → backend:8081)
  - Caching optimization
- **See:** [nginx.conf](front/nginx.conf)

#### 4. **k8s/deployment.yaml**
- **Purpose:** Kubernetes deployment manifests
- **Contents:**
  - Namespace (streetleague)
  - Backend deployment (2 replicas)
  - Frontend deployment (2 replicas)
  - Services (ClusterIP + LoadBalancer)
- **Resources:**
  - Backend: 256Mi req, 512Mi limit
  - Frontend: 128Mi req, 256Mi limit
- **Health Checks:** Liveness + Readiness probes
- **See:** [deployment.yaml](k8s/deployment.yaml)

#### 5. **k8s/config.yaml**
- **Purpose:** Kubernetes configuration & secrets
- **Contents:**
  - MongoDB connection secret
  - App config ConfigMap
  - PodDisruptionBudgets (high availability)
- **Security:** Secrets encrypted in etcd
- **See:** [config.yaml](k8s/config.yaml)

### Pipeline & Orchestration (2 files)

#### 6. **Jenkinsfile**
- **Purpose:** CI/CD pipeline definition
- **Type:** Declarative pipeline
- **Stages:** 9 stages (Checkout → Deploy → Verify)
- **Triggers:** Git push (main branch), GitHub webhook
- **Timeout:** 1 hour
- **See:** [Jenkinsfile](Jenkinsfile)

#### 7. **docker-compose.yml**
- **Purpose:** Local development & testing stack
- **Services:** 6 total
  - PostgreSQL 15 (for SonarQube)
  - SonarQube 10 (code quality)
  - Jenkins LTS (CI/CD)
  - MongoDB 7 (database)
  - Prometheus (metrics)
  - Grafana (monitoring)
- **Network:** devops-net (isolated)
- **Volumes:** Persistent storage for all services
- **See:** [docker-compose.yml](docker-compose.yml)

### Configuration Files (2 files)

#### 8. **prometheus.yml**
- **Purpose:** Prometheus scrape configuration
- **Jobs:**
  - Prometheus itself
  - Jenkins
  - SonarQube
  - Docker daemon
- **Interval:** 15s (global)
- **See:** [prometheus.yml](prometheus.yml)

#### 9. **.gitignore**
- **Purpose:** Git exclusions
- **Excludes:**
  - Java/Maven artifacts (target/)
  - Node/npm (node_modules/, dist/)
  - IDE configs (.vscode/, .idea/)
  - Environment files (.env)
  - Kubernetes configs (kubeconfig)
- **See:** [.gitignore](.gitignore)

### Launcher Scripts (4 files)

#### 10. **scripts/start-devops.bat** (Windows)
- **Purpose:** Start DevOps stack on Windows
- **Usage:** `.\scripts\start-devops.bat`
- **Waits For:** All services to be healthy
- **Shows:** Service URLs after startup
- **See:** [start-devops.bat](scripts/start-devops.bat)

#### 11. **scripts/start-devops.sh** (Unix)
- **Purpose:** Start DevOps stack on Linux/Mac
- **Usage:** `bash scripts/start-devops.sh`
- **Equivalent:** Windows batch file
- **See:** [start-devops.sh](scripts/start-devops.sh)

#### 12. **scripts/validate-infrastructure.bat** (Windows)
- **Purpose:** Validate all files & prerequisites
- **Checks:**
  - Infrastructure files exist
  - Required tools installed
  - Java version (17+)
- **Output:** Success/Warning/Error summary
- **See:** [validate-infrastructure.bat](scripts/validate-infrastructure.bat)

#### 13. **scripts/validate-infrastructure.sh** (Unix)
- **Purpose:** Validate all files & prerequisites (Unix)
- **Equivalent:** Windows batch validation
- **Additional:** Checks YAML syntax (if yamllint available)
- **See:** [validate-infrastructure.sh](scripts/validate-infrastructure.sh)

### Monitoring Configuration (3 files)

#### 14. **grafana/provisioning/datasources/prometheus.yaml**
- **Purpose:** Auto-configure Prometheus datasource
- **Server:** http://prometheus:9090
- **Default:** Yes (used by default)
- **See:** [prometheus.yaml](grafana/provisioning/datasources/prometheus.yaml)

#### 15. **grafana/provisioning/dashboards/dashboard.yaml**
- **Purpose:** Configure dashboard provisioning
- **Path:** /etc/grafana/provisioning/dashboards
- **Auto-Load:** Yes (loads .json files)
- **See:** [dashboard.yaml](grafana/provisioning/dashboards/dashboard.yaml)

#### 16. **grafana/provisioning/dashboards/streetleague-dashboard.json**
- **Purpose:** Sample Grafana dashboard
- **Panels:**
  - Container Memory Usage
  - Container CPU Usage
  - HTTP Requests
  - MongoDB Operations
- **Import:** Automatically on startup
- **See:** [streetleague-dashboard.json](grafana/provisioning/dashboards/streetleague-dashboard.json)

### Documentation (5 files)

#### 17. **DEPLOYMENT.md**
- **Length:** 300+ lines
- **Sections:** 7 major sections
- **Topics:**
  - Prerequisites
  - Maven commands
  - Git workflow
  - SonarQube
  - Jenkins setup
  - Docker deployment
  - Kubernetes
  - Grafana monitoring
  - Troubleshooting
- **Audience:** DevOps engineers, developers
- **See:** [DEPLOYMENT.md](DEPLOYMENT.md)

#### 18. **DOCKER_INSTALLATION_WINDOWS.md**
- **Purpose:** Step-by-step Docker Desktop installation
- **Platform:** Windows 10/11 Pro/Enterprise
- **Steps:** 9 sections
- **Includes:**
  - WSL 2 setup
  - Docker Desktop download & install
  - Configuration
  - Service verification
  - First-time setup
  - Troubleshooting
- **Audience:** Windows users new to Docker
- **See:** [DOCKER_INSTALLATION_WINDOWS.md](DOCKER_INSTALLATION_WINDOWS.md)

#### 19. **DEVOPS_README.md**
- **Purpose:** Project overview & quick start
- **Sections:** 10 sections
- **Includes:**
  - Quick start (2 methods)
  - Project structure
  - Prerequisites
  - Development workflow
  - CI/CD pipeline overview
  - Docker deployment
  - Kubernetes deployment
  - Monitoring
  - Testing
  - Contributing guide
- **Audience:** All team members
- **See:** [DEVOPS_README.md](DEVOPS_README.md)

#### 20. **DEVOPS_IMPLEMENTATION_SUMMARY.md**
- **Purpose:** Complete implementation summary
- **Sections:** 10 sections
- **Includes:**
  - What's been delivered
  - Pipeline workflow
  - Key features
  - Prerequisites checklist
  - Next steps (phased)
  - Service endpoints
  - Security practices
  - Monitoring setup
  - Troubleshooting
  - Verification checklist
- **Length:** Comprehensive reference
- **See:** [DEVOPS_IMPLEMENTATION_SUMMARY.md](DEVOPS_IMPLEMENTATION_SUMMARY.md)

#### 21. **DEVOPS_FILE_INDEX.md** (This file)
- **Purpose:** Quick reference map of all files
- **Use:** Navigate DevOps infrastructure quickly
- **Current File:** This document
- **See:** [DEVOPS_FILE_INDEX.md](DEVOPS_FILE_INDEX.md)

---

## 🔗 Quick Access Table

| Need | File | Location | See |
|------|------|----------|-----|
| Build backend image | Dockerfile | `back/` | [View](back/Dockerfile) |
| Build frontend image | Dockerfile | `front/` | [View](front/Dockerfile) |
| SPA routing config | nginx.conf | `front/` | [View](front/nginx.conf) |
| K8s deployment | deployment.yaml | `k8s/` | [View](k8s/deployment.yaml) |
| K8s secrets/config | config.yaml | `k8s/` | [View](k8s/config.yaml) |
| CI/CD pipeline | Jenkinsfile | root | [View](Jenkinsfile) |
| Dev stack | docker-compose.yml | root | [View](docker-compose.yml) |
| Metrics scraping | prometheus.yml | root | [View](prometheus.yml) |
| Git exclusions | .gitignore | root | [View](.gitignore) |
| Start (Windows) | start-devops.bat | `scripts/` | [View](scripts/start-devops.bat) |
| Start (Unix) | start-devops.sh | `scripts/` | [View](scripts/start-devops.sh) |
| Validate (Windows) | validate-infrastructure.bat | `scripts/` | [View](scripts/validate-infrastructure.bat) |
| Validate (Unix) | validate-infrastructure.sh | `scripts/` | [View](scripts/validate-infrastructure.sh) |
| Full deployment guide | DEPLOYMENT.md | root | [View](DEPLOYMENT.md) |
| Docker install (Windows) | DOCKER_INSTALLATION_WINDOWS.md | root | [View](DOCKER_INSTALLATION_WINDOWS.md) |
| DevOps overview | DEVOPS_README.md | root | [View](DEVOPS_README.md) |
| Implementation summary | DEVOPS_IMPLEMENTATION_SUMMARY.md | root | [View](DEVOPS_IMPLEMENTATION_SUMMARY.md) |

---

## 🎯 Common Tasks & Where to Find Them

### "How do I build Docker images?"
→ [DEVOPS_README.md](DEVOPS_README.md#-docker-deployment)
→ [DEPLOYMENT.md](DEPLOYMENT.md#-docker---continuous-delivery)

### "How do I setup Jenkins?"
→ [DEPLOYMENT.md](DEPLOYMENT.md#-jenkins-automation)
→ [DEVOPS_README.md](DEVOPS_README.md#-cicd-pipeline-jenkins)

### "How do I deploy to Kubernetes?"
→ [DEPLOYMENT.md](DEPLOYMENT.md#-kubernetes-deployment)
→ [DEVOPS_README.md](DEVOPS_README.md#-kubernetes-deployment)

### "How do I setup monitoring?"
→ [DEPLOYMENT.md](DEPLOYMENT.md#-grafana-continuous-monitoring)
→ [DEVOPS_README.md](DEVOPS_README.md#-monitoring-grafana)

### "How do I install Docker on Windows?"
→ [DOCKER_INSTALLATION_WINDOWS.md](DOCKER_INSTALLATION_WINDOWS.md)

### "What's the pipeline workflow?"
→ [DEVOPS_IMPLEMENTATION_SUMMARY.md](DEVOPS_IMPLEMENTATION_SUMMARY.md#-pipeline-workflow)

### "What services are available?"
→ [DEVOPS_IMPLEMENTATION_SUMMARY.md](DEVOPS_IMPLEMENTATION_SUMMARY.md#-service-endpoints)

### "How do I troubleshoot?"
→ [DEPLOYMENT.md](DEPLOYMENT.md#-troubleshooting)
→ [DEVOPS_IMPLEMENTATION_SUMMARY.md](DEVOPS_IMPLEMENTATION_SUMMARY.md#-troubleshooting-guide)

### "What's the next step?"
→ [DEVOPS_IMPLEMENTATION_SUMMARY.md](DEVOPS_IMPLEMENTATION_SUMMARY.md#-next-steps-immediate-actions)

---

## 📊 File Statistics

| Category | Count | Files |
|----------|-------|-------|
| Infrastructure | 5 | Dockerfile (2), nginx.conf, k8s manifests (2) |
| Pipeline/Orchestration | 2 | Jenkinsfile, docker-compose.yml |
| Configuration | 2 | prometheus.yml, .gitignore |
| Scripts | 4 | Launchers & validators (2 each OS) |
| Monitoring | 3 | Grafana provisioning + dashboard |
| Documentation | 5 | Guides + summaries + this index |
| **TOTAL** | **21** | **Complete DevOps infrastructure** |

---

## 🚀 Start Here Flowchart

```
START
  ↓
[1] Read: DEVOPS_IMPLEMENTATION_SUMMARY.md
  ↓
[2] Run: scripts/validate-infrastructure.bat
  ↓
  ├─ All ✓? → Go to [4]
  └─ Missing Docker? → Go to [3]
  ↓
[3] Read: DOCKER_INSTALLATION_WINDOWS.md
    Install Docker Desktop
    Return to [2]
  ↓
[4] Run: scripts/start-devops.bat
    Wait 2-3 minutes
  ↓
[5] Read: DEPLOYMENT.md (Jenkins section)
    Configure Jenkins
  ↓
[6] Create Pipeline Job in Jenkins
  ↓
[7] Push code to GitHub
  ↓
[8] Watch pipeline run!
  ↓
[9] View monitoring in Grafana
  ↓
✅ DEPLOYMENT COMPLETE
```

---

## 💡 Pro Tips

1. **Start with validation script** - Checks everything is ready
2. **Docker installation is one-time** - Then any project can use it
3. **Bookmark DEPLOYMENT.md** - Your most-used reference
4. **Jenkins setup is one-time** - Configure once, use forever
5. **Grafana dashboards are auto-loaded** - No extra setup needed
6. **All services have health checks** - System is self-healing
7. **Logs are your friend** - `docker-compose logs <service>` shows what's wrong
8. **Test locally first** - Use docker-compose before K8s

---

## 📞 Need Help?

1. **Check this file** - You're reading it!
2. **Check DEPLOYMENT.md** - Comprehensive guide
3. **Check DEVOPS_IMPLEMENTATION_SUMMARY.md** - Troubleshooting section
4. **Check Docker logs** - `docker-compose logs <service>`
5. **Check Kubernetes status** - `kubectl describe pod <pod>`

---

**Version:** 1.0 | **Date:** May 16, 2026 | **Status:** ✅ Complete

Good luck! 🚀
