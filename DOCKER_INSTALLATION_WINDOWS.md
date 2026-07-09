# 🐳 Docker Installation & Setup Guide

**Platform:** Windows 10/11 Pro, Enterprise, or Education (requires WSL 2)

---

## Step 1: Enable Windows Subsystem for Linux 2 (WSL 2)

### Option A: Using Windows Terminal (Recommended)
```powershell
# Run as Administrator
wsl --install
# Restart computer
```

### Option B: Manual Setup
1. **Open Windows Terminal as Administrator**
2. **Enable required features:**
   ```powershell
   dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
   dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
   ```
3. **Restart computer**
4. **Set WSL 2 as default:**
   ```powershell
   wsl --set-default-version 2
   ```
5. **Install Ubuntu from Microsoft Store** (optional but recommended)

---

## Step 2: Install Docker Desktop

### Download
1. Go to: https://www.docker.com/products/docker-desktop
2. Click **Download for Windows**
3. Download `Docker Desktop Installer.exe`

### Install
1. Run installer (double-click)
2. Keep defaults checked:
   - ☑ WSL 2 Windows Subsystem for Linux
   - ☑ Hyper-V
3. Click **Install**
4. When prompted: **Log out and log back in**
5. Restart computer

### Verify Installation
```powershell
docker --version
docker ps
# Should show: "Client: Docker Engine - Community"
```

---

## Step 3: Configure Docker Desktop

### Increase Resources (Optional but Recommended)
1. **Docker Desktop** → **Settings** (gear icon)
2. **Resources**:
   - CPUs: 4-8 (depending on your system)
   - Memory: 4-8 GB
   - Swap: 1-2 GB
3. Click **Apply & Restart**

### Enable Kubernetes (Optional)
1. **Docker Desktop** → **Settings**
2. **Kubernetes** tab
3. ☑ **Enable Kubernetes**
4. Click **Apply & Restart**
5. Wait 5-10 minutes for initialization

---

## Step 4: Start Streetleague DevOps Stack

### From PowerShell (Windows)
```powershell
cd "c:\Users\ASUS\Desktop\projet pi 2026\Projet-pi-2026"

# Start all services (Jenkins, SonarQube, Grafana, MongoDB, Prometheus)
.\scripts\start-devops.bat

# Wait for services to be ready (2-3 minutes)
```

### From Git Bash / WSL
```bash
cd /mnt/c/Users/ASUS/Desktop/projet\ pi\ 2026/Projet-pi-2026
bash scripts/start-devops.sh
```

### Or Direct Docker Compose
```bash
docker-compose up -d
docker-compose logs -f jenkins
```

---

## Step 5: Verify Services Running

### Check Containers
```powershell
docker ps
```

### Wait for Services to be Healthy
```powershell
docker-compose ps
```

Expected output:
```
NAME              STATUS
postgres          Up (healthy)
sonarqube         Up (healthy)
jenkins           Up (healthy)
mongodb           Up (healthy)
prometheus        Up (healthy)
grafana           Up (healthy)
```

### Initial Wait Times
- PostgreSQL: 10-15 seconds
- SonarQube: 30-60 seconds (first run)
- Jenkins: 30-45 seconds
- Services ready: ~2-3 minutes total

---

## Step 6: Access Services

### Dashboards
| Service | URL | Credentials |
|---------|-----|-------------|
| Jenkins | http://localhost:8080 | admin / (see step 7) |
| SonarQube | http://localhost:9000 | admin / admin |
| Grafana | http://localhost:3000 | admin / admin |
| Prometheus | http://localhost:9090 | N/A |
| MongoDB | mongodb://admin:admin123@localhost:27017 | admin / admin123 |

### Get Jenkins Admin Password (first time)
```powershell
docker exec jenkins-jenkins-1 cat /var/jenkins_home/secrets/initialAdminPassword
```

Or check logs:
```powershell
docker-compose logs jenkins
# Look for: "Jenkins initial setup is required"
```

---

## Step 7: First-Time Jenkins Setup

