pipeline { 
    agent any
    tools { 
        maven 'Maven 3.5.0' 
        jdk 'jdk8' 
    }
    stages {
    	stage ('Initialize') {
            steps {
            	 sh '''
                  echo "PATH = ${PATH}"
                  echo "M2_HOME = ${M2_HOME}"
                 '''
            }
        }
    	stage('Clean'){
    		steps{
    			sh 'mvn clean'
    		}
    	}
        stage('Build') { 
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true install' 
            }
            post {
                success {
                    junit '**/surefire-reports/**/*.xml' 
                }
            }
        }
        stage('Build Images'){
        	steps {
                sh 'cd stampe-ws && ./build.sh' 
            }
        }
    }
}