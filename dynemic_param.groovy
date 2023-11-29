pipeline {
    agent any
    
    stages {
        stage('Fetch Git Branches') {
            steps {
                script {
                    def gitURL = input(message: 'Enter Git URL', parameters: [string(defaultValue: '', description: 'Git URL', name: 'GIT_URL')])
                    
                    // Fetch branches from the Git repository
                    def branchesResult = sh(script: "git ls-remote --heads ${gitURL}", returnStatus: true)
                    if (branchesResult == 0) {
                        def branches = sh(script: "git ls-remote --heads ${gitURL}", returnStdout: true).trim().readLines().collect { it.replaceAll('.*refs/heads/', '').trim() }
                        echo "Branches: ${branches}"
                        
                        // Select source branch
                        def selectedSourceBranch = input(
                            message: 'Select Source Branch:',
                            parameters: [choice(choices: branches, description: 'Source Branch', name: 'SOURCE_BRANCH')]
                        )
                        env.GIT_URL = gitURL
                        env.SOURCE_BRANCH = selectedSourceBranch
                        
                        // Convert branches to a comma-separated string
                        def branchesString = branches.join(',')
                        
                        // Use the Extended Choice Parameter plugin for selecting multiple target branches
                        def selectedTargetBranches = input(
                            message: 'Select Target Branches:',
                            parameters: [
                                [$class: 'ExtendedChoiceParameterDefinition',
                                    name: 'TARGET_BRANCHES',
                                    description: 'Select Target Branches',
                                    multiSelectDelimiter: ',',
                                    type: 'PT_CHECKBOX',
                                    value: branchesString // Pass the string value instead of the array
                                ]
                            ]
                        )
                        env.TARGET_BRANCHES = selectedTargetBranches.join(',')
                    } else {
                        error 'Failed to retrieve branches from the Git repository.'
                    }
                }
            }
        }
        
        stage('Print Environment Variables') {
            steps {
                script {
                    echo "Git URL: ${env.GIT_URL}"
                    echo "Source Branch: ${env.SOURCE_BRANCH}"
                    echo "Target Branches: ${env.TARGET_BRANCHES}"
                }
            }
        }
        
        /*stage('Execute Python Script') {
            steps {
                script {
                    // Your Python script execution logic here, using the selected source and target branches
                    sh 'python your_python_script.py'
                }
            }
        }*/
    }
}
