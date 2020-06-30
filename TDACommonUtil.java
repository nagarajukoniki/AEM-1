package com.tda.apac.core.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.day.cq.wcm.api.WCMMode;
import com.tda.apac.core.beans.TextStyleBean;

public class TDACommonUtil {

	/**
	 * Wraps an error message in a common style
	 * 
	 * @param message
	 * @return
	 */
	public static String wrapEditMessage(String message) {
		return (message == null || message.isEmpty()) ? ""
				: "<center><div class=\"editMessage\">" + message + "</div></center>";
	};

	/**
	 * Wraps an error message in a common style
	 * 
	 * @param errors
	 * @return
	 */
	public static String wrapEditErrors(String errors) {
		return "<div class=\"editError\">" + errors + "</div>";
	}

	/**
	 * Creates an arrayList of integers. Used for sightly for loops
	 * 
	 * @param itemCount
	 * @return
	 */
	public static ArrayList<Integer> getItemList(int itemCount) {
		ArrayList<Integer> list = new ArrayList<Integer>(0);

		for (int i = 0; i < itemCount; i++) {
			list.add(i);
		}

		return list;
	}

	public static boolean isEditMode(WCMMode mode) {
		return mode == WCMMode.EDIT;
	}

	public static boolean isDesignMode(WCMMode mode) {
		return mode == WCMMode.DESIGN;
	}

	public static boolean isPreviewMode(WCMMode mode) {
		return mode == WCMMode.PREVIEW;
	}

	/**
	 * mentions target for the urlPath.
	 * 
	 * @param urlPath
	 * @return
	 */
	public static String getTarget(String newWin) {
		if (newWin == null) {
			newWin = "";
		}
		return "".equals(newWin) ? "" : "_blank";
	}

	/**
	 * appends .html to the url if the path contains content in it
	 * 
	 * @param urlPath
	 * @return
	 */
	public static String getValidPath(String urlPath) {
		if (urlPath == null) {
			urlPath = "";
		}
		return (urlPath.contains("/content/") && !urlPath.contains("/content/dam/")) ? urlPath.concat(".html")
				: urlPath;
	}

	public static TextStyleBean getFontStyle(String[] textStyle) {

		String bold = "bold";
		String italic = "italic";
		String underline = "underline";

		TextStyleBean fontStyle = new TextStyleBean();
		List<String> fontList = Arrays.asList(textStyle);

		if (fontList.contains("bold")) {
			fontStyle.setBoldStyle(bold);
		}
		if (fontList.contains("italic")) {
			fontStyle.setItalicStyle(italic);
		}
		if (fontList.contains("underline")) {
			fontStyle.setUnderlineStyle(underline);
		}

		return fontStyle;
	}

	public static String getStyleTag(String[] textStyle, String Color, String Alignment) {
		StringBuffer styleTag = new StringBuffer();
		String boldstyle = "";
		String italicstyle = "";
		String underlinestyle = "";
		String color = "";
		String align = "";
		if (textStyle != null) {
			TextStyleBean fontStyle = getFontStyle(textStyle);
			if (fontStyle.getBoldStyle() != null && fontStyle.getBoldStyle().trim().length() > 0) {
				boldstyle = "font-weight:" + fontStyle.getBoldStyle().trim() + ";";
			}
			if (fontStyle.getItalicStyle() != null && fontStyle.getItalicStyle().trim().length() > 0) {
				italicstyle = "font-style:" + fontStyle.getItalicStyle().trim() + ";";
			}
			if (fontStyle.getUnderlineStyle() != null && fontStyle.getUnderlineStyle().trim().length() > 0) {
				underlinestyle = "text-decoration:" + fontStyle.getUnderlineStyle().trim() + ";";
			}
		}
		if (Color != null && Color.trim().length() > 0) {
			color = "color:" + Color + ";";
		}
		if (Alignment != null && Alignment.trim().length() > 0) {
			align = "text-align:" + Alignment + ";";
		}

		if ((boldstyle != null && boldstyle.trim().length() > 0)
				|| (italicstyle != null && italicstyle.trim().length() > 0)
				|| (underlinestyle != null && underlinestyle.trim().length() > 0)
				|| (color != null && color.trim().length() > 0) || (align != null && align.trim().length() > 0)) {
			styleTag.append("style=\"");
			styleTag.append(boldstyle);
			styleTag.append(" ");
			styleTag.append(italicstyle);
			styleTag.append(" ");
			styleTag.append(underlinestyle);
			styleTag.append(" ");
			styleTag.append(color);
			styleTag.append(" ");
			styleTag.append(align);
			styleTag.append("\"");
		}

		return styleTag.toString();
	}
	
