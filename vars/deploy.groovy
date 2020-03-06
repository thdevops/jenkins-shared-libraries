def call(Map config = [:]) {

  steps {
    sh 'gcloud app deploy'
  }
}
