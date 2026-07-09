#!/bin/bash
# Validation script for DevOps infrastructure

echo "🔍 Streetleague DevOps Infrastructure Validation"
echo "=================================================="
echo ""

ERRORS=0
WARNINGS=0
SUCCESS=0

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check function
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1 exists"
        ((SUCCESS++))
    else
        echo -e "${RED}✗${NC} $1 MISSING"
        ((ERRORS++))
    fi
}

check_docker() {
    if command -v docker &> /dev/null; then
        echo -e "${GREEN}✓${NC} Docker installed"
        ((SUCCESS++))
    else
        echo -e "${YELLOW}⚠${NC} Docker not found (required for deployment)"
        ((WARNINGS++))
    fi
}

check_kubectl() {
    if command -v kubectl &> /dev/null; then
        echo -e "${GREEN}✓${NC} kubectl installed"
        ((SUCCESS++))
    else
        echo -e "${YELLOW}⚠${NC} kubectl not found (required for K8s)"
        ((WARNINGS++))
    fi
}

check_maven() {
    if command -v mvn &> /dev/null; then
        echo -e "${GREEN}✓${NC} Maven installed"
        ((SUCCESS++))
    else
        echo -e "${RED}✗${NC} Maven NOT installed"
        ((ERRORS++))
    fi
}

check_node() {
    if command -v node &> /dev/null; then
        echo -e "${GREEN}✓${NC} Node.js installed"
        ((SUCCESS++))
    else
        echo -e "${RED}✗${NC} Node.js NOT installed"
        ((ERRORS++))
    fi
}

# Check files
echo "📁 Checking Infrastructure Files..."
check_file "back/Dockerfile"
check_file "front/Dockerfile"
check_file "front/nginx.conf"
check_file "k8s/deployment.yaml"
check_file "k8s/config.yaml"
check_file "Jenkinsfile"
check_file "docker-compose.yml"
check_file "prometheus.yml"
check_file "DEPLOYMENT.md"
echo ""

# Check tools
echo "🛠️  Checking Required Tools..."
check_maven
check_node
check_docker
check_kubectl
echo ""

# Check Java version
echo "☕ Java Version..."
if command -v java &> /dev/null; then
    java_version=$(java -version 2>&1 | grep -oP '"?\K[0-9]+' | head -1)
    if [ "$java_version" -ge 17 ]; then
        echo -e "${GREEN}✓${NC} Java $java_version (OK)"
        ((SUCCESS++))
    else
        echo -e "${YELLOW}⚠${NC} Java $java_version (Need Java 17+)"
        ((WARNINGS++))
    fi
else
    echo -e "${RED}✗${NC} Java not found"
    ((ERRORS++))
fi
echo ""

# Validate YAML files
echo "📋 Validating YAML Syntax..."
if command -v yamllint &> /dev/null; then
    yamllint k8s/deployment.yaml && echo -e "${GREEN}✓${NC} K8s manifests valid" || ((ERRORS++))
    yamllint docker-compose.yml && echo -e "${GREEN}✓${NC} Docker Compose valid" || ((ERRORS++))
else
    echo -e "${YELLOW}⚠${NC} yamllint not available (install: pip install yamllint)"
fi
echo ""

# Summary
echo "=================================================="
echo "📊 Summary"
echo -e "  ${GREEN}✓ Success${NC}: $SUCCESS"
echo -e "  ${YELLOW}⚠ Warnings${NC}: $WARNINGS"
echo -e "  ${RED}✗ Errors${NC}: $ERRORS"
echo ""

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ Infrastructure Ready!${NC}"
    echo ""
    echo "🚀 Next Steps:"
    echo "  1. Install Docker Desktop"
    echo "  2. Run: docker-compose up -d"
    echo "  3. Access Jenkins at http://localhost:8080"
    echo "  4. Configure credentials in Jenkins"
    echo "  5. Create pipeline job"
    exit 0
else
    echo -e "${RED}❌ Fix errors before proceeding${NC}"
    exit 1
fi
