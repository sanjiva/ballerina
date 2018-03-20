/**
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Button } from 'semantic-ui-react';
import Dialog from './../../view/Dialog';

/**
 * Confirm Dialog when deleting files from disk
 * @extends React.Component
 */
class FileDeleteConfirmDialog extends React.Component {

    /**
     * @inheritdoc
     */
    constructor(props) {
        super(props);
        this.state = {
            error: '',
            showDialog: true,
        };
        this.onDialogHide = this.onDialogHide.bind(this);
    }

    /**
     * Called when user hides the dialog
     */
    onDialogHide() {
        this.setState({
            error: '',
            showDialog: false,
        });
        this.props.onCancel();
    }

    /**
     * @inheritdoc
     */
    render() {
        return (
            <Dialog
                show={this.state.showDialog}
                title={`Delete ${this.props.isFolder ? 'Folder' : 'File'} From Disk`}
                titleIcon='warning circle'
                size='small'
                actions={
                [
                    <Button
                        onClick={() => {
                            this.setState({
                                showDialog: false,
                            });
                            this.props.onConfirm();
                        }}
                        primary
                    >
                        Delete
                    </Button>,
                ]}
                closeDialog
                onHide={this.onDialogHide}
                error={this.state.error}
            >
                <h4>
                    {`Are you sure you want to delete "${this.props.target}"
                        ${this.props.isFolder ? ' and its contents' : ''} ?`}
                </h4>
                <p>
                    {`The ${this.props.isFolder ? 'Folder' : 'File'} will be deleted from the file system.`}
                </p>
            </Dialog>
        );
    }
}

FileDeleteConfirmDialog.propTypes = {
    isFolder: PropTypes.bool.isRequired,
    target: PropTypes.string.isRequired,
    onConfirm: PropTypes.func.isRequired,
    onCancel: PropTypes.func,
};

FileDeleteConfirmDialog.defaultProps = {
    onCancel: () => {},
};

export default FileDeleteConfirmDialog;
