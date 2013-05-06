/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2011 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jbilling.

 jbilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jbilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.apache.log4j.*

/*
    Load configuration files from the set "JBILLING_HOME" path (provided as either
    an environment variable or a command line system property). External configuration
    files will override default settings.
 */

def appHome = System.getProperty("JBILLING_HOME") ?: System.getenv("JBILLING_HOME")

if (appHome) {
    println "Loading configuration files from JBILLING_HOME = ${appHome}"
    grails.config.locations = [
            "file:${appHome}/${appName}-Config.groovy",
            "file:${appHome}/${appName}-DataSource.groovy"
    ]

} else {
    appHome = new File("../${appName}")
    if (appHome.listFiles({dir, file -> file ==~ /${appName}-.*\.groovy/} as FilenameFilter )) {
        println "Loading configuration files from ${appHome.canonicalPath}"
        grails.config.locations = [
                "file:${appHome.canonicalPath}/${appName}-Config.groovy",
                "file:${appHome.canonicalPath}/${appName}-DataSource.groovy"
        ]

        println "Setting JBILLING_HOME to ${appHome.canonicalPath}"
        System.setProperty("JBILLING_HOME", appHome.canonicalPath)

    } else {
        println "Loading configuration files from classpath"
    }
}

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']


// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// use the jQuery javascript library
grails.views.javascript.library="jquery"
// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password', 'creditCard', 'creditCardDTO']

// enable query caching by default
grails.hibernate.cache.queries = false

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://www.changeme.com"
    }
    // see issue http://jira.grails.org/browse/GRAILS-7598
}


/*
    Logging
 */
// log4j configuration
log4j = {
    appenders {
        console name: 'stdout',Threshold: "INFO", Target: "System.out",  layout:pattern(conversionPattern: '%d %-5r %-5p [%c] (%t:%x) %m%n')
        rollingFile name: 'jbilling', file: 'logs/jbilling.log',layout:pattern(conversionPattern: '%d %-5r %-5p [%c] (%t:%x) %m%n')
        rollingFile name: 'sql', file: 'logs/sql.log', layout:pattern(conversionPattern: '%d %-5r %-5p [%c] (%t:%x) %m%n')
    }

    root {
        info 'stdout'
        additivity: false
    }

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.apache.catalina'

    info   'com.mchange'

    debug additivity: false, jbilling: "com.sapienter.jbilling"
    info  additivity: false, jbilling: "grails.app"
    debug additivity: false, jbilling: "grails.app.service"
    debug additivity: false, jbilling: "grails.app.controller"
    info additivity: false,  jbilling: "com.sapienter.jbilling.client.authentication.CompanyUserRememberMeFilter"

//      Hibernate logging:
//      org.hibernate.SQL           Log all SQL DML statements as they are executed
//      org.hibernate.type          Log all JDBC parameters
//      org.hibernate.tool.hbm2ddl  Log all SQL DDL statements as they are executed
//      org.hibernate.pretty        Log the state of all entities (max 20 entities) associated with the session at flush time
//      org.hibernate.cache         Log all second-level cache activity
//      org.hibernate.transaction   Log transaction related activity
//      org.hibernate.jdbc          Log all JDBC resource acquisition
//      org.hibernate.hql.ast.AST   Log HQL and SQL ASTs during query parsing
//      org.hibernate.secure        Log all JAAS authorization requests
//      org.hibernate               Log everything. This is a lot of information but it is useful for troubleshooting

    // debug additivity: false, sql: "org.hibernate.SQL"
}


/*
    Static web resources
 */
grails.resources.modules = {
    'core' {
        defaultBundle 'core-ui'

        resource url: '/css/all.css', attrs: [ media: 'screen' ]
        resource url: '/css/lt7.css', attrs: [ media: 'screen' ],
                 wrapper: { s -> "<!--[if lt IE 8]>$s<![endif]-->" }
    }

    'ui' {
        dependsOn 'jquery'
        defaultBundle 'core-ui'

        resource url: '/js/main.js', disposition: 'head'
        resource url: '/js/datatable.js', disposition: 'head'
        resource url: '/js/slideBlock.js', disposition: 'head'
    }

    'input' {
        defaultBundle "input"

        resource url: '/js/form.js', disposition: 'head'
        resource url: '/js/checkbox.js', disposition: 'head'
        resource url: '/js/clearinput.js', disposition: 'head'
    }

    'panels' {
        defaultBundle 'panels'

        resource url: '/js/panels.js', disposition: 'head'
    }

    'jquery-validate' {
        defaultBundle "jquery-validate"

        resource url: '/js/jquery-validate/jquery.validate.min.js', disposition: 'head'
        resource url: '/js/jquery-validate/jquery.metadata.js', disposition: 'head'
        resource url: '/js/jquery-validate/additional-methods.min.js', disposition: 'head'
    }

    overrides {
		'jquery-theme' {
			resource id:'theme', url:'/jquery-ui/themes/jbilling/jquery-ui-1.8.7.custom.css'
		}
    }
}


