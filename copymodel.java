package com.tda.apac.core.models;


import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tda.apac.core.models.TDACommonUtil;

@Model(adaptables = Resource.class)
public class CopyModel {
	
private static final Logger logger = LoggerFactory.getLogger(CopyModel.class);


@Inject
@Optional
public String headLine;

@Inject
@Optional
public String subHeadLine;

@Inject
@Optional
public String copy;

@Inject
@Optional
public String copyTwo;

@Inject
@Optional
public String ctaButtonPlainText;

@Inject
@Optional
public String ctaButtonHref;

@Inject
@Optional
public String ctaButtonType;

public String getHeadLine() {
	return headLine;
}

public String getSubHeadLine() {
	return subHeadLine;
}
public String getCopy() {
	return copy;
}
public String getCopyTwo() {
	return copyTwo;
}
public String getCtaButtonPlainText() {
	return ctaButtonPlainText;
}
public String getCtaButtonHref() {
	return TDACommonUtil.getValidPath(ctaButtonHref);
}
public String getCtaButtonType() {
	return ctaButtonType;
}
}
