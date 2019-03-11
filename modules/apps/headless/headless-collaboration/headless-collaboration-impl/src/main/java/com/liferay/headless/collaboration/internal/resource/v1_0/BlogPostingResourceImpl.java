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

package com.liferay.headless.collaboration.internal.resource.v1_0;

import static com.liferay.portal.vulcan.util.LocalDateTimeUtil.toLocalDateTime;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.headless.collaboration.dto.v1_0.BlogPosting;
import com.liferay.headless.collaboration.dto.v1_0.Categories;
import com.liferay.headless.collaboration.dto.v1_0.Image;
import com.liferay.headless.collaboration.internal.dto.v1_0.util.AggregateRatingUtil;
import com.liferay.headless.collaboration.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.collaboration.internal.odata.entity.v1_0.BlogPostingEntityModel;
import com.liferay.headless.collaboration.resource.v1_0.BlogPostingResource;
import com.liferay.headless.common.spi.service.context.ServiceContextUtil;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;

import java.time.LocalDateTime;

import java.util.Date;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/blog-posting.properties",
	scope = ServiceScope.PROTOTYPE, service = BlogPostingResource.class
)
public class BlogPostingResourceImpl
	extends BaseBlogPostingResourceImpl implements EntityModelResource {

	@Override
	public boolean deleteBlogPosting(Long blogPostingId) throws Exception {
		_blogsEntryService.deleteEntry(blogPostingId);

		return true;
	}

	@Override
	public BlogPosting getBlogPosting(Long blogPostingId) throws Exception {
		BlogsEntry blogsEntry = _blogsEntryService.getEntry(blogPostingId);

		return _toBlogPosting(blogsEntry);
	}

	@Override
	public Page<BlogPosting> getContentSpaceBlogPostingsPage(
			Long contentSpaceId, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			booleanQuery -> {
			},
			filter, BlogsEntry.class, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					Field.STATUS, WorkflowConstants.STATUS_APPROVED);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {contentSpaceId});
			},
			document -> _toBlogPosting(
				_blogsEntryService.getEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))),
			sorts);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public BlogPosting postContentSpaceBlogPosting(
			Long contentSpaceId, BlogPosting blogPosting)
		throws Exception {

		LocalDateTime localDateTime = toLocalDateTime(
			blogPosting.getDatePublished());

		String viewableBy = Optional.ofNullable(
			blogPosting.getViewableBy()
		).map(
			BlogPosting.ViewableBy::getValue
		).orElse(
			null
		);

		return _toBlogPosting(
			_blogsEntryService.addEntry(
				blogPosting.getHeadline(), blogPosting.getAlternativeHeadline(),
				blogPosting.getFriendlyUrlPath(), blogPosting.getDescription(),
				blogPosting.getArticleBody(), localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(), true, true,
				new String[0], blogPosting.getCaption(),
				_getImageSelector(blogPosting), null,
				ServiceContextUtil.createServiceContext(
					blogPosting.getKeywords(), blogPosting.getCategoryIds(),
					contentSpaceId, viewableBy)));
	}

	@Override
	public BlogPosting putBlogPosting(
			Long blogPostingId, BlogPosting blogPosting)
		throws Exception {

		LocalDateTime localDateTime = toLocalDateTime(
			blogPosting.getDatePublished());

		String viewableBy = Optional.ofNullable(
			blogPosting.getViewableBy()
		).map(
			BlogPosting.ViewableBy::getValue
		).orElse(
			null
		);

		return _toBlogPosting(
			_blogsEntryService.updateEntry(
				blogPostingId, blogPosting.getHeadline(),
				blogPosting.getAlternativeHeadline(),
				blogPosting.getFriendlyUrlPath(), blogPosting.getDescription(),
				blogPosting.getArticleBody(), localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(), true, true,
				new String[0], blogPosting.getCaption(),
				_getImageSelector(blogPosting), null,
				ServiceContextUtil.createServiceContext(
					blogPosting.getKeywords(), blogPosting.getCategoryIds(),
					blogPosting.getContentSpace(), viewableBy)));
	}

	@Override
	protected void preparePatch(BlogPosting blogPosting) {
		blogPosting.setContentSpace((Long)null);
		blogPosting.setDateCreated((Date)null);
		blogPosting.setDateModified((Date)null);
		blogPosting.setEncodingFormat((String)null);
		blogPosting.setHasComments((Boolean)null);
	}

	private Image _getImage(BlogsEntry blogsEntry) throws Exception {
		long coverImageFileEntryId = blogsEntry.getCoverImageFileEntryId();

		if (coverImageFileEntryId == 0) {
			return null;
		}

		FileEntry fileEntry = _dlAppService.getFileEntry(coverImageFileEntryId);

		return new Image() {
			{
				contentUrl = _dlURLHelper.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), null, "", false,
					false);
				imageId = coverImageFileEntryId;
				name = blogsEntry.getCoverImageCaption();
			}
		};
	}

	private ImageSelector _getImageSelector(BlogPosting blogPosting) {
		Long imageId = blogPosting.getImageId();

		if ((imageId == null) || (imageId == 0)) {
			return null;
		}

		try {
			FileEntry fileEntry = _dlAppService.getFileEntry(imageId);

			return new ImageSelector(
				FileUtil.getBytes(fileEntry.getContentStream()),
				fileEntry.getFileName(), fileEntry.getMimeType(),
				"{\"height\": 0, \"width\": 0, \"x\": 0, \"y\": 0}");
		}
		catch (Exception e) {
			throw new BadRequestException(
				"Unable to get file entry " + imageId, e);
		}
	}

	private boolean _hasComments(BlogsEntry blogsEntry) {
		int count = _commentManager.getCommentsCount(
			BlogsEntry.class.getName(), blogsEntry.getEntryId());

		if (count > 0) {
			return true;
		}

		return false;
	}

	private BlogPosting _toBlogPosting(BlogsEntry blogsEntry) throws Exception {
		return new BlogPosting() {
			{
				alternativeHeadline = blogsEntry.getSubtitle();
				aggregateRating = AggregateRatingUtil.toAggregateRating(
					_ratingsStatsLocalService.fetchStats(
						BlogsEntry.class.getName(), blogsEntry.getEntryId()));
				articleBody = blogsEntry.getContent();
				caption = blogsEntry.getCoverImageCaption();
				categories = transformToArray(
					_assetCategoryLocalService.getCategories(
						BlogsEntry.class.getName(), blogsEntry.getEntryId()),
					assetCategory -> new Categories() {
						{
							categoryId = assetCategory.getCategoryId();
							categoryName = assetCategory.getName();
						}
					},
					Categories.class);
				contentSpace = blogsEntry.getGroupId();
				creator = CreatorUtil.toCreator(
					_portal, _userLocalService.getUser(blogsEntry.getUserId()));
				dateCreated = blogsEntry.getCreateDate();
				dateModified = blogsEntry.getModifiedDate();
				datePublished = blogsEntry.getDisplayDate();
				description = blogsEntry.getDescription();
				encodingFormat = "text/html";
				friendlyUrlPath = blogsEntry.getUrlTitle();
				hasComments = _hasComments(blogsEntry);
				headline = blogsEntry.getTitle();
				id = blogsEntry.getEntryId();
				image = _getImage(blogsEntry);
				keywords = ListUtil.toArray(
					_assetTagLocalService.getTags(
						BlogsEntry.class.getName(), blogsEntry.getEntryId()),
					AssetTag.NAME_ACCESSOR);
			}
		};
	}

	private static final EntityModel _entityModel =
		new BlogPostingEntityModel();

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private BlogsEntryService _blogsEntryService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private Portal _portal;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private UserLocalService _userLocalService;

}