	/*
	 * Builds target based on file redirect window
	 * 
	 * @param redirectwindow,target
	 * 
	 * @return
	 */

	public String getRedirectWindow(String redirectWindow) {
		String target = "";
		if (redirectWindow.equals("true")) {
			target = "_blank";
		} else {
			target = "_self";
		}
		return target;
	}
	public static boolean isEmpty(String str) {
    	return (str != null && str.trim().length() > 0) ? false:true;
    }
	/**
	 * Builds image tag based on file reference path and alternative text.
	 * 
	 * @param fileReference, altText
	 * @return
	 */
	public static String getImageTag(String fileReference, String altText) {
		String iconImage = "";
		
		if (altText == null) altText = "";
		
		if (fileReference != null && fileReference.trim().length() > 0) {
			iconImage =  "<img class='svg-img' src='"+fileReference+"' alt='"+altText+"'>";
		}
		return iconImage;
	}
	 /**
		 * Builds image tag based on file reference path and alternative text.
		 * 
		 * @param fileReference, altText, cssClass, srcDefer
		 * @return
		 */
		public static String getImageTag(String fileReference, String altText, String cssClass, String src) {
		
			StringBuffer imgTag = new StringBuffer();
			
			if (isEmpty(fileReference)) {
				fileReference = "";
			} else {
				fileReference = "src-defer=\""+fileReference+"\"";
			}
			
			if (isEmpty(altText)) {
				altText = "alt=\"\"";
			} else {
				altText = "alt=\""+altText+"\"";
			}
			
			if (isEmpty(cssClass)) {
				cssClass = "";
			} else {
				cssClass = "class=\"" + cssClass + "\"";
			}
			
			if (isEmpty(src)) {
				src = "";
			} else {
				src = "src=\""+src+"\"";
			}
			
			imgTag.append("<img ");
			imgTag.append(cssClass);
			imgTag.append(" ");
			imgTag.append(altText);
			imgTag.append(" ");
			imgTag.append(fileReference);
			imgTag.append(" ");
			imgTag.append(src);
			imgTag.append(">");
			
			return imgTag.toString();
		}
		/**
		 * Builds a modal navigation link tag. Defaulted to empty.
		 * 
		 * @param linkPath,  linkTitle, linkText, target, cssClass
		 * @return
		 */
		
	    public static String getAnchorTag(String linkText, String linkPath, String linkTitle, String target, String cssClass,String dataNetwork, String dataEvents, String dataLinkName) {
			StringBuffer anchorTag = new StringBuffer();
			
			if (isEmpty(linkText)) {
				linkText = "";
			} 
			if (isEmpty(dataEvents)) {
				dataEvents = "";
			} else {
				dataEvents = "data-dl-site_events=\""+dataEvents+"\"";
				if (isEmpty(dataLinkName)) {
					dataLinkName = "";
				} else {
					dataLinkName = "data-dl-link.name=\""+dataLinkName+"\"";
				}
			}
			if (isEmpty(dataNetwork)) {
				dataNetwork = "";
			} else {
				dataNetwork = "data-dl-social_network=\""+dataNetwork+"\"";
			}
			
			if (isEmpty(target)) {
				target = "";
			} else {
				target = "target=\""+target+"\"";
			}
			
			if (isEmpty(cssClass)) {
				cssClass = "";
			} else {
				cssClass = "class=\""+cssClass+"\"";
			}
					
			if (isEmpty(linkPath)) {
				linkPath = "href=\"#\"";
			} else {
				linkPath = "href=\""+getValidPath(linkPath)+"\"";
			}
			
			if (isEmpty(linkTitle)) {
				linkTitle = "";
			} else {
				linkTitle = "title=\""+linkTitle+"\"";
			}
			
			
			anchorTag.append("<a ");
			anchorTag.append(linkTitle);
			anchorTag.append(" ");
			anchorTag.append(linkPath);
			anchorTag.append(" ");
			anchorTag.append(target);
			anchorTag.append(" ");
			anchorTag.append(cssClass);
			anchorTag.append(" ");
			anchorTag.append(dataEvents);
			anchorTag.append(" ");
			anchorTag.append(dataNetwork);
			anchorTag.append(" ");
			anchorTag.append(dataLinkName);
			anchorTag.append("/>");
			anchorTag.append(linkText);
			anchorTag.append("</a>");
			
			return anchorTag.toString();
		}
}
