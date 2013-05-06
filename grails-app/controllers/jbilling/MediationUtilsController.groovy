package jbilling

import com.sapienter.jbilling.common.SessionInternalError

class MediationUtilsController {

    def webServicesSession

    def index = { }

    def runAllMediation = {
        try {
            webServicesSession.triggerMediation()
            render "mediation executed"
        } catch (SessionInternalError sessionInternalError) {
            render "error executing mediation"
        }

    }
}
