import com.energizedwork.grails.support.PluginsList
import org.codehaus.groovy.grails.commons.cfg.ConfigurationHelper

includeTargets << grailsScript("_GrailsPluginDev")
includeTargets << grailsScript("ReleasePlugin")

target ('default': 'Creates plugin release in local directory') {
    packagePlugin()
    checkLicense()

    releaseLocation = "/$releaseDirectory/grails-${pluginName}"
    latestRelease = "${releaseLocation}/tags/LATEST_RELEASE"
    versionedRelease = "${releaseLocation}/tags/RELEASE_${plugin.version.toString().replaceAll('\\.','_')}"

    [latestRelease, versionedRelease].each { dir ->
        ant.mkdir(dir:dir)
        ant.copy(file: pluginZip, todir: dir, overwrite:true)
        ant.copy(file: "${basedir}/plugin.xml", todir: dir, overwrite:true)
    }

    PluginsList pluginsList = new PluginsList(new File(releaseDirectory))
    pluginsList.add new File("${basedir}/plugin.xml"), releaseUrl
    pluginsList.writeOut()
}

private String getReleaseDirectory() {
    return buildConfig.grails.plugin.release.directory ?: '/tmp'
}

private String getReleaseUrl() {
    return buildConfig.grails.plugin.release.url ?: 'http://localhost'    
}

@Lazy ConfigObject buildConfig = {
    ConfigObject config = new ConfigSlurper().parse(new File("${basedir}/grails-app/conf/BuildConfig.groovy").toURI().toURL())
    ConfigurationHelper.initConfig(config)

    return config
}()