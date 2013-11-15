package org.onetwo.common.web.view.jsp.link;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.onetwo.common.web.config.BaseSiteConfig;
import org.onetwo.common.web.csrf.CsrfPreventorFactory;
import org.onetwo.common.web.utils.RequestUtils;
import org.onetwo.common.web.view.jsp.BaseHtmlTag;
import org.onetwo.common.web.view.jsp.TagUtils;

public class LinkTag extends BaseHtmlTag<LinkTagBean>{

	private String template = TagUtils.getTagPage("html/link.jsp");
	
	private String dataMethod;
	private String dataConfirm;
	private String href;
	
	@Override
	public LinkTagBean createComponent() {
		return new LinkTagBean();
	}

	@Override
	protected void populateComponent() throws JspException {
		super.populateComponent();
		component.setDataConfirm(dataConfirm);
		component.setDataMethod(dataMethod);

		boolean safeUrl = BaseSiteConfig.getInstance().isSafeRequest();
		if(!href.startsWith(RequestUtils.HTTP_KEY) && !href.startsWith(RequestUtils.HTTPS_KEY) && safeUrl){
			href = CsrfPreventorFactory.getDefault().processSafeUrl(href, (HttpServletRequest)pageContext.getRequest(), (HttpServletResponse)pageContext.getResponse());
    	}

		component.setSafeUrl(safeUrl);
		component.setHref(href);

		setComponentIntoRequest(this.getClass().getSimpleName(), component);
	}

	protected int endTag()throws Exception {
		try {
			this.pageContext.include(getTemplate());
		} finally {
			clearComponentFromRequest(this.getClass().getSimpleName());
		}
		return EVAL_PAGE;
	}

	public void setDataMethod(String dataMethod) {
		this.dataMethod = dataMethod;
	}

	public void setDataConfirm(String dataConfirm) {
		this.dataConfirm = dataConfirm;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}