/*
    Documentation
 */
grails.doc.authors="Emiliano Conde, Brian Cowdery, Emir Calabuch, Lucas Pickstone, Vikas Bodani, Crystal Bourque"
grails.doc.license="AGPL v3"
grails.doc.images=new File("src/docs/images")
grails.doc.api.org.springframework="http://static.springsource.org/spring/docs/3.0.x/javadoc-api/"
grails.doc.api.org.hibernate="http://docs.jboss.org/hibernate/stable/core/javadocs/"
grails.doc.api.java="http://docs.oracle.com/javase/6/docs/api/"

//gdoc aliases
grails.doc.alias.userGuide="1. jBilling User Guide"
grails.doc.alias.integrationGuide="2. jBilling Integration Guide"



/*
    Spring Security
 */
// require authentication on all URL's
grails.plugins.springsecurity.rejectIfNoRule = false

// failure url
grails.plugins.springsecurity.failureHandler.defaultFailureUrl = '/login/authfail?login_error=1'

// remember me cookies
grails.plugins.springsecurity.rememberMe.cookieName = "jbilling_remember_me"
grails.plugins.springsecurity.rememberMe.key = "xANgU6Y7lJVhI"

// allow user switching
grails.plugins.springsecurity.useSwitchUserFilter = true
grails.plugins.springsecurity.switchUser.targetUrl = '/user/reload'


// static security rules 
grails.plugins.springsecurity.controllerAnnotations.staticRules = [
        '/services/**': ['IS_AUTHENTICATED_FULLY','API_120'],
        '/hessian/**': ['IS_AUTHENTICATED_FULLY','API_120'],
        '/httpinvoker/**': ['IS_AUTHENTICATED_FULLY','API_120'],
        '/j_spring_security_switch_user': ["hasAnyRole('USER_SWITCHING_110', 'USER_SWITCHING_111')",'IS_AUTHENTICATED_FULLY']
]

// IP address restrictions to limit access to known systems (always use with web-services in production environments!)
/*
grails.plugins.springsecurity.ipRestrictions = [
        '/services/**': ['192.168.0.110'],
        '/hessian/**': ['192.168.0.110','192.168.0.111'],
        '/httpinvoker/**': ['192.168.0.0/24']
]
*/

// configure which URL's require HTTP and which require HTTPS
/*
portMapper.httpPort = 8080
portMapper.httpsPort = 8443

grails.plugins.springsecurity.secureChannel.definition = [
    '/services/**': 'REQUIRES_SECURE_CHANNEL',
    '/hessian/**': 'REQUIRES_SECURE_CHANNEL',
    '/httpinvoker/**': 'REQUIRES_SECURE_CHANNEL',
    '/version': 'REQUIRES_INSECURE_CHANNEL',
    '/css/**': 'ANY_CHANNEL',
    '/images/**': 'ANY_CHANNEL'
]
*/

// basic HTTP authentication filter for web-services
grails.plugins.springsecurity.useBasicAuth = true
grails.plugins.springsecurity.basic.realmName = "jBilling Web Services"

// authentication filter configuration
grails.plugins.springsecurity.filterChain.chainMap = [
        '/services/**': 'JOINED_FILTERS,-exceptionTranslationFilter, -securityContextPersistenceFilter',
        '/hessian/**': 'JOINED_FILTERS,-exceptionTranslationFilter, -securityContextPersistenceFilter',
        '/httpinvoker/**': 'statelessSecurityContextPersistenceFilter,staticAuthenticationProcessingFilter,securityContextHolderAwareRequestFilter,basicExceptionTranslationFilter,filterInvocationInterceptor',
        '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter, -statelessSecurityContextPersistenceFilter'
]

// voter configuration
grails.plugins.springsecurity.voterNames = ['authenticatedVoter', 'roleVoter', 'permissionVoter', 'webExpressionVoter']
// Valid Company Invoice Logo Image Type
validImageExtensions = ['image/png', 'image/jpeg', 'image/gif']

grails.plugins.springsecurity.useSecurityEventListener = true

//events published by the provider manager
grails.plugins.springsecurity.onAuthenticationSuccessEvent = { e, appCtx ->
    appCtx.getBean("appAuthResultHandler").loginSuccess(e)
}

grails.plugins.springsecurity.onAbstractAuthenticationFailureEvent = { e, appCtx ->
    appCtx.getBean("appAuthResultHandler").loginFailure(e)
}

// Valid Company Invoice Logo Image Type
validImageExtensions = ['image/png', 'image/jpeg', 'image/gif']
