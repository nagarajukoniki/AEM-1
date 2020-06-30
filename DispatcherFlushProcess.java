package com.tda.apac.core.workflows;

import java.util.Map;

import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.acs.commons.replication.dispatcher.DispatcherFlusher;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentFilter;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.ReplicationResult;
import com.tda.apac.core.utils.WorkflowUtils;

@Component(name = "APAC Dispatcher Flush Process-Component", service = WorkflowProcess.class,immediate = true, property = {
                                "process.label=APAC Dispatcher Flush Process" }, configurationPid = "com.tda.apac.core.workflows.DispatcherFlushProcess")

public class DispatcherFlushProcess implements WorkflowProcess {
protected final Logger log = LoggerFactory.getLogger(this.getClass());
@Reference
private DispatcherFlusher dispatcherFlusher;

ResourceResolver resourceResolver;

@Override
public void execute(WorkItem workItem, WorkflowSession wfsession, MetaDataMap metaDataMap) throws WorkflowException {
		try {
			log.info("Start of APAC DispatcherFlushProcess Workflow");
			String args[] = WorkflowUtils.buildArguments(metaDataMap);
			resourceResolver = wfsession.adaptTo(ResourceResolver.class);					
			log.info("isApprover from previous step:"+workItem.getWorkflowData().getMetaDataMap().get("isApprover"));
			String flushAgentId = WorkflowUtils.getValueFromArgs(WorkflowUtils.Arguments.DISPATCHER_FLUSH_AGENT_ID.getArgumentName(), args);
			String payload = workItem.getWorkflowData().getPayload().toString();
			ReplicationOptions dispatcherFlushOption = new ReplicationOptions();
			if (flushAgentId != null && !flushAgentId.isEmpty()) {
				dispatcherFlushOption.setFilter(new AgentFilter() {
					public boolean isIncluded(final Agent agent) {
						boolean agentIsIncluded = (findAgentIdforDispatcherFlush(flushAgentId, agent.getId()));
						return agentIsIncluded;
					}
				});
				final Map<Agent, ReplicationResult> results =dispatcherFlusher.flush(resourceResolver, ReplicationActionType.ACTIVATE, true,dispatcherFlushOption.getFilter(), payload);
					 for (final Map.Entry<Agent, ReplicationResult> entry : results.entrySet()) {
		                    log.info("Agent Key:"+entry.getKey().getId()+"; Agent Value: "+entry.getValue().isSuccess());
		                }
			}
			//Invalidate Payload Reference files
            Resource asset = resourceResolver.getResource(payload);
            if(!DamUtil.isAsset(asset)) {
            Resource resource = resourceResolver.getResource(payload+"/jcr:content");
            Node pageNode = resource.adaptTo(Node.class);
            AssetReferenceSearch referenceSearch = new AssetReferenceSearch(pageNode,"/content/",resourceResolver);
            Map<String,Asset> dependantAssetMap = referenceSearch.search();
            for (Map.Entry<String, Asset> entry : dependantAssetMap.entrySet()) {
            	dispatcherFlusher.flush(resourceResolver, ReplicationActionType.ACTIVATE, true,dispatcherFlushOption.getFilter(), entry.getKey());
            	log.info("Payload Path: "+payload+"  Asset FileName:"+entry.getKey());            
            }
            }
			
		}	
		
		catch (Exception e) {
			log.error("exception:" + e);
		} finally {
			if(resourceResolver != null && resourceResolver.isLive()) {
				resourceResolver.close();
			}
		}
		 log.info("End of APAC DispatcherFlushProcess Workflow");
	}


protected boolean findAgentIdforDispatcherFlush(String flushAgentId,String AgentId) {
	        String flushAgent =flushAgentId.trim();
	        if(AgentId.trim().startsWith(flushAgent)) {
	        	return true;	            
	 }
	return false;
}
}
