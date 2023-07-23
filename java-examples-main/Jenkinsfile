
pipeline {

    agent any

    tools {
        maven "Maven"
        //docker "Docker"
        jdk "JDK17"
    }

    stages {
        stage('Maven Install') {
          steps {
            bat 'mvn clean install'
          }
        }
        stage('SonarScan') {
          steps {
               bat 'mvn clean compile package org.sonarsource.scanner.maven:sonar-maven-plugin:RELEASE:sonar  -D sonar.token=sqa_211b1943f34270ad857d44579b11830b5c55dde3 -f ./data-service/pom.xml'
          }
        }
    }
}