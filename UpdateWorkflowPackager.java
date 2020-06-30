package com.tda.apac.core.workflows;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.PayloadMap;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.checkout.AssetCheckoutService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Component(name = "Update Workflow Packager -Component", service = WorkflowProcess.class, immediate = true, property = {
		"process.label = Update Workflow Packager Process" }, configurationPid = "com.tda.apac.core.workflows.UpdateWorkflowPackager")

public class UpdateWorkflowPackager implements WorkflowProcess {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	ResourceResolver resourceResolver;
	int lockedpages = 0;
	int pageWorkflowInstances = 0;
	Boolean WFFlag = false;

	@Override
	public void execute(WorkItem workItem, WorkflowSession wfsession, MetaDataMap metaDataMap)
			throws WorkflowException {

		resourceResolver = wfsession.adaptTo(ResourceResolver.class);
		String payload = workItem.getWorkflowData().getPayload().toString();
		log.info("Payload ::::: " + payload);

		Resource payloadResource = resourceResolver.getResource(payload);
		String primaryType = payloadResource.getResourceType();
		log.info(" primary type :: " + primaryType);

		if ("dam:Asset".equalsIgnoreCase(primaryType)) {
			validateWorkflowInstances(payload, wfsession, workItem);
			/*log.info("2");
			boolean isAssetCheckedout = validateAssetLock(payloadResource, resourceResolver);
			log.info("3");*/
			setWorkflowFlagForAsset(payloadResource, workItem);
			log.info("4");

		} else if ("cq:Page".equalsIgnoreCase(primaryType)) {
			validateWorkflowInstances(payload, wfsession, workItem);
			String workflowPackagerPage = payload + "/jcr:content/vlt:definition/filter";
			Resource resource = resourceResolver.getResource(workflowPackagerPage);
			if (resource != null) {
				validateWorkflowPackagerLock(resource, wfsession, workItem);

			} else {

				validatePageLock(payload, wfsession, workItem);
				setWorkflowFlag(workItem);

			}
		}

		pageWorkflowInstances = 0;
		lockedpages = 0;

	}

	public void validateWorkflowPackagerLock(Resource resource, WorkflowSession wfsession, WorkItem workItem) {
		try {
			Node pageFilterNode = resource.adaptTo(Node.class);
			if (pageFilterNode.hasNodes()) {
				NodeIterator resourceNodes = pageFilterNode.getNodes();
				while (resourceNodes.hasNext()) {
					try {
						Node childNode = (Node) resourceNodes.next();
						String pagePath = childNode.hasProperty("root") ? childNode.getProperty("root").getString()
								: "";
						log.info("page path ::: " + pagePath + "lockedpages :" + lockedpages);
						if (!"".equalsIgnoreCase(pagePath)) {
							validateWorkflowInstances(pagePath, wfsession, workItem);
							validatePageLock(pagePath, wfsession, workItem);
							setWorkflowFlag(workItem);
						}

					} catch (Exception e) {

					}
				}
			}
		} catch (Exception e) {
			log.error("Exception in validateWorkflowPackagerLock method" + e);
		}

	}

	public void setWorkflowFlagForAsset(Resource payloadResource, WorkItem workItem) {

		if (pageWorkflowInstances > 0) {
			WFFlag = false;
			workItem.getWorkflowData().getMetaDataMap().put("wfflag", WFFlag);
			log.info("Asset is under workflow::::");
		} else {
			WFFlag = true;
			workItem.getWorkflowData().getMetaDataMap().put("wfflag", WFFlag);
			log.info("Asset is set to go  ::::");
		}

	}

	public boolean validateAssetLock(Resource payloadResource, ResourceResolver resourceResolver) {
		boolean isCheckedOut = true;
		try {
			Asset imgAsset = resourceResolver.adaptTo(Asset.class);
			AssetCheckoutService assetCheckoutStatus = resourceResolver.adaptTo(AssetCheckoutService.class);
			isCheckedOut = assetCheckoutStatus.isCheckedOut(imgAsset);
			log.info("isCheckedOut ::: " + isCheckedOut);
		} catch (Exception e) {
			log.error("Exception in validateAssetLock method");
			e.printStackTrace();
		}
		return isCheckedOut;
	}

	public void setWorkflowFlag(WorkItem workItem) {
		if (pageWorkflowInstances > 0) {
			WFFlag = false;
			workItem.getWorkflowData().getMetaDataMap().put("wfflag", WFFlag);
			log.info("***page is under workflow");

		} else if (lockedpages > 0) {
			WFFlag = false;
			workItem.getWorkflowData().getMetaDataMap().put("wfflag", WFFlag);
			log.info("***page is under locked");

		} else {
			WFFlag = true;
			workItem.getWorkflowData().getMetaDataMap().put("wfflag", WFFlag);
			log.info("***page is free to go with workflow");
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
						lockedpages++;
						log.info("page is locked :: " + page.getPath());
					}

				} else {
					log.info(":::::Attached asset is not a page");
				}
			}
		} catch (Exception e) {
			log.error("Issue in validatepagelock method");
		}

	}

	public void validateWorkflowInstances(String pagePath, WorkflowSession wfsession, WorkItem workItem) {
		PayloadMap payloadMap = resourceResolver.adaptTo(PayloadMap.class);
		int workflowInstancesList = payloadMap.getWorkflowInstances(pagePath, true).size();
		if (workflowInstancesList > 1) {
			pageWorkflowInstances++;
			log.info("Workflow is already running with the same payload ::: " + pagePath);
		}
	}

}
