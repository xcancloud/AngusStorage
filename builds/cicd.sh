#!/bin/sh

# ---------------------------------------------------------------------------
# Jenkins CI/CD Pipeline Script for AngusStorage Project.
# Usage: sh cicd.sh --env env.dev --editionType edition.cloud_service --hosts 127.0.0.1 --dbType db.mysql
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

# Global Variables
REMOTE_APP_DIR="/data/apps/AngusStorage"
REMOTE_APP_LOGS_DIR_NAME="logs"
REMOTE_APP_CONF_DIR="/data/apps/conf/storage"

CLEAR_MAVEN_REPO="/data/repository"

# Validate input parameters
validate_parameters() {
  # Validate mandatory parameters
  if [ -z "$env" ] || [ -z "$editionType" ]; then
    echo "ERROR: Missing required parameters (env, editionType)"
    exit 1
  fi

  # Validate editionType and env compatibility
  case "$editionType" in
    edition.cloud_service)
      case "$env" in
        env.local|env.dev|env.prod) ;;
        *) echo "ERROR: Cloud edition requires env.local/dev/prod"; exit 1 ;;
      esac ;;
    edition.community|edition.enterprise|edition.datacenter)
      if [ "$env" != "env.priv" ]; then
        echo "ERROR: Private edition requires env.priv"; exit 1
      fi ;;
    *) echo "ERROR: Invalid editionType"; exit 1 ;;
  esac
}

# Check and clean environment
prepare_environment() {
  echo "INFO: Preparing build environment..."

  # Load system profile for environment variables
  if [ -f "/etc/profile" ]; then
    echo "INFO: Loading system environment variables"
    . /etc/profile
  fi

    echo "INFO: Checking Java/Maven environment"
    if ! command -v java >/dev/null || ! command -v mvn >/dev/null; then
      echo "ERROR: Java/Maven not found"; exit 1
    fi

    echo "INFO: Cleaning Maven repository at ${CLEAR_MAVEN_REPO}/cloud/xcan/"
    rm -rf "${CLEAR_MAVEN_REPO}"/cloud/xcan/*
}

# Build service module
maven_build () {
  echo "INFO: mvn build start"
  mvn -B -e -U clean package -Dmaven.test.skip=true -s ${MAVEN_HOME}/conf/xcan_repo_settings.xml -f pom.xml -P${editionType},${env},${dbType}
  if [ $? -ne 0 ]; then
    echo "ERROR: mvn build failed"
    exit 1
  fi
  echo "INFO: mvn build end"
}

# Deploy service module
deploy_service() {
  echo "INFO: Deploying service module to ${host}"
  ssh "$host" "mkdir -p ${REMOTE_APP_DIR}" || {
    echo "ERROR: Failed to init app directory"; exit 1
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && sh shutdown-storage.sh" || {
    echo "WARN: Failed to stop service, proceeding anyway"
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && find . -mindepth 1 -maxdepth 1 -not \( -name ${REMOTE_APP_LOGS_DIR_NAME} \) -exec rm -rf {} +" || {
    echo "ERROR: Failed to clean service directory"; exit 1
  }
  scp -rp "boot/target"/* "${host}:${REMOTE_APP_DIR}/" || {
    echo "ERROR: Failed to copy service files"; exit 1
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && mkdir -p conf && mv classes/spring-logback.xml conf/storage-logback.xml" || {
    echo "ERROR: Failed to rename logback file"; exit 1
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && cp -f ${REMOTE_APP_CONF_DIR}/.*.env conf/" || {
    echo "ERROR: Failed to copy env files"; exit 1
  }
  ssh "$host" "cd ${REMOTE_APP_DIR} && sh startup-storage.sh debug" || {
    echo "ERROR: Failed to start service"; exit 1
  }
  sh builds/check-health.sh ${host} || {
    echo "ERROR: Service health check failed"; exit 1
  }
}

# Main execution flow
while [ $# -gt 0 ]; do
  case "$1" in
    --env) env="$2"; shift ;;
    --editionType) editionType="$2"; shift ;;
    --hosts) hosts="$2"; shift ;;
    --dbType) dbType="${2:-db.mysql}"; shift ;;
    *) echo "WARN: Unknown parameter $1"; shift ;;
  esac
  shift
done

# Step 1: Parameter validation
validate_parameters

# Step 2: Environment preparation
prepare_environment

# Step 3: CI Phase
# clone_repository

echo "INFO: Building service module"
maven_build || {
  echo "ERROR: Service build failed"; exit 1
}

# Step 4: CD Phase
if [ -n "$hosts" ]; then
  echo "INFO: Starting deployment to hosts: ${hosts}"
  IFS=',' read -ra HOST_LIST <<< "$hosts"
  for host in "${HOST_LIST[@]}"; do
      deploy_service
  done
else
  echo "INFO: No hosts specified, skipping deployment"
fi

echo "SUCCESS: CI/CD pipeline completed successfully"
exit 0
