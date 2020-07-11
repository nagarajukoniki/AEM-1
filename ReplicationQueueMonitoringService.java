package com.tda.common.schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.adobe.acs.commons.email.EmailService;
import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tda.common.utils.CurrentRunMode;
import com.tda.common.utils.Resource;
import com.tda.common.utils.WorkflowUtils;

@Component(immediate = true, service = ReplicationQueueMonitoringService.class)
@Designate(ocd = ReplicationQueueMonitoringService.Configuration.class)
public class ReplicationQueueMonitoringService implements Runnable {

	@ObjectClassDefinition(name = "Replication Queue Monitoring Service Configuration", description = "Replication Queue Monitoring Service for finding blocked queue list and send email to tda-administrators group")
	public @interface Configuration {

		/**
		 * serviceEnabled
		 * 
		 * @return serviceEnabled
		 */
		@AttributeDefinition(name = "Enabled", description = "Enable Scheduler", type = AttributeType.BOOLEAN)
		boolean serviceEnabled() default false;

		/**
		 * schedulerExpression
		 * 
		 * @return schedulerExpression
		 */
		@AttributeDefinition(name = "Scheduler Expression", description = "Cron-job expression. Default: run every five minutes with 0 0/5 * * * ?", type = AttributeType.STRING)
		String schedulerExpression() default "0 0/5 * * * ?";

		/**
		 * schedulerExpression
		 * 
		 * @return schedulerExpression
		 */
		@AttributeDefinition(name = "Group Name", description = "Sending email to all group members", type = AttributeType.STRING)
		String groupName() default "tda-administrators";
	}

	@Reference
	private Scheduler scheduler;
	private int schedulerID;

	@Reference
	private ResourceResolverFactory resolverFactory;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Reference
	private AgentManager agentManager;
	private boolean isQueueBlocked = false;
	private ArrayList<String> blockedAgentList;

	private String emailTemplate = "/etc/notification/email/common/replication_queue_status_email_template.txt";
	private String emailGroupName = "tda-administrators";
	private static final String DEFAULT_ADMINISTRATORS_GROUP = "tda-administrators";

	@Reference
	private EmailService emailService;

	@Activate
	protected void activate(ReplicationQueueMonitoringService.Configuration config) {
		schedulerID = "Replication Queue Status".hashCode();
		addScheduler(config);
		if (config.groupName() != null && !config.groupName().isEmpty()) {
			emailGroupName = config.groupName();
		} else {
			emailGroupName = DEFAULT_ADMINISTRATORS_GROUP;
		}
	}

	@Modified
	protected void modified(ReplicationQueueMonitoringService.Configuration config) {
		removeScheduler();
		schedulerID = "Replication Queue Status".hashCode();
		if (config.groupName() != null && !config.groupName().isEmpty()) {
			emailGroupName = config.groupName();
		} else {
			emailGroupName = DEFAULT_ADMINISTRATORS_GROUP;
		}
		addScheduler(config);
	}

	@Deactivate
	protected void deactivate(ReplicationQueueMonitoringService.Configuration config) {
		removeScheduler();
	}

	/**
	 * Remove a scheduler based on the scheduler ID
	 */
	private void removeScheduler() {
		logger.debug("Removing Replication Queue Status Job '{}'", schedulerID);
		scheduler.unschedule(String.valueOf(schedulerID));
	}

	/**
	 * Add a scheduler based on the scheduler ID
	 */
	private void addScheduler(ReplicationQueueMonitoringService.Configuration config) {
		if (config.serviceEnabled()) {
			ScheduleOptions sopts = scheduler.EXPR(config.schedulerExpression());
			sopts.name(String.valueOf(schedulerID));
			sopts.canRunConcurrently(false);
			scheduler.schedule(this, sopts);
			logger.debug(" Replication Queue Status Scheduler added succesfully");
		} else {
			logger.debug("Replication Queue Status SchedulerJ ob is Disabled, no scheduler job created");
		}
	}

	@Override
	public void run() {
		logger.info("Inside Replication Queue Monitoring Service run Method");
		try {

			isQueueBlocked = checkReplicationQueueIsBlocked();
			logger.info("isQueueBlocked:" + isQueueBlocked);
			if (isQueueBlocked == true)
				sendEamil();
		} catch (Exception e) {
			logger.error("Exception in ReplicationQueue Scheduler job" + e.getMessage());
			e.printStackTrace();
		}

		logger.info("Completed  Replication Queue Monitoring Service Scheduler Job");
	}

	protected String[] getEmailAddrs(String groupName, ResourceResolver payloadResource) {
		return WorkflowUtils.getEmailAddrsFromUserPath(payloadResource, groupName);
	}

	/**
	 * Verifying Replication Queue is blocked or not The method returns true/false
	 * if replication queue is blocked then return true otherwise false
	 * 
	 */
	public boolean checkReplicationQueueIsBlocked() {
		boolean isQueueBlocked = false;
		blockedAgentList = new ArrayList<String>();
		Map<String, Agent> agents = agentManager.getAgents();
		for (String name : agents.keySet()) {
			if (agentManager.getAgents().get(name).getQueue() != null
					&& agentManager.getAgents().get(name).getQueue().getStatus() != null) {
				if (agentManager.getAgents().get(name).getQueue().getStatus().getProcessingSince() == 0
						&& agentManager.getAgents().get(name).getQueue().entries().size() > 0) {
					setBlockedAgentList(name);
					isQueueBlocked = true;
				}
			}
		}
		return isQueueBlocked;
	}

	/**
	 * Sedning email to all group members in tda-administrators group(group name
	 * configurable through OSGI Configuration) if replication queue is blocked
	 * 
	 */

	protected void sendEamil() {
		ResourceResolver resourceResolver = null;
		try {
			resourceResolver = Resource.getResourceResolver(resolverFactory);
			String[] emailTo = getEmailAddrs(emailGroupName, resourceResolver);
			logger.debug("emailTo members size:" + emailTo.length);
			if (emailTo.length > 0) {
				Map<String, String> emailParams = new HashMap<>();
				StringBuffer sb = new StringBuffer();
				sb.append("\n");
				int index = 1;
				for (String blockedAgent : getBlockedAgentList()) {
					sb.append("\t" + index++ + ".  " + blockedAgent);
					sb.append("\n");
				}
				logger.debug("currentRunMode:" + CurrentRunMode.currentRunMode);
				emailParams.put("runMode", CurrentRunMode.currentRunMode);
				emailParams.put("emailBody", sb.toString());
				List<String> failureList = emailService.sendEmail(emailTemplate, emailParams, emailTo);
				if (failureList != null && failureList.isEmpty()) {
					logger.debug("Email sent successfully to {} recipients", emailTo.length);
				} else {
					StringBuffer emailBuffer = new StringBuffer();
					for (String emailToIndividualAddress : emailTo) {
						emailBuffer.append(emailToIndividualAddress).append('|');
					}
					logger.debug("Email sent failed to {} ", emailBuffer.toString());
				}
			} else {
				logger.debug("Email Group is Empty");
			}

		} catch (Exception e) {
			logger.error("Exception in Inside Replication Queue Monitoring Service Scheduler job" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}

	}

	public ArrayList<String> getBlockedAgentList() {
		return blockedAgentList;
	}

	public void setBlockedAgentList(String agentName) {
		blockedAgentList.add(agentName);
	}

}
