package com.tda.apac.core.utils;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;


public class UserUtils {
    protected final static Logger log = LoggerFactory.getLogger(UserUtils.class);
	public final static boolean isWorkflowRequesterApprover(ResourceResolver resourceResolver,
			String principleName, String approverGroupName) {
		boolean isUserApproverAdmin = false;
		User user = null;
		Group approverAdminGroup = null;
		try {
			if (principleName != null) {
				Authorizable authorizable = getAuthorizable(resourceResolver, principleName);
				Authorizable approverAdminAuth = getAuthorizable(resourceResolver, approverGroupName);
				if (authorizable != null && !authorizable.isGroup()) {
					user = (User) authorizable;
					log.debug("logged-in user is authhorizable");
				}
				if (approverAdminAuth != null && approverAdminAuth.isGroup()) {
					approverAdminGroup = (Group) approverAdminAuth;
					log.debug("Approver Admin group  is authhorizable");
				}
				if (user != null && approverAdminGroup != null) {
					isUserApproverAdmin = approverAdminGroup.isMember(user);
					log.debug("User is member of Approver Admin group ");
				}
			}
		} catch (RepositoryException e) {
			log.error("Error while checkng user is super admin group or not", e);
		}
		log.debug("isUserApproverAdmin: " + isUserApproverAdmin);
		return isUserApproverAdmin;
	}
	public static Authorizable getAuthorizable(final ResourceResolver resourceResolver, final String userId) throws RepositoryException {
        UserManager userManager = resourceResolver.adaptTo(UserManager.class);
        return userManager.getAuthorizable(userId.trim());
    }


}
