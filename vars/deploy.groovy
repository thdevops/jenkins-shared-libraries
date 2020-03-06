def call(Map config = [:]) {
  when {
    branch 'master'
  }
  steps {
    sh 'gcloud app deploy'
  }
}
