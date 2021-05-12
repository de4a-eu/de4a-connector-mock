pipeline {
    agent none
    stages {
        stage('Test') {
            when {
                anyOf {
                    branch 'development'; branch pattern: 'PR-\\d+', comparator: 'REGEXP'
                }
            }
            agent {
                docker {
                    image 'maven:3-adoptopenjdk-11'
                    args '-u 999:1000 -v $HOME/.m2:/var/maven/.m2  -e MAVEN_CONFIG=/var/maven/.m2 -e HOME='.' --network docker-ci_default'
                }
            }
            steps {
                sh 'mvn clean test sonar:sonar -Duser.home=/var/maven -Dsonar.host.url=http://sonarqube:9000/sonarqube -Dsonar.login=$SONAR_TOKEN'
            }
        }

        stage('Build'){
            when {
                branch 'main'
            }
            agent {
                docker {
                    image 'maven:3-adoptopenjdk-11'
                    args '-v $HOME/.m2:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2 --network docker-ci_default'
                }
            }
            steps {
                sh 'mvn -Duser.home=/var/maven clean package'
            }
            // TODO: add pushing to a future de4a maven repo?
            // TODO: add building a release on a tag and push to GitHub?
        }
        stage('Docker') {
            when{
                branch 'main'
            }
            agent { label 'master' }
            environment {
                VERSION=readMavenPom().getVersion()
            }
            steps {
                script{
                    def img
                    if (env.BRANCH_NAME == 'main') {
                        img = docker.build('de4a/connector-mock','--build-arg VERSION=$VERSION .')
                        docker.withRegistry('','docker-hub-token') {
                            img.push('latest')
                            img.push('$VERSION')
                        }
                    }
                }
            }
        }
    }
    post {
        failure {
            node('master') {
                script {
                    env.ORG=env.JOB_NAME.split('/')[0]
                    env.REPO=env.JOB_NAME.split('/')[1]
                    env.BR=env.JOB_NAME.split('/')[2]
                    env.ERRORLOG = sh returnStdout: true, script: "cat ${env.JENKINS_HOME}/jobs/${env.ORG}/jobs/${env.REPO}/branches/${env.BR}/builds/${BUILD_NUMBER}/log | grep -B 1 -A 5 '\\[ERROR\\]'"
                    slackSend color: "danger", message: ":darth_maul: Build fail! :darth_maul:\nJob name: ${env.JOB_NAME}, Build number: ${env.BUILD_NUMBER}\nGit Author: ${env.CHANGE_AUTHOR}, Branch: ${env.GIT_BRANCH}, ${env.GIT_URL}\nMaven [ERROR] log below:\n ${env.ERRORLOG}"
                }
            }
        }
        success {
            node('master') {
                script {
                    if(currentBuild.getPreviousBuild() &&
                       currentBuild.getPreviousBuild().getResult().toString() != 'SUCCESS') {
                        slackSend color: "good", message: ":baby-yoda: This is the way! :baby-yoda: \nJob name: ${env.JOB_NAME}, Build number: ${env.BUILD_NUMBER}\nGit Author: ${env.CHANGE_AUTHOR}, Branch: ${env.GIT_BRANCH}, ${env.GIT_URL}\n"
                    }
                }
            }
        }
    }
}
