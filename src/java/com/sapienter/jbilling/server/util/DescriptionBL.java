/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */

package com.sapienter.jbilling.server.util;

import java.util.Collection;


import com.sapienter.jbilling.server.util.db.InternationalDescriptionDAS;

public class DescriptionBL {
    private InternationalDescriptionDAS descriptionDas;
    
    public DescriptionBL() {
        init(); 
    }
    
    void init()  {
        descriptionDas = (InternationalDescriptionDAS) Context.getBean(Context.Name.DESCRIPTION_DAS);
    }
    
    public void delete(String table, Integer foreignId) {
        Collection toDelete = descriptionDas.findByTable_Row(table, 
                foreignId);
                
        toDelete.clear(); // this would be cool if it worked.
    }
}
