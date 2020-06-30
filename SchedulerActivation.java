package com.tda.apac.core.workflows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(name = "Scheduler Activation Process -Component", service = WorkflowProcess.class, immediate = true, property = {
		"process.label=Scheduler Activation Process" }, configurationPid = "com.tda.apac.core.workflows.SchedulerActivation")

public class SchedulerActivation implements WorkflowProcess {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	ResourceResolver resourceResolver;
	String scheduledPublishDate = "";
	String scheduledActivationDate = "";

	@Override
	public void execute(WorkItem workItem, WorkflowSession wfsession, MetaDataMap metaDataMap)
			throws WorkflowException {
		try {
			log.info("Started Scheduler Activation Process Workflow");
			String payload = workItem.getWorkflowData().getPayload().toString();
			log.info("Payload ::::: " + payload);
			scheduledPublishDate = workItem.getWorkflowData().getMetaDataMap().get("schedulepublishdate").toString();

			log.info("scheduledPublishDate is  :: " + scheduledPublishDate);

			boolean activationFlag = workItem.getWorkflowData().getMetaDataMap().containsKey("scheduleactivationdate");
			boolean deployNowFlag = workItem.getWorkflowData().getMetaDataMap().containsKey("deploynow");
			
			if (deployNowFlag) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				Date date1 = new Date();
				String currentDateTime = formatter.format(date1);
				scheduledActivationDate = currentDateTime;
				log.info("System time ::::::" + currentDateTime);
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
				Date date = format.parse(currentDateTime);
				workItem.getWorkflowData().getMetaDataMap().put("scheduleactivationdate", date);
			} else if (activationFlag) {
				scheduledActivationDate = workItem.getWorkflowData().getMetaDataMap().get("scheduleactivationdate")
						.toString();
			} else {
				scheduledActivationDate = scheduledPublishDate;
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
				Date date = format.parse(scheduledPublishDate);
				workItem.getWorkflowData().getMetaDataMap().put("scheduleactivationdate", date);
			}

			log.info("scheduledActivationDate is  :: " + scheduledActivationDate);

			if (!"".equalsIgnoreCase(scheduledActivationDate)) {
				// ZonedDateTime zonedDateTime = ZonedDateTime.parse(scheduledActivationDate);
				// Date date = Date.from(zonedDateTime.toInstant());

				DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
				Date date = format.parse(scheduledActivationDate);

				log.info("date :: " + date);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.getTimeInMillis();
				log.info("calender in milli seconds :: " + calendar.getTimeInMillis() + " :: time zone "
						+ calendar.getTimeZone());
				workItem.getWorkflowData().getMetaDataMap().put("absoluteTime", calendar.getTimeInMillis());
			}
		} catch (Exception e) {
			log.error("Scheduler Activation Process Exception:" + e);
		}
	}
}
