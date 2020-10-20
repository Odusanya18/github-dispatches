# If you update this file, please follow
# https://www.thapaliya.com/en/writings/well-documented-makefiles/

# Ensure Make is run with bash shell as some syntax below is bash-specific
SHELL:=/usr/bin/env bash

.DEFAULT_GOAL:=help

# Use GOPROXY environment variable if set
GOPROXY := $(shell go env GOPROXY)
ifeq ($(GOPROXY),)
GOPROXY := https://proxy.golang.org
endif
export GOPROXY

# Binaries.
GINKGO := $(abspath $(TOOLS_BIN_DIR)/ginkgo)

KIND_VERSION ?= v0.9.0
KUBECTL_VERSION ?= v0.19.0
HELM_VERSION ?= v3.3.0

# Hosts running SELinux need :z added to volume mounts
SELINUX_ENABLED := $(shell cat /sys/fs/selinux/enforce 2> /dev/null || echo 0)

ifeq ($(SELINUX_ENABLED),1)
  DOCKER_VOL_OPTS?=:z
endif

# Activate module mode, as we use go modules to manage dependencies
GO111MODULE ?= on
export GO111MODULE

## -------------------------------------
## Install Kubectl, Helm
## -------------------------------------
.PHONY: install-kubectl
install-kubectl:  ## Install Kubectl, Helm for Kind cluster mgmt
	go get k8s.io/kubectl@$(KUBECTL_VERSION) helm.sh/helm/v3@$(HELM_VERSION)
	kubectl version --client=true
	helm version --client

## -------------------------------------
## Create Kind Kubernetes cluster
## -------------------------------------
.PHONY: create-cluster
create-cluster:  install-kubectl## Create a test Kind cluster
	go get sigs.k8s.io/kind@$(KIND_VERSION)
	kind create cluster

.PHONY: install-spinnaker-operator
install-spinnaker-operator:  ## Install the Armory Spinnaker Operator
	kubectl apply -f spinnaker-operator/deploy/crds/
	kubectl create ns spinnaker-operator
	kubectl -n spinnaker-operator apply -f spinnaker-operator/deploy/operator/basic
	kubectl -n spinnaker-operator get pods

.PHONY: apply-spinnaker-service
apply-spinnaker-service: ## Apply the basic Spinnaker Service
	kubectl -n spinnaker-operator apply -f spinnaker-operator/deploy/spinnaker/basic/SpinnakerService.yml

.PHONY: uninstall-spinnaker-operator
uninstall-spinnaker-operator:  ## Uninstall the Armory Spinnaker Operator
	kubectl -n spinnaker-operator delete -f spinnaker-operator/deploy/operator/basic
	kubectl delete -f spinnaker-operator/deploy/crds/
	kubectl delete ns spinnaker-operator
## -------------------------------------
## Delete Kind Kubernetes cluster
## -------------------------------------
.PHONY: delete-cluster
delete-cluster:  ## Delete the test Kind cluster
	kind delete cluster

.PHONY: install-minio
install-minio: ## Install minio for PV
	helm repo add stable https://charts.helm.sh/stable
	helm install --namespace spinnaker-operator minio\
		--set accessKey=minio --set secretKey=minio stable/minio

## --------------------------------------
## Help
## --------------------------------------
help:  ## Display this help
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n"} /^[a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

