# ✅ GETTING STARTED CHECKLIST

**Follow these steps in order** | Estimated time: 2 hours first time

---

## 🎯 Phase 1: Validation (5 minutes)

- [ ] **Step 1.1:** Open PowerShell or Terminal
- [ ] **Step 1.2:** Navigate to project:
  ```bash
  cd "c:\Users\ASUS\Desktop\projet pi 2026\Projet-pi-2026"
  ```
- [ ] **Step 1.3:** Run validation script:
  ```bash
  .\scripts\validate-infrastructure.bat  # Windows
  # OR
  bash scripts/validate-infrastructure.sh  # Linux/Mac
  ```
- [ ] **Step 1.4:** Check output - all should be ✓ or ⚠
- [ ] **Step 1.5:** Note any ✗ errors (fix before continuing)

---

## 🐳 Phase 2: Install Docker (30-60 minutes, skip if already installed)

- [ ] **Step 2.1:** Check if Docker already installed:
  ```bash
  docker --version
  ```
- [ ] **Step 2.2:** If not installed, open [DOCKER_INSTALLATION_WINDOWS.md](DOCKER_INSTALLATION_WINDOWS.md)
- [ ] **Step 2.3:** Follow step-by-step installation guide
- [ ] **Step 2.4:** Restart computer when prompted
- [ ] **Step 2.5:** Open Docker Desktop (it will start in background)
- [ ] **Step 2.6:** Wait until Docker icon shows "Docker is running" (2-3 min)
- [ ] **Step 2.7:** Verify installation:
  ```bash
  docker --version
  docker ps
  ```

---

## 🚀 Phase 3: Start DevOps Stack (10 minutes)

- [ ] **Step 3.1:** Ensure you're in project root:
  ```bash
  cd "c:\Users\ASUS\Desktop\projet pi 2026\Projet-pi-2026"
  pwd  # Should show: .../Projet-pi-2026
  ```
- [ ] **Step 3.2:** Start all services:
  ```bash
  .\scripts\start-devops.bat  # Windows
  # OR
  bash scripts/start-devops.sh  # Linux/Mac
  ```
- [ ] **Step 3.3:** Watch the output (should see services starting)
- [ ] **Step 3.4:** Wait for message: "Services started" or "docker-compose up -d"
- [ ] **Step 3.5:** Wait additional 2-3 minutes for all services to be healthy
- [ ] **Step 3.6:** Verify all services running:
  ```bash
  docker-compose ps
  # All should show: Up (healthy) or Up
  ```

### Service Startup Checklist
- [ ] PostgreSQL: Up (typically instant)
- [ ] SonarQube: Up (takes 30-60 sec first time)
- [ ] Jenkins: Up (takes 20-30 sec)
- [ ] MongoDB: Up (instant)
- [ ] Prometheus: Up (instant)
- [ ] Grafana: Up (instant)

---

## 🔐 Phase 4: Configure Jenkins (15 minutes)

### Initial Access
- [ ] **Step 4.1:** Open browser → http://localhost:8080
- [ ] **Step 4.2:** Should see "Jenkins is starting up" or login page
- [ ] **Step 4.3:** Get admin password:
  ```bash
  docker-compose logs jenkins | grep "initialAdminPassword"
  # Look for: **initialAdminPassword: <token_here>**
  ```
- [ ] **Step 4.4:** Copy the token (highlighted in logs)
- [ ] **Step 4.5:** Paste token into Jenkins login page
- [ ] **Step 4.6:** Click "Continue"

### Install Plugins
- [ ] **Step 4.7:** Click "Install suggested plugins"
- [ ] **Step 4.8:** Wait for plugins to install (5-10 minutes)
- [ ] **Step 4.9:** Should see "Getting Started" page after plugins done

### Create Admin User
- [ ] **Step 4.10:** Fill in form:
  - Username: `admin`
  - Password: `streetleague2026`
  - Full name: `Streetleague Admin`
  - Email: `admin@streetleague.local`
- [ ] **Step 4.11:** Click "Save and Continue"
- [ ] **Step 4.12:** Click "Save and Finish"
- [ ] **Step 4.13:** Click "Start using Jenkins"

### Add Credentials
- [ ] **Step 4.14:** Click "Manage Jenkins" (left menu)
- [ ] **Step 4.15:** Click "Credentials"
- [ ] **Step 4.16:** Click "System" → "Global credentials"
- [ ] **Step 4.17:** Click "Add Credentials" (top right)

**Add First Credential: DOCKER_HUB_USERNAME**
- [ ] **Step 4.18:** Kind: "Secret text"
- [ ] **Step 4.19:** Secret: `your-github-username`
- [ ] **Step 4.20:** ID: `DOCKER_HUB_USERNAME`
- [ ] **Step 4.21:** Click "Create"

