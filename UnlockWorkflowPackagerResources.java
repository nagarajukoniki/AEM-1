package com.tda.apac.core.workflows;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Component(name = "Unlock Workflow Packager Resources -Component", service = WorkflowProcess.class, immediate = true, property = {
		"process.label = Unlock Workflow Packager Resources Process" }, configurationPid = "com.tda.apac.core.workflows.UnlockWorkflowPackagerResources")

public class UnlockWorkflowPackagerResources implements WorkflowProcess {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	ResourceResolver resourceResolver;

	@Override
	public void execute(WorkItem workItem, WorkflowSession wfsession, MetaDataMap metaDataMap)
			throws WorkflowException {
		try {
			resourceResolver = wfsession.adaptTo(ResourceResolver.class);
			String payload = workItem.getWorkflowData().getPayload().toString();
			log.info("Payload ::::: " + payload);
			String workflowPackagerPage = payload + "/jcr:content/vlt:definition/filter";
			Resource resource = resourceResolver.getResource(workflowPackagerPage);
			Node pageFilterNode = resource.adaptTo(Node.class);
			if (pageFilterNode.hasNodes()) {
				NodeIterator resourceNodes = pageFilterNode.getNodes();
				while (resourceNodes.hasNext()) {
					try {
						Node childNode = (Node) resourceNodes.next();

						String pagePath = childNode.hasProperty("root") ? childNode.getProperty("root").getString()
								: "";
						log.info("page path ::: " + pagePath);
						if (!"".equalsIgnoreCase(pagePath)) {

							validatePageLock(pagePath, wfsession, workItem);

						}

					} catch (Exception e) {

					}
				}
			}

		} catch (

		Exception e) {
			log.error("Exception in update workflow packager  main method  :" + e);
		}
	}

	public void validatePageLock(String pagePath, WorkflowSession wfsession, WorkItem workItem) {
		try {
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			if (pageManager != null) {
				Page page = pageManager.getPage(pagePath);
				if (page != null) {
					boolean isPageLocked = page.isLocked();
					if (isPageLocked) {
						page.unlock();
						log.info(":::::page is unlocked");
					}
				} else {
					log.info(":::::Attached asset is not a page");
				}
			}
		} catch (Exception e) {
			log.error(":::::Issue in validatepagelock method");
		}

	}

}
