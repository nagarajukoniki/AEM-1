package com.tda.common.models;

/**
 * The enumeration that keeps track of which valid run modes are possible
 */
public enum RunMode {
	DEV("dev"), QA("qa"), STAGE("stage"), PREPROD("pre-prod"), PROD("prod");

	private final String runModeName;

	RunMode(String runModeName) {
		this.runModeName = runModeName;
	}

	public static RunMode findRunMode(String runMode) throws Exception {
		for (RunMode runModeToCheck : values()) {
			if (runModeToCheck.getRunModeName().equals(runMode)) {
				return runModeToCheck;
			}
		}
		return null;
	}

	/**
	 * @return returns the name of the specific run mode
	 */
	public String getRunModeName() {
		return runModeName;
	}
}
