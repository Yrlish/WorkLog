pipeline {
    stages {
        stage("Build") {
            steps {
                sh 'gradle shadowJar'
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