def call(Map config) {
    echo "Deploying ..."
    withCredentials([usernamePassword(credentialsId: 'pcfdev_user', usernameVariable: 'username', passwordVariable: 'password')]) {
        sh '''
            CF_HOME=$(pwd) cf login -a api.run.pivotal.io -u \"${username}\" -p \"${password}\" -o aurelien -s ${config.service}
            CF_HOME=$(pwd) cf push thdevops-test -p ${config.target}
        '''
    }
}
