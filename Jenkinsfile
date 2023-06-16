pipeline {
    agent any
    stages {
        stage("SCM") {
            steps {
                script {
                    git credentialsId: '31bd806d-3dd4-439b-a2ff-235caadd5e93', url: 'https://github.com/Nagesh96/wf.git'
                }
            }
        }

        stage ("Maven Build") {
            steps {
                script {
                    def mvnHome = tool name : 'maven', type : 'maven'
                    def mavenCMD = "${mvnHome}/bin/mvn"
                    sh "${mavenCMD} clean package"
                }
            }
        }

        stage("static code analysis"){
            steps{
                script{
                    withSonarQubeEnv(credentialsId: 'd04c447c-c138-4471-8bb7-48ae40199f1f'){
                    sh 'mvn clean package sonar:sonar'
                 }
                }
                
            }
        }

        stage("quality gate status"){
            steps{
                script{
                    waitForQualityGate abortPipeline: false, credentialsId: 'd04c447c-c138-4471-8bb7-48ae40199f1f'
                }
            }
        }
        stage("upload artifact"){
            steps{
                script{
                    nexusArtifactUploader artifacts:
                    [
                        [
                            artifactId: 'springboot', 
                            classifier: '', 
                            file: '/var/lib/jenkins/workspace/Demo-project/target/Uber.jar', 
                            type: 'jar'
                            ]
                    ], 
                    credentialsId: '642e3582-3e51-4565-8be7-760b2f9fae63', 
                    groupId: 'com.example', 
                    nexusUrl: '54.89.181.17:8081/', 
                    nexusVersion: 'nexus3', 
                    protocol: 'http', 
                    repository: 'demo-mix', 
                    version: '1.0.0'
                }
            }
        }

        stage("Deploy") {
            steps {
                script {
                    def tomcatDevIp = '3.82.174.58'
                    def tomcatHome = '/opt/tomcat/'
                    def webapps = tomcatHome+'webapps'
                    def tomcatStart = "${tomcatHome}bin/startup.sh"
                    def tomcatStop = "${tomcatHome}bin/shutdown.sh"
                    sh label: '', script: 'ssh root@tomcat ${tomcatStop}'
                    sh label: '', script: 'scp -o StrictHostKeyChecking=no -r $WORKSPACE/target/*.war root@tomcat:${webapps}'
                    sh label: '', script: 'ssh root@tomcat ${tomcatStart}'
                }
            }

            post {
                success {
                    mail to: 'nageswar1616@gmail.com',
                    subject: " '${JOB_NAME}' (${BUILD_NUMBER} deploment completed)",
                    body: "Deployment Succeeded please go to the ${BUILD_URL} and verify the build"
                }
                failure {
                    mail to: 'nageswar1616@gmail.com',
                    subject: " '${JOB_NAME}' (${BUILD_NUMBER}) deploment completed",
                    body: "Deployment Failed please go to the ${BUILD_URL} and verify the build"                   
                }
                always {
                    echo "This is post always stage"
                }
            }
        }
    }
}






* For nexus uploder if the version changes we dont need to change it always in pipeline we can automate.
---------------------------------------------------------------------------------------------------------

        stage("upload artifact"){
            steps{
                script{

                    def readPomVersion = readMavenPom file: 'pom.xml'
                    def nexusRepo = readPomVersion.version.endsWith("SNAPSHOT") ? "demoapp-snapshot" : "demoapp-release"  --- repo name in nexus
                    nexusArtifactUploader artifacts:
                    [
                        [
                            artifactId: 'springboot', 
                            classifier: '', 
                            file: '/var/lib/jenkins/workspace/Demo-project/target/Uber.jar', 
                            type: 'jar'
                            ]
                    ], 
                    credentialsId: '642e3582-3e51-4565-8be7-760b2f9fae63', 
                    groupId: 'com.example', 
                    nexusUrl: '54.89.181.17:8081/',
                    nexusVersion: 'nexus3', 
                    protocol: 'http', 
                    repository: nexusRepo, 
                    version: "${readPomVersion.version}"
                }
            }
        }

* For Quality gate status in sonarqube we should create webhook configuration
-------------------------------------------------------------------------------

in Sonarqube ---> Administaraton -----> Configuration -----> webhooks -----> create

Name : jenkins-webhook  ----> any name can give

URL : Jenkins URL/sonarqube-webhook
_________
| Create|
---------




