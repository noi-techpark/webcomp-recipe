pipeline {
	agent any
	environment {
		DOCKER_IMAGE = "755952719952.dkr.ecr.eu-west-1.amazonaws.com/webcompbuild:latest"
	}
	options {
		ansiColor('xterm')
	}
	stages {
		stage('Prepare') {
			steps {
				sh '''
					rm -rf $(jq -r ".dist.basePath" wcs-manifest.json)
					aws ecr get-login --region eu-west-1 --no-include-email | bash
				'''
			}
		}
		stage("Build") {
			agent {
				dockerfile {
					filename 'infrastructure/docker/node.dockerfile'
					additionalBuildArgs '--build-arg JENKINS_USER_ID=$(id -u jenkins) --build-arg JENKINS_GROUP_ID=$(id -g jenkins)'
				}
			}
			steps {
				sh '''
					npm ci
					npm run build
				'''
			}
		}
		stage('Deploy to Test Store') {
			agent {
				docker {
					alwaysPull true
					image "${DOCKER_IMAGE}"
				}
			}
			steps {
				sshagent (credentials: ['tomcatkey', 'jenkins_github_ssh_key']) {
					sh '''
						cp /webcompbuild/.env .env
						WHEN=$(date "+%Y%m%dT%H%M")
						WC_NAME=${GIT_URL##*/}
						WC_NAME=${WC_NAME%.git}
						/webcompbuild/wcstorecli.sh -u
						/webcompbuild/wcstorecli.sh -d "${WC_NAME}" "${BRANCH_NAME}-${BUILD_NUMBER}-${WHEN}"
					'''
				}
			}
		}
	}
}
