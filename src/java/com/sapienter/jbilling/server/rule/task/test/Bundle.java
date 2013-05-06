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

package com.sapienter.jbilling.server.rule.task.test;

import java.util.LinkedList;
import java.util.List;

public class Bundle {
    private Product originalProduct;
    private List<Product> replacementProducts;

    public Bundle() {
        originalProduct = null;
        replacementProducts = new LinkedList<Product>();
    }

    public void setOriginalProduct(Product originalProduct) {
        this.originalProduct = originalProduct;
    }

    public Product getOriginalProduct() {
        return originalProduct;
    }

    public void setReplacementProducts(
            List<Product> replacementProducts) {
        this.replacementProducts = replacementProducts;
    }

    public List<Product> getReplacementProducts() {
        return replacementProducts;
    }

    public void addReplacementProduct(Product product) {
        replacementProducts.add(product);
    }
}
