// Jenkins Initial Setup Script
// Wird beim ersten Start ausgefuehrt

import jenkins.model.*
import hudson.security.*
import ru.yandex.qatools.allure.jenkins.tools.*

def instance = Jenkins.getInstance()

// === Admin User erstellen ===
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "admin123")
instance.setSecurityRealm(hudsonRealm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

// === Allure Commandline konfigurieren ===
def allureDesc = instance.getDescriptor(AllureCommandlineInstallation.class)
def allureInstall = new AllureCommandlineInstallation("Allure", "/opt/allure-2.29.0", [])
allureDesc.setInstallations(allureInstall)

instance.save()

println "=== Jenkins Setup abgeschlossen ==="
println "Admin-User: admin / admin123"