**Add Second Credential: DOCKER_HUB_PASSWORD**
- [ ] **Step 4.22:** Click "Add Credentials" again
- [ ] **Step 4.23:** Kind: "Secret text"
- [ ] **Step 4.24:** Secret: `ghcr_xxxxx_your_token` (GitHub Personal Access Token)
- [ ] **Step 4.25:** ID: `DOCKER_HUB_PASSWORD`
- [ ] **Step 4.26:** Click "Create"

**Add Third Credential: sonarqube-token**
- [ ] **Step 4.27:** Click "Add Credentials" again
- [ ] **Step 4.28:** Kind: "Secret text"
- [ ] **Step 4.29:** Secret: `squ_xxxxx_sonarqube_token`
- [ ] **Step 4.30:** ID: `sonarqube-token`
- [ ] **Step 4.31:** Click "Create"

---

## 📋 Phase 5: Create Pipeline Job (10 minutes)

- [ ] **Step 5.1:** Click "Jenkins" logo (top left) → Dashboard
- [ ] **Step 5.2:** Click "New Item" (left menu)
- [ ] **Step 5.3:** Name: `Streetleague-Pipeline`
- [ ] **Step 5.4:** Select: "Pipeline"
- [ ] **Step 5.5:** Click "OK"

### Configure Pipeline
- [ ] **Step 5.6:** Scroll to "Pipeline" section
- [ ] **Step 5.7:** Definition: "Pipeline script from SCM"
- [ ] **Step 5.8:** SCM: "Git"
- [ ] **Step 5.9:** Repository URL: `https://github.com/Daly-belguith/Projet-pi-2026.git`
- [ ] **Step 5.10:** Credentials: (leave blank for public repo)
- [ ] **Step 5.11:** Branch: `*/main`
- [ ] **Step 5.12:** Script Path: `Jenkinsfile`
- [ ] **Step 5.13:** Scroll down → Click "Save"

---

## 🔧 Phase 6: Test Pipeline (10 minutes first run)

- [ ] **Step 6.1:** Jenkins Dashboard → Click on "Streetleague-Pipeline"
- [ ] **Step 6.2:** Click "Build Now" (left menu)
- [ ] **Step 6.3:** Watch "Build History" (left, bottom)
- [ ] **Step 6.4:** Click the build (e.g., "#1") once it appears
- [ ] **Step 6.5:** Click "Console Output"
- [ ] **Step 6.6:** Watch logs as pipeline runs:
  - [ ] Checkout (1-2 min)
  - [ ] Maven build backend (2-3 min)
  - [ ] Backend tests (1 min)
  - [ ] SonarQube (1-2 min)
  - [ ] Frontend build (1 min)
  - [ ] Frontend tests (1 min)
  - [ ] Docker builds (2-3 min)
  - [ ] Total: ~15 minutes first run

- [ ] **Step 6.7:** When complete, check status:
  - ✅ Success (green checkmark)
  - ⚠️ Unstable (yellow)
  - ❌ Failed (red X)

