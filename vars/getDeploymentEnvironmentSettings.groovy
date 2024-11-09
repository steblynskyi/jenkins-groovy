// vars/getDeploymentEnvironmentSettings.groovy
def call(String branchName,boolean abortConBuild = true)
{
    abortConcurrentBuild(abortConBuild)

    echo "Getting Deployment Environment Settings Target: ${branchName}"
    def innCenterDBCredentialId     = ""
    def innCenterDBHost             = ""
    def reservationDBCredentialId   = ""
    def reservationDBHost           = ""
    def innsightsDBCredentialId     = ""
    def innsightsDBHost             = ""
    def quartzDBCredentialId        = ""
    def quartzDBHost                = ""
    def deploymentEnvironment       = ""
    def versionSuffix               = ""
    def autoDeploy                  = false
    def deploymentContext           = ""
    def paymentsTestAgentURL        = ""
    def paymentsTestAgentAPIKey     = ""
    def grafanaURL                  = ""
    def grafanaCredentials          = ""

    switch("${branchName}") {

        case 'develop':
            innCenterDBCredentialId     = "111-222-333-444-555"
            innCenterDBHost             = "sql-inncenter.dev-steblynskyi.com"
            reservationDBCredentialId   = "111-222-333-444-555"
            reservationDBHost           = "reservation-db.dev-steblynskyi.com"
            innsightsDBCredentialId     = "111-222-333-444-555"
            innsightsDBHost             = "innsights-db.dev-steblynskyi.com"
            quartzDBCredentialId        = "111-222-333-444-555"
            quartzDBHost                = "sql-quartz.dev-steblynskyi.com"
            deploymentEnvironment       = "DEV"
            autoDeploy                  = true
            versionSuffix               = "-dev"
            deploymentContext           = "develop"
            paymentsTestAgentURL        = "https://payments-test-agent-api.dev-steblynskyi.com/run"
            paymentsTestAgentAPIKey     = "111-222-333-444-555"
            grafanaURL                  = "https://observability-grafana.dev-steblynskyi.com"
            grafanaCredentials          = "grafana-token-dev"
        break

        case 'qa':
            innCenterDBCredentialId     = "111-222-333-444-555"
            innCenterDBHost             = "sql-inncenter.qa-steblynskyi.com"
            reservationDBCredentialId   = "111-222-333-444-555"
            reservationDBHost           = "reservation-db.qa-steblynskyi.com"
            innsightsDBCredentialId     = "111-222-333-444-555"
            innsightsDBHost             = "innsights-db.qa-steblynskyi.com"
            quartzDBCredentialId        = "111-222-333-444-555"
            quartzDBHost                = "sql-quartz.qa-steblynskyi.com"
            deploymentEnvironment       = "QA"
            autoDeploy                  = true
            versionSuffix               = "-qa"
            deploymentContext           = "qa"
            paymentsTestAgentURL        = "https://payments-test-agent-api.qa-steblynskyi.com/run"
            paymentsTestAgentAPIKey     = "111-222-333-444-555"
            grafanaURL                  = "https://observability-grafana.qa-steblynskyi.com"
            grafanaCredentials          = "grafana-token-qa"
        break

        case 'master':
            innCenterDBCredentialId     = "111-222-333-444-555"
            innCenterDBHost             = "sql-inncenter.qa3-steblynskyi.com"
            reservationDBCredentialId   = "111-222-333-444-555"
            reservationDBHost           = "reservation-db.qa3-steblynskyi.com"
            innsightsDBCredentialId     = "111-222-333-444-555"
            innsightsDBHost             = "innsights-db.qa3-steblynskyi.com"
            quartzDBCredentialId        = "111-222-333-444-555"
            quartzDBHost                = "sql-quartz.qa3-steblynskyi.com"
            deploymentEnvironment       = "QA3"
            autoDeploy                  = true
            versionSuffix               = ""
            deploymentContext           = "qa3"
            paymentsTestAgentURL        = "https://payments-test-agent-api.qa3-steblynskyi.com/run"
            paymentsTestAgentAPIKey     = "111-222-333-444-555"
            grafanaURL                  = "https://observability-grafana.prodsteblynskyi.com"
            grafanaCredentials          = "grafana-token-prod"
        break

        default:
        break
    }

    def environmentSettings = [ InnCenterDBCredentialId     : "${innCenterDBCredentialId}"
                              , InnCenterDBHost             : "${innCenterDBHost}"
                              , ReservationDBCredentialId   : "${reservationDBCredentialId}"
                              , ReservationDBHost           : "${reservationDBHost}"
                              , InnsightsDBCredentialId     : "${innsightsDBCredentialId}"
                              , InnsightsDBHost             : "${innsightsDBHost}"
                              , QuartzDBCredentialId        : "${quartzDBCredentialId}"
                              , QuartzDBHost                : "${quartzDBHost}"
                              , DeploymentEnvironment       : "${deploymentEnvironment}"
                              , AutoDeploy                  : autoDeploy
                              , VersionSuffix               : "${versionSuffix}"
                              , DeploymentContext           : "${deploymentContext}"
                              , PaymentsTestAgentURL        : "${paymentsTestAgentURL}"
                              , PaymentsTestAgentAPIKey     : "${paymentsTestAgentAPIKey}"
                              , GrafanaURL                  : "${grafanaURL}"
                              , GrafanaCredentials          : "${grafanaCredentials}"]

    return environmentSettings
}