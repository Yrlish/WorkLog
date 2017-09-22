pipeline {
    agent any

    stages {
        stage("Versions") {
            steps {
                sh './gradlew -v'
                sh 'java -version'
            }
        }

        stage("Build") {
            steps {
                sh './gradlew --no-daemon shadowJar'
            }
        }

        stage("Archive artifact") {
            steps {
                sh './archive.sh'
                archive 'build/*.zip'
            }
        }
    }
    post {
        always {
            deleteDir() /* clean workspace */
        }
    }
}