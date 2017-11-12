// Use Curl to post this script to Jenkins https://wiki.jenkins.io/display/JENKINS/Jenkins+Script+Console

import jenkins.model.*
import hudson.security.*
import org.jenkinsci.plugins.*
import hudson.plugins.scm_sync_configuration.xstream.migration.DefaultSSCPOJO
import hudson.plugins.scm_sync_configuration.scms.*
import hudson.plugins.scm_sync_configuration.ScmSyncConfigurationPlugin
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import groovy.mock.interceptor.MockFor

def gitUrl = 'git@github.com:tianhuil/my-jenkins-config.git'

def mock = [
  getParameter: {
    key -> switch(key) {
      case 'gitRepositoryUrl':
        return gitUrl
      case 'commitMessagePattern':
        return 'Sync: [message]'
      case 'scm':
        return 'hudson.plugins.scm_sync_configuration.scms.ScmSyncGitSCM'
      default:
        return null
    }
  },
  getParameterValues: {
    return null
  }
] as StaplerRequest

def manualSynchronizationIncludes = [
  'org.jenkinsci.plugins.workflow.flow.FlowExecutionList.xml',
  'com.nirima.jenkins.plugins.docker.DockerPluginConfiguration.xml',
  'nodes/*/config.xml',
  'jobs/**/nextBuildNumber',
  'secrets/jenkins.slaves.JnlpSlaveAgentProtocol.secret',
  'secrets/master.key'
]

def data = new DefaultSSCPOJO()
data.setScmRepositoryUrl(gitUrl)
data.setScm(SCM.valueOf(ScmSyncGitSCM.class))
data.setNoUserCommitMessage(false)
data.setDisplayStatus(true)
data.setCommitMessagePattern('[message]')
data.setManualSynchronizationIncludes(manualSynchronizationIncludes)

def jsonObj = new JSONObject()
jsonObj.put('noUserCommitMessage', 'false')
jsonObj.put('displayStatus', 'true')

def instance = Jenkins.getInstance()
def scmSyncPlugin = instance.getPlugin('scm-sync-configuration')

if (true) {
  scmSyncPlugin.configure(mock, jsonObj)
}
else {
  scmSyncPlugin.loadData(data)
  scmSyncPlugin.business.reloadAllFilesFromScm()
  scmSyncPlugin.business.synchronizeAllConfigs(ScmSyncConfigurationPlugin.AVAILABLE_STRATEGIES)
  instance.save()
}