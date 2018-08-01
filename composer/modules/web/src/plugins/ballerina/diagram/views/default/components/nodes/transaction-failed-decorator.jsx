/**
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import breakpointHoc from 'src/plugins/debugger/views/BreakpointHoc';
import SimpleBBox from 'plugins/ballerina/model/view/simple-bounding-box';
import HoverGroup from 'plugins/ballerina/graphical-editor/controller-utils/hover-group';
import Node from '../../../../../model/tree/node';
import DropZone from '../../../../../drag-drop/DropZone';
import './compound-statement-decorator.css';
import Breakpoint from '../decorators/breakpoint';
import { getComponentForNodeArray } from './../../../../diagram-util';

/**
 * Wraps other UI elements and provide box with a heading.
 * Enrich elements with a action box and expression editors.
 */
class TransactionFailedDecorator extends React.Component {

    /**
     * Initialize the block decorator.
     */
    constructor(props) {
        super(props);
        this.state = {
            active: 'hidden',
        };
    }

    /**
     * Handles click event of breakpoint, adds/remove breakpoint from the node when click event fired
     *
     */
    onBreakpointClick() {
        const { model } = this.props;
        const { isBreakpoint = false } = model;
        if (isBreakpoint) {
            model.removeBreakpoint();
        } else {
            model.addBreakpoint();
        }
    }

    /**
     * Call-back for when a new value is entered via expression editor.
     */
    onUpdate() {
        // TODO: implement validate logic.
    }

    /**
     * Render breakpoint element.
     * @private
     * @return {XML} React element of the breakpoint.
     */
    renderBreakpointIndicator() {
        const breakpointSize = 14;
        const { bBox } = this.props;
        const breakpointHalf = breakpointSize / 2;
        const pointX = bBox.getRight() - breakpointHalf;
        const { model: { viewState } } = this.props;
        const statementBBox = viewState.components['statement-box'];
        const pointY = statementBBox.y - breakpointHalf;
        return (
            <Breakpoint
                x={pointX}
                y={pointY}
                size={breakpointSize}
                isBreakpoint={this.props.isBreakpoint}
                onClick={() => this.props.onBreakpointClick()}
            />
        );
    }

    /**
     * Override the rendering logic.
     * @returns {XML} rendered component.
     */
    render() {
        const { bBox, isBreakpoint, isDebugHit, connectorStartX } = this.props;
        const { designer } = this.context;

        const model = this.props.model;
        const viewState = model.viewState;
        const titleH = this.context.designer.config.compoundStatement.heading.height;
        const titleW = this.context.designer.config.compoundStatement.heading.width;
        const statementBBox = viewState.components['statement-box'];
        const gapLeft = this.context.designer.config.compoundStatement.padding.left;
        const gapTop = this.context.designer.config.compoundStatement.padding.top;

        // Defining coordinates of the diagram
        // (x,y)
        // (P1)       (P2)|---------|(P3)
        //       ---------|  failed  |
        //                |____ ____|
        //                     |(p8)
        //                   __|__
        //                   a = 1;
        //                     |
        //                     |
        //                     |
        //                    (P6)

        const p1X = bBox.x - gapLeft;
        const p1Y = statementBBox.y + gapTop;

        const p2X = bBox.x - (titleW / 2);
        const p2Y = p1Y + (titleH / 2);

        const p8X = bBox.x;
        const p8Y = p2Y + (titleH / 2);

        const p6X = bBox.x;
        const p6Y = p8Y + viewState.components['statement-box'].h - (titleH / 2);

        const p11X = p1X;
        const p11Y = p1Y + (titleH / 2);

        let statementRectClass = 'statement-title-rect';
        if (isDebugHit) {
            statementRectClass = `${statementRectClass} debug-hit`;
        }

        const body = getComponentForNodeArray(this.props.model);

        return (
            <g
                ref={(group) => {
                    this.myRoot = group;
                }}
            >
                <line
                    x1={connectorStartX}
                    y1={p2Y}
                    x2={p2X}
                    y2={p2Y}
                    className='flowchart-background-empty-rect'
                />
                <rect
                    x={p2X}
                    y={p1Y}
                    width={titleW}
                    height={titleH}
                    className={statementRectClass}
                    rx='5'
                    ry='5'
                />
                <text
                    x={p2X + designer.config.compoundStatement.text.padding}
                    y={(p1Y + p2Y) / 2}
                    className='statement-title-text-left'
                >onretry
                </text>
                <DropZone
                    x={p11X}
                    y={p11Y}
                    width={statementBBox.w}
                    height={statementBBox.h}
                    baseComponent='rect'
                    dropTarget={this.props.model.body}
                    enableDragBg
                    enableCenterOverlayLine={!this.props.disableDropzoneMiddleLineOverlay}
                />
                <line
                    x1={p8X}
                    y1={p8Y}
                    x2={p6X}
                    y2={p6Y - 10}
                    className='flowchart-background-empty-rect'
                />
                { isBreakpoint && this.renderBreakpointIndicator() }
                {this.props.children}
                {body}
                <HoverGroup model={this.props.model} region='actionBox'>
                    <rect
                        x={p8X}
                        y={p8Y - 25}
                        width={50}
                        height={25}
                        className='invisible-rect'
                    />
                </HoverGroup>
                <HoverGroup model={this.props.model} region='main'>
                    <rect
                        x={p8X}
                        y={p8Y}
                        width={50}
                        height={50}
                        className='invisible-rect'
                    />
                </HoverGroup>
            </g>);
    }
}

TransactionFailedDecorator.defaultProps = {
    draggable: null,
    children: null,
    undeletable: false,
    editorOptions: null,
    parameterEditorOptions: null,
    utilities: null,
    parameterBbox: null,
    disableButtons: {
        debug: false,
        delete: false,
        jump: false,
    },
    disableDropzoneMiddleLineOverlay: false,
    isDebugHit: false,
};

TransactionFailedDecorator.propTypes = {
    model: PropTypes.instanceOf(Node).isRequired,
    children: PropTypes.arrayOf(PropTypes.node),
    bBox: PropTypes.instanceOf(SimpleBBox).isRequired,
    onBreakpointClick: PropTypes.func.isRequired,
    isBreakpoint: PropTypes.bool.isRequired,
    connectorStartX: PropTypes.number.isRequired,
    disableDropzoneMiddleLineOverlay: PropTypes.bool,
    isDebugHit: PropTypes.bool,
};

TransactionFailedDecorator.contextTypes = {
    getOverlayContainer: PropTypes.instanceOf(Object).isRequired,
    environment: PropTypes.instanceOf(Object).isRequired,
    editor: PropTypes.instanceOf(Object).isRequired,
    mode: PropTypes.string,
    designer: PropTypes.instanceOf(Object),
};

export default breakpointHoc(TransactionFailedDecorator);
