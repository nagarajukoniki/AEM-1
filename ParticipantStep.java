package com.adobe.aem.core.models;

Hi Nandini
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
 
 
 
@Component(service=ParticipantStepChooser.class, property = {"chooser.label=Sample Implementation of dynamic participant chooser"})

public class ParticipantStep implements ParticipantStepChooser {
	 private static final Logger logger = LoggerFactory.getLogger(ParticipantStep.class);
     
     public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap)
       throws WorkflowException
     { 
            
   
    	                         
       Map<String, Object> map  = workItem.getWorkflowData().getMetaDataMap();
    	               
    	for (Map.Entry<String,Object> entry : map.entrySet()) { 
    		logger.info("################ Inside the SampleProcessStepChooserImpl MAP Iteration For values");
    		 logger.info("Key = " + entry.getKey() + 
                             ", Value = " + entry.getValue().toString()); 
               }
    
    	 logger.info("####### MetaData "+workItem.getWorkflowData().getMetaDataMap());
         logger.info("####### GetContentEditor : " + workItem.getWorkflowData().getMetaDataMap().get("contenteditor"));
         
         
        return workItem.getWorkflowData().getMetaDataMap().get("contenteditor").toString();
        
     }
}
