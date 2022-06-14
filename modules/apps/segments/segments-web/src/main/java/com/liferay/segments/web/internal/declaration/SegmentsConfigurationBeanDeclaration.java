package com.liferay.segments.web.internal.declaration;

import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;

import com.liferay.segments.configuration.SegmentsConfiguration;
import org.osgi.service.component.annotations.Component;

@Component(service = ConfigurationBeanDeclaration.class)
public class SegmentsConfigurationBeanDeclaration implements
	ConfigurationBeanDeclaration {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return SegmentsConfiguration.class;
	}
}