### If Build Fails
- [ ] Read error message in console output
- [ ] Check [DEPLOYMENT.md](DEPLOYMENT.md#troubleshooting)
- [ ] Fix issue locally
- [ ] Git commit & push to branch
- [ ] Jenkins will auto-trigger on push

---

## 📊 Phase 7: Verify Services (5 minutes)

- [ ] **Step 7.1:** Open SonarQube → http://localhost:9000
  - [ ] Login: admin / admin
  - [ ] Should show "Projects" or "Create project"

- [ ] **Step 7.2:** Open Grafana → http://localhost:3000
  - [ ] Login: admin / admin
  - [ ] Should show "Home" dashboard

- [ ] **Step 7.3:** Open MongoDB → Test connection:
  ```bash
  mongosh "mongodb://admin:admin123@localhost:27017"
  # Should show: Trying to connect...
  # Then: streetleague>
  ```

- [ ] **Step 7.4:** Test Backend:
  ```bash
  curl http://localhost:8081/streetleague/api/events
  # Should return JSON array of events
  ```

- [ ] **Step 7.5:** Test Frontend:
  ```bash
  # Run this if frontend dev server running
  curl http://localhost:4200
  # Should return HTML
  ```

---

## 🎓 Phase 8: Learn & Explore (30 minutes)

- [ ] **Step 8.1:** Read key documentation:
  - [ ] [DEVOPS_README.md](DEVOPS_README.md) (10 min)
  - [ ] [DEPLOYMENT.md](DEPLOYMENT.md) (20 min, skim first)

- [ ] **Step 8.2:** Explore Grafana:
  - [ ] Click "Dashboards"
  - [ ] Check available dashboards
  - [ ] Create a simple panel

- [ ] **Step 8.3:** Explore SonarQube:
  - [ ] Click on project
  - [ ] View code quality metrics
  - [ ] Check code coverage

- [ ] **Step 8.4:** Explore Jenkins:
  - [ ] View pipeline stages
  - [ ] Check console output
  - [ ] Review build artifacts

---

## 📱 Phase 9: First Git Push (10 minutes)

- [ ] **Step 9.1:** Make a test code change:
  ```bash
  # Example: update a comment
  git checkout -b feature/test-pipeline
  # Edit any file
  git add .
  git commit -m "test: trigger pipeline"
  git push origin feature/test-pipeline
  ```

- [ ] **Step 9.2:** Create Pull Request on GitHub
  - [ ] Go to https://github.com/Daly-belguith/Projet-pi-2026
  - [ ] Click "Pull requests" tab
  - [ ] Click "New pull request"
  - [ ] Compare: `main` ← `feature/test-pipeline`
  - [ ] Create PR

- [ ] **Step 9.3:** Jenkins automatically triggers:
  - [ ] Go to Jenkins dashboard
  - [ ] Should see new build start
  - [ ] Watch pipeline execute
  - [ ] Check results when complete

- [ ] **Step 9.4:** Review in SonarQube:
  - [ ] Go to http://localhost:9000
  - [ ] Look for new project analysis
  - [ ] Review code quality results

- [ ] **Step 9.5:** After verification, merge PR:
  - [ ] GitHub → Merge pull request
  - [ ] Jenkins triggers deployment build
  - [ ] Watch deployment to K8s (if configured)

---

## 🚀 Phase 10: Production Deployment (Optional, skip if learning only)

- [ ] **Step 10.1:** Setup Kubernetes cluster (if not using local Docker K8s)
- [ ] **Step 10.2:** Configure kubeconfig:
  ```bash
  export KUBECONFIG=~/.kube/config
  ```
- [ ] **Step 10.3:** Update Docker registry in k8s/deployment.yaml
- [ ] **Step 10.4:** Apply manifests:
  ```bash
  kubectl apply -f k8s/config.yaml
  kubectl apply -f k8s/deployment.yaml
  ```
- [ ] **Step 10.5:** Verify deployment:
  ```bash
  kubectl get pods -n streetleague
  kubectl get svc -n streetleague
  ```

---

## ✅ Completion Checklist

When ALL items are checked ✓:

- [ ] Docker running and verified
- [ ] DevOps stack started (6 services healthy)
- [ ] Jenkins configured with credentials
- [ ] Pipeline job created
- [ ] First pipeline build successful
- [ ] Services verified working:
  - [ ] SonarQube
  - [ ] Grafana
  - [ ] MongoDB
  - [ ] Backend API
- [ ] Documentation read
- [ ] First Git push triggered pipeline

---

## 🎉 You're Done!

### What You Now Have

✅ Complete CI/CD pipeline
✅ Automated testing & code analysis
✅ Docker containerization
✅ Kubernetes deployment ready
✅ Monitoring with Grafana
✅ Code quality tracking with SonarQube
✅ Centralized logging with Jenkins

### Next Steps

1. **Add more features** to your code
2. **Watch pipeline** run automatically
3. **Monitor** in Grafana
4. **Review** code quality in SonarQube
5. **Deploy to production** when ready

---

## 📚 Documentation Reference

| Need | File |
|------|------|
| Deployment details | [DEPLOYMENT.md](DEPLOYMENT.md) |
| Docker installation | [DOCKER_INSTALLATION_WINDOWS.md](DOCKER_INSTALLATION_WINDOWS.md) |
| Project overview | [DEVOPS_README.md](DEVOPS_README.md) |
| File index | [DEVOPS_FILE_INDEX.md](DEVOPS_FILE_INDEX.md) |
| Implementation summary | [DEVOPS_IMPLEMENTATION_SUMMARY.md](DEVOPS_IMPLEMENTATION_SUMMARY.md) |

---

## 💡 Pro Tips

1. **Bookmark these URLs:**
   - Jenkins: http://localhost:8080
   - SonarQube: http://localhost:9000
   - Grafana: http://localhost:3000

2. **Check logs when stuck:**
   ```bash
   docker-compose logs [service-name]
   ```

3. **Save this checklist** as a reference

4. **Time it:** Each phase has estimated times (total ~2 hours)

5. **Ask for help:** Share console output when stuck

---

**Good luck! 🚀**

You're about to have a professional DevOps setup!
