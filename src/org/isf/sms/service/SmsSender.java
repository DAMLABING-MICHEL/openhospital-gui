/**
 * SmsThread.java - 31/gen/2014
 */
package org.isf.sms.service;

import java.util.ArrayList;
import java.util.Date;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.SmsParameters;
import org.isf.sms.model.Sms;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Mwithi
 */
public class SmsSender implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(SmsSender.class);

	private boolean running = true;
	private int delay = 10;
	
	/**
	 * 
	 */
	public SmsSender() {
		logger.info("SMS Sender started...");
		SmsParameters.getSmsParameters();
		delay = SmsParameters.LOOP;
		logger.info("SMS Sender loop set to " + delay + " seconds.");
	}

	public void run() {
		while (running) {
			logger.info("SMS Sender running...");
			IoOperations smsOp = new IoOperations();
			ArrayList<Sms> smsList;
			try {
				smsList = smsOp.getList();
			} catch (OHException e1) {
				smsList = new ArrayList<Sms>();
			}
			if (!smsList.isEmpty()) {
				logger.info("Found " + smsList.size() + " SMS to send");
				if (SmsParameters.MODE.equals("GSM")) {
					
					SmsSenderGSM sender = new SmsSenderGSM();
					if (sender.initialize()) {
						for (Sms sms : smsList) {
							if (sms.getSmsDateSched().before(new Date())) {
								boolean result = sender.sendSMS(sms, GeneralData.DEBUG);
								if (result) {
									sms.setSmsDateSent(new Date());
									try {
										smsOp.saveOrUpdate(sms);
									} catch (OHException e) {
										logger.error("==> OHEXEPTION : " + e.getMessage());
									}
									logger.debug("Sent");
								} else {
									logger.error("Not sent");
								}
							}
						}
						sender.terminate();
					} else {
						logger.error("SMS Sender GSM initialization error");
						logger.error("Stopping SMS Sender...");
						setRunning(false);
					}
					
				} else if (SmsParameters.MODE.equals("HTTP")) {
					
					SmsSenderHTTP sender = new SmsSenderHTTP();
					if (sender.initialize()) {
						for (Sms sms : smsList) {
							if (sms.getSmsDateSched().before(new Date())) {
								boolean result = sender.sendSMS(sms, GeneralData.DEBUG);
								if (result) {
									sms.setSmsDateSent(new Date());
									try {
										smsOp.saveOrUpdate(sms);
									} catch (OHException e) {
										logger.error("==> OHEXEPTION : " + e.getMessage());
									}
									logger.debug("Sent");
								} else {
									logger.error("Not sent");
								}
							}
						}
					} else {
						logger.error("SMS Sender HTTP initialization error");
						logger.error("Stopping HTTP Sender...");
						setRunning(false);
					}
				}
			} else {
				logger.debug("No SMS to send.");
			}
			try {
				Thread.sleep(delay * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
