/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.arquillian.persistence.extension;

import com.liferay.arquillian.extension.descriptor.SpringDescriptor;
import com.liferay.arquillian.extension.descriptor.SpringDescriptorImpl;
import com.liferay.arquillian.persistence.extension.databaseBuilder.descriptor.SpringDescriptorBasabaseBuilderImpl;
import com.liferay.arquillian.persistence.extension.databaseBuilder.observer.InitializeDatabaseObserver;

import com.liferay.arquillian.persistence.extension.transactional.observer.TransactionUtilProducer;
import com.liferay.arquillian.persistence.extension.transactional.observer.TransactionalObserver;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * @author Cristina González Castellano
 */
public class PersistenceTestScenarioExtension implements LoadableExtension {

	@Override
	public void register(ExtensionBuilder builder) {
		builder.observer(InitializeDatabaseObserver.class);

		builder.override(
			SpringDescriptor.class, SpringDescriptorImpl.class,
			SpringDescriptorBasabaseBuilderImpl.class);;

		builder.observer(TransactionalObserver.class);

		builder.observer(TransactionUtilProducer.class);
	}

}