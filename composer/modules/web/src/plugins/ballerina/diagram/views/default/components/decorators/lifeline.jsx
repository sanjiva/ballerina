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
import _ from 'lodash';
import PropTypes from 'prop-types';
import SimpleBBox from './../../../../../model/view/simple-bounding-box';
import * as DesignerDefaults from './../../designer-defaults';
import TreeUtils from './../../../../../model/tree-util';
import OverlayComponentsRenderingUtil from './../utils/overlay-component-rendering-util';
import ArrowDecorator from '../decorators/arrow-decorator';
import HoverGroup from 'plugins/ballerina/graphical-editor/controller-utils/hover-group';

class LifeLine extends React.Component {

    constructor(props) {
        super(props);

        const bBox = this.props.bBox;
        this.topBox = new SimpleBBox(bBox.x, bBox.y, bBox.w, DesignerDefaults.lifeLine.head.height);
        this.state = { active: 'hidden' };
        this.handleConnectorProps = this.handleConnectorProps.bind(this);
    }

    onUpdate(text) {
    }

    handleConnectorProps() {
        const model = this.props.model;
        // Check if the model is an endpoint
        if (TreeUtils.isVariableDef(this.props.model)) {
            let initialExpression = _.get(model, 'variable.InitialExpression');
            if (!initialExpression || !TreeUtils.isConnectorInitExpr(initialExpression)) {
                initialExpression = TreeUtils.getConnectorInitFromStatement(model);
            }

            // if (initialExpression) {
            const node = this.props.model;
            node.viewState.showOverlayContainer = true;
            OverlayComponentsRenderingUtil.showConnectorPropertyWindow(node);
            this.context.editor.update();
            // }
        }
    }
    render() {
        const bBox = this.props.bBox;
        const iconSize = 18;
        const lineClass = this.props.className;
        const titleBoxH = DesignerDefaults.lifeLine.head.height;
        const y2 = bBox.h + bBox.y;
        const solidY1 = bBox.y + (titleBoxH / 2);
        const solidY2 = y2;// - (titleBoxH / 2);
        this.topBox = new SimpleBBox(bBox.x, bBox.y, bBox.w, titleBoxH);

        // Check if its the default worker
        const isDefaultWorker = this.props.title === 'default';
        let tooltip = this.props.title;
        if (this.props.tooltip) {
            tooltip = this.props.tooltip;
        }

        let identifier = this.props.title;

        if (TreeUtils.isEndpointTypeVariableDef(this.props.model)) {
            identifier = this.props.model.viewState.endpointIdentifier;
        }

        const startX = bBox.x + (bBox.w / 2);
        const startY = bBox.y + titleBoxH + this.context.designer.config.statement.height;
        return (<g
            className='life-line-group'
        >

            <title> {tooltip} </title>
            <line
                x1={startX}
                y1={solidY1}
                x2={startX}
                y2={solidY2}
                className={`${lineClass} life-line`}
            />
            <line
                x1={bBox.x}
                y1={solidY1}
                x2={bBox.x + bBox.w}
                y2={solidY1}
                className={`${lineClass} life-line`}
            />
            {this.props.icon &&
                <g onClick={this.handleConnectorProps}>
                    <text
                        x={startX - (iconSize / 2)}
                        y={bBox.y - 5}
                        fontFamily='font-ballerina'
                        fontSize={iconSize}
                        className={`${lineClass} life-line-icon`}
                    >
                        {this.props.icon}
                    </text>
                </g>
            }
            <line
                x1={bBox.x}
                y1={y2}
                x2={bBox.x + bBox.w}
                y2={y2}
                className={`${lineClass} life-line`}
            />
            <text
                x={startX}
                y={solidY1 - 10}
                textAnchor='middle'
                dominantBaseline='central'
                fontWeight='400'
                className={`${lineClass} life-line-title`}
            >{identifier}</text>
            <text
                x={startX}
                y={solidY2 + 10}
                textAnchor='middle'
                dominantBaseline='central'
                fontWeight='400'
                className={`${lineClass} life-line-title`}
            >{identifier}</text>
            <HoverGroup model={this.props.model} region='actionBox'>
                <rect
                    x={bBox.x}
                    y={bBox.y}
                    width={bBox.w}
                    height={bBox.h}
                    className='invisible-rect'
                />
            </HoverGroup>
            {(!TreeUtils.isForkJoin(this.props.model.parent) &&
                (isDefaultWorker || TreeUtils.isWorker(this.props.model))) &&
                <ArrowDecorator
                    start={{ x: startX, y: startY }}
                    end={{ x: startX, y: startY }}
                    classNameArrow={`${lineClass} client-invocation-arrow`}
                />
            }
        </g>);
    }
}

LifeLine.propTypes = {
    model: PropTypes.instanceOf(Object).isRequired,
    title: PropTypes.string,
    icon: PropTypes.string,
    bBox: PropTypes.instanceOf(Object).isRequired,
    tooltip: PropTypes.string,
    className: PropTypes.string.isRequired,
};

LifeLine.defaultProps = {
    editorOptions: null,
    title: 'default',
    icon: '',
    tooltip: '',
};

LifeLine.contextTypes = {
    model: PropTypes.instanceOf(Object),
    designer: PropTypes.instanceOf(Object),
    getOverlayContainer: PropTypes.instanceOf(Object).isRequired,
    editor: PropTypes.instanceOf(Object).isRequired,
    environment: PropTypes.instanceOf(Object).isRequired,
};

export default LifeLine;
