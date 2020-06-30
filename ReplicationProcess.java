package com.tda.apac.core.workflows;

import java.util.Map;
import javax.jcr.Node;
import javax.jcr.Session;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentFilter;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;
import com.tda.apac.core.utils.UserUtils;
import com.tda.apac.core.utils.WorkflowUtils;
import com.day.cq.dam.api.Asset;

@Component(name = "APAC Replication Process -Component", service = WorkflowProcess.class,immediate = true, property = {
                                "process.label=APAC Replication Process" }, configurationPid = "com.tda.apac.core.workflows.ReplicationProcess")

public class ReplicationProcess implements WorkflowProcess {
protected final Logger log = LoggerFactory.getLogger(this.getClass());
@Reference
private Replicator replicator;

ResourceResolver resourceResolver;

@Override
public void execute(WorkItem workItem, WorkflowSession wfsession, MetaDataMap metaDataMap) throws WorkflowException {
		try {	
			    log.info("Start of APAC ReplicationProcess Workflow");
			    String args[]=WorkflowUtils.buildArguments(metaDataMap);
			    String agentType = WorkflowUtils.getValueFromArgs(WorkflowUtils.Arguments.REPLICATION_TYPE.getArgumentName(), args);
			    String approverGroup = WorkflowUtils.getValueFromArgs(WorkflowUtils.Arguments.APPROVER_GROUP_NAME.getArgumentName(), args);
				resourceResolver = wfsession.adaptTo(ResourceResolver.class);	
				if(approverGroup!=null) {
					boolean isWorkflowRequesterApprover=UserUtils.isWorkflowRequesterApprover(resourceResolver, workItem.getWorkflow().getInitiator(),approverGroup);
					workItem.getWorkflowData().getMetaDataMap().put("isApprover", String.valueOf(isWorkflowRequesterApprover));	
				}else {
					workItem.getWorkflowData().getMetaDataMap().put("isApprover", "false");
				}
				ReplicationOptions opts = new ReplicationOptions();			 
				opts.setFilter(new AgentFilter() {
					public boolean isIncluded(final Agent agent) {
						boolean agentIsIncluded = (findAgentId(agentType,agent.getId()));
						return agentIsIncluded;
					}
			});			
			
			opts.setSuppressVersions(true);			
			Session session=resourceResolver.adaptTo(Session.class);
			replicator.replicate(session, ReplicationActionType.ACTIVATE,workItem.getContentPath(),opts);			
			//Asset Reference Code
            String payload = workItem.getWorkflowData().getPayload().toString();
            Resource asset = resourceResolver.getResource(payload);
            if(!DamUtil.isAsset(asset)) {
            Resource resource = resourceResolver.getResource(payload+"/jcr:content");
            Node pageNode = resource.adaptTo(Node.class);
            AssetReferenceSearch referenceSearch = new AssetReferenceSearch(pageNode,"/content/",resourceResolver);
            Map<String,Asset> dependantAssetMap = referenceSearch.search();
            opts.setSynchronous(true);         
            for (Map.Entry<String, Asset> entry : dependantAssetMap.entrySet())
                replicator.replicate (session, ReplicationActionType.ACTIVATE, entry.getKey(),opts);
            }
		}

		catch (Exception e) {
			log.error("APAC ReplicationProcess Exception:" + e);
		} finally {
			if(resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}
	}


protected boolean findAgentId(String agentType,String AgentId) {
	 for (String str : agentType.split(";")){
	        String trimmedStr = str.trim();
	        if(AgentId.startsWith(trimmedStr)) {
	        	return true;
	        }	       
	 }
	return false;
}
}
