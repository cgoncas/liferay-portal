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

package com.liferay.headless.web.experience.internal.resource;

import com.liferay.headless.web.experience.dto.StructuredContent;
import com.liferay.headless.web.experience.resource.StructuredContentResource;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.util.JournalHelper;
import com.liferay.portal.kernel.exception.NoSuchCompanyException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.SearchResultPermissionFilter;
import com.liferay.portal.kernel.search.SearchResultPermissionFilterFactory;
import com.liferay.portal.kernel.search.SearchResultPermissionFilterSearcher;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.context.AcceptLanguage;
import com.liferay.portal.vulcan.context.Pagination;
import com.liferay.portal.vulcan.dto.Page;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/structured-content.properties",
	service = StructuredContentResource.class
)
public class StructuredContentResourceImpl
	extends BaseStructuredContentResourceImpl {

	@Override
	public Page<StructuredContent> getContentSpaceStructuredContentsPage(
		Long parentId, String filter, String sort,
		AcceptLanguage acceptLanguage, Pagination pagination) {

		try {
			Hits hits = _getHits(parentId, pagination);

			return new Page<>(
				transform(
					_journalHelper.getArticles(hits),
					this::_toStructuredContent),
				hits.getLength());
		}
		catch (NoSuchGroupException nsge) {
			throw new NotFoundException(nsge);
		}
		catch (PrincipalException pe) {
			throw new NotAuthorizedException(pe);
		}
		catch (SearchException se) {
			throw new InternalServerErrorException(se);
		}
		catch (PortalException pe) {
			throw new InternalServerErrorException(pe);
		}
	}

	private SearchContext _createSearchContext(
		long companyId, long groupId, int start, int end) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			Field.CLASS_NAME_ID, JournalArticleConstants.CLASSNAME_ID_DEFAULT);
		searchContext.setAttribute(
			Field.STATUS, WorkflowConstants.STATUS_APPROVED);
		searchContext.setAttribute("head", Boolean.TRUE);
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {groupId});
		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);
		queryConfig.setSelectedFieldNames(
			Field.ARTICLE_ID, Field.SCOPE_GROUP_ID);

		return searchContext;
	}

	private Hits _getHits(long groupId, Pagination pagination) {
		try {
			Company company = _companyService.getCompanyByWebId(
				PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));

			SearchContext searchContext = _createSearchContext(
				company.getCompanyId(), groupId, pagination.getStartPosition(),
				pagination.getEndPosition());

			Query query = _getQuery(searchContext);

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (permissionChecker == null) {
				return IndexSearcherHelperUtil.search(searchContext, query);
			}

			if (searchContext.getUserId() == 0) {
				searchContext.setUserId(permissionChecker.getUserId());
			}

			SearchResultPermissionFilter searchResultPermissionFilter =
				_searchResultPermissionFilterFactory.create(
					new SearchResultPermissionFilterSearcher() {

						public Hits search(SearchContext searchContext)
							throws SearchException {

							return IndexSearcherHelperUtil.search(
								searchContext, query);
						}

					},
					permissionChecker);

			return searchResultPermissionFilter.search(searchContext);
		}
		catch (NoSuchCompanyException nsce) {
			throw new NotFoundException(nsce);
		}
		catch (SearchException se) {
			throw new InternalServerErrorException(se);
		}
		catch (PortalException pe) {
			throw new InternalServerErrorException(pe);
		}
	}

	private Query _getQuery(SearchContext searchContext) {
		Indexer<JournalArticle> indexer = _indexerRegistry.nullSafeGetIndexer(
			JournalArticle.class);

		try {
			return indexer.getFullQuery(searchContext);
		}
		catch (SearchException se) {
			Throwable throwable = se.getCause();

			while (throwable != null) {
				if (throwable instanceof NoSuchGroupException) {
					throw new NotFoundException(se);
				}

				throwable = throwable.getCause();
			}

			throw new InternalServerErrorException(se);
		}
	}

	private StructuredContent _toStructuredContent(
		JournalArticle journalArticle) {

		return new StructuredContent() {
			{
				setId(journalArticle.getResourcePrimKey());
			}
		};
	}

	@Reference
	private CompanyService _companyService;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private JournalHelper _journalHelper;

	@Reference
	private SearchResultPermissionFilterFactory
		_searchResultPermissionFilterFactory;

}