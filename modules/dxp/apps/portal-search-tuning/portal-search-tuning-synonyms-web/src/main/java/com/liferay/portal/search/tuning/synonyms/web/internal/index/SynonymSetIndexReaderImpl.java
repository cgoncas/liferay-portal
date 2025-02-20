/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.index;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = SynonymSetIndexReader.class)
public class SynonymSetIndexReaderImpl implements SynonymSetIndexReader {

	@Override
	public SynonymSet fetch(
		SynonymSetIndexName synonymSetIndexName, String id) {

		Document document = _getDocument(synonymSetIndexName, id);

		if (document == null) {
			return null;
		}

		return translate(document, id);
	}

	@Override
	public boolean isExists(SynonymSetIndexName synonymSetIndexName) {
		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(synonymSetIndexName.getIndexName());

		indicesExistsIndexRequest.setPreferLocalCluster(false);

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(indicesExistsIndexRequest);

		return indicesExistsIndexResponse.isExists();
	}

	@Override
	public List<SynonymSet> search(SynonymSetIndexName synonymSetIndexName) {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(synonymSetIndexName.getIndexName());
		searchSearchRequest.setPreferLocalCluster(false);
		searchSearchRequest.setSize(_SIZE);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		return _documentToSynonymSetTranslator.translateAll(
			searchSearchResponse.getSearchHits());
	}

	protected SynonymSet translate(Document document, String id) {
		return _documentToSynonymSetTranslator.translate(document, id);
	}

	private Document _getDocument(
		SynonymSetIndexName synonymSetIndexName, String id) {

		if (Validator.isNull(id)) {
			return null;
		}

		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			synonymSetIndexName.getIndexName(), id);

		getDocumentRequest.setFetchSource(true);
		getDocumentRequest.setFetchSourceInclude(StringPool.STAR);
		getDocumentRequest.setPreferLocalCluster(false);

		GetDocumentResponse getDocumentResponse = _searchEngineAdapter.execute(
			getDocumentRequest);

		if (!getDocumentResponse.isExists()) {
			return null;
		}

		return getDocumentResponse.getDocument();
	}

	private static final int _SIZE = 10000;

	@Reference
	private DocumentToSynonymSetTranslator _documentToSynonymSetTranslator;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}