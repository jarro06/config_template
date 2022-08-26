    
def config = [:]

def srvconf = new data.config_server()
def srv_config = srvconf.get("${JENKINS_URL}")
def job_config = [
    job: [
        name: "project_Pipeline_develop"
    ],
    git: [ 
        branch: "develop"
    ]
]


def gConfig = utilities.Tools.mergeMap(job_config, srv_config )


def scripts = """
def lib = library identifier: 'BizDevOps_JSL@develop', retriever: modernSCM(
  [\$class: 'GitSCMSource',
   remote: 'https://github.developer.allianz.io/JEQP/BizDevOps-JSL.git',
   credentialsId: 'git-token-credentials']) 
 
def config = ${utilities.Tools.formatMap(gConfig)}

def jslGeneral    = lib.de.allianz.bdo.pipeline.JSLGeneral.new()
def jslGit        = lib.de.allianz.bdo.pipeline.JSLGit.new()
def jslMaven      = lib.de.allianz.bdo.pipeline.JSLMaven.new()
def jslGhe        = lib.de.allianz.bdo.pipeline.JSLGhe.new()

def manual_commit_sha

// for questions about this job ask mario akermann/tobias pfeifer from team pipeline

pipeline {
    agent { label "\${config.job.agent}" }

    stages {
        stage('Prepare') {
            steps {
                echo "prepare"
                script {
                    jslGeneral.clean()
                }
            }    
        }
        stage('Checkout') {
            steps {
                echo "checkout"
                script {
                    jslGit.checkout( config, "JEQP", "project_Pipeline_develop", "develop")
                }
            }    
        }
        stage('Build') {
            steps {
                echo "Build"
                script {
                    dir ("project") {
                        jslMaven.build()
                    }
                }
            }    
        }
        stage('Publish Results') {
            steps {
                echo "Publish Results"
                script {
                    dir ("project") {
                        junit allowEmptyResults: true, testResults: '**/surefire-reports/TEST-*.xml'
                    }
                }
            }    
        }
    }
}
"""
        
def job = pipelineJob("${gConfig.job.name}");

job.with {

    definition {
        cps { 
            script(scripts)
        }
    } 
}  
