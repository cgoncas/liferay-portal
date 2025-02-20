import {filesize} from 'filesize';
import {uniqueId} from 'lodash';

import {UploadedFile} from '../../components/FileList/FileList';
import {Header} from '../../components/Header/Header';
import {Input} from '../../components/Input/Input';
import {MultiSelect} from '../../components/MultiSelect/MultiSelect';
import {NewAppPageFooterButtons} from '../../components/NewAppPageFooterButtons/NewAppPageFooterButtons';
import {Section} from '../../components/Section/Section';
import {UploadLogo} from '../../components/UploadLogo/UploadLogo';
import {useAppContext} from '../../manage-app-state/AppManageState';
import {TYPES} from '../../manage-app-state/actionTypes';
import {createApp, createImage, updateApp} from '../../utils/api';
import {submitBase64EncodedFile} from '../../utils/util';

import './DefineAppProfilePage.scss';

interface DefineAppProfilePageProps {
	onClickBack: () => void;
	onClickContinue: () => void;
}

const CategoriesItems = [
	{
		checked: false,
		label: 'Experience Management',
		value: 'Experience Management',
	},
	{
		checked: false,
		label: 'Collaboration and Knowledge Sharing',
		value: 'Collaboration and Knowledge Sharing',
	},
];

const TagsItems = [
	{
		checked: false,
		label: 'Analytics',
		value: 'Analytics',
	},
	{
		checked: false,
		label: 'Database',
		value: 'Database',
	},
	{
		checked: false,
		label: 'Data Visualization',
		value: 'Data Visualization',
	},
	{
		checked: false,
		label: 'Performance Management',
		value: 'Performance Management',
	},
];

export function DefineAppProfilePage({
	onClickBack,
	onClickContinue,
}: DefineAppProfilePageProps) {
	const [{appDescription, appERC, appLogo, appName, catalogId}, dispatch] =
		useAppContext();

	const handleLogoUpload = (files: FileList) => {
		const file = files[0];

		const newUploadedFile: UploadedFile = {
			error: false,
			file,
			fileName: file.name,
			id: uniqueId(),
			preview: URL.createObjectURL(file),
			progress: 0,
			readableSize: filesize(file.size),
			uploaded: true,
		};

		dispatch({
			payload: {
				file: newUploadedFile,
			},
			type: TYPES.UPDATE_APP_LOGO,
		});
	};

	const handleLogoDelete = () => {
		dispatch({
			payload: {
				file: undefined,
			},
			type: TYPES.UPDATE_APP_LOGO,
		});
	};

	return (
		<div className="profile-page-container">
			<Header
				description="Enter your new app details. 
                                This information will be used for submission, 
                                presentation, customer support, and search capabilities."
				title="Define the app profile"
			/>

			<div className="profile-page-body-container">
				<Section
					label="App Info"
					tooltip="More Info"
					tooltipText="More info"
				>
					<UploadLogo
						onDeleteFile={handleLogoDelete}
						onUpload={handleLogoUpload}
						uploadedFile={appLogo}
					/>

					<div>
						<Input
							component="input"
							label="Name"
							onChange={({target}) =>
								dispatch({
									payload: {
										value: target.value,
									},
									type: TYPES.UPDATE_APP_NAME,
								})
							}
							placeholder="Enter app name"
							required
							tooltip="Name"
							value={appName}
						/>

						<Input
							component="textarea"
							label="Description"
							localized
							onChange={({target}) =>
								dispatch({
									payload: {
										value: target.value,
									},
									type: TYPES.UPDATE_APP_DESCRIPTION,
								})
							}
							placeholder="Enter app description"
							required
							tooltip="Description"
							value={appDescription}
						/>

						<MultiSelect
							items={CategoriesItems}
							label="Categories"
							onChange={(value) =>
								dispatch({
									payload: {
										value,
									},
									type: TYPES.UPDATE_APP_CATEGORIES,
								})
							}
							placeholder="Select categories"
							required
							tooltip="Categories"
						/>

						<MultiSelect
							items={TagsItems}
							label="Tags"
							onChange={() => {}}
							placeholder="Select tags"
							required
							tooltip="Tags"
						/>
					</div>
				</Section>
			</div>

			<NewAppPageFooterButtons
				disableContinueButton={!appName || !appDescription}
				onClickBack={() => onClickBack()}
				onClickContinue={async () => {
					let product;
					let response;

					if (appERC) {
						response = await updateApp({
							appDescription,
							appERC,
							appName,
						});
					}
					else {
						response = await createApp({
							appDescription,
							appName,
							catalogId,
						});
					}

					if (!appERC) {
						product = await response.json();

						dispatch({
							payload: {
								value: {
									appERC: product.externalReferenceCode,
									appId: product.id,
									appProductId: product.productId,
									appWorkflowStatusInfo:
										product.workflowStatusInfo,
								},
							},
							type: TYPES.SUBMIT_APP_PROFILE,
						});
					}

					if (appLogo) {
						submitBase64EncodedFile(
							product.externalReferenceCode,
							appLogo.file,
							createImage,
							appLogo.fileName
						);
					}

					onClickContinue();
				}}
				showBackButton
			/>
		</div>
	);
}
