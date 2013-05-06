import javax.crypto.spec.PBEParameterSpec;

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

import groovy.sql.Sql
import javax.crypto.*
import javax.crypto.spec.PBEKeySpec;

includeTargets << grailsScript("Init")
includeTargets << new File("${basedir}/scripts/Liquibase.groovy")

target(decrypt: "Decrypts credit card information.") {
	
	depends(parseArguments)
	def db = getDatabaseParameters(argsMap)
	
	def key = argsMap.key ?: "a_long_secret_key"
	def algo = argsMap.algorithm ?: "PBEWithMD5AndDES"
	def encoding = argsMap.encoding ?: "UTF-8"
	
	def secretKeyFactory = SecretKeyFactory.getInstance(algo)
	def SecretKey secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(key.toCharArray()))
	def Cipher cipher = Cipher.getInstance(algo)
	cipher.init(Cipher.DECRYPT_MODE, secretKey, new PBEParameterSpec("3c15277f2ddae664".decodeHex(), 15))
	
	def sql = Sql.newInstance(db.url, db.username, db.password, db.driver)

	def demorow = sql.firstRow("select cc_number, name from credit_card")
	try {
		def demonumber = new String(cipher.doFinal(demorow.cc_number.decodeHex()), encoding)
		def demoname = new String(cipher.doFinal(demorow.name.decodeHex()), encoding)
		
		Ant.input(
			message: """
WARNING: This operation decrypts credit card data in the database. Due to
the nature of the decryption process, the operation does not know if the
encryption key is the right one or not.
If the decryption key provided is incorrect, this operation will overwrite
credit card data with incorrect data.
This is the first row of encrypted data to be decrypted. Make sure the values
make sense. As always, backing up your data before proceeding is recommended.
C/C Number: ${demonumber}
Name on Card: ${demoname}
Are you sure you want to continue? """, 
			validargs: "y,n",
			addproperty: "password.warning")
			if (Ant.project.properties.'password.warning'.equalsIgnoreCase("n")) {
				return
			}
		
	} catch (Throwable e) {
		println "Error decrypting data. Possibly invalid encryption key."
		return
	}
	
	def total = 0
	
	sql.resultSetConcurrency = java.sql.ResultSet.CONCUR_UPDATABLE
	
	sql.eachRow("select * from credit_card") {
		def val = it.cc_number ? new String(cipher.doFinal(it.cc_number.decodeHex()), encoding) : null
		def name = it.name ? new String(cipher.doFinal(it.name.decodeHex()), encoding) : null
		
		if (val) {
			it.cc_number = val
		}
		if (name) {
			it.name = name
		}
		
		total++
	}
	
	sql.eachRow("select * from ach") {
		def routing = it.aba_routing ? new String(cipher.doFinal(it.aba_routing.decodeHex()), encoding) : null
		def account = it.bank_account ? new String(cipher.doFinal(it.bank_account.decodeHex()), encoding) : null
		def name = it.account_name ? new String(cipher.doFinal(it.account_name.decodeHex()), encoding) : null
		
		if (routing) {
			it.aba_routing = routing
		}
		if (account) {
			it.bank_account = account
		}
		if (name) {
			it.account_name = name
		}
		
		total++
	}
	
	sql.resultSetConcurrency = java.sql.ResultSet.CONCUR_READ_ONLY
	
	println "Total rows processed: ${total}"
}



setDefaultTarget(decrypt)