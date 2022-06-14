package com.liferay.segments.web.internal.configuration.display;


import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactory;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.segments.configuration.SegmentsConfiguration;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import org.osgi.service.component.annotations.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component(service = ConfigurationScreen.class)
public class SegmentsConfigurationScreen implements ConfigurationScreen{

	private ServletContext _servletContext;

	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

	@Override
	public String getCategoryKey() {
		return "segments";
	}

	@Override
	public String getKey() {
		return "segments-service";
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(
			ResourceBundleUtil.getBundle(locale, SegmentsConfigurationScreen.class),
			"segments-service");
	}
	@Override
	public String getScope() {
		return "system";
	}

	public void render(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/HelloWorld.jsp");

			httpServletRequest.setAttribute(
				SegmentsConfiguration.class.getName(),
				_segmentsConfigurationProvider);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException("Unable to render /u2g5.jsp", exception);
		}
	}
}