1. **Open:** http://localhost:8080
2. **Paste admin password** from step 6
3. **Click "Continue"**
4. **Select "Install suggested plugins"**
5. **Create first admin user:**
   - Username: `admin`
   - Password: `streetleague2026`
   - Full name: `Streetleague Admin`
6. **Click "Save and Continue"**
7. **Instance Configuration:** Click "Save and Finish"

---

## Step 8: Configure Jenkins Credentials

### Add Docker Registry Credentials
1. **Manage Jenkins** → **Credentials** → **System** → **Global credentials**
2. **Add Credentials** → **Secret text**
   - **ID:** `DOCKER_HUB_PASSWORD`
   - **Secret:** `ghcr_xxxx_your_github_token`
3. **Repeat** for:
   - **ID:** `DOCKER_HUB_USERNAME` → `your-github-username`
   - **ID:** `sonarqube-token` → `squ_xxxx_sonarqube_token`
   - **ID:** `kubeconfig` → Upload file (Secret file)

### Add GitHub Webhook (Optional)
1. **GitHub Repo** → **Settings** → **Webhooks** → **Add webhook**
2. **Payload URL:** `http://your-jenkins-server:8080/github-webhook/`
3. **Content type:** `application/json`
4. **Events:** `Push events`

---

## Step 9: Create Jenkins Pipeline Job

1. **Jenkins Dashboard** → **New Item**
2. **Name:** `Streetleague-Pipeline`
3. **Type:** Select **Pipeline**
4. **Click OK**
5. **Pipeline section:**
   - **Definition:** "Pipeline script from SCM"
   - **SCM:** Git
   - **Repository URL:** `https://github.com/Daly-belguith/Projet-pi-2026.git`
   - **Credentials:** (GitHub credentials if private)
   - **Branch:** `*/main`
   - **Script Path:** `Jenkinsfile`
6. **Save**

### Test Pipeline
1. **Build Now**
2. Wait for pipeline to complete (5-10 minutes first run)
3. Check Console Output for any errors

---

## Troubleshooting

### Docker Desktop Won't Start
```powershell
# Check WSL 2 status
wsl --list --verbose

# If error: restart WSL
wsl --shutdown

# Restart Docker Desktop
```

### Port Already in Use
```powershell
# Find process using port
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue

# Kill process
Stop-Process -Id <PID> -Force

# Or change port in docker-compose.yml
# Change "8080:8080" to "8089:8080"
```

### Services Won't Start
```powershell
# Check logs
docker-compose logs

# Restart services
docker-compose restart

# Full restart
docker-compose down
docker-compose up -d
```

### MongoDB Connection Error
```powershell
# Test MongoDB connection
docker exec mongodb mongosh -u admin -p admin123

# Check connection string
mongodb://admin:admin123@localhost:27017/streetleague
```

### SonarQube Slow/Not Starting
- **First run:** SonarQube takes 1-2 minutes
- **Check logs:** `docker-compose logs sonarqube`
- **Wait for:** "SonarQube is ready"
- **Increase Docker resources** if issue persists

---

## Common Commands

```powershell
# Start stack
docker-compose up -d

# Stop stack
docker-compose down

# View logs
docker-compose logs -f [service-name]

# Restart service
docker-compose restart [service-name]

# Remove containers (WARNING: loses data)
docker-compose down -v

# Prune unused Docker resources
docker system prune -a
```

---

## Next Steps

1. ✅ Docker installed & running
2. ✅ Services started
3. ✅ Jenkins configured
4. 📋 Create pipeline job
5. 📋 Push code to GitHub
6. 📋 Trigger pipeline build
7. 📋 Monitor in Grafana

---

## Need Help?

- **Docker Desktop Issues:** https://docs.docker.com/desktop/troubleshoot/
- **WSL 2 Issues:** https://learn.microsoft.com/en-us/windows/wsl/
- **Jenkins Documentation:** https://www.jenkins.io/doc/
- **SonarQube Setup:** https://docs.sonarqube.org/

---

**Ready to deploy?** 🚀
```powershell
cd "c:\Users\ASUS\Desktop\projet pi 2026\Projet-pi-2026"
.\scripts\start-devops.bat
```
