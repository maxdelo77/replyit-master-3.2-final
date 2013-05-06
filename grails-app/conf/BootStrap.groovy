import com.sapienter.jbilling.server.pricing.db.RateCardDTO
import com.sapienter.jbilling.server.pricing.db.RateCardDAS
import com.sapienter.jbilling.server.pricing.RateCardBL
import com.sapienter.jbilling.server.util.Context
import com.sapienter.jbilling.client.process.JobScheduler

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

class BootStrap {

    def init = { servletContext ->
        // register rate card beans on startup
        for (RateCardDTO rateCard : new RateCardDAS().findAll()) {
            new RateCardBL(rateCard).registerSpringBeans();
        }

        // schedule jbilling background processes
        def schedulerBootstrapHelper = Context.getBean("schedulerBootstrapHelper");
        schedulerBootstrapHelper.scheduleBatchJobs();
        schedulerBootstrapHelper.schedulePluggableTasks();

        // start up the job scheduler
        JobScheduler.getInstance().start();
    }

    def destroy = {
        // shut down the job scheduler
        JobScheduler.getInstance().shutdown();
    }
}
