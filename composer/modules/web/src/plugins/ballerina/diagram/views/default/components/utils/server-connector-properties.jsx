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
 */

import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';
import { panel } from '../../designer-defaults';
import './properties-form.css';
import OverlayComponentsRenderingUtil from './overlay-component-rendering-util';
/**
 * React component for a server connector properties handler
 *
 * @class ServerConnectorProperties
 * @extends {React.Component}
 */
class ServerConnectorProperties extends React.Component {

    constructor(props) {
        super(props);
        this.handleShowModal = this.handleShowModal.bind(this);
    }

    /**
     * Show the property window
     */
    handleShowModal() {
        const node = this.props.model;
        node.viewState.showOverlayContainer = true;
        OverlayComponentsRenderingUtil.showServerConnectorPropertyWindow(node);
        this.context.editor.update();
    }

    /**
     * Renders the view for a connector properties handler
     *
     * @returns {ReactElement} The view.
     * @memberof ServerConnectorProperties
     */
    render() {
        const bBox = this.props.bBox;
        const titleHeight = panel.heading.height;
        const iconSize = 14;
        const protocolOffset = 50;
        let protocolClassName = 'protocol-rect';
        if (!_.isEmpty(this.props.model.viewState.overlayContainer)) {
            protocolClassName = 'protocol-rect-clicked';
        }
        return (
            <g id='serviceDefProps' onClick={this.handleShowModal}>
                <polygon
                    points={`${bBox.x + 35},${bBox.y - 15} 
                    ${bBox.x + 35 + 10},${bBox.y - 10 - 15} 
                    ${bBox.x + 35 + 40},${bBox.y - 10 - 15} 
                    ${bBox.x + 35 + 40 + 10},${bBox.y - 15} 
                    ${bBox.x + 35 + 40},${bBox.y + 10 - 15} 
                    ${bBox.x + 35 + 10},${bBox.y + 10 - 15}`
                    }
                    className='protocol-box'
                />
                <text
                    x={bBox.x + 60}
                    y={bBox.y - 15}
                    alignmentBaseline='middle'
                    textAnchor='middle'
                    className='protocol-label'
                >{this.props.model.getProtocolPackageIdentifier().value}</text>
            </g>
        );
    }
}

export default ServerConnectorProperties;

ServerConnectorProperties.contextTypes = {
    editor: PropTypes.instanceOf(Object).isRequired,
};
