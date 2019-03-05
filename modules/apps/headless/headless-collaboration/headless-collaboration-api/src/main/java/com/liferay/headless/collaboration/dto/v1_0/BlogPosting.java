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

package com.liferay.headless.collaboration.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

import java.util.Date;
import java.util.Objects;

import javax.annotation.Generated;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("BlogPosting")
//@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "BlogPosting")
public class BlogPosting {

	public AggregateRating getAggregateRating() {
		return aggregateRating;
	}

	public String getAlternativeHeadline() {
		return alternativeHeadline;
	}

	public String getArticleBody() {
		return articleBody;
	}

	public String getCaption() {
		return caption;
	}

	public Categories[] getCategories() {
		return categories;
	}

	public Long[] getCategoryIds() {
		return categoryIds;
	}

	public Long getContentSpace() {
		return contentSpace;
	}

	public Creator getCreator() {
		return creator;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public Date getDatePublished() {
		return datePublished;
	}

	public String getDescription() {
		return description;
	}

	public String getEncodingFormat() {
		return encodingFormat;
	}

	public String getFriendlyUrlPath() {
		return friendlyUrlPath;
	}

	public Boolean getHasComments() {
		return hasComments;
	}

	public String getHeadline() {
		return headline;
	}

	public Long getId() {
		return id;
	}

	public Image getImage() {
		return image;
	}

	public Long getImageId() {
		return imageId;
	}

	public String[] getKeywords() {
		return keywords;
	}

	public ViewableBy getViewableBy() {
		return viewableBy;
	}

	public void setAggregateRating(AggregateRating aggregateRating) {
		this.aggregateRating = aggregateRating;
	}

	@JsonIgnore
	public void setAggregateRating(
		UnsafeSupplier<AggregateRating, Exception>
			aggregateRatingUnsafeSupplier) {

		try {
			aggregateRating = aggregateRatingUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setAlternativeHeadline(String alternativeHeadline) {
		this.alternativeHeadline = alternativeHeadline;
	}

	@JsonIgnore
	public void setAlternativeHeadline(
		UnsafeSupplier<String, Exception> alternativeHeadlineUnsafeSupplier) {

		try {
			alternativeHeadline = alternativeHeadlineUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setArticleBody(String articleBody) {
		this.articleBody = articleBody;
	}

	@JsonIgnore
	public void setArticleBody(
		UnsafeSupplier<String, Exception> articleBodyUnsafeSupplier) {

		try {
			articleBody = articleBodyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@JsonIgnore
	public void setCaption(
		UnsafeSupplier<String, Exception> captionUnsafeSupplier) {

		try {
			caption = captionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setCategories(Categories[] categories) {
		this.categories = categories;
	}

	@JsonIgnore
	public void setCategories(
		UnsafeSupplier<Categories[], Exception> categoriesUnsafeSupplier) {

		try {
			categories = categoriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setCategoryIds(Long[] categoryIds) {
		this.categoryIds = categoryIds;
	}

	@JsonIgnore
	public void setCategoryIds(
		UnsafeSupplier<Long[], Exception> categoryIdsUnsafeSupplier) {

		try {
			categoryIds = categoryIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setContentSpace(Long contentSpace) {
		this.contentSpace = contentSpace;
	}

	@JsonIgnore
	public void setContentSpace(
		UnsafeSupplier<Long, Exception> contentSpaceUnsafeSupplier) {

		try {
			contentSpace = contentSpaceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	@JsonIgnore
	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}

	@JsonIgnore
	public void setDatePublished(
		UnsafeSupplier<Date, Exception> datePublishedUnsafeSupplier) {

		try {
			datePublished = datePublishedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setEncodingFormat(String encodingFormat) {
		this.encodingFormat = encodingFormat;
	}

	@JsonIgnore
	public void setEncodingFormat(
		UnsafeSupplier<String, Exception> encodingFormatUnsafeSupplier) {

		try {
			encodingFormat = encodingFormatUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setFriendlyUrlPath(String friendlyUrlPath) {
		this.friendlyUrlPath = friendlyUrlPath;
	}

	@JsonIgnore
	public void setFriendlyUrlPath(
		UnsafeSupplier<String, Exception> friendlyUrlPathUnsafeSupplier) {

		try {
			friendlyUrlPath = friendlyUrlPathUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setHasComments(Boolean hasComments) {
		this.hasComments = hasComments;
	}

	@JsonIgnore
	public void setHasComments(
		UnsafeSupplier<Boolean, Exception> hasCommentsUnsafeSupplier) {

		try {
			hasComments = hasCommentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	@JsonIgnore
	public void setHeadline(
		UnsafeSupplier<String, Exception> headlineUnsafeSupplier) {

		try {
			headline = headlineUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@JsonIgnore
	public void setImage(UnsafeSupplier<Image, Exception> imageUnsafeSupplier) {
		try {
			image = imageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	@JsonIgnore
	public void setImageId(
		UnsafeSupplier<Long, Exception> imageIdUnsafeSupplier) {

		try {
			imageId = imageIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

	@JsonIgnore
	public void setKeywords(
		UnsafeSupplier<String[], Exception> keywordsUnsafeSupplier) {

		try {
			keywords = keywordsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@JsonIgnore
	public void setViewableBy(
		UnsafeSupplier<ViewableBy, Exception> viewableByUnsafeSupplier) {

		try {
			viewableBy = viewableByUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setViewableBy(ViewableBy viewableBy) {
		this.viewableBy = viewableBy;
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		sb.append("\"aggregateRating\": ");

		sb.append(aggregateRating);
		sb.append(", ");

		sb.append("\"alternativeHeadline\": ");

		sb.append("\"");
		sb.append(alternativeHeadline);
		sb.append("\"");
		sb.append(", ");

		sb.append("\"articleBody\": ");

		sb.append("\"");
		sb.append(articleBody);
		sb.append("\"");
		sb.append(", ");

		sb.append("\"caption\": ");

		sb.append("\"");
		sb.append(caption);
		sb.append("\"");
		sb.append(", ");

		sb.append("\"categories\": ");

		if (categories == null) {
			sb.append("null");
		}
		else {
			sb.append("[");

			for (int i = 0; i < categories.length; i++) {
				sb.append(categories[i]);

				if ((i + 1) < categories.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append(", ");

		sb.append("\"categoryIds\": ");

		if (categoryIds == null) {
			sb.append("null");
		}
		else {
			sb.append("[");

			for (int i = 0; i < categoryIds.length; i++) {
				sb.append(categoryIds[i]);

				if ((i + 1) < categoryIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append(", ");

		sb.append("\"contentSpace\": ");

		sb.append(contentSpace);
		sb.append(", ");

		sb.append("\"creator\": ");

		sb.append(creator);
		sb.append(", ");

		sb.append("\"dateCreated\": ");

		sb.append("\"");
		sb.append(dateCreated);
		sb.append("\"");
		sb.append(", ");

		sb.append("\"dateModified\": ");

		sb.append("\"");
		sb.append(dateModified);
		sb.append("\"");
		sb.append(", ");

		sb.append("\"datePublished\": ");

		sb.append("\"");
		sb.append(datePublished);
		sb.append("\"");
		sb.append(", ");

		sb.append("\"description\": ");

		sb.append("\"");
		sb.append(description);
		sb.append("\"");
		sb.append(", ");

		sb.append("\"encodingFormat\": ");

		sb.append("\"");
		sb.append(encodingFormat);
		sb.append("\"");
		sb.append(", ");

		sb.append("\"friendlyUrlPath\": ");

		sb.append("\"");
		sb.append(friendlyUrlPath);
		sb.append("\"");
		sb.append(", ");

		sb.append("\"hasComments\": ");

		sb.append(hasComments);
		sb.append(", ");

		sb.append("\"headline\": ");

		sb.append("\"");
		sb.append(headline);
		sb.append("\"");
		sb.append(", ");

		sb.append("\"id\": ");

		sb.append(id);
		sb.append(", ");

		sb.append("\"image\": ");

		sb.append(image);
		sb.append(", ");

		sb.append("\"imageId\": ");

		sb.append(imageId);
		sb.append(", ");

		sb.append("\"keywords\": ");

		if (keywords == null) {
			sb.append("null");
		}
		else {
			sb.append("[");

			for (int i = 0; i < keywords.length; i++) {
				sb.append("\"");
				sb.append(keywords[i]);
				sb.append("\"");

				if ((i + 1) < keywords.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append(", ");

		sb.append("\"viewableBy\": ");

		sb.append(viewableBy);

		sb.append("}");

		return sb.toString();
	}

	public static enum ViewableBy {

		ANYONE("Anyone"), MEMBERS("Members"), OWNER("Owner");

		@JsonCreator
		public static ViewableBy create(String value) {
			for (ViewableBy viewableBy : values()) {
				if (Objects.equals(viewableBy.getValue(), value)) {
					return viewableBy;
				}
			}

			return null;
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		private ViewableBy(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLField
	@JsonProperty
	protected AggregateRating aggregateRating;

	@GraphQLField
	@JsonProperty
	protected String alternativeHeadline;

	@GraphQLField
	@JsonProperty
	protected String articleBody;

	@GraphQLField
	@JsonProperty
	protected String caption;

	@GraphQLField
	@JsonProperty
	protected Categories[] categories;

	@GraphQLField
	@JsonProperty
	protected Long[] categoryIds;

	@GraphQLField
	@JsonProperty
	protected Long contentSpace;

	@GraphQLField
	@JsonProperty
	protected Creator creator;

	@GraphQLField
	@JsonProperty
	protected Date dateCreated;

	@GraphQLField
	@JsonProperty
	protected Date dateModified;

	@GraphQLField
	@JsonProperty
	protected Date datePublished;

	@GraphQLField
	@JsonProperty
	protected String description;

	@GraphQLField
	@JsonProperty
	protected String encodingFormat;

	@GraphQLField
	@JsonProperty
	protected String friendlyUrlPath;

	@GraphQLField
	@JsonProperty
	protected Boolean hasComments;

	@GraphQLField
	@JsonProperty
	protected String headline;

	@GraphQLField
	@JsonProperty
	protected Long id;

	@GraphQLField
	@JsonProperty
	protected Image image;

	@GraphQLField
	@JsonProperty
	protected Long imageId;

	@GraphQLField
	@JsonProperty
	protected String[] keywords;

	@GraphQLField
	@JsonProperty
	protected ViewableBy viewableBy;

}